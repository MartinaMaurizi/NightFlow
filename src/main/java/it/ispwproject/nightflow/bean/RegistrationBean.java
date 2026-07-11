package it.ispwproject.nightflow.bean;

import it.ispwproject.nightflow.enumerator.Role;
import java.time.LocalDate;
import java.util.List;

public class RegistrationBean {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String confirmPassword;
    private String gender;
    private String country;
    private String city;

    private LocalDate dateOfBirth;
    private Role role;
    private List<String> localNames; // Per gli organizzatori

    public RegistrationBean() {
        // Justification: Default constructor required by JavaBean standard.
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

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

    public List<String> getLocalNames() { return localNames; }
    public void setLocalNames(List<String> localNames) { this.localNames = localNames; }
}