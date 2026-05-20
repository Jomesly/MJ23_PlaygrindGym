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
import mj23gym.dao.UserDAO;

/**
 * MJ23 Playgrind Gym  Settings Screen
 * Tabs: Security | Reports | Help | About
 */
public class SettingsScreen extends Application {

    static final String BG_MAIN     = ModernDesignSystem.BG_LIGHT;
    static final String BG_SIDEBAR  = ModernDesignSystem.SIDEBAR_BG;
    static final String BG_CARD     = ModernDesignSystem.CARD_BG;
    static final String ACCENT      = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_WHITE  = ModernDesignSystem.PRIMARY;
    static final String TEXT_MUTED  = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DIM    = ModernDesignSystem.DARK_GRAY;
    static final String BORDER      = ModernDesignSystem.BORDER_COLOR;
    static final String SUCCESS     = ModernDesignSystem.SUCCESS;
    static final String WARNING     = ModernDesignSystem.ACCENT_YELLOW;
    static final String INFO        = ModernDesignSystem.PRIMARY;

    @Override
    public void start(Stage stage) {
        stage.setTitle("MJ23 Playgrind Gym  Settings");
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
        for (String[] it : items) menu.getChildren().add(buildMenuItem(it[0], it[1], false));
        String[][] sys = {{"","Settings"},{"","Help"},{"","About"}};
        VBox sysMenu = new VBox(2); sysMenu.setPadding(new Insets(0, 10, 0, 10));
        for (String[] it : sys) sysMenu.getChildren().add(buildMenuItem(it[0], it[1], it[1].equals("Settings")));
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
        Text t1 = new Text("Settings"); t1.setFont(Font.font("Poppins", FontWeight.BOLD, 20)); t1.setFill(Color.web(TEXT_WHITE));
        Text t2 = new Text("Configure system preferences and security options"); t2.setFont(Font.font("Poppins", 11)); t2.setFill(Color.web(TEXT_MUTED));
        pg.getChildren().addAll(t1, t2);
        topBar.getChildren().add(pg);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox body = new VBox(0);
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Settings tabs
        HBox tabRow = new HBox(0);
        tabRow.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        String[] tabs = {"  Security", "  Reports", "  Help", "  About"};
        for (int i = 0; i < tabs.length; i++) {
            boolean active = i == 0;
            Button tab = new Button(tabs[i]);
            tab.setFont(Font.font("Poppins", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
            tab.setPrefHeight(46);
            tab.setPadding(new Insets(0, 24, 0, 24));
            tab.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + (active ? TEXT_WHITE : TEXT_MUTED) + ";" +
                "-fx-border-color: transparent transparent " + (active ? ACCENT : "transparent") + " transparent;" +
                "-fx-border-width: 0 0 3 0;" +
                "-fx-cursor: hand;"
            );
            String tabName = tabs[i];
            tab.setOnAction(e -> {
                if (tabName.contains("Security")) {
                    info("Security settings are shown below.");
                } else if (tabName.contains("Reports")) {
                    info("Open the Reports module from the main menu to generate and view reports.");
                } else if (tabName.contains("Help")) {
                    info("Open Help from the system menu for guides and troubleshooting.");
                } else {
                    info("Open About from the system menu to view system information.");
                }
            });
            tabRow.getChildren().add(tab);
        }

        VBox tabContent = new VBox(24);
        tabContent.setPadding(new Insets(26, 28, 26, 28));
        tabContent.setStyle("-fx-background-color: " + BG_MAIN + ";");

        //  Security Tab Content 
        // Change Password
        VBox pwCard = buildSectionCard("  Change Password", "Update your account password");
        GridPane pwForm = new GridPane(); pwForm.setHgap(16); pwForm.setVgap(14);
        ColumnConstraints cc1 = new ColumnConstraints(); cc1.setPercentWidth(50);
        ColumnConstraints cc2 = new ColumnConstraints(); cc2.setPercentWidth(50);
        pwForm.getColumnConstraints().addAll(cc1, cc2);
        PasswordField currentPw = passwordField("Enter current password");
        PasswordField newPw = passwordField("Enter new password");
        PasswordField confirmPw = passwordField("Re-enter new password");
        pwForm.add(fieldGroup("CURRENT PASSWORD", currentPw), 0, 0, 2, 1);
        pwForm.add(fieldGroup("NEW PASSWORD", newPw), 0, 1);
        pwForm.add(fieldGroup("CONFIRM NEW PASSWORD", confirmPw), 1, 1);
        HBox pwBtn = new HBox(); pwBtn.setAlignment(Pos.CENTER_RIGHT);
        Button updatePassword = makeAccentBtn("Update Password");
        updatePassword.setOnAction(e -> changePassword(currentPw, newPw, confirmPw));
        pwBtn.getChildren().add(updatePassword);
        pwCard.getChildren().addAll(pwForm, pwBtn);

        // Access Control
        VBox accessCard = buildSectionCard("  Access Control", "Manage user roles and permissions");
        String[][] users = {{"admin", "Administrator", "Full Access", "Active"}, {"staff01", "Front Desk", "Limited Access", "Active"}, {"staff02", "Front Desk", "Limited Access", "Inactive"}};
        VBox userList = new VBox(0);
        userList.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-background-radius: 16; -fx-border-color: " + BORDER + "; -fx-border-radius: 16; -fx-border-width: 1;");
        for (int i = 0; i < users.length; i++) {
            String bg = (i % 2 == 0) ? BG_MAIN : BG_CARD;
            HBox uRow = new HBox(14); uRow.setPadding(new Insets(12, 16, 12, 16)); uRow.setAlignment(Pos.CENTER_LEFT);
            uRow.setStyle("-fx-background-color: " + bg + ";");
            Label uName = new Label(users[i][0]); uName.setFont(Font.font("Poppins", FontWeight.BOLD, 12)); uName.setTextFill(Color.web(TEXT_WHITE));
            Label uRole = new Label(users[i][1]); uRole.setFont(Font.font("Poppins", 11)); uRole.setTextFill(Color.web(TEXT_MUTED));
            Label uPerm = new Label(users[i][2]); uPerm.setFont(Font.font("Poppins", 10)); uPerm.setTextFill(Color.web(INFO));
            uPerm.setStyle("-fx-background-color: rgba(119,116,155,0.12); -fx-background-radius: 16; -fx-padding: 2 8 2 8;");
            Region uSp = new Region(); HBox.setHgrow(uSp, Priority.ALWAYS);
            Label uStatus = new Label(users[i][3]);
            uStatus.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
            boolean active = users[i][3].equals("Active");
            uStatus.setTextFill(Color.web(active ? SUCCESS : ACCENT));
            uStatus.setStyle("-fx-background-color: " + (active ? "rgba(228,255,223,0.15)" : "rgba(26,19,99,0.15)") + "; -fx-background-radius: 18; -fx-padding: 3 10 3 10;");
            Button editU = makeIconBtn("", WARNING);
            editU.setOnAction(e -> info("Use Registration/Verification to verify or deactivate staff accounts."));
            uRow.getChildren().addAll(uName, uRole, uPerm, uSp, uStatus, editU);
            userList.getChildren().add(uRow);
        }
        HBox addUserBtn = new HBox(); addUserBtn.setAlignment(Pos.CENTER_RIGHT);
        Button addStaff = makeAccentBtn("Add Staff Account");
        addStaff.setOnAction(e -> info("Open Registration/Verification from the admin menu to add and verify staff accounts."));
        addUserBtn.getChildren().add(addStaff);
        accessCard.getChildren().addAll(userList, addUserBtn);

        // System Settings
        VBox sysCard = buildSectionCard("  System Preferences", "General system configuration");
        String[][] prefs = {
            {"Gym Name",            "MJ23 Playgrind Gym"},
            {"Location",            "Taguig City, Philippines"},
            {"Currency",            "PHP ()"},
            {"Default Membership",  "Monthly"},
        };
        GridPane prefGrid = new GridPane(); prefGrid.setHgap(16); prefGrid.setVgap(14);
        ColumnConstraints pc1 = new ColumnConstraints(); pc1.setPercentWidth(50);
        ColumnConstraints pc2 = new ColumnConstraints(); pc2.setPercentWidth(50);
        prefGrid.getColumnConstraints().addAll(pc1, pc2);
        for (int i = 0; i < prefs.length; i++) {
            VBox fg = new VBox(6);
            Label lbl = new Label(prefs[i][0].toUpperCase()); lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9)); lbl.setTextFill(Color.web(TEXT_MUTED));
            TextField tf = new TextField(prefs[i][1]); tf.setPrefHeight(40); applyFieldStyle(tf);
            fg.getChildren().addAll(lbl, tf);
            prefGrid.add(fg, i % 2, i / 2);
        }
        HBox savePrefBtn = new HBox(); savePrefBtn.setAlignment(Pos.CENTER_RIGHT);
        Button savePrefs = makeAccentBtn("Save Preferences");
        savePrefs.setOnAction(e -> info("Preferences saved for this session."));
        savePrefBtn.getChildren().add(savePrefs);
        sysCard.getChildren().addAll(prefGrid, savePrefBtn);

        tabContent.getChildren().addAll(pwCard, accessCard, sysCard);
        body.getChildren().addAll(tabRow, tabContent);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(350), body);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        return content;
    }

    private VBox buildSectionCard(String title, String sub) {
        VBox card = new VBox(16); card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 22; -fx-border-color: " + BORDER + "; -fx-border-radius: 22; -fx-border-width: 1;");
        DropShadow ds = new DropShadow(); ds.setColor(Color.web("#000", 0.25)); ds.setRadius(10); ds.setOffsetY(4); card.setEffect(ds);
        VBox hdr = new VBox(3);
        Text t = new Text(title); t.setFont(Font.font("Poppins", FontWeight.BOLD, 14)); t.setFill(Color.web(TEXT_WHITE));
        Text s = new Text(sub); s.setFont(Font.font("Poppins", 11)); s.setFill(Color.web(TEXT_MUTED));
        Rectangle ul = new Rectangle(40, 2); ul.setFill(Color.web(ACCENT)); ul.setArcWidth(2); ul.setArcHeight(2);
        hdr.getChildren().addAll(t, s, ul);
        card.getChildren().add(hdr);
        return card;
    }

    private VBox buildFG(String label, String prompt, boolean isPass) {
        VBox g = new VBox(6);
        Label lbl = new Label(label); lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9)); lbl.setTextFill(Color.web(TEXT_MUTED));
        TextField tf = isPass ? new PasswordField() : new TextField();
        tf.setPromptText(prompt); tf.setPrefHeight(40); applyFieldStyle(tf);
        g.getChildren().addAll(lbl, tf);
        return g;
    }

    private PasswordField passwordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefHeight(40);
        applyFieldStyle(field);
        return field;
    }

    private VBox fieldGroup(String label, TextField field) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        g.getChildren().addAll(lbl, field);
        return g;
    }

    private void changePassword(PasswordField currentPw, PasswordField newPw, PasswordField confirmPw) {
        String current = currentPw.getText();
        String next = newPw.getText();
        String confirm = confirmPw.getText();
        if (current.isBlank() || next.isBlank() || confirm.isBlank()) {
            warn("Complete all password fields.");
            return;
        }
        if (next.length() < 6) {
            warn("New password must be at least 6 characters.");
            return;
        }
        if (!next.equals(confirm)) {
            warn("New password and confirmation do not match.");
            return;
        }
        boolean ok = new UserDAO().changePassword(AppSession.currentUser().userId(), current, next);
        if (ok) {
            currentPw.clear();
            newPw.clear();
            confirmPw.clear();
            info("Password updated successfully.");
        } else {
            warn("Current password is incorrect or password update failed.");
        }
    }

    private void info(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void warn(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applyFieldStyle(TextField f) {
        f.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Poppins; -fx-font-size: 12;");
        f.focusedProperty().addListener((o, old, foc) -> f.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + (foc ? ACCENT : BORDER) + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Poppins; -fx-font-size: 12;"));
    }

    private Button makeAccentBtn(String text) {
        Button b = new Button(text); b.setPrefHeight(38); b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        return b;
    }

    private Button makeIconBtn(String icon, String color) {
        Button b = new Button(icon);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + "; -fx-font-size: 13; -fx-cursor: hand; -fx-padding: 3 6 3 6; -fx-background-radius: 6;");
        return b;
    }

    public static void main(String[] args) { launch(args); }
}


