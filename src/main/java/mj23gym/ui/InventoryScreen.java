package mj23gym.ui;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import javafx.scene.control.DatePicker;
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
    static final String INFO         = ModernDesignSystem.PRIMARY;
    static final String SUCCESS_TEXT = "#237A36";
    static final String WARNING_TEXT = "#6E6400";
    static final String ERROR_TEXT   = "#B3261E";
    static final String ERROR_BG     = "#FEE2E2";
    static final String TEXT_TITLE   = ModernDesignSystem.PRIMARY;
    static final String TEXT_SOFT    = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DARK    = ModernDesignSystem.PRIMARY_DARK;
    static final String CARD_SURFACE = ModernDesignSystem.WHITE;

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
        t1.setFill(Color.web(TEXT_TITLE));
        Text t2 = new Text("Manage gym supplies, supplements, and stock levels");
        t2.setFont(Font.font("Poppins", 11));
        t2.setFill(Color.web(TEXT_SOFT));
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
            makeStatChipText("Total Items", statTotal, TEXT_TITLE),
            makeStatChipText("In Stock", statIn, SUCCESS_TEXT),
            makeStatChipText("Low Stock", statLow, WARNING_TEXT),
            makeStatChipText("Out of Stock", statOut, TEXT_TITLE)
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
            "-fx-background-color: #FFFDF2;" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-font: 12 Poppins;" +
            "-fx-padding: 12 16 12 16;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #EFE6B8;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-cursor: hand;"
        );

        VBox tableCard = new VBox(0);
        tableCard.setStyle(
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
        tableCard.setEffect(ds);

        // Header row
        String[] headers = {"Item ID", "Item Name", "Category", "Qty", "Unit Price", "Expiry", "Stock Status", "Actions"};
        double[] colW = {8, 20, 12, 7, 10, 12, 13, 18};

        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(12, 20, 12, 20));
        tblHdr.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 12 12 0 0;" +
            "-fx-border-color: " + BORDER + " transparent transparent transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        GridPane hGrid = makeGrid(colW);
        hGrid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hGrid, Priority.ALWAYS);
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i].toUpperCase());
            h.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
            h.setStyle("-fx-text-fill: " + TEXT_SOFT + ";");
            hGrid.add(h, i, 0);
        }
        tblHdr.getChildren().add(hGrid);

        VBox rowsBox = new VBox(0);
        Text pgInfo = new Text();
        pgInfo.setFont(Font.font("Poppins", 11));
        pgInfo.setFill(Color.web(TEXT_SOFT));

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
        title.setFill(Color.web(TEXT_TITLE));
        Rectangle ul = new Rectangle(48, 3);
        ul.setFill(Color.web(ACCENT)); ul.setArcWidth(3); ul.setArcHeight(3);

        TextField nameTf = new TextField();
        TextField unitTf = new TextField();
        TextField qtyTf = new TextField();
        TextField reorderTf = new TextField();
        DatePicker expiryPicker = new DatePicker();
        expiryPicker.getEditor().setPromptText("YYYY-MM-DD optional");
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
        VBox nameBox = labeledInv("ITEM NAME", nameTf, "Enter item name");
        Label nameError = makeValidationLabel();
        nameBox.getChildren().add(nameError);
        form.add(nameBox, 0, r++, 2, 1);
        VBox catV = new VBox(6);
        Label cl = new Label("CATEGORY");
        cl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        cl.setStyle("-fx-text-fill: " + TEXT_SOFT + ";");
        catV.getChildren().addAll(cl, catBox);
        VBox priceBox = labeledInv("SELLING PRICE", unitTf, "0.00");
        Label priceError = makeValidationLabel();
        priceBox.getChildren().add(priceError);
        VBox qtyBox = labeledInv("INITIAL QUANTITY", qtyTf, "0");
        Label qtyError = makeValidationLabel();
        qtyBox.getChildren().add(qtyError);
        VBox reorderBox = labeledInv("REORDER LEVEL", reorderTf, "10");
        Label reorderError = makeValidationLabel();
        reorderBox.getChildren().add(reorderError);
        VBox expiryBox = datePickerInv("EXPIRATION DATE", expiryPicker);
        Label expiryError = makeValidationLabel();
        expiryBox.getChildren().add(expiryError);
        form.add(catV, 0, r);
        form.add(priceBox, 1, r++);
        form.add(qtyBox, 0, r);
        form.add(reorderBox, 1, r++);
        form.add(expiryBox, 0, r++, 2, 1);
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
            clearValidation(nameError, priceError, qtyError, reorderError, expiryError);
            String nm = nameTf.getText().trim();
            boolean valid = true;
            if (nm.isEmpty()) {
                setValidation(nameError, "Item name is required.");
                valid = false;
            }
            Double price = parsePositiveAmount(unitTf.getText().trim(), priceError, "Selling price");
            if (price == null) valid = false;
            Integer q = parseNonNegativeInt(qtyTf.getText().trim(), qtyError, "Initial quantity");
            if (q == null) valid = false;
            Integer reord = reorderTf.getText().trim().isEmpty() ? 10 : parseNonNegativeInt(reorderTf.getText().trim(), reorderError, "Reorder level");
            if (reord == null) valid = false;
            Date expiry = dateFromPickerOrMark(expiryPicker, expiryError);
            if (isInvalidDatePickerText(expiryPicker, expiry)) valid = false;
            if (!valid) {
                return;
            }
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
                expiry,
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
        l.setStyle("-fx-text-fill: " + TEXT_SOFT + ";");
        field.setPromptText(prompt);
        field.setPrefHeight(40);
        applyFieldStyle(field);
        g.getChildren().addAll(l, field);
        return g;
    }

    private VBox datePickerInv(String label, DatePicker picker) {
        VBox g = new VBox(6);
        Label l = new Label(label);
        l.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        l.setStyle("-fx-text-fill: " + TEXT_SOFT + ";");
        picker.setPrefHeight(40);
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;"
        );
        g.getChildren().addAll(l, picker);
        return g;
    }

    private Label makeValidationLabel() {
        Label label = new Label();
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        label.setTextFill(Color.web(ERROR_TEXT));
        label.setWrapText(true);
        label.setManaged(false);
        label.setVisible(false);
        return label;
    }

    private void setValidation(Label label, String message) {
        label.setText(message);
        label.setManaged(true);
        label.setVisible(true);
    }

    private void clearValidation(Label... labels) {
        for (Label label : labels) {
            label.setText("");
            label.setManaged(false);
            label.setVisible(false);
        }
    }

    private void invAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type);
        a.setContentText(msg);
        a.showAndWait();
    }

    private HBox makeEmptyState(String message) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24, 14, 24, 14));
        Label label = new Label(message);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        label.setTextFill(Color.web(TEXT_SOFT));
        box.getChildren().add(label);
        return box;
    }

    private HBox makeStatChipText(String label, Text valueNode, String color) {
        HBox chip = new HBox(12);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 18, 14, 18));
        chip.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow d = new DropShadow();
        d.setColor(Color.web("#000000", 0.08));
        d.setRadius(8);
        d.setOffsetY(2);
        chip.setEffect(d);
        Rectangle accent = new Rectangle(4, 36);
        accent.setArcWidth(4);
        accent.setArcHeight(4);
        accent.setFill(Color.web(color));
        valueNode.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        lbl.setFill(Color.web(TEXT_SOFT));
        chip.getChildren().addAll(accent, new VBox(2, lbl, valueNode));
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
        if (filtered.isEmpty()) {
            rowsBox.getChildren().add(makeEmptyState("No matching inventory items found."));
            return;
        }
        int r = 0;
        for (InventoryDAO.InventoryRecord it : filtered) {
            String bg = (r % 2 == 0) ? CARD_SURFACE : BG_ROW_ALT;
            HBox row = new HBox();
            row.setPadding(new Insets(11, 20, 11, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            row.setAlignment(Pos.CENTER_LEFT);
            GridPane rGrid = makeGrid(colW);
            rGrid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rGrid, Priority.ALWAYS);
            String code = "#" + it.itemCode();
            int q = it.currentStock();
            boolean needsRestock = isLowStock(it);
            boolean needsDateAttention = isExpiredOrExpiringSoon(it);
            boolean hasExpiringBatch = hasExpiringBatch(dao, it.itemId());
            rGrid.add(makeCell(code, TEXT_SOFT, true), 0, 0);
            rGrid.add(makeCell(it.itemName(), TEXT_TITLE, true), 1, 0);
            rGrid.add(makeCatBadge(it.category()), 2, 0);
            rGrid.add(makeQuantityCell(it), 3, 0);
            rGrid.add(makeCell("PHP " + String.format("%,.2f", it.sellingPrice()), TEXT_SOFT, false), 4, 0);
            rGrid.add(makeExpiryCell(dao, it), 5, 0);
            rGrid.add(makeStockBadge(it.status()), 6, 0);
            Button del = makeActionBtn("", TEXT_TITLE);
            del.setOnAction(e -> {
                Alert c = new Alert(Alert.AlertType.CONFIRMATION);
                c.setTitle("Archive Inventory Item");
                c.setHeaderText("Archive " + it.itemName() + "?");
                c.setContentText("This keeps the item history but removes it from active inventory records.");
                Optional<ButtonType> res = c.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK && dao.deactivate(it.itemId())) {
                    fullRefresh.run();
                }
            });
            del.setText(it.isActive() ? "Archive" : "Archived");
            Button edit = makeActionBtn("Edit", WARNING_TEXT);
            edit.setOnAction(e -> showEditItemDialog(it, fullRefresh));
            Button stock = makeActionBtn("Stock", TEXT_TITLE);
            stock.setOnAction(e -> showStockDialog(dao, it, fullRefresh));
            Button restore = makeActionBtn("Restore", SUCCESS_TEXT);
            restore.setOnAction(e -> {
                Alert c = new Alert(Alert.AlertType.CONFIRMATION);
                c.setTitle("Restore Inventory Item");
                c.setHeaderText("Restore " + it.itemName() + "?");
                c.setContentText("This returns the item to active inventory records.");
                Optional<ButtonType> res = c.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK && dao.reactivate(it.itemId())) {
                    fullRefresh.run();
                }
            });
            HBox actions = it.isActive() ? new HBox(6, edit, stock, del) : new HBox(6, restore);
            actions.setAlignment(Pos.CENTER_LEFT);
            rGrid.add(actions, 7, 0);
            row.getChildren().add(rGrid);
            String fBg = bg;
            if (needsRestock || needsDateAttention || hasExpiringBatch) {
                row.setCursor(javafx.scene.Cursor.HAND);
                row.setOnMouseClicked(e -> {
                    if (e.getTarget() instanceof Button) {
                        return;
                    }
                    if (hasExpiringBatch) {
                        showBatchesDialog(dao, it);
                    } else {
                        showRestockDetails(it);
                    }
                });
            }
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
        title.setFill(Color.web(TEXT_TITLE));

        TextField nameTf = new TextField(item.itemName());
        TextField descTf = new TextField(safe(item.description()));
        TextField qtyTf = new TextField(String.valueOf(item.quantity()));
        TextField stockTf = new TextField(String.valueOf(item.currentStock()));
        TextField reorderTf = new TextField(String.valueOf(item.reorderLevel()));
        TextField unitPriceTf = new TextField(String.format("%.2f", item.unitPrice()));
        TextField sellingTf = new TextField(String.format("%.2f", item.sellingPrice()));
        DatePicker expiryPicker = new DatePicker(item.expirationDate() != null ? item.expirationDate().toLocalDate() : null);
        expiryPicker.getEditor().setPromptText("YYYY-MM-DD optional");
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
        VBox editNameBox = labeledInv("ITEM NAME", nameTf, "Item name");
        Label editNameError = makeValidationLabel();
        editNameBox.getChildren().add(editNameError);
        VBox editQtyBox = labeledInv("QUANTITY", qtyTf, "0");
        Label editQtyError = makeValidationLabel();
        editQtyBox.getChildren().add(editQtyError);
        VBox editStockBox = labeledInv("CURRENT STOCK", stockTf, "0");
        Label editStockError = makeValidationLabel();
        editStockBox.getChildren().add(editStockError);
        VBox editReorderBox = labeledInv("REORDER LEVEL", reorderTf, "10");
        Label editReorderError = makeValidationLabel();
        editReorderBox.getChildren().add(editReorderError);
        VBox editUnitPriceBox = labeledInv("UNIT PRICE", unitPriceTf, "0.00");
        Label editUnitPriceError = makeValidationLabel();
        editUnitPriceBox.getChildren().add(editUnitPriceError);
        VBox editSellingBox = labeledInv("SELLING PRICE", sellingTf, "0.00");
        Label editSellingError = makeValidationLabel();
        editSellingBox.getChildren().add(editSellingError);
        VBox editExpiryBox = datePickerInv("EXPIRATION DATE", expiryPicker);
        Label editExpiryError = makeValidationLabel();
        editExpiryBox.getChildren().add(editExpiryError);
        form.add(editNameBox, 0, r++, 2, 1);
        form.add(new VBox(6, formLabel("CATEGORY"), catBox), 0, r);
        form.add(labeledInv("DESCRIPTION", descTf, "Optional"), 1, r++);
        form.add(editQtyBox, 0, r);
        form.add(editStockBox, 1, r++);
        form.add(editReorderBox, 0, r);
        form.add(editUnitPriceBox, 1, r++);
        form.add(editSellingBox, 0, r);
        form.add(labeledInv("SUPPLIER", supplierTf, "Supplier"), 1, r++);
        form.add(editExpiryBox, 0, r++, 2, 1);
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
            clearValidation(editNameError, editQtyError, editStockError, editReorderError, editUnitPriceError, editSellingError, editExpiryError);
            boolean valid = true;
            if (nameTf.getText().trim().isEmpty()) {
                setValidation(editNameError, "Item name is required.");
                valid = false;
            }
            Integer quantity = parseNonNegativeInt(qtyTf.getText().trim(), editQtyError, "Quantity");
            if (quantity == null) valid = false;
            Integer stock = parseNonNegativeInt(stockTf.getText().trim(), editStockError, "Current stock");
            if (stock == null) valid = false;
            Integer reorder = parseNonNegativeInt(reorderTf.getText().trim(), editReorderError, "Reorder level");
            if (reorder == null) valid = false;
            Double unitPrice = parseNonNegativeAmount(unitPriceTf.getText().trim(), editUnitPriceError, "Unit price");
            if (unitPrice == null) valid = false;
            Double sellingPrice = parsePositiveAmount(sellingTf.getText().trim(), editSellingError, "Selling price");
            if (sellingPrice == null) valid = false;
            Date expiry = dateFromPickerOrMark(expiryPicker, editExpiryError);
            if (isInvalidDatePickerText(expiryPicker, expiry)) valid = false;
            if (!valid) {
                return;
            }
            InventoryDAO.InventoryRecord updated = new InventoryDAO.InventoryRecord(
                item.itemId(), item.itemCode(), nameTf.getText().trim(), catBox.getValue(),
                descTf.getText().trim(), quantity, stock, reorder, reorder,
                unitPrice, sellingPrice,
                item.unitOfMeasure() != null ? item.unitOfMeasure() : "pcs",
                supplierTf.getText().trim(), item.lastRestock(), expiry, item.status(),
                notesTf.getText().trim(), item.isActive()
            );
            if (new InventoryDAO().update(updated)) {
                dialog.close();
                onSaved.run();
            } else {
                invAlert(Alert.AlertType.ERROR, "Could not update item.");
            }
        });
        btnRow.getChildren().addAll(cancel, save);
        root.getChildren().addAll(title, form, btnRow);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private void showStockDialog(InventoryDAO dao, InventoryDAO.InventoryRecord item, Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Restock Item");
        dialog.setResizable(false);

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(440);

        Text title = new Text("Restock " + item.itemName());
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_TITLE));

        TextField qtyTf = new TextField();
        TextField batchCodeTf = new TextField();
        DatePicker expiryPicker = new DatePicker(item.expirationDate() != null ? item.expirationDate().toLocalDate() : null);
        expiryPicker.getEditor().setPromptText("YYYY-MM-DD optional");
        DatePicker receivedPicker = new DatePicker(LocalDate.now());
        receivedPicker.getEditor().setPromptText("YYYY-MM-DD");
        TextField notesTf = new TextField();

        VBox qtyBox = labeledInv("BATCH QUANTITY", qtyTf, "Quantity received");
        Label qtyError = makeValidationLabel();
        qtyBox.getChildren().add(qtyError);
        VBox expiryBox = datePickerInv("EXPIRATION DATE", expiryPicker);
        Label expiryError = makeValidationLabel();
        expiryBox.getChildren().add(expiryError);
        VBox receivedBox = datePickerInv("RECEIVED DATE", receivedPicker);
        Label receivedError = makeValidationLabel();
        receivedBox.getChildren().add(receivedError);

        root.getChildren().addAll(
            title,
            qtyBox,
            labeledInv("BATCH CODE", batchCodeTf, "Optional batch or lot code"),
            expiryBox,
            receivedBox,
            labeledInv("NOTES", notesTf, "Optional")
        );

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> dialog.close());
        Button save = makeAccentBtn("Add Batch");
        save.setOnAction(e -> {
            clearValidation(qtyError, expiryError, receivedError);
            Integer qty = parseNonNegativeInt(qtyTf.getText().trim(), qtyError, "Batch quantity");
            if (qty == null || qty <= 0) {
                setValidation(qtyError, "Batch quantity must be greater than zero.");
                return;
            }
            Date expiry = dateFromPickerOrMark(expiryPicker, expiryError);
            Date received = dateFromPickerOrMark(receivedPicker, receivedError);
            boolean invalidDate = isInvalidDatePickerText(expiryPicker, expiry)
                || isInvalidDatePickerText(receivedPicker, received);
            if (invalidDate) {
                return;
            }
            int uid = AppSession.currentUser().userId();
            boolean ok = dao.insertBatch(
                item.itemId(),
                qty,
                expiry,
                received,
                batchCodeTf.getText().trim(),
                notesTf.getText().trim(),
                uid
            );
            if (ok) {
                dialog.close();
                onSaved.run();
            } else {
                invAlert(Alert.AlertType.ERROR, "Could not save restock batch.");
            }
        });
        buttons.getChildren().addAll(cancel, save);
        root.getChildren().add(buttons);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private boolean isLowStock(InventoryDAO.InventoryRecord item) {
        return item != null && item.isActive() && item.currentStock() <= item.reorderLevel();
    }

    private boolean isExpiredOrExpiringSoon(InventoryDAO.InventoryRecord item) {
        if (item == null || !item.isActive() || item.expirationDate() == null) {
            return false;
        }
        return !item.expirationDate().toLocalDate().isAfter(LocalDate.now().plusDays(30));
    }

    private StackPane makeQuantityCell(InventoryDAO.InventoryRecord item) {
        boolean needsRestock = isLowStock(item);
        StackPane cell = new StackPane();
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.setMaxWidth(Double.MAX_VALUE);

        if (needsRestock) {
            HBox qtyLine = new HBox(5);
            qtyLine.setAlignment(Pos.CENTER_LEFT);
            qtyLine.setCursor(javafx.scene.Cursor.HAND);

            Label marker = new Label("⚠");
            marker.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
            marker.setStyle("-fx-text-fill: " + ERROR_TEXT + ";");

            Label qty = new Label(String.valueOf(item.currentStock()));
            qty.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
            qty.setStyle("-fx-text-fill: " + ERROR_TEXT + ";");

            qtyLine.getChildren().addAll(marker, qty);
            cell.getChildren().add(qtyLine);
            cell.setOnMouseClicked(e -> {
                showRestockDetails(item);
                e.consume();
            });
        } else {
            Label qty = makeCell(String.valueOf(item.currentStock()), SUCCESS_TEXT, true);
            cell.getChildren().add(qty);
        }

        return cell;
    }

    private void showRestockDetails(InventoryDAO.InventoryRecord item) {
        StringBuilder details = new StringBuilder();
        details.append("Item: ").append(item.itemName()).append("\n");
        details.append("Item ID: ").append(item.itemCode()).append("\n");
        details.append("Current stock: ").append(item.currentStock()).append("\n");
        details.append("Reorder level: ").append(item.reorderLevel()).append("\n");
        details.append("Minimum stock: ").append(item.minimumStock()).append("\n");
        details.append("Recommended restock: ").append(Math.max(0, item.reorderLevel() - item.currentStock() + 1)).append("\n");
        details.append("Supplier: ").append(blankFallback(item.supplier(), "No supplier saved")).append("\n");
        details.append("Last restock: ").append(item.lastRestock() != null ? item.lastRestock() : "No date saved").append("\n");
        details.append("Expiration: ").append(expiryDetailText(item.expirationDate())).append("\n");
        details.append("Status: ").append(blankFallback(item.status(), "No status")).append("\n");
        details.append("Notes: ").append(blankFallback(item.notes(), "No notes"));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Restock Attention");
        alert.setHeaderText(attentionHeader(item));
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    private String attentionHeader(InventoryDAO.InventoryRecord item) {
        if (isLowStock(item) && isExpiredOrExpiringSoon(item)) {
            return "Low stock and expiry need attention";
        }
        if (isLowStock(item)) {
            return "Low stock: restock recommended";
        }
        if (isExpiredOrExpiringSoon(item)) {
            return "Expiration date needs attention";
        }
        return "Inventory item details";
    }

    private void updateMonitorAlert(
        Label monitorAlert,
        List<InventoryDAO.InventoryRecord> lowStock,
        List<InventoryDAO.InventoryRecord> expiring
    ) {
        if (lowStock.isEmpty() && expiring.isEmpty()) {
            monitorAlert.setText("Inventory monitor: Stock levels are healthy. No restock reminders right now.");
            monitorAlert.setStyle(
                "-fx-background-color: " + CARD_SURFACE + ";" +
                "-fx-text-fill: " + SUCCESS_TEXT + ";" +
                "-fx-font: 12 Poppins;" +
                "-fx-padding: 12 16 12 16;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: rgba(35,122,54,0.22);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;"
            );
            monitorAlert.setOnMouseClicked(null);
            return;
        }
        List<String> alerts = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (InventoryDAO.InventoryRecord item : lowStock) {
            names.add(item.itemName() + " (" + item.currentStock() + " left)");
            if (names.size() == 5) break;
        }
        if (!names.isEmpty()) {
            alerts.add("⚠ Restock needed: " + String.join(", ", names));
        }
        List<String> expiringNames = new ArrayList<>();
        for (InventoryDAO.InventoryRecord item : expiring) {
            expiringNames.add(item.itemName() + " (" + expiryText(item.expirationDate()) + ")");
            if (expiringNames.size() == 5) break;
        }
        if (!expiringNames.isEmpty()) {
            alerts.add("Expiry watch: " + String.join(", ", expiringNames));
        }
        monitorAlert.setText(String.join("    |    ", alerts) + "    Click for supplier and restock details.");
        monitorAlert.setStyle(
            "-fx-background-color: #FFFDF2;" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-font: 12 Poppins;" +
            "-fx-padding: 12 16 12 16;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #EFE6B8;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-cursor: hand;"
        );
        monitorAlert.setOnMouseClicked(e -> showInventoryMonitorDetails(lowStock, expiring));
    }

    private void showInventoryMonitorDetails(
        List<InventoryDAO.InventoryRecord> lowStock,
        List<InventoryDAO.InventoryRecord> expiring
    ) {
        StringBuilder message = new StringBuilder();
        if (!lowStock.isEmpty()) {
            message.append("RESTOCK LIST\n");
            for (InventoryDAO.InventoryRecord item : lowStock) {
                message.append("- ")
                    .append(item.itemName())
                    .append(" | Stock: ").append(item.currentStock())
                    .append(" | Reorder: ").append(item.reorderLevel())
                    .append(" | Supplier: ").append(blankFallback(item.supplier(), "No supplier saved"))
                    .append("\n");
            }
            message.append("\n");
        }
        if (!expiring.isEmpty()) {
            message.append("EXPIRATION WATCH\n");
            for (InventoryDAO.InventoryRecord item : expiring) {
                message.append("- ")
                    .append(item.itemName())
                    .append(" | ").append(expiryDetailText(item.expirationDate()))
                    .append(" | Supplier: ").append(blankFallback(item.supplier(), "No supplier saved"))
                    .append("\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inventory Restock Reminder");
        alert.setHeaderText("Restock and expiration details");
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    private Label formLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        label.setStyle("-fx-text-fill: " + TEXT_SOFT + ";");
        return label;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword.toLowerCase());
    }

    private double parseAmount(String raw) {
        if (raw == null || raw.trim().isEmpty()) return 0;
        return Double.parseDouble(raw.replace("PHP", "").replace("", "").replace(",", "").trim());
    }

    private Integer parseNonNegativeInt(String raw, Label errorLabel, String fieldName) {
        if (raw == null || raw.isBlank()) {
            setValidation(errorLabel, fieldName + " is required.");
            return null;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            if (value < 0) {
                setValidation(errorLabel, fieldName + " cannot be negative.");
                return null;
            }
            return value;
        } catch (NumberFormatException ex) {
            setValidation(errorLabel, "Use a whole number, like 0 or 10.");
            return null;
        }
    }

    private Double parsePositiveAmount(String raw, Label errorLabel, String fieldName) {
        Double value = parseNonNegativeAmount(raw, errorLabel, fieldName);
        if (value != null && value <= 0) {
            setValidation(errorLabel, fieldName + " must be greater than 0.");
            return null;
        }
        return value;
    }

    private Double parseNonNegativeAmount(String raw, Label errorLabel, String fieldName) {
        if (raw == null || raw.isBlank()) {
            setValidation(errorLabel, fieldName + " is required.");
            return null;
        }
        try {
            double value = Double.parseDouble(raw.replace("PHP", "").replace("", "").replace(",", "").trim());
            if (value < 0) {
                setValidation(errorLabel, fieldName + " cannot be negative.");
                return null;
            }
            return value;
        } catch (NumberFormatException ex) {
            setValidation(errorLabel, "Use a valid amount, like 120.00.");
            return null;
        }
    }

    private boolean isInvalidDatePickerText(DatePicker picker, Date parsed) {
        String raw = picker.getEditor().getText();
        return raw != null && !raw.isBlank() && parsed == null;
    }

    private Date dateFromPickerOrMark(DatePicker picker, Label errorLabel) {
        try {
            String raw = picker.getEditor().getText();
            if (raw == null || raw.isBlank()) {
                return null;
            }
            LocalDate value = picker.getValue() != null ? picker.getValue() : LocalDate.parse(raw.trim());
            return Date.valueOf(value);
        } catch (Exception ex) {
            setValidation(errorLabel, "Use YYYY-MM-DD, like 2026-05-18.");
            return null;
        }
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

    private String expiryDetailText(Date expirationDate) {
        if (expirationDate == null) {
            return "No expiry saved";
        }
        LocalDate expiry = expirationDate.toLocalDate();
        long days = ChronoUnit.DAYS.between(LocalDate.now(), expiry);
        if (days < 0) {
            return "Expired " + Math.abs(days) + " day(s) ago (" + expiry + ")";
        }
        if (days == 0) {
            return "Expires today (" + expiry + ")";
        }
        return "Expires in " + days + " day(s) (" + expiry + ")";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String blankFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
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
        String base =
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-prompt-text-fill: " + TEXT_SOFT + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;";
        f.setStyle(base);
        f.focusedProperty().addListener((o, old, focused) -> f.setStyle(
            base + "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";"
        ));
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;" +
            "-fx-pref-height: 38;"
        );
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

    private Button makeActionBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        btn.setMinWidth(52);
        btn.setPadding(new Insets(5, 8, 5, 8));
        String wash = buttonWash(color);
        String base =
            "-fx-background-color: " + wash + ";" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(
            base + "-fx-background-color: rgba(26,19,99,0.14);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private String buttonWash(String color) {
        if (SUCCESS_TEXT.equalsIgnoreCase(color)) return "rgba(228,255,223,0.75)";
        if (WARNING_TEXT.equalsIgnoreCase(color)) return "rgba(253,238,33,0.28)";
        if (TEXT_SOFT.equalsIgnoreCase(color)) return "rgba(119,116,155,0.12)";
        return "rgba(26,19,99,0.08)";
    }

    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        l.setStyle(
            "-fx-text-fill: " + color + ";" +
            "-fx-font-weight: " + (bold ? "bold" : "normal") + ";"
        );
        return l;
    }

    private Label makeStockBadge(String status) {
        Label b = new Label(status);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c;
        String bg;
        switch (status) {
            case "In Stock":
                c = SUCCESS_TEXT;
                bg = "rgba(228,255,223,0.75)";
                break;
            case "Low Stock":
                c = WARNING_TEXT;
                bg = "rgba(253,238,33,0.28)";
                break;
            case "Out of Stock":
                c = TEXT_TITLE;
                bg = "rgba(26,19,99,0.08)";
                break;
            default:
                c = TEXT_SOFT;
                bg = "rgba(119,116,155,0.10)";
                break;
        }
        b.setStyle(
            "-fx-text-fill: " + c + ";" +
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 4 10 4 10;"
        );
        return b;
    }

    private Label makeExpiryBadge(Date expirationDate) {
        Label b = new Label(expiryText(expirationDate));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c = TEXT_SOFT;
        String bg = "rgba(119,116,155,0.10)";
        if (expirationDate != null) {
            LocalDate expiry = expirationDate.toLocalDate();
            if (expiry.isBefore(LocalDate.now())) {
                c = TEXT_TITLE;
                bg = "rgba(26,19,99,0.08)";
            } else if (!expiry.isAfter(LocalDate.now().plusDays(30))) {
                c = WARNING_TEXT;
                bg = "rgba(253,238,33,0.28)";
            } else {
                c = SUCCESS_TEXT;
                bg = "rgba(228,255,223,0.65)";
            }
        }
        b.setStyle(
            "-fx-text-fill: " + c + ";" +
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 4 10 4 10;"
        );
        return b;
    }

    private HBox makeExpiryCell(InventoryDAO dao, InventoryDAO.InventoryRecord item) {
        HBox cell = new HBox(6);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.getChildren().add(makeExpiryBadge(item.expirationDate()));
        if (hasExpiringBatch(dao, item.itemId())) {
            Button warning = new Button("⚠");
            warning.setTooltip(new javafx.scene.control.Tooltip("Batch expiring within 30 days"));
            warning.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
            warning.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + WARNING_TEXT + ";" +
                "-fx-padding: 0 2 0 2;" +
                "-fx-cursor: hand;"
            );
            warning.setOnAction(e -> showBatchesDialog(dao, item));
            cell.getChildren().add(warning);
        }
        return cell;
    }

    private boolean hasExpiringBatch(InventoryDAO dao, int itemId) {
        LocalDate limit = LocalDate.now().plusDays(30);
        for (InventoryDAO.InventoryBatchRecord batch : dao.findBatchesByItem(itemId)) {
            if (batch.expirationDate() != null && !batch.expirationDate().toLocalDate().isAfter(limit)) {
                return true;
            }
        }
        return false;
    }

    private void showBatchesDialog(InventoryDAO dao, InventoryDAO.InventoryRecord item) {
        List<InventoryDAO.InventoryBatchRecord> batches = dao.findBatchesByItem(item.itemId());
        StringBuilder message = new StringBuilder();
        if (batches.isEmpty()) {
            message.append("No restock batches saved for this item yet.");
        } else {
            for (InventoryDAO.InventoryBatchRecord batch : batches) {
                message.append("Batch: ").append(batch.batchCode() != null ? batch.batchCode() : "-")
                    .append("\nQuantity: ").append(batch.quantity())
                    .append("\nReceived: ").append(batch.receivedDate() != null ? batch.receivedDate() : "-")
                    .append("\nExpiry: ").append(expiryDetailText(batch.expirationDate()))
                    .append("\nNotes: ").append(blankFallback(batch.notes(), "No notes"))
                    .append("\n\n");
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inventory Batches");
        alert.setHeaderText(item.itemName() + " batches");
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    private Label makeCatBadge(String cat) {
        Label b = new Label(cat != null ? cat : "Other");
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        b.setStyle(
            "-fx-text-fill: " + TEXT_SOFT + ";" +
            "-fx-background-color: rgba(119,116,155,0.12);" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 4 10 4 10;"
        );
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
