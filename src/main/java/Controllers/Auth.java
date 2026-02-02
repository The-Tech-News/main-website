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

@WebServlet(name = "Auth", urlPatterns = {"/auth"})
public class Auth extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String[] cookieHeadName = {"email", "pwdHash", "name", "groupId", "isLogin"};
    private final String emailRegex = "^[A-Za-z0-9\\.]{1,64}@[A-Za-z0-9]{1,64}\\.[A-Za-z]{1,3}$";
    private final String pwdhashRegex = "^[A-Za-z0-9]+$";
    private final String nameRegex = "^[A-Za-z0-9\\.]+$";
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
        + action=signup - Dang Kyauth
        + action=logout - Dang xuat
    */
    
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
            response.sendError(500, "email or pwdHash did not in compliant format.");
            return;
        }
        
        User user = userObjectMgmt.GetUserSignIn(email, pwdHash);
        if (user != null) {
            response.setStatus(200);
            response.addCookie(new Cookie("email", user.getEmail()));
            response.addCookie(new Cookie("pwdHash", user.getPwdHash()));
            response.addCookie(new Cookie("name", user.getName()));
            response.addCookie(new Cookie("groupId", "%d".formatted(user.getGroupId())));
            response.addCookie(new Cookie("isLogin", "true"));
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
            this.ResetCredentialCookie(response);
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (request.getParameter("action")) {
            case null -> request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/SignIn.jsp").forward(request, response);
            case "signin" -> request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/SignIn.jsp").forward(request, response);
            case "signup" -> request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/SignUp.jsp").forward(request, response);
            case "logout" -> {
                this.ResetCredentialCookie(response);
                response.sendRedirect(request.getContextPath() + "/");
            }
            case "denied" -> {
                response.setStatus(500);
                request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/Denied.jsp").forward(request, response);
            }
            default -> {}
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (request.getParameter("action")) {
            case null -> this.HandleSignIn(request, response);
            case "signin" -> this.HandleSignIn(request, response);
            case "signup" -> this.HandleSignUp(request, response);
            default -> {}
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
