package mj23gym.ui;

import java.util.List;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import mj23gym.dao.SearchDAO;

/**
 * Module 11 - Search: find records across members, inventory, and transactions.
 */
public class SearchScreen {
    static final String BG_MAIN      = ModernDesignSystem.BG_LIGHT;
    static final String BG_CARD      = ModernDesignSystem.CARD_BG;
    static final String BG_ROW_ALT   = ModernDesignSystem.HOVER_EFFECT;
    static final String ACCENT       = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK  = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_TITLE   = ModernDesignSystem.PRIMARY;
    static final String TEXT_SOFT    = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DIM     = ModernDesignSystem.DARK_GRAY;
    static final String BORDER       = ModernDesignSystem.BORDER_COLOR;
    static final String SUCCESS_TEXT = "#237A36";
    static final String WARNING_TEXT = "#6E6400";
    static final String CARD_SURFACE = ModernDesignSystem.WHITE;

    private final SearchDAO dao = new SearchDAO();
    private final VBox resultRows = new VBox(0);
    private final Label resultCount = new Label("Enter a keyword to search.");
    private final Text statMembers = new Text("—");
    private final Text statInventory = new Text("—");
    private final Text statPayments = new Text("—");
    private final Text statPos = new Text("—");
    private CheckBox membersFilter;
    private CheckBox inventoryFilter;
    private CheckBox paymentsFilter;
    private CheckBox posFilter;
    private TextField keywordField;
    private String lastKeyword = "";

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
        Text title = new Text("Search");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_TITLE));
        Text sub = new Text("Find member, inventory, payment, and POS transaction records");
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

        for (Text t : new Text[] { statMembers, statInventory, statPayments, statPos }) {
            t.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        }

        HBox moduleStats = new HBox(14);
        moduleStats.getChildren().addAll(
            moduleStatChip("Members", statMembers, TEXT_TITLE),
            moduleStatChip("Inventory", statInventory, WARNING_TEXT),
            moduleStatChip("Payments", statPayments, SUCCESS_TEXT),
            moduleStatChip("POS", statPos, TEXT_TITLE)
        );

        VBox searchCard = buildSectionCard(
            "Search Records",
            "Use a name, ID, item, reference number, status, or payment method."
        );
        keywordField = styledField();
        keywordField.setPromptText("Search members, inventory, or transactions...");
        Button searchBtn = accentButton("Search");
        Button clearBtn = outlineButton("Clear");
        HBox searchRow = new HBox(10, keywordField, searchBtn, clearBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(keywordField, Priority.ALWAYS);

        Label filterLbl = new Label("Search in");
        filterLbl.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        filterLbl.setTextFill(Color.web(TEXT_SOFT));

        membersFilter = filterPill("Members", true);
        inventoryFilter = filterPill("Inventory", true);
        paymentsFilter = filterPill("Payments", true);
        posFilter = filterPill("POS", true);
        HBox filters = new HBox(10, membersFilter, inventoryFilter, paymentsFilter, posFilter);
        filters.setAlignment(Pos.CENTER_LEFT);

        Label hint = new Label("Try: member name, item code, receipt #, payment ref, or status");
        hint.setFont(Font.font("Poppins", 10));
        hint.setTextFill(Color.web(TEXT_SOFT));
        hint.setWrapText(true);
        hint.setStyle(
            "-fx-background-color: rgba(26,19,99,0.05);" +
            "-fx-padding: 8 12;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(26,19,99,0.10);" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        Runnable onFilterChange = () -> {
            if (!lastKeyword.isBlank()) {
                runSearch(lastKeyword);
            }
        };
        for (CheckBox cb : new CheckBox[] { membersFilter, inventoryFilter, paymentsFilter, posFilter }) {
            cb.selectedProperty().addListener((o, a, b) -> onFilterChange.run());
        }

        searchBtn.setOnAction(e -> runSearch(keywordField.getText()));
        keywordField.setOnAction(e -> runSearch(keywordField.getText()));
        clearBtn.setOnAction(e -> {
            keywordField.clear();
            lastKeyword = "";
            resetStats();
            resultRows.getChildren().clear();
            resultCount.setText("Enter a keyword to search.");
            resultRows.getChildren().add(emptyState("No search has been performed yet.", "Enter a keyword above to find records across all modules."));
        });

        VBox filterBlock = new VBox(8, filterLbl, filters);
        searchCard.getChildren().addAll(searchRow, filterBlock, hint);

        VBox resultsShell = buildResultsTable();
        resultCount.setTextFill(Color.web(TEXT_TITLE));
        resultCount.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        resultRows.getChildren().add(emptyState("No search has been performed yet.", "Results will appear here grouped by module."));

        body.getChildren().addAll(moduleStats, searchCard, resultsShell);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FadeTransition ft = new FadeTransition(Duration.millis(300), body);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return content;
    }

    private VBox buildResultsTable() {
        VBox shell = new VBox(0);
        shell.setStyle(
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
        shell.setEffect(ds);

        VBox head = new VBox(6);
        head.setPadding(new Insets(18, 20, 12, 20));
        Rectangle bar = new Rectangle(42, 3);
        bar.setArcWidth(3);
        bar.setArcHeight(3);
        bar.setFill(Color.web(ACCENT));
        Text t = new Text("Search Results");
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        t.setFill(Color.web(TEXT_TITLE));
        Text s = new Text("Matching records grouped by module");
        s.setFont(Font.font("Poppins", 11));
        s.setFill(Color.web(TEXT_SOFT));
        head.getChildren().addAll(bar, t, s, resultCount);

        String[] headers = {"Module", "Record", "Details", "Status", "Date"};
        double[] widths = {13, 27, 38, 12, 10};
        HBox tblHdr = new HBox();
        tblHdr.setPadding(new Insets(10, 16, 10, 16));
        tblHdr.setStyle(
            "-fx-background-color: " + BG_ROW_ALT + ";" +
            "-fx-border-color: " + BORDER + " transparent transparent transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );
        GridPane hGrid = headerGrid(widths);
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i].toUpperCase());
            h.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
            h.setStyle("-fx-text-fill: " + TEXT_SOFT + ";");
            hGrid.add(h, i, 0);
        }
        tblHdr.getChildren().add(hGrid);

        shell.getChildren().addAll(head, tblHdr, resultRows);
        return shell;
    }

    private void runSearch(String rawKeyword) {
        resultRows.getChildren().clear();
        lastKeyword = rawKeyword == null ? "" : rawKeyword.trim();

        if (lastKeyword.isBlank()) {
            resetStats();
            resultCount.setText("Enter a keyword to search.");
            resultRows.getChildren().add(emptyState("No keyword entered.", "Try a member name, item name, member code, receipt number, or reference number."));
            return;
        }

        List<SearchDAO.SearchResult> all = dao.searchAll(lastKeyword);
        List<SearchDAO.SearchResult> results = all.stream().filter(this::allowed).toList();

        updateModuleStats(all);

        resultCount.setText(results.size() + " result(s) for \"" + lastKeyword + "\"");
        if (results.isEmpty()) {
            resultRows.getChildren().add(emptyState("No matching records found.", "Try a different keyword or enable more modules in the filters above."));
            return;
        }

        int index = 0;
        for (SearchDAO.SearchResult result : results) {
            resultRows.getChildren().add(resultRow(result, index++));
        }
    }

    private void resetStats() {
        for (Text t : new Text[] { statMembers, statInventory, statPayments, statPos }) {
            t.setText("—");
        }
    }

    private void updateModuleStats(List<SearchDAO.SearchResult> all) {
        long m = all.stream().filter(r -> "Members".equals(r.module())).count();
        long i = all.stream().filter(r -> "Inventory".equals(r.module())).count();
        long p = all.stream().filter(r -> "Payments".equals(r.module())).count();
        long o = all.stream().filter(r -> "POS".equals(r.module())).count();
        statMembers.setText(String.valueOf(m));
        statInventory.setText(String.valueOf(i));
        statPayments.setText(String.valueOf(p));
        statPos.setText(String.valueOf(o));
        statMembers.setFill(Color.web(TEXT_TITLE));
        statInventory.setFill(Color.web(WARNING_TEXT));
        statPayments.setFill(Color.web(SUCCESS_TEXT));
        statPos.setFill(Color.web(TEXT_TITLE));
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
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
            "-fx-background-color: " + (index % 2 == 0 ? CARD_SURFACE : BG_ROW_ALT) + ";" +
            (index > 0 ? "-fx-border-color: " + BORDER + " transparent transparent transparent;-fx-border-width: 1 0 0 0;" : "")
        );
        row.getColumnConstraints().add(percent(13));
        row.getColumnConstraints().add(percent(27));
        row.getColumnConstraints().add(percent(38));
        row.getColumnConstraints().add(percent(12));
        row.getColumnConstraints().add(percent(10));

        row.add(moduleBadge(result.module()), 0, 0);
        row.add(makeCell(result.title(), TEXT_TITLE, true), 1, 0);
        row.add(makeCell(result.detail(), TEXT_SOFT, false), 2, 0);
        row.add(statusBadge(result.status()), 3, 0);
        row.add(makeCell(result.recordDate() != null ? result.recordDate().toString() : "—", TEXT_DIM, false), 4, 0);
        return row;
    }

    private GridPane headerGrid(double[] widths) {
        GridPane g = new GridPane();
        g.setHgap(12);
        g.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(g, Priority.ALWAYS);
        for (double w : widths) {
            g.getColumnConstraints().add(percent(w));
        }
        return g;
    }

    private javafx.scene.layout.ColumnConstraints percent(double value) {
        javafx.scene.layout.ColumnConstraints col = new javafx.scene.layout.ColumnConstraints();
        col.setPercentWidth(value);
        col.setHgrow(Priority.ALWAYS);
        return col;
    }

    private VBox buildSectionCard(String title, String subtitle) {
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
        card.getChildren().addAll(accentBar, t, s);
        return card;
    }

    private HBox moduleStatChip(String label, Text value, String color) {
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

    private TextField styledField() {
        TextField field = new TextField();
        field.setPrefHeight(42);
        String base =
            "-fx-background-color: " + CARD_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-text-fill: " + TEXT_TITLE + ";" +
            "-fx-prompt-text-fill: " + TEXT_SOFT + ";" +
            "-fx-padding: 0 14 0 14;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;";
        field.setStyle(base);
        field.focusedProperty().addListener((o, old, focused) -> field.setStyle(
            base + "-fx-border-color: " + (focused ? ACCENT : BORDER) + ";"
        ));
        return field;
    }

    private CheckBox filterPill(String text, boolean selected) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(selected);
        cb.setFont(Font.font("Poppins", FontWeight.BOLD, 11));
        applyFilterPillStyle(cb);
        cb.selectedProperty().addListener((o, a, b) -> applyFilterPillStyle(cb));
        return cb;
    }

    private void applyFilterPillStyle(CheckBox cb) {
        boolean on = cb.isSelected();
        cb.setStyle(
            "-fx-text-fill: " + (on ? TEXT_TITLE : TEXT_SOFT) + ";" +
            "-fx-background-color: " + (on ? "rgba(26,19,99,0.10)" : CARD_SURFACE) + ";" +
            "-fx-border-color: " + (on ? ACCENT : BORDER) + ";" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
    }

    private Button accentButton(String text) {
        Button b = new Button(text);
        b.setPrefHeight(42);
        b.setPadding(new Insets(0, 22, 0, 22));
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
        b.setPrefHeight(42);
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

    private Label makeCell(String text, String color, boolean bold) {
        Label label = new Label(text == null ? "" : text);
        label.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        label.setStyle(
            "-fx-text-fill: " + color + ";" +
            "-fx-font-weight: " + (bold ? "bold" : "normal") + ";"
        );
        label.setWrapText(true);
        return label;
    }

    private Label moduleBadge(String module) {
        String textColor;
        String bg;
        switch (module) {
            case "Members" -> {
                textColor = TEXT_TITLE;
                bg = "rgba(26,19,99,0.10)";
            }
            case "Inventory" -> {
                textColor = WARNING_TEXT;
                bg = "rgba(253,238,33,0.28)";
            }
            case "Payments" -> {
                textColor = SUCCESS_TEXT;
                bg = "rgba(228,255,223,0.75)";
            }
            case "POS" -> {
                textColor = TEXT_TITLE;
                bg = "rgba(119,116,155,0.12)";
            }
            default -> {
                textColor = TEXT_SOFT;
                bg = "rgba(119,116,155,0.10)";
            }
        }
        Label badge = new Label(module);
        badge.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        badge.setStyle(
            "-fx-text-fill: " + textColor + ";" +
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 4 10 4 10;"
        );
        return badge;
    }

    private Label statusBadge(String status) {
        String s = status == null || status.isBlank() ? "—" : status;
        String lower = s.toLowerCase();
        String textColor = TEXT_SOFT;
        String bg = "rgba(119,116,155,0.12)";
        if (lower.contains("active") || lower.contains("paid") || lower.contains("completed") || lower.contains("in stock")) {
            textColor = SUCCESS_TEXT;
            bg = "rgba(228,255,223,0.75)";
        } else if (lower.contains("low") || lower.contains("pending") || lower.contains("overdue")) {
            textColor = WARNING_TEXT;
            bg = "rgba(253,238,33,0.28)";
        } else if (lower.contains("inactive") || lower.contains("out") || lower.contains("cancel")) {
            textColor = TEXT_TITLE;
            bg = "rgba(26,19,99,0.08)";
        }
        Label badge = new Label(s);
        badge.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        badge.setStyle(
            "-fx-text-fill: " + textColor + ";" +
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 4 10 4 10;"
        );
        return badge;
    }

    private VBox emptyState(String title, String detail) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(28, 24, 28, 24));
        box.setStyle(
            "-fx-background-color: " + BG_ROW_ALT + ";" +
            "-fx-background-radius: 0 0 18 18;"
        );
        Label t = new Label(title);
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
        t.setTextFill(Color.web(TEXT_TITLE));
        Label d = new Label(detail);
        d.setFont(Font.font("Poppins", 11));
        d.setTextFill(Color.web(TEXT_SOFT));
        d.setWrapText(true);
        box.getChildren().addAll(t, d);
        return box;
    }
}
