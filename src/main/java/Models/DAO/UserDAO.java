package Models.DAO;

import Models.DBContext;
import Models.Objects.User;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
            ps.setString(2, this.hashPasswordPBKDF2(password));
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
            ps.setString(2, this.hashPasswordPBKDF2(password));
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

    private String hashPasswordPBKDF2(String raw) {
        // PBKDF2 parameters
        int iterations = 65536;
        int keyLength = 256; // bits

        try {
            // Generate a random salt
            byte[] salt = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(salt);

            // Derive the key (hash)
            PBEKeySpec spec = new PBEKeySpec(raw.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            // Encode as iterations:saltHex:hashHex
            StringBuilder sb = new StringBuilder();
            sb.append(iterations).append(":");
            for (byte b : salt) {
                sb.append(String.format("%02x", b));
            }
            sb.append(":");
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
