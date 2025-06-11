package com.example.BomberMAN;

import com.example.BomberMAN.GamePlay.Bot;
import com.example.BomberMAN.GamePlay.Player;
import com.example.BomberMAN.GamePlay.Tile;
import com.example.BomberMAN.GamePlay.Bonus;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.layout.VBox;

/**
 * Classe principale du jeu BomberMAN avec gestion complète des bonus d'invincibilité.
 * Gère le cycle de vie du jeu, les bonus actifs et l'interface utilisateur.
 */
public class Game
{
    /** Taille d'une tuile en pixels. */
    public static final int TILE_SIZE = 40;

    /** Largeur de la grille (en nombre de tuiles). */
    public static final int GRID_WIDTH = 13;

    /** Hauteur de la grille (en nombre de tuiles). */
    public static final int GRID_HEIGHT = 11;


    // === COMPOSANTS PRINCIPAUX ===
    private GridPane grid;                  /**< Conteneur JavaFX représentant la grille de jeu. */
    private Tile[][] tiles;                 /**< Tableau des tuiles du plateau. */
    private Player player;                  /**< Joueur principal. */
    private boolean isSoloMode;             /**< Mode solo activé ou non. */
    private Bot bot;

    /**< Bot contrôlé par l'IA en mode solo. */
    // === GESTION DES BONUS ===
    private List<Bonus> activeBonus;        /**< Liste des bonus actifs sur la carte. */
    private Timeline bonusCheckTimer;       /**< Timer pour vérifier la collecte des bonus. */

    // === CONSTANTES DE CONFIGURATION ===
    private static final double BONUS_CHECK_INTERVAL = 100.0; // millisecondes
    private static final double INVINCIBILITY_DURATION = 5.0; // secondes
    private static final int SCENE_WIDTH = 536;
    private static final int SCENE_HEIGHT = 454;

    /** Thème actuel pour les tuiles et les joueurs. */
    private String currentTheme = "BomberMan"; // Thème par défaut
    private String mapName; // Nom de la carte à charger

    // === TIMER ===
    private Label timerLabel;              // Label pour le timer
    private Timeline gameTimer;            // Timer principal du jeu
    private int timeSeconds = 120;         // 2 minutes en secondes
    private BorderPane rootPane;           // Conteneur principal

    // === SCORES ===
    private int scoreSolo = 0; // Score pour le mode solo
    private int[] scoreMulti = new int[4]; // Scores pour les 4 joueurs en mode multi
    private Label scoreSoloLabel; // Affichage du score solo
    private Label[] scoreMultiLabels = new Label[4]; // Affichage des scores multi

    /**
     * Constructeur de la classe Game.
     *
     * @param isSoloMode True pour lancer une partie en solo, false pour multijoueur.
     * @param mapName Le nom du fichier de la carte à charger.
     */
    public Game(boolean isSoloMode, String mapName)
    {
        this.isSoloMode = isSoloMode;
        this.mapName = mapName;
        this.activeBonus = new ArrayList<>();

        System.out.println("Nouvelle partie créée - Mode: " + (isSoloMode ? "Solo" : "Multijoueur"));
        System.out.println("Durée d'invincibilité configurée: " + INVINCIBILITY_DURATION + " secondes");
    }

    /**
     * Lance la partie et initialise le plateau, le joueur, les murs, les briques et le bot si besoin.
     *
     * @param stage Fenêtre principale JavaFX utilisée pour afficher le jeu.
     */
    public void start(Stage stage)
    {
        try {
            initializeGame();
            setupScene(stage);
            startGameSystems();

            System.out.println("Jeu démarré avec succès !");
            System.out.println("Système de bonus d'invincibilité actif (durée: " + INVINCIBILITY_DURATION + "s)");

        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du jeu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialise les composants principaux du jeu
     */
    private void initializeGame()
    {
        grid = new GridPane();
        grid.setStyle("-fx-background-color: #2c3e50;");
        Tile.loadAllTextures();
        Tile.setCurrentTheme(currentTheme);

        // Timer en haut
        timerLabel = new Label("02:00");
        timerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #fff; -fx-font-weight: bold; -fx-padding: 7px; -fx-background-color: #222; -fx-alignment: center;");
        timerLabel.setMaxWidth(Double.MAX_VALUE);
        timerLabel.setMinHeight(32);
        timerLabel.setAlignment(javafx.geometry.Pos.CENTER);

        // Affichage horizontal du score à côté du timer
        javafx.scene.layout.HBox topBar = new javafx.scene.layout.HBox();
        topBar.setStyle("-fx-background-color: #222; -fx-padding: 0 5 0 0;");
        topBar.setSpacing(10);
        topBar.setMinHeight(32);
        topBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        topBar.getChildren().add(timerLabel);
        if (isSoloMode) {
            scoreSoloLabel = new Label("Score : 0");
            scoreSoloLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #fff; -fx-font-weight: bold; -fx-padding: 0 0 0 10px; -fx-background-color: #222;");
            scoreSoloLabel.setMinHeight(32);
            scoreSoloLabel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            topBar.getChildren().add(scoreSoloLabel);
        } else {
            for (int i = 0; i < 4; i++) {
                scoreMulti[i] = 0;
                scoreMultiLabels[i] = new Label("Joueur " + (i+1) + " : 0");
                scoreMultiLabels[i].setStyle("-fx-font-size: 10px; -fx-text-fill: #fff; -fx-font-weight: bold; -fx-padding: 0 0 0 10px; -fx-background-color: #222;");
                scoreMultiLabels[i].setMinHeight(32);
                scoreMultiLabels[i].setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                topBar.getChildren().add(scoreMultiLabels[i]);
            }
        }
        rootPane = new BorderPane();
        rootPane.setTop(topBar);
        rootPane.setCenter(grid);

        // Tentative de chargement dans texture_Maps, sinon dans niveau
        String mapPath = null;
        java.io.File resMap = new java.io.File("src/main/resources/com/example/BomberMAN/BomberMAN/texture_Maps/" + mapName);
        if (resMap.exists()) {
            mapPath = resMap.getPath();
        } else {
            java.io.File devMap = new java.io.File("niveau/" + mapName);
            if (devMap.exists()) {
                mapPath = devMap.getPath();
            } else {
                throw new RuntimeException("Carte introuvable : " + mapName);
            }
        }
        this.tiles = com.example.BomberMAN.Maps.MapLoader.loadMap(mapPath, grid);
        populateGrid();

        // Création du joueur avec référence au jeu pour les bonus
        player = new Player(1, 1, 11, 9, 11, 1,1, 9, grid, tiles, this, currentTheme, mapName);

        System.out.println("Composants du jeu initialisés");
    }

    /**
     * Remplit la grille avec les tuiles chargées
     */
    private void populateGrid()
    {
        for (int y = 0; y < tiles.length; y++)
        {
            for (int x = 0; x < tiles[y].length; x++)
            {
                if (tiles[y][x] != null) {
                    grid.add(tiles[y][x].getRectangle(), x, y);
                }
            }
        }
    }

    /**
     * Configure la scène et les contrôles
     */
    private void setupScene(Stage stage)
    {
        Scene scene = new Scene(rootPane, SCENE_WIDTH, SCENE_HEIGHT + 50); // +50 pour le timer
        setupKeyboardControls(scene);
        stage.setTitle("BomberMan - " + (isSoloMode ? "Solo" : "Multijoueur") + " [Invincibilité: " + INVINCIBILITY_DURATION + "s]");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Configure les contrôles clavier
     */
    private void setupKeyboardControls(Scene scene)
    {
        scene.setOnKeyPressed(e -> {
            try {
                switch (e.getCode())
                {
                    // === COMMANDES JOUEUR 1 ===
                    case Z -> {
                        player.movePlayer1(0, -1);
                        checkBonusCollectionImmediate(); // Vérification immédiate après mouvement
                    }
                    case S -> {
                        player.movePlayer1(0, 1);
                        checkBonusCollectionImmediate();
                    }
                    case Q -> {
                        player.movePlayer1(-1, 0);
                        checkBonusCollectionImmediate();
                    }
                    case D -> {
                        player.movePlayer1(1, 0);
                        checkBonusCollectionImmediate();
                    }
                    case SPACE -> player.placeBombPlayer1();
                }

                // === COMMANDES JOUEUR 2 (SI MULTIJOUEUR) ===
                if (!isSoloMode)
                {
                    switch (e.getCode())
                    {
                        case I -> {
                            player.movePlayer2(0, -1);
                            checkBonusCollectionImmediate();
                        }
                        case K -> {
                            player.movePlayer2(0, 1);
                            checkBonusCollectionImmediate();
                        }
                        case J -> {
                            player.movePlayer2(-1, 0);
                            checkBonusCollectionImmediate();
                        }
                        case L -> {
                            player.movePlayer2(1, 0);
                            checkBonusCollectionImmediate();
                        }
                        case EXCLAMATION_MARK -> player.placeBombPlayer2();
                    }

                    switch (e.getCode())
                    {
                        case UP -> {
                            player.movePlayer3(0, -1);
                            checkBonusCollectionImmediate();
                        }
                        case DOWN -> {
                            player.movePlayer3(0, 1);
                            checkBonusCollectionImmediate();
                        }
                        case LEFT -> {
                            player.movePlayer3(-1, 0);
                            checkBonusCollectionImmediate();
                        }
                        case RIGHT -> {
                            player.movePlayer3(1, 0);
                            checkBonusCollectionImmediate();
                        }
                        case CONTROL -> player.placeBombPlayer3();
                    }

                    switch (e.getCode())
                    {
                        case NUMPAD8 -> {
                            player.movePlayer4(0, -1);
                            checkBonusCollectionImmediate();
                        }
                        case NUMPAD5 -> {
                            player.movePlayer4(0, 1);
                            checkBonusCollectionImmediate();
                        }
                        case NUMPAD4 -> {
                            player.movePlayer4(-1, 0);
                            checkBonusCollectionImmediate();
                        }
                        case NUMPAD6 -> {
                            player.movePlayer4(1, 0);
                            checkBonusCollectionImmediate();
                        }
                        case NUMPAD0 -> player.placeBombPlayer4();
                    }
                }



                // === TOUCHES DE DEBUG (optionnel) ===
                switch (e.getCode()) {
                    case F1 -> printGameStatus();
                    case F2 -> printBonusStatus();
                }

            } catch (Exception ex) {
                System.err.println("Erreur lors du traitement des touches: " + ex.getMessage());
            }
        });
    }

    /**
     * Démarre les systèmes automatiques du jeu
     */
    private void startGameSystems()
    {
        // Démarrer le timer de vérification des bonus
        startBonusChecker();

        // Créer le bot si mode solo
        if (isSoloMode)
        {
            bot = new Bot(player, tiles);
            System.out.println("Bot IA créé pour le mode solo");
        }

        startTimer();
    }

    /**
     * Démarre le timer de jeu qui compte à rebours.
     * Affiche le temps restant dans le label du timer.
     */
    private void startTimer() {
        updateTimerLabel();
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeSeconds--;
            updateTimerLabel();
            if (timeSeconds <= 0) {
                gameTimer.stop();
                onTimerEnd();
            }
        }));
        gameTimer.setCycleCount(timeSeconds);
        gameTimer.play();
    }


    /**
     * Met à jour le label du timer avec le temps restant.
     * Format: MM:SS
     */
    private void updateTimerLabel() {
        int min = timeSeconds / 60;
        int sec = timeSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", min, sec));
    }

    /**
     * Gère la fin du timer : affiche un message et remplace la scène.
     */
    private void onTimerEnd() {
        timerLabel.setText("Temps écoulé !");

        BorderPane blackPane = new BorderPane();
        Label endLabel = new Label("Temps écoulé !");
        endLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");
        blackPane.setStyle("-fx-background-color: black;");
        blackPane.setCenter(endLabel);
        rootPane.getScene().setRoot(blackPane);
    }

    /**
     * Démarre le timer qui vérifie périodiquement si les joueurs ramassent des bonus.
     * Optimisé pour une meilleure performance et réactivité.
     */
    private void startBonusChecker()
    {
        bonusCheckTimer = new Timeline(new KeyFrame(
                Duration.millis(BONUS_CHECK_INTERVAL),
                e -> checkBonusCollection()
        ));
        bonusCheckTimer.setCycleCount(Timeline.INDEFINITE);
        bonusCheckTimer.play();

        System.out.println("Système de vérification des bonus démarré (intervalle: " + BONUS_CHECK_INTERVAL + "ms)");
    }

    /**
     * Vérifie si les joueurs sont sur des bonus et les applique.
     * Version optimisée avec gestion d'erreurs renforcée.
     */
    private void checkBonusCollection()
    {
        if (activeBonus.isEmpty()) return; // Optimisation : pas de bonus à vérifier

        Iterator<Bonus> iterator = activeBonus.iterator();
        int collectedCount = 0;

        while (iterator.hasNext())
        {
            try {
                Bonus bonus = iterator.next();
                if (bonus != null && bonus.checkAndApplyBonus(player))
                {
                    iterator.remove();
                    collectedCount++;

                    // Log spécifique pour l'invincibilité
                    if (bonus.getType() == Bonus.BonusType.INVINCIBILITY) {
                        System.out.println("BONUS INVINCIBILITÉ COLLECTÉ ! Durée: " + INVINCIBILITY_DURATION + " secondes");
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la vérification d'un bonus: " + e.getMessage());
                iterator.remove(); // Supprimer le bonus défectueux
            }
        }

        if (collectedCount > 0) {
            System.out.println(collectedCount + " bonus collecté(s). Bonus restants: " + activeBonus.size());
        }
    }

    /**
     * Vérification immédiate des bonus (appelée après chaque mouvement)
     */
    private void checkBonusCollectionImmediate()
    {
        checkBonusCollection();
    }

    /**
     * Ajoute un nouveau bonus à la liste des bonus actifs.
     * Appelé par la classe Bomb quand elle détruit des tuiles.
     * Version améliorée avec validation et logging.
     */
    public void addBonus(Bonus bonus)
    {
        if (bonus == null) {
            System.err.println("Tentative d'ajout d'un bonus null ignorée");
            return;
        }

        try {
            activeBonus.add(bonus);

            // Log détaillé selon le type de bonus
            String logMessage = "Nouveau bonus ajouté: " + bonus.getType().getDescription() +
                    " en position (" + bonus.getX() + ", " + bonus.getY() + ")";

            if (bonus.getType() == Bonus.BonusType.INVINCIBILITY) {
                logMessage += " [INVINCIBILITÉ - Durée: " + INVINCIBILITY_DURATION + "s]";
            }

            System.out.println(logMessage);
            System.out.println("Total bonus actifs: " + activeBonus.size());

        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du bonus: " + e.getMessage());
        }
    }

    /**
     * Supprime tous les bonus actifs (utile pour redémarrage de partie)
     */
    public void clearAllBonus()
    {
        int removedCount = activeBonus.size();
        activeBonus.clear();
        System.out.println(removedCount + " bonus supprimés de la carte");
    }

    /**
     * Arrête tous les timers du jeu (important pour éviter les fuites mémoire)
     */
    public void stopGameSystems()
    {
        if (bonusCheckTimer != null) {
            bonusCheckTimer.stop();
            System.out.println("Système de vérification des bonus arrêté");
        }

        clearAllBonus();
    }

    /**
     * Redémarre complètement le jeu
     */
    public void restart(Stage stage)
    {
        try {
            System.out.println("Redémarrage du jeu en cours...");

            // Arrêter les systèmes actuels
            stopGameSystems();

            // Créer une nouvelle instance
            Game newGame = new Game(this.isSoloMode, this.mapName);
            newGame.start(stage);

            System.out.println("Jeu redémarré avec succès !");

        } catch (Exception e) {
            System.err.println("Erreur lors du redémarrage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthodes pour mettre à jour les scores
    public void addScoreSolo(int points) {
        scoreSolo += points;
        if (scoreSoloLabel != null) {
            scoreSoloLabel.setText("Score : " + scoreSolo);
        }
    }
    public void addScoreMulti(int playerIndex, int points) {
        if (playerIndex >= 0 && playerIndex < 4) {
            scoreMulti[playerIndex] += points;
            if (scoreMultiLabels[playerIndex] != null) {
                scoreMultiLabels[playerIndex].setText("Joueur " + (playerIndex+1) + " : " + scoreMulti[playerIndex]);
            }
        }
    }

    // === MÉTHODES DE DEBUG ET MONITORING ===

    /**
     * Affiche le statut général du jeu
     */
    private void printGameStatus()
    {
        System.out.println("=== STATUT DU JEU ===");
        System.out.println("Mode: " + (isSoloMode ? "Solo" : "Multijoueur"));
        System.out.println("Bonus actifs: " + activeBonus.size());
        System.out.println("Joueur 1 - Position: (" + player.getX1() + "," + player.getY1() + ") | PV: " + player.getPv1() + " | Invincible: " + player.isInvinciblePlayer1());
        System.out.println("Joueur 2 - Position: (" + player.getX2() + "," + player.getY2() + ") | PV: " + player.getPv2() + " | Invincible: " + player.isInvinciblePlayer2());
        System.out.println("Durée invincibilité: " + INVINCIBILITY_DURATION + " secondes");
        System.out.println("===================");
    }

    /**
     * Affiche le statut détaillé des bonus
     */
    private void printBonusStatus()
    {
        System.out.println("=== STATUT DES BONUS ===");
        System.out.println("Nombre total de bonus actifs: " + activeBonus.size());

        for (int i = 0; i < activeBonus.size(); i++) {
            Bonus bonus = activeBonus.get(i);
            System.out.println("Bonus " + (i+1) + ": " + bonus.getType().getDescription() +
                    " à (" + bonus.getX() + "," + bonus.getY() + ")");
        }

        System.out.println("Système de collecte: " + (bonusCheckTimer != null && bonusCheckTimer.getStatus() == Timeline.Status.RUNNING ? "ACTIF" : "INACTIF"));
        System.out.println("========================");
    }

    /**
     * Définit le thème actuel pour les tuiles du jeu.
     * Cette méthode est utilisée pour appliquer un thème visuel au jeu.
     * @param themeName Le nom du thème à définir (par exemple, "BomberMan" ou "Manoir").
     */
    public void setCurrentThemes(String themeName)
    {
        this.currentTheme = themeName;
    }

    /**
     * Retourne le mode de jeu actuellement.
     * @return Le mode de jeu actuellement.
     */
    public boolean isSoloMode() {
        return isSoloMode;
    }
}
