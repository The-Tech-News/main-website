package Models.DAO;

import Models.DBContext;
import Models.Objects.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DBContext {
    // User Sign-in (SELECT)
    public User GetUserSignIn(String email, String pwdHash) {
        User user = null;
        
        String sqlCommand = """
                            SELECT TOP (1) [id], [email], [pwdHash], [name], [isEnabled], [groupId]
                                FROM [technewsdb].[dbo].[User]
                                WHERE [email] = ? AND [pwdHash] = ?;
                            """;
        
        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            ps.setString(2, pwdHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("pwdHash"),
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
    public int CreateNewUser(String email, String pwdHash, String name) {
        int returnValue = -1;
        
        String sqlCommand = """
                            DECLARE	@return_value int;
                            EXEC	@return_value = [technewsdb].[dbo].[NewUser]
                            		@email = N?,
                            		@pwdHash = N?,
                            		@name = N?,
                            		@groupId = 2;
                            SELECT	'retval' = @return_value;
                            """;
        
        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            ps.setString(2, pwdHash);
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
}
