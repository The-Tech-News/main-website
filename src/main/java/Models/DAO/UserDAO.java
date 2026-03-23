package Models.DAO;

import Models.DBContext;
import Models.Objects.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DBContext {

    // User Sign-in (SELECT)
    public User GetUserSignIn(String email, String password) {
        User user = null;

        String sqlCommand = """
                            SELECT TOP (1) [id], [email], [pwdHash], [name], [isEnabled], [groupId]
                                FROM [technewsdb].[dbo].[User]
                                WHERE [email] = ? AND [pwdHash] = ? AND isEnabled = 1;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            ps.setString(2, this.HashingMD5(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("name"),
                            rs.getInt("groupId")
                    );
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return user;
    }

    // Get user by email (no password check)
    public User GetUserSignIn(String email) {
        User user = null;

        String sqlCommand = """
                            SELECT TOP (1) [id], [email], [pwdHash], [name], [isEnabled], [groupId]
                                FROM [technewsdb].[dbo].[User]
                                WHERE [email] = ? AND isEnabled = 1;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("name"),
                            rs.getInt("groupId")
                    );
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return user;
    }

    // User Sign up (Proc)
    public int CreateNewUser(String email, String password, String name) {
        int returnValue = -1;

        String sqlCommand = """
                            DECLARE	@return_value int;
                            EXEC	@return_value = [technewsdb].[dbo].[NewUser]
                            		@email = ?,
                            		@pwdHash = ?,
                            		@name = ?,
                            		@groupId = 2;
                            SELECT	'retval' = @return_value;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            ps.setString(2, this.HashingMD5(password));
            ps.setString(3, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    returnValue = rs.getInt("retval");
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return returnValue;
    }

    public HashMap<Integer, String> GetUserName() {
        HashMap<Integer, String> list = new HashMap<>();

        String sqlCommand = """
                            SELECT [id], [name]
                                FROM [technewsdb].[dbo].[User]
                                WHERE isEnabled = 1;
                            """;

        try (
                PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.put(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return list;
    }

    private String HashingMD5(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mess = md.digest(raw.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : mess) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
