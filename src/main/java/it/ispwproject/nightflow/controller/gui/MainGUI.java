package it.ispwproject.nightflow.controller.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application {

    // Dimensioni base della finestra, perfette per un layout desktop
    public static final int WINDOW_WIDTH  = 900;
    public static final int WINDOW_HEIGHT = 580;

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("NightFlow - The Nightlife Platform"); // Titolo aggiornato
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setResizable(true);

        showLogin();
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