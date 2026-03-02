package Controllers;

import Models.DAO.PostDAO;
import Models.Objects.Post;
import Models.Objects.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "PostServlet", urlPatterns = {"/post"})
public class PostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PostDAO postDAO = new PostDAO();
    private final String numberRegex = "^[0-9]+$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loggedUser = (session == null) ? null : (User) session.getAttribute("loggedUser");

        String idStr = request.getParameter("id");

        // HOME: /post  (or /post with no id)
        if (idStr == null || idStr.isBlank()) {
            ArrayList<Post> posts = postDAO.GetHomePosts(loggedUser);
            request.setAttribute("posts", posts);
            request.getRequestDispatcher("/WEB-INF/JSPViews/PostView/Home.jsp").forward(request, response);
            return;
        }

        // DETAIL: /post?id=...
        if (!idStr.matches(numberRegex)) {
            response.sendError(400, "Invalid id");
            return;
        }

        int id = Integer.parseInt(idStr);

        Post post = postDAO.GetPostForView(id, loggedUser);
        if (post == null) {
            // use 404 to avoid revealing hidden posts
            response.sendError(404);
            return;
        }

        request.setAttribute("post", post);
        request.getRequestDispatcher("/WEB-INF/JSPViews/PostView/Detail.jsp").forward(request, response);
    }
}