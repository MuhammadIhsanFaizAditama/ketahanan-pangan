package sipangan.model;

// Class User
public class User extends Person {

    // Atribut
    private String username;
    private String password;

    // Konstruktor
    public User(String nama, String alamat, String email,
            String username, String password) {
        super(nama, alamat, email);
        setUsername(username);
        setPassword(password);
    }

    // Getter dan Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username tidak boleh kosong!");
        this.username = username.trim().toLowerCase();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 4)
            throw new IllegalArgumentException("Password minimal 4 karakter!");
        this.password = password;
    }

    // Verifikasi password
    public boolean verifyPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    // Override tampilkanInfo
    @Override
    public void tampilkanInfo() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║         INFO USER - SiPangan                ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.printf ("║  Nama     : %-32s ║%n", getNama());
        System.out.printf ("║  Alamat   : %-32s ║%n", getAlamat());
        System.out.printf ("║  Email    : %-32s ║%n", getEmail());
        System.out.printf ("║  Username : %-32s ║%n", username);
        System.out.println("║  Password : [RAHASIA - Tidak ditampilkan]    ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }
}
