package com.example.ce216project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelloController {

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    public void initialize() {
        helpMenuItem.setOnAction(event -> showHelpManual());
    }

    private void showHelpManual() {
        String manualText = loadManualText();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("User Manual");
        alert.setHeaderText("How to Use the Software");
        alert.setContentText(manualText);
        alert.showAndWait();
    }

    private String loadManualText() {
        try {
            return new String(Files.readAllBytes(Paths.get("manual.txt")));
        } catch (IOException e) {
            return "User manual could not be loaded.";
        }
    }
    @FXML
    private Label welcomeText;


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}