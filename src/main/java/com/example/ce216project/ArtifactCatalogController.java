package com.example.ce216project;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

public class ArtifactCatalogController {
    private ArtifactCatalogApp app;

    public void setApp(ArtifactCatalogApp app) {
        this.app = app;
    }

    @FXML
    private Button btnImport;

    @FXML
    private TilePane artifactContainer;

    @FXML
    private ListView<String> artifactListView;

    @FXML
    private void handleHome() {
        showMessage("Home clicked! ...");
    }


    @FXML
    private Button btnBackToAll;


    @FXML
    private void handleAdd() {
        if (app != null) {
            app.showAddArtifactDialog();
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

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import JSON File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        Stage stage = (Stage) btnImport.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                Object json = new org.json.JSONTokener(content).nextValue();

                JSONArray newArtifacts = (json instanceof JSONObject)
                        ? new JSONArray().put((JSONObject) json)
                        : (JSONArray) json;

                JSONArray allArtifacts = app.getArtifacts();
                for (int i = 0; i < newArtifacts.length(); i++) {
                    allArtifacts.put(newArtifacts.getJSONObject(i));
                }

                app.displayArtifacts(allArtifacts);

            } catch (Exception e) {
                showMessage("Error: JSON could not be loaded.\n" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showMessage("No file selected.");
        }
    }

    @FXML
    private void handleBackToAll() {
        app.displayArtifacts(app.getArtifacts());
        btnBackToAll.setVisible(false);
    }
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
            app.showBasicHelp();
        }
    }

    @FXML
    private void handleListArtifacts() {
        if (app != null) {
            app.displayArtifacts(app.getArtifacts());
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

                StringBuilder tagsText = new StringBuilder("🏷 Tags: ");
                if (artifact.has("tags") && artifact.get("tags") instanceof JSONArray) {
                    JSONArray tags = artifact.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        tagsText.append(tags.getString(j));
                        if (j != tags.length() - 1) tagsText.append(", ");
                    }
                }
                Label tagsLabel = new Label(tagsText.toString());
                tagsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #607D8B;");
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

                TextFlow cat = createBoldTextLine("Category: ", artifact.optString("category", "N/A"));
                TextFlow civ = createBoldTextLine("Civilization: ", artifact.optString("civilization", "N/A"));
                TextFlow comp = createBoldTextLine("Composition: ", artifact.optString("composition", "Unknown"));
                TextFlow date = createBoldTextLine("Discovery Date: ", artifact.optString("discoverydate", "Unknown"));
                TextFlow place = createBoldTextLine("Current Place: ", artifact.optString("currentplace", "Unknown"));


                extraInfoBox.getChildren().addAll(cat, civ, comp, date, place);

                detailButton.setOnAction(e -> {
                    extraInfoBox.setVisible(!extraInfoBox.isVisible());
                });

                VBox infoBox = new VBox(name, location, tagsLabel, detailButton, extraInfoBox);
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
    private void handleFilterByTag() {
        showTagFilterDialog();
    }

    private void filterArtifactsByTag(String targetTag) {
        JSONArray filtered = new JSONArray();
        JSONArray allArtifacts = app.getArtifacts();

        for (int i = 0; i < allArtifacts.length(); i++) {
            JSONObject artifact = allArtifacts.getJSONObject(i);
            if (artifact.has("tags")) {
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

    private void showTagFilterDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Filter by Tag");
        dialog.initModality(Modality.APPLICATION_MODAL);

        Label instruction = new Label("Enter the tag to filter artifacts:");
        instruction.setStyle("-fx-font-size: 14px; -fx-text-fill: #37474F;");

        TextField tagField = new TextField();
        tagField.setPromptText("e.g. Ancient, War, Pottery");

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10, okButton, cancelButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-alignment: center;");

        VBox layout = new VBox(15, instruction, tagField, buttonBox);
        layout.setPadding(new Insets(20));
        layout.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 12;
        -fx-border-color: #B0BEC5;
        -fx-border-radius: 12;
        -fx-border-width: 1;
    """);

        Scene scene = new Scene(layout, 320, 180);
        dialog.setScene(scene);

        okButton.setOnAction(e -> {
            String tag = tagField.getText().trim().toLowerCase();
            if (!tag.isEmpty()) {
                filterArtifactsByTag(tag);
                dialog.close();
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        dialog.showAndWait();
    }
}