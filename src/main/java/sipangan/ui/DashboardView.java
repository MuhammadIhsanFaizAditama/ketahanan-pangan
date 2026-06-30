package sipangan.ui;

import sipangan.db.SiPanganDAO;
import sipangan.model.Stok;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardView extends VBox {

    private Label dashTotalProdukValue;
    private Label dashStokKritisValue;
    private Label dashAlertLabel;

    public DashboardView() {
        setupUI();
    }

    private void setupUI() {
        this.setSpacing(24);
        this.setPadding(new Insets(0));
        this.setStyle("-fx-background-color: " + Theme.C_BG + ";");

        // Page Header bar
        HBox header = UIHelper.buildPageHeader("📊  Dashboard",
            "Ringkasan kondisi stok pangan nasional secara real-time");

        // Content scrollable area
        VBox content = new VBox(24);
        content.setPadding(new Insets(32));
        VBox.setVgrow(content, Priority.ALWAYS);

        // Stat cards
        dashTotalProdukValue = new Label("—");
        dashStokKritisValue  = new Label("—");
        Label dummyGudang    = new Label("7");

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            buildStatCard("📦", "Total Produk",  dashTotalProdukValue, Theme.C_PRIMARY),
            buildStatCard("⚠️", "Stok Kritis",   dashStokKritisValue,  Theme.C_DANGER),
            buildStatCard("🏭", "Gudang Aktif",  dummyGudang,          Theme.C_SUCCESS)
        );

        // Alert panel
        VBox alertBox = new VBox(14);
        alertBox.setPadding(new Insets(22, 24, 24, 24));
        alertBox.setStyle(
            "-fx-background-color: " + Theme.C_SURFACE + "; -fx-background-radius: 12; " +
            "-fx-border-color: " + Theme.C_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1; " +
            Theme.SHADOW_SM
        );

        HBox alertHeader = new HBox(10);
        alertHeader.setAlignment(Pos.CENTER_LEFT);
        Label alertIcon  = new Label("⚠️");
        alertIcon.setStyle("-fx-font-size: 16px;");
        Label alertTitle = new Label("Peringatan Stok Kritis");
        alertTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + Theme.C_TEXT1 + ";");
        alertHeader.getChildren().addAll(alertIcon, alertTitle);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + Theme.C_BORDER + ";");

        dashAlertLabel = new Label("Memuat data...");
        dashAlertLabel.setWrapText(true);
        dashAlertLabel.setStyle("-fx-text-fill: " + Theme.C_TEXT2 + "; -fx-font-size: 13px; -fx-line-spacing: 4;");

        alertBox.getChildren().addAll(alertHeader, sep, dashAlertLabel);
        content.getChildren().addAll(statsRow, alertBox);

        this.getChildren().addAll(header, content);
        
        refresh();
    }

    public void refresh() {
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
            dashAlertLabel.setStyle("-fx-text-fill: " + Theme.C_DANGER + "; -fx-font-size: 13px; -fx-line-spacing: 4;");
        } else {
            dashAlertLabel.setText("✅  Semua stok dalam kondisi aman. Tidak ada produk yang perlu dikhawatirkan.");
            dashAlertLabel.setStyle("-fx-text-fill: " + Theme.C_SUCCESS + "; -fx-font-size: 13px; -fx-line-spacing: 4;");
        }
    }

    private VBox buildStatCard(String icon, String label, Label valueLabel, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setMinWidth(190);
        card.setStyle(
            "-fx-background-color: " + Theme.C_SURFACE + "; -fx-background-radius: 12; " +
            "-fx-border-color: " + Theme.C_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1; " +
            Theme.SHADOW_SM
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        Label ikonLbl = new Label(icon);
        ikonLbl.setStyle("-fx-font-size: 22px;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.C_TEXT2 + ";");
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
}
