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

        // Chargement de l'image de la bombe
        Image bombImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/bomb.png").toExternalForm());
        bombSprite = new ImageView(bombImage);
        bombSprite.setFitWidth(Game.TILE_SIZE);
        bombSprite.setFitHeight(Game.TILE_SIZE);

        // Placement de la bombe dans la grille
        grid.add(bombSprite, x, y);

        // Déclenchement de l'explosion après 1 seconde
        Timeline explosionDelay = new Timeline(new KeyFrame(Duration.seconds(1), ev -> explode(x, y)));
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
        // Suppression de l'image de la bombe
        grid.getChildren().remove(bombSprite);

        // Affichage de l'explosion (visuellement sur 3x3 tuiles)
        Image explosionImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/explosion.png").toExternalForm());
        ImageView explosionSprite = new ImageView(explosionImage);
        explosionSprite.setFitWidth(Game.TILE_SIZE * 3);
        explosionSprite.setFitHeight(Game.TILE_SIZE * 3);

        int centerX = x - 1;
        int centerY = y - 1;

        // Affichage de l'explosion dans la grille
        grid.add(explosionSprite, centerX, centerY, 3, 3);

        // Retrait de l'explosion après 0.5 seconde
        Timeline clear = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            grid.getChildren().remove(explosionSprite);
        }));
        clear.setCycleCount(1);
        clear.play();

        // Définition des directions touchées par l'explosion (croix + centre)
        int[][] directions = {
                {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] dir : directions)
        {
            int dx = x + dir[0];
            int dy = y + dir[1];

            // Vérifie les limites de la grille
            if (dx >= 0 && dx < Game.GRID_WIDTH && dy >= 0 && dy < Game.GRID_HEIGHT) {
                Tile tile = tiles[dy][dx];
                if (tile != null && tile.isBreakable()) {
                    // Suppression des tuiles cassables
                    tile.destroy();
                }
            }
        }
    }
}