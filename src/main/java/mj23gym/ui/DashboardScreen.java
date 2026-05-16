package mj23gym.ui;

import java.time.LocalDate;
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
import mj23gym.dao.EquipmentDAO;
import mj23gym.dao.InventoryDAO;
import mj23gym.dao.MemberDAO;
import mj23gym.dao.PaymentDAO;
import mj23gym.dao.PosDAO;
import mj23gym.dao.SearchDAO;

/**
 * MJ23 Playgrind Gym – Dashboard Screen
 * Matches the official screen design color scheme:
 *   BG Main      : #1a1a2e
 *   Sidebar      : #0d1b2a
 *   Card         : #1e2a3a
 *   Accent       : #e63946
 *   Table rows   : #1e2a3a / #253545
 *   Text         : #ffffff / #b0bec5
 */
public class DashboardScreen extends Application {

    // ── Palette ────────────────────────────────────────────────────
    static final String BG_MAIN      = "#1a1a2e";
    static final String BG_SIDEBAR   = "#0d1b2a";
    static final String BG_CARD      = "#1e2a3a";
    static final String BG_ROW_ALT   = "#253545";
    static final String ACCENT       = "#e63946";
    static final String ACCENT_DARK  = "#c0303b";
    static final String TEXT_WHITE   = "#ffffff";
    static final String TEXT_MUTED   = "#b0bec5";
    static final String TEXT_DIM     = "#607080";
    static final String BORDER       = "#253545";
    static final String SUCCESS      = "#4caf50";
    static final String WARNING      = "#ff9800";
    static final String INFO         = "#2196f3";

    private String activeMenu = "Dashboard";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym – Dashboard");

        BorderPane root = new BorderPane();
        root.setPrefSize(1200, 720);
        root.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // ── SIDEBAR ────────────────────────────────────────────────
        VBox sidebar = buildSidebar(root);
        root.setLeft(sidebar);

        // ── MAIN CONTENT ───────────────────────────────────────────
        VBox mainContent = buildDashboardContent();
        root.setCenter(mainContent);

        // ── Fade in ────────────────────────────────────────────────
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

    // ══════════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════════
    private VBox buildSidebar(BorderPane root) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");

        // Top red accent
        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web(ACCENT));

        // Logo area
        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));

        StackPane logoBadge = new StackPane();
        logoBadge.setPrefSize(42, 42);
        Rectangle logoBg = new Rectangle(42, 42);
        logoBg.setArcWidth(10);
        logoBg.setArcHeight(10);
        logoBg.setFill(Color.web(ACCENT));
        Text logoTxt = new Text("MJ");
        logoTxt.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        logoTxt.setFill(Color.WHITE);
        logoBadge.getChildren().addAll(logoBg, logoTxt);

        VBox logoText = new VBox(1);
        Text gymName = new Text("MJ23 PLAYGRIND");
        gymName.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        gymName.setFill(Color.web(TEXT_WHITE));
        Text gymSub = new Text("GYM");
        gymSub.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        gymSub.setFill(Color.web(ACCENT));
        logoText.getChildren().addAll(gymName, gymSub);

        logoArea.getChildren().addAll(logoBadge, logoText);

        // Sidebar divider
        Rectangle div1 = makeSidebarDivider();

        // Section label
        Label menuLabel = makeSectionLabel("MAIN MENU");

        // Menu items
        String[][] menuItems = {
            {"🏠", "Dashboard"},
            {"👥", "Member Management"},
            {"💳", "Payment & Billing"},
            {"📦", "Inventory"},
            {"🏋", "Equipment"},
            {"🛒", "Point of Sale"},
            {"📊", "Reports"},
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
            {"⚙", "Settings"},
            {"❓", "Help"},
            {"ℹ", "About"},
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
        avatarTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        avatarTxt.setFill(Color.WHITE);
        StackPane avatarStack = new StackPane(avatar, avatarTxt);
        avatarStack.setPrefSize(36, 36);

        VBox userInfo = new VBox(2);
        Text userName = new Text("Admin");
        userName.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        userName.setFill(Color.web(TEXT_WHITE));
        Text userRole = new Text("Administrator");
        userRole.setFont(Font.font("Verdana", 10));
        userRole.setFill(Color.web(TEXT_MUTED));
        userInfo.getChildren().addAll(userName, userRole);

        Button logoutBtn = new Button("⏻");
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
        labelTxt.setFont(Font.font("Verdana", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        labelTxt.setFill(active ? Color.web(TEXT_WHITE) : Color.web(TEXT_MUTED));

        item.getChildren().addAll(activeBar, iconTxt, labelTxt);

        if (active) {
            item.setStyle(
                "-fx-background-color: " + BG_CARD + ";" +
                "-fx-background-radius: 8;"
            );
        } else {
            item.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;");
            item.setOnMouseEntered(e -> item.setStyle(
                "-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8;"
            ));
            item.setOnMouseExited(e -> item.setStyle(
                "-fx-background-color: transparent; -fx-background-radius: 8;"
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
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_DIM));
        lbl.setPadding(new Insets(4, 0, 6, 20));
        return lbl;
    }

    // ══════════════════════════════════════════════════════════════
    // DASHBOARD MAIN CONTENT
    // ══════════════════════════════════════════════════════════════
    public VBox buildDashboardContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // ── Top Bar ────────────────────────────────────────────────
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
        pgTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        pgTitle.setFill(Color.web(TEXT_WHITE));
        Text pgSub = new Text("Welcome back, " + user.displayName() + "  •  " + LocalDate.now());
        pgSub.setFont(Font.font("Verdana", 11));
        pgSub.setFill(Color.web(TEXT_MUTED));
        pageTitle.getChildren().addAll(pgTitle, pgSub);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search...");
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
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 11;"
        );

        // Notification bell
        Button notifBtn = new Button("🔔");
        notifBtn.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 50%;" +
            "-fx-font-size: 14;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 6 10 6 10;"
        );

        topBar.getChildren().addAll(pageTitle, topSpacer, searchField, notifBtn);

        // ── Scrollable body ────────────────────────────────────────
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(24);
        body.setPadding(new Insets(28, 28, 28, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // ── Fetch data from DAOs ──────────────────────────────────
        MemberDAO memberDAO = new MemberDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();
        EquipmentDAO equipmentDAO = new EquipmentDAO();
        PosDAO posDAO = new PosDAO();

        int totalMembers = memberDAO.countAll();
        double revenueToday = paymentDAO.todayRevenue() + posDAO.todayPosTotal();
        int lowStockCount = inventoryDAO.countLowStock();
        int maintDue = equipmentDAO.findMaintenanceDue().size();
        searchField.setOnAction(e -> showDashboardSearch(searchField.getText()));
        notifBtn.setOnAction(e -> showDashboardNotifications(lowStockCount, maintDue));

        List<MemberDAO.MemberRecord> recentMembers = memberDAO.findRecent(5);
        
        List<PaymentDAO.PaymentRecord> recentPayments = paymentDAO.findRecentPayments(5);

        // ── Summary Cards Row ──────────────────────────────────────
        HBox summaryCards = new HBox(18);
        summaryCards.setAlignment(Pos.CENTER_LEFT);

        String revStr = String.format("₱%.0f", revenueToday);
        summaryCards.getChildren().addAll(
            makeSummaryCard("👥", "Total Members",    String.valueOf(totalMembers),  "+5 this month",  ACCENT,   true),
            makeSummaryCard("💰", "Revenue Today",    revStr, "+₱820 vs. yesterday", SUCCESS, false),
            makeSummaryCard("📦", "Low Stock Items",  String.valueOf(lowStockCount),    "Needs restocking", WARNING, false),
            makeSummaryCard("🏋", "Maintenance Due", String.valueOf(maintDue),
                maintDue > 0 ? "Within 30 days" : "None due soon", INFO, false)
        );

        // ── Recent Activity + Quick Stats row ─────────────────────
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
            memberTableData[i][3] = m.status() != null ? m.status() : "Active";
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
            paymentTableData[i][1] = String.format("₱%.2f", p.amount());
            paymentTableData[i][2] = p.paymentMethod() != null ? p.paymentMethod() : "Cash";
            paymentTableData[i][3] = p.paymentDate() != null ? p.paymentDate().toString() : LocalDate.now().toString();
            paymentTableData[i][4] = p.status() != null ? p.status() : "Pending";
        }

        // ── Recent Payments table ──────────────────────────────────
        VBox paymentsCard = buildTableCard(
            "Recent Payments",
            new String[]{"Member", "Amount", "Method", "Date", "Status"},
            paymentTableData.length > 0 ? paymentTableData : new String[][]{
                {"Loading...", "₱0.00", "Loading...", LocalDate.now().toString(), "Loading..."}
            }
        );

        body.getChildren().addAll(summaryCards, midRow, paymentsCard);
        scrollPane.setContent(body);

        content.getChildren().addAll(topBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return content;
    }

    // ── Summary stat card ──────────────────────────────────────────
    private VBox makeSummaryCard(String icon, String label, String value,
                                  String sub, String color, boolean highlighted) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setPrefWidth(210);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + (highlighted ? color : BORDER) + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: " + (highlighted ? "0 0 0 4" : "1") + ";"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.3));
        ds.setRadius(12);
        ds.setOffsetY(4);
        card.setEffect(ds);
        HBox.setHgrow(card, Priority.ALWAYS);

        // Icon circle
        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(40, 40);
        Circle bg = new Circle(20);
        bg.setFill(Color.web(color, 0.18));
        Text iconTxt = new Text(icon);
        iconTxt.setFont(Font.font(16));
        iconCircle.getChildren().addAll(bg, iconTxt);

        Text valTxt = new Text(value);
        valTxt.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        valTxt.setFill(Color.web(TEXT_WHITE));

        Text lblTxt = new Text(label);
        lblTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        lblTxt.setFill(Color.web(TEXT_MUTED));

        Text subTxt = new Text(sub);
        subTxt.setFont(Font.font("Verdana", 10));
        subTxt.setFill(Color.web(color));

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        topRow.getChildren().addAll(new VBox(4, lblTxt, valTxt), sp, iconCircle);

        card.getChildren().addAll(topRow, subTxt);
        return card;
    }

    // ── Data table card ────────────────────────────────────────────
    private VBox buildTableCard(String title, String[] headers, String[][] rows) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
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
        titleTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        titleTxt.setFill(Color.web(TEXT_WHITE));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        cardHeader.getChildren().addAll(titleTxt, sp);

        // Table
        GridPane table = new GridPane();
        table.setPadding(new Insets(0));

        // Header row
        for (int c = 0; c < headers.length; c++) {
            Label h = new Label(headers[c].toUpperCase());
            h.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
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
                if (c == rows[r].length - 1) {
                    // Status badge
                    Label badge = makeStatusBadge(cellVal);
                    badge.setPadding(new Insets(8, 14, 8, 14));
                    badge.setMaxWidth(Double.MAX_VALUE);
                    GridPane.setHgrow(badge, Priority.ALWAYS);
                    GridPane.setFillWidth(badge, true);
                    String rowBg = bg;
                    badge.setStyle(badge.getStyle() +
                        " -fx-background-color: " + rowBg + ";");
                    table.add(badge, c, r + 1);
                } else {
                    Label cell = new Label(cellVal);
                    cell.setFont(Font.font("Verdana", 11));
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

    // ── Status badge ───────────────────────────────────────────────
    private Label makeStatusBadge(String status) {
        Label badge = new Label(status);
        badge.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        String color, bg;
        switch (status.toLowerCase()) {
            case "active": case "paid":
                color = SUCCESS; bg = "rgba(76,175,80,0.15)"; break;
            case "expired": case "overdue":
                color = ACCENT; bg = "rgba(230,57,70,0.15)"; break;
            case "pending":
                color = WARNING; bg = "rgba(255,152,0,0.15)"; break;
            default:
                color = TEXT_MUTED; bg = "transparent"; break;
        }
        badge.setTextFill(Color.web(color));
        badge.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 3 10 3 10;"
        );
        return badge;
    }

    // ── Quick stats side panel ─────────────────────────────────────
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
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
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
        t.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        t.setFill(Color.web(TEXT_WHITE));
        header.getChildren().add(t);

        VBox stats = new VBox(0);
        String[][] statItems = {
            {"Active Members",    String.valueOf(activeMembers),   ACCENT},
            {"Expired Members",   String.valueOf(expiredMembers),  WARNING},
            {"POS Sales Today",   String.format("₱%.0f", posToday), SUCCESS},
            {"Equipment OK",      String.valueOf(equipmentOk),     SUCCESS},
            {"Under Maintenance", String.valueOf(underMaintenance), WARNING},
            {"Low Stock Alerts",  String.valueOf(lowStockAlerts),   ACCENT},
        };

        for (int i = 0; i < statItems.length; i++) {
            String bg = (i % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 20, 12, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            Text label = new Text(statItems[i][0]);
            label.setFont(Font.font("Verdana", 11));
            label.setFill(Color.web(TEXT_MUTED));
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Text val = new Text(statItems[i][1]);
            val.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            val.setFill(Color.web(statItems[i][2]));
            row.getChildren().addAll(label, sp, val);
            stats.getChildren().add(row);
        }

        card.getChildren().addAll(header, stats);
        return card;
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

    public static void main(String[] args) { launch(args); }
}
