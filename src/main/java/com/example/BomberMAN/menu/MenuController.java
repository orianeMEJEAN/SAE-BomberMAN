package com.example.BomberMAN.menu;

import com.example.BomberMAN.Game;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class MenuController {
    @FXML private StackPane rootPane;
    @FXML private Label continu;
    @FXML private StackPane popupPane;
    @FXML private StackPane modePane;

    @FXML private Button btnNP;
    @FXML private Button btnOp;
    @FXML private Button btnQ;
    @FXML private Button btnSolo;
    @FXML private Button btnMulti;

    private List<Button> menuButtons;
    private List<Button> modeButtons;

    private int selectedIndex = 0;
    private boolean popupShown = false;
    private boolean modeShown = false;

    private MediaPlayer mediaPlayer;
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void initialize() {
        rootPane.setOnKeyPressed(this::handleKeyPress);
        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> rootPane.requestFocus());

        FadeTransition ft = new FadeTransition(Duration.seconds(0.7), continu);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();

        Media media = new Media(getClass().getResource("sound/MenuPrincipal.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        menuButtons = List.of(btnNP, btnOp, btnQ);
        modeButtons = List.of(btnSolo, btnMulti);

        btnSolo.setOnAction(e -> startGame(true));
        btnMulti.setOnAction(e -> startGame(false));
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case ESCAPE:
                if (modeShown) {
                    hideModePopup();
                    modeShown = false;
                } else if (popupShown) {
                    hidePopup();
                    popupShown = false;
                }
                break;

            case UP:
                if (popupShown && !modeShown) {
                    selectedIndex = (selectedIndex - 1 + menuButtons.size()) % menuButtons.size();
                    updateFocus(menuButtons);
                } else if (modeShown) {
                    selectedIndex = (selectedIndex - 1 + modeButtons.size()) % modeButtons.size();
                    updateFocus(modeButtons);
                }
                break;

            case DOWN:
                if (popupShown && !modeShown) {
                    selectedIndex = (selectedIndex + 1) % menuButtons.size();
                    updateFocus(menuButtons);
                } else if (modeShown) {
                    selectedIndex = (selectedIndex + 1) % modeButtons.size();
                    updateFocus(modeButtons);
                }
                break;

            case ENTER:
                if (modeShown) {
                    executeModeSelected();
                } else if (popupShown) {
                    executeSelected();
                }
                break;

            default:
                if (!popupShown) {
                    showPopup();
                    popupShown = true;
                    selectedIndex = 0;
                    updateFocus(menuButtons);
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
        pt.setOnFinished(e -> popupPane.setVisible(false));
        pt.play();
    }

    private void showModePopup() {
        popupPane.setVisible(false);
        modePane.setVisible(true);
        modePane.setOpacity(0);
        modePane.setTranslateY(600);

        selectedIndex = 0;
        updateFocus(modeButtons);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), modePane);
        tt.setFromY(600);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), modePane);
        ft.setFromValue(0);
        ft.setToValue(1);

        new ParallelTransition(tt, ft).play();
    }

    private void hideModePopup() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), modePane);
        tt.setFromY(0);
        tt.setToY(600);
        tt.setInterpolator(Interpolator.EASE_IN);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), modePane);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> modePane.setVisible(false));
        pt.play();
    }

    private void updateFocus(List<Button> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setStyle("");
        }
        buttons.get(selectedIndex).setStyle("-fx-border-color: white; -fx-border-width: 2;");
    }

    private void executeSelected() {
        Button selected = menuButtons.get(selectedIndex);
        switch (selected.getText()) {
            case "Nouvelle Partie" -> {
                selectedIndex = 0;
                updateFocus(modeButtons);
                showModePopup();
                modeShown = true;
            }
            case "Options" -> System.out.println("Options sélectionnées");
            case "Quitter" -> Platform.exit();
        }
    }

    private void executeModeSelected() {
        Button selected = modeButtons.get(selectedIndex);
        if (selected.getText().equals("Solo")) {
            startGame(true);
        } else {
            startGame(false);
        }
    }

    private void startGame(boolean isSolo) {
        mediaPlayer.stop();
        Game game = new Game(isSolo);
        game.start(primaryStage);
    }

    // Boutons clic (optionnels, si activés via FXML)
    @FXML private void handleNP() { showModePopup(); }
    @FXML private void handleOp() { System.out.println("Options sélectionnées"); }
    @FXML private void handleQ() { Platform.exit(); }
}