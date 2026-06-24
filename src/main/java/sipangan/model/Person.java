package sipangan.model;

// =========================================================================
// FILE    : Person.java
// PACKAGE : sipangan.model
// KONSEP  : ABSTRACT CLASS — kelas "setengah jadi" yang tidak bisa
//           diinstansiasi langsung: new Person() → ERROR KOMPILASI!
//
// Fungsinya sebagai "cetak biru" (blueprint) untuk kelas turunan seperti User.
// Semua entitas manusia dalam sistem (User, dsb) pasti punya nama, alamat, email.
// Daripada tulis ulang, kita taruh di sini dan biarkan turunan mewarisinya.
// =========================================================================
public abstract class Person {

    // [ENKAPSULASI] Atribut 'private' = HANYA bisa diakses dari dalam kelas Person.
    // Kelas lain, bahkan kelas turunan seperti User, TIDAK bisa: user.nama → ERROR.
    // Akses dari luar hanya melalui getter/setter.
    private String nama;
    private String alamat;
    private String email;

    /**
     * Constructor: dijalankan otomatis saat objek turunan (mis: User) dibuat.
     * Contoh: new User("Budi", "Jakarta", ...) → Person(nama, alamat, email) ini
     * dipanggil lebih dulu via super() di constructor User.
     */
    public Person(String nama, String alamat, String email) {
        setNama(nama); // Gunakan setter agar validasi ikut berjalan
        setAlamat(alamat);
        setEmail(email);
    }

    // ── GETTER: satu-satunya cara membaca nilai atribut private dari luar kelas ──
    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getEmail() {
        return email;
    }

    // ── SETTER dengan VALIDASI ──
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty())
            throw new IllegalArgumentException("Nama tidak boleh kosong!");
        this.nama = nama.trim();
    }

    public void setAlamat(String alamat) {
        this.alamat = (alamat != null) ? alamat : "";
    }

    public void setEmail(String email) {
        // Validasi format email sederhana: harus mengandung '@'
        if (email != null && !email.isEmpty() && !email.contains("@"))
            throw new IllegalArgumentException("Format email tidak valid! Harus mengandung '@'.");
        this.email = email;
    }

    /**
     * [ABSTRACT METHOD] Tidak ada implementasi di sini — hanya deklarasi.
     * Setiap kelas yang extends Person WAJIB membuat isi dari method ini.
     * Inilah yang memaksa setiap subclass punya cara "tampil" sendiri.
     */
    public abstract void tampilkanInfo();
}
