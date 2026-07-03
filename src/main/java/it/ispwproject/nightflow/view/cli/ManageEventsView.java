package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.util.logger.AppLogger; // 🌟 Logger
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ManageEventsView {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void mostraIntestazione() {
        CLIRenderer.intestazione("NightFlow  –  Gestisci Eventi");
    }

    public int mostraMenuEventi() {
        CLIRenderer.sezione("Pannello di Controllo Eventi");
        CLIRenderer.voceMenu(1, "Visualizza i tuoi eventi");
        CLIRenderer.voceMenu(2, "Crea un nuovo evento");
        CLIRenderer.voceMenuZero("Torna alla Dashboard");
        return CLIRenderer.chiediScelta("Scelta: ", 0, 2);
    }

    public void mostraListaEventi(List<EventBean> eventi) {
        CLIRenderer.sezione("I tuoi eventi in programma");

        for (int i = 0; i < eventi.size(); i++) {
            EventBean e = eventi.get(i);
            String dataFormattata = e.getDateTime() != null ? e.getDateTime().format(DT_FMT) : "Data n.d.";

            // 🌟 Sostituiti System.out con AppLogger
            AppLogger.logInfo(String.format("  [%d] %-24s | %s | %s",
                    i + 1, e.getName(), dataFormattata, e.getLocalName()));
            AppLogger.logInfo(String.format("      Prezzo: %.2f€  |  Biglietti Rimasti: %d",
                    e.getPrice(), e.getAvailableTickets()));
            CLIRenderer.separatore(); // Usa il metodo già presente nel renderer
        }
    }

    public EventBean chiediDatiNuovoEvento() {
        CLIRenderer.sezione("Inserimento Nuovo Evento");

        String nome = CLIRenderer.chiediCampo("Nome dell'evento: ");
        String descrizione = CLIRenderer.chiediCampo("Descrizione: ");

        LocalDateTime dataOra = null;
        while (dataOra == null) {
            String dataStr = CLIRenderer.chiediCampo("Data e Ora (formato GG/MM/AAAA HH:MM): ");
            try {
                dataOra = LocalDateTime.parse(dataStr, DT_FMT);
            } catch (DateTimeParseException e) {
                CLIRenderer.errore("Formato data non valido! (es. 22/11/2026 22:30).");
            }
        }

        String locale = CLIRenderer.chiediCampo("Nome del Locale (es. Jolie Club): ");
        String indirizzo = CLIRenderer.chiediCampo("Indirizzo del Locale (es. Via Velletri, 13): ");

        int biglietti = 0;
        boolean numValido = false;
        while (!numValido) {
            try {
                biglietti = Integer.parseInt(CLIRenderer.chiediCampo("Numero totale di biglietti disponibili: "));
                if (biglietti > 0) numValido = true;
                else CLIRenderer.errore("Il numero di biglietti deve essere maggiore di zero.");
            } catch (NumberFormatException e) {
                CLIRenderer.errore("Inserisci un numero intero valido.");
            }
        }

        double prezzo = 0.0;
        numValido = false;
        while (!numValido) {
            try {
                prezzo = Double.parseDouble(CLIRenderer.chiediCampo("Prezzo del biglietto (€): "));
                if (prezzo >= 0) numValido = true;
                else CLIRenderer.errore("Il prezzo non può essere negativo.");
            } catch (NumberFormatException e) {
                CLIRenderer.errore("Inserisci un prezzo valido (usa il punto per i decimali, es. 15.50).");
            }
        }

        EventBean newEvent = new EventBean();
        newEvent.setName(nome);
        newEvent.setDescription(descrizione);
        newEvent.setDateTime(dataOra);
        newEvent.setLocation(indirizzo);
        newEvent.setLocalName(locale);
        newEvent.setAvailableTickets(biglietti);
        newEvent.setPrice(prezzo);

        return newEvent;
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public void attesaInvio() {
        CLIRenderer.chiediCampo("[ INVIO per continuare ]");
    }
}