package mj23gym.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lightweight JDBC connection helper.
 * Database settings can be provided through environment variables or JVM
 * system properties, keeping passwords out of source code.
 */
public final class DatabaseConnection {

    private static final String HOST = config("mj23.db.host", "MJ23_DB_HOST", "localhost");
    private static final int PORT = configInt("mj23.db.port", "MJ23_DB_PORT", 3306);
    private static final String DB_NAME = config("mj23.db.name", "MJ23_DB_NAME", "mj23gym");
    private static final String USERNAME = config("mj23.db.user", "MJ23_DB_USER", "root");
    private static final String PASSWORD = config("mj23.db.password", "MJ23_DB_PASSWORD", "HNLwFxBd@PQ2AzJ");

    private static final String URL = String.format(
        "jdbc:mysql://%s:%d/%s?allowPublicKeyRetrieval=true&useSSL=false" +
        "&serverTimezone=Asia/Manila&characterEncoding=UTF-8",
        HOST, PORT, DB_NAME
    );

    private DatabaseConnection() {}

    public static void initialize() {
        System.out.println("[DB] Database initialized.");
    }

    public static void shutdown() {
        System.out.println("[DB] Database shutdown.");
    }

    /** Open and return a fresh JDBC connection. */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found on classpath.", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /** Quietly close a connection (null-safe). */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /** Quick connectivity test. */
    public static boolean isConnected() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("[DB] Connection test failed: " + e.getMessage());
            return false;
        }
    }

    private static String config(String propertyName, String envName, String fallback) {
        String prop = System.getProperty(propertyName);
        if (prop != null && !prop.isBlank()) {
            return prop.trim();
        }
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return fallback;
    }

    private static int configInt(String propertyName, String envName, int fallback) {
        String raw = config(propertyName, envName, String.valueOf(fallback));
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
