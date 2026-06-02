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
import javafx.scene.Node;
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
import mj23gym.dao.PaymentDAO;
import mj23gym.dao.PlanDAO;

/**
 * MJ23 Playgrind Gym  Member Management Screen
 * Searchable, filterable member table with Add / Edit / View controls.
 */
public class MemberManagementScreen extends Application {

    static final String BG_MAIN      = "#F3F1F6";
    static final String BG_SIDEBAR   = ModernDesignSystem.SIDEBAR_BG;
    static final String BG_CARD      = ModernDesignSystem.WHITE;
    static final String BG_ROW_ALT   = "#F8F6FC";
    static final String ACCENT       = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK  = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_WHITE   = "#111827";
    static final String TEXT_MUTED   = "#374151";
    static final String TEXT_DIM     = "#4B5563";
    static final String TABLE_TEXT   = "#111827";
    static final String TABLE_SUBTEXT = "#2F3545";
    static final String DETAIL_TITLE  = "#111827";
    static final String DETAIL_TEXT   = "#1F2937";
    static final String DETAIL_MUTED  = "#374151";
    static final String BORDER       = "#DDD8EA";
    static final String HEADER_BG    = "#F0EDF8";
    static final String HOVER_BG     = "#F1EEFA";
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
        PaymentDAO paymentDAO = new PaymentDAO();

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
            int total = 0;
            int active = 0;
            int expired = 0;
            LocalDate today = LocalDate.now();
            int renewals = 0;
            for (MemberDAO.MemberRecord m : memberDAO.findAll()) {
                if (!"Cancelled".equalsIgnoreCase(m.status())) {
                    total++;
                }
                String effectiveStatus = effectiveMemberStatus(m);
                if ("Active".equalsIgnoreCase(effectiveStatus)) active++;
                if ("Expired".equalsIgnoreCase(effectiveStatus)) expired++;
            }
            Date todaySql = Date.valueOf(today);
            for (PaymentDAO.PaymentRecord payment : paymentDAO.findByDateRange(todaySql, todaySql)) {
                if ("Membership".equalsIgnoreCase(payment.paymentType())
                    && "Completed".equalsIgnoreCase(payment.status())
                    && payment.amount() > 0) {
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
        Text statSummaryTitle = new Text();
        VBox statSummaryRows = new VBox(0);
        VBox statSummaryPanel = buildStatSummaryPanel(statSummaryTitle, statSummaryRows);

        HBox totalChip = makeStatChipText("Total Members", statTotalVal, TEXT_WHITE);
        HBox activeChip = makeStatChipText("Active", statActiveVal, SUCCESS_TEXT);
        HBox expiredChip = makeStatChipText("Expired", statExpiredVal, ACCENT);
        HBox renewChip = makeStatChipText("Renewals Today", statRenewVal, WARNING_TEXT);
        statsRow.getChildren().addAll(
            totalChip,
            activeChip,
            expiredChip,
            renewChip
        );
        wireStatChip(totalChip, statsRow, () -> updateStatSummary(memberDAO, paymentDAO, "Total Members", statSummaryTitle, statSummaryRows));
        wireStatChip(activeChip, statsRow, () -> updateStatSummary(memberDAO, paymentDAO, "Active Members", statSummaryTitle, statSummaryRows));
        wireStatChip(expiredChip, statsRow, () -> updateStatSummary(memberDAO, paymentDAO, "Expired Members", statSummaryTitle, statSummaryRows));
        wireStatChip(renewChip, statsRow, () -> updateStatSummary(memberDAO, paymentDAO, "Renewals Today", statSummaryTitle, statSummaryRows));
        updateStatSummary(memberDAO, paymentDAO, "Total Members", statSummaryTitle, statSummaryRows);
        applyStatChipStyle(totalChip, true);

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
            "-fx-font-size: 12;"
        );

        ComboBox<String> filterPlan = new ComboBox<>();
        filterPlan.getItems().add("All Plans");
        filterPlan.getItems().addAll(new PlanDAO().activePlanNames());
        filterPlan.setValue("All Plans");
        styleCombo(filterPlan);

        ComboBox<String> filterStatus = new ComboBox<>();
        filterStatus.getItems().addAll("All Status", "Active", "Payment Pending", "Expired");
        filterStatus.setValue("All Status");
        styleCombo(filterStatus);

        Region ctrlSp = new Region();
        HBox.setHgrow(ctrlSp, Priority.ALWAYS);

        Button archivedBtn = new Button("Archived Members");
        archivedBtn.setPrefHeight(38);
        archivedBtn.setPadding(new Insets(0, 18, 0, 18));
        archivedBtn.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        archivedBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );

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

        controls.getChildren().addAll(search, filterPlan, filterStatus, ctrlSp, archivedBtn, addBtn);

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
        ds.setColor(Color.web(ACCENT, 0.10));
        ds.setRadius(14); ds.setOffsetY(4);
        tableCard.setEffect(ds);

        // Table header row
        String[] headers = {"Member ID","Full Name","Gender","Phone","Plan","Registered","Status","Actions"};
        double[] colWidths = {7, 14, 6, 10, 8, 9, 9, 37};

        // Header
        HBox tableHeaderRow = new HBox();
        tableHeaderRow.setPadding(new Insets(12, 16, 12, 16));
        tableHeaderRow.setStyle(
            "-fx-background-color: " + HEADER_BG + ";" +
            "-fx-background-radius: 22 22 0 0;" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
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
            h.setTextFill(Color.web(TABLE_TEXT));
            h.setStyle("-fx-text-fill: " + TABLE_TEXT + ";");
            h.setPadding(new Insets(0, 8, 0, 8));
            headerGrid.add(h, c, 0);
        }
        tableHeaderRow.getChildren().add(headerGrid);

        VBox rows = new VBox(0);
        Text pageInfo = new Text();
        pageInfo.setFont(Font.font("Poppins", 11));
        pageInfo.setFill(Color.web(TEXT_DIM));

        VBox attendanceRows = new VBox(0);
        Text attendanceInfo = new Text();
        attendanceInfo.setFont(Font.font("Poppins", 11));
        attendanceInfo.setFill(Color.web(TEXT_DIM));
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
        archivedBtn.setOnAction(e -> showArchivedMembersDialog(memberDAO, refreshHolder[0]));
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

        body.getChildren().addAll(statsRow, statSummaryPanel, controls, tableCard, attendanceCard);
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
        ds.setColor(Color.web(ACCENT, 0.10));
        ds.setRadius(12);
        ds.setOffsetY(4);
        card.setEffect(ds);

        String[] headers = {"Member ID", "Name", "Session Type", "Check-in Time", "Notes"};
        double[] widths = {12, 24, 14, 22, 28};
        HBox tableHeaderRow = new HBox();
        tableHeaderRow.setPadding(new Insets(12, 16, 12, 16));
        tableHeaderRow.setStyle(
            "-fx-background-color: " + HEADER_BG + ";" +
            "-fx-background-radius: 22 22 0 0;" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
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
            label.setTextFill(Color.web(TEXT_MUTED));
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
            row.setStyle(
                "-fx-background-color: " + bg + ";" +
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                "-fx-border-width: 0 0 1 0;"
            );
            for (double width : widths) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(width);
                cc.setHgrow(Priority.ALWAYS);
                row.getColumnConstraints().add(cc);
            }

            addAttendanceCell(row, 0, safeText(record.memberCode()), FontWeight.BOLD, TABLE_TEXT);
            addAttendanceCell(row, 1, safeText(record.memberName()), FontWeight.BOLD, TABLE_TEXT);
            row.add(makeSessionTypeBadge(record.sessionType()), 2, 0);
            addAttendanceCell(row, 3, formatCheckInTime(record.timeIn()), FontWeight.NORMAL, TABLE_SUBTEXT);
            addAttendanceCell(row, 4, record.notes() == null || record.notes().isBlank() ? "-" : record.notes(), FontWeight.NORMAL, TABLE_SUBTEXT);
            String finalBg = bg;
            row.setOnMouseEntered(ev -> row.setStyle(
                "-fx-background-color: " + HOVER_BG + ";" +
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                "-fx-border-width: 0 0 1 0;"
            ));
            row.setOnMouseExited(ev -> row.setStyle(
                "-fx-background-color: " + finalBg + ";" +
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                "-fx-border-width: 0 0 1 0;"
            ));
            rows.getChildren().add(row);
            index++;
        }
    }

    private void addAttendanceCell(GridPane row, int column, String text, FontWeight weight, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Poppins", weight, 11));
        label.setTextFill(Color.web(color));
        label.setStyle("-fx-text-fill: " + color + ";");
        label.setPadding(new Insets(0, 8, 0, 8));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        GridPane.setHgrow(label, Priority.ALWAYS);
        row.add(label, column, 0);
    }

    private Label makeSessionTypeBadge(String sessionType) {
        String value = sessionType == null || sessionType.isBlank() ? "Member Session" : sessionType;
        String color;
        String bg;
        if ("Per Session".equalsIgnoreCase(value)) {
            color = WARNING_TEXT;
            bg = "#FFF5C2";
        } else if ("Daily".equalsIgnoreCase(value)) {
            color = "#1D4ED8";
            bg = "#DBEAFE";
        } else {
            color = SUCCESS_TEXT;
            bg = "#DCFCE7";
        }
        Label badge = new Label(value);
        badge.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        badge.setTextFill(Color.web(color));
        badge.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + color + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 3 9 3 9;"
        );
        return badge;
    }

    private String formatCheckInTime(java.sql.Timestamp timeIn) {
        if (timeIn == null) {
            return "-";
        }
        return timeIn.toLocalDateTime().format(CHECK_IN_TIME_FORMAT);
    }

    private void styleStatValue(Text val) {
        val.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
    }

    private HBox makeStatChipText(String label, Text valueNode, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setCursor(javafx.scene.Cursor.HAND);
        HBox.setHgrow(chip, Priority.ALWAYS);
        chip.getProperties().put("statColor", readableAccent(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        lbl.setFill(Color.web(TEXT_DIM));
        chip.getChildren().addAll(new VBox(2, lbl, valueNode));
        valueNode.setFill(Color.web(readableAccent(color)));
        applyStatChipStyle(chip, false);
        return chip;
    }

    private VBox buildStatSummaryPanel(Text title, VBox rows) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(16, 18, 16, 18));
        panel.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;"
        );
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        title.setFill(Color.web(TEXT_WHITE));
        rows.setSpacing(0);
        panel.getChildren().addAll(title, rows);
        return panel;
    }

    private void wireStatChip(HBox chip, HBox group, Runnable action) {
        chip.setOnMouseClicked(e -> {
            for (javafx.scene.Node node : group.getChildren()) {
                if (node instanceof HBox other) {
                    applyStatChipStyle(other, false);
                }
            }
            applyStatChipStyle(chip, true);
            action.run();
        });
    }

    private void applyStatChipStyle(HBox chip, boolean selected) {
        String color = String.valueOf(chip.getProperties().getOrDefault("statColor", ACCENT));
        chip.setStyle(
            "-fx-background-color: " + (selected ? "rgba(26,19,99,0.05)" : BG_CARD) + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: " + (selected ? "2" : "1.5") + ";"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web(color, selected ? 0.18 : 0.08));
        ds.setRadius(selected ? 10 : 6);
        ds.setOffsetY(selected ? 3 : 2);
        chip.setEffect(ds);
    }

    private void updateStatSummary(
        MemberDAO dao,
        PaymentDAO paymentDAO,
        String title,
        Text titleNode,
        VBox rowsNode
    ) {
        List<MemberDAO.MemberRecord> members = dao.findAll();
        LocalDate today = LocalDate.now();
        rowsNode.getChildren().clear();
        List<MemberDAO.MemberRecord> visibleMembers = new ArrayList<>();
        List<PaymentDAO.PaymentRecord> visiblePayments = new ArrayList<>();

        int active = 0;
        int expired = 0;
        int archived = 0;
        for (MemberDAO.MemberRecord member : members) {
            String status = effectiveMemberStatus(member);
            if ("Active".equalsIgnoreCase(status)) active++;
            if ("Expired".equalsIgnoreCase(status)) expired++;
            if ("Archived".equalsIgnoreCase(status)) archived++;
        }

        titleNode.setText(title);
        switch (title) {
            case "Active Members" -> {
                for (MemberDAO.MemberRecord member : members) {
                    if ("Active".equalsIgnoreCase(effectiveMemberStatus(member))) {
                        visibleMembers.add(member);
                    }
                }
                addSummaryCount(rowsNode, active + " active member(s)");
                addMemberSummaryRows(rowsNode, visibleMembers);
            }
            case "Expired Members" -> {
                for (MemberDAO.MemberRecord member : members) {
                    if ("Expired".equalsIgnoreCase(effectiveMemberStatus(member))) {
                        visibleMembers.add(member);
                    }
                }
                addSummaryCount(rowsNode, expired + " expired member(s)");
                addMemberSummaryRows(rowsNode, visibleMembers);
            }
            case "Renewals Today" -> {
                Date todaySql = Date.valueOf(today);
                for (PaymentDAO.PaymentRecord payment : paymentDAO.findByDateRange(todaySql, todaySql)) {
                    if ("Membership".equalsIgnoreCase(payment.paymentType())
                        && "Completed".equalsIgnoreCase(payment.status())
                        && payment.amount() > 0) {
                        visiblePayments.add(payment);
                    }
                }
                addSummaryCount(rowsNode, visiblePayments.size() + " renewed member(s) today");
                addPaymentSummaryRows(rowsNode, visiblePayments);
            }
            default -> {
                for (MemberDAO.MemberRecord member : members) {
                    if (!"Cancelled".equalsIgnoreCase(member.status())) {
                        visibleMembers.add(member);
                    }
                }
                addSummaryCount(
                    rowsNode,
                    visibleMembers.size() + " total member(s): " + active + " active, " + expired +
                        " expired"
                );
                addMemberSummaryRows(rowsNode, visibleMembers);
            }
        }
    }

    private void addSummaryCount(VBox rowsNode, String text) {
        Label count = new Label(text);
        count.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        count.setTextFill(Color.web(TEXT_DIM));
        count.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        count.setPadding(new Insets(0, 0, 8, 0));
        rowsNode.getChildren().add(count);
    }

    private void addMemberSummaryRows(VBox rowsNode, List<MemberDAO.MemberRecord> members) {
        if (members.isEmpty()) {
            rowsNode.getChildren().add(makeEmptyState("No members found for this box."));
            return;
        }
        int limit = Math.min(members.size(), 8);
        for (int i = 0; i < limit; i++) {
            MemberDAO.MemberRecord member = members.get(i);
            rowsNode.getChildren().add(makeMemberSummaryRow(
                "#" + safeText(member.memberCode()),
                member.fullName(),
                effectiveMemberStatus(member) + " - " + safeText(member.membershipType()),
                member.membershipEndDate() != null ? "Ends " + member.membershipEndDate() : ""
            ));
        }
        addMoreSummaryRow(rowsNode, members.size() - limit);
    }

    private void addPaymentSummaryRows(VBox rowsNode, List<PaymentDAO.PaymentRecord> payments) {
        if (payments.isEmpty()) {
            rowsNode.getChildren().add(makeEmptyState("No renewed members today."));
            return;
        }
        int limit = Math.min(payments.size(), 8);
        for (int i = 0; i < limit; i++) {
            PaymentDAO.PaymentRecord payment = payments.get(i);
            rowsNode.getChildren().add(makeMemberSummaryRow(
                "#" + safeText(payment.memberCode()),
                safeText(payment.memberName()),
                safeText(payment.planTypeSnapshot()) + " - " + safeText(payment.paymentMethod()),
                "Paid " + String.format("PHP %.2f", payment.amount())
            ));
        }
        addMoreSummaryRow(rowsNode, payments.size() - limit);
    }

    private void addMoreSummaryRow(VBox rowsNode, int remaining) {
        if (remaining <= 0) {
            return;
        }
        Label more = new Label("+" + remaining + " more");
        more.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        more.setTextFill(Color.web(ACCENT));
        more.setStyle("-fx-text-fill: " + ACCENT + ";");
        more.setPadding(new Insets(8, 0, 0, 0));
        rowsNode.getChildren().add(more);
    }

    private HBox makeMemberSummaryRow(String code, String name, String detail, String trailing) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(9, 0, 9, 0));
        row.setStyle(
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        VBox textBox = new VBox(2);
        Label nameLabel = new Label(safeText(name).isBlank() ? "Unnamed member" : name);
        nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        nameLabel.setTextFill(Color.web(TEXT_WHITE));
        nameLabel.setStyle("-fx-text-fill: " + TEXT_WHITE + ";");
        Label detailLabel = new Label(code + " - " + detail);
        detailLabel.setFont(Font.font("Poppins", 10));
        detailLabel.setTextFill(Color.web(TEXT_DIM));
        detailLabel.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        textBox.getChildren().addAll(nameLabel, detailLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label trailingLabel = new Label(trailing);
        trailingLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        trailingLabel.setTextFill(Color.web(ACCENT));
        trailingLabel.setStyle("-fx-text-fill: " + ACCENT + ";");
        row.getChildren().addAll(textBox, spacer, trailingLabel);
        return row;
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
            if ("Cancelled".equalsIgnoreCase(m.status())) {
                continue;
            }
            if (!"All Plans".equals(planFilter)) {
                String t = m.membershipType();
                if (t == null || !t.equalsIgnoreCase(planFilter)) {
                    continue;
                }
            }
            if (!"All Status".equals(statusFilter)) {
                String s = effectiveMemberStatus(m);
                if (s == null || !s.equalsIgnoreCase(statusFilter)) {
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
            dataRow.setStyle(
                "-fx-background-color: " + bg + ";" +
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                "-fx-border-width: 0 0 1 0;"
            );
            dataRow.setAlignment(Pos.CENTER_LEFT);

            GridPane rowGrid = new GridPane();
            rowGrid.setHgap(4);
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

            rowGrid.add(makeCell(code, TABLE_TEXT, true), 0, 0);
            rowGrid.add(makeCell(m.fullName(), TABLE_TEXT, false), 1, 0);
            rowGrid.add(makeCell(genderLabel, TABLE_SUBTEXT, false), 2, 0);
            rowGrid.add(makeCell(m.contactNumber(), TABLE_SUBTEXT, false), 3, 0);
            rowGrid.add(makePlanBadge(m.membershipType() != null ? m.membershipType() : "Monthly"), 4, 0);
            rowGrid.add(makeCell(start, TABLE_SUBTEXT, false), 5, 0);
            HBox statusBox = new HBox(6);
            statusBox.setAlignment(Pos.CENTER_LEFT);
            statusBox.getChildren().add(makeStatusBadge(effectiveMemberStatus(m)));
            Label expiryBadge = makeMembershipExpiryBadge(m);
            if (expiryBadge != null) {
                statusBox.getChildren().add(expiryBadge);
            }
            rowGrid.add(statusBox, 6, 0);

            HBox actions = new HBox(6);
            actions.setAlignment(Pos.CENTER_LEFT);
            actions.setMinWidth(245);
            Button viewBtn = makeActionBtn("", "#77749B");
            viewBtn.setOnAction(e -> showMemberDetails(m));
            viewBtn.setText("View");

            Button editBtn = makeActionBtn("Edit", WARNING_TEXT);
            editBtn.setOnAction(e -> showEditMemberDialog(m, fullRefresh));

            Button archiveBtn = makeActionBtn("Archive", TEXT_MUTED);
            archiveBtn.setOnAction(e -> archiveMember(dao, m, fullRefresh));
            boolean renewable = isMembershipExpired(m);
            Button attendanceBtn = makeActionBtn(renewable ? "Renew" : "Check", renewable ? ACCENT : SUCCESS_TEXT);
            attendanceBtn.setOnAction(e -> {
                if (renewable) {
                    showRenewMemberDialog(m, fullRefresh);
                } else {
                    recordMemberAttendance(dao, m, fullRefresh);
                }
            });

            actions.getChildren().addAll(viewBtn, editBtn, archiveBtn, attendanceBtn);
            rowGrid.add(actions, 7, 0);

            dataRow.getChildren().add(rowGrid);
            String finalBg = bg;
            dataRow.setOnMouseEntered(ev -> dataRow.setStyle(
                "-fx-background-color: " + HOVER_BG + ";" +
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                "-fx-border-width: 0 0 1 0;"
            ));
            dataRow.setOnMouseExited(ev -> dataRow.setStyle(
                "-fx-background-color: " + finalBg + ";" +
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                "-fx-border-width: 0 0 1 0;"
            ));

            rows.getChildren().add(dataRow);
            r++;
        }
    }

    //  Helper builders 
    private void showMemberDetails(MemberDAO.MemberRecord m) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Member Details");
        dialog.setResizable(true);

        VBox root = new VBox(18);
        root.setPadding(new Insets(26));
        root.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        StackPane avatar = new StackPane();
        avatar.setPrefSize(58, 58);
        avatar.setMinSize(58, 58);
        avatar.setMaxSize(58, 58);
        Rectangle avatarBg = new Rectangle(58, 58);
        avatarBg.setArcWidth(18);
        avatarBg.setArcHeight(18);
        avatarBg.setFill(Color.web(ACCENT));
        Text initials = new Text(memberInitials(m));
        initials.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        initials.setFill(Color.WHITE);
        avatar.getChildren().addAll(avatarBg, initials);

        VBox titleBox = new VBox(5);
        Text name = new Text(m.fullName());
        name.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        name.setFill(Color.web(DETAIL_TITLE));
        outlineText(name, 0.22);
        HBox badges = new HBox(8);
        badges.setAlignment(Pos.CENTER_LEFT);
        badges.getChildren().addAll(
            makeStatusBadge(effectiveMemberStatus(m)),
            makePlanBadge(blankFallback(m.membershipType(), "No Plan"))
        );
        Label code = new Label("#" + safeText(m.memberCode()));
        code.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        code.setTextFill(Color.web(DETAIL_MUTED));
        code.setStyle("-fx-text-fill: " + DETAIL_MUTED + ";");
        titleBox.getChildren().addAll(name, code, badges);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Button close = makeActionBtn("Close", TEXT_MUTED);
        close.setPrefWidth(72);
        close.setOnAction(e -> dialog.close());
        header.getChildren().addAll(avatar, titleBox, headerSpacer, close);

        GridPane summary = new GridPane();
        summary.setHgap(12);
        summary.setVgap(12);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            cc.setHgrow(Priority.ALWAYS);
            summary.getColumnConstraints().add(cc);
        }
        summary.add(detailMetric("Status", effectiveMemberStatus(m)), 0, 0);
        summary.add(detailMetric("Plan", blankFallback(m.membershipType(), "No Plan")), 1, 0);
        summary.add(detailMetric("End Date", blankFallback(textDate(m.membershipEndDate()), "Not set")), 2, 0);
        summary.add(detailMetric("Balance", memberBalanceText(m)), 3, 0);

        GridPane contactGrid = detailGrid();
        addDetailField(contactGrid, 0, "Phone", m.contactNumber());
        addDetailField(contactGrid, 1, "Email", m.email());
        addDetailField(contactGrid, 2, "Address", m.address());
        addDetailField(contactGrid, 3, "Gender", m.gender());
        addDetailField(contactGrid, 4, "Date of Birth", textDate(m.dateOfBirth()));

        GridPane membershipGrid = detailGrid();
        addDetailField(membershipGrid, 0, "Membership Type", m.membershipType());
        addDetailField(membershipGrid, 1, "Start Date", textDate(m.membershipStartDate()));
        addDetailField(membershipGrid, 2, "End Date", textDate(m.membershipEndDate()));
        addDetailField(membershipGrid, 3, "Payment State", hasPendingMembershipPayment(m) ? "Payment Pending" : "No pending balance");
        if ("Per Session".equalsIgnoreCase(m.membershipType())) {
            addDetailField(membershipGrid, 4, "Sessions Paid", String.valueOf(m.sessionsPaid()));
            addDetailField(membershipGrid, 5, "Sessions Remaining", String.valueOf(m.sessionsRemaining()));
        }

        GridPane emergencyGrid = detailGrid();
        addDetailField(emergencyGrid, 0, "Emergency Contact", m.emergencyContact());
        addDetailField(emergencyGrid, 1, "Emergency Phone", m.emergencyPhone());

        VBox content = new VBox(14);
        content.getChildren().addAll(
            detailCard("Contact Information", contactGrid),
            detailCard("Membership Information", membershipGrid),
            detailCard("Emergency Contact", emergencyGrid),
            detailCard("Member Plan History", buildPlanHistoryList(m))
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.getChildren().addAll(header, summary, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Scene scene = new Scene(root, 780, 660);
        dialog.setScene(scene);
        dialog.setMinWidth(700);
        dialog.setMinHeight(560);
        dialog.showAndWait();
    }

    private String memberInitials(MemberDAO.MemberRecord member) {
        String first = safeText(member.firstName());
        String last = safeText(member.lastName());
        String a = first.isBlank() ? "" : first.substring(0, 1);
        String b = last.isBlank() ? "" : last.substring(0, 1);
        String initials = (a + b).trim();
        return initials.isBlank() ? "M" : initials.toUpperCase();
    }

    private String memberBalanceText(MemberDAO.MemberRecord member) {
        if ("Cancelled".equalsIgnoreCase(member.status())) {
            return "Archived";
        }
        try {
            double balance = new PaymentDAO().balanceDueForMember(member.memberId());
            return balance > 0.009 ? formatPeso(balance) : "Paid";
        } catch (RuntimeException ex) {
            return "Unavailable";
        }
    }

    private VBox detailMetric(String label, String value) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(13, 14, 13, 14));
        box.setMinHeight(76);
        box.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        labelNode.setTextFill(Color.web(DETAIL_MUTED));
        labelNode.setStyle("-fx-text-fill: " + DETAIL_MUTED + ";");
        Label valueNode = new Label(blankFallback(value, "-"));
        valueNode.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        valueNode.setTextFill(Color.web(DETAIL_TITLE));
        valueNode.setStyle("-fx-text-fill: " + DETAIL_TITLE + ";");
        valueNode.setWrapText(true);
        box.getChildren().addAll(labelNode, valueNode);
        return box;
    }

    private VBox detailCard(String title, Node body) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;"
        );
        Text heading = new Text(title);
        heading.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        heading.setFill(Color.web(DETAIL_TITLE));
        Rectangle line = new Rectangle(42, 3);
        line.setArcWidth(3);
        line.setArcHeight(3);
        line.setFill(Color.web(ModernDesignSystem.ACCENT_YELLOW));
        card.getChildren().addAll(new VBox(4, heading, line), body);
        return card;
    }

    private GridPane detailGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        return grid;
    }

    private void addDetailField(GridPane grid, int index, String label, String value) {
        grid.add(detailField(label, value), index % 2, index / 2);
    }

    private VBox detailField(String label, String value) {
        VBox box = new VBox(4);
        box.setMinWidth(0);
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        labelNode.setTextFill(Color.web(ACCENT));
        labelNode.setStyle("-fx-text-fill: " + ACCENT + ";");
        Label valueNode = new Label(blankFallback(value, "-"));
        valueNode.setFont(Font.font("Poppins", 12));
        valueNode.setTextFill(Color.web(DETAIL_TEXT));
        valueNode.setStyle("-fx-text-fill: " + DETAIL_TEXT + ";");
        valueNode.setWrapText(true);
        valueNode.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(labelNode, valueNode);
        return box;
    }

    private VBox buildPlanHistoryList(MemberDAO.MemberRecord member) {
        VBox rows = new VBox(8);
        rows.getChildren().add(historyRow(
            "Current Plan",
            blankFallback(member.membershipType(), "No plan"),
            blankFallback(textDate(member.membershipStartDate()), "Not set")
                + " to "
                + blankFallback(textDate(member.membershipEndDate()), "Not set"),
            effectiveMemberStatus(member)
        ));

        List<PaymentDAO.PaymentRecord> payments = new PaymentDAO().findByMember(member.memberId());
        int shown = 0;
        for (PaymentDAO.PaymentRecord payment : payments) {
            if (!"Membership".equalsIgnoreCase(payment.paymentType())) {
                continue;
            }
            rows.getChildren().add(historyRow(
                textDate(payment.paymentDate()),
                blankFallback(payment.planTypeSnapshot(), blankFallback(member.membershipType(), "Membership")),
                formatPeso(payment.amount()) + " - " + blankFallback(payment.paymentMethod(), "Payment"),
                blankFallback(payment.status(), "Recorded")
            ));
            if (payment.notes() != null && !payment.notes().isBlank()) {
                Label notes = new Label(payment.notes());
                notes.setFont(Font.font("Poppins", 10));
                notes.setTextFill(Color.web(DETAIL_MUTED));
                notes.setStyle("-fx-text-fill: " + DETAIL_MUTED + ";");
                notes.setWrapText(true);
                notes.setPadding(new Insets(-4, 12, 4, 14));
                rows.getChildren().add(notes);
            }
            shown++;
        }
        if (shown == 0) {
            Label empty = new Label("No payment or plan-change records saved yet.");
            empty.setFont(Font.font("Poppins", 11));
            empty.setTextFill(Color.web(DETAIL_MUTED));
            empty.setStyle("-fx-text-fill: " + DETAIL_MUTED + ";");
            empty.setPadding(new Insets(8, 0, 4, 0));
            rows.getChildren().add(empty);
        }
        return rows;
    }

    private HBox historyRow(String date, String title, String detail, String status) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        VBox main = new VBox(3);
        Label titleNode = new Label(blankFallback(title, "-"));
        titleNode.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        titleNode.setTextFill(Color.web(DETAIL_TITLE));
        titleNode.setStyle("-fx-text-fill: " + DETAIL_TITLE + ";");
        Label detailNode = new Label(blankFallback(date, "-") + " | " + blankFallback(detail, "-"));
        detailNode.setFont(Font.font("Poppins", 10));
        detailNode.setTextFill(Color.web(DETAIL_MUTED));
        detailNode.setStyle("-fx-text-fill: " + DETAIL_MUTED + ";");
        detailNode.setWrapText(true);
        main.getChildren().addAll(titleNode, detailNode);
        HBox.setHgrow(main, Priority.ALWAYS);
        row.getChildren().addAll(main, makeStatusBadge(status));
        return row;
    }

    private void recordMemberAttendance(MemberDAO dao, MemberDAO.MemberRecord m, Runnable fullRefresh) {
        if ("Cancelled".equalsIgnoreCase(m.status())) {
            alertErr("Archived members cannot be checked in.");
            return;
        }
        if (isMembershipExpired(m)) {
            alertErr("This membership is expired. Renew the member before checking them in.");
            return;
        }
        if (hasPendingMembershipPayment(m)) {
            alertErr("This member still has a pending membership payment. Complete payment before check-in.");
            return;
        }
        TextInputDialog notesDialog = new TextInputDialog("");
        notesDialog.setTitle("Member Attendance");
        notesDialog.setHeaderText("Record check-in for " + m.fullName());
        notesDialog.setContentText("Notes (optional):");
        Optional<String> notes = notesDialog.showAndWait();
        if (notes.isPresent()) {
            String sessionType = derivedAttendanceSessionType(m.membershipType());
            if (dao.recordAttendance(m.memberId(), sessionType, notes.get().trim())) {
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Attendance");
                ok.setHeaderText("Check-in recorded");
                ok.setContentText(m.fullName() + " was checked in as " + sessionType + ".");
                ok.showAndWait();
                fullRefresh.run();
            } else {
                if ("Per Session".equalsIgnoreCase(m.membershipType())) {
                    alertErr("No remaining sessions. Record a Per Session payment before checking this member in.");
                } else {
                    alertErr("Could not record attendance. Check membership status and payment balance.");
                }
            }
        }
    }

    private String derivedAttendanceSessionType(String membershipType) {
        if ("Per Session".equalsIgnoreCase(membershipType)) {
            return "Per Session";
        }
        if ("Daily".equalsIgnoreCase(membershipType)) {
            return "Daily";
        }
        return "Member Session";
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

    private void showArchivedMembersDialog(MemberDAO dao, Runnable fullRefresh) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Archived Members");
        dialog.setResizable(true);

        VBox root = new VBox(18);
        root.setPadding(new Insets(24));
        root.setPrefSize(900, 540);
        root.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Rectangle accent = new Rectangle(5, 44);
        accent.setArcWidth(5);
        accent.setArcHeight(5);
        accent.setFill(Color.web(ACCENT));

        Text title = new Text("Archived Members");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        outlineText(title, 0.24);

        Text subtitle = new Text("Archived members are hidden from the main list. Admins may permanently delete archived records.");
        subtitle.setFont(Font.font("Poppins", 11));
        subtitle.setFill(Color.web(TABLE_SUBTEXT));
        outlineText(subtitle, 0.14);
        titleRow.getChildren().addAll(accent, new VBox(2, title, subtitle));

        VBox tableCard = new VBox(0);
        tableCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;"
        );
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(ACCENT, 0.10));
        shadow.setRadius(12);
        shadow.setOffsetY(4);
        tableCard.setEffect(shadow);

        String[] headers = {"Member ID", "Full Name", "Plan", "Start Date", "End Date", "Actions"};
        double[] widths = {12, 22, 14, 13, 13, 26};
        HBox header = new HBox();
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setStyle(
            "-fx-background-color: " + HEADER_BG + ";" +
            "-fx-background-radius: 18 18 0 0;" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        GridPane headerGrid = new GridPane();
        headerGrid.setHgap(4);
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
            label.setTextFill(Color.web(TABLE_TEXT));
            label.setStyle("-fx-text-fill: " + TABLE_TEXT + ";");
            headerGrid.add(label, i, 0);
        }
        header.getChildren().add(headerGrid);

        VBox list = new VBox(0);
        Runnable[] refreshArchived = new Runnable[1];
        refreshArchived[0] = () -> {
            list.getChildren().clear();
            List<MemberDAO.MemberRecord> archived = dao.findByStatus("Cancelled");
            if (archived.isEmpty()) {
                list.getChildren().add(makeEmptyState("No archived members."));
                return;
            }

            for (int i = 0; i < archived.size(); i++) {
                MemberDAO.MemberRecord member = archived.get(i);
                list.getChildren().add(buildArchivedMemberRow(dao, member, widths, i, refreshArchived[0], fullRefresh));
            }
        };

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setMaxHeight(360);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        tableCard.getChildren().addAll(header, scroll);

        Button close = new Button("Close");
        close.setPrefHeight(38);
        close.setPadding(new Insets(0, 18, 0, 18));
        close.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        close.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-cursor: hand;"
        );
        close.setOnAction(e -> dialog.close());

        HBox footer = new HBox(close);
        footer.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().addAll(titleRow, tableCard, footer);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        refreshArchived[0].run();
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private HBox buildArchivedMemberRow(
        MemberDAO dao,
        MemberDAO.MemberRecord member,
        double[] widths,
        int index,
        Runnable refreshArchived,
        Runnable fullRefresh
    ) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 14, 12, 14));
        row.setStyle(
            "-fx-background-color: " + (index % 2 == 0 ? BG_CARD : BG_ROW_ALT) + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(4);
        for (double width : widths) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(width);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        grid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(grid, Priority.ALWAYS);

        Button view = makeArchivedActionBtn("View", TEXT_MUTED, 44);
        view.setOnAction(e -> showMemberDetails(member));

        Button unarchive = makeArchivedActionBtn("Unarchive", SUCCESS_TEXT, 82);
        unarchive.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Unarchive Member");
            confirm.setHeaderText("Restore " + member.fullName() + "?");
            confirm.setContentText("This returns the member to the main member list.");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK && dao.updateStatus(member.memberId(), "Active")) {
                refreshArchived.run();
                fullRefresh.run();
            }
        });

        HBox actions = new HBox(6, view, unarchive);
        actions.setAlignment(Pos.CENTER_LEFT);
        if (AppSession.currentUser().isAdmin()) {
            Button delete = makeArchivedActionBtn("Delete", ACCENT, 58);
            delete.setOnAction(e -> permanentlyDeleteArchivedMember(dao, member, refreshArchived, fullRefresh));
            actions.getChildren().add(delete);
        }

        grid.add(archivedCell("#" + member.memberCode(), TABLE_TEXT, true), 0, 0);
        grid.add(archivedCell(member.fullName(), TABLE_TEXT, true), 1, 0);
        grid.add(makePlanBadge(safeText(member.membershipType()).isBlank() ? "No Plan" : member.membershipType()), 2, 0);
        grid.add(archivedCell(textDate(member.membershipStartDate()), TABLE_SUBTEXT, false), 3, 0);
        grid.add(archivedCell(textDate(member.membershipEndDate()), TABLE_SUBTEXT, false), 4, 0);
        grid.add(actions, 5, 0);

        row.getChildren().add(grid);
        return row;
    }

    private void permanentlyDeleteArchivedMember(
        MemberDAO dao,
        MemberDAO.MemberRecord member,
        Runnable refreshArchived,
        Runnable fullRefresh
    ) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Archived Member");
        confirm.setHeaderText("Permanently delete " + member.fullName() + "?");
        confirm.setContentText("This cannot be undone. Keep archived instead if you still need payment, attendance, or report history.");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) {
            return;
        }
        if (dao.delete(member.memberId())) {
            refreshArchived.run();
            fullRefresh.run();
        } else {
            alertErr("Could not delete this member. Existing payments or attendance may still depend on this record.");
        }
    }

    private Label archivedCell(String text, String color, boolean bold) {
        Label label = new Label(safeText(text).isBlank() ? "-" : text);
        label.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        label.setTextFill(Color.web(color));
        label.setStyle("-fx-text-fill: " + color + ";");
        label.setWrapText(true);
        return label;
    }

    private Button makeArchivedActionBtn(String text, String color, double width) {
        Button btn = makeActionBtn(text, color);
        btn.setMinWidth(width);
        btn.setPrefWidth(width);
        btn.setMaxWidth(width);
        btn.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String readableColor = readableAccent(color);
        String baseStyle =
            "-fx-background-color: rgba(26,19,99,0.045);" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-font-size: 10;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 4 3 4;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: rgba(26,19,99,0.08);" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;";
        String hoverStyle =
            "-fx-background-color: rgba(26,19,99,0.10);" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-font-size: 10;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 4 3 4;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: rgba(26,19,99,0.12);" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }

    private void openPaymentScreenForRenewal(MemberDAO.MemberRecord member) {
        try {
            Stage stage = new Stage();
            new PaymentScreen(member.memberCode()).start(stage);
            stage.setTitle("Payment & Billing - Renew " + member.fullName() + " (" + member.memberCode() + ")");
        } catch (Exception ex) {
            alertErr("Could not open Payment & Billing. Open it from the sidebar to collect payment.");
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

        title.setText("Renew Member");
        Text subtitle = new Text(m.fullName() + " keeps the same details. Select a plan, then process payment.");
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

        Button renew = new Button("Proceed to Payment");
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
                m.emergencyPhone(),
                m.sessionsPaid(),
                m.sessionsRemaining()
            );

            if (new MemberDAO().update(renewed)) {
                PaymentDAO paymentDAO = new PaymentDAO();
                int uid = AppSession.currentUser().userId();
                paymentDAO.closeBillingOnUpgrade(m.memberId(), uid);
                paymentDAO.ensureBillingForMember(
                    m.memberId(),
                    plan.getValue(),
                    Date.valueOf(end),
                    uid
                );
                dialog.close();
                onSaved.run();
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Member Renewed");
                ok.setHeaderText("Plan selected");
                ok.setContentText(m.fullName() + " was renewed with saved details. Payment & Billing will open with this member selected.");
                ok.showAndWait();
                openPaymentScreenForRenewal(m);
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
                emergencyPhone.getText().trim(),
                m.sessionsPaid(),
                m.sessionsRemaining()
            );

            boolean planChanged = !safeText(m.membershipType()).equalsIgnoreCase(safeText(updated.membershipType()));
            if (planChanged) {
                try {
                    PaymentDAO.UpgradeResult result = new PaymentDAO().processPlanUpgrade(
                        updated,
                        AppSession.currentUser().userId()
                    );
                    dialog.close();
                    onSaved.run();
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("Plan Upgrade");
                    ok.setHeaderText("Previous billing closed. New " + result.newPlan() + " billing created.");
                    ok.setContentText("Old plan snapshot: " + safeText(result.previousPlan()));
                    ok.showAndWait();
                } catch (RuntimeException ex) {
                    alertErr("Could not complete plan upgrade. No changes were saved.");
                }
            } else if (new MemberDAO().update(updated)) {
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

    private String blankFallback(String text, String fallback) {
        return text != null && !text.isBlank() ? text : fallback;
    }

    private String formatPeso(double amount) {
        return "PHP " + String.format("%,.2f", amount);
    }

    private String sessionCreditText(MemberDAO.MemberRecord m) {
        if (!"Per Session".equalsIgnoreCase(m.membershipType())) {
            return "";
        }
        return "\nSessions Paid: " + m.sessionsPaid()
            + "\nSessions Remaining: " + m.sessionsRemaining();
    }

    private String statusText(String status) {
        return "Cancelled".equalsIgnoreCase(status) ? "Archived" : safeText(status);
    }

    private String effectiveMemberStatus(MemberDAO.MemberRecord member) {
        if (member == null) {
            return "";
        }
        if ("Cancelled".equalsIgnoreCase(member.status())) {
            return "Archived";
        }
        if (isMembershipExpired(member)) {
            return "Expired";
        }
        if (hasPendingMembershipPayment(member)) {
            return "Payment Pending";
        }
        return safeText(member.status()).isBlank() ? "Active" : safeText(member.status());
    }

    private boolean hasPendingMembershipPayment(MemberDAO.MemberRecord member) {
        if (member == null || "Cancelled".equalsIgnoreCase(member.status())) {
            return false;
        }
        try {
            return new PaymentDAO().balanceDueForMember(member.memberId()) > 0.009;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private boolean isMembershipExpired(MemberDAO.MemberRecord member) {
        if (member == null || member.membershipEndDate() == null) {
            return false;
        }
        if ("Cancelled".equalsIgnoreCase(member.status())) {
            return false;
        }
        return "Expired".equalsIgnoreCase(member.status())
            || member.membershipEndDate().toLocalDate().isBefore(LocalDate.now());
    }

    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        String textColor = readableAccent(color);
        l.setTextFill(Color.web(textColor));
        l.setPadding(new Insets(0, 8, 0, 8));
        l.setMinWidth(0);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setStyle("-fx-text-fill: " + textColor + ";");
        return l;
    }

    private Label makeStatusBadge(String status) {
        String display = statusText(status);
        Label b = new Label(display);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c, bg;
        switch (display.toLowerCase()) {
            case "active": c = "#14532D"; bg = "#DCFCE7"; break;
            case "payment pending": c = "#92400E"; bg = "#FEF3C7"; break;
            case "expired": c = "#312E81"; bg = "#E0E7FF"; break;
            case "archived": case "cancelled": c = "#374151"; bg = "#E5E7EB"; break;
            case "suspended": c = "#713F12"; bg = "#FEF3C7"; break;
            default: c = TABLE_TEXT; bg = "#E5E7EB"; break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + c + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: rgba(26,19,99,0.08);" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 3 10 3 10;"
        );
        b.setPadding(new Insets(0, 8, 0, 8));
        return b;
    }

    private Label makeMembershipExpiryBadge(MemberDAO.MemberRecord member) {
        if (!"Active".equalsIgnoreCase(effectiveMemberStatus(member)) || member.membershipEndDate() == null) {
            return null;
        }
        LocalDate expiry = member.membershipEndDate().toLocalDate();
        LocalDate today = LocalDate.now();
        if (!expiry.isAfter(today.plusDays(7))) {
            return makeExpiryBadge("Expiring", WARNING_TEXT, "#FFF5C2");
        }
        return null;
    }

    private Label makeExpiryBadge(String text, String color, String bg) {
        Label badge = new Label(text);
        badge.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        badge.setTextFill(Color.web(color));
        badge.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + color + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 2 8 2 8;"
        );
        return badge;
    }

    private Label makePlanBadge(String plan) {
        String display = plan != null && !plan.isBlank() ? plan : "No Plan";
        Label b = new Label(display);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c, bg;
        switch (display.toLowerCase()) {
            case "monthly": c = "#312E81"; bg = "#E0E7FF"; break;
            case "daily": case "per session": c = "#713F12"; bg = "#FEF3C7"; break;
            case "quarterly": case "semi annual": case "yearly": case "annual":
                c = "#14532D"; bg = "#DCFCE7"; break;
            default: c = TABLE_TEXT; bg = "#E5E7EB"; break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + c + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: rgba(26,19,99,0.08);" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 3 10 3 10;"
        );
        b.setPadding(new Insets(0, 8, 0, 8));
        return b;
    }

    private Button makeActionBtn(String icon, String color) {
        Button btn = new Button(icon);
        String readableColor = readableAccent(color);
        btn.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        btn.setStyle(
            "-fx-background-color: rgba(26,19,99,0.045);" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 4 6 4 6;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: rgba(26,19,99,0.08);" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(26,19,99,0.10);" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 4 6 4 6;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: rgba(26,19,99,0.12);" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: rgba(26,19,99,0.045);" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 4 6 4 6;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: rgba(26,19,99,0.08);" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
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
            "-fx-border-color: " + readableAccent(color) + " " + BORDER + " " + BORDER + " " + readableAccent(color) + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1 1 1 4;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web(ACCENT, 0.09));
        ds.setRadius(8); ds.setOffsetY(3);
        chip.setEffect(ds);
        Text val = new Text(value);
        val.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        val.setFill(Color.web(readableAccent(color)));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", 11));
        lbl.setFill(Color.web(TEXT_DIM));
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
            "-fx-pref-height: 38;"
        );
    }

    private Button makePagBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        String bg = active ? ACCENT : BG_CARD;
        String fg = active ? ModernDesignSystem.WHITE : TEXT_MUTED;
        b.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + fg + ";" +
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
        plan.setPrefHeight(40);
        planBox.getChildren().addAll(pl, plan);
        form.add(planBox, 1, r++);
        VBox gBox = new VBox(6);
        Label gl = new Label("GENDER");
        gl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        gl.setTextFill(Color.web(ACCENT));
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
                "",
                0,
                0
            );
            int newId = dao.insert(rec, uid);
            if (newId > 0) {
                new PaymentDAO().ensureBillingForMember(newId, plan.getValue(), end, uid);
                dialog.close();
                onSaved.run();
                Alert created = new Alert(Alert.AlertType.INFORMATION);
                created.setTitle("Member Registered");
                created.setHeaderText(fn + " " + ln + " is registered");
                created.setContentText("Payment status: Pending. An unpaid invoice was created in Payment & Billing.");
                created.showAndWait();
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
        picker.setPrefHeight(40);
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
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
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;"
        );
        f.focusedProperty().addListener((o, old, focused) -> f.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;"
        ));
    }

    private void outlineText(Text text, double width) {
        text.setStroke(Color.TRANSPARENT);
        text.setStrokeWidth(0);
    }

    private String readableAccent(String color) {
        if (SUCCESS.equalsIgnoreCase(color)) return SUCCESS_TEXT;
        if (WARNING.equalsIgnoreCase(color)) return WARNING_TEXT;
        return color;
    }

    public static void main(String[] args) { launch(args); }
}
