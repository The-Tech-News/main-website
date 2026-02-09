package Controllers;

import dao.PostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Post;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/posts")
public class PostListServlet extends HttpServlet {

    private PostDAO postDAO = new PostDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check login
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(404);
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            List<Post> posts = postDAO.getAllPosts();
            request.setAttribute("posts", posts);
            request.getRequestDispatcher("/WEB-INF/PostListView/list.jsp")
                    .forward(request, response);
            return;
        }

        if (action.equals("create")) {
            if (!isAdminOrEditor(user)) {
                response.sendError(500);
                return;
            }
            request.getRequestDispatcher("/WEB-INF/PostListView/create.jsp")
                    .forward(request, response);
            return;
        }

        if (action.equals("edit") || action.equals("hide")) {
            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (Exception e) {
                response.sendError(404);
                return;
            }

            Post post = postDAO.getPostById(id);
            if (post == null) {
                response.sendError(404);
                return;
            }

            // Permission check
            if (!hasPermission(user, post)) {
                response.sendError(500);
                return;
            }

            if (action.equals("edit")) {
                request.setAttribute("post", post);
                request.getRequestDispatcher("/WEB-INF/PostListView/edit.jsp")
                        .forward(request, response);
            } else {
                postDAO.hidePost(id);
                response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(404);
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        if (!isAdminOrEditor(user)) {
            response.sendError(500);
            return;
        }

        if (action.equals("create")) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            postDAO.createPost(userId, categoryId, title, content);
            response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
        }

        if (action.equals("edit")) {
            int id = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            Post post = postDAO.getPostById(id);
            if (!hasPermission(user, post)) {
                response.sendError(500);
                return;
            }

            postDAO.updatePost(id, title, content);
            response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
        }
    }

    private boolean isAdminOrEditor(User user) {
        return user.getGroupName().equalsIgnoreCase("Admin")
                || user.getGroupName().equalsIgnoreCase("Editor");
    }

    private boolean hasPermission(User user, Post post) {
        if (user.getGroupName().equalsIgnoreCase("Admin")) return true;
        return user.getGroupName().equalsIgnoreCase("Editor")
                && post.getUserId() == user.getId();
    }
}
