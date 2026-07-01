package sipangan.util;

import sipangan.db.SiPanganDAO;
import sipangan.model.Stok;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// =========================================================================
// FILE    : LaporanPDFGenerator.java
// PACKAGE : sipangan.util
// KONSEP  : KELAS UTILITAS — berisi method 'static' yang bisa dipanggil
//           tanpa membuat objek: LaporanPDFGenerator.generate(data);
//
// Kelas ini dipisahkan dari SiPanganApp agar setiap kelas punya
// SATU tanggung jawab saja (Single Responsibility Principle):
//   - SiPanganApp → mengurus interaksi pengguna (CLI menu)
//   - LaporanPDFGenerator → mengurus pembuatan file PDF
//
// Library yang digunakan: Apache PDFBox (pembuatan dokumen PDF)
// =========================================================================
public class LaporanPDFGenerator {

    /**
     * Membuat file laporan PDF berisi daftar stok dan statistik.
     * Method ini bersifat 'static' → bisa dipanggil langsung tanpa objek.
     *
     * @param dataStok daftar objek Stok yang akan dicetak ke PDF
     */
    public static void generate(List<Stok> dataStok) {
        // Buat nama file unik menggunakan timestamp agar tidak menimpa file lama
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String namaFile  = "laporan_stok_" + timestamp + ".pdf";
        File outputFile  = new File(namaFile);

        // try-with-resources: dokumen PDF otomatis ditutup setelah blok ini selesai
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            // ── Pengaturan posisi dan ukuran ──
            float margin     = 50f;
            float pageWidth  = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float tableWidth = pageWidth - 2 * margin;

            // Lebar kolom tabel (dalam satuan point/piksel PDF)
            float[] colWidths = { 28f, 110f, 48f, 48f, 72f, 130f, 55f };

            // ── Warna-warna yang digunakan (format RGB 0.0 - 1.0) ──
            float[] colorAccent  = { 0.15f, 0.23f, 0.37f };  // Biru tua (header)
            float[] colorKritis  = { 1.00f, 0.89f, 0.89f };  // Merah muda (baris kritis)
            float[] colorWhite   = { 1f, 1f, 1f };
            float[] colorAlt     = { 0.97f, 0.98f, 1.00f };  // Biru sangat muda (baris genap)
            float[] colorText    = { 0.12f, 0.18f, 0.24f };  // Hitam lembut (teks biasa)
            float[] colorMuted   = { 0.39f, 0.45f, 0.52f };  // Abu (footer)
            float[] colorRed     = { 0.86f, 0.15f, 0.15f };  // Merah (status KRITIS)
            float[] colorGreen   = { 0.02f, 0.59f, 0.42f };  // Hijau (status AMAN)

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = pageHeight - margin;

                // ── BAGIAN 1: Header Judul ──
                tulisHeader(cs, margin, y, tableWidth, colorAccent);
                y -= 80;

                // ── BAGIAN 2: Kotak Statistik ──
                tulisStatistik(cs, margin, y, tableWidth, dataStok.size());
                y -= 60;

                // ── BAGIAN 3: Tabel Data Stok ──
                y = tulisTabelStok(cs, margin, y, tableWidth, colWidths,
                        dataStok, colorAccent, colorKritis, colorWhite,
                        colorAlt, colorText, colorRed, colorGreen);

                // ── BAGIAN 4: Footer ──
                tulisFooter(cs, margin, tableWidth, colorAccent, colorMuted);
            }

            // Simpan file PDF ke disk
            doc.save(outputFile);
            System.out.println("✅ PDF Berhasil Dibuat!");
            System.out.println("Laporan disimpan di: " + outputFile.getAbsolutePath());

            // Coba buka PDF secara otomatis di aplikasi default OS
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                System.out.println("Membuka file PDF...");
                Desktop.getDesktop().open(outputFile);
            }

        } catch (IOException ex) {
            System.out.println("❌ Terjadi kesalahan saat membuat PDF: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // =====================================================================
    // METHOD HELPER PRIVATE — memecah proses pembuatan PDF menjadi
    // bagian-bagian kecil yang mudah dibaca dan dipahami.
    // =====================================================================

    /** Menulis header judul laporan (kotak biru tua di bagian atas). */
    private static void tulisHeader(PDPageContentStream cs, float margin,
            float y, float tableWidth, float[] colorAccent) throws IOException {

        // Background kotak header
        cs.setNonStrokingColor(colorAccent[0], colorAccent[1], colorAccent[2]);
        cs.addRect(margin, y - 70, tableWidth, 70);
        cs.fill();

        // Judul utama (putih)
        cs.setNonStrokingColor(1f, 1f, 1f);
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
        cs.newLineAtOffset(margin + 12, y - 28);
        cs.showText("LAPORAN STOK PANGAN NASIONAL");
        cs.endText();

        // Sub-judul
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        cs.newLineAtOffset(margin + 12, y - 44);
        cs.showText("SiPangan — Sistem Informasi & Distribusi Stok Pangan");
        cs.endText();

        // Tanggal cetak
        String tglCetak = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss").format(new Date());
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 9);
        cs.newLineAtOffset(margin + 12, y - 58);
        cs.showText("Dicetak pada: " + tglCetak);
        cs.endText();
    }

    /** Menulis 3 kotak statistik ringkasan (Total Produk, Total Stok, Stok Kritis). */
    private static void tulisStatistik(PDPageContentStream cs, float margin,
            float y, float tableWidth, int totalStok) throws IOException {

        int totalProduk = SiPanganDAO.countProduk();
        int stokKritis  = SiPanganDAO.countStokKritis();

        float boxW = (tableWidth - 20) / 3f;
        String[][] stats = {
            { "Total Produk", String.valueOf(totalProduk) },
            { "Total Stok",   String.valueOf(totalStok)   },
            { "Stok Kritis",  String.valueOf(stokKritis)  }
        };
        // Warna tiap kotak: biru, hijau, merah
        float[] statColors = {
            0.22f, 0.38f, 0.55f,
            0.02f, 0.59f, 0.42f,
            0.86f, 0.15f, 0.15f
        };

        for (int i = 0; i < 3; i++) {
            float bx = margin + i * (boxW + 10);

            // Background kotak
            cs.setNonStrokingColor(statColors[i * 3], statColors[i * 3 + 1], statColors[i * 3 + 2]);
            cs.addRect(bx, y - 45, boxW, 45);
            cs.fill();

            // Label (teks kecil)
            cs.setNonStrokingColor(1f, 1f, 1f);
            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
            cs.newLineAtOffset(bx + 8, y - 15);
            cs.showText(stats[i][0]);
            cs.endText();

            // Angka (teks besar)
            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
            cs.newLineAtOffset(bx + 8, y - 36);
            cs.showText(stats[i][1]);
            cs.endText();
        }
    }

    /** Menulis tabel data stok lengkap dengan header dan baris data. */
    private static float tulisTabelStok(PDPageContentStream cs, float margin,
            float y, float tableWidth, float[] colWidths, List<Stok> dataStok,
            float[] colorAccent, float[] colorKritis, float[] colorWhite,
            float[] colorAlt, float[] colorText, float[] colorRed,
            float[] colorGreen) throws IOException {

        String[] headers = { "ID", "Nama Produk", "Qty", "Min", "Status", "Gudang Wilayah", "Alert" };
        float rowH = 20f;

        // ── Header tabel ──
        cs.setNonStrokingColor(colorAccent[0], colorAccent[1], colorAccent[2]);
        cs.addRect(margin, y - rowH, tableWidth, rowH);
        cs.fill();

        cs.setNonStrokingColor(1f, 1f, 1f);
        float xCol = margin + 4;
        for (int c = 0; c < headers.length; c++) {
            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 8);
            cs.newLineAtOffset(xCol, y - rowH + 6);
            cs.showText(headers[c]);
            cs.endText();
            xCol += colWidths[c];
        }
        y -= rowH;

        // ── Baris data ──
        for (int idx = 0; idx < dataStok.size(); idx++) {
            Stok s       = dataStok.get(idx);
            boolean kritis = s.isBawahMinimum();
            boolean altRow = (idx % 2 == 1) && !kritis;

            // Warna latar baris: merah muda jika kritis, putih/biru muda bergantian
            if (kritis)       cs.setNonStrokingColor(colorKritis[0], colorKritis[1], colorKritis[2]);
            else if (altRow)  cs.setNonStrokingColor(colorAlt[0], colorAlt[1], colorAlt[2]);
            else              cs.setNonStrokingColor(colorWhite[0], colorWhite[1], colorWhite[2]);
            cs.addRect(margin, y - rowH, tableWidth, rowH);
            cs.fill();

            // Garis pemisah baris
            cs.setStrokingColor(0.85f, 0.87f, 0.90f);
            cs.setLineWidth(0.4f);
            cs.moveTo(margin, y - rowH);
            cs.lineTo(margin + tableWidth, y - rowH);
            cs.stroke();

            // Isi data baris
            String[] vals = {
                String.valueOf(s.getIdStok()),
                truncate(s.getProduk().getNamaProduk(), 18),
                String.valueOf(s.getKuantitas()),
                String.valueOf(s.getBatasMinimum()),
                s.getStatusKualitas(),
                truncate(s.getGudangWilayah(), 20),
                kritis ? "KRITIS" : "AMAN"
            };

            xCol = margin + 4;
            for (int c = 0; c < vals.length; c++) {
                // Kolom Alert diberi warna merah/hijau sesuai status
                if (c == 6) {
                    cs.setNonStrokingColor(
                        kritis ? colorRed[0] : colorGreen[0],
                        kritis ? colorRed[1] : colorGreen[1],
                        kritis ? colorRed[2] : colorGreen[2]);
                } else {
                    cs.setNonStrokingColor(colorText[0], colorText[1], colorText[2]);
                }
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
                cs.newLineAtOffset(xCol, y - rowH + 6);
                cs.showText(vals[c]);
                cs.endText();
                xCol += colWidths[c];
            }
            y -= rowH;

            // Hentikan jika ruang halaman hampir habis
            if (y < margin + 60 && idx < dataStok.size() - 1) {
                break;
            }
        }

        return y;
    }

    /** Menulis footer di bagian bawah halaman PDF. */
    private static void tulisFooter(PDPageContentStream cs, float margin,
            float tableWidth, float[] colorAccent, float[] colorMuted) throws IOException {

        float y = margin + 40;

        // Garis pemisah footer
        cs.setStrokingColor(colorAccent[0], colorAccent[1], colorAccent[2]);
        cs.setLineWidth(1f);
        cs.moveTo(margin, y);
        cs.lineTo(margin + tableWidth, y);
        cs.stroke();

        // Teks footer kiri
        cs.setNonStrokingColor(colorMuted[0], colorMuted[1], colorMuted[2]);
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 8);
        cs.newLineAtOffset(margin, y - 14);
        cs.showText("Dokumen digenerate otomatis oleh SiPangan — Sistem Informasi Stok Pangan Nasional.");
        cs.endText();

        // Teks footer kanan (nomor halaman)
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 8);
        cs.newLineAtOffset(margin + tableWidth - 80, y - 14);
        cs.showText("Halaman 1 dari 1");
        cs.endText();
    }

    /**
     * Helper: potong string yang terlalu panjang agar muat di kolom tabel PDF.
     * Jika panjang string melebihi 'max', sisanya diganti dengan '…'.
     */
    private static String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max - 1) + "…" : (s != null ? s : "");
    }
}
