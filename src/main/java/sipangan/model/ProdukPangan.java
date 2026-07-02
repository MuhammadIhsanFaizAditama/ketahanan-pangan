package sipangan.model;

// Class Produk Pangan
public class ProdukPangan {

    // Atribut
    private int idProduk;
    private String namaProduk;
    private String kategori;
    private String tanggalKedaluwarsa;

    // Konstruktor
    public ProdukPangan(int idProduk, String namaProduk,
            String kategori, String tanggalKedaluwarsa) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.kategori = kategori;
        this.tanggalKedaluwarsa = tanggalKedaluwarsa;
    }

    // Getter
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

    // Setter
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

    // Override toString
    @Override
    public String toString() {
        return namaProduk + "  [" + kategori + "]";
    }
}
