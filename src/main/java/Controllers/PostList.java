package Controllers;

import Models.DAO.CategoryDAO;
import Models.DAO.PostListDAO;
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

@WebServlet(name = "PostList", urlPatterns = {"/admin/posts"})
public class PostList extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private final PostListDAO postObjectMgmt;
    private final CategoryDAO categoryObjectMgmt;

    private final String numberRegex = "^[0-9]+$";
    
    public PostList() {
        this.postObjectMgmt = new PostListDAO();
        this.categoryObjectMgmt = new CategoryDAO();
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
        
        return (u.getId() == 1);
    }
    
    private void CreatePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        int userId = -1;
        int categoryId = -1;
        
        try {
            String userIdStr = request.getParameter("userId");
            String categoryIdStr = request.getParameter("categoryId");
            
            if (userIdStr == null || categoryIdStr == null) {
                response.sendError(500, "Either User ID or Category ID is not specified");
                return;
            }
            
            if (!userIdStr.matches(this.numberRegex) || !categoryIdStr.matches(this.numberRegex)) {
                response.sendError(500, "Either User ID or Category ID must be a number");
                return;
            }
            
            userId = Integer.parseInt(userIdStr);
            categoryId = Integer.parseInt(categoryIdStr);
        } catch (NumberFormatException numex) {
            response.sendError(500, "Unable to parse Id as integer");
            return;
        }
        
        if (title == null || content == null) {
            response.sendError(500, "Either title or content should be not null");
            return;
        }
        
        if (userId == -1 || categoryId == -1) {
            response.sendError(500, "Either title or content should be not null");
            return;
        }
        
        int executeQue = this.postObjectMgmt.InsertPost(title, content, userId, categoryId);
        if (executeQue != 0) {
            response.sendError(500, "The category could not be editied");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/posts?action=list");
        }
    }
    
    private void EditPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User u = (User) session.getAttribute("loggedUser");
        
        if (!this.IsAuthenticated(session)) {
            request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
            return;
        }
        
        switch (request.getParameter("action")) {
            case null -> {
                response.sendRedirect("/admin/posts?action=list");
            }
            case "list" -> {
                ArrayList<Post> posts = (IsAdmin(session)) ? postObjectMgmt.GetAll() : postObjectMgmt.GetPostsByUserId(u.getId());
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
        HttpSession session = request.getSession(false);
        User u = (User) session.getAttribute("loggedUser");
        
        if (!this.IsAuthenticated(session)) {
            request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
            return;
        }
        
        switch (request.getParameter("action")) {
            case "create" -> {
                this.CreatePost(request, response);
            }
            case "edit" -> {
                
            }
            case "hide" -> {
                
            }
            default -> {
                response.setStatus(404);
                request.getRequestDispatcher("/WEB-INF/JSPViews/PostListView/NoPermission.jsp").forward(request, response);
            }
        }
    }
}
