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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mj23gym.dao.InventoryDAO;
import mj23gym.dao.PaymentDAO;
import mj23gym.dao.PosDAO;
import mj23gym.dao.ReportDAO;

/**
 * MJ23 Playgrind Gym  Reports Screen
 * Covers: Generate Report + Sales Report views
 */
public class ReportsScreen extends Application {

    static final String BG_MAIN     = "#F2F4F8";
    static final String BG_SIDEBAR  = "#E9EDF6";
    static final String BG_CARD     = "#F8F9FC";
    static final String BG_ROW_ALT  = "#EEF2FA";
    static final String ACCENT      = "#1A1363";
    static final String ACCENT_DARK = "#332F4F";
    static final String TEXT_WHITE  = "#1A1363";
    static final String TEXT_MUTED  = "#77749B";
    static final String TEXT_DIM    = "#4B4B4B";
    static final String BORDER      = "#D9DDEA";
    static final String SUCCESS     = "#2F6F5E";
    static final String WARNING     = "#8A6D00";
    static final String INFO        = "#1A1363";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Reports");
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
        Rectangle topAccent = new Rectangle(230, 5); topAccent.setFill(Color.web("#FDEE21"));
        HBox logoArea = new HBox(12); logoArea.setAlignment(Pos.CENTER_LEFT); logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane badge = new StackPane(); badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42); bb.setArcWidth(10); bb.setArcHeight(10); bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ"); bt.setFont(Font.font("Poppins", FontWeight.BOLD, 16)); bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND"); l1.setFont(Font.font("Poppins", FontWeight.BOLD, 11)); l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM"); l2.setFont(Font.font("Poppins", FontWeight.BOLD, 11)); l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2); logoArea.getChildren().addAll(badge, lt);
        String[][] items = {
            {"","Dashboard"},{"","Member Management"},{"","Payment & Billing"},
            {"","Inventory"},{"","Equipment"},{"","Point of Sale"},{"","Reports"}
        };
        VBox menu = new VBox(2); menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items) menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Reports")));
        String[][] sys = {{"","Settings"},{"","Help"},{"","About"}};
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
        Text lbl = new Text(label); lbl.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 12)); lbl.setFill(active ? Color.web(TEXT_WHITE) : Color.web(TEXT_MUTED));
        item.getChildren().addAll(bar, ico, lbl);
        item.setStyle(active ? "-fx-background-color: " + BG_CARD + "; -fx-background-radius: 16;" : "-fx-background-color: transparent; -fx-background-radius: 16;");
        if (!active) {
            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 16;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent; -fx-background-radius: 16;"));
        }
        return item;
    }

    private Label makeSecLbl(String t) {
        Label l = new Label(t); l.setFont(Font.font("Poppins", FontWeight.BOLD, 9)); l.setTextFill(Color.web(TEXT_DIM)); l.setPadding(new Insets(8, 0, 6, 20)); return l;
    }

    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text t1 = new Text("Reports & Analytics"); t1.setFont(Font.font("Poppins", FontWeight.BOLD, 20)); t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Generate payment, sales, and inventory summary reports"); t2.setFont(Font.font("Poppins", 11)); t2.setFill(Color.web(TEXT_MUTED));
        pg.getChildren().addAll(t1, t2);
        Region tSp = new Region(); HBox.setHgrow(tSp, Priority.ALWAYS);
        Button exportBtn = makeAccentBtn("Save Report");
        topBar.getChildren().addAll(pg, tSp, exportBtn);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(22);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        final String[] selectedReport = {"Sales Report"};
        final Runnable[] refreshDisplay = new Runnable[1];

        ComboBox<String> groupBy = new ComboBox<>();
        groupBy.getItems().addAll("Sales Report", "Payment Report", "Inventory Report");
        groupBy.setValue(selectedReport[0]);
        styleCombo(groupBy);

        //  Report type selector 
        HBox typeRow = new HBox(14);
        typeRow.setAlignment(Pos.CENTER_LEFT);
        String[][] reportTypes = {
            {"POS", "Sales Report",     "Revenue from product sales"},
            {"PAY", "Payment Report",   "Membership dues and payments"},
            {"INV", "Inventory Report", "Stock levels and item status"},
        };
        List<VBox> reportCards = new ArrayList<>();
        for (String[] rt : reportTypes) {
            VBox rtCard = buildReportTypeCard(rt[0], rt[1], rt[2], rt[1].equals("Sales Report"));
            String reportName = rt[1];
            rtCard.setOnMouseClicked(e -> {
                selectedReport[0] = reportName;
                groupBy.setValue(reportName);
                for (VBox card : reportCards) {
                    applyReportTypeCardStyle(card, reportName.equals((String) card.getUserData()));
                }
                if (refreshDisplay[0] != null) {
                    refreshDisplay[0].run();
                }
            });
            rtCard.setUserData(reportName);
            reportCards.add(rtCard);
            HBox.setHgrow(rtCard, Priority.ALWAYS);
            typeRow.getChildren().add(rtCard);
        }
        groupBy.setOnAction(e -> {
            if (groupBy.getValue() == null) {
                return;
            }
            selectedReport[0] = groupBy.getValue();
            for (VBox card : reportCards) {
                applyReportTypeCardStyle(card, selectedReport[0].equals((String) card.getUserData()));
            }
            if (refreshDisplay[0] != null) {
                refreshDisplay[0].run();
            }
        });

        //  Filters row 
        VBox filterCard = new VBox(16);
        filterCard.setPadding(new Insets(22));
        filterCard.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 22; -fx-border-color: " + BORDER + "; -fx-border-radius: 22; -fx-border-width: 1;");

        Text filterTitle = new Text("Report Filters");
        filterTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        filterTitle.setFill(Color.web(TEXT_WHITE));

        HBox filterRow = new HBox(16);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        Text salesHdr = new Text("POS Sales");
        salesHdr.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        salesHdr.setFill(Color.web(TEXT_WHITE));

        Text payHdr = new Text("Membership Payments");
        payHdr.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        payHdr.setFill(Color.web(TEXT_WHITE));

        TextField tfFrom = new TextField();
        TextField tfTo = new TextField();
        LocalDate today = LocalDate.now();
        tfFrom.setText(today.withDayOfMonth(1).toString());
        tfTo.setText(today.toString());
        VBox dateFrom = wrapLabeledField("DATE FROM", tfFrom);
        VBox dateTo = wrapLabeledField("DATE TO", tfTo);

        PaymentDAO paymentDAO = new PaymentDAO();
        PosDAO posDAO = new PosDAO();
        ReportDAO reportDAO = new ReportDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();

        Text revVal = new Text("0");
        Text trxVal = new Text("0");
        Text topProd = new Text("");
        Text topSub = new Text("No POS lines in range");
        Text trxSub = new Text("Rows in selected report");
        Text avgSub = new Text("Selected total divided by range days");
        Text topProdSub = new Text(" ");
        Text avgVal = new Text("0");
        styleSummaryValue(revVal);
        styleSummaryValue(trxVal);
        styleSummaryValue(topProd);
        topSub.setFont(Font.font("Poppins", 10));
        topSub.setFill(Color.web(TEXT_DIM));
        trxSub.setFont(Font.font("Poppins", 10));
        trxSub.setFill(Color.web(TEXT_DIM));
        avgSub.setFont(Font.font("Poppins", 10));
        avgSub.setFill(Color.web(TEXT_DIM));
        topProdSub.setFont(Font.font("Poppins", 10));
        topProdSub.setFill(Color.web(TEXT_DIM));
        styleSummaryValue(avgVal);

        VBox salesRows = new VBox(0);
        Label salesTotQty = new Label("0");
        Label salesTotAmt = new Label("0");
        HBox salesTotalRow = buildSalesTotalRow(salesTotQty, salesTotAmt);

        VBox payRows = new VBox(0);
        VBox inventoryRows = new VBox(0);

        Runnable refreshAll = () -> applyReportRange(
            selectedReport[0],
            tfFrom,
            tfTo,
            revVal,
            trxVal,
            topProd,
            topSub,
            topProdSub,
            avgVal,
            salesHdr,
            payHdr,
            salesRows,
            salesTotalRow,
            salesTotQty,
            salesTotAmt,
            payRows,
            inventoryRows,
            paymentDAO,
            posDAO,
            inventoryDAO
        );
        VBox groupBox = new VBox(6);
        Label gLbl = new Label("REPORT TYPE");
        gLbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        gLbl.setTextFill(Color.web(TEXT_MUTED));
        groupBox.getChildren().addAll(gLbl, groupBy);

        VBox savedReportRows = new VBox(0);

        Region fSp = new Region();
        HBox.setHgrow(fSp, Priority.ALWAYS);
        Button genBtn = makeAccentBtn("  Generate Report");
        genBtn.setPrefHeight(42);
        genBtn.setOnAction(e -> generateAndSaveReport(tfFrom, tfTo, groupBy, refreshAll, reportDAO, savedReportRows));
        exportBtn.setOnAction(e -> generateAndSaveReport(tfFrom, tfTo, groupBy, refreshAll, reportDAO, savedReportRows));

        filterRow.getChildren().addAll(dateFrom, dateTo, groupBox, fSp, genBtn);
        filterCard.getChildren().addAll(filterTitle, filterRow);

        Text revSub = new Text("Selected report total");
        revSub.setFont(Font.font("Poppins", 10));
        revSub.setFill(Color.web(TEXT_DIM));

        HBox summaryStats = new HBox(16);
        summaryStats.getChildren().addAll(
            makeStatCard("Total Amount / Stock Value", revVal, revSub, SUCCESS),
            makeStatCard("Record Count", trxVal, trxSub, INFO),
            makeStatCard("Top Item / Alerts", topProd, topProdSub, WARNING),
            makeStatCard("Average Per Day", avgVal, avgSub, ACCENT)
        );

        VBox salesTable = buildSalesTableShell(salesHdr, salesRows, salesTotalRow);
        VBox paymentSummary = buildPaymentSummaryShell(payHdr, payRows);
        VBox inventorySummary = buildInventorySummaryShell(inventoryRows);
        VBox savedReports = buildSavedReportsShell(savedReportRows, reportDAO);
        refreshDisplay[0] = () -> {
            refreshAll.run();
            salesTable.setVisible("Sales Report".equals(selectedReport[0]));
            salesTable.setManaged("Sales Report".equals(selectedReport[0]));
            paymentSummary.setVisible("Payment Report".equals(selectedReport[0]));
            paymentSummary.setManaged("Payment Report".equals(selectedReport[0]));
            inventorySummary.setVisible("Inventory Report".equals(selectedReport[0]));
            inventorySummary.setManaged("Inventory Report".equals(selectedReport[0]));
        };

        body.getChildren().addAll(typeRow, filterCard, summaryStats, salesTable, paymentSummary, inventorySummary, savedReports);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        refreshDisplay[0].run();
        return content;
    }

    private void generateAndSaveReport(
        TextField tfFrom,
        TextField tfTo,
        ComboBox<String> reportType,
        Runnable refreshAll,
        ReportDAO reportDAO,
        VBox savedReportRows
    ) {
        LocalDate fromLd;
        LocalDate toLd;
        try {
            fromLd = LocalDate.parse(tfFrom.getText().trim());
            toLd = LocalDate.parse(tfTo.getText().trim());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.WARNING, "Use YYYY-MM-DD for both dates.").showAndWait();
            return;
        }
        if (toLd.isBefore(fromLd)) {
            new Alert(Alert.AlertType.WARNING, "End date must be on or after start date.").showAndWait();
            return;
        }

        refreshAll.run();

        Date from = Date.valueOf(fromLd);
        Date to = Date.valueOf(toLd);
        String selectedName = reportType.getValue() != null ? reportType.getValue() : "Sales Report";
        String type = reportDAO.normalizeReportType(selectedName);
        ReportDAO.ReportMetrics metrics = reportDAO.buildMetrics(from, to);
        String notes = reportDAO.buildNotes(selectedName, from, to, metrics);
        String name = selectedName + " - " + fromLd + " to " + toLd;
        int reportId = reportDAO.saveReport(name, type, from, to, notes, AppSession.currentUser().userId());
        if (reportId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Report was generated on screen but could not be saved. Check database connection.").showAndWait();
            return;
        }

        populateSavedReportRows(savedReportRows, reportDAO);
        showReportDialog(reportDAO.findReport(reportId).orElse(
            new ReportDAO.ReportRecord(reportId, name, type, from, to, notes, AppSession.currentUser().displayName(), null)
        ));
    }

    private void styleSummaryValue(Text t) {
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 24));
        t.setFill(Color.web(TEXT_WHITE));
    }

    private VBox wrapLabeledField(String label, TextField field) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        field.setPrefHeight(40);
        applyFieldStyle(field);
        g.getChildren().addAll(lbl, field);
        return g;
    }

    private void applyReportRange(
        String reportMode,
        TextField tfFrom,
        TextField tfTo,
        Text revVal,
        Text trxVal,
        Text topProd,
        Text topSub,
        Text topProdSub,
        Text avgVal,
        Text salesHdr,
        Text payHdr,
        VBox salesRows,
        HBox salesTotalRow,
        Label salesTotQty,
        Label salesTotAmt,
        VBox payRows,
        VBox inventoryRows,
        PaymentDAO paymentDAO,
        PosDAO posDAO,
        InventoryDAO inventoryDAO
    ) {
        LocalDate fromLd;
        LocalDate toLd;
        try {
            fromLd = LocalDate.parse(tfFrom.getText().trim());
            toLd = LocalDate.parse(tfTo.getText().trim());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.WARNING, "Use YYYY-MM-DD for both dates.").showAndWait();
            return;
        }
        if (toLd.isBefore(fromLd)) {
            new Alert(Alert.AlertType.WARNING, "End date must be on or after start date.").showAndWait();
            return;
        }

        Date from = Date.valueOf(fromLd);
        Date to = Date.valueOf(toLd);

        double payRev = paymentDAO.sumCompletedBetween(from, to);
        double posRev = posDAO.sumPosRevenueBetween(from, to);
        double totalRev = switch (reportMode) {
            case "Payment Report" -> payRev;
            case "Inventory Report" -> inventoryValue(inventoryDAO.findAll());
            default -> posRev;
        };

        int payCount = paymentDAO.countBetween(from, to);
        int posCount = posDAO.countPosTransactionsBetween(from, to);
        List<InventoryDAO.InventoryRecord> inventory = inventoryDAO.findAll();
        int trx = switch (reportMode) {
            case "Payment Report" -> payCount;
            case "Inventory Report" -> inventory.size();
            default -> posCount;
        };

        long days = ChronoUnit.DAYS.between(fromLd, toLd) + 1;
        double avg = days > 0 ? totalRev / days : 0;

        List<PosDAO.SaleDetailRow> lines = posDAO.findSaleLinesBetween(from, to);
        Map<String, Integer> qtyByItem = new HashMap<>();
        int totalQty = 0;
        double lineSum = 0;
        for (PosDAO.SaleDetailRow row : lines) {
            qtyByItem.merge(row.itemName(), row.quantity(), Integer::sum);
            totalQty += row.quantity();
            lineSum += row.subtotal();
        }
        String topName = "";
        int topQty = 0;
        for (Map.Entry<String, Integer> e : qtyByItem.entrySet()) {
            if (e.getValue() > topQty) {
                topQty = e.getValue();
                topName = e.getKey();
            }
        }

        revVal.setText(String.format("%.0f", totalRev));
        trxVal.setText(String.valueOf(trx));
        if ("Payment Report".equals(reportMode)) {
            topProd.setText(String.valueOf(payCount));
            topSub.setText("Membership payment records in range");
            topProdSub.setText("Payment transactions");
        } else if ("Inventory Report".equals(reportMode)) {
            int lowStock = inventoryDAO.countLowStock();
            topProd.setText(String.valueOf(lowStock));
            topSub.setText("Items at or below reorder level");
            topProdSub.setText("Low-stock items");
        } else {
            topProd.setText(topName);
            topSub.setText(topQty > 0 ? topQty + " units (POS) in range" : "No POS line items in range");
            topProdSub.setText(topQty > 0 ? "Best seller in POS lines" : " ");
        }
        avgVal.setText(String.format("%.0f", avg));

        salesHdr.setText("POS Sales - " + fromLd + " to " + toLd);
        payHdr.setText("Membership Payments - " + fromLd + " to " + toLd);

        populateSalesRows(salesRows, lines, salesTotalRow, salesTotQty, salesTotAmt);
        populatePaymentRows(payRows, paymentDAO.findPaymentSummaryBetween(from, to));
        populateInventoryRows(inventoryRows, inventory);
    }

    private HBox buildSalesTotalRow(Label qtyLbl, Label amtLbl) {
        double[] colW = {14, 24, 10, 12, 12, 18};
        HBox totalRow = new HBox();
        totalRow.setPadding(new Insets(12, 20, 12, 20));
        totalRow.setStyle(
            "-fx-background-color: rgba(26,19,99,0.08);" +
            "-fx-border-color: " + BORDER + " transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;");
        GridPane tg = makeGrid(colW);
        tg.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tg, Priority.ALWAYS);
        qtyLbl.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        qtyLbl.setTextFill(Color.web(TEXT_WHITE));
        amtLbl.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        amtLbl.setTextFill(Color.web(ACCENT));
        tg.add(makeCell("TOTAL", TEXT_MUTED, true), 0, 0);
        tg.add(makeCell("", TEXT_WHITE, false), 1, 0);
        tg.add(qtyLbl, 2, 0);
        tg.add(makeCell("", TEXT_WHITE, false), 3, 0);
        tg.add(amtLbl, 4, 0);
        tg.add(makeCell("", TEXT_WHITE, false), 5, 0);
        totalRow.getChildren().add(tg);
        return totalRow;
    }

    private void populateSalesRows(
        VBox rowsBox,
        List<PosDAO.SaleDetailRow> lines,
        HBox salesTotalRow,
        Label totQty,
        Label totAmt
    ) {
        rowsBox.getChildren().clear();
        double[] colW = {14, 24, 10, 12, 12, 18};
        int qtySum = 0;
        double amtSum = 0;
        int r = 0;
        for (PosDAO.SaleDetailRow row : lines) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox rowBox = new HBox();
            rowBox.setPadding(new Insets(10, 20, 10, 20));
            rowBox.setStyle("-fx-background-color: " + bg + ";");
            GridPane rg = makeGrid(colW);
            rg.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rg, Priority.ALWAYS);
            String day = row.saleDate() != null ? row.saleDate().toString() : "";
            rg.add(makeCell(day, TEXT_MUTED, false), 0, 0);
            rg.add(makeCell(row.itemName(), TEXT_WHITE, false), 1, 0);
            rg.add(makeCell(String.valueOf(row.quantity()), TEXT_WHITE, true), 2, 0);
            rg.add(makeCell(String.format("%.2f", row.unitPrice()), TEXT_MUTED, false), 3, 0);
            rg.add(makeCell(String.format("%.2f", row.subtotal()), SUCCESS, true), 4, 0);
            String pm = row.paymentMethod() != null ? row.paymentMethod() : "Cash";
            rg.add(makeMethodBadge(pm), 5, 0);
            rowBox.getChildren().add(rg);
            String fBg = bg;
            rowBox.setOnMouseEntered(e -> rowBox.setStyle("-fx-background-color: rgba(26,19,99,0.05);"));
            rowBox.setOnMouseExited(e -> rowBox.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(rowBox);
            qtySum += row.quantity();
            amtSum += row.subtotal();
            r++;
        }
        totQty.setText(String.valueOf(qtySum));
        totAmt.setText(String.format("%.2f", amtSum));
        if (!rowsBox.getChildren().contains(salesTotalRow)) {
            rowsBox.getChildren().add(salesTotalRow);
        }
    }

    private void populatePaymentRows(VBox rowsBox, List<PaymentDAO.PaymentSummaryRow> rows) {
        rowsBox.getChildren().clear();
        double[] colW = {22, 13, 14, 16, 15, 12};
        int r = 0;
        for (PaymentDAO.PaymentSummaryRow pr : rows) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setPadding(new Insets(10, 20, 10, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            GridPane rg = makeGrid(colW);
            rg.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rg, Priority.ALWAYS);
            String plan = pr.membershipType() != null ? pr.membershipType() : "";
            String method = pr.paymentMethod() != null ? pr.paymentMethod() : "Cash";
            String dateStr = pr.paymentDate() != null ? pr.paymentDate().toString() : "";
            String statusLbl = displayPaymentStatus(pr.status());
            rg.add(makeCell(pr.memberName(), TEXT_WHITE, false), 0, 0);
            rg.add(makeCell(plan, TEXT_MUTED, false), 1, 0);
            rg.add(makeCell(String.format("%.2f", pr.amount()), SUCCESS, true), 2, 0);
            rg.add(makeMethodBadge(method), 3, 0);
            rg.add(makeCell(dateStr, TEXT_MUTED, false), 4, 0);
            rg.add(makeStatusBadge(statusLbl), 5, 0);
            row.getChildren().add(rg);
            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(26,19,99,0.05);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(row);
            r++;
        }
        if (rows.isEmpty()) {
            Label empty = new Label("No payments in this date range.");
            empty.setTextFill(Color.web(TEXT_MUTED));
            empty.setPadding(new Insets(16, 20, 16, 20));
            rowsBox.getChildren().add(empty);
        }
    }

    private String displayPaymentStatus(String raw) {
        if (raw == null) {
            return "";
        }
        if ("Completed".equalsIgnoreCase(raw)) {
            return "Paid";
        }
        return raw;
    }

    private double inventoryValue(List<InventoryDAO.InventoryRecord> rows) {
        double total = 0;
        for (InventoryDAO.InventoryRecord row : rows) {
            total += row.currentStock() * row.sellingPrice();
        }
        return total;
    }

    private void populateInventoryRows(VBox rowsBox, List<InventoryDAO.InventoryRecord> rows) {
        rowsBox.getChildren().clear();
        double[] colW = {13, 24, 14, 10, 12, 12, 15};
        int r = 0;
        for (InventoryDAO.InventoryRecord item : rows) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setPadding(new Insets(10, 20, 10, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            GridPane grid = makeGrid(colW);
            grid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(grid, Priority.ALWAYS);

            String expiry = item.expirationDate() != null ? item.expirationDate().toString() : "No expiry";
            double value = item.currentStock() * item.sellingPrice();
            grid.add(makeCell(item.itemCode(), TEXT_MUTED, false), 0, 0);
            grid.add(makeCell(item.itemName(), TEXT_WHITE, false), 1, 0);
            grid.add(makeCell(item.category(), TEXT_MUTED, false), 2, 0);
            grid.add(makeCell(String.valueOf(item.currentStock()), TEXT_WHITE, true), 3, 0);
            grid.add(makeCell(String.format("%.2f", item.sellingPrice()), TEXT_MUTED, false), 4, 0);
            grid.add(makeCell(String.format("%.2f", value), SUCCESS, true), 5, 0);
            grid.add(makeStatusBadge(item.status()), 6, 0);

            row.getChildren().add(grid);
            Tooltip.install(row, new Tooltip("Expiration: " + expiry + " | Supplier: " + safeText(item.supplier())));
            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(26,19,99,0.05);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(row);
            r++;
        }
        if (rows.isEmpty()) {
            Label empty = new Label("No active inventory items found.");
            empty.setTextFill(Color.web(TEXT_MUTED));
            empty.setPadding(new Insets(16, 20, 16, 20));
            rowsBox.getChildren().add(empty);
        }
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private VBox buildReportTypeCard(String icon, String title, String sub, boolean active) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setUserData(title);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);
        Text ico = new Text(icon);
        ico.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        Text t = new Text(title);
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        Text s = new Text(sub);
        s.setFont(Font.font("Poppins", 10));
        card.getChildren().addAll(ico, t, s);
        applyReportTypeCardStyle(card, active);
        return card;
    }

    private void applyReportTypeCardStyle(VBox card, boolean active) {
        String borderColor = active ? ACCENT : BORDER;
        String bgColor = active ? "rgba(26,19,99,0.08)" : BG_CARD;
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: " + (active ? "0 0 0 4" : "1") + ";");
        for (javafx.scene.Node node : card.getChildren()) {
            if (node instanceof Text text) {
                text.setFill(Color.web(node == card.getChildren().get(0)
                    ? (active ? ACCENT : TEXT_MUTED)
                    : (active ? TEXT_WHITE : TEXT_MUTED)));
            }
        }
    }

    private VBox buildSalesTableShell(Text titleNode, VBox rowsBox, HBox totalRow) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);

        HBox hdr = new HBox();
        hdr.setPadding(new Insets(16, 20, 14, 20));
        hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        titleNode.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        titleNode.setFill(Color.web(TEXT_WHITE));
        Region hSp = new Region();
        HBox.setHgrow(hSp, Priority.ALWAYS);
        Button printBtn = new Button("Print");
        printBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-font-size: 11;" +
            "-fx-cursor: hand;"
        );
        printBtn.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION,
            "Generate and save the report first, then open it from Generated Reports to review printable details.")
            .showAndWait());
        hdr.getChildren().addAll(titleNode, hSp, printBtn);

        String[] headers = {"Date", "Item", "Qty Sold", "Unit Price", "Total", "Payment Method"};
        double[] colW = {14, 24, 10, 12, 12, 18};

        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(10, 20, 10, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
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

        card.getChildren().addAll(hdr, tblHdr, rowsBox);
        return card;
    }

    private VBox buildPaymentSummaryShell(Text titleNode, VBox rowsBox) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);

        HBox hdr = new HBox();
        hdr.setPadding(new Insets(16, 20, 14, 20));
        hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        titleNode.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        titleNode.setFill(Color.web(TEXT_WHITE));
        hdr.getChildren().add(titleNode);

        String[] headers = {"Member", "Plan", "Amount Paid", "Method", "Date", "Status"};
        double[] colW = {22, 13, 14, 16, 15, 12};

        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(10, 20, 10, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
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

        card.getChildren().addAll(hdr, tblHdr, rowsBox);
        return card;
    }

    private VBox buildInventorySummaryShell(VBox rowsBox) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);

        HBox hdr = new HBox();
        hdr.setPadding(new Insets(16, 20, 14, 20));
        hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        Text title = new Text("Inventory Stock Summary");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        title.setFill(Color.web(TEXT_WHITE));
        hdr.getChildren().add(title);

        String[] headers = {"Code", "Item", "Category", "Stock", "Unit Price", "Stock Value", "Status"};
        double[] colW = {13, 24, 14, 10, 12, 12, 15};
        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(10, 20, 10, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
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

        card.getChildren().addAll(hdr, tblHdr, rowsBox);
        return card;
    }

    private VBox buildSavedReportsShell(VBox rowsBox, ReportDAO reportDAO) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);

        HBox hdr = new HBox();
        hdr.setPadding(new Insets(16, 20, 14, 20));
        hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        Text title = new Text("Generated Reports");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        title.setFill(Color.web(TEXT_WHITE));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-font-size: 11;" +
            "-fx-cursor: hand;");
        refreshBtn.setOnAction(e -> populateSavedReportRows(rowsBox, reportDAO));
        hdr.getChildren().addAll(title, sp, refreshBtn);

        String[] headers = {"Report", "Type", "Period", "Generated By", "Generated At", "Action"};
        double[] colW = {25, 12, 20, 15, 18, 10};
        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(10, 20, 10, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
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

        populateSavedReportRows(rowsBox, reportDAO);
        card.getChildren().addAll(hdr, tblHdr, rowsBox);
        return card;
    }

    private void populateSavedReportRows(VBox rowsBox, ReportDAO reportDAO) {
        rowsBox.getChildren().clear();
        List<ReportDAO.ReportRecord> reports = reportDAO.findRecentReports(20);
        if (reports.isEmpty()) {
            Label empty = new Label("No generated reports saved yet.");
            empty.setTextFill(Color.web(TEXT_MUTED));
            empty.setPadding(new Insets(16, 20, 16, 20));
            rowsBox.getChildren().add(empty);
            return;
        }

        double[] colW = {25, 12, 20, 15, 18, 10};
        int rowIndex = 0;
        for (ReportDAO.ReportRecord report : reports) {
            String bg = (rowIndex % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox row = new HBox();
            row.setPadding(new Insets(10, 20, 10, 20));
            row.setStyle("-fx-background-color: " + bg + ";");
            GridPane grid = makeGrid(colW);
            grid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(grid, Priority.ALWAYS);

            String period = report.periodStart() + " to " + report.periodEnd();
            String generatedAt = report.generatedAt() != null ? report.generatedAt().toLocalDateTime().toString().replace('T', ' ') : "";
            Button viewBtn = new Button("View");
            viewBtn.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
            viewBtn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;");
            viewBtn.setOnAction(e -> showReportDialog(report));

            grid.add(makeCell(report.reportName(), TEXT_WHITE, false), 0, 0);
            grid.add(makeStatusBadge(report.reportType()), 1, 0);
            grid.add(makeCell(period, TEXT_MUTED, false), 2, 0);
            grid.add(makeCell(report.generatedBy(), TEXT_MUTED, false), 3, 0);
            grid.add(makeCell(generatedAt, TEXT_MUTED, false), 4, 0);
            grid.add(viewBtn, 5, 0);

            row.getChildren().add(grid);
            String fBg = bg;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(26,19,99,0.05);"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(row);
            rowIndex++;
        }
    }

    private void showReportDialog(ReportDAO.ReportRecord report) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Generated Report");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setPrefWidth(640);
        box.setStyle("-fx-background-color: " + BG_MAIN + ";");

        Text title = new Text(report.reportName());
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        Label meta = new Label(
            "Type: " + report.reportType() +
            " | Period: " + report.periodStart() + " to " + report.periodEnd() +
            " | Generated by: " + report.generatedBy()
        );
        meta.setTextFill(Color.web(TEXT_MUTED));
        meta.setWrapText(true);

        TextArea body = new TextArea(report.notes() != null ? report.notes() : "");
        body.setEditable(false);
        body.setWrapText(true);
        body.setPrefRowCount(18);
        body.setStyle(
            "-fx-control-inner-background: " + BG_CARD + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-font-family: Consolas;" +
            "-fx-font-size: 12;");

        box.getChildren().addAll(title, meta, body);
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setStyle("-fx-background-color: " + BG_MAIN + ";");
        dialog.showAndWait();
    }

    private VBox makeStatCard(String label, Text value, Text sub, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;");
        HBox.setHgrow(card, Priority.ALWAYS);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", 11));
        lbl.setFill(Color.web(TEXT_MUTED));
        value.setFont(Font.font("Poppins", FontWeight.BOLD, 24));
        value.setFill(Color.web(color));
        sub.setFont(Font.font("Poppins", 10));
        sub.setFill(Color.web(TEXT_DIM));
        card.getChildren().addAll(lbl, value, sub);
        return card;
    }

    private GridPane makeGrid(double[] widths) {
        GridPane g = new GridPane();
        for (double w : widths) { ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(w); cc.setHgrow(Priority.ALWAYS); g.getColumnConstraints().add(cc); }
        return g;
    }

    private void applyFieldStyle(TextField f) {
        f.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Poppins; -fx-font-size: 12;");
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-text-fill: " + TEXT_WHITE + "; -fx-font-family: Poppins; -fx-font-size: 12; -fx-pref-height: 40;");
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text); b.setPrefHeight(38); b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        return b;
    }

    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text); l.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11)); l.setTextFill(Color.web(color)); return l;
    }

    private Label makeMethodBadge(String method) {
        Label b = new Label(method); b.setFont(Font.font("Poppins", 10));
        String c; String bg;
        switch (method) {
            case "Cash": c = SUCCESS; bg = "rgba(228,255,223,0.12)"; break;
            case "GCash": c = INFO; bg = "rgba(119,116,155,0.12)"; break;
            default: c = WARNING; bg = "rgba(253,238,33,0.12)"; break;
        }
        b.setTextFill(Color.web(c)); b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 18; -fx-padding: 3 10 3 10;"); return b;
    }

    private Label makeStatusBadge(String status) {
        String label = status == null || status.isBlank() ? "-" : status;
        Label b = new Label(label); b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        String c = label.equals("Paid") || label.equals("In Stock") ? SUCCESS : ACCENT;
        String bg = label.equals("Paid") || label.equals("In Stock") ? "rgba(228,255,223,0.15)" : "rgba(26,19,99,0.15)";
        b.setTextFill(Color.web(c)); b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 18; -fx-padding: 3 10 3 10;"); return b;
    }

    public static void main(String[] args) { launch(args); }
}
