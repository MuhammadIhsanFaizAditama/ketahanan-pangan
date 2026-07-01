-- =====================================================================
-- FILE       : sipangan_db.sql
-- DESKRIPSI  : Skrip DDL (Data Definition Language) untuk membuat
--              skema database SiPangan di MySQL.
--
-- CARA PAKAI :
--   1. Buka MySQL Workbench atau terminal MySQL.
--   2. Jalankan file ini: SOURCE /path/ke/sipangan_db.sql;
--      atau copy-paste isi file ini ke query editor, lalu Execute.
-- =====================================================================

-- Buat database jika belum ada, lalu gunakan database tersebut.
CREATE DATABASE IF NOT EXISTS sipangan_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sipangan_db;

-- ─────────────────────────────────────────────────────────────────────
-- TABEL: users
-- Menyimpan data pengguna aplikasi (admin, operator gudang, dll.)
-- Sesuai dengan class User (turunan dari abstract class Person) di Java.
-- ─────────────────────────────────────────────────────────────────────
DROP TABLE IF EXISTS stok;
DROP TABLE IF EXISTS produk_pangan;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    nama     VARCHAR(100) NOT NULL,
    alamat   VARCHAR(255) DEFAULT '',
    email    VARCHAR(100) DEFAULT '',
    username VARCHAR(50)  NOT NULL UNIQUE,  -- UNIQUE: tidak boleh ada username yang sama
    password VARCHAR(100) NOT NULL          -- Di produksi nyata, ini harus di-hash (BCrypt, dll.)
);

-- ─────────────────────────────────────────────────────────────────────
-- TABEL: produk_pangan
-- Menyimpan master data produk pangan nasional.
-- Sesuai dengan class ProdukPangan di Java.
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE produk_pangan (
    id_produk          INT          AUTO_INCREMENT PRIMARY KEY,
    nama_produk        VARCHAR(150) NOT NULL,
    kategori           VARCHAR(80)  DEFAULT 'Umum',
    tgl_kedaluwarsa    DATE         NULL   -- NULL berarti tanggal belum diketahui / tidak berlaku
);

-- ─────────────────────────────────────────────────────────────────────
-- TABEL: stok
-- Menyimpan data stok per gudang per produk.
-- Sesuai dengan class Stok (implements Notifikasi, has-a ProdukPangan) di Java.
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE stok (
    id_stok         INT         AUTO_INCREMENT PRIMARY KEY,
    id_produk       INT         NOT NULL,
    kuantitas       INT         NOT NULL DEFAULT 0,
    batas_minimum   INT         NOT NULL DEFAULT 10,
    status_kualitas VARCHAR(20) NOT NULL DEFAULT 'LAYAK',
                    -- Hanya boleh: 'LAYAK', 'PERLU_CEPAT', 'TIDAK_LAYAK'
                    -- (Validasi lebih lanjut dilakukan di layer Java/OOP)
    gudang_wilayah  VARCHAR(150) DEFAULT 'Gudang Pusat',

    -- Foreign Key: id_produk di tabel stok WAJIB ada di tabel produk_pangan.
    -- ON DELETE RESTRICT: tidak bisa hapus produk jika masih ada stoknya.
    CONSTRAINT fk_stok_produk
        FOREIGN KEY (id_produk) REFERENCES produk_pangan(id_produk)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- =====================================================================
-- DATA AWAL (SEEDING) - Data sampel untuk langsung bisa dicoba
-- =====================================================================

-- Akun pengguna default (username: admin, password: admin123)
INSERT INTO users (nama, alamat, email, username, password) VALUES
('Administrator SiPangan', 'Kementan RI, Jakarta Pusat', 'admin@sipangan.go.id', 'admin', 'admin123'),
('Operator Gudang Jawa',   'Surabaya, Jawa Timur',       'op.jawa@sipangan.go.id', 'operator', 'op1234');

-- Data produk pangan
INSERT INTO produk_pangan (nama_produk, kategori, tgl_kedaluwarsa) VALUES
('Beras Medium IR-64',  'Biji-bijian',  '2026-06-30'),
('Jagung Pipil Kuning', 'Biji-bijian',  '2026-04-15'),
('Minyak Goreng Curah', 'Minyak',       '2025-12-31'),
('Gula Pasir SHS',      'Pemanis',      '2026-08-20'),
('Tepung Terigu Cakra', 'Tepung',       '2025-11-30'),
('Kedelai Lokal',       'Kacang-kacangan', '2026-02-28'),
('Garam Beriodium',     'Bumbu Dasar',  '2027-01-01');

-- Data stok gudang
-- Beberapa sengaja dibuat kritis (kuantitas <= batas_minimum) untuk demo alert
INSERT INTO stok (id_produk, kuantitas, batas_minimum, status_kualitas, gudang_wilayah) VALUES
(1, 1500, 200,  'LAYAK',       'Gudang Bulog Jakarta Utara'),   -- Aman
(2, 85,   100,  'PERLU_CEPAT', 'Gudang Bulog Surabaya'),        -- ⚠ KRITIS
(3, 320,  50,   'LAYAK',       'Gudang Bulog Bandung'),          -- Aman
(4, 18,   30,   'LAYAK',       'Gudang Bulog Medan'),            -- ⚠ KRITIS
(5, 450,  80,   'LAYAK',       'Gudang Bulog Makassar'),         -- Aman
(6, 5,    25,   'TIDAK_LAYAK', 'Gudang Bulog Semarang'),         -- ⚠ KRITIS + Tidak Layak
(7, 900,  100,  'LAYAK',       'Gudang Bulog Palembang');        -- Aman
