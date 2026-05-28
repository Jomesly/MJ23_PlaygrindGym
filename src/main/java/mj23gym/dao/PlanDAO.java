package mj23gym.dao;

import java.sql.Connection;
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
 * Data-access object for the membership plans module.
 */
public class PlanDAO {

    public record PlanRecord(
        int planId,
        String planName,
        String description,
        String duration,
        double price,
        String benefits,
        boolean active
    ) {}

    private static final String PLAN_ENUM =
        "'Daily','Per Session','Monthly','Quarterly','Semi Annual','Yearly','Annual'";

    public void ensurePlanSetup() {
        ensurePlanEnums();
        seedDefaultPlans();
    }

    public List<PlanRecord> findAll() {
        return query("SELECT * FROM plans ORDER BY is_active DESC, price ASC, plan_name ASC", ps -> {});
    }

    public List<PlanRecord> findActive() {
        return query("SELECT * FROM plans WHERE is_active=TRUE ORDER BY price ASC, plan_name ASC", ps -> {});
    }

    public List<String> activePlanNames() {
        List<String> names = new ArrayList<>();
        for (PlanRecord p : findActive()) {
            names.add(p.planName());
        }
        if (names.isEmpty()) {
            names.add("Per Session");
            names.add("Monthly");
            names.add("Quarterly");
            names.add("Semi Annual");
            names.add("Annual");
        }
        return names;
    }

    public Optional<PlanRecord> findById(int id) {
        List<PlanRecord> rows = query("SELECT * FROM plans WHERE plan_id=?", ps -> ps.setInt(1, id));
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public int insert(PlanRecord plan, int createdBy) {
        String sql =
            "INSERT INTO plans (plan_name, description, duration, price, benefits, is_active, created_by)" +
            " VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, plan.planName());
            ps.setString(2, plan.description());
            ps.setString(3, normalizePlanName(plan.duration()));
            ps.setDouble(4, plan.price());
            ps.setString(5, plan.benefits());
            ps.setBoolean(6, plan.active());
            if (createdBy > 0) ps.setInt(7, createdBy); else ps.setNull(7, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        } catch (SQLException e) {
            System.err.println("[PlanDAO] insert error: " + e.getMessage());
            return -1;
        }
    }

    public boolean update(PlanRecord plan) {
        String sql =
            "UPDATE plans SET plan_name=?, description=?, duration=?, price=?, benefits=?, is_active=?, updated_at=NOW()" +
            " WHERE plan_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plan.planName());
            ps.setString(2, plan.description());
            ps.setString(3, normalizePlanName(plan.duration()));
            ps.setDouble(4, plan.price());
            ps.setString(5, plan.benefits());
            ps.setBoolean(6, plan.active());
            ps.setInt(7, plan.planId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PlanDAO] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int planId) {
        String sql = "DELETE FROM plans WHERE plan_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, planId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PlanDAO] delete error: " + e.getMessage());
            return false;
        }
    }

    public boolean setActive(int planId, boolean active) {
        String sql = "UPDATE plans SET is_active=?, updated_at=NOW() WHERE plan_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, planId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PlanDAO] setActive error: " + e.getMessage());
            return false;
        }
    }

    public void seedDefaultPlans() {
        ensureDefault("Per Session", "Single gym session access", "Per Session", 100.00,
            "One walk-in training session.");
        ensureDefault("Monthly", "One-month gym membership", "Monthly", 788.00,
            "Unlimited access for one month.");
        ensureDefault("Quarterly", "Three-month gym membership", "Quarterly", 1988.00,
            "Unlimited access for three months.");
        ensureDefault("Semi Annual", "Six-month gym membership", "Semi Annual", 3288.00,
            "Unlimited access for six months.");
        ensureDefault("Annual", "Twelve-month gym membership", "Annual", 4988.00,
            "Unlimited access for one year.");
    }

    public static String normalizePlanName(String value) {
        if (value == null || value.isBlank()) {
            return "Monthly";
        }
        return switch (value.trim()) {
            case "Daily" -> "Per Session";
            case "Yearly" -> "Annual";
            default -> value.trim();
        };
    }

    @FunctionalInterface
    interface ParamSetter { void set(PreparedStatement ps) throws SQLException; }

    private List<PlanRecord> query(String sql, ParamSetter setter) {
        List<PlanRecord> plans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    plans.add(new PlanRecord(
                        rs.getInt("plan_id"),
                        rs.getString("plan_name"),
                        rs.getString("description"),
                        rs.getString("duration"),
                        rs.getDouble("price"),
                        rs.getString("benefits"),
                        rs.getBoolean("is_active")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[PlanDAO] query error: " + e.getMessage());
        }
        return plans;
    }

    private void ensureDefault(String name, String description, String duration, double price, String benefits) {
        String existsSql = "SELECT plan_id FROM plans WHERE plan_name=? LIMIT 1";
        String insertSql =
            "INSERT INTO plans (plan_name, description, duration, price, benefits, is_active)" +
            " VALUES (?,?,?,?,?,TRUE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement exists = conn.prepareStatement(existsSql)) {
            exists.setString(1, name);
            try (ResultSet rs = exists.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setString(1, name);
                insert.setString(2, description);
                insert.setString(3, duration);
                insert.setDouble(4, price);
                insert.setString(5, benefits);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[PlanDAO] seedDefault error: " + e.getMessage());
        }
    }

    private void ensurePlanEnums() {
        String planSql = "ALTER TABLE plans MODIFY duration ENUM(" + PLAN_ENUM + ") DEFAULT 'Monthly'";
        String memberSql = "ALTER TABLE members MODIFY membership_type ENUM(" + PLAN_ENUM + ") DEFAULT 'Monthly'";
        String attendanceSql = "ALTER TABLE attendance MODIFY session_type ENUM('Daily','Per Session','Member Session','Monthly','Quarterly','Semi Annual','Yearly','Annual') DEFAULT 'Member Session'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate(planSql);
            st.executeUpdate(memberSql);
            st.executeUpdate(attendanceSql);
        } catch (SQLException e) {
            System.err.println("[PlanDAO] ensurePlanEnums warning: " + e.getMessage());
        }
    }
}
