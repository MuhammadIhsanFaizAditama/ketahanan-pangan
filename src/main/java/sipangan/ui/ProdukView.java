package sipangan.ui;

import sipangan.db.SiPanganDAO;
import sipangan.model.ProdukPangan;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ProdukView extends VBox {

    private TableView<ProdukPangan> produkTable;

    public ProdukView() {
        setupUI();
    }

    private void setupUI() {
        this.setStyle("-fx-background-color: " + Theme.C_BG + ";");

        HBox header = UIHelper.buildPageHeader("📦  Produk Pangan", "Kelola data produk pangan yang tersedia dalam sistem");

        VBox content = new VBox(16);
        content.setPadding(new Insets(28, 32, 32, 32));
        VBox.setVgrow(content, Priority.ALWAYS);

        produkTable = new TableView<>();
        produkTable.setStyle(
            "-fx-background-color: " + Theme.C_SURFACE + "; " +
            "-fx-border-color: " + Theme.C_BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10; " +
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
        HBox toolbar = UIHelper.buildToolbarCard(
            UIHelper.uiBtn("➕  Tambah",  Theme.C_SUCCESS),
            UIHelper.uiBtn("✏️  Edit",    Theme.C_PRIMARY),
            UIHelper.uiBtn("🗑️  Hapus",   Theme.C_DANGER),
            UIHelper.uiBtn("🔄  Refresh", Theme.C_TEXT2)
        );
        Button btnTambah  = (Button) ((HBox) toolbar.getChildren().get(0)).getChildren().get(0);
        Button btnEdit    = (Button) ((HBox) toolbar.getChildren().get(0)).getChildren().get(1);
        Button btnHapus   = (Button) ((HBox) toolbar.getChildren().get(0)).getChildren().get(2);
        Button btnRefresh = (Button) ((HBox) toolbar.getChildren().get(0)).getChildren().get(3);

        btnTambah.setOnAction(e -> {
            ProdukPangan baru = tampilDialogProduk(null);
            if (baru != null && SiPanganDAO.tambahProduk(baru)) {
                refresh(); UIHelper.popupInfo("Berhasil", "Produk berhasil ditambahkan!");
            }
        });
        btnEdit.setOnAction(e -> {
            ProdukPangan sel = produkTable.getSelectionModel().getSelectedItem();
            if (sel == null) { UIHelper.popupWarn("Pilih baris produk yang ingin diedit!"); return; }
            ProdukPangan edited = tampilDialogProduk(sel);
            if (edited != null && SiPanganDAO.updateProduk(edited)) {
                refresh(); UIHelper.popupInfo("Berhasil", "Produk berhasil diperbarui!");
            }
        });
        btnHapus.setOnAction(e -> {
            ProdukPangan sel = produkTable.getSelectionModel().getSelectedItem();
            if (sel == null) { UIHelper.popupWarn("Pilih baris produk yang ingin dihapus!"); return; }
            if (UIHelper.popupKonfirmasi("Hapus Produk?",
                    "Hapus '" + sel.getNamaProduk() + "'?\nTidak bisa dihapus jika masih ada stok terkait.")) {
                if (SiPanganDAO.hapusProduk(sel.getIdProduk())) {
                    refresh(); UIHelper.popupInfo("Berhasil", "Produk berhasil dihapus!");
                } else {
                    UIHelper.popupWarn("Gagal hapus. Pastikan tidak ada stok yang menggunakan produk ini.");
                }
            }
        });
        btnRefresh.setOnAction(e -> refresh());

        content.getChildren().addAll(toolbar, produkTable);
        this.getChildren().addAll(header, content);
        
        refresh();
    }

    public void refresh() {
        if (produkTable != null) {
            produkTable.setItems(FXCollections.observableArrayList(SiPanganDAO.getAllProduk()));
        }
    }

    private ProdukPangan tampilDialogProduk(ProdukPangan existing) {
        Dialog<ProdukPangan> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Tambah Produk Baru" : "Edit Produk");
        dlg.setHeaderText(existing == null ? "Isi detail produk pangan baru"
                : "Perbarui: " + existing.getNamaProduk());

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(14);
        grid.setPadding(new Insets(24));

        TextField tfNama = UIHelper.uiTextField("Nama produk");
        TextField tfKat  = UIHelper.uiTextField("cth: Biji-bijian, Minyak, Tepung");
        TextField tfTgl  = UIHelper.uiTextField("Format: YYYY-MM-DD");

        if (existing != null) {
            tfNama.setText(existing.getNamaProduk());
            tfKat.setText(existing.getKategori());
            tfTgl.setText(existing.getTanggalKedaluwarsa());
        }

        grid.addRow(0, UIHelper.dialogLabel("Nama Produk :"),     tfNama);
        grid.addRow(1, UIHelper.dialogLabel("Kategori :"),        tfKat);
        grid.addRow(2, UIHelper.dialogLabel("Tgl Kedaluwarsa :"), tfTgl);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (tfNama.getText().trim().isEmpty()) { UIHelper.popupWarn("Nama produk tidak boleh kosong!"); return null; }
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

    private TableColumn<ProdukPangan, Integer> pColI(String title, int w) {
        TableColumn<ProdukPangan, Integer> col = new TableColumn<>(title);
        col.setPrefWidth(w); return col;
    }
    private TableColumn<ProdukPangan, String> pColS(String title, int w) {
        TableColumn<ProdukPangan, String> col = new TableColumn<>(title);
        col.setPrefWidth(w); return col;
    }
}
