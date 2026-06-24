package sipangan.model;

// =========================================================================
// FILE    : User.java
// PACKAGE : sipangan.model
// KONSEP  : INHERITANCE (Pewarisan) — User mewarisi Person.
//           ENKAPSULASI KETAT — password punya setter tapi TIDAK ada getter.
//           POLYMORPHISM — override method tampilkanInfo() dari Person.
//
// Prinsip: "User IS-A Person" → User adalah salah satu jenis Person.
// =========================================================================
public class User extends Person {

    // Atribut tambahan khusus User (tidak ada di Person)
    private String username;

    // [ENKAPSULASI KETAT] password:
    // ✅ Setter ADA → boleh mengubah password
    // ❌ Getter TIDAK ADA → nilai password tidak pernah bisa dibaca dari luar
    // Ini mencegah "credential leak" (kebocoran kata sandi).
    private String password;

    /**
     * Constructor User: panggil super() untuk inisialisasi atribut Person terlebih
     * dahulu, baru kemudian isi atribut tambahan milik User.
     * 'super(...)' WAJIB menjadi baris pertama di constructor kelas turunan.
     */
    public User(String nama, String alamat, String email,
            String username, String password) {
        super(nama, alamat, email); // Delegasi ke constructor Person
        setUsername(username);
        setPassword(password);
    }

    // Getter untuk username (aman untuk ditampilkan di GUI)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username tidak boleh kosong!");
        this.username = username.trim().toLowerCase(); // Simpan selalu dalam huruf kecil
    }

    // Setter password: boleh digunakan, tapi tidak ada getter-nya.
    public void setPassword(String password) {
        if (password == null || password.length() < 4)
            throw new IllegalArgumentException("Password minimal 4 karakter!");
        this.password = password;
    }

    /**
     * Cara aman memverifikasi password dari luar kelas.
     * Kita bisa MEMERIKSA kebenarannya, tapi tidak bisa MEMBACA nilainya.
     */
    public boolean verifyPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    /**
     * [POLYMORPHISM — METHOD OVERRIDING]
     * Menimpa (override) method abstrak dari kelas induk Person.
     * 
     * @Override memastikan compiler memeriksa bahwa ini benar-benar override.
     *
     *           Polimorfisme: method yang sama (tampilkanInfo()), tapi
     *           isi/perilakunya
     *           berbeda tergantung kelas yang menjalankannya.
     */
    @Override
    public void tampilkanInfo() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║    INFO USER — SiPangan               ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.printf("║  Nama     : %-25s ║%n", getNama());
        System.out.printf("║  Alamat   : %-25s ║%n", getAlamat());
        System.out.printf("║  Email    : %-25s ║%n", getEmail());
        System.out.printf("║  Username : %-25s ║%n", username);
        System.out.println("║  Password : [RAHASIA - Tidak ditampilkan] ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }
}
