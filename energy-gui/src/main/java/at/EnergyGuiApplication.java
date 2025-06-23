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

    private final TextArea responseArea = new TextArea();
    private final PieChart pieChart = new PieChart();
    private final TableView<EnergyDataRow> historyTable = new TableView<>();

    // Neue Labels oben für Live-Werte
    private final Label communityLabel = new Label("Community Pool: –");
    private final Label gridLabel = new Label("Grid Portion: –");

    // Neue Labels unten für Summen
    private final Label sumProducedLabel = new Label("Community produced: –");
    private final Label sumUsedLabel = new Label("Community used: –");
    private final Label sumGridLabel = new Label("Grid used: –");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Energy GUI – Community vs. Grid");

        // Button für aktuelle Daten
        Button currentBtn = new Button("Get Current Data");
        currentBtn.setOnAction(e -> fetchAndDisplayCurrentData());

        // Eingabefelder mit aktuellem Zeitraum
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        Label startLabel = new Label("Start:");
        TextField startField = new TextField(
                LocalDateTime.now().minusHours(2).withSecond(0).withNano(0).format(formatter)
        );

        Label endLabel = new Label("End:");
        TextField endField = new TextField(
                LocalDateTime.now().withSecond(0).withNano(0).format(formatter)
        );

        Button historyBtn = new Button("Get Historical Data");
        historyBtn.setOnAction(e -> {
            String start = startField.getText().trim();
            String end = endField.getText().trim();

            if (!start.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}") ||
                    !end.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")) {
                showAlert("Ungültiges Format! Bitte verwende YYYY-MM-DDTHH:MM");
                return;
            }

            fetchAndDisplayHistoricalData(start, end);
        });

        // Tabelle vorbereiten
        TableColumn<EnergyDataRow, String> hourCol = new TableColumn<>("Hour");
        hourCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHour()));

        TableColumn<EnergyDataRow, Number> producedCol = new TableColumn<>("Produced");
        producedCol.setCellValueFactory(data -> new ReadOnlyDoubleWrapper(data.getValue().getCommunityProduced()));

        TableColumn<EnergyDataRow, Number> usedCol = new TableColumn<>("Used");
        usedCol.setCellValueFactory(data -> new ReadOnlyDoubleWrapper(data.getValue().getCommunityUsed()));

        TableColumn<EnergyDataRow, Number> gridCol = new TableColumn<>("Grid Used");
        gridCol.setCellValueFactory(data -> new ReadOnlyDoubleWrapper(data.getValue().getGridUsed()));

        historyTable.getColumns().addAll(hourCol, producedCol, usedCol, gridCol);
        historyTable.setPrefHeight(200);

        // Live-Anzeige oben (Prozentwerte + PieChart)
        HBox liveValuesBox = new HBox(15, communityLabel, gridLabel);
        VBox currentSection = new VBox(10, currentBtn, liveValuesBox, pieChart);

        // History-Filter
        HBox historyBox = new HBox(10, startLabel, startField, endLabel, endField, historyBtn);

        // Summenbereich
        HBox sumBox = new HBox(20, sumProducedLabel, sumUsedLabel, sumGridLabel);

        // Ausgabe unten
        responseArea.setEditable(false);
        responseArea.setWrapText(true);

        // Hauptlayout
        VBox layout = new VBox(15, currentSection, historyBox, responseArea, historyTable, sumBox);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 950, 680);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Auto-Refresh für aktuelle Daten
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(40), event -> fetchAndDisplayCurrentData())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void fetchAndDisplayCurrentData() {
        String url = "http://localhost:8080/energy/current";
        String response = getApiResponse(url);
        responseArea.setText(response);

        try {
            JSONObject obj = new JSONObject(response);
            double production = obj.getDouble("production");
            double consumption = obj.getDouble("consumption");

            if (consumption == 0) {
                communityLabel.setText("Community Pool: 0.00%");
                gridLabel.setText("Grid Portion: 0.00%");
                pieChart.getData().setAll(
                        new PieChart.Data("Community", 0),
                        new PieChart.Data("Grid", 0)
                );
                return;
            }

            double communityPercent = (production / consumption) * 100.0;
            double gridPercent = 100.0 - communityPercent;

            if (communityPercent > 100) communityPercent = 100;
            if (gridPercent < 0) gridPercent = 0;

            communityLabel.setText(String.format("Community Pool: %.2f%%", communityPercent));
            gridLabel.setText(String.format("Grid Portion: %.2f%%", gridPercent));

            pieChart.getData().setAll(
                    new PieChart.Data("Community", communityPercent),
                    new PieChart.Data("Grid", gridPercent)
            );


        } catch (Exception ex) {
            responseArea.setText("Fehler beim Parsen: " + ex.getMessage() + "\n" + response);
        }
    }


    private void fetchAndDisplayHistoricalData(String start, String end) {
        String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);
        String response = getApiResponse(url);
        responseArea.setText(response);

        try {
            JSONArray array = new JSONArray(response);
            List<EnergyDataRow> rows = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String ts = obj.getString("timestamp");
                LocalDateTime dt = LocalDateTime.parse(ts);
                String hour = String.format("%02d:00", dt.getHour());

                double produced = obj.getDouble("production");
                double used     = obj.getDouble("consumption");
                double gridUsed = obj.getDouble("gridUsed");

                rows.add(new EnergyDataRow(hour, produced, used, gridUsed));
            }

            historyTable.getItems().setAll(rows);

            // Summen berechnen
            double totalProduced = rows.stream().mapToDouble(EnergyDataRow::getCommunityProduced).sum();
            double totalUsed     = rows.stream().mapToDouble(EnergyDataRow::getCommunityUsed).sum();
            double totalGrid     = rows.stream().mapToDouble(EnergyDataRow::getGridUsed).sum();

            sumProducedLabel.setText(String.format("Community produced: %.2f kWh", totalProduced));
            sumUsedLabel.setText(String.format("Community used: %.2f kWh", totalUsed));
            sumGridLabel.setText(String.format("Grid used: %.2f kWh", totalGrid));

        } catch (Exception e) {
            responseArea.setText("Fehler beim Parsen der History-Daten:\n" + e.getMessage() + "\n" + response);
        }
    }

    private String getApiResponse(String urlString) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return "Serverfehler: " + conn.getResponseCode();
            }

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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Datenmodell für Tabelleneinträge
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
