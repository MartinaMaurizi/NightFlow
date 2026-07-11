package it.ispwproject.nightflow.model;


import it.ispwproject.nightflow.enumerator.Role;
import java.time.LocalDate;

public abstract class User {

    private int id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private Role role;
    private LocalDate dateOfBirth;
    private String gender;
    private String country;
    private String city;

    protected User() {}

    // Costruttore base (per utenti semplici)
    protected User(int id, String name, String surname, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Costruttore completo (per la registrazione con tutti i dati anagrafici)
    protected User(int id, String name, String surname, String email, String password,
                Role role, LocalDate dateOfBirth, String gender, String country, String city) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.country = country;
        this.city = city;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean hasRole(Role role) {
        return this.role == role;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    // Utility per comodità nel Controller
    public String getFullName() { return name + " " + surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}