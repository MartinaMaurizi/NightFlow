package it.ispwproject.nightflow.controller.applicativo;
import it.ispwproject.nightflow.bean.*;
import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.exception.BookingException;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Client;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : BookingControllerTest
 * Description: Verifica che il sistema gestisca correttamente
 *              i conflitti di prenotazione (es. due utenti
 *              che cercano di prenotare l'ultimo biglietto
 *              disponibile contemporaneamente).
 * ------------------------------------------------------------
 */
class BookingControllerTest {

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
    }

    @Test

    void testPrenotazioneSuEventoGiaRiservato() throws DAOException, BookingException {
        // Evento di prova (es. concerto con posti limitati)
        EventBean event = new EventBean(1, "Jolie Club Night", "Serata techno", null, "Milano", "Jolie", 1, 20.0);

        // --- Studente 1 riserva il biglietto ---
        Client c1 = new Client(1, "Marco", "Verdi", "marco@nightflow.it", null);
        SessionManager.getInstance().setLoggedUser(c1);
        BookingController bc1 = new BookingController();
        ClientBean cb1 = new ClientBean(1, "Marco", "Verdi", "marco@nightflow.it");

        // Prepariamo la prenotazione (qui scatta il blocco del biglietto)
        bc1.prepareBookingSummary(new BookingRequestBean(cb1, event, "Standard"));

        // --- Studente 2 tenta lo stesso evento ---
        Client c2 = new Client(2, "Anna", "Neri", "anna@nightflow.it", null);
        SessionManager.getInstance().setLoggedUser(c2);
        BookingController bc2 = new BookingController();
        ClientBean cb2 = new ClientBean(2, "Anna", "Neri", "anna@nightflow.it");

        // Deve ricevere BookingException perché il biglietto è in "Soft Lock" dallo studente 1
        assertThrows(BookingException.class, () ->
                bc2.prepareBookingSummary(new BookingRequestBean(cb2, event, "Standard"))
        );
    }
}