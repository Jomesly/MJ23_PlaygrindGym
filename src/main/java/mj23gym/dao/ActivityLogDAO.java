package mj23gym.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import mj23gym.util.DatabaseConnection;

public final class ActivityLogDAO {

    public record ActivitySession(
        String username,
        String fullName,
        String role,
        String action,
        Timestamp occurredAt
    ) {}

    public void logSession(int userId, String role, String action) {
        if (userId <= 0 || !isTrackedRole(role)) {
            return;
        }

        String sql =
            "INSERT INTO audit_logs (user_id, entity_type, entity_id, action, new_values, status) " +
            "VALUES (?, 'activity_session', ?, ?, ?, 'Success')";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            String snapshot = buildSessionSnapshot(conn, userId, role);
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setString(3, action);
            ps.setString(4, snapshot);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ActivityLogDAO] logSession error: " + e.getMessage());
        }
    }

    public List<ActivitySession> findRecentSessions(int limit) {
        String sql =
            "SELECT a.entity_id, a.new_values, u.username, u.full_name, u.role, a.action, a.created_at " +
            "FROM audit_logs a LEFT JOIN users u ON a.user_id = u.user_id " +
            "WHERE a.entity_type = 'activity_session' " +
            "AND a.action IN ('LOGIN', 'LOGOUT') " +
            "ORDER BY a.created_at DESC, a.log_id DESC LIMIT ?";
        List<ActivitySession> sessions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String values = rs.getString("new_values");
                    String role = normalizeRole(firstPresent(extractValue(values, "role"), rs.getString("role")));
                    if (!isTrackedRole(role)) {
                        continue;
                    }
                    String username = firstPresent(rs.getString("username"), extractValue(values, "username"));
                    if (username == null || username.isBlank()) {
                        username = "User #" + rs.getInt("entity_id");
                    }
                    sessions.add(new ActivitySession(
                        username,
                        firstPresent(rs.getString("full_name"), extractValue(values, "fullName")),
                        role,
                        displayAction(rs.getString("action")),
                        rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ActivityLogDAO] findRecentSessions error: " + e.getMessage());
        }
        return sessions;
    }

    private String buildSessionSnapshot(Connection conn, int userId, String fallbackRole) throws SQLException {
        String sql = "SELECT username, full_name, role FROM users WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "role=" + cleanSnapshotValue(normalizeRole(firstPresent(rs.getString("role"), fallbackRole))) +
                        ";username=" + cleanSnapshotValue(rs.getString("username")) +
                        ";fullName=" + cleanSnapshotValue(rs.getString("full_name"));
                }
            }
        }
        return "role=" + cleanSnapshotValue(normalizeRole(fallbackRole)) + ";username=User #" + userId + ";fullName=";
    }

    private boolean isTrackedRole(String role) {
        return "admin".equalsIgnoreCase(role) || "staff".equalsIgnoreCase(role);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "Staff";
        }
        return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
    }

    private String displayAction(String action) {
        if ("LOGOUT".equalsIgnoreCase(action)) {
            return "Signed out";
        }
        return "Signed in";
    }

    private String firstPresent(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }

    private String extractValue(String values, String key) {
        if (values == null || values.isBlank() || key == null || key.isBlank()) {
            return null;
        }
        String prefix = key + "=";
        String[] parts = values.split(";");
        for (String part : parts) {
            if (part.startsWith(prefix)) {
                return part.substring(prefix.length());
            }
        }
        return null;
    }

    private String cleanSnapshotValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(';', ' ').replace('\n', ' ').replace('\r', ' ').trim();
    }
}
