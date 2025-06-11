package com.example.BomberMAN.menu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX pour le menu de Bomberman.
 * <p>
 * Cette classe charge la vue FXML du menu, configure la scène, charge une police personnalisée,
 * et affiche la fenêtre principale.
 */
public class MenuApplication extends Application
{

    /**
     * Point d'entrée JavaFX : configure et affiche la fenêtre principale.
     *
     * @param stage La fenêtre principale fournie par le framework JavaFX.
     * @throws Exception en cas d'erreur lors du chargement du fichier FXML ou des ressources.
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        // Chargement du fichier FXML
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("fxml/Menu-view.fxml"));
        Scene scene;
        // On détecte le mode solo/multi via une variable statique dans MenuController
        boolean isSolo = MenuController.isSoloModeSelected();
        if (isSolo) {
            scene = new Scene(fxmlLoader.load(), 800, 800);
        } else {
            scene = new Scene(fxmlLoader.load(), 1000, 800);
        }

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

    /**
     * Méthode principale du programme.
     * <p>
     * Lance l'application JavaFX.
     *
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
