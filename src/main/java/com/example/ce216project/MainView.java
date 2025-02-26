package com.example.ce216project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView extends Application {
    @Override
    public void start(Stage primaryStage) {
        TableView<String> tableView = new TableView<>();

        Button addButton = new Button("Yeni Eser Ekle");
        addButton.setOnAction(e -> {
            // Yeni eser ekleme ekranı açılacak
            AddArtifactView.display();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(tableView, addButton);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setTitle("Tarihi Eser Kataloğu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

