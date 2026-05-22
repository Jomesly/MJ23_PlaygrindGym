package mj23gym.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import mj23gym.dao.ActivityLogDAO;
import mj23gym.dao.EquipmentDAO;
import mj23gym.dao.InventoryDAO;
import mj23gym.dao.MemberDAO;
import mj23gym.dao.PaymentDAO;
import mj23gym.dao.PosDAO;
import mj23gym.dao.SearchDAO;

/**
 * MJ23 Playgrind Gym  Dashboard Screen
 * Matches the official screen design color scheme:
 *   BG Main      : #ECE9E9
 *   Sidebar      : #FFFFFF
 *   Card         : #FFFFFF
 *   Accent       : #1A1363
 *   Table rows   : #FFFFFF / #E4FFDF
 *   Text         : #ffffff / #77749B
 */
public class DashboardScreen extends Application {

    // Use modern design system colors
    static final String BG_MAIN      = ModernDesignSystem.BG_LIGHT;
    static final String BG_SIDEBAR   = ModernDesignSystem.SIDEBAR_BG;
    static final String BG_CARD      = ModernDesignSystem.CARD_BG;
    static final String BG_ROW_ALT   = ModernDesignSystem.HOVER_EFFECT;
    static final String ACCENT       = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK  = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_WHITE   = ModernDesignSystem.PRIMARY;
    static final String TEXT_MUTED   = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DIM     = ModernDesignSystem.DARK_GRAY;
    static final String BORDER       = ModernDesignSystem.BORDER_COLOR;
    static final String SUCCESS      = ModernDesignSystem.SUCCESS;
    static final String WARNING      = ModernDesignSystem.ACCENT_YELLOW;
    static final String INFO         = ModernDesignSystem.PRIMARY;
    static final String SUCCESS_TEXT = "#237A36";
    static final String WARNING_TEXT = "#6E6400";
    private static final String LOGO_PATH = "/images/mj23-logo.png";
    private static final DateTimeFormatter ACTIVITY_TIME_FORMAT =
        DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private String activeMenu = "Dashboard";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Dashboard");

        BorderPane root = new BorderPane();
        root.setPrefSize(1200, 720);
        root.setStyle("-fx-background-color: " + BG_MAIN + ";");

        //  SIDEBAR 
        VBox sidebar = buildSidebar(root);
        root.setLeft(sidebar);

        //  MAIN CONTENT 
        VBox mainContent = buildDashboardContent();
        root.setCenter(mainContent);

        //  Fade in 
        FadeTransition ft = new FadeTransition(Duration.millis(400), mainContent);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.show();
    }

    // 
    // SIDEBAR
    // 
    private VBox buildSidebar(BorderPane root) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setStyle(
            "-fx-background-color: " + BG_SIDEBAR + ";" +
            "-fx-padding: 0;"
        );

        // Top accent with modern styling
        Rectangle topAccent = new Rectangle(230, 6);
        topAccent.setFill(Color.web(ModernDesignSystem.ACCENT_YELLOW));

        // Logo area
        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));

        StackPane logoBadge = new StackPane();
        logoBadge.setPrefSize(52, 52);
        logoBadge.setMaxSize(52, 52);
        Rectangle logoBg = new Rectangle(52, 52);
        logoBg.setArcWidth(ModernDesignSystem.RADIUS_MEDIUM);
        logoBg.setArcHeight(ModernDesignSystem.RADIUS_MEDIUM);
        logoBg.setFill(Color.web(ModernDesignSystem.WHITE));
        logoBg.setStroke(Color.web(ModernDesignSystem.BORDER_COLOR));
        logoBg.setEffect(ModernDesignSystem.createElevation2());
        ImageView logoView = new ImageView(loadImage(LOGO_PATH));
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(44);
        logoView.setFitHeight(44);
        logoBadge.getChildren().addAll(logoBg, logoView);

        VBox logoText = new VBox(1);
        Text gymName = new Text("MJ23 PLAYGRIND");
        gymName.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 11));
        gymName.setFill(Color.web(ModernDesignSystem.PRIMARY));
        Text gymSub = new Text("GYM");
        gymSub.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 11));
        gymSub.setFill(Color.web(ModernDesignSystem.ACCENT_YELLOW));
        logoText.getChildren().addAll(gymName, gymSub);

        logoArea.getChildren().addAll(logoBadge, logoText);

        // Sidebar divider
        Rectangle div1 = makeSidebarDivider();

        // Section label
        Label menuLabel = makeSectionLabel("MAIN MENU");

        // Menu items
        String[][] menuItems = {
            {"DB", "Dashboard"},
            {"MB", "Member Management"},
            {"PAY", "Payment & Billing"},
            {"INV", "Inventory"},
            {"EQ", "Equipment"},
            {"POS", "Point of Sale"},
            {"REP", "Reports"},
        };

        VBox menuBox = new VBox(2);
        menuBox.setPadding(new Insets(0, 10, 0, 10));
        for (String[] item : menuItems) {
            HBox menuItem = buildMenuItem(item[0], item[1], item[1].equals(activeMenu), root);
            menuBox.getChildren().add(menuItem);
        }

        Rectangle div2 = makeSidebarDivider();
        Label settingsLabel = makeSectionLabel("SYSTEM");

        String[][] sysItems = {
            {"ST", "Settings"},
            {"?", "Help"},
            {"i", "About"},
        };

        VBox sysBox = new VBox(2);
        sysBox.setPadding(new Insets(0, 10, 0, 10));
        for (String[] item : sysItems) {
            HBox menuItem = buildMenuItem(item[0], item[1], false, root);
            sysBox.getChildren().add(menuItem);
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // User info at bottom
        HBox userBox = new HBox(12);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(16, 16, 20, 16));
        userBox.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + " transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );

        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(ACCENT));
        Text avatarTxt = new Text("A");
        avatarTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        avatarTxt.setFill(Color.WHITE);
        StackPane avatarStack = new StackPane(avatar, avatarTxt);
        avatarStack.setPrefSize(36, 36);

        VBox userInfo = new VBox(2);
        Text userName = new Text("Admin");
        userName.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        userName.setFill(Color.web(TEXT_WHITE));
        Text userRole = new Text("Administrator");
        userRole.setFont(Font.font("Poppins", 10));
        userRole.setFill(Color.web(TEXT_MUTED));
        userInfo.getChildren().addAll(userName, userRole);

        Button logoutBtn = new Button("");
        logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 14;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 4 8 4 8;"
        );
        logoutBtn.setTooltip(new Tooltip("Logout"));
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-font-size: 14;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 4 8 4 8;"
        ));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 14;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 4 8 4 8;"
        ));
        logoutBtn.setOnAction(e -> ((Stage) logoutBtn.getScene().getWindow()).close());
        HBox.setHgrow(userInfo, Priority.ALWAYS);
        userBox.getChildren().addAll(avatarStack, userInfo, logoutBtn);

        sidebar.getChildren().addAll(
            topAccent, logoArea, div1, menuLabel, menuBox,
            div2, settingsLabel, sysBox, spacer, userBox
        );

        return sidebar;
    }

    private HBox buildMenuItem(String icon, String label, boolean active, BorderPane root) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(11, 16, 11, 16));
        item.setCursor(javafx.scene.Cursor.HAND);

        // Active indicator bar
        Rectangle activeBar = new Rectangle(3, 36);
        activeBar.setArcWidth(3);
        activeBar.setArcHeight(3);
        activeBar.setFill(active ? Color.web(ACCENT) : Color.TRANSPARENT);

        Text iconTxt = new Text(icon);
        iconTxt.setFont(Font.font(14));

        Text labelTxt = new Text(label);
        labelTxt.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        labelTxt.setFill(active ? Color.web(TEXT_WHITE) : Color.web(TEXT_MUTED));

        item.getChildren().addAll(activeBar, iconTxt, labelTxt);

        if (active) {
            item.setStyle(
                "-fx-background-color: " + BG_CARD + ";" +
                "-fx-background-radius: 16;"
            );
        } else {
            item.setStyle("-fx-background-color: transparent; -fx-background-radius: 16;");
            item.setOnMouseEntered(e -> item.setStyle(
                "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 16;"
            ));
            item.setOnMouseExited(e -> item.setStyle(
                "-fx-background-color: transparent; -fx-background-radius: 16;"
            ));
        }

        item.setOnMouseClicked(e -> {
            VBox screen = switch (label) {
                case "Dashboard" -> buildDashboardContent();
                case "Member Management" -> new MemberManagementScreen().buildContent();
                case "Payment & Billing" -> new PaymentScreen().buildContent();
                case "Inventory" -> new InventoryScreen().buildContent();
                case "Equipment" -> new AddEquipmentScreen().buildContent();
                case "Point of Sale" -> new POSScreen().buildContent();
                case "Reports" -> new ReportsScreen().buildContent();
                case "Settings" -> new SettingsScreen().buildContent();
                case "Help" -> new HelpScreen().buildContent();
                case "About" -> new AboutScreen().buildContent();
                default -> buildDashboardContent();
            };
            root.setCenter(screen);
        });

        return item;
    }

    private Rectangle makeSidebarDivider() {
        Rectangle r = new Rectangle();
        r.setHeight(1);
        r.setWidth(230);
        r.setFill(Color.web(BORDER));
        VBox.setMargin(r, new Insets(6, 0, 6, 0));
        return r;
    }

    private Label makeSectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_DIM));
        lbl.setPadding(new Insets(4, 0, 6, 20));
        return lbl;
    }

    // 
    // DASHBOARD MAIN CONTENT
    // 
    public VBox buildDashboardContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        //  Top Bar 
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setSpacing(16);
        topBar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        VBox pageTitle = new VBox(2);
        AppSession.User user = AppSession.currentUser();
        Text pgTitle = new Text("Dashboard");
        pgTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        pgTitle.setFill(Color.web(TEXT_WHITE));
        Text pgSub = new Text("Welcome back, " + user.displayName() + " " + LocalDate.now());
        pgSub.setFont(Font.font("Poppins", 11));
        pgSub.setFill(Color.web(TEXT_MUTED));
        pageTitle.getChildren().addAll(pgTitle, pgSub);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(220);
        searchField.setPrefHeight(36);
        searchField.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 18;" +
            "-fx-background-radius: 18;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 14 0 14;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 11;"
        );

        // Notification bell
        Button notifBtn = new Button("🔔");
        notifBtn.setStyle(
            "-fx-background-color: " + ModernDesignSystem.SIDEBAR_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-background-radius: 50%;" +
            "-fx-font-size: 14;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 6 10 6 10;"
        );

        topBar.getChildren().addAll(pageTitle, topSpacer, searchField, notifBtn);

        //  Scrollable body 
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(24);
        body.setPadding(new Insets(28, 28, 28, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        //  Fetch data from DAOs 
        MemberDAO memberDAO = new MemberDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();
        EquipmentDAO equipmentDAO = new EquipmentDAO();
        PosDAO posDAO = new PosDAO();
        ActivityLogDAO activityLogDAO = new ActivityLogDAO();

        int totalMembers = memberDAO.countAll();
        double revenueToday = paymentDAO.todayRevenue() + posDAO.todayPosTotal();
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate yesterday = today.minusDays(1);
        int membersThisMonth = memberDAO.countCreatedBetween(
            java.sql.Date.valueOf(monthStart),
            java.sql.Date.valueOf(today)
        );
        double revenueYesterday = paymentDAO.sumCompletedBetween(
            java.sql.Date.valueOf(yesterday),
            java.sql.Date.valueOf(yesterday)
        ) + posDAO.sumPosRevenueBetween(
            java.sql.Date.valueOf(yesterday),
            java.sql.Date.valueOf(yesterday)
        );
        int lowStockCount = inventoryDAO.countLowStock();
        int maintDue = equipmentDAO.findMaintenanceDue().size();
        searchField.setOnAction(e -> showDashboardSearch(searchField.getText()));
        notifBtn.setOnAction(e -> showDashboardNotifications(lowStockCount, maintDue));

        List<MemberDAO.MemberRecord> recentMembers = memberDAO.findRecent(5);
        
        List<PaymentDAO.PaymentRecord> recentPayments = paymentDAO.findRecentPayments(5);
        List<ActivityLogDAO.ActivitySession> recentSessions = activityLogDAO.findRecentSessions(8);

        //  Summary Cards Row 
        HBox summaryCards = new HBox(18);
        summaryCards.setAlignment(Pos.CENTER_LEFT);

        String revStr = String.format("%.0f", revenueToday);
        summaryCards.getChildren().addAll(
            makeSummaryCard("👥", "Total Members",    String.valueOf(totalMembers),  formatMembersThisMonth(membersThisMonth),  ACCENT,   true),
            makeSummaryCard("💰", "Revenue Today",    revStr, formatRevenueDelta(revenueToday, revenueYesterday), SUCCESS, false),
            makeSummaryCard("📦", "Low Stock Items",  String.valueOf(lowStockCount),    "Needs restocking", WARNING, false),
            makeSummaryCard("🔧", "Maintenance Due", String.valueOf(maintDue),
                maintDue > 0 ? "Within 30 days" : "None due soon", INFO, false)
        );

        VBox dashboardDetails = new VBox(0);
        dashboardDetails.getChildren().setAll(
            buildMembersDetail(memberDAO, totalMembers, recentMembers)
        );

        makeSummaryCardsClickable(
            summaryCards,
            () -> dashboardDetails.getChildren().setAll(buildMembersDetail(memberDAO, totalMembers, recentMembers)),
            () -> dashboardDetails.getChildren().setAll(buildRevenueDetail(paymentDAO, posDAO, revenueToday)),
            () -> dashboardDetails.getChildren().setAll(buildLowStockDetail(inventoryDAO)),
            () -> dashboardDetails.getChildren().setAll(buildMaintenanceDetail(equipmentDAO))
        );

        //  Recent Activity + Quick Stats row 
        HBox midRow = new HBox(18);
        HBox.setHgrow(midRow, Priority.ALWAYS);

        int totalEq = equipmentDAO.findAll().size();
        int maintCount = equipmentDAO.countByCondition("Maintenance");
        int brokenCount = equipmentDAO.countByCondition("Broken");
        int equipOk = Math.max(0, totalEq - maintCount - brokenCount);

        int activeMembers = memberDAO.countByStatus("Active");
        int expiredMembers = memberDAO.countByStatus("Expired");

        // Convert member records to table data
        String[][] memberTableData = new String[recentMembers.size()][4];
        for (int i = 0; i < recentMembers.size(); i++) {
            MemberDAO.MemberRecord m = recentMembers.get(i);
            memberTableData[i][0] = "#" + m.memberCode();
            memberTableData[i][1] = m.fullName();
            memberTableData[i][2] = m.membershipType() != null ? m.membershipType() : "Monthly";
            memberTableData[i][3] = formatStatusDisplay(m.status() != null ? m.status() : "Active");
        }

        // Recent Members table
        VBox recentMembersCard = buildTableCard(
            "Recent Member Registrations",
            new String[]{"Member ID", "Name", "Plan", "Status"},
            memberTableData.length > 0 ? memberTableData : new String[][]{
                {"#M-001", "Loading...", "Loading...", "Loading..."}
            }
        );
        HBox.setHgrow(recentMembersCard, Priority.ALWAYS);

        // Quick stats panel
        VBox quickStats = buildQuickStats(
            activeMembers,
            expiredMembers,
            posDAO.todayPosTotal(),
            Math.max(0, equipOk),
            equipmentDAO.countByCondition("Maintenance"),
            lowStockCount
        );
        quickStats.setMinWidth(240);
        quickStats.setMaxWidth(260);

        midRow.getChildren().addAll(recentMembersCard, quickStats);

        // Convert payment records to table data
        String[][] paymentTableData = new String[recentPayments.size()][5];
        for (int i = 0; i < recentPayments.size(); i++) {
            PaymentDAO.PaymentRecord p = recentPayments.get(i);
            paymentTableData[i][0] = p.memberName() != null ? p.memberName() : "Unknown";
            paymentTableData[i][1] = String.format("%.2f", p.amount());
            paymentTableData[i][2] = p.paymentMethod() != null ? p.paymentMethod() : "Cash";
            paymentTableData[i][3] = p.paymentDate() != null ? p.paymentDate().toString() : LocalDate.now().toString();
            paymentTableData[i][4] = p.status() != null ? p.status() : "Pending";
        }

        //  Recent Payments table 
        VBox paymentsCard = buildTableCard(
            "Recent Payments",
            new String[]{"Member", "Amount", "Method", "Date", "Status"},
            paymentTableData.length > 0 ? paymentTableData : new String[][]{
                {"Loading...", "0.00", "Loading...", LocalDate.now().toString(), "Loading..."}
            }
        );

        body.getChildren().addAll(summaryCards, dashboardDetails, buildActivitySessions(recentSessions));
        scrollPane.setContent(body);

        content.getChildren().addAll(topBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return content;
    }

    private VBox buildActivitySessions(List<ActivityLogDAO.ActivitySession> sessions) {
        String[][] rows = new String[Math.max(1, sessions.size())][4];
        if (sessions.isEmpty()) {
            rows[0] = new String[]{"No records", "No activity sessions saved yet", "-", "-"};
        } else {
            for (int i = 0; i < sessions.size(); i++) {
                ActivityLogDAO.ActivitySession session = sessions.get(i);
                rows[i][0] = session.fullName() != null && !session.fullName().isBlank()
                    ? session.fullName()
                    : session.username();
                rows[i][1] = session.role();
                rows[i][2] = session.action();
                rows[i][3] = session.occurredAt() != null
                    ? session.occurredAt().toLocalDateTime().format(ACTIVITY_TIME_FORMAT)
                    : "-";
            }
        }
        return buildTableCard(
            "Activity Sessions",
            new String[]{"User", "Role", "Activity", "Saved Time"},
            rows,
            false
        );
    }

    //  Summary stat card - Enhanced with better visual design
    private VBox makeSummaryCard(String icon, String label, String value,
                                  String sub, String color, boolean highlighted) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(22, 24, 22, 24));
        card.setPrefWidth(220);
        card.setMinHeight(160);
        card.setCursor(javafx.scene.Cursor.HAND);
        String readableColor = readableAccent(color);
        String baseStyle =
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;";
        String hoverStyle =
            "-fx-background-color: " + ModernDesignSystem.HOVER_EFFECT + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;";
        String selectedStyle =
            "-fx-background-color: " + ModernDesignSystem.HOVER_EFFECT + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 3;";

        DropShadow baseShadow = createSummaryShadow(ACCENT, 0.10, 12, 4);
        DropShadow hoverShadow = createSummaryShadow(ACCENT, 0.16, 18, 6);
        DropShadow selectedShadow = createSummaryShadow(readableColor, 0.22, 20, 7);
        card.getProperties().put("summaryBaseStyle", baseStyle);
        card.getProperties().put("summaryHoverStyle", hoverStyle);
        card.getProperties().put("summarySelectedStyle", selectedStyle);
        card.getProperties().put("summaryBaseShadow", baseShadow);
        card.getProperties().put("summaryHoverShadow", hoverShadow);
        card.getProperties().put("summarySelectedShadow", selectedShadow);
        card.getProperties().put("summarySelected", highlighted);
        applySummaryCardVisual(card, highlighted ? "summarySelectedStyle" : "summaryBaseStyle",
            highlighted ? "summarySelectedShadow" : "summaryBaseShadow");
        card.setOnMouseEntered(e -> {
            boolean selected = Boolean.TRUE.equals(card.getProperties().get("summarySelected"));
            applySummaryCardVisual(card, selected ? "summarySelectedStyle" : "summaryHoverStyle",
                selected ? "summarySelectedShadow" : "summaryHoverShadow");
        });
        card.setOnMouseExited(e -> {
            boolean selected = Boolean.TRUE.equals(card.getProperties().get("summarySelected"));
            applySummaryCardVisual(card, selected ? "summarySelectedStyle" : "summaryBaseStyle",
                selected ? "summarySelectedShadow" : "summaryBaseShadow");
        });
        HBox.setHgrow(card, Priority.ALWAYS);

        Rectangle accentCut = new Rectangle(46, 4);
        accentCut.setArcWidth(4);
        accentCut.setArcHeight(4);
        accentCut.setFill(Color.web(color));

        // Top row with icon and value
        HBox topRow = new HBox(14);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Icon circle - enhanced with better styling
        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(56, 56);
        Rectangle iconBg = new Rectangle(56, 56);
        iconBg.setArcWidth(14);
        iconBg.setArcHeight(14);
        iconBg.setFill(Color.web(color, 0.14));
        iconBg.setStroke(Color.web(readableColor, 0.18));
        Text iconTxt = new Text(icon);
        iconTxt.setFont(Font.font(28));
        iconCircle.getChildren().addAll(iconBg, iconTxt);

        // Value and label
        VBox valueSection = new VBox(4);
        Text lblTxt = new Text(label);
        lblTxt.setFont(Font.font("Poppins", FontWeight.NORMAL, 10));
        lblTxt.setFill(Color.web(TEXT_MUTED));

        Text valTxt = new Text(value);
        valTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 32));
        valTxt.setFill(Color.web(TEXT_WHITE));
        valTxt.setStroke(Color.web(ModernDesignSystem.WHITE, 0.85));
        valTxt.setStrokeWidth(0.35);

        valueSection.getChildren().addAll(lblTxt, valTxt);
        topRow.getChildren().addAll(iconCircle, valueSection);

        // Subtitle with color accent
        Text subTxt = new Text(sub);
        subTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        subTxt.setFill(Color.web(readableColor));

        card.getChildren().addAll(accentCut, topRow, subTxt);
        return card;
    }

    private void applySummaryCardVisual(javafx.scene.Node card, String styleKey, String shadowKey) {
        Object style = card.getProperties().get(styleKey);
        Object shadow = card.getProperties().get(shadowKey);
        if (style instanceof String) {
            card.setStyle((String) style);
        }
        if (shadow instanceof DropShadow) {
            card.setEffect((DropShadow) shadow);
        }
    }

    private DropShadow createSummaryShadow(String color, double opacity, double radius, double offsetY) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(color, opacity));
        shadow.setRadius(radius);
        shadow.setOffsetY(offsetY);
        shadow.setOffsetX(0);
        return shadow;
    }

    private void makeSummaryCardsClickable(HBox cards, Runnable... actions) {
        for (int i = 0; i < cards.getChildren().size() && i < actions.length; i++) {
            Runnable action = actions[i];
            javafx.scene.Node card = cards.getChildren().get(i);
            card.setOnMouseClicked(e -> {
                if (action != null) {
                    action.run();
                }
                for (javafx.scene.Node otherCard : cards.getChildren()) {
                    otherCard.getProperties().put("summarySelected", false);
                    applySummaryCardVisual(otherCard, "summaryBaseStyle", "summaryBaseShadow");
                }
                card.getProperties().put("summarySelected", true);
                applySummaryCardVisual(card, "summarySelectedStyle", "summarySelectedShadow");
            });
        }
    }

    //  Data table card 
    private VBox buildTableCard(String title, String[] headers, String[][] rows) {
        return buildTableCard(title, headers, rows, true);
    }

    private VBox buildTableCard(String title, String[] headers, String[][] rows, boolean badgeLastColumn) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);

        // Card header
        HBox cardHeader = new HBox();
        cardHeader.setPadding(new Insets(16, 20, 14, 20));
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setStyle(
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        Text titleTxt = new Text(title);
        titleTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        titleTxt.setFill(Color.web(TEXT_WHITE));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        cardHeader.getChildren().addAll(titleTxt, sp);

        // Table
        GridPane table = new GridPane();
        table.setPadding(new Insets(0));

        // Header row
        for (int c = 0; c < headers.length; c++) {
            Label h = new Label(headers[c].toUpperCase());
            h.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
            h.setTextFill(Color.web(TEXT_DIM));
            h.setPadding(new Insets(10, 14, 10, 14));
            h.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(h, Priority.ALWAYS);
            table.add(h, c, 0);
        }

        // Data rows
        for (int r = 0; r < rows.length; r++) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            for (int c = 0; c < rows[r].length; c++) {
                String cellVal = rows[r][c];
                if (badgeLastColumn && c == rows[r].length - 1) {
                    // Status badge
                    StackPane badgeCell = new StackPane(makeStatusBadge(cellVal));
                    badgeCell.setAlignment(Pos.CENTER_LEFT);
                    badgeCell.setPadding(new Insets(8, 14, 8, 14));
                    badgeCell.setMaxWidth(Double.MAX_VALUE);
                    badgeCell.setStyle("-fx-background-color: " + bg + ";");
                    GridPane.setHgrow(badgeCell, Priority.ALWAYS);
                    GridPane.setFillWidth(badgeCell, true);
                    table.add(badgeCell, c, r + 1);
                } else {
                    Label cell = new Label(cellVal);
                    cell.setFont(Font.font("Poppins", 11));
                    cell.setTextFill(Color.web(TEXT_WHITE));
                    cell.setPadding(new Insets(10, 14, 10, 14));
                    cell.setMaxWidth(Double.MAX_VALUE);
                    cell.setStyle("-fx-background-color: " + bg + ";");
                    GridPane.setHgrow(cell, Priority.ALWAYS);
                    GridPane.setFillWidth(cell, true);
                    table.add(cell, c, r + 1);
                }
            }
        }

        // Column constraints - equal width
        for (int i = 0; i < headers.length; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setPercentWidth(100.0 / headers.length);
            table.getColumnConstraints().add(cc);
        }

        card.getChildren().addAll(cardHeader, table);
        return card;
    }

    //  Status badge 
    private Label makeStatusBadge(String status) {
        String displayStatus = formatStatusDisplay(status);
        Label badge = new Label(displayStatus);
        badge.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String color, bg;
        switch (displayStatus.toLowerCase()) {
            case "active": case "paid":
                color = SUCCESS_TEXT; bg = "rgba(228,255,223,0.85)"; break;
            case "expired": case "overdue":
                color = ACCENT; bg = "rgba(26,19,99,0.15)"; break;
            case "pending":
                color = WARNING_TEXT; bg = "rgba(253,238,33,0.35)"; break;
            case "cancelled": case "archived":
                color = TEXT_MUTED; bg = "rgba(119,116,155,0.15)"; break;
            default:
                color = TEXT_MUTED; bg = "transparent"; break;
        }
        badge.setTextFill(Color.web(color));
        badge.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 18;" +
            "-fx-padding: 3 10 3 10;"
        );
        return badge;
    }

    //  Quick stats side panel 
    private VBox buildQuickStats(
        int activeMembers,
        int expiredMembers,
        double posToday,
        int equipmentOk,
        int underMaintenance,
        int lowStockAlerts
    ) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);

        HBox header = new HBox();
        header.setPadding(new Insets(16, 20, 14, 20));
        header.setStyle(
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        Text t = new Text("Quick Stats");
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        t.setFill(Color.web(TEXT_WHITE));
        header.getChildren().add(t);

        VBox stats = new VBox(0);
        String[][] statItems = {
            {"Active Members",    String.valueOf(activeMembers),   ACCENT},
            {"Expired Members",   String.valueOf(expiredMembers),  WARNING_TEXT},
            {"POS Sales Today",   String.format("%.0f", posToday), SUCCESS_TEXT},
            {"Equipment OK",      String.valueOf(equipmentOk),     SUCCESS_TEXT},
            {"Under Maintenance", String.valueOf(underMaintenance), WARNING_TEXT},
            {"Low Stock Alerts",  String.valueOf(lowStockAlerts),   ACCENT},
        };

        for (int i = 0; i < statItems.length; i++) {
            String bg = (i % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 20, 12, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            Text label = new Text(statItems[i][0]);
            label.setFont(Font.font("Poppins", 11));
            label.setFill(Color.web(TEXT_MUTED));
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Text val = new Text(statItems[i][1]);
            val.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
            val.setFill(Color.web(statItems[i][2]));
            val.setStroke(Color.web(ModernDesignSystem.WHITE, 0.75));
            val.setStrokeWidth(0.25);
            row.getChildren().addAll(label, sp, val);
            stats.getChildren().add(row);
        }

        card.getChildren().addAll(header, stats);
        return card;
    }

    private VBox buildMembersDetail(MemberDAO memberDAO, int totalMembers, List<MemberDAO.MemberRecord> recentMembers) {
        String[][] rows = new String[Math.min(8, recentMembers.size())][5];
        for (int i = 0; i < rows.length; i++) {
            MemberDAO.MemberRecord m = recentMembers.get(i);
            rows[i][0] = "#" + m.memberCode();
            rows[i][1] = m.fullName();
            rows[i][2] = m.membershipType() != null ? m.membershipType() : "No plan";
            rows[i][3] = m.membershipEndDate() != null ? m.membershipEndDate().toString() : "No end date";
            rows[i][4] = formatStatusDisplay(m.status());
        }

        return buildDashboardDetailSection(
            "Total Members",
            "Member count breakdown and newest registrations",
            new String[][]{
                {"All Members", String.valueOf(totalMembers), ACCENT},
                {"Active", String.valueOf(memberDAO.countByStatus("Active")), SUCCESS_TEXT},
                {"Expired", String.valueOf(memberDAO.countByStatus("Expired")), WARNING_TEXT},
                {"Archived", String.valueOf(memberDAO.countByStatus("Cancelled")), TEXT_MUTED}
            },
            "Member Details",
            new String[]{"Member ID", "Name", "Plan", "End Date", "Status"},
            rows.length > 0 ? rows : new String[][]{{"No records", "No recent members yet", "-", "-", "Pending"}},
            true
        );
    }

    private VBox buildRevenueDetail(PaymentDAO paymentDAO, PosDAO posDAO, double totalToday) {
        java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());
        double membershipRevenue = paymentDAO.todayRevenue();
        double posRevenue = posDAO.todayPosTotal();
        List<PaymentDAO.PaymentRecord> payments = paymentDAO.findByDateRange(today, today);
        List<PosDAO.SaleDetailRow> sales = posDAO.findSaleLinesBetween(today, today);

        String[][] paymentRows = new String[Math.min(6, payments.size())][5];
        for (int i = 0; i < paymentRows.length; i++) {
            PaymentDAO.PaymentRecord p = payments.get(i);
            paymentRows[i][0] = p.memberName() != null ? p.memberName() : "Unknown member";
            paymentRows[i][1] = formatCurrency(p.amount());
            paymentRows[i][2] = p.paymentMethod() != null ? p.paymentMethod() : "Cash";
            paymentRows[i][3] = p.transactionRef() != null ? p.transactionRef() : "-";
            paymentRows[i][4] = p.status() != null ? p.status() : "Pending";
        }

        String[][] saleRows = new String[Math.min(6, sales.size())][5];
        for (int i = 0; i < saleRows.length; i++) {
            PosDAO.SaleDetailRow s = sales.get(i);
            saleRows[i][0] = s.itemName();
            saleRows[i][1] = String.valueOf(s.quantity());
            saleRows[i][2] = formatCurrency(s.unitPrice());
            saleRows[i][3] = formatCurrency(s.subtotal());
            saleRows[i][4] = s.paymentMethod() != null ? s.paymentMethod() : "Cash";
        }

        VBox section = buildDashboardDetailSection(
            "Revenue Today",
            "Membership payment and POS sales activity for today",
            new String[][]{
                {"Total", formatCurrency(totalToday), ACCENT},
                {"Membership", formatCurrency(membershipRevenue), SUCCESS_TEXT},
                {"POS Sales", formatCurrency(posRevenue), WARNING_TEXT},
                {"Payments", String.valueOf(payments.size()), TEXT_MUTED}
            },
            "Membership Payments",
            new String[]{"Member", "Amount", "Method", "Reference", "Status"},
            paymentRows.length > 0 ? paymentRows : new String[][]{{"No records", "No payments today", "-", "-", "Pending"}},
            true
        );
        section.getChildren().add(buildTableCard(
            "POS Items Sold",
            new String[]{"Item", "Qty", "Unit Price", "Subtotal", "Method"},
            saleRows.length > 0 ? saleRows : new String[][]{{"No records", "0", "PHP 0.00", "PHP 0.00", "-"}},
            false
        ));
        return section;
    }

    private VBox buildLowStockDetail(InventoryDAO inventoryDAO) {
        List<InventoryDAO.InventoryRecord> items = inventoryDAO.findLowStock();
        String[][] rows = new String[Math.min(10, items.size())][6];
        int outOfStock = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).currentStock() <= 0) outOfStock++;
        }
        for (int i = 0; i < rows.length; i++) {
            InventoryDAO.InventoryRecord item = items.get(i);
            rows[i][0] = item.itemCode() != null ? item.itemCode() : "-";
            rows[i][1] = item.itemName();
            rows[i][2] = item.category() != null ? item.category() : "-";
            rows[i][3] = String.valueOf(item.currentStock());
            rows[i][4] = String.valueOf(item.reorderLevel());
            rows[i][5] = item.status() != null ? item.status() : "Low Stock";
        }

        return buildDashboardDetailSection(
            "Low Stock Items",
            "Inventory that needs restocking or attention",
            new String[][]{
                {"Low Stock", String.valueOf(items.size()), ACCENT},
                {"Out of Stock", String.valueOf(outOfStock), WARNING_TEXT},
                {"Need Restock", String.valueOf(Math.max(0, items.size() - outOfStock)), SUCCESS_TEXT},
                {"Shown", String.valueOf(rows.length), TEXT_MUTED}
            },
            "Restock List",
            new String[]{"Code", "Item", "Category", "Stock", "Reorder", "Status"},
            rows.length > 0 ? rows : new String[][]{{"No records", "All items are above reorder level", "-", "0", "0", "Active"}},
            true
        );
    }

    private VBox buildMaintenanceDetail(EquipmentDAO equipmentDAO) {
        List<EquipmentDAO.EquipmentRecord> equipment = equipmentDAO.findMaintenanceDue();
        String[][] rows = new String[Math.min(10, equipment.size())][6];
        int maintenance = 0;
        int broken = 0;
        for (EquipmentDAO.EquipmentRecord e : equipment) {
            if ("Maintenance".equalsIgnoreCase(e.condition())) maintenance++;
            if ("Broken".equalsIgnoreCase(e.condition())) broken++;
        }
        for (int i = 0; i < rows.length; i++) {
            EquipmentDAO.EquipmentRecord e = equipment.get(i);
            rows[i][0] = e.equipmentCode() != null ? e.equipmentCode() : "-";
            rows[i][1] = e.equipmentName();
            rows[i][2] = e.category() != null ? e.category() : "-";
            rows[i][3] = e.location() != null ? e.location() : "-";
            rows[i][4] = e.nextMaintenance() != null ? e.nextMaintenance().toString() : "Not set";
            rows[i][5] = e.condition() != null ? e.condition() : "Due";
        }

        return buildDashboardDetailSection(
            "Maintenance Due",
            "Equipment with maintenance scheduled within the next 30 days",
            new String[][]{
                {"Due Soon", String.valueOf(equipment.size()), ACCENT},
                {"Maintenance", String.valueOf(maintenance), WARNING_TEXT},
                {"Broken", String.valueOf(broken), TEXT_MUTED},
                {"Shown", String.valueOf(rows.length), SUCCESS_TEXT}
            },
            "Equipment Due",
            new String[]{"Code", "Equipment", "Category", "Location", "Next Date", "Condition"},
            rows.length > 0 ? rows : new String[][]{{"No records", "No maintenance due soon", "-", "-", "Not set", "Active"}},
            true
        );
    }

    private VBox buildDashboardDetailSection(
        String title,
        String subtitle,
        String[][] metrics,
        String tableTitle,
        String[] headers,
        String[][] rows,
        boolean badgeLastColumn
    ) {
        VBox section = new VBox(16);
        section.setStyle(
            "-fx-background-color: transparent;"
        );

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(2);
        Text titleTxt = new Text(title);
        titleTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 17));
        titleTxt.setFill(Color.web(TEXT_WHITE));
        Text subtitleTxt = new Text(subtitle);
        subtitleTxt.setFont(Font.font("Poppins", 11));
        subtitleTxt.setFill(Color.web(TEXT_MUTED));
        titleBox.getChildren().addAll(titleTxt, subtitleTxt);

        Rectangle accentCut = new Rectangle(5, 42);
        accentCut.setArcWidth(5);
        accentCut.setArcHeight(5);
        accentCut.setFill(Color.web(ACCENT));
        header.getChildren().addAll(accentCut, titleBox);

        HBox metricRow = new HBox(12);
        for (String[] metric : metrics) {
            metricRow.getChildren().add(buildInlineMetric(metric[0], metric[1], metric[2]));
        }

        VBox table = buildTableCard(tableTitle, headers, rows, badgeLastColumn);
        section.getChildren().addAll(header, metricRow, table);
        return section;
    }

    private HBox buildInlineMetric(String label, String value, String color) {
        HBox metric = new HBox(10);
        metric.setAlignment(Pos.CENTER_LEFT);
        metric.setPadding(new Insets(12, 14, 12, 14));
        metric.setMinHeight(58);
        metric.setMaxWidth(Double.MAX_VALUE);
        metric.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;"
        );
        Rectangle cut = new Rectangle(4, 28);
        cut.setArcWidth(4);
        cut.setArcHeight(4);
        cut.setFill(Color.web(color));
        VBox textBox = new VBox(1);
        Text labelTxt = new Text(label);
        labelTxt.setFont(Font.font("Poppins", 10));
        labelTxt.setFill(Color.web(TEXT_MUTED));
        Text valueTxt = new Text(value);
        valueTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 15));
        valueTxt.setFill(Color.web(readableAccent(color)));
        valueTxt.setStroke(Color.web(ModernDesignSystem.WHITE, 0.65));
        valueTxt.setStrokeWidth(0.25);
        textBox.getChildren().addAll(labelTxt, valueTxt);
        metric.getChildren().addAll(cut, textBox);
        HBox.setHgrow(metric, Priority.ALWAYS);
        return metric;
    }

    private void showDashboardPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String formatCurrency(double amount) {
        return "PHP " + String.format("%,.2f", amount);
    }

    private String formatMembersThisMonth(int membersThisMonth) {
        return membersThisMonth + (membersThisMonth == 1 ? " new member this month" : " new members this month");
    }

    private String formatRevenueDelta(double todayRevenue, double yesterdayRevenue) {
        double difference = todayRevenue - yesterdayRevenue;
        if (Math.abs(difference) < 0.01) {
            return "Same as yesterday";
        }
        String prefix = difference > 0 ? "+" : "-";
        return prefix + formatCurrency(Math.abs(difference)) + " vs. yesterday";
    }

    private void showDashboardSearch(String query) {
        if (query == null || query.isBlank()) {
            new Alert(Alert.AlertType.INFORMATION, "Type a member, item, transaction ID, payment method, or reference number, then press Enter.").showAndWait();
            return;
        }

        List<SearchDAO.SearchResult> results = new SearchDAO().searchAll(query);
        if (results.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No records found for: " + query.trim()).showAndWait();
            return;
        }

        StringBuilder message = new StringBuilder();
        int limit = Math.min(8, results.size());
        for (int i = 0; i < limit; i++) {
            SearchDAO.SearchResult result = results.get(i);
            message.append(result.module())
                .append(": ")
                .append(result.title())
                .append("\n")
                .append(result.detail())
                .append("\n\n");
        }
        if (results.size() > limit) {
            message.append("Showing ").append(limit).append(" of ").append(results.size()).append(" matches. Open Search for full results.");
        }
        new Alert(Alert.AlertType.INFORMATION, message.toString()).showAndWait();
    }

    private void showDashboardNotifications(int lowStockCount, int maintDue) {
        StringBuilder message = new StringBuilder();
        if (lowStockCount > 0) {
            message.append(lowStockCount).append(" inventory item(s) need restocking.\n");
        }
        if (maintDue > 0) {
            message.append(maintDue).append(" equipment maintenance task(s) are due within 30 days.\n");
        }
        if (message.length() == 0) {
            message.append("No low-stock or maintenance alerts right now.");
        }
        new Alert(Alert.AlertType.INFORMATION, message.toString()).showAndWait();
    }

    // Helper method to format status display - convert Cancelled to Archived for consistency
    private String formatStatusDisplay(String status) {
        if (status == null) return "Unknown";
        return "Cancelled".equalsIgnoreCase(status) ? "Archived" : status;
    }

    private String readableAccent(String color) {
        if (SUCCESS.equalsIgnoreCase(color)) return SUCCESS_TEXT;
        if (WARNING.equalsIgnoreCase(color)) return WARNING_TEXT;
        return color;
    }

    private Image loadImage(String resourcePath) {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            return new WritableImage(1, 1);
        }
        return new Image(resource.toExternalForm());
    }

    public static void main(String[] args) { launch(args); }
}
