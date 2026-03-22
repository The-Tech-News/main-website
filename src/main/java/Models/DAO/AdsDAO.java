package Models.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Models.DBContext;
import Models.Objects.Ads;

public class AdsDAO {

    private static final Logger LOGGER = Logger.getLogger(AdsDAO.class.getName());

    // Lấy tất cả quảng cáo
    /**
     * Lấy toàn bộ quảng cáo trong bảng Ads.
     *
     * @return danh sách các quảng cáo
     */
    public List<Ads> getAllAds() {

        List<Ads> list = new ArrayList<>();
        String sql = "SELECT id, postId, title, uriImage FROM Ads";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ads ads = new Ads(
                        rs.getInt("id"),
                        rs.getInt("postId"),
                        rs.getString("title"),
                        rs.getString("uriImage")
                );
                list.add(ads);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all ads", e);
        }

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
        String sql = "SELECT id, postId, title, uriImage FROM Ads WHERE id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ads(
                            rs.getInt("id"),
                            rs.getInt("postId"),
                            rs.getString("title"),
                            rs.getString("uriImage")
                    );
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching ad by ID", e);
        }

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

        List<Ads> list = new ArrayList<>();
        String sql = "SELECT id, postId, title, uriImage FROM Ads WHERE postId = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ads ads = new Ads(
                            rs.getInt("id"),
                            rs.getInt("postId"),
                            rs.getString("title"),
                            rs.getString("uriImage")
                    );
                    list.add(ads);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching ads by postId", e);
        }

        return list;
    }

    // Thêm quảng cáo
    /**
     * Thêm quảng cáo mới vào database.
     *
     * @param ads object Ads chứa dữ liệu cần thêm
     */
    public void insertAds(Ads ads) {
        String sql = "INSERT INTO Ads(postId, title, uriImage) VALUES (?, ?, ?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ads.getPostId());
            ps.setString(2, ads.getTitle());
            ps.setString(3, ads.getUriImage());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting ad", e);
        }
    }
    // Cập nhật quảng cáo

    /**
     * Cập nhật thông tin quảng cáo.
     *
     * @param ads object Ads chứa dữ liệu cần cập nhật
     */
    public void updateAds(Ads ads) {
        String sql = "UPDATE Ads SET postId=?, title=?, uriImage=? WHERE id=?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ads.getPostId());
            ps.setString(2, ads.getTitle());
            ps.setString(3, ads.getUriImage());
            ps.setInt(4, ads.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating ad", e);
        }
    }

    // Xóa quảng cáo
    /**
     * Xóa quảng cáo khỏi database.
     *
     * @param id id quảng cáo cần xóa
     */
    public void deleteAds(int id) {
        String sql = "DELETE FROM Ads WHERE id=?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting ad", e);
        }
    }
}
