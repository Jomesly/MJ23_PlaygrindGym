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
        String    planTypeSnapshot,
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

    public record BillingSummaryRow(
        int billingId,
        int memberId,
        String memberCode,
        String memberName,
        Date dueDate,
        double amountDue,
        double amountPaid,
        String paymentStatus,
        String status
    ) {}

    public record UpgradeResult(
        int paymentId,
        int billingId,
        String previousPlan,
        String newPlan
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
            "SELECT b.* FROM billing b JOIN members m ON b.member_id=m.member_id " +
            "WHERE b.member_id=? AND m.status <> 'Cancelled' AND b.payment_status <> 'Paid' " +
            "AND b.status <> 'Cancelled' ORDER BY b.due_date ASC, b.billing_id DESC LIMIT 1",
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
            "SELECT b.* FROM billing b JOIN members m ON b.member_id=m.member_id " +
            "WHERE (b.payment_status='Overdue' OR (b.payment_status='Unpaid' AND b.due_date < CURDATE())) " +
            "AND b.status <> 'Cancelled' AND m.status <> 'Cancelled' ORDER BY b.due_date",
            ps -> {}
        );
    }

    public List<BillingSummaryRow> findPendingInvoiceSummaries() {
        String sql =
            "SELECT b.billing_id, b.member_id, m.unique_member_code AS member_code," +
            " CONCAT(m.first_name,' ',m.last_name) AS member_name, b.due_date," +
            " b.amount_due, b.amount_paid, b.payment_status, b.status" +
            " FROM billing b JOIN members m ON b.member_id=m.member_id" +
            " WHERE b.payment_status='Unpaid' AND b.due_date >= CURDATE()" +
            " AND b.status <> 'Cancelled' AND m.status <> 'Cancelled'" +
            " ORDER BY b.due_date ASC, b.billing_id DESC";
        return billingSummaryQuery(sql);
    }

    public List<BillingSummaryRow> findOverdueInvoiceSummaries() {
        String sql =
            "SELECT b.billing_id, b.member_id, m.unique_member_code AS member_code," +
            " CONCAT(m.first_name,' ',m.last_name) AS member_name, b.due_date," +
            " b.amount_due, b.amount_paid, b.payment_status, b.status" +
            " FROM billing b JOIN members m ON b.member_id=m.member_id" +
            " WHERE (b.payment_status='Overdue' OR (b.payment_status='Unpaid' AND b.due_date < CURDATE()))" +
            " AND b.status <> 'Cancelled' AND m.status <> 'Cancelled'" +
            " ORDER BY b.due_date ASC, b.billing_id DESC";
        return billingSummaryQuery(sql);
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

    public void closeBillingOnUpgrade(int memberId, int processedBy) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            closeBillingOnUpgrade(conn, memberId, processedBy);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new IllegalStateException("Could not close old billing records for upgrade.", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public UpgradeResult processPlanUpgrade(MemberDAO.MemberRecord updatedMember, int processedBy) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            ensurePlanSnapshotColumn(conn);
            ensureMemberSessionColumns(conn);

            String previousPlan = currentMembershipType(conn, updatedMember.memberId());
            closeBillingOnUpgrade(conn, updatedMember.memberId(), processedBy);
            int paymentId = insertUpgradeSnapshotPayment(
                conn,
                updatedMember.memberId(),
                previousPlan,
                updatedMember.membershipType(),
                processedBy
            );
            updateMemberForUpgrade(conn, updatedMember);
            int billingId = createBillingForMember(
                conn,
                updatedMember.memberId(),
                updatedMember.membershipType(),
                updatedMember.membershipEndDate(),
                processedBy
            );

            conn.commit();
            return new UpgradeResult(paymentId, billingId, previousPlan, updatedMember.membershipType());
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new IllegalStateException("Could not complete plan upgrade.", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // ── PAYMENT RECORDS ───────────────────────────────────────────

    public List<PaymentRecord> findRecentPayments(int limit) {
        String sql =
            "SELECT pr.*, CONCAT(m.first_name,' ',m.last_name) AS member_name," +
            " m.unique_member_code AS member_code" +
            " FROM payment_records pr" +
            " JOIN members m ON pr.member_id = m.member_id" +
            " WHERE m.status <> 'Cancelled'" +
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
            " WHERE pr.payment_date BETWEEN ? AND ?" +
            " AND m.status <> 'Cancelled'" +
            " ORDER BY pr.payment_date DESC";
        return paymentQuery(sql, ps -> { ps.setDate(1, from); ps.setDate(2, to); });
    }

    /** Row for reports: member + plan + payment. */
    public record PaymentSummaryRow(
        String memberName,
        String planTypeSnapshot,
        String membershipType,
        double amount,
        String paymentMethod,
        Date paymentDate,
        String status
    ) {}

    public List<PaymentSummaryRow> findPaymentSummaryBetween(Date from, Date to) {
        String sql =
            "SELECT CONCAT(m.first_name,' ',m.last_name) AS member_name," +
            " pr.plan_type_snapshot, m.membership_type, pr.amount, pr.payment_method, pr.payment_date, pr.status" +
            " FROM payment_records pr" +
            " JOIN members m ON pr.member_id = m.member_id" +
            " WHERE pr.payment_date BETWEEN ? AND ?" +
            " AND pr.amount > 0" +
            " AND m.status <> 'Cancelled'" +
            " ORDER BY pr.payment_date DESC, pr.created_at DESC";
        List<PaymentSummaryRow> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensurePlanSnapshotColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PaymentSummaryRow(
                        rs.getString("member_name"),
                        rs.getString("plan_type_snapshot"),
                        rs.getString("membership_type"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getDate("payment_date"),
                        rs.getString("status")
                    ));
                }
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
            "SELECT COALESCE(SUM(pr.amount),0) FROM payment_records pr " +
            "JOIN members m ON pr.member_id=m.member_id " +
            "WHERE pr.status='Completed' AND pr.amount > 0 AND pr.payment_date BETWEEN ? AND ? " +
            "AND m.status <> 'Cancelled'";
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
        String sql =
            "SELECT COUNT(*) FROM payment_records pr JOIN members m ON pr.member_id=m.member_id " +
            "WHERE pr.amount > 0 AND pr.payment_date BETWEEN ? AND ? AND m.status <> 'Cancelled'";
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
        String sql =
            "SELECT COALESCE(SUM(pr.amount),0) FROM payment_records pr " +
            "JOIN members m ON pr.member_id=m.member_id " +
            "WHERE pr.payment_date=CURDATE() AND pr.status='Completed' AND pr.amount > 0 " +
            "AND m.status <> 'Cancelled'";
        return scalarDouble(sql);
    }

    /** Total revenue for a given month (YYYY-MM). */
    public double monthRevenue(String yyyyMm) {
        String sql = "SELECT COALESCE(SUM(pr.amount),0) FROM payment_records pr " +
                     "JOIN members m ON pr.member_id=m.member_id " +
                     "WHERE DATE_FORMAT(pr.payment_date,'%Y-%m')=? AND pr.status='Completed' " +
                     "AND pr.amount > 0 AND m.status <> 'Cancelled'";
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
            "INSERT INTO payment_records (member_id, billing_id, payment_method, payment_type, plan_type_snapshot," +
            " payment_date, amount, transaction_ref, status, notes, processed_by)" +
            " VALUES (?,?,?,?,?,?,?,?, 'Completed',?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensurePlanSnapshotColumn(conn);
            ensureMemberSessionColumns(conn);
            String planSnapshot = currentMembershipType(conn, memberId);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            if (billingId != null) ps.setInt(2, billingId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, method);
            ps.setString(4, type);
            ps.setString(5, planSnapshot);
            ps.setDate(6, paymentDate);
            ps.setDouble(7, amount);
            ps.setString(8, txRef);
            ps.setString(9, notes);
            if (processedBy > 0) ps.setInt(10, processedBy); else ps.setNull(10, Types.INTEGER);
            ps.executeUpdate();
            int pid = -1;
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) pid = gk.getInt(1); }
            // Update billing record
            if (pid > 0) {
                incrementPerSessionCreditIfNeeded(conn, memberId, planSnapshot);
            }
            if (pid > 0 && billingId != null) {
                updateBillingAfterPayment(conn, billingId, amount);
            }
            return pid;
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] insertPayment error: " + e.getMessage());
            return -1;
        }
    }

    public int processMembershipPayment(int memberId, int billingId, String method,
                                        Date paymentDate, double amount, String txRef,
                                        String notes, int processedBy) {
        String sql =
            "INSERT INTO payment_records (member_id, billing_id, payment_method, payment_type, plan_type_snapshot," +
            " payment_date, amount, transaction_ref, status, notes, processed_by)" +
            " VALUES (?,?,?,?,?,?,?,?, 'Completed',?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensurePlanSnapshotColumn(conn);
            ensureMemberSessionColumns(conn);
            String planSnapshot = currentMembershipType(conn, memberId);
            conn.setAutoCommit(false);
            int paymentId;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, memberId);
                if (billingId > 0) ps.setInt(2, billingId); else ps.setNull(2, Types.INTEGER);
                ps.setString(3, method);
                ps.setString(4, "Membership");
                ps.setString(5, planSnapshot);
                ps.setDate(6, paymentDate);
                ps.setDouble(7, amount);
                ps.setString(8, txRef);
                ps.setString(9, notes);
                if (processedBy > 0) ps.setInt(10, processedBy); else ps.setNull(10, Types.INTEGER);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    paymentId = keys.next() ? keys.getInt(1) : -1;
                }
            }
            if (paymentId > 0) {
                incrementPerSessionCreditIfNeeded(conn, memberId, planSnapshot);
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
            " pr.plan_type_snapshot, m.membership_type, b.amount_due, b.amount_paid, u.full_name AS processed_by_name" +
            " FROM payment_records pr" +
            " JOIN members m ON pr.member_id = m.member_id" +
            " LEFT JOIN billing b ON pr.billing_id = b.billing_id" +
            " LEFT JOIN users u ON pr.processed_by = u.user_id" +
            " WHERE pr.payment_id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensurePlanSnapshotColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double due = rs.getDouble("amount_due");
                    double paid = rs.getDouble("amount_paid");
                    return Optional.of(new ReceiptRecord(
                        rs.getInt("payment_id"),
                        rs.getString("unique_member_code"),
                        rs.getString("member_name"),
                        firstPresent(rs.getString("plan_type_snapshot"), rs.getString("membership_type")),
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

    private void closeBillingOnUpgrade(Connection conn, int memberId, int processedBy) throws SQLException {
        String selectSql =
            "SELECT billing_id, member_id, plan_id, billing_date, due_date, amount_due, amount_paid," +
            " payment_status, status, created_by, created_at, updated_at" +
            " FROM billing WHERE member_id=? AND status='Active' AND payment_status <> 'Paid'" +
            " FOR UPDATE";
        String updateSql = "UPDATE billing SET status='Cancelled', updated_at=NOW() WHERE billing_id=?";
        try (PreparedStatement select = conn.prepareStatement(selectSql)) {
            select.setInt(1, memberId);
            try (ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    int billingId = rs.getInt("billing_id");
                    String snapshot = billingSnapshotJson(rs, "Closed on plan upgrade");
                    try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                        update.setInt(1, billingId);
                        update.executeUpdate();
                    }
                    logBillingUpgradeCancellation(conn, billingId, snapshot, processedBy);
                }
            }
        }
    }

    private int insertUpgradeSnapshotPayment(Connection conn, int memberId, String previousPlan,
                                             String newPlan, int processedBy) throws SQLException {
        String sql =
            "INSERT INTO payment_records (member_id, billing_id, payment_method, payment_type, plan_type_snapshot," +
            " payment_date, amount, transaction_ref, status, notes, processed_by)" +
            " VALUES (?, NULL, 'Other', 'Membership', ?, CURDATE(), 0, ?, 'Completed', ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            ps.setString(2, previousPlan);
            ps.setString(3, "UPGRADE-" + System.currentTimeMillis());
            ps.setString(4, "Plan upgrade from " + firstPresent(previousPlan, "Unknown") +
                " to " + firstPresent(newPlan, "Unknown") + ". No payment collected in this screen.");
            if (processedBy > 0) ps.setInt(5, processedBy); else ps.setNull(5, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    private void updateMemberForUpgrade(Connection conn, MemberDAO.MemberRecord m) throws SQLException {
        String sql =
            "UPDATE members SET first_name=?, last_name=?, contact_number=?, email=?," +
            " address=?, date_of_birth=?, gender=?, emergency_contact=?, emergency_phone=?," +
            " membership_type=?, membership_start_date=?, membership_end_date=?, status=?," +
            " updated_at=NOW() WHERE member_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
            if (ps.executeUpdate() <= 0) {
                throw new SQLException("No member row updated for plan upgrade.");
            }
        }
    }

    private int createBillingForMember(Connection conn, int memberId, String membershipType,
                                       Date dueDate, int createdBy) throws SQLException {
        String normalized = mj23gym.dao.PlanDAO.normalizePlanName(membershipType);
        int planId = findPlanId(conn, normalized);
        double amount = planPriceForMembership(conn, normalized);
        String sql =
            "INSERT INTO billing (member_id, plan_id, billing_date, due_date, amount_due, amount_paid, payment_status, status, created_by)" +
            " VALUES (?,?,?,?,?,0,'Unpaid','Active',?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            if (planId > 0) ps.setInt(2, planId); else ps.setNull(2, Types.INTEGER);
            ps.setDate(3, new Date(System.currentTimeMillis()));
            ps.setDate(4, dueDate);
            ps.setDouble(5, amount);
            if (createdBy > 0) ps.setInt(6, createdBy); else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    private String currentMembershipType(Connection conn, int memberId) throws SQLException {
        String sql = "SELECT membership_type FROM members WHERE member_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("membership_type") : null;
            }
        }
    }

    private double planPriceForMembership(Connection conn, String membershipType) throws SQLException {
        String normalized = mj23gym.dao.PlanDAO.normalizePlanName(membershipType);
        String sql =
            "SELECT price FROM plans WHERE (plan_name=? OR duration=?) AND is_active=TRUE " +
            "ORDER BY price DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalized);
            ps.setString(2, normalized);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
            }
        }
        return fallbackPlanPrice(normalized);
    }

    private int findPlanId(Connection conn, String planName) throws SQLException {
        String sql = "SELECT plan_id FROM plans WHERE plan_name=? OR duration=? ORDER BY is_active DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, planName);
            ps.setString(2, planName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("plan_id") : 0;
            }
        }
    }

    private void logBillingUpgradeCancellation(Connection conn, int billingId, String oldValues, int processedBy)
            throws SQLException {
        String sql =
            "INSERT INTO audit_logs (user_id, entity_type, entity_id, action, old_values, new_values, status)" +
            " VALUES (?, 'billing', ?, 'CANCELLED_ON_UPGRADE', ?, ?, 'Success')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (processedBy > 0) ps.setInt(1, processedBy); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, billingId);
            ps.setString(3, oldValues);
            ps.setString(4, "{\"status\":\"Cancelled\",\"reason\":\"Closed on plan upgrade\"}");
            ps.executeUpdate();
        }
    }

    private String billingSnapshotJson(ResultSet rs, String reason) throws SQLException {
        return "{" +
            "\"billing_id\":" + rs.getInt("billing_id") + "," +
            "\"member_id\":" + rs.getInt("member_id") + "," +
            "\"plan_id\":" + nullableInt(rs, "plan_id") + "," +
            "\"billing_date\":\"" + jsonValue(rs.getDate("billing_date")) + "\"," +
            "\"due_date\":\"" + jsonValue(rs.getDate("due_date")) + "\"," +
            "\"amount_due\":" + rs.getDouble("amount_due") + "," +
            "\"amount_paid\":" + rs.getDouble("amount_paid") + "," +
            "\"payment_status\":\"" + jsonValue(rs.getString("payment_status")) + "\"," +
            "\"status\":\"" + jsonValue(rs.getString("status")) + "\"," +
            "\"created_by\":" + nullableInt(rs, "created_by") + "," +
            "\"created_at\":\"" + jsonValue(rs.getTimestamp("created_at")) + "\"," +
            "\"updated_at\":\"" + jsonValue(rs.getTimestamp("updated_at")) + "\"," +
            "\"reason\":\"" + jsonValue(reason) + "\"" +
            "}";
    }

    private String nullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? "null" : String.valueOf(value);
    }

    private String jsonValue(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString().replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void incrementPerSessionCreditIfNeeded(Connection conn, int memberId, String planSnapshot) throws SQLException {
        if (!"Per Session".equalsIgnoreCase(planSnapshot)) {
            return;
        }
        String sql =
            "UPDATE members SET sessions_paid=sessions_paid+1," +
            " sessions_remaining=sessions_remaining+1, updated_at=NOW() WHERE member_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.executeUpdate();
        }
    }

    private void ensureMemberSessionColumns(Connection conn) {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "members", "sessions_paid")) {
            if (!rs.next()) {
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("ALTER TABLE members ADD COLUMN sessions_paid INT NOT NULL DEFAULT 0");
                }
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] ensure sessions_paid error: " + e.getMessage());
        }
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "members", "sessions_remaining")) {
            if (!rs.next()) {
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("ALTER TABLE members ADD COLUMN sessions_remaining INT NOT NULL DEFAULT 0");
                }
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] ensure sessions_remaining error: " + e.getMessage());
        }
    }

    private void ensurePlanSnapshotColumn(Connection conn) {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "payment_records", "plan_type_snapshot")) {
            if (rs.next()) {
                return;
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] check plan_type_snapshot error: " + e.getMessage());
            return;
        }
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("ALTER TABLE payment_records ADD COLUMN plan_type_snapshot VARCHAR(50) NULL AFTER payment_type");
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] add plan_type_snapshot error: " + e.getMessage());
        }
    }

    private String firstPresent(String first, String fallback) {
        return first != null && !first.isBlank() ? first : fallback;
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
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensurePlanSnapshotColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PaymentRecord(
                        rs.getInt("payment_id"), rs.getInt("member_id"),
                        (Integer) rs.getObject("billing_id"),
                        rs.getString("payment_method"), rs.getString("payment_type"),
                        rs.getString("plan_type_snapshot"),
                        rs.getDate("payment_date"), rs.getDouble("amount"),
                        rs.getString("transaction_ref"), rs.getString("status"),
                        rs.getString("notes"), rs.getInt("processed_by"),
                        rs.getTimestamp("created_at"),
                        rs.getString("member_name"), rs.getString("member_code")));
                }
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
        String sql =
            "SELECT COUNT(*) FROM billing b JOIN members m ON b.member_id=m.member_id " +
            "WHERE b.payment_status = 'Unpaid' AND b.due_date >= CURDATE() " +
            "AND b.status <> 'Cancelled' AND m.status <> 'Cancelled'";
        return scalarInt(sql);
    }

    /** Billing overdue or unpaid past due date. */
    public int countOverdueAccounts() {
        return findOverdueInvoiceSummaries().size();
    }

    /** Completed membership (or any) payments recorded this calendar month. */
    public int countCompletedPaymentsThisMonth() {
        String sql =
            "SELECT COUNT(*) FROM payment_records pr JOIN members m ON pr.member_id=m.member_id " +
            "WHERE pr.status = 'Completed' AND pr.amount > 0 AND m.status <> 'Cancelled' " +
            "AND YEAR(pr.payment_date) = YEAR(CURDATE()) AND MONTH(pr.payment_date) = MONTH(CURDATE())";
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

    private List<BillingSummaryRow> billingSummaryQuery(String sql) {
        List<BillingSummaryRow> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new BillingSummaryRow(
                    rs.getInt("billing_id"),
                    rs.getInt("member_id"),
                    rs.getString("member_code"),
                    rs.getString("member_name"),
                    rs.getDate("due_date"),
                    rs.getDouble("amount_due"),
                    rs.getDouble("amount_paid"),
                    rs.getString("payment_status"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] billingSummaryQuery error: " + e.getMessage());
        }
        return rows;
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
