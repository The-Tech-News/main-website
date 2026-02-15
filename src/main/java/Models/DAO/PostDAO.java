package Models.DAO;

import Models.DBContext;
import Models.Objects.Post;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostDAO extends DBContext {

    public ArrayList<Post> GetAll() {
        ArrayList<Post> list = new ArrayList<>();

        String sql = """
                    SELECT [id],[userId], [categoryId], [title], [content], [isHidden]
                        FROM [technewsdb].[dbo].[Post];
                    """;

        try (PreparedStatement ps = getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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

    public Post getById(int id) throws SQLException {
        String sql = "SELECT * FROM PostList WHERE id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Post(
                            rs.getInt("id"),
                            rs.getInt("userId"),
                            rs.getInt("categoryId"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getBoolean("hidden")
                    );
                }
            }
        }
        return null;
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
