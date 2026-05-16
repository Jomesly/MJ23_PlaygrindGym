package mj23gym.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mj23gym.util.DatabaseConnection;

/**
 * Data-access object for `billing` and `payment_records` tables.
 */
public class PaymentDAO {

    // ── DTOs ──────────────────────────────────────────────────────

    public record BillingRecord(
        int    billingId,
        int    memberId,
        int    planId,
        Date   billingDate,
        Date   dueDate,
        double amountDue,
        double amountPaid,
        String paymentStatus,
        String status
    ) {}

    public record PaymentRecord(
        int       paymentId,
        int       memberId,
        Integer   billingId,
        String    paymentMethod,
        String    paymentType,
        Date      paymentDate,
        double    amount,
        String    transactionRef,
        String    status,
        String    notes,
        int       processedBy,
        Timestamp createdAt,
        // Joined fields
        String    memberName,
        String    memberCode
    ) {}

    public record ReceiptRecord(
        int paymentId,
        String memberCode,
        String memberName,
        String planName,
        String paymentMethod,
        String paymentType,
        Date paymentDate,
        double amountPaid,
        double invoiceAmount,
        double balanceAfter,
        String transactionRef,
        String notes,
        String processedBy
    ) {}

    // ── BILLING READ ──────────────────────────────────────────────

    public List<BillingRecord> findBillingByMember(int memberId) {
        return billingQuery(
            "SELECT * FROM billing WHERE member_id=? ORDER BY billing_date DESC",
            ps -> ps.setInt(1, memberId)
        );
    }

    public Optional<BillingRecord> findOpenBillingByMember(int memberId) {
        List<BillingRecord> rows = billingQuery(
            "SELECT * FROM billing WHERE member_id=? AND payment_status <> 'Paid' " +
            "AND status <> 'Cancelled' ORDER BY due_date ASC, billing_id DESC LIMIT 1",
            ps -> ps.setInt(1, memberId)
        );
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public double balanceDueForMember(int memberId) {
        Optional<BillingRecord> billing = findOpenBillingByMember(memberId);
        if (billing.isEmpty()) {
            return 0;
        }
        BillingRecord b = billing.get();
        return Math.max(0, b.amountDue() - b.amountPaid());
    }

    public double planPriceForMembership(String membershipType) {
        String normalized = mj23gym.dao.PlanDAO.normalizePlanName(membershipType);
        String sql =
            "SELECT price FROM plans WHERE (plan_name=? OR duration=?) AND is_active=TRUE " +
            "ORDER BY price DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalized);
            ps.setString(2, normalized);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] planPriceForMembership error: " + e.getMessage());
        }
        return fallbackPlanPrice(normalized);
    }

    public List<BillingRecord> findOverdue() {
        return billingQuery(
            "SELECT * FROM billing WHERE payment_status='Overdue' OR " +
            "(payment_status='Unpaid' AND due_date < CURDATE()) ORDER BY due_date",
            ps -> {}
        );
    }

    // ── BILLING CREATE ────────────────────────────────────────────

    public int insertBilling(BillingRecord b, int createdBy) {
        String sql =
            "INSERT INTO billing (member_id, plan_id, billing_date, due_date, amount_due, created_by)" +
            " VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.memberId());
            if (b.planId() > 0) ps.setInt(2, b.planId()); else ps.setNull(2, Types.INTEGER);
            ps.setDate(3, b.billingDate());
            ps.setDate(4, b.dueDate());
            ps.setDouble(5, b.amountDue());
            if (createdBy > 0) ps.setInt(6, createdBy); else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { return gk.next() ? gk.getInt(1) : -1; }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] insertBilling error: " + e.getMessage());
            return -1;
        }
    }

    public int ensureBillingForMember(int memberId, String membershipType, Date dueDate, int createdBy) {
        Optional<BillingRecord> existing = findOpenBillingByMember(memberId);
        if (existing.isPresent()) {
            return existing.get().billingId();
        }
        String normalized = mj23gym.dao.PlanDAO.normalizePlanName(membershipType);
        int planId = findPlanId(normalized);
        double amount = planPriceForMembership(normalized);
        BillingRecord billing = new BillingRecord(
            0,
            memberId,
            planId,
            new Date(System.currentTimeMillis()),
            dueDate,
            amount,
            0,
            "Unpaid",
            "Active"
        );
        return insertBilling(billing, createdBy);
    }

    // ── PAYMENT RECORDS ───────────────────────────────────────────

    public List<PaymentRecord> findRecentPayments(int limit) {
        String sql =
            "SELECT pr.*, CONCAT(m.first_name,' ',m.last_name) AS member_name," +
            " m.unique_member_code AS member_code" +
            " FROM payment_records pr" +
            " JOIN members m ON pr.member_id = m.member_id" +
            " ORDER BY pr.payment_date DESC, pr.created_at DESC LIMIT ?";
        return paymentQuery(sql, ps -> ps.setInt(1, limit));
    }

    public List<PaymentRecord> findByMember(int memberId) {
        String sql =
            "SELECT pr.*, CONCAT(m.first_name,' ',m.last_name) AS member_name," +
            " m.unique_member_code AS member_code" +
            " FROM payment_records pr" +
            " JOIN members m ON pr.member_id = m.member_id" +
            " WHERE pr.member_id=? ORDER BY pr.payment_date DESC";
        return paymentQuery(sql, ps -> ps.setInt(1, memberId));
    }

    public List<PaymentRecord> findByDateRange(Date from, Date to) {
        String sql =
            "SELECT pr.*, CONCAT(m.first_name,' ',m.last_name) AS member_name," +
            " m.unique_member_code AS member_code" +
            " FROM payment_records pr" +
            " JOIN members m ON pr.member_id = m.member_id" +
            " WHERE pr.payment_date BETWEEN ? AND ? ORDER BY pr.payment_date DESC";
        return paymentQuery(sql, ps -> { ps.setDate(1, from); ps.setDate(2, to); });
    }

    /** Row for reports: member + plan + payment. */
    public record PaymentSummaryRow(
        String memberName,
        String membershipType,
        double amount,
        String paymentMethod,
        Date paymentDate,
        String status
    ) {}

    public List<PaymentSummaryRow> findPaymentSummaryBetween(Date from, Date to) {
        String sql =
            "SELECT CONCAT(m.first_name,' ',m.last_name) AS member_name," +
            " m.membership_type, pr.amount, pr.payment_method, pr.payment_date, pr.status" +
            " FROM payment_records pr" +
            " JOIN members m ON pr.member_id = m.member_id" +
            " WHERE pr.payment_date BETWEEN ? AND ?" +
            " ORDER BY pr.payment_date DESC, pr.created_at DESC";
        List<PaymentSummaryRow> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PaymentSummaryRow(
                        rs.getString("member_name"),
                        rs.getString("membership_type"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getDate("payment_date"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] findPaymentSummaryBetween error: " + e.getMessage());
        }
        return list;
    }

    /** Sum of completed payments in date range (membership revenue). */
    public double sumCompletedBetween(Date from, Date to) {
        String sql =
            "SELECT COALESCE(SUM(amount),0) FROM payment_records" +
            " WHERE status='Completed' AND payment_date BETWEEN ? AND ?";
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

    public int countBetween(Date from, Date to) {
        String sql = "SELECT COUNT(*) FROM payment_records WHERE payment_date BETWEEN ? AND ?";
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

    /** Total revenue for today. */
    public double todayRevenue() {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payment_records WHERE payment_date=CURDATE() AND status='Completed'";
        return scalarDouble(sql);
    }

    /** Total revenue for a given month (YYYY-MM). */
    public double monthRevenue(String yyyyMm) {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payment_records " +
                     "WHERE DATE_FORMAT(payment_date,'%Y-%m')=? AND status='Completed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, yyyyMm);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getDouble(1) : 0; }
        } catch (SQLException e) { return 0; }
    }

    // ── PAYMENT INSERT ────────────────────────────────────────────

    /**
     * Record a payment and update the linked billing record.
     * Returns generated payment_id or -1 on failure.
     */
    public int insertPayment(int memberId, Integer billingId, String method, String type,
                             Date paymentDate, double amount, String txRef,
                             String notes, int processedBy) {
        String sql =
            "INSERT INTO payment_records (member_id, billing_id, payment_method, payment_type," +
            " payment_date, amount, transaction_ref, status, notes, processed_by)" +
            " VALUES (?,?,?,?,?,?,?,'Completed',?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            if (billingId != null) ps.setInt(2, billingId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, method);
            ps.setString(4, type);
            ps.setDate(5, paymentDate);
            ps.setDouble(6, amount);
            ps.setString(7, txRef);
            ps.setString(8, notes);
            if (processedBy > 0) ps.setInt(9, processedBy); else ps.setNull(9, Types.INTEGER);
            ps.executeUpdate();
            int pid = -1;
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) pid = gk.getInt(1); }
            // Update billing record
            if (pid > 0 && billingId != null) {
                updateBillingAfterPayment(conn, billingId, amount);
            }
            return pid;
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] insertPayment error: " + e.getMessage());
            return -1;
        }
    }

    public int processMembershipPayment(int memberId, int billingId, String method,
                                        Date paymentDate, double amount, String txRef,
                                        String notes, int processedBy) {
        String sql =
            "INSERT INTO payment_records (member_id, billing_id, payment_method, payment_type," +
            " payment_date, amount, transaction_ref, status, notes, processed_by)" +
            " VALUES (?,?,?,?,?,?,?,'Completed',?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            int paymentId;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, memberId);
                if (billingId > 0) ps.setInt(2, billingId); else ps.setNull(2, Types.INTEGER);
                ps.setString(3, method);
                ps.setString(4, "Membership");
                ps.setDate(5, paymentDate);
                ps.setDouble(6, amount);
                ps.setString(7, txRef);
                ps.setString(8, notes);
                if (processedBy > 0) ps.setInt(9, processedBy); else ps.setNull(9, Types.INTEGER);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    paymentId = keys.next() ? keys.getInt(1) : -1;
                }
            }
            if (paymentId > 0 && billingId > 0) {
                updateBillingAfterPayment(conn, billingId, amount);
            }
            conn.commit();
            return paymentId;
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] processMembershipPayment error: " + e.getMessage());
            return -1;
        }
    }

    public Optional<ReceiptRecord> findReceipt(int paymentId) {
        String sql =
            "SELECT pr.payment_id, pr.payment_method, pr.payment_type, pr.payment_date, pr.amount," +
            " pr.transaction_ref, pr.notes, m.unique_member_code, CONCAT(m.first_name,' ',m.last_name) AS member_name," +
            " m.membership_type, b.amount_due, b.amount_paid, u.full_name AS processed_by_name" +
            " FROM payment_records pr" +
            " JOIN members m ON pr.member_id = m.member_id" +
            " LEFT JOIN billing b ON pr.billing_id = b.billing_id" +
            " LEFT JOIN users u ON pr.processed_by = u.user_id" +
            " WHERE pr.payment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double due = rs.getDouble("amount_due");
                    double paid = rs.getDouble("amount_paid");
                    return Optional.of(new ReceiptRecord(
                        rs.getInt("payment_id"),
                        rs.getString("unique_member_code"),
                        rs.getString("member_name"),
                        rs.getString("membership_type"),
                        rs.getString("payment_method"),
                        rs.getString("payment_type"),
                        rs.getDate("payment_date"),
                        rs.getDouble("amount"),
                        due,
                        Math.max(0, due - paid),
                        rs.getString("transaction_ref"),
                        rs.getString("notes"),
                        rs.getString("processed_by_name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] findReceipt error: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void updateBillingAfterPayment(Connection conn, int billingId, double paidAmount) {
        String sql = "UPDATE billing SET" +
                     " payment_status = CASE WHEN amount_paid + ? >= amount_due THEN 'Paid' ELSE 'Unpaid' END," +
                     " status = CASE WHEN amount_paid + ? >= amount_due THEN 'Completed' ELSE 'Active' END," +
                     " amount_paid = amount_paid + ?, updated_at=NOW() WHERE billing_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, paidAmount);
            ps.setDouble(2, paidAmount);
            ps.setDouble(3, paidAmount);
            ps.setInt(4, billingId);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    @FunctionalInterface
    interface ParamSetter { void set(PreparedStatement ps) throws SQLException; }

    private List<BillingRecord> billingQuery(String sql, ParamSetter setter) {
        List<BillingRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BillingRecord(
                        rs.getInt("billing_id"), rs.getInt("member_id"),
                        rs.getInt("plan_id"), rs.getDate("billing_date"),
                        rs.getDate("due_date"), rs.getDouble("amount_due"),
                        rs.getDouble("amount_paid"), rs.getString("payment_status"),
                        rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] billingQuery error: " + e.getMessage());
        }
        return list;
    }

    private List<PaymentRecord> paymentQuery(String sql, ParamSetter setter) {
        List<PaymentRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PaymentRecord(
                        rs.getInt("payment_id"), rs.getInt("member_id"),
                        (Integer) rs.getObject("billing_id"),
                        rs.getString("payment_method"), rs.getString("payment_type"),
                        rs.getDate("payment_date"), rs.getDouble("amount"),
                        rs.getString("transaction_ref"), rs.getString("status"),
                        rs.getString("notes"), rs.getInt("processed_by"),
                        rs.getTimestamp("created_at"),
                        rs.getString("member_name"), rs.getString("member_code")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] paymentQuery error: " + e.getMessage());
        }
        return list;
    }

    private double scalarDouble(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    /** Billing rows still marked Unpaid (not yet fully paid). */
    public int countPendingInvoices() {
        String sql = "SELECT COUNT(*) FROM billing WHERE payment_status = 'Unpaid'";
        return scalarInt(sql);
    }

    /** Billing overdue or unpaid past due date. */
    public int countOverdueAccounts() {
        return findOverdue().size();
    }

    /** Completed membership (or any) payments recorded this calendar month. */
    public int countCompletedPaymentsThisMonth() {
        String sql =
            "SELECT COUNT(*) FROM payment_records WHERE status = 'Completed' " +
            "AND YEAR(payment_date) = YEAR(CURDATE()) AND MONTH(payment_date) = MONTH(CURDATE())";
        return scalarInt(sql);
    }

    private int scalarInt(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private int findPlanId(String planName) {
        String sql = "SELECT plan_id FROM plans WHERE plan_name=? OR duration=? ORDER BY is_active DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, planName);
            ps.setString(2, planName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("plan_id") : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    private double fallbackPlanPrice(String planName) {
        if ("Per Session".equalsIgnoreCase(planName) || "Daily".equalsIgnoreCase(planName)) return 100;
        if ("Quarterly".equalsIgnoreCase(planName)) return 1988;
        if ("Semi Annual".equalsIgnoreCase(planName)) return 3288;
        if ("Annual".equalsIgnoreCase(planName) || "Yearly".equalsIgnoreCase(planName)) return 4988;
        return 788;
    }
}
