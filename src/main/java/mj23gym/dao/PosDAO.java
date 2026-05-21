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
 * Point-of-sale: writes {@code pos_transactions}, line items, and decrements inventory stock.
 */
public final class PosDAO {

    public record SaleLine(int itemId, String itemName, int quantity, double unitPrice) {}

    public record ReceiptLine(String itemName, int quantity, double unitPrice, double subtotal) {}

    public record PosReceipt(
        int transactionId,
        Date saleDate,
        String paymentMethod,
        String referenceNumber,
        double totalAmount,
        String processedBy,
        List<ReceiptLine> lines
    ) {}

    /** One line from POS history for reporting. */
    public record SaleDetailRow(
        Date saleDate,
        String itemName,
        int quantity,
        double unitPrice,
        double subtotal,
        String paymentMethod
    ) {}

    /**
     * POS line items whose parent sale falls on {@code from}–{@code to} (inclusive, by date).
     */
    public List<SaleDetailRow> findSaleLinesBetween(Date from, Date to) {
        String sql =
            "SELECT DATE(pt.sale_date) AS sale_day, pti.item_name, pti.quantity," +
            " pti.unit_price, pti.subtotal, pt.payment_method" +
            " FROM pos_transaction_items pti" +
            " JOIN pos_transactions pt ON pti.transaction_id = pt.transaction_id" +
            " WHERE DATE(pt.sale_date) BETWEEN ? AND ?" +
            " ORDER BY pt.sale_date DESC, pti.transaction_item_id DESC";
        List<SaleDetailRow> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SaleDetailRow(
                        rs.getDate("sale_day"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("subtotal"),
                        rs.getString("payment_method")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[PosDAO] findSaleLinesBetween error: " + e.getMessage());
        }
        return list;
    }

    public double sumPosRevenueBetween(Date from, Date to) {
        String sql =
            "SELECT COALESCE(SUM(total_amount),0) FROM pos_transactions" +
            " WHERE DATE(sale_date) BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    public int countPosTransactionsBetween(Date from, Date to) {
        String sql =
            "SELECT COUNT(*) FROM pos_transactions WHERE DATE(sale_date) BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    /** Total POS sales recorded today (by {@code sale_date}). */
    public double todayPosTotal() {
        String sql =
            "SELECT COALESCE(SUM(total_amount),0) FROM pos_transactions WHERE DATE(sale_date)=CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public Optional<PosReceipt> findReceipt(int transactionId) {
        String txSql =
            "SELECT pt.transaction_id, DATE(pt.sale_date) AS sale_day, pt.payment_method," +
            " pt.reference_number, pt.total_amount, u.full_name AS processed_by_name" +
            " FROM pos_transactions pt" +
            " LEFT JOIN users u ON pt.processed_by = u.user_id" +
            " WHERE pt.transaction_id=?";
        String lineSql =
            "SELECT item_name, quantity, unit_price, subtotal FROM pos_transaction_items" +
            " WHERE transaction_id=? ORDER BY transaction_item_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement txPs = conn.prepareStatement(txSql)) {
            txPs.setInt(1, transactionId);
            try (ResultSet txRs = txPs.executeQuery()) {
                if (!txRs.next()) {
                    return Optional.empty();
                }
                List<ReceiptLine> lines = new ArrayList<>();
                try (PreparedStatement linePs = conn.prepareStatement(lineSql)) {
                    linePs.setInt(1, transactionId);
                    try (ResultSet rs = linePs.executeQuery()) {
                        while (rs.next()) {
                            lines.add(new ReceiptLine(
                                rs.getString("item_name"),
                                rs.getInt("quantity"),
                                rs.getDouble("unit_price"),
                                rs.getDouble("subtotal")
                            ));
                        }
                    }
                }
                return Optional.of(new PosReceipt(
                    txRs.getInt("transaction_id"),
                    txRs.getDate("sale_day"),
                    txRs.getString("payment_method"),
                    txRs.getString("reference_number"),
                    txRs.getDouble("total_amount"),
                    txRs.getString("processed_by_name"),
                    lines
                ));
            }
        } catch (SQLException e) {
            System.err.println("[PosDAO] findReceipt error: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Persist a sale and reduce {@code inventory.current_stock} (and {@code quantity}) per line.
     *
     * @return new {@code transaction_id}, or {@code -1} on failure
     */
    public int completeSale(
        Integer memberId,
        String paymentMethod,
        String referenceNumber,
        int processedBy,
        List<SaleLine> lines
    ) {
        if (lines == null || lines.isEmpty()) {
            return -1;
        }
        double total = 0;
        for (SaleLine l : lines) {
            total += l.unitPrice() * l.quantity();
        }
        String insertTx =
            "INSERT INTO pos_transactions (member_id, payment_method, reference_number, total_amount, processed_by) " +
            "VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (SaleLine l : lines) {
                try (PreparedStatement chk = conn.prepareStatement(
                        "SELECT current_stock FROM inventory WHERE item_id=? AND is_active=TRUE")) {
                    chk.setInt(1, l.itemId());
                    try (ResultSet rs = chk.executeQuery()) {
                        if (!rs.next() || rs.getInt(1) < l.quantity()) {
                            return -1;
                        }
                    }
                }
            }

            conn.setAutoCommit(false);
            try {
                int txId;
                try (PreparedStatement ps = conn.prepareStatement(insertTx, Statement.RETURN_GENERATED_KEYS)) {
                    if (memberId != null) {
                        ps.setInt(1, memberId);
                    } else {
                        ps.setNull(1, Types.INTEGER);
                    }
                    ps.setString(2, paymentMethod != null ? paymentMethod : "Cash");
                    ps.setString(3, referenceNumber != null && !referenceNumber.isBlank() ? referenceNumber : null);
                    ps.setDouble(4, total);
                    if (processedBy > 0) {
                        ps.setInt(5, processedBy);
                    } else {
                        ps.setNull(5, Types.INTEGER);
                    }
                    ps.executeUpdate();
                    try (ResultSet gk = ps.getGeneratedKeys()) {
                        if (!gk.next()) {
                            conn.rollback();
                            return -1;
                        }
                        txId = gk.getInt(1);
                    }
                }

                String insertLine =
                    "INSERT INTO pos_transaction_items (transaction_id, item_id, item_name, quantity, unit_price, subtotal) " +
                    "VALUES (?,?,?,?,?,?)";
                String updStock =
                    "UPDATE inventory SET current_stock = current_stock - ?, quantity = quantity - ?, " +
                    "status = CASE " +
                    "WHEN current_stock - ? <= 0 THEN 'Out of Stock' " +
                    "WHEN current_stock - ? <= reorder_level THEN 'Low Stock' " +
                    "ELSE 'In Stock' END, updated_at = NOW() WHERE item_id = ?";

                try (PreparedStatement linePs = conn.prepareStatement(insertLine);
                     PreparedStatement stockPs = conn.prepareStatement(updStock)) {
                    for (SaleLine l : lines) {
                        double sub = l.unitPrice() * l.quantity();
                        linePs.setInt(1, txId);
                        linePs.setInt(2, l.itemId());
                        linePs.setString(3, l.itemName());
                        linePs.setInt(4, l.quantity());
                        linePs.setDouble(5, l.unitPrice());
                        linePs.setDouble(6, sub);
                        linePs.addBatch();

                        stockPs.setInt(1, l.quantity());
                        stockPs.setInt(2, l.quantity());
                        stockPs.setInt(3, l.quantity());
                        stockPs.setInt(4, l.quantity());
                        stockPs.setInt(5, l.itemId());
                        stockPs.addBatch();
                    }
                    linePs.executeBatch();
                    stockPs.executeBatch();
                }

                conn.commit();
                return txId;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[PosDAO] completeSale error: " + e.getMessage());
                return -1;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("[PosDAO] completeSale connection error: " + e.getMessage());
            return -1;
        }
    }
}
