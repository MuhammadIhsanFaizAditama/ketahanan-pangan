# 🌾 SiPangan — Panduan Setup & Cara Menjalankan

**Sistem Informasi & Distribusi Stok Pangan Nasional**  
Aplikasi Java CLI (Command Line Interface) | JDBC + MySQL + PDFBox

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
│               ├── util/                       ← Utilitas / Helper
│               │   └── LaporanPDFGenerator.java → Pembuat laporan PDF (PDFBox)
│               │
│               └── SiPanganApp.java            ← Entry Point CLI (main class)
│
├── lib/                                ← Folder Library / Dependency Lokal
│   └── pdfbox-app-3.0.3.jar            ← Apache PDFBox (untuk cetak PDF)
├── out/                                ← Hasil kompilasi (.class) — dibuat otomatis
├── compile.bat                         ← Skrip kompilasi manual (klik 2x)
├── run.bat                             ← Skrip jalankan aplikasi manual (klik 2x)
├── sipangan_db.sql                     ← DDL + data sampel MySQL
├── pom.xml                             ← Konfigurasi Maven Project
└── README.md                           ← Panduan ini
```

---

## ✅ Prasyarat (Yang Harus Dipasang)

| Komponen | Versi | Link Download |
|---|---|---|
| **JDK (Java Development Kit)** | 17+ | [adoptium.net](https://adoptium.net/) |
| **MySQL Server** | 5.7 atau 8.x/9.x | [mysql.com](https://dev.mysql.com/downloads/) |
| **MySQL Connector/J** | 9.x (sudah diatur otomatis via Maven atau manual) | [mysql.com/downloads/connector/j](https://dev.mysql.com/downloads/connector/j/) |

---

## 🗃️ Langkah 1 — Setup Database MySQL

1. Pastikan **MySQL Server** sudah berjalan.
2. Buka **MySQL Workbench**, **DBeaver**, **HeidiSQL**, atau **terminal MySQL**.
3. Jalankan skrip `sipangan_db.sql`:

```bash
# Via terminal MySQL:
mysql -u root -p < sipangan_db.sql

# Atau di tool GUI database seperti DBeaver/Workbench:
# Buka file sipangan_db.sql -> Execute All Queries (⚡)
```

4. Verifikasi database berhasil dibuat:
```sql
USE sipangan_db;
SHOW TABLES;       -- harus muncul: users, produk_pangan, stok
SELECT * FROM users;
```

---

## ⚙️ Langkah 2 — Konfigurasi Koneksi Database

Buka file `src/main/java/sipangan/db/DatabaseConnection.java` dan sesuaikan baris berikut dengan kredensial MySQL lokal Anda:

```java
private static final String DB_USER = "root"; // ← Ganti jika username MySQL berbeda
private static final String DB_PASS = "";      // ← Isi password MySQL Anda (kosongkan jika tidak ada)
```

> Jika MySQL Anda menggunakan port atau host berbeda, sesuaikan juga bagian `DB_URL` di file tersebut.

---

## 🚀 Langkah 3 — Kompilasi & Jalankan Aplikasi

Anda dapat mengompilasi dan menjalankan aplikasi SiPangan menggunakan salah satu dari dua opsi di bawah ini:

### 📦 Opsi A: Menggunakan Maven (Sangat Direkomendasikan & Otomatis)
Cara ini paling praktis karena Maven akan mengunduh dan mengatur semua dependency (MySQL Driver, PDFBox) secara otomatis tanpa konfigurasi manual.

1. **Kompilasi Proyek:**
   Buka terminal di folder proyek ini, lalu jalankan:
   ```bash
   mvn clean compile
   ```
2. **Jalankan Aplikasi:**
   Jalankan perintah berikut untuk menjalankan aplikasi CLI:
   ```bash
   mvn exec:java
   ```

---

### 🔨 Opsi B: Menggunakan Batch Script Manual (`compile.bat` & `run.bat`)
Jika Anda ingin menjalankan tanpa Maven (secara konvensional):

1. **Konfigurasi Path Jar Driver MySQL:**
   Buka file `compile.bat` dan `run.bat` menggunakan text editor, lalu sesuaikan nilai variabel `MYSQL_JAR` di baris atas sesuai lokasi file `.jar` MySQL Connector di komputer Anda:
   ```bat
   set MYSQL_JAR=C:\path\ke\mysql-connector-j-9.x.o.jar
   ```

2. **Kompilasi Proyek:**
   Double-click `compile.bat` atau jalankan di CMD/PowerShell:
   ```cmd
   compile.bat
   ```
   *Jika berhasil, folder `out/` akan dibuat berisi file `.class`.*

3. **Jalankan Aplikasi:**
   Double-click `run.bat` atau jalankan di CMD/PowerShell:
   ```cmd
   run.bat
   ```

---

## 🔐 Login Default (Sampel Data)

| Field | Nilai |
|---|---|
| **Username** | `admin` |
| **Password** | `admin123` |

---

## 🖥️ Fitur Aplikasi CLI

| Menu | Fitur |
|---|---|
| **Login** | Validasi akun ke database MySQL, enkapsulasi ketat password |
| **Dashboard** | Statistik total produk terdaftar, jumlah stok kritis, daftar warning stok kritis |
| **Manajemen Produk Pangan** | CRUD Lengkap (Tampilkan Semua, Tambah, Edit, Hapus Produk) |
| **Manajemen Stok Gudang** | CRUD Stok, Transaksi Kurangi Stok (auto-trigger alert), Cetak Laporan PDF |
| **Cetak Laporan PDF** | Membuat dokumen PDF resmi yang rapi menggunakan library Apache PDFBox, lengkap dengan statistik, pewarnaan baris otomatis (merah untuk stok kritis), dan membuka file PDF secara otomatis |

---

## 🎯 Konsep OOP yang Diimplementasikan

| Konsep | File | Implementasi |
|---|---|---|
| **Abstract Class** | `Person.java` | Kelas abstrak penampung data personal, tidak bisa di-`new` langsung. |
| **Inheritance (Pewarisan)** | `User.java` | `User extends Person` — mewarisi properti `nama`, `alamat`, dan `email`. |
| **Encapsulation** | Semua model | Atribut dibuat `private` dan diakses melalui getter/setter dengan validasi ketat. |
| **Polymorphism** | `User.java`, `Stok.java` | Override method `tampilkanInfo()` dari `Person` dan `kirimNotifikasi()` dari interface. |
| **Interface** | `Notifikasi.java` | Mendefinisikan kontrak method `kirimNotifikasi(String pesan)`. |
| **Association (HAS-A)** | `Stok.java` | Kelas `Stok` menyimpan referensi ke objek `ProdukPangan`. |
| **Event Trigger** | `Stok.java` | Fungsi `kurangiStok()` otomatis memicu `kirimNotifikasi()` ketika stok menyentuh/di bawah batas minimum. |
| **SRP (Single Responsibility)** | `LaporanPDFGenerator.java` | Memisahkan logika pembuatan file PDFBox secara mandiri dari logika alur CLI program. |

---

## 🐛 Troubleshooting Umum

| Error | Solusi |
|---|---|
| `Communications link failure` | MySQL Server belum berjalan atau port salah. Pastikan XAMPP/MySQL Server Anda aktif. |
| `Access denied for user 'root'` | Password database salah. Sesuaikan `DB_PASS` di `DatabaseConnection.java`. |
| `Unknown database 'sipangan_db'` | Database belum di-import. Jalankan skrip `sipangan_db.sql` di MySQL Anda terlebih dahulu. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Path `MYSQL_JAR` di `compile.bat`/`run.bat` salah atau belum diarahkan ke file `.jar` driver JDBC MySQL yang valid. |
| `error: package sipangan.model does not exist` | Kompilasi tidak dilakukan dari root folder `ketahanan-panggan/`. Pastikan terminal berada di root folder proyek saat kompilasi. |

---

*Dibuat untuk keperluan pembelajaran PBO (Pemrograman Berorientasi Objek)*
