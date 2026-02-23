package Controllers;

import Models.DAO.CommentDAO;
import Models.Objects.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;

@WebServlet("/comment")
public class Comment extends HttpServlet {

    private CommentDAO commentDAO;

    @Override
    public void init() {
        commentDAO = new CommentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            request.getRequestDispatcher("home.jsp").forward(request, response);
            return;
        }

        switch (action) {
            case "get":
                getComments(request, response);
                break;
            case "create":
            case "delete":
                // create and delete must be POST
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "create":
                createComment(request, response);
                break;
            case "delete":
                deleteComment(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void getComments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String postIdRaw = request.getParameter("postid");
        int postId;
        try {
            postId = Integer.parseInt(postIdRaw);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<Models.Objects.Comment> comments = commentDAO.getByPostId(postId);

        request.setAttribute("comments", comments);
        request.getRequestDispatcher("post-detail.jsp").forward(request, response);
    }

    private void createComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String postIdRaw = request.getParameter("postid");
        String content = request.getParameter("content");

        int postId;
        try {
            postId = Integer.parseInt(postIdRaw);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Models.Objects.Comment c = new Models.Objects.Comment(0, user.getId(), postId, content.trim(), null, false);

        commentDAO.create(c);

        response.sendRedirect("post?action=detail&id=" + postId);
    }

    private void deleteComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idRaw = request.getParameter("id");
        int id;
        try {
            id = Integer.parseInt(idRaw);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Models.Objects.Comment existing = commentDAO.getById(id);
        if (existing == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean isOwner = existing.getUserId() == user.getId();
        boolean isAdmin = user.getGroupId() == 1;

        if (!isOwner && !isAdmin) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        commentDAO.hide(id);

        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "home.jsp");
    }
}
