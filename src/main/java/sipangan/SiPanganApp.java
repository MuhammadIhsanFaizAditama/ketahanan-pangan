package sipangan;

import sipangan.model.User;
import sipangan.ui.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

// =========================================================================
// FILE    : SiPanganApp.java
// PACKAGE : sipangan
// KONSEP  : Kelas utama JavaFX — Runner & Scene Manager
// =========================================================================
public class SiPanganApp extends Application {

    private Stage primaryStage;
    private User  currentUser;

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

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // =========================================================================
    // SCENE SWITCHERS
    // =========================================================================

    public void showLoginScene() {
        primaryStage.setScene(new Scene(new LoginView(this)));
    }

    public void showDashboardScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + Theme.C_BG + ";");

        DashboardView panelDashboard = new DashboardView();
        ProdukView panelProduk       = new ProdukView();
        StokView panelStok           = new StokView();

        StackPane contentArea = new StackPane(panelStok, panelProduk, panelDashboard);
        VBox sidebar = buildSidebar(panelDashboard, panelProduk, panelStok);

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();

        panelDashboard.refresh();
        panelStok.refresh();
    }

    // =========================================================================
    // SIDEBAR
    // =========================================================================

    private VBox buildSidebar(DashboardView dashPanel, ProdukView produkPanel, StokView stokPanel) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + Theme.C_SIDEBAR_BG + ";");

        // Header / Logo
        VBox logoBox = new VBox(4);
        logoBox.setPadding(new Insets(22, 20, 22, 20));
        logoBox.setStyle("-fx-background-color: " + Theme.C_SIDEBAR_HDR + ";");
        Label logo    = new Label("🌾  SiPangan");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
        Label logoSub = new Label("Stok Pangan Nasional");
        logoSub.setStyle("-fx-font-size: 10.5px; -fx-text-fill: " + Theme.C_SIDEBAR_MUTE + ";");
        logoBox.getChildren().addAll(logo, logoSub);

        // User info card
        HBox userBox = new HBox(12);
        userBox.setPadding(new Insets(14, 20, 14, 20));
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04); " +
            "-fx-border-color: transparent transparent " + Theme.C_SIDEBAR_MUTE + " transparent; " +
            "-fx-border-width: 0 0 1 0;"
        );

        // Avatar circle with initial
        StackPane avatar = new StackPane();
        Circle circle = new Circle(18);
        circle.setStyle("-fx-fill: " + Theme.C_PRIMARY + ";");
        String initial = (currentUser != null && currentUser.getNama() != null && !currentUser.getNama().isEmpty())
            ? currentUser.getNama().substring(0, 1).toUpperCase() : "U";
        Label initLbl = new Label(initial);
        initLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        avatar.getChildren().addAll(circle, initLbl);

        VBox userInfo = new VBox(2);
        Label userName   = new Label(currentUser != null ? currentUser.getNama() : "—");
        userName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
        Label userHandle = new Label("@" + (currentUser != null ? currentUser.getUsername() : "—"));
        userHandle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Theme.C_SIDEBAR_MUTE + ";");
        userInfo.getChildren().addAll(userName, userHandle);
        userBox.getChildren().addAll(avatar, userInfo);

        // Nav section label
        Label navSectionLbl = new Label("NAVIGASI");
        navSectionLbl.setStyle(
            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Theme.C_SIDEBAR_MUTE + "; " +
            "-fx-padding: 16 20 6 20;"
        );

        // Nav buttons
        Button[] navBtns = {
            buildNavBtn("📊", "Dashboard",      true),
            buildNavBtn("📦", "Produk Pangan",  false),
            buildNavBtn("🏭", "Manajemen Stok", false),
        };
        navBtns[0].setOnAction(e -> { setActiveNav(navBtns, 0); dashPanel.toFront();   dashPanel.refresh();   });
        navBtns[1].setOnAction(e -> { setActiveNav(navBtns, 1); produkPanel.toFront(); produkPanel.refresh(); });
        navBtns[2].setOnAction(e -> { setActiveNav(navBtns, 2); stokPanel.toFront();   stokPanel.refresh();   });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout
        Label bottomSep = new Label();
        bottomSep.setStyle(
            "-fx-background-color: " + Theme.C_SIDEBAR_MUTE + "; -fx-min-height: 1; -fx-max-height: 1; " +
            "-fx-padding: 0; -fx-pref-height: 1;"
        );
        bottomSep.setMaxWidth(Double.MAX_VALUE);

        Button btnLogout = buildNavBtn("🚪", "Keluar / Logout", false);
        btnLogout.setStyle(btnLogout.getStyle()
            .replace("-fx-text-fill: " + Theme.C_SIDEBAR_TXT, "-fx-text-fill: #FC8181"));
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
            "-fx-background-color: " + (active ? Theme.C_SIDEBAR_HOVER : "transparent") + "; " +
            "-fx-text-fill: "         + (active ? "#FFFFFF" : Theme.C_SIDEBAR_TXT) + "; " +
            "-fx-font-size: 13px; -fx-padding: 12 20; " +
            "-fx-background-radius: 0; -fx-cursor: hand; " +
            "-fx-border-color: " + (active ? Theme.C_PRIMARY : "transparent") + " transparent transparent transparent; " +
            "-fx-border-width: 0 0 0 3;"
        );
        if (!active) {
            btn.setOnMouseEntered(e -> {
                if (!btn.getStyle().contains(Theme.C_SIDEBAR_HOVER))
                    btn.setStyle(btn.getStyle().replace("transparent;", Theme.C_SIDEBAR_HOVER + ";").replace(
                        "-fx-text-fill: " + Theme.C_SIDEBAR_TXT, "-fx-text-fill: #FFFFFF"));
            });
            btn.setOnMouseExited(e -> {
                if (!btn.getStyle().contains(Theme.C_PRIMARY + " transparent"))
                    btn.setStyle(btn.getStyle().replace(Theme.C_SIDEBAR_HOVER + ";", "transparent;").replace(
                        "-fx-text-fill: #FFFFFF", "-fx-text-fill: " + Theme.C_SIDEBAR_TXT));
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
}
