package mj23gym.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

/**
 * MJ23 Playgrind Gym – Payment & Billing Screen
 * Shows member payment form with cash/GCash/bank transfer options,
 * automated dues/discount/penalty calculation, and payment history table.
 */
public class PaymentScreen extends Application {

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

    // Selected payment method
    private String selectedMethod = "Cash";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym – Payment & Billing");
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
        StackPane logoBadge = new StackPane();
        logoBadge.setPrefSize(42, 42);
        Rectangle lb = new Rectangle(42, 42);
        lb.setArcWidth(10); lb.setArcHeight(10);
        lb.setFill(Color.web(ACCENT));
        Text lt = new Text("MJ");
        lt.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        lt.setFill(Color.WHITE);
        logoBadge.getChildren().addAll(lb, lt);
        VBox logoTxt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND");
        l1.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM");
        l2.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        l2.setFill(Color.web(ACCENT));
        logoTxt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(logoBadge, logoTxt);
        String[][] items = {
            {"🏠","Dashboard"},{"👥","Member Management"},{"💳","Payment & Billing"},
            {"📦","Inventory"},{"🏋","Equipment"},{"🛒","Point of Sale"},{"📊","Reports"}
        };
        VBox menu = new VBox(2);
        menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Payment & Billing")));
        String[][] sys = {{"⚙","Settings"},{"❓","Help"},{"ℹ","About"}};
        VBox sysMenu = new VBox(2);
        sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys)
            sysMenu.getChildren().add(buildMenuItem(it[0], it[1], false));
        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        Rectangle d1 = new Rectangle(230, 1); d1.setFill(Color.web(BORDER));
        Rectangle d2 = new Rectangle(230, 1); d2.setFill(Color.web(BORDER));
        sidebar.getChildren().addAll(topAccent, logoArea, d1, makeSecLbl("MAIN MENU"),
            menu, d2, makeSecLbl("SYSTEM"), sysMenu, sp);
        return sidebar;
    }

    private HBox buildMenuItem(String icon, String label, boolean active) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(11, 16, 11, 16));
        item.setCursor(javafx.scene.Cursor.HAND);
        Rectangle bar = new Rectangle(3, 36);
        bar.setArcWidth(3); bar.setArcHeight(3);
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
        Label l = new Label(t);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_DIM));
        l.setPadding(new Insets(8, 0, 6, 20));
        return l;
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
            "-fx-border-width: 0 0 1 0;"
        );
        VBox pgInfo = new VBox(2);
        Text t1 = new Text("Payment & Billing");
        t1.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Process member payments and manage billing records");
        t2.setFont(Font.font("Verdana", 11));
        t2.setFill(Color.web(TEXT_MUTED));
        pgInfo.getChildren().addAll(t1, t2);
        topBar.getChildren().add(pgInfo);

        // Scrollable body
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(22);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // ── Stats row ──────────────────────────────────────────────
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            makeStatChip("💰 Total Collected Today", "₱4,320", SUCCESS),
            makeStatChip("⚠  Overdue Accounts",      "7",      ACCENT),
            makeStatChip("📋 Pending Invoices",       "3",      WARNING),
            makeStatChip("✅ Paid This Month",         "128",    INFO)
        );

        // ── Two-column layout: Payment Form + Summary ──────────────
        HBox mainRow = new HBox(20);

        // LEFT: Payment form card
        VBox payCard = new VBox(18);
        payCard.setPadding(new Insets(26, 26, 26, 26));
        payCard.setPrefWidth(500);
        payCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.3));
        ds.setRadius(12); ds.setOffsetY(4);
        payCard.setEffect(ds);

        Text payTitle = new Text("Process Payment");
        payTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 17));
        payTitle.setFill(Color.web(TEXT_WHITE));
        Rectangle payLine = new Rectangle(48, 3);
        payLine.setFill(Color.web(ACCENT)); payLine.setArcWidth(3); payLine.setArcHeight(3);

        // Member lookup
        VBox memberLookup = new VBox(8);
        Label mlbl = makeFieldLabel("SEARCH MEMBER");
        HBox searchRow = new HBox(10);
        TextField memberSearch = new TextField();
        memberSearch.setPromptText("Enter Member ID or Name...");
        memberSearch.setPrefHeight(40);
        HBox.setHgrow(memberSearch, Priority.ALWAYS);
        applyFieldStyle(memberSearch);
        Button findBtn = new Button("Find");
        findBtn.setPrefHeight(40);
        findBtn.setPadding(new Insets(0, 16, 0, 16));
        findBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        findBtn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        searchRow.getChildren().addAll(memberSearch, findBtn);
        memberLookup.getChildren().addAll(mlbl, searchRow);

        // Member info display
        VBox memberInfo = new VBox(0);
        memberInfo.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );
        memberInfo.setPadding(new Insets(16));
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20); infoGrid.setVgap(10);
        String[][] infoData = {
            {"Member ID",   "#M-001"},
            {"Name",        "Juan dela Cruz"},
            {"Plan",        "Monthly – ₱800"},
            {"Status",      "Active"},
            {"Balance Due", "₱800"},
            {"Due Date",    "2025-06-30"},
        };
        for (int i = 0; i < infoData.length; i++) {
            Label key = new Label(infoData[i][0] + ":");
            key.setFont(Font.font("Verdana", 11));
            key.setTextFill(Color.web(TEXT_DIM));
            Label val = new Label(infoData[i][1]);
            val.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            String valColor = infoData[i][0].equals("Balance Due") ? ACCENT :
                              infoData[i][0].equals("Status") ? SUCCESS : TEXT_WHITE;
            val.setTextFill(Color.web(valColor));
            infoGrid.add(key, 0, i);
            infoGrid.add(val, 1, i);
        }
        memberInfo.getChildren().add(infoGrid);

        // Payment method selector
        VBox methodGroup = new VBox(10);
        Label methodLbl = makeFieldLabel("PAYMENT METHOD");
        HBox methodBtns = new HBox(12);
        String[] methods = {"Cash", "GCash", "Bank Transfer"};
        String[] methodIcons = {"💵", "📱", "🏦"};
        ToggleGroup methodGroup2 = new ToggleGroup();
        for (int i = 0; i < methods.length; i++) {
            ToggleButton tb = new ToggleButton(methodIcons[i] + "  " + methods[i]);
            tb.setToggleGroup(methodGroup2);
            tb.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            tb.setPrefHeight(40);
            HBox.setHgrow(tb, Priority.ALWAYS);
            tb.setMaxWidth(Double.MAX_VALUE);
            if (methods[i].equals("Cash")) tb.setSelected(true);
            styleToggleBtn(tb, methods[i].equals("Cash"));
            tb.selectedProperty().addListener((obs, old, sel) -> styleToggleBtn(tb, sel));
        }
        methodBtns.getChildren().setAll(
            methodGroup2.getToggles().stream()
                .map(t -> (ToggleButton) t)
                .toArray(ToggleButton[]::new)
        );
        methodGroup.getChildren().addAll(methodLbl, methodBtns);

        // Reference number (for GCash / Bank)
        VBox refGroup = new VBox(6);
        Label refLbl = makeFieldLabel("REFERENCE NUMBER (GCash / Bank Transfer)");
        TextField refField = new TextField();
        refField.setPromptText("Enter reference number after confirming payment");
        refField.setPrefHeight(40);
        applyFieldStyle(refField);
        refGroup.getChildren().addAll(refLbl, refField);

        // Amount fields
        GridPane amtGrid = new GridPane();
        amtGrid.setHgap(16); amtGrid.setVgap(14);
        ColumnConstraints ca = new ColumnConstraints(); ca.setPercentWidth(50);
        ColumnConstraints cb = new ColumnConstraints(); cb.setPercentWidth(50);
        amtGrid.getColumnConstraints().addAll(ca, cb);

        amtGrid.add(buildAmtField("AMOUNT DUE", "₱800.00", true), 0, 0);
        amtGrid.add(buildAmtField("DISCOUNT", "₱0.00", false), 1, 0);
        amtGrid.add(buildAmtField("PENALTY (Late)", "₱0.00", false), 0, 1);
        amtGrid.add(buildAmtField("TOTAL AMOUNT", "₱800.00", true), 1, 1);

        // Notes
        VBox notesGroup = new VBox(6);
        Label notesLbl = makeFieldLabel("NOTES (Optional)");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Additional payment notes...");
        notesArea.setPrefRowCount(2);
        notesArea.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;"
        );
        notesGroup.getChildren().addAll(notesLbl, notesArea);

        // Submit buttons
        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button clearBtn = new Button("Clear");
        clearBtn.setPrefHeight(42);
        clearBtn.setPadding(new Insets(0, 20, 0, 20));
        clearBtn.setFont(Font.font("Verdana", 12));
        clearBtn.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;"
        );
        Button printBtn = new Button("🖨  Print Receipt");
        printBtn.setPrefHeight(42);
        printBtn.setPadding(new Insets(0, 20, 0, 20));
        printBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        printBtn.setStyle(
            "-fx-background-color: " + INFO + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        Button processBtn = new Button("✔  Process Payment");
        processBtn.setPrefHeight(42);
        processBtn.setPadding(new Insets(0, 20, 0, 20));
        processBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        processBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        processBtn.setOnMouseEntered(e -> processBtn.setStyle(
            "-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        processBtn.setOnMouseExited(e -> processBtn.setStyle(
            "-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        btnRow.getChildren().addAll(clearBtn, printBtn, processBtn);

        payCard.getChildren().addAll(
            payTitle, payLine, memberLookup, memberInfo,
            methodGroup, refGroup, amtGrid, notesGroup, btnRow
        );

        // RIGHT: Payment history
        VBox histCard = buildPaymentHistory();
        HBox.setHgrow(histCard, Priority.ALWAYS);

        mainRow.getChildren().addAll(payCard, histCard);
        body.getChildren().addAll(statsRow, mainRow);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    // ── Payment history table ──────────────────────────────────────
    private VBox buildPaymentHistory() {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10); ds.setOffsetY(4);
        card.setEffect(ds);

        HBox header = new HBox();
        header.setPadding(new Insets(16, 20, 14, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        Text t = new Text("Payment History");
        t.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        t.setFill(Color.web(TEXT_WHITE));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("All", "Cash", "GCash", "Bank Transfer");
        filter.setValue("All");
        filter.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 6;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-font-size: 11;"
        );
        header.getChildren().addAll(t, sp, filter);

        // Table headers
        String[] hdrs = {"Member", "Amount", "Method", "Date", "Status"};
        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(10, 20, 10, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        GridPane hg = makePayGrid();
        hg.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hg, Priority.ALWAYS);
        for (int i = 0; i < hdrs.length; i++) {
            Label h = new Label(hdrs[i].toUpperCase());
            h.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
            h.setTextFill(Color.web(TEXT_DIM));
            hg.add(h, i, 0);
        }
        tblHdr.getChildren().add(hg);

        String[][] rows = {
            {"Juan dela Cruz",  "₱800",  "Cash",          "2025-06-01", "Paid"},
            {"Maria Santos",    "₱100",  "GCash",         "2025-06-01", "Paid"},
            {"Pedro Reyes",     "₱800",  "Bank Transfer", "2025-05-30", "Overdue"},
            {"Ana Garcia",      "₱50",   "Cash",          "2025-06-01", "Paid"},
            {"Carlo Mendoza",   "₱800",  "GCash",         "2025-05-28", "Paid"},
            {"Liza Fernandez",  "₱800",  "Cash",          "2025-05-15", "Overdue"},
            {"Ramon Torres",    "₱100",  "GCash",         "2025-06-01", "Paid"},
            {"Celia Villanueva","₱50",   "Cash",          "2025-06-01", "Paid"},
        };

        VBox rowsBox = new VBox(0);
        for (int r = 0; r < rows.length; r++) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox dataRow = new HBox();
            dataRow.setPadding(new Insets(10, 20, 10, 20));
            dataRow.setStyle("-fx-background-color: " + bg + ";");
            GridPane rg = makePayGrid();
            rg.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rg, Priority.ALWAYS);
            rg.add(makeCell(rows[r][0], TEXT_WHITE, false), 0, 0);
            rg.add(makeCell(rows[r][1], SUCCESS, true), 1, 0);
            rg.add(makeMethodBadge(rows[r][2]), 2, 0);
            rg.add(makeCell(rows[r][3], TEXT_MUTED, false), 3, 0);
            rg.add(makeStatusBadge(rows[r][4]), 4, 0);
            dataRow.getChildren().add(rg);
            String fBg = bg;
            dataRow.setOnMouseEntered(e -> dataRow.setStyle("-fx-background-color: rgba(230,57,70,0.05);"));
            dataRow.setOnMouseExited(e -> dataRow.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(dataRow);
        }

        card.getChildren().addAll(header, tblHdr, rowsBox);
        return card;
    }

    // ── Helpers ────────────────────────────────────────────────────
    private GridPane makePayGrid() {
        GridPane g = new GridPane();
        double[] widths = {28, 14, 18, 18, 14};
        for (double w : widths) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(w);
            cc.setHgrow(Priority.ALWAYS);
            g.getColumnConstraints().add(cc);
        }
        return g;
    }

    private VBox buildAmtField(String label, String value, boolean highlight) {
        VBox g = new VBox(6);
        Label lbl = makeFieldLabel(label);
        TextField tf = new TextField(value);
        tf.setPrefHeight(40);
        tf.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + (highlight ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + (highlight ? TEXT_WHITE : TEXT_MUTED) + ";" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: " + (highlight ? "bold" : "normal") + ";" +
            "-fx-padding: 0 12 0 12;"
        );
        g.getChildren().addAll(lbl, tf);
        return g;
    }

    private Label makeFieldLabel(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_MUTED));
        return l;
    }

    private void applyFieldStyle(TextField f) {
        f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;"
        );
        f.focusedProperty().addListener((o, old, focused) -> f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;"
        ));
    }

    private void styleToggleBtn(ToggleButton tb, boolean selected) {
        tb.setStyle(
            "-fx-background-color: " + (selected ? ACCENT : BG_MAIN) + ";" +
            "-fx-text-fill: " + (selected ? "white" : TEXT_MUTED) + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + (selected ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: Verdana;"
        );
    }

    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        l.setTextFill(Color.web(color));
        return l;
    }

    private Label makeStatusBadge(String status) {
        Label b = new Label(status);
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        String c = status.equalsIgnoreCase("Paid") ? SUCCESS : ACCENT;
        String bg = status.equalsIgnoreCase("Paid") ? "rgba(76,175,80,0.15)" : "rgba(230,57,70,0.15)";
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        return b;
    }

    private Label makeMethodBadge(String method) {
        Label b = new Label(method);
        b.setFont(Font.font("Verdana", 10));
        String c; String bg;
        switch (method) {
            case "Cash": c = SUCCESS; bg = "rgba(76,175,80,0.12)"; break;
            case "GCash": c = "#2196f3"; bg = "rgba(33,150,243,0.12)"; break;
            default: c = WARNING; bg = "rgba(255,152,0,0.12)"; break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        return b;
    }

    private HBox makeStatChip(String label, String value, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.2));
        ds.setRadius(8); ds.setOffsetY(3);
        chip.setEffect(ds);
        Text val = new Text(value);
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        val.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Verdana", 11));
        lbl.setFill(Color.web(TEXT_MUTED));
        chip.getChildren().add(new VBox(2, lbl, val));
        return chip;
    }

    public static void main(String[] args) { launch(args); }
}
