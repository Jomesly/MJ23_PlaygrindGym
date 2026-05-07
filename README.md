# MJ23 Playgrind Gym Management System

## Overview
A JavaFX-based gym management system with integrated screens for:
- Dashboard
- Member Management
- Payment & Billing
- Inventory Management
- Equipment Management
- Point of Sale (POS)
- Reports
- Settings

## What Was Fixed
1. ✅ Fixed syntax errors: Removed duplicate class declarations
2. ✅ Fixed malformed class endings: Removed extra closing braces and misplaced imports
3. ✅ Created main application launcher (GymManagementApp.java)
4. ✅ Integrated all screens to work together
5. ✅ All files now use consistent package structure: `mj23gym.ui`

## Compilation & Execution

### Requirements
- Java 11 or higher
- JavaFX SDK 11 or higher
- IDE (VS Code, IntelliJ IDEA, Eclipse) OR command line

### Option 1: Using Command Line (Windows)

1. **Download JavaFX SDK** from https://gluonhq.com/products/javafx/

2. **Set up environment variable:**
   ```
   set JAVAFX_HOME=C:\path\to\javafx-sdk-XX
   ```

3. **Compile all files:**
   ```
   javac --module-path %JAVAFX_HOME%\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics *.java
   ```

4. **Run the application:**
   ```
   java --module-path %JAVAFX_HOME%\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics GymManagementApp
   ```

### Option 2: Using VS Code with Maven/Gradle

Create a `pom.xml` file in the project directory:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>mj23</groupId>
    <artifactId>gym-management</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>MJ23 Playgrind Gym</name>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <javafx.version>21</javafx.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>mj23gym.ui.GymManagementApp</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Then run:
```
mvn clean javafx:run
```

### Option 3: Using IntelliJ IDEA

1. Create a new JavaFX project
2. Copy all `.java` files to `src/`
3. Go to File → Project Structure → Libraries
4. Add JavaFX SDK as a library
5. Edit Run Configuration → VM Options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```
6. Run GymManagementApp.java

## File Structure
```
MJ23_Playgrind_Gym/
├── GymManagementApp.java         (Main launcher - integrates all screens)
├── AddEquipmentScreen.java        (Equipment management screen)
├── DashboardScreen.java           (Dashboard screen)
├── InventoryScreen.java           (Inventory management screen)
├── LoginScreen.java               (Login screen)
├── MemberManagementScreen.java    (Member management screen)
├── PaymentScreen.java             (Payment & billing screen)
├── POSScreen.java                 (Point of sale screen)
├── ReportsScreen.java             (Reports screen)
├── SettingsScreen.java            (Settings screen)
└── README.md                      (This file)
```

## Key Features

### Color Scheme
- Main Background: #1a1a2e (Deep Navy)
- Sidebar: #0d1b2a (Darker Navy)
- Cards: #1e2a3a (Card Surface)
- Accent: #e63946 (Red - Buttons, Highlights)
- Text: #ffffff (White), #b0bec5 (Muted)

### Navigation
All screens are accessible from the left sidebar. Each screen has:
- Consistent styling and theme
- Main menu navigation
- System menu (Settings, Help, About)

### Integration
- All screens are now part of a unified application
- **GymManagementApp.java** acts as the main controller
- Screens share the same color palette
- Navigation between screens is seamless

## Usage

1. **Launch the application** using one of the methods above
2. **Navigate** using the left sidebar menu
3. **Switch between screens** by clicking menu items
4. Each screen loads dynamically without closing the application

## Notes for Developers

- Package: `mj23gym.ui`
- Main class: `GymManagementApp`
- JavaFX framework is required for all GUI components
- All screens extend `Application` but GymManagementApp handles the UI switching

## Future Enhancements

- Add database connectivity (MySQL/SQLite)
- Implement actual data persistence
- Add user authentication (LoginScreen)
- Integrate business logic for calculations
- Add data export/import functionality
- Implement real-time notifications
- Add user session management

## License
MJ23 Playgrind Gym Management System
