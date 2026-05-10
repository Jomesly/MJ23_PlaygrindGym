# MJ23 Playgrind Gym Management System
## Complete Setup Guide (Java 17 + JavaFX + MySQL 8)

## System Requirements

- **Java**: OpenJDK 17 LTS or later
- **JavaFX**: 21.0.3 (compatible with Java 17+)
- **MySQL**: 8.0+ (Community Edition or Enterprise)
- **Maven**: 3.6.0 or later
- **RAM**: Minimum 4GB
- **Storage**: Minimum 1GB free space

---

## Step 1: Install Java 17 LTS

### Windows
1. Download OpenJDK 17 LTS from [Eclipse Temurin](https://adoptium.net/)
2. Run the installer and follow the prompts
3. Set `JAVA_HOME` environment variable:
   ```
   JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17.x.x
   ```
4. Add to `PATH`:
   ```
   %JAVA_HOME%\bin
   ```
5. Verify installation:
   ```
   java -version
   javac -version
   ```

### macOS
```bash
brew install openjdk@17
```

### Linux (Ubuntu/Debian)
```bash
sudo apt-get install openjdk-17-jdk
java -version
```

---

## Step 2: Install Maven

### Windows
1. Download Apache Maven from [maven.apache.org](https://maven.apache.org/)
2. Extract to a directory (e.g., `C:\apache-maven-3.9.x`)
3. Set `MAVEN_HOME` environment variable:
   ```
   MAVEN_HOME = C:\apache-maven-3.9.x
   ```
4. Add to `PATH`:
   ```
   %MAVEN_HOME%\bin
   ```
5. Verify installation:
   ```
   mvn -version
   ```

### macOS
```bash
brew install maven
mvn -version
```

### Linux (Ubuntu/Debian)
```bash
sudo apt-get install maven
mvn -version
```

---

## Step 3: Install MySQL 8.0

### Windows
1. Download MySQL Community Server from [mysql.com](https://dev.mysql.com/downloads/mysql/)
2. Run the installer (MSI Installer recommended)
3. Choose setup type: Developer Default
4. Configure MySQL Server:
   - Port: **3306** (default)
   - MySQL X Protocol Port: **33060**
   - Config Type: Development Machine
5. Set password for root user
6. Create Windows Service
7. Complete installation

### macOS
```bash
brew install mysql
brew services start mysql
mysql -u root
```

### Linux (Ubuntu/Debian)
```bash
sudo apt-get install mysql-server
sudo mysql_secure_installation
sudo systemctl start mysql
```

### Verify MySQL Installation
```bash
mysql -u root -p
mysql> SELECT VERSION();
```

---

## Step 4: Create Database and Tables

### Option A: Using MySQL Command Line

1. Connect to MySQL:
   ```bash
   mysql -u root -p
   ```

2. Copy and paste the contents of `database_schema.sql`:
   ```sql
   -- Copy entire contents from database_schema.sql and execute
   ```

3. Verify database creation:
   ```sql
   SHOW DATABASES;
   USE mj23gym;
   SHOW TABLES;
   ```

### Option B: Using MySQL Workbench

1. Download [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)
2. Open MySQL Workbench
3. Create new connection (localhost:3306)
4. Open the SQL Editor
5. Open and execute `database_schema.sql`

### Option C: Using Command Line Script

```bash
mysql -u root -p < database_schema.sql
```

---

## Step 5: Configure Database Connection

### Update Database Credentials

Edit `src/main/java/mj23gym/util/DatabaseConnection.java`:

```java
// Line 16-20
private static final String DB_HOST = "localhost";      // Your MySQL host
private static final int DB_PORT = 3306;               // MySQL port
private static final String DB_NAME = "mj23gym";       // Database name
private static final String DB_USER = "root";          // MySQL username
private static final String DB_PASSWORD = "";          // MySQL password
```

**Example for different scenarios:**

**Local Development:**
```java
private static final String DB_HOST = "localhost";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

**Remote Server:**
```java
private static final String DB_HOST = "192.168.1.100";  // Server IP
private static final String DB_USER = "gymuser";
private static final String DB_PASSWORD = "secure_password";
```

---

## Step 6: Build the Project

### Navigate to Project Directory
```bash
cd "c:\Users\james\school works\MJ23_Playgrind_Gym"
```

### Clean and Build
```bash
mvn clean compile
```

### Build with Dependencies
```bash
mvn clean install -DskipTests
```

### Verify Compilation
```bash
mvn test-compile
```

---

## Step 7: Run the Application

### Using Maven (Recommended)
```bash
mvn javafx:run
```

### Using JAR File
```bash
mvn clean package
java -jar target/gym-management-system-1.0.0.jar
```

### Using Java Directly
```bash
mvn compile exec:java -Dexec.mainClass="mj23gym.ui.LoginScreen"
```

---

## Step 8: Test Database Connection

When you first run the application, you should see:
```
[DB] Connection pool initialized successfully!
```

If you see an error:
```
[DB ERROR] Failed to initialize database connection pool
```

**Troubleshooting:**
1. Verify MySQL is running: `mysql -u root -p -e "SELECT VERSION();"`
2. Check database exists: `mysql -u root -p -e "SHOW DATABASES;"`
3. Verify credentials in DatabaseConnection.java
4. Check firewall allows port 3306
5. Review console for detailed error message

---

## Troubleshooting Common Issues

### Issue: "Cannot load driver class com.mysql.cj.jdbc.Driver"
**Solution:** 
- Maven dependencies not downloaded
- Run: `mvn clean install`
- Check internet connection

### Issue: "Access denied for user 'root'@'localhost'"
**Solution:**
- Wrong password in DatabaseConnection.java
- Reset MySQL root password:
  ```bash
  mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';"
  ```

### Issue: "Unknown database 'mj23gym'"
**Solution:**
- Database not created yet
- Execute database_schema.sql:
  ```bash
  mysql -u root -p < database_schema.sql
  ```

### Issue: "JavaFX not found" or "No module named javafx"
**Solution:**
- Run Maven install: `mvn clean install`
- Clear .m2 cache: `rmdir /s %USERPROFILE%\.m2\repository`

### Issue: Java version mismatch
**Solution:**
- Verify Java version: `java -version`
- Should show version 17 or higher
- Update JAVA_HOME if needed

---

## Project Structure

```
MJ23_Playgrind_Gym/
├── pom.xml                           # Maven configuration (Java 17, JavaFX 21, MySQL 8)
├── database_schema.sql               # Database schema and sample data
├── README.md                         # Setup instructions
├── src/
│   └── main/
│       └── java/
│           └── mj23gym/
│               ├── ui/               # UI Screens (JavaFX)
│               │   ├── GymManagementApp.java        # Main application launcher
│               │   ├── DashboardScreen.java
│               │   ├── MemberManagementScreen.java
│               │   ├── PaymentScreen.java
│               │   ├── InventoryScreen.java
│               │   ├── AddEquipmentScreen.java
│               │   ├── POSScreen.java
│               │   ├── ReportsScreen.java
│               │   ├── SettingsScreen.java
│               │   └── LoginScreen.java
│               └── util/             # Utilities
│                   └── DatabaseConnection.java      # MySQL connection pooling
├── target/                          # Compiled classes (auto-generated)
└── pom.xml                          # Maven configuration

```

---

## Dependencies Overview

| Dependency | Version | Purpose |
|-----------|---------|---------|
| JavaFX | 21.0.3 | User Interface Framework |
| MySQL Connector | 8.0.33 | Database Driver |
| HikariCP | 5.1.0 | Connection Pooling |
| SLF4J | 2.0.9 | Logging Framework |

---

## Database Credentials (Default)

```
Host: localhost
Port: 3306
Database: mj23gym
Username: root
Password: [your MySQL root password]
```

**Change these in production!**

---

## Starting MySQL Service

### Windows
```bash
net start MySQL80
```

### macOS
```bash
brew services start mysql
```

### Linux
```bash
sudo systemctl start mysql
```

---

## Checking MySQL Status

```bash
mysql -u root -p -e "SHOW STATUS;"
```

---

## Performance Tuning (Optional)

### Connection Pool Settings
Edit `DatabaseConnection.java`:
```java
config.setMaximumPoolSize(10);        // Max connections
config.setMinimumIdle(2);             // Min idle connections
config.setConnectionTimeout(30000);   // 30 seconds
```

### MySQL my.cnf Optimization
```ini
[mysqld]
max_connections = 200
max_allowed_packet = 64M
innodb_buffer_pool_size = 1G
```

---

## Backup Database

### Full Backup
```bash
mysqldump -u root -p mj23gym > mj23gym_backup.sql
```

### Restore from Backup
```bash
mysql -u root -p mj23gym < mj23gym_backup.sql
```

---

## Next Steps

1. Update database credentials in `DatabaseConnection.java`
2. Run `mvn clean install` to download all dependencies
3. Execute `database_schema.sql` to create tables
4. Run `mvn javafx:run` to start the application
5. Login with admin credentials:
   - Username: `admin`
   - Password: `admin123`

---

## Support & Resources

- **JavaFX Documentation**: https://openjfx.io/
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **Maven Documentation**: https://maven.apache.org/
- **HikariCP Documentation**: https://brettwooldridge.github.io/HikariCP/

---

**Last Updated**: May 7, 2026
**Compatibility**: Java 17+ | JavaFX 21+ | MySQL 8.0+
