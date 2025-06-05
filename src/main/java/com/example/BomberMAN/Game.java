package com.example.BomberMAN;

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

                else if ((x != 1 || y != 1) && Math.random() < 0.2)
                {
                    tile.setType(Tile.Type.BREAKABLE);
                }

                tiles[y][x] = tile;
                grid.add(tile.getRectangle(), x, y);
            }
        }

        player = new Player(1, 1, grid, tiles);

        Scene scene = new Scene(grid, 536, 454);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) player.move(0, -1);
            if (e.getCode() == KeyCode.DOWN) player.move(0, 1);
            if (e.getCode() == KeyCode.LEFT) player.move(-1, 0);
            if (e.getCode() == KeyCode.RIGHT) player.move(1, 0);
            if (e.getCode() == KeyCode.SPACE) player.placeBomb(tiles);
        });

        stage.setTitle("BomberMan - G1-4");
        stage.setScene(scene);
        stage.show();
    }
}