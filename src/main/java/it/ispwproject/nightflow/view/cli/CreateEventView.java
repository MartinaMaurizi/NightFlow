package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.EventBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreateEventView {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void mostraIntestazione() {
        CLIRenderer.intestazione("Creazione Nuovo Evento");
    }

    public String chiediStringa(String prompt) {
        while (true) {
            String input = CLIRenderer.chiediCampo(prompt);
            if (!input.isEmpty()) return input;

            CLIRenderer.errore("Il campo non può essere vuoto.");
        }
    }

    public LocalDateTime chiediDataOra(String prompt) {
        while (true) {
            String input = CLIRenderer.chiediCampo(prompt);
            try {
                LocalDateTime dateTime = LocalDateTime.parse(input, DATE_FORMATTER);
                if (dateTime.isBefore(LocalDateTime.now())) {
                    CLIRenderer.errore("La data non può essere nel passato.");
                } else {
                    return dateTime;
                }
            } catch (DateTimeParseException e) {
                CLIRenderer.errore("Formato non valido. Usa yyyy-MM-dd HH:mm (es. 2026-08-15 23:30).");
            }
        }
    }

    public int chiediIntero(String prompt) {
        while (true) {
            String input = CLIRenderer.chiediCampo(prompt);
            try {
                int numero = Integer.parseInt(input);
                if (numero > 0) return numero;

                CLIRenderer.errore("Inserisci un numero maggiore di zero.");
            } catch (NumberFormatException e) {
                CLIRenderer.errore("Devi inserire un numero intero valido.");
            }
        }
    }

    public double chiediDecimale(String prompt) {
        while (true) {
            // Sostituisce la virgola col punto per evitare crash nel parsing
            String input = CLIRenderer.chiediCampo(prompt).replace(",", ".");
            try {
                double numero = Double.parseDouble(input);
                if (numero >= 0) return numero;

                CLIRenderer.errore("Il prezzo non può essere negativo.");
            } catch (NumberFormatException e) {
                CLIRenderer.errore("Devi inserire un numero decimale valido (es. 15.50).");
            }
        }
    }

    public void mostraRiepilogo(EventBean bean) {
        CLIRenderer.sezione("Riepilogo Evento");
        CLIRenderer.campo("Nome", bean.getName());
        CLIRenderer.campo("Descrizione", bean.getDescription());
        CLIRenderer.campo("Locale", bean.getLocalName());
        CLIRenderer.campo("Indirizzo", bean.getLocation());
        CLIRenderer.campo("Data e Ora", bean.getDateTime().format(DATE_FORMATTER));
        CLIRenderer.campo("Capacità", bean.getTotalCapacity() + " posti");
        CLIRenderer.campo("Prezzo", String.format("€%.2f", bean.getPrice()));
        CLIRenderer.separatore();
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public void mostraSuccesso(String message) {
        CLIRenderer.successo(message);
    }

    public void mostraErrore(String message) {
        CLIRenderer.errore(message);
    }

    public void mostraMessaggio(String message) {
        CLIRenderer.messaggio(message);
    }

    public void attesaInvio() {
        CLIRenderer.vuota();
        CLIRenderer.chiediSceltaStringa("Premi INVIO per continuare");
    }
}