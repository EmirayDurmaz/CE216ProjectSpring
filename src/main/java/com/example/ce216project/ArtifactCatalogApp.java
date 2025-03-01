package com.example.ce216project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ArtifactCatalogApp extends Application {
    private TextArea displayArea;
    private TextField searchField;
    private JSONArray artifacts = new JSONArray();
    private Set<String> importedFiles = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Historical Artifact Catalog");

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefHeight(400);

        searchField = new TextField();
        searchField.setPromptText("Search artifacts...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                displayArtifacts(artifacts);
            } else {
                searchArtifacts(newValue);
            }
        });

        // Butonlar
        Button addButton = new Button("Add Artifact");
        addButton.setOnAction(e -> showAddArtifactDialog());

        Button editButton = new Button("Edit Artifact");
        editButton.setOnAction(e -> showEditArtifactDialog());

        Button deleteButton = new Button("Delete Artifact");
        deleteButton.setOnAction(e -> showDeleteArtifactDialog());

        Button saveButton = new Button("Save Imported Artifacts");
        saveButton.setOnAction(e -> saveImportedArtifacts());

        Button exportButton = new Button("Export JSON File");
        exportButton.setOnAction(e -> exportJsonFile(primaryStage));

        Button importButton = new Button("Import JSON File");
        importButton.setOnAction(e -> importJsonFile(primaryStage));

        Button helpButton = new Button("Help");
        helpButton.setOnAction(e -> showBasicHelp());

        // **Menü Çubuğu**
        MenuBar menuBar = new MenuBar();
        Menu helpMenu = new Menu("Help");

        // Menü Öğeleri
        MenuItem documentationItem = new MenuItem("Documentation");
        documentationItem.setOnAction(e -> showDocumentation());

        MenuItem examplesItem = new MenuItem("Examples");
        examplesItem.setOnAction(e -> showExamples());

        MenuItem supportItem = new MenuItem("Support Web Site");
        supportItem.setOnAction(e -> openSupportWebsite());

        MenuItem updatesItem = new MenuItem("Check for Updates");
        updatesItem.setOnAction(e -> checkForUpdates());

        MenuItem accessibilityItem = new MenuItem("Accessibility");
        accessibilityItem.setOnAction(e -> showAccessibilityOptions());

        helpMenu.getItems().addAll(documentationItem, examplesItem, supportItem, updatesItem, accessibilityItem);
        menuBar.getMenus().add(helpMenu);

        // **Düzen**
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, saveButton, exportButton, importButton, helpButton);
        VBox vbox = new VBox(10, menuBar, searchField, displayArea, buttonBox);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 800, 600);

        // **f1 Tuşuna Basılınca Help Penceresini Aç**
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F1) {
                showDocumentation();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // **Help Butonu İçin Bilgi Penceresi**
    private void showBasicHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("How to Use the Software");
        alert.setContentText("1. Import artifacts using 'Import JSON File'.\n"
                + "2. Search for artifacts using the search bar.\n"
                + "3. Save or export artifacts as needed.\n"
                + "4. For further assistance, refer to the documentation.");
        alert.showAndWait();
    }

    // **Menü Öğeleri İçin İşlevler**
    private void showDocumentation() {
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setTitle("User Manual & FAQ");

        TextArea helpText = new TextArea();
        helpText.setEditable(false);
        helpText.setWrapText(true);
        helpText.setText(
                "Welcome to the Historical Artifact Catalog!\n\n"
                        + "How to Use:\n"
                        + "1. Click 'Import JSON File' to load artifact data.\n"
                        + "2. Use the search bar to find artifacts quickly.\n"
                        + "3. Click 'Export JSON File' to save your data.\n"
                        + "4. Press 'Save Imported Artifacts' to store imported data.\n\n"
                        + "Frequently Asked Questions (FAQ):\n"
                        + "Q: How do I add new artifacts?\n"
                        + "A: Currently, you can add artifacts by editing the JSON file manually.\n\n"
                        + "Q: How do I delete an artifact?\n"
                        + "A: There is no direct delete option, but you can edit the JSON file and reload it.\n\n"
                        + "Q: Can I edit artifacts in the application?\n"
                        + "A: No, but future updates may include editing capabilities.\n\n"
                        + "Press 'Close' to exit this window.");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> helpStage.close());

        VBox layout = new VBox(10, helpText, closeButton);
        layout.setPadding(new Insets(10));

        Scene helpScene = new Scene(layout, 500, 400);
        helpStage.setScene(helpScene);
        helpStage.show();
    }

    private void showExamples() {
        Stage exampleStage = new Stage();
        exampleStage.setTitle("Example Uses of the Application");

        Image exampleImage = new Image(getClass().getResourceAsStream("/images/image1.png"));
        ImageView imageView = new ImageView(exampleImage);
        imageView.setFitWidth(500);
        imageView.setPreserveRatio(true);

        VBox layout = new VBox(10, imageView);
        Scene scene = new Scene(layout, 600, 400);

        exampleStage.setScene(scene);
        exampleStage.show();
    }

    private void openSupportWebsite() {
        getHostServices().showDocument("https://www.mathworks.com/help/matlab/");
    }

    private void checkForUpdates() {
        displayArea.setText("Checking for updates...");
    }

    private void showAccessibilityOptions() {
        displayArea.setText("Accessibility: Modify settings for better usability.");
    }
    private void showAddArtifactDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Artifact");

        TextField idField = new TextField();
        idField.setPromptText("Artifact ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Artifact Name");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField civilizationField = new TextField();
        civilizationField.setPromptText("Civilization");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            JSONObject newArtifact = new JSONObject();
            newArtifact.put("artifactid", idField.getText());
            newArtifact.put("artifactname", nameField.getText());
            newArtifact.put("category", categoryField.getText());
            newArtifact.put("civilization", civilizationField.getText());

            artifacts.put(newArtifact);
            displayArtifacts(artifacts);
            dialog.close();
        });

        VBox layout = new VBox(10, idField, nameField, categoryField, civilizationField, addButton);
        layout.setPadding(new Insets(10));
        Scene scene = new Scene(layout, 300, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showEditArtifactDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Artifact");
        dialog.setHeaderText("Enter the Artifact ID to Edit:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(artifactId -> {
            JSONObject artifact = findArtifactById(artifactId);
            if (artifact != null) {
                Stage editStage = new Stage();
                editStage.initModality(Modality.APPLICATION_MODAL);
                editStage.setTitle("Edit Artifact");

                TextField nameField = new TextField(artifact.optString("artifactname", ""));
                TextField categoryField = new TextField(artifact.optString("category", ""));
                TextField civilizationField = new TextField(artifact.optString("civilization", ""));

                Button saveButton = new Button("Save");
                saveButton.setOnAction(e -> {
                    artifact.put("artifactname", nameField.getText());
                    artifact.put("category", categoryField.getText());
                    artifact.put("civilization", civilizationField.getText());

                    displayArtifacts(artifacts);
                    editStage.close();
                });

                VBox layout = new VBox(10, nameField, categoryField, civilizationField, saveButton);
                layout.setPadding(new Insets(10));
                Scene scene = new Scene(layout, 300, 250);
                editStage.setScene(scene);
                editStage.show();
            } else {
                showAlert("Artifact Not Found", "No artifact found with ID: " + artifactId);
            }
        });
    }

    private void showDeleteArtifactDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Artifact");
        dialog.setHeaderText("Enter the Artifact ID to Delete:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(artifactId -> {
            int indexToRemove = -1;

            // Silinecek artifactin indeksini bul
            for (int i = 0; i < artifacts.length(); i++) {
                JSONObject artifact = artifacts.getJSONObject(i);
                if (artifact.optString("artifactid").equals(artifactId)) {
                    indexToRemove = i;
                    break;
                }
            }

            // Eğer indeks bulunduysa artifacti sil
            if (indexToRemove != -1) {
                artifacts.remove(indexToRemove);
                displayArtifacts(artifacts);
            } else {
                showAlert("Artifact Not Found", "No artifact found with ID: " + artifactId);
            }
        });
    }

    private JSONObject findArtifactById(String artifactId) {
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.optString("artifactid").equals(artifactId)) {
                return artifact;
            }
        }
        return null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void displayArtifacts(JSONArray artifactsToDisplay) {
        StringBuilder formattedText = new StringBuilder();
        for (int i = 0; i < artifactsToDisplay.length(); i++) {
            JSONObject artifact = artifactsToDisplay.getJSONObject(i);
            formattedText.append("ID: ").append(artifact.optString("artifactid", "N/A")).append("\n");
            formattedText.append("Name: ").append(artifact.optString("artifactname", "N/A")).append("\n");
            formattedText.append("Category: ").append(artifact.optString("category", "N/A")).append("\n");
            formattedText.append("Civilization: ").append(artifact.optString("civilization", "N/A")).append("\n");
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
    public static void main(String[] args) {
        launch(args);
    }
}
