package com.example.BomberMAN.menu;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MenuController {
    @FXML
    private StackPane rootPane;

    @FXML
    private Label continu;

    private MediaPlayer mediaPlayer;

    @FXML
    private StackPane popupPane;

    private boolean popupShown = false;

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
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case ESCAPE:
                if (popupShown) {
                    hidePopup();
                    popupShown = false;
                }
                break;

            default:
                if (!popupShown) {
                    showPopup();
                    popupShown = true;
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
        pt.setOnFinished(e -> popupPane.setVisible(false)); // cacher après l’animation
        pt.play();
    }

}