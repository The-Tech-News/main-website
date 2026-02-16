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
    
    public Post GetPostById(int postId) {
        Post post = null;
        
        String sqlCommand = """
                            SELECT [id], [userId], [categoryId], [title], [content], [isHidden]
                                FROM [technewsdb].[dbo].[Post]
                                WHERE [id] = ?;
                            """;
        
        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    post = new Post(
                            rs.getInt("id"),
                            rs.getInt("userId"),
                            rs.getInt("categoryId"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getBoolean("isHidden")
                    );
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }
        
        return post;
    }

    public int InsertPost(String title, String content, int userId, int categoryId) {
        int returnValue = -1;
        
        String sqlCommand = """
                            DECLARE	@return_value int;
                            EXEC	@return_value = [technewsdb].[dbo].[sp_InsertPost]
                                        @title = ?,
                                        @content = ?,
                                        @userId = ?,
                                        @categoryId = ?;
                            SELECT	'retval' = @return_value;
                            """;

        try (PreparedStatement ps = getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, userId);
            ps.setInt(4, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    returnValue = rs.getInt("retval");
                }
            } catch (SQLException sqlEx) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }
        
        return returnValue;
    }

    public int UpdatePost(int id, String title, String content, int categoryId) {
        int returnValue = -1;
        
        String sqlCommand = """
                            DECLARE	@return_value int;
                            EXEC	@return_value = [technewsdb].[dbo].[sp_UpdatePost]
                                        @postId = ?,
                                        @title = ?,
                                        @content = ?,
                                        @categoryId = ?;
                            SELECT	'retval' = @return_value;
                            """;

        try (PreparedStatement ps = getConnection().prepareStatement(sqlCommand)) {
            ps.setInt(1, id);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.setInt(4, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    returnValue = rs.getInt("retval");
                }
            } catch (SQLException sqlEx) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }
        
        return returnValue;
    }

    public void hide(int id) throws SQLException {
        String sql = "UPDATE PostList SET hidden = 1 WHERE id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
