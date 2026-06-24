package sipangan;

import sipangan.db.SiPanganDAO;
import sipangan.model.ProdukPangan;
import sipangan.model.Stok;
import sipangan.model.User;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// =========================================================================
// FILE    : SiPanganApp.java
// PACKAGE : sipangan
// KONSEP  : Kelas utama JavaFX — Clean & Modern UI Redesign
// Login Default: admin / admin123
// =========================================================================
public class SiPanganApp extends Application {

    // ── Instance Variables ────────────────────────────────────────────
    private Stage  primaryStage;
    private User   currentUser;

    private TableView<Stok>         stokTable;
    private TableView<ProdukPangan> produkTable;
    private ObservableList<Stok>    stokData;

    private Label dashTotalProdukValue;
    private Label dashStokKritisValue;
    private Label dashAlertLabel;

    // ── Design Tokens (Clean Neutral Palette) ─────────────────────────
    private static final String C_BG           = "#F7F8FA";
    private static final String C_SURFACE      = "#FFFFFF";
    private static final String C_SURFACE2     = "#F1F3F6";
    private static final String C_BORDER       = "#E4E7EE";
    private static final String C_BORDER_DARK  = "#CDD2DC";

    private static final String C_SIDEBAR_BG   = "#1C2333";
    private static final String C_SIDEBAR_HDR  = "#141A27";
    private static final String C_SIDEBAR_HOVER= "#252D42";
    private static final String C_SIDEBAR_TXT  = "#A8B3CF";
    private static final String C_SIDEBAR_MUTE = "#5A657E";

    private static final String C_PRIMARY      = "#3B6EF8";
    private static final String C_PRIMARY_DRK  = "#2A5CE4";
    private static final String C_SUCCESS      = "#12B886";
    private static final String C_SUCCESS_BG   = "#E6FBF4";
    private static final String C_DANGER       = "#F03E3E";
    private static final String C_DANGER_BG    = "#FFF5F5";
    private static final String C_WARNING      = "#F59F00";
    private static final String C_WARNING_BG   = "#FFF9DB";
    private static final String C_PURPLE       = "#7950F2";

    private static final String C_TEXT1        = "#1A1F36";
    private static final String C_TEXT2        = "#5A6480";
    private static final String C_TEXT3        = "#8D96AE";

    private static final String SHADOW_SM      =
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 3);";
    private static final String SHADOW_MD      =
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 24, 0, 0, 6);";

    // ── Entry Point ───────────────────────────────────────────────────
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("SiPangan — Sistem Informasi & Distribusi Stok Pangan");
        stage.setWidth(1180);
        stage.setHeight(740);
        stage.setMinWidth(960);
        stage.setMinHeight(620);
        showLoginScene();
        stage.show();
    }

    public static void main(String[] args) { launch(args); }


    // =========================================================================
    // SCENE 1 — LOGIN  (Split Layout: Branding | Form)
    // =========================================================================
    private void showLoginScene() {
        HBox root = new HBox();
        root.setStyle("-fx-background-color: " + C_BG + ";");

        // ── LEFT PANEL — Branding ─────────────────────────────────────
        VBox leftPanel = new VBox(16);
        leftPanel.setPrefWidth(480);
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPadding(new Insets(64, 56, 64, 56));
        leftPanel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #1C2333, #253050);"
        );

        Label badge = new Label("🌾  SiPangan");
        badge.setStyle(
            "-fx-background-color: rgba(59,110,248,0.2); " +
            "-fx-text-fill: #7EA8FA; " +
            "-fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-padding: 6 14; -fx-background-radius: 20;"
        );

        Label heroTitle = new Label("Sistem Informasi\nStok Pangan\nNasional");
        heroTitle.setStyle(
            "-fx-font-size: 40px; -fx-font-weight: bold; " +
            "-fx-text-fill: #FFFFFF; -fx-line-spacing: 4;"
        );
        heroTitle.setWrapText(true);

        Label heroSub = new Label(
            "Pantau, kelola, dan distribusikan stok pangan\nnasional secara efisien dan real-time."
        );
        heroSub.setStyle("-fx-font-size: 14px; -fx-text-fill: " + C_SIDEBAR_TXT + "; -fx-line-spacing: 3;");
        heroSub.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Feature bullets
        VBox bullets = new VBox(10);
        String[][] features = {
            {"📊", "Dashboard stok real-time"},
            {"📦", "Manajemen produk pangan"},
            {"🏭", "Monitoring gudang wilayah"},
            {"📄", "Ekspor laporan ke PDF"},
        };
        for (String[] f : features) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label icon = new Label(f[0]);
            icon.setStyle("-fx-font-size: 16px;");
            Label txt = new Label(f[1]);
            txt.setStyle("-fx-font-size: 13px; -fx-text-fill: " + C_SIDEBAR_TXT + ";");
            row.getChildren().addAll(icon, txt);
            bullets.getChildren().add(row);
        }

        leftPanel.getChildren().addAll(badge, heroTitle, heroSub, spacer, bullets);

        // ── RIGHT PANEL — Form ────────────────────────────────────────
        VBox rightPanel = new VBox();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(48));
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        rightPanel.setStyle("-fx-background-color: " + C_BG + ";");

        VBox formCard = new VBox(0);
        formCard.setMaxWidth(420);
        formCard.setMinWidth(360);
        formCard.setStyle(
            "-fx-background-color: " + C_SURFACE + "; " +
            "-fx-background-radius: 16; " +
            "-fx-border-color: " + C_BORDER + "; " +
            "-fx-border-radius: 16; -fx-border-width: 1; " +
            SHADOW_MD
        );

        // Card header
        VBox cardHeader = new VBox(6);
        cardHeader.setPadding(new Insets(32, 32, 24, 32));
        cardHeader.setStyle(
            "-fx-border-color: transparent transparent " + C_BORDER + " transparent; " +
            "-fx-border-width: 0 0 1 0;"
        );
        Label formTitle = new Label("Selamat Datang 👋");
        formTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + C_TEXT1 + ";");
        Label formSub   = new Label("Masuk ke akun SiPangan Anda");
        formSub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + C_TEXT2 + ";");
        cardHeader.getChildren().addAll(formTitle, formSub);

        // Card body
        VBox cardBody = new VBox(16);
        cardBody.setPadding(new Insets(28, 32, 32, 32));

        VBox fieldUser = buildFormField("Username", "Masukkan username...");
        TextField tfUsername = (TextField) ((VBox) fieldUser).getChildren().get(1);

        VBox fieldPass = buildFormFieldPass("Password", "Masukkan password...");
        PasswordField pfPassword = (PasswordField) ((VBox) fieldPass).getChildren().get(1);

        Label lblError = new Label();
        lblError.setStyle(
            "-fx-text-fill: " + C_DANGER + "; -fx-font-size: 12px; " +
            "-fx-background-color: " + C_DANGER_BG + "; -fx-padding: 8 12; " +
            "-fx-background-radius: 6;"
        );
        lblError.setWrapText(true);
        lblError.setVisible(false);
        lblError.setManaged(false);

        Button btnLogin = new Button("Masuk →");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle(
            "-fx-background-color: " + C_PRIMARY + "; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 13; -fx-cursor: hand;"
        );
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(
            "-fx-background-color: " + C_PRIMARY_DRK + "; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 13; -fx-cursor: hand;"
        ));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(
            "-fx-background-color: " + C_PRIMARY + "; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 13; -fx-cursor: hand;"
        ));

        Label lblHint = new Label("💡  Default: admin / admin123");
        lblHint.setStyle(
            "-fx-text-fill: " + C_TEXT3 + "; -fx-font-size: 11.5px; " +
            "-fx-background-color: " + C_SURFACE2 + "; " +
            "-fx-padding: 7 12; -fx-background-radius: 6;"
        );

        btnLogin.setOnAction(ev -> {
            String u = tfUsername.getText().trim();
            String p = pfPassword.getText();
            if (u.isEmpty() || p.isEmpty()) {
                showError(lblError, "⚠  Username dan password tidak boleh kosong!");
                return;
            }
            lblError.setVisible(false); lblError.setManaged(false);
            User found = SiPanganDAO.login(u, p);
            if (found != null) {
                currentUser = found;
                currentUser.tampilkanInfo();
                showDashboardScene();
            } else {
                showError(lblError, "⚠  Username atau password salah. Silakan coba lagi.");
                pfPassword.clear();
                pfPassword.requestFocus();
            }
        });

        pfPassword.setOnAction(e -> btnLogin.fire());
        tfUsername.setOnAction(e -> pfPassword.requestFocus());

        cardBody.getChildren().addAll(fieldUser, fieldPass, lblError, btnLogin, lblHint);
        formCard.getChildren().addAll(cardHeader, cardBody);
        rightPanel.getChildren().add(formCard);

        root.getChildren().addAll(leftPanel, rightPanel);
        primaryStage.setScene(new Scene(root));
    }

    private void showError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    // Builds a labeled form field (VBox with Label + TextField)
    private VBox buildFormField(String labelText, String placeholder) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + C_TEXT1 + ";");
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setStyle(
            "-fx-background-color: " + C_SURFACE2 + "; -fx-text-fill: " + C_TEXT1 + "; " +
            "-fx-border-color: " + C_BORDER + "; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 13px;"
        );
        tf.focusedProperty().addListener((obs, old, focused) ->
            tf.setStyle(
                "-fx-background-color: " + C_SURFACE + "; -fx-text-fill: " + C_TEXT1 + "; " +
                "-fx-border-color: " + (focused ? C_PRIMARY : C_BORDER) + "; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 13px;"
            )
        );
        box.getChildren().addAll(lbl, tf);
        return box;
    }

    private VBox buildFormFieldPass(String labelText, String placeholder) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + C_TEXT1 + ";");
        PasswordField pf = new PasswordField();
        pf.setPromptText(placeholder);
        pf.setStyle(
            "-fx-background-color: " + C_SURFACE2 + "; -fx-text-fill: " + C_TEXT1 + "; " +
            "-fx-border-color: " + C_BORDER + "; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 13px;"
        );
        pf.focusedProperty().addListener((obs, old, focused) ->
            pf.setStyle(
                "-fx-background-color: " + C_SURFACE + "; -fx-text-fill: " + C_TEXT1 + "; " +
                "-fx-border-color: " + (focused ? C_PRIMARY : C_BORDER) + "; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 13px;"
            )
        );
        box.getChildren().addAll(lbl, pf);
        return box;
    }


    // =========================================================================
    // SCENE 2 — DASHBOARD (Sidebar + Content)
    // =========================================================================
    private void showDashboardScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + C_BG + ";");

        VBox panelDashboard = buildDashboardPanel();
        VBox panelProduk    = buildProdukPanel();
        VBox panelStok      = buildStokPanel();

        StackPane contentArea = new StackPane(panelStok, panelProduk, panelDashboard);
        VBox sidebar = buildSidebar(panelDashboard, panelProduk, panelStok);

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();
        refreshDashboard();
        refreshStokTable();
    }

    // ── Sidebar ───────────────────────────────────────────────────────
    private VBox buildSidebar(VBox dashPanel, VBox produkPanel, VBox stokPanel) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + C_SIDEBAR_BG + ";");

        // Header / Logo
        VBox logoBox = new VBox(4);
        logoBox.setPadding(new Insets(22, 20, 22, 20));
        logoBox.setStyle("-fx-background-color: " + C_SIDEBAR_HDR + ";");
        Label logo    = new Label("🌾  SiPangan");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
        Label logoSub = new Label("Stok Pangan Nasional");
        logoSub.setStyle("-fx-font-size: 10.5px; -fx-text-fill: " + C_SIDEBAR_MUTE + ";");
        logoBox.getChildren().addAll(logo, logoSub);

        // User info card
        HBox userBox = new HBox(12);
        userBox.setPadding(new Insets(14, 20, 14, 20));
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04); " +
            "-fx-border-color: transparent transparent " + C_SIDEBAR_MUTE + " transparent; " +
            "-fx-border-width: 0 0 1 0;"
        );

        // Avatar circle with initial
        StackPane avatar = new StackPane();
        Circle circle = new Circle(18);
        circle.setStyle("-fx-fill: " + C_PRIMARY + ";");
        String initial = (currentUser != null && currentUser.getNama() != null && !currentUser.getNama().isEmpty())
            ? currentUser.getNama().substring(0, 1).toUpperCase() : "U";
        Label initLbl = new Label(initial);
        initLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        avatar.getChildren().addAll(circle, initLbl);

        VBox userInfo = new VBox(2);
        Label userName   = new Label(currentUser != null ? currentUser.getNama() : "—");
        userName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
        Label userHandle = new Label("@" + (currentUser != null ? currentUser.getUsername() : "—"));
        userHandle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + C_SIDEBAR_MUTE + ";");
        userInfo.getChildren().addAll(userName, userHandle);
        userBox.getChildren().addAll(avatar, userInfo);

        // Nav section label
        Label navSectionLbl = new Label("NAVIGASI");
        navSectionLbl.setStyle(
            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + C_SIDEBAR_MUTE + "; " +
            "-fx-padding: 16 20 6 20;"
        );

        // Nav buttons
        Button[] navBtns = {
            buildNavBtn("📊", "Dashboard",      true),
            buildNavBtn("📦", "Produk Pangan",  false),
            buildNavBtn("🏭", "Manajemen Stok", false),
        };
        navBtns[0].setOnAction(e -> { setActiveNav(navBtns, 0); dashPanel.toFront();   refreshDashboard();   });
        navBtns[1].setOnAction(e -> { setActiveNav(navBtns, 1); produkPanel.toFront(); refreshProdukTable(); });
        navBtns[2].setOnAction(e -> { setActiveNav(navBtns, 2); stokPanel.toFront();   refreshStokTable();   });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout
        Label bottomSep = new Label();
        bottomSep.setStyle(
            "-fx-background-color: " + C_SIDEBAR_MUTE + "; -fx-min-height: 1; -fx-max-height: 1; " +
            "-fx-padding: 0; -fx-pref-height: 1;"
        );
        bottomSep.setMaxWidth(Double.MAX_VALUE);

        Button btnLogout = buildNavBtn("🚪", "Keluar / Logout", false);
        btnLogout.setStyle(btnLogout.getStyle()
            .replace("-fx-text-fill: " + C_SIDEBAR_TXT, "-fx-text-fill: #FC8181"));
        btnLogout.setOnAction(e -> { currentUser = null; showLoginScene(); });

        sidebar.getChildren().addAll(
            logoBox, userBox, navSectionLbl,
            navBtns[0], navBtns[1], navBtns[2],
            spacer, bottomSep, btnLogout
        );
        return sidebar;
    }

    private Button buildNavBtn(String icon, String label, boolean active) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        applyNavBtnStyle(btn, icon, label, active);
        return btn;
    }

    private void applyNavBtnStyle(Button btn, String icon, String label, boolean active) {
        btn.setText(icon + "   " + label);
        btn.setStyle(
            "-fx-background-color: " + (active ? C_SIDEBAR_HOVER : "transparent") + "; " +
            "-fx-text-fill: "         + (active ? "#FFFFFF" : C_SIDEBAR_TXT) + "; " +
            "-fx-font-size: 13px; -fx-padding: 12 20; " +
            "-fx-background-radius: 0; -fx-cursor: hand; " +
            "-fx-border-color: " + (active ? C_PRIMARY : "transparent") + " transparent transparent transparent; " +
            "-fx-border-width: 0 0 0 3;"
        );
        if (!active) {
            btn.setOnMouseEntered(e -> {
                if (!btn.getStyle().contains(C_SIDEBAR_HOVER))
                    btn.setStyle(btn.getStyle().replace("transparent;", C_SIDEBAR_HOVER + ";").replace(
                        "-fx-text-fill: " + C_SIDEBAR_TXT, "-fx-text-fill: #FFFFFF"));
            });
            btn.setOnMouseExited(e -> {
                if (!btn.getStyle().contains(C_PRIMARY + " transparent"))
                    btn.setStyle(btn.getStyle().replace(C_SIDEBAR_HOVER + ";", "transparent;").replace(
                        "-fx-text-fill: #FFFFFF", "-fx-text-fill: " + C_SIDEBAR_TXT));
            });
        }
    }

    private void setActiveNav(Button[] btns, int activeIdx) {
        String[] icons   = {"📊", "📦", "🏭"};
        String[] labels  = {"Dashboard", "Produk Pangan", "Manajemen Stok"};
        for (int i = 0; i < btns.length; i++) {
            applyNavBtnStyle(btns[i], icons[i], labels[i], i == activeIdx);
        }
    }


    // =========================================================================
    // PANEL A — DASHBOARD
    // =========================================================================
    private VBox buildDashboardPanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(0));
        panel.setStyle("-fx-background-color: " + C_BG + ";");

        // Page Header bar
        HBox header = buildPageHeader("📊  Dashboard",
            "Ringkasan kondisi stok pangan nasional secara real-time");

        // Content scrollable area
        VBox content = new VBox(24);
        content.setPadding(new Insets(32));

        // Stat cards
        dashTotalProdukValue = new Label("—");
        dashStokKritisValue  = new Label("—");
        Label dummyGudang    = new Label("7");

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            buildStatCard("📦", "Total Produk",  dashTotalProdukValue, C_PRIMARY),
            buildStatCard("⚠️", "Stok Kritis",   dashStokKritisValue,  C_DANGER),
            buildStatCard("🏭", "Gudang Aktif",  dummyGudang,          C_SUCCESS)
        );

        // Alert panel
        VBox alertBox = new VBox(14);
        alertBox.setPadding(new Insets(22, 24, 24, 24));
        alertBox.setStyle(
            "-fx-background-color: " + C_SURFACE + "; -fx-background-radius: 12; " +
            "-fx-border-color: " + C_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1; " +
            SHADOW_SM
        );

        HBox alertHeader = new HBox(10);
        alertHeader.setAlignment(Pos.CENTER_LEFT);
        Label alertIcon  = new Label("⚠️");
        alertIcon.setStyle("-fx-font-size: 16px;");
        Label alertTitle = new Label("Peringatan Stok Kritis");
        alertTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + C_TEXT1 + ";");
        alertHeader.getChildren().addAll(alertIcon, alertTitle);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + C_BORDER + ";");

        dashAlertLabel = new Label("Memuat data...");
        dashAlertLabel.setWrapText(true);
        dashAlertLabel.setStyle("-fx-text-fill: " + C_TEXT2 + "; -fx-font-size: 13px; -fx-line-spacing: 4;");

        alertBox.getChildren().addAll(alertHeader, sep, dashAlertLabel);
        content.getChildren().addAll(statsRow, alertBox);

        panel.getChildren().addAll(header, content);
        VBox.setVgrow(content, Priority.ALWAYS);
        return panel;
    }

    private void refreshDashboard() {
        if (dashTotalProdukValue == null) return;
        dashTotalProdukValue.setText(String.valueOf(SiPanganDAO.countProduk()));
        dashStokKritisValue.setText(String.valueOf(SiPanganDAO.countStokKritis()));

        List<Stok> all = SiPanganDAO.getAllStok();
        StringBuilder sb = new StringBuilder();
        for (Stok s : all) {
            if (s.isBawahMinimum()) {
                sb.append(String.format("• %-20s  [%s]  →  %d unit  (Min: %d)%n",
                    s.getProduk().getNamaProduk(), s.getGudangWilayah(),
                    s.getKuantitas(), s.getBatasMinimum()));
            }
        }
        if (sb.length() > 0) {
            dashAlertLabel.setText(sb.toString().trim());
            dashAlertLabel.setStyle("-fx-text-fill: " + C_DANGER + "; -fx-font-size: 13px; -fx-line-spacing: 4;");
        } else {
            dashAlertLabel.setText("✅  Semua stok dalam kondisi aman. Tidak ada produk yang perlu dikhawatirkan.");
            dashAlertLabel.setStyle("-fx-text-fill: " + C_SUCCESS + "; -fx-font-size: 13px; -fx-line-spacing: 4;");
        }
    }


    // =========================================================================
    // PANEL B — MANAJEMEN PRODUK
    // =========================================================================
    private VBox buildProdukPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: " + C_BG + ";");

        HBox header = buildPageHeader("📦  Produk Pangan", "Kelola data produk pangan yang tersedia dalam sistem");

        VBox content = new VBox(16);
        content.setPadding(new Insets(28, 32, 32, 32));
        VBox.setVgrow(content, Priority.ALWAYS);

        produkTable = new TableView<>();
        produkTable.setStyle(
            "-fx-background-color: " + C_SURFACE + "; " +
            "-fx-border-color: " + C_BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-table-header-border-color: transparent;"
        );
        produkTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        produkTable.setFixedCellSize(42);
        VBox.setVgrow(produkTable, Priority.ALWAYS);

        TableColumn<ProdukPangan, Integer> colId   = pColI("ID",               50);
        TableColumn<ProdukPangan, String>  colNama = pColS("Nama Produk",      220);
        TableColumn<ProdukPangan, String>  colKat  = pColS("Kategori",         160);
        TableColumn<ProdukPangan, String>  colTgl  = pColS("Tgl. Kedaluwarsa", 150);

        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdProduk()).asObject());
        colNama.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNamaProduk()));
        colKat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getKategori()));
        colTgl.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTanggalKedaluwarsa()));

        produkTable.getColumns().addAll(colId, colNama, colKat, colTgl);

        // Toolbar
        HBox toolbar = buildToolbarCard(
            uiBtn("➕  Tambah",  C_SUCCESS),
            uiBtn("✏️  Edit",    C_PRIMARY),
            uiBtn("🗑️  Hapus",   C_DANGER),
            uiBtn("🔄  Refresh", C_TEXT2)
        );
        Button btnTambah  = (Button) ((HBox) toolbar.getChildren().get(0)).getChildren().get(0);
        Button btnEdit    = (Button) ((HBox) toolbar.getChildren().get(0)).getChildren().get(1);
        Button btnHapus   = (Button) ((HBox) toolbar.getChildren().get(0)).getChildren().get(2);
        Button btnRefresh = (Button) ((HBox) toolbar.getChildren().get(0)).getChildren().get(3);

        btnTambah.setOnAction(e -> {
            ProdukPangan baru = tampilDialogProduk(null);
            if (baru != null && SiPanganDAO.tambahProduk(baru)) {
                refreshProdukTable(); popupInfo("Berhasil", "Produk berhasil ditambahkan!");
            }
        });
        btnEdit.setOnAction(e -> {
            ProdukPangan sel = produkTable.getSelectionModel().getSelectedItem();
            if (sel == null) { popupWarn("Pilih baris produk yang ingin diedit!"); return; }
            ProdukPangan edited = tampilDialogProduk(sel);
            if (edited != null && SiPanganDAO.updateProduk(edited)) {
                refreshProdukTable(); popupInfo("Berhasil", "Produk berhasil diperbarui!");
            }
        });
        btnHapus.setOnAction(e -> {
            ProdukPangan sel = produkTable.getSelectionModel().getSelectedItem();
            if (sel == null) { popupWarn("Pilih baris produk yang ingin dihapus!"); return; }
            if (popupKonfirmasi("Hapus Produk?",
                    "Hapus '" + sel.getNamaProduk() + "'?\nTidak bisa dihapus jika masih ada stok terkait.")) {
                if (SiPanganDAO.hapusProduk(sel.getIdProduk())) {
                    refreshProdukTable(); popupInfo("Berhasil", "Produk berhasil dihapus!");
                } else {
                    popupWarn("Gagal hapus. Pastikan tidak ada stok yang menggunakan produk ini.");
                }
            }
        });
        btnRefresh.setOnAction(e -> refreshProdukTable());

        content.getChildren().addAll(toolbar, produkTable);
        panel.getChildren().addAll(header, content);
        return panel;
    }

    private void refreshProdukTable() {
        if (produkTable != null)
            produkTable.setItems(FXCollections.observableArrayList(SiPanganDAO.getAllProduk()));
    }

    private ProdukPangan tampilDialogProduk(ProdukPangan existing) {
        Dialog<ProdukPangan> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Tambah Produk Baru" : "Edit Produk");
        dlg.setHeaderText(existing == null ? "Isi detail produk pangan baru"
                : "Perbarui: " + existing.getNamaProduk());

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(14);
        grid.setPadding(new Insets(24));

        TextField tfNama = uiTextField("Nama produk");
        TextField tfKat  = uiTextField("cth: Biji-bijian, Minyak, Tepung");
        TextField tfTgl  = uiTextField("Format: YYYY-MM-DD");

        if (existing != null) {
            tfNama.setText(existing.getNamaProduk());
            tfKat.setText(existing.getKategori());
            tfTgl.setText(existing.getTanggalKedaluwarsa());
        }

        grid.addRow(0, dialogLabel("Nama Produk :"),     tfNama);
        grid.addRow(1, dialogLabel("Kategori :"),        tfKat);
        grid.addRow(2, dialogLabel("Tgl Kedaluwarsa :"), tfTgl);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (tfNama.getText().trim().isEmpty()) { popupWarn("Nama produk tidak boleh kosong!"); return null; }
                ProdukPangan p = (existing != null) ? existing : new ProdukPangan(0, "", "", "");
                p.setNamaProduk(tfNama.getText().trim());
                p.setKategori(tfKat.getText().trim());
                p.setTanggalKedaluwarsa(tfTgl.getText().trim());
                return p;
            }
            return null;
        });
        return dlg.showAndWait().orElse(null);
    }


    // =========================================================================
    // PANEL C — MANAJEMEN STOK
    // =========================================================================
    private VBox buildStokPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: " + C_BG + ";");

        HBox header = buildPageHeader("🏭  Manajemen Stok Gudang", "Pantau dan kelola kuantitas stok di seluruh gudang wilayah");

        VBox content = new VBox(16);
        content.setPadding(new Insets(28, 32, 32, 32));
        VBox.setVgrow(content, Priority.ALWAYS);

        // Alert banner
        Label stokAlertBanner = new Label();
        stokAlertBanner.setWrapText(true);
        stokAlertBanner.setVisible(false);
        stokAlertBanner.setManaged(false);
        stokAlertBanner.setStyle(
            "-fx-background-color: " + C_DANGER_BG + "; -fx-text-fill: " + C_DANGER + "; " +
            "-fx-padding: 12 16; -fx-background-radius: 8; " +
            "-fx-border-color: #FEB2B2; -fx-border-radius: 8; -fx-border-width: 0 0 0 4; " +
            "-fx-font-size: 13px; -fx-font-weight: bold;"
        );

        // Table
        stokTable = new TableView<>();
        stokData  = FXCollections.observableArrayList();
        stokTable.setItems(stokData);
        stokTable.setStyle(
            "-fx-background-color: " + C_SURFACE + "; " +
            "-fx-border-color: " + C_BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        stokTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stokTable.setFixedCellSize(42);
        VBox.setVgrow(stokTable, Priority.ALWAYS);

        TableColumn<Stok, Integer> colId     = sColI("ID",              48);
        TableColumn<Stok, String>  colProduk = sColS("Nama Produk",   175);
        TableColumn<Stok, Integer> colQty    = sColI("Kuantitas",       90);
        TableColumn<Stok, Integer> colMin    = sColI("Batas Min.",      90);
        TableColumn<Stok, String>  colStatus = sColS("Status",        135);
        TableColumn<Stok, String>  colGudang = sColS("Gudang Wilayah",200);

        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdStok()).asObject());
        colProduk.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProduk().getNamaProduk()));
        colQty.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getKuantitas()).asObject());
        colMin.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getBatasMinimum()).asObject());
        colGudang.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGudangWilayah()));

        // Status chip cell
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatusKualitas()));
        colStatus.setCellFactory(col -> new TableCell<Stok, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null); return; }
                Label chip = new Label(item);
                String bg, fg;
                switch (item) {
                    case "LAYAK":
                        bg = C_SUCCESS_BG;
                        fg = C_SUCCESS;
                        break;
                    case "PERLU_CEPAT":
                        bg = C_WARNING_BG;
                        fg = C_WARNING;
                        break;
                    default:
                        bg = C_DANGER_BG;
                        fg = C_DANGER;
                        break;
                }
                chip.setStyle(
                    "-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; " +
                    "-fx-font-size: 11px; -fx-font-weight: bold; " +
                    "-fx-padding: 3 9; -fx-background-radius: 20;"
                );
                setGraphic(chip); setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        // Kuantitas coloring
        colQty.setCellFactory(col -> new TableCell<Stok, Integer>() {
            @Override protected void updateItem(Integer val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); return; }
                setText(String.valueOf(val));
                Stok row = getTableRow() != null ? (Stok) getTableRow().getItem() : null;
                if (row != null && row.isBawahMinimum()) {
                    setStyle("-fx-text-fill: " + C_DANGER + "; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: " + C_TEXT1 + ";");
                }
            }
        });

        // Row factory — kritis highlight
        stokTable.setRowFactory(tv -> new TableRow<Stok>() {
            @Override protected void updateItem(Stok item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && item.isBawahMinimum()) {
                    setStyle("-fx-background-color: " + C_DANGER_BG + ";");
                } else {
                    setStyle("");
                }
            }
        });

        stokTable.getColumns().addAll(colId, colProduk, colQty, colMin, colStatus, colGudang);

        // Toolbar
        HBox toolbar = buildToolbarCard(
            uiBtn("➕  Tambah",      C_SUCCESS),
            uiBtn("➖  Kurangi",     C_DANGER),
            uiBtn("✏️  Edit",        C_PRIMARY),
            uiBtn("🗑️  Hapus",       "#B91C1C"),
            uiBtn("📄  Cetak PDF",   C_PURPLE),
            uiBtn("🔄  Refresh",     C_TEXT2)
        );
        HBox btnRow    = (HBox) toolbar.getChildren().get(0);
        Button btnTambah  = (Button) btnRow.getChildren().get(0);
        Button btnKurangi = (Button) btnRow.getChildren().get(1);
        Button btnEdit    = (Button) btnRow.getChildren().get(2);
        Button btnHapus   = (Button) btnRow.getChildren().get(3);
        Button btnLaporan = (Button) btnRow.getChildren().get(4);
        Button btnRefresh = (Button) btnRow.getChildren().get(5);

        btnTambah.setOnAction(e -> {
            List<ProdukPangan> produkList = SiPanganDAO.getAllProduk();
            if (produkList.isEmpty()) { popupWarn("Tambah produk terlebih dahulu!"); return; }
            Stok baru = tampilDialogStok(null, produkList);
            if (baru != null && SiPanganDAO.tambahStok(baru)) {
                refreshStokTable(); popupInfo("Berhasil", "Stok berhasil ditambahkan!");
            }
        });

        btnKurangi.setOnAction(e -> {
            Stok dipilih = stokTable.getSelectionModel().getSelectedItem();
            if (dipilih == null) { popupWarn("Pilih baris stok terlebih dahulu!"); return; }
            TextInputDialog dlg = new TextInputDialog("10");
            dlg.setTitle("Kurangi Stok");
            dlg.setHeaderText("Produk    : " + dipilih.getProduk().getNamaProduk() +
                              "\nGudang    : " + dipilih.getGudangWilayah() +
                              "\nStok saat ini : " + dipilih.getKuantitas() + " unit");
            dlg.setContentText("Jumlah yang dikurangi:");
            dlg.showAndWait().ifPresent(inputStr -> {
                try {
                    int jumlah = Integer.parseInt(inputStr.trim());
                    if (jumlah <= 0) { popupWarn("Jumlah harus lebih dari 0!"); return; }
                    if (jumlah > dipilih.getKuantitas()) {
                        popupWarn("Stok tidak mencukupi! Tersedia: " + dipilih.getKuantitas()); return;
                    }
                    String alertMsg = dipilih.kurangiStok(jumlah);
                    boolean ok = SiPanganDAO.updateKuantitasStok(dipilih.getIdStok(), dipilih.getKuantitas());
                    if (ok) {
                        refreshStokTable();
                        if (alertMsg != null) {
                            stokAlertBanner.setText("⚠  STOK KRITIS: " + alertMsg);
                            stokAlertBanner.setVisible(true); stokAlertBanner.setManaged(true);
                            popupDialog("⚠ Peringatan Stok Kritis!", alertMsg, Alert.AlertType.WARNING);
                        } else {
                            stokAlertBanner.setVisible(false); stokAlertBanner.setManaged(false);
                            popupInfo("Berhasil", "Stok berhasil dikurangi " + jumlah + " unit.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    popupWarn("Masukkan angka yang valid! Contoh: 10");
                }
            });
        });

        btnEdit.setOnAction(e -> {
            Stok sel = stokTable.getSelectionModel().getSelectedItem();
            if (sel == null) { popupWarn("Pilih baris stok yang ingin diedit!"); return; }
            Stok edited = tampilDialogStok(sel, SiPanganDAO.getAllProduk());
            if (edited != null && SiPanganDAO.updateStok(edited)) {
                refreshStokTable(); popupInfo("Berhasil", "Stok berhasil diperbarui!");
            }
        });

        btnHapus.setOnAction(e -> {
            Stok sel = stokTable.getSelectionModel().getSelectedItem();
            if (sel == null) { popupWarn("Pilih baris stok yang ingin dihapus!"); return; }
            if (popupKonfirmasi("Hapus Stok?", "Hapus data stok ini?")) {
                if (SiPanganDAO.hapusStok(sel.getIdStok())) {
                    refreshStokTable(); popupInfo("Berhasil", "Stok berhasil dihapus!");
                }
            }
        });

        btnLaporan.setOnAction(e -> cetakLaporanStokPDF());
        btnRefresh.setOnAction(e -> {
            refreshStokTable();
            stokAlertBanner.setVisible(false); stokAlertBanner.setManaged(false);
        });

        content.getChildren().addAll(stokAlertBanner, toolbar, stokTable);
        panel.getChildren().addAll(header, content);
        return panel;
    }

    private void refreshStokTable() {
        if (stokData != null) stokData.setAll(SiPanganDAO.getAllStok());
    }

    private Stok tampilDialogStok(Stok existing, List<ProdukPangan> produkList) {
        Dialog<Stok> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Tambah Data Stok" : "Edit Data Stok");
        dlg.setHeaderText(existing == null ? "Isi detail stok baru"
                : "Perbarui stok: " + existing.getProduk().getNamaProduk());

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(14);
        grid.setPadding(new Insets(24));

        ComboBox<ProdukPangan> cbProduk = new ComboBox<>(
            FXCollections.observableArrayList(produkList));
        cbProduk.setPromptText("— Pilih Produk —");
        cbProduk.setPrefWidth(260);

        TextField tfQty    = uiTextField("cth: 100");
        TextField tfMin    = uiTextField("cth: 20");

        ComboBox<String> cbStatus = new ComboBox<>(
            FXCollections.observableArrayList("LAYAK", "PERLU_CEPAT", "TIDAK_LAYAK"));
        cbStatus.setPromptText("— Pilih Status —");
        cbStatus.setPrefWidth(260);

        TextField tfGudang = uiTextField("cth: Gudang Bulog Jakarta Utara");

        if (existing != null) {
            produkList.stream()
                .filter(p -> p.getIdProduk() == existing.getProduk().getIdProduk())
                .findFirst().ifPresent(cbProduk::setValue);
            tfQty.setText(String.valueOf(existing.getKuantitas()));
            tfMin.setText(String.valueOf(existing.getBatasMinimum()));
            cbStatus.setValue(existing.getStatusKualitas());
            tfGudang.setText(existing.getGudangWilayah());
        }

        grid.addRow(0, dialogLabel("Produk :"),          cbProduk);
        grid.addRow(1, dialogLabel("Kuantitas :"),        tfQty);
        grid.addRow(2, dialogLabel("Batas Minimum :"),    tfMin);
        grid.addRow(3, dialogLabel("Status Kualitas :"),  cbStatus);
        grid.addRow(4, dialogLabel("Gudang Wilayah :"),   tfGudang);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (cbProduk.getValue() == null) { popupWarn("Pilih produk!"); return null; }
                if (cbStatus.getValue() == null) { popupWarn("Pilih status kualitas!"); return null; }
                if (tfGudang.getText().trim().isEmpty()) { popupWarn("Nama gudang tidak boleh kosong!"); return null; }
                try {
                    int qty = Integer.parseInt(tfQty.getText().trim());
                    int min = Integer.parseInt(tfMin.getText().trim());
                    if (qty < 0 || min < 0) { popupWarn("Kuantitas/minimum tidak boleh negatif!"); return null; }
                    return new Stok(existing != null ? existing.getIdStok() : 0,
                        cbProduk.getValue(), qty, min,
                        cbStatus.getValue(), tfGudang.getText().trim());
                } catch (NumberFormatException ex) {
                    popupWarn("Kuantitas dan Batas Minimum harus berupa angka!"); return null;
                }
            }
            return null;
        });
        return dlg.showAndWait().orElse(null);
    }


    // =========================================================================
    // PDF REPORT — Apache PDFBox
    // =========================================================================
    private void cetakLaporanStokPDF() {
        List<Stok> dataStok = SiPanganDAO.getAllStok();
        String timestamp    = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String namaFile     = "laporan_stok_" + timestamp + ".pdf";
        File   outputFile   = new File(namaFile);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float margin     = 50f;
            float pageWidth  = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float tableWidth = pageWidth - 2 * margin;

            float[] colWidths = { 28f, 110f, 48f, 48f, 72f, 130f, 55f };

            float[] colorAccent  = { 0.15f, 0.23f, 0.37f };
            float[] colorHeader  = { 0.95f, 0.97f, 1.00f };
            float[] colorKritis  = { 1.00f, 0.89f, 0.89f };
            float[] colorWhite   = { 1f, 1f, 1f };
            float[] colorAlt     = { 0.97f, 0.98f, 1.00f };
            float[] colorText    = { 0.12f, 0.18f, 0.24f };
            float[] colorMuted   = { 0.39f, 0.45f, 0.52f };
            float[] colorRed     = { 0.86f, 0.15f, 0.15f };
            float[] colorGreen   = { 0.02f, 0.59f, 0.42f };

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = pageHeight - margin;

                cs.setNonStrokingColor(colorAccent[0], colorAccent[1], colorAccent[2]);
                cs.addRect(margin, y - 70, tableWidth, 70);
                cs.fill();

                cs.setNonStrokingColor(1f, 1f, 1f);
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                cs.newLineAtOffset(margin + 12, y - 28);
                cs.showText("LAPORAN STOK PANGAN NASIONAL");
                cs.endText();

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(margin + 12, y - 44);
                cs.showText("SiPangan — Sistem Informasi & Distribusi Stok Pangan");
                cs.endText();

                String tglCetak = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss").format(new Date());
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 9);
                cs.newLineAtOffset(margin + 12, y - 58);
                cs.showText("Dicetak pada: " + tglCetak);
                cs.endText();

                y -= 80;

                int totalProduk = SiPanganDAO.countProduk();
                int stokKritis  = SiPanganDAO.countStokKritis();
                int totalStok   = dataStok.size();

                float boxW = (tableWidth - 20) / 3f;
                String[][] stats = {
                    { "Total Produk", String.valueOf(totalProduk) },
                    { "Total Stok",   String.valueOf(totalStok)   },
                    { "Stok Kritis",  String.valueOf(stokKritis)  }
                };
                float[] statColors = {
                    0.22f, 0.38f, 0.55f,
                    0.02f, 0.59f, 0.42f,
                    0.86f, 0.15f, 0.15f
                };

                for (int i = 0; i < 3; i++) {
                    float bx = margin + i * (boxW + 10);
                    cs.setNonStrokingColor(statColors[i*3], statColors[i*3+1], statColors[i*3+2]);
                    cs.addRect(bx, y - 45, boxW, 45);
                    cs.fill();
                    cs.setNonStrokingColor(1f, 1f, 1f);
                    cs.beginText();
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
                    cs.newLineAtOffset(bx + 8, y - 15);
                    cs.showText(stats[i][0]);
                    cs.endText();
                    cs.beginText();
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
                    cs.newLineAtOffset(bx + 8, y - 36);
                    cs.showText(stats[i][1]);
                    cs.endText();
                }
                y -= 60;

                String[] headers = { "ID", "Nama Produk", "Qty", "Min", "Status", "Gudang Wilayah", "Alert" };
                float rowH = 20f;

                cs.setNonStrokingColor(colorAccent[0], colorAccent[1], colorAccent[2]);
                cs.addRect(margin, y - rowH, tableWidth, rowH);
                cs.fill();

                cs.setNonStrokingColor(1f, 1f, 1f);
                float xCol = margin + 4;
                for (int c = 0; c < headers.length; c++) {
                    cs.beginText();
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 8);
                    cs.newLineAtOffset(xCol, y - rowH + 6);
                    cs.showText(headers[c]);
                    cs.endText();
                    xCol += colWidths[c];
                }
                y -= rowH;

                for (int idx = 0; idx < dataStok.size(); idx++) {
                    Stok s       = dataStok.get(idx);
                    boolean kritis = s.isBawahMinimum();
                    boolean altRow = (idx % 2 == 1) && !kritis;

                    if (kritis)       cs.setNonStrokingColor(colorKritis[0], colorKritis[1], colorKritis[2]);
                    else if (altRow)  cs.setNonStrokingColor(colorAlt[0], colorAlt[1], colorAlt[2]);
                    else              cs.setNonStrokingColor(colorWhite[0], colorWhite[1], colorWhite[2]);
                    cs.addRect(margin, y - rowH, tableWidth, rowH);
                    cs.fill();

                    cs.setStrokingColor(0.85f, 0.87f, 0.90f);
                    cs.setLineWidth(0.4f);
                    cs.moveTo(margin, y - rowH);
                    cs.lineTo(margin + tableWidth, y - rowH);
                    cs.stroke();

                    String[] vals = {
                        String.valueOf(s.getIdStok()),
                        truncate(s.getProduk().getNamaProduk(), 18),
                        String.valueOf(s.getKuantitas()),
                        String.valueOf(s.getBatasMinimum()),
                        s.getStatusKualitas(),
                        truncate(s.getGudangWilayah(), 20),
                        kritis ? "KRITIS" : "AMAN"
                    };

                    xCol = margin + 4;
                    for (int c = 0; c < vals.length; c++) {
                        if (c == 6) {
                            cs.setNonStrokingColor(
                                kritis ? colorRed[0] : colorGreen[0],
                                kritis ? colorRed[1] : colorGreen[1],
                                kritis ? colorRed[2] : colorGreen[2]);
                        } else {
                            cs.setNonStrokingColor(colorText[0], colorText[1], colorText[2]);
                        }
                        cs.beginText();
                        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
                        cs.newLineAtOffset(xCol, y - rowH + 6);
                        cs.showText(vals[c]);
                        cs.endText();
                        xCol += colWidths[c];
                    }
                    y -= rowH;

                    if (y < margin + 60 && idx < dataStok.size() - 1) { cs.close(); break; }
                }

                y = margin + 40;
                cs.setStrokingColor(colorAccent[0], colorAccent[1], colorAccent[2]);
                cs.setLineWidth(1f);
                cs.moveTo(margin, y); cs.lineTo(margin + tableWidth, y); cs.stroke();
                cs.setNonStrokingColor(colorMuted[0], colorMuted[1], colorMuted[2]);
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 8);
                cs.newLineAtOffset(margin, y - 14);
                cs.showText("Dokumen digenerate otomatis oleh SiPangan — Sistem Informasi Stok Pangan Nasional.");
                cs.endText();
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 8);
                cs.newLineAtOffset(margin + tableWidth - 80, y - 14);
                cs.showText("Halaman 1 dari 1");
                cs.endText();
            }

            doc.save(outputFile);
            popupInfo("✅ PDF Berhasil Dibuat",
                "Laporan disimpan sebagai:\n" + outputFile.getAbsolutePath() + "\n\nMembuka file PDF...");
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(outputFile);
            }

        } catch (IOException ex) {
            popupDialog("❌ Gagal Buat PDF",
                "Terjadi kesalahan saat membuat PDF:\n" + ex.getMessage(), Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }

    private String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max - 1) + "…" : (s != null ? s : "");
    }


    // =========================================================================
    // UI HELPERS
    // =========================================================================

    /** Page header bar with title + subtitle */
    private HBox buildPageHeader(String title, String subtitle) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(24, 32, 24, 32));
        bar.setStyle(
            "-fx-background-color: " + C_SURFACE + "; " +
            "-fx-border-color: transparent transparent " + C_BORDER + " transparent; " +
            "-fx-border-width: 0 0 1 0;"
        );
        VBox text = new VBox(4);
        Label lTitle = new Label(title);
        lTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + C_TEXT1 + ";");
        Label lSub   = new Label(subtitle);
        lSub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + C_TEXT2 + ";");
        text.getChildren().addAll(lTitle, lSub);
        bar.getChildren().add(text);
        return bar;
    }

    /** Toolbar card wrapping buttons */
    private HBox buildToolbarCard(Button... buttons) {
        HBox outerBar = new HBox();
        outerBar.setStyle(
            "-fx-background-color: " + C_SURFACE + "; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: " + C_BORDER + "; -fx-border-radius: 10; -fx-border-width: 1; " +
            "-fx-padding: 12 16;"
        );
        HBox btnRow = new HBox(8);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        btnRow.getChildren().addAll(buttons);
        outerBar.getChildren().add(btnRow);
        return outerBar;
    }

    /** Stat card for dashboard */
    private VBox buildStatCard(String icon, String label, Label valueLabel, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setMinWidth(190);
        card.setStyle(
            "-fx-background-color: " + C_SURFACE + "; -fx-background-radius: 12; " +
            "-fx-border-color: " + C_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1; " +
            SHADOW_SM
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        Label ikonLbl = new Label(icon);
        ikonLbl.setStyle("-fx-font-size: 22px;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + C_TEXT2 + ";");
        top.getChildren().addAll(ikonLbl, lbl);

        valueLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        // Accent bottom bar
        Label accentBar = new Label();
        accentBar.setMaxWidth(Double.MAX_VALUE);
        accentBar.setMinHeight(3); accentBar.setMaxHeight(3);
        accentBar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");

        card.getChildren().addAll(top, valueLabel, accentBar);
        return card;
    }

    private Label dialogLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + C_TEXT1 + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        return l;
    }

    private Label uiLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + C_TEXT2 + "; -fx-font-size: 12px;");
        return l;
    }

    private TextField uiTextField(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setPrefWidth(260);
        tf.setStyle(
            "-fx-background-color: " + C_SURFACE2 + "; -fx-text-fill: " + C_TEXT1 + "; " +
            "-fx-border-color: " + C_BORDER + "; -fx-border-radius: 7; " +
            "-fx-background-radius: 7; -fx-padding: 8 10; -fx-font-size: 13px;"
        );
        return tf;
    }

    private Button uiBtn(String text, String bgColor) {
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

    private TableColumn<ProdukPangan, Integer> pColI(String title, int w) {
        TableColumn<ProdukPangan, Integer> col = new TableColumn<>(title);
        col.setPrefWidth(w); return col;
    }
    private TableColumn<ProdukPangan, String> pColS(String title, int w) {
        TableColumn<ProdukPangan, String> col = new TableColumn<>(title);
        col.setPrefWidth(w); return col;
    }
    private TableColumn<Stok, Integer> sColI(String title, int w) {
        TableColumn<Stok, Integer> col = new TableColumn<>(title);
        col.setPrefWidth(w); return col;
    }
    private TableColumn<Stok, String> sColS(String title, int w) {
        TableColumn<Stok, String> col = new TableColumn<>(title);
        col.setPrefWidth(w); return col;
    }

    private void popupInfo(String judul, String pesan) {
        popupDialog(judul, pesan, Alert.AlertType.INFORMATION);
    }
    private void popupWarn(String pesan) {
        popupDialog("Peringatan", pesan, Alert.AlertType.WARNING);
    }
    private void popupDialog(String judul, String pesan, Alert.AlertType tipe) {
        Alert a = new Alert(tipe, pesan, ButtonType.OK);
        a.setTitle(judul); a.setHeaderText(null); a.showAndWait();
    }
    private boolean popupKonfirmasi(String judul, String pesan) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, pesan, ButtonType.YES, ButtonType.NO);
        a.setTitle(judul); a.setHeaderText(null);
        return a.showAndWait().map(r -> r == ButtonType.YES).orElse(false);
    }
}
