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

            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Artifact Catalog");
            primaryStage.show();

            loadDefaultArtifacts();

            controller.updateTagList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> existingIds = new HashSet<>();


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
                        JSONObject artifact = fileArtifacts.getJSONObject(i);
                        String id = artifact.optString("artifactid", "").trim();
                        if (id.isEmpty()) {
                            id = generateUniqueArtifactId();
                            artifact.put("artifactid", id);
                        }
                        if (!existingIds.contains(id)) {
                            artifacts.put(artifact);
                            existingIds.add(id);
                        }
                    }
                }
            }
            displayArtifacts(artifacts);
        } catch (Exception e) {
            System.out.println("Error loading default artifacts: " + e.getMessage());
        }
    }

    private int nextArtifactId = 1000;
    private String generateUniqueArtifactId() {
        while (existingIds.contains(String.valueOf(nextArtifactId))) {
            nextArtifactId++;
        }
        return String.valueOf(nextArtifactId++);
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

        VBox form = new VBox(10);
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.TOP_LEFT);
        form.setStyle("-fx-background-color: #F5F5F5;");

        String textFieldStyle = "-fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 8;";

        Label idLabel = new Label("Artifact ID");
        TextField idField = new TextField();
        idField.setEditable(false);
        idField.setText(generateUniqueArtifactId());
        idField.setStyle(textFieldStyle);

        Label nameLabel = new Label("Artifact Name");
        TextField nameField = new TextField();
        nameField.setPromptText("Artifact Name");
        nameField.setStyle(textFieldStyle);

        Label categoryLabel = new Label("Category");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        categoryField.setStyle(textFieldStyle);

        Label civilizationLabel = new Label("Civilization");
        TextField civilizationField = new TextField();
        civilizationField.setPromptText("Civilization");
        civilizationField.setStyle(textFieldStyle);

        Label discoveryLocationLabel = new Label("Discovery Location");
        TextField discoveryLocationField = new TextField();
        discoveryLocationField.setPromptText("Discovery Location");
        discoveryLocationField.setStyle(textFieldStyle);

        Label compositionLabel = new Label("Composition");
        TextField compositionField = new TextField();
        compositionField.setPromptText("Composition");
        compositionField.setStyle(textFieldStyle);

        Label discoveryDateLabel = new Label("Discovery Date");
        DatePicker discoveryDatePicker = new DatePicker();
        discoveryDatePicker.setPromptText("Discovery Date");
        discoveryDatePicker.setStyle(
                "-fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 8; -fx-font-size: 12px; -fx-pref-height: 30px;"
        );

        Label currentPlaceLabel = new Label("Current Place");
        TextField currentPlaceField = new TextField();
        currentPlaceField.setPromptText("Current Place");
        currentPlaceField.setStyle(textFieldStyle);

        Label widthLabel = new Label("Width (cm)");
        TextField widthField = new TextField();
        widthField.setPromptText("Width (cm)");
        widthField.setStyle(textFieldStyle);

        Label lengthLabel = new Label("Length (cm)");
        TextField lengthField = new TextField();
        lengthField.setPromptText("Length (cm)");
        lengthField.setStyle(textFieldStyle);

        Label heightLabel = new Label("Height (cm)");
        TextField heightField = new TextField();
        heightField.setPromptText("Height (cm)");
        heightField.setStyle(textFieldStyle);

        Label weightLabel = new Label("Weight (kg)");
        TextField weightField = new TextField();
        weightField.setPromptText("Weight (kg)");
        weightField.setStyle(textFieldStyle);

        Label tagsLabel = new Label("Tags");
        FlowPane tagsPane = new FlowPane();
        tagsPane.setHgap(5);
        tagsPane.setVgap(5);
        tagsPane.setPrefWrapLength(300);

        TextField newTagField = new TextField();
        newTagField.setPromptText("Add tag and press Enter");
        newTagField.setStyle(textFieldStyle);

        Set<String> currentTags = new HashSet<>();

        newTagField.setOnAction(e -> {
            String newTag = newTagField.getText().trim().toLowerCase();

            if (newTag.isEmpty()) {
                return;
            }

            if (currentTags.contains(newTag)) {
                showAlert("Duplicate Tag", "This tag already exists!");
                newTagField.clear();
                return;
            }

            currentTags.add(newTag);
            Label tagLabel = createTagLabel(newTag, currentTags, tagsPane);
            tagsPane.getChildren().add(tagLabel);

            newTagField.clear();
        });

        Label imagePathLabel = new Label("Image Path");
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
        addButton.setPrefHeight(40);
        addButton.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 10;
        -fx-font-size: 14px;
        -fx-padding: 8 16 8 16;
    """);

        addButton.setOnAction(e -> {

            if (!widthField.getText().trim().isEmpty() && !isNumeric(widthField.getText().trim())) {
                showAlert("Input Error", "Width must be a valid number.");
                return;
            }
            if (!lengthField.getText().trim().isEmpty() && !isNumeric(lengthField.getText().trim())) {
                showAlert("Input Error", "Length must be a valid number.");
                return;
            }
            if (!heightField.getText().trim().isEmpty() && !isNumeric(heightField.getText().trim())) {
                showAlert("Input Error", "Height must be a valid number.");
                return;
            }
            if (!weightField.getText().trim().isEmpty() && !isNumeric(weightField.getText().trim())) {
                showAlert("Input Error", "Weight must be a valid number.");
                return;
            }


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
            newArtifact.put("discoverydate", discoveryDatePicker.getValue() != null ? discoveryDatePicker.getValue().toString() : "Unknown");
            newArtifact.put("currentplace", currentPlaceField.getText().trim().isEmpty() ? "Unknown" : currentPlaceField.getText().trim());

            JSONObject dimensions = new JSONObject();
            dimensions.put("width", widthField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(widthField.getText().trim()));
            dimensions.put("length", lengthField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(lengthField.getText().trim()));
            dimensions.put("height", heightField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(heightField.getText().trim()));

            newArtifact.put("weight", weightField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(weightField.getText().trim()));


            newArtifact.put("weight", weightField.getText().trim().isEmpty() ? 0 : Integer.parseInt(weightField.getText().trim()));

            JSONArray tagsArray = new JSONArray();
            for (String tag : currentTags) {
                tagsArray.put(tag);
            }
            newArtifact.put("tags", tagsArray);

            newArtifact.put("image", imagePathField.getText().trim());

            artifacts.put(newArtifact);
            displayArtifacts(artifacts);

            if (controller != null) {
                controller.updateTagList();
            }

            dialog.close();
        });


        form.getChildren().addAll(
                idLabel, idField,
                nameLabel, nameField,
                categoryLabel, categoryField,
                civilizationLabel, civilizationField,
                discoveryLocationLabel, discoveryLocationField,
                compositionLabel, compositionField,
                discoveryDateLabel, discoveryDatePicker,
                currentPlaceLabel, currentPlaceField,
                widthLabel, widthField,
                lengthLabel, lengthField,
                heightLabel, heightField,
                weightLabel, weightField,
                tagsLabel, newTagField, tagsPane,
                imagePathLabel, imagePathField,
                browseImageButton,
                addButton
        );

        scrollPane.setContent(form);
        Scene scene = new Scene(scrollPane, 420, 600);
        dialog.setScene(scene);
        dialog.show();
    }

    private Label createTagLabel(String tag, Set<String> tagsSet, FlowPane container) {
        Label label = new Label(tag + "  ✕");
        label.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-text-fill: white;
        -fx-padding: 4 8 4 8;
        -fx-background-radius: 10;
        -fx-cursor: hand;
    """);

        label.setOnMouseClicked(event -> {
            tagsSet.remove(tag);
            container.getChildren().remove(label);
        });

        return label;
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

    private void updateAllTags() {
        Set<String> tagsSet = new HashSet<>();

        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                JSONArray tags = artifact.getJSONArray("tags");
                for (int j = 0; j < tags.length(); j++) {
                    tagsSet.add(tags.getString(j).toLowerCase());
                }
            }
        }

        if (controller != null) {
            controller.updateTagsFromSet(tagsSet);
        }
    }


    public void showEditArtifactDialog() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Edit Artifact");
        idDialog.setHeaderText("Enter the Artifact ID to Edit:");
        Optional<String> result = idDialog.showAndWait();

        result.ifPresent(artifactId -> {
            JSONObject artifact = findArtifactById(artifactId);
            if (artifact != null) {
                Stage editStage = new Stage();
                editStage.initModality(Modality.APPLICATION_MODAL);
                editStage.setTitle("Edit Artifact");

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToWidth(true);

                VBox form = new VBox(10);
                form.setPadding(new Insets(25));
                form.setAlignment(Pos.TOP_LEFT);
                form.setStyle("-fx-background-color: #F5F5F5;");

                String textFieldStyle = "-fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 8;";

                Label idLabel = new Label("Artifact ID");
                TextField idField = new TextField(artifact.optString("artifactid", ""));
                idField.setEditable(false);
                idField.setStyle(textFieldStyle);

                Label nameLabel = new Label("Artifact Name");
                TextField nameField = new TextField(artifact.optString("artifactname", ""));
                nameField.setStyle(textFieldStyle);

                Label categoryLabel = new Label("Category");
                ComboBox<String> categoryComboBox = new ComboBox<>();
                categoryComboBox.getItems().addAll("Sculpture", "Manuscript", "Weapon", "Tool", "Jewelry", "Other");
                categoryComboBox.setEditable(false);

                String currentCategory = artifact.optString("category", "");
                TextField customCategoryField = new TextField();
                customCategoryField.setPromptText("Enter custom category");
                customCategoryField.setVisible(false);
                customCategoryField.setStyle(textFieldStyle);

                if (categoryComboBox.getItems().contains(currentCategory)) {
                    categoryComboBox.setValue(currentCategory);
                } else {
                    categoryComboBox.setValue("Other");
                    customCategoryField.setText(currentCategory);
                    customCategoryField.setVisible(true);
                }

                categoryComboBox.setOnAction(e -> {
                    String selected = categoryComboBox.getValue();
                    customCategoryField.setVisible("Other".equals(selected));
                });

                Label civilizationLabel = new Label("Civilization");
                TextField civilizationField = new TextField(artifact.optString("civilization", ""));
                civilizationField.setStyle(textFieldStyle);

                Label discoveryLocationLabel = new Label("Discovery Location");
                TextField discoveryLocationField = new TextField(artifact.optString("discoverylocation", ""));
                discoveryLocationField.setStyle(textFieldStyle);

                Label compositionLabel = new Label("Composition");
                TextField compositionField = new TextField(artifact.optString("composition", ""));
                compositionField.setStyle(textFieldStyle);

                Label discoveryDateLabel = new Label("Discovery Date");
                DatePicker discoveryDatePicker = new DatePicker();
                if (!artifact.optString("discoverydate", "").isEmpty() && !artifact.optString("discoverydate", "Unknown").equals("Unknown")) {
                    try {
                        discoveryDatePicker.setValue(java.time.LocalDate.parse(artifact.optString("discoverydate")));
                    } catch (Exception ignored) {
                    }
                }
                discoveryDatePicker.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 8; -fx-font-size: 12px; -fx-pref-height: 30px;");

                Label currentPlaceLabel = new Label("Current Place");
                TextField currentPlaceField = new TextField(artifact.optString("currentplace", ""));
                currentPlaceField.setStyle(textFieldStyle);

                Label widthLabel = new Label("Width (cm)");
                TextField widthField = new TextField();
                widthField.setText(String.valueOf(artifact.optJSONObject("dimensions") != null ? artifact.optJSONObject("dimensions").optDouble("width", 0.0) : 0.0));
                widthField.setStyle(textFieldStyle);

                Label lengthLabel = new Label("Length (cm)");
                TextField lengthField = new TextField();
                lengthField.setText(String.valueOf(artifact.optJSONObject("dimensions") != null ? artifact.optJSONObject("dimensions").optDouble("length", 0.0) : 0.0));
                lengthField.setStyle(textFieldStyle);

                Label heightLabel = new Label("Height (cm)");
                TextField heightField = new TextField();
                heightField.setText(String.valueOf(artifact.optJSONObject("dimensions") != null ? artifact.optJSONObject("dimensions").optDouble("height", 0.0) : 0.0));
                heightField.setStyle(textFieldStyle);

                Label weightLabel = new Label("Weight (kg)");
                TextField weightField = new TextField(String.valueOf(artifact.optDouble("weight", 0.0)));
                weightField.setStyle(textFieldStyle);

                Label tagsLabel = new Label("Tags");
                FlowPane tagsPane = new FlowPane();
                tagsPane.setHgap(5);
                tagsPane.setVgap(5);
                tagsPane.setPrefWrapLength(300);

                Set<String> currentTags = new HashSet<>();
                if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                    JSONArray tagsArray = artifact.getJSONArray("tags");
                    for (int i = 0; i < tagsArray.length(); i++) {
                        String tag = tagsArray.getString(i);
                        currentTags.add(tag);
                        Label tagLabel = createTagLabel(tag, currentTags, tagsPane);
                        tagsPane.getChildren().add(tagLabel);
                    }
                }

                TextField newTagField = new TextField();
                newTagField.setPromptText("Add tag and press Enter");
                newTagField.setStyle(textFieldStyle);

                newTagField.setOnAction(e -> {
                    String newTag = newTagField.getText().trim().toLowerCase();

                    if (newTag.isEmpty()) {
                        return;
                    }

                    if (currentTags.contains(newTag)) {
                        showAlert("Duplicate Tag", "This tag already exists!");
                        newTagField.clear();
                        return;
                    }

                    currentTags.add(newTag);
                    Label tagLabel = createTagLabel(newTag, currentTags, tagsPane);
                    tagsPane.getChildren().add(tagLabel);

                    newTagField.clear();
                });

                Label imagePathLabel = new Label("Image Path");
                TextField imagePathField = new TextField(artifact.optString("image", ""));
                imagePathField.setEditable(false);
                imagePathField.setStyle(textFieldStyle);

                Button browseImageButton = new Button("Select Image");
                browseImageButton.setOnAction(event -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Choose Image File");
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
                    );
                    File selectedFile = fileChooser.showOpenDialog(editStage);
                    if (selectedFile != null) {
                        imagePathField.setText(selectedFile.toURI().toString());
                    }
                });

                Button saveButton = new Button("Save");
                saveButton.setMaxWidth(Double.MAX_VALUE);
                saveButton.setPrefHeight(40);
                saveButton.setStyle("""
                -fx-background-color: #6C63FF;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 10;
                -fx-font-size: 14px;
                -fx-padding: 8 16 8 16;
            """);

                saveButton.setOnAction(e -> {

                    if (!widthField.getText().trim().isEmpty() && !isNumeric(widthField.getText().trim())) {
                        showAlert("Input Error", "Width must be a valid number.");
                        return;
                    }
                    if (!lengthField.getText().trim().isEmpty() && !isNumeric(lengthField.getText().trim())) {
                        showAlert("Input Error", "Length must be a valid number.");
                        return;
                    }
                    if (!heightField.getText().trim().isEmpty() && !isNumeric(heightField.getText().trim())) {
                        showAlert("Input Error", "Height must be a valid number.");
                        return;
                    }
                    if (!weightField.getText().trim().isEmpty() && !isNumeric(weightField.getText().trim())) {
                        showAlert("Input Error", "Weight must be a valid number.");
                        return;
                    }


                    artifact.put("artifactname", nameField.getText().trim().isEmpty() ? "Unknown" : nameField.getText().trim());

                    String selectedCategory = categoryComboBox.getValue();
                    if ("Other".equals(selectedCategory)) {
                        artifact.put("category", customCategoryField.getText().trim().isEmpty() ? "Unknown" : customCategoryField.getText().trim());
                    } else {
                        artifact.put("category", selectedCategory);
                    }

                    artifact.put("civilization", civilizationField.getText().trim().isEmpty() ? "Unknown" : civilizationField.getText().trim());
                    artifact.put("discoverylocation", discoveryLocationField.getText().trim().isEmpty() ? "Unknown" : discoveryLocationField.getText().trim());
                    artifact.put("composition", compositionField.getText().trim().isEmpty() ? "Unknown" : compositionField.getText().trim());
                    artifact.put("discoverydate", discoveryDatePicker.getValue() != null ? discoveryDatePicker.getValue().toString() : "Unknown");
                    artifact.put("currentplace", currentPlaceField.getText().trim().isEmpty() ? "Unknown" : currentPlaceField.getText().trim());

                    JSONObject dimensions = new JSONObject();
                    dimensions.put("width", widthField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(widthField.getText().trim()));
                    dimensions.put("length", lengthField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(lengthField.getText().trim()));
                    dimensions.put("height", heightField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(heightField.getText().trim()));
                    artifact.put("dimensions", dimensions);

                    artifact.put("weight", weightField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(weightField.getText().trim()));

                    JSONArray tagsArray = new JSONArray();
                    for (String tag : currentTags) {
                        tagsArray.put(tag);
                    }
                    artifact.put("tags", tagsArray);

                    artifact.put("image", imagePathField.getText().trim());

                    updateAllTags();

                    displayArtifacts(artifacts);
                    editStage.close();
                });

                form.getChildren().addAll(
                        idLabel, idField,
                        nameLabel, nameField,
                        categoryLabel, categoryComboBox, customCategoryField,
                        civilizationLabel, civilizationField,
                        discoveryLocationLabel, discoveryLocationField,
                        compositionLabel, compositionField,
                        discoveryDateLabel, discoveryDatePicker,
                        currentPlaceLabel, currentPlaceField,
                        widthLabel, widthField,
                        lengthLabel, lengthField,
                        heightLabel, heightField,
                        weightLabel, weightField,
                        tagsLabel, newTagField, tagsPane,
                        imagePathLabel, imagePathField,
                        browseImageButton,
                        saveButton
                );

                scrollPane.setContent(form);
                Scene scene = new Scene(scrollPane, 420, 700);
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

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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

        Button selectAllFieldsButton = new Button("Select All Fields");
        selectAllFieldsButton.setOnAction(e -> fieldListView.getSelectionModel().selectAll());

        Button selectAllArtifactsButton = new Button("Select All Artifacts");
        selectAllArtifactsButton.setOnAction(e -> artifactListView.getSelectionModel().selectAll());

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

        VBox layout = new VBox(10,
                new Label("Select artifacts to export:"),
                artifactListView,
                selectAllArtifactsButton,
                new Label("Select fields to export:"),
                fieldListView,
                selectAllFieldsButton,
                exportButton
        );
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 550);
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

                JSONArray newArtifacts;
                if (json instanceof JSONObject) {

                    newArtifacts = new JSONArray().put((JSONObject) json);
                } else if (json instanceof JSONArray) {
                    newArtifacts = (JSONArray) json;
                } else {
                    showAlert("Import Error", "Invalid JSON format.");
                    return;
                }

                int addedCount = 0;
                int skippedCount = 0;

                for (int i = 0; i < newArtifacts.length(); i++) {
                    JSONObject artifact = newArtifacts.getJSONObject(i);

                    if (!artifact.has("artifactid") || artifact.getString("artifactid").isBlank()) {
                        artifact.put("artifactid", generateUniqueArtifactId());
                    }

                    String incomingId = artifact.getString("artifactid");
                    if (isArtifactIdExists(incomingId)) {
                        skippedCount++;
                        continue;
                    }

                    artifacts.put(artifact);
                    addedCount++;
                }

                if (skippedCount > 0) {
                    showAlert("Duplicate Skipped", skippedCount + " artifact(s) with duplicate ID were skipped.");
                }

                if (controller != null) {
                    controller.updateTagList();
                }

                displayArtifacts(artifacts);

            } catch (Exception e) {
                showAlert("Import Error", "Failed to import JSON: " + e.getMessage());
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