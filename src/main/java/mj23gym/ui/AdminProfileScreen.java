package mj23gym.ui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import mj23gym.dao.ProfileActivityDAO;
import mj23gym.dao.UserDAO;

/**
 * MJ23 Playgrind Gym - Profile Screen
 * Displays the logged-in user's account info, editable profile fields,
 * and password management.
 */
public class AdminProfileScreen extends Application {

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
    static final String WARNING     = "#8A6D00";
    static final String INFO        = "#1A1363";
    static final String TEXT_TITLE  = ModernDesignSystem.PRIMARY;
    static final String TEXT_SOFT   = ModernDesignSystem.TEXT_MUTED;
    static final String SUCCESS_TEXT = "#237A36";
    static final String WARNING_TEXT = "#6E6400";
    static final String CARD_SURFACE = ModernDesignSystem.WHITE;
    static final String BRAND_YELLOW = "#FDEE21";

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym - Profile");
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
        StackPane badge = new StackPane(); badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42); bb.setArcWidth(10); bb.setArcHeight(10); bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ"); bt.setFont(Font.font("Poppins", FontWeight.BOLD, 16)); bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND"); l1.setFont(Font.font("Poppins", FontWeight.BOLD, 11)); l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM"); l2.setFont(Font.font("Poppins", FontWeight.BOLD, 11)); l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(badge, lt);

        String[][] items = {
            {"","Dashboard"},{"","Member Management"},{"","Payment & Billing"},
            {"","Inventory"},{"","Equipment"},{"","Point of Sale"},{"","Reports"}
        };
        VBox menu = new VBox(2); menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], false));

        String[][] sys = {{"","Settings"},{"","Help"},{"","About"}};
        VBox sysMenu = new VBox(2); sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys)
            sysMenu.getChildren().add(buildMenuItem(it[0], it[1], false));

        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        Rectangle d1 = new Rectangle(230, 1); d1.setFill(Color.web(BORDER));
        Rectangle d2 = new Rectangle(230, 1); d2.setFill(Color.web(BORDER));

        // Active user at bottom  highlighted since this IS the profile
        HBox userBox = new HBox(12);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(16, 16, 20, 16));
        userBox.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + " transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );
        AppSession.User user = AppSession.currentUser();
        Circle avatar = new Circle(18); avatar.setFill(Color.web(ACCENT));
        Text avTxt = new Text(user.initial()); avTxt.setFont(Font.font("Poppins", FontWeight.BOLD, 13)); avTxt.setFill(Color.WHITE);
        StackPane avStack = new StackPane(avatar, avTxt); avStack.setPrefSize(36, 36);
        VBox userInfo = new VBox(2);
        Text uName = new Text(user.displayName()); uName.setFont(Font.font("Poppins", FontWeight.BOLD, 12)); uName.setFill(Color.web(TEXT_WHITE));
        Text uRole = new Text(user.role()); uRole.setFont(Font.font("Poppins", 10)); uRole.setFill(Color.web(ACCENT));
        userInfo.getChildren().addAll(uName, uRole);
        HBox.setHgrow(userInfo, Priority.ALWAYS);
        userBox.getChildren().addAll(avStack, userInfo);

        sidebar.getChildren().addAll(
            topAccent, logoArea, d1, makeSecLbl("MAIN MENU"), menu,
            d2, makeSecLbl("SYSTEM"), sysMenu, sp, userBox
        );
        return sidebar;
    }

    private HBox buildMenuItem(String icon, String label, boolean active) {
        HBox item = new HBox(12); item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(11, 16, 11, 16)); item.setCursor(javafx.scene.Cursor.HAND);
        Rectangle bar = new Rectangle(3, 36); bar.setArcWidth(3); bar.setArcHeight(3);
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
        Label l = new Label(t); l.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_DIM)); l.setPadding(new Insets(8, 0, 6, 20)); return l;
    }

    // 
    // MAIN CONTENT
    // 
    public VBox buildContent() {
        AppSession.User user = AppSession.currentUser();

        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");
        content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Top bar
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text t1 = new Text("Profile");
        t1.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        t1.setFill(Color.web(TEXT_TITLE));
        Text t2 = new Text("Manage your account details and password");
        t2.setFont(Font.font("Poppins", 11));
        t2.setFill(Color.web(TEXT_SOFT));
        pg.getChildren().addAll(t1, t2);
        topBar.getChildren().add(pg);

        // Scrollable body
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox body = new VBox(22);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");
        body.setFillWidth(true);
        body.setMaxWidth(Double.MAX_VALUE);

        // Profile header card
        VBox profileHeaderCard = new VBox(0);
        profileHeaderCard.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: rgba(26,19,99,0.14);" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1.2;"
        );
        DropShadow ds1 = new DropShadow();
        ds1.setColor(Color.web(ACCENT, 0.08));
        ds1.setRadius(10);
        ds1.setOffsetY(3);
        profileHeaderCard.setEffect(ds1);

        Region banner = new Region();
        banner.setPrefHeight(52);
        banner.setMinHeight(52);
        banner.setMaxHeight(52);
        banner.setMaxWidth(Double.MAX_VALUE);
        banner.setStyle(
            "-fx-background-color: linear-gradient(to right, rgba(26,19,99,0.10), rgba(228,255,223,0.55));" +
            "-fx-background-radius: 18 18 0 0;"
        );

        HBox profileInfo = new HBox(22);
        profileInfo.setPadding(new Insets(0, 28, 24, 28));
        profileInfo.setAlignment(Pos.BOTTOM_LEFT);

        StackPane bigAvatar = new StackPane();
        bigAvatar.setPrefSize(84, 84);
        bigAvatar.setMaxSize(84, 84);
        bigAvatar.setTranslateY(-24);
        Circle ring = new Circle(42);
        ring.setFill(Color.web(CARD_SURFACE));
        ring.setStroke(Color.web(CARD_SURFACE));
        ring.setStrokeWidth(3);
        Circle bigCircle = new Circle(38);
        bigCircle.setFill(Color.web(ACCENT));
        Text bigInitial = new Text(user.initial());
        bigInitial.setFont(Font.font("Poppins", FontWeight.BOLD, 32));
        bigInitial.setFill(Color.WHITE);
        bigAvatar.getChildren().addAll(ring, bigCircle, bigInitial);

        VBox nameInfo = new VBox(6);
        nameInfo.setTranslateY(-8);
        Text adminName = new Text(user.displayName());
        adminName.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        adminName.setFill(Color.web(TEXT_TITLE));
        HBox badges = new HBox(8);
        Label roleBadge = pillBadge(user.role(), TEXT_TITLE, "rgba(26,19,99,0.10)");
        Label statusBadge = pillBadge("Active", SUCCESS_TEXT, "rgba(228,255,223,0.75)");
        badges.getChildren().addAll(roleBadge, statusBadge);
        Text lastLogin = new Text(user.lastLoginText());
        lastLogin.setFont(Font.font("Poppins", 10));
        lastLogin.setFill(Color.web(TEXT_SOFT));
        nameInfo.getChildren().addAll(adminName, badges, lastLogin);

        Region nameSp = new Region();
        HBox.setHgrow(nameSp, Priority.ALWAYS);

        Button editProfileBtn = outlineButton("Edit Profile");
        editProfileBtn.setTranslateY(-8);

        profileInfo.getChildren().addAll(bigAvatar, nameInfo, nameSp, editProfileBtn);
        profileHeaderCard.getChildren().addAll(banner, profileInfo);

        //  Two-column layout 
        HBox twoCol = new HBox(22);
        twoCol.setAlignment(Pos.TOP_LEFT);
        twoCol.setMaxWidth(Double.MAX_VALUE);

        // LEFT column
        VBox leftCol = new VBox(22);
        leftCol.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Profile Details Card
        VBox detailsCard = buildSectionCard("Profile Information", "Your personal account details");
        GridPane detailsForm = new GridPane();
        detailsForm.setHgap(16); detailsForm.setVgap(14);
        ColumnConstraints dc1 = new ColumnConstraints(); dc1.setPercentWidth(50);
        ColumnConstraints dc2 = new ColumnConstraints(); dc2.setPercentWidth(50);
        detailsForm.getColumnConstraints().addAll(dc1, dc2);

        TextField firstNameTf = profileField(user.firstName(), false);
        TextField lastNameTf = profileField(user.lastName(), false);
        TextField usernameTf = profileField(user.username(), true);
        TextField emailTf = profileField(user.email(), false);
        TextField phoneTf = profileField(user.phone(), false);
        TextField positionTf = profileField(user.position(), true);

        detailsForm.add(fieldGroup("FIRST NAME", firstNameTf), 0, 0);
        detailsForm.add(fieldGroup("LAST NAME", lastNameTf), 1, 0);
        detailsForm.add(fieldGroup("USERNAME", usernameTf), 0, 1);
        detailsForm.add(fieldGroup("EMAIL ADDRESS", emailTf), 1, 1);
        detailsForm.add(fieldGroup("PHONE NUMBER", phoneTf), 0, 2);
        detailsForm.add(fieldGroup("POSITION", positionTf), 1, 2);

        HBox saveBtn = new HBox(); saveBtn.setAlignment(Pos.CENTER_RIGHT);
        saveBtn.getChildren().add(makeAccentBtn("  Save Changes"));
        saveBtn.getChildren().clear();
        Button saveProfile = makeAccentBtn("Save Changes");
        saveProfile.setOnAction(e -> saveProfileChanges(firstNameTf, lastNameTf, emailTf, phoneTf, adminName));
        saveBtn.getChildren().add(saveProfile);
        detailsCard.getChildren().addAll(detailsForm, saveBtn);
        detailsCard.setManaged(false);
        detailsCard.setVisible(false);
        leftCol.getChildren().add(detailsCard);

        // Account Stats Card
        VBox statsCard = buildSectionCard("Account Activity", "Your system usage statistics");
        ProfileActivityDAO.ProfileActivityStats activityStats =
            new ProfileActivityDAO().todayForUser(user.userId());
        HBox statsRow = new HBox(14);
        statsRow.getChildren().addAll(
            makeMiniStat("Sessions Today", String.valueOf(activityStats.sessionsToday()), TEXT_TITLE),
            makeMiniStat("Members Added", String.valueOf(activityStats.membersAddedToday()), SUCCESS_TEXT),
            makeMiniStat("Payments Processed", String.valueOf(activityStats.paymentsProcessedToday()), WARNING_TEXT),
            makeMiniStat("Reports Generated", String.valueOf(activityStats.reportsGeneratedToday()), TEXT_TITLE)
        );
        statsCard.getChildren().add(statsRow);
        leftCol.getChildren().add(statsCard);

        // RIGHT column
        VBox rightCol = new VBox(22);
        rightCol.setMinWidth(320);
        rightCol.setMaxWidth(340);

        // Change Password Card
        VBox pwCard = buildSectionCard("Change Password", "Update your login password");
        PasswordField currentPw = new PasswordField();
        PasswordField newPw = new PasswordField();
        PasswordField confirmPw = new PasswordField();
        currentPw.setPromptText("Enter current password");
        newPw.setPromptText("Enter new password");
        confirmPw.setPromptText("Re-enter new password");
        applyFieldStyle(currentPw);
        applyFieldStyle(newPw);
        applyFieldStyle(confirmPw);
        pwCard.getChildren().addAll(
            fieldGroup("CURRENT PASSWORD", currentPw),
            fieldGroup("NEW PASSWORD", newPw),
            fieldGroup("CONFIRM NEW PASSWORD", confirmPw)
        );
        // Password strength indicator
        VBox strengthBox = new VBox(6);
        Label strengthLbl = new Label("PASSWORD STRENGTH");
        strengthLbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        strengthLbl.setTextFill(Color.web(TEXT_SOFT));
        HBox segBar = new HBox(4);
        Region[] strengthSegs = new Region[5];
        for (int i = 0; i < 5; i++) {
            Region seg = new Region();
            seg.setPrefHeight(6);
            strengthSegs[i] = seg;
            HBox.setHgrow(seg, Priority.ALWAYS);
            segBar.getChildren().add(seg);
        }
        Label weakLbl = new Label("Enter a new password to check strength");
        weakLbl.setFont(Font.font("Poppins", 10));
        weakLbl.setTextFill(Color.web(TEXT_SOFT));
        Runnable refreshStrength = () -> {
            int score = passwordStrengthScore(newPw.getText());
            String[] colors = {BORDER, BORDER, BORDER, BORDER, BORDER};
            String hint;
            String hintColor;
            if (newPw.getText().isEmpty()) {
                hint = "Enter a new password to check strength";
                hintColor = TEXT_SOFT;
            } else if (score <= 1) {
                colors[0] = ACCENT;
                hint = "Weak — add numbers and symbols";
                hintColor = WARNING_TEXT;
            } else if (score <= 3) {
                for (int i = 0; i <= score; i++) {
                    colors[i] = ACCENT;
                }
                hint = "Fair — consider a longer passphrase";
                hintColor = TEXT_TITLE;
            } else {
                for (int i = 0; i < 5; i++) {
                    colors[i] = SUCCESS_TEXT;
                }
                hint = "Strong password";
                hintColor = SUCCESS_TEXT;
            }
            for (int i = 0; i < 5; i++) {
                strengthSegs[i].setStyle(
                    "-fx-background-color: " + colors[i] + ";" +
                    "-fx-background-radius: 3;"
                );
            }
            weakLbl.setText(hint);
            weakLbl.setStyle("-fx-text-fill: " + hintColor + ";");
        };
        newPw.textProperty().addListener((o, a, b) -> refreshStrength.run());
        refreshStrength.run();
        strengthBox.getChildren().addAll(strengthLbl, segBar, weakLbl);
        HBox pwBtn = new HBox(); pwBtn.setAlignment(Pos.CENTER_RIGHT);
        Button updatePassword = makeAccentBtn("Update Password");
        updatePassword.setOnAction(e -> changePassword(currentPw, newPw, confirmPw));
        pwBtn.getChildren().add(updatePassword);
        pwCard.getChildren().addAll(strengthBox, pwBtn);
        pwCard.setManaged(false);
        pwCard.setVisible(false);
        rightCol.getChildren().add(pwCard);
        rightCol.setManaged(false);
        rightCol.setVisible(false);

        editProfileBtn.setOnAction(e -> {
            boolean show = !detailsCard.isVisible();
            detailsCard.setManaged(show);
            detailsCard.setVisible(show);
            pwCard.setManaged(show);
            pwCard.setVisible(show);
            rightCol.setManaged(show);
            rightCol.setVisible(show);
            editProfileBtn.setText(show ? "Hide Edit" : "Edit Profile");
            if (show) {
                firstNameTf.requestFocus();
            }
        });

        twoCol.getChildren().addAll(leftCol, rightCol);
        body.getChildren().addAll(profileHeaderCard, twoCol);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(400), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    //  Helpers 
    private VBox buildSectionCard(String title, String sub) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(22, 24, 24, 24));
        card.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: rgba(26,19,99,0.14);" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1.2;"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web(ACCENT, 0.07));
        ds.setRadius(9);
        ds.setOffsetY(2);
        card.setEffect(ds);
        Text t = new Text(title);
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        t.setFill(Color.web(TEXT_TITLE));
        Text s = new Text(sub);
        s.setFont(Font.font("Poppins", 11));
        s.setFill(Color.web(TEXT_SOFT));
        VBox hdr = new VBox(5, t, s);
        card.getChildren().add(hdr);
        return card;
    }

    private Label pillBadge(String text, String color, String bg) {
        Label badge = new Label(text);
        badge.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        badge.setStyle(
            "-fx-text-fill: " + color + ";" +
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 4 12 4 12;"
        );
        return badge;
    }

    private int passwordStrengthScore(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        int score = 0;
        if (password.length() >= 8) {
            score++;
        }
        if (password.length() >= 12) {
            score++;
        }
        if (password.matches(".*[0-9].*")) {
            score++;
        }
        if (password.matches(".*[A-Z].*") && password.matches(".*[a-z].*")) {
            score++;
        }
        if (password.matches(".*[^A-Za-z0-9].*")) {
            score++;
        }
        return score;
    }

    private VBox buildFG(String label, String prompt, String value, boolean isPass) {
        VBox g = new VBox(6);
        Label lbl = new Label(label); lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9)); lbl.setTextFill(Color.web(TEXT_MUTED));
        TextField tf = isPass ? new PasswordField() : new TextField(value);
        tf.setPromptText(prompt); tf.setPrefHeight(40);
        applyFieldStyle(tf);
        g.getChildren().addAll(lbl, tf);
        return g;
    }

    private TextField profileField(String value, boolean readOnly) {
        TextField field = new TextField(value == null ? "" : value);
        field.setPrefHeight(40);
        field.setEditable(!readOnly);
        if (readOnly) {
            field.setStyle(
                "-fx-background-color: " + BG_ROW_ALT + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: " + TEXT_SOFT + ";" +
                "-fx-padding: 10 12;" +
                "-fx-font-family: Poppins;" +
                "-fx-font-size: 12;"
            );
        } else {
            applyFieldStyle(field);
        }
        return field;
    }

    private VBox fieldGroup(String label, TextField field) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_SOFT));
        g.getChildren().addAll(lbl, field);
        return g;
    }

    private void saveProfileChanges(
        TextField firstNameTf,
        TextField lastNameTf,
        TextField emailTf,
        TextField phoneTf,
        Text headerName
    ) {
        String firstName = firstNameTf.getText().trim();
        String lastName = lastNameTf.getText().trim();
        String email = emailTf.getText().trim();
        String phone = phoneTf.getText().trim();
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showProfileAlert(Alert.AlertType.WARNING, "First name and last name are required.");
            return;
        }
        String fullName = firstName + " " + lastName;
        UserDAO userDAO = new UserDAO();
        int userId = AppSession.currentUser().userId();
        if (userDAO.updateProfile(userId, fullName, email, phone)) {
            userDAO.findById(userId).ifPresent(AppSession::updateCurrentUser);
            headerName.setText(fullName);
            showProfileAlert(Alert.AlertType.INFORMATION, "Profile updated successfully.");
        } else {
            showProfileAlert(Alert.AlertType.ERROR, "Could not update profile.");
        }
    }

    private void changePassword(PasswordField currentPw, PasswordField newPw, PasswordField confirmPw) {
        String current = currentPw.getText();
        String next = newPw.getText();
        String confirm = confirmPw.getText();
        if (current.isBlank() || next.isBlank() || confirm.isBlank()) {
            showProfileAlert(Alert.AlertType.WARNING, "Complete all password fields.");
            return;
        }
        if (next.length() < 6) {
            showProfileAlert(Alert.AlertType.WARNING, "New password must be at least 6 characters.");
            return;
        }
        if (!next.equals(confirm)) {
            showProfileAlert(Alert.AlertType.WARNING, "New password and confirmation do not match.");
            return;
        }
        boolean ok = new UserDAO().changePassword(AppSession.currentUser().userId(), current, next);
        if (ok) {
            currentPw.clear();
            newPw.clear();
            confirmPw.clear();
            showProfileAlert(Alert.AlertType.INFORMATION, "Password updated successfully.");
        } else {
            showProfileAlert(Alert.AlertType.ERROR, "Current password is incorrect or password update failed.");
        }
    }

    private void showProfileAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applyFieldStyle(TextField f) {
        String base =
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-prompt-text-fill: " + TEXT_SOFT + ";" +
            "-fx-padding: 10 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;";
        f.setStyle(base);
        f.focusedProperty().addListener((o, old, foc) -> f.setStyle(
            base + "-fx-border-color: " + (foc ? ACCENT : BORDER) + ";"
        ));
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text); b.setPrefHeight(38); b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        return b;
    }

    private VBox makeMiniStat(String label, String value, String color) {
        VBox wrap = new VBox();
        VBox chip = new VBox(6);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(16, 18, 16, 18));
        String outline = Color.web(color, 0.75).toString().replace("0x", "#");
        String wash = Color.web(color, 0.08).toString().replace("0x", "#");
        chip.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + CARD_SURFACE + ", " + wash + ");" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + outline + ";" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1.8;" +
            "-fx-effect: dropshadow(gaussian, rgba(26,19,99,0.06), 7, 0.0, 0, 2);"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        Text val = new Text(value);
        val.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        val.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setFill(Color.web(TEXT_SOFT));
        lbl.setWrappingWidth(140);
        chip.getChildren().addAll(val, lbl);
        wrap.getChildren().add(chip);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return wrap;
    }

    private Button outlineButton(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38);
        b.setPadding(new Insets(0, 18, 0, 18));
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

    public static void main(String[] args) { launch(args); }
}


