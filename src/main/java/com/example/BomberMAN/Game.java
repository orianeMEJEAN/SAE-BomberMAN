package com.example.BomberMAN;

import com.example.BomberMAN.GamePlay.Bot;
import com.example.BomberMAN.GamePlay.Player;
import com.example.BomberMAN.GamePlay.Tile;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Game
{
    public static final int TILE_SIZE = 40;
    public static final int GRID_WIDTH = 13;
    public static final int GRID_HEIGHT = 11;

    private GridPane grid;
    private Tile[][] tiles;
    private Player player;
    private boolean isSoloMode;

    public Game(boolean isSoloMode)
    {
        this.isSoloMode = isSoloMode;
    }

    public void start(Stage stage)
    {
        grid = new GridPane();
        tiles = new Tile[GRID_HEIGHT][GRID_WIDTH];

        for (int y = 0; y < GRID_HEIGHT; y++)
        {
            for (int x = 0; x < GRID_WIDTH; x++)
            {
                Tile tile = new Tile(x, y);

                if (x == 0 || y == 0 || x == GRID_WIDTH - 1 || y == GRID_HEIGHT - 1)
                {
                    tile.setType(Tile.Type.WALL);
                }
                else if ((x == 1 || y == 1) && (x == 2 || y == 1) && (x == 1 || y == 2) && (x == 9 || y == 11) && (x == 11 || y == 8) && (x == 10 || y == 9))
                {
                    tile.setType(Tile.Type.EMPTY);
                }
                else if ((x != 1 || y != 1) && (x != 2 || y != 1) && (x != 1 || y != 2) && (x != 11 || y != 9) && (x != 11 || y != 8) && (x != 10 || y != 9) && Math.random() < 0.2)
                {
                    tile.setType(Tile.Type.BREAKABLE);
                }
                else if ((x != 1 || y != 1) && (x != 2 || y != 1) && (x != 1 || y != 2) && (x != 11 || y != 9) && (x != 11 || y != 8) && (x != 10 || y != 9) && Math.random() < 0.2)
                {
                    tile.setType(Tile.Type.WALL);
                }

                tiles[y][x] = tile;
                grid.add(tile.getRectangle(), x, y);
            }
        }

        // Création de l'objet Player
        player = new Player(1, 1, 11, 9, grid, tiles);

        Scene scene = new Scene(grid, 536, 454);

        scene.setOnKeyPressed(e -> {
            // Joueur 1 (toujours actif)
            switch (e.getCode())
            {
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
            new Bot(player);
        }

        stage.setTitle("BomberMan - " + (isSoloMode ? "Solo" : "Multijoueur"));
        stage.setScene(scene);
        stage.show();
    }
}