package mj23gym.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mj23gym.util.DatabaseConnection;

/**
 * Account activity summary for the logged-in user's Profile screen.
 */
public final class ProfileActivityDAO {

    public record ProfileActivityStats(
        int sessionsToday,
        int membersAddedToday,
        int paymentsProcessedToday,
        int reportsGeneratedToday
    ) {}

    public ProfileActivityStats todayForUser(int userId) {
        if (userId <= 0) {
            return new ProfileActivityStats(0, 0, 0, 0);
        }

        return new ProfileActivityStats(
            scalarInt(
                "SELECT COUNT(*) FROM audit_logs " +
                "WHERE user_id=? AND entity_type='activity_session' AND action='LOGIN' " +
                "AND DATE(created_at)=CURDATE()",
                userId
            ),
            scalarInt(
                "SELECT COUNT(*) FROM members WHERE created_by=? AND DATE(created_at)=CURDATE()",
                userId
            ),
            scalarInt(
                "SELECT " +
                "(SELECT COUNT(*) FROM payment_records WHERE processed_by=? AND payment_date=CURDATE()) + " +
                "(SELECT COUNT(*) FROM pos_transactions WHERE processed_by=? AND DATE(sale_date)=CURDATE())",
                userId,
                userId
            ),
            scalarInt(
                "SELECT COUNT(*) FROM reports WHERE generated_by=? AND DATE(generated_at)=CURDATE()",
                userId
            )
        );
    }

    private int scalarInt(String sql, int... params) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[ProfileActivityDAO] scalarInt error: " + e.getMessage());
            return 0;
        }
    }
}
