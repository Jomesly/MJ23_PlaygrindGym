package mj23gym.ui;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import mj23gym.dao.MemberDAO;
import mj23gym.dao.PlanDAO;

/**
 * MJ23 Playgrind Gym  Member Management Screen
 * Searchable, filterable member table with Add / Edit / View controls.
 */
public class MemberManagementScreen extends Application {

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
    static final String SUCCESS_TEXT = "#237A36";
    static final String WARNING_TEXT = "#6E6400";
    static final String ERROR_TEXT   = "#B3261E";
    private static final DateTimeFormatter CHECK_IN_TIME_FORMAT =
        DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Member Management");

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
    // SIDEBAR (same structure as Dashboard)
    // 
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");

        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web("#FDEE21"));

        // Logo
        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane logoBadge = new StackPane();
        logoBadge.setPrefSize(42, 42);
        Rectangle logoBg = new Rectangle(42, 42);
        logoBg.setArcWidth(10); logoBg.setArcHeight(10);
        logoBg.setFill(Color.web(ACCENT));
        Text logoTxt = new Text("MJ");
        logoTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        logoTxt.setFill(Color.WHITE);
        logoBadge.getChildren().addAll(logoBg, logoTxt);
        VBox logoText = new VBox(1);
        Text g1 = new Text("MJ23 PLAYGRIND");
        g1.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        g1.setFill(Color.web(TEXT_WHITE));
        outlineText(g1, 0.22);
        Text g2 = new Text("GYM");
        g2.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        g2.setFill(Color.web(ACCENT));
        outlineText(g2, 0.22);
        logoText.getChildren().addAll(g1, g2);
        logoArea.getChildren().addAll(logoBadge, logoText);

        // Menu items
        String[][] items = {
            {"","Dashboard"},{"","Member Management"},{"","Payment & Billing"},
            {"","Inventory"},{"","Equipment"},{"","Point of Sale"},{"","Reports"}
        };
        VBox menuBox = new VBox(2);
        menuBox.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items) {
            boolean active = it[1].equals("Member Management");
            menuBox.getChildren().add(buildMenuItem(it[0], it[1], active));
        }

        String[][] sysItems = {{"","Settings"},{"","Help"},{"","About"}};
        VBox sysBox = new VBox(2);
        sysBox.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sysItems)
            sysBox.getChildren().add(buildMenuItem(it[0], it[1], false));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Rectangle div1 = new Rectangle(230, 1);
        div1.setFill(Color.web(BORDER));
        Rectangle div2 = new Rectangle(230, 1);
        div2.setFill(Color.web(BORDER));

        Label menuLbl = makeSecLabel("MAIN MENU");
        Label sysLbl  = makeSecLabel("SYSTEM");

        sidebar.getChildren().addAll(
            topAccent, logoArea, div1, menuLbl, menuBox,
            div2, sysLbl, sysBox, spacer
        );
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
        if (active) {
            item.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 16;");
        } else {
            item.setStyle("-fx-background-color: transparent; -fx-background-radius: 16;");
            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 16;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent; -fx-background-radius: 16;"));
        }
        return item;
    }

    private Label makeSecLabel(String t) {
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
        topBar.setSpacing(16);
        topBar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        VBox pg = new VBox(2);
        Text pgT = new Text("Member Management");
        pgT.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        pgT.setFill(Color.web(TEXT_WHITE));
        outlineText(pgT, 0.28);
        Text pgS = new Text("Manage gym members, attendance, and membership plans");
        pgS.setFont(Font.font("Poppins", 11));
        pgS.setFill(Color.web(TEXT_DIM));
        pg.getChildren().addAll(pgT, pgS);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        topBar.getChildren().addAll(pg, sp);

        // Body
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(20);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        MemberDAO memberDAO = new MemberDAO();

        //  Stats row 
        Text statTotalVal = new Text();
        Text statActiveVal = new Text();
        Text statExpiredVal = new Text();
        Text statRenewVal = new Text();
        styleStatValue(statTotalVal);
        styleStatValue(statActiveVal);
        styleStatValue(statExpiredVal);
        styleStatValue(statRenewVal);

        Runnable refreshStats = () -> {
            int total = memberDAO.countAll();
            int active = memberDAO.countByStatus("Active");
            int expired = memberDAO.countByStatus("Expired");
            LocalDate today = LocalDate.now();
            int renewals = 0;
            for (MemberDAO.MemberRecord m : memberDAO.findAll()) {
                if (m.membershipEndDate() != null
                    && m.membershipEndDate().toLocalDate().equals(today)) {
                    renewals++;
                }
            }
            statTotalVal.setText(String.valueOf(total));
            statActiveVal.setText(String.valueOf(active));
            statExpiredVal.setText(String.valueOf(expired));
            statRenewVal.setText(String.valueOf(renewals));
        };
        refreshStats.run();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            makeStatChipText(" Total Members", statTotalVal, TEXT_WHITE),
            makeStatChipText(" Active", statActiveVal, SUCCESS_TEXT),
            makeStatChipText(" Expired", statExpiredVal, ACCENT),
            makeStatChipText(" Renewals Today", statRenewVal, WARNING_TEXT)
        );

        //  Controls row 
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Search member name or ID...");
        search.setPrefWidth(280);
        search.setPrefHeight(38);
        search.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 14 0 14;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.70), 1, 0.0, 0, 0);"
        );

        ComboBox<String> filterPlan = new ComboBox<>();
        filterPlan.getItems().add("All Plans");
        filterPlan.getItems().addAll(new PlanDAO().activePlanNames());
        filterPlan.setValue("All Plans");
        styleCombo(filterPlan);

        ComboBox<String> filterStatus = new ComboBox<>();
        filterStatus.getItems().addAll("All Status", "Active", "Expired", "Suspended", "Archived");
        filterStatus.setValue("All Status");
        styleCombo(filterStatus);

        Region ctrlSp = new Region();
        HBox.setHgrow(ctrlSp, Priority.ALWAYS);

        Button addBtn = new Button("Add Member");
        addBtn.setPrefHeight(38);
        addBtn.setPadding(new Insets(0, 18, 0, 18));
        addBtn.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        addBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(
            "-fx-background-color: " + ACCENT_DARK + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        ));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        ));

        controls.getChildren().addAll(search, filterPlan, filterStatus, ctrlSp, addBtn);

        //  Table card 
        VBox tableCard = new VBox(0);
        tableCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.3));
        ds.setRadius(12); ds.setOffsetY(4);
        tableCard.setEffect(ds);

        // Table header row
        String[] headers = {"Member ID","Full Name","Gender","Phone","Plan","Registered","Status","Actions"};
        double[] colWidths = {7, 14, 6, 10, 8, 9, 9, 37};

        // Header
        HBox tableHeaderRow = new HBox();
        tableHeaderRow.setPadding(new Insets(12, 16, 12, 16));
        tableHeaderRow.setStyle(
            "-fx-background-color: " + BG_SIDEBAR + ";" +
            "-fx-background-radius: 12 12 0 0;"
        );
        GridPane headerGrid = new GridPane();
        for (double w : colWidths) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(w);
            cc.setHgrow(Priority.ALWAYS);
            headerGrid.getColumnConstraints().add(cc);
        }
        headerGrid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(headerGrid, Priority.ALWAYS);

        for (int c = 0; c < headers.length; c++) {
            Label h = new Label(headers[c].toUpperCase());
            h.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
            h.setTextFill(Color.web(ACCENT));
            h.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
            h.setPadding(new Insets(0, 8, 0, 8));
            headerGrid.add(h, c, 0);
        }
        tableHeaderRow.getChildren().add(headerGrid);

        VBox rows = new VBox(0);
        Text pageInfo = new Text();
        pageInfo.setFont(Font.font("Poppins", 11));
        pageInfo.setFill(Color.web(TEXT_DIM));
        outlineText(pageInfo, 0.18);

        VBox attendanceRows = new VBox(0);
        Text attendanceInfo = new Text();
        attendanceInfo.setFont(Font.font("Poppins", 11));
        attendanceInfo.setFill(Color.web(TEXT_DIM));
        outlineText(attendanceInfo, 0.18);
        VBox attendanceCard = buildRecentAttendanceCard(attendanceRows, attendanceInfo);

        final Runnable[] refreshHolder = new Runnable[1];
        refreshHolder[0] = () -> {
            refreshMemberRows(
                rows,
                pageInfo,
                memberDAO,
                search.getText().trim(),
                filterPlan.getValue(),
                filterStatus.getValue(),
                colWidths,
                refreshHolder[0]
            );
            refreshAttendanceRows(attendanceRows, attendanceInfo, memberDAO);
            refreshStats.run();
        };

        search.setOnAction(e -> refreshHolder[0].run());
        filterPlan.setOnAction(e -> refreshHolder[0].run());
        filterStatus.setOnAction(e -> refreshHolder[0].run());
        addBtn.setOnAction(e -> showAddMemberDialog(refreshHolder[0]));

        refreshHolder[0].run();

        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER_RIGHT);
        pagination.setPadding(new Insets(14, 20, 14, 20));
        pagination.setStyle(
            "-fx-border-color: " + BORDER + " transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );
        Region pgSp = new Region();
        HBox.setHgrow(pgSp, Priority.ALWAYS);
        pagination.getChildren().addAll(pageInfo, pgSp);

        ScrollPane memberRowsScroll = tableRowsScroll(rows, 430);
        tableCard.getChildren().addAll(tableHeaderRow, memberRowsScroll, pagination);

        body.getChildren().addAll(statsRow, controls, tableCard, attendanceCard);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        return content;
    }

    private VBox buildRecentAttendanceCard(VBox attendanceRows, Text attendanceInfo) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.3));
        ds.setRadius(12);
        ds.setOffsetY(4);
        card.setEffect(ds);

        String[] headers = {"Member ID", "Name", "Plan", "Check-in Time", "Notes"};
        double[] widths = {12, 24, 14, 22, 28};
        HBox tableHeaderRow = new HBox();
        tableHeaderRow.setPadding(new Insets(12, 16, 12, 16));
        tableHeaderRow.setStyle(
            "-fx-background-color: " + BG_SIDEBAR + ";" +
            "-fx-background-radius: 12 12 0 0;"
        );
        GridPane headerGrid = new GridPane();
        for (double width : widths) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(width);
            cc.setHgrow(Priority.ALWAYS);
            headerGrid.getColumnConstraints().add(cc);
        }
        headerGrid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(headerGrid, Priority.ALWAYS);
        for (int i = 0; i < headers.length; i++) {
            Label label = new Label(headers[i].toUpperCase());
            label.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
            label.setTextFill(Color.web(ACCENT));
            label.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
            label.setPadding(new Insets(0, 8, 0, 8));
            headerGrid.add(label, i, 0);
        }
        tableHeaderRow.getChildren().add(headerGrid);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(14, 20, 14, 20));
        footer.setStyle(
            "-fx-border-color: " + BORDER + " transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        footer.getChildren().addAll(sp, attendanceInfo);

        ScrollPane attendanceRowsScroll = tableRowsScroll(attendanceRows, 260);
        card.getChildren().addAll(tableHeaderRow, attendanceRowsScroll, footer);
        return card;
    }

    private ScrollPane tableRowsScroll(VBox rows, double maxHeight) {
        ScrollPane scroll = new ScrollPane(rows);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setMaxHeight(maxHeight);
        scroll.setPrefViewportHeight(maxHeight);
        scroll.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        rows.setFillWidth(true);
        rows.setMaxWidth(Double.MAX_VALUE);
        return scroll;
    }

    private void refreshAttendanceRows(VBox rows, Text attendanceInfo, MemberDAO dao) {
        rows.getChildren().clear();
        List<MemberDAO.AttendanceRecord> records = dao.findRecentAttendance(8);
        attendanceInfo.setText(records.isEmpty() ? "No check-ins yet" : records.size() + " shown");
        if (records.isEmpty()) {
            Label empty = new Label("No member check-ins have been recorded yet.");
            empty.setPadding(new Insets(14, 20, 14, 20));
            empty.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
            empty.setTextFill(Color.web(TEXT_MUTED));
            rows.getChildren().add(empty);
            return;
        }

        double[] widths = {12, 24, 14, 22, 28};
        int index = 0;
        for (MemberDAO.AttendanceRecord record : records) {
            GridPane row = new GridPane();
            row.setPadding(new Insets(11, 16, 11, 16));
            String bg = (index % 2 == 0 ? BG_CARD : BG_ROW_ALT);
            row.setStyle("-fx-background-color: " + bg + ";");
            for (double width : widths) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(width);
                cc.setHgrow(Priority.ALWAYS);
                row.getColumnConstraints().add(cc);
            }

            addAttendanceCell(row, 0, safeText(record.memberCode()), FontWeight.BOLD, TEXT_WHITE);
            addAttendanceCell(row, 1, safeText(record.memberName()), FontWeight.BOLD, TEXT_WHITE);
            addAttendanceCell(row, 2, safeText(record.sessionType()), FontWeight.NORMAL, TEXT_MUTED);
            addAttendanceCell(row, 3, formatCheckInTime(record.timeIn()), FontWeight.NORMAL, TEXT_MUTED);
            addAttendanceCell(row, 4, record.notes() == null || record.notes().isBlank() ? "-" : record.notes(), FontWeight.NORMAL, TEXT_MUTED);
            String finalBg = bg;
            row.setOnMouseEntered(ev -> row.setStyle("-fx-background-color: rgba(26,19,99,0.06);"));
            row.setOnMouseExited(ev -> row.setStyle("-fx-background-color: " + finalBg + ";"));
            rows.getChildren().add(row);
            index++;
        }
    }

    private void addAttendanceCell(GridPane row, int column, String text, FontWeight weight, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Poppins", weight, 11));
        label.setTextFill(Color.web(color));
        label.setPadding(new Insets(0, 8, 0, 8));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        GridPane.setHgrow(label, Priority.ALWAYS);
        row.add(label, column, 0);
    }

    private String formatCheckInTime(java.sql.Timestamp timeIn) {
        if (timeIn == null) {
            return "-";
        }
        return timeIn.toLocalDateTime().format(CHECK_IN_TIME_FORMAT);
    }

    private void styleStatValue(Text val) {
        val.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        outlineText(val, 0.30);
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
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.2));
        ds.setRadius(8); ds.setOffsetY(3);
        chip.setEffect(ds);
        valueNode.setFill(Color.web(readableAccent(color)));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", 11));
        lbl.setFill(Color.web(TEXT_DIM));
        outlineText(lbl, 0.18);
        chip.getChildren().addAll(new VBox(2, lbl, valueNode));
        return chip;
    }

    private void refreshMemberRows(
        VBox rows,
        Text pageInfo,
        MemberDAO dao,
        String keyword,
        String planFilter,
        String statusFilter,
        double[] colWidths,
        Runnable fullRefresh
    ) {
        rows.getChildren().clear();
        List<MemberDAO.MemberRecord> source =
            keyword.isBlank() ? dao.findAll() : dao.search(keyword);
        List<MemberDAO.MemberRecord> filtered = new ArrayList<>();
        for (MemberDAO.MemberRecord m : source) {
            if (!"All Plans".equals(planFilter)) {
                String t = m.membershipType();
                if (t == null || !t.equalsIgnoreCase(planFilter)) {
                    continue;
                }
            }
            if (!"All Status".equals(statusFilter)) {
                String dbStatus = "Archived".equals(statusFilter) ? "Cancelled" : statusFilter;
                String s = m.status();
                if (s == null || !s.equalsIgnoreCase(dbStatus)) {
                    continue;
                }
            }
            filtered.add(m);
        }
        pageInfo.setText("Showing " + filtered.size() + " member(s)");
        if (filtered.isEmpty()) {
            rows.getChildren().add(makeEmptyState("No matching members found."));
            return;
        }

        int r = 0;
        for (MemberDAO.MemberRecord m : filtered) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox dataRow = new HBox();
            dataRow.setPadding(new Insets(11, 16, 11, 16));
            dataRow.setStyle("-fx-background-color: " + bg + ";");
            dataRow.setAlignment(Pos.CENTER_LEFT);

            GridPane rowGrid = new GridPane();
            for (double w : colWidths) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(w);
                cc.setHgrow(Priority.ALWAYS);
                rowGrid.getColumnConstraints().add(cc);
            }
            rowGrid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rowGrid, Priority.ALWAYS);

            String code = "#" + m.memberCode();
            String genderLabel = "Other".equalsIgnoreCase(m.gender()) ? "Other"
                : "F".equalsIgnoreCase(m.gender()) ? " Female" : " Male";
            String start = m.membershipStartDate() != null ? m.membershipStartDate().toString() : "";

            rowGrid.add(makeCell(code, ACCENT, true), 0, 0);
            rowGrid.add(makeCell(m.fullName(), TEXT_WHITE, false), 1, 0);
            rowGrid.add(makeCell(genderLabel, TEXT_DIM, false), 2, 0);
            rowGrid.add(makeCell(m.contactNumber(), TEXT_DIM, false), 3, 0);
            rowGrid.add(makePlanBadge(m.membershipType() != null ? m.membershipType() : "Monthly"), 4, 0);
            rowGrid.add(makeCell(start, TEXT_DIM, false), 5, 0);
            rowGrid.add(makeStatusBadge(m.status() != null ? m.status() : "Active"), 6, 0);

            HBox actions = new HBox(6);
            actions.setAlignment(Pos.CENTER_LEFT);
            actions.setMinWidth(245);
            Button viewBtn = makeActionBtn("", "#77749B");
            viewBtn.setOnAction(e -> {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Member");
                a.setHeaderText(m.fullName());
                a.setContentText(
                    "Code: " + m.memberCode() + "\nEmail: " + m.email() + "\nPhone: " + m.contactNumber()
                        + "\nAddress: " + m.address() + "\nPlan: " + m.membershipType()
                        + "\nStart: " + start + "\nEnd: "
                        + (m.membershipEndDate() != null ? m.membershipEndDate().toString() : "")
                        + "\nStatus: " + m.status()
                );
                a.showAndWait();
            });
            viewBtn.setText("View");

            Button editBtn = makeActionBtn("Edit", WARNING_TEXT);
            editBtn.setOnAction(e -> showEditMemberDialog(m, fullRefresh));

            Button archiveBtn = makeActionBtn("Archive", TEXT_MUTED);
            archiveBtn.setOnAction(e -> archiveMember(dao, m, fullRefresh));
            Button delBtn = makeActionBtn("", ACCENT);
            delBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Member");
                confirm.setHeaderText("Remove " + m.fullName() + "?");
                confirm.setContentText("This permanently deletes the member record. Use Archive if you need to keep history.");
                Optional<ButtonType> res = confirm.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK
                    && dao.findById(m.memberId()).isPresent()
                    && dao.delete(m.memberId())) {
                    fullRefresh.run();
                }
            });
            delBtn.setText("Delete");
            boolean archived = "Cancelled".equalsIgnoreCase(m.status());
            Button attendanceBtn = makeActionBtn(archived ? "Renew" : "Check", archived ? ACCENT : SUCCESS_TEXT);
            attendanceBtn.setOnAction(e -> {
                if (archived) {
                    showRenewMemberDialog(m, fullRefresh);
                } else {
                    recordMemberAttendance(dao, m, fullRefresh);
                }
            });

            actions.getChildren().addAll(viewBtn, editBtn, archiveBtn, delBtn, attendanceBtn);
            rowGrid.add(actions, 7, 0);

            dataRow.getChildren().add(rowGrid);
            String finalBg = bg;
            dataRow.setOnMouseEntered(ev -> dataRow.setStyle("-fx-background-color: rgba(26,19,99,0.06);"));
            dataRow.setOnMouseExited(ev -> dataRow.setStyle("-fx-background-color: " + finalBg + ";"));

            rows.getChildren().add(dataRow);
            r++;
        }
    }

    //  Helper builders 
    private void showMemberDetails(MemberDAO.MemberRecord m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Member Details");
        a.setHeaderText(m.fullName());
        a.setContentText(
            "Member ID: " + m.memberCode()
                + "\nEmail: " + m.email()
                + "\nPhone: " + m.contactNumber()
                + "\nAddress: " + m.address()
                + "\nGender: " + m.gender()
                + "\nDate of Birth: " + textDate(m.dateOfBirth())
                + "\nPlan: " + m.membershipType()
                + "\nStart: " + textDate(m.membershipStartDate())
                + "\nEnd: " + textDate(m.membershipEndDate())
                + "\nStatus: " + statusText(m.status())
                + "\nEmergency Contact: " + safeText(m.emergencyContact())
                + "\nEmergency Phone: " + safeText(m.emergencyPhone())
        );
        a.showAndWait();
    }

    private void recordMemberAttendance(MemberDAO dao, MemberDAO.MemberRecord m, Runnable fullRefresh) {
        if ("Cancelled".equalsIgnoreCase(m.status())) {
            alertErr("Archived members cannot be checked in.");
            return;
        }
        TextInputDialog notesDialog = new TextInputDialog("");
        notesDialog.setTitle("Member Attendance");
        notesDialog.setHeaderText("Record check-in for " + m.fullName());
        notesDialog.setContentText("Notes (optional):");
        Optional<String> notes = notesDialog.showAndWait();
        if (notes.isPresent()) {
            if (dao.recordAttendance(m.memberId(), m.membershipType(), notes.get().trim())) {
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Attendance");
                ok.setHeaderText("Check-in recorded");
                ok.setContentText(m.fullName() + " was checked in with the current timestamp.");
                ok.showAndWait();
                fullRefresh.run();
            } else {
                alertErr("Could not record attendance. Please check the database connection.");
            }
        }
    }

    private void archiveMember(MemberDAO dao, MemberDAO.MemberRecord m, Runnable fullRefresh) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Archive Member");
        confirm.setHeaderText("Archive " + m.fullName() + "?");
        confirm.setContentText("This keeps the member history but removes the member from active records.");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK && dao.updateStatus(m.memberId(), "Cancelled")) {
            fullRefresh.run();
        }
    }

    private void showRenewMemberDialog(MemberDAO.MemberRecord m, Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Renew Member");
        dialog.setResizable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(430);

        Text title = new Text("Renew Archived Member");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        outlineText(title, 0.28);

        Text subtitle = new Text(m.fullName() + " will be reactivated using the selected plan.");
        subtitle.setFont(Font.font("Poppins", 11));
        subtitle.setFill(Color.web(TEXT_DIM));
        outlineText(subtitle, 0.16);

        ComboBox<String> plan = new ComboBox<>();
        plan.getItems().addAll(new PlanDAO().activePlanNames());
        plan.setValue(planValueOrDefault(m.membershipType(), plan.getItems()));
        styleCombo(plan);

        DatePicker startDate = new DatePicker(LocalDate.now());
        startDate.getEditor().setPromptText("YYYY-MM-DD");
        TextField endDate = new TextField(membershipEndForPlan(LocalDate.now(), plan.getValue()).toString());
        endDate.setEditable(false);
        Label startError = makeValidationLabel();

        plan.setOnAction(e -> {
            try {
                LocalDate start = startDate.getValue();
                endDate.setText(membershipEndForPlan(start, plan.getValue()).toString());
            } catch (Exception ignored) {
                endDate.setText("");
            }
        });
        startDate.valueProperty().addListener((obs, old, value) -> {
            try {
                endDate.setText(membershipEndForPlan(value, plan.getValue()).toString());
            } catch (Exception ignored) {
                endDate.setText("");
            }
        });

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setPrefHeight(40);
        cancel.setPadding(new Insets(0, 20, 0, 20));
        cancel.setOnAction(e -> dialog.close());

        Button renew = new Button("Renew Member");
        renew.setPrefHeight(40);
        renew.setPadding(new Insets(0, 20, 0, 20));
        renew.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        renew.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        renew.setOnAction(e -> {
            clearValidation(startError);
            Date startSql = dateFromPickerOrMark(startDate, startError);
            if (startSql == null) {
                return;
            }
            LocalDate start = startSql.toLocalDate();

            LocalDate end = membershipEndForPlan(start, plan.getValue());
            MemberDAO.MemberRecord renewed = new MemberDAO.MemberRecord(
                m.memberId(),
                m.memberCode(),
                m.firstName(),
                m.lastName(),
                m.contactNumber(),
                m.email(),
                m.address(),
                m.dateOfBirth(),
                m.gender(),
                plan.getValue(),
                Date.valueOf(start),
                Date.valueOf(end),
                "Active",
                m.emergencyContact(),
                m.emergencyPhone()
            );

            if (new MemberDAO().update(renewed)) {
                dialog.close();
                onSaved.run();
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Member Renewed");
                ok.setHeaderText("Member reactivated");
                ok.setContentText(m.fullName() + " is now Active and can check in.");
                ok.showAndWait();
            } else {
                alertErr("Could not renew member. Please check the database connection.");
            }
        });

        buttons.getChildren().addAll(cancel, renew);
        root.getChildren().addAll(
            title,
            subtitle,
            comboBoxField("PLAN", plan),
            withValidation(datePickerField("START DATE", startDate), startError),
            labeledField("END DATE", endDate, "Auto-calculated"),
            buttons
        );

        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private void showEditMemberDialog(MemberDAO.MemberRecord m, Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Member");
        dialog.setResizable(false);

        VBox root = new VBox(14);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(520);

        Text title = new Text("Update Member Details");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        outlineText(title, 0.28);

        TextField firstName = new TextField(m.firstName());
        TextField lastName = new TextField(m.lastName());
        TextField phone = new TextField(m.contactNumber());
        TextField email = new TextField(m.email());
        TextField address = new TextField(m.address());
        DatePicker dob = new DatePicker(m.dateOfBirth() != null ? m.dateOfBirth().toLocalDate() : null);
        dob.getEditor().setPromptText("YYYY-MM-DD");
        TextField emergencyContact = new TextField(safeText(m.emergencyContact()));
        TextField emergencyPhone = new TextField(safeText(m.emergencyPhone()));

        ComboBox<String> plan = new ComboBox<>();
        plan.getItems().addAll(new PlanDAO().activePlanNames());
        plan.setValue(planValueOrDefault(m.membershipType(), plan.getItems()));
        styleCombo(plan);

        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("M", "F", "Other");
        gender.setValue(m.gender() != null ? m.gender() : "M");
        styleCombo(gender);

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("Active", "Expired", "Suspended", "Cancelled");
        status.setValue(m.status() != null ? m.status() : "Active");
        styleCombo(status);

        VBox firstBox = labeledField("FIRST NAME", firstName, "Enter first name");
        Label firstError = makeValidationLabel();
        firstBox.getChildren().add(firstError);
        VBox lastBox = labeledField("LAST NAME", lastName, "Enter last name");
        Label lastError = makeValidationLabel();
        lastBox.getChildren().add(lastError);
        VBox phoneBox = labeledField("PHONE", phone, "09XXXXXXXXX");
        Label phoneError = makeValidationLabel();
        phoneBox.getChildren().add(phoneError);
        VBox emailBox = labeledField("EMAIL", email, "email@example.com");
        Label emailError = makeValidationLabel();
        emailBox.getChildren().add(emailError);
        VBox addressBox = labeledField("ADDRESS", address, "Full address");
        Label addressError = makeValidationLabel();
        addressBox.getChildren().add(addressError);
        VBox dobBox = datePickerField("DATE OF BIRTH", dob);
        Label dobError = makeValidationLabel();
        dobBox.getChildren().add(dobError);

        root.getChildren().addAll(
            title,
            firstBox,
            lastBox,
            phoneBox,
            emailBox,
            addressBox,
            dobBox,
            comboBoxField("PLAN", plan),
            comboBoxField("GENDER", gender),
            comboBoxField("STATUS", status),
            labeledField("EMERGENCY CONTACT", emergencyContact, "Contact name"),
            labeledField("EMERGENCY PHONE", emergencyPhone, "Contact phone")
        );

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> dialog.close());
        Button save = new Button("Save Changes");
        save.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;");
        save.setOnAction(e -> {
            clearValidation(firstError, lastError, phoneError, emailError, addressError, dobError);
            boolean valid = true;
            if (firstName.getText().trim().isEmpty()) { setValidation(firstError, "First name is required."); valid = false; }
            if (lastName.getText().trim().isEmpty()) { setValidation(lastError, "Last name is required."); valid = false; }
            if (phone.getText().trim().isEmpty()) { setValidation(phoneError, "Phone is required."); valid = false; }
            if (email.getText().trim().isEmpty()) { setValidation(emailError, "Email is required."); valid = false; }
            if (address.getText().trim().isEmpty()) { setValidation(addressError, "Address is required."); valid = false; }
            Date dobSql = dateFromPickerOrMark(dob, dobError);
            if (isInvalidDatePickerText(dob, dobSql)) valid = false;
            if (!valid) return;

            MemberDAO.MemberRecord updated = new MemberDAO.MemberRecord(
                m.memberId(),
                m.memberCode(),
                firstName.getText().trim(),
                lastName.getText().trim(),
                phone.getText().trim(),
                email.getText().trim(),
                address.getText().trim(),
                dobSql,
                gender.getValue(),
                plan.getValue(),
                m.membershipStartDate(),
                m.membershipEndDate(),
                status.getValue(),
                emergencyContact.getText().trim(),
                emergencyPhone.getText().trim()
            );

            if (new MemberDAO().update(updated)) {
                dialog.close();
                onSaved.run();
            } else {
                alertErr("Could not update member details.");
            }
        });
        buttons.getChildren().addAll(cancel, save);
        root.getChildren().add(buttons);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_CARD + ";");
        dialog.setScene(new Scene(scroll, 560, 680));
        dialog.showAndWait();
    }

    private VBox comboBoxField(String label, ComboBox<String> combo) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(ACCENT));
        lbl.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
        combo.setPrefHeight(40);
        box.getChildren().addAll(lbl, combo);
        return box;
    }

    private String textDate(Date date) {
        return date != null ? date.toString() : "";
    }

    private String safeText(String text) {
        return text != null && !text.isBlank() ? text : "";
    }

    private String statusText(String status) {
        return "Cancelled".equalsIgnoreCase(status) ? "Archived" : safeText(status);
    }

    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        l.setTextFill(Color.web(readableAccent(color)));
        l.setPadding(new Insets(0, 8, 0, 8));
        l.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.72), 1, 0.0, 0, 0);");
        return l;
    }

    private Label makeStatusBadge(String status) {
        String display = statusText(status);
        Label b = new Label(display);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c, bg;
        switch (display.toLowerCase()) {
            case "active": c = SUCCESS_TEXT; bg = "rgba(228,255,223,0.85)"; break;
            case "expired": c = ACCENT; bg = "rgba(26,19,99,0.14)"; break;
            case "archived": case "cancelled": c = TEXT_DIM; bg = "rgba(119,116,155,0.16)"; break;
            case "suspended": c = WARNING_TEXT; bg = "rgba(253,238,33,0.32)"; break;
            default: c = TEXT_DIM; bg = "rgba(236,233,233,0.80)"; break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 18;" +
            "-fx-padding: 3 10 3 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);"
        );
        b.setPadding(new Insets(0, 8, 0, 8));
        return b;
    }

    private Label makePlanBadge(String plan) {
        String display = plan != null && !plan.isBlank() ? plan : "No Plan";
        Label b = new Label(display);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c, bg;
        switch (display.toLowerCase()) {
            case "monthly": c = ACCENT; bg = "rgba(26,19,99,0.12)"; break;
            case "daily": case "per session": c = WARNING_TEXT; bg = "rgba(253,238,33,0.32)"; break;
            case "quarterly": case "semi annual": case "yearly": case "annual":
                c = SUCCESS_TEXT; bg = "rgba(228,255,223,0.85)"; break;
            default: c = TEXT_DIM; bg = "rgba(236,233,233,0.85)"; break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 18;" +
            "-fx-padding: 3 10 3 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);"
        );
        b.setPadding(new Insets(0, 8, 0, 8));
        return b;
    }

    private Button makeActionBtn(String icon, String color) {
        Button btn = new Button(icon);
        String readableColor = readableAccent(color);
        btn.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 4 3 4;" +
            "-fx-background-radius: 6;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.85), 1, 0.0, 0, 0);"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(26,19,99,0.08);" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 4 3 4;" +
            "-fx-background-radius: 6;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 4 3 4;" +
            "-fx-background-radius: 6;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.85), 1, 0.0, 0, 0);"
        ));
        return btn;
    }

    private HBox makeStatChip(String label, String value, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.2));
        ds.setRadius(8); ds.setOffsetY(3);
        chip.setEffect(ds);
        Text val = new Text(value);
        val.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        val.setFill(Color.web(readableAccent(color)));
        outlineText(val, 0.30);
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", 11));
        lbl.setFill(Color.web(TEXT_DIM));
        outlineText(lbl, 0.18);
        chip.getChildren().addAll(new VBox(2, lbl, val));
        return chip;
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
            "-fx-pref-height: 38;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.70), 1, 0.0, 0, 0);"
        );
    }

    private Button makePagBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        String bg = active ? ACCENT : BG_CARD;
        b.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 12 5 12;"
        );
        return b;
    }

    //  Add Member Dialog 
    private void showAddMemberDialog(Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Member");
        dialog.setResizable(false);

        VBox root = new VBox(18);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(460);

        Text title = new Text("Add New Member");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        outlineText(title, 0.28);
        Rectangle underline = new Rectangle(48, 3);
        underline.setFill(Color.web(ACCENT));
        underline.setArcWidth(3); underline.setArcHeight(3);

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(14);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        form.getColumnConstraints().addAll(c1, c2);

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField phone = new TextField();
        TextField email = new TextField();
        TextField address = new TextField();
        DatePicker dobField = new DatePicker();
        dobField.getEditor().setPromptText("YYYY-MM-DD");
        ComboBox<String> plan = new ComboBox<>();
        plan.getItems().addAll(new PlanDAO().activePlanNames());
        plan.setValue(planValueOrDefault("Monthly", plan.getItems()));
        styleCombo(plan);
        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("M", "F", "Other");
        gender.setValue("M");
        styleCombo(gender);

        int r = 0;
        VBox firstBox = labeledField("FIRST NAME", firstName, "Enter first name");
        Label firstError = makeValidationLabel();
        firstBox.getChildren().add(firstError);
        VBox lastBox = labeledField("LAST NAME", lastName, "Enter last name");
        Label lastError = makeValidationLabel();
        lastBox.getChildren().add(lastError);
        VBox phoneBox = labeledField("PHONE", phone, "09XXXXXXXXX");
        Label phoneError = makeValidationLabel();
        phoneBox.getChildren().add(phoneError);
        VBox emailBox = labeledField("EMAIL", email, "email@example.com");
        Label emailError = makeValidationLabel();
        emailBox.getChildren().add(emailError);
        form.add(firstBox, 0, r);
        form.add(lastBox, 1, r++);
        form.add(phoneBox, 0, r);
        form.add(emailBox, 1, r++);
        VBox addrBox = labeledField("ADDRESS", address, "Full address");
        Label addressError = makeValidationLabel();
        addrBox.getChildren().add(addressError);
        form.add(addrBox, 0, r++, 2, 1);
        GridPane.setColumnSpan(addrBox, 2);
        VBox dobBox = datePickerField("DATE OF BIRTH (optional)", dobField);
        Label dobError = makeValidationLabel();
        dobBox.getChildren().add(dobError);
        form.add(dobBox, 0, r);
        VBox planBox = new VBox(6);
        Label pl = new Label("PLAN");
        pl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        pl.setTextFill(Color.web(ACCENT));
        pl.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
        plan.setPrefHeight(40);
        planBox.getChildren().addAll(pl, plan);
        form.add(planBox, 1, r++);
        VBox gBox = new VBox(6);
        Label gl = new Label("GENDER");
        gl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        gl.setTextFill(Color.web(ACCENT));
        gl.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
        gender.setPrefHeight(40);
        gBox.getChildren().addAll(gl, gender);
        form.add(gBox, 0, r++, 2, 1);
        GridPane.setColumnSpan(gBox, 2);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setPrefHeight(40);
        cancel.setPadding(new Insets(0, 20, 0, 20));
        cancel.setFont(Font.font("Poppins", 12));
        cancel.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        cancel.setOnAction(e -> dialog.close());

        MemberDAO dao = new MemberDAO();
        Button save = new Button("Save Member");
        save.setPrefHeight(40);
        save.setPadding(new Insets(0, 20, 0, 20));
        save.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        save.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        save.setOnAction(e -> {
            clearValidation(firstError, lastError, phoneError, emailError, addressError, dobError);
            String fn = firstName.getText().trim();
            String ln = lastName.getText().trim();
            boolean valid = true;
            if (fn.isEmpty()) { setValidation(firstError, "First name is required."); valid = false; }
            if (ln.isEmpty()) { setValidation(lastError, "Last name is required."); valid = false; }
            String ph = phone.getText().trim();
            String em = email.getText().trim();
            String ad = address.getText().trim();
            if (ph.isEmpty()) { setValidation(phoneError, "Phone is required."); valid = false; }
            if (em.isEmpty()) { setValidation(emailError, "Email is required."); valid = false; }
            if (ad.isEmpty()) { setValidation(addressError, "Address is required."); valid = false; }
            Date dobSql = dateFromPickerOrMark(dobField, dobError);
            if (isInvalidDatePickerText(dobField, dobSql)) valid = false;
            if (!valid) return;
            Date start = Date.valueOf(LocalDate.now());
            Date end = Date.valueOf(membershipEndForPlan(start.toLocalDate(), plan.getValue()));
            int uid = AppSession.currentUser().userId();
            MemberDAO.MemberRecord rec = new MemberDAO.MemberRecord(
                0,
                "",
                fn,
                ln,
                ph,
                em,
                ad,
                dobSql,
                gender.getValue(),
                plan.getValue(),
                start,
                end,
                "Active",
                "",
                ""
            );
            int newId = dao.insert(rec, uid);
            if (newId > 0) {
                dialog.close();
                onSaved.run();
            } else {
                alertErr("Could not save member. Check database connection or duplicate email/code.");
            }
        });

        btnRow.getChildren().addAll(cancel, save);

        root.getChildren().addAll(title, underline, form, btnRow);
        Scene s = new Scene(root);
        s.setFill(Color.web(BG_CARD));
        dialog.setScene(s);
        dialog.showAndWait();
    }

    private VBox labeledField(String label, TextField field, String prompt) {
        VBox fg = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(ACCENT));
        lbl.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
        field.setPromptText(prompt);
        field.setPrefHeight(40);
        applyFieldStyle(field);
        fg.getChildren().addAll(lbl, field);
        return fg;
    }

    private VBox datePickerField(String label, DatePicker picker) {
        VBox fg = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(ACCENT));
        lbl.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
        picker.setPrefHeight(40);
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;"
        );
        fg.getChildren().addAll(lbl, picker);
        return fg;
    }

    private VBox withValidation(VBox fieldBox, Label validation) {
        fieldBox.getChildren().add(validation);
        return fieldBox;
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

    private HBox makeEmptyState(String message) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24, 14, 24, 14));
        Label label = new Label(message);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        label.setTextFill(Color.web(TEXT_DIM));
        box.getChildren().add(label);
        return box;
    }

    private LocalDate membershipEndForPlan(LocalDate start, String planType) {
        if (planType == null) {
            return start.plusMonths(1);
        }
        return switch (planType) {
            case "Daily", "Per Session" -> start.plusDays(1);
            case "Monthly" -> start.plusMonths(1);
            case "Quarterly" -> start.plusMonths(3);
            case "Semi Annual" -> start.plusMonths(6);
            case "Yearly", "Annual" -> start.plusYears(1);
            default -> start.plusMonths(1);
        };
    }

    private String planValueOrDefault(String requested, List<String> choices) {
        String normalized = PlanDAO.normalizePlanName(requested);
        if (choices.contains(normalized)) {
            return normalized;
        }
        if (choices.contains("Monthly")) {
            return "Monthly";
        }
        return choices.isEmpty() ? "Monthly" : choices.get(0);
    }

    private void alertErr(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Validation");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void applyFieldStyle(TextField f) {
        f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.72), 1, 0.0, 0, 0);"
        );
        f.focusedProperty().addListener((o, old, focused) -> f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.72), 1, 0.0, 0, 0);"
        ));
    }

    private void outlineText(Text text, double width) {
        text.setStroke(Color.web(ModernDesignSystem.WHITE, 0.82));
        text.setStrokeWidth(width);
    }

    private String readableAccent(String color) {
        if (SUCCESS.equalsIgnoreCase(color)) return SUCCESS_TEXT;
        if (WARNING.equalsIgnoreCase(color)) return WARNING_TEXT;
        return color;
    }

    public static void main(String[] args) { launch(args); }
}
