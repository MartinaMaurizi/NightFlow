package it.ispwproject.nightflow.model;

import it.ispwproject.nightflow.enumerator.Role;
import java.util.List;
import java.util.ArrayList;

public class Organizer extends User {

    private List<String> localNames; // Lista di locali

    public Organizer() {
        super();
        this.localNames = new ArrayList<>();
    }

    // Costruttore snello, esattamente come il Client!
    public Organizer(int id, String name, String surname, String email, String password) {
        super(id, name, surname, email, password, Role.ORGANIZER);
        this.localNames = new ArrayList<>();
    }

    public List<String> getLocalNames() { return localNames; }
    public void setLocalNames(List<String> localNames) { this.localNames = localNames; }

    // Metodo helper per aggiungere un locale alla volta
    public void addLocalName(String localName) {
        if (this.localNames == null) this.localNames = new ArrayList<>();
        this.localNames.add(localName);
    }
}