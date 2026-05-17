package mj23gym.ui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
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
 * MJ23 Playgrind Gym — Help Screen
 * Search help, FAQ accordion, module guides, troubleshooting, and contact info.
 */
public class HelpScreen extends Application {

    static final String BG_MAIN       = ModernDesignSystem.BG_LIGHT;
    static final String BG_SIDEBAR    = ModernDesignSystem.SIDEBAR_BG;
    static final String BG_CARD       = ModernDesignSystem.CARD_BG;
    static final String BG_ROW_ALT    = ModernDesignSystem.HOVER_EFFECT;
    static final String ACCENT        = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK   = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_TITLE    = ModernDesignSystem.PRIMARY;
    static final String TEXT_SOFT     = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DIM      = ModernDesignSystem.DARK_GRAY;
    static final String BORDER        = ModernDesignSystem.BORDER_COLOR;
    static final String SUCCESS_TEXT  = "#237A36";
    static final String WARNING_TEXT  = "#6E6400";
    static final String CARD_SURFACE  = ModernDesignSystem.WHITE;
    static final String BRAND_YELLOW  = "#FDEE21";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym — Help & Support");
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

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");

        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web(BRAND_YELLOW));

        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane badge = new StackPane();
        badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42);
        bb.setArcWidth(10);
        bb.setArcHeight(10);
        bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ");
        bt.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND");
        l1.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        l1.setFill(Color.web(TEXT_TITLE));
        Text l2 = new Text("GYM");
        l2.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(badge, lt);

        String[][] items = {
            {"", "Dashboard"}, {"", "Member Management"}, {"", "Payment & Billing"},
            {"", "Inventory"}, {"", "Equipment"}, {"", "Point of Sale"}, {"", "Reports"}
        };
        VBox menu = new VBox(2);
        menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items) {
            menu.getChildren().add(buildMenuItem(it[0], it[1], false));
        }

        String[][] sys = {{"", "Settings"}, {"", "Help"}, {"", "About"}};
        VBox sysMenu = new VBox(2);
        sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys) {
            sysMenu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Help")));
        }

        Region sp = new Region();
        VBox.setVgrow(sp, Priority.ALWAYS);
        Rectangle d1 = new Rectangle(230, 1);
        d1.setFill(Color.web(BORDER));
        Rectangle d2 = new Rectangle(230, 1);
        d2.setFill(Color.web(BORDER));

        sidebar.getChildren().addAll(
            topAccent, logoArea, d1, makeSecLbl("MAIN MENU"), menu,
            d2, makeSecLbl("SYSTEM"), sysMenu, sp
        );
        return sidebar;
    }

    private HBox buildMenuItem(String icon, String label, boolean active) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(11, 16, 11, 16));
        item.setCursor(javafx.scene.Cursor.HAND);
        Rectangle bar = new Rectangle(3, 36);
        bar.setArcWidth(3);
        bar.setArcHeight(3);
        bar.setFill(active ? Color.web(ACCENT) : Color.TRANSPARENT);
        Text ico = new Text(icon);
        ico.setFont(Font.font(14));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        lbl.setFill(active ? Color.web(TEXT_TITLE) : Color.web(TEXT_SOFT));
        item.getChildren().addAll(bar, ico, lbl);
        item.setStyle(active
            ? "-fx-background-color: " + BG_CARD + "; -fx-background-radius: 16;"
            : "-fx-background-color: transparent; -fx-background-radius: 16;");
        if (!active) {
            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 16;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent; -fx-background-radius: 16;"));
        }
        return item;
    }

    private Label makeSecLbl(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_DIM));
        l.setPadding(new Insets(8, 0, 6, 20));
        return l;
    }

    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");
        final Accordion[] faqRef = new Accordion[1];

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        VBox pg = new VBox(2);
        Text t1 = new Text("Help & Support");
        t1.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        t1.setFill(Color.web(TEXT_TITLE));
        Text t2 = new Text("Guides, FAQs, and troubleshooting for the MJ23 Management System");
        t2.setFont(Font.font("Poppins", 11));
        t2.setFill(Color.web(TEXT_SOFT));
        pg.getChildren().addAll(t1, t2);
        topBar.getChildren().add(pg);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(22);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Hero search banner
        VBox heroBanner = new VBox(16);
        heroBanner.setAlignment(Pos.CENTER);
        heroBanner.setPadding(new Insets(32, 36, 32, 36));
        heroBanner.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;"
        );
        DropShadow heroDs = new DropShadow();
        heroDs.setColor(Color.web(ACCENT, 0.12));
        heroDs.setRadius(14);
        heroDs.setOffsetY(4);
        heroBanner.setEffect(heroDs);

        StackPane heroIcon = iconBadge("?", TEXT_TITLE, "rgba(26,19,99,0.10)", 52);

        Text heroTitle = new Text("How can we help you?");
        heroTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 24));
        heroTitle.setFill(Color.web(TEXT_TITLE));
        heroTitle.setTextAlignment(TextAlignment.CENTER);

        Text heroSub = new Text("Search our help documentation or browse the topics below");
        heroSub.setFont(Font.font("Poppins", 12));
        heroSub.setFill(Color.web(TEXT_SOFT));
        heroSub.setTextAlignment(TextAlignment.CENTER);

        HBox searchBox = new HBox(0);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setMaxWidth(560);
        TextField searchField = new TextField();
        searchField.setPromptText("Search help topics, e.g. 'add member' or 'backup'...");
        searchField.setPrefHeight(44);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        applyFieldStyle(searchField);
        searchField.setStyle(searchField.getStyle() + "-fx-border-radius: 12 0 0 12; -fx-background-radius: 12 0 0 12;");

        Button searchBtn = accentButton("Search");
        searchBtn.setPrefHeight(44);
        searchBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 0 12 12 0;" +
            "-fx-border-radius: 0 12 12 0;" +
            "-fx-cursor: hand;"
        );
        searchBtn.setOnMouseEntered(e -> searchBtn.setStyle(
            "-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 0 12 12 0; -fx-cursor: hand;"
        ));
        searchBtn.setOnMouseExited(e -> searchBtn.setStyle(
            "-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 0 12 12 0; -fx-cursor: hand;"
        ));
        searchBtn.setOnAction(e -> searchHelpTopic(searchField.getText(), faqRef[0]));
        searchField.setOnAction(e -> searchHelpTopic(searchField.getText(), faqRef[0]));
        searchBox.getChildren().addAll(searchField, searchBtn);

        heroBanner.getChildren().addAll(heroIcon, heroTitle, heroSub, searchBox);

        // Quick topic cards
        FlowPane topicRow = new FlowPane(14, 14);
        topicRow.setPrefWrapLength(1120);
        String[][] topics = {
            {"M",  "Members",   "Adding, editing, and\nmanaging members",       TEXT_TITLE, "rgba(26,19,99,0.10)"},
            {"P",  "Payments",  "Processing dues, billing\nand receipts",          SUCCESS_TEXT, "rgba(228,255,223,0.75)"},
            {"I",  "Inventory", "Managing stock levels\nand item tracking",        WARNING_TEXT, "rgba(253,238,33,0.35)"},
            {"E",  "Equipment", "Equipment records and\nmaintenance scheduling", TEXT_TITLE, "rgba(119,116,155,0.14)"},
            {"$",  "POS",       "Point of sale transactions\nand sales records", TEXT_TITLE, "rgba(26,19,99,0.08)"},
            {"?",  "Search",    "Find members, inventory,\nand transactions",    TEXT_SOFT, "rgba(119,116,155,0.12)"},
            {"R",  "Reports",   "Generating and exporting\nsystem reports",      SUCCESS_TEXT, "rgba(228,255,223,0.65)"},
        };
        for (String[] topic : topics) {
            VBox topicCard = buildTopicCard(topic[0], topic[1], topic[2], topic[3], topic[4]);
            topicCard.setOnMouseClicked(e -> searchHelpTopic(topic[1], faqRef[0]));
            topicCard.setPrefWidth(148);
            topicCard.setMinWidth(140);
            topicRow.getChildren().add(topicCard);
        }

        HBox midRow = new HBox(20);

        VBox faqCard = buildSectionCard("Frequently Asked Questions", "Common questions and answers");
        Accordion faqAccordion = new Accordion();
        faqAccordion.setStyle("-fx-background-color: transparent;");
        faqRef[0] = faqAccordion;

        String[][] faqs = {
            {
                "How do I add a new gym member?",
                "Go to Member Management from the sidebar, then click the Add Member button in the top-right. Fill in the member's personal details including name, phone, email, plan type, and date of registration. Click Save Member to confirm."
            },
            {
                "How do I process a membership payment?",
                "Navigate to Payment & Billing. Use the member search to find the member, select a payment method (Cash, GCash, or Bank Transfer), enter the reference number for electronic payments after confirming the transaction, then click Process Payment."
            },
            {
                "How do I add or update inventory items?",
                "Go to Inventory from the sidebar. Click Add Item to register a new product, or click Edit on any existing item to update its quantity, price, or details."
            },
            {
                "How do I schedule equipment maintenance?",
                "Open the Equipment module. Find the equipment in the table and click Maint to update its condition and set a new maintenance date."
            },
            {
                "How do I generate a sales or payment report?",
                "Go to Reports & Analytics. Select the report type (Sales, Payment, or Inventory), set the date range and filters, then click Generate Report. You can also save it from Generated Reports."
            },
            {
                "How do I change my password?",
                "Go to Profile, find the Change Password section, enter your current password and your new password, then click Update Password."
            },
            {
                "How do I add a new staff account?",
                "Admin users can open Registration/Verification, enter the staff username, name, email, phone, and password, then verify the staff account before login is allowed."
            },
            {
                "How do I search records?",
                "Open the Search module. Type a member name, member code, inventory item, transaction ID, or reference number, then use the module filters to narrow results."
            },
            {
                "How do I back up or restore system data?",
                "Admin users can open Maintenance, click Create Backup to save a database copy, or Restore Backup to recover from a generated SQL backup file."
            },
            {
                "What payment methods are supported?",
                "The system supports Cash, GCash, and Bank Transfer. For GCash and bank transfers, staff must verify the transaction and enter the reference number."
            },
        };

        for (String[] faq : faqs) {
            TitledPane tp = new TitledPane();
            tp.setText(faq[0]);
            tp.setStyle(
                "-fx-background-color: " + CARD_SURFACE + ";" +
                "-fx-text-fill: " + TEXT_TITLE + ";" +
                "-fx-font-family: Poppins;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12;"
            );

            Label answerLbl = new Label(faq[1]);
            answerLbl.setFont(Font.font("Poppins", 11));
            answerLbl.setTextFill(Color.web(TEXT_SOFT));
            answerLbl.setWrapText(true);
            answerLbl.setPadding(new Insets(14, 16, 14, 16));
            answerLbl.setMaxWidth(Double.MAX_VALUE);
            answerLbl.setStyle("-fx-background-color: " + BG_ROW_ALT + ";");

            tp.setContent(answerLbl);
            tp.setExpanded(false);
            faqAccordion.getPanes().add(tp);
        }
        if (!faqAccordion.getPanes().isEmpty()) {
            faqAccordion.setExpandedPane(faqAccordion.getPanes().get(0));
        }
        faqCard.getChildren().add(faqAccordion);
        HBox.setHgrow(faqCard, Priority.ALWAYS);

        VBox rightCol = new VBox(20);
        rightCol.setMinWidth(300);
        rightCol.setMaxWidth(340);

        VBox guideCard = buildSectionCard("Module Quick Guide", "Overview of each system module");
        String[][] modules = {
            {"DB", "Dashboard",         "Overview, stats, recent activity"},
            {"MM", "Member Mgmt.",      "Add, edit, view all members"},
            {"PY", "Payment & Billing", "Process dues and track payments"},
            {"IN", "Inventory",         "Manage supplies and stock"},
            {"EQ", "Equipment",         "Track and maintain gym equipment"},
            {"PO", "POS",               "Process product sales"},
            {"SR", "Search",            "Find records across modules"},
            {"RP", "Reports",           "Generate and export reports"},
            {"MT", "Maintenance",       "Backup, restore, and tools"},
            {"ST", "Settings",          "System config and security"},
        };
        VBox moduleList = new VBox(0);
        moduleList.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;"
        );
        for (int i = 0; i < modules.length; i++) {
            String bg = (i % 2 == 0) ? CARD_SURFACE : BG_ROW_ALT;
            HBox mRow = new HBox(12);
            mRow.setPadding(new Insets(10, 14, 10, 14));
            mRow.setStyle("-fx-background-color: " + bg + ";");
            mRow.setAlignment(Pos.CENTER_LEFT);
            StackPane mIco = iconBadge(modules[i][0], TEXT_TITLE, "rgba(26,19,99,0.08)", 36);
            VBox mInfo = new VBox(2);
            Label mName = new Label(modules[i][1]);
            mName.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
            mName.setTextFill(Color.web(TEXT_TITLE));
            Label mDesc = new Label(modules[i][2]);
            mDesc.setFont(Font.font("Poppins", 10));
            mDesc.setTextFill(Color.web(TEXT_SOFT));
            mDesc.setWrapText(true);
            mInfo.getChildren().addAll(mName, mDesc);
            HBox.setHgrow(mInfo, Priority.ALWAYS);
            mRow.getChildren().addAll(mIco, mInfo);
            moduleList.getChildren().add(mRow);
        }
        guideCard.getChildren().add(moduleList);
        rightCol.getChildren().add(guideCard);

        VBox troubleCard = buildSectionCard("Troubleshooting", "Common issues and solutions");
        VBox issueList = new VBox(10);
        String[][] issues = {
            {"Login not working",       "Check your username/password. Contact your admin to reset credentials if needed."},
            {"Data not saving",         "Ensure all required fields are filled. Check your database connection (MySQL)."},
            {"Reports not generating",  "Verify the selected date range has data. Check that filters are set correctly."},
            {"Inventory not updating",  "Confirm the item was saved. Refresh the screen or check for input errors."},
            {"Search has no results",   "Try a shorter keyword, remove filters, or search by ID/reference number."},
            {"Backup or restore failed","Confirm MySQL is running and the restore file was generated by this system."},
        };
        for (String[] issue : issues) {
            VBox issueBox = new VBox(8);
            issueBox.setPadding(new Insets(12, 14, 12, 14));
            issueBox.setStyle(
                "-fx-background-color: " + BG_ROW_ALT + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + WARNING_TEXT + ";" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 0 0 0 3;"
            );
            HBox issueHdr = new HBox(10);
            issueHdr.setAlignment(Pos.CENTER_LEFT);
            StackPane issueIco = iconBadge("!", WARNING_TEXT, "rgba(253,238,33,0.35)", 28);
            Label issueTitle = new Label(issue[0]);
            issueTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
            issueTitle.setTextFill(Color.web(TEXT_TITLE));
            issueHdr.getChildren().addAll(issueIco, issueTitle);
            Label issueSol = new Label(issue[1]);
            issueSol.setFont(Font.font("Poppins", 10));
            issueSol.setTextFill(Color.web(TEXT_SOFT));
            issueSol.setWrapText(true);
            issueBox.getChildren().addAll(issueHdr, issueSol);
            issueList.getChildren().add(issueBox);
        }
        troubleCard.getChildren().add(issueList);
        rightCol.getChildren().add(troubleCard);

        midRow.getChildren().addAll(faqCard, rightCol);

        // Contact card
        VBox contactCard = new VBox(18);
        contactCard.setPadding(new Insets(28, 32, 28, 32));
        contactCard.setAlignment(Pos.CENTER);
        contactCard.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;"
        );
        DropShadow cDs = new DropShadow();
        cDs.setColor(Color.web(ACCENT, 0.10));
        cDs.setRadius(12);
        cDs.setOffsetY(4);
        contactCard.setEffect(cDs);

        Rectangle contactBar = new Rectangle(42, 3);
        contactBar.setArcWidth(3);
        contactBar.setArcHeight(3);
        contactBar.setFill(Color.web(ACCENT));

        Text ctTitle = new Text("Still need help?");
        ctTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        ctTitle.setFill(Color.web(TEXT_TITLE));
        ctTitle.setTextAlignment(TextAlignment.CENTER);

        Text ctSub = new Text("If you can't find what you're looking for above,\ncontact your system administrator or the development team.");
        ctSub.setFont(Font.font("Poppins", 11));
        ctSub.setFill(Color.web(TEXT_SOFT));
        ctSub.setTextAlignment(TextAlignment.CENTER);

        HBox emailRow = new HBox(10);
        emailRow.setAlignment(Pos.CENTER);
        StackPane mailIcon = iconBadge("@", TEXT_TITLE, "rgba(26,19,99,0.08)", 40);
        Text email = new Text("jameslyfiguracion@gmail.com");
        email.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        email.setFill(Color.web(TEXT_TITLE));
        emailRow.getChildren().addAll(mailIcon, email);

        HBox versionRow = new HBox(20);
        versionRow.setAlignment(Pos.CENTER);
        for (String vi : new String[] {"v1.0.0", "MJ23 Playgrind Gym"}) {
            Label vLbl = new Label(vi);
            vLbl.setFont(Font.font("Poppins", 10));
            vLbl.setTextFill(Color.web(TEXT_SOFT));
            vLbl.setStyle(
                "-fx-background-color: " + BG_ROW_ALT + ";" +
                "-fx-padding: 4 10;" +
                "-fx-background-radius: 10;"
            );
            versionRow.getChildren().add(vLbl);
        }

        contactCard.getChildren().addAll(contactBar, ctTitle, ctSub, emailRow, versionRow);

        body.getChildren().addAll(heroBanner, topicRow, midRow, contactCard);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(400), body);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return content;
    }

    private StackPane iconBadge(String glyph, String textColor, String bgColor, double size) {
        StackPane stack = new StackPane();
        stack.setPrefSize(size, size);
        stack.setMaxSize(size, size);
        Circle bg = new Circle(size / 2);
        bg.setFill(Color.web(bgColor));
        Text text = new Text(glyph);
        double fontSize = glyph.length() > 2 ? 10 : (glyph.length() > 1 ? 12 : 18);
        text.setFont(Font.font("Poppins", FontWeight.BOLD, fontSize));
        text.setFill(Color.web(textColor));
        stack.getChildren().addAll(bg, text);
        return stack;
    }

    private VBox buildTopicCard(String glyph, String title, String desc, String accentColor, String bgTint) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18, 16, 18, 16));
        card.setAlignment(Pos.TOP_LEFT);
        card.setCursor(javafx.scene.Cursor.HAND);
        String baseStyle =
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;";
        card.setStyle(baseStyle);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.08));
        ds.setRadius(10);
        ds.setOffsetY(3);
        card.setEffect(ds);

        StackPane iconCircle = iconBadge(glyph, accentColor, bgTint, 48);

        Text titleTxt = new Text(title);
        titleTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        titleTxt.setFill(Color.web(TEXT_TITLE));

        Text descTxt = new Text(desc);
        descTxt.setFont(Font.font("Poppins", 10));
        descTxt.setFill(Color.web(TEXT_SOFT));
        descTxt.setWrappingWidth(130);

        card.getChildren().addAll(iconCircle, titleTxt, descTxt);
        card.setOnMouseEntered(e -> card.setStyle(
            baseStyle +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-background-color: rgba(26,19,99,0.04);"
        ));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));
        return card;
    }

    private void searchHelpTopic(String query, Accordion faqAccordion) {
        if (faqAccordion == null) {
            return;
        }
        if (query == null || query.isBlank()) {
            new Alert(Alert.AlertType.INFORMATION, "Type a help topic or click a topic card to open related instructions.").showAndWait();
            return;
        }

        String term = query.trim().toLowerCase();
        for (TitledPane pane : faqAccordion.getPanes()) {
            String title = pane.getText() != null ? pane.getText().toLowerCase() : "";
            String body = "";
            if (pane.getContent() instanceof Label label && label.getText() != null) {
                body = label.getText().toLowerCase();
            }
            if (title.contains(term) || body.contains(term) || matchesTopicAlias(term, title, body)) {
                faqAccordion.setExpandedPane(pane);
                pane.requestFocus();
                return;
            }
        }

        new Alert(Alert.AlertType.INFORMATION,
            "No matching help topic found. Try Members, Payments, Inventory, Equipment, POS, Search, Reports, Maintenance, Settings, or Password.")
            .showAndWait();
    }

    private boolean matchesTopicAlias(String term, String title, String body) {
        return (term.contains("member") && (title.contains("member") || body.contains("member")))
            || (term.contains("payment") && (title.contains("payment") || body.contains("payment")))
            || (term.contains("inventory") && (title.contains("inventory") || body.contains("inventory")))
            || (term.contains("equipment") && (title.contains("equipment") || body.contains("equipment")))
            || (term.equals("pos") && body.contains("sale"))
            || (term.contains("search") && (title.contains("search") || body.contains("search")))
            || (term.contains("report") && (title.contains("report") || body.contains("report")))
            || (term.contains("password") && (title.contains("password") || body.contains("password")))
            || (term.contains("maintenance") && (title.contains("maintenance") || body.contains("maintenance")))
            || (term.contains("backup") && body.contains("backup"));
    }

    private VBox buildSectionCard(String title, String sub) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 22, 22, 22));
        card.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web(ACCENT, 0.10));
        ds.setRadius(12);
        ds.setOffsetY(4);
        card.setEffect(ds);
        Rectangle accentBar = new Rectangle(42, 3);
        accentBar.setArcWidth(3);
        accentBar.setArcHeight(3);
        accentBar.setFill(Color.web(ACCENT));
        Text t = new Text(title);
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        t.setFill(Color.web(TEXT_TITLE));
        Text s = new Text(sub);
        s.setFont(Font.font("Poppins", 11));
        s.setFill(Color.web(TEXT_SOFT));
        card.getChildren().addAll(new VBox(6, accentBar, t, s));
        return card;
    }

    private void applyFieldStyle(TextField f) {
        String base =
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-prompt-text-fill: " + TEXT_SOFT + ";" +
            "-fx-padding: 10 14;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;";
        f.setStyle(base);
        f.focusedProperty().addListener((o, old, foc) -> f.setStyle(
            base + "-fx-border-color: " + (foc ? ACCENT : BORDER) + ";"
        ));
    }

    private Button accentButton(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38);
        b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        String base =
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 14;" +
            "-fx-cursor: hand;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(base.replace(ACCENT, ACCENT_DARK)));
        b.setOnMouseExited(e -> b.setStyle(base));
        return b;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
