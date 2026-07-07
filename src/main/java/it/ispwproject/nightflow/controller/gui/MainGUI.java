package it.ispwproject.nightflow.controller.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application {

    public static final int WINDOW_WIDTH  = 1050;
    public static final int WINDOW_HEIGHT = 580;

    // SonarCloud vuole che se è static, sia gestito con estrema cautela.
    // In JavaFX è accettabile.
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        // Assegniamo lo stage in modo sicuro
        setPrimaryStage(stage);

        primaryStage.setTitle("NightFlow - The Nightlife Platform");
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        primaryStage.setResizable(true);

        showLogin();
    }

    // Metodo privato statico per gestire l'assegnazione
    private static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void showLogin() {
        new LoginGUI(primaryStage).show();
    }

    public static void showRegistration() {
        new RegistrationGUI(primaryStage).show();
    }

    public static void showDashboardClient() {
        new DashboardClientGUI(primaryStage).show();
    }

    // Sostituisce la dashboard del tutor
    public static void showDashboardOrganizer() {
        new DashboardOrganizerGUI(primaryStage).show();
    }

    public static void showMyBookings() {
        // Sostituisce la scena corrente con la schermata delle prenotazioni
        new ViewBookingsGUI(primaryStage).show();
    }

    public static void launch(String[] args) {
        Application.launch(MainGUI.class, args);
    }
}