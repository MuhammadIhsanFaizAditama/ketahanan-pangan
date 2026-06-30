package sipangan.ui;

import sipangan.SiPanganApp;
import sipangan.db.SiPanganDAO;
import sipangan.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

public class LoginView extends HBox {

    private final SiPanganApp app;

    public LoginView(SiPanganApp app) {
        this.app = app;
        setupUI();
    }

    private void setupUI() {
        this.setStyle("-fx-background-color: " + Theme.C_BG + ";");

        // ── LEFT PANEL — Branding ─────────────────────────────────────
        VBox leftPanel = new VBox(16);
        leftPanel.setPrefWidth(480);
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPadding(new Insets(64, 56, 64, 56));
        leftPanel.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1C2333, #253050);");

        Label badge = new Label("🌾  SiPangan");
        badge.setStyle(
                "-fx-background-color: rgba(59,110,248,0.2); " +
                        "-fx-text-fill: #7EA8FA; " +
                        "-fx-font-size: 13px; -fx-font-weight: bold; " +
                        "-fx-padding: 6 14; -fx-background-radius: 20;");

        Label heroTitle = new Label("Sistem Informasi\nStok Pangan\nNasional");
        heroTitle.setStyle(
                "-fx-font-size: 40px; -fx-font-weight: bold; " +
                        "-fx-text-fill: #FFFFFF; -fx-line-spacing: 4;");
        heroTitle.setWrapText(true);

        Label heroSub = new Label(
                "Pantau, kelola, dan distribusikan stok pangan\nnasional secara efisien dan real-time.");
        heroSub.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.C_SIDEBAR_TXT + "; -fx-line-spacing: 3;");
        heroSub.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Feature bullets
        VBox bullets = new VBox(10);
        String[][] features = {
                { "📊", "Dashboard stok real-time" },
                { "📦", "Manajemen produk pangan" },
                { "🏭", "Monitoring gudang wilayah" },
                { "📄", "Ekspor laporan ke PDF" },
        };
        for (String[] f : features) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label icon = new Label(f[0]);
            icon.setStyle("-fx-font-size: 16px;");
            Label txt = new Label(f[1]);
            txt.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Theme.C_SIDEBAR_TXT + ";");
            row.getChildren().addAll(icon, txt);
            bullets.getChildren().add(row);
        }

        leftPanel.getChildren().addAll(badge, heroTitle, heroSub, spacer, bullets);

        // ── RIGHT PANEL — Form ────────────────────────────────────────
        VBox rightPanel = new VBox();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(48));
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        rightPanel.setStyle("-fx-background-color: " + Theme.C_BG + ";");

        VBox formCard = new VBox(0);
        formCard.setMaxWidth(420);
        formCard.setMinWidth(360);
        formCard.setStyle(
                "-fx-background-color: " + Theme.C_SURFACE + "; " +
                        "-fx-background-radius: 16; " +
                        "-fx-border-color: " + Theme.C_BORDER + "; " +
                        "-fx-border-radius: 16; -fx-border-width: 1; " +
                        Theme.SHADOW_MD);

        // Card header
        VBox cardHeader = new VBox(6);
        cardHeader.setPadding(new Insets(32, 32, 24, 32));
        cardHeader.setStyle(
                "-fx-border-color: transparent transparent " + Theme.C_BORDER + " transparent; " +
                        "-fx-border-width: 0 0 1 0;");
        Label formTitle = new Label("Selamat Datang 👋");
        formTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + Theme.C_TEXT1 + ";");
        Label formSub = new Label("Masuk ke akun SiPangan Anda");
        formSub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Theme.C_TEXT2 + ";");
        cardHeader.getChildren().addAll(formTitle, formSub);

        // Card body
        VBox cardBody = new VBox(16);
        cardBody.setPadding(new Insets(28, 32, 32, 32));

        VBox fieldUser = buildFormField("Username", "Masukkan username...");
        TextField tfUsername = (TextField) fieldUser.getChildren().get(1);

        VBox fieldPass = buildFormFieldPass("Password", "Masukkan password...");
        PasswordField pfPassword = (PasswordField) fieldPass.getChildren().get(1);

        Label lblError = new Label();
        lblError.setStyle(
                "-fx-text-fill: " + Theme.C_DANGER + "; -fx-font-size: 12px; " +
                        "-fx-background-color: " + Theme.C_DANGER_BG + "; -fx-padding: 8 12; " +
                        "-fx-background-radius: 6;");
        lblError.setWrapText(true);
        lblError.setVisible(false);
        lblError.setManaged(false);

        Button btnLogin = new Button("Masuk →");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle(
                "-fx-background-color: " + Theme.C_PRIMARY + "; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 13; -fx-cursor: hand;");
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(
                "-fx-background-color: " + Theme.C_PRIMARY_DRK + "; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 13; -fx-cursor: hand;"));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(
                "-fx-background-color: " + Theme.C_PRIMARY + "; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 13; -fx-cursor: hand;"));

        Label lblHint = new Label("💡  Default: admin / admin123");
        lblHint.setStyle(
                "-fx-text-fill: " + Theme.C_TEXT3 + "; -fx-font-size: 11.5px; " +
                        "-fx-background-color: " + Theme.C_SURFACE2 + "; " +
                        "-fx-padding: 7 12; -fx-background-radius: 6;");

        btnLogin.setOnAction(ev -> {
            String u = tfUsername.getText().trim();
            String p = pfPassword.getText();
            if (u.isEmpty() || p.isEmpty()) {
                showError(lblError, "⚠  Username dan password tidak boleh kosong!");
                return;
            }
            lblError.setVisible(false);
            lblError.setManaged(false);
            User found = SiPanganDAO.login(u, p);
            if (found != null) {
                app.setCurrentUser(found);
                found.tampilkanInfo();
                app.showDashboardScene();
            } else {
                showError(lblError, "Username atau password salah. Silakan coba lagi.");
                pfPassword.clear();
                pfPassword.requestFocus();
            }
        });

        pfPassword.setOnAction(e -> btnLogin.fire());
        tfUsername.setOnAction(e -> pfPassword.requestFocus());

        cardBody.getChildren().addAll(fieldUser, fieldPass, lblError, btnLogin, lblHint);
        formCard.getChildren().addAll(cardHeader, cardBody);
        rightPanel.getChildren().add(formCard);

        this.getChildren().addAll(leftPanel, rightPanel);
    }

    private void showError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    private VBox buildFormField(String labelText, String placeholder) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Theme.C_TEXT1 + ";");
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setStyle(
                "-fx-background-color: " + Theme.C_SURFACE2 + "; -fx-text-fill: " + Theme.C_TEXT1 + "; " +
                        "-fx-border-color: " + Theme.C_BORDER + "; -fx-border-radius: 8; " +
                        "-fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 13px;");
        tf.focusedProperty().addListener((obs, old, focused) -> tf.setStyle(
                "-fx-background-color: " + (focused ? Theme.C_SURFACE : Theme.C_SURFACE2) + "; -fx-text-fill: "
                        + Theme.C_TEXT1 + "; " +
                        "-fx-border-color: " + (focused ? Theme.C_PRIMARY : Theme.C_BORDER) + "; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 13px;"));
        box.getChildren().addAll(lbl, tf);
        return box;
    }

    private VBox buildFormFieldPass(String labelText, String placeholder) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Theme.C_TEXT1 + ";");
        PasswordField pf = new PasswordField();
        pf.setPromptText(placeholder);
        pf.setStyle(
                "-fx-background-color: " + Theme.C_SURFACE2 + "; -fx-text-fill: " + Theme.C_TEXT1 + "; " +
                        "-fx-border-color: " + Theme.C_BORDER + "; -fx-border-radius: 8; " +
                        "-fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 13px;");
        pf.focusedProperty().addListener((obs, old, focused) -> pf.setStyle(
                "-fx-background-color: " + (focused ? Theme.C_SURFACE : Theme.C_SURFACE2) + "; -fx-text-fill: "
                        + Theme.C_TEXT1 + "; " +
                        "-fx-border-color: " + (focused ? Theme.C_PRIMARY : Theme.C_BORDER) + "; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 13px;"));
        box.getChildren().addAll(lbl, pf);
        return box;
    }
}
