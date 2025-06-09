/**
 * Contrôleur du menu principal du jeu BomberMAN.
 * Gère les animations, les interactions clavier, les effets visuels et la navigation vers le jeu.
 */
package com.example.BomberMAN.menu;

import com.example.BomberMAN.Game;
import com.example.BomberMAN.mapEditor.MapEditor;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;

import java.util.List;

/**
 * Contrôleur de l'interface de menu principal.
 */
public class MenuController {

    /** Pane racine contenant tous les éléments du menu. */
    @FXML private StackPane rootPane;

    /** Label "Appuyez pour continuer". */
    @FXML private Label continu;

    /** Pane du popup du menu principal (Nouvelle Partie, Options, Quitter). */
    @FXML private StackPane popupPane;

    /** Pane de sélection du mode de jeu (Solo, Multi). */
    @FXML private StackPane modePane;

    /** Bouton "Nouvelle Partie". */
    @FXML private Button btnNP;

    /** Bouton "Options". */
    @FXML private Button btnOp;

    /** Bouton "Quitter". */
    @FXML private Button btnQ;

    /** Bouton "Solo". */
    @FXML private Button btnSolo;

    /** Bouton "Multi". */
    @FXML private Button btnMulti;

    /** Bouton "Edit". */
    @FXML private Button btnEdit;

    /** Bouton "Retour". */
    @FXML private Button btnR;

    /** Liste des boutons du menu principal. */
    private List<Button> menuButtons;

    /** Liste des boutons de sélection de mode. */
    private List<Button> modeButtons;

    /** Index de l'élément sélectionné dans le menu courant. */
    private int selectedIndex = 0;

    /** Indique si le popup du menu principal est affiché. */
    private boolean popupShown = false;

    /** Indique si le popup de sélection de mode est affiché. */
    private boolean modeShown = false;

    /** Lecteur média pour la musique de fond. */
    private MediaPlayer mediaPlayer;

    /** Lecteur média pour sound effect */
    private AudioClip  sfxMove;

    /** Pane des options */
    @FXML private StackPane optionsPane;

    /** Slider du volume */
    @FXML private Slider volumeSlider;

    /** Rectangle pour le flou */
    @FXML
    private Rectangle overlay;

    /** Indique si le l'overlay de flou est visible */
    private boolean isOverlayVisible = false;

    /** Référence à la fenêtre principale. */
    private Stage primaryStage;

    /**
     * Définit la fenêtre principale pour le lancement du jeu.
     * @param stage La fenêtre JavaFX principale.
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Initialise le menu, configure les effets et les événements clavier.
     */
    @FXML
    public void initialize() {
        rootPane.setOnKeyPressed(this::handleKeyPress);
        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> rootPane.requestFocus());

        // Animation du label "Appuyez pour continuer"
        FadeTransition ft = new FadeTransition(Duration.seconds(0.7), continu);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();

        // Chargement et lecture de la musique de fond
        Media media = new Media(getClass().getResource("sound/MenuPrincipal.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.2);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        // Chargement et lecture du sound effect
        String moveSoundPath = getClass().getResource("sound/switch.mp3").toExternalForm();
        sfxMove = new AudioClip(moveSoundPath);;

        // Binding entre le volume et le slider
        volumeSlider.setValue(mediaPlayer.getVolume());
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            mediaPlayer.setVolume(newVal.doubleValue());
        });

        menuButtons = List.of(btnNP, btnOp, btnQ);
        modeButtons = List.of(btnSolo, btnMulti, btnEdit, btnR);

        btnSolo.setOnAction(e -> startGame(true));
        btnMulti.setOnAction(e -> startGame(false));
        btnEdit.setOnAction(e -> startMapEditor());
        btnR.setOnAction(e -> handleRetour());
    }

    /**
     * Gère les événements clavier (navigation, validation, retour).
     * @param event L'événement clavier.
     */
    private void handleKeyPress(KeyEvent event) {
        if (optionsPane.isVisible()) {
            switch (event.getCode()) {
                case LEFT -> {
                    double newVal = volumeSlider.getValue() - volumeSlider.getBlockIncrement();
                    if (newVal < volumeSlider.getMin()) newVal = volumeSlider.getMin();
                    volumeSlider.setValue(newVal);
                    mediaPlayer.setVolume(newVal);
                    event.consume();
                }
                case RIGHT -> {
                    double newVal = volumeSlider.getValue() + volumeSlider.getBlockIncrement();
                    if (newVal > volumeSlider.getMax()) newVal = volumeSlider.getMax();
                    volumeSlider.setValue(newVal);
                    mediaPlayer.setVolume(newVal);
                    event.consume();
                }
                case ESCAPE -> {
                    handleRetourOptions();
                    event.consume();
                }
                default -> {}
            }
            return; // On stoppe ici car options active
        }

        switch (event.getCode()) {
            case ESCAPE:
                if (optionsPane.isVisible()) {
                    hideOptionsPane();
                    togglePopup();
                } else if (modeShown) {
                    hideModePopup();
                    modeShown = false;
                    popupShown = true;
                    selectedIndex = 0;
                    updateFocus(menuButtons);
                    togglePopup();
                } else if (popupShown) {
                    hidePopup();
                    popupShown = false;
                    togglePopup();
                }
                break;

            case UP:
                if (popupShown && !modeShown && !optionsPane.isVisible()) {
                    selectedIndex = (selectedIndex - 1 + menuButtons.size()) % menuButtons.size();
                    sfxMove.play();
                    updateFocus(menuButtons);
                } else if (modeShown) {
                    selectedIndex = (selectedIndex - 1 + modeButtons.size()) % modeButtons.size();
                    sfxMove.play();
                    updateFocus(modeButtons);
                }
                break;

            case DOWN:
                if (popupShown && !modeShown && !optionsPane.isVisible()) {
                    selectedIndex = (selectedIndex + 1) % menuButtons.size();
                    sfxMove.play();
                    updateFocus(menuButtons);
                } else if (modeShown) {
                    selectedIndex = (selectedIndex + 1) % modeButtons.size();
                    sfxMove.play();
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
                if (!popupShown && !optionsPane.isVisible()) {
                    showPopup();
                    popupShown = true;
                    selectedIndex = 0;
                    updateFocus(menuButtons);
                    togglePopup();
                }
                break;
        }
    }

    /**
     * Affiche le popup du menu principal avec animation.
     */
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

    /**
     * Cache le popup du menu principal avec animation.
     */
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

    /**
     * Affiche la sélection du mode de jeu avec animation.
     */
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

    /**
     * Cache le panneau de sélection du mode de jeu.
     */
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

    private void hideOptionsPane() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), optionsPane);
        tt.setFromY(0);
        tt.setToY(600);
        tt.setInterpolator(Interpolator.EASE_IN);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), optionsPane);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> {
            optionsPane.setVisible(false);
            popupShown = true;
            selectedIndex = 0;
            updateFocus(menuButtons);
            showPopup();
        });
        pt.play();
    }

    /**
     * Applique un effect de flou Gaussian.
     */
    private void togglePopup() {
        isOverlayVisible = !isOverlayVisible;
        overlay.setVisible(isOverlayVisible);
        overlay.setOpacity(isOverlayVisible ? 0.5 : 0);
    }
    
    /**
     * Met à jour le style du bouton sélectionné.
     * @param buttons La liste de boutons concernés.
     */
    private void updateFocus(List<Button> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setStyle("");
        }
        buttons.get(selectedIndex).setStyle("-fx-border-color: white; -fx-border-width: 2;");
    }

    /**
     * Exécute l'action associée au bouton actuellement sélectionné du menu principal.
     */
    private void executeSelected() {
        Button selected = menuButtons.get(selectedIndex);
        switch (selected.getText()) {
            case "Nouvelle Partie" -> {
                selectedIndex = 0;
                updateFocus(modeButtons);
                showModePopup();
                modeShown = true;
            }
            case "Options" -> {
                showOptionsPane();
                popupShown = false;
            }
            case "Quitter" -> Platform.exit();
        }
    }

    /**
     * Exécute le démarrage du jeu selon le mode sélectionné (solo ou multijoueur).
     */
    private void executeModeSelected() {
        Button selected = modeButtons.get(selectedIndex);
        if (selected.getText().equals("Solo")) {
            startGame(true);
        } else if (selected.getText().equals("Editeur de carte")) {
            startMapEditor();
        } else if (selected.getText().equals("Retour")) {
            handleRetour();
        } else {
            startGame(false);
        }
    }

    /**
     * Lance le jeu en mode solo ou multijoueur.
     * @param isSolo true pour le mode solo, false pour multijoueur.
     */
    private void startGame(boolean isSolo) {
        mediaPlayer.stop();
        Game game = new Game(isSolo);
        game.start(primaryStage);
    }


    /**
     * Lance l'éditeur de carte.
     */
    @FXML
    private void startMapEditor() {
        MapEditor editor = new MapEditor();
        Stage stage = (Stage) rootPane.getScene().getWindow();
        editor.start(stage);
    }

    private void handleRetour() {
        hideModePopup();
        showPopup();
        modeShown = false;
        popupShown = true;
        selectedIndex = 0;
        updateFocus(menuButtons);
    }

    /**
     * Execute l'affiche des options
     */
    private void showOptionsPane() {
        popupPane.setVisible(false);
        optionsPane.setVisible(true);
        optionsPane.setOpacity(0);
        optionsPane.setTranslateY(600);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), optionsPane);
        tt.setFromY(600);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), optionsPane);
        ft.setFromValue(0);
        ft.setToValue(1);

        new ParallelTransition(tt, ft).play();

        Platform.runLater(() -> optionsPane.requestFocus());
    }

    @FXML
    private void handleRetourOptions() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), optionsPane);
        tt.setFromY(0);
        tt.setToY(600);
        tt.setInterpolator(Interpolator.EASE_IN);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), optionsPane);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> {
            optionsPane.setVisible(false);
            showPopup();
            popupShown = true;
        });
        pt.play();
    }

    /** Gère le clic sur le bouton Nouvelle Partie (FXML). */
    @FXML private void handleNP() { showModePopup(); }

    /** Gère le clic sur le bouton Options (FXML). */
    @FXML private void handleOp() { System.out.println("Options sélectionnées"); }

    /** Gère le clic sur le bouton Quitter (FXML). */
    @FXML private void handleQ() { Platform.exit(); }
}
