package com.example.BomberMAN;

import com.example.BomberMAN.GamePlay.Bot;
import com.example.BomberMAN.GamePlay.Player;
import com.example.BomberMAN.GamePlay.Tile;
import com.example.BomberMAN.Maps.MapLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Game
{
    /** Taille d'une tuile en pixels. */
    public static final int TILE_SIZE = 40;

    /** Largeur de la grille (en nombre de tuiles). */
    public static final int GRID_WIDTH = 13;

    /** Hauteur de la grille (en nombre de tuiles). */
    public static final int GRID_HEIGHT = 11;

    private GridPane grid;           /**< Conteneur JavaFX représentant la grille de jeu. */
    private Tile[][] tiles;          /**< Tableau des tuiles du plateau. */
    private Player player;           /**< Joueur principal. */
    private boolean isSoloMode;      /**< Mode solo activé ou non. */
    private Bot bot;                 /**< Bot contrôlé par l'IA en mode solo. */

    /**
     * Constructeur de la classe Game.
     *
     * @param isSoloMode True pour lancer une partie en solo, false pour multijoueur.
     */
    public Game(boolean isSoloMode)
    {
        this.isSoloMode = isSoloMode;
    }

    /**
     * Lance la partie et initialise le plateau, le joueur, les murs, les briques et le bot si besoin.
     *
     * @param stage Fenêtre principale JavaFX utilisée pour afficher le jeu.
     */
    public void start(Stage stage)
    {
        grid = new GridPane();

        Tile.loadTextures(); // charger les textures d’abord
        tiles = MapLoader.loadMap("src/main/resources/com/example/BomberMAN/BomberMAN/texture_Maps/map1.map");

        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                grid.add(tiles[y][x].getRectangle(), x, y);
            }
        }

        player = new Player(1, 1, 11, 9, grid, tiles);

        Scene scene = new Scene(grid, 536, 454);

        // Gestion des touches clavier
        scene.setOnKeyPressed(e -> {
            switch (e.getCode())
            {
                // Commandes Joueur 1
                case Z -> player.movePlayer1(0, -1);
                case S -> player.movePlayer1(0, 1);
                case Q -> player.movePlayer1(-1, 0);
                case D -> player.movePlayer1(1, 0);
                case SPACE -> player.placeBombPlayer1();
            }

            if (!isSoloMode)
            {
                // Joueur 2 (si multijoueur)
                switch (e.getCode())
                {
                    case UP -> player.movePlayer2(0, -1);
                    case DOWN -> player.movePlayer2(0, 1);
                    case LEFT -> player.movePlayer2(-1, 0);
                    case RIGHT -> player.movePlayer2(1, 0);
                    case CONTROL -> player.placeBombPlayer2();
                }
            }
        });

        // Créer le bot si mode solo
        if (isSoloMode)
        {
            bot = new Bot(player, tiles);
        }

        stage.setTitle("BomberMan - " + (isSoloMode ? "Solo" : "Multijoueur"));
        stage.setScene(scene);
        stage.show();
    }
}