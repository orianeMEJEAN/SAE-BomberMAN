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
    private int x, y;   // Joueur 1
    private int x2, y2; // Joueur 2

    private ImageView sprite, sprite2;
    private GridPane grid;

    private Image imgUp, imgDown, imgLeft, imgRight, imgDefault;
    private Image imgUp2, imgDown2, imgLeft2, imgRight2, imgDefault2;

    private boolean canMove1 = true;
    private boolean canMove2 = true;

    private Tile[][] tiles;

    public Player(int x, int y, int x2, int y2, GridPane grid, Tile[][] tiles)
    {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.grid = grid;
        this.tiles = tiles;

        // Joueur 1
        imgDefault = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player.png").toExternalForm());
        imgUp = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player_up.png").toExternalForm());
        imgDown = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player_down.png").toExternalForm());
        imgLeft = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player_left.png").toExternalForm());
        imgRight = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player_right.png").toExternalForm());

        sprite = new ImageView(imgDefault);
        sprite.setFitWidth(40);
        sprite.setFitHeight(40);
        grid.add(sprite, x, y);

        // Joueur 2
        imgDefault2 = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J2/Player2-default.png").toExternalForm());
        imgUp2 = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J2/Player2-up.png").toExternalForm());
        imgDown2 = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J2/Player2-down.png").toExternalForm());
        imgLeft2 = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J2/Player2-left.png").toExternalForm());
        imgRight2 = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J2/Player2-right.png").toExternalForm());

        sprite2 = new ImageView(imgDefault2);
        sprite2.setFitWidth(40);
        sprite2.setFitHeight(40);
        grid.add(sprite2, x2, y2);
    }

    public void movePlayer1(int dx, int dy)
    {
        if (!canMove1) return;

        int newX = x + dx;
        int newY = y + dy;

        if (newX < 0 || newX >= Game.GRID_WIDTH || newY < 0 || newY >= Game.GRID_HEIGHT) return;
        if (!tiles[newY][newX].isWalkable()) return;

        if (dx == -1) sprite.setImage(imgLeft);
        else if (dx == 1) sprite.setImage(imgRight);
        else if (dy == -1) sprite.setImage(imgUp);
        else if (dy == 1) sprite.setImage(imgDown);

        x = newX;
        y = newY;
        GridPane.setColumnIndex(sprite, x);
        GridPane.setRowIndex(sprite, y);
    }

    public void movePlayer2(int dx, int dy)
    {
        if (!canMove2) return;

        int newX = x2 + dx;
        int newY = y2 + dy;

        if (newX < 0 || newX >= Game.GRID_WIDTH || newY < 0 || newY >= Game.GRID_HEIGHT) return;
        if (!tiles[newY][newX].isWalkable()) return;

        if (dx == -1) sprite2.setImage(imgLeft2);
        else if (dx == 1) sprite2.setImage(imgRight2);
        else if (dy == -1) sprite2.setImage(imgUp2);
        else if (dy == 1) sprite2.setImage(imgDown2);

        x2 = newX;
        y2 = newY;
        GridPane.setColumnIndex(sprite2, x2);
        GridPane.setRowIndex(sprite2, y2);
    }

    public void placeBombPlayer1()
    {
        if (!canMove1) return;

        lockMovement1();
        final int bombX = x;
        final int bombY = y;

        sprite.setVisible(false);

        Image gif = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/BomberManV2.gif").toExternalForm());
        ImageView gifView = new ImageView(gif);
        gifView.setFitWidth(40);
        gifView.setFitHeight(40);
        grid.add(gifView, bombX, bombY);

        Timeline waitGIF = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            grid.getChildren().remove(gifView);
            new Bomb(bombX, bombY, grid, tiles, this);
            grid.getChildren().remove(sprite);
            grid.add(sprite, x, y);
            sprite.setVisible(true);
            unlockMovement1();
        }));
        waitGIF.setCycleCount(1);
        waitGIF.play();
    }

    public void placeBombPlayer2()
    {
        if (!canMove2) return;

        lockMovement2();
        final int bombX = x2;
        final int bombY = y2;

        sprite2.setVisible(false);

        Image gif = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J2/BomberManP2V1.gif").toExternalForm());
        ImageView gifView = new ImageView(gif);
        gifView.setFitWidth(40);
        gifView.setFitHeight(40);
        grid.add(gifView, bombX, bombY);

        Timeline waitGIF = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            grid.getChildren().remove(gifView);
            new Bomb(bombX, bombY, grid, tiles, this);
            grid.getChildren().remove(sprite2);
            grid.add(sprite2, x2, y2);
            sprite2.setVisible(true);
            unlockMovement2();
        }));
        waitGIF.setCycleCount(1);
        waitGIF.play();
    }

    public void lockMovement1() { canMove1 = false; }
    public void unlockMovement1() { canMove1 = true; }
    public void lockMovement2() { canMove2 = false; }
    public void unlockMovement2() { canMove2 = true; }

    public ImageView getSprite1() { return sprite; }
    public ImageView getSprite2() { return sprite2; }

    public int getX1() { return x; }
    public int getY1() { return y; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }
}
