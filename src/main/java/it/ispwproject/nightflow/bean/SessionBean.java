package it.ispwproject.nightflow.bean;

import it.ispwproject.nightflow.enumerator.Role;

/**
 * Bean leggero che rappresenta la sessione autenticata.
 * Contiene solo email e role — dati minimi per l'autorizzazione.
 * Per i dati completi dell'utente usa SessionManager.getLoggedUser().
 **/
public class SessionBean {

    private final String email;
    private final Role role;

    public SessionBean(String email, Role role) {
        this.email = email;
        this.role  = role;
    }

    public String getEmail() { return email; }
    public Role getRole()    {
        return role; }
}

