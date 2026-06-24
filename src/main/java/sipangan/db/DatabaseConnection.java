package sipangan.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// =========================================================================
// FILE    : DatabaseConnection.java
// PACKAGE : sipangan.db
// KONSEP  : Kelas utilitas JDBC untuk membuka koneksi ke database MySQL.
//
// Semua method bersifat 'static' → bisa dipanggil tanpa membuat objek:
//   DatabaseConnection.getConnection()   ← langsung, tanpa 'new'
//
// ⚙️ KONFIGURASI: Sesuaikan DB_USER dan DB_PASS dengan setup MySQL Anda!
// =========================================================================
public class DatabaseConnection {

    // ─────────────────────────────────────────────────────────────────
    // ⚙️ GANTI NILAI DI BAWAH INI SESUAI SETUP MYSQL ANDA:
    // ─────────────────────────────────────────────────────────────────
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sipangan_db" +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    // ^ Ganti 'localhost:3306' jika MySQL Anda ada di host/port berbeda

    private static final String DB_USER = "root"; // ← Ganti jika perlu
    private static final String DB_PASS = ""; // ← Isi password MySQL Anda
    // ─────────────────────────────────────────────────────────────────

    /**
     * Membuka dan mengembalikan objek Connection ke MySQL.
     *
     * CARA PAKAI YANG BENAR (try-with-resources):
     *
     * try (Connection conn = DatabaseConnection.getConnection()) {
     * // ... gunakan conn di sini ...
     * } // conn.close() otomatis dipanggil, meski ada exception!
     *
     * Jangan lupa: Connection adalah resource "berat" (koneksi jaringan).
     * Harus SELALU ditutup setelah selesai agar tidak terjadi resource leak.
     *
     * @return objek Connection yang siap digunakan
     * @throws SQLException jika koneksi gagal (server mati, password salah, dll.)
     */
    public static Connection getConnection() throws SQLException {
        // DriverManager secara otomatis mencari MySQL JDBC Driver
        // (dari file mysql-connector-java.jar di classpath) dan membuka koneksi.
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
