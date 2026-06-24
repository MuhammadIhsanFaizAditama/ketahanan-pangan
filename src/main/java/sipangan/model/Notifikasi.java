package sipangan.model;

// =========================================================================
// FILE    : Notifikasi.java
// PACKAGE : sipangan.model
// KONSEP  : INTERFACE — mendefinisikan "kontrak" bagi kelas yang
//           mengimplementasikannya.
//
// Kelas apapun yang menulis "implements Notifikasi" WAJIB membuat
// implementasi method kirimNotifikasi(). Jika tidak → ERROR kompilasi.
//
// Interface TIDAK bisa diinstansiasi: new Notifikasi() → ERROR!
// Interface hanya berisi DEKLARASI method, bukan isinya.
// =========================================================================
public interface Notifikasi {

    /**
     * Kontrak: kelas yang implements Notifikasi WAJIB mengimplementasikan
     * method ini dengan logikanya sendiri.
     * Tidak ada kurung kurawal {} karena belum ada isi/badan method.
     *
     * @param pesan isi pesan notifikasi yang akan dikirim
     */
    void kirimNotifikasi(String pesan);
}
