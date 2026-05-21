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
 * MJ23 Playgrind Gym – Point of Sale (POS) Screen
 */
public class POSScreen extends Application {

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

    private final String[][] products = {
        {"Whey Protein (1 serving)", "₱120", "Supplements"},
        {"Creatine (1 serving)",     "₱85",  "Supplements"},
        {"Nature Spring Water",       "₱25",  "Drinks"},
        {"Gatorade (Blue)",           "₱55",  "Drinks"},
        {"Energy Bar",                "₱65",  "Snacks"},
        {"B-Complex Vitamins",        "₱42",  "Supplements"},
        {"Gym Gloves (M)",            "₱350", "Accessories"},
        {"Resistance Band",           "₱180", "Equipment"},
    };

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym – Point of Sale");
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
        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane badge = new StackPane(); badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42); bb.setArcWidth(10); bb.setArcHeight(10); bb.setFill(Color.web(ACCENT));
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
        for (String[] it : items) menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Point of Sale")));
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
        Text t1 = new Text("Point of Sale"); t1.setFont(Font.font("Georgia", FontWeight.BOLD, 20)); t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Process product sales and record transactions"); t2.setFont(Font.font("Verdana", 11)); t2.setFill(Color.web(TEXT_MUTED));
        pg.getChildren().addAll(t1, t2);
        topBar.getChildren().add(pg);

        // Main body — two columns: products grid | cart/checkout
        HBox body = new HBox(20);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");
        HBox.setHgrow(body, Priority.ALWAYS);

        // LEFT: Product grid
        VBox leftCol = new VBox(14);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Search + filter
        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search product...");
        searchField.setPrefHeight(38);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        applyFieldStyle(searchField);
        ComboBox<String> catFilter = new ComboBox<>();
        catFilter.getItems().addAll("All", "Supplements", "Drinks", "Snacks", "Accessories", "Equipment");
        catFilter.setValue("All"); styleCombo(catFilter);
        searchRow.getChildren().addAll(searchField, catFilter);

        // Product grid
        ScrollPane prodScroll = new ScrollPane();
        prodScroll.setFitToWidth(true);
        prodScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        prodScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        GridPane productGrid = new GridPane();
        productGrid.setHgap(14); productGrid.setVgap(14);
        productGrid.setPadding(new Insets(4, 0, 4, 0));

        for (int i = 0; i < products.length; i++) {
            String[] p = products[i];
            VBox card = buildProductCard(p[0], p[1], p[2]);
            productGrid.add(card, i % 3, i / 3);
        }

        // Column constraints for 3 columns
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(33.33);
            cc.setHgrow(Priority.ALWAYS);
            productGrid.getColumnConstraints().add(cc);
        }

        prodScroll.setContent(productGrid);
        leftCol.getChildren().addAll(searchRow, prodScroll);
        VBox.setVgrow(prodScroll, Priority.ALWAYS);

        // RIGHT: Cart + Checkout
        VBox cartCol = new VBox(14);
        cartCol.setMinWidth(300);
        cartCol.setMaxWidth(320);

        // Cart card
        VBox cartCard = new VBox(0);
        cartCard.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.3)); ds.setRadius(12); ds.setOffsetY(4); cartCard.setEffect(ds);
        VBox.setVgrow(cartCard, Priority.ALWAYS);

        HBox cartHeader = new HBox();
        cartHeader.setPadding(new Insets(16, 20, 14, 20));
        cartHeader.setAlignment(Pos.CENTER_LEFT);
        cartHeader.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        Text cartTitle = new Text("🛒  Current Order");
        cartTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        cartTitle.setFill(Color.web(TEXT_WHITE));
        Region cartSp = new Region(); HBox.setHgrow(cartSp, Priority.ALWAYS);
        Button clearCartBtn = new Button("Clear");
        clearCartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ACCENT + "; -fx-font-size: 11; -fx-cursor: hand; -fx-padding: 2 0 2 0;");
        cartHeader.getChildren().addAll(cartTitle, cartSp, clearCartBtn);

        // Cart items
        VBox cartItems = new VBox(0);
        String[][] cartData = {
            {"Nature Spring Water", "2", "₱25",  "₱50"},
            {"Whey Protein",        "1", "₱120", "₱120"},
            {"Energy Bar",          "3", "₱65",  "₱195"},
        };
        for (int i = 0; i < cartData.length; i++) {
            String bg = (i % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox ci = new HBox(8);
            ci.setPadding(new Insets(10, 16, 10, 16));
            ci.setStyle("-fx-background-color: " + bg + ";");
            ci.setAlignment(Pos.CENTER_LEFT);
            VBox nameCol = new VBox(2);
            HBox.setHgrow(nameCol, Priority.ALWAYS);
            Label name = new Label(cartData[i][0]);
            name.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            name.setTextFill(Color.web(TEXT_WHITE));
            Label qty = new Label("Qty: " + cartData[i][1] + "  ×  " + cartData[i][2]);
            qty.setFont(Font.font("Verdana", 10));
            qty.setTextFill(Color.web(TEXT_MUTED));
            nameCol.getChildren().addAll(name, qty);
            Label total = new Label(cartData[i][3]);
            total.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            total.setTextFill(Color.web(SUCCESS));
            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ACCENT + "; -fx-font-size: 11; -fx-cursor: hand; -fx-padding: 2 4 2 4;");
            ci.getChildren().addAll(nameCol, total, removeBtn);
            cartItems.getChildren().add(ci);
        }

        // Order summary
        VBox summary = new VBox(10);
        summary.setPadding(new Insets(16, 20, 16, 20));
        summary.setStyle("-fx-border-color: " + BORDER + " transparent transparent transparent; -fx-border-width: 1 0 0 0;");

        summary.getChildren().addAll(
            makeSummaryRow("Subtotal",  "₱365",  TEXT_MUTED, false),
            makeSummaryRow("Discount",  "-₱0",   SUCCESS,    false),
            makeSummaryRow("TOTAL",     "₱365",  TEXT_WHITE, true)
        );

        // Payment method
        Label pmLbl = new Label("PAYMENT METHOD");
        pmLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        pmLbl.setTextFill(Color.web(TEXT_MUTED));
        HBox pmBtns = new HBox(8);
        ToggleGroup tg = new ToggleGroup();
        String[] methods = {"💵 Cash", "📱 GCash", "🏦 Bank"};
        for (int i = 0; i < methods.length; i++) {
            ToggleButton tb = new ToggleButton(methods[i]);
            tb.setToggleGroup(tg);
            tb.setFont(Font.font("Verdana", 10));
            tb.setPrefHeight(34);
            HBox.setHgrow(tb, Priority.ALWAYS);
            tb.setMaxWidth(Double.MAX_VALUE);
            if (i == 0) tb.setSelected(true);
            styleToggleBtn(tb, i == 0);
            tb.selectedProperty().addListener((obs, old, sel) -> styleToggleBtn(tb, sel));
        }
        pmBtns.getChildren().setAll(tg.getToggles().stream().map(t -> (ToggleButton) t).toArray(ToggleButton[]::new));

        Button checkoutBtn = new Button("✔  Process Sale");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutBtn.setPrefHeight(46);
        checkoutBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        checkoutBtn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        checkoutBtn.setOnMouseEntered(e -> checkoutBtn.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        checkoutBtn.setOnMouseExited(e -> checkoutBtn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));

        summary.getChildren().addAll(pmLbl, pmBtns, checkoutBtn);
        cartCard.getChildren().addAll(cartHeader, cartItems, summary);
        cartCol.getChildren().add(cartCard);
        VBox.setVgrow(cartCard, Priority.ALWAYS);

        body.getChildren().addAll(leftCol, cartCol);

        ScrollPane bodyScroll = new ScrollPane(body);
        bodyScroll.setFitToWidth(true);
        bodyScroll.setFitToHeight(true);
        bodyScroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");

        content.getChildren().addAll(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    private VBox buildProductCard(String name, String price, String cat) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-border-width: 1;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(8); ds.setOffsetY(3); card.setEffect(ds);

        // Icon
        Text icon = new Text(cat.equals("Drinks") ? "🥤" : cat.equals("Snacks") ? "🍫" : cat.equals("Accessories") ? "🧤" : cat.equals("Equipment") ? "🏋" : "💊");
        icon.setFont(Font.font(28));

        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        nameLbl.setTextFill(Color.web(TEXT_WHITE));
        nameLbl.setWrapText(true);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label catLbl = new Label(cat);
        catLbl.setFont(Font.font("Verdana", 9));
        catLbl.setTextFill(Color.web(INFO));
        catLbl.setStyle("-fx-background-color: rgba(33,150,243,0.12); -fx-background-radius: 8; -fx-padding: 2 8 2 8;");

        Label priceLbl = new Label(price);
        priceLbl.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        priceLbl.setTextFill(Color.web(SUCCESS));

        Button addBtn = new Button("＋ Add");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setPrefHeight(30);
        addBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        addBtn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        addBtn.setOnMouseEntered(e -> addBtn.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;"));
        addBtn.setOnMouseExited(e -> addBtn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;"));

        card.getChildren().addAll(icon, nameLbl, catLbl, priceLbl, addBtn);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #253545; -fx-background-radius: 10; -fx-border-color: " + ACCENT + "; -fx-border-radius: 10; -fx-border-width: 1;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-border-width: 1;"));
        return card;
    }

    private HBox makeSummaryRow(String label, String value, String color, boolean large) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", large ? FontWeight.BOLD : FontWeight.NORMAL, large ? 13 : 11));
        lbl.setTextFill(Color.web(large ? TEXT_WHITE : TEXT_MUTED));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label val = new Label(value);
        val.setFont(Font.font("Verdana", FontWeight.BOLD, large ? 15 : 12));
        val.setTextFill(Color.web(color));
        row.getChildren().addAll(lbl, sp, val);
        return row;
    }

    private void styleToggleBtn(ToggleButton tb, boolean selected) {
        tb.setStyle("-fx-background-color: " + (selected ? ACCENT : BG_MAIN) + "; -fx-text-fill: " + (selected ? "white" : TEXT_MUTED) + "; -fx-background-radius: 8; -fx-border-color: " + (selected ? ACCENT : BORDER) + "; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-family: Verdana;");
    }

    private void applyFieldStyle(TextField f) {
        f.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Verdana; -fx-font-size: 12;");
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: " + TEXT_WHITE + "; -fx-font-family: Verdana; -fx-font-size: 12; -fx-pref-height: 38;");
    }

    public static void main(String[] args) { launch(args); }
}
