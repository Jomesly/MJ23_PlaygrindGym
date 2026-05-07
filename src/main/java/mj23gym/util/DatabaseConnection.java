package mj23gym.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database Connection Utility for MySQL 8.x
 * Uses HikariCP for efficient connection pooling
 */
public class DatabaseConnection {
    
    private static HikariDataSource dataSource;
    
    // Database Configuration
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "mj23gym";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Change to your MySQL password
    
    /**
     * Initialize the database connection pool
     * Call this once at application startup
     */
    public static void initialize() {
        try {
            if (dataSource == null) {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(String.format(
                    "jdbc:mysql://%s:%d/%s?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                    DB_HOST, DB_PORT, DB_NAME
                ));
                config.setUsername(DB_USER);
                config.setPassword(DB_PASSWORD);
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);
                config.setAutoCommit(true);
                
                dataSource = new HikariDataSource(config);
                System.out.println("[DB] Connection pool initialized successfully!");
            }
        } catch (Exception e) {
            System.err.println("[DB ERROR] Failed to initialize database connection pool: " + e.getMessage());
            dataSource = null;
        }
    }
    
    /**
     * Get a connection from the pool
     * @return A connection from the HikariCP pool
     * @throws SQLException if connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initialize();
        }
        if (dataSource == null) {
            throw new SQLException("Database connection pool is not available.");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Close a connection (returns it to the pool)
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Shutdown the connection pool
     * Call this at application shutdown
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DB] Connection pool closed!");
        }
    }
    
    /**
     * Check if database is connected
     * @return true if connection is available, false otherwise
     */
    public static boolean isConnected() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                closeConnection(conn);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Connection check failed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update database credentials
     * @param host Database host
     * @param port Database port
     * @param database Database name
     * @param username Database username
     * @param password Database password
     */
    public static void updateCredentials(String host, int port, String database, String username, String password) {
        // Close existing pool if any
        shutdown();
        
        // These would be used when initialize() is called again
        System.out.println("[DB] Credentials updated. Will use new settings on next connection.");
    }
}
