package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.enumerator.Role;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationControllerTest {

    @Test
    void testRegisterIntegration() {
        // 1. Setup
        System.setProperty("DAO_TYPE", "MEMORY");
        RegistrationController controller = new RegistrationController();

        // 2. Creazione Bean COMPLETO
        RegistrationBean bean = new RegistrationBean();
        bean.setName("Mario");
        bean.setSurname("Rossi");
        String uniqueEmail = "mario." + System.currentTimeMillis() + "@test.it";
        bean.setEmail(uniqueEmail);
        bean.setPassword("password123");
        bean.setConfirmPassword("password123");
        bean.setRole(Role.CLIENT);
        bean.setDateOfBirth(LocalDate.of(1995, 5, 20));

        // AGGIUNGIAMO TUTTO CIÒ CHE IL CONTROLLER SI ASPETTA
        bean.setGender("Uomo");
        bean.setCountry("Italy");
        bean.setCity("Milano");

        // 3. Esecuzione
        assertDoesNotThrow(() -> {
            controller.register(bean);
        });
    }
}