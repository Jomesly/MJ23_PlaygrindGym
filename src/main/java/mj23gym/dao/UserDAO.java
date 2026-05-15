package mj23gym.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mj23gym.util.DatabaseConnection;
import mj23gym.util.PasswordUtil;

/**
 * Data-access object for the `users` table.
 */
public class UserDAO {

    // ── DTOs ──────────────────────────────────────────────────────

    public record UserRecord(
        int     userId,
        String  username,
        String  fullName,
        String  email,
        String  phone,
        String  role,
        String  status,
        Timestamp lastLogin
    ) {}

    /**
     * Verify credentials; on success update last_login and return the user.
     * Supports both bcrypt hashed and plain text passwords.
     * @return Optional.empty() if username not found or password wrong.
     */
    public Optional<UserRecord> authenticate(String username, String plainPassword) {
        String sql = "SELECT user_id, username, password, full_name, email, phone, role, status, last_login " +
                     "FROM users WHERE username = ? AND status = 'active' AND is_active = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    // Support both bcrypt and plain text passwords
                    boolean ok = storedHash.startsWith("$2")
                                 ? PasswordUtil.verify(plainPassword, storedHash)
                                 : storedHash.equals(plainPassword);   // plain text fallback
                    if (ok) {
                        updateLastLogin(conn, rs.getInt("user_id"));
                        return Optional.of(mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] authenticate error: " + e.getMessage());
        }
        return Optional.empty();
    }

    /** Fallback plain-text check for the seed admin (password not yet hashed). */
    public Optional<UserRecord> authenticatePlain(String username, String plainPassword) {
        String sql = "SELECT user_id, username, password, full_name, email, phone, role, status, last_login " +
                     "FROM users WHERE username = ? AND status = 'active'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    boolean ok = stored.startsWith("$2") 
                                 ? PasswordUtil.verify(plainPassword, stored) 
                                 : stored.equals(plainPassword);   // plain fallback
                    if (ok) {
                        updateLastLogin(conn, rs.getInt("user_id"));
                        return Optional.of(mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] authenticatePlain error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<UserRecord> findActiveByRecoveryIdentity(String username, String emailOrPhone) {
        String sql = "SELECT user_id, username, full_name, email, phone, role, status, last_login " +
                     "FROM users WHERE username = ? AND status = 'active' AND is_active = TRUE " +
                     "AND (LOWER(email) = LOWER(?) OR phone = ?)";
        String identity = emailOrPhone == null ? "" : emailOrPhone.trim();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, identity);
            ps.setString(3, identity);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findActiveByRecoveryIdentity error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<UserRecord> findUsernameByRecoveryIdentity(String emailOrPhone) {
        String sql = "SELECT user_id, username, full_name, email, phone, role, status, last_login " +
                     "FROM users WHERE status = 'active' AND is_active = TRUE " +
                     "AND (LOWER(email) = LOWER(?) OR phone = ?) ORDER BY user_id LIMIT 1";
        String identity = emailOrPhone == null ? "" : emailOrPhone.trim();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identity);
            ps.setString(2, identity);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findUsernameByRecoveryIdentity error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean resetPasswordAfterRecovery(int userId, String newPlainPassword) {
        String sql = "UPDATE users SET password=?, updated_at=NOW() WHERE user_id=? AND status='active'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hash(newPlainPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] resetPasswordAfterRecovery error: " + e.getMessage());
            return false;
        }
    }

    // ── CRUD ──────────────────────────────────────────────────────

    public List<UserRecord> findAll() {
        List<UserRecord> list = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name, email, phone, role, status, last_login " +
                     "FROM users ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(String username, String fullName, String email,
                          String phone, String role, String plainPassword) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(plainPassword));
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] insert error: " + e.getMessage());
            return false;
        }
    }

    public boolean registerStaffForVerification(String username, String fullName, String email,
                                                String phone, String plainPassword) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role, status, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, 'staff', 'inactive', FALSE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(plainPassword));
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, phone);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] registerStaffForVerification error: " + e.getMessage());
            return false;
        }
    }

    public boolean setVerificationStatus(int userId, boolean verified) {
        String sql = "UPDATE users SET status=?, is_active=?, updated_at=NOW() " +
                     "WHERE user_id=? AND role='staff'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, verified ? "active" : "inactive");
            ps.setBoolean(2, verified);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] setVerificationStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProfile(int userId, String fullName, String email, String phone) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, updated_at=NOW() WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateProfile error: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(int userId, String oldPlain, String newPlain) {
        // Verify old password first
        String sel = "SELECT password FROM users WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sel)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String stored = rs.getString(1);
                boolean match = stored.startsWith("$2")
                                ? PasswordUtil.verify(oldPlain, stored)
                                : stored.equals(oldPlain);
                if (!match) return false;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] changePassword verify error: " + e.getMessage());
            return false;
        }
        String upd = "UPDATE users SET password=?, updated_at=NOW() WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(upd)) {
            ps.setString(1, PasswordUtil.hash(newPlain));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] changePassword update error: " + e.getMessage());
            return false;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void updateLastLogin(Connection conn, int userId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET last_login=NOW() WHERE user_id=?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private UserRecord mapRow(ResultSet rs) throws SQLException {
        return new UserRecord(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("role"),
            rs.getString("status"),
            rs.getTimestamp("last_login")
        );
    }
}
