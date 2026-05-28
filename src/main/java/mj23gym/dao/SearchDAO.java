package mj23gym.dao;

import mj23gym.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Cross-module search for members, inventory, POS sales, and membership payments.
 */
public final class SearchDAO {
    public record SearchResult(
        String module,
        String title,
        String detail,
        String status,
        Timestamp recordDate
    ) {}

    public List<SearchResult> searchAll(String keyword) {
        List<SearchResult> results = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            return results;
        }
        String like = "%" + keyword.trim() + "%";
        results.addAll(searchMembers(like));
        results.addAll(searchInventory(like));
        results.addAll(searchPayments(like));
        results.addAll(searchPos(like));
        return results;
    }

    private List<SearchResult> searchMembers(String like) {
        String sql =
            "SELECT unique_member_code, first_name, last_name, email, contact_number, membership_type, status, created_at " +
            "FROM members WHERE status <> 'Cancelled' AND (unique_member_code LIKE ? OR first_name LIKE ? OR last_name LIKE ? " +
            "OR email LIKE ? OR contact_number LIKE ?) ORDER BY updated_at DESC LIMIT 25";
        return query(sql, like, rs -> new SearchResult(
            "Members",
            rs.getString("unique_member_code") + " - " + rs.getString("first_name") + " " + rs.getString("last_name"),
            "Plan: " + rs.getString("membership_type") + " | Email: " + rs.getString("email") + " | Phone: " + rs.getString("contact_number"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        ));
    }

    private List<SearchResult> searchInventory(String like) {
        String sql =
            "SELECT item_code, item_name, category, current_stock, reorder_level, status, updated_at " +
            "FROM inventory WHERE is_active=TRUE AND (item_code LIKE ? OR item_name LIKE ? OR category LIKE ? " +
            "OR supplier LIKE ? OR status LIKE ?) ORDER BY updated_at DESC LIMIT 25";
        return query(sql, like, rs -> new SearchResult(
            "Inventory",
            rs.getString("item_code") + " - " + rs.getString("item_name"),
            "Category: " + rs.getString("category") + " | QTY: " + rs.getInt("current_stock") + " | Restock at: " + rs.getInt("reorder_level"),
            rs.getString("status"),
            rs.getTimestamp("updated_at")
        ));
    }

    private List<SearchResult> searchPayments(String like) {
        String sql =
            "SELECT pr.payment_id, pr.amount, pr.payment_method, pr.transaction_ref, pr.status, pr.created_at, " +
            "m.unique_member_code, CONCAT(m.first_name,' ',m.last_name) AS member_name " +
            "FROM payment_records pr JOIN members m ON pr.member_id=m.member_id " +
            "WHERE pr.amount > 0 AND m.status <> 'Cancelled' AND (CAST(pr.payment_id AS CHAR) LIKE ? OR pr.transaction_ref LIKE ? OR pr.payment_method LIKE ? " +
            "OR m.unique_member_code LIKE ? OR m.first_name LIKE ? OR m.last_name LIKE ?) " +
            "ORDER BY pr.created_at DESC LIMIT 25";
        return query(sql, like, rs -> new SearchResult(
            "Payments",
            "Payment #" + rs.getInt("payment_id") + " - " + rs.getString("member_name"),
            "Member: " + rs.getString("unique_member_code") + " | Method: " + rs.getString("payment_method") +
                " | Amount: PHP " + String.format("%.2f", rs.getDouble("amount")) +
                " | Ref: " + safe(rs.getString("transaction_ref")),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        ));
    }

    private List<SearchResult> searchPos(String like) {
        String sql =
            "SELECT DISTINCT pt.transaction_id, pt.payment_method, pt.reference_number, pt.total_amount, pt.sale_date, " +
            "COALESCE(CONCAT(m.first_name,' ',m.last_name), 'Walk-in') AS buyer, " +
            "GROUP_CONCAT(pti.item_name SEPARATOR ', ') AS items " +
            "FROM pos_transactions pt " +
            "LEFT JOIN members m ON pt.member_id=m.member_id " +
            "LEFT JOIN pos_transaction_items pti ON pt.transaction_id=pti.transaction_id " +
            "WHERE CAST(pt.transaction_id AS CHAR) LIKE ? OR pt.reference_number LIKE ? OR pt.payment_method LIKE ? " +
            "OR m.first_name LIKE ? OR m.last_name LIKE ? OR pti.item_name LIKE ? " +
            "GROUP BY pt.transaction_id, pt.payment_method, pt.reference_number, pt.total_amount, pt.sale_date, buyer " +
            "ORDER BY pt.sale_date DESC LIMIT 25";
        return query(sql, like, rs -> new SearchResult(
            "POS",
            "POS #" + rs.getInt("transaction_id") + " - " + rs.getString("buyer"),
            "Items: " + safe(rs.getString("items")) + " | Method: " + rs.getString("payment_method") +
                " | Total: PHP " + String.format("%.2f", rs.getDouble("total_amount")) +
                " | Ref: " + safe(rs.getString("reference_number")),
            "Completed",
            rs.getTimestamp("sale_date")
        ));
    }

    private interface Mapper {
        SearchResult map(ResultSet rs) throws SQLException;
    }

    private List<SearchResult> query(String sql, String like, Mapper mapper) {
        List<SearchResult> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= ps.getParameterMetaData().getParameterCount(); i++) {
                ps.setString(i, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[SearchDAO] query error: " + e.getMessage());
        }
        return rows;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
