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
 * Data-access object for the `equipment` and `maintenance_logs` tables.
 */
public class EquipmentDAO {

    // ── DTOs ──────────────────────────────────────────────────────

    public record EquipmentRecord(
        int    equipmentId,
        String equipmentCode,
        String equipmentName,
        String category,
        String brandModel,
        Date   purchaseDate,
        double purchaseCost,
        double cost,
        int    quantity,
        String condition,
        String location,
        Date   lastMaintenance,
        Date   nextMaintenance,
        String maintenanceNotes,
        String notes,
        boolean isActive
    ) {}

    public record MaintenanceLog(
        int    maintenanceId,
        int    equipmentId,
        Date   maintenanceDate,
        String maintenanceType,
        String description,
        double cost,
        String performedBy,
        String status,
        Date   nextScheduledDate,
        String notes
    ) {}

    // ── EQUIPMENT READ ────────────────────────────────────────────

    public List<EquipmentRecord> findAll() {
        return query("SELECT * FROM equipment WHERE is_active=TRUE ORDER BY equipment_name", ps -> {});
    }

    public List<EquipmentRecord> findArchived() {
        return query("SELECT * FROM equipment WHERE is_active=FALSE ORDER BY equipment_name", ps -> {});
    }

    public List<EquipmentRecord> findByCategory(String category) {
        return query("SELECT * FROM equipment WHERE category=? AND is_active=TRUE ORDER BY equipment_name",
                     ps -> ps.setString(1, category));
    }

    public List<EquipmentRecord> findByCondition(String condition) {
        return query("SELECT * FROM equipment WHERE `condition`=? AND is_active=TRUE ORDER BY equipment_name",
                     ps -> ps.setString(1, condition));
    }

    public List<EquipmentRecord> findMaintenanceDue() {
        return query("SELECT * FROM equipment WHERE is_active=TRUE " +
                     "AND next_maintenance <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) ORDER BY next_maintenance",
                     ps -> {});
    }

    public Optional<EquipmentRecord> findById(int id) {
        List<EquipmentRecord> r = query("SELECT * FROM equipment WHERE equipment_id=?",
                                         ps -> ps.setInt(1, id));
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    public int countByCondition(String condition) {
        String sql = "SELECT COUNT(*) FROM equipment WHERE `condition`=? AND is_active=TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, condition);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) { System.err.println("[EquipmentDAO] count error: " + e.getMessage()); return 0; }
    }

    // ── EQUIPMENT CREATE ──────────────────────────────────────────

    public int insert(EquipmentRecord eq, int createdBy) {
        String code = (eq.equipmentCode() == null || eq.equipmentCode().isBlank())
                      ? generateNextCode() : eq.equipmentCode();
        String sql =
            "INSERT INTO equipment (equipment_code, equipment_name, category, brand_model," +
            " purchase_date, purchase_cost, cost, quantity, `condition`, location," +
            " last_maintenance, next_maintenance, maintenance_notes, notes, is_active, created_by)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,TRUE,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  code);
            ps.setString(2,  eq.equipmentName());
            ps.setString(3,  eq.category());
            ps.setString(4,  eq.brandModel());
            ps.setDate(5,    eq.purchaseDate());
            ps.setDouble(6,  eq.purchaseCost());
            ps.setDouble(7,  eq.cost());
            ps.setInt(8,     eq.quantity());
            ps.setString(9,  eq.condition());
            ps.setString(10, eq.location());
            ps.setDate(11,   eq.lastMaintenance());
            ps.setDate(12,   eq.nextMaintenance());
            ps.setString(13, eq.maintenanceNotes());
            ps.setString(14, eq.notes());
            if (createdBy > 0) ps.setInt(15, createdBy); else ps.setNull(15, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { return gk.next() ? gk.getInt(1) : -1; }
        } catch (SQLException e) {
            System.err.println("[EquipmentDAO] insert error: " + e.getMessage());
            return -1;
        }
    }

    // ── EQUIPMENT UPDATE ──────────────────────────────────────────

    public boolean update(EquipmentRecord eq) {
        String sql =
            "UPDATE equipment SET equipment_name=?, category=?, brand_model=?, purchase_date=?," +
            " purchase_cost=?, cost=?, quantity=?, `condition`=?, location=?, last_maintenance=?," +
            " next_maintenance=?, maintenance_notes=?, notes=?, updated_at=NOW() WHERE equipment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  eq.equipmentName());
            ps.setString(2,  eq.category());
            ps.setString(3,  eq.brandModel());
            ps.setDate(4,    eq.purchaseDate());
            ps.setDouble(5,  eq.purchaseCost());
            ps.setDouble(6,  eq.cost());
            ps.setInt(7,     eq.quantity());
            ps.setString(8,  eq.condition());
            ps.setString(9,  eq.location());
            ps.setDate(10,   eq.lastMaintenance());
            ps.setDate(11,   eq.nextMaintenance());
            ps.setString(12, eq.maintenanceNotes());
            ps.setString(13, eq.notes());
            ps.setInt(14,    eq.equipmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EquipmentDAO] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivate(int equipmentId) {
        String sql = "UPDATE equipment SET is_active=FALSE, updated_at=NOW() WHERE equipment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EquipmentDAO] deactivate error: " + e.getMessage());
            return false;
        }
    }

    // ── MAINTENANCE LOGS ──────────────────────────────────────────

    public boolean reactivate(int equipmentId) {
        String sql = "UPDATE equipment SET is_active=TRUE, updated_at=NOW() WHERE equipment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EquipmentDAO] reactivate error: " + e.getMessage());
            return false;
        }
    }

    public List<MaintenanceLog> getMaintenanceLogs(int equipmentId) {
        List<MaintenanceLog> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_logs WHERE equipment_id=? ORDER BY maintenance_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EquipmentDAO] getMaintenanceLogs error: " + e.getMessage());
        }
        return list;
    }

    public boolean addMaintenanceLog(MaintenanceLog log, int recordedBy) {
        String sql =
            "INSERT INTO maintenance_logs (equipment_id, maintenance_date, maintenance_type," +
            " description, cost, performed_by, status, next_scheduled_date, notes, recorded_by)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,     log.equipmentId());
            ps.setDate(2,    log.maintenanceDate());
            ps.setString(3,  log.maintenanceType());
            ps.setString(4,  log.description());
            ps.setDouble(5,  log.cost());
            ps.setString(6,  log.performedBy());
            ps.setString(7,  log.status() != null ? log.status() : "Completed");
            ps.setDate(8,    log.nextScheduledDate());
            ps.setString(9,  log.notes());
            if (recordedBy > 0) ps.setInt(10, recordedBy); else ps.setNull(10, Types.INTEGER);
            boolean ok = ps.executeUpdate() > 0;
            // Also update equipment's last/next maintenance dates
            if (ok && log.nextScheduledDate() != null) {
                String upd = "UPDATE equipment SET last_maintenance=?, next_maintenance=?, updated_at=NOW() WHERE equipment_id=?";
                try (PreparedStatement ups = conn.prepareStatement(upd)) {
                    ups.setDate(1, log.maintenanceDate());
                    ups.setDate(2, log.nextScheduledDate());
                    ups.setInt(3, log.equipmentId());
                    ups.executeUpdate();
                }
            }
            return ok;
        } catch (SQLException e) {
            System.err.println("[EquipmentDAO] addMaintenanceLog error: " + e.getMessage());
            return false;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    @FunctionalInterface
    interface ParamSetter { void set(PreparedStatement ps) throws SQLException; }

    private List<EquipmentRecord> query(String sql, ParamSetter setter) {
        List<EquipmentRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EquipmentDAO] query error: " + e.getMessage());
        }
        return list;
    }

    private EquipmentRecord map(ResultSet rs) throws SQLException {
        return new EquipmentRecord(
            rs.getInt("equipment_id"),
            rs.getString("equipment_code"),
            rs.getString("equipment_name"),
            rs.getString("category"),
            rs.getString("brand_model"),
            rs.getDate("purchase_date"),
            rs.getDouble("purchase_cost"),
            rs.getDouble("cost"),
            rs.getInt("quantity"),
            rs.getString("condition"),
            rs.getString("location"),
            rs.getDate("last_maintenance"),
            rs.getDate("next_maintenance"),
            rs.getString("maintenance_notes"),
            rs.getString("notes"),
            rs.getBoolean("is_active")
        );
    }

    private MaintenanceLog mapLog(ResultSet rs) throws SQLException {
        return new MaintenanceLog(
            rs.getInt("maintenance_id"),
            rs.getInt("equipment_id"),
            rs.getDate("maintenance_date"),
            rs.getString("maintenance_type"),
            rs.getString("description"),
            rs.getDouble("cost"),
            rs.getString("performed_by"),
            rs.getString("status"),
            rs.getDate("next_scheduled_date"),
            rs.getString("notes")
        );
    }

    private String generateNextCode() {
        String sql =
            "SELECT COALESCE(MAX(CAST(SUBSTRING(equipment_code, 4) AS UNSIGNED)), 0) + 1 " +
            "FROM equipment WHERE equipment_code REGEXP '^EQ-[0-9]+$'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int n = rs.next() ? rs.getInt(1) : 1;
            return String.format("EQ-%03d", n);
        } catch (SQLException e) {
            return "EQ-" + System.currentTimeMillis();
        }
    }
}
