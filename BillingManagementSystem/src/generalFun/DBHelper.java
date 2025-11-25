package generalFun;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class DBHelper {

    @FunctionalInterface
    public interface ParamSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    public static <T> List<T> queryList(String sql, ParamSetter setter, ResultMapper<T> mapper) {
        List<T> out = new ArrayList<>();
        try (Connection conn = db.DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Query failed: " + sql, e);
        }
        return out;
    }

    public static int update(String sql, ParamSetter setter) {
        try (Connection conn = db.DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.set(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Update failed: " + sql, e);
        }
    }
}
