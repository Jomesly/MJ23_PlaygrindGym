package mj23gym.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mj23gym.dao.PlanDAO;
import mj23gym.util.DatabaseConnection;

/**
 * MJ23 Playgrind Gym  Main Application Launcher
 * Central application that integrates all screens
 * 
 * Technology Stack:
 * - Java 17 LTS
 * - JavaFX 21.0.3
 * - MySQL 8.0
 * - HikariCP for connection pooling
 */
public class GymManagementApp extends Application {

    // Use modern design system colors
    private static final String BG_MAIN      = ModernDesignSystem.BG_LIGHT;
    private static final String BG_SIDEBAR   = ModernDesignSystem.SIDEBAR_BG;
    private static final String BG_CARD      = ModernDesignSystem.CARD_BG;
    private static final String ACCENT       = ModernDesignSystem.PRIMARY;
    private static final String TEXT_WHITE   = ModernDesignSystem.PRIMARY;
    private static final String TEXT_MUTED   = ModernDesignSystem.TEXT_MUTED;
    private static final String LOGO_PATH    = "/images/mj23-logo.png";

    private BorderPane rootPane;
    private StackPane contentArea;
    private String currentScreen = "dashboard";
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize database connection
        System.out.println("[APP] Initializing database connection...");
        DatabaseConnection.initialize();
        
        // Check database connectivity
        if (DatabaseConnection.isConnected()) {
            System.out.println("[APP] Database connection verified successfully!");
            new PlanDAO().ensurePlanSetup();
        } else {
            System.err.println("[APP WARNING] Could not verify database connection. Some features may not work.");
        }
        
        primaryStage.setTitle("MJ23 Playgrind Gym  Management System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(720);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.setResizable(true);

        // Create root layout
        rootPane = new BorderPane();
        rootPane.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Create sidebar with navigation
        VBox sidebar = createSidebar();
        rootPane.setLeft(sidebar);

        // Create content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: " + BG_MAIN + ";");
        rootPane.setCenter(contentArea);

        // Load default screen
        loadScreen("dashboard");

        Scene scene = new Scene(rootPane, 1200, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Handle application shutdown
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("[APP] Shutting down application...");
            DatabaseConnection.shutdown();
            System.out.println("[APP] Application closed successfully!");
            System.exit(0);
        });
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setStyle(
            "-fx-background-color: " + BG_SIDEBAR + ";" +
            "-fx-padding: 0;"
        );
        sidebar.setSpacing(0);

        // Top accent bar with modern styling
        Rectangle topAccent = new Rectangle(230, 6);
        topAccent.setFill(Color.web(ModernDesignSystem.ACCENT_YELLOW));

        // Logo section
        VBox logo = createLogoSection();

        // Navigation menu
        VBox navMenu = createNavigationMenu();

        // System menu
        VBox sysMenu = createSystemMenu();

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(topAccent, logo, new Separator(), navMenu,
                                     new Separator(), sysMenu, spacer, createUserFooter());
        return sidebar;
    }

    private VBox createLogoSection() {
        VBox logo = new VBox(8);
        logo.setPadding(new Insets(16, 16, 14, 16));
        logo.setAlignment(Pos.CENTER);
        logo.setStyle("-fx-background-color: transparent;");

        StackPane logoBadge = new StackPane();
        logoBadge.setPrefSize(74, 74);
        logoBadge.setMaxSize(74, 74);
        Rectangle badgeBg = new Rectangle(74, 74);
        badgeBg.setArcWidth(ModernDesignSystem.RADIUS_LARGE);
        badgeBg.setArcHeight(ModernDesignSystem.RADIUS_LARGE);
        badgeBg.setFill(Color.web(ModernDesignSystem.WHITE));
        badgeBg.setStroke(Color.web(ModernDesignSystem.BORDER_COLOR));
        badgeBg.setEffect(ModernDesignSystem.createElevation2());

        ImageView logoImage = new ImageView(loadImage(LOGO_PATH));
        logoImage.setPreserveRatio(true);
        logoImage.setFitWidth(62);
        logoImage.setFitHeight(62);
        logoBadge.getChildren().addAll(badgeBg, logoImage);

        Text title = new Text("MJ23 PLAYGRIND\nGYM");
        title.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 11));
        title.setFill(Color.web(ModernDesignSystem.PRIMARY));
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        title.setStroke(Color.web(ModernDesignSystem.WHITE, 0.75));
        title.setStrokeWidth(0.22);

        logo.getChildren().addAll(logoBadge, title);
        return logo;
    }

    private VBox createNavigationMenu() {
        VBox menu = new VBox(2);
        menu.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));

        String[][] items = {
            {"DB", "Dashboard", "dashboard"},
            {"MB", "Member Management", "members"},
            {"PL", "Manage Plans", "plans"},
            {"PAY", "Payment & Billing", "payment"},
            {"INV", "Inventory", "inventory"},
            {"EQ", "Equipment", "equipment"},
            {"POS", "Point of Sale", "pos"},
            {"SE", "Search", "search"},
            {"REP", "Reports", "reports"}
        };

        for (String[] item : items) {
            Button btn = createNavButton(item[0], item[1], item[2]);
            menu.getChildren().add(btn);
        }

        return menu;
    }

    private VBox createSystemMenu() {
        VBox menu = new VBox(2);
        menu.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));

        String[][] items = AppSession.currentUser().isAdmin()
            ? new String[][] {
                {"RV", "Registration/Verification", "registration"},
                {"PR", "Profile", "profile"},
                {"MT", "Maintenance", "maintenance"},
                {"ST", "Settings", "settings"},
                {"?", "Help", "help"},
                {"i", "About", "about"}
            }
            : new String[][] {
            {"PR", "Profile", "profile"},
            {"ST", "Settings", "settings"},
            {"?", "Help", "help"},
            {"i", "About", "about"}
        };

        for (String[] item : items) {
            Button btn = createNavButton(item[0], item[1], item[2]);
            menu.getChildren().add(btn);
        }

        return menu;
    }

    private VBox createUserFooter() {
        AppSession.User user = AppSession.currentUser();

        VBox footer = new VBox(10);
        footer.setPadding(new Insets(14, 14, 16, 14));
        footer.setStyle(
            "-fx-background-color: " + ModernDesignSystem.CARD_BG + ";" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_MEDIUM + ";" +
            "-fx-padding: 14 14 16 14;" +
            "-fx-border-color: " + ModernDesignSystem.BORDER_COLOR + " transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );

        HBox accountRow = new HBox(10);
        accountRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.setPrefSize(36, 36);
        Rectangle avatarBg = new Rectangle(36, 36);
        avatarBg.setArcWidth(10);
        avatarBg.setArcHeight(10);
        avatarBg.setFill(Color.web(ModernDesignSystem.PRIMARY));
        avatarBg.setEffect(ModernDesignSystem.createElevation2());
        Text avatarInitial = new Text(user.initial());
        avatarInitial.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 14));
        avatarInitial.setFill(Color.WHITE);
        avatar.getChildren().addAll(avatarBg, avatarInitial);

        VBox accountText = new VBox(2);
        Text name = new Text(user.displayName());
        name.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 11));
        name.setFill(Color.web(ModernDesignSystem.PRIMARY));
        Text role = new Text(user.role());
        role.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, 9));
        role.setFill(Color.web(ModernDesignSystem.TEXT_MUTED));
        accountText.getChildren().addAll(name, role);

        accountRow.getChildren().addAll(avatar, accountText);

        Button logoutButton = new Button("Logout");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + ModernDesignSystem.PRIMARY + ";" +
            "-fx-border-radius: " + ModernDesignSystem.RADIUS_SMALL + ";" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_SMALL + ";" +
            "-fx-text-fill: " + ModernDesignSystem.PRIMARY + ";" +
            "-fx-font-family: '" + ModernDesignSystem.FONT_FAMILY + "';" +
            "-fx-font-size: " + ModernDesignSystem.FONT_BODY + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 9 12;" +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
            "-fx-background-color: " + ModernDesignSystem.PRIMARY + ";" +
            "-fx-border-color: " + ModernDesignSystem.PRIMARY + ";" +
            "-fx-border-radius: " + ModernDesignSystem.RADIUS_SMALL + ";" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_SMALL + ";" +
            "-fx-text-fill: " + ModernDesignSystem.WHITE + ";" +
            "-fx-font-family: '" + ModernDesignSystem.FONT_FAMILY + "';" +
            "-fx-font-size: " + ModernDesignSystem.FONT_BODY + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 9 12;" +
            "-fx-cursor: hand;"
        ));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + ModernDesignSystem.PRIMARY + ";" +
            "-fx-border-radius: " + ModernDesignSystem.RADIUS_SMALL + ";" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_SMALL + ";" +
            "-fx-text-fill: " + ModernDesignSystem.PRIMARY + ";" +
            "-fx-font-family: '" + ModernDesignSystem.FONT_FAMILY + "';" +
            "-fx-font-size: " + ModernDesignSystem.FONT_BODY + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 9 12;" +
            "-fx-cursor: hand;"
        ));
        logoutButton.setOnAction(e -> logout());

        footer.getChildren().addAll(accountRow, logoutButton);
        return footer;
    }

    private void styleLogoutButton(Button button, boolean hovered) {
        // Method removed - styling now handled inline in createUserFooter()
    }

    private void logout() {
        AppSession.logout();
        try {
            new LoginScreen().start(primaryStage);
        } catch (Exception ex) {
            System.err.println("[APP ERROR] Could not return to login screen: " + ex.getMessage());
        }
    }

    private Button createNavButton(String icon, String label, String screenId) {
        Button btn = new Button(icon + "  " + label);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + ModernDesignSystem.TEXT_MUTED + ";" +
            "-fx-font-family: '" + ModernDesignSystem.FONT_FAMILY + "';" +
            "-fx-font-size: " + ModernDesignSystem.FONT_BODY + ";" +
            "-fx-padding: " + ModernDesignSystem.SPACING_M + " " + ModernDesignSystem.SPACING_L + ";" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-cursor: hand;"
        );
        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setOnAction(e -> {
            currentScreen = screenId;
            loadScreen(screenId);
            updateNavButtonStyles();
        });

        // Hover effect with modern styling
        btn.setOnMouseEntered(e ->
            btn.setStyle(
                "-fx-background-color: " + ModernDesignSystem.HOVER_EFFECT + ";" +
                "-fx-text-fill: " + ModernDesignSystem.PRIMARY + ";" +
                "-fx-font-family: '" + ModernDesignSystem.FONT_FAMILY + "';" +
                "-fx-font-size: " + ModernDesignSystem.FONT_BODY + ";" +
                "-fx-font-weight: bold;" +
                "-fx-padding: " + ModernDesignSystem.SPACING_M + " " + ModernDesignSystem.SPACING_L + ";" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: " + ModernDesignSystem.RADIUS_SMALL + ";"
            )
        );

        btn.setOnMouseExited(e ->
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + ModernDesignSystem.TEXT_MUTED + ";" +
                "-fx-font-family: '" + ModernDesignSystem.FONT_FAMILY + "';" +
                "-fx-font-size: " + ModernDesignSystem.FONT_BODY + ";" +
                "-fx-padding: " + ModernDesignSystem.SPACING_M + " " + ModernDesignSystem.SPACING_L + ";" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-cursor: hand;"
            )
        );

        return btn;
    }

    private void updateNavButtonStyles() {
        // This would update button styles based on current screen
    }

    private void loadScreen(String screenId) {
        contentArea.getChildren().clear();

        VBox screen = switch (screenId) {
            case "dashboard" -> new DashboardScreen().buildDashboardContent();
            case "members" -> new MemberManagementScreen().buildContent();
            case "plans" -> new PlanManagementScreen().buildContent();
            case "payment" -> new PaymentScreen().buildContent();
            case "inventory" -> new InventoryScreen().buildContent();
            case "equipment" -> new AddEquipmentScreen().buildContent();
            case "pos" -> new POSScreen().buildContent();
            case "search" -> new SearchScreen().buildContent();
            case "reports" -> new ReportsScreen().buildContent();
            case "maintenance" -> AppSession.currentUser().isAdmin()
                ? new MaintenanceScreen().buildContent()
                : createAccessDeniedScreen();
            case "accounts", "registration" -> AppSession.currentUser().isAdmin()
                ? new AccountManagementScreen().buildContent()
                : createAccessDeniedScreen();
            case "profile" -> new AdminProfileScreen().buildContent();
            case "settings" -> new SettingsScreen().buildContent();
            case "about" -> new AboutScreen().buildContent();
            case "help" -> new HelpScreen().buildContent();
            default -> new DashboardScreen().buildDashboardContent();
        };

        screen.setMaxWidth(Double.MAX_VALUE);
        screen.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(screen, Priority.ALWAYS);
        contentArea.getChildren().add(screen);
    }

    private VBox createAccessDeniedScreen() {
        VBox vbox = new VBox(12);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");

        Text title = new Text("Access Restricted");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));

        Label message = new Label("Account registration and verification are available to administrators only.");
        message.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font: 14 Poppins;");

        vbox.getChildren().addAll(title, message);
        return vbox;
    }

    private VBox createDashboardScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");

        Text title = new Text("Dashboard");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));

        Text subtitle = new Text("Welcome to MJ23 Playgrind Gym Management System");
        subtitle.setFont(Font.font(14));
        subtitle.setFill(Color.web(TEXT_MUTED));

        Label content = new Label("Dashboard content - All screens are now integrated and working together!");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Poppins;");

        vbox.getChildren().addAll(title, subtitle, content);
        return vbox;
    }

    private VBox createMembersScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Member Management");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Member Management content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Poppins;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createPaymentScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Payment & Billing");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Payment & Billing content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Poppins;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createInventoryScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Inventory Management");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Inventory content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Poppins;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createEquipmentScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Equipment Management");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Equipment content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Poppins;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createPOSScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Point of Sale");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("POS content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Poppins;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createReportsScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Reports");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Reports content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Poppins;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createSettingsScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Settings");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Settings content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Poppins;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private Image loadImage(String resourcePath) {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            return new WritableImage(1, 1);
        }
        return new Image(resource.toExternalForm());
    }

    public static void main(String[] args) {
        // Launch the login screen as the main entry point
        LoginScreen.launch(LoginScreen.class, args);
    }
}

