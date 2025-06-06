package com.example.BomberMAN.menu;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.List;

public class MenuController {
    @FXML
    private StackPane rootPane;

    @FXML
    private Label continu;

    private MediaPlayer mediaPlayer;

    @FXML
    private StackPane popupPane;

    private boolean popupShown = false;

    @FXML private Button btnNP;
    @FXML private Button btnOp;
    @FXML private Button btnQ;

    private List<Button> menuButtons;
    private int selectedIndex = 0;

    @FXML
    public void initialize() {
        // Ajouter un écouteur d'événement clavier au root
        rootPane.setOnKeyPressed(this::handleKeyPress);

        // Important : demander le focus pour capter les touches
        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> rootPane.requestFocus());
        rootPane.requestFocus();


        // Création de l'animation de fade
        FadeTransition ft = new FadeTransition(Duration.seconds(0.7), continu);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setCycleCount(FadeTransition.INDEFINITE); // animation en boucle
        ft.setAutoReverse(true);
        ft.play();

        Media media = new Media(getClass().getResource("sound/MenuPrincipal.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        menuButtons = List.of(btnNP, btnOp, btnQ);
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case ESCAPE:
                if (popupShown) {
                    hidePopup();
                    popupShown = false;
                }
                break;

            case UP:
                if (popupShown) {
                    selectedIndex = (selectedIndex - 1 + menuButtons.size()) % menuButtons.size();
                    updateFocus();
                }
                break;

            case DOWN:
                if (popupShown) {
                    selectedIndex = (selectedIndex + 1) % menuButtons.size();
                    updateFocus();
                }
                break;

            case ENTER:
                if (popupShown) {
                    executeSelected();
                }
                break;

            default:
                if (!popupShown) {
                    showPopup();
                    popupShown = true;
                    updateFocus();
                }
                break;
        }
    }

    private void showPopup() {
        popupPane.setVisible(true);
        popupPane.setTranslateY(600);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), popupPane);
        tt.setFromY(600);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), popupPane);
        ft.setFromValue(0);
        ft.setToValue(1);

        new ParallelTransition(tt, ft).play();

        Platform.runLater(() -> rootPane.requestFocus());
    }

    private void hidePopup() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), popupPane);
        tt.setFromY(0);
        tt.setToY(600);
        tt.setInterpolator(Interpolator.EASE_IN);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), popupPane);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> {
            popupPane.setVisible(false);
            Platform.runLater(() -> rootPane.requestFocus()); // ⬅️ Ajouté ici
        });
        pt.play();
    }

    private void updateFocus() {
        for (int i = 0; i < menuButtons.size(); i++) {
            Button btn = menuButtons.get(i);
            btn.setStyle(""); // reset style
        }
        Button selected = menuButtons.get(selectedIndex);
        selected.setStyle("-fx-border-color: white; -fx-border-width: 2;");
    }

    private void executeSelected() {
        Button selected = menuButtons.get(selectedIndex);
        switch (selected.getText()) {
            case "Nouvelle Partie" -> System.out.println("Nouvelle Partie sélectionnée");
            case "Options" -> System.out.println("Options sélectionnées");
            case "Quitter" -> System.exit(0);
        }
    }

    @FXML
    private void handleNP() {
        System.out.println("Nouvelle partie lancée !");
        // → ici, charger une autre scène, démarrer le jeu, etc.
    }

    @FXML
    private void handleOp() {
        System.out.println("Options ouvertes !");
        // → afficher un autre menu, pop-up, etc.
    }

    @FXML
    private void handleQ() {
        System.out.println("Quitter !");
        Platform.exit(); // ferme proprement l'application
    }
}