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
                this.getComments(request, response);
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
                createComment(request, response);
            }
            case "delete" -> {
                deleteComment(request, response);
            }
            default -> {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void getComments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    private void createComment(HttpServletRequest request, HttpServletResponse response) throws IOException {

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

    private void deleteComment(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        String idRaw = request.getParameter("id");
        String postIdRaw = request.getParameter("postid");
        int id, postId;
        try {
            id = Integer.parseInt(idRaw);
            postId = postIdRaw != null ? Integer.parseInt(postIdRaw) : 1;
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        commentDAO.hide(id, user.getId());
        response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
    }
}
