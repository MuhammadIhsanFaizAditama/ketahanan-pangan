# 🌾 SiPangan — Panduan Setup & Cara Menjalankan

**Sistem Informasi & Distribusi Stok Pangan Nasional**  
Aplikasi Desktop Java | JavaFX + JDBC + MySQL

---

## 📁 Struktur File Proyek

```
ketahanan-panggan/
│
├── src/
│   └── main/
│       └── java/
│           └── sipangan/
│               ├── model/                      ← Kelas-kelas OOP
│               │   ├── Notifikasi.java         → Interface
│               │   ├── Person.java             → Abstract Class
│               │   ├── User.java               → Inheritance + Polymorphism
│               │   ├── ProdukPangan.java       → Entitas Produk
│               │   └── Stok.java               → Asosiasi + Event Trigger
│               │
│               ├── db/                         ← Lapisan Database
│               │   ├── DatabaseConnection.java → Koneksi JDBC
│               │   └── SiPanganDAO.java        → CRUD (PreparedStatement)
│               │
│               └── SiPanganApp.java            ← GUI JavaFX (main class)
│
├── out/                                ← Hasil kompilasi (.class) — dibuat otomatis
├── compile.bat                         ← Skrip kompilasi (klik 2x)
├── run.bat                             ← Skrip jalankan aplikasi (klik 2x)
├── sipangan_db.sql                     ← DDL + data sampel MySQL
└── README.md                           ← Panduan ini
```

---

## ✅ Prasyarat (Yang Harus Dipasang)

| Komponen | Versi | Link Download |
|---|---|---|
| **JDK (Java Development Kit)** | 11 atau 17+ | [adoptium.net](https://adoptium.net/) |
| **JavaFX SDK** | Sesuai JDK | [openjfx.io](https://openjfx.io/) |
| **MySQL Server** | 5.7 atau 8.x | [mysql.com](https://dev.mysql.com/downloads/) |
| **MySQL Connector/J** | 8.x | [mysql.com/downloads/connector/j](https://dev.mysql.com/downloads/connector/j/) |

---

## 🗃️ Langkah 1 — Setup Database MySQL

1. Pastikan **MySQL Server** sudah berjalan.
2. Buka **MySQL Workbench**, **DBeaver**, atau **terminal MySQL**.
3. Jalankan skrip `sipangan_db.sql`:

```bash
# Via terminal MySQL:
mysql -u root -p < sipangan_db.sql

# Atau di MySQL Workbench:
# File → Open SQL Script → pilih sipangan_db.sql → klik Execute (⚡)
```

4. Verifikasi database berhasil dibuat:
```sql
USE sipangan_db;
SHOW TABLES;       -- harus muncul: users, produk_pangan, stok
SELECT * FROM users;
```

---

## ⚙️ Langkah 2 — Konfigurasi Koneksi Database

Buka file `src/sipangan/db/DatabaseConnection.java` dan sesuaikan baris berikut:

```java
private static final String DB_USER = "root"; // ← Ganti jika username MySQL berbeda
private static final String DB_PASS = "";      // ← Isi password MySQL Anda (kosongkan jika tidak ada)
```

> Jika MySQL Anda menggunakan port atau host berbeda, sesuaikan juga bagian `DB_URL`.

---

## 🚀 Langkah 3 — Kompilasi & Jalankan Aplikasi

Anda dapat mengompilasi dan menjalankan aplikasi SiPangan menggunakan salah satu dari dua cara di bawah ini:

### 📦 Opsi A: Menggunakan Maven (Sangat Direkomendasikan & Otomatis)
Cara ini paling praktis karena Maven akan mengunduh dan mengatur semua dependency (JavaFX, MySQL Driver, PDFBox) secara otomatis tanpa perlu konfigurasi path manual.

1. **Kompilasi Proyek:**
   Buka terminal di folder proyek ini, lalu jalankan:
   ```bash
   mvn clean compile
   ```
2. **Jalankan Aplikasi:**
   Jalankan perintah berikut untuk membuka aplikasi:
   ```bash
   mvn javafx:run
   ```

---

### 🔨 Opsi B: Menggunakan Batch Script Manual (`compile.bat` & `run.bat`)
Jika Anda tidak ingin menggunakan Maven, Anda harus mengunduh library eksternal secara manual dan mengonfigurasi path-nya.

1. **Konfigurasi Path:**
   Buka file `compile.bat` dan `run.bat`, lalu sesuaikan `JAVAFX_PATH` dan `MYSQL_JAR` di baris teratas dengan lokasi penyimpanan di komputer Anda:
   ```bat
   set JAVAFX_PATH=C:\path\ke\javafx-sdk\lib
   set MYSQL_JAR=C:\path\ke\mysql-connector-j.jar
   ```

2. **Kompilasi Proyek:**
   Double-click `compile.bat` atau jalankan di CMD:
   ```cmd
   compile.bat
   ```
   *Jika berhasil, folder `out/` akan otomatis dibuat berisi file `.class`.*

3. **Jalankan Aplikasi:**
   Double-click `run.bat` atau jalankan di CMD:
   ```cmd
   run.bat
   ```

Jendela aplikasi SiPangan akan terbuka. Login menggunakan akun default di bawah.

---

## 🔐 Login Default

| Field | Nilai |
|---|---|
| **Username** | `admin` |
| **Password** | `admin123` |

---

## 🖥️ Fitur Aplikasi

| Menu | Fitur |
|---|---|
| **Login** | Validasi ke database MySQL, enkapsulasi ketat password |
| **Dashboard** | Statistik total produk, jumlah stok kritis, daftar peringatan |
| **Produk Pangan** | CRUD lengkap (Tambah, Edit, Hapus, Refresh) |
| **Manajemen Stok** | CRUD stok, Kurangi Stok (auto-alert), warna merah jika kritis |
| **Cetak Laporan** | Simulasi JasperReports (boilerplate siap untuk integrasi penuh) |

---

## 🎯 Konsep OOP yang Diimplementasikan

| Konsep | File | Implementasi |
|---|---|---|
| **Abstract Class** | `Person.java` | Tidak bisa di-`new`, punya method abstrak |
| **Inheritance** | `User.java` | `User extends Person` — mewarisi atribut induk |
| **Encapsulation** | semua model | Semua atribut `private`, akses via getter/setter tervalidasi |
| **Polymorphism** | `User.java`, `Stok.java` | Override `tampilkanInfo()` dan `kirimNotifikasi()` |
| **Interface** | `Notifikasi.java` | Kontrak yang wajib dipenuhi kelas implementornya |
| **Association (HAS-A)** | `Stok.java` | `Stok` memiliki referensi ke objek `ProdukPangan` |
| **Event Trigger** | `Stok.java` | `kurangiStok()` otomatis memicu `kirimNotifikasi()` saat kritis |

---

## 📊 Mengaktifkan JasperReports (Opsional)

Untuk mengaktifkan cetak laporan PDF asli:

1. **Download** `jasperreports-x.x.x-all.jar` dari [SourceForge JasperReports](https://sourceforge.net/projects/jasperreports/files/)
2. **Tambahkan** path `.jar` ke variabel `MYSQL_JAR` di `compile.bat` dan `run.bat` (pisahkan dengan `;`)
3. **Buat** template `laporan_stok.jrxml` (bisa menggunakan Jaspersoft Studio)
4. **Uncomment** blok kode JasperReports di method `cetakLaporanStok()` di `SiPanganApp.java`

---

## 🐛 Troubleshooting Umum

| Error | Solusi |
|---|---|
| `Communications link failure` | MySQL Server belum berjalan. Jalankan MySQL terlebih dahulu. |
| `Access denied for user 'root'` | Password di `DatabaseConnection.java` salah. Sesuaikan `DB_PASS`. |
| `Unknown database 'sipangan_db'` | Belum menjalankan `sipangan_db.sql`. Jalankan dulu di MySQL. |
| `Error: JavaFX runtime components are missing` | Path `JAVAFX_PATH` di `compile.bat`/`run.bat` salah atau belum diisi. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Path `MYSQL_JAR` di `compile.bat`/`run.bat` salah atau file `.jar` belum ada. |
| `error: package sipangan.model does not exist` | Kompilasi tidak dilakukan dari folder `ketahanan-panggan/`. Pastikan jalankan `compile.bat` dari folder yang benar. |

---

*Dibuat untuk keperluan pembelajaran PBO (Pemrograman Berorientasi Objek)*
