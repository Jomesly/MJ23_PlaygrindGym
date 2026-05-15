package mj23gym.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import mj23gym.dao.InventoryDAO;
import mj23gym.dao.PosDAO;

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

        InventoryDAO inventoryDAO = new InventoryDAO();
        PosDAO posDAO = new PosDAO();
        Map<Integer, Integer> cart = new LinkedHashMap<>();

        // Search + filter
        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search product...");
        searchField.setPrefHeight(38);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        applyFieldStyle(searchField);
        ComboBox<String> catFilter = new ComboBox<>();
        catFilter.getItems().addAll("All", "Supplements", "Drinks", "Snacks", "Accessories", "Equipment", "Other");
        catFilter.setValue("All"); styleCombo(catFilter);
        searchRow.getChildren().addAll(searchField, catFilter);

        ScrollPane prodScroll = new ScrollPane();
        prodScroll.setFitToWidth(true);
        prodScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        prodScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        GridPane productGrid = new GridPane();
        productGrid.setHgap(14); productGrid.setVgap(14);
        productGrid.setPadding(new Insets(4, 0, 4, 0));

        final Runnable[] refreshCartRef = new Runnable[1];

        Runnable rebuildProductGrid = () -> {
            productGrid.getChildren().clear();
            productGrid.getColumnConstraints().clear();
            for (int i = 0; i < 3; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(33.33);
                cc.setHgrow(Priority.ALWAYS);
                productGrid.getColumnConstraints().add(cc);
            }
            String q = searchField.getText().trim().toLowerCase();
            String cat = catFilter.getValue();
            List<InventoryDAO.InventoryRecord> list = inventoryDAO.findAll();
            List<InventoryDAO.InventoryRecord> show = new ArrayList<>();
            for (InventoryDAO.InventoryRecord it : list) {
                if (it.currentStock() <= 0) {
                    continue;
                }
                if (!"All".equals(cat) && (it.category() == null || !it.category().equalsIgnoreCase(cat))) {
                    continue;
                }
                if (!q.isEmpty()) {
                    String nm = (it.itemName() + " " + it.itemCode()).toLowerCase();
                    if (!nm.contains(q)) {
                        continue;
                    }
                }
                show.add(it);
            }
            int col = 0, row = 0;
            for (InventoryDAO.InventoryRecord it : show) {
                String priceStr = String.format("₱%.0f", it.sellingPrice());
                String catStr = it.category() != null ? it.category() : "Other";
                VBox card = buildProductCard(it.itemName(), priceStr, catStr, () -> {
                    cart.merge(it.itemId(), 1, Integer::sum);
                    if (refreshCartRef[0] != null) {
                        refreshCartRef[0].run();
                    }
                });
                productGrid.add(card, col, row);
                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
        };
        searchField.setOnAction(e -> rebuildProductGrid.run());
        catFilter.setOnAction(e -> rebuildProductGrid.run());

        // RIGHT: Cart + Checkout
        VBox cartCol = new VBox(14);
        cartCol.setMinWidth(300);
        cartCol.setMaxWidth(320);

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

        VBox cartItems = new VBox(0);
        Text subVal = new Text("₱0");
        subVal.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        subVal.setFill(Color.web(TEXT_WHITE));
        Text totVal = new Text("₱0");
        totVal.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        totVal.setFill(Color.web(TEXT_WHITE));

        Runnable refreshCart = () -> {
            cartItems.getChildren().clear();
            double sub = 0;
            int idx = 0;
            for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
                int itemId = e.getKey();
                int qty = e.getValue();
                InventoryDAO.InventoryRecord inv = inventoryDAO.findById(itemId).orElse(null);
                if (inv == null) {
                    continue;
                }
                double line = inv.sellingPrice() * qty;
                sub += line;
                String bg = (idx % 2 == 0) ? BG_CARD : BG_ROW_ALT;
                idx++;
                HBox ci = new HBox(8);
                ci.setPadding(new Insets(10, 16, 10, 16));
                ci.setStyle("-fx-background-color: " + bg + ";");
                ci.setAlignment(Pos.CENTER_LEFT);
                VBox nameCol = new VBox(2);
                HBox.setHgrow(nameCol, Priority.ALWAYS);
                Label name = new Label(inv.itemName());
                name.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
                name.setTextFill(Color.web(TEXT_WHITE));
                Label qtyLbl = new Label("Qty: " + qty + "  ×  " + String.format("₱%.2f", inv.sellingPrice()));
                qtyLbl.setFont(Font.font("Verdana", 10));
                qtyLbl.setTextFill(Color.web(TEXT_MUTED));
                nameCol.getChildren().addAll(name, qtyLbl);
                Label total = new Label(String.format("₱%.2f", line));
                total.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                total.setTextFill(Color.web(SUCCESS));
                Button removeBtn = new Button("✕");
                removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ACCENT + "; -fx-font-size: 11; -fx-cursor: hand; -fx-padding: 2 4 2 4;");
                int fid = itemId;
                removeBtn.setOnAction(ev -> {
                    cart.remove(fid);
                    if (refreshCartRef[0] != null) {
                        refreshCartRef[0].run();
                    }
                });
                ci.getChildren().addAll(nameCol, total, removeBtn);
                cartItems.getChildren().add(ci);
            }
            subVal.setText(String.format("₱%.2f", sub));
            totVal.setText(String.format("₱%.2f", sub));
        };
        refreshCartRef[0] = refreshCart;

        rebuildProductGrid.run();
        refreshCart.run();

        prodScroll.setContent(productGrid);
        leftCol.getChildren().addAll(searchRow, prodScroll);
        VBox.setVgrow(prodScroll, Priority.ALWAYS);

        clearCartBtn.setOnAction(e -> {
            cart.clear();
            refreshCart.run();
        });

        VBox summary = new VBox(10);
        summary.setPadding(new Insets(16, 20, 16, 20));
        summary.setStyle("-fx-border-color: " + BORDER + " transparent transparent transparent; -fx-border-width: 1 0 0 0;");

        HBox subRow = new HBox();
        subRow.setAlignment(Pos.CENTER_LEFT);
        Label sl = new Label("Subtotal");
        sl.setFont(Font.font("Verdana", 11));
        sl.setTextFill(Color.web(TEXT_MUTED));
        Region s1 = new Region(); HBox.setHgrow(s1, Priority.ALWAYS);
        subRow.getChildren().addAll(sl, s1, subVal);
        HBox totRow = new HBox();
        totRow.setAlignment(Pos.CENTER_LEFT);
        Label tl = new Label("TOTAL");
        tl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        tl.setTextFill(Color.web(TEXT_WHITE));
        Region s2 = new Region(); HBox.setHgrow(s2, Priority.ALWAYS);
        totRow.getChildren().addAll(tl, s2, totVal);
        summary.getChildren().addAll(subRow, totRow);

        Label pmLbl = new Label("PAYMENT METHOD");
        pmLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        pmLbl.setTextFill(Color.web(TEXT_MUTED));
        HBox pmBtns = new HBox(8);
        ToggleGroup tg = new ToggleGroup();
        String[] pmLabels = {"💵 Cash", "📱 GCash", "🏦 Bank"};
        String[] pmDb = {"Cash", "GCash", "Bank Transfer"};
        for (int i = 0; i < pmLabels.length; i++) {
            ToggleButton tb = new ToggleButton(pmLabels[i]);
            tb.setToggleGroup(tg);
            tb.setUserData(pmDb[i]);
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
        checkoutBtn.setOnAction(e -> {
            if (cart.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Cart is empty.");
                a.showAndWait();
                return;
            }
            List<PosDAO.SaleLine> lines = new ArrayList<>();
            for (Map.Entry<Integer, Integer> en : cart.entrySet()) {
                inventoryDAO.findById(en.getKey()).ifPresent(inv ->
                    lines.add(new PosDAO.SaleLine(inv.itemId(), inv.itemName(), en.getValue(), inv.sellingPrice()))
                );
            }
            if (lines.isEmpty()) {
                return;
            }
            ToggleButton sel = (ToggleButton) tg.getSelectedToggle();
            String method = sel != null && sel.getUserData() != null ? sel.getUserData().toString() : "Cash";
            int uid = AppSession.currentUser().userId();
            int tx = posDAO.completeSale(null, method, null, uid, lines);
            if (tx > 0) {
                cart.clear();
                refreshCart.run();
                rebuildProductGrid.run();
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setContentText("Sale saved. Transaction #" + tx);
                ok.showAndWait();
            } else {
                Alert er = new Alert(Alert.AlertType.ERROR);
                er.setContentText("Sale failed. Check stock and database.");
                er.showAndWait();
            }
        });

        summary.getChildren().addAll(pmLbl, pmBtns, checkoutBtn);
        cartCard.getChildren().addAll(cartHeader, cartItems, summary);
        cartCol.getChildren().add(cartCard);
        VBox.setVgrow(cartCard, Priority.ALWAYS);

        body.getChildren().addAll(leftCol, cartCol);

        content.getChildren().addAll(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    private VBox buildProductCard(String name, String price, String cat, Runnable onAdd) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-border-width: 1;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(8); ds.setOffsetY(3); card.setEffect(ds);

        String c = cat != null ? cat : "Other";
        Text icon = new Text(c.equals("Drinks") ? "🥤" : c.equals("Snacks") ? "🍫" : c.equals("Accessories") ? "🧤" : c.equals("Equipment") ? "🏋" : "💊");
        icon.setFont(Font.font(28));

        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        nameLbl.setTextFill(Color.web(TEXT_WHITE));
        nameLbl.setWrapText(true);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label catLbl = new Label(c);
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
        addBtn.setOnAction(e -> onAdd.run());

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
