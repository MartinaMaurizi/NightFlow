package it.ispwproject.nightflow.demo;

import it.ispwproject.nightflow.enumerator.PaymentMethod;
import it.ispwproject.nightflow.model.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month; // Aggiunto import
import java.util.ArrayList;
import java.util.List;
import it.ispwproject.nightflow.util.PasswordUtils;

public class DemoDataStore {

    // 1. Assegniamo la password chiamando un metodo sicuro
    private static final String DEFAULT_PASSWORD = generateDefaultPassword();

    // 2. Creiamo il metodo sicuro che gestisce l'eccezione
    private static String generateDefaultPassword() {
        try {
            return PasswordUtils.hash("password");
        } catch (Exception e) {
            // Se la crittografia fallisce, restituisce una stringa di emergenza
            return "password_fallback";
        }
    }

    private static DemoDataStore instance;

    private final List<User>    users    = new ArrayList<>();
    private final List<Event>   events   = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    private int nextUserId    = 10;
    private int nextEventId   = 7;
    private int nextBookingId = 3;

    private DemoDataStore() {
        initData();
    }

    public static synchronized DemoDataStore getInstance() {
        if (instance == null) instance = new DemoDataStore();
        return instance;
    }

    public static void reset() { instance = null; }

    private void initData() {
        // ── Utenti ───────────────────────────────────────────────

        Client c1 = new Client(1, "Demo", "Client", "client@demo", DEFAULT_PASSWORD);
        // RISOLTO: Uso di Month.JANUARY invece di 1
        c1.setDateOfBirth(LocalDate.of(2001, Month.JANUARY, 13));

        Client c2 = new Client(2, "Anna", "Bianchi", "anna@demo", DEFAULT_PASSWORD);
        // RISOLTO: Uso di Month.AUGUST invece di 8
        c2.setDateOfBirth(LocalDate.of(1998, Month.AUGUST, 5));

        Organizer o1 = new Organizer(3, "Luca", "Organizer", "org@demo", DEFAULT_PASSWORD);
        o1.setGender("M");
        o1.setCountry("Italy");
        o1.setCity("Roma");
        o1.addLocalName("Jolie Club");

        Organizer o2 = new Organizer(4, "Marco", "Bianchi", "marco@demo", DEFAULT_PASSWORD);
        o2.setGender("M");
        o2.setCountry("Italy");
        o2.setCity("Roma");
        o2.addLocalName("Magazzini Generali");

        users.add(c1); users.add(c2); users.add(o1); users.add(o2);


        // ── Eventi ───────────────────────────────────────────────

        // Prendiamo SOLO la data di oggi, pulita senza ore/minuti/secondi
        LocalDate today = LocalDate.now(Clock.systemDefaultZone());

        // Ora creiamo gli eventi unendo il giorno a un orario ESATTO con .atTime(ora, minuti)
        Event e1 = new Event(1, "Latin Night", "Ospite speciale DJ Carl Cox.",
                today.plusDays(1).atTime(23, 0), // Domani alle 23:00 spaccate
                "Via Velletri 13, Roma", "Jolie Club", 100, 20.0, o1.getId());

        Event e2 = new Event(2, "Aperitivo in Terrazza", "Buffet e spritz.",
                today.plusDays(2).atTime(19, 30), // Tra 2 giorni alle 19:30
                "Via del Santuario, Roma", "Amazonia", 50, 15.0, o1.getId());

        Event e3 = new Event(3, "Indie Rock Live", "Musica dal vivo.",
                today.plusDays(1).atTime(21, 30), // Domani alle 21:30
                "Via Pietrasanta 16, Roma", "Magazzini Generali", 200, 25.0, o2.getId());

        Event e4 = new Event(4, "The sanctuary eco retreat techno", "Ingresso in lista",
                today.plusDays(5).atTime(23, 30), // Tra 5 giorni alle 23:30
                "Via delle Terme di Traiano", "The sanctuary eco retreat", 150, 30.0, o1.getId());

        Event e5 = new Event(5, "Cena Buffet", "Grande Buffet",
                today.plusDays(7).atTime(22, 0), // Tra 7 giorni alle 22:00
                "Via del Santuario, Roma", "Jerò Restaurant", 100, 25.0, o1.getId());

        Event e6 = new Event(6, "Soft Music", "Serata aperta a tutti",
                today.plusDays(10).atTime(23, 0), // Tra 10 giorni alle 23:00
                "Via Pietrasanta 16, Roma", "Satyrus", 200, 20.0, o2.getId());

        //  EVENTO PASSATO PER TESTARE LO STORICO PRENOTAZIONI
        // Uso di Month.MAY invece di 5
        Event ePassato = new Event(99, "Festa Passata", "Un evento di test nel passato",
                LocalDateTime.of(2025, Month.MAY, 20, 22, 30), "Via Tribale 3, Roma", "Sanctuary", 100, 15.0, o1.getId());

        events.add(e1); events.add(e2); events.add(e3);
        events.add(e4); events.add(e5); events.add(e6);
        events.add(ePassato);


        // ── Prenotazioni ─────────────────────────────────────────

        Booking b1 = new Booking(c1, e1);
        b1.setId(1);
        b1.setTicketType("Tavolo VIP");
        b1.setTicketCode("TKT-NEW-125");
        b1.setPaymentMethod(PaymentMethod.PAY_ON_SITE);
        b1.confirm();
        e1.setAvailableTickets(e1.getAvailableTickets() - 1);
        bookings.add(b1);

        // PRENOTAZIONE EVENTO PASSATO PER IL CLIENTE DEMO (c1)
        Booking bPassata = new Booking(c1, ePassato);
        bPassata.setId(999);
        bPassata.setTicketType("Ingresso VIP");
        bPassata.setTicketCode("TKT-OLD-123");
        bPassata.setPaymentMethod(PaymentMethod.PAYPAL);
        bPassata.confirm();
        bookings.add(bPassata);
    }

    public List<User>       getUsers()       { return users; }
    public List<Event>      getEvents()      { return events; }
    public List<Booking>    getBookings()    { return bookings; }

    public int nextUserId()    { return nextUserId++; }
    public int nextEventId()   { return nextEventId++; }
    public int nextBookingId() { return nextBookingId++; }
}