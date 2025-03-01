package com.example.ce216project;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ArtifactCatalogApp extends Application {
    private TextArea displayArea;
    private TextField searchField;
    private File currentFile;
    private JSONArray artifacts = new JSONArray();
    private Set<String> importedFiles = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Historical Artifact Catalog");

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefHeight(500);

        searchField = new TextField();
        searchField.setPromptText("Search artifacts...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                displayArtifacts(artifacts); // Ensure all artifacts are displayed when search is cleared
            } else {
                searchArtifacts(newValue);
            }
        });

        Button saveButton = new Button("Save Imported Artifacts");
        saveButton.setOnAction(e -> saveImportedArtifacts());

        Button exportButton = new Button("Export JSON File");
        exportButton.setOnAction(e -> exportJsonFile(primaryStage));

        Button importButton = new Button("Import JSON File");
        importButton.setOnAction(e -> importJsonFile(primaryStage));

        HBox buttonBox = new HBox(10, saveButton, exportButton, importButton);
        VBox vbox = new VBox(10, buttonBox, searchField, displayArea);

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayArtifacts(JSONArray artifactsToDisplay) {
        StringBuilder formattedText = new StringBuilder();

        for (int i = 0; i < artifactsToDisplay.length(); i++) {
            JSONObject artifact = artifactsToDisplay.getJSONObject(i);
            formattedText.append("Artifact ID: ").append(artifact.optString("artifactid", "N/A")).append("\n");
            formattedText.append("Artifact Name: ").append(artifact.optString("artifactname", "N/A")).append("\n");
            formattedText.append("Category: ").append(artifact.optString("category", "N/A")).append("\n");
            formattedText.append("Civilization: ").append(artifact.optString("civilization", "N/A")).append("\n");
            formattedText.append("Composition: ").append(artifact.optString("composition", "N/A")).append("\n");
            formattedText.append("Discovery Location: ").append(artifact.optString("discoverylocation", "N/A")).append("\n");
            formattedText.append("Current Place: ").append(artifact.optString("currentplace", "N/A")).append("\n");
            formattedText.append("Discovery Date: ").append(artifact.optString("discoverydate", "N/A")).append("\n");
            formattedText.append("Weight: ").append(artifact.optDouble("weight", 0)).append("\n");
            formattedText.append("---------------------------------------\n");
        }

        displayArea.setText(formattedText.toString());
    }

    private void exportJsonFile(Stage stage) {
        if (artifacts.isEmpty()) {
            displayArea.setText("No data to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(artifacts.toString(4));
                displayArea.setText("JSON file exported successfully to " + file.getAbsolutePath());
            } catch (IOException e) {
                displayArea.setText("Error exporting file: " + e.getMessage());
            }
        }
    }

    private void searchArtifacts(String query) {
        JSONArray filteredArtifacts = new JSONArray();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.toString().toLowerCase().contains(query.toLowerCase())) {
                filteredArtifacts.put(artifact);
            }
        }
        displayArtifacts(filteredArtifacts);
    }

    private void saveImportedArtifacts() {
        if (artifacts.isEmpty()) {
            displayArea.setText("No artifacts to save.");
            return;
        }

        try (FileWriter file = new FileWriter("artifacts.json")) {
            file.write(artifacts.toString(4));
            displayArea.appendText("\nImported artifacts saved successfully!");
        } catch (IOException e) {
            displayArea.setText("Error saving artifacts: " + e.getMessage());
        }
    }

    private void importJsonFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            if (importedFiles.contains(file.getAbsolutePath())) {
                displayArea.setText("This file has already been imported.");
                return;
            }

            importedFiles.add(file.getAbsolutePath());
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONArray newArtifacts = new JSONArray(content);
                for (int i = 0; i < newArtifacts.length(); i++) {
                    artifacts.put(newArtifacts.getJSONObject(i));
                }
                displayArtifacts(artifacts);
            } catch (IOException e) {
                displayArea.setText("Error loading artifacts: " + e.getMessage());
            }
        }
    }

    public static void main(String[]args){

        launch(args);
        //push try
    }
}
