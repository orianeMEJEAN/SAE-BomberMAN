package com.example.BomberMAN;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage)
    {
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setStyle("-fx-background-color: #2c3e50; -fx-padding: 50px;");

        Button soloButton = new Button("Mode Solo");
        soloButton.setPrefSize(200, 50);
        soloButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #3498db; -fx-text-fill: white;");

        Button multiButton = new Button("Mode Multijoueur");
        multiButton.setPrefSize(200, 50);
        multiButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #e67e22; -fx-text-fill: white;");

        soloButton.setOnAction(e -> {
            Game game = new Game(true); // Solo
            game.start(primaryStage);
        });

        multiButton.setOnAction(e -> {
            Game game = new Game(false); // Multi
            game.start(primaryStage);
        });

        menuLayout.getChildren().addAll(soloButton, multiButton);

        Scene menuScene = new Scene(menuLayout, 800, 600);
        primaryStage.setTitle("BomberMan - Menu Principal");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}