package Controllers;

import java.io.IOException;

import Models.DAO.CategoryDAO;
import Models.DAO.CommentDAO;
import Models.DAO.PostDAO;
import Models.DAO.StatisticDAO;
import Models.DAO.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Post", urlPatterns = {"/post"})
public class Post extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient final PostDAO postDAO;
    private transient final CommentDAO commentDAO;
    private transient final CategoryDAO categoryDao;
    private transient final StatisticDAO statDAO;
    private transient final UserDAO userDAO;

    private final String numberRegex = "^[0-9]+$";

    public Post() {
        this.postDAO = new PostDAO();
        this.commentDAO = new CommentDAO();
        this.statDAO = new StatisticDAO();
        this.categoryDao = new CategoryDAO();
        this.userDAO = new UserDAO();
    }

    private void GetHomePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("posts", postDAO.GetHomePosts());
        request.setAttribute("categories", this.categoryDao.GetHashCategory());
        request.setAttribute("users", this.userDAO.GetUserName());
        request.getRequestDispatcher("/WEB-INF/JSPViews/PostView/Home.jsp").forward(request, response);
    }

    private void GetPostId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            Models.Objects.Post p = this.postDAO.GetPostId(id);
            
            if (p == null) {
                response.sendError(404, "Post not found");
                return;
            }

            request.setAttribute("post", p);
            request.setAttribute("category", this.categoryDao.GetCategory(p.getCategoryId()));
            request.setAttribute("users", this.userDAO.GetUserName());
            request.setAttribute("comment", this.commentDAO.getByPostId(id));

            this.statDAO.IncreaseViewCount(id);

            request.getRequestDispatcher("/WEB-INF/JSPViews/PostView/Detail.jsp").forward(request, response);
        } catch (NumberFormatException ex) {
            response.sendError(400);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");

        switch (idStr) {
            case null -> {
                this.GetHomePost(request, response);
            }
            default -> {
                if (!idStr.matches(this.numberRegex)) {
                    response.sendRedirect(request.getContextPath() + "/post");
                    return;
                }

                this.GetPostId(request, response);
            }
        }
    }
}
