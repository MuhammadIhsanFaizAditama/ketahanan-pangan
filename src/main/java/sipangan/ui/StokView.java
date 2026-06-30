package sipangan.ui;

import sipangan.db.SiPanganDAO;
import sipangan.model.ProdukPangan;
import sipangan.model.Stok;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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

public class StokView extends VBox {

    private TableView<Stok>         stokTable;
    private ObservableList<Stok>    stokData;
    private Label                   stokAlertBanner;

    public StokView() {
        setupUI();
    }

    private void setupUI() {
        this.setStyle("-fx-background-color: " + Theme.C_BG + ";");

        HBox header = UIHelper.buildPageHeader("🏭  Manajemen Stok Gudang", "Pantau dan kelola kuantitas stok di seluruh gudang wilayah");

        VBox content = new VBox(16);
        content.setPadding(new Insets(28, 32, 32, 32));
        VBox.setVgrow(content, Priority.ALWAYS);

        // Alert banner
        stokAlertBanner = new Label();
        stokAlertBanner.setWrapText(true);
        stokAlertBanner.setVisible(false);
        stokAlertBanner.setManaged(false);
        stokAlertBanner.setStyle(
            "-fx-background-color: " + Theme.C_DANGER_BG + "; -fx-text-fill: " + Theme.C_DANGER + "; " +
            "-fx-padding: 12 16; -fx-background-radius: 8; " +
            "-fx-border-color: #FEB2B2; -fx-border-radius: 8; -fx-border-width: 0 0 0 4; " +
            "-fx-font-size: 13px; -fx-font-weight: bold;"
        );

        // Table
        stokTable = new TableView<>();
        stokData  = FXCollections.observableArrayList();
        stokTable.setItems(stokData);
        stokTable.setStyle(
            "-fx-background-color: " + Theme.C_SURFACE + "; " +
            "-fx-border-color: " + Theme.C_BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;"
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
                        bg = Theme.C_SUCCESS_BG;
                        fg = Theme.C_SUCCESS;
                        break;
                    case "PERLU_CEPAT":
                        bg = Theme.C_WARNING_BG;
                        fg = Theme.C_WARNING;
                        break;
                    default:
                        bg = Theme.C_DANGER_BG;
                        fg = Theme.C_DANGER;
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
                    setStyle("-fx-text-fill: " + Theme.C_DANGER + "; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: " + Theme.C_TEXT1 + ";");
                }
            }
        });

        // Row factory — kritis highlight
        stokTable.setRowFactory(tv -> new TableRow<Stok>() {
            @Override protected void updateItem(Stok item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && item.isBawahMinimum()) {
                    setStyle("-fx-background-color: " + Theme.C_DANGER_BG + ";");
                } else {
                    setStyle("");
                }
            }
        });

        stokTable.getColumns().addAll(colId, colProduk, colQty, colMin, colStatus, colGudang);

        // Toolbar
        HBox toolbar = UIHelper.buildToolbarCard(
            UIHelper.uiBtn("➕  Tambah",      Theme.C_SUCCESS),
            UIHelper.uiBtn("➖  Kurangi",     Theme.C_DANGER),
            UIHelper.uiBtn("✏️  Edit",        Theme.C_PRIMARY),
            UIHelper.uiBtn("🗑️  Hapus",       "#B91C1C"),
            UIHelper.uiBtn("📄  Cetak PDF",   Theme.C_PURPLE),
            UIHelper.uiBtn("🔄  Refresh",     Theme.C_TEXT2)
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
            if (produkList.isEmpty()) { UIHelper.popupWarn("Tambah produk terlebih dahulu!"); return; }
            Stok baru = tampilDialogStok(null, produkList);
            if (baru != null && SiPanganDAO.tambahStok(baru)) {
                refresh(); UIHelper.popupInfo("Berhasil", "Stok berhasil ditambahkan!");
            }
        });

        btnKurangi.setOnAction(e -> {
            Stok dipilih = stokTable.getSelectionModel().getSelectedItem();
            if (dipilih == null) { UIHelper.popupWarn("Pilih baris stok terlebih dahulu!"); return; }
            TextInputDialog dlg = new TextInputDialog("10");
            dlg.setTitle("Kurangi Stok");
            dlg.setHeaderText("Produk    : " + dipilih.getProduk().getNamaProduk() +
                              "\nGudang    : " + dipilih.getGudangWilayah() +
                              "\nStok saat ini : " + dipilih.getKuantitas() + " unit");
            dlg.setContentText("Jumlah yang dikurangi:");
            dlg.showAndWait().ifPresent(inputStr -> {
                try {
                    int jumlah = Integer.parseInt(inputStr.trim());
                    if (jumlah <= 0) { UIHelper.popupWarn("Jumlah harus lebih dari 0!"); return; }
                    if (jumlah > dipilih.getKuantitas()) {
                        UIHelper.popupWarn("Stok tidak mencukupi! Tersedia: " + dipilih.getKuantitas()); return;
                    }
                    String alertMsg = dipilih.kurangiStok(jumlah);
                    boolean ok = SiPanganDAO.updateKuantitasStok(dipilih.getIdStok(), dipilih.getKuantitas());
                    if (ok) {
                        refresh();
                        if (alertMsg != null) {
                            stokAlertBanner.setText("⚠  STOK KRITIS: " + alertMsg);
                            stokAlertBanner.setVisible(true); stokAlertBanner.setManaged(true);
                            UIHelper.popupDialog("⚠ Peringatan Stok Kritis!", alertMsg, Alert.AlertType.WARNING);
                        } else {
                            stokAlertBanner.setVisible(false); stokAlertBanner.setManaged(false);
                            UIHelper.popupInfo("Berhasil", "Stok berhasil dikurangi " + jumlah + " unit.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    UIHelper.popupWarn("Masukkan angka yang valid! Contoh: 10");
                }
            });
        });

        btnEdit.setOnAction(e -> {
            Stok sel = stokTable.getSelectionModel().getSelectedItem();
            if (sel == null) { UIHelper.popupWarn("Pilih baris stok yang ingin diedit!"); return; }
            Stok edited = tampilDialogStok(sel, SiPanganDAO.getAllProduk());
            if (edited != null && SiPanganDAO.updateStok(edited)) {
                refresh(); UIHelper.popupInfo("Berhasil", "Stok berhasil diperbarui!");
            }
        });

        btnHapus.setOnAction(e -> {
            Stok sel = stokTable.getSelectionModel().getSelectedItem();
            if (sel == null) { UIHelper.popupWarn("Pilih baris stok yang ingin dihapus!"); return; }
            if (UIHelper.popupKonfirmasi("Hapus Stok?", "Hapus data stok ini?")) {
                if (SiPanganDAO.hapusStok(sel.getIdStok())) {
                    refresh(); UIHelper.popupInfo("Berhasil", "Stok berhasil dihapus!");
                }
            }
        });

        btnLaporan.setOnAction(e -> cetakLaporanStokPDF());
        btnRefresh.setOnAction(e -> {
            refresh();
            stokAlertBanner.setVisible(false); stokAlertBanner.setManaged(false);
        });

        content.getChildren().addAll(stokAlertBanner, toolbar, stokTable);
        this.getChildren().addAll(header, content);

        refresh();
    }

    public void refresh() {
        if (stokData != null) {
            stokData.setAll(SiPanganDAO.getAllStok());
        }
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

        TextField tfQty    = UIHelper.uiTextField("cth: 100");
        TextField tfMin    = UIHelper.uiTextField("cth: 20");

        ComboBox<String> cbStatus = new ComboBox<>(
            FXCollections.observableArrayList("LAYAK", "PERLU_CEPAT", "TIDAK_LAYAK"));
        cbStatus.setPromptText("— Pilih Status —");
        cbStatus.setPrefWidth(260);

        TextField tfGudang = UIHelper.uiTextField("cth: Gudang Bulog Jakarta Utara");

        if (existing != null) {
            produkList.stream()
                .filter(p -> p.getIdProduk() == existing.getProduk().getIdProduk())
                .findFirst().ifPresent(cbProduk::setValue);
            tfQty.setText(String.valueOf(existing.getKuantitas()));
            tfMin.setText(String.valueOf(existing.getBatasMinimum()));
            cbStatus.setValue(existing.getStatusKualitas());
            tfGudang.setText(existing.getGudangWilayah());
        }

        grid.addRow(0, UIHelper.dialogLabel("Produk :"),          cbProduk);
        grid.addRow(1, UIHelper.dialogLabel("Kuantitas :"),        tfQty);
        grid.addRow(2, UIHelper.dialogLabel("Batas Minimum :"),    tfMin);
        grid.addRow(3, UIHelper.dialogLabel("Status Kualitas :"),  cbStatus);
        grid.addRow(4, UIHelper.dialogLabel("Gudang Wilayah :"),   tfGudang);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (cbProduk.getValue() == null) { UIHelper.popupWarn("Pilih produk!"); return null; }
                if (cbStatus.getValue() == null) { UIHelper.popupWarn("Pilih status kualitas!"); return null; }
                if (tfGudang.getText().trim().isEmpty()) { UIHelper.popupWarn("Nama gudang tidak boleh kosong!"); return null; }
                try {
                    int qty = Integer.parseInt(tfQty.getText().trim());
                    int min = Integer.parseInt(tfMin.getText().trim());
                    if (qty < 0 || min < 0) { UIHelper.popupWarn("Kuantitas/minimum tidak boleh negatif!"); return null; }
                    return new Stok(existing != null ? existing.getIdStok() : 0,
                        cbProduk.getValue(), qty, min,
                        cbStatus.getValue(), tfGudang.getText().trim());
                } catch (NumberFormatException ex) {
                    UIHelper.popupWarn("Kuantitas dan Batas Minimum harus berupa angka!"); return null;
                }
            }
            return null;
        });
        return dlg.showAndWait().orElse(null);
    }

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
            UIHelper.popupInfo("✅ PDF Berhasil Dibuat",
                "Laporan disimpan sebagai:\n" + outputFile.getAbsolutePath() + "\n\nMembuka file PDF...");
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(outputFile);
            }

        } catch (IOException ex) {
            UIHelper.popupDialog("❌ Gagal Buat PDF",
                "Terjadi kesalahan saat membuat PDF:\n" + ex.getMessage(), Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }

    private String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max - 1) + "…" : (s != null ? s : "");
    }

    private TableColumn<Stok, Integer> sColI(String title, int w) {
        TableColumn<Stok, Integer> col = new TableColumn<>(title);
        col.setPrefWidth(w); return col;
    }
    private TableColumn<Stok, String> sColS(String title, int w) {
        TableColumn<Stok, String> col = new TableColumn<>(title);
        col.setPrefWidth(w); return col;
    }
}
