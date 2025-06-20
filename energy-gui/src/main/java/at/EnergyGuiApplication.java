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
import java.util.ArrayList;
import java.util.List;

public class EnergyGuiApplication extends Application {

    private TextArea responseArea = new TextArea();
    private PieChart pieChart = new PieChart();
    private TableView<EnergyDataRow> historyTable = new TableView<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Energy GUI – Community vs. Grid");

        // Button für aktuelle Daten
        Button currentBtn = new Button("Get Current Data");
        currentBtn.setOnAction(e -> fetchAndDisplayCurrentData());

        // Eingabe für History-Zeitraum
        Label startLabel = new Label("Start:");
        TextField startField = new TextField("2025-01-10T14:00");
        Label endLabel = new Label("End:");
        TextField endField = new TextField("2025-01-10T15:00");
        Button historyBtn = new Button("Get Historical Data");

        historyBtn.setOnAction(e -> {
            String start = startField.getText();
            String end = endField.getText();
            fetchAndDisplayHistoricalData(start, end);
        });

        // Textausgabe
        responseArea.setEditable(false);
        responseArea.setWrapText(true);

        // Tabelle konfigurieren
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

        // Layout
        HBox currentBox = new HBox(10, currentBtn);
        HBox historyBox = new HBox(10, startLabel, startField, endLabel, endField, historyBtn);
        VBox layout = new VBox(15, currentBox, historyBox, pieChart, responseArea, historyTable);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 900, 650);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Automatische Aktualisierung
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> fetchAndDisplayCurrentData())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void fetchAndDisplayCurrentData() {
        String url = "http://localhost:8080/api/energy/current";
        String response = getApiResponse(url);
        responseArea.setText(response);

        try {
            JSONObject obj = new JSONObject(response);
            double grid = obj.getDouble("grid_portion");
            double community = 100.0 - grid;

            pieChart.setTitle("Live: Community vs. Grid");
            pieChart.getData().setAll(
                    new PieChart.Data("Community", community),
                    new PieChart.Data("Grid", grid)
            );

        } catch (Exception ex) {
            responseArea.setText("Fehler beim Parsen: " + ex.getMessage() + "\n" + response);
        }
    }

    private void fetchAndDisplayHistoricalData(String start, String end) {
        String url = String.format("http://localhost:8080/api/energy/historical?start=%s&end=%s", start, end);
        String response = getApiResponse(url);
        responseArea.setText(response);

        try {
            JSONArray array = new JSONArray(response);
            List<EnergyDataRow> rows = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String hour = obj.getString("hour");
                double produced = obj.getDouble("community_produced");
                double used = obj.getDouble("community_used");
                double grid = obj.getDouble("grid_used");

                rows.add(new EnergyDataRow(hour, produced, used, grid));
            }

            historyTable.getItems().setAll(rows);

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

    public static void main(String[] args) {
        launch(args);
    }
}

// Neue Datenklasse für die Tabelle
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
