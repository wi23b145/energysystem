package at;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnergyGuiApplication extends Application {

    // TextArea zum Anzeigen der API-Antwort
    private TextArea responseArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        // Fenster-Titel setzen
        primaryStage.setTitle("Energy GUI - Community vs. Grid");

        // Button zum Abrufen der aktuellen Daten
        Button getCurrentBtn = new Button("Get Current Data");
        getCurrentBtn.setOnAction(e -> {
            // API-Aufruf für aktuelle Daten und Anzeige der Antwort in der TextArea
            String response = getApiResponse("http://localhost:8080/energy/current");
            responseArea.setText(response);  // Antwort in der TextArea anzeigen
        });

        // Steuerung für historische Daten
        Label startLabel = new Label("Start:");  // Label für den Startzeitpunkt
        TextField startField = new TextField("2025-01-10T14:00");  // Textfeld für Startzeit

        Label endLabel = new Label("End:");  // Label für den Endzeitpunkt
        TextField endField = new TextField("2025-01-10T15:00");  // Textfeld für Endzeit

        // Button zum Abrufen der historischen Daten
        Button getHistoricalBtn = new Button("Get Historical Data");
        getHistoricalBtn.setOnAction(e -> {
            // Start- und Endzeit aus den Textfeldern lesen
            String start = startField.getText();
            String end = endField.getText();
            // URL mit den Parametern für Start- und Endzeit erstellen
            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);
            // API-Aufruf für historische Daten und Anzeige der Antwort in der TextArea
            String response = getApiResponse(url);
            responseArea.setText(response);  // Antwort in der TextArea anzeigen
        });

        // Layout-Container für die Schaltflächen und Textfelder
        HBox currentBox = new HBox(10, getCurrentBtn);  // Box für den Button zum Abrufen der aktuellen Daten
        HBox timeBox = new HBox(10, startLabel, startField, endLabel, endField, getHistoricalBtn);  // Box für Start- und Endzeit sowie Button für historische Daten
        VBox layout = new VBox(10, currentBox, timeBox, responseArea);  // Vertikales Layout für die gesamte GUI
        layout.setPadding(new Insets(15));  // Innenabstände im Layout setzen

        // TextArea für die Anzeige der API-Antwort konfigurieren
        responseArea.setEditable(false);  // TextArea nicht bearbeitbar machen
        responseArea.setWrapText(true);  // Zeilenumbruch in der TextArea aktivieren

        // Szene erstellen und auf der Stage anzeigen
        Scene scene = new Scene(layout, 800, 400);  // Szene mit Layout und Fenstergröße erstellen
        primaryStage.setScene(scene);  // Szene auf der Stage setzen
        primaryStage.show();  // Fenster anzeigen
    }

    // Methode zum Abrufen der API-Antwort
    private String getApiResponse(String urlString) {
        StringBuilder response = new StringBuilder();  // StringBuilder für die Antwort
        try {
            URL url = new URL(urlString);  // URL aus dem übergebenen String erstellen
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  // Verbindung zur URL herstellen
            conn.setRequestMethod("GET");  // HTTP-GET-Methode festlegen

            // Eingabestrom für die API-Antwort öffnen
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {  // Zeilenweise Antwort lesen
                response.append(line).append("\n");  // Zeilen zur Antwort hinzufügen
            }
            in.close();  // Eingabestrom schließen
        } catch (Exception e) {
            // Fehlerbehandlung, falls ein Problem auftritt
            response.append("Error: ").append(e.getMessage());  // Fehlermeldung in der Antwort anzeigen
        }
        return response.toString();  // Antwort als String zurückgeben
    }

    // Main-Methode zum Starten der Anwendung
    public static void main(String[] args) {
        launch(args);  // JavaFX-Anwendung starten
    }
}
