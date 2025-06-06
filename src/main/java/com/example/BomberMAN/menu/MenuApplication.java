package com.example.BomberMAN.menu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("fxml/Menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        Font.loadFont(MenuApplication.class.getResource("police/Jersey10.ttf").toExternalForm(), 10);
        stage.setTitle("Bomberman - Menu");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}