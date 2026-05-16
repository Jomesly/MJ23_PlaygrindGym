package mj23gym.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * About / Info page for the MJ23 Playgrind Gym system.
 */
public class AboutScreen {
    static final String BG_MAIN     = "#1a1a2e";
    static final String BG_CARD     = "#1e2a3a";
    static final String ACCENT      = "#e63946";
    static final String TEXT_WHITE  = "#ffffff";
    static final String TEXT_MUTED  = "#b0bec5";
    static final String TEXT_DIM    = "#607080";
    static final String BORDER      = "#253545";
    static final String SUCCESS     = "#4caf50";
    static final String INFO        = "#2196f3";
    static final String WARNING     = "#ff9800";

    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text title = new Text("About");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        Text sub = new Text("System information, modules, and project details");
        sub.setFont(Font.font("Verdana", 11));
        sub.setFill(Color.web(TEXT_MUTED));
        pg.getChildren().addAll(title, sub);
        topBar.getChildren().add(pg);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");

        VBox body = new VBox(22);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        VBox hero = card();
        hero.setAlignment(Pos.CENTER_LEFT);
        Text app = new Text("MJ23 Playgrind Gym Management System");
        app.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        app.setFill(Color.web(TEXT_WHITE));
        Text version = new Text("Version 1.0.0 | JavaFX + MySQL");
        version.setFont(Font.font("Verdana", 12));
        version.setFill(Color.web(TEXT_MUTED));
        Text purpose = new Text("A desktop gym management system for members, staff registration, plans, billing, inventory, equipment, POS, reports, maintenance, search, help, and profiles.");
        purpose.setFont(Font.font("Verdana", 12));
        purpose.setFill(Color.web(TEXT_MUTED));
        purpose.setWrappingWidth(850);
        hero.getChildren().addAll(app, version, purpose);

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            stat("13", "Implemented Modules", ACCENT),
            stat("Admin/Staff", "Role-based Access", INFO),
            stat("MySQL", "Persistent Storage", SUCCESS),
            stat("JavaFX", "Desktop UI", WARNING)
        );

        VBox modules = card();
        Text mt = sectionTitle("Included Modules");
        String[] names = {
            "Security", "Registration/Verification", "Member Management", "Manage Plans",
            "Billing & Payment", "Inventory Management", "Equipment Management", "Point of Sale",
            "Report", "Maintenance", "Search", "Help", "Profile"
        };
        VBox list = new VBox(8);
        for (String name : names) {
            Label row = new Label(name);
            row.setTextFill(Color.web(TEXT_WHITE));
            row.setFont(Font.font("Verdana", 11));
            row.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8; -fx-padding: 8 12;");
            list.getChildren().add(row);
        }
        modules.getChildren().addAll(mt, list);

        VBox team = card();
        team.getChildren().addAll(
            sectionTitle("Project Information"),
            info("System", "MJ23 Playgrind Gym Management System"),
            info("Platform", "Java 17 / JavaFX / MySQL"),
            info("Purpose", "Academic gym operations system with role-based security and operational modules"),
            info("Support", "Use Help for workflows and troubleshooting")
        );

        body.getChildren().addAll(hero, stats, modules, team);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(300), body);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return content;
    }

    private VBox card() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);
        return card;
    }

    private VBox stat(String value, String label, String color) {
        VBox box = card();
        HBox.setHgrow(box, Priority.ALWAYS);
        Text v = new Text(value);
        v.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        v.setFill(Color.web(color));
        Text l = new Text(label);
        l.setFont(Font.font("Verdana", 11));
        l.setFill(Color.web(TEXT_MUTED));
        box.getChildren().addAll(v, l);
        return box;
    }

    private Text sectionTitle(String value) {
        Text t = new Text(value);
        t.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        t.setFill(Color.web(TEXT_WHITE));
        return t;
    }

    private HBox info(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Rectangle marker = new Rectangle(3, 26);
        marker.setFill(Color.web(ACCENT));
        Label l = new Label(label + ":");
        l.setTextFill(Color.web(TEXT_DIM));
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        Label v = new Label(value);
        v.setTextFill(Color.web(TEXT_MUTED));
        v.setFont(Font.font("Verdana", 11));
        v.setWrapText(true);
        row.getChildren().addAll(marker, l, v);
        return row;
    }
}
