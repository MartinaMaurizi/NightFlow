package it.ispwproject.nightflow.controller.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;

public final class GUIUtils {

    private GUIUtils() {
        // Costruttore privato per impedire l'istanziamento di questa classe di utilità
    }

    public static Scene createScene(Parent root) {
        loadFonts();

        Scene scene = new Scene(
                root,
                MainGUI.WINDOW_WIDTH,
                MainGUI.WINDOW_HEIGHT
        );

        scene.getStylesheets().add(
                GUIUtils.class
                        .getResource("/styles/nightflow.css")
                        .toExternalForm()
        );

        return scene;
    }

    private static void loadFonts() {
        // Caricamento del font Poppins (assicurati che i file .ttf siano in resources/fonts/)
        Font.loadFont(GUIUtils.class.getResourceAsStream("/fonts/Poppins-Regular.ttf"), 14);
        Font.loadFont(GUIUtils.class.getResourceAsStream("/fonts/Poppins-Bold.ttf"), 14);
        Font.loadFont(GUIUtils.class.getResourceAsStream("/fonts/Poppins-Italic.ttf"), 14);
        Font.loadFont(GUIUtils.class.getResourceAsStream("/fonts/Poppins-BoldItalic.ttf"), 14);
    }
}