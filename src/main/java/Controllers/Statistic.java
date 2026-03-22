package Controllers;

import java.io.IOException;
import java.util.ArrayList;

import Models.DAO.StatisticDAO;
import Models.Objects.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Statistic", urlPatterns = {"/admin/stat"})
public class Statistic extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient final StatisticDAO statDAO;

    public Statistic() {
        this.statDAO = new StatisticDAO();
    }

    private boolean IsAdmin(User u) {
        return (u != null && u.getGroupId() == 1);
    }

    private void GetTop(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String topStr = request.getParameter("top");
            ArrayList<int[]> stats
                    = (topStr != null && !topStr.isBlank() && topStr.matches("^[0-9]+$"))
                    ? statDAO.GetTop(Integer.parseInt(topStr))
                    : statDAO.GetAll();
            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/WEB-INF/JSPViews/StatisticView/List.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(400);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (session == null) ? null : (User) session.getAttribute("loggedUser");

        if (!IsAdmin(loggedUser)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.getRequestDispatcher("/WEB-INF/JSPViews/StatisticView/NoPermission.jsp").forward(request, response);
            return;
        }

        switch (request.getParameter("action")) {
            case null -> {
                response.sendRedirect(request.getContextPath() + "/admin/stat?action=list");
            }
            case "list" -> {
                this.GetTop(request, response);
            }
            default -> {
                response.sendRedirect(request.getContextPath() + "/admin/stat?action=list");
            }
        }
    }
}
