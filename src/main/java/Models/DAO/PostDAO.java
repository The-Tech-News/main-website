package Models.DAO;

import db.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Post;

public class PostDAO extends DBContext {

    public List<Post> getAll() throws SQLException {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM PostList WHERE hidden = 0";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Post p = new Post(
                rs.getInt("id"),
                rs.getInt("userId"),
                rs.getInt("categoryId"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getBoolean("hidden")
            );
            list.add(p);
        }
        return list;
    }

    public void insert(Post p) throws SQLException {
        String sql = """
            INSERT INTO PostList(userId, categoryId, title, content, hidden)
            VALUES (?, ?, ?, ?, 0)
        """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, p.getUserId());
        ps.setInt(2, p.getCategoryId());
        ps.setString(3, p.getTitle());
        ps.setString(4, p.getContent());
        ps.executeUpdate();
    }

    public Post getById(int id) throws SQLException {
        String sql = "SELECT * FROM PostList WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

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
        return null;
    }

    public void update(Post p) throws SQLException {
        String sql = """
            UPDATE PostList
            SET title = ?, content = ?
            WHERE id = ?
        """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getTitle());
        ps.setString(2, p.getContent());
        ps.setInt(3, p.getId());
        ps.executeUpdate();
    }

    public void hide(int id) throws SQLException {
        String sql = "UPDATE PostList SET hidden = 1 WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
}
