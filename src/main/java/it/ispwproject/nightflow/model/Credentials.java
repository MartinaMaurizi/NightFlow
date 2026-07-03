package it.ispwproject.nightflow.model;

import it.ispwproject.nightflow.enumerator.Role;

/**
 * Credentials — oggetto TEMPORANEO usato solo durante il login.
 * NON è una entity persistente — non ha id perché non viene salvata nel DB.
 * Contiene solo i dati minimi per verificare l'accesso:
 * email, password hashata, ruolo.
 * Viene creata in LoginDAO e consumata in LoginController.
 */
public class Credentials {

    private String email;
    private String password;
    private Role role;

    public Credentials(String email, String password, Role role) {
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public Role   getRole()     { return role; }
}