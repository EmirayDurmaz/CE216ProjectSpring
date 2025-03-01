package com.example.ce216project;
import javafx.application.Application;
import javafx.collections.ObservableList;
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
                displayArtifacts(artifacts);
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

        // Tag ListView ve Filtreleme Butonu
        ListView<String> tagListView = new ListView<>();
        tagListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        // Mevcut artifact'lerdeki tüm benzersiz etiketleri listeye ekle
        Set<String> uniqueTags = new HashSet<>();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                JSONArray tagsArray = artifact.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    uniqueTags.add(tagsArray.getString(j)); // Benzersiz etiketleri Set'e ekle
                }
            }
        }

// Terminale etiketleri yazdır
        System.out.println("Available Tags: " + uniqueTags);

// Eğer hiç etiket bulunamazsa kullanıcıya mesaj göster
        if (uniqueTags.isEmpty()) {
            uniqueTags.add("No tags available");
        }

        tagListView.getItems().clear(); // Önce temizle
        tagListView.getItems().addAll(uniqueTags);



        Button filterButton = new Button("Filter by Tags");
        filterButton.setOnAction(e -> filterArtifactsByTags(tagListView.getSelectionModel().getSelectedItems()));

        HBox buttonBox = new HBox(10, saveButton, exportButton, importButton);
       // HBox filterBox = new HBox(10, tagListView, filterButton);

        //VBox vbox = new VBox(10, buttonBox, searchField, filterBox, displayArea);

        VBox searchBox = new VBox(5, new Label("Search Artifacts"), searchField);
        VBox filterBox = new VBox(5, new Label("Filter by Tags"), tagListView, filterButton);

        HBox controls = new HBox(20, searchBox, filterBox);
        VBox vbox = new VBox(10, buttonBox, controls, displayArea);

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

    /*private void searchArtifacts(String query) {
        JSONArray filteredArtifacts = new JSONArray();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.toString().toLowerCase().contains(query.toLowerCase())) {
                filteredArtifacts.put(artifact);
            }
        }
        displayArtifacts(filteredArtifacts);
    }

     */

    private void searchArtifacts(String query) {
        JSONArray filteredArtifacts = new JSONArray();
        String lowerCaseQuery = query.toLowerCase();

        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);

            // JSON içindeki tüm alanlarda arama yap
            if (artifact.toString().toLowerCase().contains(lowerCaseQuery)) {
                filteredArtifacts.put(artifact);
            }
        }
        displayArtifacts(filteredArtifacts);
    }


    /*private void filterArtifactsByTags(ObservableList<String> selectedTags) {
        if (selectedTags.isEmpty()) {
            displayArtifacts(artifacts);
            return;
        }

        JSONArray filteredArtifacts = new JSONArray();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.has("tags")) {
                JSONArray tagsArray = artifact.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    if (selectedTags.contains(tagsArray.getString(j).toLowerCase())) {
                        filteredArtifacts.put(artifact);
                        break;
                    }
                }
            }
        }

        displayArtifacts(filteredArtifacts);
    }

     */
   /* private void filterArtifactsByTags(ObservableList<String> selectedTags) {
        if (selectedTags.isEmpty()) {
            displayArtifacts(artifacts);
            return;
        }

        JSONArray filteredArtifacts = new JSONArray();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                JSONArray tagsArray = artifact.getJSONArray("tags");

                // Seçilen etiketlerden en az birine sahip artifact'leri listele
                for (int j = 0; j < tagsArray.length(); j++) {
                    if (selectedTags.contains(tagsArray.getString(j).toLowerCase())) {
                        filteredArtifacts.put(artifact);
                        break;
                    }
                }
            }
        }

        displayArtifacts(filteredArtifacts);
    } */

    private void filterArtifactsByTags(ObservableList<String> selectedTags) {
        if (selectedTags.isEmpty()) {
            displayArtifacts(artifacts);
            return;
        }

        JSONArray filteredArtifacts = new JSONArray();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                JSONArray tagsArray = artifact.getJSONArray("tags");

                // Küçük harf formatında karşılaştırma yap
                Set<String> artifactTags = new HashSet<>();
                for (int j = 0; j < tagsArray.length(); j++) {
                    artifactTags.add(tagsArray.getString(j).toLowerCase());
                }

                for (String selectedTag : selectedTags) {
                    if (artifactTags.contains(selectedTag.toLowerCase())) {
                        filteredArtifacts.put(artifact);
                        break; // Artifact zaten filtrelendi, tekrar eklemeye gerek yok
                    }
                }
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

    /*private void importJsonFile(Stage stage) {
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

     */
    private void importJsonFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONArray newArtifacts = new JSONArray(content);

                // JSON'un düzgün okunup okunmadığını kontrol edelim
                System.out.println("Imported JSON Data: " + newArtifacts.toString(4));

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
