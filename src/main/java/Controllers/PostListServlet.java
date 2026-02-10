package Controllers;

import Models.DAO.PostDAO;
import Models.Objects.Post;   
import Models.Objects.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
@WebServlet("/admin/posts")
public class PostListServlet extends HttpServlet {

    private PostDAO postDAO = new PostDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendError(404);
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            List<Post> posts = postDAO.getAll();
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

        if (action.equals("edit")) {
            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Post post = postDAO.getById(id);
            if (post == null || !hasPermission(user, post)) {
                response.sendError(500);
                return;
            }

            request.setAttribute("post", post);
            request.getRequestDispatcher("/WEB-INF/PostListView/edit.jsp")
                    .forward(request, response);
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
            int categoryId;
            try {
                categoryId = Integer.parseInt(request.getParameter("categoryId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String title = request.getParameter("title");
            String content = request.getParameter("content");

            Post p = new Post();
            p.setUserId(user.getId());
            p.setCategoryId(categoryId);
            p.setTitle(title);
            p.setContent(content);

            postDAO.insert(p);
            response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
        }

        if (action.equals("edit")) {
            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String title = request.getParameter("title");
            String content = request.getParameter("content");

            Post post = postDAO.getById(id);
            if (!hasPermission(user, post)) {
                response.sendError(500);
                return;
            }

            post.setTitle(title);
            post.setContent(content);
            postDAO.update(post);

            response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
        }

        if (action.equals("hide")) {
            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Post post = postDAO.getById(id);
            if (!hasPermission(user, post)) {
                response.sendError(500);
                return;
            }

            postDAO.hide(id);
            response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
        }
    }

  private boolean isAdminOrEditor(User user) {
    return user.getGroupId() == 1 || user.getGroupId() == 2;
}


   private boolean hasPermission(User user, Post post) {
    if (user.getGroupId() == 1) return true; // Admin
    return user.getGroupId() == 2            // Editor
            && post.getUserId() == user.getId();
}

}
