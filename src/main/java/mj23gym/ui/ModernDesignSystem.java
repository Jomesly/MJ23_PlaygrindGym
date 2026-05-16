package mj23gym.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Modern Design System for MJ23 Playgrind Gym
 * Instagram-inspired aesthetics with rounded corners, modern shapes, and harmonious colors
 * 
 * Color Palette:
 * - Primary: #1A1363 (Deep Purple)
 * - Yellow Accent: #FDEE21 (Vibrant Yellow)
 * - Light Yellow: #FFFF7D (Pale Yellow)
 * - Dark Gray: #4B4B4B & #332F4
 * - Muted Purple: #77749B (Text)
 * - Light Gray: #ECE9E9 (Background)
 * - Light Green: #E4FFDF (Success/Accent)
 * - White: #FFFFFF
 */
public class ModernDesignSystem {

    // ========== COLOR PALETTE ==========
    public static final String PRIMARY           = "#1A1363";      // Deep Purple
    public static final String PRIMARY_DARK      = "#0F0D47";      // Darker purple
    public static final String ACCENT_YELLOW     = "#FDEE21";      // Vibrant yellow
    public static final String ACCENT_YELLOW_LT  = "#FFFF7D";      // Light yellow
    public static final String DARK_GRAY         = "#4B4B4B";      // Dark gray
    public static final String DARK_GRAY_LT      = "#332F4F";      // Lighter dark gray
    public static final String TEXT_MUTED        = "#77749B";      // Muted purple
    public static final String BG_LIGHT          = "#ECE9E9";      // Light background
    public static final String SUCCESS           = "#E4FFDF";      // Light green
    public static final String WHITE             = "#FFFFFF";      // White
    
    // Additional palettes for complementary colors
    public static final String SIDEBAR_BG        = "#F5F3F9";      // Soft purple background
    public static final String CARD_BG           = "#FAFAF9";      // Near white with warmth
    public static final String BORDER_COLOR      = "#E8E6EB";      // Subtle border
    public static final String HOVER_EFFECT      = "#F0ECFF";      // Hover state
    public static final String SHADOW_COLOR      = "#1A1363";      // Shadow uses primary

    // ========== FONT SYSTEM ==========
    public static final String FONT_FAMILY       = "Poppins";
    public static final int FONT_LARGE_TITLE     = 32;
    public static final int FONT_TITLE           = 24;
    public static final int FONT_SUBTITLE        = 18;
    public static final int FONT_BODY_LARGE      = 14;
    public static final int FONT_BODY             = 12;
    public static final int FONT_SMALL           = 10;

    // ========== SPACING & RADIUS ==========
    public static final int RADIUS_LARGE         = 20;            // Large rounded corners
    public static final int RADIUS_MEDIUM        = 12;            // Medium rounded corners
    public static final int RADIUS_SMALL         = 8;             // Small rounded corners
    public static final int SPACING_XL           = 24;            // Extra large spacing
    public static final int SPACING_L            = 16;            // Large spacing
    public static final int SPACING_M            = 12;            // Medium spacing
    public static final int SPACING_S            = 8;             // Small spacing
    public static final int SPACING_XS           = 4;             // Extra small spacing

    // ========== SHADOW EFFECTS ==========
    public static DropShadow createElevation1() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(SHADOW_COLOR, 0.08));
        shadow.setRadius(2);
        shadow.setOffsetY(1);
        return shadow;
    }

    public static DropShadow createElevation2() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(SHADOW_COLOR, 0.12));
        shadow.setRadius(4);
        shadow.setOffsetY(2);
        return shadow;
    }

    public static DropShadow createElevation3() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(SHADOW_COLOR, 0.16));
        shadow.setRadius(8);
        shadow.setOffsetY(4);
        return shadow;
    }

    public static DropShadow createElevation4() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(SHADOW_COLOR, 0.20));
        shadow.setRadius(12);
        shadow.setOffsetY(6);
        return shadow;
    }

    // ========== BUTTON STYLES ==========
    public static Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(26, 19, 99, 0.15), 8, 0.0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + PRIMARY_DARK + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(26, 19, 99, 0.25), 12, 0.0, 0, 4);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(26, 19, 99, 0.15), 8, 0.0, 0, 2);"
        ));
        
        return button;
    }

    public static Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + ACCENT_YELLOW + ";" +
            "-fx-text-fill: " + PRIMARY + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(253, 238, 33, 0.20), 8, 0.0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + ACCENT_YELLOW_LT + ";" +
            "-fx-text-fill: " + PRIMARY + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(253, 238, 33, 0.30), 12, 0.0, 0, 4);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + ACCENT_YELLOW + ";" +
            "-fx-text-fill: " + PRIMARY + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(253, 238, 33, 0.20), 8, 0.0, 0, 2);"
        ));
        
        return button;
    }

    public static Button createOutlineButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + PRIMARY + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-color: " + PRIMARY + ";" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + HOVER_EFFECT + ";" +
            "-fx-text-fill: " + PRIMARY + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-color: " + PRIMARY + ";" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + PRIMARY + ";" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY_LARGE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28 12 28;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-color: " + PRIMARY + ";" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;"
        ));
        
        return button;
    }

    // ========== TEXT FIELD STYLES ==========
    public static TextField createModernTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY + ";" +
            "-fx-padding: 12 16 12 16;" +
            "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-text-fill: " + DARK_GRAY + ";" +
            "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(26, 19, 99, 0.05), 4, 0.0, 0, 1);"
        );
        
        // Focused state
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle(
                    "-fx-font-family: '" + FONT_FAMILY + "';" +
                    "-fx-font-size: " + FONT_BODY + ";" +
                    "-fx-padding: 12 16 12 16;" +
                    "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-border-color: " + PRIMARY + ";" +
                    "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-text-fill: " + DARK_GRAY + ";" +
                    "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
                    "-fx-effect: dropshadow(gaussian, rgba(26, 19, 99, 0.12), 8, 0.0, 0, 2);"
                );
            } else {
                textField.setStyle(
                    "-fx-font-family: '" + FONT_FAMILY + "';" +
                    "-fx-font-size: " + FONT_BODY + ";" +
                    "-fx-padding: 12 16 12 16;" +
                    "-fx-background-radius: " + RADIUS_MEDIUM + ";" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-border-color: " + BORDER_COLOR + ";" +
                    "-fx-border-radius: " + RADIUS_MEDIUM + ";" +
                    "-fx-border-width: 1.5;" +
                    "-fx-text-fill: " + DARK_GRAY + ";" +
                    "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
                    "-fx-effect: dropshadow(gaussian, rgba(26, 19, 99, 0.05), 4, 0.0, 0, 1);"
                );
            }
        });
        
        return textField;
    }

    // ========== CARD STYLES ==========
    public static StackPane createModernCard(Region content) {
        StackPane card = new StackPane();
        Rectangle background = new Rectangle();
        background.setFill(Color.web(CARD_BG));
        background.setArcWidth(RADIUS_LARGE);
        background.setArcHeight(RADIUS_LARGE);
        background.setEffect(createElevation2());
        
        card.getChildren().add(background);
        card.getChildren().add(content);
        card.setPadding(new Insets(SPACING_L));
        
        return card;
    }

    public static VBox createModernPanel() {
        VBox panel = new VBox();
        panel.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: " + RADIUS_LARGE + ";" +
            "-fx-padding: " + SPACING_L + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(26, 19, 99, 0.12), 8, 0.0, 0, 2);"
        );
        panel.setSpacing(SPACING_M);
        return panel;
    }

    // ========== LABEL STYLES ==========
    public static Label createHeading(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_TITLE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY + ";"
        );
        return label;
    }

    public static Label createSubheading(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_SUBTITLE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY + ";"
        );
        return label;
    }

    public static Label createBodyText(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY + ";" +
            "-fx-text-fill: " + DARK_GRAY + ";"
        );
        return label;
    }

    public static Label createMutedText(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-size: " + FONT_BODY + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );
        return label;
    }

    // ========== ACCENT BAR ==========
    public static Rectangle createAccentBar(int width, int height) {
        Rectangle bar = new Rectangle(width, height);
        bar.setFill(Color.web(ACCENT_YELLOW));
        bar.setArcWidth(height);
        bar.setArcHeight(height);
        return bar;
    }

    // ========== GRADIENT BACKGROUND ==========
    public static String getBackgroundStyle() {
        return "-fx-background-color: " + BG_LIGHT + ";";
    }

    public static String getSidebarStyle() {
        return "-fx-background-color: " + SIDEBAR_BG + ";";
    }
}
