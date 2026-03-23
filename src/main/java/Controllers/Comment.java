package Controllers;

import java.io.IOException;

import Models.DAO.CommentDAO;
import Models.Objects.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/comment")
public class Comment extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient final CommentDAO commentDAO;

    public Comment() {
        this.commentDAO = new CommentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/");
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

    private void CreateComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        try {
            int postId = Integer.parseInt(request.getParameter("postid"));
            String content = request.getParameter("content");

            if (content == null || content.trim().isEmpty()) {
                response.sendError(400);
                return;
            }

            commentDAO.create(user.getId(), postId, content.trim());

            response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
        } catch (NumberFormatException ex) {
            response.sendError(500);
        }
    }

    private void DeleteComment(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendError(401);
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            Models.Objects.Comment comment = commentDAO.GetComment(id);
            
            if (comment == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Comment not found.");
                return;
            }

            if (user.getId() == comment.getUserId() || user.getGroupId() == 1) {
                commentDAO.hide(id, user.getId());
                response.sendRedirect(request.getContextPath() + "/post?id=" + comment.getPostId());
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to delete this comment.");
            }
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters.");
        }
    }
}
