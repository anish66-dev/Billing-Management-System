package db;
import java.sql.*;

public class DBConnect {
    private static final String URL = "jdbc:mysql://localhost:3306/billing_system";
    private static final String USER = "root";
    private static final String PASS = "anish";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
