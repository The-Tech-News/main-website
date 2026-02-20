package Models.DAO;

import Models.DBContext;
import Models.Objects.Category;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryDAO extends DBContext {

    public ArrayList<Category> GetListCategory() {
        ArrayList<Category> catList = new ArrayList<>();

        String sqlCommand = """
                            SELECT [id], [name], [description]
                                FROM [technewsdb].[dbo].[Category];
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                catList.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return catList;
    }

    public int NewCategory(String name, String description) {
        int returnValue = -1;

        String sqlCommand = """
                            DECLARE	@return_value int;
                            EXEC	@return_value = [technewsdb].[dbo].[NewCategory]
                                        @name = ?,
                                        @description = ?;
                            SELECT	'retval' = @return_value;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, name);
            ps.setString(2, description);
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

    public int EditCategory(String oldName, String newName, String description) {
        int returnValue = -1;

        String sqlCommand = """
                            DECLARE	@return_value int;
                            EXEC	@return_value = [dbo].[EditCategory]
                                    @oldName = ?,
                                    @newName = ?,
                                    @description = ?;
                            SELECT  "retval" = @return_value;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, oldName);
            ps.setString(2, newName);
            ps.setString(3, description);
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
