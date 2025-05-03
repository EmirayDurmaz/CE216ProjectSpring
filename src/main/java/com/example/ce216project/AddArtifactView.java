package com.example.ce216project;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddArtifactView {
    public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add New Artifact");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Artifact Name");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String artifactName = nameInput.getText();
            System.out.println("New Artifact Added: " + artifactName);
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Artifact Name:"), nameInput, saveButton);

        Scene scene = new Scene(layout, 300, 200);
        window.setScene(scene);
        window.showAndWait();
}
}