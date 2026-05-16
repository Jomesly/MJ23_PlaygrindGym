package mj23gym.ui;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
import mj23gym.dao.InventoryDAO;

/**
 * MJ23 Playgrind Gym  Inventory Screen
 * Paginated table of gym supplies/products with Add, Search, Edit controls.
 */
public class InventoryScreen extends Application {

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
    static final String INFO        = "#1A1363";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Inventory");
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

    // 
    // SIDEBAR
    // 
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230); sidebar.setMinWidth(230); sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web("#FDEE21"));
        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane badge = new StackPane();
        badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42);
        bb.setArcWidth(10); bb.setArcHeight(10);
        bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ");
        bt.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND");
        l1.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM");
        l2.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(badge, lt);
        String[][] items = {
            {"","Dashboard"},{"","Member Management"},{"","Payment & Billing"},
            {"","Inventory"},{"","Equipment"},{"","Point of Sale"},{"","Reports"}
        };
        VBox menu = new VBox(2);
        menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Inventory")));
        String[][] sys = {{"","Settings"},{"","Help"},{"","About"}};
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
        lbl.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        lbl.setFill(active ? Color.web(TEXT_WHITE) : Color.web(TEXT_MUTED));
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

    // 
    // MAIN CONTENT
    // 
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
        t1.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Manage gym supplies, supplements, and stock levels");
        t2.setFont(Font.font("Poppins", 11));
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

        InventoryDAO inventoryDAO = new InventoryDAO();

        Text statTotal = new Text();
        Text statIn = new Text();
        Text statLow = new Text();
        Text statOut = new Text();
        for (Text t : new Text[] { statTotal, statIn, statLow, statOut }) {
            t.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        }

        Runnable refreshStats = () -> {
            List<InventoryDAO.InventoryRecord> all = inventoryDAO.findAll();
            int total = all.size();
            int inC = 0, lowC = 0, outC = 0;
            for (InventoryDAO.InventoryRecord it : all) {
                String st = it.status();
                if ("Out of Stock".equals(st)) {
                    outC++;
                } else if ("Low Stock".equals(st)) {
                    lowC++;
                } else {
                    inC++;
                }
            }
            statTotal.setText(String.valueOf(total));
            statIn.setText(String.valueOf(inC));
            statLow.setText(String.valueOf(lowC));
            statOut.setText(String.valueOf(outC));
        };
        refreshStats.run();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            makeStatChipText(" Total Items", statTotal, TEXT_WHITE),
            makeStatChipText(" In Stock", statIn, SUCCESS),
            makeStatChipText("  Low Stock", statLow, WARNING),
            makeStatChipText(" Out of Stock", statOut, ACCENT)
        );

        //  Controls row 
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Search item name or ID...");
        search.setPrefWidth(260);
        search.setPrefHeight(38);
        applyFieldStyle(search);

        ComboBox<String> catFilter = new ComboBox<>();
        catFilter.getItems().addAll("All Categories", "Supplements", "Drinks", "Equipment", "Accessories", "Snacks", "Other");
        catFilter.setValue("All Categories");
        styleCombo(catFilter);

        ComboBox<String> stockFilter = new ComboBox<>();
        stockFilter.getItems().addAll("All Stock", "In Stock", "Low Stock", "Out of Stock", "Archived");
        stockFilter.setValue("All Stock");
        styleCombo(stockFilter);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button addBtn = makeAccentBtn("  Add Item");

        controls.getChildren().addAll(search, catFilter, stockFilter, sp, addBtn);

        Label monitorAlert = new Label();
        monitorAlert.setWrapText(true);
        monitorAlert.setStyle(
            "-fx-background-color: rgba(253,238,33,0.12);" +
            "-fx-text-fill: " + WARNING + ";" +
            "-fx-font: bold 12 Poppins;" +
            "-fx-padding: 10 14;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(253,238,33,0.25);" +
            "-fx-border-radius: 16;"
        );

        //  Table card 
        VBox tableCard = new VBox(0);
        tableCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.3)); ds.setRadius(12); ds.setOffsetY(4);
        tableCard.setEffect(ds);

        // Header row
        String[] headers = {"Item ID", "Item Name", "Category", "Qty", "Unit Price", "Expiry", "Stock Status", "Actions"};
        double[] colW = {8, 20, 12, 7, 10, 12, 13, 18};

        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(12, 20, 12, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + "; -fx-background-radius: 12 12 0 0;");
        GridPane hGrid = makeGrid(colW);
        hGrid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hGrid, Priority.ALWAYS);
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i].toUpperCase());
            h.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
            h.setTextFill(Color.web(TEXT_DIM));
            hGrid.add(h, i, 0);
        }
        tblHdr.getChildren().add(hGrid);

        VBox rowsBox = new VBox(0);
        Text pgInfo = new Text();
        pgInfo.setFont(Font.font("Poppins", 11));
        pgInfo.setFill(Color.web(TEXT_MUTED));

        final Runnable[] refreshHolder = new Runnable[1];
        refreshHolder[0] = () -> {
            refreshInventoryRows(
                rowsBox,
                pgInfo,
                inventoryDAO,
                search.getText().trim(),
                catFilter.getValue(),
                stockFilter.getValue(),
                colW,
                refreshHolder[0]
            );
            refreshStats.run();
            updateMonitorAlert(monitorAlert, inventoryDAO.findLowStock(), inventoryDAO.findExpiredOrExpiringSoon());
        };
        search.setOnAction(e -> refreshHolder[0].run());
        catFilter.setOnAction(e -> refreshHolder[0].run());
        stockFilter.setOnAction(e -> refreshHolder[0].run());
        addBtn.setOnAction(e -> showAddItemDialog(refreshHolder[0]));
        refreshHolder[0].run();

        HBox pag = new HBox(10);
        pag.setAlignment(Pos.CENTER_RIGHT);
        pag.setPadding(new Insets(14, 20, 14, 20));
        pag.setStyle("-fx-border-color: " + BORDER + " transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        Region pgSp = new Region(); HBox.setHgrow(pgSp, Priority.ALWAYS);
        pag.getChildren().addAll(pgInfo, pgSp);

        tableCard.getChildren().addAll(tblHdr, rowsBox, pag);
        body.getChildren().addAll(statsRow, controls, monitorAlert, tableCard);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    //  Add Item Dialog 
    private void showAddItemDialog(Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Item");
        dialog.setResizable(false);

        VBox root = new VBox(18);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(440);

        Text title = new Text("Add New Item");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        Rectangle ul = new Rectangle(48, 3);
        ul.setFill(Color.web(ACCENT)); ul.setArcWidth(3); ul.setArcHeight(3);

        TextField nameTf = new TextField();
        TextField unitTf = new TextField();
        TextField qtyTf = new TextField();
        TextField reorderTf = new TextField();
        TextField expiryTf = new TextField();
        TextField supplierTf = new TextField();
        TextField notesTf = new TextField();
        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll("Supplements", "Drinks", "Equipment", "Accessories", "Snacks", "Other");
        catBox.setValue("Supplements");
        styleCombo(catBox);

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(14);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        form.getColumnConstraints().addAll(c1, c2);
        int r = 0;
        form.add(labeledInv("ITEM NAME", nameTf, "Enter item name"), 0, r++, 2, 1);
        VBox catV = new VBox(6);
        Label cl = new Label("CATEGORY");
        cl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        cl.setTextFill(Color.web(TEXT_MUTED));
        catV.getChildren().addAll(cl, catBox);
        form.add(catV, 0, r);
        form.add(labeledInv("SELLING PRICE", unitTf, "0.00"), 1, r++);
        form.add(labeledInv("INITIAL QUANTITY", qtyTf, "0"), 0, r);
        form.add(labeledInv("REORDER LEVEL", reorderTf, "10"), 1, r++);
        form.add(labeledInv("EXPIRATION DATE", expiryTf, "YYYY-MM-DD optional"), 0, r++, 2, 1);
        form.add(labeledInv("SUPPLIER", supplierTf, "Supplier name"), 0, r++, 2, 1);
        form.add(labeledInv("NOTES", notesTf, "Optional"), 0, r++, 2, 1);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setPrefHeight(40); cancel.setPadding(new Insets(0, 20, 0, 20));
        cancel.setFont(Font.font("Poppins", 12));
        cancel.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-text-fill: " + TEXT_MUTED + "; -fx-background-radius: 16; -fx-cursor: hand;");
        cancel.setOnAction(e -> dialog.close());
        InventoryDAO dao = new InventoryDAO();
        Button save = makeAccentBtn("Save Item");
        save.setPrefHeight(40); save.setPadding(new Insets(0, 20, 0, 20));
        save.setOnAction(e -> {
            try {
                String nm = nameTf.getText().trim();
                if (nm.isEmpty()) {
                    invAlert(Alert.AlertType.WARNING, "Item name is required.");
                    return;
                }
                double price = Double.parseDouble(unitTf.getText().trim().replace("", "").replace(",", ""));
                int q = Integer.parseInt(qtyTf.getText().trim());
                int reord = reorderTf.getText().trim().isEmpty() ? 10 : Integer.parseInt(reorderTf.getText().trim());
                InventoryDAO.InventoryRecord rec = new InventoryDAO.InventoryRecord(
                    0,
                    "",
                    nm,
                    catBox.getValue(),
                    "",
                    q,
                    q,
                    reord,
                    reord,
                    price,
                    price,
                    "pcs",
                    supplierTf.getText().trim(),
                    null,
                    parseDate(expiryTf.getText().trim()),
                    "",
                    notesTf.getText().trim(),
                    true
                );
                int id = dao.insert(rec, AppSession.currentUser().userId());
                if (id > 0) {
                    dialog.close();
                    onSaved.run();
                } else {
                    invAlert(Alert.AlertType.ERROR, "Save failed. Check database connection.");
                }
            } catch (NumberFormatException ex) {
                invAlert(Alert.AlertType.ERROR, "Invalid number in price, quantity, or reorder level.");
            } catch (IllegalArgumentException ex) {
                invAlert(Alert.AlertType.ERROR, "Invalid expiration date. Use YYYY-MM-DD.");
            }
        });
        btnRow.getChildren().addAll(cancel, save);

        root.getChildren().addAll(title, ul, form, btnRow);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private VBox labeledInv(String label, TextField field, String prompt) {
        VBox g = new VBox(6);
        Label l = new Label(label);
        l.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_MUTED));
        field.setPromptText(prompt);
        field.setPrefHeight(40);
        applyFieldStyle(field);
        g.getChildren().addAll(l, field);
        return g;
    }

    private void invAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type);
        a.setContentText(msg);
        a.showAndWait();
    }

    private HBox makeStatChipText(String label, Text valueNode, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;");
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow d = new DropShadow(); d.setColor(Color.web("#000", 0.2)); d.setRadius(8); d.setOffsetY(3);
        chip.setEffect(d);
        valueNode.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", 11));
        lbl.setFill(Color.web(TEXT_MUTED));
        chip.getChildren().add(new VBox(2, lbl, valueNode));
        return chip;
    }

    private void refreshInventoryRows(
        VBox rowsBox,
        Text pgInfo,
        InventoryDAO dao,
        String keyword,
        String catFilterVal,
        String stockFilterVal,
        double[] colW,
        Runnable fullRefresh
    ) {
        rowsBox.getChildren().clear();
        boolean archivedView = "Archived".equals(stockFilterVal);
        List<InventoryDAO.InventoryRecord> list =
            archivedView ? dao.findArchived() : (keyword.isBlank() ? dao.findAll() : dao.search(keyword));
        List<InventoryDAO.InventoryRecord> filtered = new ArrayList<>();
        for (InventoryDAO.InventoryRecord it : list) {
            if (archivedView && !keyword.isBlank()
                && !containsIgnoreCase(it.itemName(), keyword)
                && !containsIgnoreCase(it.itemCode(), keyword)
                && !containsIgnoreCase(it.category(), keyword)) {
                continue;
            }
            if (!"All Categories".equals(catFilterVal)
                && (it.category() == null || !it.category().equalsIgnoreCase(catFilterVal))) {
                continue;
            }
            if (!archivedView && !"All Stock".equals(stockFilterVal)
                && (it.status() == null || !it.status().equalsIgnoreCase(stockFilterVal))) {
                continue;
            }
            filtered.add(it);
        }
        pgInfo.setText("Showing " + filtered.size() + " item(s)");
        int r = 0;
        for (InventoryDAO.InventoryRecord it : filtered) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setPadding(new Insets(11, 20, 11, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            row.setAlignment(Pos.CENTER_LEFT);
            GridPane rGrid = makeGrid(colW);
            rGrid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rGrid, Priority.ALWAYS);
            String code = "#" + it.itemCode();
            int q = it.currentStock();
            String qtyColor = q <= it.reorderLevel() ? ACCENT : TEXT_WHITE;
            rGrid.add(makeCell(code, ACCENT, true), 0, 0);
            rGrid.add(makeCell(it.itemName(), TEXT_WHITE, false), 1, 0);
            rGrid.add(makeCatBadge(it.category()), 2, 0);
            rGrid.add(makeCell(String.valueOf(q), qtyColor, true), 3, 0);
            rGrid.add(makeCell(String.format("%.2f", it.sellingPrice()), TEXT_MUTED, false), 4, 0);
            rGrid.add(makeExpiryBadge(it.expirationDate()), 5, 0);
            rGrid.add(makeStockBadge(it.status()), 6, 0);
            Button del = makeActionBtn("", ACCENT);
            del.setOnAction(e -> {
                Alert c = new Alert(Alert.AlertType.CONFIRMATION);
                c.setContentText("Archive " + it.itemName() + "? It will be removed from active inventory but kept for reference.");
                Optional<ButtonType> res = c.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK && dao.deactivate(it.itemId())) {
                    fullRefresh.run();
                }
            });
            del.setText(it.isActive() ? "Archive" : "Archived");
            Button edit = makeActionBtn("Edit", WARNING);
            edit.setOnAction(e -> showEditItemDialog(it, fullRefresh));
            Button stock = makeActionBtn("Stock", INFO);
            stock.setOnAction(e -> showStockDialog(dao, it, fullRefresh));
            Button restore = makeActionBtn("Restore", SUCCESS);
            restore.setOnAction(e -> {
                if (dao.reactivate(it.itemId())) {
                    fullRefresh.run();
                }
            });
            HBox actions = it.isActive() ? new HBox(6, edit, stock, del) : new HBox(6, restore);
            actions.setAlignment(Pos.CENTER_LEFT);
            rGrid.add(actions, 7, 0);
            row.getChildren().add(rGrid);
            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(26,19,99,0.06);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(row);
            r++;
        }
    }

    //  Helpers 
    private void showEditItemDialog(InventoryDAO.InventoryRecord item, Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Item");
        dialog.setResizable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(500);

        Text title = new Text("Update Inventory Item");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));

        TextField nameTf = new TextField(item.itemName());
        TextField descTf = new TextField(safe(item.description()));
        TextField qtyTf = new TextField(String.valueOf(item.quantity()));
        TextField stockTf = new TextField(String.valueOf(item.currentStock()));
        TextField reorderTf = new TextField(String.valueOf(item.reorderLevel()));
        TextField unitPriceTf = new TextField(String.format("%.2f", item.unitPrice()));
        TextField sellingTf = new TextField(String.format("%.2f", item.sellingPrice()));
        TextField expiryTf = new TextField(item.expirationDate() != null ? item.expirationDate().toString() : "");
        TextField supplierTf = new TextField(safe(item.supplier()));
        TextField notesTf = new TextField(safe(item.notes()));
        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll("Supplements", "Drinks", "Equipment", "Accessories", "Snacks", "Other");
        catBox.setValue(item.category() != null ? item.category() : "Other");
        styleCombo(catBox);

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        form.getColumnConstraints().addAll(c1, c2);

        int r = 0;
        form.add(labeledInv("ITEM NAME", nameTf, "Item name"), 0, r++, 2, 1);
        form.add(new VBox(6, formLabel("CATEGORY"), catBox), 0, r);
        form.add(labeledInv("DESCRIPTION", descTf, "Optional"), 1, r++);
        form.add(labeledInv("QUANTITY", qtyTf, "0"), 0, r);
        form.add(labeledInv("CURRENT STOCK", stockTf, "0"), 1, r++);
        form.add(labeledInv("REORDER LEVEL", reorderTf, "10"), 0, r);
        form.add(labeledInv("UNIT PRICE", unitPriceTf, "0.00"), 1, r++);
        form.add(labeledInv("SELLING PRICE", sellingTf, "0.00"), 0, r);
        form.add(labeledInv("SUPPLIER", supplierTf, "Supplier"), 1, r++);
        form.add(labeledInv("EXPIRATION DATE", expiryTf, "YYYY-MM-DD optional"), 0, r++, 2, 1);
        form.add(labeledInv("NOTES", notesTf, "Optional"), 0, r++, 2, 1);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setPrefHeight(40);
        cancel.setPadding(new Insets(0, 20, 0, 20));
        cancel.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-text-fill: " + TEXT_MUTED + "; -fx-background-radius: 16;");
        cancel.setOnAction(e -> dialog.close());
        Button save = makeAccentBtn("Update Item");
        save.setOnAction(e -> {
            try {
                if (nameTf.getText().trim().isEmpty()) {
                    invAlert(Alert.AlertType.WARNING, "Item name is required.");
                    return;
                }
                int quantity = Integer.parseInt(qtyTf.getText().trim());
                int stock = Integer.parseInt(stockTf.getText().trim());
                int reorder = Integer.parseInt(reorderTf.getText().trim());
                InventoryDAO.InventoryRecord updated = new InventoryDAO.InventoryRecord(
                    item.itemId(), item.itemCode(), nameTf.getText().trim(), catBox.getValue(),
                    descTf.getText().trim(), quantity, stock, reorder, reorder,
                    parseAmount(unitPriceTf.getText()), parseAmount(sellingTf.getText()),
                    item.unitOfMeasure() != null ? item.unitOfMeasure() : "pcs",
                    supplierTf.getText().trim(), item.lastRestock(), parseDate(expiryTf.getText().trim()), item.status(),
                    notesTf.getText().trim(), item.isActive()
                );
                if (new InventoryDAO().update(updated)) {
                    dialog.close();
                    onSaved.run();
                } else {
                    invAlert(Alert.AlertType.ERROR, "Could not update item.");
                }
            } catch (NumberFormatException ex) {
                invAlert(Alert.AlertType.ERROR, "Invalid quantity, stock, reorder level, or price.");
            } catch (IllegalArgumentException ex) {
                invAlert(Alert.AlertType.ERROR, "Invalid expiration date. Use YYYY-MM-DD.");
            }
        });
        btnRow.getChildren().addAll(cancel, save);
        root.getChildren().addAll(title, form, btnRow);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private void showStockDialog(InventoryDAO dao, InventoryDAO.InventoryRecord item, Runnable onSaved) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(item.currentStock()));
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Set current stock for " + item.itemName());
        dialog.setContentText("Current stock:");
        Optional<String> value = dialog.showAndWait();
        if (value.isPresent()) {
            try {
                int stock = Integer.parseInt(value.get().trim());
                if (stock < 0) {
                    invAlert(Alert.AlertType.WARNING, "Stock cannot be negative.");
                    return;
                }
                if (dao.adjustStock(item.itemId(), stock)) {
                    onSaved.run();
                } else {
                    invAlert(Alert.AlertType.ERROR, "Could not update stock.");
                }
            } catch (NumberFormatException ex) {
                invAlert(Alert.AlertType.ERROR, "Enter a valid stock quantity.");
            }
        }
    }

    private void updateMonitorAlert(
        Label monitorAlert,
        List<InventoryDAO.InventoryRecord> lowStock,
        List<InventoryDAO.InventoryRecord> expiring
    ) {
        if (lowStock.isEmpty() && expiring.isEmpty()) {
            monitorAlert.setText("Inventory monitor: All active stock levels are above reorder level.");
            monitorAlert.setStyle("-fx-background-color: rgba(228,255,223,0.12); -fx-text-fill: " + SUCCESS + "; -fx-font: bold 12 Poppins; -fx-padding: 10 14; -fx-background-radius: 16; -fx-border-color: rgba(228,255,223,0.25); -fx-border-radius: 16;");
            return;
        }
        List<String> alerts = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (InventoryDAO.InventoryRecord item : lowStock) {
            names.add(item.itemName() + " (" + item.currentStock() + " left)");
            if (names.size() == 5) break;
        }
        if (!names.isEmpty()) {
            alerts.add("Restock: " + String.join(", ", names));
        }
        List<String> expiringNames = new ArrayList<>();
        for (InventoryDAO.InventoryRecord item : expiring) {
            expiringNames.add(item.itemName() + " (" + expiryText(item.expirationDate()) + ")");
            if (expiringNames.size() == 5) break;
        }
        if (!expiringNames.isEmpty()) {
            alerts.add("Expiry: " + String.join(", ", expiringNames));
        }
        monitorAlert.setText(String.join(" | ", alerts));
        monitorAlert.setStyle("-fx-background-color: rgba(253,238,33,0.12); -fx-text-fill: " + WARNING + "; -fx-font: bold 12 Poppins; -fx-padding: 10 14; -fx-background-radius: 16; -fx-border-color: rgba(253,238,33,0.25); -fx-border-radius: 16;");
    }

    private Label formLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        label.setTextFill(Color.web(TEXT_MUTED));
        return label;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword.toLowerCase());
    }

    private double parseAmount(String raw) {
        if (raw == null || raw.trim().isEmpty()) return 0;
        return Double.parseDouble(raw.replace("PHP", "").replace("", "").replace(",", "").trim());
    }

    private Date parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return Date.valueOf(LocalDate.parse(raw.trim()));
    }

    private String expiryText(Date expirationDate) {
        if (expirationDate == null) return "No expiry";
        LocalDate expiry = expirationDate.toLocalDate();
        if (expiry.isBefore(LocalDate.now())) return "Expired " + expiry;
        return expiry.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

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
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
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
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;");
        f.focusedProperty().addListener((o, old, focused) -> f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;"));
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;" +
            "-fx-pref-height: 38;");
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38);
        b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
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
        l.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        l.setTextFill(Color.web(color));
        return l;
    }

    private Label makeStockBadge(String status) {
        Label b = new Label(status);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c, bg;
        switch (status) {
            case "In Stock":     c = SUCCESS; bg = "rgba(228,255,223,0.15)";  break;
            case "Low Stock":    c = ACCENT; bg = "rgba(26,19,99,0.15)";  break;
            case "Out of Stock": c = ACCENT;  bg = "rgba(26,19,99,0.15)";  break;
            default:             c = TEXT_MUTED; bg = "transparent";        break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 18; -fx-padding: 3 10 3 10;");
        return b;
    }

    private Label makeExpiryBadge(Date expirationDate) {
        Label b = new Label(expiryText(expirationDate));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c = TEXT_MUTED;
        String bg = "rgba(255,255,255,0.06)";
        if (expirationDate != null) {
            LocalDate expiry = expirationDate.toLocalDate();
            if (expiry.isBefore(LocalDate.now())) {
                c = ACCENT;
                bg = "rgba(26,19,99,0.15)";
            } else if (!expiry.isAfter(LocalDate.now().plusDays(30))) {
                c = WARNING;
                bg = "rgba(253,238,33,0.15)";
            } else {
                c = SUCCESS;
                bg = "rgba(228,255,223,0.12)";
            }
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 18; -fx-padding: 3 10 3 10;");
        return b;
    }

    private Label makeCatBadge(String cat) {
        Label b = new Label(cat);
        b.setFont(Font.font("Poppins", 10));
        b.setTextFill(Color.web(INFO));
        b.setStyle("-fx-background-color: rgba(119,116,155,0.13); -fx-background-radius: 18; -fx-padding: 3 10 3 10;");
        return b;
    }

    private HBox makeStatChip(String label, String value, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 18; -fx-border-color: " + BORDER + "; -fx-border-radius: 18; -fx-border-width: 1;");
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.2)); ds.setRadius(8); ds.setOffsetY(3);
        chip.setEffect(ds);
        Text val = new Text(value);
        val.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        val.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", 11));
        lbl.setFill(Color.web(TEXT_MUTED));
        chip.getChildren().add(new VBox(2, lbl, val));
        return chip;
    }

    private Button makePagBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        b.setStyle("-fx-background-color: " + (active ? ACCENT : BG_CARD) + "; -fx-text-fill: " + TEXT_WHITE + "; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 12 5 12;");
        return b;
    }

    public static void main(String[] args) { launch(args); }
}



