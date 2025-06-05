package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class Player
{
    private int x, y;
    private ImageView sprite;
    private GridPane grid;

    private Image imgUp, imgDown, imgLeft, imgRight, imgDefault;
    private boolean canMove = true;
    private Tile[][] tiles;

    public Player(int x, int y, GridPane grid, Tile[][] tiles)
    {
        this.x = x;
        this.y = y;
        this.grid = grid;
        this.tiles = tiles;

        imgDefault = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player.png").toExternalForm());
        imgUp = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player_up.png").toExternalForm());
        imgDown = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player_down.png").toExternalForm());
        imgLeft = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player_left.png").toExternalForm());
        imgRight = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player_right.png").toExternalForm());

        sprite = new ImageView(imgDefault);
        sprite.setFitWidth(40);
        sprite.setFitHeight(40);

        grid.add(sprite, x, y);
    }

    public void move(int dx, int dy)
    {
        if (!canMove) return;

        int newX = x + dx;
        int newY = y + dy;

        if (newX < 0 || newX >= Game.GRID_WIDTH || newY < 0 || newY >= Game.GRID_HEIGHT)
        {
            return;
        }

        if (!tiles[newY][newX].isWalkable())
        {
            return;
        }

        if (dx == -1) sprite.setImage(imgLeft);
        else if (dx == 1) sprite.setImage(imgRight);
        else if (dy == -1) sprite.setImage(imgUp);
        else if (dy == 1) sprite.setImage(imgDown);
        else sprite.setImage(imgDefault);

        x = newX;
        y = newY;
        GridPane.setColumnIndex(sprite, x);
        GridPane.setRowIndex(sprite, y);
    }


    public void placeBomb(Tile[][] tiles)
    {
        if (canMove)
        {
            lockMovement();

            final int bombX = x;
            final int bombY = y;

            sprite.setVisible(false);

            Image gif = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/BomberManV2.gif").toExternalForm());
            ImageView gifView = new ImageView(gif);
            gifView.setPreserveRatio(false);
            gifView.setFitWidth(40);
            gifView.setFitHeight(40);
            grid.add(gifView, bombX, bombY);

            Timeline waitGIF = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                grid.getChildren().remove(gifView);

                new Bomb(bombX, bombY, grid, tiles, this);
                grid.getChildren().remove(sprite);
                grid.add(sprite, x, y);
                sprite.setVisible(true);
                unlockMovement();
            }));
            waitGIF.setCycleCount(1);
            waitGIF.play();
        }
    }

    public boolean canMove()
    {
        return canMove;
    }

    public void lockMovement()
    {
        canMove = false;
    }

    public void unlockMovement()
    {
        canMove = true;
    }

    public ImageView getSprite()
    {
        return sprite;
    }
}