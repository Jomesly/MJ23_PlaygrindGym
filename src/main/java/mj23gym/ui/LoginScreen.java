package mj23gym.ui;

import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mj23gym.dao.UserDAO;

/**
 * MJ23 Playgrind Gym  Login Screen
 *
 * Color scheme matched exactly from the official screen design mockups:
 *   BG Main      : #ECE9E9  (deep navy background)
 *   BG Darker    : #FFFFFF  (left panel / sidebar dark)
 *   BG Card      : #FFFFFF  (card/panel surface)
 *   Accent Red   : #1A1363  (buttons, highlights, active states)
 *   Accent Dark  : #332F4F  (hover state for red)
 *   Field BG     : #0f2030  (input background)
 *   Field Border : #E4FFDF  (input border default)
 *   Text White   : #ffffff
 *   Text Muted   : #77749B
 *   Text Dim     : #4B4B4B
 */
public class LoginScreen extends Application {

    // Use modern design system colors
    private static final String BG_MAIN      = ModernDesignSystem.BG_LIGHT;
    private static final String BG_DARKER    = ModernDesignSystem.SIDEBAR_BG;
    private static final String BG_CARD      = ModernDesignSystem.CARD_BG;
    private static final String ACCENT       = ModernDesignSystem.PRIMARY;
    private static final String ACCENT_DARK  = ModernDesignSystem.PRIMARY_DARK;
    private static final String FIELD_BG     = ModernDesignSystem.WHITE;
    private static final String FIELD_BORDER = ModernDesignSystem.SUCCESS;
    private static final String FIELD_FOCUS  = ModernDesignSystem.PRIMARY;
    private static final String TEXT_WHITE   = ModernDesignSystem.PRIMARY;
    private static final String TEXT_MUTED   = ModernDesignSystem.TEXT_MUTED;
    private static final String TEXT_DIM     = ModernDesignSystem.DARK_GRAY;

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Management System");

        //  Root: left branding panel + right form panel 
        HBox root = new HBox();
        root.setPrefSize(950, 620);

        // 
        // LEFT PANEL  dark branding side
        // 
        VBox left = new VBox();
        left.setPrefWidth(390);
        left.setMinWidth(390);
        left.setMaxWidth(390);
        left.setAlignment(Pos.CENTER);
        left.setSpacing(0);
        left.setStyle("-fx-background-color: " + BG_DARKER + ";");

        // Top accent bar
        Rectangle topBar = new Rectangle(390, 5);
        topBar.setFill(Color.web("#FDEE21"));

        // Spacer so content is vertically centered
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);

        // Logo badge
        StackPane badge = new StackPane();
        badge.setPrefSize(96, 96);
        badge.setMaxSize(96, 96);
        Rectangle badgeBg = new Rectangle(96, 96);
        badgeBg.setArcWidth(ModernDesignSystem.RADIUS_LARGE);
        badgeBg.setArcHeight(ModernDesignSystem.RADIUS_LARGE);
        badgeBg.setFill(Color.web(ModernDesignSystem.PRIMARY));
        badgeBg.setEffect(ModernDesignSystem.createElevation4());
        VBox badgeWords = new VBox(-6);
        badgeWords.setAlignment(Pos.CENTER);
        Text mj = new Text("MJ");
        mj.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 36));
        mj.setFill(Color.WHITE);
        Text t23 = new Text("23");
        t23.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 15));
        t23.setFill(Color.web("#ffffff", 0.80));
        badgeWords.getChildren().addAll(mj, t23);
        badge.getChildren().addAll(badgeBg, badgeWords);

        // Gym title
        Text line1 = new Text("MJ23 PLAYGRIND");
        line1.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        line1.setFill(Color.web(TEXT_WHITE));
        Text line2 = new Text("GYM");
        line2.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        line2.setFill(Color.web(ACCENT));
        VBox gymTitle = new VBox(0, line1, line2);
        gymTitle.setAlignment(Pos.CENTER);

        // Red underline
        Rectangle redLine = new Rectangle(110, 3);
        redLine.setFill(Color.web(ACCENT));
        redLine.setArcWidth(3);
        redLine.setArcHeight(3);

        // Tagline
        Text tagline = new Text("Stronger Every Day.\nSmarter Every Session.");
        tagline.setFont(Font.font("Poppins", 11));
        tagline.setFill(Color.web(TEXT_MUTED));
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setLineSpacing(5);

        // Info pills
        HBox locPill  = makePill("  Taguig City, Philippines");
        HBox verPill  = makePill("Management System v1.0");
        VBox pillBox  = new VBox(10, locPill, verPill);
        pillBox.setAlignment(Pos.CENTER);

        Region botSpacer = new Region();
        VBox.setVgrow(botSpacer, Priority.ALWAYS);

        // Footer text at bottom of left panel
        Text leftFooter = new Text("CS 301  Software Engineering 1  |  TIP-QC");
        leftFooter.setFont(Font.font("Poppins", 9));
        leftFooter.setFill(Color.web(TEXT_DIM));

        // Assemble left panel with spacing
        VBox leftContent = new VBox(22,
            badge, gymTitle, redLine, tagline, pillBox
        );
        leftContent.setAlignment(Pos.CENTER);

        left.getChildren().addAll(
            topBar, topSpacer, leftContent, botSpacer,
            new HBox(leftFooter) {{ setAlignment(Pos.CENTER); setPadding(new Insets(0, 0, 20, 0)); }}
        );

        // 
        // RIGHT PANEL  login form
        // 
        VBox right = new VBox();
        right.setAlignment(Pos.CENTER);
        HBox.setHgrow(right, Priority.ALWAYS);
        right.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Card
        VBox card = new VBox(16);
        card.setMaxWidth(370);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(38, 38, 38, 38));
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_LARGE + ";" +
            "-fx-border-color: " + ModernDesignSystem.BORDER_COLOR + ";" +
            "-fx-border-radius: " + ModernDesignSystem.RADIUS_LARGE + ";" +
            "-fx-border-width: 1.5;"
        );
        card.setEffect(ModernDesignSystem.createElevation4());

        // Card header
        Text title = new Text("Sign In");
        title.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 26));
        title.setFill(Color.web(TEXT_WHITE));
        Text subtitle = new Text("Enter your credentials to continue");
        subtitle.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, 11));
        subtitle.setFill(Color.web(TEXT_MUTED));
        VBox cardHeader = new VBox(4, title, subtitle);

        Rectangle accentUnderline = new Rectangle(48, 3);
        accentUnderline.setFill(Color.web(ModernDesignSystem.ACCENT_YELLOW));
        accentUnderline.setArcWidth(3);
        accentUnderline.setArcHeight(3);

        // Fields
        VBox userGroup = buildFieldGroup("USERNAME", "Enter your username", false);
        TextField usernameField = (TextField) userGroup.getChildren().get(1);

        VBox passGroup = buildFieldGroup("PASSWORD", "Enter your password", true);
        PasswordField passwordField = (PasswordField) passGroup.getChildren().get(1);

        // Forgot link
        Hyperlink forgot = new Hyperlink("Forgot Password?");
        forgot.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, 10));
        forgot.setTextFill(Color.web(ModernDesignSystem.ACCENT_YELLOW));
        forgot.setBorder(Border.EMPTY);
        forgot.setPadding(Insets.EMPTY);
        forgot.setOnMouseEntered(e -> forgot.setTextFill(Color.web(ModernDesignSystem.ACCENT_YELLOW_LT)));
        forgot.setOnMouseExited(e -> forgot.setTextFill(Color.web(ModernDesignSystem.ACCENT_YELLOW)));
        forgot.setOnAction(e -> showForgotDialog(stage));
        HBox forgotRow = new HBox(forgot);
        forgotRow.setAlignment(Pos.CENTER_RIGHT);

        // Message label
        Label msgLbl = new Label();
        msgLbl.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, 11));
        msgLbl.setTextFill(Color.web(ModernDesignSystem.PRIMARY));
        msgLbl.setWrapText(true);
        msgLbl.setMaxWidth(Double.MAX_VALUE);
        msgLbl.setVisible(false);
        msgLbl.setPadding(new Insets(8, 12, 8, 12));
        msgLbl.setStyle(
            "-fx-background-color: " + ModernDesignSystem.SUCCESS + ";" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_SMALL + ";"
        );

        // Login button
        Button loginBtn = ModernDesignSystem.createPrimaryButton("LOGIN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(46);

        // Login action
        Runnable doLogin = () -> {
            String u = usernameField.getText().trim();
            String p = passwordField.getText().trim();
            if (u.isEmpty() || p.isEmpty()) {
                showMsg(msgLbl, "Please enter both username and password.", false);
                shakeCard(card);
                return;
            }
            
            loginBtn.setDisable(true);
            msgLbl.setText("  Authenticating...");
            msgLbl.setVisible(true);
            
            // Try to authenticate with database
            new Thread(() -> {
                try {
                    UserDAO userDAO = new UserDAO();
                    // Supports both bcrypt and plain text passwords
                    Optional<UserDAO.UserRecord> user = userDAO.authenticate(u, p);
                    
                    javafx.application.Platform.runLater(() -> {
                        if (user.isPresent()) {
                            UserDAO.UserRecord ur = user.get();
                            showMsg(msgLbl, "Login successful! Loading dashboard...", true);
                            AppSession.login(AppSession.User.fromUserRecord(ur));
                            try {
                                new GymManagementApp().start(stage);
                            } catch (Exception ex) {
                                showMsg(msgLbl, "Could not load dashboard: " + ex.getMessage(), false);
                                loginBtn.setDisable(false);
                            }
                        } else {
                            showMsg(msgLbl, "Invalid username or password. Try again.", false);
                            passwordField.clear();
                            shakeCard(card);
                            loginBtn.setDisable(false);
                        }
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        showMsg(msgLbl, "Database error: " + ex.getMessage(), false);
                        loginBtn.setDisable(false);
                    });
                }
            }).start();
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // Card footer
        Text cardFooter = new Text(" 2025 MJ23 Playgrind Gym    All rights reserved");
        cardFooter.setFont(Font.font("Poppins", 9));
        cardFooter.setFill(Color.web(TEXT_DIM));
        HBox cardFooterBox = new HBox(cardFooter);
        cardFooterBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
            cardHeader,
            accentUnderline,
            userGroup,
            passGroup,
            forgotRow,
            msgLbl,
            loginBtn,
            cardFooterBox
        );

        right.getChildren().add(card);

        //  Assemble & animate 
        root.getChildren().addAll(left, right);

        card.setOpacity(0);
        card.setTranslateY(18);
        FadeTransition ft = new FadeTransition(Duration.millis(480), card);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(120));
        TranslateTransition tt = new TranslateTransition(Duration.millis(480), card);
        tt.setToY(0);
        tt.setDelay(Duration.millis(120));
        ft.play();
        tt.play();

        //  Scene 
        Scene scene = new Scene(root, 950, 620);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    //  Field group builder 
    private VBox buildFieldGroup(String label, String prompt, boolean isPass) {
        VBox g = new VBox(7);
        Label lbl = new Label(label);
        lbl.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 10));
        lbl.setTextFill(Color.web(ModernDesignSystem.TEXT_MUTED));

        TextField f = isPass ? new PasswordField() : new TextField();
        f.setPromptText(prompt);
        f.setPrefHeight(44);
        fieldStyle(f, false);
        f.focusedProperty().addListener((o, old, focused) -> fieldStyle(f, focused));
        g.getChildren().addAll(lbl, f);
        return g;
    }

    private void fieldStyle(TextField f, boolean focused) {
        f.setStyle(
            "-fx-background-color: " + ModernDesignSystem.WHITE + ";" +
            "-fx-border-color: " + (focused ? ModernDesignSystem.PRIMARY : ModernDesignSystem.BORDER_COLOR) + ";" +
            "-fx-border-radius: " + ModernDesignSystem.RADIUS_MEDIUM + ";" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_MEDIUM + ";" +
            "-fx-border-width: " + (focused ? "2" : "1.5") + ";" +
            "-fx-text-fill: " + ModernDesignSystem.DARK_GRAY + ";" +
            "-fx-prompt-text-fill: " + ModernDesignSystem.TEXT_MUTED + ";" +
            "-fx-padding: 0 14 0 14;" +
            "-fx-font-family: '" + ModernDesignSystem.FONT_FAMILY + "';" +
            "-fx-font-size: " + ModernDesignSystem.FONT_BODY + ";" +
            "-fx-effect: " + (focused ? 
                "dropshadow(gaussian, rgba(26, 19, 99, 0.12), 8, 0.0, 0, 2)" :
                "dropshadow(gaussian, rgba(26, 19, 99, 0.05), 4, 0.0, 0, 1)") + ";"
        );
    }

    private void styleBtn(Button b, boolean hovered) {
        // Method removed - use ModernDesignSystem.createPrimaryButton instead
    }

    private void showMsg(Label l, String msg, boolean success) {
        l.setText(msg);
        l.setTextFill(success ? Color.web(ModernDesignSystem.DARK_GRAY) : Color.web(ModernDesignSystem.PRIMARY));
        l.setStyle(
            "-fx-background-color: " + (success
                ? ModernDesignSystem.SUCCESS : ModernDesignSystem.HOVER_EFFECT) + ";" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_SMALL + ";"
        );
        l.setVisible(true);
    }

    private void shakeCard(javafx.scene.Node n) {
        TranslateTransition t = new TranslateTransition(Duration.millis(55), n);
        t.setByX(9);
        t.setCycleCount(6);
        t.setAutoReverse(true);
        t.setOnFinished(e -> n.setTranslateX(0));
        t.play();
    }

    private HBox makePill(String text) {
        HBox pill = new HBox();
        pill.setAlignment(Pos.CENTER);
        pill.setPadding(new Insets(6, 16, 6, 16));
        pill.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + FIELD_BORDER + ";" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;"
        );
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Poppins", 10));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        pill.getChildren().add(lbl);
        return pill;
    }

    private void showForgotDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.setTitle("Account Recovery");
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + BG_CARD + ";");

        Text title = new Text("Forgot Username or Password");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));

        Text subtitle = new Text("Verify your identity using your registered email or phone number.");
        subtitle.setFont(Font.font("Poppins", 11));
        subtitle.setFill(Color.web(TEXT_MUTED));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            new Tab("Username Recovery", buildUsernameRecoveryPane()),
            new Tab("Password Recovery", buildPasswordRecoveryPane())
        );
        tabs.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-control-inner-background: " + BG_MAIN + ";"
        );

        Button close = new Button("CLOSE");
        close.setPrefHeight(38);
        close.setMaxWidth(Double.MAX_VALUE);
        styleSecondaryButton(close, false);
        close.setOnMouseEntered(e -> styleSecondaryButton(close, true));
        close.setOnMouseExited(e -> styleSecondaryButton(close, false));
        close.setOnAction(e -> dialog.close());

        root.getChildren().addAll(title, subtitle, tabs, close);
        Scene scene = new Scene(root, 520, 440);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private VBox buildUsernameRecoveryPane() {
        VBox pane = recoveryPane();
        TextField identityField = buildRecoveryField("Registered email or phone");
        Label message = buildRecoveryMessage();

        Button recover = buildRecoveryButton("RECOVER USERNAME");
        recover.setOnAction(e -> {
            String identity = identityField.getText().trim();
            if (identity.isEmpty()) {
                setRecoveryMessage(message, "Enter your registered email or phone number.", false);
                return;
            }

            recover.setDisable(true);
            setRecoveryMessage(message, "Verifying identity...", true);
            new Thread(() -> {
                Optional<UserDAO.UserRecord> user = new UserDAO().findUsernameByRecoveryIdentity(identity);
                javafx.application.Platform.runLater(() -> {
                    if (user.isPresent()) {
                        setRecoveryMessage(message, "Your username is: " + user.get().username(), true);
                    } else {
                        setRecoveryMessage(message, "No active account matched that email or phone.", false);
                    }
                    recover.setDisable(false);
                });
            }).start();
        });

        pane.getChildren().addAll(
            recoveryHint("Submodule 1.1 - confirms identity before showing the account username."),
            identityField,
            message,
            recover
        );
        return pane;
    }

    private VBox buildPasswordRecoveryPane() {
        VBox pane = recoveryPane();
        TextField usernameField = buildRecoveryField("Username");
        TextField identityField = buildRecoveryField("Registered email or phone");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");
        fieldStyle(newPasswordField, false);
        newPasswordField.focusedProperty().addListener((o, old, focused) -> fieldStyle(newPasswordField, focused));

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        fieldStyle(confirmPasswordField, false);
        confirmPasswordField.focusedProperty().addListener((o, old, focused) -> fieldStyle(confirmPasswordField, focused));

        Label message = buildRecoveryMessage();
        Button reset = buildRecoveryButton("RESET PASSWORD");
        reset.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String identity = identityField.getText().trim();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.isEmpty() || identity.isEmpty() || newPassword.isEmpty()) {
                setRecoveryMessage(message, "Username, identity, and new password are required.", false);
                return;
            }
            if (newPassword.length() < 6) {
                setRecoveryMessage(message, "Password must be at least 6 characters.", false);
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                setRecoveryMessage(message, "New password and confirmation do not match.", false);
                return;
            }

            reset.setDisable(true);
            setRecoveryMessage(message, "Verifying identity and resetting password...", true);
            new Thread(() -> {
                UserDAO userDAO = new UserDAO();
                Optional<UserDAO.UserRecord> user = userDAO.findActiveByRecoveryIdentity(username, identity);
                boolean changed = user.isPresent()
                    && userDAO.resetPasswordAfterRecovery(user.get().userId(), newPassword);

                javafx.application.Platform.runLater(() -> {
                    if (changed) {
                        newPasswordField.clear();
                        confirmPasswordField.clear();
                        setRecoveryMessage(message, "Password reset successful. You can now sign in.", true);
                    } else {
                        setRecoveryMessage(message, "Could not verify that username with the given email or phone.", false);
                    }
                    reset.setDisable(false);
                });
            }).start();
        });

        pane.getChildren().addAll(
            recoveryHint("Submodule 1.2 - verifies identity before allowing a password reset."),
            usernameField,
            identityField,
            newPasswordField,
            confirmPasswordField,
            message,
            reset
        );
        return pane;
    }

    private VBox recoveryPane() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(18, 0, 0, 0));
        pane.setStyle("-fx-background-color: " + BG_CARD + ";");
        return pane;
    }

    private Text recoveryHint(String text) {
        Text hint = new Text(text);
        hint.setFont(Font.font("Poppins", 10));
        hint.setFill(Color.web(TEXT_MUTED));
        return hint;
    }

    private TextField buildRecoveryField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(42);
        fieldStyle(field, false);
        field.focusedProperty().addListener((o, old, focused) -> fieldStyle(field, focused));
        return field;
    }

    private Label buildRecoveryMessage() {
        Label message = new Label();
        message.setFont(Font.font("Poppins", 11));
        message.setWrapText(true);
        message.setVisible(false);
        message.setMaxWidth(Double.MAX_VALUE);
        message.setPadding(new Insets(8, 12, 8, 12));
        return message;
    }

    private Button buildRecoveryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(42);
        button.setMaxWidth(Double.MAX_VALUE);
        styleBtn(button, false);
        button.setOnMouseEntered(e -> styleBtn(button, true));
        button.setOnMouseExited(e -> styleBtn(button, false));
        return button;
    }

    private void styleSecondaryButton(Button button, boolean hovered) {
        button.setStyle(
            "-fx-background-color: " + (hovered ? FIELD_BORDER : FIELD_BG) + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-border-color: " + FIELD_BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
    }

    private void setRecoveryMessage(Label label, String text, boolean success) {
        label.setText(text);
        label.setTextFill(success ? Color.web("#4B4B4B") : Color.web(ACCENT));
        label.setStyle(
            "-fx-background-color: " + (success
                ? "rgba(228,255,223,0.12)" : "rgba(26,19,99,0.12)") + ";" +
            "-fx-background-radius: 7;"
        );
        label.setVisible(true);
    }

    public static void main(String[] args) { launch(args); }
}



