package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.EventBean;
import java.util.List;

public class SearchEventView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("Cerca Eventi");
    }

    public String chiediTermineRicerca() {
        CLIRenderer.messaggio("Inserisci il nome di un evento, di un locale o la città.");
        return CLIRenderer.chiediSceltaStringa("Cerca (o '0' per annullare)");
    }

    public void mostraRisultati(List<EventBean> risultati) {
        CLIRenderer.sezione("Risultati della ricerca (" + risultati.size() + " trovati)");

        for (int i = 0; i < risultati.size(); i++) {
            EventBean e = risultati.get(i);
            // 🌟 Aggiunto l'indice numerico [1], [2] per permettere la selezione
            System.out.printf("  [%d] %s %-20s @ %-15s [%s]%n",
                    i + 1,
                    CLIRenderer.PARTY,
                    e.getName(),
                    e.getLocalName(),
                    e.getDateTime().toLocalDate().toString());
            System.out.printf("      %s %s - Biglietti: %d - Prezzo: €%.2f%n",
                    CLIRenderer.LOCATION,
                    e.getLocation(),
                    e.getAvailableTickets(),
                    e.getPrice());
            CLIRenderer.separatore();
        }
    }

    public int chiediSelezioneEvento(int max) {
        return CLIRenderer.chiediScelta("Seleziona il numero dell'evento per prenotare (o '0' per tornare indietro)", 0, max);
    }

    public void mostraNessunRisultato() {
        CLIRenderer.messaggio("Nessun evento trovato per i criteri inseriti.");
    }

    public void mostraErrore(String errore) {
        CLIRenderer.errore(errore);
    }

    public void attesaInvio() {
        CLIRenderer.chiediSceltaStringa("Premi 0 per tornare al menu");
    }
}