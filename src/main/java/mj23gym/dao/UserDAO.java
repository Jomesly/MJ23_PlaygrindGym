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
    public static final String DEFAULT_RECOVERY_QUESTION = "What is your registered phone number?";

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
                    boolean ok = PasswordUtil.verify(plainPassword, storedHash);
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

    public Optional<UserRecord> recoverUsername(String email, String phone, String question, String answer) {
        String sql = "SELECT user_id, username, password, full_name, email, phone, role, status, last_login, " +
                     "recovery_question, recovery_answer_hash " +
                     "FROM users WHERE LOWER(email)=LOWER(?) AND phone=? AND status='active' AND is_active=TRUE";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureRecoveryColumns(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clean(email));
                ps.setString(2, clean(phone));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (verifyRecoveryAnswer(rs, question, answer)) {
                            saveRecoveryChallenge(conn, rs.getInt("user_id"), question, answer);
                            return Optional.of(mapRow(rs));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] recoverUsername error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean resetPasswordWithRecovery(String username, String email, String phone,
                                             String question, String answer, String newPlainPassword) {
        String sql = "SELECT user_id, username, password, full_name, email, phone, role, status, last_login, " +
                     "recovery_question, recovery_answer_hash " +
                     "FROM users WHERE username=? AND LOWER(email)=LOWER(?) AND phone=? " +
                     "AND status='active' AND is_active=TRUE";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureRecoveryColumns(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clean(username));
                ps.setString(2, clean(email));
                ps.setString(3, clean(phone));
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !verifyRecoveryAnswer(rs, question, answer)) {
                        return false;
                    }
                    int userId = rs.getInt("user_id");
                    try (PreparedStatement upd = conn.prepareStatement(
                            "UPDATE users SET password=?, recovery_question=?, recovery_answer_hash=?, updated_at=NOW() WHERE user_id=?")) {
                        upd.setString(1, PasswordUtil.hash(newPlainPassword));
                        upd.setString(2, clean(question));
                        upd.setString(3, PasswordUtil.hash(clean(answer)));
                        upd.setInt(4, userId);
                        return upd.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] resetPasswordWithRecovery error: " + e.getMessage());
            return false;
        }
    }

    public Optional<UserRecord> verifyPasswordRecoveryIdentity(String username, String email, String phone,
                                                               String question, String answer) {
        String sql = "SELECT user_id, username, password, full_name, email, phone, role, status, last_login, " +
                     "recovery_question, recovery_answer_hash " +
                     "FROM users WHERE username=? AND LOWER(email)=LOWER(?) AND phone=? " +
                     "AND status='active' AND is_active=TRUE";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureRecoveryColumns(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clean(username));
                ps.setString(2, clean(email));
                ps.setString(3, clean(phone));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && verifyRecoveryAnswer(rs, question, answer)) {
                        saveRecoveryChallenge(conn, rs.getInt("user_id"), question, answer);
                        return Optional.of(mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] verifyPasswordRecoveryIdentity error: " + e.getMessage());
        }
        return Optional.empty();
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

    public Optional<UserRecord> findById(int userId) {
        String sql = "SELECT user_id, username, full_name, email, phone, role, status, last_login " +
                     "FROM users WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
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
        return registerStaffForVerification(username, fullName, email, phone, plainPassword,
            DEFAULT_RECOVERY_QUESTION, phone);
    }

    public boolean registerStaffForVerification(String username, String fullName, String email,
                                                String phone, String plainPassword,
                                                String recoveryQuestion, String recoveryAnswer) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role, status, is_active, " +
                     "recovery_question, recovery_answer_hash) VALUES (?, ?, ?, ?, ?, 'staff', 'inactive', FALSE, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureRecoveryColumns(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, PasswordUtil.hash(plainPassword));
                ps.setString(3, fullName);
                ps.setString(4, email);
                ps.setString(5, phone);
                ps.setString(6, clean(recoveryQuestion));
                ps.setString(7, PasswordUtil.hash(clean(recoveryAnswer)));
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] registerStaffForVerification error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRecoveryChallenge(int userId, String recoveryQuestion, String recoveryAnswer) {
        if (userId <= 0 || clean(recoveryQuestion).isEmpty() || clean(recoveryAnswer).isEmpty()) {
            return false;
        }
        String sql = "UPDATE users SET recovery_question=?, recovery_answer_hash=?, updated_at=NOW() WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureRecoveryColumns(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clean(recoveryQuestion));
                ps.setString(2, PasswordUtil.hash(clean(recoveryAnswer)));
                ps.setInt(3, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateRecoveryChallenge error: " + e.getMessage());
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
                boolean match = PasswordUtil.verify(oldPlain, stored);
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

    public boolean resetStaffPassword(int userId, String newPlainPassword) {
        String sql = "UPDATE users SET password=?, updated_at=NOW() WHERE user_id=? AND role='staff'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hash(newPlainPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] resetStaffPassword error: " + e.getMessage());
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

    private void ensureRecoveryColumns(Connection conn) throws SQLException {
        ensureColumn(conn, "recovery_question", "VARCHAR(255)");
        ensureColumn(conn, "recovery_answer_hash", "VARCHAR(255)");
    }

    private void ensureColumn(Connection conn, String column, String definition) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "users", column)) {
            if (!rs.next()) {
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("ALTER TABLE users ADD COLUMN " + column + " " + definition);
                }
            }
        }
    }

    private boolean verifyRecoveryAnswer(ResultSet rs, String question, String answer) throws SQLException {
        String selectedQuestion = clean(question);
        String typedAnswer = clean(answer);
        if (selectedQuestion.isEmpty() || typedAnswer.isEmpty()) {
            return false;
        }

        String storedQuestion = clean(rs.getString("recovery_question"));
        String storedHash = rs.getString("recovery_answer_hash");
        if (!storedQuestion.isEmpty() && storedHash != null && !storedHash.isBlank()) {
            return storedQuestion.equalsIgnoreCase(selectedQuestion)
                && PasswordUtil.verify(typedAnswer, storedHash);
        }

        return DEFAULT_RECOVERY_QUESTION.equalsIgnoreCase(selectedQuestion)
            && typedAnswer.equals(clean(rs.getString("phone")));
    }

    private void saveRecoveryChallenge(Connection conn, int userId, String question, String answer) throws SQLException {
        if (clean(question).isEmpty() || clean(answer).isEmpty()) {
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET recovery_question=?, recovery_answer_hash=?, updated_at=NOW() " +
                "WHERE user_id=? AND (recovery_answer_hash IS NULL OR recovery_answer_hash='')")) {
            ps.setString(1, clean(question));
            ps.setString(2, PasswordUtil.hash(clean(answer)));
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
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
