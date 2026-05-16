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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mj23gym.dao.MemberDAO;
import mj23gym.dao.PaymentDAO;

/**
 * MJ23 Playgrind Gym  Payment & Billing Screen
 * Shows member payment form with cash/GCash/bank transfer options,
 * automated dues/discount/penalty calculation, and payment history table.
 */
public class PaymentScreen extends Application {

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

    // Selected payment method
    private String selectedMethod = "Cash";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Payment & Billing");
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
        StackPane logoBadge = new StackPane();
        logoBadge.setPrefSize(42, 42);
        Rectangle lb = new Rectangle(42, 42);
        lb.setArcWidth(10); lb.setArcHeight(10);
        lb.setFill(Color.web(ACCENT));
        Text lt = new Text("MJ");
        lt.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        lt.setFill(Color.WHITE);
        logoBadge.getChildren().addAll(lb, lt);
        VBox logoTxt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND");
        l1.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM");
        l2.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        l2.setFill(Color.web(ACCENT));
        logoTxt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(logoBadge, logoTxt);
        String[][] items = {
            {"","Dashboard"},{"","Member Management"},{"","Payment & Billing"},
            {"","Inventory"},{"","Equipment"},{"","Point of Sale"},{"","Reports"}
        };
        VBox menu = new VBox(2);
        menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Payment & Billing")));
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
            "-fx-border-width: 0 0 1 0;"
        );
        VBox pgInfo = new VBox(2);
        Text t1 = new Text("Payment & Billing");
        t1.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Process member payments and manage billing records");
        t2.setFont(Font.font("Poppins", 11));
        t2.setFill(Color.web(TEXT_MUTED));
        pgInfo.getChildren().addAll(t1, t2);
        topBar.getChildren().add(pgInfo);

        // Scrollable body
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(22);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        MemberDAO memberDAO = new MemberDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        final MemberDAO.MemberRecord[] selectedMember = new MemberDAO.MemberRecord[1];
        final int[] selectedBillingId = {0};
        final int[] lastPaymentId = {0};

        //  Stats row 
        double today = paymentDAO.todayRevenue();
        int overdue = paymentDAO.countOverdueAccounts();
        int pending = paymentDAO.countPendingInvoices();
        int paidMonth = paymentDAO.countCompletedPaymentsThisMonth();
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            makeStatChip(" Total Collected Today", String.format("%.0f", today), SUCCESS),
            makeStatChip("  Overdue Accounts", String.valueOf(overdue), ACCENT),
            makeStatChip(" Pending Invoices", String.valueOf(pending), WARNING),
            makeStatChip(" Paid This Month", String.valueOf(paidMonth), INFO)
        );

        //  Two-column layout: Payment Form + Summary 
        HBox mainRow = new HBox(20);

        VBox histRows = new VBox(0);
        VBox histCard = buildPaymentHistory(paymentDAO, histRows);
        Runnable refreshHist = () -> populatePaymentHistoryRows(histRows, paymentDAO.findRecentPayments(50));
        refreshHist.run();
        HBox.setHgrow(histCard, Priority.ALWAYS);

        // LEFT: Payment form card
        VBox payCard = new VBox(18);
        payCard.setPadding(new Insets(26, 26, 26, 26));
        payCard.setPrefWidth(500);
        payCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.3));
        ds.setRadius(12); ds.setOffsetY(4);
        payCard.setEffect(ds);

        Text payTitle = new Text("Process Payment");
        payTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 17));
        payTitle.setFill(Color.web(TEXT_WHITE));
        Rectangle payLine = new Rectangle(48, 3);
        payLine.setFill(Color.web(ACCENT)); payLine.setArcWidth(3); payLine.setArcHeight(3);

        // Member lookup
        VBox memberLookup = new VBox(8);
        Label mlbl = makeFieldLabel("SEARCH MEMBER");
        HBox searchRow = new HBox(10);
        TextField memberSearch = new TextField();
        memberSearch.setPromptText("Enter Member ID or Name...");
        memberSearch.setPrefHeight(40);
        HBox.setHgrow(memberSearch, Priority.ALWAYS);
        applyFieldStyle(memberSearch);
        Button findBtn = new Button("Find");
        findBtn.setPrefHeight(40);
        findBtn.setPadding(new Insets(0, 16, 0, 16));
        findBtn.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        findBtn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;");
        searchRow.getChildren().addAll(memberSearch, findBtn);
        memberLookup.getChildren().addAll(mlbl, searchRow);

        // Member info display
        VBox memberInfo = new VBox(0);
        memberInfo.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;"
        );
        memberInfo.setPadding(new Insets(16));
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20); infoGrid.setVgap(10);
        String[] infoKeys = {"Member ID", "Name", "Plan", "Status", "Balance Due", "Due Date"};
        Label[] infoVals = new Label[infoKeys.length];
        for (int i = 0; i < infoKeys.length; i++) {
            Label key = new Label(infoKeys[i] + ":");
            key.setFont(Font.font("Poppins", 11));
            key.setTextFill(Color.web(TEXT_DIM));
            Label val = new Label("");
            val.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
            val.setTextFill(Color.web(TEXT_WHITE));
            infoVals[i] = val;
            infoGrid.add(key, 0, i);
            infoGrid.add(val, 1, i);
        }
        memberInfo.getChildren().add(infoGrid);

        Runnable updateMemberInfo = () -> {
            MemberDAO.MemberRecord m = selectedMember[0];
            if (m == null) {
                for (Label v : infoVals) {
                    v.setText("");
                    v.setTextFill(Color.web(TEXT_WHITE));
                }
                selectedBillingId[0] = 0;
                return;
            }
            infoVals[0].setText("#" + m.memberCode());
            infoVals[1].setText(m.fullName());
            infoVals[2].setText(m.membershipType() != null ? m.membershipType() : "");
            infoVals[3].setText(m.status() != null ? m.status() : "");
            infoVals[3].setTextFill(Color.web(
                m.status() != null && m.status().equalsIgnoreCase("Active") ? SUCCESS : ACCENT));
            infoVals[4].setText("0");
            infoVals[4].setTextFill(Color.web(ACCENT));
            infoVals[5].setText(
                m.membershipEndDate() != null ? m.membershipEndDate().toString() : "");
            infoVals[5].setTextFill(Color.web(TEXT_WHITE));
        };
        updateMemberInfo.run();

        // Payment method selector
        VBox methodGroup = new VBox(10);
        Label methodLbl = makeFieldLabel("PAYMENT METHOD");
        HBox methodBtns = new HBox(12);
        String[] methods = {"Cash", "GCash", "Bank Transfer"};
        String[] methodIcons = {"", "", ""};
        ToggleGroup methodGroup2 = new ToggleGroup();
        for (int i = 0; i < methods.length; i++) {
            ToggleButton tb = new ToggleButton(methodIcons[i] + "  " + methods[i]);
            tb.setToggleGroup(methodGroup2);
            tb.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
            tb.setPrefHeight(40);
            HBox.setHgrow(tb, Priority.ALWAYS);
            tb.setMaxWidth(Double.MAX_VALUE);
            if (methods[i].equals("Cash")) tb.setSelected(true);
            tb.setUserData(methods[i]);
            styleToggleBtn(tb, methods[i].equals("Cash"));
            tb.selectedProperty().addListener((obs, old, sel) -> styleToggleBtn(tb, sel));
        }
        methodBtns.getChildren().setAll(
            methodGroup2.getToggles().stream()
                .map(t -> (ToggleButton) t)
                .toArray(ToggleButton[]::new)
        );
        methodGroup.getChildren().addAll(methodLbl, methodBtns);

        // Reference number (for GCash / Bank)
        VBox refGroup = new VBox(6);
        Label refLbl = makeFieldLabel("REFERENCE NUMBER (GCash / Bank Transfer)");
        TextField refField = new TextField();
        refField.setPromptText("Enter reference number after confirming payment");
        refField.setPrefHeight(40);
        applyFieldStyle(refField);
        refGroup.getChildren().addAll(refLbl, refField);

        // Amount fields
        GridPane amtGrid = new GridPane();
        amtGrid.setHgap(16); amtGrid.setVgap(14);
        ColumnConstraints ca = new ColumnConstraints(); ca.setPercentWidth(50);
        ColumnConstraints cb = new ColumnConstraints(); cb.setPercentWidth(50);
        amtGrid.getColumnConstraints().addAll(ca, cb);

        VBox dueBox = buildAmtField("AMOUNT DUE", "0", true);
        VBox discBox = buildAmtField("DISCOUNT", "0", false);
        VBox penBox = buildAmtField("PENALTY (Late)", "0", false);
        VBox totBox = buildAmtField("TOTAL AMOUNT", "0", true);
        TextField dueTf = (TextField) dueBox.getChildren().get(1);
        TextField discountTf = (TextField) discBox.getChildren().get(1);
        TextField penaltyTf = (TextField) penBox.getChildren().get(1);
        TextField totalTf = (TextField) totBox.getChildren().get(1);

        Runnable recalcTotal = () -> {
            try {
                double due = parseMoney(dueTf.getText());
                double disc = parseMoney(discountTf.getText());
                double pen = parseMoney(penaltyTf.getText());
                double tot = Math.max(0, due - disc + pen);
                totalTf.setText(String.format("%.2f", tot));
            } catch (NumberFormatException ex) {
                totalTf.setText("0");
            }
        };
        dueTf.textProperty().addListener((o, a, b) -> recalcTotal.run());
        discountTf.textProperty().addListener((o, a, b) -> recalcTotal.run());
        penaltyTf.textProperty().addListener((o, a, b) -> recalcTotal.run());

        amtGrid.add(dueBox, 0, 0);
        amtGrid.add(discBox, 1, 0);
        amtGrid.add(penBox, 0, 1);
        amtGrid.add(totBox, 1, 1);

        findBtn.setOnAction(e -> {
            String q = memberSearch.getText().trim();
            if (q.isEmpty()) {
                selectedMember[0] = null;
                selectedBillingId[0] = 0;
                updateMemberInfo.run();
                return;
            }
            Optional<MemberDAO.MemberRecord> hit = Optional.empty();
            if (q.startsWith("M-") || q.startsWith("#")) {
                String code = q.startsWith("#") ? q.substring(1) : q;
                hit = memberDAO.findByCode(code);
            }
            if (hit.isEmpty()) {
                List<MemberDAO.MemberRecord> found = memberDAO.search(q);
                if (found.size() == 1) {
                    hit = Optional.of(found.get(0));
                }
            }
            if (hit.isEmpty()) {
                selectedMember[0] = null;
                selectedBillingId[0] = 0;
                updateMemberInfo.run();
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Member");
                a.setContentText("No single member matched. Try member code (e.g. M-001) or refine your search.");
                a.showAndWait();
                return;
            }
            selectedMember[0] = hit.get();
            selectedBillingId[0] = paymentDAO.ensureBillingForMember(
                selectedMember[0].memberId(),
                selectedMember[0].membershipType(),
                selectedMember[0].membershipEndDate(),
                AppSession.currentUser().userId()
            );
            updateMemberInfo.run();
            double balance = paymentDAO.balanceDueForMember(selectedMember[0].memberId());
            if (balance <= 0) {
                balance = paymentDAO.planPriceForMembership(selectedMember[0].membershipType());
            }
            infoVals[4].setText(formatPeso(balance));
            dueTf.setText(String.format("%.2f", balance));
            discountTf.setText("0");
            penaltyTf.setText("0");
            recalcTotal.run();
        });

        // Notes
        VBox notesGroup = new VBox(6);
        Label notesLbl = makeFieldLabel("NOTES (Optional)");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Additional payment notes...");
        notesArea.setPrefRowCount(2);
        notesArea.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;"
        );
        notesGroup.getChildren().addAll(notesLbl, notesArea);

        // Submit buttons
        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button clearBtn = new Button("Clear");
        clearBtn.setPrefHeight(42);
        clearBtn.setPadding(new Insets(0, 20, 0, 20));
        clearBtn.setFont(Font.font("Poppins", 12));
        clearBtn.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;"
        );
        Button printBtn = new Button("Print Receipt");
        printBtn.setPrefHeight(42);
        printBtn.setPadding(new Insets(0, 20, 0, 20));
        printBtn.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        printBtn.setStyle(
            "-fx-background-color: " + INFO + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        Button processBtn = new Button("Process Payment");
        processBtn.setPrefHeight(42);
        processBtn.setPadding(new Insets(0, 20, 0, 20));
        processBtn.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        processBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        processBtn.setOnMouseEntered(e -> processBtn.setStyle(
            "-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"
        ));
        processBtn.setOnMouseExited(e -> processBtn.setStyle(
            "-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"
        ));
        processBtn.setOnAction(e -> {
            if (selectedMember[0] == null) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Find and select a member first.");
                a.showAndWait();
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(totalTf.getText().trim().replace(",", ""));
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Invalid total amount.");
                a.showAndWait();
                return;
            }
            if (amount <= 0) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Amount must be greater than zero.");
                a.showAndWait();
                return;
            }
            ToggleButton sel = (ToggleButton) methodGroup2.getSelectedToggle();
            String method = sel != null && sel.getUserData() != null ? sel.getUserData().toString() : "Cash";
            if (!"Cash".equals(method) && refField.getText().trim().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Reference number is required for GCash and bank transfer payments.");
                a.showAndWait();
                return;
            }
            int uid = AppSession.currentUser().userId();
            if (selectedBillingId[0] <= 0) {
                selectedBillingId[0] = paymentDAO.ensureBillingForMember(
                    selectedMember[0].memberId(),
                    selectedMember[0].membershipType(),
                    selectedMember[0].membershipEndDate(),
                    uid
                );
            }
            int pid = paymentDAO.processMembershipPayment(
                selectedMember[0].memberId(),
                selectedBillingId[0],
                method,
                new Date(System.currentTimeMillis()),
                amount,
                refField.getText().trim(),
                notesArea.getText().trim(),
                uid
            );
            if (pid > 0) {
                lastPaymentId[0] = pid;
                refreshHist.run();
                updateMemberInfo.run();
                double balance = paymentDAO.balanceDueForMember(selectedMember[0].memberId());
                infoVals[4].setText(formatPeso(balance));
                dueTf.setText(String.format("%.2f", balance));
                discountTf.setText("0");
                penaltyTf.setText("0");
                recalcTotal.run();
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setContentText("Payment recorded. Reference #" + pid);
                ok.showAndWait();
                showReceiptDialog(paymentDAO, pid);
            } else {
                Alert er = new Alert(Alert.AlertType.ERROR);
                er.setContentText("Payment failed. Check database connection.");
                er.showAndWait();
            }
        });
        clearBtn.setOnAction(e -> {
            memberSearch.clear();
            selectedMember[0] = null;
            selectedBillingId[0] = 0;
            lastPaymentId[0] = 0;
            updateMemberInfo.run();
            refField.clear();
            notesArea.clear();
            dueTf.setText("0");
            discountTf.setText("0");
            penaltyTf.setText("0");
            recalcTotal.run();
        });
        printBtn.setOnAction(e -> {
            if (lastPaymentId[0] <= 0) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Process a payment first before printing a receipt.");
                a.showAndWait();
                return;
            }
            showReceiptDialog(paymentDAO, lastPaymentId[0]);
        });
        btnRow.getChildren().addAll(clearBtn, printBtn, processBtn);

        payCard.getChildren().addAll(
            payTitle, payLine, memberLookup, memberInfo,
            methodGroup, refGroup, amtGrid, notesGroup, btnRow
        );

        mainRow.getChildren().addAll(payCard, histCard);
        body.getChildren().addAll(statsRow, mainRow);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    //  Payment history table 
    private VBox buildPaymentHistory(PaymentDAO paymentDAO, VBox rowsBox) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10); ds.setOffsetY(4);
        card.setEffect(ds);

        HBox header = new HBox();
        header.setPadding(new Insets(16, 20, 14, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        Text t = new Text("Payment History");
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        t.setFill(Color.web(TEXT_WHITE));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("All", "Cash", "GCash", "Bank Transfer");
        filter.setValue("All");
        filter.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 6;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-font-size: 11;"
        );
        header.getChildren().addAll(t, sp, filter);

        String[] hdrs = {"Member", "Amount", "Method", "Date", "Status"};
        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(10, 20, 10, 20));
        tblHdr.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        GridPane hg = makePayGrid();
        hg.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hg, Priority.ALWAYS);
        for (int i = 0; i < hdrs.length; i++) {
            Label h = new Label(hdrs[i].toUpperCase());
            h.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
            h.setTextFill(Color.web(TEXT_DIM));
            hg.add(h, i, 0);
        }
        tblHdr.getChildren().add(hg);

        filter.setOnAction(e -> {
            String f = filter.getValue();
            List<PaymentDAO.PaymentRecord> all = paymentDAO.findRecentPayments(80);
            if ("All".equals(f)) {
                populatePaymentHistoryRows(rowsBox, all);
            } else {
                List<PaymentDAO.PaymentRecord> sub = new java.util.ArrayList<>();
                for (PaymentDAO.PaymentRecord p : all) {
                    if (p.paymentMethod() != null && p.paymentMethod().equalsIgnoreCase(f)) {
                        sub.add(p);
                    }
                }
                populatePaymentHistoryRows(rowsBox, sub);
            }
        });

        card.getChildren().addAll(header, tblHdr, rowsBox);
        return card;
    }

    private void populatePaymentHistoryRows(VBox rowsBox, List<PaymentDAO.PaymentRecord> payments) {
        rowsBox.getChildren().clear();
        int r = 0;
        for (PaymentDAO.PaymentRecord pr : payments) {
            String bg = (r % 2 == 0) ? BG_CARD : BG_ROW_ALT;
            HBox dataRow = new HBox();
            dataRow.setPadding(new Insets(10, 20, 10, 20));
            dataRow.setStyle("-fx-background-color: " + bg + ";");
            GridPane rg = makePayGrid();
            rg.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rg, Priority.ALWAYS);
            String name = pr.memberName() != null ? pr.memberName() : "Member";
            String amt = String.format("%.2f", pr.amount());
            String method = pr.paymentMethod() != null ? pr.paymentMethod() : "Cash";
            String dateStr = pr.paymentDate() != null ? pr.paymentDate().toString() : "";
            String statusLbl = "Completed".equalsIgnoreCase(pr.status()) ? "Paid" : pr.status();
            rg.add(makeCell(name, TEXT_WHITE, false), 0, 0);
            rg.add(makeCell(amt, SUCCESS, true), 1, 0);
            rg.add(makeMethodBadge(method), 2, 0);
            rg.add(makeCell(dateStr, TEXT_MUTED, false), 3, 0);
            rg.add(makeStatusBadge(statusLbl), 4, 0);
            dataRow.getChildren().add(rg);
            String fBg = bg;
            dataRow.setOnMouseEntered(e -> dataRow.setStyle("-fx-background-color: rgba(26,19,99,0.05);"));
            dataRow.setOnMouseExited(e -> dataRow.setStyle("-fx-background-color: " + fBg + ";"));
            rowsBox.getChildren().add(dataRow);
            r++;
        }
    }

    private static double parseMoney(String raw) {
        if (raw == null) {
            return 0;
        }
        String s = raw.replace("", "").replace(",", "").trim();
        if (s.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(s);
    }

    //  Helpers 
    private void showReceiptDialog(PaymentDAO paymentDAO, int paymentId) {
        Optional<PaymentDAO.ReceiptRecord> receipt = paymentDAO.findReceipt(paymentId);
        if (receipt.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Could not load receipt details.");
            a.showAndWait();
            return;
        }

        PaymentDAO.ReceiptRecord r = receipt.get();
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Payment Receipt");

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");
        root.setPrefWidth(430);

        Text title = new Text("MJ23 PLAYGRIND GYM");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        Text sub = new Text("Official Payment Receipt");
        sub.setFont(Font.font("Poppins", 12));
        sub.setFill(Color.web(TEXT_MUTED));

        TextArea receiptText = new TextArea(buildReceiptText(r));
        receiptText.setEditable(false);
        receiptText.setWrapText(false);
        receiptText.setPrefRowCount(17);
        receiptText.setStyle(
            "-fx-control-inner-background: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-font-family: Consolas;" +
            "-fx-font-size: 12;"
        );

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button close = new Button("Close");
        close.setPrefHeight(36);
        close.setPadding(new Insets(0, 18, 0, 18));
        close.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-text-fill: " + TEXT_MUTED + "; -fx-background-radius: 16;");
        close.setOnAction(e -> dialog.close());
        buttons.getChildren().add(close);

        root.getChildren().addAll(title, sub, receiptText, buttons);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private String buildReceiptText(PaymentDAO.ReceiptRecord r) {
        return ""
            + "Receipt No.: " + r.paymentId() + "\n"
            + "Date       : " + (r.paymentDate() != null ? r.paymentDate() : "") + "\n"
            + "Member ID  : " + safe(r.memberCode()) + "\n"
            + "Member     : " + safe(r.memberName()) + "\n"
            + "Plan       : " + safe(r.planName()) + "\n"
            + "Type       : " + safe(r.paymentType()) + "\n"
            + "Method     : " + safe(r.paymentMethod()) + "\n"
            + "Reference  : " + safe(r.transactionRef()) + "\n"
            + "Processed  : " + safe(r.processedBy()) + "\n"
            + "\n"
            + "Invoice    : " + formatPeso(r.invoiceAmount()) + "\n"
            + "Paid       : " + formatPeso(r.amountPaid()) + "\n"
            + "Balance    : " + formatPeso(r.balanceAfter()) + "\n"
            + "\n"
            + "Notes      : " + safe(r.notes()) + "\n"
            + "\nThank you for your payment.";
    }

    private static String formatPeso(double amount) {
        return "PHP " + String.format("%,.2f", amount);
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private GridPane makePayGrid() {
        GridPane g = new GridPane();
        double[] widths = {28, 14, 18, 18, 14};
        for (double w : widths) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(w);
            cc.setHgrow(Priority.ALWAYS);
            g.getColumnConstraints().add(cc);
        }
        return g;
    }

    private VBox buildAmtField(String label, String value, boolean highlight) {
        VBox g = new VBox(6);
        Label lbl = makeFieldLabel(label);
        TextField tf = new TextField(value);
        tf.setPrefHeight(40);
        tf.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + (highlight ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + (highlight ? TEXT_WHITE : TEXT_MUTED) + ";" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: " + (highlight ? "bold" : "normal") + ";" +
            "-fx-padding: 0 12 0 12;"
        );
        g.getChildren().addAll(lbl, tf);
        return g;
    }

    private Label makeFieldLabel(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_MUTED));
        return l;
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
            "-fx-font-size: 12;"
        );
        f.focusedProperty().addListener((o, old, focused) -> f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
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

    private void styleToggleBtn(ToggleButton tb, boolean selected) {
        tb.setStyle(
            "-fx-background-color: " + (selected ? ACCENT : BG_MAIN) + ";" +
            "-fx-text-fill: " + (selected ? "white" : TEXT_MUTED) + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + (selected ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 16;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: Poppins;"
        );
    }

    private Label makeCell(String text, String color, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        l.setTextFill(Color.web(color));
        return l;
    }

    private Label makeStatusBadge(String status) {
        Label b = new Label(status);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        boolean ok = status.equalsIgnoreCase("Paid") || status.equalsIgnoreCase("Completed");
        String c = ok ? SUCCESS : ACCENT;
        String bg = ok ? "rgba(228,255,223,0.15)" : "rgba(26,19,99,0.15)";
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 18; -fx-padding: 3 10 3 10;");
        return b;
    }

    private Label makeMethodBadge(String method) {
        Label b = new Label(method);
        b.setFont(Font.font("Poppins", 10));
        String c; String bg;
        if ("Cash".equalsIgnoreCase(method)) {
            c = SUCCESS; bg = "rgba(228,255,223,0.12)";
        } else if ("GCash".equalsIgnoreCase(method)) {
            c = "#77749B"; bg = "rgba(119,116,155,0.12)";
        } else {
            c = WARNING; bg = "rgba(253,238,33,0.12)";
        }
        b.setTextFill(Color.web(c));
        b.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 18; -fx-padding: 3 10 3 10;");
        return b;
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
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.2));
        ds.setRadius(8); ds.setOffsetY(3);
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

    public static void main(String[] args) { launch(args); }
}



