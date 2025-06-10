package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 * Représente une bombe placée sur la grille, qui explose après un court délai.
 */
public class Bomb
{
    private ImageView bombSprite;
    private GridPane grid;
    private Tile[][] tiles;
    private Player player;
    private boolean active = true;
    private int bombX, bombY; // Position de la bombe
    private int playerNumber; // 1 ou 2, pour identifier quel joueur a posé la bombe

    /**
     * Initialise une bombe aux coordonnées spécifiées sur la grille.
     * @param x Position horizontale de la bombe
     * @param y Position verticale de la bombe
     * @param grid Grille de jeu (interface)
     * @param tiles Grille logique des tuiles
     * @param player Référence au joueur (non utilisé dans cette version, à évaluer)
     */
    public Bomb(int x, int y, GridPane grid, Tile[][] tiles, Player player)
    {
        this.grid = grid;
        this.tiles = tiles;
        this.player = player;

        // Déterminer quel joueur a posé la bombe
        if (x == player.getX1() && y == player.getY1()) {
            this.playerNumber = 1;
        } else {
            this.playerNumber = 2;
        }
        
        Image bombImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/bomb.png").toExternalForm());
        bombSprite = new ImageView(bombImage);
        bombSprite.setFitWidth(Game.TILE_SIZE);
        bombSprite.setFitHeight(Game.TILE_SIZE);

        grid.add(bombSprite, x, y);

        Timeline explosionDelay = new Timeline(new KeyFrame(Duration.seconds(2), ev -> explode(x, y)));
        explosionDelay.setCycleCount(1);
        explosionDelay.play();
    }

    /**
     * Gère l'explosion de la bombe : animation, suppression de tuiles cassables, etc.
     * @param x Position horizontale d'explosion (même que la bombe)
     * @param y Position verticale d'explosion
     */
    private void explode(int x, int y)
    {
        grid.getChildren().remove(bombSprite);

        Image explosionImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/explosion.png").toExternalForm());
        ImageView explosionSprite = new ImageView(explosionImage);
        explosionSprite.setFitWidth(Game.TILE_SIZE * 3);
        explosionSprite.setFitHeight(Game.TILE_SIZE * 3);

        int centerX = x - 1;
        int centerY = y - 1;

        grid.add(explosionSprite, centerX, centerY, 3, 3);

        Timeline clear = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            grid.getChildren().remove(explosionSprite);
        }));
        clear.setCycleCount(1);
        clear.play();

        // Libérer le joueur qui a posé cette bombe
        if (playerNumber == 1) {
            player.releaseBombPlayer1();
        } else {
            player.releaseBombPlayer2();
        }

        int[][] directions = {
                {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] dir : directions)
        {
            int dx = x + dir[0];
            int dy = y + dir[1];

            if (dx >= 0 && dx < Game.GRID_WIDTH && dy >= 0 && dy < Game.GRID_HEIGHT)
            {
                Tile tile = tiles[dy][dx];
                if (tile != null && tile.isBreakable())
                {
                    tile.destroy();
                }

                // Vérifie si un joueur est touché
                if (dx == player.getX1() && dy == player.getY1())
                {
                    player.setPv1(0);
                    player.deathJ1();
                }
                else if (dx == player.getX2() && dy == player.getY2())
                {
                    player.setPv2(0);
                    player.deathJ2();
                }
            }
        }
        active = false;
    }
}