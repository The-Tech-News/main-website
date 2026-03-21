package Controllers;

import Models.DAO.CommentDAO;
import Models.Objects.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/comment")
public class Comment extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CommentDAO commentDAO;

    public Comment() {
        this.commentDAO = new CommentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // default action is redirecting to default '/'
        switch (request.getParameter("action")) {
            case "get" -> {
                this.GetComments(request, response);
            }
            default -> {
                response.sendRedirect(request.getContextPath() + "/");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (request.getParameter("action")) {
            case "create" -> {
                CreateComment(request, response);
            }
            case "delete" -> {
                DeleteComment(request, response);
            }
            default -> {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void GetComments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int postId = Integer.parseInt(request.getParameter("postid"));
            List<Models.Objects.Comment> comments = commentDAO.getByPostId(postId);
            request.setAttribute("comments", comments);
            request.setAttribute("postId", postId);
            request.getRequestDispatcher("/WEB-INF/JSPViews/CommentView/Create.jsp").forward(request, response);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getLocalizedMessage());
        }
    }

    private void CreateComment(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        try {
            int postId = Integer.parseInt(request.getParameter("postid"));
            String content = request.getParameter("content");

            if (content == null || content.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            commentDAO.create(user.getId(), postId, content.trim());

            response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void DeleteComment(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        int id, postId;
        try {
            id = Integer.parseInt(request.getParameter("id"));
            postId = (request.getParameter("postid")) != null ? Integer.parseInt(request.getParameter("postid")) : 1;
            commentDAO.hide(id, user.getId());
            response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
