package mj23gym.ui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * MJ23 Playgrind Gym – Help Screen
 * Contains: Search help, FAQ accordion, module guides,
 * troubleshooting tips, and contact/support info.
 */
public class HelpScreen extends Application {

    static final String BG_MAIN     = "#1a1a2e";
    static final String BG_SIDEBAR  = "#0d1b2a";
    static final String BG_CARD     = "#1e2a3a";
    static final String BG_ROW_ALT  = "#253545";
    static final String ACCENT      = "#e63946";
    static final String ACCENT_DARK = "#c0303b";
    static final String TEXT_WHITE  = "#ffffff";
    static final String TEXT_MUTED  = "#b0bec5";
    static final String TEXT_DIM    = "#607080";
    static final String BORDER      = "#253545";
    static final String SUCCESS     = "#4caf50";
    static final String WARNING     = "#ff9800";
    static final String INFO        = "#2196f3";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym – Help & Support");
        BorderPane root = new BorderPane();
        root.setPrefSize(1200, 720);
        root.setStyle("-fx-background-color: " + BG_MAIN + ";");
        root.setLeft(buildSidebar());
        root.setCenter(buildContent());
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
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230); sidebar.setMinWidth(230); sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");

        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web(ACCENT));

        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane badge = new StackPane(); badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42);
        bb.setArcWidth(10); bb.setArcHeight(10); bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ"); bt.setFont(Font.font("Georgia", FontWeight.BOLD, 16)); bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND"); l1.setFont(Font.font("Georgia", FontWeight.BOLD, 11)); l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM"); l2.setFont(Font.font("Georgia", FontWeight.BOLD, 11)); l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(badge, lt);

        String[][] items = {
            {"🏠","Dashboard"},{"👥","Member Management"},{"💳","Payment & Billing"},
            {"📦","Inventory"},{"🏋","Equipment"},{"🛒","Point of Sale"},{"📊","Reports"}
        };
        VBox menu = new VBox(2); menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], false));

        String[][] sys = {{"⚙","Settings"},{"❓","Help"},{"ℹ","About"}};
        VBox sysMenu = new VBox(2); sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys)
            sysMenu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Help")));

        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        Rectangle d1 = new Rectangle(230, 1); d1.setFill(Color.web(BORDER));
        Rectangle d2 = new Rectangle(230, 1); d2.setFill(Color.web(BORDER));

        sidebar.getChildren().addAll(
            topAccent, logoArea, d1, makeSecLbl("MAIN MENU"), menu,
            d2, makeSecLbl("SYSTEM"), sysMenu, sp
        );
        return sidebar;
    }

    private HBox buildMenuItem(String icon, String label, boolean active) {
        HBox item = new HBox(12); item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(11, 16, 11, 16)); item.setCursor(javafx.scene.Cursor.HAND);
        Rectangle bar = new Rectangle(3, 36); bar.setArcWidth(3); bar.setArcHeight(3);
        bar.setFill(active ? Color.web(ACCENT) : Color.TRANSPARENT);
        Text ico = new Text(icon); ico.setFont(Font.font(14));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Verdana", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        lbl.setFill(active ? Color.web(TEXT_WHITE) : Color.web(TEXT_MUTED));
        item.getChildren().addAll(bar, ico, lbl);
        item.setStyle(active
            ? "-fx-background-color: " + BG_CARD + "; -fx-background-radius: 8;"
            : "-fx-background-color: transparent; -fx-background-radius: 8;");
        if (!active) {
            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;"));
        }
        return item;
    }

    private Label makeSecLbl(String t) {
        Label l = new Label(t); l.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_DIM)); l.setPadding(new Insets(8, 0, 6, 20)); return l;
    }

    // ══════════════════════════════════════════════════════════════
    // MAIN CONTENT
    // ══════════════════════════════════════════════════════════════
    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Top bar
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text t1 = new Text("Help & Support");
        t1.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Guides, FAQs, and troubleshooting for the MJ23 Management System");
        t2.setFont(Font.font("Verdana", 11));
        t2.setFill(Color.web(TEXT_MUTED));
        pg.getChildren().addAll(t1, t2);
        topBar.getChildren().add(pg);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(24);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // ── Hero Search Banner ─────────────────────────────────────
        VBox heroBanner = new VBox(14);
        heroBanner.setAlignment(Pos.CENTER);
        heroBanner.setPadding(new Insets(36, 40, 36, 40));
        heroBanner.setStyle(
            "-fx-background-color: linear-gradient(to right, " + BG_SIDEBAR + ", " + BG_CARD + ");" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 0 0 0 4;");
        DropShadow heroDs = new DropShadow(); heroDs.setColor(Color.web("#000", 0.35)); heroDs.setRadius(16); heroDs.setOffsetY(6);
        heroBanner.setEffect(heroDs);

        Text heroIcon = new Text("❓");
        heroIcon.setFont(Font.font(40));

        Text heroTitle = new Text("How can we help you?");
        heroTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        heroTitle.setFill(Color.web(TEXT_WHITE));
        heroTitle.setTextAlignment(TextAlignment.CENTER);

        Text heroSub = new Text("Search our help documentation or browse the topics below");
        heroSub.setFont(Font.font("Verdana", 12));
        heroSub.setFill(Color.web(TEXT_MUTED));
        heroSub.setTextAlignment(TextAlignment.CENTER);

        // Search bar
        HBox searchBox = new HBox(0);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setMaxWidth(560);
        TextField searchField = new TextField();
        searchField.setPromptText("Search help topics, e.g. 'how to add a member'...");
        searchField.setPrefHeight(46);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + " transparent " + BORDER + " " + BORDER + ";" +
            "-fx-border-radius: 10 0 0 10;" +
            "-fx-background-radius: 10 0 0 10;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 14 0 14;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;");
        Button searchBtn = new Button("Search");
        searchBtn.setPrefHeight(46);
        searchBtn.setPadding(new Insets(0, 22, 0, 22));
        searchBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        searchBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 0 10 10 0;" +
            "-fx-cursor: hand;");
        searchBtn.setOnMouseEntered(e -> searchBtn.setStyle(
            "-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 0 10 10 0; -fx-cursor: hand;"));
        searchBtn.setOnMouseExited(e -> searchBtn.setStyle(
            "-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 0 10 10 0; -fx-cursor: hand;"));
        searchBox.getChildren().addAll(searchField, searchBtn);

        heroBanner.getChildren().addAll(heroIcon, heroTitle, heroSub, searchBox);

        // ── Quick Topic Cards ──────────────────────────────────────
        HBox topicRow = new HBox(14);
        String[][] topics = {
            {"👥", "Members",     "Adding, editing, and\nmanaging members",       "#2196f3"},
            {"💳", "Payments",    "Processing dues, billing\nand receipts",        "#4caf50"},
            {"📦", "Inventory",   "Managing stock levels\nand item tracking",      "#ff9800"},
            {"🏋", "Equipment",   "Equipment records and\nmaintenance scheduling", "#9c27b0"},
            {"🛒", "POS",         "Point of sale transactions\nand sales records", "#e63946"},
            {"📊", "Reports",     "Generating and exporting\nsystem reports",      "#00bcd4"},
        };
        for (String[] topic : topics) {
            VBox topicCard = buildTopicCard(topic[0], topic[1], topic[2], topic[3]);
            HBox.setHgrow(topicCard, Priority.ALWAYS);
            topicRow.getChildren().add(topicCard);
        }

        // ── Two-column: FAQ + Module Guide ─────────────────────────
        HBox midRow = new HBox(22);

        // LEFT: FAQ Accordion
        VBox faqCard = buildSectionCard("💬  Frequently Asked Questions", "Common questions and answers");
        Accordion faqAccordion = new Accordion();
        faqAccordion.setStyle("-fx-background-color: transparent;");

        String[][] faqs = {
            {
                "How do I add a new gym member?",
                "Go to Member Management from the sidebar, then click the '＋ Add Member' button in the top-right. Fill in the member's personal details including name, phone, email, plan type, and date of registration. Click 'Save Member' to confirm."
            },
            {
                "How do I process a membership payment?",
                "Navigate to Payment & Billing. Use the member search to find the member, select a payment method (Cash, GCash, or Bank Transfer), enter the reference number for electronic payments after confirming the transaction, then click 'Process Payment'."
            },
            {
                "How do I add or update inventory items?",
                "Go to Inventory from the sidebar. Click '＋ Add Item' to register a new product, or click the edit (✏) icon on any existing item to update its quantity, price, or details."
            },
            {
                "How do I schedule equipment maintenance?",
                "Open the Equipment module. Find the equipment in the table and click the wrench (🔧) icon to update its condition and set a new maintenance date."
            },
            {
                "How do I generate a sales or payment report?",
                "Go to Reports & Analytics. Select the report type (Sales, Payment, or Inventory), set the date range and filters, then click 'Generate Report'. You can also export it as a PDF."
            },
            {
                "How do I change my password?",
                "Go to Profile, find the 'Change Password' section, enter your current password and your new password, then click 'Update Password'."
            },
            {
                "How do I add a new staff account?",
                "Go to Settings > Access Control. Click '＋ Add Staff Account', fill in the staff details and assign a role, then save."
            },
            {
                "What payment methods are supported?",
                "The system supports three payment methods: Cash (in-person), GCash (digital wallet), and Bank Transfer. For GCash and bank transfers, staff must manually verify the transaction and enter the reference number."
            },
        };

        for (String[] faq : faqs) {
            TitledPane tp = new TitledPane();
            tp.setStyle(
                "-fx-background-color: " + BG_MAIN + ";" +
                "-fx-text-fill: " + TEXT_WHITE + ";" +
                "-fx-font-family: Verdana;" +
                "-fx-font-size: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;");
            tp.setText(faq[0]);

            Label answerLbl = new Label(faq[1]);
            answerLbl.setFont(Font.font("Verdana", 11));
            answerLbl.setTextFill(Color.web(TEXT_MUTED));
            answerLbl.setWrapText(true);
            answerLbl.setPadding(new Insets(12, 16, 12, 16));
            answerLbl.setStyle("-fx-background-color: " + BG_ROW_ALT + ";");

            tp.setContent(answerLbl);
            tp.setExpanded(false);
            faqAccordion.getPanes().add(tp);
        }

        // Expand first FAQ by default
        if (!faqAccordion.getPanes().isEmpty()) {
            faqAccordion.setExpandedPane(faqAccordion.getPanes().get(0));
        }

        faqCard.getChildren().add(faqAccordion);
        HBox.setHgrow(faqCard, Priority.ALWAYS);

        // RIGHT: Module Guide + Troubleshooting
        VBox rightCol = new VBox(22);
        rightCol.setMinWidth(300);
        rightCol.setMaxWidth(320);

        // Quick Guide Card
        VBox guideCard = buildSectionCard("📖  Module Quick Guide", "Overview of each system module");
        String[][] modules = {
            {"🏠", "Dashboard",        "Overview, stats, recent activity"},
            {"👥", "Member Mgmt.",     "Add, edit, view all members"},
            {"💳", "Payment & Billing","Process dues and track payments"},
            {"📦", "Inventory",        "Manage supplies and stock"},
            {"🏋", "Equipment",        "Track and maintain gym equipment"},
            {"🛒", "POS",              "Process product sales"},
            {"📊", "Reports",          "Generate and export reports"},
            {"⚙",  "Settings",         "System config and security"},
        };
        VBox moduleList = new VBox(0);
        moduleList.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;");
        for (int i = 0; i < modules.length; i++) {
            String bg = (i % 2 == 0) ? BG_MAIN : BG_ROW_ALT;
            HBox mRow = new HBox(12); mRow.setPadding(new Insets(10, 14, 10, 14));
            mRow.setStyle("-fx-background-color: " + bg + ";");
            mRow.setAlignment(Pos.CENTER_LEFT);
            Text mIco = new Text(modules[i][0]); mIco.setFont(Font.font(15));
            VBox mInfo = new VBox(2);
            Label mName = new Label(modules[i][1]);
            mName.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            mName.setTextFill(Color.web(TEXT_WHITE));
            Label mDesc = new Label(modules[i][2]);
            mDesc.setFont(Font.font("Verdana", 10));
            mDesc.setTextFill(Color.web(TEXT_DIM));
            mInfo.getChildren().addAll(mName, mDesc);
            mRow.getChildren().addAll(mIco, mInfo);
            moduleList.getChildren().add(mRow);
        }
        guideCard.getChildren().add(moduleList);
        rightCol.getChildren().add(guideCard);

        // Troubleshooting Card
        VBox troubleCard = buildSectionCard("🔧  Troubleshooting", "Common issues and solutions");
        String[][] issues = {
            {"Login not working",      "Check your username/password. Contact your admin to reset credentials if needed."},
            {"Data not saving",        "Ensure all required fields are filled. Check your database connection (MySQL)."},
            {"Reports not generating", "Verify the selected date range has data. Check that filters are set correctly."},
            {"Inventory not updating", "Confirm the item was saved. Refresh the screen or check for input errors."},
        };
        VBox issueList = new VBox(10);
        for (String[] issue : issues) {
            VBox issueBox = new VBox(5);
            issueBox.setPadding(new Insets(12, 14, 12, 14));
            issueBox.setStyle(
                "-fx-background-color: " + BG_MAIN + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-border-width: 0 0 0 3;");
            // change left border per issue to warning color
            HBox issueHdr = new HBox(8); issueHdr.setAlignment(Pos.CENTER_LEFT);
            Text issueIco = new Text("⚠"); issueIco.setFont(Font.font(13)); issueIco.setFill(Color.web(WARNING));
            Label issueTitle = new Label(issue[0]);
            issueTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            issueTitle.setTextFill(Color.web(TEXT_WHITE));
            issueHdr.getChildren().addAll(issueIco, issueTitle);
            Label issueSol = new Label(issue[1]);
            issueSol.setFont(Font.font("Verdana", 10));
            issueSol.setTextFill(Color.web(TEXT_MUTED));
            issueSol.setWrapText(true);
            issueBox.getChildren().addAll(issueHdr, issueSol);
            issueList.getChildren().add(issueBox);
        }
        troubleCard.getChildren().add(issueList);
        rightCol.getChildren().add(troubleCard);

        midRow.getChildren().addAll(faqCard, rightCol);

        // ── Contact / Support Card ─────────────────────────────────
        VBox contactCard = new VBox(20);
        contactCard.setPadding(new Insets(28, 32, 28, 32));
        contactCard.setAlignment(Pos.CENTER);
        contactCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;");
        DropShadow cDs = new DropShadow(); cDs.setColor(Color.web("#000", 0.25)); cDs.setRadius(12); cDs.setOffsetY(4);
        contactCard.setEffect(cDs);

        Text ctTitle = new Text("Still need help?");
        ctTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        ctTitle.setFill(Color.web(TEXT_WHITE));
        ctTitle.setTextAlignment(TextAlignment.CENTER);

        Text ctSub = new Text("If you can't find what you're looking for in the guides above,\ncontact your system administrator or the development team.");
        ctSub.setFont(Font.font("Verdana", 11));
        ctSub.setFill(Color.web(TEXT_MUTED));
        ctSub.setTextAlignment(TextAlignment.CENTER);

        HBox contactInfo = new HBox(20);
        contactInfo.setAlignment(Pos.CENTER);
        contactInfo.getChildren().addAll(
            makeContactChip("📧", "Email",    "group3@tip.edu.ph"),
            makeContactChip("🏫", "School",   "TIP – Quezon City"),
            makeContactChip("📚", "Subject",  "CS 301 – SE 1"),
            makeContactChip("👨‍💻", "Dev Team", "Group 3 – CS31S4")
        );

        // System version info
        HBox versionRow = new HBox(24);
        versionRow.setAlignment(Pos.CENTER);
        String[] versionItems = {"v1.0.0", "Java 17 + JavaFX", "MySQL 8", "© 2025 MJ23 Playgrind Gym"};
        for (String vi : versionItems) {
            Label vLbl = new Label(vi);
            vLbl.setFont(Font.font("Verdana", 10));
            vLbl.setTextFill(Color.web(TEXT_DIM));
            versionRow.getChildren().add(vLbl);
        }

        contactCard.getChildren().addAll(ctTitle, ctSub, contactInfo, versionRow);

        body.getChildren().addAll(heroBanner, topicRow, midRow, contactCard);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(400), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    // ── Helpers ────────────────────────────────────────────────────
    private VBox buildTopicCard(String icon, String title, String desc, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18, 16, 18, 16));
        card.setAlignment(Pos.TOP_LEFT);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 0 0 0 4;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(10); ds.setOffsetY(4); card.setEffect(ds);

        // Icon circle
        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(44, 44); iconCircle.setMaxSize(44, 44);
        Circle bg = new Circle(22); bg.setFill(Color.web(color, 0.18));
        Text ico = new Text(icon); ico.setFont(Font.font(20));
        iconCircle.getChildren().addAll(bg, ico);

        Text titleTxt = new Text(title);
        titleTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        titleTxt.setFill(Color.web(TEXT_WHITE));

        Text descTxt = new Text(desc);
        descTxt.setFont(Font.font("Verdana", 10));
        descTxt.setFill(Color.web(TEXT_MUTED));
        descTxt.setWrappingWidth(120);

        card.getChildren().addAll(iconCircle, titleTxt, descTxt);
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: " + BG_ROW_ALT + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 0 0 0 4;"));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 0 0 0 4;"));
        return card;
    }

    private VBox buildSectionCard(String title, String sub) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(10); ds.setOffsetY(4); card.setEffect(ds);
        Text t = new Text(title); t.setFont(Font.font("Verdana", FontWeight.BOLD, 14)); t.setFill(Color.web(TEXT_WHITE));
        Text s = new Text(sub); s.setFont(Font.font("Verdana", 11)); s.setFill(Color.web(TEXT_MUTED));
        Rectangle ul = new Rectangle(40, 2); ul.setFill(Color.web(ACCENT)); ul.setArcWidth(2); ul.setArcHeight(2);
        card.getChildren().addAll(new VBox(3, t, s, ul));
        return card;
    }

    private VBox makeContactChip(String icon, String label, String value) {
        VBox chip = new VBox(6);
        chip.setAlignment(Pos.CENTER);
        chip.setPadding(new Insets(14, 22, 14, 22));
        chip.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;");
        Text ico = new Text(icon); ico.setFont(Font.font(20));
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_DIM));
        Label val = new Label(value);
        val.setFont(Font.font("Verdana", 11));
        val.setTextFill(Color.web(TEXT_WHITE));
        chip.getChildren().addAll(ico, lbl, val);
        return chip;
    }

    public static void main(String[] args) { launch(args); }
}
