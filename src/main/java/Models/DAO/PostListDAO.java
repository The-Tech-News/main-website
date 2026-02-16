package Models.DAO;

import Models.DBContext;
import Models.Objects.Post;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostListDAO extends DBContext {

    public ArrayList<Post> GetAll() {
        ArrayList<Post> list = new ArrayList<>();

        String sqlCommand = """
                            SELECT [id],[userId], [categoryId], [title], [content], [isHidden]
                                FROM [technewsdb].[dbo].[Post];
                            """;

        try (PreparedStatement ps = getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Post(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getInt("categoryId"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getBoolean("isHidden")
                ));
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return list;
    }

    public ArrayList<Post> GetPostsByUserId(int userId) {
        ArrayList<Post> posts = new ArrayList<>();

        String sqlCommand = """
                            SELECT [id], [userId], [categoryId], [title], [content], [isHidden]
                                FROM [technewsdb].[dbo].[Post]
                                WHERE [userId] = ?;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(
                            rs.getInt("id"),
                            rs.getInt("userId"),
                            rs.getInt("categoryId"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getBoolean("isHidden")
                    ));
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return posts;
    }

    public void insert(Post p) {
        String sql = """
                    DECLARE	@return_value int;
                    EXEC	@return_value = [technewsdb].[dbo].[sp_InsertPost]
                                @title = ?,
                                @content = ?,
                                @userId = ?,
                                @categoryId = ?;
                    SELECT	'retval' = @return_value;
                    """;

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, p.getUserId());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getTitle());
            ps.setString(4, p.getContent());
            ps.executeUpdate();
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }
    }

    public void update(Post p) throws SQLException {
        String sql = """
                        UPDATE PostList
                        SET title = ?, content = ?
                        WHERE id = ?
                    """;

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getContent());
            ps.setInt(3, p.getId());
            ps.executeUpdate();
        }
    }

    public void hide(int id) throws SQLException {
        String sql = "UPDATE PostList SET hidden = 1 WHERE id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
