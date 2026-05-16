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
import mj23gym.dao.UserDAO;

/**
 * MJ23 Playgrind Gym - Profile Screen
 * Displays the logged-in user's account info, editable profile fields,
 * and password management.
 */
public class AdminProfileScreen extends Application {

    static final String BG_MAIN     = "#1a1a2e";
    static final String BG_SIDEBAR  = "#0d1b2a";
    static final String BG_CARD     = "#1e2a3a";
    static final String BG_ROW_ALT  = "#253545";
    static final String ACCENT      = "#e63946";
    static final String ACCENT_DARK = "#c0303b";
    static final String TEXT_WHITE  = "#ffffff";
    static final String TEXT_MUTED  = "#b0bec5";
    static final String TEXT_DIM    = "#607080";
    static final String BORDER      = "#253545";
    static final String SUCCESS     = "#4caf50";
    static final String WARNING     = "#ff9800";
    static final String INFO        = "#2196f3";

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

    // ══════════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════════
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230); sidebar.setMinWidth(230); sidebar.setMaxWidth(230);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");

        Rectangle topAccent = new Rectangle(230, 5);
        topAccent.setFill(Color.web(ACCENT));

        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setPadding(new Insets(22, 20, 22, 20));
        StackPane badge = new StackPane(); badge.setPrefSize(42, 42);
        Rectangle bb = new Rectangle(42, 42); bb.setArcWidth(10); bb.setArcHeight(10); bb.setFill(Color.web(ACCENT));
        Text bt = new Text("MJ"); bt.setFont(Font.font("Georgia", FontWeight.BOLD, 16)); bt.setFill(Color.WHITE);
        badge.getChildren().addAll(bb, bt);
        VBox lt = new VBox(1);
        Text l1 = new Text("MJ23 PLAYGRIND"); l1.setFont(Font.font("Georgia", FontWeight.BOLD, 11)); l1.setFill(Color.web(TEXT_WHITE));
        Text l2 = new Text("GYM"); l2.setFont(Font.font("Georgia", FontWeight.BOLD, 11)); l2.setFill(Color.web(ACCENT));
        lt.getChildren().addAll(l1, l2);
        logoArea.getChildren().addAll(badge, lt);

        String[][] items = {
            {"🏠","Dashboard"},{"👥","Member Management"},{"💳","Payment & Billing"},
            {"📦","Inventory"},{"🏋","Equipment"},{"🛒","Point of Sale"},{"📊","Reports"}
        };
        VBox menu = new VBox(2); menu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : items)
            menu.getChildren().add(buildMenuItem(it[0], it[1], false));

        String[][] sys = {{"⚙","Settings"},{"❓","Help"},{"ℹ","About"}};
        VBox sysMenu = new VBox(2); sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys)
            sysMenu.getChildren().add(buildMenuItem(it[0], it[1], false));

        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        Rectangle d1 = new Rectangle(230, 1); d1.setFill(Color.web(BORDER));
        Rectangle d2 = new Rectangle(230, 1); d2.setFill(Color.web(BORDER));

        // Active user at bottom — highlighted since this IS the profile
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
        Text avTxt = new Text(user.initial()); avTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 13)); avTxt.setFill(Color.WHITE);
        StackPane avStack = new StackPane(avatar, avTxt); avStack.setPrefSize(36, 36);
        VBox userInfo = new VBox(2);
        Text uName = new Text(user.displayName()); uName.setFont(Font.font("Verdana", FontWeight.BOLD, 12)); uName.setFill(Color.web(TEXT_WHITE));
        Text uRole = new Text(user.role()); uRole.setFont(Font.font("Verdana", 10)); uRole.setFill(Color.web(ACCENT));
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
        lbl.setFont(Font.font("Verdana", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        lbl.setFill(active ? Color.web(TEXT_WHITE) : Color.web(TEXT_MUTED));
        item.getChildren().addAll(bar, ico, lbl);
        item.setStyle(active
            ? "-fx-background-color: " + BG_CARD + "; -fx-background-radius: 8;"
            : "-fx-background-color: transparent; -fx-background-radius: 8;");
        if (!active) {
            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;"));
        }
        return item;
    }

    private Label makeSecLbl(String t) {
        Label l = new Label(t); l.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_DIM)); l.setPadding(new Insets(8, 0, 6, 20)); return l;
    }

    // ══════════════════════════════════════════════════════════════
    // MAIN CONTENT
    // ══════════════════════════════════════════════════════════════
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
        t1.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Manage your account details and password");
        t2.setFont(Font.font("Verdana", 11));
        t2.setFill(Color.web(TEXT_MUTED));
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

        // ── Profile Header Card ────────────────────────────────────
        VBox profileHeaderCard = new VBox(0);
        profileHeaderCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;");
        DropShadow ds1 = new DropShadow(); ds1.setColor(Color.web("#000", 0.3)); ds1.setRadius(12); ds1.setOffsetY(4);
        profileHeaderCard.setEffect(ds1);

        // Red banner. Use a resizable Region instead of binding a Rectangle
        // to its parent width, which can create an endless layout resize loop.
        Region banner = new Region();
        banner.setPrefHeight(80);
        banner.setMinHeight(80);
        banner.setMaxHeight(80);
        banner.setMaxWidth(Double.MAX_VALUE);
        banner.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-background-radius: 12 12 0 0;");

        // Avatar + name overlay
        HBox profileInfo = new HBox(22);
        profileInfo.setPadding(new Insets(0, 28, 24, 28));
        profileInfo.setAlignment(Pos.BOTTOM_LEFT);

        // Large avatar circle
        StackPane bigAvatar = new StackPane();
        bigAvatar.setPrefSize(80, 80);
        bigAvatar.setMaxSize(80, 80);
        bigAvatar.setTranslateY(-30);
        Circle bigCircle = new Circle(40);
        bigCircle.setFill(Color.web(BG_SIDEBAR));
        bigCircle.setStroke(Color.web(ACCENT));
        bigCircle.setStrokeWidth(3);
        Text bigInitial = new Text(user.initial());
        bigInitial.setFont(Font.font("Georgia", FontWeight.BOLD, 34));
        bigInitial.setFill(Color.web(TEXT_WHITE));
        bigAvatar.getChildren().addAll(bigCircle, bigInitial);

        VBox nameInfo = new VBox(4);
        nameInfo.setTranslateY(-10);
        Text adminName = new Text(user.displayName());
        adminName.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        adminName.setFill(Color.web(TEXT_WHITE));
        HBox badges = new HBox(8);
        Label roleBadge = new Label(user.role());
        roleBadge.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        roleBadge.setTextFill(Color.web(ACCENT));
        roleBadge.setStyle("-fx-background-color: rgba(230,57,70,0.15); -fx-background-radius: 10; -fx-padding: 3 12 3 12;");
        Label statusBadge = new Label("✔  Active");
        statusBadge.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        statusBadge.setTextFill(Color.web(SUCCESS));
        statusBadge.setStyle("-fx-background-color: rgba(76,175,80,0.15); -fx-background-radius: 10; -fx-padding: 3 12 3 12;");
        badges.getChildren().addAll(roleBadge, statusBadge);
        Text lastLogin = new Text(user.lastLoginText());
        lastLogin.setFont(Font.font("Verdana", 10));
        lastLogin.setFill(Color.web(TEXT_DIM));
        nameInfo.getChildren().addAll(adminName, badges, lastLogin);

        Region nameSp = new Region(); HBox.setHgrow(nameSp, Priority.ALWAYS);

        Button editProfileBtn = makeAccentBtn("✏  Edit Profile");
        editProfileBtn.setTranslateY(-10);

        profileInfo.getChildren().addAll(bigAvatar, nameInfo, nameSp, editProfileBtn);
        profileHeaderCard.getChildren().addAll(banner, profileInfo);

        // ── Two-column layout ──────────────────────────────────────
        HBox twoCol = new HBox(22);
        twoCol.setAlignment(Pos.TOP_LEFT);
        twoCol.setMaxWidth(Double.MAX_VALUE);

        // LEFT column
        VBox leftCol = new VBox(22);
        leftCol.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Profile Details Card
        VBox detailsCard = buildSectionCard("👤  Profile Information", "Your personal account details");
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
        saveBtn.getChildren().add(makeAccentBtn("💾  Save Changes"));
        saveBtn.getChildren().clear();
        Button saveProfile = makeAccentBtn("Save Changes");
        saveProfile.setOnAction(e -> saveProfileChanges(firstNameTf, lastNameTf, emailTf, phoneTf, adminName));
        saveBtn.getChildren().add(saveProfile);
        detailsCard.getChildren().addAll(detailsForm, saveBtn);
        leftCol.getChildren().add(detailsCard);

        // Account Stats Card
        VBox statsCard = buildSectionCard("📊  Account Activity", "Your system usage statistics");
        HBox statsRow = new HBox(14);
        statsRow.getChildren().addAll(
            makeMiniStat("Sessions Today",   "12",    INFO),
            makeMiniStat("Members Added",    "5",     SUCCESS),
            makeMiniStat("Payments Processed","28",   WARNING),
            makeMiniStat("Reports Generated","3",     ACCENT)
        );
        statsCard.getChildren().add(statsRow);
        leftCol.getChildren().add(statsCard);

        // RIGHT column
        VBox rightCol = new VBox(22);
        rightCol.setMinWidth(320);
        rightCol.setMaxWidth(340);

        // Change Password Card
        VBox pwCard = buildSectionCard("🔒  Change Password", "Update your login password");
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
        strengthLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        strengthLbl.setTextFill(Color.web(TEXT_MUTED));
        HBox strengthBar = new HBox(4);
        for (int i = 0; i < 5; i++) {
            Rectangle seg = new Rectangle(0, 6);
            seg.setArcWidth(3); seg.setArcHeight(3);
            seg.setFill(i < 2 ? Color.web(ACCENT) : Color.web(BORDER));
            HBox.setHgrow(new Region(), Priority.ALWAYS);
            strengthBar.getChildren().add(seg);
            if (i < 4) strengthBar.getChildren().add(new Region() {{ setMinWidth(4); }});
        }
        strengthBar.setMaxWidth(Double.MAX_VALUE);
        // Rebuild as equal segments
        HBox segBar = new HBox(4);
        String[] segColors = {ACCENT, ACCENT, BORDER, BORDER, BORDER};
        for (String sc : segColors) {
            Region seg = new Region();
            seg.setPrefHeight(6);
            seg.setStyle("-fx-background-color: " + sc + "; -fx-background-radius: 3;");
            HBox.setHgrow(seg, Priority.ALWAYS);
            segBar.getChildren().add(seg);
        }
        Label weakLbl = new Label("Weak – add numbers and symbols");
        weakLbl.setFont(Font.font("Verdana", 10)); weakLbl.setTextFill(Color.web(ACCENT));
        strengthBox.getChildren().addAll(strengthLbl, segBar, weakLbl);
        HBox pwBtn = new HBox(); pwBtn.setAlignment(Pos.CENTER_RIGHT);
        Button updatePassword = makeAccentBtn("Update Password");
        updatePassword.setOnAction(e -> changePassword(currentPw, newPw, confirmPw));
        pwBtn.getChildren().add(updatePassword);
        pwCard.getChildren().addAll(strengthBox, pwBtn);
        rightCol.getChildren().add(pwCard);

        twoCol.getChildren().addAll(leftCol, rightCol);
        body.getChildren().addAll(profileHeaderCard, twoCol);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(400), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    // ── Helpers ────────────────────────────────────────────────────
    private VBox buildSectionCard(String title, String sub) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(10); ds.setOffsetY(4); card.setEffect(ds);
        Text t = new Text(title); t.setFont(Font.font("Verdana", FontWeight.BOLD, 14)); t.setFill(Color.web(TEXT_WHITE));
        Text s = new Text(sub); s.setFont(Font.font("Verdana", 11)); s.setFill(Color.web(TEXT_MUTED));
        Rectangle ul = new Rectangle(40, 2); ul.setFill(Color.web(ACCENT)); ul.setArcWidth(2); ul.setArcHeight(2);
        VBox hdr = new VBox(3, t, s, ul);
        card.getChildren().add(hdr);
        return card;
    }

    private VBox buildFG(String label, String prompt, String value, boolean isPass) {
        VBox g = new VBox(6);
        Label lbl = new Label(label); lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9)); lbl.setTextFill(Color.web(TEXT_MUTED));
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
        applyFieldStyle(field);
        if (readOnly) {
            field.setStyle(field.getStyle() + "-fx-opacity: 0.72;");
        }
        return field;
    }

    private VBox fieldGroup(String label, TextField field) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_MUTED));
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
        f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;");
        f.focusedProperty().addListener((o, old, foc) -> f.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + (foc ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Verdana;" +
            "-fx-font-size: 12;"));
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text); b.setPrefHeight(38); b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        return b;
    }

    private VBox makeMiniStat(String label, String value, String color) {
        VBox v = new VBox(4); v.setAlignment(Pos.CENTER); v.setPadding(new Insets(14, 10, 14, 10));
        v.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-border-width: 1;");
        HBox.setHgrow(v, Priority.ALWAYS);
        Text val = new Text(value); val.setFont(Font.font("Georgia", FontWeight.BOLD, 22)); val.setFill(Color.web(color));
        Text lbl = new Text(label); lbl.setFont(Font.font("Verdana", 10)); lbl.setFill(Color.web(TEXT_MUTED));
        lbl.setWrappingWidth(80); lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        v.getChildren().addAll(val, lbl);
        return v;
    }

    public static void main(String[] args) { launch(args); }
}
