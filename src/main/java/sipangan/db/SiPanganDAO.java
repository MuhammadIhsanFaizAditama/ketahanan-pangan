package sipangan.db;

import sipangan.model.ProdukPangan;
import sipangan.model.Stok;
import sipangan.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// =========================================================================
// FILE    : SiPanganDAO.java
// PACKAGE : sipangan.db
// KONSEP  : DATA ACCESS OBJECT (DAO) — pola desain yang memisahkan logika
//           akses database (SQL) dari logika bisnis dan antarmuka CLI.
//
// Semua operasi CRUD (Create-Read-Update-Delete) ke MySQL ada di sini.
// Menggunakan PreparedStatement, bukan String concatenation, untuk mencegah
// SQL Injection — ini WAJIB dalam praktik keamanan JDBC.
// =========================================================================
public class SiPanganDAO {

    // ═════════════════════════════════════════════════════════════════
    // A. OPERASI USER — Autentikasi
    // ═════════════════════════════════════════════════════════════════

    /**
     * Validasi login ke tabel 'users' menggunakan PreparedStatement.
     *
     * Mengapa PreparedStatement, bukan Statement biasa?
     * String sql = "SELECT * FROM users WHERE username='" + username + "'";
     * Jika username diisi: admin' OR '1'='1 → query selalu benar! Bahaya!
     * PreparedStatement mengescape karakter khusus secara otomatis → aman.
     *
     * @return objek User jika login berhasil, null jika gagal
     */
    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        // Try-with-resources: conn & ps otomatis ditutup setelah blok try selesai
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username); // Isi placeholder '?' pertama
            ps.setString(2, password); // Isi placeholder '?' kedua
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Ada baris hasil = username + password cocok
                return new User(
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Login Error: " + e.getMessage());
        }
        return null; // null = login gagal
    }

    // ═════════════════════════════════════════════════════════════════
    // B. CRUD PRODUK PANGAN
    // ═════════════════════════════════════════════════════════════════

    /** READ — Ambil semua produk dari database, diurutkan berdasarkan nama */
    public static List<ProdukPangan> getAllProduk() {
        List<ProdukPangan> list = new ArrayList<>();
        String sql = "SELECT * FROM produk_pangan ORDER BY nama_produk ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) { // Iterasi setiap baris hasil query
                list.add(new ProdukPangan(
                        rs.getInt("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("kategori"),
                        rs.getString("tgl_kedaluwarsa")));
            }
        } catch (SQLException e) {
            System.err.println("[DAO] GetAllProduk Error: " + e.getMessage());
        }
        return list;
    }

    /** CREATE — Tambah produk baru ke database */
    public static boolean tambahProduk(ProdukPangan p) {
        String sql = "INSERT INTO produk_pangan (nama_produk, kategori, tgl_kedaluwarsa) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNamaProduk());
            ps.setString(2, p.getKategori());
            ps.setString(3, p.getTanggalKedaluwarsa());
            // executeUpdate() = jumlah baris yang terpengaruh; > 0 berarti berhasil
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] TambahProduk Error: " + e.getMessage());
            return false;
        }
    }

    /** UPDATE — Perbarui data produk yang sudah ada berdasarkan ID */
    public static boolean updateProduk(ProdukPangan p) {
        String sql = "UPDATE produk_pangan " +
                "SET nama_produk = ?, kategori = ?, tgl_kedaluwarsa = ? " +
                "WHERE id_produk = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNamaProduk());
            ps.setString(2, p.getKategori());
            ps.setString(3, p.getTanggalKedaluwarsa());
            ps.setInt(4, p.getIdProduk()); // Klausa WHERE — pastikan hanya produk ini
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] UpdateProduk Error: " + e.getMessage());
            return false;
        }
    }

    /** DELETE — Hapus produk berdasarkan ID */
    public static boolean hapusProduk(int idProduk) {
        // Catatan: jika produk masih direferensi oleh tabel stok (FK),
        // MySQL akan menolak penghapusan → tangani pesannya di CLI.
        String sql = "DELETE FROM produk_pangan WHERE id_produk = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProduk);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] HapusProduk Error: " + e.getMessage());
            return false;
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // C. CRUD STOK GUDANG
    // ═════════════════════════════════════════════════════════════════

    /**
     * READ — Ambil semua stok beserta nama produknya menggunakan SQL JOIN.
     *
     * INNER JOIN menggabungkan baris dari tabel 'stok' dan 'produk_pangan'
     * berdasarkan kolom yang berhubungan (id_produk).
     * Hasilnya: satu baris = data stok + data produk (tanpa query dua kali).
     */
    public static List<Stok> getAllStok() {
        List<Stok> list = new ArrayList<>();
        String sql = "SELECT s.id_stok, s.id_produk, s.kuantitas, s.batas_minimum, " +
                "       s.status_kualitas, s.gudang_wilayah, " +
                "       p.nama_produk, p.kategori, p.tgl_kedaluwarsa " +
                "FROM stok s " +
                "INNER JOIN produk_pangan p ON s.id_produk = p.id_produk " +
                "ORDER BY p.nama_produk ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Rekonstruksi objek ProdukPangan dari kolom hasil JOIN
                ProdukPangan produk = new ProdukPangan(
                        rs.getInt("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("kategori"),
                        rs.getString("tgl_kedaluwarsa"));
                // Rekonstruksi objek Stok — Stok has-a ProdukPangan (Asosiasi)
                list.add(new Stok(
                        rs.getInt("id_stok"),
                        produk, // ← referensi ke objek ProdukPangan
                        rs.getInt("kuantitas"),
                        rs.getInt("batas_minimum"),
                        rs.getString("status_kualitas"),
                        rs.getString("gudang_wilayah")));
            }
        } catch (SQLException e) {
            System.err.println("[DAO] GetAllStok Error: " + e.getMessage());
        }
        return list;
    }

    /** CREATE — Tambah entri stok baru */
    public static boolean tambahStok(Stok s) {
        String sql = "INSERT INTO stok (id_produk, kuantitas, batas_minimum, " +
                "                  status_kualitas, gudang_wilayah) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getProduk().getIdProduk()); // Simpan ID produk (bukan objeknya)
            ps.setInt(2, s.getKuantitas());
            ps.setInt(3, s.getBatasMinimum());
            ps.setString(4, s.getStatusKualitas());
            ps.setString(5, s.getGudangWilayah());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] TambahStok Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * UPDATE — Perbarui HANYA kolom kuantitas pada satu baris stok.
     * Digunakan setelah kurangiStok() dipanggil pada objek Stok di memori.
     * Pola: objek Java diperbarui → disinkronkan ke database.
     */
    public static boolean updateKuantitasStok(int idStok, int kuantitasBaru) {
        String sql = "UPDATE stok SET kuantitas = ? WHERE id_stok = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, kuantitasBaru);
            ps.setInt(2, idStok); // WHERE clause — hanya baris dengan id_stok ini!
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] UpdateKuantitas Error: " + e.getMessage());
            return false;
        }
    }

    /** UPDATE — Perbarui semua field stok (untuk fitur Edit Stok) */
    public static boolean updateStok(Stok s) {
        String sql = "UPDATE stok " +
                "SET kuantitas = ?, batas_minimum = ?, " +
                "    status_kualitas = ?, gudang_wilayah = ? " +
                "WHERE id_stok = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getKuantitas());
            ps.setInt(2, s.getBatasMinimum());
            ps.setString(3, s.getStatusKualitas());
            ps.setString(4, s.getGudangWilayah());
            ps.setInt(5, s.getIdStok());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] UpdateStok Error: " + e.getMessage());
            return false;
        }
    }

    /** DELETE — Hapus satu entri stok dari database */
    public static boolean hapusStok(int idStok) {
        String sql = "DELETE FROM stok WHERE id_stok = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idStok);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] HapusStok Error: " + e.getMessage());
            return false;
        }
    }

    // ─── Fungsi Statistik (untuk Dashboard) ──────────────────────────

    /** Hitung total jumlah produk yang terdaftar */
    public static int countProduk() {
        String sql = "SELECT COUNT(*) FROM produk_pangan";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DAO] CountProduk Error: " + e.getMessage());
        }
        return 0;
    }

    /** Hitung berapa stok yang kuantitasnya di bawah/sama dengan batasMinimum */
    public static int countStokKritis() {
        String sql = "SELECT COUNT(*) FROM stok WHERE kuantitas <= batas_minimum";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DAO] CountStokKritis Error: " + e.getMessage());
        }
        return 0;
    }
}
