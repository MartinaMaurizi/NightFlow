package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.exception.RegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month; // Aggiunto import

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class: RegistrationControllerTest
 * Author: Martina Maurizi
 * Description: Verifica che il sistema impedisca la registrazione
 * di due account con la stessa email. Dopo una prima
 * registrazione avvenuta con successo, un secondo
 * tentativo con la stessa email deve lanciare una
 * RegistrationException.
 * ------------------------------------------------------------
 */
class RegistrationControllerTest {

    private RegistrationController registrationController;

    @BeforeEach
    void setup() {
        // Ripuliamo i dati in memoria per avere un ambiente di test pulito
        DemoDataStore.getInstance().getUsers().clear();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
        registrationController = new RegistrationController();
    }

    @Test
    void testRegistrazioneConEmailDuplicata() throws RegistrationException {
        // Prima registrazione — deve andare a buon fine
        RegistrationBean bean = new RegistrationBean();
        bean.setName("Mario");
        bean.setSurname("Rossi");
        bean.setEmail("mario@test.com");
        bean.setPassword("Password123");
        bean.setConfirmPassword("Password123");
        bean.setRole(Role.CLIENT);

        // 🌟 Campi anagrafici obbligatori per NightFlow!
        // RISOLTO: Uso di Month.MAY invece di 5
        bean.setDateOfBirth(LocalDate.of(1995, Month.MAY, 20));
        bean.setGender("Uomo");
        bean.setCountry("Italia");
        bean.setCity("Roma");

        registrationController.register(bean);

        // Seconda registrazione con la stessa email — deve lanciare RegistrationException
        RegistrationBean duplicato = new RegistrationBean();
        duplicato.setName("Luigi");
        duplicato.setSurname("Verdi");
        duplicato.setEmail("mario@test.com"); // 🌟 STESSA EMAIL
        duplicato.setPassword("Password123");
        duplicato.setConfirmPassword("Password123");
        duplicato.setRole(Role.CLIENT);

        // Campi anagrafici obbligatori
        // RISOLTO: Uso di Month.JUNE invece di 6
        duplicato.setDateOfBirth(LocalDate.of(1996, Month.JUNE, 21));
        duplicato.setGender("Uomo");
        duplicato.setCountry("Italia");
        duplicato.setCity("Milano");

        assertThrows(RegistrationException.class, () ->
                registrationController.register(duplicato)
        );
    }
}