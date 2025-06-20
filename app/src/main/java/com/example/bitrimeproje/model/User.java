package com.example.bitrimeproje.model;  // Model paketinde olmalı!

public class User {
    private String isim;
    private String email;
    private String telefon;

    // Boş Constructor (Firebase için gerekli)
    public User() {
    }

    // Parametreli Constructor
    public User(String isim, String email, String telefon) {
        this.isim = isim;
        this.email = email;
        this.telefon = telefon;
    }

    // Getter ve Setter Metodları
    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
}
