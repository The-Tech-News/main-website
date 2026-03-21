package Models.DAO;

import Models.DBContext;
import Models.Objects.Ads;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp AdsDAO dùng để thao tác với database bảng Ads.
 *
 * DAO (Data Access Object) có nhiệm vụ: - Kết nối database - Thực hiện các câu
 * lệnh SQL - Chuyển dữ liệu từ database thành object Ads
 *
 * Các chức năng chính: - Lấy tất cả quảng cáo - Lấy quảng cáo theo id - Lấy
 * quảng cáo theo postId - Thêm quảng cáo - Cập nhật quảng cáo - Xóa quảng cáo
 *
 * @author PC
 */
public class AdsDAO {

    // Lấy tất cả quảng cáo
    /**
     * Lấy toàn bộ quảng cáo trong bảng Ads.
     *
     * @return danh sách các quảng cáo
     */
    public List<Ads> getAllAds() {

	List<Ads> list = new ArrayList<>();
	String sql = "SELECT id, postId, title, uriImage FROM Ads";

	try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

	    while (rs.next()) {
		Ads ads = new Ads(
			rs.getInt("id"),
			rs.getInt("postId"),
			rs.getString("title"),
			rs.getString("uriImage")
		);
		list.add(ads);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
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

	try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

	} catch (Exception e) {
	    e.printStackTrace();
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

	try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

	} catch (Exception e) {
	    e.printStackTrace();
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

	try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	    ps.setInt(1, ads.getPostId());
	    ps.setString(2, ads.getTitle());
	    ps.setString(3, ads.getUriImage());
	    ps.executeUpdate();
	} catch (Exception e) {
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
	String sql = "UPDATE Ads SET postId=?, title=?, uriImage=? WHERE id=?";

	try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	    ps.setInt(1, ads.getPostId());
	    ps.setString(2, ads.getTitle());
	    ps.setString(3, ads.getUriImage());
	    ps.setInt(4, ads.getId());
	    ps.executeUpdate();
	} catch (Exception e) {
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
	String sql = "DELETE FROM Ads WHERE id=?";

	try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	    ps.setInt(1, id);
	    ps.executeUpdate();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
