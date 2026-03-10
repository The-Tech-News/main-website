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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "Statistic", urlPatterns = {"/admin/stat"})
public class Statistic extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(Statistic.class.getName());

    private final StatisticDAO statDAO = new StatisticDAO();
    private final String numberRegex = "^[0-9]+$";

    private boolean IsAdmin(User u) {
        return u != null && u.getGroupId() == 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);
            User loggedUser = (session == null) ? null : (User) session.getAttribute("loggedUser");

            // Admin only
            if (!IsAdmin(loggedUser)) {
                response.sendError(404);
                return;
            }

            String action = request.getParameter("action");
            if (action == null) {
                action = "list";
            }

            if (!action.equals("list")) {
                response.sendError(404);
                return;
            }

            String topStr = request.getParameter("top");
            ArrayList<int[]> stats;

            if (topStr != null && !topStr.isBlank()) {
                if (!topStr.matches(numberRegex)) {
                    response.sendError(400);
                    return;
                }

                int top = Integer.parseInt(topStr);
                if (top <= 0) {
                    response.sendError(400);
                    return;
                }

                stats = statDAO.GetTop(top);
                request.setAttribute("top", top);
            } else {
                stats = statDAO.GetAll();
            }

            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/WEB-INF/JSPViews/StatisticView/List.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Statistic servlet error", e);
            response.sendError(500);
        }
    }
}
