# MJ23 Playgrind Gym - Quick Start Checklist

## Pre-Requisites Verification

### Java 17 LTS
- [ ] Java 17 installed
  ```bash
  java -version
  ```
  Should output: `openjdk version "17.x.x"`

### Maven 3.6+
- [ ] Maven installed
  ```bash
  mvn -version
  ```
  Should output: `Apache Maven 3.x.x`

### MySQL 8.0
- [ ] MySQL Server running
  ```bash
  mysql -u root -p -e "SELECT VERSION();"
  ```
  Should output: `8.0.xx`

---

## Configuration Steps

### Step 1: Database Setup
- [ ] MySQL 8.0 is installed and running
- [ ] Port 3306 is accessible
- [ ] Execute `database_schema.sql`:
  ```bash
  mysql -u root -p < database_schema.sql
  ```
- [ ] Verify database created:
  ```bash
  mysql -u root -p -e "USE mj23gym; SHOW TABLES;"
  ```

### Step 2: Database Credentials Configuration
File: `src/main/java/mj23gym/util/DatabaseConnection.java` (Lines 16-20)

Update the following values:
```java
private static final String DB_HOST = "localhost";        // ← Change if remote
private static final int DB_PORT = 3306;                  // ← Change if custom port
private static final String DB_NAME = "mj23gym";          // ← Database name
private static final String DB_USER = "root";             // ← MySQL username
private static final String DB_PASSWORD = "";             // ← MySQL password
```

**Common Scenarios:**

**Local Development (Default):**
```java
private static final String DB_HOST = "localhost";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";  // Change to your MySQL root password
```

**Remote Server:**
```java
private static final String DB_HOST = "192.168.1.100";
private static final String DB_USER = "gymuser";
private static final String DB_PASSWORD = "your_secure_password";
```

**Docker Container:**
```java
private static final String DB_HOST = "mysql-container";
private static final String DB_PORT = 3306;
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "root_password";
```

### Step 3: Project Build
- [ ] Navigate to project directory:
  ```bash
  cd "c:\Users\james\school works\MJ23_Playgrind_Gym"
  ```

- [ ] Clean and compile:
  ```bash
  mvn clean compile
  ```
  Should show: `BUILD SUCCESS`

- [ ] Build complete project:
  ```bash
  mvn clean install -DskipTests
  ```
  Should show: `BUILD SUCCESS`

- [ ] Verify dependencies downloaded:
  - Check: `~/.m2/repository/org/openjfx/` contains JavaFX files
  - Check: `~/.m2/repository/mysql/` contains MySQL connector
  - Check: `~/.m2/repository/com/zaxxer/` contains HikariCP

---

## Running the Application

### Method 1: Using Maven (Recommended)
```bash
mvn javafx:run
```
Expected output:
```
[APP] Initializing database connection...
[DB] Connection pool initialized successfully!
[APP] Database connection verified successfully!
```

### Method 2: Using Java Directly
```bash
mvn compile exec:java -Dexec.mainClass="mj23gym.ui.GymManagementApp"
```

### Method 3: Create and Run JAR
```bash
# Build JAR file
mvn clean package

# Run the JAR
java -jar target/gym-management-system-1.0.0.jar
```

---

## Troubleshooting Checklist

### Database Connection Issues

**Problem: "Cannot get JDBC Connection"**
- [ ] MySQL service is running (`mysql -u root -p -e "SELECT 1;"`)
- [ ] Port 3306 is accessible (no firewall blocking)
- [ ] Database credentials are correct
- [ ] `mj23gym` database exists
- [ ] User has necessary privileges

**Check credentials:**
```bash
mysql -u root -p -e "USE mj23gym; SHOW TABLES;"
```

**Problem: "Unknown database 'mj23gym'"**
- [ ] Execute database_schema.sql
- [ ] Verify execution completed without errors

**Problem: "Access denied for user 'root'@'localhost'"**
- [ ] Check password in DatabaseConnection.java matches MySQL password
- [ ] Reset MySQL password if needed:
  ```bash
  mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';"
  ```

### Compilation Issues

**Problem: "Cannot find symbol: class Application"**
- [ ] Run `mvn clean install` to download JavaFX dependencies
- [ ] Check internet connection is active

**Problem: "Package org.openjfx not found"**
- [ ] Clear Maven cache: `rmdir /s %USERPROFILE%\.m2\repository\org\openjfx`
- [ ] Run: `mvn clean install`

**Problem: "Java version mismatch"**
- [ ] Verify Java version: `java -version` (should show 17.x)
- [ ] Update pom.xml `<source>` and `<target>` to match installed version

### Runtime Issues

**Problem: GUI doesn't appear / closes immediately**
- [ ] Check console for error messages
- [ ] Verify all JavaFX modules loaded correctly
- [ ] Check system supports OpenGL (for JavaFX rendering)

**Problem: Application runs but database features don't work**
- [ ] Check database initialization message in console
- [ ] Verify database_schema.sql executed successfully
- [ ] Check tables exist: `mysql -u root -p -e "USE mj23gym; SHOW TABLES;"`

---

## Technology Stack Verification

### Java 17 LTS
```bash
java -version
# Expected: openjdk version "17.x.x" ... LTS
```

### JavaFX 21.0.3
Check in pom.xml:
```xml
<javafx.version>21.0.3</javafx.version>
```
This version is compatible with Java 17+

### MySQL 8.0
```bash
mysql -V
# Expected: mysql  Ver 8.0.xx for ... on ...
```

### HikariCP (Connection Pooling)
Version: 5.1.0
- Provides connection pool management
- Automatically downloads with `mvn install`

---

## Project Structure Verification

```
MJ23_Playgrind_Gym/
├── pom.xml                          ✓ Maven config (Java 17, JavaFX 21, MySQL 8)
├── database_schema.sql              ✓ MySQL database creation script
├── SETUP_GUIDE.md                   ✓ Detailed setup instructions
├── QUICK_START.md                   ✓ This file
├── README.md                        ✓ Project overview
├── src/
│   └── main/
│       └── java/
│           └── mj23gym/
│               ├── ui/              ✓ UI Screens
│               │   ├── GymManagementApp.java
│               │   ├── DashboardScreen.java
│               │   ├── LoginScreen.java
│               │   ├── MemberManagementScreen.java
│               │   ├── PaymentScreen.java
│               │   ├── InventoryScreen.java
│               │   ├── AddEquipmentScreen.java
│               │   ├── POSScreen.java
│               │   ├── ReportsScreen.java
│               │   └── SettingsScreen.java
│               └── util/            ✓ Utilities
│                   └── DatabaseConnection.java
└── target/                          (Auto-generated during build)
```

---

## Performance Baseline

### Expected Startup Time
- First run (with Maven dependency download): 2-3 minutes
- Subsequent runs: 15-30 seconds
- Database connection initialization: <2 seconds

### System Resource Usage
- RAM: 200-300 MB
- CPU: < 15% during idle
- Disk: ~800 MB (with all dependencies)

---

## Default Credentials

### Administrator Login
```
Username: admin
Password: admin123
```

⚠️ **IMPORTANT**: Change these credentials immediately in production!

### Database Connection
```
Host: localhost
Port: 3306
Database: mj23gym
User: root
Password: [your MySQL root password]
```

---

## Next Actions After Successful Startup

1. **Login**: Use admin credentials
2. **Change admin password**: Go to Settings
3. **Create database backup**: `mysqldump -u root -p mj23gym > backup.sql`
4. **Customize database credentials**: Update DatabaseConnection.java
5. **Set up regular backups**: Schedule nightly database backups

---

## Support Resources

- **JavaFX**: https://openjfx.io/
- **MySQL**: https://dev.mysql.com/
- **Maven**: https://maven.apache.org/
- **HikariCP**: https://brettwooldridge.github.io/HikariCP/

---

## Success Indicators

When the application starts successfully, you should see:
```
[APP] Initializing database connection...
[INFO] Downloading from central: ...
[DB] Connection pool initialized successfully!
[APP] Database connection verified successfully!
```

And the GUI window opens with the main menu and all screens accessible from the sidebar.

---

**Compatibility**: Java 17+ | JavaFX 21+ | MySQL 8.0+
**Last Updated**: May 7, 2026
