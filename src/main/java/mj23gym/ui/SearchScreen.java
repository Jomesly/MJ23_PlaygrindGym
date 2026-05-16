package mj23gym.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import mj23gym.dao.SearchDAO;

import java.util.List;

/**
 * Module 11 - Search: find records across members, inventory, and transactions.
 */
public class SearchScreen {
    static final String BG_MAIN     = "#1a1a2e";
    static final String BG_CARD     = "#1e2a3a";
    static final String BG_ROW_ALT  = "#253545";
    static final String BG_SIDEBAR  = "#0d1b2a";
    static final String ACCENT      = "#e63946";
    static final String ACCENT_DARK = "#c0303b";
    static final String TEXT_WHITE  = "#ffffff";
    static final String TEXT_MUTED  = "#b0bec5";
    static final String TEXT_DIM    = "#607080";
    static final String BORDER      = "#253545";
    static final String SUCCESS     = "#4caf50";
    static final String WARNING     = "#ff9800";
    static final String INFO        = "#2196f3";

    private final SearchDAO dao = new SearchDAO();
    private final VBox resultRows = new VBox(0);
    private final Label resultCount = new Label("Enter a keyword to search.");
    private CheckBox membersFilter;
    private CheckBox inventoryFilter;
    private CheckBox paymentsFilter;
    private CheckBox posFilter;

    public VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text title = new Text("Search");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        Text sub = new Text("Find member, inventory, payment, and POS transaction records");
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

        VBox searchCard = sectionCard("Search Records", "Use a name, ID, item, reference number, status, or payment method.");
        TextField keyword = styledField();
        keyword.setPromptText("Search members, inventory, or transactions...");
        Button searchBtn = accentButton("Search");
        HBox searchRow = new HBox(10, keyword, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(keyword, Priority.ALWAYS);

        membersFilter = filter("Members", true);
        inventoryFilter = filter("Inventory", true);
        paymentsFilter = filter("Payments", true);
        posFilter = filter("POS", true);
        HBox filters = new HBox(18, membersFilter, inventoryFilter, paymentsFilter, posFilter);
        filters.setAlignment(Pos.CENTER_LEFT);

        searchBtn.setOnAction(e -> runSearch(keyword.getText()));
        keyword.setOnAction(e -> runSearch(keyword.getText()));
        searchCard.getChildren().addAll(searchRow, filters);

        VBox resultsCard = sectionCard("Search Results", "Matching records grouped by module.");
        resultCount.setTextFill(Color.web(TEXT_MUTED));
        resultCount.setFont(Font.font("Verdana", 11));
        resultRows.getChildren().add(emptyLabel("No search has been performed yet."));
        resultsCard.getChildren().addAll(resultCount, resultRows);

        body.getChildren().addAll(searchCard, resultsCard);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(300), body);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return content;
    }

    private void runSearch(String rawKeyword) {
        resultRows.getChildren().clear();
        if (rawKeyword == null || rawKeyword.isBlank()) {
            resultCount.setText("Enter a keyword to search.");
            resultRows.getChildren().add(emptyLabel("Try a member name, item name, member code, receipt number, or reference number."));
            return;
        }

        List<SearchDAO.SearchResult> results = dao.searchAll(rawKeyword).stream()
            .filter(this::allowed)
            .toList();

        resultCount.setText(results.size() + " result(s) for \"" + rawKeyword.trim() + "\"");
        if (results.isEmpty()) {
            resultRows.getChildren().add(emptyLabel("No matching records found."));
            return;
        }

        int index = 0;
        for (SearchDAO.SearchResult result : results) {
            resultRows.getChildren().add(resultRow(result, index++));
        }
    }

    private boolean allowed(SearchDAO.SearchResult result) {
        return switch (result.module()) {
            case "Members" -> membersFilter.isSelected();
            case "Inventory" -> inventoryFilter.isSelected();
            case "Payments" -> paymentsFilter.isSelected();
            case "POS" -> posFilter.isSelected();
            default -> true;
        };
    }

    private GridPane resultRow(SearchDAO.SearchResult result, int index) {
        GridPane row = new GridPane();
        row.setHgap(12);
        row.setPadding(new Insets(13, 16, 13, 16));
        row.setStyle("-fx-background-color: " + (index % 2 == 0 ? BG_CARD : BG_ROW_ALT) + ";");
        row.getColumnConstraints().add(percent(13));
        row.getColumnConstraints().add(percent(27));
        row.getColumnConstraints().add(percent(40));
        row.getColumnConstraints().add(percent(10));
        row.getColumnConstraints().add(percent(10));

        row.add(moduleBadge(result.module()), 0, 0);
        row.add(cell(result.title(), TEXT_WHITE, true), 1, 0);
        row.add(cell(result.detail(), TEXT_MUTED, false), 2, 0);
        row.add(statusBadge(result.status()), 3, 0);
        row.add(cell(result.recordDate() != null ? result.recordDate().toString() : "", TEXT_DIM, false), 4, 0);
        return row;
    }

    private javafx.scene.layout.ColumnConstraints percent(double value) {
        javafx.scene.layout.ColumnConstraints col = new javafx.scene.layout.ColumnConstraints();
        col.setPercentWidth(value);
        col.setHgrow(Priority.ALWAYS);
        return col;
    }

    private VBox sectionCard(String title, String subtitle) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);
        Text t = new Text(title);
        t.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        t.setFill(Color.web(TEXT_WHITE));
        Text s = new Text(subtitle);
        s.setFont(Font.font("Verdana", 11));
        s.setFill(Color.web(TEXT_MUTED));
        card.getChildren().addAll(t, s);
        return card;
    }

    private TextField styledField() {
        TextField field = new TextField();
        field.setPrefHeight(42);
        field.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Verdana;");
        return field;
    }

    private CheckBox filter(String text, boolean selected) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(selected);
        cb.setTextFill(Color.web(TEXT_MUTED));
        cb.setFont(Font.font("Verdana", 11));
        return cb;
    }

    private Button accentButton(String text) {
        Button b = new Button(text);
        b.setPrefHeight(42);
        b.setPadding(new Insets(0, 22, 0, 22));
        b.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        return b;
    }

    private Label cell(String text, String color, boolean bold) {
        Label label = new Label(text == null ? "" : text);
        label.setFont(Font.font("Verdana", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        label.setTextFill(Color.web(color));
        label.setWrapText(true);
        return label;
    }

    private Label moduleBadge(String module) {
        String color = switch (module) {
            case "Members" -> INFO;
            case "Inventory" -> WARNING;
            case "Payments" -> SUCCESS;
            case "POS" -> ACCENT;
            default -> TEXT_MUTED;
        };
        Label badge = new Label(module);
        badge.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        badge.setTextFill(Color.web(color));
        badge.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 10; -fx-padding: 3 9 3 9;");
        return badge;
    }

    private Label statusBadge(String status) {
        Label badge = new Label(status == null ? "-" : status);
        badge.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        badge.setTextFill(Color.web(TEXT_WHITE));
        badge.setStyle("-fx-background-color: rgba(76,175,80,0.15); -fx-background-radius: 10; -fx-padding: 3 9 3 9;");
        return badge;
    }

    private Label emptyLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web(TEXT_MUTED));
        label.setPadding(new Insets(16));
        return label;
    }
}
