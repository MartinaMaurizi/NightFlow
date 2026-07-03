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

    // Se hai bisogno di aggiungere i nuovi campi anagrafici (nascita, città, ecc.)
    // che hai usato nel RegistrationController:
    public Client(int id, String name, String surname, String email, String password,
                  java.time.LocalDate dateOfBirth, String gender, String country, String city) {
        super(id, name, surname, email, password, Role.CLIENT, dateOfBirth, gender, country, city);
    }
}