package Models.DAO;

import Models.DBContext;
import Models.Objects.Post;
import Models.Objects.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostDAO extends DBContext {

    private Post MapPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getInt("id"),
                rs.getInt("userId"),
                rs.getInt("categoryId"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getBoolean("isHidden")
        );
    }

    // Home list: public posts + (if logged in) own hidden + (if admin) all
    public ArrayList<Post> GetHomePosts(User viewer) {
        ArrayList<Post> list = new ArrayList<>();
        String sql;

        if (viewer == null) {
            sql = """
                SELECT [id],[userId],[categoryId],[title],[content],[isHidden]
                FROM [technewsdb].[dbo].[Post]
                WHERE [isHidden] = 0
                ORDER BY [id] DESC;
            """;
            try (PreparedStatement ps = super.getConnection().prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(MapPost(rs));
            } catch (SQLException ex) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
            return list;
        }

        if (viewer.getGroupId() == 1) { // admin
            sql = """
                SELECT [id],[userId],[categoryId],[title],[content],[isHidden]
                FROM [technewsdb].[dbo].[Post]
                ORDER BY [id] DESC;
            """;
            try (PreparedStatement ps = super.getConnection().prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(MapPost(rs));
            } catch (SQLException ex) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
            return list;
        }

        sql = """
            SELECT [id],[userId],[categoryId],[title],[content],[isHidden]
            FROM [technewsdb].[dbo].[Post]
            WHERE [isHidden] = 0 OR ([isHidden] = 1 AND [userId] = ?)
            ORDER BY [id] DESC;
        """;
        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, viewer.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(MapPost(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    // Detail view: returns null if not found OR not allowed
    public Post GetPostForView(int postId, User viewer) {
        Post post = null;

        String sql = """
            SELECT [id],[userId],[categoryId],[title],[content],[isHidden]
            FROM [technewsdb].[dbo].[Post]
            WHERE [id] = ?;
        """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) post = MapPost(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (post == null) return null;

        // visibility rule from #12
        if (!post.isHidden()) return post;
        if (viewer == null) return null;

        boolean isAdmin = viewer.getGroupId() == 1;
        boolean isAuthor = viewer.getId() == post.getUserId();

        return (isAdmin || isAuthor) ? post : null;
    }
}