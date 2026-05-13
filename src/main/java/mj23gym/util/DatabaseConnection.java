package mj23gym.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lightweight JDBC connection helper.
 * Uses plain DriverManager (no external dependency needed for campus projects).
 * Swap the constants below to match your MySQL setup.
 */
public final class DatabaseConnection {

    public static void initialize() {
    System.out.println("[DB] Database initialized.");
}

public static void shutdown() {
    System.out.println("[DB] Database shutdown.");
}

    // ── Connection settings ──────────────────────────────────────
    private static final String HOST     = "localhost";
    private static final int    PORT     = 3306;
    private static final String DB_NAME  = "mj23gym";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "HNLwFxBd@PQ2AzJ";          // change as needed

    private static final String URL = String.format(
        "jdbc:mysql://%s:%d/%s?allowPublicKeyRetrieval=true&useSSL=false" +
        "&serverTimezone=UTC&characterEncoding=UTF-8",
        HOST, PORT, DB_NAME
    );

    private DatabaseConnection() {}

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
            try { conn.close(); } catch (SQLException ignored) {}
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
}