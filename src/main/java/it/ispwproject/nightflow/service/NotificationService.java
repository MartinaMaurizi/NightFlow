package it.ispwproject.nightflow.service;

/*
 * Servizio per l'invio di notifiche email tramite SendGrid per NightFlow.
 * Gestisce la comunicazione con il servizio email esterno,
 * mantenendo separata la logica applicativa dall'invio delle email.
 */

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.exception.NotificationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NotificationService {

    private static final String PROPERTIES_FILE = "src/main/resources/db.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Impossibile caricare db.properties");
        }
    }

    private static final String API_KEY    = properties.getProperty("SENDGRID_API_KEY");
    private static final String FROM_EMAIL = properties.getProperty("SENDGRID_FROM_EMAIL");

    private static final String TEMPLATE_CONFIRMATION           = "d-e58af91bd9b2428eb628457e5908a2f5";
    private static final String TEMPLATE_CANCELLATION           = "d-b0485d77c6e0494eb281dc356d586016";
    private static final String TEMPLATE_CONFIRMATION_ORGANIZER = "d-fceb5a0323a947a2a118f11e92c9445d";
    private static final String TEMPLATE_CANCELLATION_ORGANIZER = "d-83eff5b598d2455ebacd6b02e70b4819";
    private static final String TEMPLATE_MODIFICATION = "d-0d4cf9e7b7814db888202a31f7a3c6bf";
    // Variabili dinamiche che andranno a riempire i buchi nel template dell'email
    private static final String KEY_CLIENT_NAME    = "clientName";
    private static final String KEY_EVENT_NAME     = "eventName";
    private static final String KEY_EVENT_DATETIME = "eventDateTime";
    private static final String KEY_LOCATION       = "location";
    private static final String KEY_TICKET_CODE    = "ticketCode";
    private static final String KEY_LOCAL_NAME    = "localName";

    private NotificationService() {}

    public static void sendBookingConfirmation(String toEmail,
                                               BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalization(toEmail, booking);
        // Aggiungiamo il codice del biglietto all'email del cliente
        p.addDynamicTemplateData(KEY_TICKET_CODE, booking.getTicketCode());
        sendTemplateEmail(TEMPLATE_CONFIRMATION, p);
    }

    public static void sendBookingCancellation(String toEmail,
                                               BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalization(toEmail, booking);
        sendTemplateEmail(TEMPLATE_CANCELLATION, p);
    }

    public static void sendBookingConfirmationToOrganizer(String toEmail,
                                                          BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalizationForOrganizer(toEmail, booking);
        sendTemplateEmail(TEMPLATE_CONFIRMATION_ORGANIZER, p);
    }

    public static void sendBookingCancellationToOrganizer(String toEmail,
                                                          BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalizationForOrganizer(toEmail, booking);
        sendTemplateEmail(TEMPLATE_CANCELLATION_ORGANIZER, p);
    }
    public static void sendEventModification(String toEmail, BookingResponseBean booking, String changeDescription) throws NotificationException {
        Personalization p = buildPersonalization(toEmail, booking);
        p.addDynamicTemplateData("changeDescription", changeDescription); // Nuova variabile
        sendTemplateEmail(TEMPLATE_MODIFICATION, p);
    }
    // Metodo privato per assemblare i dati del Cliente
    private static Personalization buildPersonalization(String toEmail,
                                                        BookingResponseBean booking) {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));

        String clientFullName = booking.getClient().getName() + " " + booking.getClient().getSurname();

        p.addDynamicTemplateData(KEY_CLIENT_NAME, clientFullName);
        p.addDynamicTemplateData(KEY_EVENT_NAME, booking.getEvent().getName());
        p.addDynamicTemplateData(KEY_EVENT_DATETIME, booking.getEvent().getDateTime().toString());
        p.addDynamicTemplateData(KEY_LOCATION, booking.getEvent().getLocation());
        p.addDynamicTemplateData(KEY_LOCAL_NAME, booking.getEvent().getLocalName());

        return p;
    }

    // Metodo privato per assemblare i dati per l'Organizzatore
    private static Personalization buildPersonalizationForOrganizer(String toEmail,
                                                                    BookingResponseBean booking) {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));

        String clientFullName = booking.getClient() != null
                ? booking.getClient().getName() + " " + booking.getClient().getSurname()
                : "Utente sconosciuto";

        p.addDynamicTemplateData(KEY_CLIENT_NAME, clientFullName);
        p.addDynamicTemplateData(KEY_EVENT_NAME, booking.getEvent().getName());
        p.addDynamicTemplateData(KEY_EVENT_DATETIME, booking.getEvent().getDateTime().toString());

        return p;
    }

    // Metodo effettivo che fa la chiamata HTTP a SendGrid
    private static void sendTemplateEmail(String templateId,
                                          Personalization personalization) throws NotificationException {
        Mail mail = new Mail();
        mail.setFrom(new Email(FROM_EMAIL, "NightFlow")); // Cambiato il mittente!
        mail.setTemplateId(templateId);
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                throw new NotificationException(
                        "Errore invio email (status " + response.getStatusCode() +
                                "): " + response.getBody());
            }

        } catch (IOException e) {
            throw new NotificationException("Errore durante l'invio email: " + e.getMessage(), e);
        }
    }
}