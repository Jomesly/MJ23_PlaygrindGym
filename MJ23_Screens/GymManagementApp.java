package mj23gym.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
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

/**
 * MJ23 Playgrind Gym – Main Application Launcher
 * Central application that integrates all screens
 */
public class GymManagementApp extends Application {

    static final String BG_MAIN      = "#1a1a2e";
    static final String BG_SIDEBAR   = "#0d1b2a";
    static final String BG_CARD      = "#1e2a3a";
    static final String ACCENT       = "#e63946";
    static final String TEXT_WHITE   = "#ffffff";
    static final String TEXT_MUTED   = "#b0bec5";

    private BorderPane rootPane;
    private StackPane contentArea;
    private String currentScreen = "dashboard";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MJ23 Playgrind Gym – Management System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(720);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);

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
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        sidebar.setSpacing(0);

        // Top accent bar
        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web(ACCENT));

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
                                     new Separator(), sysMenu, spacer);
        return sidebar;
    }

    private VBox createLogoSection() {
        VBox logo = new VBox(8);
        logo.setPadding(new javafx.geometry.Insets(20));
        logo.setAlignment(Pos.CENTER);

        Text title = new Text("MJ23\nPLAYGRIND\nGYM");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        title.setFill(Color.web(TEXT_WHITE));

        logo.getChildren().add(title);
        return logo;
    }

    private VBox createNavigationMenu() {
        VBox menu = new VBox(2);
        menu.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));

        String[][] items = {
            {"🏠", "Dashboard", "dashboard"},
            {"👥", "Member Management", "members"},
            {"💳", "Payment & Billing", "payment"},
            {"📦", "Inventory", "inventory"},
            {"🏋", "Equipment", "equipment"},
            {"🛒", "Point of Sale", "pos"},
            {"📊", "Reports", "reports"}
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

        String[][] items = {
            {"⚙", "Settings", "settings"},
            {"❓", "Help", "help"},
            {"ℹ", "About", "about"}
        };

        for (String[] item : items) {
            Button btn = createNavButton(item[0], item[1], item[2]);
            menu.getChildren().add(btn);
        }

        return menu;
    }

    private Button createNavButton(String icon, String label, String screenId) {
        Button btn = new Button(icon + "  " + label);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font: 12 Verdana;" +
            "-fx-padding: 12 16;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-cursor: hand;"
        );
        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setOnAction(e -> {
            currentScreen = screenId;
            loadScreen(screenId);
            updateNavButtonStyles();
        });

        // Hover effect
        btn.setOnMouseEntered(e ->
            btn.setStyle(
                "-fx-background-color: rgba(230,57,70,0.15);" +
                "-fx-text-fill: " + TEXT_WHITE + ";" +
                "-fx-font: bold 12 Verdana;" +
                "-fx-padding: 12 16;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-cursor: hand;"
            )
        );

        btn.setOnMouseExited(e ->
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                "-fx-font: 12 Verdana;" +
                "-fx-padding: 12 16;" +
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
            case "payment" -> new PaymentScreen().buildContent();
            case "inventory" -> new InventoryScreen().buildContent();
            case "equipment" -> new AddEquipmentScreen().buildContent();
            case "pos" -> new POSScreen().buildContent();
            case "reports" -> new ReportsScreen().buildContent();
            case "settings", "help", "about" -> new SettingsScreen().buildContent();
            default -> new DashboardScreen().buildDashboardContent();
        };

        contentArea.getChildren().add(screen);
    }

    private VBox createDashboardScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");

        Text title = new Text("Dashboard");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));

        Text subtitle = new Text("Welcome to MJ23 Playgrind Gym Management System");
        subtitle.setFont(Font.font(14));
        subtitle.setFill(Color.web(TEXT_MUTED));

        Label content = new Label("Dashboard content - All screens are now integrated and working together!");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Arial;");

        vbox.getChildren().addAll(title, subtitle, content);
        return vbox;
    }

    private VBox createMembersScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Member Management");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Member Management content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Arial;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createPaymentScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Payment & Billing");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Payment & Billing content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Arial;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createInventoryScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Inventory Management");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Inventory content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Arial;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createEquipmentScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Equipment Management");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Equipment content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Arial;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createPOSScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Point of Sale");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("POS content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Arial;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createReportsScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Reports");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Reports content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Arial;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    private VBox createSettingsScreen() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: " + BG_MAIN + ";");
        Text title = new Text("Settings");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web(TEXT_WHITE));
        Label content = new Label("Settings content loaded");
        content.setStyle("-fx-text-fill: " + TEXT_WHITE + "; -fx-font: 14 Arial;");
        vbox.getChildren().addAll(title, content);
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
