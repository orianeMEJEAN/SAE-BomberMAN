package com.example.BomberMAN;

import com.example.BomberMAN.GamePlay.Player;
import com.example.BomberMAN.GamePlay.Tile;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
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
                else if ((x == 1 || y == 1) && (x == 2 || y == 1) && (x == 1 || y == 2) && (x == 9 || y == 11) && (x == 8 || y == 1) && (x == 9 || y == 10))
                {
                    tile.setType(Tile.Type.EMPTY);
                }
                else if ((x != 1 || y != 1) && (x != 2 || y != 1) && (x != 1 || y != 2) && Math.random() < 0.2)
                {
                    tile.setType(Tile.Type.BREAKABLE);
                }
                else if ((x != 1 || y != 1) && (x != 2 || y != 1) && (x != 1 || y != 2) && Math.random() < 0.2)
                {
                    tile.setType(Tile.Type.WALL);
                }

                tiles[y][x] = tile;
                grid.add(tile.getRectangle(), x, y);
            }
        }

        // Création d’un seul objet Player pour les 2 joueurs
        player = new Player(1, 1, 11, 9, grid, tiles);

        Scene scene = new Scene(grid, 536, 454);

        // Contrôles clavier pour les 2 joueurs
        scene.setOnKeyPressed(e -> {
            // Joueur 1 (flèches + CTRL)
            if (e.getCode() == KeyCode.UP) player.movePlayer1(0, -1);
            if (e.getCode() == KeyCode.DOWN) player.movePlayer1(0, 1);
            if (e.getCode() == KeyCode.LEFT) player.movePlayer1(-1, 0);
            if (e.getCode() == KeyCode.RIGHT) player.movePlayer1(1, 0);
            if (e.getCode() == KeyCode.CONTROL) player.placeBombPlayer1();

            // Joueur 2 (ZQSD + Espace)
            if (e.getCode() == KeyCode.Z) player.movePlayer2(0, -1);
            if (e.getCode() == KeyCode.S) player.movePlayer2(0, 1);
            if (e.getCode() == KeyCode.Q) player.movePlayer2(-1, 0);
            if (e.getCode() == KeyCode.D) player.movePlayer2(1, 0);
            if (e.getCode() == KeyCode.SPACE) player.placeBombPlayer2();
        });

        stage.setTitle("BomberMan - G1-4");
        stage.setScene(scene);
        stage.show();
    }
}
