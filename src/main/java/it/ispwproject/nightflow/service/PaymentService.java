package it.ispwproject.nightflow.service;

import it.ispwproject.nightflow.bean.PaymentRequestBean;
import it.ispwproject.nightflow.util.logger.AppLogger;

public class PaymentService {

    public boolean processPayment(PaymentRequestBean request) {
        // Trattandosi di un progetto universitario, qui si simula l'API di PayPal
        AppLogger.logInfo("Contattando la banca per l'utente: " + request.getUserEmail());
        AppLogger.logInfo("Addebito in corso di: " + request.getAmount() + "€");

        // Finge che il pagamento vada sempre a buon fine
        return true;
    }
}