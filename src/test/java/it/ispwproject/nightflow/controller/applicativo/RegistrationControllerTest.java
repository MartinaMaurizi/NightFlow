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
        // 1. Setup: Imposta il DAO su JDBC o MEMORY
        System.setProperty("DAO_TYPE", "MEMORY"); // O "JDBC" se hai il DB collegato

        RegistrationController controller = new RegistrationController();

        // 2. Creazione Bean
        RegistrationBean bean = new RegistrationBean();
        bean.setName("Mario");
        bean.setSurname("Rossi");
        bean.setEmail("mario.rossi@test.it");
        bean.setPassword("password123");
        bean.setConfirmPassword("password123");
        bean.setRole(Role.CLIENT);
        bean.setDateOfBirth(LocalDate.of(1995, 5, 20));

        // 3. Esecuzione
        assertDoesNotThrow(() -> {
            controller.register(bean);
        });

        // 4. Verifica (opzionale: controlla se l'utente esiste nel DB)
    }
}