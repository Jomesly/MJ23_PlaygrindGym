package mj23gym.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mj23gym.util.DatabaseConnection;

/**
 * Data-access object for the `members` table.
 */
public class MemberDAO {

    // ── DTO ───────────────────────────────────────────────────────

    public record MemberRecord(
        int    memberId,
        String memberCode,
        String firstName,
        String lastName,
        String contactNumber,
        String email,
        String address,
        Date   dateOfBirth,
        String gender,
        String membershipType,
        Date   membershipStartDate,
        Date   membershipEndDate,
        String status,
        String emergencyContact,
        String emergencyPhone
    ) {
        public String fullName() { return firstName + " " + lastName; }
    }

    // ── READ ──────────────────────────────────────────────────────

    public List<MemberRecord> findAll() {
        return query("SELECT * FROM members ORDER BY last_name, first_name", ps -> {});
    }

    /** Newest registrations first (for dashboard widgets). */
    public List<MemberRecord> findRecent(int limit) {
        return query(
            "SELECT * FROM members ORDER BY created_at DESC LIMIT ?",
            ps -> ps.setInt(1, Math.max(1, limit))
        );
    }

    public List<MemberRecord> findByStatus(String status) {
        return query("SELECT * FROM members WHERE status=? ORDER BY last_name",
                     ps -> ps.setString(1, status));
    }

    public List<MemberRecord> search(String keyword) {
        String k = "%" + keyword + "%";
        return query(
            "SELECT * FROM members WHERE first_name LIKE ? OR last_name LIKE ? " +
            "OR unique_member_code LIKE ? OR email LIKE ? ORDER BY last_name",
            ps -> { ps.setString(1,k); ps.setString(2,k); ps.setString(3,k); ps.setString(4,k); }
        );
    }

    public Optional<MemberRecord> findByCode(String code) {
        List<MemberRecord> res = query(
            "SELECT * FROM members WHERE unique_member_code=?",
            ps -> ps.setString(1, code)
        );
        return res.isEmpty() ? Optional.empty() : Optional.of(res.get(0));
    }

    public Optional<MemberRecord> findById(int id) {
        List<MemberRecord> res = query(
            "SELECT * FROM members WHERE member_id=?",
            ps -> ps.setInt(1, id)
        );
        return res.isEmpty() ? Optional.empty() : Optional.of(res.get(0));
    }

    // ── COUNT / STATS ─────────────────────────────────────────────

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM members WHERE status=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[MemberDAO] countByStatus error: " + e.getMessage());
            return 0;
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM members";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] countAll error: " + e.getMessage());
            return 0;
        }
    }

    // ── CREATE ────────────────────────────────────────────────────

    /**
     * Insert a new member.  member_code is auto-generated if null/empty.
     * Returns the generated member_id, or -1 on failure.
     */
    public int insert(MemberRecord m, int createdBy) {
        String code = (m.memberCode() == null || m.memberCode().isBlank())
                      ? generateNextCode() : m.memberCode();
        String sql =
            "INSERT INTO members (unique_member_code, first_name, last_name, contact_number," +
            " email, address, date_of_birth, gender, emergency_contact, emergency_phone," +
            " membership_type, membership_start_date, membership_end_date, status, created_by)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, m.firstName());
            ps.setString(3, m.lastName());
            ps.setString(4, m.contactNumber());
            ps.setString(5, m.email());
            ps.setString(6, m.address());
            ps.setDate(7, m.dateOfBirth());
            ps.setString(8, m.gender());
            ps.setString(9, m.emergencyContact());
            ps.setString(10, m.emergencyPhone());
            ps.setString(11, m.membershipType());
            ps.setDate(12, m.membershipStartDate());
            ps.setDate(13, m.membershipEndDate());
            ps.setString(14, m.status() != null ? m.status() : "Active");
            if (createdBy > 0) ps.setInt(15, createdBy); else ps.setNull(15, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                return gk.next() ? gk.getInt(1) : -1;
            }
        } catch (SQLException e) {
            System.err.println("[MemberDAO] insert error: " + e.getMessage());
            return -1;
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────

    public boolean update(MemberRecord m) {
        String sql =
            "UPDATE members SET first_name=?, last_name=?, contact_number=?, email=?," +
            " address=?, date_of_birth=?, gender=?, emergency_contact=?, emergency_phone=?," +
            " membership_type=?, membership_start_date=?, membership_end_date=?, status=?," +
            " updated_at=NOW() WHERE member_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  m.firstName());
            ps.setString(2,  m.lastName());
            ps.setString(3,  m.contactNumber());
            ps.setString(4,  m.email());
            ps.setString(5,  m.address());
            ps.setDate(6,    m.dateOfBirth());
            ps.setString(7,  m.gender());
            ps.setString(8,  m.emergencyContact());
            ps.setString(9,  m.emergencyPhone());
            ps.setString(10, m.membershipType());
            ps.setDate(11,   m.membershipStartDate());
            ps.setDate(12,   m.membershipEndDate());
            ps.setString(13, m.status());
            ps.setInt(14,    m.memberId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int memberId, String status) {
        String sql = "UPDATE members SET status=?, updated_at=NOW() WHERE member_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, memberId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] updateStatus error: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────

    public boolean delete(int memberId) {
        String sql = "DELETE FROM members WHERE member_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] delete error: " + e.getMessage());
            return false;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    @FunctionalInterface
    interface ParamSetter { void set(PreparedStatement ps) throws SQLException; }

    private List<MemberRecord> query(String sql, ParamSetter setter) {
        List<MemberRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MemberDAO] query error: " + e.getMessage());
        }
        return list;
    }

    private MemberRecord map(ResultSet rs) throws SQLException {
        return new MemberRecord(
            rs.getInt("member_id"),
            rs.getString("unique_member_code"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("contact_number"),
            rs.getString("email"),
            rs.getString("address"),
            rs.getDate("date_of_birth"),
            rs.getString("gender"),
            rs.getString("membership_type"),
            rs.getDate("membership_start_date"),
            rs.getDate("membership_end_date"),
            rs.getString("status"),
            rs.getString("emergency_contact"),
            rs.getString("emergency_phone")
        );
    }

    private String generateNextCode() {
        String sql = "SELECT COUNT(*) FROM members";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int n = rs.next() ? rs.getInt(1) + 1 : 1;
            return String.format("M-%03d", n);
        } catch (SQLException e) {
            return "M-" + System.currentTimeMillis();
        }
    }
}