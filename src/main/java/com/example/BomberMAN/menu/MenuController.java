/**
 * Contrôleur du menu principal du jeu BomberMAN.
 * Gère les animations, les interactions clavier, les effets visuels et la navigation vers le jeu.
 */
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

    /** Référence à la fenêtre principale. */
    private Stage primaryStage;

    /**
     * Définit la fenêtre principale pour le lancement du jeu.
     * @param stage La fenêtre JavaFX principale.
     */
    public void setPrimaryStage(Stage stage)
    {
        this.primaryStage = stage;
    }

    /**
     * Initialise le menu, configure les effets et les événements clavier.
     */
    @FXML
    public void initialize()
    {
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
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        menuButtons = List.of(btnNP, btnOp, btnQ);
        modeButtons = List.of(btnSolo, btnMulti);

        btnSolo.setOnAction(e -> startGame(true));
        btnMulti.setOnAction(e -> startGame(false));
    }

    /**
     * Gère les événements clavier (navigation, validation, retour).
     * @param event L'événement clavier.
     */
    private void handleKeyPress(KeyEvent event)
    {
        switch (event.getCode())
        {
            case ESCAPE:
                if (modeShown)
                {
                    hideModePopup();
                    modeShown = false;
                } else if (popupShown)
                {
                    hidePopup();
                    popupShown = false;
                }
                break;

            case UP:
                if (popupShown && !modeShown)
                {
                    selectedIndex = (selectedIndex - 1 + menuButtons.size()) % menuButtons.size();
                    updateFocus(menuButtons);
                } else if (modeShown)
                {
                    selectedIndex = (selectedIndex - 1 + modeButtons.size()) % modeButtons.size();
                    updateFocus(modeButtons);
                }
                break;

            case DOWN:
                if (popupShown && !modeShown)
                {
                    selectedIndex = (selectedIndex + 1) % menuButtons.size();
                    updateFocus(menuButtons);
                } else if (modeShown)
                {
                    selectedIndex = (selectedIndex + 1) % modeButtons.size();
                    updateFocus(modeButtons);
                }
                break;

            case ENTER:
                if (modeShown)
                {
                    executeModeSelected();
                } else if (popupShown)
                {
                    executeSelected();
                }
                break;

            default:
                if (!popupShown)
                {
                    showPopup();
                    popupShown = true;
                    selectedIndex = 0;
                    updateFocus(menuButtons);
                }
                break;
        }
    }

    /**
     * Affiche le popup du menu principal avec animation.
     */
    private void showPopup()
    {
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
    private void hidePopup()
    {
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
    private void showModePopup()
    {
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
    private void hideModePopup()
    {
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

    /**
     * Met à jour le style du bouton sélectionné.
     * @param buttons La liste de boutons concernés.
     */
    private void updateFocus(List<Button> buttons)
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            buttons.get(i).setStyle("");
        }
        buttons.get(selectedIndex).setStyle("-fx-border-color: white; -fx-border-width: 2;");
    }

    /**
     * Exécute l'action associée au bouton actuellement sélectionné du menu principal.
     */
    private void executeSelected()
    {
        Button selected = menuButtons.get(selectedIndex);
        switch (selected.getText())
        {
            case "Nouvelle Partie" ->
            {
                selectedIndex = 0;
                updateFocus(modeButtons);
                showModePopup();
                modeShown = true;
            }
            case "Options" -> System.out.println("Options sélectionnées");
            case "Quitter" -> Platform.exit();
        }
    }

    /**
     * Exécute le démarrage du jeu selon le mode sélectionné (solo ou multijoueur).
     */
    private void executeModeSelected()
    {
        Button selected = modeButtons.get(selectedIndex);
        if (selected.getText().equals("Solo"))
        {
            startGame(true);
        } else
        {
            startGame(false);
        }
    }

    /**
     * Lance le jeu en mode solo ou multijoueur.
     * @param isSolo true pour le mode solo, false pour multijoueur.
     */
    private void startGame(boolean isSolo)
    {
        mediaPlayer.stop();
        Game game = new Game(isSolo);
        game.start(primaryStage);
    }

    /** Gère le clic sur le bouton Nouvelle Partie (FXML). */
    @FXML private void handleNP() { showModePopup(); }

    /** Gère le clic sur le bouton Options (FXML). */
    @FXML private void handleOp() { System.out.println("Options sélectionnées"); }

    /** Gère le clic sur le bouton Quitter (FXML). */
    @FXML private void handleQ() { Platform.exit(); }
}