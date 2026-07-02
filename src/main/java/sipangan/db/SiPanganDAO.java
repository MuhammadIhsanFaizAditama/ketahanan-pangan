package sipangan.db;

import sipangan.model.ProdukPangan;
import sipangan.model.Stok;
import sipangan.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Data Access Object
public class SiPanganDAO {

    // Autentikasi User
    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
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
        return null;
    }

    // CRUD Produk Pangan
    public static List<ProdukPangan> getAllProduk() {
        List<ProdukPangan> list = new ArrayList<>();
        String sql = "SELECT * FROM produk_pangan ORDER BY nama_produk ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
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

    public static boolean tambahProduk(ProdukPangan p) {
        String sql = "INSERT INTO produk_pangan (nama_produk, kategori, tgl_kedaluwarsa) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNamaProduk());
            ps.setString(2, p.getKategori());
            ps.setString(3, p.getTanggalKedaluwarsa());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] TambahProduk Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateProduk(ProdukPangan p) {
        String sql = "UPDATE produk_pangan " +
                "SET nama_produk = ?, kategori = ?, tgl_kedaluwarsa = ? " +
                "WHERE id_produk = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNamaProduk());
            ps.setString(2, p.getKategori());
            ps.setString(3, p.getTanggalKedaluwarsa());
            ps.setInt(4, p.getIdProduk());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] UpdateProduk Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean hapusProduk(int idProduk) {
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

    // CRUD Stok Gudang
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
                ProdukPangan produk = new ProdukPangan(
                        rs.getInt("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("kategori"),
                        rs.getString("tgl_kedaluwarsa"));
                list.add(new Stok(
                        rs.getInt("id_stok"),
                        produk,
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

    public static boolean tambahStok(Stok s) {
        String sql = "INSERT INTO stok (id_produk, kuantitas, batas_minimum, " +
                "                  status_kualitas, gudang_wilayah) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getProduk().getIdProduk());
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

    // Update kuantitas stok
    public static boolean updateKuantitasStok(int idStok, int kuantitasBaru) {
        String sql = "UPDATE stok SET kuantitas = ? WHERE id_stok = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, kuantitasBaru);
            ps.setInt(2, idStok);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] UpdateKuantitas Error: " + e.getMessage());
            return false;
        }
    }

    // Update semua data stok
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

    // Hapus data stok
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

    // Statistik Dashboard
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
