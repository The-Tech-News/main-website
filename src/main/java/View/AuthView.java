package View;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AuthView", urlPatterns = {"/auth"})
public class AuthView extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (request.getParameter("action")) {
            case null -> request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/SignIn.jsp").forward(request, response);
            case "signin" -> request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/SignIn.jsp").forward(request, response);
            case "signup" -> request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/SignUp.jsp").forward(request, response);
            case "logout" -> request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/LogOut.jsp").forward(request, response);
            default -> {}
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
