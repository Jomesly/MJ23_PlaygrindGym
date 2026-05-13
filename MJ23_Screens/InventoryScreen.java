package mj23gym.ui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * MJ23 Playgrind Gym – Inventory Screen
 * Paginated table of gym supplies/products with Add, Search, Edit controls.
 */
public class InventoryScreen extends Application {

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

    private final String[][] items = {
        {"#INV-001", "Whey Protein (1kg)",    "Supplements", "12",  "₱1,200", "Low Stock"},
        {"#INV-002", "Creatine (500g)",        "Supplements", "25",  "₱850",   "In Stock"},
        {"#INV-003", "Nature Spring Water",    "Drinks",      "48",  "₱25",    "In Stock"},
        {"#INV-004", "Gatorade (Blue)",        "Drinks",      "3",   "₱55",    "Low Stock"},
        {"#INV-005", "Resistance Bands",       "Equipment",   "10",  "₱180",   "In Stock"},
        {"#INV-006", "Jump Rope",              "Equipment",   "7",   "₱120",   "In Stock"},
        {"#INV-007", "Gym Gloves (M)",         "Accessories", "0",   "₱350",   "Out of Stock"},
        {"#INV-008", "Gym Gloves (L)",         "Accessories", "5",   "₱350",   "In Stock"},
        {"#INV-009", "Vitamins (B-Complex)",   "Supplements", "18",  "₱420",   "In Stock"},
        {"#INV-010", "Energy Bar",             "Snacks",      "2",   "₱65",    "Low Stock"},
    };

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym – Inventory");
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
        StackPane badge = new StackPane();
        badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42);
        bb.setArcWidth(10); bb.setArcHeight(10);
        bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ");
        bt.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND");
        l1.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM");
        l2.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(badge, lt);
        String[][] items = {
            {"🏠","Dashboard"},{"👥","Member Management"},{"💳","Payment & Billing"},
            {"📦","Inventory"},{"🏋","Equipment"},{"🛒","Point of Sale"},{"📊","Reports"}
        };
        VBox menu = new VBox(2);
        menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Inventory")));
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
            "-fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text t1 = new Text("Inventory");
        t1.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Manage gym supplies, supplements, and stock levels");
        t2.setFont(Font.font("Verdana", 11));
        t2.setFill(Color.web(TEXT_MUTED));
        pg.getChildren().addAll(t1, t2);
        topBar.getChildren().add(pg);

        // Scrollable body
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(20);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // ── Stats row ──────────────────────────────────────────────
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            makeStatChip("📦 Total Items",      "10",  TEXT_WHITE),
            makeStatChip("✅ In Stock",          "6",   SUCCESS),
            makeStatChip("⚠  Low Stock",         "3",   WARNING),
            makeStatChip("❌ Out of Stock",       "1",   ACCENT)
        );

        // ── Controls row ───────────────────────────────────────────
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("🔍  Search item name or ID...");
        search.setPrefWidth(260);
        search.setPrefHeight(38);
        applyFieldStyle(search);

        ComboBox<String> catFilter = new ComboBox<>();
        catFilter.getItems().addAll("All Categories", "Supplements", "Drinks", "Equipment", "Accessories", "Snacks");
        catFilter.setValue("All Categories");
        styleCombo(catFilter);

        ComboBox<String> stockFilter = new ComboBox<>();
        stockFilter.getItems().addAll("All Stock", "In Stock", "Low Stock", "Out of Stock");
        stockFilter.setValue("All Stock");
        styleCombo(stockFilter);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button addBtn = makeAccentBtn("＋  Add Item");
        addBtn.setOnAction(e -> showAddItemDialog());

        controls.getChildren().addAll(search, catFilter, stockFilter, sp, addBtn);

        // ── Table card ─────────────────────────────────────────────
        VBox tableCard = new VBox(0);
        tableCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.3)); ds.setRadius(12); ds.setOffsetY(4);
        tableCard.setEffect(ds);

        // Header row
        String[] headers = {"Item ID", "Item Name", "Category", "Qty", "Unit Price", "Stock Status", "Actions"};
        double[] colW = {9, 22, 13, 8, 11, 14, 13};

        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(12, 20, 12, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + "; -fx-background-radius: 12 12 0 0;");
        GridPane hGrid = makeGrid(colW);
        hGrid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hGrid, Priority.ALWAYS);
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i].toUpperCase());
            h.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
            h.setTextFill(Color.web(TEXT_DIM));
            hGrid.add(h, i, 0);
        }
        tblHdr.getChildren().add(hGrid);

        // Data rows
        VBox rowsBox = new VBox(0);
        for (int r = 0; r < this.items.length; r++) {
            String[] item = this.items[r];
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setPadding(new Insets(11, 20, 11, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            row.setAlignment(Pos.CENTER_LEFT);

            GridPane rGrid = makeGrid(colW);
            rGrid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rGrid, Priority.ALWAYS);

            rGrid.add(makeCell(item[0], ACCENT, true), 0, 0);
            rGrid.add(makeCell(item[1], TEXT_WHITE, false), 1, 0);
            rGrid.add(makeCatBadge(item[2]), 2, 0);

            // Qty — highlight red if low/out
            String qtyColor = item[3].equals("0") ? ACCENT :
                              Integer.parseInt(item[3]) <= 5 ? WARNING : TEXT_WHITE;
            rGrid.add(makeCell(item[3], qtyColor, true), 3, 0);
            rGrid.add(makeCell(item[4], TEXT_MUTED, false), 4, 0);
            rGrid.add(makeStockBadge(item[5]), 5, 0);

            HBox actions = new HBox(6);
            actions.setAlignment(Pos.CENTER_LEFT);
            actions.getChildren().addAll(
                makeActionBtn("✏", WARNING),
                makeActionBtn("📋", INFO),
                makeActionBtn("🗑", ACCENT)
            );
            rGrid.add(actions, 6, 0);
            row.getChildren().add(rGrid);

            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(230,57,70,0.06);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(row);
        }

        // Pagination
        HBox pag = new HBox(10);
        pag.setAlignment(Pos.CENTER_RIGHT);
        pag.setPadding(new Insets(14, 20, 14, 20));
        pag.setStyle("-fx-border-color: " + BORDER + " transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        Text pgInfo = new Text("Showing 1–10 of 47 items");
        pgInfo.setFont(Font.font("Verdana", 11));
        pgInfo.setFill(Color.web(TEXT_MUTED));
        Region pgSp = new Region(); HBox.setHgrow(pgSp, Priority.ALWAYS);
        pag.getChildren().addAll(pgInfo, pgSp,
            makePagBtn("← Prev", false), makePagBtn("1", true),
            makePagBtn("2", false), makePagBtn("3", false), makePagBtn("Next →", false));

        tableCard.getChildren().addAll(tblHdr, rowsBox, pag);
        body.getChildren().addAll(statsRow, controls, tableCard);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    // ── Add Item Dialog ────────────────────────────────────────────
    private void showAddItemDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Item");
        dialog.setResizable(false);

        VBox root = new VBox(18);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(440);

        Text title = new Text("Add New Item");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        Rectangle ul = new Rectangle(48, 3);
        ul.setFill(Color.web(ACCENT)); ul.setArcWidth(3); ul.setArcHeight(3);

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(14);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        form.getColumnConstraints().addAll(c1, c2);

        form.add(buildFieldGroup("ITEM NAME",    "Enter item name",      false), 0, 0, 2, 1);
        form.add(buildFieldGroup("CATEGORY",     "e.g. Supplements",     false), 0, 1);
        form.add(buildFieldGroup("UNIT PRICE",   "₱0.00",                false), 1, 1);
        form.add(buildFieldGroup("QUANTITY",     "Enter quantity",        false), 0, 2);
        form.add(buildFieldGroup("LOW STOCK THRESHOLD", "e.g. 5",        false), 1, 2);
        form.add(buildFieldGroup("SUPPLIER",     "Supplier name",         false), 0, 3, 2, 1);
        form.add(buildFieldGroup("NOTES",        "Optional notes...",     false), 0, 4, 2, 1);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setPrefHeight(40); cancel.setPadding(new Insets(0, 20, 0, 20));
        cancel.setFont(Font.font("Verdana", 12));
        cancel.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-text-fill: " + TEXT_MUTED + "; -fx-background-radius: 8; -fx-cursor: hand;");
        cancel.setOnAction(e -> dialog.close());
        Button save = makeAccentBtn("Save Item");
        save.setPrefHeight(40); save.setPadding(new Insets(0, 20, 0, 20));
        save.setOnAction(e -> dialog.close());
        btnRow.getChildren().addAll(cancel, save);

        root.getChildren().addAll(title, ul, form, btnRow);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    // ── Helpers ────────────────────────────────────────────────────
    private GridPane makeGrid(double[] widths) {
        GridPane g = new GridPane();
        for (double w : widths) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(w);
            cc.setHgrow(Priority.ALWAYS);
            g.getColumnConstraints().add(cc);
        }
        return g;
    }

    private VBox buildFieldGroup(String label, String prompt, boolean isPass) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        TextField tf = isPass ? new PasswordField() : new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(40);
        applyFieldStyle(tf);
        g.getChildren().addAll(lbl, tf);
        return g;
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
            "-fx-font-size: 12;");
        f.focusedProperty().addListener((o, old, focused) -> f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;"));
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;" +
            "-fx-pref-height: 38;");
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38);
        b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        return b;
    }

    private Button makeActionBtn(String icon, String color) {
        Button btn = new Button(icon);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + "; -fx-font-size: 13; -fx-cursor: hand; -fx-padding: 3 6 3 6; -fx-background-radius: 6;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-text-fill: " + color + "; -fx-font-size: 13; -fx-cursor: hand; -fx-padding: 3 6 3 6; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + "; -fx-font-size: 13; -fx-cursor: hand; -fx-padding: 3 6 3 6; -fx-background-radius: 6;"));
        return btn;
    }

    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        l.setTextFill(Color.web(color));
        return l;
    }

    private Label makeStockBadge(String status) {
        Label b = new Label(status);
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        String c, bg;
        switch (status) {
            case "In Stock":     c = SUCCESS; bg = "rgba(76,175,80,0.15)";  break;
            case "Low Stock":    c = WARNING; bg = "rgba(255,152,0,0.15)";  break;
            case "Out of Stock": c = ACCENT;  bg = "rgba(230,57,70,0.15)";  break;
            default:             c = TEXT_MUTED; bg = "transparent";        break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        return b;
    }

    private Label makeCatBadge(String cat) {
        Label b = new Label(cat);
        b.setFont(Font.font("Verdana", 10));
        b.setTextFill(Color.web(INFO));
        b.setStyle("-fx-background-color: rgba(33,150,243,0.13); -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        return b;
    }

    private HBox makeStatChip(String label, String value, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-border-width: 1;");
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.2)); ds.setRadius(8); ds.setOffsetY(3);
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

    private Button makePagBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setFont(Font.font("Verdana", active ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        b.setStyle("-fx-background-color: " + (active ? ACCENT : BG_CARD) + "; -fx-text-fill: " + TEXT_WHITE + "; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 12 5 12;");
        return b;
    }

    public static void main(String[] args) { launch(args); }
}
