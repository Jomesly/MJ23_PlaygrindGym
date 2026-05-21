package mj23gym;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import mj23gym.util.DatabaseConnection;
import mj23gym.util.PasswordUtil;

public class DebugLogin {
    public static void main(String[] args) {
        System.out.println("=== DEBUG LOGIN CHECKER ===\n");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if admin user exists
            String sql = "SELECT user_id, username, password, full_name, role, status, is_active FROM users WHERE username='admin'";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                
                if (rs.next()) {
                    System.out.println("✓ Admin user found in database:");
                    System.out.println("  - user_id: " + rs.getInt("user_id"));
                    System.out.println("  - username: " + rs.getString("username"));
                    System.out.println("  - full_name: " + rs.getString("full_name"));
                    System.out.println("  - role: " + rs.getString("role"));
                    System.out.println("  - status: " + rs.getString("status"));
                    System.out.println("  - is_active: " + rs.getBoolean("is_active"));
                    
                    String storedPassword = rs.getString("password");
                    System.out.println("  - password hash: " + storedPassword);
                    System.out.println("\n  Testing password verification:");
                    
                    String testPassword = "admin123";
                    boolean isHashed = storedPassword.startsWith("$2");
                    System.out.println("  - Is hashed (starts with $2): " + isHashed);
                    
                    if (isHashed) {
                        boolean verified = PasswordUtil.verify(testPassword, storedPassword);
                        System.out.println("  - Bcrypt verify('admin123'): " + verified);
                    } else {
                        boolean match = storedPassword.equals(testPassword);
                        System.out.println("  - Plain text match('admin123'): " + match);
                    }
                    
                    // Check what the authentication query would find
                    System.out.println("\n✓ Checking authentication query conditions:");
                    String status = rs.getString("status");
                    boolean isActive = rs.getBoolean("is_active");
                    System.out.println("  - status='active': " + "active".equals(status));
                    System.out.println("  - is_active=true: " + isActive);
                    System.out.println("  - Both conditions met: " + ("active".equals(status) && isActive));
                    
                } else {
                    System.out.println("✗ Admin user NOT found in database!");
                    System.out.println("\nAll users in database:");
                    String allUsers = "SELECT user_id, username, full_name, status, is_active FROM users";
                    try (Statement st2 = conn.createStatement();
                         ResultSet rs2 = st2.executeQuery(allUsers)) {
                        while (rs2.next()) {
                            System.out.println("  - " + rs2.getString("username") + 
                                             " (status=" + rs2.getString("status") + 
                                             ", is_active=" + rs2.getBoolean("is_active") + ")");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
