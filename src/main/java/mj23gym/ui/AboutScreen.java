package mj23gym.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
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
import javafx.util.Duration;

/**
 * About / Info page for the MJ23 Playgrind Gym system.
 */
public class AboutScreen {
    static final String BG_MAIN       = ModernDesignSystem.BG_LIGHT;
    static final String BG_CARD       = ModernDesignSystem.CARD_BG;
    static final String BG_ROW_ALT      = ModernDesignSystem.HOVER_EFFECT;
    static final String ACCENT        = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK   = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_TITLE    = ModernDesignSystem.PRIMARY;
    static final String TEXT_SOFT     = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DIM      = ModernDesignSystem.DARK_GRAY;
    static final String BORDER        = ModernDesignSystem.BORDER_COLOR;
    static final String SUCCESS_TEXT  = "#237A36";
    static final String WARNING_TEXT  = "#6E6400";
    static final String CARD_SURFACE  = ModernDesignSystem.WHITE;
    static final String BRAND_YELLOW  = "#FDEE21";

    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        VBox pg = new VBox(2);
        Text title = new Text("About");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_TITLE));
        Text sub = new Text("System information, modules, and project details");
        sub.setFont(Font.font("Poppins", 11));
        sub.setFill(Color.web(TEXT_SOFT));
        pg.getChildren().addAll(title, sub);
        topBar.getChildren().add(pg);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");

        VBox body = new VBox(20);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        VBox hero = buildHeroCard();

        HBox stats = new HBox(14);
        stats.getChildren().addAll(
            statChip("13", "Implemented Modules", TEXT_TITLE),
            statChip("2", "User Roles", SUCCESS_TEXT),
            statChip("v1.0.0", "Current Version", TEXT_TITLE),
            statChip("MJ23", "Playgrind Gym", WARNING_TEXT)
        );

        HBox bottomRow = new HBox(20);
        VBox modules = buildModulesCard();
        VBox team = buildProjectCard();
        HBox.setHgrow(modules, Priority.ALWAYS);
        HBox.setHgrow(team, Priority.ALWAYS);
        bottomRow.getChildren().addAll(modules, team);

        body.getChildren().addAll(hero, stats, bottomRow);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(300), body);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return content;
    }

    private VBox buildHeroCard() {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;"
        );
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web(ACCENT, 0.12));
        ds.setRadius(14);
        ds.setOffsetY(4);
        card.setEffect(ds);

        Region yellowStripe = new Region();
        yellowStripe.setPrefHeight(4);
        yellowStripe.setMaxHeight(4);
        yellowStripe.setStyle("-fx-background-color: " + BRAND_YELLOW + "; -fx-background-radius: 20 20 0 0;");
        Region banner = new Region();
        banner.setPrefHeight(8);
        banner.setMaxHeight(8);
        banner.setStyle("-fx-background-color: linear-gradient(to right, " + ACCENT + ", " + ACCENT_DARK + ");");

        HBox heroBody = new HBox(24);
        heroBody.setAlignment(Pos.CENTER_LEFT);
        heroBody.setPadding(new Insets(24, 28, 28, 28));

        StackPane logo = new StackPane();
        logo.setPrefSize(72, 72);
        Circle ring = new Circle(36);
        ring.setFill(Color.web(CARD_SURFACE));
        Circle bg = new Circle(32);
        bg.setFill(Color.web(ACCENT));
        Text mj = new Text("MJ");
        mj.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        mj.setFill(Color.WHITE);
        logo.getChildren().addAll(ring, bg, mj);

        VBox textBlock = new VBox(8);
        Text app = new Text("MJ23 Playgrind Gym");
        app.setFont(Font.font("Poppins", FontWeight.BOLD, 26));
        app.setFill(Color.web(TEXT_TITLE));
        Text tagline = new Text("Management System");
        tagline.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        tagline.setFill(Color.web(TEXT_SOFT));
        Text purpose = new Text(
            "A desktop gym management system for members, staff registration, plans, billing, " +
            "inventory, equipment, POS, reports, maintenance, search, help, and profiles."
        );
        purpose.setFont(Font.font("Poppins", 12));
        purpose.setFill(Color.web(TEXT_SOFT));
        purpose.setWrappingWidth(720);
        textBlock.getChildren().addAll(app, tagline, purpose);
        HBox.setHgrow(textBlock, Priority.ALWAYS);

        heroBody.getChildren().addAll(logo, textBlock);
        card.getChildren().addAll(yellowStripe, banner, heroBody);
        return card;
    }

    private VBox buildModulesCard() {
        VBox card = sectionCard("Included Modules", "Core features built for this project");
        String[][] modules = {
            {"SC", "Security"},
            {"RV", "Registration / Verification"},
            {"MM", "Member Management"},
            {"PL", "Manage Plans"},
            {"PY", "Billing & Payment"},
            {"IN", "Inventory Management"},
            {"EQ", "Equipment Management"},
            {"PO", "Point of Sale"},
            {"RP", "Reports"},
            {"MT", "Maintenance"},
            {"SR", "Search"},
            {"HP", "Help"},
            {"PR", "Profile"},
        };
        FlowPane grid = new FlowPane(10, 10);
        grid.setPrefWrapLength(520);
        for (String[] mod : modules) {
            grid.getChildren().add(modulePill(mod[0], mod[1]));
        }
        card.getChildren().add(grid);
        return card;
    }

    private VBox buildProjectCard() {
        VBox card = sectionCard("Project Information", "Academic gym operations system");
        card.getChildren().addAll(
            infoRow("System", "MJ23 Playgrind Gym Management System"),
            infoRow("Version", "v1.0.0"),
            infoRow("Platform", "Java 17 / JavaFX / MySQL"),
            infoRow("Purpose", "Role-based security and operational modules for gym staff and administrators"),
            infoRow("Support", "Use Help for guides, FAQs, and troubleshooting")
        );

        VBox contact = new VBox(10);
        contact.setAlignment(Pos.CENTER_LEFT);
        contact.setPadding(new Insets(14, 16, 14, 16));
        contact.setStyle(
            "-fx-background-color: " + BG_ROW_ALT + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(26,19,99,0.12);" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;"
        );
        Label contactLbl = new Label("Contact");
        contactLbl.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        contactLbl.setTextFill(Color.web(TEXT_SOFT));
        HBox emailRow = new HBox(10);
        emailRow.setAlignment(Pos.CENTER_LEFT);
        emailRow.getChildren().addAll(
            iconBadge("@", 28),
            emailText("jameslyfiguracion@gmail.com")
        );
        contact.getChildren().addAll(contactLbl, emailRow);
        card.getChildren().add(contact);
        return card;
    }

    private HBox modulePill(String glyph, String name) {
        HBox pill = new HBox(10);
        pill.setAlignment(Pos.CENTER_LEFT);
        pill.setPadding(new Insets(8, 14, 8, 10));
        pill.setStyle(
            "-fx-background-color: " + BG_ROW_ALT + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;"
        );
        pill.getChildren().addAll(iconBadge(glyph, 26), moduleName(name));
        return pill;
    }

    private Text moduleName(String name) {
        Text t = new Text(name);
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        t.setFill(Color.web(TEXT_TITLE));
        return t;
    }

    private StackPane iconBadge(String glyph, double size) {
        StackPane stack = new StackPane();
        stack.setPrefSize(size, size);
        Circle bg = new Circle(size / 2);
        bg.setFill(Color.web("rgba(26,19,99,0.08)"));
        Text text = new Text(glyph);
        double fontSize = glyph.length() > 2 ? 9 : 11;
        text.setFont(Font.font("Poppins", FontWeight.BOLD, fontSize));
        text.setFill(Color.web(TEXT_TITLE));
        stack.getChildren().addAll(bg, text);
        return stack;
    }

    private Text emailText(String email) {
        Text t = new Text(email);
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        t.setFill(Color.web(TEXT_TITLE));
        return t;
    }

    private VBox sectionCard(String title, String subtitle) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 22, 22, 22));
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
        Rectangle accentBar = new Rectangle(42, 3);
        accentBar.setArcWidth(3);
        accentBar.setArcHeight(3);
        accentBar.setFill(Color.web(ACCENT));
        Text t = new Text(title);
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        t.setFill(Color.web(TEXT_TITLE));
        Text s = new Text(subtitle);
        s.setFont(Font.font("Poppins", 11));
        s.setFill(Color.web(TEXT_SOFT));
        card.getChildren().add(new VBox(6, accentBar, t, s));
        return card;
    }

    private HBox statChip(String value, String label, String color) {
        HBox chip = new HBox(12);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 18, 14, 18));
        chip.setStyle(
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow d = new DropShadow();
        d.setColor(Color.web("#000000", 0.08));
        d.setRadius(8);
        d.setOffsetY(2);
        chip.setEffect(d);
        Rectangle accent = new Rectangle(4, 36);
        accent.setArcWidth(4);
        accent.setArcHeight(4);
        accent.setFill(Color.web(color));
        Text val = new Text(value);
        val.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        val.setFill(Color.web(color));
        Text lbl = new Text(label);
        lbl.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        lbl.setFill(Color.web(TEXT_SOFT));
        chip.getChildren().addAll(accent, new VBox(2, lbl, val));
        return chip;
    }

    private HBox infoRow(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);
        Rectangle marker = new Rectangle(3, 28);
        marker.setArcWidth(3);
        marker.setArcHeight(3);
        marker.setFill(Color.web(ACCENT));
        VBox labels = new VBox(2);
        Label l = new Label(label);
        l.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        l.setTextFill(Color.web(TEXT_SOFT));
        Label v = new Label(value);
        v.setFont(Font.font("Poppins", 11));
        v.setTextFill(Color.web(TEXT_TITLE));
        v.setWrapText(true);
        v.setMaxWidth(280);
        labels.getChildren().addAll(l, v);
        row.getChildren().addAll(marker, labels);
        return row;
    }
}
