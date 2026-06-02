package mj23gym.ui;

import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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
import mj23gym.dao.ActivityLogDAO;
import mj23gym.dao.UserDAO;
import mj23gym.util.DatabaseConnection;

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
    private static final String LOGO_PATH     = "/images/mj23-logo.png";
    private static final String GYM_PHOTO_PATH = "/images/gym-mj23-place.jpg";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Management System");

        //  Root: left branding panel + right form panel 
        HBox root = new HBox();
        root.setPrefSize(950, 620);

        // 
        // LEFT PANEL  dark branding side
        // 
        StackPane left = new StackPane();
        left.setPrefWidth(390);
        left.setMinWidth(390);
        left.setMaxWidth(390);
        left.setStyle("-fx-background-color: " + BG_DARKER + ";");

        ImageView sidePhoto = new ImageView(loadImage(GYM_PHOTO_PATH));
        sidePhoto.setPreserveRatio(false);
        sidePhoto.setFitWidth(390);
        sidePhoto.fitHeightProperty().bind(left.heightProperty());
        sidePhoto.setOpacity(0.11);
        sidePhoto.setEffect(new GaussianBlur(3));
        sidePhoto.setMouseTransparent(true);

        Rectangle sideWash = new Rectangle();
        sideWash.setWidth(390);
        sideWash.heightProperty().bind(left.heightProperty());
        sideWash.setFill(Color.web("#F7F6FC", 0.91));
        sideWash.setMouseTransparent(true);

        // Spacer so content is vertically centered
        Region topSpacer = new Region();
        topSpacer.setPrefHeight(82);

        // Logo badge
        StackPane badge = new StackPane();
        badge.setPrefSize(148, 148);
        badge.setMaxSize(148, 148);
        Rectangle badgeBg = new Rectangle(148, 148);
        badgeBg.setArcWidth(ModernDesignSystem.RADIUS_LARGE);
        badgeBg.setArcHeight(ModernDesignSystem.RADIUS_LARGE);
        badgeBg.setFill(Color.web(ModernDesignSystem.WHITE));
        badgeBg.setStroke(Color.web(ModernDesignSystem.BORDER_COLOR));
        badgeBg.setEffect(ModernDesignSystem.createElevation4());
        ImageView logoView = new ImageView(loadImage(LOGO_PATH));
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(126);
        logoView.setFitHeight(126);
        badge.getChildren().addAll(badgeBg, logoView);

        // Gym title
        Text line1 = new Text("MJ23 PLAYGRIND");
        line1.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        line1.setFill(Color.web(TEXT_WHITE));
        line1.setStroke(Color.web(ModernDesignSystem.WHITE, 0.80));
        line1.setStrokeWidth(0.25);
        Text line2 = new Text("GYM");
        line2.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        line2.setFill(Color.web(ACCENT));
        line2.setStroke(Color.web(ModernDesignSystem.WHITE, 0.80));
        line2.setStrokeWidth(0.25);
        VBox gymTitle = new VBox(0, line1, line2);
        gymTitle.setAlignment(Pos.CENTER);

        Rectangle redLine = new Rectangle(112, 3);
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

        Text footer = new Text("Secure staff access for MJ23 operations");
        footer.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        footer.setFill(Color.web(TEXT_MUTED));
        footer.setTextAlignment(TextAlignment.CENTER);

        // Assemble left panel with spacing
        VBox leftContent = new VBox(22,
            badge, gymTitle, redLine, tagline, pillBox
        );
        leftContent.setAlignment(Pos.CENTER);
        leftContent.setPadding(new Insets(0, 28, 0, 28));

        VBox leftShell = new VBox(0, topSpacer, leftContent, botSpacer, footer);
        leftShell.setAlignment(Pos.CENTER);
        leftShell.setPadding(new Insets(42, 0, 28, 0));
        StackPane.setAlignment(leftShell, Pos.CENTER);

        left.getChildren().addAll(sidePhoto, sideWash, leftShell);

        // 
        // RIGHT PANEL  login form
        // 
        StackPane right = new StackPane();
        right.setAlignment(Pos.CENTER);
        HBox.setHgrow(right, Priority.ALWAYS);
        right.setStyle("-fx-background-color: " + BG_MAIN + ";");

        ImageView gymPhoto = new ImageView(loadImage(GYM_PHOTO_PATH));
        gymPhoto.setPreserveRatio(true);
        gymPhoto.fitWidthProperty().bind(right.widthProperty());
        gymPhoto.fitHeightProperty().bind(right.heightProperty());
        gymPhoto.setOpacity(0.42);
        gymPhoto.setEffect(new GaussianBlur(5));
        gymPhoto.setMouseTransparent(true);
        gymPhoto.setManaged(false);
        StackPane.setAlignment(gymPhoto, Pos.CENTER);

        Rectangle photoWash = new Rectangle();
        photoWash.widthProperty().bind(right.widthProperty());
        photoWash.heightProperty().bind(right.heightProperty());
        photoWash.setFill(Color.web(BG_MAIN, 0.58));
        photoWash.setMouseTransparent(true);
        photoWash.setManaged(false);

        // Card
        VBox card = new VBox(16);
        card.setPrefWidth(330);
        card.setMaxWidth(330);
        card.setMinHeight(390);
        card.setMaxHeight(420);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(28, 30, 24, 30));
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.98);" +
            "-fx-background-radius: " + ModernDesignSystem.RADIUS_LARGE + ";" +
            "-fx-border-color: " + ModernDesignSystem.BORDER_COLOR + ";" +
            "-fx-border-radius: " + ModernDesignSystem.RADIUS_LARGE + ";" +
            "-fx-border-width: 1.5;"
        );
        card.setEffect(ModernDesignSystem.createElevation4());

        // Card header
        Text title = new Text("Sign In");
        title.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 23));
        title.setFill(Color.web(TEXT_WHITE));
        title.setStroke(Color.web(ModernDesignSystem.WHITE, 0.85));
        title.setStrokeWidth(0.28);
        Text subtitle = new Text("Enter your credentials to continue");
        subtitle.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, 11));
        subtitle.setFill(Color.web("#4B4B4B"));
        VBox cardHeader = new VBox(4, title, subtitle);

        // Fields
        VBox userGroup = buildFieldGroup("USERNAME", "Enter your username", false);
        TextField usernameField = (TextField) userGroup.getChildren().get(1);

        VBox passGroup = buildFieldGroup("PASSWORD", "Enter your password", true);
        PasswordField passwordField = (PasswordField) passGroup.getChildren().get(1);

        // Forgot link
        Hyperlink forgot = new Hyperlink("Forgot Password?");
        forgot.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, 10));
        forgot.setTextFill(Color.web("#6E6400"));
        forgot.setBorder(Border.EMPTY);
        forgot.setPadding(Insets.EMPTY);
        forgot.setOnMouseEntered(e -> forgot.setTextFill(Color.web(ModernDesignSystem.PRIMARY)));
        forgot.setOnMouseExited(e -> forgot.setTextFill(Color.web("#6E6400")));
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
        loginBtn.setPrefHeight(42);

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
                    if (!DatabaseConnection.isConnected()) {
                        javafx.application.Platform.runLater(() -> {
                            showMsg(msgLbl, "Cannot connect to database. Set MJ23_DB_USER and MJ23_DB_PASSWORD, then restart the app.", false);
                            loginBtn.setDisable(false);
                        });
                        return;
                    }
                    UserDAO userDAO = new UserDAO();
                    // Supports both bcrypt and plain text passwords
                    Optional<UserDAO.UserRecord> user = userDAO.authenticate(u, p);
                    
                    javafx.application.Platform.runLater(() -> {
                        if (user.isPresent()) {
                            UserDAO.UserRecord ur = user.get();
                            showMsg(msgLbl, "Login successful! Loading dashboard...", true);
                            AppSession.login(AppSession.User.fromUserRecord(ur));
                            new ActivityLogDAO().logSession(ur.userId(), ur.role(), "LOGIN");
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
        cardFooter.setStroke(Color.web(ModernDesignSystem.WHITE, 0.70));
        cardFooter.setStrokeWidth(0.18);
        HBox cardFooterBox = new HBox(cardFooter);
        cardFooterBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
            cardHeader,
            userGroup,
            passGroup,
            forgotRow,
            msgLbl,
            loginBtn,
            cardFooterBox
        );

        right.getChildren().addAll(gymPhoto, photoWash, card);
        card.toFront();

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
        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setResizable(true);
        stage.setFullScreen(false);
        stage.setMaximized(true);
        stage.show();
    }

    //  Field group builder 
    private VBox buildFieldGroup(String label, String prompt, boolean isPass) {
        VBox g = new VBox(7);
        Label lbl = new Label(label);
        lbl.setFont(Font.font(ModernDesignSystem.FONT_FAMILY, FontWeight.BOLD, 10));
        lbl.setTextFill(Color.web(ModernDesignSystem.PRIMARY));

        TextField f = isPass ? new PasswordField() : new TextField();
        f.setPromptText(prompt);
        f.setPrefHeight(40);
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
        boolean disabled = b.isDisabled();
        String bg = disabled
            ? "#E6E6EA"
            : (hovered ? ModernDesignSystem.PRIMARY_DARK : ModernDesignSystem.PRIMARY);
        String border = disabled ? "#D5D4DD" : ModernDesignSystem.PRIMARY;
        String text = disabled ? ModernDesignSystem.TEXT_MUTED : ModernDesignSystem.WHITE;
        String shadow = disabled
            ? "dropshadow(gaussian, rgba(26,19,99,0.04), 3, 0.0, 0, 1)"
            : "dropshadow(gaussian, rgba(26,19,99,0.18), 8, 0.0, 0, 2)";
        b.setOpacity(1.0);
        b.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + text + ";" +
            "-fx-font-family: '" + ModernDesignSystem.FONT_FAMILY + "';" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: " + (disabled ? "default" : "hand") + ";" +
            "-fx-effect: " + shadow + ";"
        );
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

    private Image loadImage(String resourcePath) {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            return new WritableImage(1, 1);
        }
        return new Image(resource.toExternalForm());
    }

    private HBox makePill(String text) {
        HBox pill = new HBox();
        pill.setAlignment(Pos.CENTER);
        pill.setPrefWidth(260);
        pill.setMaxWidth(260);
        pill.setPadding(new Insets(8, 18, 8, 18));
        pill.setStyle(
            "-fx-background-color: rgba(255,255,255,0.86);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(26,19,99,0.13);" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(26,19,99,0.05), 5, 0.0, 0, 1);"
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

        VBox root = new VBox(18);
        root.setPadding(new Insets(26));
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #FFFFFF, #F5F4FA);" +
            "-fx-border-color: " + ModernDesignSystem.BORDER_COLOR + ";" +
            "-fx-border-width: 1;"
        );

        Text title = new Text("Account Recovery");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        title.setFill(Color.web(TEXT_WHITE));

        Text subtitle = new Text("Verify your identity before recovering your username or changing your password.");
        subtitle.setFont(Font.font("Poppins", 11));
        subtitle.setFill(Color.web(TEXT_MUTED));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            new Tab("Username Recovery", buildUsernameRecoveryPane()),
            new Tab("Password Recovery", buildPasswordRecoveryPane())
        );
        tabs.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-control-inner-background: " + BG_MAIN + ";"
        );
        tabs.getStyleClass().add("recovery-tabs");

        Button close = new Button("CLOSE");
        close.setPrefHeight(38);
        close.setMaxWidth(Double.MAX_VALUE);
        styleSecondaryButton(close, false);
        close.setOnMouseEntered(e -> styleSecondaryButton(close, true));
        close.setOnMouseExited(e -> styleSecondaryButton(close, false));
        close.setOnAction(e -> dialog.close());

        root.getChildren().addAll(new VBox(4, title, subtitle), tabs, close);
        Scene scene = new Scene(root, 620, 620);
        scene.getStylesheets().add("data:text/css," + recoveryDialogCss().replace("\n", ""));
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private String recoveryDialogCss() {
        return """
            .recovery-tabs {
                -fx-tab-min-height: 42px;
                -fx-tab-max-height: 42px;
                -fx-background-color: transparent;
            }
            .recovery-tabs .tab-header-area {
                -fx-padding: 0 0 12 0;
            }
            .recovery-tabs .tab-header-background {
                -fx-background-color: rgba(26,19,99,0.07);
                -fx-background-radius: 16;
                -fx-border-color: rgba(26,19,99,0.12);
                -fx-border-radius: 16;
            }
            .recovery-tabs .headers-region {
                -fx-padding: 4;
            }
            .recovery-tabs .tab {
                -fx-background-color: transparent;
                -fx-background-radius: 12;
                -fx-border-color: transparent;
                -fx-padding: 0 18 0 18;
                -fx-focus-color: transparent;
                -fx-faint-focus-color: transparent;
            }
            .recovery-tabs .tab:selected {
                -fx-background-color: #1A1363;
                -fx-effect: dropshadow(gaussian, rgba(26,19,99,0.20), 8, 0, 0, 2);
            }
            .recovery-tabs .tab-label {
                -fx-font-family: Poppins;
                -fx-font-size: 12px;
                -fx-font-weight: bold;
                -fx-text-fill: #77749B;
            }
            .recovery-tabs .tab:selected .tab-label {
                -fx-text-fill: white;
            }
            .recovery-tabs .tab-content-area {
                -fx-background-color: transparent;
                -fx-padding: 0;
            }
            .recovery-tabs:focused .tab:selected .focus-indicator {
                -fx-border-color: transparent;
            }
            """;
    }

    private VBox buildUsernameRecoveryPane() {
        VBox pane = recoveryPane();
        TextField emailField = buildRecoveryField("Registered email");
        TextField phoneField = buildRecoveryField("Registered phone number");
        ComboBox<String> questionBox = buildRecoveryQuestionBox();
        PasswordField answerField = buildRecoveryPasswordField("Security answer");
        Label message = buildRecoveryMessage();

        Button recover = buildRecoveryButton("RECOVER USERNAME");
        wireRecoveryButtonState(recover, questionBox, emailField, phoneField, answerField);
        recover.setOnAction(e -> {
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String question = questionBox.getValue();
            String answer = answerField.getText().trim();
            if (email.isEmpty() || phone.isEmpty() || question == null || answer.isEmpty()) {
                setRecoveryMessage(message, "Email, phone, security question, and answer are required.", false);
                return;
            }

            recover.setDisable(true);
            styleBtn(recover, false);
            setRecoveryMessage(message, "Checking account recovery details...", true);
            new Thread(() -> {
                UserDAO userDAO = new UserDAO();
                Optional<UserDAO.UserRecord> user = userDAO.recoverUsername(email, phone, question, answer);
                javafx.application.Platform.runLater(() -> {
                    if (user.isPresent()) {
                        setRecoveryMessage(message, "Username found: " + user.get().username(), true);
                    } else {
                        setRecoveryMessage(message, "Recovery details did not match an active account.", false);
                    }
                    recover.setDisable(false);
                    styleBtn(recover, false);
                });
            }).start();
        });

        pane.getChildren().addAll(
            recoveryHint("Use your registered email, phone, and recovery answer."),
            emailField,
            phoneField,
            questionBox,
            answerField,
            message,
            recover
        );
        return pane;
    }

    private VBox buildPasswordRecoveryPane() {
        VBox pane = recoveryPane();
        TextField usernameField = buildRecoveryField("Username");
        TextField emailField = buildRecoveryField("Registered email");
        TextField phoneField = buildRecoveryField("Registered phone number");
        ComboBox<String> questionBox = buildRecoveryQuestionBox();
        PasswordField answerField = buildRecoveryPasswordField("Security answer");
        PasswordField newPasswordField = buildRecoveryPasswordField("New password");
        PasswordField confirmPasswordField = buildRecoveryPasswordField("Confirm new password");
        setRecoveryPasswordStepVisible(false, newPasswordField, confirmPasswordField);

        Label message = buildRecoveryMessage();
        Button verify = buildRecoveryButton("VERIFY RECOVERY DETAILS");
        wireRecoveryButtonState(verify, questionBox, usernameField, emailField, phoneField, answerField);
        Button reset = buildRecoveryButton("SAVE NEW PASSWORD");
        reset.setVisible(false);
        reset.setManaged(false);

        final String[] verifiedUsername = {""};
        final String[] verifiedEmail = {""};
        final String[] verifiedPhone = {""};
        final String[] verifiedQuestion = {""};
        final String[] verifiedAnswer = {""};

        verify.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String question = questionBox.getValue();
            String answer = answerField.getText().trim();

            if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || question == null || answer.isEmpty()) {
                setRecoveryMessage(message, "Complete username, email, phone, security question, and answer.", false);
                return;
            }

            verify.setDisable(true);
            styleBtn(verify, false);
            setRecoveryMessage(message, "Verifying recovery details...", true);
            new Thread(() -> {
                UserDAO userDAO = new UserDAO();
                Optional<UserDAO.UserRecord> user = userDAO.verifyPasswordRecoveryIdentity(
                    username, email, phone, question, answer
                );

                javafx.application.Platform.runLater(() -> {
                    if (user.isPresent()) {
                        verifiedUsername[0] = username;
                        verifiedEmail[0] = email;
                        verifiedPhone[0] = phone;
                        verifiedQuestion[0] = question;
                        verifiedAnswer[0] = answer;

                        usernameField.setDisable(true);
                        emailField.setDisable(true);
                        phoneField.setDisable(true);
                        questionBox.setDisable(true);
                        answerField.setDisable(true);
                        verify.setVisible(false);
                        verify.setManaged(false);
                        setRecoveryPasswordStepVisible(true, newPasswordField, confirmPasswordField);
                        reset.setVisible(true);
                        reset.setManaged(true);
                        setRecoveryMessage(message, "Identity confirmed. Enter and confirm your new password.", true);
                    } else {
                        setRecoveryMessage(message, "Recovery details did not match an active account.", false);
                        verify.setDisable(false);
                        styleBtn(verify, false);
                    }
                });
            }).start();
        });

        reset.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (verifiedUsername[0].isEmpty()) {
                setRecoveryMessage(message, "Verify recovery details before setting a new password.", false);
                return;
            }
            if (newPassword == null || newPassword.length() < 6) {
                setRecoveryMessage(message, "New password must be at least 6 characters.", false);
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                setRecoveryMessage(message, "New password and confirmation do not match.", false);
                return;
            }

            reset.setDisable(true);
            styleBtn(reset, false);
            setRecoveryMessage(message, "Verifying identity and saving new password...", true);
            new Thread(() -> {
                UserDAO userDAO = new UserDAO();
                boolean saved = userDAO.resetPasswordWithRecovery(
                    verifiedUsername[0],
                    verifiedEmail[0],
                    verifiedPhone[0],
                    verifiedQuestion[0],
                    verifiedAnswer[0],
                    newPassword
                );

                javafx.application.Platform.runLater(() -> {
                    if (saved) {
                        setRecoveryMessage(message, "Password updated. You can now sign in with the new password.", true);
                        newPasswordField.clear();
                        confirmPasswordField.clear();
                        reset.setDisable(true);
                        styleBtn(reset, false);
                    } else {
                        setRecoveryMessage(message, "Could not save the new password. Verify details again.", false);
                        reset.setDisable(false);
                        styleBtn(reset, false);
                    }
                });
            }).start();
        });

        pane.getChildren().addAll(
            recoveryHint("Step 1: verify account details. Step 2: set the new password."),
            usernameField,
            emailField,
            phoneField,
            questionBox,
            answerField,
            verify,
            newPasswordField,
            confirmPasswordField,
            message,
            reset
        );
        return pane;
    }

    private VBox recoveryPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(18, 4, 6, 4));
        pane.setStyle("-fx-background-color: transparent;");
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

    private PasswordField buildRecoveryPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefHeight(42);
        fieldStyle(field, false);
        field.focusedProperty().addListener((o, old, focused) -> fieldStyle(field, focused));
        return field;
    }

    private void setRecoveryPasswordStepVisible(boolean visible, PasswordField... fields) {
        for (PasswordField field : fields) {
            field.setVisible(visible);
            field.setManaged(visible);
        }
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
            "-fx-background-color: " + FIELD_BG + ";" +
            "-fx-border-color: " + ModernDesignSystem.BORDER_COLOR + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;"
        );
        return box;
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

    private void wireRecoveryButtonState(Button button, ComboBox<String> questionBox, TextField... fields) {
        Runnable refresh = () -> {
            boolean complete = questionBox.getValue() != null && !questionBox.getValue().isBlank();
            for (TextField field : fields) {
                complete = complete && field.getText() != null && !field.getText().trim().isEmpty();
            }
            button.setDisable(!complete);
            styleBtn(button, false);
        };

        for (TextField field : fields) {
            field.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        }
        questionBox.valueProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        refresh.run();
    }

    private void styleSecondaryButton(Button button, boolean hovered) {
        String bg = hovered ? "rgba(26,19,99,0.10)" : ModernDesignSystem.WHITE;
        String border = hovered ? ModernDesignSystem.PRIMARY : "rgba(26,19,99,0.22)";
        String text = hovered ? ModernDesignSystem.PRIMARY : ModernDesignSystem.DARK_GRAY;
        button.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + text + ";" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(26,19,99,0.08), 6, 0.0, 0, 2);"
        );
    }

    private void setRecoveryMessage(Label label, String text, boolean success) {
        label.setText(text);
        label.setTextFill(success ? Color.web("#4B4B4B") : Color.web(ACCENT));
        label.setStyle(
            "-fx-background-color: " + (success
                ? "rgba(228,255,223,0.80)" : "rgba(230,57,70,0.12)") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + (success ? ModernDesignSystem.SUCCESS : ACCENT) + ";" +
            "-fx-border-radius: 10;"
        );
        label.setVisible(true);
    }

    public static void main(String[] args) { launch(args); }
}
