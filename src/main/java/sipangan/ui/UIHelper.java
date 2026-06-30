package sipangan.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UIHelper {

    /** Page header bar with title + subtitle */
    public static HBox buildPageHeader(String title, String subtitle) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(24, 32, 24, 32));
        bar.setStyle(
            "-fx-background-color: " + Theme.C_SURFACE + "; " +
            "-fx-border-color: transparent transparent " + Theme.C_BORDER + " transparent; " +
            "-fx-border-width: 0 0 1 0;"
        );
        VBox text = new VBox(4);
        Label lTitle = new Label(title);
        lTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + Theme.C_TEXT1 + ";");
        Label lSub   = new Label(subtitle);
        lSub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Theme.C_TEXT2 + ";");
        text.getChildren().addAll(lTitle, lSub);
        bar.getChildren().add(text);
        return bar;
    }

    /** Toolbar card wrapping buttons */
    public static HBox buildToolbarCard(Button... buttons) {
        HBox outerBar = new HBox();
        outerBar.setStyle(
            "-fx-background-color: " + Theme.C_SURFACE + "; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: " + Theme.C_BORDER + "; -fx-border-radius: 10; -fx-border-width: 1; " +
            "-fx-padding: 12 16;"
        );
        HBox btnRow = new HBox(8);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        btnRow.getChildren().addAll(buttons);
        outerBar.getChildren().add(btnRow);
        return outerBar;
    }

    public static Label dialogLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + Theme.C_TEXT1 + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        return l;
    }

    public static Label uiLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + Theme.C_TEXT2 + "; -fx-font-size: 12px;");
        return l;
    }

    public static TextField uiTextField(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setPrefWidth(260);
        tf.setStyle(
            "-fx-background-color: " + Theme.C_SURFACE2 + "; -fx-text-fill: " + Theme.C_TEXT1 + "; " +
            "-fx-border-color: " + Theme.C_BORDER + "; -fx-border-radius: 7; " +
            "-fx-background-radius: 7; -fx-padding: 8 10; -fx-font-size: 13px;"
        );
        return tf;
    }

    public static Button uiBtn(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + bgColor + "; -fx-text-fill: white; " +
            "-fx-background-radius: 7; -fx-padding: 8 16; " +
            "-fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;"
        );
        btn.setOnMouseEntered(e -> btn.setOpacity(0.87));
        btn.setOnMouseExited(e  -> btn.setOpacity(1.0));
        return btn;
    }

    public static void popupInfo(String judul, String pesan) {
        popupDialog(judul, pesan, Alert.AlertType.INFORMATION);
    }

    public static void popupWarn(String pesan) {
        popupDialog("Peringatan", pesan, Alert.AlertType.WARNING);
    }

    public static void popupDialog(String judul, String pesan, Alert.AlertType tipe) {
        Alert a = new Alert(tipe, pesan, ButtonType.OK);
        a.setTitle(judul); a.setHeaderText(null); a.showAndWait();
    }

    public static boolean popupKonfirmasi(String judul, String pesan) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, pesan, ButtonType.YES, ButtonType.NO);
        a.setTitle(judul); a.setHeaderText(null);
        return a.showAndWait().map(r -> r == ButtonType.YES).orElse(false);
    }
}
