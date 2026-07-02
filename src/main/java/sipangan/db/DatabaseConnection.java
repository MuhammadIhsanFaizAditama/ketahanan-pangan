package sipangan.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Koneksi Database
public class DatabaseConnection {

    // Konfigurasi Database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sipangan_db" +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    // Mendapatkan koneksi database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
