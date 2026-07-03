package it.ispwproject.nightflow.demo;

import it.ispwproject.nightflow.model.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoDataStore {

    // 🌟 COSTANTI PER EVITARE DUPLICAZIONI (Soddisfa SonarCloud)
    private static final String DEFAULT_PASSWORD = "password";
    private static final String JOLIE_CLUB = "Jolie Club";

    private static DemoDataStore instance;

    private final List<User>    users    = new ArrayList<>();
    private final List<Event>   events   = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    private final Map<Integer, List<Integer>> favoriteLocals = new HashMap<>();

    private int nextUserId    = 10;
    private int nextEventId   = 5;
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
        Client    c1    = new Client(1, "Demo",     "Client", "client@demo",  DEFAULT_PASSWORD);
        Client    c2    = new Client(2, "Anna",     "Bianchi",  "anna@demo",    DEFAULT_PASSWORD);

        Organizer o1 = new Organizer(3, "Demo", "Organizer", "org@demo", DEFAULT_PASSWORD, null, "M", "Italy", "Milano",
                new ArrayList<>(List.of(JOLIE_CLUB)));
        Organizer o2 = new Organizer(4, "Marco", "Bianchi", "marco@demo", DEFAULT_PASSWORD, null, "M", "Italy", "Milano",
                new ArrayList<>(List.of("Magazzini Generali")));

        users.add(c1); users.add(c2); users.add(o1); users.add(o2);

        favoriteLocals.put(1, new ArrayList<>(List.of(3)));

        // ── Eventi ───────────────────────────────────────────────
        // 🌟 Clock.systemDefaultZone() per SonarCloud
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());

        Event e1 = new Event(1, "Techno Night", "Ospite speciale DJ Carl Cox.",
                now.plusDays(1).withHour(23),
                "Milano, Via Navigli", JOLIE_CLUB, 100, 20.0, o1.getId());

        Event e2 = new Event(2, "Aperitivo in Terrazza", "Buffet e spritz.",
                now.plusDays(2).withHour(19),
                "Milano, Via Navigli", JOLIE_CLUB, 50, 15.0, o1.getId());

        Event e3 = new Event(3, "Indie Rock Live", "Musica dal vivo.",
                now.plusDays(1).withHour(21),
                "Milano, Via Pietrasanta", "Magazzini Generali", 200, 25.0, o2.getId());

        events.add(e1); events.add(e2); events.add(e3);

        // ── Prenotazioni ─────────────────────────────────────────
        Booking b1 = new Booking(c1, e1);
        b1.setId(1);
        b1.confirm();
        e1.setAvailableTickets(e1.getAvailableTickets() - 1);
        bookings.add(b1);
    }

    public List<User>       getUsers()       { return users; }
    public List<Event>      getEvents()      { return events; }
    public List<Booking>    getBookings()    { return bookings; }
    public Map<Integer, List<Integer>> getFavoriteLocals() { return favoriteLocals; }

    public int nextUserId()    { return nextUserId++; }
    public int nextEventId()   { return nextEventId++; }
    public int nextBookingId() { return nextBookingId++; }
}