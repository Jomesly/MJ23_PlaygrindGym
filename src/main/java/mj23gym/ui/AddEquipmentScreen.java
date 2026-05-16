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

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mj23gym.dao.EquipmentDAO;

/**
 * MJ23 Playgrind Gym  Add Equipment / Equipment Management Screen
 */
public class AddEquipmentScreen extends Application {

    static final String BG_MAIN     = ModernDesignSystem.BG_LIGHT;
    static final String BG_SIDEBAR  = ModernDesignSystem.SIDEBAR_BG;
    static final String BG_CARD     = ModernDesignSystem.CARD_BG;
    static final String BG_ROW_ALT  = ModernDesignSystem.HOVER_EFFECT;
    static final String ACCENT      = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_WHITE  = ModernDesignSystem.PRIMARY;
    static final String TEXT_MUTED  = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DIM    = ModernDesignSystem.DARK_GRAY;
    static final String BORDER      = ModernDesignSystem.BORDER_COLOR;
    static final String SUCCESS     = ModernDesignSystem.SUCCESS;
    static final String WARNING     = ModernDesignSystem.ACCENT_YELLOW;
    static final String INFO        = ModernDesignSystem.PRIMARY;

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Equipment Management");
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
        topAccent.setFill(Color.web("#FDEE21"));
        HBox logoArea = buildLogoArea();
        String[][] items = {
            {"","Dashboard"},{"","Member Management"},{"","Payment & Billing"},
            {"","Inventory"},{"","Equipment"},{"","Point of Sale"},{"","Reports"}
        };
        VBox menu = new VBox(2);
        menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Equipment")));
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

        EquipmentDAO equipmentDAO = new EquipmentDAO();

        Text statTotalVal = new Text();
        Text statGoodVal = new Text();
        Text statMaintVal = new Text();
        Text statFairVal = new Text();
        for (Text t : new Text[] { statTotalVal, statGoodVal, statMaintVal, statFairVal }) {
            t.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        }

        Runnable refreshStats = () -> {
            statTotalVal.setText(String.valueOf(equipmentDAO.findAll().size()));
            statGoodVal.setText(String.valueOf(equipmentDAO.countByCondition("Good")));
            statMaintVal.setText(String.valueOf(equipmentDAO.countByCondition("Maintenance")));
            statFairVal.setText(String.valueOf(equipmentDAO.countByCondition("Fair")));
        };
        refreshStats.run();

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            makeStatChipText(" Total Equipment", statTotalVal, TEXT_WHITE),
            makeStatChipText(" Good", statGoodVal, SUCCESS),
            makeStatChipText(" Maintenance", statMaintVal, WARNING),
            makeStatChipText(" Fair", statFairVal, INFO)
        );

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);
        TextField search = new TextField();
        search.setPromptText("Search equipment...");
        search.setPrefWidth(250);
        search.setPrefHeight(38);
        applyFieldStyle(search);
        ComboBox<String> catFilter = new ComboBox<>();
        catFilter.getItems().addAll("All Types", "Cardio", "Strength", "Bodyweight", "Flexibility", "Other");
        catFilter.setValue("All Types");
        styleCombo(catFilter);
        ComboBox<String> condFilter = new ComboBox<>();
        condFilter.getItems().addAll("All Conditions", "Good", "Fair", "Maintenance", "Broken");
        condFilter.setValue("All Conditions");
        styleCombo(condFilter);
        Region ctrlSp = new Region();
        HBox.setHgrow(ctrlSp, Priority.ALWAYS);
        controls.getChildren().addAll(search, catFilter, condFilter, ctrlSp);

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

        HBox mainRow = new HBox(20);

        VBox rows = new VBox(0);
        Text pgInfo = new Text();
        pgInfo.setFont(Font.font("Poppins", 11));
        pgInfo.setFill(Color.web(TEXT_MUTED));

        double[] colW = {8, 22, 12, 7, 13, 14, 14};
        VBox tableCard = buildEquipmentTableShell(colW, rows, pgInfo);
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        TextField addName = new TextField();
        ComboBox<String> addCat = new ComboBox<>();
        addCat.getItems().addAll("Cardio", "Strength", "Bodyweight", "Flexibility", "Other");
        addCat.setValue("Cardio");
        styleCombo(addCat);
        TextField addQty = new TextField();
        addQty.setPromptText("1");
        ComboBox<String> addCond = new ComboBox<>();
        addCond.getItems().addAll("Good", "Fair", "Maintenance", "Broken");
        addCond.setValue("Good");
        styleCombo(addCond);
        TextField addPurchase = new TextField();
        addPurchase.setPromptText("Purchase date YYYY-MM-DD (optional)");
        TextField addNextMaint = new TextField();
        addNextMaint.setPromptText("Next maintenance YYYY-MM-DD (optional)");
        TextField addNotes = new TextField();
        addNotes.setPromptText("Optional notes");

        final Runnable[] refreshHolder = new Runnable[1];
        refreshHolder[0] = () -> {
            refreshEquipmentRows(
                rows,
                pgInfo,
                equipmentDAO,
                search.getText().trim(),
                catFilter.getValue(),
                condFilter.getValue(),
                colW,
                refreshHolder[0]
            );
            refreshStats.run();
            updateEquipmentMonitor(monitorAlert, equipmentDAO);
        };

        search.setOnAction(e -> refreshHolder[0].run());
        catFilter.setOnAction(e -> refreshHolder[0].run());
        condFilter.setOnAction(e -> refreshHolder[0].run());
        refreshHolder[0].run();

        VBox addCard = buildQuickAddCard(
            equipmentDAO,
            addName,
            addCat,
            addQty,
            addCond,
            addPurchase,
            addNextMaint,
            addNotes,
            refreshHolder[0]
        );
        addCard.setMinWidth(300);
        addCard.setMaxWidth(320);

        mainRow.getChildren().addAll(tableCard, addCard);
        body.getChildren().addAll(stats, controls, monitorAlert, mainRow);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return content;
    }

    private VBox buildEquipmentTableShell(double[] colW, VBox rows, Text pgInfo) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.3));
        ds.setRadius(12);
        ds.setOffsetY(4);
        card.setEffect(ds);

        String[] headers = {"ID", "Equipment Name", "Type", "Qty", "Condition", "Next Maint.", "Actions"};
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

        HBox pag = new HBox(10);
        pag.setAlignment(Pos.CENTER_RIGHT);
        pag.setPadding(new Insets(14, 20, 14, 20));
        pag.setStyle("-fx-border-color: " + BORDER + " transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        Region pgSp = new Region();
        HBox.setHgrow(pgSp, Priority.ALWAYS);
        pag.getChildren().addAll(pgInfo, pgSp);

        card.getChildren().addAll(tblHdr, rows, pag);
        return card;
    }

    private void refreshEquipmentRows(
        VBox rows,
        Text pgInfo,
        EquipmentDAO dao,
        String keyword,
        String catFilterVal,
        String condFilterVal,
        double[] colW,
        Runnable fullRefresh
    ) {
        rows.getChildren().clear();
        String q = keyword.toLowerCase();
        List<EquipmentDAO.EquipmentRecord> filtered = new ArrayList<>();
        for (EquipmentDAO.EquipmentRecord eq : dao.findAll()) {
            if (!"All Types".equals(catFilterVal)
                && (eq.category() == null || !eq.category().equalsIgnoreCase(catFilterVal))) {
                continue;
            }
            if (!"All Conditions".equals(condFilterVal)
                && (eq.condition() == null || !eq.condition().equalsIgnoreCase(condFilterVal))) {
                continue;
            }
            if (!q.isEmpty()) {
                String blob = ((eq.equipmentName() != null ? eq.equipmentName() : "") + " "
                    + (eq.equipmentCode() != null ? eq.equipmentCode() : "")).toLowerCase();
                if (!blob.contains(q)) {
                    continue;
                }
            }
            filtered.add(eq);
        }
        pgInfo.setText("Showing " + filtered.size() + " item(s)");

        int r = 0;
        for (EquipmentDAO.EquipmentRecord eq : filtered) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setPadding(new Insets(11, 20, 11, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            row.setAlignment(Pos.CENTER_LEFT);
            GridPane rg = makeGrid(colW);
            rg.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rg, Priority.ALWAYS);
            String code = "#" + eq.equipmentCode();
            String next = eq.nextMaintenance() != null ? eq.nextMaintenance().toString() : "";
            rg.add(makeCell(code, ACCENT, true), 0, 0);
            rg.add(makeCell(eq.equipmentName(), TEXT_WHITE, false), 1, 0);
            rg.add(makeTypeBadge(eq.category() != null ? eq.category() : "Other"), 2, 0);
            rg.add(makeCell(String.valueOf(eq.quantity()), TEXT_WHITE, true), 3, 0);
            rg.add(makeCondBadge(eq.condition() != null ? eq.condition() : "Good"), 4, 0);
            rg.add(makeCell(next, TEXT_MUTED, false), 5, 0);
            Button editBtn = makeActionBtn("Edit", WARNING);
            editBtn.setOnAction(e -> showEditEquipmentDialog(eq, fullRefresh));
            Button maintBtn = makeActionBtn("Maint", INFO);
            maintBtn.setOnAction(e -> showMaintenanceDialog(dao, eq, fullRefresh));

            Button delBtn = makeActionBtn("", ACCENT);
            delBtn.setText("Delete");
            delBtn.setOnAction(e -> {
                Alert c = new Alert(Alert.AlertType.CONFIRMATION);
                c.setContentText("Remove obsolete equipment " + eq.equipmentName() + " from active records?");
                Optional<ButtonType> res = c.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK && dao.deactivate(eq.equipmentId())) {
                    fullRefresh.run();
                }
            });
            HBox actions = new HBox(6, editBtn, maintBtn, delBtn);
            actions.setAlignment(Pos.CENTER_LEFT);
            rg.add(actions, 6, 0);

            row.getChildren().add(rg);
            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(26,19,99,0.06);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rows.getChildren().add(row);
            r++;
        }
    }

    private void showEditEquipmentDialog(EquipmentDAO.EquipmentRecord eq, Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.setTitle("Update Equipment");
        dialog.setResizable(false);

        VBox root = new VBox(14);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(520);

        Text title = new Text("Update Equipment");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));

        TextField name = new TextField(eq.equipmentName());
        TextField brand = new TextField(safe(eq.brandModel()));
        TextField qty = new TextField(String.valueOf(eq.quantity()));
        TextField location = new TextField(safe(eq.location()));
        TextField purchaseCost = new TextField(String.format("%.2f", eq.purchaseCost()));
        TextField purchaseDate = new TextField(dateText(eq.purchaseDate()));
        TextField lastMaint = new TextField(dateText(eq.lastMaintenance()));
        TextField nextMaint = new TextField(dateText(eq.nextMaintenance()));
        TextField maintNotes = new TextField(safe(eq.maintenanceNotes()));
        TextField notes = new TextField(safe(eq.notes()));
        ComboBox<String> category = new ComboBox<>();
        category.getItems().addAll("Cardio", "Strength", "Bodyweight", "Flexibility", "Other");
        category.setValue(eq.category() != null ? eq.category() : "Other");
        styleCombo(category);
        ComboBox<String> condition = new ComboBox<>();
        condition.getItems().addAll("Good", "Fair", "Maintenance", "Broken");
        condition.setValue(eq.condition() != null ? eq.condition() : "Good");
        styleCombo(condition);

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(12);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        form.getColumnConstraints().addAll(c1, c2);
        int r = 0;
        form.add(labeledField("EQUIPMENT NAME", name, "Name"), 0, r++, 2, 1);
        form.add(comboField("CATEGORY", category), 0, r);
        form.add(labeledField("BRAND / MODEL", brand, "Brand or model"), 1, r++);
        form.add(labeledField("QUANTITY", qty, "1"), 0, r);
        form.add(comboField("CONDITION", condition), 1, r++);
        form.add(labeledField("LOCATION", location, "Area"), 0, r);
        form.add(labeledField("PURCHASE COST", purchaseCost, "0.00"), 1, r++);
        form.add(labeledField("PURCHASE DATE", purchaseDate, "YYYY-MM-DD"), 0, r);
        form.add(labeledField("LAST MAINTENANCE", lastMaint, "YYYY-MM-DD"), 1, r++);
        form.add(labeledField("NEXT MAINTENANCE", nextMaint, "YYYY-MM-DD"), 0, r++, 2, 1);
        form.add(labeledField("MAINTENANCE NOTES", maintNotes, "Notes"), 0, r++, 2, 1);
        form.add(labeledField("GENERAL NOTES", notes, "Notes"), 0, r++, 2, 1);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setPrefHeight(38);
        cancel.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-text-fill: " + TEXT_MUTED + "; -fx-background-radius: 16;");
        cancel.setOnAction(e -> dialog.close());
        Button save = makeAccentBtn("Save Changes");
        save.setOnAction(e -> {
            try {
                if (name.getText().trim().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Equipment name is required.").showAndWait();
                    return;
                }
                EquipmentDAO.EquipmentRecord updated = new EquipmentDAO.EquipmentRecord(
                    eq.equipmentId(),
                    eq.equipmentCode(),
                    name.getText().trim(),
                    category.getValue(),
                    brand.getText().trim(),
                    parseOptionalSqlDate(purchaseDate.getText().trim()),
                    parseAmount(purchaseCost.getText()),
                    parseAmount(purchaseCost.getText()),
                    Integer.parseInt(qty.getText().trim()),
                    condition.getValue(),
                    location.getText().trim(),
                    parseOptionalSqlDate(lastMaint.getText().trim()),
                    parseOptionalSqlDate(nextMaint.getText().trim()),
                    maintNotes.getText().trim(),
                    notes.getText().trim(),
                    eq.isActive()
                );
                if (new EquipmentDAO().update(updated)) {
                    dialog.close();
                    onSaved.run();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Could not update equipment.").showAndWait();
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid quantity or cost.").showAndWait();
            }
        });
        buttons.getChildren().addAll(cancel, save);
        root.getChildren().addAll(title, form, buttons);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private void showMaintenanceDialog(EquipmentDAO dao, EquipmentDAO.EquipmentRecord eq, Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.setTitle("Schedule Maintenance");
        dialog.setResizable(false);

        VBox root = new VBox(14);
        root.setPadding(new Insets(26));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(440);

        Text title = new Text("Maintenance - " + eq.equipmentName());
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        title.setFill(Color.web(TEXT_WHITE));

        TextField type = new TextField("Preventive Maintenance");
        TextField date = new TextField(LocalDate.now().toString());
        TextField next = new TextField(eq.nextMaintenance() != null ? eq.nextMaintenance().toString() : LocalDate.now().plusMonths(1).toString());
        TextField cost = new TextField("0.00");
        TextField performedBy = new TextField();
        TextField notes = new TextField(safe(eq.maintenanceNotes()));

        VBox fields = new VBox(10,
            labeledField("TYPE", type, "Maintenance type"),
            labeledField("MAINTENANCE DATE", date, "YYYY-MM-DD"),
            labeledField("NEXT SCHEDULE", next, "YYYY-MM-DD"),
            labeledField("COST", cost, "0.00"),
            labeledField("PERFORMED BY", performedBy, "Staff or technician"),
            labeledField("NOTES", notes, "Details")
        );

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> dialog.close());
        Button save = makeAccentBtn("Save Maintenance");
        save.setOnAction(e -> {
            EquipmentDAO.MaintenanceLog log = new EquipmentDAO.MaintenanceLog(
                0,
                eq.equipmentId(),
                parseOptionalSqlDate(date.getText().trim()),
                type.getText().trim(),
                notes.getText().trim(),
                parseAmount(cost.getText()),
                performedBy.getText().trim(),
                "Completed",
                parseOptionalSqlDate(next.getText().trim()),
                notes.getText().trim()
            );
            if (log.maintenanceDate() == null) {
                new Alert(Alert.AlertType.WARNING, "Maintenance date is required in YYYY-MM-DD format.").showAndWait();
                return;
            }
            if (dao.addMaintenanceLog(log, AppSession.currentUser().userId())) {
                dialog.close();
                onSaved.run();
            } else {
                new Alert(Alert.AlertType.ERROR, "Could not save maintenance record.").showAndWait();
            }
        });
        buttons.getChildren().addAll(cancel, save);
        root.getChildren().addAll(title, fields, buttons);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private void updateEquipmentMonitor(Label monitorAlert, EquipmentDAO dao) {
        List<EquipmentDAO.EquipmentRecord> due = dao.findMaintenanceDue();
        int broken = dao.countByCondition("Broken");
        int maintenance = dao.countByCondition("Maintenance");
        if (due.isEmpty() && broken == 0 && maintenance == 0) {
            monitorAlert.setText("Equipment monitor: all active equipment is in usable condition with no upcoming maintenance alerts.");
            monitorAlert.setStyle("-fx-background-color: rgba(228,255,223,0.12); -fx-text-fill: " + SUCCESS + "; -fx-font: bold 12 Poppins; -fx-padding: 10 14; -fx-background-radius: 16; -fx-border-color: rgba(228,255,223,0.25); -fx-border-radius: 16;");
            return;
        }
        List<String> dueNames = new ArrayList<>();
        for (EquipmentDAO.EquipmentRecord eq : due) {
            dueNames.add(eq.equipmentName() + " (" + dateText(eq.nextMaintenance()) + ")");
            if (dueNames.size() == 4) break;
        }
        String dueText = dueNames.isEmpty() ? "none due soon" : String.join(", ", dueNames);
        monitorAlert.setText("Equipment monitor: " + broken + " broken, " + maintenance + " in maintenance, due soon: " + dueText);
        monitorAlert.setStyle("-fx-background-color: rgba(253,238,33,0.12); -fx-text-fill: " + WARNING + "; -fx-font: bold 12 Poppins; -fx-padding: 10 14; -fx-background-radius: 16; -fx-border-color: rgba(253,238,33,0.25); -fx-border-radius: 16;");
    }

    private VBox buildQuickAddCard(
        EquipmentDAO dao,
        TextField addName,
        ComboBox<String> addCat,
        TextField addQty,
        ComboBox<String> addCond,
        TextField addPurchase,
        TextField addNextMaint,
        TextField addNotes,
        Runnable onSaved
    ) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web(ACCENT, 0.2));
        ds.setRadius(14);
        ds.setOffsetY(4);
        card.setEffect(ds);

        Text title = new Text("Add Equipment");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        title.setFill(Color.web(TEXT_WHITE));
        Rectangle ul = new Rectangle(40, 3);
        ul.setFill(Color.web(ACCENT));
        ul.setArcWidth(3);
        ul.setArcHeight(3);

        VBox nameBox = labeledField("EQUIPMENT NAME", addName, "e.g. Treadmill");
        VBox catBox = new VBox(6);
        Label catLbl = new Label("CATEGORY");
        catLbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        catLbl.setTextFill(Color.web(TEXT_MUTED));
        addCat.setPrefHeight(40);
        catBox.getChildren().addAll(catLbl, addCat);

        VBox qtyBox = labeledField("QUANTITY", addQty, "1");
        VBox condBox = new VBox(6);
        Label condLbl = new Label("CONDITION");
        condLbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        condLbl.setTextFill(Color.web(TEXT_MUTED));
        addCond.setPrefHeight(40);
        condBox.getChildren().addAll(condLbl, addCond);

        VBox purchBox = labeledField("PURCHASE DATE (optional)", addPurchase, "YYYY-MM-DD");
        VBox nextBox = labeledField("NEXT MAINTENANCE (optional)", addNextMaint, "YYYY-MM-DD");
        VBox notesBox = labeledField("NOTES (optional)", addNotes, "Notes");

        Runnable resetForm = () -> {
            addName.clear();
            addQty.clear();
            addPurchase.clear();
            addNextMaint.clear();
            addNotes.clear();
            addCat.setValue("Cardio");
            addCond.setValue("Good");
        };

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button clear = new Button("Clear");
        clear.setPrefHeight(38);
        clear.setPadding(new Insets(0, 16, 0, 16));
        clear.setFont(Font.font("Poppins", 11));
        clear.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        clear.setOnAction(e -> resetForm.run());

        Button save = makeAccentBtn("Save");
        save.setPrefHeight(38);
        save.setPadding(new Insets(0, 20, 0, 20));
        save.setOnAction(e -> {
            String nm = addName.getText().trim();
            if (nm.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Equipment name is required.").showAndWait();
                return;
            }
            int qty;
            try {
                String qs = addQty.getText().trim();
                qty = qs.isEmpty() ? 1 : Integer.parseInt(qs);
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid quantity.").showAndWait();
                return;
            }
            Date purchase = parseOptionalSqlDate(addPurchase.getText().trim());
            Date nextMaint = parseOptionalSqlDate(addNextMaint.getText().trim());
            EquipmentDAO.EquipmentRecord rec = new EquipmentDAO.EquipmentRecord(
                0,
                "",
                nm,
                addCat.getValue(),
                "",
                purchase,
                0,
                0,
                qty,
                addCond.getValue(),
                "",
                null,
                nextMaint,
                null,
                addNotes.getText().trim(),
                true
            );
            int id = dao.insert(rec, AppSession.currentUser().userId());
            if (id > 0) {
                resetForm.run();
                onSaved.run();
                new Alert(Alert.AlertType.INFORMATION, "Equipment saved.").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Save failed. Check database connection or duplicate code.")
                    .showAndWait();
            }
        });

        btnRow.getChildren().addAll(clear, save);
        card.getChildren().addAll(
            title, ul, nameBox, catBox, qtyBox, condBox, purchBox, nextBox, notesBox, btnRow
        );
        return card;
    }

    private VBox labeledField(String label, TextField field, String prompt) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        applyFieldStyle(field);
        g.getChildren().addAll(lbl, field);
        return g;
    }

    private VBox comboField(String label, ComboBox<String> combo) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        combo.setPrefHeight(38);
        g.getChildren().addAll(lbl, combo);
        return g;
    }

    private Date parseOptionalSqlDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Date.valueOf(LocalDate.parse(raw));
        } catch (Exception ex) {
            return null;
        }
    }

    private double parseAmount(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return 0;
        }
        return Double.parseDouble(raw.replace("PHP", "").replace("", "").replace(",", "").trim());
    }

    private String dateText(Date date) {
        return date != null ? date.toString() : "";
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
        DropShadow d = new DropShadow();
        d.setColor(Color.web("#000", 0.2));
        d.setRadius(8);
        d.setOffsetY(3);
        chip.setEffect(d);
        valueNode.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", 11));
        lbl.setFill(Color.web(TEXT_MUTED));
        chip.getChildren().add(new VBox(2, lbl, valueNode));
        return chip;
    }

    private HBox buildTopBar(String title, String sub) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(18, 28, 18, 28));
        bar.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text t1 = new Text(title); t1.setFont(Font.font("Poppins", FontWeight.BOLD, 20)); t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text(sub); t2.setFont(Font.font("Poppins", 11)); t2.setFill(Color.web(TEXT_MUTED));
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
        f.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Poppins; -fx-font-size: 12;");
        f.focusedProperty().addListener((o, old, foc) -> f.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + (foc ? ACCENT : BORDER) + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Poppins; -fx-font-size: 12;"));
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-text-fill: " + TEXT_WHITE + "; -fx-font-family: Poppins; -fx-font-size: 12; -fx-pref-height: 38;");
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38); b.setPadding(new Insets(0, 18, 0, 18));
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

    private Label makeCondBadge(String cond) {
        Label b = new Label(cond);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c;
        String bg;
        switch (cond) {
            case "Good":
                c = SUCCESS;
                bg = "rgba(228,255,223,0.15)";
                break;
            case "Maintenance":
                c = ACCENT;
                bg = "rgba(26,19,99,0.15)";
                break;
            case "Broken":
                c = ACCENT;
                bg = "rgba(26,19,99,0.22)";
                break;
            case "Fair":
                c = WARNING;
                bg = "rgba(253,238,33,0.15)";
                break;
            default:
                c = TEXT_MUTED;
                bg = "transparent";
                break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 18; -fx-padding: 3 10 3 10;");
        return b;
    }

    private Label makeTypeBadge(String type) {
        Label b = new Label(type);
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
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.2)); ds.setRadius(8); ds.setOffsetY(3); chip.setEffect(ds);
        Text val = new Text(value); val.setFont(Font.font("Poppins", FontWeight.BOLD, 22)); val.setFill(Color.web(color));
        Text lbl = new Text(label); lbl.setFont(Font.font("Poppins", 11)); lbl.setFill(Color.web(TEXT_MUTED));
        chip.getChildren().add(new VBox(2, lbl, val));
        return chip;
    }

    public static void main(String[] args) { launch(args); }
}



