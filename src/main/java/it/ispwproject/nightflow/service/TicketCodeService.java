package it.ispwproject.nightflow.service;

import java.util.UUID;

/**
 * Servizio per la generazione di codici univoci per i biglietti degli eventi.
 * Genera una stringa alfanumerica sicura che l'utente può mostrare all'ingresso
 * del locale per effettuare il check-in (es. NF-TKT-A1B2C3D4).
 */
public final class TicketCodeService {

    // Prefisso personalizzato per i biglietti di NightFlow
    private static final String PREFIX = "NF-TKT-";

    // Costruttore privato per impedire l'istanziazione di questa classe di utilità
    private TicketCodeService() {}

    /**
     * Genera il codice alfanumerico univoco del biglietto.
     * @return Stringa formattata contenente il codice.
     */
    public static String generate() {
        // Prende i primi 8 caratteri di un UUID generato casualmente e li rende maiuscoli
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return PREFIX + randomPart;
    }
}