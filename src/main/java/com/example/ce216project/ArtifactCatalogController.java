package com.example.ce216project;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.scene.control.Label;

public class ArtifactCatalogController {
    private ArtifactCatalogApp app;

    public void setApp(ArtifactCatalogApp app) {
        this.app = app;
    }
    @FXML
    private Button btnImport;

    @FXML
    private void handleHome() {
        showMessage("Home clicked! Ana ekrana dönülüyor...");
    }

    @FXML
    private void handleAdd() {
        showMessage("Add Artifact clicked! Yeni eser ekleme ekranı açılıyor...");
    }

    @FXML
    private void handleEdit() {
        showMessage("Edit Artifact clicked! Düzenleme ekranı açılıyor...");
    }

    @FXML
    private void handleDelete() {
        showMessage("Delete Artifact clicked! Seçili eser siliniyor...");
    }

    @FXML
    private TextArea displayArea;

    @FXML
    private void handleImport() {
        if (app != null) {
            Stage stage = (Stage) displayArea.getScene().getWindow();
            app.importJsonFile(stage);
            app.displayArtifacts(app.getArtifacts());
        } else {
            System.out.println("ArtifactCatalogApp bağlantısı yok!");
        }
    }


    @FXML
    private void handleExport() {
        showMessage("Export JSON clicked! JSON dosyası dışa aktarılıyor...");
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private ListView<String> artifactListView;

    @FXML
    private VBox artifactContainer;

    public void displayArtifacts(JSONArray artifacts) {
        Platform.runLater(() -> {
            artifactContainer.getChildren().clear();

            for (int i = 0; i < artifacts.length(); i++) {
                JSONObject artifact = artifacts.getJSONObject(i);


                Label title = new Label("🆔 " + artifact.optString("artifactid", "N/A") +
                        " - " + artifact.optString("artifactname", "N/A"));
                title.equals("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");


                Label details = new Label(
                        "📂 Category: " + artifact.optString("category", "N/A") +
                                "\n🏛 Civilization: " + artifact.optString("civilization", "N/A") +
                                "\n📍 Discovery Location: " + artifact.optString("discoverylocation", "Unknown") +
                                "\n🧪 Composition: " + artifact.optString("composition", "Unknown") +
                                "\n📅 Discovery Date: " + artifact.optString("discoverydate", "Unknown") +
                                "\n🏛 Current Place: " + artifact.optString("currentplace", "Unknown"));
                details.equals("-fx-font-size: 12px; -fx-text-fill: #555;");


                VBox card = new VBox(5, title, details);
                card.setPadding(new Insets(15));
                card.setStyle("-fx-background-color: #ffffff; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-border-color: #d1d1d1; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

                artifactContainer.getChildren().add(card);
            }
        });
    }

    @FXML
    private void handleHelp() {
        app.showBasicHelp();
    }

}
