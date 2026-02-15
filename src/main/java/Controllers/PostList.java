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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "PostList", urlPatterns = {"/admin/posts"})
public class PostList extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PostDAO postObjectMgmt;

    public PostList() {
        this.postObjectMgmt = new PostDAO();
    }

    private boolean IsAuthenticated(HttpSession session) {
        return session == null || session.getAttribute("loggedUser") == null;
    }

    private boolean isAdminOrEditor(User user) {
        return user.getGroupId() == 1 || user.getGroupId() == 2;
    }

    private boolean hasPermission(User user, Post post) {
        if (user.getGroupId() == 1) {
            return true;
        }
        return user.getGroupId() == 2 && post.getUserId() == user.getId();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession(false);
//        
//        if (!this.IsAuthenticated(session)) {
//            response.sendError(500, "User is not authenticated");
//            return;
//        }

        switch (request.getParameter("action")) {
            case null -> {
                response.sendRedirect("/admin/posts?action=list");
            }
            case "list" -> {
                ArrayList<Post> posts = postObjectMgmt.GetAll();
                request.setAttribute("posts", posts);
                request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/List.jsp").forward(request, response);
            }
            case "create" -> {
//                if (!isAdminOrEditor(user)) {
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
//                    return;
//                }
//                request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/Create.jsp").forward(request, response);
            }
            case "edit" -> {
//                int id = Integer.parseInt(request.getParameter("id"));
//                Post post = null;
//                try {
//                    post = postObjectMgmt.getById(id);
//                } catch (SQLException ex) {
//                    Logger.getLogger(PostList.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//                if (post == null || !hasPermission(user, post)) {
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
//                    return;
//                }
//
//                request.setAttribute("post", post);
//                request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/Edit.jsp").forward(request, response);
            }
            default -> {

            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            response.sendError(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//        if (!isAdminOrEditor(user)) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }

        String action = request.getParameter("action");

//        try {
//            if (action.equals("create")) {
//                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
//
//                Post p = new Post(
//                        categoryId
//                );
//                p.setUserId(user.getId());
//                p.setCategoryId(categoryId);
//                p.setTitle(request.getParameter("title"));
//                p.setContent(request.getParameter("content"));
//
//                postObjectMgmt.insert(p);
//            }

//            if (action.equals("edit")) {
//                int id = Integer.parseInt(request.getParameter("id"));
//                Post post = postObjectMgmt.getById(id);
//
//                if (!hasPermission(user, post)) {
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
//                    return;
//                }
//
//                post.setTitle(request.getParameter("title"));
//                post.setContent(request.getParameter("content"));
//                postObjectMgmt.update(post);
//            }
//
//            if (action.equals("hide")) {
//                int id = Integer.parseInt(request.getParameter("id"));
//                Post post = postObjectMgmt.getById(id);
//
//                if (!hasPermission(user, post)) {
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
//                    return;
//                }
//
//                postObjectMgmt.hide(id);
//            }

//            response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
//
//        } catch (NumberFormatException e) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//        }
    }
}
