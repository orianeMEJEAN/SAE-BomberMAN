package com.example.BomberMAN;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class Bomb
{
    private ImageView bombSprite;
    private GridPane grid;
    private Tile[][] tiles;

    public Bomb(int x, int y, GridPane grid, Tile[][] tiles, Player player)
    {
        this.grid = grid;
        this.tiles = tiles;

        Image bombImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/bomb.png").toExternalForm());
        bombSprite = new ImageView(bombImage);
        bombSprite.setFitWidth(Game.TILE_SIZE);
        bombSprite.setFitHeight(Game.TILE_SIZE);
        grid.add(bombSprite, x, y);

        Timeline explosionDelay = new Timeline(new KeyFrame(Duration.seconds(1), ev -> explode(x, y)));
        explosionDelay.setCycleCount(1);
        explosionDelay.play();
    }

    private void explode(int x, int y)
    {
        grid.getChildren().remove(bombSprite);

        // Explosion visuelle (affichée même si elle dépasse)
        Image explosionImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/explosion.png").toExternalForm());
        ImageView explosionSprite = new ImageView(explosionImage);
        explosionSprite.setFitWidth(Game.TILE_SIZE * 3);
        explosionSprite.setFitHeight(Game.TILE_SIZE * 3);

        int centerX = x - 1;
        int centerY = y - 1;

        // 💥 Affiche toujours l’explosion (quitte à ce qu’elle dépasse)
        grid.add(explosionSprite, centerX, centerY, 3, 3);

        // Explosion visuelle retirée après 0.5s
        Timeline clear = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            grid.getChildren().remove(explosionSprite);
        }));
        clear.setCycleCount(1);
        clear.play();

        // 💣 Casse les blocs autour (même si explosion dépasse)
        int[][] directions = {
                {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] dir : directions) {
            int dx = x + dir[0];
            int dy = y + dir[1];

            if (dx >= 0 && dx < Game.GRID_WIDTH && dy >= 0 && dy < Game.GRID_HEIGHT) {
                Tile tile = tiles[dy][dx];
                if (tile != null && tile.isBreakable()) {
                    tile.destroy();
                }
            }
        }
    }

}