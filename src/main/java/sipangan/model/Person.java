package sipangan.model;

// Class Person (Abstract)
public abstract class Person {

    // Atribut
    private String nama;
    private String alamat;
    private String email;

    // Konstruktor
    public Person(String nama, String alamat, String email) {
        setNama(nama);
        setAlamat(alamat);
        setEmail(email);
    }

    // Getter
    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getEmail() {
        return email;
    }

    // Setter
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty())
            throw new IllegalArgumentException("Nama tidak boleh kosong!");
        this.nama = nama.trim();
    }

    public void setAlamat(String alamat) {
        this.alamat = (alamat != null) ? alamat : "";
    }

    public void setEmail(String email) {
        if (email != null && !email.isEmpty() && !email.contains("@"))
            throw new IllegalArgumentException("Format email tidak valid! Harus mengandung '@'.");
        this.email = email;
    }

    // Method abstrak tampilkan info
    public abstract void tampilkanInfo();
}
