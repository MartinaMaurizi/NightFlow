package it.ispwproject.nightflow.model;

import it.ispwproject.nightflow.enumerator.Role;

import java.time.LocalDate;

/**
 * Rappresenta un utente di tipo Cliente (chi prenota gli eventi).
 */
public class Client extends User {

    // 🌟 1. Aggiungiamo la variabile
    private LocalDate dateOfBirth;

    public Client() {
        super();
    }

    // Costruttore standard
    public Client(int id, String name, String surname, String email, String password) {
        super(id, name, surname, email, password, Role.CLIENT);
    }

    // 🌟 2. Aggiungiamo Getter e Setter
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}