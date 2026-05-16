package mj23gym.ui;

import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
 * Account Management Screen  Create and manage system user accounts
 */
public class AccountManagementScreen extends Application {

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

    private UserDAO userDAO = new UserDAO();
    private TableView<UserRowData> usersTable;
    private Label statusLabel;

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

    // 
    // SIDEBAR
    // 
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
        for (String[] it : items) menu.getChildren().add(buildMenuItem(it[0], it[1], false));
        String[][] sys = {{"","Accounts"},{"","Settings"},{"","Help"}};
        VBox sysMenu = new VBox(2); sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys) sysMenu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Accounts")));
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

    // 
    // CONTENT
    // 
    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Top bar with title and add button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        
        VBox titleBox = new VBox(2);
        Text titleText = new Text("Registration / Verification");
        titleText.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        titleText.setFill(Color.web(TEXT_WHITE));
        Text subtitleText = new Text("Register staff accounts, check details, and activate verified users");
        subtitleText.setFont(Font.font("Poppins", 11));
        subtitleText.setFill(Color.web(TEXT_MUTED));
        titleBox.getChildren().addAll(titleText, subtitleText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+  REGISTER STAFF");
        addBtn.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        addBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-padding: 10 20 10 20;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(
            "-fx-background-color: " + ACCENT_DARK + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-padding: 10 20 10 20;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        ));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-padding: 10 20 10 20;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        ));
        addBtn.setOnAction(e -> showCreateAccountDialog());

        Button verifyBtn = buildActionButton("VERIFY STAFF", SUCCESS);
        verifyBtn.setOnAction(e -> updateSelectedVerification(true));

        Button holdBtn = buildActionButton("MARK UNVERIFIED", WARNING);
        holdBtn.setOnAction(e -> updateSelectedVerification(false));

        topBar.getChildren().addAll(titleBox, spacer, verifyBtn, holdBtn, addBtn);

        // Status label
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Poppins", 11));
        statusLabel.setPadding(new Insets(8, 28, 8, 28));
        statusLabel.setVisible(false);

        // Build table
        usersTable = buildUsersTable();
        ScrollPane scroll = new ScrollPane(usersTable);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + ";");

        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topBar, statusLabel, scroll);

        // Load users on display
        loadUsersTable();

        return content;
    }

    private TableView<UserRowData> buildUsersTable() {
        TableView<UserRowData> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-control-inner-background: " + BG_MAIN + ";" +
            "-fx-table-cell-border-color: " + BORDER + ";"
        );

        TableColumn<UserRowData, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().userId));
        idCol.setPrefWidth(80);

        TableColumn<UserRowData, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().username));
        usernameCol.setPrefWidth(120);

        TableColumn<UserRowData, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().fullName));
        nameCol.setPrefWidth(150);

        TableColumn<UserRowData, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().email));
        emailCol.setPrefWidth(180);

        TableColumn<UserRowData, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().phone));
        phoneCol.setPrefWidth(120);

        TableColumn<UserRowData, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().role));
        roleCol.setPrefWidth(100);

        TableColumn<UserRowData, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().status));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, usernameCol, nameCol, emailCol, phoneCol, roleCol, statusCol);
        return table;
    }

    private void loadUsersTable() {
        new Thread(() -> {
            try {
                List<UserDAO.UserRecord> users = userDAO.findAll();
                ObservableList<UserRowData> data = FXCollections.observableArrayList();
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
                }
                javafx.application.Platform.runLater(() -> usersTable.setItems(data));
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> showStatus("Error loading users: " + ex.getMessage(), false));
            }
        }).start();
    }

    // 
    // CREATE ACCOUNT DIALOG
    // 
    private Button buildActionButton(String label, String color) {
        Button button = new Button(label);
        button.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-padding: 10 16 10 16;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        return button;
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

    private void showCreateAccountDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Register Staff for Verification");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);

        VBox form = new VBox(12);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color: " + BG_CARD + ";");

        Text titleText = new Text("Register New Staff");
        titleText.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        titleText.setFill(Color.web(TEXT_WHITE));

        HBox usernameBox = buildInputField("Username", "staff01");
        TextField usernameField = (TextField) usernameBox.getChildren().get(1);

        HBox fullNameBox = buildInputField("Full Name", "John Smith");
        TextField fullNameField = (TextField) fullNameBox.getChildren().get(1);

        HBox emailBox = buildInputField("Email", "john@mj23gym.com");
        TextField emailField = (TextField) emailBox.getChildren().get(1);

        HBox phoneBox = buildInputField("Phone", "09171234567");
        TextField phoneField = (TextField) phoneBox.getChildren().get(1);

        Label roleInfo = new Label("Role is automatically saved as staff. New staff stay inactive until verified.");
        roleInfo.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        roleInfo.setTextFill(Color.web(INFO));
        roleInfo.setWrapText(true);

        HBox passwordBox = buildInputField("Password", "");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle(inputStyle());
        passwordField.setPromptText("Password");
        ((HBox) passwordBox).getChildren().set(1, passwordField);

        HBox confirmPassBox = buildInputField("Confirm Password", "");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setStyle(inputStyle());
        confirmPassField.setPromptText("Confirm Password");
        ((HBox) confirmPassBox).getChildren().set(1, confirmPassField);

        Label msgLabel = new Label();
        msgLabel.setFont(Font.font("Poppins", 10));
        msgLabel.setWrapText(true);
        msgLabel.setVisible(false);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-padding: 10 24 10 24;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> dialog.close());

        Button saveBtn = new Button("REGISTER FOR VERIFICATION");
        saveBtn.setStyle(
            "-fx-background-color: " + SUCCESS + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-padding: 10 24 10 24;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: bold;"
        );
        saveBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText();
            String confirmPass = confirmPassField.getText();

            String validationError = validateStaffRegistration(username, fullName, email, phone, password, confirmPass);
            if (validationError != null) {
                msgLabel.setText(validationError);
                msgLabel.setTextFill(Color.web(WARNING));
                msgLabel.setVisible(true);
                return;
            }

            saveBtn.setDisable(true);
            msgLabel.setText("Registering staff for admin verification...");
            msgLabel.setTextFill(Color.web(INFO));
            msgLabel.setVisible(true);

            new Thread(() -> {
                try {
                    boolean success = userDAO.registerStaffForVerification(username, fullName, email, phone, password);
                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            msgLabel.setText("Staff registered. Verify the row to activate login access.");
                            msgLabel.setTextFill(Color.web(SUCCESS));
                            showStatus("Staff '" + username + "' registered as unverified.", true);
                            loadUsersTable();
                            new Thread(() -> {
                                try { Thread.sleep(1500); } catch (InterruptedException ex) {}
                                javafx.application.Platform.runLater(dialog::close);
                            }).start();
                        } else {
                            msgLabel.setText("Failed to register staff. Check for duplicate username or database rules.");
                            msgLabel.setTextFill(Color.web(WARNING));
                            saveBtn.setDisable(false);
                        }
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        msgLabel.setText("Error: " + ex.getMessage());
                        msgLabel.setTextFill(Color.web(WARNING));
                        saveBtn.setDisable(false);
                    });
                }
            }).start();
        });

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        form.getChildren().addAll(
            titleText,
            usernameBox,
            fullNameBox,
            emailBox,
            phoneBox,
            roleInfo,
            passwordBox,
            confirmPassBox,
            msgLabel,
            buttonBox
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_CARD + ";");

        Scene dialogScene = new Scene(scroll, 500, 650);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private String inputStyle() {
        return "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-padding: 8 12 8 12;" +
            "-fx-font-size: 11;";
    }

    private HBox buildInputField(String label, String placeholder) {
        HBox box = new HBox(12);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        lbl.setMinWidth(100);
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setStyle(inputStyle());
        field.setPrefWidth(250);
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private String validateStaffRegistration(String username, String fullName, String email,
                                             String phone, String password, String confirmPass) {
        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            return "Username, full name, email, phone, and password are required";
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
        return null;
    }

    private void showStatus(String message, boolean isSuccess) {
        statusLabel.setText((isSuccess ? "" : "") + message);
        statusLabel.setTextFill(Color.web(isSuccess ? SUCCESS : WARNING));
        statusLabel.setStyle(
            "-fx-background-color: " + (isSuccess ? "rgba(228,255,223,0.12)" : "rgba(253,238,33,0.12)") + ";" +
            "-fx-background-radius: 6;"
        );
        statusLabel.setVisible(true);
    }
}



