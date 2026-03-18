package Controllers;

import Models.DAO.PostDAO;
import Models.DAO.CommentDAO;
import Models.DAO.StatisticDAO;
import Models.Objects.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Post", urlPatterns = {"/post"})
public class Post extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PostDAO postDAO = new PostDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final StatisticDAO statDAO = new StatisticDAO();

    private final String numberRegex = "^[0-9]+$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loggedUser = (session == null) ? null : (User) session.getAttribute("loggedUser");

        String idStr = request.getParameter("id");

        // HOME: /post
        if (idStr == null || idStr.isBlank()) {
            ArrayList<Models.Objects.Post> posts = postDAO.GetHomePosts(loggedUser);
            request.setAttribute("posts", posts);
            request.getRequestDispatcher("/WEB-INF/JSPViews/PostView/Home.jsp").forward(request, response);
            return;
        }

        // DETAIL: /post?id=...
        if (!idStr.matches(numberRegex)) {
            response.sendError(400, "Invalid id");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException ex) {
            response.sendError(400, "Invalid id");
            return;
        }

        Models.Objects.Post post = postDAO.GetPostForView(id, loggedUser);
        if (post == null) {
            response.sendError(404);
            return;
        }
        List<Models.Objects.Comment> comments = commentDAO.getByPostId(id);
        statDAO.IncreaseViewCount(id);
        request.setAttribute("post", post);
        request.setAttribute("comments", comments);
        request.getRequestDispatcher("/WEB-INF/JSPViews/PostView/Detail.jsp").forward(request, response);
    }
}
