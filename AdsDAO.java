package Models.DAO;

import Models.DBContext;
import Models.Objects.Ads;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp AdsDAO dùng để thao tác với database bảng Ads.
 * 
 * DAO (Data Access Object) có nhiệm vụ:
 * - Kết nối database
 * - Thực hiện các câu lệnh SQL
 * - Chuyển dữ liệu từ database thành object Ads
 * 
 * Các chức năng chính:
 * - Lấy tất cả quảng cáo
 * - Lấy quảng cáo theo id
 * - Lấy quảng cáo theo postId
 * - Thêm quảng cáo
 * - Cập nhật quảng cáo
 * - Xóa quảng cáo
 * 
 * @author PC
 */
public class AdsDAO {

    // Biến connection dùng để kết nối database
    Connection conn;

    /**
     * Constructor của AdsDAO.
     * 
     * Khi tạo AdsDAO sẽ tự động:
     * - Tạo DBContext
     * - Lấy connection từ DBContext
     */
    public AdsDAO() {

        // Lấy connection từ lớp DBContext
        conn = new DBContext().getConnection();
    }

    // Lấy tất cả quảng cáo

    /**
     * Lấy toàn bộ quảng cáo trong bảng Ads.
     *
     * @return danh sách các quảng cáo
     */
    public List<Ads> getAllAds() {

        // Tạo list để lưu danh sách quảng cáo
        List<Ads> list = new ArrayList<>();

        // Câu lệnh SQL lấy toàn bộ bảng Ads
        String sql = "SELECT * FROM Ads";

        try {

            // Tạo PreparedStatement để thực thi SQL
            PreparedStatement ps = conn.prepareStatement(sql);

            // Thực hiện truy vấn và nhận kết quả
            ResultSet rs = ps.executeQuery();

            // Duyệt từng dòng dữ liệu trong ResultSet
            while (rs.next()) {

                // Tạo object Ads từ dữ liệu database
                Ads ads = new Ads(
                        rs.getInt("id"),        // lấy id
                        rs.getInt("postId"),    // lấy postId
                        rs.getString("title"),  // lấy title
                        rs.getString("uriImage")// lấy đường dẫn ảnh
                );

                // Thêm ads vào list
                list.add(ads);
            }

        } catch (Exception e) {

            // In lỗi ra console để debug
            e.printStackTrace();
        }

        // Trả về danh sách quảng cáo
        return list;
    }

    // Lấy quảng cáo theo ID

    /**
     * Lấy quảng cáo theo id.
     *
     * @param id id của quảng cáo cần tìm
     * @return object Ads nếu tồn tại, ngược lại trả về null
     */
    public Ads getAdsById(int id) {

        // SQL tìm quảng cáo theo id
        String sql = "SELECT * FROM Ads WHERE id = ?";

        try {

            // Tạo PreparedStatement
            PreparedStatement ps = conn.prepareStatement(sql);

            // Gán giá trị id vào dấu ?
            ps.setInt(1, id);

            // Thực thi query
            ResultSet rs = ps.executeQuery();

            // Nếu có dữ liệu
            if (rs.next()) {

                // Trả về object Ads
                return new Ads(
                        rs.getInt("id"),
                        rs.getInt("postId"),
                        rs.getString("title"),
                        rs.getString("uriImage")
                );
            }

        } catch (Exception e) {

            // In lỗi ra console
            e.printStackTrace();
        }

        // Nếu không tìm thấy trả về null
        return null;
    }

    // Lấy quảng cáo theo postId

    /**
     * Lấy danh sách quảng cáo theo postId.
     *
     * @param postId id của bài viết
     * @return danh sách quảng cáo thuộc bài viết đó
     */
    public List<Ads> getAdsByPostId(int postId) {

        // Tạo list lưu kết quả
        List<Ads> list = new ArrayList<>();

        // SQL tìm quảng cáo theo postId
        String sql = "SELECT * FROM Ads WHERE postId = ?";

        try {

            // Tạo PreparedStatement
            PreparedStatement ps = conn.prepareStatement(sql);

            // Gán giá trị postId
            ps.setInt(1, postId);

            // Thực thi query
            ResultSet rs = ps.executeQuery();

            // Duyệt từng dòng dữ liệu
            while (rs.next()) {

                // Tạo object Ads từ dữ liệu database
                Ads ads = new Ads(
                        rs.getInt("id"),
                        rs.getInt("postId"),
                        rs.getString("title"),
                        rs.getString("uriImage")
                );

                // Thêm vào danh sách
                list.add(ads);
            }

        } catch (Exception e) {

            // In lỗi ra console
            e.printStackTrace();
        }

        // Trả về danh sách quảng cáo
        return list;
    }

    // Thêm quảng cáo

    /**
     * Thêm quảng cáo mới vào database.
     *
     * @param ads object Ads chứa dữ liệu cần thêm
     */
    public void insertAds(Ads ads) {

        // SQL insert dữ liệu vào bảng Ads
        String sql = "INSERT INTO Ads(postId, title, uriImage) VALUES (?, ?, ?)";

        try {

            // Tạo PreparedStatement
            PreparedStatement ps = conn.prepareStatement(sql);

            // Gán dữ liệu vào các dấu ?
            ps.setInt(1, ads.getPostId());
            ps.setString(2, ads.getTitle());
            ps.setString(3, ads.getUriImage());

            // Thực thi lệnh insert
            ps.executeUpdate();

        } catch (Exception e) {

            // In lỗi ra console
            e.printStackTrace();
        }
    }

    // Cập nhật quảng cáo

    /**
     * Cập nhật thông tin quảng cáo.
     *
     * @param ads object Ads chứa dữ liệu cần cập nhật
     */
    public void updateAds(Ads ads) {

        // SQL update quảng cáo theo id
        String sql = "UPDATE Ads SET postId=?, title=?, uriImage=? WHERE id=?";

        try {

            // Tạo PreparedStatement
            PreparedStatement ps = conn.prepareStatement(sql);

            // Gán dữ liệu vào câu SQL
            ps.setInt(1, ads.getPostId());
            ps.setString(2, ads.getTitle());
            ps.setString(3, ads.getUriImage());
            ps.setInt(4, ads.getId());

            // Thực thi update
            ps.executeUpdate();

        } catch (Exception e) {

            // In lỗi ra console
            e.printStackTrace();
        }
    }

    // Xóa quảng cáo

    /**
     * Xóa quảng cáo khỏi database.
     *
     * @param id id quảng cáo cần xóa
     */
    public void deleteAds(int id) {

        // SQL xóa quảng cáo theo id
        String sql = "DELETE FROM Ads WHERE id=?";

        try {

            // Tạo PreparedStatement
            PreparedStatement ps = conn.prepareStatement(sql);

            // Gán id cần xóa
            ps.setInt(1, id);

            // Thực thi lệnh delete
            ps.executeUpdate();

        } catch (Exception e) {

            // In lỗi ra console
            e.printStackTrace();
        }
    }
}