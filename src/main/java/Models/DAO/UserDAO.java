package Models.DAO;

import Models.DBContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DBContext {
    
    // User Sign-in (SELECT)
    public boolean UserSignIn(String email, String pwdHash) {
        boolean isUser = false;
        
        String sqlCommand = """
                            SELECT 1
                                FROM [User]
                                WHERE [email] = '%s' AND [pwdHash] = '%s'
                            """;
        
        try (Statement smt = super.getConnection().createStatement(); ResultSet rs = smt.executeQuery(sqlCommand)) {
            if (rs.next()) {
                isUser = true;
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return isUser;
    }
}
