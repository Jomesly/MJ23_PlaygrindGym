package mj23gym.dao;

import mj23gym.util.DatabaseConnection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * System maintenance support: backups, restores, and feature/tool registry.
 */
public final class MaintenanceDAO {
    private static final String[] BACKUP_TABLES = {
        "users", "plans", "members", "attendance", "inventory", "equipment", "maintenance_logs",
        "billing", "payment_records", "pos_transactions", "pos_transaction_items", "reports",
        "system_tools"
    };

    public record BackupRecord(
        int backupId,
        String backupName,
        String filePath,
        String status,
        int generatedBy,
        Timestamp createdAt
    ) {}

    public record ToolRecord(
        int toolId,
        String toolName,
        String moduleName,
        String description,
        String status,
        String version,
        int updatedBy,
        Timestamp createdAt,
        Timestamp updatedAt
    ) {}

    public void ensureMaintenanceTables() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS system_backups (" +
                "backup_id INT AUTO_INCREMENT PRIMARY KEY," +
                "backup_name VARCHAR(150) NOT NULL," +
                "file_path VARCHAR(255) NOT NULL," +
                "status ENUM('Created','Restored','Failed') DEFAULT 'Created'," +
                "notes TEXT," +
                "generated_by INT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (generated_by) REFERENCES users(user_id) ON DELETE SET NULL," +
                "INDEX idx_created_at (created_at)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS system_tools (" +
                "tool_id INT AUTO_INCREMENT PRIMARY KEY," +
                "tool_name VARCHAR(120) NOT NULL," +
                "module_name VARCHAR(120) NOT NULL," +
                "description TEXT," +
                "status ENUM('Active','Archived') DEFAULT 'Active'," +
                "version VARCHAR(30) DEFAULT '1.0'," +
                "updated_by INT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL," +
                "INDEX idx_tool_status (status)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
            seedDefaultTools(conn);
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] ensureMaintenanceTables error: " + e.getMessage());
        }
    }

    public BackupRecord createBackup(Path workspaceRoot, int generatedBy) {
        ensureMaintenanceTables();
        Path backupDir = workspaceRoot.resolve("backups");
        String stamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        String name = "MJ23_Backup_" + stamp;
        Path backupFile = backupDir.resolve(name + ".sql");

        try {
            Files.createDirectories(backupDir);
            try (Connection conn = DatabaseConnection.getConnection();
                 BufferedWriter writer = Files.newBufferedWriter(backupFile, StandardCharsets.UTF_8)) {
                writer.write("-- MJ23 Playgrind Gym database backup\n");
                writer.write("-- Generated at " + LocalDateTime.now() + "\n");
                writer.write("SET FOREIGN_KEY_CHECKS=0;\n\n");
                for (String table : BACKUP_TABLES) {
                    if (tableExists(conn, table)) {
                        exportTable(conn, table, writer);
                    }
                }
                writer.write("SET FOREIGN_KEY_CHECKS=1;\n");
            }
            int id = insertBackupRecord(name, backupFile.toString(), "Created", "Backup created successfully.", generatedBy);
            return new BackupRecord(id, name, backupFile.toString(), "Created", generatedBy, null);
        } catch (SQLException | IOException e) {
            System.err.println("[MaintenanceDAO] createBackup error: " + e.getMessage());
            int id = insertBackupRecord(name, backupFile.toString(), "Failed", e.getMessage(), generatedBy);
            return new BackupRecord(id, name, backupFile.toString(), "Failed", generatedBy, null);
        }
    }

    public boolean restoreBackup(Path backupFile, int restoredBy) {
        ensureMaintenanceTables();
        if (backupFile == null || !Files.exists(backupFile)) {
            return false;
        }
        try {
            String sql = Files.readString(backupFile, StandardCharsets.UTF_8);
            List<String> statements = splitStatements(sql);
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement st = conn.createStatement()) {
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        st.execute(trimmed);
                    }
                }
            }
            insertBackupRecord("Restored " + backupFile.getFileName(), backupFile.toString(), "Restored", "Backup restored.", restoredBy);
            return true;
        } catch (SQLException | IOException e) {
            System.err.println("[MaintenanceDAO] restoreBackup error: " + e.getMessage());
            insertBackupRecord("Failed restore " + backupFile.getFileName(), backupFile.toString(), "Failed", e.getMessage(), restoredBy);
            return false;
        }
    }

    public List<BackupRecord> findRecentBackups() {
        ensureMaintenanceTables();
        List<BackupRecord> rows = new ArrayList<>();
        String sql = "SELECT * FROM system_backups ORDER BY created_at DESC, backup_id DESC LIMIT 20";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new BackupRecord(
                    rs.getInt("backup_id"),
                    rs.getString("backup_name"),
                    rs.getString("file_path"),
                    rs.getString("status"),
                    rs.getInt("generated_by"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] findRecentBackups error: " + e.getMessage());
        }
        return rows;
    }

    public List<ToolRecord> findTools(boolean includeArchived) {
        ensureMaintenanceTables();
        List<ToolRecord> rows = new ArrayList<>();
        String sql = "SELECT * FROM system_tools " +
            (includeArchived ? "" : "WHERE status='Active' ") +
            "ORDER BY status, module_name, tool_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(mapTool(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] findTools error: " + e.getMessage());
        }
        return rows;
    }

    public boolean addTool(String toolName, String moduleName, String description, String version, int updatedBy) {
        ensureMaintenanceTables();
        String sql = "INSERT INTO system_tools (tool_name, module_name, description, version, updated_by) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toolName);
            ps.setString(2, moduleName);
            ps.setString(3, description);
            ps.setString(4, version == null || version.isBlank() ? "1.0" : version);
            setUser(ps, 5, updatedBy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] addTool error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTool(ToolRecord tool, int updatedBy) {
        ensureMaintenanceTables();
        String sql =
            "UPDATE system_tools SET tool_name=?, module_name=?, description=?, version=?, updated_by=?, updated_at=NOW() " +
            "WHERE tool_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tool.toolName());
            ps.setString(2, tool.moduleName());
            ps.setString(3, tool.description());
            ps.setString(4, tool.version());
            setUser(ps, 5, updatedBy);
            ps.setInt(6, tool.toolId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] updateTool error: " + e.getMessage());
            return false;
        }
    }

    public boolean setToolArchived(int toolId, boolean archived, int updatedBy) {
        ensureMaintenanceTables();
        String sql = "UPDATE system_tools SET status=?, updated_by=?, updated_at=NOW() WHERE tool_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, archived ? "Archived" : "Active");
            setUser(ps, 2, updatedBy);
            ps.setInt(3, toolId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] setToolArchived error: " + e.getMessage());
            return false;
        }
    }

    private void exportTable(Connection conn, String table, BufferedWriter writer) throws SQLException, IOException {
        writer.write("-- Table: " + table + "\n");
        writer.write("DELETE FROM `" + table + "`;\n");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM `" + table + "`")) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            while (rs.next()) {
                writer.write("INSERT INTO `" + table + "` (");
                for (int i = 1; i <= cols; i++) {
                    if (i > 1) writer.write(",");
                    writer.write("`" + md.getColumnName(i) + "`");
                }
                writer.write(") VALUES (");
                for (int i = 1; i <= cols; i++) {
                    if (i > 1) writer.write(",");
                    writer.write(sqlLiteral(rs.getObject(i)));
                }
                writer.write(");\n");
            }
        }
        writer.write("\n");
    }

    private String sqlLiteral(Object value) {
        if (value == null) return "NULL";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean b) return b ? "TRUE" : "FALSE";
        return "'" + value.toString().replace("\\", "\\\\").replace("'", "''") + "'";
    }

    private List<String> splitStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuote = false;
        String withoutComments = stripLineComments(sql);
        for (int i = 0; i < withoutComments.length(); i++) {
            char c = withoutComments.charAt(i);
            if (c == '\'' && (i == 0 || withoutComments.charAt(i - 1) != '\\')) {
                inQuote = !inQuote;
            }
            if (c == ';' && !inQuote) {
                statements.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (!current.toString().trim().isEmpty()) {
            statements.add(current.toString());
        }
        return statements;
    }

    private String stripLineComments(String sql) {
        StringBuilder cleaned = new StringBuilder();
        for (String line : sql.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--")) {
                cleaned.append(line).append('\n');
            }
        }
        return cleaned.toString();
    }

    private boolean tableExists(Connection conn, String table) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, table, new String[] {"TABLE"})) {
            return rs.next();
        }
    }

    private int insertBackupRecord(String name, String path, String status, String notes, int userId) {
        String sql = "INSERT INTO system_backups (backup_name, file_path, status, notes, generated_by) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, path);
            ps.setString(3, status);
            ps.setString(4, notes);
            setUser(ps, 5, userId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    private void seedDefaultTools(Connection conn) throws SQLException {
        String[][] tools = {
            {"Backup", "Maintenance", "Create secured database backup copies.", "1.0"},
            {"Restore", "Maintenance", "Recover data from saved backup files.", "1.0"},
            {"Member Archive", "Member Management", "Archive inactive members without deleting records.", "1.0"},
            {"Inventory Monitor", "Inventory Management", "Highlight low-stock items for restocking.", "1.0"},
            {"POS Checkout", "Point of Sale", "Process item purchases separately from membership billing.", "1.0"},
            {"Report Generator", "Report", "Generate and view stored system reports.", "1.0"}
        };
        String existsSql = "SELECT COUNT(*) FROM system_tools WHERE tool_name=? AND module_name=?";
        String insertSql = "INSERT INTO system_tools (tool_name, module_name, description, version) VALUES (?,?,?,?)";
        try (PreparedStatement exists = conn.prepareStatement(existsSql);
             PreparedStatement insert = conn.prepareStatement(insertSql)) {
            for (String[] tool : tools) {
                exists.setString(1, tool[0]);
                exists.setString(2, tool[1]);
                try (ResultSet rs = exists.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        continue;
                    }
                }
                insert.setString(1, tool[0]);
                insert.setString(2, tool[1]);
                insert.setString(3, tool[2]);
                insert.setString(4, tool[3]);
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private ToolRecord mapTool(ResultSet rs) throws SQLException {
        return new ToolRecord(
            rs.getInt("tool_id"),
            rs.getString("tool_name"),
            rs.getString("module_name"),
            rs.getString("description"),
            rs.getString("status"),
            rs.getString("version"),
            rs.getInt("updated_by"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }

    private void setUser(PreparedStatement ps, int index, int userId) throws SQLException {
        if (userId > 0) {
            ps.setInt(index, userId);
        } else {
            ps.setNull(index, Types.INTEGER);
        }
    }
}
