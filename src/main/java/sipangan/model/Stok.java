package sipangan.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Class Stok
public class Stok implements Notifikasi {

    // Atribut
    private int idStok;
    private int kuantitas;
    private int batasMinimum;
    private String statusKualitas;
    private String gudangWilayah;
    private ProdukPangan produk;

    private static final Set<String> STATUS_VALID = new HashSet<>(Arrays.asList("LAYAK", "PERLU_CEPAT", "TIDAK_LAYAK"));

    // Konstruktor
    public Stok(int idStok, ProdukPangan produk, int kuantitas,
            int batasMinimum, String statusKualitas, String gudangWilayah) {
        this.idStok = idStok;
        this.produk = produk;
        this.kuantitas = kuantitas;
        this.batasMinimum = batasMinimum;
        setStatusKualitas(statusKualitas);
        this.gudangWilayah = gudangWilayah;
    }

    // Getter
    public int getIdStok() {
        return idStok;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public int getBatasMinimum() {
        return batasMinimum;
    }

    public String getStatusKualitas() {
        return statusKualitas;
    }

    public String getGudangWilayah() {
        return gudangWilayah;
    }

    public ProdukPangan getProduk() {
        return produk;
    }

    // Setter
    public void setKuantitas(int qty) {
        this.kuantitas = qty;
    }

    public void setBatasMinimum(int min) {
        this.batasMinimum = min;
    }

    public void setGudangWilayah(String gudang) {
        this.gudangWilayah = gudang;
    }

    public void setStatusKualitas(String status) {
        if (status == null || !STATUS_VALID.contains(status.toUpperCase()))
            throw new IllegalArgumentException(
                    "Status tidak valid! Harus: LAYAK, PERLU_CEPAT, atau TIDAK_LAYAK. " +
                            "Diterima: '" + status + "'");
        this.statusKualitas = status.toUpperCase();
    }

    // Implementasi interface Notifikasi
    @Override
    public void kirimNotifikasi(String pesan) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ALERT SISTEM — STOK KRITIS TERDETEKSI!          ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  " + pesan);
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    // Kurangi stok
    public String kurangiStok(int jumlah) {
        this.kuantitas -= jumlah;

        if (this.kuantitas <= this.batasMinimum) {
            String pesan = String.format(
                    "Stok '%s' di %s tersisa %d unit! (Batas Minimum: %d unit)",
                    produk.getNamaProduk(), gudangWilayah, this.kuantitas, this.batasMinimum);
            kirimNotifikasi(pesan);
            return pesan;
        }

        return null;
    }

    // Cek batas minimum
    public boolean isBawahMinimum() {
        return kuantitas <= batasMinimum;
    }
}
