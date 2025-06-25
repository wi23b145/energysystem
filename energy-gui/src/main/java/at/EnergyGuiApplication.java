package at;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EnergyGuiApplication extends Application {

    // TextArea für die API-Antwort und Debugging
    private final TextArea responseArea = new TextArea();

    // PieChart zur Darstellung der Energieanteile (Community vs. Grid)
    private final PieChart pieChart = new PieChart();

    // Tabelle, die historische Daten anzeigt (Stunde, produzierte, verwendete und zugeführte Energie)
    private final TableView<EnergyDataRow> historyTable = new TableView<>();

    // Labels oben für die Live-Daten (Aktueller Community Pool und Grid Anteil)
    private final Label communityLabel = new Label("Community Pool: –");
    private final Label gridLabel = new Label("Grid Portion: –");

    // Labels unten für die Summen von produzierter, verwendeter und zugeführter Energie
    private final Label sumProducedLabel = new Label("Community produced: –");
    private final Label sumUsedLabel = new Label("Community used: –");
    private final Label sumGridLabel = new Label("Grid used: –");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Energy GUI – Community vs. Grid");

        // Button zum Abrufen aktueller Daten von der API
        Button currentBtn = new Button("Get Current Data");
        currentBtn.setOnAction(e -> fetchAndDisplayCurrentData());

        // Erstellen von Eingabefeldern für den Zeitraum (Start- und Endzeit) der historischen Daten
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        Label startLabel = new Label("Start:");
        TextField startField = new TextField(
                LocalDateTime.now().minusHours(2).withSecond(0).withNano(0).format(formatter)
        );

        Label endLabel = new Label("End:");
        TextField endField = new TextField(
                LocalDateTime.now().withSecond(0).withNano(0).format(formatter)
        );

        // Button zum Abrufen historischer Daten
        Button historyBtn = new Button("Get Historical Data");
        historyBtn.setOnAction(e -> {
            String start = startField.getText().trim();
            String end = endField.getText().trim();

            // Validierung des Datumsformats
            if (!start.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}") ||
                    !end.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")) {
                showAlert("Ungültiges Format! Bitte verwende YYYY-MM-DDTHH:MM");
                return;
            }

            // Abrufen und Anzeigen historischer Daten
            fetchAndDisplayHistoricalData(start, end);
        });

        // Erstellung der Tabelle mit Spalten für die Stunde, produzierte, verwendete und zugeführte Energie
        TableColumn<EnergyDataRow, String> hourCol = new TableColumn<>("Hour");
        hourCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHour()));

        TableColumn<EnergyDataRow, Number> producedCol = new TableColumn<>("Produced");
        producedCol.setCellValueFactory(data -> new ReadOnlyDoubleWrapper(data.getValue().getCommunityProduced()));

        TableColumn<EnergyDataRow, Number> usedCol = new TableColumn<>("Used");
        usedCol.setCellValueFactory(data -> new ReadOnlyDoubleWrapper(data.getValue().getCommunityUsed()));

        TableColumn<EnergyDataRow, Number> gridCol = new TableColumn<>("Grid Used");
        gridCol.setCellValueFactory(data -> new ReadOnlyDoubleWrapper(data.getValue().getGridUsed()));

        historyTable.getColumns().addAll(hourCol, producedCol, usedCol, gridCol);
        historyTable.setPrefHeight(200); // Höhe der Tabelle

        // HBox für die Anzeige der Live-Werte (Prozentuale Verteilung von Community und Grid)
        HBox liveValuesBox = new HBox(15, communityLabel, gridLabel);
        VBox currentSection = new VBox(10, currentBtn, liveValuesBox, pieChart);

        // HBox für die Filter des historischen Zeitraums (Start und End)
        HBox historyBox = new HBox(10, startLabel, startField, endLabel, endField, historyBtn);

        // HBox für die Summen von produzierter, verwendeter und zugeführter Energie
        HBox sumBox = new HBox(20, sumProducedLabel, sumUsedLabel, sumGridLabel);

        // TextArea für die Ausgabe von Fehlern oder API-Antworten
        responseArea.setEditable(false);
        responseArea.setWrapText(true);

        // Das Hauptlayout der GUI
        VBox layout = new VBox(15, currentSection, historyBox, responseArea, historyTable, sumBox);
        layout.setPadding(new Insets(15));

        // Die Szene mit der Hauptlayout und der Fenstergröße
        Scene scene = new Scene(layout, 950, 680);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Auto-Refresh für aktuelle Daten alle 40 Sekunden
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(40), event -> fetchAndDisplayCurrentData())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void fetchAndDisplayCurrentData() {
        // URL für die API, um die aktuellen Energie-Daten abzurufen
        String url = "http://localhost:8080/energy/current";
        String response = getApiResponse(url);
        responseArea.setText(response); // Zeigt die Antwort in der TextArea an

        try {
            JSONObject obj = new JSONObject(response);
            double production = obj.getDouble("production"); // Energieproduktion
            double consumption = obj.getDouble("consumption"); // Energieverbrauch

            // Wenn der Verbrauch null ist, wird ein leerer Pie-Chart angezeigt
            if (consumption == 0) {
                communityLabel.setText("Community Pool: 0.00%");
                gridLabel.setText("Grid Portion: 0.00%");
                pieChart.getData().setAll(
                        new PieChart.Data("Community", 0),
                        new PieChart.Data("Grid", 0)
                );
                return;
            }

            // Berechnet die Prozentanteile von Community und Grid
            double communityPercent = (production / consumption) * 100.0;
            double gridPercent = 100.0 - communityPercent;

            // Sicherstellen, dass die Prozentwerte zwischen 0 und 100 liegen
            if (communityPercent > 100) communityPercent = 100;
            if (gridPercent < 0) gridPercent = 0;

            // Aktualisieren der Labels und des Pie-Charts mit den berechneten Werten
            communityLabel.setText(String.format("Community Pool: %.2f%%", communityPercent));
            gridLabel.setText(String.format("Grid Portion: %.2f%%", gridPercent));

            pieChart.getData().setAll(
                    new PieChart.Data("Community", communityPercent),
                    new PieChart.Data("Grid", gridPercent)
            );
        } catch (Exception ex) {
            // Fehlerbehandlung beim Parsen der API-Antwort
            responseArea.setText("Fehler beim Parsen: " + ex.getMessage() + "\n" + response);
        }
    }

    private void fetchAndDisplayHistoricalData(String start, String end) {
        // URL für die API, um historische Daten zwischen Start- und Endzeit abzurufen
        String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);
        String response = getApiResponse(url);
        responseArea.setText(response); // Zeigt die Antwort in der TextArea an

        try {
            JSONArray array = new JSONArray(response); // Antwort als JSON-Array
            List<EnergyDataRow> rows = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String ts = obj.getString("timestamp"); // Zeitstempel der Daten
                LocalDateTime dt = LocalDateTime.parse(ts); // Zeitstempel in LocalDateTime umwandeln
                String hour = String.format("%02d:00", dt.getHour()); // Stunde extrahieren

                // Produzierte, verwendete und zugeführte Energie für jede Stunde
                double produced = obj.getDouble("production");
                double used     = obj.getDouble("consumption");
                double gridUsed = obj.getDouble("gridUsed");

                // Zeile für die Tabelle erstellen
                rows.add(new EnergyDataRow(hour, produced, used, gridUsed));
            }

            // Die Tabelle mit den historischen Daten füllen
            historyTable.getItems().setAll(rows);

            // Berechnen der Gesamtsummen
            double totalProduced = rows.stream().mapToDouble(EnergyDataRow::getCommunityProduced).sum();
            double totalUsed     = rows.stream().mapToDouble(EnergyDataRow::getCommunityUsed).sum();
            double totalGrid     = rows.stream().mapToDouble(EnergyDataRow::getGridUsed).sum();

            // Summen in den entsprechenden Labels anzeigen
            sumProducedLabel.setText(String.format("Community produced: %.2f kWh", totalProduced));
            sumUsedLabel.setText(String.format("Community used: %.2f kWh", totalUsed));
            sumGridLabel.setText(String.format("Grid used: %.2f kWh", totalGrid));

        } catch (Exception e) {
            // Fehlerbehandlung bei der Verarbeitung der historischen Daten
            responseArea.setText("Fehler beim Parsen der History-Daten:\n" + e.getMessage() + "\n" + response);
        }
    }

    private String getApiResponse(String urlString) {
        // Holt die Antwort von der API
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Überprüft den Statuscode der Antwort (200 = OK)
            if (conn.getResponseCode() != 200) {
                return "Serverfehler: " + conn.getResponseCode();
            }

            // Liest die Antwort
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();
        } catch (Exception e) {
            response.append("Verbindungsfehler: ").append(e.getMessage());
        }
        return response.toString();
    }

    // Zeigt eine Fehlermeldung als Dialog an
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args); // Startet die JavaFX-Anwendung
    }
}

// Datenmodell für die Tabelle (jede Zeile zeigt eine Stunde und die entsprechenden Energiemengen)
class EnergyDataRow {
    private final String hour;
    private final double communityProduced;
    private final double communityUsed;
    private final double gridUsed;

    public EnergyDataRow(String hour, double communityProduced, double communityUsed, double gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public String getHour() { return hour; }
    public double getCommunityProduced() { return communityProduced; }
    public double getCommunityUsed() { return communityUsed; }
    public double getGridUsed() { return gridUsed; }
}
