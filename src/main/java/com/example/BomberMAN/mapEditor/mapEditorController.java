package com.example.BomberMAN.mapEditor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mapEditorController {

    @FXML
    private GridPane mapGrid;

    private final int rows = 10;
    private final int cols = 10;

    @FXML
    public void initialize() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Button cell = new Button();
                cell.setPrefSize(40, 40);
                cell.setStyle("-fx-background-color: lightgray;");
                int x = i, y = j;
                cell.setOnAction(e -> toggleCell(cell));
                mapGrid.add(cell, j, i);
            }
        }
    }

    private void toggleCell(Button cell) {
        String current = cell.getStyle();
        if (current.contains("lightgray")) {
            cell.setStyle("-fx-background-color: black;");
        } else {
            cell.setStyle("-fx-background-color: lightgray;");
        }
    }

    @FXML
    public void handleSave() {
        System.out.println("Sauvegarde de la map...");
        // Implémenter la logique d’export de la grille
    }

    @FXML
    public void handleLoad() {
        System.out.println("Chargement d'une map...");
        // Implémenter la logique de chargement
    }

    @FXML
    public void handleReturn() {
        // Fermer la fenêtre de création de map
        Stage stage = (Stage) mapGrid.getScene().getWindow();
        stage.close();
    }
}
