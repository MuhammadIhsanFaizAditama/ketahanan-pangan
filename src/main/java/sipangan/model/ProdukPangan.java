package sipangan.model;

// =========================================================================
// FILE    : ProdukPangan.java
// PACKAGE : sipangan.model
// KONSEP  : CLASS biasa dengan enkapsulasi standar (getter & setter).
//           Merepresentasikan satu entitas produk pangan dalam sistem.
// =========================================================================
public class ProdukPangan {

    // Atribut private — enkapsulasi standar
    private int idProduk;
    private String namaProduk;
    private String kategori;
    private String tanggalKedaluwarsa; // Format: YYYY-MM-DD (disimpan sebagai String)

    /**
     * Constructor: wajib mengisi semua atribut saat membuat objek baru.
     */
    public ProdukPangan(int idProduk, String namaProduk,
            String kategori, String tanggalKedaluwarsa) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.kategori = kategori;
        this.tanggalKedaluwarsa = tanggalKedaluwarsa;
    }

    // ── Getter (Baca) ──
    public int getIdProduk() {
        return idProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public String getKategori() {
        return kategori;
    }

    public String getTanggalKedaluwarsa() {
        return tanggalKedaluwarsa;
    }

    // ── Setter (Tulis) ──
    public void setIdProduk(int id) {
        this.idProduk = id;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public void setTanggalKedaluwarsa(String tgl) {
        this.tanggalKedaluwarsa = tgl;
    }

    /**
     * toString() di-override agar objek menampilkan teks yang bermakna
     * saat dicetak ke konsol (CLI). Tanpa ini, System.out.println() hanya
     * menampilkan: "ProdukPangan@7a3f4f72" (tidak berguna).
     */
    @Override
    public String toString() {
        return namaProduk + "  [" + kategori + "]";
    }
}
