package mj23gym.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import mj23gym.dao.MaintenanceDAO;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Module 10 - Maintenance: backup, restore, and system tool management.
 */
public class MaintenanceScreen {
    static final String BG_MAIN     = "#F2F4F8";
    static final String BG_CARD     = "#F8F9FC";
    static final String BG_ROW_ALT  = "#EEF2FA";
    static final String BG_SIDEBAR  = "#E9EDF6";
    static final String ACCENT      = "#1A1363";
    static final String ACCENT_DARK = "#332F4F";
    static final String TEXT_WHITE  = "#1A1363";
    static final String TEXT_MUTED  = "#77749B";
    static final String TEXT_DIM    = "#4B4B4B";
    static final String BORDER      = "#D9DDEA";
    static final String SUCCESS     = "#2F6F5E";
    static final String WARNING     = "#8A6D00";
    static final String INFO        = "#1A1363";

    private final MaintenanceDAO dao = new MaintenanceDAO();
    private final VBox backupRows = new VBox(0);
    private final VBox toolRows = new VBox(0);
    private CheckBox showArchived;

    public VBox buildContent() {
        dao.ensureMaintenanceTables();

        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_MAIN + ";");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        VBox pg = new VBox(2);
        Text title = new Text("Maintenance");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web(TEXT_WHITE));
        Text sub = new Text("Back up data, restore saved copies, and manage system tools");
        sub.setFont(Font.font("Poppins", 11));
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

        HBox operationCards = new HBox(16);
        operationCards.getChildren().addAll(buildBackupCard(), buildRestoreCard());

        VBox backups = buildBackupsTable();
        VBox tools = buildToolsPanel();

        body.getChildren().addAll(operationCards, backups, tools);
        scroll.setContent(body);
        content.getChildren().addAll(topBar, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        refreshBackups();
        refreshTools();

        FadeTransition ft = new FadeTransition(Duration.millis(300), body);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return content;
    }

    private VBox buildBackupCard() {
        VBox card = sectionCard("Backup", "Save a recoverable copy of system data.");
        Text detail = bodyText("Creates a SQL backup file in the project backups folder and records it in the database.");
        Button backupBtn = accentButton("Create Backup");
        backupBtn.setOnAction(e -> {
            MaintenanceDAO.BackupRecord backup = dao.createBackup(Path.of("").toAbsolutePath(), AppSession.currentUser().userId());
            refreshBackups();
            if ("Created".equalsIgnoreCase(backup.status())) {
                new Alert(Alert.AlertType.INFORMATION, "Backup created:\n" + backup.filePath()).showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Backup failed. Check database connection and folder permissions.").showAndWait();
            }
        });
        card.getChildren().addAll(detail, rightAligned(backupBtn));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox buildRestoreCard() {
        VBox card = sectionCard("Restore", "Recover data from a selected backup file.");
        Text detail = bodyText("Choose a backup file generated by this system. Restore is admin-only and requires confirmation.");
        Button restoreBtn = accentButton("Restore Backup");
        restoreBtn.setOnAction(e -> chooseAndRestoreBackup());
        card.getChildren().addAll(detail, rightAligned(restoreBtn));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox buildBackupsTable() {
        VBox card = sectionCard("Backup History", "Recently created or restored backup records.");
        String[] headers = {"Backup", "Status", "File", "Created At"};
        double[] widths = {24, 12, 44, 20};
        card.getChildren().add(tableHeader(headers, widths));
        backupRows.setStyle("-fx-background-color: " + BG_CARD + ";");
        card.getChildren().add(backupRows);
        return card;
    }

    private VBox buildToolsPanel() {
        VBox card = sectionCard("Manage Tools", "Add, update, archive, or restore system features.");

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        showArchived = new CheckBox("Show archived");
        showArchived.setTextFill(Color.web(TEXT_MUTED));
        showArchived.setOnAction(e -> refreshTools());
        Button addBtn = accentButton("Add Tool");
        addBtn.setOnAction(e -> showToolDialog(null));
        actions.getChildren().addAll(showArchived, addBtn);

        String[] headers = {"Tool", "Module", "Version", "Status", "Description", "Actions"};
        double[] widths = {16, 16, 9, 11, 30, 18};
        card.getChildren().addAll(actions, tableHeader(headers, widths), toolRows);
        return card;
    }

    private void chooseAndRestoreBackup() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select MJ23 Backup SQL File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL backup", "*.sql"));
        File backupDir = Path.of("").toAbsolutePath().resolve("backups").toFile();
        if (backupDir.exists()) {
            chooser.setInitialDirectory(backupDir);
        }
        File selected = chooser.showOpenDialog(null);
        if (selected == null) {
            return;
        }
        Alert confirm = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Restore this backup?\n\n" + selected.getAbsolutePath() + "\n\nThis will replace records included in the backup file.",
            ButtonType.CANCEL,
            ButtonType.OK
        );
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        boolean ok = dao.restoreBackup(selected.toPath(), AppSession.currentUser().userId());
        refreshBackups();
        refreshTools();
        new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
            ok ? "Restore completed." : "Restore failed. Use a backup generated by this system.").showAndWait();
    }

    private void refreshBackups() {
        backupRows.getChildren().clear();
        List<MaintenanceDAO.BackupRecord> backups = dao.findRecentBackups();
        if (backups.isEmpty()) {
            backupRows.getChildren().add(emptyLabel("No backups recorded yet."));
            return;
        }
        double[] widths = {24, 12, 44, 20};
        int i = 0;
        for (MaintenanceDAO.BackupRecord backup : backups) {
            GridPane grid = rowGrid(widths, i++);
            grid.add(cell(backup.backupName(), TEXT_WHITE, true), 0, 0);
            grid.add(statusBadge(backup.status()), 1, 0);
            grid.add(cell(backup.filePath(), TEXT_MUTED, false), 2, 0);
            grid.add(cell(backup.createdAt() != null ? backup.createdAt().toString() : "", TEXT_MUTED, false), 3, 0);
            backupRows.getChildren().add(grid);
        }
    }

    private void refreshTools() {
        toolRows.getChildren().clear();
        boolean includeArchived = showArchived != null && showArchived.isSelected();
        List<MaintenanceDAO.ToolRecord> tools = dao.findTools(includeArchived);
        if (tools.isEmpty()) {
            toolRows.getChildren().add(emptyLabel("No tools found."));
            return;
        }
        double[] widths = {16, 16, 9, 11, 30, 18};
        int i = 0;
        for (MaintenanceDAO.ToolRecord tool : tools) {
            GridPane grid = rowGrid(widths, i++);
            grid.add(cell(tool.toolName(), TEXT_WHITE, true), 0, 0);
            grid.add(cell(tool.moduleName(), TEXT_MUTED, false), 1, 0);
            grid.add(cell(tool.version(), INFO, true), 2, 0);
            grid.add(statusBadge(tool.status()), 3, 0);
            grid.add(cell(tool.description(), TEXT_MUTED, false), 4, 0);

            HBox actions = new HBox(8);
            Button edit = smallButton("Edit", WARNING);
            edit.setOnAction(e -> showToolDialog(tool));
            boolean archived = "Archived".equalsIgnoreCase(tool.status());
            Button archive = smallButton(archived ? "Restore" : "Archive", archived ? SUCCESS : ACCENT);
            archive.setOnAction(e -> {
                dao.setToolArchived(tool.toolId(), !archived, AppSession.currentUser().userId());
                refreshTools();
            });
            actions.getChildren().addAll(edit, archive);
            grid.add(actions, 5, 0);
            toolRows.getChildren().add(grid);
        }
    }

    private void showToolDialog(MaintenanceDAO.ToolRecord existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Tool" : "Update Tool");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setPrefWidth(520);
        box.setStyle("-fx-background-color: " + BG_MAIN + ";");

        TextField name = styledField(existing != null ? existing.toolName() : "");
        TextField module = styledField(existing != null ? existing.moduleName() : "");
        TextField version = styledField(existing != null ? existing.version() : "1.0");
        TextArea description = new TextArea(existing != null ? existing.description() : "");
        description.setPrefRowCount(4);
        description.setWrapText(true);
        description.setStyle("-fx-control-inner-background: " + BG_CARD + "; -fx-text-fill: " + TEXT_WHITE + "; -fx-font-family: Poppins;");

        box.getChildren().addAll(
            labeled("TOOL NAME", name),
            labeled("MODULE", module),
            labeled("VERSION", version),
            labeled("DESCRIPTION", description)
        );
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setStyle("-fx-background-color: " + BG_MAIN + ";");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        if (name.getText().isBlank() || module.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Tool name and module are required.").showAndWait();
            return;
        }
        boolean ok;
        if (existing == null) {
            ok = dao.addTool(name.getText().trim(), module.getText().trim(), description.getText().trim(), version.getText().trim(), AppSession.currentUser().userId());
        } else {
            ok = dao.updateTool(new MaintenanceDAO.ToolRecord(
                existing.toolId(),
                name.getText().trim(),
                module.getText().trim(),
                description.getText().trim(),
                existing.status(),
                version.getText().trim(),
                existing.updatedBy(),
                existing.createdAt(),
                existing.updatedAt()
            ), AppSession.currentUser().userId());
        }
        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "Could not save tool.").showAndWait();
        }
        refreshTools();
    }

    private VBox sectionCard(String title, String subtitle) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 22; -fx-border-color: " + BORDER + "; -fx-border-radius: 22; -fx-border-width: 1;");
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000", 0.25));
        ds.setRadius(10);
        ds.setOffsetY(4);
        card.setEffect(ds);
        Text t = new Text(title);
        t.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        t.setFill(Color.web(TEXT_WHITE));
        Text s = new Text(subtitle);
        s.setFont(Font.font("Poppins", 11));
        s.setFill(Color.web(TEXT_MUTED));
        card.getChildren().addAll(t, s);
        return card;
    }

    private HBox rightAligned(Button button) {
        HBox box = new HBox(button);
        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    private Text bodyText(String value) {
        Text text = new Text(value);
        text.setFont(Font.font("Poppins", 11));
        text.setFill(Color.web(TEXT_MUTED));
        text.setWrappingWidth(440);
        return text;
    }

    private HBox tableHeader(String[] headers, double[] widths) {
        HBox wrap = new HBox();
        wrap.setPadding(new Insets(10, 16, 10, 16));
        wrap.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");
        GridPane grid = makeGrid(widths);
        HBox.setHgrow(grid, Priority.ALWAYS);
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i].toUpperCase());
            h.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
            h.setTextFill(Color.web(TEXT_DIM));
            grid.add(h, i, 0);
        }
        wrap.getChildren().add(grid);
        return wrap;
    }

    private GridPane rowGrid(double[] widths, int index) {
        GridPane grid = makeGrid(widths);
        grid.setPadding(new Insets(11, 16, 11, 16));
        grid.setStyle("-fx-background-color: " + (index % 2 == 0 ? BG_CARD : BG_ROW_ALT) + ";");
        return grid;
    }

    private GridPane makeGrid(double[] widths) {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        for (double width : widths) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(width);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }
        return grid;
    }

    private Label cell(String text, String color, boolean bold) {
        Label label = new Label(text == null ? "" : text);
        label.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 11));
        label.setTextFill(Color.web(color));
        label.setWrapText(true);
        return label;
    }

    private Label statusBadge(String status) {
        String value = status == null ? "Unknown" : status;
        String color = switch (value) {
            case "Created", "Active", "Restored" -> SUCCESS;
            case "Archived" -> WARNING;
            case "Failed" -> ACCENT;
            default -> INFO;
        };
        Label badge = new Label(value);
        badge.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        badge.setTextFill(Color.web(color));
        badge.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 18; -fx-padding: 3 9 3 9;");
        return badge;
    }

    private Label emptyLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web(TEXT_MUTED));
        label.setPadding(new Insets(16));
        return label;
    }

    private Button accentButton(String text) {
        Button b = new Button(text);
        b.setPrefHeight(38);
        b.setPadding(new Insets(0, 18, 0, 18));
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + ACCENT_DARK + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 16; -fx-cursor: hand;"));
        return b;
    }

    private Button smallButton(String text, String color) {
        Button b = new Button(text);
        b.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
        b.setStyle("-fx-background-color: transparent; -fx-border-color: " + color + "; -fx-border-radius: 16; -fx-text-fill: " + color + "; -fx-cursor: hand;");
        return b;
    }

    private TextField styledField(String text) {
        TextField field = new TextField(text);
        field.setPrefHeight(40);
        field.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-text-fill: " + TEXT_WHITE + "; -fx-prompt-text-fill: " + TEXT_DIM + "; -fx-padding: 0 12 0 12; -fx-font-family: Poppins;");
        return field;
    }

    private VBox labeled(String label, javafx.scene.Node input) {
        VBox box = new VBox(6);
        Label l = new Label(label);
        l.setFont(Font.font("Poppins", FontWeight.BOLD, 9));
        l.setTextFill(Color.web(TEXT_MUTED));
        box.getChildren().addAll(l, input);
        return box;
    }
}



