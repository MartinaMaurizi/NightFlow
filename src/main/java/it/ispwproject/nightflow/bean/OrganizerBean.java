package it.ispwproject.nightflow.bean;

import java.util.List;

public class OrganizerBean {
    private int id;
    private String name;
    private String surname;
    private String email;
    private List<String> localNames; // Lista di locali gestiti

    public OrganizerBean() {}

    public OrganizerBean(int id, String name, String surname, String email, List<String> localNames) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.localNames = localNames;
    }

    public String getFullName() { return name + " " + surname; }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getLocalNames() { return localNames; }
    public void setLocalNames(List<String> localNames) { this.localNames = localNames; }
}