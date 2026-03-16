package Models.DAO;

import Models.DBContext;
import Models.Objects.Comment;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentDAO extends DBContext {

    // Get comment by postId
    public List<Comment> getByPostId(int postId) {
        List<Comment> list = new ArrayList<>();

        String sql = """
            SELECT id, userId, postId, content, createdAt, isHidden
                FROM Comment
                WHERE postId = ? AND isHidden = 0
                ORDER BY createdAt ASC;
        """;

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Comment(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getInt("postId"),
                        rs.getString("content"),
                        rs.getTimestamp("createdAt"),
                        rs.getBoolean("isHidden")
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    // Create comment
    public void create(int userId, int postId, String content) {
        String sql = "{call sp_CreateComment(?, ?, ?)}";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            ps.setString(3, content);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Get comment by id
    public Comment getById(int id) {
        String sql = """
            SELECT id, userId, postId, content, createdAt, isHidden
            FROM Comment
            WHERE id = ?
        """;

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Comment(
                            rs.getInt("id"),
                            rs.getInt("userId"),
                            rs.getInt("postId"),
                            rs.getString("content"),
                            rs.getTimestamp("createdAt"),
                            rs.getBoolean("isHidden")
                    );
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    // Hide comment
    public void hide(int commentId, int requesterId) {
        String sql = "{call sp_HideComment(?, ?)}";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setInt(2, requesterId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
