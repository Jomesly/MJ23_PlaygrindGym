package mj23gym;

import java.sql.Connection;

import mj23gym.util.DatabaseConnection;

public class TestDB {

    public static void main(String[] args) {

        System.out.println(
            "Testing database connection..."
        );

        try (Connection conn =
                     DatabaseConnection.getConnection()) {

            if (conn != null && !conn.isClosed()) {

                System.out.println(
                    "SUCCESS: Database Connected!"
                );

            }

        } catch (Exception e) {

            System.out.println(
                "FAILED: " + e.getMessage()
            );

        }

        System.exit(0);
    }
}