package com.example.BomberMAN.menu;

import com.example.BomberMAN.Game;
import com.example.BomberMAN.GamePlay.Tile;
import com.example.BomberMAN.mapEditor.MapEditor;
import com.fasterxml.jackson.databind.JsonSerializer;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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

    /** Bouton Thème */
    @FXML private Button btnSKIN;

    /** Bouton "Edit". */
    @FXML private Button btnEdit;

    /** Boutons de sélection de cartes. */
    @FXML private Button btnCh;

    /** Liste des noms de cartes disponibles. */
    private List<String> availableMaps;
    private int currentMapIndex = 0;

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

    /** Lecteur média pour l'annonce du titre de fond. */
    private MediaPlayer bomberName;

    /** Lecteur média pour sound effect de déplacement. */
    private AudioClip sfxMove;

    /** Lecteur média pour sound effect d'utilisation. */
    private AudioClip sfxUse;

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

    /** Pane de sélection de thème. */
    @FXML private StackPane themePane;
    /** Bouton pour sélectionner le thème BomberMan. */
    @FXML private Button btnThemeBomberMan;
    /** Bouton pour sélectionner le thème Manoir. */
    @FXML private Button btnThemeManoir;
    /** Bouton pour retourner du menu des thèmes. */
    @FXML private Button btnThemeRetour;

    /** Indique si le popup de sélection de thème est affiché. */
    private boolean themeShown = false;

    /** Thème actuellement sélectionné. */
    private String currentTheme = "BomberMan"; // Thème par défaut
    /** Liste des boutons de sélection de thème. */
    private List<Button> themeButtons;
    /** ImageView affichant l'image de fond du menu. */
    @FXML private ImageView backgroundImageView;

    /** Bouton "Règles". */
    @FXML private Button btnRules;

    /** Bouton "Crédits". */
    @FXML private Button btnCredits;

    /** Pane des règles. */
    @FXML private StackPane rulesPane;

    /** Pane des crédits. */
    @FXML private StackPane creditsPane;

    /** Indique la carte sélectonnée pour le jeu. */
    private String selectedMap;

    /** Indique si le popup des règles est affiché. */
    private boolean rulesShown = false;

    /** Indique si le popup des crédits est affiché. */
    private boolean creditsShown = false;

    /**
     * Définit la fenêtre principale pour le lancement du jeu.
     * @param stage La fenêtre JavaFX principale.
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Initialise le menu, configure les effets et les événements clavier.
     * Cette méthode est automatiquement appelée après le chargement du FXML.
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

        // Chargement et lecture de l'annonce du jeu
        Media media1 = new Media(getClass().getResource("sound/SuperBomberman.mp3").toExternalForm());
        bomberName = new MediaPlayer(media1);
        bomberName.setVolume(0.4);
        bomberName.play();

        // Chargement et lecture du sound effect de déplacement
        String moveSoundPath = getClass().getResource("sound/switch.mp3").toExternalForm();
        sfxMove = new AudioClip(moveSoundPath);

        // Chargement et lecture du sound effect d'utilisation
        String useSoundPath = getClass().getResource("sound/click.mp3").toExternalForm();
        sfxUse = new AudioClip(useSoundPath);
        sfxUse.setVolume(1.8);

        // Binding entre le volume du lecteur média et le slider
        volumeSlider.setValue(mediaPlayer.getVolume());
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            mediaPlayer.setVolume(newVal.doubleValue());
        });

        // Initialisation des listes de boutons
        menuButtons = List.of(btnNP, btnSKIN, btnRules, btnCredits, btnOp, btnQ);
        modeButtons = List.of(btnSolo, btnMulti, btnEdit, btnCh, btnR);

        // Configuration des actions pour les boutons de sélection de mode
        btnSolo.setOnAction(e -> startGame(true));
        btnMulti.setOnAction(e -> startGame(false));
        btnEdit.setOnAction(e -> startMapEditor());
        btnR.setOnAction(e -> handleRetour());

        // Configuration des actions pour les boutons de sélection de thème
        btnThemeBomberMan.setOnAction(e -> selectTheme("BomberMan"));
        btnThemeManoir.setOnAction(e -> selectTheme("Manoir"));
        btnThemeRetour.setOnAction(e -> handleRetourTheme());

        // Initialisation de la liste des boutons de thème
        themeButtons = List.of(btnThemeBomberMan, btnThemeManoir, btnThemeRetour);

        availableMaps = loadAvailableMaps(); // À implémenter selon où sont stockées tes maps
        currentMapIndex = 0;
        updateMapLabel();

        // Charger toutes les textures au démarrage
        Tile.loadAllTextures();
    }

    /**
     * Gère les événements clavier (navigation, validation, retour).
     * Cette méthode est appelée lorsqu'une touche est pressée.
     * @param event L'événement clavier.
     */
    private void handleKeyPress(KeyEvent event) {
        // Si le panneau d'options est visible, gérer les interactions spécifiques au volume
        if (optionsPane.isVisible()) {
            switch (event.getCode()) {
                case LEFT -> {
                    double newVal = volumeSlider.getValue() - volumeSlider.getBlockIncrement();
                    if (newVal < volumeSlider.getMin()) newVal = volumeSlider.getMin();
                    volumeSlider.setValue(newVal);
                    mediaPlayer.setVolume(newVal);
                    event.consume(); // Consommer l'événement pour éviter qu'il ne soit traité ailleurs
                }
                case RIGHT -> {
                    double newVal = volumeSlider.getValue() + volumeSlider.getBlockIncrement();
                    if (newVal > volumeSlider.getMax()) newVal = volumeSlider.getMax();
                    volumeSlider.setValue(newVal);
                    mediaPlayer.setVolume(newVal);
                    event.consume(); // Consommer l'événement
                }
                case ESCAPE -> {
                    handleRetourOptions(); // Gérer le retour des options
                    event.consume(); // Consommer l'événement
                }
                default -> {} // Ne rien faire pour les autres touches
            }
            return; // Sortir de la méthode car les options sont actives
        }

        // Gérer les événements clavier en fonction de l'état des popups
        switch (event.getCode())
        {
            case ESCAPE:
                if (optionsPane.isVisible())
                {
                    hideOptionsPane();
                    togglePopup(); // Désactiver le flou
                }
                else if (rulesShown)
                {
                    handleRetourRules();
                }
                else if (creditsShown)
                {
                    handleRetourCredits();
                }
                else if (themeShown)
                {
                    handleRetourTheme(); // Retourner du menu des thèmes
                }
                else if (modeShown)
                {
                    hideModePopup(); // Cacher le popup de mode
                    modeShown = false;
                    popupShown = true;
                    selectedIndex = 0;
                    updateFocus(menuButtons); // Mettre à jour le focus sur les boutons du menu principal
                    togglePopup(); // Désactiver le flou
                }
                else if (popupShown)
                {
                    hidePopup(); // Cacher le popup principal
                    popupShown = false;
                    togglePopup(); // Désactiver le flou
                }
                break;

            case UP:
                if (popupShown && !modeShown && !themeShown && !optionsPane.isVisible())
                {
                    selectedIndex = (selectedIndex - 1 + menuButtons.size()) % menuButtons.size();
                    sfxMove.play(); // Jouer l'effet sonore de déplacement
                    updateFocus(menuButtons); // Mettre à jour le focus
                }
                else if (modeShown)
                {
                    selectedIndex = (selectedIndex - 1 + modeButtons.size()) % modeButtons.size();
                    sfxMove.play(); // Jouer l'effet sonore de déplacement
                    updateFocus(modeButtons); // Mettre à jour le focus
                }
                else if (themeShown)
                {
                    selectedIndex = (selectedIndex - 1 + themeButtons.size()) % themeButtons.size();
                    sfxMove.play(); // Jouer l'effet sonore de déplacement
                    updateFocus(themeButtons); // Mettre à jour le focus
                }
                break;

            case DOWN:
                if (popupShown && !modeShown && !themeShown && !optionsPane.isVisible())
                {
                    selectedIndex = (selectedIndex + 1) % menuButtons.size();
                    sfxMove.play(); // Jouer l'effet sonore de déplacement
                    updateFocus(menuButtons); // Mettre à jour le focus
                }
                else if (modeShown)
                {
                    selectedIndex = (selectedIndex + 1) % modeButtons.size();
                    sfxMove.play(); // Jouer l'effet sonore de déplacement
                    updateFocus(modeButtons); // Mettre à jour le focus
                }
                else if (themeShown)
                {
                    selectedIndex = (selectedIndex + 1) % themeButtons.size();
                    sfxMove.play(); // Jouer l'effet sonore de déplacement
                    updateFocus(themeButtons); // Mettre à jour le focus
                }
                break;

            case LEFT:
                if (modeShown && modePane.isVisible()) {
                    currentMapIndex = (currentMapIndex - 1 + availableMaps.size()) % availableMaps.size();
                    updateMapLabel();
                    selectedMap = availableMaps.get(currentMapIndex);
                    sfxMove.play();
                    event.consume();
                }
                break;
            case RIGHT:
                if (modeShown && modePane.isVisible()) {
                    currentMapIndex = (currentMapIndex + 1) % availableMaps.size();
                    updateMapLabel();
                    selectedMap = availableMaps.get(currentMapIndex);
                    sfxMove.play();
                    event.consume();
                }
                break;

            case ENTER:
                if (themeShown)
                {
                    executeThemeSelected(); // Exécuter l'action du thème sélectionné
                    sfxUse.play(); // Jouer l'effet sonore d'utilisation
                }
                else if (modeShown)
                {
                    executeModeSelected(); // Exécuter l'action du mode sélectionné
                    sfxUse.play(); // Jouer l'effet sonore d'utilisation
                }
                else if (popupShown)
                {
                    executeSelected(); // Exécuter l'action du menu principal sélectionné
                    sfxUse.play(); // Jouer l'effet sonore d'utilisation
                }
                break;

            default:
                // Si aucun popup n'est affiché et que les options ne sont pas visibles, afficher le popup principal
                if (!popupShown && !optionsPane.isVisible() && !rulesShown && !creditsShown) {
                    showPopup();
                    selectedMap = availableMaps.get(currentMapIndex);
                    popupShown = true;
                    selectedIndex = 0;
                    updateFocus(menuButtons);
                    togglePopup(); // Activer le flou
                }
                break;
        }
    }

    /**
     * Affiche le popup du menu principal avec animation.
     */
    private void showPopup() {
        popupPane.setVisible(true);
        popupPane.setTranslateY(600); // Position initiale en dehors de l'écran

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), popupPane);
        tt.setFromY(600);
        tt.setToY(0); // Position finale au centre
        tt.setInterpolator(Interpolator.EASE_OUT); // Interpolation pour un mouvement fluide

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), popupPane);
        ft.setFromValue(0); // Début de l'opacité à 0
        ft.setToValue(1); // Fin de l'opacité à 1

        new ParallelTransition(tt, ft).play(); // Jouer les deux animations en parallèle

        Platform.runLater(() -> rootPane.requestFocus()); // Demander le focus au panneau racine
    }

    /**
     * Cache le popup du menu principal avec animation.
     */
    private void hidePopup() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), popupPane);
        tt.setFromY(0);
        tt.setToY(600); // Déplacer vers le bas de l'écran
        tt.setInterpolator(Interpolator.EASE_IN); // Interpolation pour un mouvement fluide

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), popupPane);
        ft.setFromValue(1);
        ft.setToValue(0); // Diminuer l'opacité à 0

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> popupPane.setVisible(false)); // Cacher le panneau une fois l'animation terminée
        pt.play();
    }

    /**
     * Affiche la sélection du mode de jeu avec animation.
     */
    private void showModePopup() {
        popupPane.setVisible(false); // Cacher le popup principal
        modePane.setVisible(true);
        modePane.setOpacity(0);
        modePane.setTranslateY(600);

        selectedIndex = 0;
        updateFocus(modeButtons); // Mettre à jour le focus sur les boutons de mode

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
     * Affiche le panneau de sélection de thème avec animation.
     */
    public void showThemePopup()
    {
        popupPane.setVisible(false); // Cacher le popup principal
        themePane.setVisible(true);
        themePane.setOpacity(0);
        themePane.setTranslateY(600);

        selectedIndex = 0;
        updateFocus(themeButtons); // Mettre à jour le focus sur les boutons de thème
        themeShown = true; // Indiquer que le menu des thèmes est affiché
        popupShown = false; // Indiquer que le menu principal est caché

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), themePane);
        tt.setFromY(600);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), themePane);
        ft.setFromValue(0);
        ft.setToValue(1);

        new ParallelTransition(tt, ft).play();

        Platform.runLater(() -> rootPane.requestFocus()); // Demander le focus au panneau racine
    }

    /**
     * Affiche le panneau des règles avec animation.
     */
    public void showRulesPane() {
        popupPane.setVisible(false); // Cacher le popup principal
        rulesPane.setVisible(true);
        rulesPane.setOpacity(0);
        rulesPane.setTranslateY(600);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), rulesPane);
        tt.setFromY(600);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), rulesPane);
        ft.setFromValue(0);
        ft.setToValue(1);

        new ParallelTransition(tt, ft).play();

        rulesShown = true;
        popupShown = false;
        togglePopup(); // Activer le flou
        Platform.runLater(() -> rootPane.requestFocus()); // Demander le focus au panneau racine
    }

    /**
     * Cache le panneau des règles avec animation.
     */
    private void hideRulesPane() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), rulesPane);
        tt.setFromY(0);
        tt.setToY(600);
        tt.setInterpolator(Interpolator.EASE_IN);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), rulesPane);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> rulesPane.setVisible(false));
        pt.play();
    }

    private List<String> loadAvailableMaps() {
        List<String> maps = new ArrayList<>();

        // 1. Maps dans le dossier niveau/ (développement)
        File niveauDir = new File("niveau/");
        if (niveauDir.exists() && niveauDir.isDirectory()) {
            File[] files = niveauDir.listFiles((d, name) -> name.endsWith(".map"));
            if (files != null) {
                for (File file : files) {
                    maps.add(file.getName());
                }
            }
        }

        // 2. Maps dans les ressources (texture_Maps)
        try {
            File nDir = new File("src/main/resources/com/example/BomberMAN/BomberMAN/texture_Maps/");
            if (nDir.exists() && nDir.isDirectory()) {
                File[] files = nDir.listFiles((d, name) -> name.endsWith(".map"));
                if (files != null) {
                    for (File file : files) {
                        maps.add(file.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maps;
    }

    /**
     * Charge la liste des cartes disponibles.
     */
    private void updateMapLabel() {
        btnCh.setText(availableMaps.get(currentMapIndex));
    }

    /**
     * Gère l'action de retour depuis le panneau des règles vers le menu principal.
     */
    @FXML
    private void handleRetourRules() {
        hideRulesPane();
        rulesShown = false;
        popupShown = true;
        selectedIndex = 0;
        updateFocus(menuButtons);
        togglePopup(); // Désactiver le flou
        showPopup();
    }

    /**
     * Affiche le panneau des crédits avec animation.
     */
    public void showCreditsPane() {
        popupPane.setVisible(false); // Cacher le popup principal
        creditsPane.setVisible(true);
        creditsPane.setOpacity(0);
        creditsPane.setTranslateY(600);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), creditsPane);
        tt.setFromY(600);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), creditsPane);
        ft.setFromValue(0);
        ft.setToValue(1);

        new ParallelTransition(tt, ft).play();

        creditsShown = true;
        popupShown = false;
        togglePopup(); // Activer le flou
        Platform.runLater(() -> rootPane.requestFocus()); // Demander le focus au panneau racine
    }

    /**
     * Cache le panneau des crédits avec animation.
     */
    private void hideCreditsPane() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), creditsPane);
        tt.setFromY(0);
        tt.setToY(600);
        tt.setInterpolator(Interpolator.EASE_IN);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), creditsPane);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> creditsPane.setVisible(false));
        pt.play();
    }

    /**
     * Gère l'action de retour depuis le panneau des crédits vers le menu principal.
     */
    @FXML
    private void handleRetourCredits() {
        hideCreditsPane();
        creditsShown = false;
        popupShown = true;
        selectedIndex = 0;
        updateFocus(menuButtons);
        togglePopup(); // Désactiver le flou
        showPopup();
    }

    /**
     * Cache le panneau de sélection du mode de jeu avec animation.
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
        pt.setOnFinished(e -> modePane.setVisible(false)); // Cacher le panneau une fois l'animation terminée
        pt.play();
    }

    /**
     * Cache le panneau des options avec animation.
     */
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
            optionsPane.setVisible(false); // Cacher le panneau une fois l'animation terminée
            popupShown = true; // Rendre le popup principal visible logiquement
            selectedIndex = 0; // Réinitialiser l'index de sélection
            updateFocus(menuButtons); // Mettre à jour le focus sur les boutons du menu principal
            showPopup(); // Afficher le popup principal
        });
        pt.play();
    }

    /**
     * Cache le panneau de sélection de thème avec animation.
     */
    private void hideThemePopup()
    {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), themePane);
        tt.setFromY(0);
        tt.setToY(600);
        tt.setInterpolator(Interpolator.EASE_IN);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), themePane);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> themePane.setVisible(false)); // Cacher le panneau une fois l'animation terminée
        pt.play();
    }

    /**
     * Active ou désactive un effet de flou (overlay) sur l'écran.
     * Cet overlay est utilisé pour simuler un flou lorsque des popups sont affichés.
     */
    private void togglePopup() {
        isOverlayVisible = !isOverlayVisible; // Inverser l'état de visibilité
        overlay.setVisible(isOverlayVisible); // Rendre l'overlay visible ou invisible
        overlay.setOpacity(isOverlayVisible ? 0.5 : 0); // Définir l'opacité (0.5 pour le flou, 0 pour transparent)
    }

    /**
     * Met à jour le style du bouton actuellement sélectionné dans une liste de boutons.
     * Supprime le style de tous les boutons et applique un style de bordure blanche au bouton sélectionné.
     * @param buttons La liste de boutons concernés (menu principal, modes de jeu, ou thèmes).
     */
    private void updateFocus(List<Button> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setStyle(""); // Supprimer le style de tous les boutons
        }
        buttons.get(selectedIndex).setStyle("-fx-border-color: white; -fx-border-width: 2;"); // Appliquer le style au bouton sélectionné
    }

    /**
     * Exécute l'action associée au bouton actuellement sélectionné du menu principal.
     * Cette méthode est appelée lorsque la touche "Entrée" est pressée dans le menu principal.
     */
    private void executeSelected() {
        Button selected = menuButtons.get(selectedIndex); // Obtenir le bouton sélectionné
        switch (selected.getText()) {
            case "Nouvelle Partie" -> {
                selectedIndex = 0; // Réinitialiser l'index de sélection
                updateFocus(modeButtons); // Mettre à jour le focus sur les boutons de mode
                showModePopup(); // Afficher le popup de sélection de mode
                modeShown = true; // Indiquer que le menu de mode est affiché
                popupShown = false; // Indiquer que le menu principal est caché
            }
            case "Thèmes" -> {
                selectedIndex = 0; // Réinitialiser l'index de sélection
                updateFocus(themeButtons); // Mettre à jour le focus sur les boutons de thème
                showThemePopup(); // Afficher le popup de sélection de thème
                themeShown = true; // Indiquer que le menu des thèmes est affiché
                popupShown = false; // Indiquer que le menu principal est caché
            }
            case "Règles" -> {
                showRulesPane();
            }
            case "Crédits" -> {
                showCreditsPane();
            }
            case "Options" -> {
                showOptionsPane(); // Afficher le panneau des options
                popupShown = false; // Indiquer que le menu principal est caché
            }
            case "Quitter" -> Platform.exit(); // Quitter l'application
        }
    }

    /**
     * Exécute le démarrage du jeu selon le mode sélectionné (solo ou multijoueur) ou l'éditeur de carte.
     * Cette méthode est appelée lorsque la touche "Entrée" est pressée dans le menu de sélection de mode.
     */
    private void executeModeSelected() {
        Button selected = modeButtons.get(selectedIndex); // Obtenir le bouton sélectionné
        if (selected.getText().equals("Solo")) {
            startGame(true); // Lancer le jeu en mode solo
        } else if (selected.getText().equals("Multijoueur")) {
            startGame(false); // Lancer le jeu en mode multijoueur
        } else if (selected.getText().equals("Editeur de carte")) {
            startMapEditor(); // Lancer l'éditeur de carte
        } else if (selected.getText().equals("Retour")) {
            handleRetour(); // Gérer le retour au menu principal
        }
    }

    /**
     * Lance le jeu en mode solo ou multijoueur.
     * Arrête la musique de fond et initialise une nouvelle instance du jeu.
     * @param isSolo true pour le mode solo, false pour multijoueur.
     */
    private void startGame(boolean isSolo) {
        mediaPlayer.stop(); // Arrêter la musique de fond
        Game game = new Game(isSolo, selectedMap); // Créer une nouvelle instance du jeu
        // Passer le thème au jeu
        game.setCurrentThemes(currentTheme);
        game.start(primaryStage); // Démarrer le jeu sur la fenêtre principale
    }

    /**
     * Lance l'éditeur de carte.
     * Crée une nouvelle instance de MapEditor et l'affiche.
     */
    @FXML
    private void startMapEditor() {
        MapEditor editor = new MapEditor();
        Stage stage = (Stage) rootPane.getScene().getWindow(); // Obtenir la fenêtre actuelle
        editor.start(stage); // Démarrer l'éditeur de carte
    }

    /**
     * Gère l'action de retour depuis le menu de sélection de mode vers le menu principal.
     * Cache le popup de mode et réaffiche le popup principal.
     */
    private void handleRetour() {
        hideModePopup(); // Cacher le popup de mode
        showPopup(); // Afficher le popup principal
        modeShown = false; // Indiquer que le menu de mode est caché
        popupShown = true; // Indiquer que le menu principal est affiché
        selectedIndex = 0; // Réinitialiser l'index de sélection
        updateFocus(menuButtons); // Mettre à jour le focus sur les boutons du menu principal
    }

    /**
     * Affiche le panneau des options avec animation.
     */
    private void showOptionsPane() {
        popupPane.setVisible(false); // Cacher le popup principal
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

        Platform.runLater(() -> optionsPane.requestFocus()); // Demander le focus au panneau des options
    }

    /**
     * Gère l'action de retour depuis le panneau des options vers le menu principal.
     * Cache le panneau des options et réaffiche le popup principal.
     */
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
            optionsPane.setVisible(false); // Cacher le panneau des options
            showPopup(); // Afficher le popup principal
            popupShown = true; // Indiquer que le menu principal est affiché
        });
        pt.play();
    }

    /** Gère le clic sur le bouton "Nouvelle Partie" (méthode FXML). */
    @FXML private void handleNP() { showModePopup(); }

    /** Gère le clic sur le bouton "Thèmes" (méthode FXML). */
    @FXML private void handleSkin()
    {
        showThemePopup();
    }

    /** Gère le clic sur le bouton "Options" (méthode FXML). */
    @FXML private void handleOp() { System.out.println("Options sélectionnées"); /* showOptionsPane(); est géré par handleKeyPress */ }

    /** Gère le clic sur le bouton "Quitter" (méthode FXML). */
    @FXML private void handleQ() { Platform.exit(); }

    /** Gère le clic sur le bouton "Règles" (méthode FXML). */
    @FXML private void handleRules() { showRulesPane(); }

    /** Gère le clic sur le bouton "Crédits" (méthode FXML). */
    @FXML private void handleCredits() { showCreditsPane(); }

    /**
     * Sélectionne un thème et applique les changements visuels.
     * Joue un effet sonore de confirmation et affiche un message dans la console.
     * Retourne ensuite au menu principal.
     * @param themeName Le nom du thème à appliquer ("BomberMan" ou "Manoir").
     */
    private void selectTheme(String themeName)
    {
        currentTheme = themeName; // Mettre à jour le thème actuel
        applyTheme(themeName); // Appliquer les changements visuels du thème

        // Jouer un son de confirmation
        sfxUse.play();

        // Afficher un feedback visuel (optionnel)
        System.out.println("Thème sélectionné : " + themeName);

        // Retourner au menu principal après sélection
        handleRetourTheme();
    }

    /**
     * Applique le thème sélectionné à l'interface en changeant l'image de fond.
     * @param themeName Le nom du thème à appliquer.
     */
    private void applyTheme(String themeName)
    {
        switch (themeName) {
            case "BomberMan" -> {
                backgroundImageView.setImage(new javafx.scene.image.Image(getClass().getResource("/com/example/BomberMAN/menu/image/FondMenu1.png").toExternalForm()));
            }
            case "Manoir" -> {
                backgroundImageView.setImage(new javafx.scene.image.Image(getClass().getResource("/com/example/BomberMAN/menu/image/FondMenuManoir.png").toExternalForm()));
            }
            default -> {
                // Thème par défaut si non reconnu
                backgroundImageView.setImage(new javafx.scene.image.Image(
                        getClass().getResource("/com/example/BomberMAN/menu/image/FondMenu1.png").toExternalForm()));
                updateButtonsTheme("#FF6B35", "#FFFFFF"); // Appliquer un style de bouton par défaut
            }
        }
    }

    /**
     * Cette méthode est actuellement appelée uniquement par le cas 'default' dans applyTheme,
     * mais pourrait être étendue pour des thèmes plus complexes.
     * @param primaryColor Couleur primaire du thème en format hexadécimal (ex: "#FF6B35").
     * @param textColor Couleur du texte des boutons en format hexadécimal (ex: "#FFFFFF").
     */
    private void updateButtonsTheme(String primaryColor, String textColor)
    {
        String buttonStyle = String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 5;",
                primaryColor, textColor
        );

        // Appliquer le style à tous les boutons des différents menus
        menuButtons.forEach(button -> button.setStyle(buttonStyle));
        modeButtons.forEach(button -> button.setStyle(buttonStyle));
        themeButtons.forEach(button -> button.setStyle(buttonStyle));
    }

    /**
     * Cache le panneau de sélection de thème et retourne au menu principal.
     * Réinitialise l'état des drapeaux et le focus.
     */
    private void handleRetourTheme()
    {
        hideThemePopup(); // Cacher le popup de thème
        themeShown = false; // Indiquer que le menu des thèmes est caché
        popupShown = true; // Indiquer que le menu principal est affiché
        selectedIndex = 0; // Réinitialiser l'index de sélection
        updateFocus(menuButtons); // Mettre à jour le focus sur les boutons du menu principal
        showPopup(); // Afficher le popup principal
    }

    /**
     * Exécute l'action associée au bouton de thème actuellement sélectionné.
     * Cette méthode est appelée lorsque la touche "Entrée" est pressée dans le menu des thèmes.
     */
    private void executeThemeSelected() {
        Button selected = themeButtons.get(selectedIndex); // Obtenir le bouton sélectionné
        switch (selected.getText()) {
            case "BomberMan" -> selectTheme("BomberMan"); // Sélectionner le thème BomberMan
            case "Manoir" -> selectTheme("Manoir"); // Sélectionner le thème Manoir
            case "Retour" -> handleRetourTheme(); // Retourner au menu principal
        }
    }
}
