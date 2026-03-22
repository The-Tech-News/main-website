package Controllers;

import Models.DAO.AdsDAO;
import Models.Objects.Ads;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "Ads", urlPatterns = {"/ads", "/admin/ads"})
public class Ads extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Ads.class.getName());

    private final AdsDAO adsDAO;

    public Ads() {
        this.adsDAO = new AdsDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Kiểm tra quyền admin nếu truy cập đường dẫn /admin
        if (request.getServletPath().startsWith("/admin")) {

            // Lấy session hiện tại (false = không tạo session mới)
            HttpSession session = request.getSession(false);

            // Nếu chưa login hoặc chưa có groupId -> chuyển về trang login
            if (session == null || session.getAttribute("groupId") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Lấy groupId từ session
            int groupId = (int) session.getAttribute("groupId");

            // Nếu không phải admin (groupId != 1) -> về trang chủ
            if (groupId != 1) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
        }

        // Lấy tham số action trên URL
        String action = request.getParameter("action");

        // ========================
        // /ads?id=&postId=
        // ========================
        if (request.getServletPath().equals("/ads")) {

            List<Ads> list;

            try {
                int postId = Integer.parseInt(request.getParameter("postId"));
                list = adsDAO.getAdsByPostId(postId);
            } catch (NumberFormatException e) {
                list = new java.util.ArrayList<>();
            }

            request.setAttribute("adsList", list);
            request.getRequestDispatcher("/WEB-INF/JSPViews/AdsView/list.jsp")
                    .forward(request, response);
            return;
        }

        // ========================
        // /admin/ads
        // ========================
        // Nếu action null hoặc action=list -> hiển thị danh sách ads
        if (action == null || action.equals("list")) {

            // Lấy toàn bộ ads từ database
            List<Ads> list = adsDAO.getAllAds();

            // Gửi danh sách ads sang JSP
            request.setAttribute("adsList", list);

            // Forward sang trang list.jsp
            request.getRequestDispatcher("/WEB-INF/JSPViews/AdsView/list.jsp")
                    .forward(request, response);
        } // ========================
        // Mở form create ads
        // ========================
        else if (action.equals("create")) {

            // Forward sang trang create.jsp
            request.getRequestDispatcher("/WEB-INF/JSPViews/AdsView/create.jsp")
                    .forward(request, response);
        } // ========================
        // Mở form edit ads
        // ========================
        else if (action.equals("edit")) {

            // Lấy id ads cần chỉnh sửa
            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
                return;
            }
            // Lấy thông tin ads theo id
            Ads ads = adsDAO.getAdsById(id);

            // Gửi dữ liệu ads sang JSP
            request.setAttribute("ads", ads);

            // Forward sang trang edit.jsp
            request.getRequestDispatcher("/WEB-INF/JSPViews/AdsView/edit.jsp")
                    .forward(request, response);
        } // ========================
        // Delete ads
        // ========================
        else if (action.equals("delete")) {

            // Lấy id ads cần xóa
            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
                return;
            }
            // Gọi DAO để xóa ads trong database
            adsDAO.deleteAds(id);

            // Redirect về danh sách ads
            response.sendRedirect("ads?action=list");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy action từ form
        String action = request.getParameter("action");

        // ========================
        // Create Ads
        // ========================
        if (action.equals("create")) {

            // Lấy dữ liệu từ form
            int postId;
            try {
                postId = Integer.parseInt(request.getParameter("postId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid postId");
                return;
            }
            String title = request.getParameter("title");
            String uriImage = request.getParameter("uriImage");

            // Tạo object Ads mới
            Ads newAd = new Ads(0, postId, title, uriImage);

            // Insert ads vào database
            adsDAO.insertAds(newAd);

            // Redirect về danh sách ads
            response.sendRedirect("ads?action=list");
        } // ========================
        // Edit Ads
        // ========================
        else if (action.equals("edit")) {

            // Lấy dữ liệu từ form
            int id, postId;
            try {
                id = Integer.parseInt(request.getParameter("id"));
                postId = Integer.parseInt(request.getParameter("postId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data");
                return;
            }
            String title = request.getParameter("title");
            String uriImage = request.getParameter("uriImage");

            // Tạo object Ads cập nhật
            Ads updatedAd = new Ads(id, postId, title, uriImage);

            // Update ads trong database
            adsDAO.updateAds(updatedAd);

            // Redirect về danh sách ads
            response.sendRedirect("ads?action=list");
        }
    }
}
