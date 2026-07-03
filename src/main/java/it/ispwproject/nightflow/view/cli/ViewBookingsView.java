package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.BookingResponseBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewBookingsView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("NightFlow  –  Le mie prenotazioni");
    }

    public void mostraTab(int nConfermate, int nCancellate, int nScadute) {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Confermate   (" + nConfermate + ")");
        CLIRenderer.voceMenu(2, "Cancellate   (" + nCancellate + ")");
        CLIRenderer.voceMenu(3, "Passate      (" + nScadute + ")");
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraConfermate(List<BookingResponseBean> bookings) {
        CLIRenderer.sezione("Prenotazioni Confermate");
        if (bookings.isEmpty()) {
            CLIRenderer.messaggio("Non hai prenotazioni attive.");
            CLIRenderer.separatore();
            return;
        }
        for (BookingResponseBean b : bookings) {
            CLIRenderer.vuota();
            System.out.println("  " + CLIRenderer.LINE_THIN);
            CLIRenderer.campo("Evento",    b.getEvent().getName());
            CLIRenderer.campo("Locale",    b.getEvent().getLocalName());
            CLIRenderer.campo("Data/Ora",  b.getEvent().getDateTime().toLocalDate() + " "
                    + b.getEvent().getDateTime().toLocalTime());
            CLIRenderer.campo("Cod. Tkt",  b.getTicketCode());
            CLIRenderer.campo("Tipo",      b.getTicketType());
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraCancellate(List<BookingResponseBean> bookings) {
        CLIRenderer.sezione("Prenotazioni Cancellate");
        if (bookings.isEmpty()) {
            CLIRenderer.messaggio("Non hai prenotazioni cancellate.");
            CLIRenderer.separatore();
            return;
        }
        for (BookingResponseBean b : bookings) {
            CLIRenderer.vuota();
            System.out.println("  " + CLIRenderer.LINE_THIN);
            CLIRenderer.campo("Evento",    b.getEvent().getName());
            CLIRenderer.campo("Locale",    b.getEvent().getLocalName());
            CLIRenderer.campo("Data",      b.getEvent().getDateTime().toLocalDate().toString());
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraScadute(List<BookingResponseBean> past) {
        CLIRenderer.sezione("Storico Serate");
        if (past.isEmpty()) {
            CLIRenderer.messaggio("Nessuna serata passata.");
            CLIRenderer.separatore();
            return;
        }

        // Raggruppa per Nome Evento (o LocalName se preferisci)
        Map<String, List<BookingResponseBean>> byEvent = past.stream()
                .collect(Collectors.groupingBy(b -> b.getEvent().getName()));

        List<String> eventNames = byEvent.keySet().stream().sorted().toList();

        for (String name : eventNames) {
            List<BookingResponseBean> group = byEvent.get(name).stream()
                    .sorted((a, b) -> b.getEvent().getDateTime().compareTo(a.getEvent().getDateTime()))
                    .toList();

            CLIRenderer.vuota();
            System.out.printf("  %s  (%d partecipazione/i)%n",
                    name, group.size());
            System.out.println("  " + CLIRenderer.LINE_THIN);

            for (BookingResponseBean b : group) {
                System.out.printf("  %s  %s  @ %s%n",
                        CLIRenderer.BULLET,
                        b.getEvent().getDateTime().toLocalDate(),
                        b.getEvent().getLocalName());
            }
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}