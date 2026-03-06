package Controllers;

import Models.DAO.UserDAO;
import Models.Objects.User;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.Date;
import java.time.Instant;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "Auth", urlPatterns = {"/auth"})
public class Auth extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String[] cookieHeadName = {"email", "name"};
    private final String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private final String pwdhashRegex = "^[A-Za-z0-9]+$";
    private final String nameRegex = "^[\\p{L}\\. ]+$";

    private final String OidcIssuer;
    private final String OidcClientId;
    private final String OidcClientSecret;

    private boolean isOidcEnabled = false;

    private final UserDAO userObjectMgmt;

    public Auth() {
        this.userObjectMgmt = new UserDAO();

        this.OidcIssuer = System.getenv("OIDC_ISSUER");
        this.OidcClientId = System.getenv("OIDC_CLIENT_ID");
        this.OidcClientSecret = System.getenv("OIDC_CLIENT_SECRET");

        if (this.OidcIssuer != null && this.OidcClientId != null && this.OidcClientSecret != null) {
            this.isOidcEnabled = true;
        }
    }

    /*
    API Mapping for Auth
    - POST
        + action=signin - Dang Nhap
        + action=signup - Dang Ky
        + action=logout - Dang xuat
    - GET
        + action=signin - Dang Nhap
        + action=signup - Dang Ky
        + action=logout - Dang xuat
     */
    private boolean IsSignedIn(HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("loggedUser");
        return u != null;
    }

    private Cookie RequestNewCookie(String cookieName, String cookieValue, String contextPath) {
        Cookie c = new Cookie(cookieName, cookieValue);

        c.setMaxAge(60 * 60);
        c.setPath(contextPath);

        c.setHttpOnly(true);
        c.setSecure(true);

        return c;
    }

    private void ResetCredentialCookie(HttpServletResponse response) {
        for (String cn : cookieHeadName) {
            Cookie c = new Cookie(cn, "");
            c.setMaxAge(0);
            response.addCookie(c);
        }
    }

    private void HandleSignIn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String pwdHash = request.getParameter("pwdHash");

        if (email == null || pwdHash == null) {
            response.sendError(500, "Required parameter is null. Please check the input.");
            return;
        }

        if (!email.matches(emailRegex) || !pwdHash.matches(pwdhashRegex)) {
            response.sendError(500, "email or pwdHash did not in valid format.");
            return;
        }

        User user = userObjectMgmt.GetUserSignIn(email, pwdHash);
        if (user != null) {
            String encodedName = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8.toString());
            
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", user);
            
            response.addCookie(this.RequestNewCookie("email", user.getEmail(), request.getContextPath()));
            response.addCookie(this.RequestNewCookie("name", encodedName, request.getContextPath()));
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            this.ResetCredentialCookie(response);
            response.sendRedirect(request.getContextPath() + "/auth?action=denied");
        }
    }

    private void HandleSignUp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String pwdHash = request.getParameter("pwdHash");
        String name = request.getParameter("name");

        if (email == null || pwdHash == null || name == null) {
            response.sendError(500, "Required parameter is null. Please check the input.");
            return;
        }

        if (!email.matches(emailRegex) || !pwdHash.matches(pwdhashRegex) || !name.matches(nameRegex)) {
            response.sendError(500, "Either email, pwdHash, or name is not in correct format. Please check the input.");
            return;
        }

        int sqlExec = this.userObjectMgmt.CreateNewUser(email, pwdHash, name);
        if (sqlExec != 0) {
            response.sendError(500, "The user could not be created.");
        } else {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
        }
    }

    // Start OIDC login by redirecting to Keycloak
    private void HandleOidcLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.OidcIssuer == null || this.OidcClientId == null || this.OidcClientSecret == null) {
            response.sendError(500, "OIDC not configured on server.");
            return;
        }

        String state = UUID.randomUUID().toString();
        HttpSession session = request.getSession();
        session.setAttribute("oidc_state", state);

        String redirectUri = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()) + "/auth?action=oidc_callback";
        
        // Replace with HTTPS
        redirectUri = redirectUri.replace("http://", "https://");
        
        String authUrl = String.format(
                "%s/protocol/openid-connect/auth?response_type=code&client_id=%s&redirect_uri=%s&scope=openid%%20profile%%20email&state=%s",
                this.OidcIssuer,
                URLEncoder.encode(this.OidcClientId, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(state, StandardCharsets.UTF_8.toString())
        );

        response.sendRedirect(authUrl);
    }

    // OIDC callback: exchange code, validate id_token, and sign-in/create u user
    private void HandleOidcCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
        String state = request.getParameter("state");
        String code = request.getParameter("code");

        HttpSession session = request.getSession();
        
        if (session == null || state == null || !state.equals(session.getAttribute("oidc_state"))) {
            response.sendError(400, "Invalid OIDC state.");
            return;
        }

        if (code == null) {
            response.sendError(400, "Authorization code is missing.");
            return;
        }

        String redirectUri = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()) + "/auth?action=oidc_callback";

        // Exchange code for tokens
        URL tokenUrl = new URI(this.OidcIssuer + "/protocol/openid-connect/token").toURL();
        HttpURLConnection conn = (HttpURLConnection) tokenUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String body = String.format(
                "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
                URLEncoder.encode(code, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(this.OidcClientId, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(this.OidcClientSecret, StandardCharsets.UTF_8.toString())
        );

        conn.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));

        int status = conn.getResponseCode();
        if (status != 200) {
            response.sendError(500, "Token endpoint returned " + status);
            return;
        }

        try (JsonReader jr = Json.createReader(conn.getInputStream())) {
            JsonObject jo = jr.readObject();
            String idToken = jo.getString("id_token", null);

            if (idToken == null) {
                response.sendError(500, "No id_token in token response.");
                return;
            }

            // Verify id_token signature and claims via JWKS
            try {
                URL jwksUrl = new URI(this.OidcIssuer + "/protocol/openid-connect/certs").toURL();
                ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
                JWKSource<SecurityContext> keySource = JWKSourceBuilder.create(jwksUrl).build();
                jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource));

                JWTClaimsSet claims = jwtProcessor.process(idToken, null);

                // Basic claim checks
                String issuer = claims.getIssuer();
                if (issuer == null || !issuer.equals(this.OidcIssuer)) {
                    response.sendError(500, "Issuer mismatch");
                    return;
                }

                if (!claims.getAudience().contains(this.OidcClientId)) {
                    response.sendError(500, "Token audience mismatch.");
                    return;
                }

                Date exp = claims.getExpirationTime();
                if (exp == null || exp.toInstant().isBefore(Instant.now())) {
                    response.sendError(500, "Token expired.");
                    return;
                }

                String email = claims.getStringClaim("email");
                String name = claims.getStringClaim("name");

                if (email == null) {
                    response.sendError(500, "Email is not specified.");
                    return;
                }

                if (!email.matches(this.emailRegex)) {
                    response.sendError(500, "email is not in correct format.");
                    return;
                }

                if (name == null) {
                    name = claims.getStringClaim("preferred_username");
                }

                if (!name.matches(name)) {
                    response.sendError(500, "name is not in correct format.");
                    return;
                }

                // Find or create u user
                User u = this.userObjectMgmt.GetUserSignIn(email);
                String placeholderPwd = UUID.randomUUID().toString().replace("-", "");
                
                if (u == null) {
                    int created = this.userObjectMgmt.CreateNewUser(email, placeholderPwd, name);
                    
                    if (created != 0) {
                        response.sendError(500, "Could not create local user for OIDC account.");
                        return;
                    }
                    
                    u = this.userObjectMgmt.GetUserSignIn(email, placeholderPwd);
                }

                if (u == null) {
                    response.sendError(500, "Local user lookup failed after creation.");
                    return;
                }

                // Sign in locally
                String encodedName = URLEncoder.encode(u.getName(), StandardCharsets.UTF_8.toString());
                response.addCookie(this.RequestNewCookie("email", u.getEmail(), request.getContextPath()));
                response.addCookie(this.RequestNewCookie("name", encodedName, request.getContextPath()));
                session.setAttribute("loggedUser", u);

                response.sendRedirect(request.getContextPath() + "/");
            } catch (JOSEException | BadJOSEException | IOException | ParseException ex) {
                response.sendError(500, "Failed with reason: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (request.getParameter("action")) {
            case null -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin");
                }
            }
            case "signin" -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/SignIn.jsp").forward(request, response);
                }
            }
            case "oidc_signin" -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    if (this.isOidcEnabled) {
                        this.HandleOidcLogin(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/auth?action=denined");
                    }
                }
            }
            case "oidc_callback" -> {
                if (this.isOidcEnabled) {
                    try {
                        this.HandleOidcCallback(request, response);
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(Auth.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth?action=denined");
                }
            }
            case "signup" -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/SignUp.jsp").forward(request, response);
                }
            }
            case "logout" -> {
                this.ResetCredentialCookie(response);
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/");
            }
            case "denied" -> {
                response.setStatus(500);
                request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/Denied.jsp").forward(request, response);
            }
            default -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin");
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (request.getParameter("action")) {
            case "signin" ->
                this.HandleSignIn(request, response);
            case "signup" ->
                this.HandleSignUp(request, response);
            default -> {
                response.setStatus(404);
                request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/Denied.jsp").forward(request, response);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
