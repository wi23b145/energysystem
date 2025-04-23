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

    private TextArea responseArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Energy GUI - Community vs. Grid");

        // Current Data Button
        Button getCurrentBtn = new Button("Get Current Data");
        getCurrentBtn.setOnAction(e -> {
            String response = getApiResponse("http://localhost:8080/energy/current");
            responseArea.setText(response);
        });

        // Historical Data Controls
        Label startLabel = new Label("Start:");
        TextField startField = new TextField("2025-01-10T14:00");

        Label endLabel = new Label("End:");
        TextField endField = new TextField("2025-01-10T15:00");

        Button getHistoricalBtn = new Button("Get Historical Data");
        getHistoricalBtn.setOnAction(e -> {
            String start = startField.getText();
            String end = endField.getText();
            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);
            String response = getApiResponse(url);
            responseArea.setText(response);
        });

        // Layout
        HBox currentBox = new HBox(10, getCurrentBtn);
        HBox timeBox = new HBox(10, startLabel, startField, endLabel, endField, getHistoricalBtn);
        VBox layout = new VBox(10, currentBox, timeBox, responseArea);
        layout.setPadding(new Insets(15));

        responseArea.setEditable(false);
        responseArea.setWrapText(true);

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getApiResponse(String urlString) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();
        } catch (Exception e) {
            response.append("Error: ").append(e.getMessage());
        }
        return response.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
