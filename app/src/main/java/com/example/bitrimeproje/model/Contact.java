package com.example.bitrimeproje.model;

public class Contact {
    private String id;
    private String name;
    private String surname;
    private String phone;
    private String email;

    // Boş Constructor (Firebase için gerekli!)
    public Contact() { }

    public Contact(String id, String name, String surname, String phone, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
    }

    // Getter ve Setter Metodları
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}
