package Models.DAO;

import Models.DBContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticDAO extends DBContext {

    public ArrayList<int[]> GetAll() {
        ArrayList<int[]> list = new ArrayList<>();

        String sql = """
            SELECT postId, [count]
            FROM dbo.PostStat
            ORDER BY [count] DESC, postId ASC;
        """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new int[]{ rs.getInt("postId"), rs.getInt("count") });
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public ArrayList<int[]> GetTop(int top) {
        ArrayList<int[]> list = new ArrayList<>();

        String sql = """
            SELECT TOP (?) postId, [count]
            FROM dbo.PostStat
            ORDER BY [count] DESC, postId ASC;
        """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, top);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new int[]{ rs.getInt("postId"), rs.getInt("count") });
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    // Auto-increment views (Upsert)
    public void IncreaseViewCount(int postId) {
        String sql = """
            MERGE dbo.PostStat AS target
            USING (SELECT ? AS postId) AS source
            ON target.postId = source.postId
            WHEN MATCHED THEN
                UPDATE SET [count] = target.[count] + 1
            WHEN NOT MATCHED THEN
                INSERT (postId, [count]) VALUES (source.postId, 1);
        """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}