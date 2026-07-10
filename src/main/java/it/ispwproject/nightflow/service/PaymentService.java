package it.ispwproject.nightflow.service;

import it.ispwproject.nightflow.bean.PaymentRequestBean;

public class PaymentService {

    // Questo è il metodo che metti nel diagramma VOPC!
    public boolean processPayment(PaymentRequestBean request) {
        // Trattandosi di un progetto universitario, qui si simula l'API di Stripe/PayPal
        System.out.println("Contattando la banca per l'utente: " + request.getUserEmail());
        System.out.println("Addebito in corso di: " + request.getAmount() + "€");

        // Finge che il pagamento vada sempre a buon fine
        return true;
    }
}