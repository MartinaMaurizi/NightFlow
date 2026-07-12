package it.ispwproject.nightflow.view.cli;

import java.util.Scanner;

/**
 * Utility di rendering per NightFlow CLI.
 */
public final class CLIRenderer {

    // Costanti visive
    public static final int    WIDTH     = 60;
    public static final String LINE      = "─".repeat(WIDTH);
    public static final String LINE_THIN = "╌".repeat(WIDTH);
    public static final String LINE_DECO = "═".repeat(WIDTH);

    // Simboli di stato NightFlow
    public static final String OK      = "✓";
    public static final String ERR     = "✗";
    public static final String BULLET  = "•";
    public static final String PARTY   = "🎉"; // Per eventi
    public static final String TICKET  = "🎟️"; // Per prenotazioni
    public static final String LOCATION = "📍"; // Per luoghi

    public static final Scanner SCANNER = new Scanner(System.in);

    private CLIRenderer() {}

    public static void intestazione(String titolo) {
        System.out.println();
        System.out.println(LINE_DECO);
        System.out.println("  NightFlow – " + titolo);
        System.out.println(LINE_DECO);
    }

    public static void intestazioneBenvenuto(String nome, String ruolo) {
        String left  = "  Ciao, " + nome + "!";
        String right = "[ " + ruolo + " ]";
        int spaces   = WIDTH - left.length() - right.length();
        String pad   = spaces > 0 ? " ".repeat(spaces) : "  ";
        System.out.println();
        System.out.println(LINE_DECO);
        System.out.println(left + pad + right);
        System.out.println(LINE_DECO);
    }

    public static void sezione(String etichetta) {
        System.out.println("\n  ── " + etichetta);
    }

    public static void separatore() {
        System.out.println(LINE);
    }

    public static void vuota() {
        System.out.println();
    }

    public static void messaggio(String testo) {
        System.out.println("  " + testo);
    }

    public static void successo(String testo) {
        System.out.println("  " + OK + " " + testo);
        System.out.println(LINE);
    }

    public static void errore(String testo) {
        System.out.println("  " + ERR + " " + testo);
    }

    public static void campo(String etichetta, String valore) {
        System.out.printf("  %-12s: %s%n", etichetta, valore != null ? valore : "—");
    }

    public static void voceMenu(int numero, String etichetta) {
        System.out.printf("  [%d] %s%n", numero, etichetta);
    }

    public static void voceMenuZero(String etichetta) {
        System.out.println();
        System.out.printf("  [0] %s%n", etichetta);
    }

    public static int chiediScelta(String prompt, int min, int max) {
        while (true) {
            System.out.printf("%n  %s [%d–%d]: ", prompt, min, max);
            String input = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.printf("  Inserisci un numero tra %d e %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Input non valido.");
            }
        }
    }

    public static String chiediSceltaStringa(String prompt) {
        System.out.printf("\n  %s: ", prompt);
        return SCANNER.nextLine().trim();
    }

    public static boolean chiediConferma(String prompt) {
        while (true) {
            System.out.printf("  %s [s/n]: ", prompt);
            String input = SCANNER.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("sì")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("  Rispondi con 's' oppure 'n'.");
        }
    }

    public static String chiediCampo(String label) {
        System.out.printf("  %s: ", label);
        return SCANNER.nextLine().trim();
    }

    public static String centra(String testo) {
        int pad = Math.max(0, (WIDTH - testo.length()) / 2);
        return " ".repeat(pad) + testo;
    }
}