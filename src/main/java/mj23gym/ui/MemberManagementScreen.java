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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mj23gym.dao.MemberDAO;

/**
 * MJ23 Playgrind Gym – Member Management Screen
 * Searchable, filterable member table with Add / Edit / View controls.
 */
public class MemberManagementScreen extends Application {

    static final String BG_MAIN      = "#1a1a2e";
    static final String BG_SIDEBAR   = "#0d1b2a";
    static final String BG_CARD      = "#1e2a3a";
    static final String BG_ROW_ALT   = "#253545";
    static final String ACCENT       = "#e63946";
    static final String ACCENT_DARK  = "#c0303b";
    static final String TEXT_WHITE   = "#ffffff";
    static final String TEXT_MUTED   = "#b0bec5";
    static final String TEXT_DIM     = "#607080";
    static final String BORDER       = "#253545";
    static final String SUCCESS      = "#4caf50";
    static final String WARNING      = "#ff9800";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym – Member Management");

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
    // SIDEBAR (same structure as Dashboard)
    // ══════════════════════════════════════════════════════════════
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");

        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web(ACCENT));

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
        logoTxt.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        logoTxt.setFill(Color.WHITE);
        logoBadge.getChildren().addAll(logoBg, logoTxt);
        VBox logoText = new VBox(1);
        Text g1 = new Text("MJ23 PLAYGRIND");
        g1.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        g1.setFill(Color.web(TEXT_WHITE));
        Text g2 = new Text("GYM");
        g2.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        g2.setFill(Color.web(ACCENT));
        logoText.getChildren().addAll(g1, g2);
        logoArea.getChildren().addAll(logoBadge, logoText);

        // Menu items
        String[][] items = {
            {"🏠","Dashboard"},{"👥","Member Management"},{"💳","Payment & Billing"},
            {"📦","Inventory"},{"🏋","Equipment"},{"🛒","Point of Sale"},{"📊","Reports"}
        };
        VBox menuBox = new VBox(2);
        menuBox.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items) {
            boolean active = it[1].equals("Member Management");
            menuBox.getChildren().add(buildMenuItem(it[0], it[1], active));
        }

        String[][] sysItems = {{"⚙","Settings"},{"❓","Help"},{"ℹ","About"}};
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
        lbl.setFont(Font.font("Verdana", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        lbl.setFill(active ? Color.web(TEXT_WHITE) : Color.web(TEXT_MUTED));
        item.getChildren().addAll(bar, ico, lbl);
        if (active) {
            item.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 8;");
        } else {
            item.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;");
            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;"));
        }
        return item;
    }

    private Label makeSecLabel(String t) {
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
        topBar.setSpacing(16);
        topBar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        VBox pg = new VBox(2);
        Text pgT = new Text("Member Management");
        pgT.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        pgT.setFill(Color.web(TEXT_WHITE));
        Text pgS = new Text("Manage gym members, attendance, and membership plans");
        pgS.setFont(Font.font("Verdana", 11));
        pgS.setFill(Color.web(TEXT_MUTED));
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

        // ── Stats row ──────────────────────────────────────────────
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
            makeStatChipText("👥 Total Members", statTotalVal, TEXT_WHITE),
            makeStatChipText("✅ Active", statActiveVal, SUCCESS),
            makeStatChipText("❌ Expired", statExpiredVal, ACCENT),
            makeStatChipText("📅 Renewals Today", statRenewVal, WARNING)
        );

        // ── Controls row ───────────────────────────────────────────
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("🔍  Search member name or ID...");
        search.setPrefWidth(280);
        search.setPrefHeight(38);
        search.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 14 0 14;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;"
        );

        ComboBox<String> filterPlan = new ComboBox<>();
        filterPlan.getItems().addAll("All Plans", "Monthly", "Daily", "Quarterly", "Yearly", "Per Session");
        filterPlan.setValue("All Plans");
        styleCombo(filterPlan);

        ComboBox<String> filterStatus = new ComboBox<>();
        filterStatus.getItems().addAll("All Status", "Active", "Expired", "Suspended", "Cancelled");
        filterStatus.setValue("All Status");
        styleCombo(filterStatus);

        Region ctrlSp = new Region();
        HBox.setHgrow(ctrlSp, Priority.ALWAYS);

        Button addBtn = new Button("＋  Add Member");
        addBtn.setPrefHeight(38);
        addBtn.setPadding(new Insets(0, 18, 0, 18));
        addBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        addBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(
            "-fx-background-color: " + ACCENT_DARK + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));

        controls.getChildren().addAll(search, filterPlan, filterStatus, ctrlSp, addBtn);

        // ── Table card ─────────────────────────────────────────────
        VBox tableCard = new VBox(0);
        tableCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.3));
        ds.setRadius(12); ds.setOffsetY(4);
        tableCard.setEffect(ds);

        // Table header row
        String[] headers = {"Member ID","Full Name","Gender","Phone","Plan","Registered","Status","Actions"};
        double[] colWidths = {8, 16, 7, 12, 10, 11, 9, 14};

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
            h.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
            h.setTextFill(Color.web(TEXT_DIM));
            h.setPadding(new Insets(0, 8, 0, 8));
            headerGrid.add(h, c, 0);
        }
        tableHeaderRow.getChildren().add(headerGrid);

        VBox rows = new VBox(0);
        Text pageInfo = new Text();
        pageInfo.setFont(Font.font("Verdana", 11));
        pageInfo.setFill(Color.web(TEXT_MUTED));

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

        tableCard.getChildren().addAll(tableHeaderRow, rows, pagination);

        body.getChildren().addAll(statsRow, controls, tableCard);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        return content;
    }

    private void styleStatValue(Text val) {
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
    }

    private HBox makeStatChipText(String label, Text valueNode, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.2));
        ds.setRadius(8); ds.setOffsetY(3);
        chip.setEffect(ds);
        valueNode.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Verdana", 11));
        lbl.setFill(Color.web(TEXT_MUTED));
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
                String s = m.status();
                if (s == null || !s.equalsIgnoreCase(statusFilter)) {
                    continue;
                }
            }
            filtered.add(m);
        }
        pageInfo.setText("Showing " + filtered.size() + " member(s)");

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
                : "F".equalsIgnoreCase(m.gender()) ? "♀ Female" : "♂ Male";
            String start = m.membershipStartDate() != null ? m.membershipStartDate().toString() : "—";

            rowGrid.add(makeCell(code, ACCENT, true), 0, 0);
            rowGrid.add(makeCell(m.fullName(), TEXT_WHITE, false), 1, 0);
            rowGrid.add(makeCell(genderLabel, TEXT_MUTED, false), 2, 0);
            rowGrid.add(makeCell(m.contactNumber(), TEXT_MUTED, false), 3, 0);
            rowGrid.add(makePlanBadge(m.membershipType() != null ? m.membershipType() : "Monthly"), 4, 0);
            rowGrid.add(makeCell(start, TEXT_MUTED, false), 5, 0);
            rowGrid.add(makeStatusBadge(m.status() != null ? m.status() : "Active"), 6, 0);

            HBox actions = new HBox(6);
            actions.setAlignment(Pos.CENTER_LEFT);
            Button viewBtn = makeActionBtn("👁", "#2196f3");
            viewBtn.setOnAction(e -> {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Member");
                a.setHeaderText(m.fullName());
                a.setContentText(
                    "Code: " + m.memberCode() + "\nEmail: " + m.email() + "\nPhone: " + m.contactNumber()
                        + "\nAddress: " + m.address() + "\nPlan: " + m.membershipType()
                        + "\nStart: " + start + "\nEnd: "
                        + (m.membershipEndDate() != null ? m.membershipEndDate().toString() : "—")
                        + "\nStatus: " + m.status()
                );
                a.showAndWait();
            });
            Button delBtn = makeActionBtn("🗑", ACCENT);
            delBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete member");
                confirm.setHeaderText("Remove " + m.fullName() + "?");
                confirm.setContentText("This cannot be undone.");
                Optional<ButtonType> res = confirm.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK && dao.delete(m.memberId())) {
                    fullRefresh.run();
                }
            });
            actions.getChildren().addAll(viewBtn, delBtn);
            rowGrid.add(actions, 7, 0);

            dataRow.getChildren().add(rowGrid);
            String finalBg = bg;
            dataRow.setOnMouseEntered(ev -> dataRow.setStyle("-fx-background-color: rgba(230,57,70,0.06);"));
            dataRow.setOnMouseExited(ev -> dataRow.setStyle("-fx-background-color: " + finalBg + ";"));

            rows.getChildren().add(dataRow);
            r++;
        }
    }

    // ── Helper builders ────────────────────────────────────────────
    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        l.setTextFill(Color.web(color));
        l.setPadding(new Insets(0, 8, 0, 8));
        return l;
    }

    private Label makeStatusBadge(String status) {
        Label b = new Label(status);
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        String c, bg;
        switch (status.toLowerCase()) {
            case "active": c = SUCCESS; bg = "rgba(76,175,80,0.15)"; break;
            case "expired": c = ACCENT; bg = "rgba(230,57,70,0.15)"; break;
            default: c = TEXT_MUTED; bg = "transparent"; break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        b.setPadding(new Insets(0, 8, 0, 8));
        return b;
    }

    private Label makePlanBadge(String plan) {
        Label b = new Label(plan);
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        String c, bg;
        switch (plan.toLowerCase()) {
            case "monthly": c = "#2196f3"; bg = "rgba(33,150,243,0.15)"; break;
            case "daily":   c = WARNING;   bg = "rgba(255,152,0,0.15)";  break;
            default:        c = TEXT_MUTED; bg = "rgba(176,190,197,0.1)"; break;
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        b.setPadding(new Insets(0, 8, 0, 8));
        return b;
    }

    private Button makeActionBtn(String icon, String color) {
        Button btn = new Button(icon);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-font-size: 13;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 6 3 6;" +
            "-fx-background-radius: 6;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-text-fill: " + color + ";" +
            "-fx-font-size: 13;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 6 3 6;" +
            "-fx-background-radius: 6;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-font-size: 13;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 6 3 6;" +
            "-fx-background-radius: 6;"
        ));
        return btn;
    }

    private HBox makeStatChip(String label, String value, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.2));
        ds.setRadius(8); ds.setOffsetY(3);
        chip.setEffect(ds);
        Text val = new Text(value);
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        val.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Verdana", 11));
        lbl.setFill(Color.web(TEXT_MUTED));
        chip.getChildren().addAll(new VBox(2, lbl, val));
        return chip;
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
            "-fx-pref-height: 38;"
        );
    }

    private Button makePagBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setFont(Font.font("Verdana", active ? FontWeight.BOLD : FontWeight.NORMAL, 11));
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

    // ── Add Member Dialog ──────────────────────────────────────────
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
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
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
        TextField dobField = new TextField();
        ComboBox<String> plan = new ComboBox<>();
        plan.getItems().addAll("Daily", "Monthly", "Quarterly", "Yearly", "Per Session");
        plan.setValue("Monthly");
        styleCombo(plan);
        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("M", "F", "Other");
        gender.setValue("M");
        styleCombo(gender);

        int r = 0;
        form.add(labeledField("FIRST NAME", firstName, "Enter first name"), 0, r);
        form.add(labeledField("LAST NAME", lastName, "Enter last name"), 1, r++);
        form.add(labeledField("PHONE", phone, "09XXXXXXXXX"), 0, r);
        form.add(labeledField("EMAIL", email, "email@example.com"), 1, r++);
        VBox addrBox = labeledField("ADDRESS", address, "Full address");
        form.add(addrBox, 0, r++, 2, 1);
        GridPane.setColumnSpan(addrBox, 2);
        form.add(labeledField("DATE OF BIRTH (optional)", dobField, "YYYY-MM-DD"), 0, r);
        VBox planBox = new VBox(6);
        Label pl = new Label("PLAN");
        pl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        pl.setTextFill(Color.web(TEXT_MUTED));
        plan.setPrefHeight(40);
        planBox.getChildren().addAll(pl, plan);
        form.add(planBox, 1, r++);
        VBox gBox = new VBox(6);
        Label gl = new Label("GENDER");
        gl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        gl.setTextFill(Color.web(TEXT_MUTED));
        gender.setPrefHeight(40);
        gBox.getChildren().addAll(gl, gender);
        form.add(gBox, 0, r++, 2, 1);
        GridPane.setColumnSpan(gBox, 2);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setPrefHeight(40);
        cancel.setPadding(new Insets(0, 20, 0, 20));
        cancel.setFont(Font.font("Verdana", 12));
        cancel.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        cancel.setOnAction(e -> dialog.close());

        MemberDAO dao = new MemberDAO();
        Button save = new Button("Save Member");
        save.setPrefHeight(40);
        save.setPadding(new Insets(0, 20, 0, 20));
        save.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        save.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        save.setOnAction(e -> {
            String fn = firstName.getText().trim();
            String ln = lastName.getText().trim();
            if (fn.isEmpty() || ln.isEmpty()) {
                alertErr("Please enter first and last name.");
                return;
            }
            String ph = phone.getText().trim();
            String em = email.getText().trim();
            String ad = address.getText().trim();
            if (ph.isEmpty() || em.isEmpty() || ad.isEmpty()) {
                alertErr("Phone, email, and address are required.");
                return;
            }
            Date dobSql = null;
            String dobStr = dobField.getText().trim();
            if (!dobStr.isEmpty()) {
                try {
                    dobSql = Date.valueOf(LocalDate.parse(dobStr));
                } catch (Exception ex) {
                    alertErr("Invalid date of birth. Use YYYY-MM-DD.");
                    return;
                }
            }
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
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        field.setPromptText(prompt);
        field.setPrefHeight(40);
        applyFieldStyle(field);
        fg.getChildren().addAll(lbl, field);
        return fg;
    }

    private LocalDate membershipEndForPlan(LocalDate start, String planType) {
        if (planType == null) {
            return start.plusMonths(1);
        }
        return switch (planType) {
            case "Daily" -> start.plusDays(1);
            case "Monthly" -> start.plusMonths(1);
            case "Quarterly" -> start.plusMonths(3);
            case "Yearly" -> start.plusYears(1);
            case "Per Session" -> start.plusMonths(1);
            default -> start.plusMonths(1);
        };
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
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;"
        );
        f.focusedProperty().addListener((o, old, focused) -> f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;"
        ));
    }

    public static void main(String[] args) { launch(args); }
}
