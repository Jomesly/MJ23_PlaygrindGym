package mj23gym.ui;

import java.util.List;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mj23gym.dao.PlanDAO;

public class PlanManagementScreen {

    static final String BG_MAIN      = ModernDesignSystem.BG_LIGHT;
    static final String BG_CARD      = ModernDesignSystem.CARD_BG;
    static final String BG_ROW_ALT   = ModernDesignSystem.HOVER_EFFECT;
    static final String BG_SIDEBAR   = ModernDesignSystem.SIDEBAR_BG;
    static final String ACCENT       = ModernDesignSystem.PRIMARY;
    static final String ACCENT_DARK  = ModernDesignSystem.PRIMARY_DARK;
    static final String TEXT_WHITE   = ModernDesignSystem.PRIMARY;
    static final String TEXT_MUTED   = ModernDesignSystem.TEXT_MUTED;
    static final String TEXT_DIM     = ModernDesignSystem.DARK_GRAY;
    static final String BORDER       = ModernDesignSystem.BORDER_COLOR;
    static final String SUCCESS      = ModernDesignSystem.SUCCESS;
    static final String WARNING      = ModernDesignSystem.ACCENT_YELLOW;
    static final String SUCCESS_TEXT = "#237A36";
    static final String WARNING_TEXT = "#6E6400";

    private final PlanDAO dao = new PlanDAO();

    public VBox buildContent() {
        dao.ensurePlanSetup();

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

        VBox titleBox = new VBox(2);
        Text title = new Text("Manage Plans");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        outlineText(title, 0.28);
        Text subtitle = new Text("Create, update, and remove membership pricing options");
        subtitle.setFont(Font.font("Poppins", 11));
        subtitle.setFill(Color.web(TEXT_DIM));
        titleBox.getChildren().addAll(title, subtitle);
        topBar.getChildren().add(titleBox);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: " + BG_MAIN + "; -fx-background-color: " + BG_MAIN + ";");

        VBox body = new VBox(20);
        body.setPadding(new Insets(26, 28, 26, 28));
        body.setStyle("-fx-background-color: " + BG_MAIN + ";");

        Text activeCount = new Text();
        Text totalCount = new Text();
        styleStatValue(activeCount);
        styleStatValue(totalCount);

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            statChip("Active Plans", activeCount, SUCCESS_TEXT),
            statChip("Total Plans", totalCount, TEXT_WHITE)
        );

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_RIGHT);
        Button addButton = primaryButton("Add Plan");
        controls.getChildren().add(addButton);

        VBox table = tableCard();
        VBox rows = new VBox(0);
        String[] headers = {"Plan", "Duration", "Price", "Benefits", "Status", "Actions"};
        double[] widths = {17, 13, 12, 27, 11, 20};
        table.getChildren().addAll(headerRow(headers, widths), rows);

        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            refreshRows(rows, widths, refresh[0]);
            List<PlanDAO.PlanRecord> all = dao.findAll();
            long active = all.stream().filter(PlanDAO.PlanRecord::active).count();
            activeCount.setText(String.valueOf(active));
            totalCount.setText(String.valueOf(all.size()));
        };
        addButton.setOnAction(e -> showPlanDialog(null, refresh[0]));
        refresh[0].run();

        body.getChildren().addAll(stats, controls, table);
        scroll.setContent(body);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        content.getChildren().addAll(topBar, scroll);
        return content;
    }

    private void refreshRows(VBox rows, double[] widths, Runnable refresh) {
        rows.getChildren().clear();
        List<PlanDAO.PlanRecord> plans = dao.findAll();
        int rowIndex = 0;
        for (PlanDAO.PlanRecord plan : plans) {
            HBox row = new HBox();
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: " + (rowIndex++ % 2 == 0 ? BG_CARD : BG_ROW_ALT) + ";");

            GridPane grid = grid(widths);
            grid.add(cell(plan.planName(), TEXT_WHITE, true), 0, 0);
            grid.add(cell(plan.duration(), TEXT_DIM, false), 1, 0);
            grid.add(cell("PHP " + String.format("%,.2f", plan.price()), TEXT_WHITE, true), 2, 0);
            grid.add(cell(emptyDash(plan.benefits()), TEXT_DIM, false), 3, 0);
            grid.add(statusBadge(plan.active()), 4, 0);

            HBox actions = new HBox(6);
            actions.setAlignment(Pos.CENTER_LEFT);
            Button edit = smallButton("Edit", WARNING_TEXT);
            Button toggle = smallButton(plan.active() ? "Disable" : "Enable", plan.active() ? TEXT_DIM : SUCCESS_TEXT);
            Button remove = smallButton("Remove", ACCENT);
            edit.setOnAction(e -> showPlanDialog(plan, refresh));
            toggle.setOnAction(e -> {
                dao.setActive(plan.planId(), !plan.active());
                refresh.run();
            });
            remove.setOnAction(e -> confirmRemove(plan, refresh));
            actions.getChildren().addAll(edit, toggle, remove);
            grid.add(actions, 5, 0);

            row.getChildren().add(grid);
            rows.getChildren().add(row);
        }
        if (plans.isEmpty()) {
            Label empty = new Label("No membership plans found.");
            empty.setPadding(new Insets(18));
            empty.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font: 12 Poppins;");
            rows.getChildren().add(empty);
        }
    }

    private void showPlanDialog(PlanDAO.PlanRecord existing, Runnable onSaved) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Add Plan" : "Edit Plan");
        dialog.setResizable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(28));
        root.setPrefWidth(480);
        root.setStyle("-fx-background-color: " + BG_CARD + ";");

        Text title = new Text(existing == null ? "Add Plan" : "Edit Plan");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        outlineText(title, 0.28);
        Rectangle underline = new Rectangle(48, 3);
        underline.setArcWidth(3);
        underline.setArcHeight(3);
        underline.setFill(Color.web(ACCENT));

        TextField name = new TextField(existing != null ? existing.planName() : "");
        TextField price = new TextField(existing != null ? String.format("%.2f", existing.price()) : "");
        TextArea description = new TextArea(existing != null ? emptyString(existing.description()) : "");
        TextArea benefits = new TextArea(existing != null ? emptyString(existing.benefits()) : "");
        ComboBox<String> duration = new ComboBox<>();
        duration.getItems().addAll("Per Session", "Monthly", "Quarterly", "Semi Annual", "Annual");
        duration.setValue(existing != null ? PlanDAO.normalizePlanName(existing.duration()) : "Monthly");
        CheckBox active = new CheckBox("Active plan");
        active.setSelected(existing == null || existing.active());
        active.setStyle(
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-font: 12 Poppins;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);"
        );

        applyFieldStyle(name);
        applyFieldStyle(price);
        applyTextAreaStyle(description);
        applyTextAreaStyle(benefits);
        styleCombo(duration);
        description.setPrefRowCount(2);
        benefits.setPrefRowCount(2);

        root.getChildren().addAll(
            title,
            underline,
            labeledField("PLAN NAME", name, "Monthly"),
            comboField("DURATION", duration),
            labeledField("PRICE", price, "788.00"),
            textAreaField("DESCRIPTION", description),
            textAreaField("BENEFITS", benefits),
            active
        );

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = secondaryButton("Cancel");
        Button save = primaryButton(existing == null ? "Save Plan" : "Update Plan");
        cancel.setOnAction(e -> dialog.close());
        save.setOnAction(e -> {
            String planName = name.getText().trim();
            if (planName.isEmpty()) {
                alertErr("Plan name is required.");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(price.getText().trim().replace(",", ""));
            } catch (NumberFormatException ex) {
                alertErr("Please enter a valid price.");
                return;
            }
            if (amount < 0) {
                alertErr("Price cannot be negative.");
                return;
            }
            PlanDAO.PlanRecord record = new PlanDAO.PlanRecord(
                existing == null ? 0 : existing.planId(),
                planName,
                description.getText().trim(),
                duration.getValue(),
                amount,
                benefits.getText().trim(),
                active.isSelected()
            );
            boolean ok = existing == null
                ? dao.insert(record, AppSession.currentUser().userId()) > 0
                : dao.update(record);
            if (ok) {
                dialog.close();
                onSaved.run();
            } else {
                alertErr("Could not save plan. Check your database connection and plan details.");
            }
        });
        buttons.getChildren().addAll(cancel, save);
        root.getChildren().add(buttons);

        Scene scene = new Scene(root);
        scene.setFill(Color.web(BG_CARD));
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void confirmRemove(PlanDAO.PlanRecord plan, Runnable refresh) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Plan");
        confirm.setHeaderText(null);
        confirm.setContentText("Remove " + plan.planName() + " from membership plans?");
        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.isPresent() && answer.get() == ButtonType.OK) {
            if (dao.delete(plan.planId())) {
                refresh.run();
            } else {
                alertErr("Could not remove plan. Try disabling it instead.");
            }
        }
    }

    private HBox headerRow(String[] headers, double[] widths) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: " + BG_SIDEBAR + "; -fx-background-radius: 12 12 0 0;");
        GridPane grid = grid(widths);
        for (int i = 0; i < headers.length; i++) {
            Label label = new Label(headers[i].toUpperCase());
            label.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
            label.setTextFill(Color.web(ACCENT));
            label.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
            grid.add(label, i, 0);
        }
        row.getChildren().add(grid);
        return row;
    }

    private GridPane grid(double[] widths) {
        GridPane grid = new GridPane();
        for (double width : widths) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(width);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        grid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(grid, Priority.ALWAYS);
        return grid;
    }

    private VBox tableCard() {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 22;" +
            "-fx-border-width: 1;"
        );
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.3));
        shadow.setRadius(12);
        shadow.setOffsetY(4);
        card.setEffect(shadow);
        return card;
    }

    private HBox statChip(String label, Text value, String color) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(14, 20, 14, 20));
        chip.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(chip, Priority.ALWAYS);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.12));
        shadow.setRadius(8);
        shadow.setOffsetY(3);
        chip.setEffect(shadow);
        Text labelNode = new Text(label);
        labelNode.setFont(Font.font("Poppins", 11));
        labelNode.setFill(Color.web(TEXT_DIM));
        outlineText(labelNode, 0.18);
        value.setFill(Color.web(readableAccent(color)));
        outlineText(value, 0.30);
        chip.getChildren().add(new VBox(2, labelNode, value));
        return chip;
    }

    private void styleStatValue(Text value) {
        value.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
    }

    private Label cell(String text, String color, boolean bold) {
        Label label = new Label(text);
        label.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        label.setTextFill(Color.web(readableAccent(color)));
        label.setPadding(new Insets(0, 8, 0, 0));
        label.setWrapText(true);
        label.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.72), 1, 0.0, 0, 0);");
        return label;
    }

    private Label statusBadge(boolean active) {
        Label badge = new Label(active ? "Active" : "Inactive");
        badge.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        badge.setTextFill(Color.web(active ? SUCCESS_TEXT : TEXT_DIM));
        badge.setPadding(new Insets(4, 8, 4, 8));
        badge.setStyle(
            "-fx-background-color: " + (active ? "rgba(228,255,223,0.85)" : "rgba(119,116,155,0.16)") + ";" +
            "-fx-background-radius: 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);"
        );
        return badge;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(38);
        button.setPadding(new Insets(0, 18, 0, 18));
        button.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        button.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + ACCENT_DARK + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        ));
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(38);
        button.setPadding(new Insets(0, 18, 0, 18));
        button.setFont(Font.font("Poppins", 12));
        button.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        return button;
    }

    private Button smallButton(String text, String color) {
        Button button = new Button(text);
        String readableColor = readableAccent(color);
        button.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        button.setMinWidth(58);
        button.setPadding(new Insets(5, 8, 5, 8));
        button.setStyle(
            "-fx-background-color: " + buttonWash(color) + ";" +
            "-fx-text-fill: " + readableColor + ";" +
            "-fx-border-color: " + readableColor + ";" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);"
        );
        return button;
    }

    private VBox labeledField(String label, TextField field, String prompt) {
        field.setPromptText(prompt);
        VBox box = new VBox(6);
        Label lbl = formLabel(label);
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private VBox comboField(String label, ComboBox<String> combo) {
        VBox box = new VBox(6);
        box.getChildren().addAll(formLabel(label), combo);
        return box;
    }

    private VBox textAreaField(String label, TextArea area) {
        VBox box = new VBox(6);
        box.getChildren().addAll(formLabel(label), area);
        return box;
    }

    private Label formLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        label.setTextFill(Color.web(ACCENT));
        label.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.75), 1, 0.0, 0, 0);");
        return label;
    }

    private void applyFieldStyle(TextField field) {
        field.setPrefHeight(40);
        field.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.72), 1, 0.0, 0, 0);"
        );
    }

    private void applyTextAreaStyle(TextArea area) {
        area.setStyle(
            "-fx-control-inner-background: " + BG_MAIN + ";" +
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-prompt-text-fill: " + TEXT_DIM + ";" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.72), 1, 0.0, 0, 0);"
        );
        area.setWrapText(true);
    }

    private void styleCombo(ComboBox<String> combo) {
        combo.setPrefHeight(40);
        combo.setStyle(
            "-fx-background-color: " + BG_MAIN + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-font-family: Poppins;" +
            "-fx-font-size: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.72), 1, 0.0, 0, 0);"
        );
    }

    private String emptyDash(String text) {
        return text == null || text.isBlank() ? "-" : text;
    }

    private String emptyString(String text) {
        return text == null ? "" : text;
    }

    private void alertErr(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Manage Plans");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void outlineText(Text text, double width) {
        text.setStroke(Color.web(ModernDesignSystem.WHITE, 0.82));
        text.setStrokeWidth(width);
    }

    private String readableAccent(String color) {
        if (SUCCESS.equalsIgnoreCase(color)) return SUCCESS_TEXT;
        if (WARNING.equalsIgnoreCase(color)) return WARNING_TEXT;
        return color;
    }

    private String buttonWash(String color) {
        String readable = readableAccent(color);
        if (SUCCESS_TEXT.equalsIgnoreCase(readable)) return "rgba(228,255,223,0.85)";
        if (WARNING_TEXT.equalsIgnoreCase(readable)) return "rgba(253,238,33,0.32)";
        if (TEXT_DIM.equalsIgnoreCase(readable) || TEXT_MUTED.equalsIgnoreCase(readable)) return "rgba(119,116,155,0.14)";
        return "rgba(26,19,99,0.12)";
    }
}
