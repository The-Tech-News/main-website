package Controllers;

import Models.DAO.CategoryDAO;
import Models.Objects.User;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Category", urlPatterns = {"/admin/category"})
public class Category extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final String nameRegex = "^[A-Za-z0-9]+$";
    private final String descriptionRegex = "^[0-9\\p{L}\\. ]+$";

    private final CategoryDAO categoryObjectMgmt;

    public Category() {
        this.categoryObjectMgmt = new CategoryDAO();
    }

    private void CreateCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");

        if (name == null || description == null) {
            response.sendError(500, "Required parameter is null, please check the input");
            return;
        }

        if (!name.matches(nameRegex) || !description.matches(descriptionRegex)) {
            response.sendError(500, "Required parameter is not in the compliance format. Please check the input");
            return;
        }

        int sqlExec = this.categoryObjectMgmt.NewCategory(name, description);
        if (sqlExec != 0) {
            response.sendError(500, "The category could not be created");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/category?action=list");
        }
    }

    private void EditCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String oldName = request.getParameter("oldName");
        String newName = request.getParameter("newName");
        String description = request.getParameter("description");

        if (oldName == null || newName == null || description == null) {
            response.sendError(500, "Required parameter is null. Please check the input.");
            return;
        }

        if (!oldName.matches(nameRegex) || !newName.matches(nameRegex) || !description.matches(descriptionRegex)) {
            response.sendError(500, "Required parameter is not in the compliance format. Please check the input");
            return;
        }

        int sqlExec = this.categoryObjectMgmt.EditCategory(oldName, newName, description);
        if (sqlExec != 0) {
            response.sendError(500, "The category could not be editied");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/category?action=list");
        }
    }

    private boolean HasPermission(HttpServletRequest request) {
        boolean hasPermission = false;

        User u = (User) request.getSession().getAttribute("loggedUser");

        if (u != null) {
            if (u.getId() == 1) {
                hasPermission = true;
            }
        }

        return hasPermission;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!this.HasPermission(request)) {
            request.getRequestDispatcher("/WEB-INF/JSPViews/CategoryView/NoPermission.jsp").forward(request, response);
        }

        switch (request.getParameter("action")) {
            case null -> {
                response.sendRedirect(request.getContextPath() + "/admin/category?action=list");
            }
            case "list" -> {
                request.setAttribute("CategoryList", this.categoryObjectMgmt.GetListCategory());
                request.getRequestDispatcher("/WEB-INF/JSPViews/CategoryView/List.jsp").forward(request, response);
            }
            case "create" -> {
                request.getRequestDispatcher("/WEB-INF/JSPViews/CategoryView/Create.jsp").forward(request, response);
            }
            case "edit" -> {
                request.getRequestDispatcher("/WEB-INF/JSPViews/CategoryView/Edit.jsp").forward(request, response);
            }
            default -> {
                request.getRequestDispatcher("/WEB-INF/JSPViews/CategoryView/NoPermission.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!this.HasPermission(request)) {
            request.getRequestDispatcher("/WEB-INF/JSPViews/CategoryView/NoPermission.jsp").forward(request, response);
        }

        switch (request.getParameter("action")) {
            case "create" -> {
                this.CreateCategory(request, response);
            }
            case "edit" -> {
                this.EditCategory(request, response);
            }
            default -> {
                response.setStatus(404);
                request.getRequestDispatcher("/WEB-INF/JSPViews/CategoryView/NoPermission.jsp").forward(request, response);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
