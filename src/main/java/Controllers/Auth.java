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

@WebServlet(name = "Auth", urlPatterns = {"/auth"})
public class Auth extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String[] cookieHeadName = {"email", "pwdHash", "name", "groupId", "isLogin"};
    private final String emailRegex = "^[A-Za-z0-9\\.]{1,64}@[A-Za-z0-9]{1,64}\\.[A-Za-z]{1,10}$";
    private final String pwdhashRegex = "^[A-Za-z0-9]+$";
    private final String nameRegex = "^[\\p{L}\\. ]+$";
    
    private final UserDAO userObjectMgmt;
    
    public Auth() {
        this.userObjectMgmt = new UserDAO();
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
            response.setStatus(200);
            
            response.addCookie(this.RequestNewCookie("email", user.getEmail(), request.getContextPath()));
            response.addCookie(this.RequestNewCookie("pwdHash", user.getPwdHash(), request.getContextPath()));
            response.addCookie(this.RequestNewCookie("name", encodedName, request.getContextPath()));
            
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", user);
            
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
            case "signin" -> this.HandleSignIn(request, response);
            case "signup" -> this.HandleSignUp(request, response);
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
