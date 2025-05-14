package com.example.ce216project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private ListView<String> tagListView = new ListView<>();


    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce216project/mainui.fxml"));
            Parent root = loader.load();

            ArtifactCatalogController controller = loader.getController();
            controller.setApp(this);
            setController(controller);

            Scene scene = new Scene(root, 900, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Artifact Catalog");
            primaryStage.show();

            loadDefaultArtifacts();

            controller.updateTagList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDefaultArtifacts() {
        String[] defaultFiles = {
                "src/main/resources/artifacts1.json",
                "src/main/resources/artifacts2.json",
                "src/main/resources/artifacts3.json"
        };

        try {
            for (String path : defaultFiles) {
                File file = new File(path);
                if (file.exists()) {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    JSONArray fileArtifacts = new JSONArray(content);
                    for (int i = 0; i < fileArtifacts.length(); i++) {
                        artifacts.put(fileArtifacts.getJSONObject(i));
                    }
                } else {
                    System.out.println("File not found: " + path);
                }
            }

            displayArtifacts(artifacts);
        } catch (Exception e) {
            System.out.println("Error loading default artifacts: " + e.getMessage());
        }
    }

    void showBasicHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("How to Use the Software");
        alert.setContentText("1. Import artifacts using 'Import JSON File'.\n"
                + "2. Search for artifacts using the search bar.\n"
                + "3. Save or export artifacts as needed.\n"
                + "4. For further assistance, refer to the documentation.");
        alert.showAndWait();
    }


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
        Stage dialog = new Stage();
        dialog.setTitle("Add Artifact");
        dialog.initModality(Modality.APPLICATION_MODAL);

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
    public void showAddArtifactDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Artifact");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.CENTER);
        form.setStyle("""
            -fx-background-color: #F5F5F5;
        """);

        String textFieldStyle = "-fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 8;";

        TextField idField = new TextField(); idField.setPromptText("Artifact ID * (Required)"); idField.setStyle(textFieldStyle);
        TextField nameField = new TextField(); nameField.setPromptText("Artifact Name"); nameField.setStyle(textFieldStyle);
        TextField categoryField = new TextField(); categoryField.setPromptText("Category"); categoryField.setStyle(textFieldStyle);
        TextField civilizationField = new TextField(); civilizationField.setPromptText("Civilization"); civilizationField.setStyle(textFieldStyle);
        TextField discoveryLocationField = new TextField(); discoveryLocationField.setPromptText("Discovery Location"); discoveryLocationField.setStyle(textFieldStyle);
        TextField compositionField = new TextField(); compositionField.setPromptText("Composition"); compositionField.setStyle(textFieldStyle);
        DatePicker discoveryDatePicker = new DatePicker();
        discoveryDatePicker.setPromptText("Discovery Date");
        discoveryDatePicker.setStyle("""
         -fx-background-radius: 10;
        -fx-border-radius: 10;
        -fx-padding: 8;
        -fx-font-size: 12px;
        -fx-pref-height: 30px;
        """);


        TextField currentPlaceField = new TextField(); currentPlaceField.setPromptText("Current Place"); currentPlaceField.setStyle(textFieldStyle);
        TextField widthField = new TextField(); widthField.setPromptText("Width (cm)"); widthField.setStyle(textFieldStyle);
        TextField lengthField = new TextField(); lengthField.setPromptText("Length (cm)"); lengthField.setStyle(textFieldStyle);
        TextField heightField = new TextField(); heightField.setPromptText("Height (cm)"); heightField.setStyle(textFieldStyle);
        TextField weightField = new TextField(); weightField.setPromptText("Weight (kg)"); weightField.setStyle(textFieldStyle);
        TextField tagsField = new TextField(); tagsField.setPromptText("Tags (comma separated)"); tagsField.setStyle(textFieldStyle);
        TextField imagePathField = new TextField();
        imagePathField.setPromptText("Image Path");
        imagePathField.setStyle(textFieldStyle);
        imagePathField.setEditable(false);

        Button browseImageButton = new Button("Select Image");
        browseImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog);
            if (selectedFile != null) {
                imagePathField.setText(selectedFile.toURI().toString());
            }
        });
        Button addButton = new Button("➕ Add Artifact");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");

        addButton.setOnAction(e -> {
            String artifactId = idField.getText().trim();

            if (artifactId.isEmpty()) {
                showAlert("Error", "Artifact ID is required!");
                return;
            }

            if (isArtifactIdExists(artifactId)) {
                showAlert("Error", "An artifact with this ID already exists!");
                return;
            }

            JSONObject newArtifact = new JSONObject();
            newArtifact.put("artifactid", artifactId);
            newArtifact.put("artifactname", nameField.getText().trim().isEmpty() ? "Unknown" : nameField.getText().trim());
            newArtifact.put("category", categoryField.getText().trim().isEmpty() ? "Unknown" : categoryField.getText().trim());
            newArtifact.put("civilization", civilizationField.getText().trim().isEmpty() ? "Unknown" : civilizationField.getText().trim());
            newArtifact.put("discoverylocation", discoveryLocationField.getText().trim().isEmpty() ? "Unknown" : discoveryLocationField.getText().trim());
            newArtifact.put("composition", compositionField.getText().trim().isEmpty() ? "Unknown" : compositionField.getText().trim());
            newArtifact.put("discoverydate",
                    discoveryDatePicker.getValue() != null ? discoveryDatePicker.getValue().toString() : "Unknown");

            newArtifact.put("currentplace", currentPlaceField.getText().trim().isEmpty() ? "Unknown" : currentPlaceField.getText().trim());
            newArtifact.put("image", imagePathField.getText().trim());

            JSONObject dimensions = new JSONObject();
            dimensions.put("width", widthField.getText().trim().isEmpty() ? 0 : Integer.parseInt(widthField.getText().trim()));
            dimensions.put("length", lengthField.getText().trim().isEmpty() ? 0 : Integer.parseInt(lengthField.getText().trim()));
            dimensions.put("height", heightField.getText().trim().isEmpty() ? 0 : Integer.parseInt(heightField.getText().trim()));
            newArtifact.put("dimensions", dimensions);

            newArtifact.put("weight", weightField.getText().trim().isEmpty() ? 0 : Integer.parseInt(weightField.getText().trim()));

            JSONArray tagsArray = new JSONArray();
            String tagsInput = tagsField.getText().trim();
            if (!tagsInput.isEmpty()) {
                String[] tags = tagsInput.split(",");
                for (String tag : tags) {
                    tagsArray.put(tag.trim());
                }
            }
            newArtifact.put("tags", tagsArray);

            artifacts.put(newArtifact);
            displayArtifacts(artifacts);
            dialog.close();
        });

        form.getChildren().addAll(
                idField, nameField, categoryField, civilizationField, discoveryLocationField,
                compositionField, discoveryDatePicker, currentPlaceField,
                widthField, lengthField, heightField, weightField, tagsField,
                imagePathField, browseImageButton, addButton
        );


        scrollPane.setContent(form);
        Scene scene = new Scene(scrollPane, 400, 500);
        dialog.setScene(scene);
        dialog.show();
    }
    private boolean isArtifactIdExists(String artifactId) {
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.optString("artifactid").equalsIgnoreCase(artifactId)) {
                return true;
            }
        }
        return false;
    }

    public void showEditArtifactDialog() {
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

    public void showDeleteArtifactDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Artifact");
        dialog.setHeaderText("Enter the Artifact ID to Delete:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(artifactId -> {
            int indexToRemove = -1;


            for (int i = 0; i < artifacts.length(); i++) {
                JSONObject artifact = artifacts.getJSONObject(i);
                if (artifact.optString("artifactid").equals(artifactId)) {
                    indexToRemove = i;
                    break;
                }
            }

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

    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void searchArtifactsByName(String name) {
        JSONArray filteredArtifacts = new JSONArray();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.optString("artifactname", "").toLowerCase().contains(name.toLowerCase())) {
                filteredArtifacts.put(artifact);
            }
        }
        displayArtifacts(filteredArtifacts);
    }

    private void searchArtifactsById(String id) {
        JSONArray filteredArtifacts = new JSONArray();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.optString("artifactid", "").equalsIgnoreCase(id)) {
                filteredArtifacts.put(artifact);
            }
        }
        displayArtifacts(filteredArtifacts);
    }
    private ArtifactCatalogController controller;

    public void setController(ArtifactCatalogController controller) {
        this.controller = controller;
    }

    public void displayArtifacts(JSONArray artifacts) {
        if (controller != null) {
            controller.displayArtifacts(artifacts);
        } else {
            System.out.println("No controller connection!");
        }
    }


    public void exportJsonFile(Stage stage) {
        if (artifacts.isEmpty()) {
            displayArea.setText("No data to export.");
            return;
        }

        Stage selectionStage = new Stage();
        selectionStage.initModality(Modality.APPLICATION_MODAL);
        selectionStage.setTitle("Select Artifacts and Fields to Export");

        ListView<String> artifactListView = new ListView<>();
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            String artifactId = artifact.optString("artifactid", "No ID");
            String artifactName = artifact.optString("artifactname", "Unnamed Artifact");
            artifactListView.getItems().add(artifactId + " - " + artifactName);
        }
        artifactListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        String[] possibleFields = {"artifactid", "artifactname", "category", "civilization", "discoverylocation", "composition", "discoverydate", "currentplace", "dimensions", "weight", "tags"};
        ListView<String> fieldListView = new ListView<>();
        fieldListView.getItems().addAll(possibleFields);
        fieldListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        Button selectAllButton = new Button("Select All");
        selectAllButton.setOnAction(e -> fieldListView.getSelectionModel().selectAll());

        Button exportButton = new Button("Export Selected");
        exportButton.setOnAction(e -> {
            ObservableList<String> selectedArtifacts = artifactListView.getSelectionModel().getSelectedItems();
            ObservableList<String> selectedFields = fieldListView.getSelectionModel().getSelectedItems();

            if (selectedArtifacts.isEmpty() || selectedFields.isEmpty()) {
                showAlert("Export Error", "Please select at least one artifact and one field to export.");
                return;
            }
            selectionStage.close();
            saveJsonWithSelectedArtifactsAndFields(stage, selectedArtifacts, selectedFields);
        });

        VBox layout = new VBox(10, new Label("Select artifacts to export:"), artifactListView,
                new Label("Select fields to export:"), fieldListView, selectAllButton, exportButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 500);
        selectionStage.setScene(scene);
        selectionStage.show();
    }

    public void saveJsonWithSelectedArtifactsAndFields(Stage stage, ObservableList<String> selectedArtifacts, ObservableList<String> selectedFields) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            JSONArray filteredArtifacts = new JSONArray();

            for (int i = 0; i < artifacts.length(); i++) {
                JSONObject artifact = artifacts.getJSONObject(i);
                String artifactIdentifier = artifact.optString("artifactid", "No ID") + " - " + artifact.optString("artifactname", "Unnamed Artifact");
                if (selectedArtifacts.contains(artifactIdentifier)) {
                    JSONObject filteredArtifact = new JSONObject();

                    for (String field : selectedFields) {
                        if (artifact.has(field)) {
                            filteredArtifact.put(field, artifact.get(field));
                        }
                    }
                    filteredArtifacts.put(filteredArtifact);
                }
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(filteredArtifacts.toString(4));
                displayArea.setText("JSON file exported successfully to " + file.getAbsolutePath());
            } catch (IOException e) {
                displayArea.setText("Error exporting file: " + e.getMessage());
            }
        }
    }

    public JSONArray getArtifacts() {
        return artifacts;
    }
    private void searchArtifacts(String query) {
        JSONArray filteredArtifacts = new JSONArray();
        String lowerCaseQuery = query.toLowerCase();

        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);

            if (artifact.toString().toLowerCase().contains(lowerCaseQuery)) {
                filteredArtifacts.put(artifact);
            }
        }
        displayArtifacts(filteredArtifacts);
    }
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

                Set<String> artifactTags = new HashSet<>();
                for (int j = 0; j < tagsArray.length(); j++) {
                    artifactTags.add(tagsArray.getString(j).toLowerCase());
                }

                for (String selectedTag : selectedTags) {
                    if (artifactTags.contains(selectedTag.toLowerCase())) {
                        filteredArtifacts.put(artifact);
                        break;
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

        try (FileWriter file = new FileWriter("artifacts1.json")) {
            file.write(artifacts.toString(4));
            displayArea.appendText("\nImported artifacts saved successfully!");
        } catch (IOException e) {
            displayArea.setText("Error saving artifacts: " + e.getMessage());
        }
    }

    public void importJsonFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                Object json = new org.json.JSONTokener(content).nextValue();
                JSONArray newArtifacts = json instanceof JSONObject ? new JSONArray().put((JSONObject) json) : (JSONArray) json;

                System.out.println("Imported JSON Data: " + newArtifacts.toString(4));

                for (int i = 0; i < newArtifacts.length(); i++) {
                    artifacts.put(newArtifacts.getJSONObject(i));
                }


                displayArtifacts(artifacts);

            } catch (Exception e) {
                System.out.println("Error loading artifacts: " + e.getMessage());
            }
        }
    }

    private void updateTagListView() {
        Set<String> uniqueTags = new HashSet<>();

        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                JSONArray tagsArray = artifact.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    uniqueTags.add(tagsArray.getString(j));
                }
            }
        }

        System.out.println("Updated Available Tags: " + uniqueTags);

        Platform.runLater(() -> {
            tagListView.getItems().clear();
            if (uniqueTags.isEmpty()) {
                tagListView.getItems().add("No tags available");
            } else {
                ObservableList<String> tagItems = javafx.collections.FXCollections.observableArrayList(uniqueTags);
                tagListView.setItems(tagItems);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}