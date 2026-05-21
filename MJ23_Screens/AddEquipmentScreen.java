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
 * MJ23 Playgrind Gym – Add Equipment / Equipment Management Screen
 */
public class AddEquipmentScreen extends Application {

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

    private final String[][] equipment = {
        {"#EQ-001", "Treadmill",          "Cardio",    "2", "Good",        "2025-07-01"},
        {"#EQ-002", "Cable Machine",      "Strength",  "1", "Maintenance", "2025-06-15"},
        {"#EQ-003", "Barbell Set",        "Strength",  "4", "Good",        "2025-08-01"},
        {"#EQ-004", "Dumbbell Rack",      "Strength",  "1", "Good",        "2025-09-01"},
        {"#EQ-005", "Bench Press",        "Strength",  "2", "Good",        "2025-07-15"},
        {"#EQ-006", "Stationary Bike",    "Cardio",    "2", "Good",        "2025-08-01"},
        {"#EQ-007", "Pull-Up Bar",        "Bodyweight","3", "Good",        "2025-10-01"},
        {"#EQ-008", "Leg Press Machine",  "Strength",  "1", "Fair",        "2025-06-20"},
    };

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym – Equipment Management");
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
        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web(ACCENT));
        HBox logoArea = buildLogoArea();
        String[][] items = {
            {"🏠","Dashboard"},{"👥","Member Management"},{"💳","Payment & Billing"},
            {"📦","Inventory"},{"🏋","Equipment"},{"🛒","Point of Sale"},{"📊","Reports"}
        };
        VBox menu = new VBox(2);
        menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Equipment")));
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

    private HBox buildLogoArea() {
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
        return logoArea;
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

    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox topBar = buildTopBar("Equipment Management",
            "Track gym equipment, conditions, and maintenance schedules");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(20);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Stats
        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            makeStatChip("🏋 Total Equipment", "8",  TEXT_WHITE),
            makeStatChip("✅ Good Condition",   "6",  SUCCESS),
            makeStatChip("🔧 Maintenance",      "1",  WARNING),
            makeStatChip("⚠  Fair Condition",   "1",  INFO)
        );

        // Controls
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);
        TextField search = new TextField();
        search.setPromptText("🔍  Search equipment...");
        search.setPrefWidth(250); search.setPrefHeight(38);
        applyFieldStyle(search);
        ComboBox<String> catFilter = new ComboBox<>();
        catFilter.getItems().addAll("All Types", "Cardio", "Strength", "Bodyweight");
        catFilter.setValue("All Types"); styleCombo(catFilter);
        ComboBox<String> condFilter = new ComboBox<>();
        condFilter.getItems().addAll("All Conditions", "Good", "Fair", "Maintenance");
        condFilter.setValue("All Conditions"); styleCombo(condFilter);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button addBtn = makeAccentBtn("＋  Add Equipment");
        addBtn.setOnAction(e -> showAddEquipmentDialog());
        controls.getChildren().addAll(search, catFilter, condFilter, sp, addBtn);

        // Two-column layout: table + add form
        HBox mainRow = new HBox(20);

        // Equipment table
        VBox tableCard = buildEquipmentTable();
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        // Quick Add form card
        VBox addCard = buildAddFormCard();
        addCard.setMinWidth(300);
        addCard.setMaxWidth(320);

        mainRow.getChildren().addAll(tableCard, addCard);
        body.getChildren().addAll(stats, controls, mainRow);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    private VBox buildEquipmentTable() {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.3)); ds.setRadius(12); ds.setOffsetY(4);
        card.setEffect(ds);

        String[] headers = {"ID", "Equipment Name", "Type", "Qty", "Condition", "Next Maint.", "Actions"};
        double[] colW = {8, 22, 12, 7, 13, 14, 14};

        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(12, 20, 12, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + "; -fx-background-radius: 12 12 0 0;");
        GridPane hGrid = makeGrid(colW);
        hGrid.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(hGrid, Priority.ALWAYS);
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i].toUpperCase());
            h.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
            h.setTextFill(Color.web(TEXT_DIM));
            hGrid.add(h, i, 0);
        }
        tblHdr.getChildren().add(hGrid);

        VBox rows = new VBox(0);
        for (int r = 0; r < equipment.length; r++) {
            String[] eq = equipment[r];
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setPadding(new Insets(11, 20, 11, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            row.setAlignment(Pos.CENTER_LEFT);
            GridPane rg = makeGrid(colW);
            rg.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(rg, Priority.ALWAYS);
            rg.add(makeCell(eq[0], ACCENT, true), 0, 0);
            rg.add(makeCell(eq[1], TEXT_WHITE, false), 1, 0);
            rg.add(makeTypeBadge(eq[2]), 2, 0);
            rg.add(makeCell(eq[3], TEXT_WHITE, true), 3, 0);
            rg.add(makeCondBadge(eq[4]), 4, 0);
            rg.add(makeCell(eq[5], TEXT_MUTED, false), 5, 0);
            HBox actions = new HBox(6);
            actions.setAlignment(Pos.CENTER_LEFT);
            actions.getChildren().addAll(
                makeActionBtn("✏", WARNING),
                makeActionBtn("🔧", INFO),
                makeActionBtn("🗑", ACCENT)
            );
            rg.add(actions, 6, 0);
            row.getChildren().add(rg);
            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(230,57,70,0.06);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rows.getChildren().add(row);
        }
        card.getChildren().addAll(tblHdr, rows);
        return card;
    }

    private VBox buildAddFormCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web(ACCENT, 0.2)); ds.setRadius(14); ds.setOffsetY(4);
        card.setEffect(ds);

        Text title = new Text("Add Equipment");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        title.setFill(Color.web(TEXT_WHITE));
        Rectangle ul = new Rectangle(40, 3);
        ul.setFill(Color.web(ACCENT)); ul.setArcWidth(3); ul.setArcHeight(3);

        card.getChildren().addAll(title, ul,
            buildFG("EQUIPMENT NAME", "e.g. Treadmill"),
            buildFG("TYPE / CATEGORY", "e.g. Cardio"),
            buildFG("QUANTITY", "Enter quantity"),
            buildFG("CONDITION", "Good / Fair / Poor"),
            buildFG("DATE ACQUIRED", "YYYY-MM-DD"),
            buildFG("NEXT MAINTENANCE", "YYYY-MM-DD"),
            buildFG("NOTES", "Optional notes...")
        );

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button clear = new Button("Clear");
        clear.setPrefHeight(38); clear.setPadding(new Insets(0, 16, 0, 16));
        clear.setFont(Font.font("Verdana", 11));
        clear.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-text-fill: " + TEXT_MUTED + "; -fx-background-radius: 8; -fx-cursor: hand;");
        Button save = makeAccentBtn("Save");
        save.setPrefHeight(38); save.setPadding(new Insets(0, 20, 0, 20));
        btnRow.getChildren().addAll(clear, save);
        card.getChildren().add(btnRow);
        return card;
    }

    private VBox buildFG(String label, String prompt) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        TextField tf = new TextField();
        tf.setPromptText(prompt); tf.setPrefHeight(38);
        applyFieldStyle(tf);
        g.getChildren().addAll(lbl, tf);
        return g;
    }

    private void showAddEquipmentDialog() { /* same as inline form */ }

    private HBox buildTopBar(String title, String sub) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(18, 28, 18, 28));
        bar.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text t1 = new Text(title); t1.setFont(Font.font("Georgia", FontWeight.BOLD, 20)); t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text(sub); t2.setFont(Font.font("Verdana", 11)); t2.setFill(Color.web(TEXT_MUTED));
        pg.getChildren().addAll(t1, t2);
        bar.getChildren().add(pg);
        return bar;
    }

    private GridPane makeGrid(double[] widths) {
        GridPane g = new GridPane();
        for (double w : widths) { ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(w); cc.setHgrow(Priority.ALWAYS); g.getColumnConstraints().add(cc); }
        return g;
    }

    private void applyFieldStyle(TextField f) {
        f.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Verdana; -fx-font-size: 12;");
        f.focusedProperty().addListener((o, old, foc) -> f.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + (foc ? ACCENT : BORDER) + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Verdana; -fx-font-size: 12;"));
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: " + TEXT_WHITE + "; -fx-font-family: Verdana; -fx-font-size: 12; -fx-pref-height: 38;");
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38); b.setPadding(new Insets(0, 18, 0, 18));
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

    private Label makeCondBadge(String cond) {
        Label b = new Label(cond);
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        String c, bg;
        switch (cond) {
            case "Good":        c = SUCCESS; bg = "rgba(76,175,80,0.15)";  break;
            case "Maintenance": c = ACCENT;  bg = "rgba(230,57,70,0.15)";  break;
            default:            c = WARNING; bg = "rgba(255,152,0,0.15)";  break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        return b;
    }

    private Label makeTypeBadge(String type) {
        Label b = new Label(type);
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
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.2)); ds.setRadius(8); ds.setOffsetY(3); chip.setEffect(ds);
        Text val = new Text(value); val.setFont(Font.font("Georgia", FontWeight.BOLD, 22)); val.setFill(Color.web(color));
        Text lbl = new Text(label); lbl.setFont(Font.font("Verdana", 11)); lbl.setFill(Color.web(TEXT_MUTED));
        chip.getChildren().add(new VBox(2, lbl, val));
        return chip;
    }

    public static void main(String[] args) { launch(args); }
}
