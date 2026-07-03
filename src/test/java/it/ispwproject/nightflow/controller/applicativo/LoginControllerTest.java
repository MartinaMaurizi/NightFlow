package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.exception.LoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : LoginControllerTest
 * Description: Verifica che il sistema gestisca correttamente
 *              i tentativi di accesso non autorizzati,
 *              indipendentemente dalla modalità di persistenza.
 * ------------------------------------------------------------
 */

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setup() {
        // Possiamo testare il controller in modalità DEMO (InMemory)
        // per isolare la logica di business dal database reale
        DAOFactory.setPersistence(DAOFactory.DEMO);
        loginController = new LoginController();
    }

    @Test
    void testLoginConCredenzialiErrate() {
        // Verifica che un'email non registrata causi il lancio di una LoginException
        assertThrows(LoginException.class, () ->
                loginController.login("utente_inesistente@nightflow.it", "wrongPassword")
        );
    }
}