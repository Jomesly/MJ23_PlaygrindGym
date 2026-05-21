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
 * MJ23 Playgrind Gym – Reports Screen
 * Covers: Generate Report + Sales Report views
 */
public class ReportsScreen extends Application {

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
        stage.setTitle("MJ23 Playgrind Gym – Reports");
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
        sidebar.setPrefWidth(230); sidebar.setMinWidth(230); sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        Rectangle topAccent = new Rectangle(230, 5); topAccent.setFill(Color.web(ACCENT));
        HBox logoArea = new HBox(12); logoArea.setAlignment(Pos.CENTER_LEFT); logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane badge = new StackPane(); badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42); bb.setArcWidth(10); bb.setArcHeight(10); bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ"); bt.setFont(Font.font("Georgia", FontWeight.BOLD, 16)); bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND"); l1.setFont(Font.font("Georgia", FontWeight.BOLD, 11)); l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM"); l2.setFont(Font.font("Georgia", FontWeight.BOLD, 11)); l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2); logoArea.getChildren().addAll(badge, lt);
        String[][] items = {
            {"🏠","Dashboard"},{"👥","Member Management"},{"💳","Payment & Billing"},
            {"📦","Inventory"},{"🏋","Equipment"},{"🛒","Point of Sale"},{"📊","Reports"}
        };
        VBox menu = new VBox(2); menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items) menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Reports")));
        String[][] sys = {{"⚙","Settings"},{"❓","Help"},{"ℹ","About"}};
        VBox sysMenu = new VBox(2); sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys) sysMenu.getChildren().add(buildMenuItem(it[0], it[1], false));
        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        Rectangle d1 = new Rectangle(230, 1); d1.setFill(Color.web(BORDER));
        Rectangle d2 = new Rectangle(230, 1); d2.setFill(Color.web(BORDER));
        sidebar.getChildren().addAll(topAccent, logoArea, d1, makeSecLbl("MAIN MENU"), menu, d2, makeSecLbl("SYSTEM"), sysMenu, sp);
        return sidebar;
    }

    private HBox buildMenuItem(String icon, String label, boolean active) {
        HBox item = new HBox(12); item.setAlignment(Pos.CENTER_LEFT); item.setPadding(new Insets(11, 16, 11, 16)); item.setCursor(javafx.scene.Cursor.HAND);
        Rectangle bar = new Rectangle(3, 36); bar.setArcWidth(3); bar.setArcHeight(3); bar.setFill(active ? Color.web(ACCENT) : Color.TRANSPARENT);
        Text ico = new Text(icon); ico.setFont(Font.font(14));
        Text lbl = new Text(label); lbl.setFont(Font.font("Verdana", active ? FontWeight.BOLD : FontWeight.NORMAL, 12)); lbl.setFill(active ? Color.web(TEXT_WHITE) : Color.web(TEXT_MUTED));
        item.getChildren().addAll(bar, ico, lbl);
        item.setStyle(active ? "-fx-background-color: " + BG_CARD + "; -fx-background-radius: 8;" : "-fx-background-color: transparent; -fx-background-radius: 8;");
        if (!active) {
            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;"));
        }
        return item;
    }

    private Label makeSecLbl(String t) {
        Label l = new Label(t); l.setFont(Font.font("Verdana", FontWeight.BOLD, 9)); l.setTextFill(Color.web(TEXT_DIM)); l.setPadding(new Insets(8, 0, 6, 20)); return l;
    }

    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text t1 = new Text("Reports & Analytics"); t1.setFont(Font.font("Georgia", FontWeight.BOLD, 20)); t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Generate payment, sales, and inventory summary reports"); t2.setFont(Font.font("Verdana", 11)); t2.setFill(Color.web(TEXT_MUTED));
        pg.getChildren().addAll(t1, t2);
        Region tSp = new Region(); HBox.setHgrow(tSp, Priority.ALWAYS);
        Button exportBtn = makeAccentBtn("⬇  Export PDF");
        topBar.getChildren().addAll(pg, tSp, exportBtn);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(22);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // ── Report type selector ───────────────────────────────────
        HBox typeRow = new HBox(14);
        typeRow.setAlignment(Pos.CENTER_LEFT);
        String[][] reportTypes = {
            {"📊", "Sales Report",     "Revenue from product sales"},
            {"💳", "Payment Report",   "Membership dues & payments"},
            {"📦", "Inventory Report", "Stock levels & movements"},
        };
        for (String[] rt : reportTypes) {
            VBox rtCard = buildReportTypeCard(rt[0], rt[1], rt[2], rt[1].equals("Sales Report"));
            HBox.setHgrow(rtCard, Priority.ALWAYS);
            typeRow.getChildren().add(rtCard);
        }

        // ── Filters row ────────────────────────────────────────────
        VBox filterCard = new VBox(16);
        filterCard.setPadding(new Insets(22));
        filterCard.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");

        Text filterTitle = new Text("Report Filters");
        filterTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        filterTitle.setFill(Color.web(TEXT_WHITE));

        HBox filterRow = new HBox(16);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        VBox dateFrom = buildDateField("DATE FROM", "2025-06-01");
        VBox dateTo   = buildDateField("DATE TO",   "2025-06-30");

        ComboBox<String> groupBy = new ComboBox<>();
        groupBy.getItems().addAll("By Day", "By Week", "By Month");
        groupBy.setValue("By Day"); styleCombo(groupBy);
        VBox groupBox = new VBox(6);
        Label gLbl = new Label("GROUP BY"); gLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9)); gLbl.setTextFill(Color.web(TEXT_MUTED));
        groupBox.getChildren().addAll(gLbl, groupBy);

        Region fSp = new Region(); HBox.setHgrow(fSp, Priority.ALWAYS);
        Button genBtn = makeAccentBtn("📊  Generate Report");
        genBtn.setPrefHeight(42);

        filterRow.getChildren().addAll(dateFrom, dateTo, groupBox, fSp, genBtn);
        filterCard.getChildren().addAll(filterTitle, filterRow);

        // ── Summary Stats ──────────────────────────────────────────
        HBox summaryStats = new HBox(16);
        summaryStats.getChildren().addAll(
            makeStatCard("Total Revenue",     "₱28,450", "+12% vs last month",  SUCCESS),
            makeStatCard("Total Transactions","246",     "+8 this week",         INFO),
            makeStatCard("Top Product",       "Whey Protein", "38 units sold",   WARNING),
            makeStatCard("Avg. Daily Sales",  "₱948",   "Based on 30 days",     ACCENT)
        );

        // ── Sales Report Table ─────────────────────────────────────
        VBox salesTable = buildSalesTable();

        // ── Payment Summary Table ──────────────────────────────────
        VBox paymentSummary = buildPaymentSummary();

        body.getChildren().addAll(typeRow, filterCard, summaryStats, salesTable, paymentSummary);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    private VBox buildReportTypeCard(String icon, String title, String sub, boolean active) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setCursor(javafx.scene.Cursor.HAND);
        String borderColor = active ? ACCENT : BORDER;
        String bgColor = active ? "rgba(230,57,70,0.08)" : BG_CARD;
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 12; -fx-border-color: " + borderColor + "; -fx-border-radius: 12; -fx-border-width: " + (active ? "0 0 0 4" : "1") + ";");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(10); ds.setOffsetY(4); card.setEffect(ds);
        Text ico = new Text(icon); ico.setFont(Font.font(22));
        Text t = new Text(title); t.setFont(Font.font("Verdana", FontWeight.BOLD, 13)); t.setFill(Color.web(active ? TEXT_WHITE : TEXT_MUTED));
        Text s = new Text(sub); s.setFont(Font.font("Verdana", 10)); s.setFill(Color.web(TEXT_DIM));
        card.getChildren().addAll(ico, t, s);
        return card;
    }

    private VBox buildSalesTable() {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(10); ds.setOffsetY(4); card.setEffect(ds);

        HBox hdr = new HBox(); hdr.setPadding(new Insets(16, 20, 14, 20)); hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        Text ht = new Text("Sales Report – June 2025"); ht.setFont(Font.font("Verdana", FontWeight.BOLD, 13)); ht.setFill(Color.web(TEXT_WHITE));
        Region hSp = new Region(); HBox.setHgrow(hSp, Priority.ALWAYS);
        Button printBtn = new Button("🖨  Print");
        printBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ACCENT + "; -fx-font-size: 11; -fx-cursor: hand;");
        hdr.getChildren().addAll(ht, hSp, printBtn);

        String[] headers = {"Date", "Item", "Qty Sold", "Unit Price", "Total", "Payment Method"};
        double[] colW = {14, 24, 10, 12, 12, 18};

        HBox tblHdr = new HBox(); tblHdr.setPadding(new Insets(10, 20, 10, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        GridPane hGrid = makeGrid(colW); hGrid.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(hGrid, Priority.ALWAYS);
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i].toUpperCase()); h.setFont(Font.font("Verdana", FontWeight.BOLD, 9)); h.setTextFill(Color.web(TEXT_DIM));
            hGrid.add(h, i, 0);
        }
        tblHdr.getChildren().add(hGrid);

        String[][] rows = {
            {"2025-06-01", "Whey Protein",       "3", "₱120", "₱360",  "Cash"},
            {"2025-06-01", "Nature Spring Water", "5", "₱25",  "₱125",  "GCash"},
            {"2025-06-02", "Energy Bar",          "4", "₱65",  "₱260",  "Cash"},
            {"2025-06-02", "Gatorade (Blue)",     "2", "₱55",  "₱110",  "GCash"},
            {"2025-06-03", "Creatine",            "1", "₱85",  "₱85",   "Bank Transfer"},
            {"2025-06-03", "Gym Gloves (M)",      "1", "₱350", "₱350",  "Cash"},
        };

        VBox rowsBox = new VBox(0);
        for (int r = 0; r < rows.length; r++) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox(); row.setPadding(new Insets(10, 20, 10, 20)); row.setStyle("-fx-background-color: " + bg + ";");
            GridPane rg = makeGrid(colW); rg.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(rg, Priority.ALWAYS);
            rg.add(makeCell(rows[r][0], TEXT_MUTED, false), 0, 0);
            rg.add(makeCell(rows[r][1], TEXT_WHITE, false), 1, 0);
            rg.add(makeCell(rows[r][2], TEXT_WHITE, true), 2, 0);
            rg.add(makeCell(rows[r][3], TEXT_MUTED, false), 3, 0);
            rg.add(makeCell(rows[r][4], SUCCESS, true), 4, 0);
            rg.add(makeMethodBadge(rows[r][5]), 5, 0);
            row.getChildren().add(rg);
            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(230,57,70,0.05);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(row);
        }

        // Total row
        HBox totalRow = new HBox(); totalRow.setPadding(new Insets(12, 20, 12, 20));
        totalRow.setStyle("-fx-background-color: rgba(230,57,70,0.08); -fx-border-color: " + BORDER + " transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        GridPane tg = makeGrid(colW); tg.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(tg, Priority.ALWAYS);
        tg.add(makeCell("TOTAL", TEXT_MUTED, true), 0, 0);
        tg.add(makeCell("", TEXT_WHITE, false), 1, 0);
        tg.add(makeCell("16", TEXT_WHITE, true), 2, 0);
        tg.add(makeCell("", TEXT_WHITE, false), 3, 0);
        tg.add(makeCell("₱1,290", ACCENT, true), 4, 0);
        totalRow.getChildren().add(tg);

        card.getChildren().addAll(hdr, tblHdr, rowsBox, totalRow);
        return card;
    }

    private VBox buildPaymentSummary() {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(10); ds.setOffsetY(4); card.setEffect(ds);

        HBox hdr = new HBox(); hdr.setPadding(new Insets(16, 20, 14, 20)); hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        Text ht = new Text("Payment Summary – June 2025"); ht.setFont(Font.font("Verdana", FontWeight.BOLD, 13)); ht.setFill(Color.web(TEXT_WHITE));
        hdr.getChildren().add(ht);

        String[] headers = {"Member", "Plan", "Amount Paid", "Method", "Date", "Status"};
        double[] colW = {22, 13, 14, 16, 15, 12};

        HBox tblHdr = new HBox(); tblHdr.setPadding(new Insets(10, 20, 10, 20)); tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        GridPane hGrid = makeGrid(colW); hGrid.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(hGrid, Priority.ALWAYS);
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i].toUpperCase()); h.setFont(Font.font("Verdana", FontWeight.BOLD, 9)); h.setTextFill(Color.web(TEXT_DIM));
            hGrid.add(h, i, 0);
        }
        tblHdr.getChildren().add(hGrid);

        String[][] rows = {
            {"Juan dela Cruz",   "Monthly",    "₱800", "Cash",          "2025-06-01", "Paid"},
            {"Maria Santos",     "Daily",      "₱100", "GCash",         "2025-06-01", "Paid"},
            {"Pedro Reyes",      "Monthly",    "₱800", "Bank Transfer", "2025-05-30", "Overdue"},
            {"Ana Garcia",       "Per Session","₱50",  "Cash",          "2025-06-01", "Paid"},
            {"Carlo Mendoza",    "Monthly",    "₱800", "GCash",         "2025-06-01", "Paid"},
        };

        VBox rowsBox = new VBox(0);
        for (int r = 0; r < rows.length; r++) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox(); row.setPadding(new Insets(10, 20, 10, 20)); row.setStyle("-fx-background-color: " + bg + ";");
            GridPane rg = makeGrid(colW); rg.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(rg, Priority.ALWAYS);
            rg.add(makeCell(rows[r][0], TEXT_WHITE, false), 0, 0);
            rg.add(makeCell(rows[r][1], TEXT_MUTED, false), 1, 0);
            rg.add(makeCell(rows[r][2], SUCCESS, true), 2, 0);
            rg.add(makeMethodBadge(rows[r][3]), 3, 0);
            rg.add(makeCell(rows[r][4], TEXT_MUTED, false), 4, 0);
            rg.add(makeStatusBadge(rows[r][5]), 5, 0);
            row.getChildren().add(rg);
            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(230,57,70,0.05);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(row);
        }
        card.getChildren().addAll(hdr, tblHdr, rowsBox);
        return card;
    }

    private VBox makeStatCard(String label, String value, String sub, String color) {
        VBox card = new VBox(8); card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        HBox.setHgrow(card, Priority.ALWAYS);
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(10); ds.setOffsetY(4); card.setEffect(ds);
        Text lbl = new Text(label); lbl.setFont(Font.font("Verdana", 11)); lbl.setFill(Color.web(TEXT_MUTED));
        Text val = new Text(value); val.setFont(Font.font("Georgia", FontWeight.BOLD, 24)); val.setFill(Color.web(color));
        Text s = new Text(sub); s.setFont(Font.font("Verdana", 10)); s.setFill(Color.web(TEXT_DIM));
        card.getChildren().addAll(lbl, val, s);
        return card;
    }

    private VBox buildDateField(String label, String def) {
        VBox g = new VBox(6);
        Label lbl = new Label(label); lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9)); lbl.setTextFill(Color.web(TEXT_MUTED));
        TextField tf = new TextField(def); tf.setPrefHeight(40); applyFieldStyle(tf);
        g.getChildren().addAll(lbl, tf);
        return g;
    }

    private GridPane makeGrid(double[] widths) {
        GridPane g = new GridPane();
        for (double w : widths) { ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(w); cc.setHgrow(Priority.ALWAYS); g.getColumnConstraints().add(cc); }
        return g;
    }

    private void applyFieldStyle(TextField f) {
        f.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Verdana; -fx-font-size: 12;");
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: " + TEXT_WHITE + "; -fx-font-family: Verdana; -fx-font-size: 12; -fx-pref-height: 40;");
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text); b.setPrefHeight(38); b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        return b;
    }

    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text); l.setFont(Font.font("Verdana", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11)); l.setTextFill(Color.web(color)); return l;
    }

    private Label makeMethodBadge(String method) {
        Label b = new Label(method); b.setFont(Font.font("Verdana", 10));
        String c; String bg;
        switch (method) {
            case "Cash": c = SUCCESS; bg = "rgba(76,175,80,0.12)"; break;
            case "GCash": c = INFO; bg = "rgba(33,150,243,0.12)"; break;
            default: c = WARNING; bg = "rgba(255,152,0,0.12)"; break;
        }
        b.setTextFill(Color.web(c)); b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 3 10 3 10;"); return b;
    }

    private Label makeStatusBadge(String status) {
        Label b = new Label(status); b.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        String c = status.equals("Paid") ? SUCCESS : ACCENT;
        String bg = status.equals("Paid") ? "rgba(76,175,80,0.15)" : "rgba(230,57,70,0.15)";
        b.setTextFill(Color.web(c)); b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 3 10 3 10;"); return b;
    }

    public static void main(String[] args) { launch(args); }
}
