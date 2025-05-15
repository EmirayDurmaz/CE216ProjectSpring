package com.example.ce216project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

public class ArtifactCatalogController {
    private ArtifactCatalogApp app;

    public void setApp(ArtifactCatalogApp app) {
        this.app = app;
        updateTagList();
    }

    @FXML
    private Button btnImport;

    @FXML
    private TilePane artifactContainer;

    @FXML
    private ComboBox<String> tagFilterComboBox;

    @FXML
    private Button btnBackToAll;


    private ObservableList<String> allTags = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tagFilterComboBox.setItems(allTags);

        tagFilterComboBox.setOnAction(e -> {
            String selectedTag = tagFilterComboBox.getSelectionModel().getSelectedItem();
            if (selectedTag == null || selectedTag.isEmpty()) {
                if (app != null) {
                    app.displayArtifacts(app.getArtifacts());
                }
                btnBackToAll.setVisible(false);
            } else {
                filterArtifactsByTag(selectedTag.toLowerCase());
                btnBackToAll.setVisible(true);
            }
        });

        btnBackToAll.setVisible(false);
    }

    public void updateTagList() {
        if (app == null) return;

        allTags.clear();

        JSONArray artifacts = app.getArtifacts();
        Set<String> tagsSet = new HashSet<>();

        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                JSONArray tagsArray = artifact.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    String tag = tagsArray.getString(j).toLowerCase();
                    tagsSet.add(tag);
                }
            }
        }

        List<String> sortedTags = new ArrayList<>(tagsSet);
        Collections.sort(sortedTags);

        allTags.addAll(sortedTags);

        System.out.println("Loaded tags: " + allTags);
    }

    public void updateTagsFromSet(Set<String> tagsSet) {

        Platform.runLater(() -> {
            allTags.clear();
            List<String> sortedTags = new ArrayList<>(tagsSet);
            Collections.sort(sortedTags);
            allTags.addAll(sortedTags);

        });
    }


    private void filterArtifactsByTag(String targetTag) {
        if (app == null) return;

        JSONArray filtered = new JSONArray();
        JSONArray allArtifacts = app.getArtifacts();

        for (int i = 0; i < allArtifacts.length(); i++) {
            JSONObject artifact = allArtifacts.getJSONObject(i);
            if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                JSONArray tags = artifact.getJSONArray("tags");
                for (int j = 0; j < tags.length(); j++) {
                    String tag = tags.getString(j).toLowerCase();
                    if (tag.equals(targetTag)) {
                        filtered.put(artifact);
                        break;
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            showMessage("No artifacts found with tag: " + targetTag);
        }

        app.displayArtifacts(filtered);
    }

    @FXML
    private void handleBackToAll() {
        if (app != null) {
            app.displayArtifacts(app.getArtifacts());
            btnBackToAll.setVisible(false);
            tagFilterComboBox.getSelectionModel().clearSelection();
        }
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void displayArtifacts(JSONArray artifacts) {
        Platform.runLater(() -> {
            artifactContainer.getChildren().clear();

            for (int i = 0; i < artifacts.length(); i++) {
                JSONObject artifact = artifacts.getJSONObject(i);

                VBox card = new VBox();
                card.setPrefWidth(260);
                card.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 12;
                    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.3, 0, 2);
                """);

                String imagePath = artifact.optString("image", "file:images/artifact_default.jpg");
                if (!imagePath.startsWith("file:") && !imagePath.startsWith("http")) {
                    imagePath = "file:" + imagePath;
                }

                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(imagePath));
                } catch (Exception e) {
                    imageView.setImage(new Image("file:images/artifact_default.jpg"));
                }

                imageView.setFitWidth(260);
                imageView.setFitHeight(180);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);

                imageView.setStyle("-fx-alignment: center;");

                Label name = new Label("🏺 " + artifact.optString("artifactname", "N/A"));
                name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #263238;");

                Label location = new Label("📍 " + artifact.optString("discoverylocation", "Unknown"));
                location.setStyle("-fx-font-size: 13px; -fx-text-fill: #455A64;");

                Label idLabel = new Label("ID: " + artifact.optString("artifactid", "N/A"));
                idLabel.setStyle("""
                    -fx-font-size: 10px;
                    -fx-background-color: #ECEFF1;
                    -fx-text-fill: #37474F;
                    -fx-padding: 2 6 2 6;
                    -fx-background-radius: 6;
                    -fx-alignment: center-right;
                """);

                StringBuilder tagsText = new StringBuilder("🏷 Tags: ");
                if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                    JSONArray tags = artifact.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        tagsText.append(tags.getString(j));
                        if (j != tags.length() - 1) tagsText.append(", ");
                    }
                }
                Label tagsLabel = new Label(tagsText.toString());
                tagsLabel.setStyle("""
                    -fx-font-size: 13px;
                    -fx-font-weight: bold;
                    -fx-text-fill: #37474F;
                """);
                tagsLabel.setWrapText(true);

                Button detailButton = new Button("Details");
                detailButton.setStyle("""
                    -fx-background-color: #6C63FF;
                    -fx-text-fill: white;
                    -fx-font-size: 11px;
                    -fx-padding: 2 6 2 6;
                    -fx-background-radius: 4;
                    -fx-border-radius: 4;
                    -fx-effect: none;
                """);

                VBox extraInfoBox = new VBox();
                extraInfoBox.setSpacing(4);
                extraInfoBox.setPadding(new Insets(8, 0, 0, 0));
                extraInfoBox.setVisible(false);
                extraInfoBox.managedProperty().bind(extraInfoBox.visibleProperty());


                TextFlow cat = createBoldTextLine("Category: ", artifact.optString("category", "N/A"));
                TextFlow civ = createBoldTextLine("Civilization: ", artifact.optString("civilization", "N/A"));
                TextFlow comp = createBoldTextLine("Composition: ", artifact.optString("composition", "Unknown"));
                TextFlow date = createBoldTextLine("Discovery Date: ", artifact.optString("discoverydate", "Unknown"));
                TextFlow place = createBoldTextLine("Current Place: ", artifact.optString("currentplace", "Unknown"));


                JSONObject dimensions = artifact.optJSONObject("dimensions");
                String dimValues;
                if (dimensions != null) {
                    dimValues = String.format("Width %.2f cm, Length %.2f cm, Height %.2f cm",
                            dimensions.optDouble("width", 0.0),
                            dimensions.optDouble("length", 0.0),
                            dimensions.optDouble("height", 0.0));
                } else {
                    dimValues = "N/A";
                }
                TextFlow dimensionsFlow = createBoldTextLine("Dimensions: ", dimValues);


                double weight = artifact.optDouble("weight", 0.0);
                TextFlow weightFlow = createBoldTextLine("Weight: ", String.format("%.2f kg", weight));


                extraInfoBox.getChildren().addAll(cat, civ, comp, date, place, dimensionsFlow, weightFlow);
                ;

                detailButton.setOnAction(e -> extraInfoBox.setVisible(!extraInfoBox.isVisible()));

                VBox infoBox = new VBox(name, idLabel, location, tagsLabel, detailButton, extraInfoBox);
                infoBox.setSpacing(4);
                infoBox.setPadding(new Insets(10));

                card.getChildren().addAll(imageView, infoBox);
                artifactContainer.getChildren().add(card);
            }
        });
    }

    private TextFlow createBoldTextLine(String label, String value) {
        Text labelBold = new Text(label);
        labelBold.setStyle("-fx-font-weight: bold; -fx-fill: #263238;");

        Text labelValue = new Text(value);
        labelValue.setStyle("-fx-fill: #455A64;");

        return new TextFlow(labelBold, labelValue);
    }



    @FXML
    private void handleAdd() {
        if (app != null) {
            app.showAddArtifactDialog();
            updateTagList();
        } else {
            showMessage("There is no app connection.");
        }
    }

    @FXML
    private void handleEdit() {
        if (app != null) {
            app.showEditArtifactDialog();
        } else {
            showMessage("There is no app connection.");
        }
    }

    @FXML
    private void handleDelete() {
        if (app != null) {
            app.showDeleteArtifactDialog();
        } else {
            showMessage("There is no app connection.");
        }
    }

    @FXML
    private void handleSearch() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Artifacts");
        dialog.setHeaderText("Search all fields (e.g., category, civilization, location)");
        dialog.setContentText("Enter search term:");

        dialog.showAndWait().ifPresent(query -> {
            String lowerQuery = query.toLowerCase();
            JSONArray filtered = new JSONArray();

            for (int i = 0; i < app.getArtifacts().length(); i++) {
                JSONObject artifact = app.getArtifacts().getJSONObject(i);
                if (artifact.toString().toLowerCase().contains(lowerQuery)) {
                    filtered.put(artifact);
                }
            }

            if (filtered.isEmpty()) {
                showMessage("No artifacts found for: " + query);
            } else {
                app.displayArtifacts(filtered);
                btnBackToAll.setVisible(true);
            }
        });
    }

    @FXML
    private void handleImport() {
        if (app == null) {
            showMessage("There is no app connection.");
            return;
        }

        Stage stage = (Stage) btnImport.getScene().getWindow();
        app.importJsonFile(stage);
    }

    /*@FXML
    private void handleBackToAll() {
        app.displayArtifacts(app.getArtifacts());
        btnBackToAll.setVisible(false);
    }
    */
    @FXML
    private void handleExport() {
        if (app != null) {
            Stage stage = (Stage) btnImport.getScene().getWindow();
            app.exportJsonFile(stage);
        } else {
            showMessage("There is no app connection!.");
        }
    }


    @FXML
    private void handleHelp() {
        if (app != null) {
            app.showHelpCenter();
        }

    }

    @FXML
    private void handleListArtifacts() {
        if (app != null) {
            app.displayArtifacts(app.getArtifacts());
        }
    }



    @FXML
    private void handleFilterByTag() {
        showTagFilterDialog();
    }



    private void showTagFilterDialog() {
        if (app == null) return;

        Dialog<ObservableList<String>> dialog = new Dialog<>();
        dialog.setTitle("Filter by Tags");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        VBox checkBoxContainer = new VBox(8);
        checkBoxContainer.setPadding(new Insets(10));

        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String tag : allTags) {
            CheckBox checkBox = new CheckBox(tag);
            checkBoxes.add(checkBox);
            checkBoxContainer.getChildren().add(checkBox);
        }

        ScrollPane scrollPane = new ScrollPane(checkBoxContainer);
        scrollPane.setPrefSize(250, 300);
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(10, new Label("Select tags to filter artifacts:"), scrollPane);
        content.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                ObservableList<String> selectedTags = FXCollections.observableArrayList();
                for (CheckBox cb : checkBoxes) {
                    if (cb.isSelected()) {
                        selectedTags.add(cb.getText());
                    }
                }
                return selectedTags;
            }
            return null;
        });

        Optional<ObservableList<String>> result = dialog.showAndWait();

        result.ifPresent(selectedTags -> {
            if (!selectedTags.isEmpty()) {
                filterArtifactsByTags(selectedTags);
                btnBackToAll.setVisible(true);
            }
        });
        btnBackToAll.setVisible(true);
        btnBackToAll.setText("🔙 Show All Artifacts");
        btnBackToAll.setStyle("""
    -fx-background-color: #ECEFF1;
    -fx-text-fill: #263238;
    -fx-font-weight: bold;
    -fx-background-radius: 8;
    -fx-border-color: #B0BEC5;
    -fx-border-radius: 8;
""");
        btnBackToAll.setVisible(true);


    }

    private void filterArtifactsByTags(ObservableList<String> selectedTags) {
        if (app == null) return;

        if (selectedTags.isEmpty()) {
            app.displayArtifacts(app.getArtifacts());
            return;
        }

        JSONArray filteredArtifacts = new JSONArray();
        JSONArray allArtifacts = app.getArtifacts();

        for (int i = 0; i < allArtifacts.length(); i++) {
            JSONObject artifact = allArtifacts.getJSONObject(i);

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

        if (filteredArtifacts.isEmpty()) {
            showMessage("No artifacts found with selected tags.");
        }

        app.displayArtifacts(filteredArtifacts);
    }



}