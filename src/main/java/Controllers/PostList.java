package Controllers;

import dao.PostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.Post;

@WebServlet("/admin/posts")
public class PostListServlet extends HttpServlet {

    private PostDAO dao = new PostDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                List<Post> list = dao.getAll();
                request.setAttribute("posts", list);
                request.getRequestDispatcher("/WEB-INF/PostListView/list.jsp")
                        .forward(request, response);

            } else if (action.equals("create")) {
                request.getRequestDispatcher("/WEB-INF/PostListView/create.jsp")
                        .forward(request, response);

            } else if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Post p = dao.getById(id);
                request.setAttribute("post", p);
                request.getRequestDispatcher("/WEB-INF/PostListView/edit.jsp")
                        .forward(request, response);
            }
        } catch (Exception e) {
            response.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action.equals("create")) {
                Post p = new Post();
                p.setUserId(Integer.parseInt(request.getParameter("userId")));
                p.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
                p.setTitle(request.getParameter("title"));
                p.setContent(request.getParameter("content"));

                dao.insert(p);
                response.sendRedirect("posts?action=list");

            } else if (action.equals("edit")) {
                Post p = new Post();
                p.setId(Integer.parseInt(request.getParameter("id")));
                p.setTitle(request.getParameter("title"));
                p.setContent(request.getParameter("content"));

                dao.update(p);
                response.sendRedirect("posts?action=list");

            } else if (action.equals("hide")) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.hide(id);
                response.sendRedirect("posts?action=list");
            }
        } catch (Exception e) {
            response.sendError(500);
        }
    }
}
