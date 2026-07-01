package sipangan.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// =========================================================================
// FILE    : Stok.java
// PACKAGE : sipangan.model
// KONSEP  : ASOSIASI (HAS-A) + IMPLEMENTASI INTERFACE
//
// 1. ASOSIASI: Stok "memiliki" sebuah ProdukPangan.
//    Ini BUKAN inheritance. Stok tidak "adalah" ProdukPangan.
//    Stok punya REFERENSI ke objek ProdukPangan: private ProdukPangan produk;
//
// 2. INTERFACE: Stok implements Notifikasi → WAJIB buat kirimNotifikasi().
//
// 3. EVENT TRIGGER: method kurangiStok() OTOMATIS memanggil kirimNotifikasi()
//    saat stok menyentuh/melewati batasMinimum (Automated Stock Alert System).
// =========================================================================
public class Stok implements Notifikasi {

    private int idStok;
    private int kuantitas;
    private int batasMinimum;
    private String statusKualitas;
    private String gudangWilayah;

    // [ASOSIASI — HAS-A] Stok "punya" referensi ke objek ProdukPangan.
    // Artinya: setiap Stok tahu ia menyimpan produk apa.
    private ProdukPangan produk;

    // Kumpulan nilai yang VALID untuk statusKualitas.
    // 'static final' = konstan, milik kelas (bukan per-objek), tidak bisa diubah.
    private static final Set<String> STATUS_VALID = new HashSet<>(Arrays.asList("LAYAK", "PERLU_CEPAT", "TIDAK_LAYAK"));

    /**
     * Constructor Stok.
     */
    public Stok(int idStok, ProdukPangan produk, int kuantitas,
            int batasMinimum, String statusKualitas, String gudangWilayah) {
        this.idStok = idStok;
        this.produk = produk;
        this.kuantitas = kuantitas;
        this.batasMinimum = batasMinimum;
        setStatusKualitas(statusKualitas); // Pakai setter agar validasi berjalan
        this.gudangWilayah = gudangWilayah;
    }

    // ── Getter ──
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
    } // ← Akses objek ProdukPangan

    // ── Setter biasa ──
    public void setKuantitas(int qty) {
        this.kuantitas = qty;
    }

    public void setBatasMinimum(int min) {
        this.batasMinimum = min;
    }

    public void setGudangWilayah(String gudang) {
        this.gudangWilayah = gudang;
    }

    /**
     * [ENKAPSULASI + VALIDASI KETAT]
     * Setter ini MENOLAK nilai yang tidak ada dalam STATUS_VALID.
     * Sehingga tidak mungkin ada status sembarangan dalam sistem.
     */
    public void setStatusKualitas(String status) {
        if (status == null || !STATUS_VALID.contains(status.toUpperCase()))
            throw new IllegalArgumentException(
                    "Status tidak valid! Harus: LAYAK, PERLU_CEPAT, atau TIDAK_LAYAK. " +
                            "Diterima: '" + status + "'");
        this.statusKualitas = status.toUpperCase();
    }

    /**
     * [POLYMORPHISM — IMPLEMENTASI INTERFACE Notifikasi]
     * Pemenuhan "kontrak" dari interface Notifikasi.
     * Dalam aplikasi nyata: kirim email/SMS. Sekarang: cetak ke konsol.
     */
    @Override
    public void kirimNotifikasi(String pesan) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ALERT SISTEM — STOK KRITIS TERDETEKSI!          ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  " + pesan);
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * [AUTOMATED STOCK ALERT SYSTEM — EVENT TRIGGER]
     * Mengurangi kuantitas stok DAN secara OTOMATIS memicu kirimNotifikasi()
     * jika kuantitas menyentuh atau melewati batasMinimum.
     *
     * Validasi (jumlah <= 0, jumlah > kuantitas) dilakukan di layer CLI sebelum
     * method ini dipanggil.
     *
     * @param jumlah jumlah unit yang dikurangi (diasumsikan sudah valid)
     * @return String pesan alert jika kritis, null jika stok masih aman
     */
    public String kurangiStok(int jumlah) {
        this.kuantitas -= jumlah; // Kurangi stok di memori

        // Cek: apakah stok sekarang menyentuh/melewati batas minimum?
        if (this.kuantitas <= this.batasMinimum) {
            String pesan = String.format(
                    "Stok '%s' di %s tersisa %d unit! (Batas Minimum: %d unit)",
                    produk.getNamaProduk(), gudangWilayah, this.kuantitas, this.batasMinimum);
            kirimNotifikasi(pesan); // Otomatis trigger! ← inilah Event Trigger-nya
            return pesan; // Kembalikan pesan agar bisa ditampilkan di CLI
        }

        return null; // null = stok masih aman, tidak ada alert
    }

    /**
     * Helper: cek apakah stok sedang kritis (untuk penanda status di tampilan CLI).
     */
    public boolean isBawahMinimum() {
        return kuantitas <= batasMinimum;
    }
}
