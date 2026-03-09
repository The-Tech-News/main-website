package Controllers;

import Models.DAO.CommentDAO;
import Models.Objects.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        switch (action) {
            case "get" -> getComments(request, response);
            case "create", "delete" -> // create and delete must be POST
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST);
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
            case "create" -> createComment(request, response);
            case "delete" -> deleteComment(request, response);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST);
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
        request.setAttribute("postId", postId);
        request.getRequestDispatcher("/WEB-INF/JSPViews/CommentView/Create.jsp").forward(request, response);
    }

    private void createComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
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

        String referer = request.getHeader("Referer");
        String safeRedirect = getSafeRedirectTarget(request, referer, request.getContextPath() + "/");
        response.sendRedirect(safeRedirect);
    }

    private void deleteComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
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
        String safeRedirect = getSafeRedirectTarget(request, referer, request.getContextPath() + "/");
        response.sendRedirect(safeRedirect);
    }

    /**
     * Returns a safe redirect target based on the provided referer.
     * <p>
     * If the referer is a relative URI, it is returned as-is. If it is an absolute URI,
     * it is only used when its host matches the current server name and its path starts
     * with the current context path. Otherwise, the provided defaultPath is returned.
     */
    private String getSafeRedirectTarget(HttpServletRequest request, String referer, String defaultPath) {
        if (referer == null || referer.isEmpty()) {
            return defaultPath;
        }

        try {
            URI uri = new URI(referer);

            // Allow relative URLs (no scheme/host)
            if (!uri.isAbsolute()) {
                String path = uri.toString();
                return path.isEmpty() ? defaultPath : path;
            }

            String serverName = request.getServerName();
            String contextPath = request.getContextPath();

            // Only allow redirects to the same host and within the same context path
            if (serverName.equalsIgnoreCase(uri.getHost())) {
                String path = uri.getPath();
                if (path != null && path.startsWith(contextPath)) {
                    String query = uri.getQuery();
                    StringBuilder target = new StringBuilder(path);
                    if (query != null && !query.isEmpty()) {
                        target.append('?').append(query);
                    }
                    return target.toString();
                }
            }
        } catch (URISyntaxException e) {
            // fall through to default on parse error
        }

        return defaultPath;
    }
}
