package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.exception.AvailabilityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvailabilityControllerTest {

    private EventController eventController;

    @BeforeEach
    void setup() {
        // Usiamo la modalità MEMORY per testare la logica reale senza sporcare il DB
        System.setProperty("DAO_TYPE", "MEMORY");
        eventController = new EventController();
    }

    @Test
    void testEventoSovrapposto() throws DAOException, AvailabilityException {
        // 1. Arrange: Prepariamo due eventi nello stesso locale e stesso orario
        LocalDateTime date = LocalDateTime.of(2030, 6, 16, 22, 0);

        EventBean event1 = new EventBean(0, "Techno Night", "Descrizione", date, "Milano", "Jolie Club", 100, 15.0);
        EventBean event2 = new EventBean(0, "Aperitivo Serale", "Descrizione", date, "Milano", "Jolie Club", 50, 10.0);

        // 2. Act & Assert: Il primo viene creato, il secondo deve fallire
        eventController.createEvent(event1);

        assertThrows(AvailabilityException.class, () ->
                        eventController.createEvent(event2),
                "Il sistema dovrebbe impedire la creazione di un evento sovrapposto"
        );
    }
}