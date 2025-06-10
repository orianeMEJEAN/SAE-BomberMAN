package com.example.BomberMAN.mapEditor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MapEditor {
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/mapEditor.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Éditeur de Map");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
