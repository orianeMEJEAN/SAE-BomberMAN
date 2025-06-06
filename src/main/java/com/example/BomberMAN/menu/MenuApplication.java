package com.example.BomberMAN.menu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MenuApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Chargement du fichier FXML
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("fxml/Menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        // Charger la police personnalisée (si utilisée dans le CSS ou les labels)
        Font.loadFont(MenuApplication.class.getResource("police/Jersey10.ttf").toExternalForm(), 10);

        // Récupérer le contrôleur et lui transmettre la fenêtre principale
        MenuController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);

        // Configuration de la fenêtre
        stage.setTitle("Bomberman - Menu");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}