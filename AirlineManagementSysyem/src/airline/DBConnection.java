package airline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton utility class for managing MySQL database connections.
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/airlinedb";
    private static final String USER     = "root";
    private static final String PASSWORD = "vishal1223"; // Change to your MySQL password

    private static Connection connection = null;

    private DBConnection() {}

    /**
     * Returns a shared Connection, creating one if it does not exist yet.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }

    /** Closes the shared connection (call on application exit). */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
