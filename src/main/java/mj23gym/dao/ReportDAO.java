package mj23gym.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mj23gym.util.DatabaseConnection;

/**
 * Data-access object for generated system reports.
 */
public final class ReportDAO {

    public record ReportRecord(
        int reportId,
        String reportName,
        String reportType,
        Date periodStart,
        Date periodEnd,
        String notes,
        String generatedBy,
        String filePath,
        Timestamp generatedAt
    ) {}

    public int saveReport(
        String reportName,
        String reportType,
        Date periodStart,
        Date periodEnd,
        String notes,
        String filePath,
        int generatedBy
    ) {
        String sql =
            "INSERT INTO reports (report_name, report_type, report_period_start, report_period_end, notes, file_path, generated_by) " +
            "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, reportName);
            ps.setString(2, normalizeReportType(reportType));
            ps.setDate(3, periodStart);
            ps.setDate(4, periodEnd);
            ps.setString(5, notes);
            ps.setString(6, filePath);
            if (generatedBy > 0) {
                ps.setInt(7, generatedBy);
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] saveReport error: " + e.getMessage());
            return -1;
        }
    }

    public List<ReportRecord> findRecentReports(int limit) {
        String sql =
            "SELECT r.report_id, r.report_name, r.report_type, r.report_period_start, r.report_period_end," +
            " r.notes, r.file_path, r.generated_at, u.full_name AS generated_by_name" +
            " FROM reports r LEFT JOIN users u ON r.generated_by = u.user_id" +
            " ORDER BY r.generated_at DESC, r.report_id DESC LIMIT ?";
        List<ReportRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] findRecentReports error: " + e.getMessage());
        }
        return list;
    }

    public Optional<ReportRecord> findReport(int reportId) {
        String sql =
            "SELECT r.report_id, r.report_name, r.report_type, r.report_period_start, r.report_period_end," +
            " r.notes, r.file_path, r.generated_at, u.full_name AS generated_by_name" +
            " FROM reports r LEFT JOIN users u ON r.generated_by = u.user_id" +
            " WHERE r.report_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapReport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] findReport error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public ReportMetrics buildMetrics(Date from, Date to) {
        return new ReportMetrics(
            scalarInt("SELECT COUNT(*) FROM members WHERE created_at BETWEEN ? AND DATE_ADD(?, INTERVAL 1 DAY)", from, to),
            scalarInt("SELECT COUNT(*) FROM members WHERE status='Active'", from, to),
            scalarInt("SELECT COUNT(*) FROM attendance WHERE attendance_date BETWEEN ? AND ?", from, to),
            scalarInt("SELECT COUNT(*) FROM inventory WHERE is_active=TRUE", from, to),
            scalarInt("SELECT COUNT(*) FROM inventory WHERE is_active=TRUE AND current_stock <= reorder_level", from, to),
            scalarInt("SELECT COUNT(*) FROM equipment WHERE is_active=TRUE", from, to),
            scalarInt("SELECT COUNT(*) FROM equipment WHERE is_active=TRUE AND (`condition`='Maintenance' OR `condition`='Broken' OR next_maintenance <= DATE_ADD(CURDATE(), INTERVAL 30 DAY))", from, to),
            scalarDouble("SELECT COALESCE(SUM(amount),0) FROM payment_records WHERE status='Completed' AND payment_date BETWEEN ? AND ?", from, to),
            scalarDouble("SELECT COALESCE(SUM(total_amount),0) FROM pos_transactions WHERE DATE(sale_date) BETWEEN ? AND ?", from, to),
            scalarInt("SELECT COUNT(*) FROM payment_records WHERE payment_date BETWEEN ? AND ?", from, to),
            scalarInt("SELECT COUNT(*) FROM pos_transactions WHERE DATE(sale_date) BETWEEN ? AND ?", from, to)
        );
    }

    public record ReportMetrics(
        int newMembers,
        int activeMembers,
        int attendanceCheckIns,
        int activeInventoryItems,
        int lowStockItems,
        int activeEquipment,
        int equipmentNeedsAttention,
        double membershipRevenue,
        double posRevenue,
        int paymentTransactions,
        int posTransactions
    ) {
        public double totalRevenue() {
            return membershipRevenue + posRevenue;
        }

        public int totalTransactions() {
            return paymentTransactions + posTransactions;
        }
    }

    public String buildNotes(String reportType, Date from, Date to, ReportMetrics m) {
        String type = normalizeReportType(reportType);
        StringBuilder sb = new StringBuilder();
        sb.append("Report Type: ").append(reportType == null || reportType.isBlank() ? type : reportType).append('\n');
        sb.append("Period: ").append(from).append(" to ").append(to).append('\n');
        sb.append('\n');
        if ("Inventory".equals(type)) {
            sb.append("Inventory Summary").append('\n');
            sb.append("- Active Inventory Items: ").append(m.activeInventoryItems()).append('\n');
            sb.append("- Low Stock Items: ").append(m.lowStockItems()).append('\n');
            sb.append("- Active Equipment: ").append(m.activeEquipment()).append('\n');
            sb.append("- Equipment Needing Attention: ").append(m.equipmentNeedsAttention()).append('\n');
        } else if (reportType != null && reportType.toLowerCase().contains("payment")) {
            sb.append("Payment Summary").append('\n');
            sb.append("- Membership Revenue: PHP ").append(String.format("%.2f", m.membershipRevenue())).append('\n');
            sb.append("- Payment Transactions: ").append(m.paymentTransactions()).append('\n');
            sb.append("- New Members: ").append(m.newMembers()).append('\n');
            sb.append("- Active Members: ").append(m.activeMembers()).append('\n');
        } else {
            sb.append("Sales Summary").append('\n');
            sb.append("- POS Revenue: PHP ").append(String.format("%.2f", m.posRevenue())).append('\n');
            sb.append("- POS Transactions: ").append(m.posTransactions()).append('\n');
            sb.append("- Total Revenue: PHP ").append(String.format("%.2f", m.posRevenue())).append('\n');
        }
        return sb.toString();
    }

    public String normalizeReportType(String reportType) {
        if (reportType == null || reportType.isBlank()) {
            return "Other";
        }
        if ("Sales".equalsIgnoreCase(reportType)
                || "Sales Report".equalsIgnoreCase(reportType)
                || "Payment".equalsIgnoreCase(reportType)
                || "Payment Report".equalsIgnoreCase(reportType)
                || "Billing".equalsIgnoreCase(reportType)
                || "Financial".equalsIgnoreCase(reportType)) {
            return "Financial";
        }
        if ("Membership".equalsIgnoreCase(reportType)
                || "Attendance".equalsIgnoreCase(reportType)
                || "Inventory".equalsIgnoreCase(reportType)
                || "Inventory Report".equalsIgnoreCase(reportType)
                || "Equipment".equalsIgnoreCase(reportType)) {
            return reportType.toLowerCase().contains("inventory") ? "Inventory" : capitalize(reportType);
        }
        return "Other";
    }

    private ReportRecord mapReport(ResultSet rs) throws SQLException {
        String generatedBy = rs.getString("generated_by_name");
        if (generatedBy == null || generatedBy.isBlank()) {
            generatedBy = "System";
        }
        return new ReportRecord(
            rs.getInt("report_id"),
            rs.getString("report_name"),
            rs.getString("report_type"),
            rs.getDate("report_period_start"),
            rs.getDate("report_period_end"),
            rs.getString("notes"),
            generatedBy,
            rs.getString("file_path"),
            rs.getTimestamp("generated_at")
        );
    }

    private int scalarInt(String sql, Date from, Date to) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramCount = countPlaceholders(sql);
            if (paramCount >= 1) {
                ps.setDate(1, from);
            }
            if (paramCount >= 2) {
                ps.setDate(2, to);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] scalarInt error: " + e.getMessage());
            return 0;
        }
    }

    private int countPlaceholders(String sql) {
        int count = 0;
        for (int i = 0; i < sql.length(); i++) {
            if (sql.charAt(i) == '?') {
                count++;
            }
        }
        return count;
    }

    private double scalarDouble(String sql, Date from, Date to) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] scalarDouble error: " + e.getMessage());
            return 0;
        }
    }

    private String capitalize(String value) {
        String lower = value.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
