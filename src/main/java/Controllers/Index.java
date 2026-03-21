package Controllers;

import Models.DAO.CategoryDAO;
import Models.DAO.PostDAO;
import Models.DAO.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Index", urlPatterns = {"/index", ""})
public class Index extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private final PostDAO postDao;
    private final CategoryDAO categoryDao;
    private final UserDAO userDAO;

    public Index() {
        this.postDao = new PostDAO();
        this.categoryDao = new CategoryDAO();
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("category", this.categoryDao.GetHashCategory());
        request.setAttribute("post", this.postDao.GetHomePostTop());
        request.setAttribute("users", this.userDAO.GetUserName());
        request.getRequestDispatcher("/WEB-INF/JSPViews/index.jsp").forward(request, response);
    }
}
