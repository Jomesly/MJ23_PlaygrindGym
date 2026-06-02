package mj23gym.ui;

import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
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
import mj23gym.dao.UserDAO;

/**
 * Account Management Screen — register staff and verify accounts before login.
 */
public class AccountManagementScreen extends Application {

    static final String BG_MAIN       = ModernDesignSystem.BG_LIGHT;
    static final String BG_SIDEBAR    = ModernDesignSystem.SIDEBAR_BG;
    static final String BG_CARD       = ModernDesignSystem.CARD_BG;
    static final String BG_ROW_ALT    = ModernDesignSystem.HOVER_EFFECT;
    static final String ACCENT        = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK   = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_TITLE    = ModernDesignSystem.PRIMARY;
    static final String TEXT_SOFT     = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DIM      = ModernDesignSystem.DARK_GRAY;
    static final String BORDER        = ModernDesignSystem.BORDER_COLOR;
    static final String SUCCESS_TEXT  = "#237A36";
    static final String WARNING_TEXT  = "#6E6400";
    static final String CARD_SURFACE  = ModernDesignSystem.WHITE;

    private final UserDAO userDAO = new UserDAO();
    private TableView<UserRowData> usersTable;
    private Label statusLabel;
    private final Text statTotal = new Text("0");
    private final Text statVerified = new Text("0");
    private final Text statPending = new Text("0");

    public static class UserRowData {
        public String userId;
        public String username;
        public String fullName;
        public String email;
        public String phone;
        public String role;
        public String status;

        public UserRowData(String userId, String username, String fullName, String email, String phone, String role, String status) {
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.role = role;
            this.status = status;
        }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Account Management");
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
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web("#FDEE21"));
        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane badge = new StackPane();
        badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42);
        bb.setArcWidth(10);
        bb.setArcHeight(10);
        bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ");
        bt.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND");
        l1.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        l1.setFill(Color.web(TEXT_TITLE));
        Text l2 = new Text("GYM");
        l2.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(badge, lt);
        String[][] items = {
            {"", "Dashboard"}, {"", "Member Management"}, {"", "Payment & Billing"},
            {"", "Inventory"}, {"", "Equipment"}, {"", "Point of Sale"}, {"", "Reports"}
        };
        VBox menu = new VBox(2);
        menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items) {
            menu.getChildren().add(buildMenuItem(it[0], it[1], false));
        }
        String[][] sys = {{"", "Accounts"}, {"", "Settings"}, {"", "Help"}};
        VBox sysMenu = new VBox(2);
        sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys) {
            sysMenu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Accounts")));
        }
        Region sp = new Region();
        VBox.setVgrow(sp, Priority.ALWAYS);
        Rectangle d1 = new Rectangle(230, 1);
        d1.setFill(Color.web(BORDER));
        Rectangle d2 = new Rectangle(230, 1);
        d2.setFill(Color.web(BORDER));
        sidebar.getChildren().addAll(topAccent, logoArea, d1, makeSecLbl("MAIN MENU"), menu, d2, makeSecLbl("SYSTEM"), sysMenu, sp);
        return sidebar;
    }

    private HBox buildMenuItem(String icon, String label, boolean active) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(11, 16, 11, 16));
        item.setCursor(javafx.scene.Cursor.HAND);
        Rectangle bar = new Rectangle(3, 36);
        bar.setArcWidth(3);
        bar.setArcHeight(3);
        bar.setFill(active ? Color.web(ACCENT) : Color.TRANSPARENT);
        Text ico = new Text(icon);
        ico.setFont(Font.font(14));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        lbl.setFill(active ? Color.web(TEXT_TITLE) : Color.web(TEXT_SOFT));
        item.getChildren().addAll(bar, ico, lbl);
        item.setStyle(active ? "-fx-background-color: " + BG_CARD + "; -fx-background-radius: 16;" : "-fx-background-color: transparent; -fx-background-radius: 16;");
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

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        VBox titleBox = new VBox(2);
        Text titleText = new Text("Registration / Verification");
        titleText.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        titleText.setFill(Color.web(TEXT_TITLE));
        Text subtitleText = new Text("Register staff accounts, review details, and activate verified users");
        subtitleText.setFont(Font.font("Poppins", 11));
        subtitleText.setFill(Color.web(TEXT_SOFT));
        titleBox.getChildren().addAll(titleText, subtitleText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button verifyBtn = pillButton("Verify Staff", SUCCESS_TEXT);
        verifyBtn.setOnAction(e -> updateSelectedVerification(true));

        Button holdBtn = pillButton("Mark Unverified", WARNING_TEXT);
        holdBtn.setOnAction(e -> updateSelectedVerification(false));

        Button recoveryBtn = pillButton("Recovery Setup", TEXT_TITLE);
        recoveryBtn.setOnAction(e -> showRecoverySetupDialog());

        Button addBtn = accentButton("+ Register Staff");
        addBtn.setOnAction(e -> showCreateAccountDialog());

        topBar.getChildren().addAll(titleBox, spacer, verifyBtn, holdBtn, recoveryBtn, addBtn);

        statusLabel = new Label();
        statusLabel.setFont(Font.font("Poppins", 11));
        statusLabel.setPadding(new Insets(0, 28, 0, 28));
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        for (Text t : new Text[] { statTotal, statVerified, statPending }) {
            t.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        }

        HBox statsRow = new HBox(14);
        statsRow.getChildren().addAll(
            statChip("Total Staff", statTotal, TEXT_TITLE),
            statChip("Verified (Active)", statVerified, SUCCESS_TEXT),
            statChip("Pending Verification", statPending, WARNING_TEXT)
        );

        Label hint = new Label("New staff stay inactive until you select a row and click Verify Staff. Only staff accounts can be verified here.");
        hint.setFont(Font.font("Poppins", 11));
        hint.setTextFill(Color.web(TEXT_SOFT));
        hint.setWrapText(true);
        hint.setMaxWidth(Double.MAX_VALUE);
        hint.setStyle(
            "-fx-background-color: rgba(26,19,99,0.05);" +
            "-fx-padding: 12 16;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(26,19,99,0.12);" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;"
        );

        usersTable = buildUsersTable();
        VBox tableCard = buildTableCard(usersTable);

        VBox body = new VBox(18, statsRow, hint, statusLabel, tableCard);
        body.setPadding(new Insets(24, 28, 28, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topBar, scroll);
        loadUsersTable();
        return content;
    }

    private VBox buildTableCard(TableView<UserRowData> table) {
        VBox card = new VBox(0);
        card.setStyle(
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
        card.setEffect(ds);

        VBox head = new VBox(6);
        head.setPadding(new Insets(18, 20, 12, 20));
        Rectangle bar = new Rectangle(42, 3);
        bar.setArcWidth(3);
        bar.setArcHeight(3);
        bar.setFill(Color.web(ACCENT));
        Text title = new Text("Staff Accounts");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        title.setFill(Color.web(TEXT_TITLE));
        Text sub = new Text("Select a row to verify or mark unverified");
        sub.setFont(Font.font("Poppins", 11));
        sub.setFill(Color.web(TEXT_SOFT));
        head.getChildren().addAll(bar, title, sub);

        table.setPrefHeight(420);
        table.setMinHeight(280);
        VBox tableWrap = new VBox(table);
        tableWrap.setPadding(new Insets(0, 12, 12, 12));
        VBox.setVgrow(table, Priority.ALWAYS);

        card.getChildren().addAll(head, tableWrap);
        return card;
    }

    private TableView<UserRowData> buildUsersTable() {
        TableView<UserRowData> table = new TableView<>();
        applyTableStyle(table);

        TableColumn<UserRowData, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().userId));
        idCol.setPrefWidth(56);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<UserRowData, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().username));
        usernameCol.setPrefWidth(110);
        styleTextColumn(usernameCol, true);

        TableColumn<UserRowData, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().fullName));
        nameCol.setPrefWidth(150);
        styleTextColumn(nameCol, true);

        TableColumn<UserRowData, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().email));
        emailCol.setPrefWidth(200);
        styleTextColumn(emailCol, false);

        TableColumn<UserRowData, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().phone));
        phoneCol.setPrefWidth(120);
        styleTextColumn(phoneCol, false);

        TableColumn<UserRowData, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().role));
        roleCol.setPrefWidth(90);
        roleCol.setCellFactory(col -> badgeCell(this::roleBadge));

        TableColumn<UserRowData, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().status));
        statusCol.setPrefWidth(110);
        statusCol.setCellFactory(col -> badgeCell(this::statusBadge));

        table.getColumns().addAll(idCol, usernameCol, nameCol, emailCol, phoneCol, roleCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return table;
    }

    private void applyTableStyle(TableView<?> table) {
        table.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-control-inner-background: " + CARD_SURFACE + ";" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 0;" +
            "-fx-table-cell-border-color: " + BORDER + ";" +
            "-fx-table-header-border-color: " + BORDER + ";"
        );
        table.setFixedCellSize(40);
        String css =
            ".table-view .column-header {" +
            "  -fx-background-color: " + BG_ROW_ALT + ";" +
            "  -fx-border-color: " + BORDER + ";" +
            "  -fx-border-width: 0 0 1 0;" +
            "}" +
            ".table-view .column-header .label {" +
            "  -fx-font-family: Poppins;" +
            "  -fx-font-size: 10px;" +
            "  -fx-font-weight: bold;" +
            "  -fx-text-fill: " + TEXT_SOFT + ";" +
            "}" +
            ".table-view .table-row-cell {" +
            "  -fx-background-color: " + CARD_SURFACE + ";" +
            "  -fx-border-color: transparent;" +
            "}" +
            ".table-view .table-row-cell:odd {" +
            "  -fx-background-color: " + BG_ROW_ALT + ";" +
            "}" +
            ".table-view .table-row-cell:selected {" +
            "  -fx-background-color: rgba(26,19,99,0.12);" +
            "  -fx-background-insets: 0;" +
            "}" +
            ".table-view .table-row-cell:hover {" +
            "  -fx-background-color: rgba(26,19,99,0.06);" +
            "}" +
            ".table-view .table-cell {" +
            "  -fx-border-color: transparent;" +
            "  -fx-padding: 8 10;" +
            "}";
        table.getStylesheets().add("data:text/css," + css.replace("\n", ""));
    }

    private void styleTextColumn(TableColumn<UserRowData, String> col, boolean bold) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
                setTextFill(Color.web(bold ? TEXT_TITLE : TEXT_SOFT));
            }
        });
    }

    private TableCell<UserRowData, String> badgeCell(java.util.function.Function<String, Label> factory) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(factory.apply(item));
            }
        };
    }

    private Label roleBadge(String role) {
        String r = role == null ? "" : role.toLowerCase();
        String c = r.contains("admin") ? TEXT_TITLE : TEXT_SOFT;
        String bg = r.contains("admin") ? "rgba(26,19,99,0.10)" : "rgba(119,116,155,0.12)";
        return pillLabel(role, c, bg);
    }

    private Label statusBadge(String status) {
        String s = status == null ? "" : status.toLowerCase();
        String c;
        String bg;
        if (s.contains("active")) {
            c = SUCCESS_TEXT;
            bg = "rgba(228,255,223,0.75)";
        } else if (s.contains("inactive")) {
            c = WARNING_TEXT;
            bg = "rgba(253,238,33,0.28)";
        } else {
            c = TEXT_SOFT;
            bg = "rgba(119,116,155,0.12)";
        }
        return pillLabel(displayStatus(status), c, bg);
    }

    private String displayStatus(String status) {
        if (status == null) {
            return "—";
        }
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }

    private Label pillLabel(String text, String color, String bg) {
        Label b = new Label(text == null ? "—" : text);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        b.setStyle(
            "-fx-text-fill: " + color + ";" +
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 4 10 4 10;"
        );
        return b;
    }

    private void loadUsersTable() {
        new Thread(() -> {
            try {
                List<UserDAO.UserRecord> users = userDAO.findAll();
                ObservableList<UserRowData> data = FXCollections.observableArrayList();
                int staffTotal = 0;
                int verified = 0;
                int pending = 0;
                for (UserDAO.UserRecord ur : users) {
                    data.add(new UserRowData(
                        String.valueOf(ur.userId()),
                        ur.username(),
                        ur.fullName(),
                        ur.email(),
                        ur.phone(),
                        ur.role(),
                        ur.status()
                    ));
                    if ("staff".equalsIgnoreCase(ur.role())) {
                        staffTotal++;
                        if ("active".equalsIgnoreCase(ur.status())) {
                            verified++;
                        } else {
                            pending++;
                        }
                    }
                }
                final int t = staffTotal;
                final int v = verified;
                final int p = pending;
                javafx.application.Platform.runLater(() -> {
                    usersTable.setItems(data);
                    statTotal.setText(String.valueOf(t));
                    statVerified.setText(String.valueOf(v));
                    statPending.setText(String.valueOf(p));
                    statTotal.setFill(Color.web(TEXT_TITLE));
                    statVerified.setFill(Color.web(SUCCESS_TEXT));
                    statPending.setFill(Color.web(WARNING_TEXT));
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> showStatus("Error loading users: " + ex.getMessage(), false));
            }
        }).start();
    }

    private void updateSelectedVerification(boolean verified) {
        UserRowData selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("Select a staff account first.", false);
            return;
        }
        if (!"staff".equalsIgnoreCase(selected.role)) {
            showStatus("Only staff accounts can be verified in Module 2.", false);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(selected.userId);
        } catch (NumberFormatException ex) {
            showStatus("Selected account has an invalid user ID.", false);
            return;
        }

        new Thread(() -> {
            boolean success = userDAO.setVerificationStatus(userId, verified);
            javafx.application.Platform.runLater(() -> {
                if (success) {
                    showStatus(selected.username + (verified
                        ? " is now verified and can log in as staff."
                        : " is now unverified and cannot log in."), true);
                    loadUsersTable();
                } else {
                    showStatus("Could not update verification status.", false);
                }
            });
        }).start();
    }

    private void showRecoverySetupDialog() {
        UserRowData selected = usersTable == null ? null : usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("Select an account first before setting recovery information.", false);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(selected.userId);
        } catch (NumberFormatException ex) {
            showStatus("Selected account has an invalid user ID.", false);
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Recovery Setup");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);

        VBox form = new VBox(14);
        form.setPadding(new Insets(24, 28, 28, 28));
        form.setStyle("-fx-background-color: " + CARD_SURFACE + ";");
        form.setPrefWidth(500);

        Text titleText = new Text("Set Recovery Details");
        titleText.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        titleText.setFill(Color.web(TEXT_TITLE));
        Text subText = new Text(selected.username + " - " + selected.fullName);
        subText.setFont(Font.font("Poppins", 11));
        subText.setFill(Color.web(TEXT_SOFT));

        ComboBox<String> recoveryQuestionField = buildRecoveryQuestionBox();
        VBox recoveryQuestionBox = comboField("Security Question", recoveryQuestionField);

        VBox recoveryAnswerBox = formField("Security Answer", "");
        PasswordField recoveryAnswerField = new PasswordField();
        applyFieldStyle(recoveryAnswerField);
        recoveryAnswerField.setPromptText("Enter answer");
        recoveryAnswerBox.getChildren().set(1, recoveryAnswerField);

        VBox confirmAnswerBox = formField("Confirm Answer", "");
        PasswordField confirmAnswerField = new PasswordField();
        applyFieldStyle(confirmAnswerField);
        confirmAnswerField.setPromptText("Re-enter answer");
        confirmAnswerBox.getChildren().set(1, confirmAnswerField);

        Label msgLabel = new Label();
        msgLabel.setFont(Font.font("Poppins", 10));
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);
        msgLabel.setManaged(false);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = outlineButton("Cancel");
        cancelBtn.setOnAction(e -> dialog.close());
        Button saveBtn = accentButton("Save Recovery Info");
        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        saveBtn.setOnAction(e -> {
            String question = recoveryQuestionField.getValue();
            String answer = recoveryAnswerField.getText();
            String confirm = confirmAnswerField.getText();

            if (question == null || question.isBlank() || answer == null || answer.trim().isEmpty()) {
                showInlineDialogMessage(msgLabel, "Security question and answer are required.", false);
                return;
            }
            if (answer.trim().length() < 3) {
                showInlineDialogMessage(msgLabel, "Security answer must be at least 3 characters.", false);
                return;
            }
            if (!answer.equals(confirm)) {
                showInlineDialogMessage(msgLabel, "Security answer and confirmation do not match.", false);
                return;
            }

            saveBtn.setDisable(true);
            showInlineDialogMessage(msgLabel, "Saving recovery information...", true);
            new Thread(() -> {
                boolean success = userDAO.updateRecoveryChallenge(userId, question, answer);
                javafx.application.Platform.runLater(() -> {
                    if (success) {
                        showStatus("Recovery information saved for " + selected.username + ".", true);
                        showInlineDialogMessage(msgLabel, "Recovery information saved.", true);
                        new Thread(() -> {
                            try {
                                Thread.sleep(900);
                            } catch (InterruptedException ignored) {
                            }
                            javafx.application.Platform.runLater(dialog::close);
                        }).start();
                    } else {
                        showInlineDialogMessage(msgLabel, "Could not save recovery information.", false);
                        saveBtn.setDisable(false);
                    }
                });
            }).start();
        });

        form.getChildren().addAll(
            titleText, subText,
            recoveryQuestionBox, recoveryAnswerBox, confirmAnswerBox,
            msgLabel, buttonBox
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: " + CARD_SURFACE + "; -fx-background: " + CARD_SURFACE + ";");

        Scene dialogScene = new Scene(scroll, 520, 540);
        dialogScene.setFill(Color.web(CARD_SURFACE));
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void showCreateAccountDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Register Staff for Verification");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);

        VBox form = new VBox(14);
        form.setPadding(new Insets(24, 28, 28, 28));
        form.setStyle("-fx-background-color: " + CARD_SURFACE + ";");
        form.setPrefWidth(480);

        Rectangle accentBar = new Rectangle(42, 3);
        accentBar.setArcWidth(3);
        accentBar.setArcHeight(3);
        accentBar.setFill(Color.web(ACCENT));
        Text titleText = new Text("Register New Staff");
        titleText.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        titleText.setFill(Color.web(TEXT_TITLE));
        Text subText = new Text("Account stays inactive until an admin verifies it");
        subText.setFont(Font.font("Poppins", 11));
        subText.setFill(Color.web(TEXT_SOFT));

        VBox usernameBox = formField("Username", "staff01");
        TextField usernameField = (TextField) usernameBox.getChildren().get(1);

        VBox fullNameBox = formField("Full Name", "John Smith");
        TextField fullNameField = (TextField) fullNameBox.getChildren().get(1);

        VBox emailBox = formField("Email", "john@mj23gym.com");
        TextField emailField = (TextField) emailBox.getChildren().get(1);

        VBox phoneBox = formField("Phone", "09171234567");
        TextField phoneField = (TextField) phoneBox.getChildren().get(1);

        Label roleInfo = new Label("Role is saved as staff. Login is blocked until verification.");
        roleInfo.setFont(Font.font("Poppins", 10));
        roleInfo.setTextFill(Color.web(TEXT_SOFT));
        roleInfo.setWrapText(true);
        roleInfo.setStyle(
            "-fx-background-color: rgba(253,238,33,0.22);" +
            "-fx-text-fill: " + WARNING_TEXT + ";" +
            "-fx-padding: 8 12;" +
            "-fx-background-radius: 12;"
        );

        VBox passwordBox = formField("Password", "");
        PasswordField passwordField = new PasswordField();
        applyFieldStyle(passwordField);
        passwordField.setPromptText("At least 6 characters");
        passwordBox.getChildren().set(1, passwordField);

        VBox confirmPassBox = formField("Confirm Password", "");
        PasswordField confirmPassField = new PasswordField();
        applyFieldStyle(confirmPassField);
        confirmPassField.setPromptText("Re-enter password");
        confirmPassBox.getChildren().set(1, confirmPassField);

        ComboBox<String> recoveryQuestionField = buildRecoveryQuestionBox();
        VBox recoveryQuestionBox = comboField("Security Question", recoveryQuestionField);

        VBox recoveryAnswerBox = formField("Security Answer", "");
        PasswordField recoveryAnswerField = new PasswordField();
        applyFieldStyle(recoveryAnswerField);
        recoveryAnswerField.setPromptText("Answer for account recovery");
        recoveryAnswerBox.getChildren().set(1, recoveryAnswerField);

        Label msgLabel = new Label();
        msgLabel.setFont(Font.font("Poppins", 10));
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);
        msgLabel.setManaged(false);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = outlineButton("Cancel");
        cancelBtn.setOnAction(e -> dialog.close());
        Button saveBtn = accentButton("Register for Verification");
        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        saveBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText();
            String confirmPass = confirmPassField.getText();
            String recoveryQuestion = recoveryQuestionField.getValue();
            String recoveryAnswer = recoveryAnswerField.getText();

            String validationError = validateStaffRegistration(
                username, fullName, email, phone, password, confirmPass, recoveryQuestion, recoveryAnswer
            );
            if (validationError != null) {
                msgLabel.setText(validationError);
                msgLabel.setTextFill(Color.web(WARNING_TEXT));
                msgLabel.setVisible(true);
                msgLabel.setManaged(true);
                return;
            }

            saveBtn.setDisable(true);
            msgLabel.setText("Registering staff for admin verification...");
            msgLabel.setTextFill(Color.web(TEXT_TITLE));
            msgLabel.setVisible(true);
            msgLabel.setManaged(true);

            new Thread(() -> {
                try {
                    boolean success = userDAO.registerStaffForVerification(
                        username, fullName, email, phone, password, recoveryQuestion, recoveryAnswer
                    );
                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            msgLabel.setText("Staff registered. Select the row and click Verify Staff to activate login.");
                            msgLabel.setTextFill(Color.web(SUCCESS_TEXT));
                            showStatus("Staff '" + username + "' registered as unverified.", true);
                            loadUsersTable();
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException ignored) {
                                }
                                javafx.application.Platform.runLater(dialog::close);
                            }).start();
                        } else {
                            msgLabel.setText("Failed to register staff. Check for duplicate username or database rules.");
                            msgLabel.setTextFill(Color.web(WARNING_TEXT));
                            saveBtn.setDisable(false);
                        }
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        msgLabel.setText("Error: " + ex.getMessage());
                        msgLabel.setTextFill(Color.web(WARNING_TEXT));
                        saveBtn.setDisable(false);
                    });
                }
            }).start();
        });

        form.getChildren().addAll(
            accentBar, titleText, subText,
            usernameBox, fullNameBox, emailBox, phoneBox,
            roleInfo, passwordBox, confirmPassBox,
            recoveryQuestionBox, recoveryAnswerBox,
            msgLabel, buttonBox
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: " + CARD_SURFACE + "; -fx-background: " + CARD_SURFACE + ";");

        Scene dialogScene = new Scene(scroll, 520, 640);
        dialogScene.setFill(Color.web(CARD_SURFACE));
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private VBox formField(String label, String placeholder) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        lbl.setTextFill(Color.web(TEXT_SOFT));
        TextField field = new TextField();
        field.setPromptText(placeholder);
        applyFieldStyle(field);
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private VBox comboField(String label, ComboBox<String> combo) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        lbl.setTextFill(Color.web(TEXT_SOFT));
        box.getChildren().addAll(lbl, combo);
        return box;
    }

    private ComboBox<String> buildRecoveryQuestionBox() {
        ComboBox<String> box = new ComboBox<>();
        box.getItems().addAll(
            UserDAO.DEFAULT_RECOVERY_QUESTION,
            "What is your mother's maiden name?",
            "What city were you born in?",
            "What was the name of your first school?"
        );
        box.setValue(UserDAO.DEFAULT_RECOVERY_QUESTION);
        box.setPrefHeight(42);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;"
        );
        return box;
    }

    private void applyFieldStyle(TextField field) {
        String base =
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-prompt-text-fill: " + TEXT_SOFT + ";" +
            "-fx-padding: 10 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;";
        field.setStyle(base);
        field.focusedProperty().addListener((o, old, focused) -> field.setStyle(
            base + "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";"
        ));
    }

    private String validateStaffRegistration(String username, String fullName, String email,
                                             String phone, String password, String confirmPass,
                                             String recoveryQuestion, String recoveryAnswer) {
        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            return "Username, full name, email, phone, and password are required";
        }
        if (recoveryQuestion == null || recoveryQuestion.isBlank() || recoveryAnswer == null || recoveryAnswer.trim().isEmpty()) {
            return "Security question and answer are required for account recovery";
        }
        if (!username.matches("^[A-Za-z0-9._-]{4,30}$")) {
            return "Username must be 4-30 characters using letters, numbers, dot, dash, or underscore";
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Enter a valid email address";
        }
        if (!phone.matches("^[0-9+\\-\\s]{7,20}$")) {
            return "Enter a valid phone number";
        }
        if (!password.equals(confirmPass)) {
            return "Passwords do not match";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        if (recoveryAnswer.trim().length() < 3) {
            return "Security answer must be at least 3 characters";
        }
        return null;
    }

    private HBox statChip(String label, Text value, String color) {
        HBox chip = new HBox(12);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 18, 14, 18));
        String outline = readableAccent(color);
        chip.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + outline + ";" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1.5;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow d = new DropShadow();
        d.setColor(Color.web(outline, 0.08));
        d.setRadius(6);
        d.setOffsetY(2);
        chip.setEffect(d);
        value.setFill(Color.web(outline));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        lbl.setFill(Color.web(TEXT_SOFT));
        chip.getChildren().addAll(new VBox(2, lbl, value));
        return chip;
    }

    private String readableAccent(String color) {
        if (color == null || color.isBlank() || "#FDEE21".equalsIgnoreCase(color)) {
            return WARNING_TEXT;
        }
        return color;
    }

    private Button accentButton(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38);
        b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        String base =
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 14;" +
            "-fx-cursor: hand;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(base.replace(ACCENT, ACCENT_DARK)));
        b.setOnMouseExited(e -> b.setStyle(base));
        return b;
    }

    private Button outlineButton(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38);
        b.setPadding(new Insets(0, 16, 0, 16));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        String base =
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-cursor: hand;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(base + "-fx-background-color: rgba(26,19,99,0.08);"));
        b.setOnMouseExited(e -> b.setStyle(base));
        return b;
    }

    private Button pillButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        btn.setPadding(new Insets(8, 14, 8, 14));
        String wash = SUCCESS_TEXT.equals(color)
            ? "rgba(228,255,223,0.75)"
            : WARNING_TEXT.equals(color)
                ? "rgba(253,238,33,0.28)"
                : "rgba(26,19,99,0.08)";
        String base =
            "-fx-background-color: " + wash + ";" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base + "-fx-background-color: rgba(26,19,99,0.12);"));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private void showStatus(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Color.web(isSuccess ? SUCCESS_TEXT : WARNING_TEXT));
        statusLabel.setStyle(
            "-fx-background-color: " + (isSuccess ? "rgba(228,255,223,0.75)" : "rgba(253,238,33,0.28)") + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 10 14;" +
            "-fx-border-color: " + (isSuccess ? "rgba(35,122,54,0.25)" : "rgba(110,100,0,0.25)") + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;"
        );
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void showInlineDialogMessage(Label label, String message, boolean isSuccess) {
        label.setText(message);
        label.setTextFill(Color.web(isSuccess ? SUCCESS_TEXT : WARNING_TEXT));
        label.setStyle(
            "-fx-background-color: " + (isSuccess ? "rgba(228,255,223,0.75)" : "rgba(253,238,33,0.28)") + ";" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 8 12;" +
            "-fx-border-color: " + (isSuccess ? "rgba(35,122,54,0.25)" : "rgba(110,100,0,0.25)") + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        label.setVisible(true);
        label.setManaged(true);
    }
}
