package it.ispwproject.nightflow.model;

import it.ispwproject.nightflow.enumerator.Role;

/**
 * Rappresenta un utente di tipo Cliente (chi prenota gli eventi).
 */
public class Client extends User {

    public Client() {
        super();
    }

    // Costruttore standard
    public Client(int id, String name, String surname, String email, String password) {
        super(id, name, surname, email, password, Role.CLIENT);
    }
}