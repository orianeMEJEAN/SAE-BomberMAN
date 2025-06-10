package com.example.BomberMAN;

import com.example.BomberMAN.GamePlay.Bot;
import com.example.BomberMAN.GamePlay.Player;
import com.example.BomberMAN.GamePlay.Tile;
import com.example.BomberMAN.Maps.MapLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * La classe Game représente le cœur du jeu BomberMan, gérant l'initialisation du plateau,
 * des joueurs, des tuiles, et les interactions principales du jeu.
 */
public class Game
{
    /** Taille d'une tuile en pixels. */
    public static final int TILE_SIZE = 40;

    /** Largeur de la grille (en nombre de tuiles). */
    public static final int GRID_WIDTH = 13;

    /** Hauteur de la grille (en nombre de tuiles). */
    public static final int GRID_HEIGHT = 11;

    /** Conteneur JavaFX représentant la grille de jeu. */
    private GridPane grid;
    /** Tableau des tuiles du plateau de jeu. */
    private Tile[][] tiles;
    /** Instance du joueur principal. */
    private Player player;
    /** Indique si le mode de jeu est en solo (true) ou multijoueur (false). */
    private boolean isSoloMode;
    /** Instance du bot contrôlé par l'IA en mode solo. */
    private Bot bot;

    /**
     * Constructeur de la classe Game.
     * Initialise le mode de jeu (solo ou multijoueur).
     *
     * @param isSoloMode Vrai pour lancer une partie en solo, faux pour multijoueur.
     */
    public Game(boolean isSoloMode)
    {
        this.isSoloMode = isSoloMode;
    }

    /**
     * Lance la partie et initialise tous les composants nécessaires :
     * le plateau de jeu (GridPane), les tuiles, le joueur, et configure les écouteurs
     * d'événements clavier pour les mouvements des joueurs et le placement des bombes.
     * Crée également un bot si le jeu est en mode solo.
     *
     * @param stage La fenêtre principale JavaFX (Stage) utilisée pour afficher le jeu.
     */
    public void start(Stage stage)
    {
        grid = new GridPane();

        Tile.loadAllTextures(); // Charger toutes les textures nécessaires pour les tuiles.
        // Charger la carte depuis un fichier spécifié.
        tiles = MapLoader.loadMap("src/main/resources/com/example/BomberMAN/BomberMAN/texture_Maps/map1.map");

        // Parcourir toutes les tuiles et les ajouter à la grille de jeu.
        for (int y = 0; y < tiles.length; y++)
        {
            for (int x = 0; x < tiles[y].length; x++)
            {
                grid.add(tiles[y][x].getRectangle(), x, y);
            }
        }

        // Initialisation du joueur avec ses positions de départ et la grille de jeu.
        player = new Player(1, 1, 11, 9, grid, tiles);

        // Création de la scène du jeu avec la grille et ses dimensions.
        Scene scene = new Scene(grid, 536, 454);

        // Gestion des touches clavier pour les mouvements des joueurs et le placement des bombes.
        scene.setOnKeyPressed(e -> {
            switch (e.getCode())
            {
                // Commandes du Joueur 1
                case Z -> player.movePlayer1(0, -1); // Déplacement vers le haut
                case S -> player.movePlayer1(0, 1);  // Déplacement vers le bas
                case Q -> player.movePlayer1(-1, 0); // Déplacement vers la gauche
                case D -> player.movePlayer1(1, 0);  // Déplacement vers la droite
                case SPACE -> player.placeBombPlayer1(); // Placement d'une bombe
            }

            // Si le mode n'est pas solo, configurer les commandes pour le Joueur 2.
            if (!isSoloMode)
            {
                switch (e.getCode())
                {
                    case UP -> player.movePlayer2(0, -1);    // Déplacement vers le haut
                    case DOWN -> player.movePlayer2(0, 1);  // Déplacement vers le bas
                    case LEFT -> player.movePlayer2(-1, 0); // Déplacement vers la gauche
                    case RIGHT -> player.movePlayer2(1, 0); // Déplacement vers la droite
                    case CONTROL -> player.placeBombPlayer2(); // Placement d'une bombe
                }
            }
        });

        // Créer l'instance du bot si le mode est solo.
        if (isSoloMode)
        {
            bot = new Bot(player, tiles);
        }

        // Définir le titre de la fenêtre en fonction du mode de jeu.
        stage.setTitle("BomberMan - " + (isSoloMode ? "Solo" : "Multijoueur"));
        stage.setScene(scene); // Définir la scène du jeu sur la fenêtre principale.
        stage.show(); // Afficher la fenêtre.
    }

    /**
     * Définit le thème actuel pour les tuiles du jeu.
     * Cette méthode est utilisée pour appliquer un thème visuel au jeu.
     * @param themeName Le nom du thème à définir (par exemple, "BomberMan" ou "Manoir").
     */
    public void setCurrentThemes(String themeName)
    {
        Tile.setCurrentTheme(themeName);
    }
}