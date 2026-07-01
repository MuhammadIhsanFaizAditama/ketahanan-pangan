package sipangan;

import sipangan.db.SiPanganDAO;
import sipangan.model.ProdukPangan;
import sipangan.model.Stok;
import sipangan.model.User;
import sipangan.util.LaporanPDFGenerator;

import java.util.List;
import java.util.Scanner;

// =========================================================================
// FILE    : SiPanganApp.java
// PACKAGE : sipangan
// KONSEP  : ENTRY POINT APLIKASI CLI (Command Line Interface)
//
// Kelas ini menjalankan seluruh alur interaksi pengguna melalui terminal.
// Method main() membuat OBJEK dari kelas ini, lalu memanggil method run():
//
//   SiPanganApp app = new SiPanganApp();  ← buat objek
//   app.run();                             ← panggil method dari objek
//
// Ini menunjukkan prinsip OOP: data (currentUser, scanner) dan perilaku
// (showMainMenu, doLogin, dll) dikemas dalam SATU OBJEK, bukan tersebar
// sebagai fungsi-fungsi static yang terpisah.
// =========================================================================
public class SiPanganApp {

    // [ENKAPSULASI] Atribut instance — milik setiap objek SiPanganApp.
    // Berbeda dengan 'static' yang milik KELAS (shared), atribut instance
    // milik OBJEK individual: setiap objek punya salinan sendiri.
    private User currentUser;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * ENTRY POINT: Titik masuk program Java.
     *
     * Di sini kita MEMBUAT OBJEK dari SiPanganApp, lalu MEMANGGIL METHOD
     * milik objek tersebut. Ini menunjukkan pola dasar OOP:
     *   1. new SiPanganApp() → buat objek di memori (heap)
     *   2. app.run()         → panggil method instance milik objek
     *
     * Method main() sendiri tetap harus 'static' karena JVM memanggilnya
     * sebelum ada objek apapun yang dibuat.
     */
    public static void main(String[] args) {
        SiPanganApp app = new SiPanganApp();
        app.run();
    }

    /**
     * Method instance yang menjalankan loop utama aplikasi.
     * Karena bukan static, method ini punya akses langsung ke atribut
     * instance: 'currentUser' dan 'scanner' milik objek yang memanggilnya.
     */
    public void run() {
        System.out.println("🌾 Selamat Datang di SiPangan CLI 🌾");

        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    // =========================================================================
    // MENU AUTENTIKASI
    // =========================================================================

    private void showAuthMenu() {
        System.out.println("\n=======================================================");
        System.out.println("               MENU AUTENTIKASI SIPANGAN");
        System.out.println("=======================================================");
        System.out.println("1. Login");
        System.out.println("2. Keluar");
        System.out.print("Pilih menu [1-2]: ");

        String pilihan = scanner.nextLine().trim();
        switch (pilihan) {
            case "1":
                doLogin();
                break;
            case "2":
                System.out.println("\nTerima kasih telah menggunakan SiPangan. Sampai jumpa!");
                System.exit(0);
                break;
            default:
                System.out.println("⚠️ Pilihan tidak valid. Silakan coba lagi.");
        }
    }

    private void doLogin() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("                     MASUK KE AKUN");
        System.out.println("-------------------------------------------------------");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = SiPanganDAO.login(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("\n✅ Login berhasil! Selamat datang, " + currentUser.getNama() + ".");
            currentUser.tampilkanInfo(); // Memanggil Override Polymorphic Method dari abstract class Person
        } else {
            System.out.println("❌ Login gagal! Username atau password salah.");
        }
    }

    // =========================================================================
    // MENU UTAMA
    // =========================================================================

    private void showMainMenu() {
        System.out.println("\n=======================================================");
        System.out.println("                     MENU UTAMA");
        System.out.println("=======================================================");
        System.out.println("Pengguna: " + currentUser.getNama() + " (@" + currentUser.getUsername() + ")");
        System.out.println("-------------------------------------------------------");
        System.out.println("1. Dashboard (Statistik & Alert)");
        System.out.println("2. Manajemen Produk Pangan (CRUD)");
        System.out.println("3. Manajemen Stok Gudang (CRUD, Transaksi & Laporan)");
        System.out.println("4. Keluar / Logout");
        System.out.print("Pilih menu [1-4]: ");

        String pilihan = scanner.nextLine().trim();
        switch (pilihan) {
            case "1":
                showDashboard();
                break;
            case "2":
                showProdukMenu();
                break;
            case "3":
                showStokMenu();
                break;
            case "4":
                currentUser = null;
                System.out.println("\n🚪 Berhasil logout.");
                break;
            default:
                System.out.println("⚠️ Pilihan tidak valid. Silakan coba lagi.");
        }
    }

    // =========================================================================
    // 1. DASHBOARD
    // =========================================================================

    private void showDashboard() {
        System.out.println("\n=======================================================");
        System.out.println("                 DASHBOARD SIPANGAN");
        System.out.println("=======================================================");
        int totalProduk = SiPanganDAO.countProduk();
        int stokKritis = SiPanganDAO.countStokKritis();

        System.out.printf("📦 Total Jenis Produk Terdaftar : %d\n", totalProduk);
        System.out.printf("⚠️ Jumlah Entri Stok Kritis     : %d\n", stokKritis);
        System.out.println("-------------------------------------------------------");

        List<Stok> allStok = SiPanganDAO.getAllStok();
        System.out.println("DAFTAR STOK KRITIS (<= Batas Minimum):");
        boolean adaKritis = false;

        System.out.printf("| %-4s | %-20s | %-12s | %-12s | %-25s |\n", "ID", "Nama Produk", "Kuantitas", "Batas Min", "Gudang Wilayah");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Stok s : allStok) {
            if (s.isBawahMinimum()) {
                adaKritis = true;
                System.out.printf("| %-4d | %-20s | %-12d | %-12d | %-25s |\n",
                        s.getIdStok(),
                        truncate(s.getProduk().getNamaProduk(), 20),
                        s.getKuantitas(),
                        s.getBatasMinimum(),
                        truncate(s.getGudangWilayah(), 25)
                );
            }
        }
        if (!adaKritis) {
            System.out.println("|                    TIDAK ADA STOK KRITIS                      |");
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.print("\nTekan Enter untuk kembali ke Menu Utama...");
        scanner.nextLine();
    }

    // =========================================================================
    // 2. MANAJEMEN PRODUK PANGAN (CRUD)
    // =========================================================================

    private void showProdukMenu() {
        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("               MANAJEMEN PRODUK PANGAN");
            System.out.println("=======================================================");
            System.out.println("1. Tampilkan Semua Produk");
            System.out.println("2. Tambah Produk Baru");
            System.out.println("3. Edit Produk");
            System.out.println("4. Hapus Produk");
            System.out.println("5. Kembali ke Menu Utama");
            System.out.print("Pilih tindakan [1-5]: ");

            String pilihan = scanner.nextLine().trim();
            switch (pilihan) {
                case "1":
                    tampilkanSemuaProduk();
                    break;
                case "2":
                    tambahProdukBaru();
                    break;
                case "3":
                    editProduk();
                    break;
                case "4":
                    hapusProduk();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("⚠️ Pilihan tidak valid.");
            }
        }
    }

    private void tampilkanSemuaProduk() {
        List<ProdukPangan> list = SiPanganDAO.getAllProduk();
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("                            DAFTAR PRODUK PANGAN");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("| %-6s | %-30s | %-18s | %-16s |\n", "ID", "Nama Produk", "Kategori", "Tgl Kedaluwarsa");
        System.out.println("--------------------------------------------------------------------------------");
        for (ProdukPangan p : list) {
            System.out.printf("| %-6d | %-30s | %-18s | %-16s |\n",
                    p.getIdProduk(),
                    truncate(p.getNamaProduk(), 30),
                    truncate(p.getKategori(), 18),
                    p.getTanggalKedaluwarsa() != null ? p.getTanggalKedaluwarsa() : "-"
            );
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private void tambahProdukBaru() {
        System.out.println("\n--- Tambah Produk Baru ---");
        System.out.print("Nama Produk: ");
        String nama = scanner.nextLine().trim();
        if (nama.isEmpty()) {
            System.out.println("❌ Nama produk tidak boleh kosong.");
            return;
        }
        System.out.print("Kategori (cth: Biji-bijian, Minyak, Tepung): ");
        String kategori = scanner.nextLine().trim();
        System.out.print("Tgl Kedaluwarsa (YYYY-MM-DD atau kosongkan jika tidak ada): ");
        String tgl = scanner.nextLine().trim();
        if (tgl.isEmpty()) {
            tgl = null;
        }

        ProdukPangan baru = new ProdukPangan(0, nama, kategori, tgl);
        if (SiPanganDAO.tambahProduk(baru)) {
            System.out.println("✅ Produk berhasil ditambahkan!");
        } else {
            System.out.println("❌ Gagal menambahkan produk.");
        }
    }

    private void editProduk() {
        tampilkanSemuaProduk();
        System.out.print("\nMasukkan ID Produk yang ingin diedit: ");
        int id = readIntInput();
        if (id == -1) return;

        List<ProdukPangan> list = SiPanganDAO.getAllProduk();
        ProdukPangan target = null;
        for (ProdukPangan p : list) {
            if (p.getIdProduk() == id) {
                target = p;
                break;
            }
        }

        if (target == null) {
            System.out.println("❌ Produk dengan ID " + id + " tidak ditemukan.");
            return;
        }

        System.out.println("\nEdit Produk: " + target.getNamaProduk());
        System.out.print("Nama Baru (kosongkan untuk tidak mengubah): ");
        String nama = scanner.nextLine().trim();
        if (!nama.isEmpty()) {
            target.setNamaProduk(nama);
        }

        System.out.print("Kategori Baru (kosongkan untuk tidak mengubah): ");
        String kategori = scanner.nextLine().trim();
        if (!kategori.isEmpty()) {
            target.setKategori(kategori);
        }

        System.out.print("Tgl Kedaluwarsa Baru (YYYY-MM-DD atau kosongkan untuk tidak mengubah, tulis 'null' untuk menghapus tgl): ");
        String tgl = scanner.nextLine().trim();
        if (!tgl.isEmpty()) {
            if (tgl.equalsIgnoreCase("null")) {
                target.setTanggalKedaluwarsa(null);
            } else {
                target.setTanggalKedaluwarsa(tgl);
            }
        }

        if (SiPanganDAO.updateProduk(target)) {
            System.out.println("✅ Produk berhasil diperbarui!");
        } else {
            System.out.println("❌ Gagal memperbarui produk.");
        }
    }

    private void hapusProduk() {
        tampilkanSemuaProduk();
        System.out.print("\nMasukkan ID Produk yang ingin dihapus: ");
        int id = readIntInput();
        if (id == -1) return;

        System.out.print("Apakah Anda yakin ingin menghapus produk ini? (y/n): ");
        String konfirmasi = scanner.nextLine().trim().toLowerCase();
        if (konfirmasi.equals("y")) {
            if (SiPanganDAO.hapusProduk(id)) {
                System.out.println("✅ Produk berhasil dihapus!");
            } else {
                System.out.println("❌ Gagal menghapus produk. Pastikan tidak ada stok yang terkait dengan produk ini.");
            }
        } else {
            System.out.println("Hapus dibatalkan.");
        }
    }

    // =========================================================================
    // 3. MANAJEMEN STOK GUDANG
    // =========================================================================

    private void showStokMenu() {
        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("               MANAJEMEN STOK GUDANG");
            System.out.println("=======================================================");
            System.out.println("1. Tampilkan Semua Stok");
            System.out.println("2. Tambah Data Stok");
            System.out.println("3. Kurangi Stok (Pemicu Alert)");
            System.out.println("4. Edit Data Stok");
            System.out.println("5. Hapus Data Stok");
            System.out.println("6. Cetak Laporan PDF");
            System.out.println("7. Kembali ke Menu Utama");
            System.out.print("Pilih tindakan [1-7]: ");

            String pilihan = scanner.nextLine().trim();
            switch (pilihan) {
                case "1":
                    tampilkanSemuaStok();
                    break;
                case "2":
                    tambahDataStok();
                    break;
                case "3":
                    kurangiStok();
                    break;
                case "4":
                    editDataStok();
                    break;
                case "5":
                    hapusDataStok();
                    break;
                case "6":
                    cetakLaporanPDF();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("⚠️ Pilihan tidak valid.");
            }
        }
    }

    private void tampilkanSemuaStok() {
        List<Stok> list = SiPanganDAO.getAllStok();
        System.out.println("\n----------------------------------------------------------------------------------------------------------------");
        System.out.println("                                             DAFTAR STOK GUDANG WILAYAH");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-6s | %-25s | %-10s | %-10s | %-12s | %-30s | %-6s |\n",
                "ID", "Nama Produk", "Kuantitas", "Batas Min", "Kualitas", "Gudang Wilayah", "Status");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        for (Stok s : list) {
            String status = s.isBawahMinimum() ? "KRITIS" : "AMAN";
            System.out.printf("| %-6d | %-25s | %-10d | %-10d | %-12s | %-30s | %-6s |\n",
                    s.getIdStok(),
                    truncate(s.getProduk().getNamaProduk(), 25),
                    s.getKuantitas(),
                    s.getBatasMinimum(),
                    s.getStatusKualitas(),
                    truncate(s.getGudangWilayah(), 30),
                    status
            );
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    private void tambahDataStok() {
        List<ProdukPangan> produkList = SiPanganDAO.getAllProduk();
        if (produkList.isEmpty()) {
            System.out.println("❌ Tidak ada produk pangan terdaftar. Tambah produk terlebih dahulu.");
            return;
        }

        tampilkanSemuaProduk();
        System.out.print("\nMasukkan ID Produk untuk stok ini: ");
        int idProduk = readIntInput();
        if (idProduk == -1) return;

        ProdukPangan targetProduk = null;
        for (ProdukPangan p : produkList) {
            if (p.getIdProduk() == idProduk) {
                targetProduk = p;
                break;
            }
        }

        if (targetProduk == null) {
            System.out.println("❌ Produk dengan ID " + idProduk + " tidak ditemukan.");
            return;
        }

        System.out.print("Kuantitas Awal: ");
        int qty = readIntInput();
        if (qty < 0) {
            System.out.println("❌ Kuantitas tidak boleh negatif.");
            return;
        }

        System.out.print("Batas Minimum: ");
        int min = readIntInput();
        if (min < 0) {
            System.out.println("❌ Batas minimum tidak boleh negatif.");
            return;
        }

        System.out.print("Status Kualitas (LAYAK / PERLU_CEPAT / TIDAK_LAYAK): ");
        String status = scanner.nextLine().trim().toUpperCase();

        System.out.print("Nama Gudang Wilayah: ");
        String gudang = scanner.nextLine().trim();
        if (gudang.isEmpty()) {
            System.out.println("❌ Gudang wilayah tidak boleh kosong.");
            return;
        }

        try {
            Stok baru = new Stok(0, targetProduk, qty, min, status, gudang);
            if (SiPanganDAO.tambahStok(baru)) {
                System.out.println("✅ Stok berhasil ditambahkan!");
            } else {
                System.out.println("❌ Gagal menambahkan data stok.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ Error: " + ex.getMessage());
        }
    }

    private void kurangiStok() {
        tampilkanSemuaStok();
        System.out.print("\nMasukkan ID Stok yang ingin dikurangi: ");
        int idStok = readIntInput();
        if (idStok == -1) return;

        List<Stok> list = SiPanganDAO.getAllStok();
        Stok target = null;
        for (Stok s : list) {
            if (s.getIdStok() == idStok) {
                target = s;
                break;
            }
        }

        if (target == null) {
            System.out.println("❌ Stok dengan ID " + idStok + " tidak ditemukan.");
            return;
        }

        System.out.println("Stok saat ini: " + target.getKuantitas() + " unit (" + target.getProduk().getNamaProduk() + " di " + target.getGudangWilayah() + ")");
        System.out.print("Masukkan jumlah yang ingin dikurangi: ");
        int jumlah = readIntInput();
        if (jumlah <= 0) {
            System.out.println("❌ Jumlah pengurangan harus lebih dari 0.");
            return;
        }

        if (jumlah > target.getKuantitas()) {
            System.out.println("❌ Stok tidak mencukupi! Tersedia hanya " + target.getKuantitas() + " unit.");
            return;
        }

        // Panggil metode logika bisnis kurangiStok yang secara otomatis men-trigger Notifikasi
        String alertMsg = target.kurangiStok(jumlah);

        // Simpan perubahan ke database
        boolean ok = SiPanganDAO.updateKuantitasStok(target.getIdStok(), target.getKuantitas());
        if (ok) {
            System.out.println("✅ Stok berhasil dikurangi di database.");
            if (alertMsg != null) {
                System.out.println("\n⚠️ [EVENT ALERT SYSTEM TRIGGERED]");
                System.out.println(alertMsg);
            }
        } else {
            System.out.println("❌ Gagal memperbarui kuantitas stok di database.");
        }
    }

    private void editDataStok() {
        tampilkanSemuaStok();
        System.out.print("\nMasukkan ID Stok yang ingin diedit: ");
        int idStok = readIntInput();
        if (idStok == -1) return;

        List<Stok> list = SiPanganDAO.getAllStok();
        Stok target = null;
        for (Stok s : list) {
            if (s.getIdStok() == idStok) {
                target = s;
                break;
            }
        }

        if (target == null) {
            System.out.println("❌ Stok dengan ID " + idStok + " tidak ditemukan.");
            return;
        }

        System.out.println("\nEdit data stok untuk: " + target.getProduk().getNamaProduk() + " di " + target.getGudangWilayah());

        System.out.print("Kuantitas Baru (kosongkan untuk tidak mengubah): ");
        String qtyInput = scanner.nextLine().trim();
        if (!qtyInput.isEmpty()) {
            try {
                int q = Integer.parseInt(qtyInput);
                if (q < 0) throw new NumberFormatException();
                target.setKuantitas(q);
            } catch (NumberFormatException e) {
                System.out.println("❌ Kuantitas tidak valid. Perubahan dibatalkan.");
                return;
            }
        }

        System.out.print("Batas Minimum Baru (kosongkan untuk tidak mengubah): ");
        String minInput = scanner.nextLine().trim();
        if (!minInput.isEmpty()) {
            try {
                int m = Integer.parseInt(minInput);
                if (m < 0) throw new NumberFormatException();
                target.setBatasMinimum(m);
            } catch (NumberFormatException e) {
                System.out.println("❌ Batas minimum tidak valid. Perubahan dibatalkan.");
                return;
            }
        }

        System.out.print("Status Kualitas Baru (LAYAK / PERLU_CEPAT / TIDAK_LAYAK, kosongkan untuk tidak mengubah): ");
        String status = scanner.nextLine().trim().toUpperCase();
        if (!status.isEmpty()) {
            try {
                target.setStatusKualitas(status);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
                return;
            }
        }

        System.out.print("Nama Gudang Wilayah Baru (kosongkan untuk tidak mengubah): ");
        String gudang = scanner.nextLine().trim();
        if (!gudang.isEmpty()) {
            target.setGudangWilayah(gudang);
        }

        if (SiPanganDAO.updateStok(target)) {
            System.out.println("✅ Data stok berhasil diperbarui!");
        } else {
            System.out.println("❌ Gagal memperbarui data stok.");
        }
    }

    private void hapusDataStok() {
        tampilkanSemuaStok();
        System.out.print("\nMasukkan ID Stok yang ingin dihapus: ");
        int idStok = readIntInput();
        if (idStok == -1) return;

        System.out.print("Apakah Anda yakin ingin menghapus data stok ini? (y/n): ");
        String konfirmasi = scanner.nextLine().trim().toLowerCase();
        if (konfirmasi.equals("y")) {
            if (SiPanganDAO.hapusStok(idStok)) {
                System.out.println("✅ Data stok berhasil dihapus!");
            } else {
                System.out.println("❌ Gagal menghapus data stok.");
            }
        } else {
            System.out.println("Hapus dibatalkan.");
        }
    }

    // =========================================================================
    // 4. CETAK PDF LAPORAN
    // =========================================================================

    /**
     * Mendelegasikan pembuatan laporan PDF ke kelas utilitas
     * LaporanPDFGenerator. Ini menunjukkan pemisahan tanggung jawab:
     *   - SiPanganApp → mengurus interaksi pengguna (menu CLI)
     *   - LaporanPDFGenerator → mengurus pembuatan file PDF
     */
    private void cetakLaporanPDF() {
        System.out.println("\n⏳ Sedang membuat laporan stok PDF...");
        List<Stok> dataStok = SiPanganDAO.getAllStok();
        LaporanPDFGenerator.generate(dataStok);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Membaca input angka dari pengguna.
     * Mengembalikan -1 jika input bukan angka valid.
     */
    private int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Input harus berupa angka!");
            return -1;
        }
    }

    /**
     * Helper: potong string yang terlalu panjang agar tampilan tabel tetap rapi.
     * Jika panjang string melebihi 'max', sisanya diganti dengan '…'.
     */
    private String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max - 1) + "…" : (s != null ? s : "");
    }
}
