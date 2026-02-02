package Models.DAO;

import Models.DBContext;
import Models.Objects.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DBContext {
    // User Sign-in (SELECT)
    public User GetUserSignIn(String email, String pwdHash) {
        User user = null;
        
        String sqlCommand = """
                            SELECT TOP (1) [id], [email], [pwdHash], [name], [isEnabled], [groupId]
                                FROM [technewsdb].[dbo].[User]
                                WHERE [email] = '%s' AND [pwdHash] = '%s';
                            """.formatted(email, pwdHash);
        
        try (Statement smt = super.getConnection().createStatement(); ResultSet rs = smt.executeQuery(sqlCommand)) {
            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("pwdHash"),
                        rs.getString("name"),
                        rs.getInt("groupId")
                );
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
                            		@email = N'%s',
                            		@pwdHash = N'%s',
                            		@name = N'%s',
                            		@groupId = 2;
                            SELECT	'retval' = @return_value;
                            """.formatted(email, pwdHash, name);
        
        System.out.println(sqlCommand);
        
        try (Statement smt = super.getConnection().createStatement(); ResultSet rs = smt.executeQuery(sqlCommand)) {
            if (rs.next()) {
                returnValue = rs.getInt("retval");
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }
        
        return returnValue;
    }
}
