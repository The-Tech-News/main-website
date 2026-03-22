package Controllers;

import java.io.IOException;
import java.util.ArrayList;

import Models.DAO.CategoryDAO;
import Models.DAO.PostListDAO;
import Models.DAO.UserDAO;
import Models.Objects.Post;
import Models.Objects.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "PostList", urlPatterns = {"/admin/posts"})
public class PostList extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final String numberRegex = "^[0-9]+$";
    private transient final PostListDAO postObjectMgmt;
    private transient final CategoryDAO categoryObjectMgmt;
    private transient final UserDAO userDao;

    public PostList() {
        this.postObjectMgmt = new PostListDAO();
        this.categoryObjectMgmt = new CategoryDAO();
        this.userDao = new UserDAO();
    }

    private boolean IsAuthenticated(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        return (u != null);
    }

    private boolean IsAdmin(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        if (!this.IsAuthenticated(session)) {
            return false;
        }

        return (u.getGroupId() == 1);
    }

    private boolean HasAccessToPost(HttpSession session, int postId) {
        if (!IsAuthenticated(session)) {
            return false;
        }

        User u = (User) session.getAttribute("loggedUser");

        if (IsAdmin(session)) {
            return true;
        }

        Post post = postObjectMgmt.GetPostById(postId);
        if (post == null) {
            return false;
        }

        return post.getUserId() == u.getId();
    }

    private void CreatePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || !IsAuthenticated(session)) {
            response.sendError(401);
            return;
        }

        User u = (User) session.getAttribute("loggedUser");

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryIdStr = request.getParameter("categoryId");

        if (title == null || content == null || title.trim().isEmpty() || content.trim().isEmpty()) {
            response.sendError(400);
            return;
        }

        if (categoryIdStr == null) {
            response.sendError(400);
            return;
        }

        int userId = u.getId();

        try {
            int categoryId = Integer.parseInt(categoryIdStr);
            int executeQue = this.postObjectMgmt.InsertPost(
                    title.trim(),
                    content.trim(),
                    userId,
                    categoryId
            );
            if (executeQue != 0) {
                response.sendError(500, "Unable to create post");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
            }
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid Category ID");
        }
    }

    private void EditPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (title == null || content == null) {
            response.sendError(400, "Either title or content should be not null");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));

            int executeQue = this.postObjectMgmt.UpdatePost(id, title, content, categoryId);
            if (executeQue != 0) {
                response.sendError(500, "The category could not be editied");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
            }
        } catch (NumberFormatException numex) {
            response.sendError(400);
        }
    }

    private void HidePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            int executeQue = this.postObjectMgmt.HidePost(id);
            
            if (executeQue != 0) {
                response.sendError(500, "The category could not be hiden");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
            }
        } catch (NumberFormatException numex) {
            response.sendError(400, "Unable to parse Id as integer");
            return;
        }

    }

    private void UnHidePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            int executeQue = this.postObjectMgmt.UnhidePost(id);
            
            if (executeQue != 0) {
                response.sendError(500);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
            }
        } catch (NumberFormatException numex) {
            response.sendError(400, "Unable to parse Id as integer");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || !IsAuthenticated(session)) {
            response.setStatus(401);
            request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
            return;
        }

        User u = (User) session.getAttribute("loggedUser");

        switch (request.getParameter("action")) {
            case null -> {
                response.sendRedirect("/admin/posts?action=list");
            }
            case "list" -> {
                ArrayList<Post> posts = (IsAdmin(session)) ? postObjectMgmt.GetAll() : postObjectMgmt.GetPostsByUserId(u.getId());
                request.setAttribute("users", this.userDao.GetUserName());
                request.setAttribute("posts", posts);
                request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/List.jsp").forward(request, response);
            }
            case "create" -> {
                ArrayList<Models.Objects.Category> categories = categoryObjectMgmt.GetListCategory();
                request.setAttribute("categories", categories);
                request.setAttribute("userId", u.getId());
                request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/Create.jsp").forward(request, response);
            }
            case "edit" -> {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (!HasAccessToPost(session, id)) {
                        request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
                        return;
                    }

                    Post post = this.postObjectMgmt.GetPostById(id);
                    ArrayList<Models.Objects.Category> categories = categoryObjectMgmt.GetListCategory();
                    request.setAttribute("categories", categories);
                    request.setAttribute("post", post);
                    request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/Edit.jsp").forward(request, response);
                } catch (NumberFormatException numEx) {
                    response.sendError(400);
                }
            }
            case "hide" -> {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (!HasAccessToPost(session, id)) {
                        request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
                        return;
                    }
                    request.setAttribute("id", id);
                    request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/Hide.jsp").forward(request, response);

                } catch (NumberFormatException numEx) {
                    response.sendError(400);
                }
            }
            case "unhide" -> {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (!HasAccessToPost(session, id)) {
                        request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
                        return;
                    }
                    request.setAttribute("id", id);
                    request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/UnHide.jsp").forward(request, response);

                } catch (NumberFormatException numEx) {
                    response.sendError(400);
                }
            }
            default -> {
                request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || !IsAuthenticated(session)) {
            request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
            return;
        }

        switch (request.getParameter("action")) {
            case "create" -> {
                this.CreatePost(request, response);
            }
            case "edit" -> {
                String idStr = request.getParameter("id");
                if (idStr == null || !idStr.matches(numberRegex)) {
                    response.sendError(400);
                    return;
                }

                try {
                    int id = Integer.parseInt(idStr);
                    if (!HasAccessToPost(session, id)) {
                        request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
                        return;
                    }
                    this.EditPost(request, response);
                } catch (NumberFormatException e) {
                    response.sendError(400);
                }
            }
            case "hide" -> {
                String idStr = request.getParameter("id");
                if (idStr == null || !idStr.matches(numberRegex)) {
                    response.sendError(400);
                    return;
                }

                try {
                    int id = Integer.parseInt(idStr);
                    if (!HasAccessToPost(session, id)) {
                        request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
                        return;
                    }
                    this.HidePost(request, response);
                } catch (NumberFormatException e) {
                    response.sendError(400);
                }
            }
            case "unhide" -> {
                String idStr = request.getParameter("id");
                if (idStr == null || !idStr.matches(numberRegex)) {
                    response.sendError(400);
                    return;
                }

                try {
                    int id = Integer.parseInt(idStr);
                    if (!HasAccessToPost(session, id)) {
                        request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
                        return;
                    }
                    this.UnHidePost(request, response);
                } catch (NumberFormatException e) {
                    response.sendError(400);
                }
            }
            default -> {
                response.setStatus(404);
                request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
            }
        }
    }
}
