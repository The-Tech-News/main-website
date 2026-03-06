package Controllers;

import Models.DAO.StatisticDAO;
import Models.Objects.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "Statistic", urlPatterns = {"/admin/stat"})
public class Statistic extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final StatisticDAO statDAO = new StatisticDAO();
    private final String numberRegex = "^[0-9]+$";

    private boolean IsAdmin(User u) {
        return (u != null && u.getGroupId() == 1);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (!IsAdmin((User) session.getAttribute("loggedUser"))) {
            response.sendError(404);
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "list";
        if (!action.equals("list")) {
            response.sendError(404);
            return;
        }

        String topStr = request.getParameter("top");
        ArrayList<int[]> stats;

        if (topStr != null && !topStr.isBlank()) {
            if (!topStr.matches(numberRegex)) {
                response.sendError(400, "Invalid top");
                return;
            }
            int top;
            try {
                top = Integer.parseInt(topStr);
            } catch (NumberFormatException e) {
                response.sendError(400, "Invalid top");
                return;
            }
            if (top <= 0) {
                response.sendError(400, "top must be > 0");
                return;
            }

            stats = statDAO.GetTop(top);
            request.setAttribute("top", top);
        } else {
            stats = statDAO.GetAll();
        }

        request.setAttribute("stats", stats);
        request.getRequestDispatcher("/WEB-INF/JSPViews/StatisticView/List.jsp").forward(request, response);
    }
}