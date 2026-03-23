package Models.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

import Models.DBContext;

public class StatisticDAO extends DBContext {

    public HashMap<Integer, Integer> GetViews() {
        HashMap<Integer, Integer> list = new HashMap<>();

        String sqlComm = """
                            SELECT [postId], [count]
                                FROM [dbo].[PostStat]
                                ORDER BY [count] DESC, [postId] ASC;
                        """;

        try (
                PreparedStatement ps = super.getConnection().prepareStatement(sqlComm);
                ResultSet rs = ps.executeQuery()
            ) {
                while (rs.next()) {
                    list.put(rs.getInt("postId"), rs.getInt("count"));
                }
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public HashMap<Integer, Integer> GetViews(int top) {
        HashMap<Integer, Integer> list = new HashMap<>();

        String sqlComm = """
                            SELECT TOP (?) [postId], [count]
                                FROM [dbo].[PostStat]
                                ORDER BY [count] DESC, [postId] ASC;
                        """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlComm)) {
            ps.setInt(1, top);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.put(rs.getInt("postId"), rs.getInt("count"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public void IncreaseViewCount(int postId) {
        String sqlComm = """
                            MERGE dbo.PostStat AS target
                            USING (SELECT ? AS postId) AS source
                            ON target.postId = source.postId
                            WHEN MATCHED THEN
                                UPDATE SET [count] = target.[count] + 1
                            WHEN NOT MATCHED THEN
                                INSERT (postId, [count]) VALUES (source.postId, 1);
                        """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlComm)) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
