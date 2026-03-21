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

    public ArrayList<Post> GetHomePosts() {
        ArrayList<Post> list = new ArrayList<>();
        String sql = """
                SELECT [id], [userId], [categoryId], [title], [content], [isHidden]
                    FROM [technewsdb].[dbo].[Post]
                    WHERE [isHidden] = 0
                    ORDER BY [id] DESC;
            """;

        try (
                PreparedStatement ps = super.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(
                        new Post(
                                rs.getInt("id"),
                                rs.getInt("userId"),
                                rs.getInt("categoryId"),
                                rs.getString("title"),
                                rs.getString("content"),
                                rs.getBoolean("isHidden")
                        ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public Post GetPostId(int id) {
        Post p = null;

        String sql = """
                SELECT TOP(1) [id], [userId], [categoryId], [title], [content], [isHidden]
                    FROM [technewsdb].[dbo].[Post]
                    WHERE [isHidden] = 0 AND [id] = ?
                    ORDER BY [id] DESC;
            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new Post(
                            rs.getInt("id"),
                            rs.getInt("userId"),
                            rs.getInt("categoryId"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getBoolean("isHidden")
                    );
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        return p;
    }
    
}
