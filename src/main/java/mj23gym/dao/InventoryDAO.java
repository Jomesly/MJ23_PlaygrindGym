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
 * Data-access object for the `inventory` table.
 */
public class InventoryDAO {

    public record InventoryRecord(
        int     itemId,
        String  itemCode,
        String  itemName,
        String  category,
        String  description,
        int     quantity,
        int     currentStock,
        int     minimumStock,
        int     reorderLevel,
        double  unitPrice,
        double  sellingPrice,
        String  unitOfMeasure,
        String  supplier,
        Date    lastRestock,
        Date    expirationDate,
        String  status,
        String  notes,
        boolean isActive
    ) {}

    // ── READ ──────────────────────────────────────────────────────

    public List<InventoryRecord> findAll() {
        ensureExpirationColumn();
        return query("SELECT * FROM inventory WHERE is_active=TRUE ORDER BY item_name", ps -> {});
    }

    public List<InventoryRecord> findArchived() {
        ensureExpirationColumn();
        return query("SELECT * FROM inventory WHERE is_active=FALSE ORDER BY item_name", ps -> {});
    }

    public List<InventoryRecord> findByCategory(String category) {
        return query("SELECT * FROM inventory WHERE category=? AND is_active=TRUE ORDER BY item_name",
                     ps -> ps.setString(1, category));
    }

    public List<InventoryRecord> findByStatus(String status) {
        return query("SELECT * FROM inventory WHERE status=? AND is_active=TRUE ORDER BY item_name",
                     ps -> ps.setString(1, status));
    }

    public List<InventoryRecord> search(String keyword) {
        String k = "%" + keyword + "%";
        return query("SELECT * FROM inventory WHERE is_active=TRUE " +
                     "AND (item_name LIKE ? OR item_code LIKE ? OR category LIKE ?) ORDER BY item_name",
                     ps -> { ps.setString(1,k); ps.setString(2,k); ps.setString(3,k); });
    }

    public List<InventoryRecord> findLowStock() {
        return query("SELECT * FROM inventory WHERE current_stock <= reorder_level AND is_active=TRUE ORDER BY item_name",
                     ps -> {});
    }

    public List<InventoryRecord> findExpiredOrExpiringSoon() {
        ensureExpirationColumn();
        return query(
            "SELECT * FROM inventory WHERE is_active=TRUE AND expiration_date IS NOT NULL " +
            "AND expiration_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) ORDER BY expiration_date, item_name",
            ps -> {}
        );
    }

    public Optional<InventoryRecord> findById(int id) {
        List<InventoryRecord> r = query("SELECT * FROM inventory WHERE item_id=?",
                                        ps -> ps.setInt(1, id));
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    public int countLowStock() {
        return scalarCount("SELECT COUNT(*) FROM inventory WHERE current_stock <= reorder_level AND is_active=TRUE");
    }

    public int countOutOfStock() {
        return scalarCount("SELECT COUNT(*) FROM inventory WHERE current_stock=0 AND is_active=TRUE");
    }

    // ── CREATE ────────────────────────────────────────────────────

    /** Returns generated item_id, or -1 on failure. */
    public int insert(InventoryRecord item, int createdBy) {
        String code = (item.itemCode() == null || item.itemCode().isBlank())
                      ? generateNextCode() : item.itemCode();
        String sql =
            "INSERT INTO inventory (item_code, item_name, category, description, quantity," +
            " current_stock, minimum_stock, reorder_level, unit_price, selling_price," +
            " unit_of_measure, supplier, last_restock, expiration_date, status, notes, is_active, created_by)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,TRUE,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureExpirationColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  code);
            ps.setString(2,  item.itemName());
            ps.setString(3,  item.category());
            ps.setString(4,  item.description());
            ps.setInt(5,     item.quantity());
            ps.setInt(6,     item.currentStock());
            ps.setInt(7,     item.minimumStock());
            ps.setInt(8,     item.reorderLevel());
            ps.setDouble(9,  item.unitPrice());
            ps.setDouble(10, item.sellingPrice());
            ps.setString(11, item.unitOfMeasure());
            ps.setString(12, item.supplier());
            ps.setDate(13,   item.lastRestock());
            ps.setDate(14,   item.expirationDate());
            ps.setString(15, deriveStatus(item.currentStock(), item.reorderLevel()));
            ps.setString(16, item.notes());
            if (createdBy > 0) ps.setInt(17, createdBy); else ps.setNull(17, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                return gk.next() ? gk.getInt(1) : -1;
            }
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] insert error: " + e.getMessage());
            return -1;
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────

    public boolean update(InventoryRecord item) {
        String sql =
            "UPDATE inventory SET item_name=?, category=?, description=?, quantity=?," +
            " current_stock=?, minimum_stock=?, reorder_level=?, unit_price=?, selling_price=?," +
            " unit_of_measure=?, supplier=?, last_restock=?, expiration_date=?, status=?, notes=?, updated_at=NOW()" +
            " WHERE item_id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureExpirationColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  item.itemName());
            ps.setString(2,  item.category());
            ps.setString(3,  item.description());
            ps.setInt(4,     item.quantity());
            ps.setInt(5,     item.currentStock());
            ps.setInt(6,     item.minimumStock());
            ps.setInt(7,     item.reorderLevel());
            ps.setDouble(8,  item.unitPrice());
            ps.setDouble(9,  item.sellingPrice());
            ps.setString(10, item.unitOfMeasure());
            ps.setString(11, item.supplier());
            ps.setDate(12,   item.lastRestock());
            ps.setDate(13,   item.expirationDate());
            ps.setString(14, deriveStatus(item.currentStock(), item.reorderLevel()));
            ps.setString(15, item.notes());
            ps.setInt(16,    item.itemId());
            return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] update error: " + e.getMessage());
            return false;
        }
    }

    /** Adjust stock quantity and auto-update status. */
    public boolean adjustStock(int itemId, int newStock) {
        String sql = "UPDATE inventory SET current_stock=?, status=?, updated_at=NOW() WHERE item_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // get reorder level first
            int rol = getReorderLevel(conn, itemId);
            ps.setInt(1, newStock);
            ps.setString(2, deriveStatus(newStock, rol));
            ps.setInt(3, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] adjustStock error: " + e.getMessage());
            return false;
        }
    }

    /** Soft-delete (is_active = false). */
    public boolean deactivate(int itemId) {
        String sql = "UPDATE inventory SET is_active=FALSE, updated_at=NOW() WHERE item_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] deactivate error: " + e.getMessage());
            return false;
        }
    }

    /** Restore an archived item to active listings. */
    public boolean reactivate(int itemId) {
        String sql = "UPDATE inventory SET is_active=TRUE, updated_at=NOW() WHERE item_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] reactivate error: " + e.getMessage());
            return false;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    @FunctionalInterface
    interface ParamSetter { void set(PreparedStatement ps) throws SQLException; }

    private List<InventoryRecord> query(String sql, ParamSetter setter) {
        List<InventoryRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] query error: " + e.getMessage());
        }
        return list;
    }

    private InventoryRecord map(ResultSet rs) throws SQLException {
        return new InventoryRecord(
            rs.getInt("item_id"),
            rs.getString("item_code"),
            rs.getString("item_name"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getInt("quantity"),
            rs.getInt("current_stock"),
            rs.getInt("minimum_stock"),
            rs.getInt("reorder_level"),
            rs.getDouble("unit_price"),
            rs.getDouble("selling_price"),
            rs.getString("unit_of_measure"),
            rs.getString("supplier"),
            rs.getDate("last_restock"),
            getOptionalDate(rs, "expiration_date"),
            rs.getString("status"),
            rs.getString("notes"),
            rs.getBoolean("is_active")
        );
    }

    private String deriveStatus(int stock, int reorderLevel) {
        if (stock == 0) return "Out of Stock";
        if (stock <= reorderLevel) return "Low Stock";
        return "In Stock";
    }

    private int getReorderLevel(Connection conn, int itemId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT reorder_level FROM inventory WHERE item_id=?")) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 10;
            }
        }
    }

    private int scalarCount(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] count error: " + e.getMessage());
            return 0;
        }
    }

    private String generateNextCode() {
        String sql = "SELECT COUNT(*) FROM inventory";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int n = rs.next() ? rs.getInt(1) + 1 : 1;
            return String.format("INV-%03d", n);
        } catch (SQLException e) {
            return "INV-" + System.currentTimeMillis();
        }
    }

    private Date getOptionalDate(ResultSet rs, String column) {
        try {
            return rs.getDate(column);
        } catch (SQLException e) {
            return null;
        }
    }

    private void ensureExpirationColumn() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureExpirationColumn(conn);
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] ensureExpirationColumn error: " + e.getMessage());
        }
    }

    private void ensureExpirationColumn(Connection conn) {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "inventory", "expiration_date")) {
            if (rs.next()) {
                return;
            }
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] check expiration_date error: " + e.getMessage());
            return;
        }
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("ALTER TABLE inventory ADD COLUMN expiration_date DATE NULL AFTER last_restock");
        } catch (SQLException e) {
            System.err.println("[InventoryDAO] add expiration_date error: " + e.getMessage());
        }
    }
}
