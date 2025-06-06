package com.example.BomberMAN.GamePlay;

import com.almasb.fxgl.texture.NineSliceTextureBuilder;
import com.example.BomberMAN.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.application.Platform;
import java.util.Optional;

public class Player {
    private int x1, y1;
    private int x2, y2;
    private int pv1 = 1, pv2 = 1;

    private ImageView sprite, sprite2;
    private GridPane grid;

    private Image imgUp, imgDown, imgLeft, imgRight, imgDefault;
    private Image imgUp2, imgDown2, imgLeft2, imgRight2, imgDefault2;

    private boolean canMove1 = true;
    private boolean canMove2 = true;

    private Tile[][] tiles;

    public Player(int x, int y, int x2, int y2, GridPane grid, Tile[][] tiles) {
        this.x1 = x;
        this.y1 = y;
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

    public void movePlayer1(int dx, int dy) {
        if (!canMove1) return;

        int newX = x1 + dx;
        int newY = y1 + dy;

        if (newX < 0 || newX >= Game.GRID_WIDTH || newY < 0 || newY >= Game.GRID_HEIGHT) return;
        if (!tiles[newY][newX].isWalkable()) return;

        if (dx == -1) sprite.setImage(imgLeft);
        else if (dx == 1) sprite.setImage(imgRight);
        else if (dy == -1) sprite.setImage(imgUp);
        else if (dy == 1) sprite.setImage(imgDown);

        x1 = newX;
        y1 = newY;
        GridPane.setColumnIndex(sprite, x1);
        GridPane.setRowIndex(sprite, y1);
    }

    public void movePlayer2(int dx, int dy) {
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

    public void placeBombPlayer1() {
        if (!canMove1) return;

        lockMovement1();
        final int bombX = x1;
        final int bombY = y1;

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
            grid.add(sprite, x1, y1);
            sprite.setVisible(true);
            unlockMovement1();
        }));
        waitGIF.setCycleCount(1);
        waitGIF.play();
    }

    public void placeBombPlayer2() {
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


    public void deathJ1() {
        if (pv1 <= 0) {
            canMove1 = false;
            sprite.setVisible(false);  // Correct : cache le sprite du joueur 1
            System.out.println("Joueur 1 éliminé....");

            // Utiliser Platform.runLater pour s'assurer que l'alerte s'affiche sur le thread JavaFX
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fin de partie");
                alert.setHeaderText(null);
                alert.setContentText("Le joueur 2 a gagné !");
                alert.showAndWait();

                // Arrêter le programme après fermeture de la popup
                Platform.exit();
            });
        }
    }

    public void deathJ2() {
        if (pv2 <= 0) {
            canMove2 = false;
            sprite2.setVisible(false);  // Correction : cache le sprite du joueur 2, pas celui du joueur 1
            System.out.println("Joueur 2 éliminé....");

            // Utiliser Platform.runLater pour s'assurer que l'alerte s'affiche sur le thread JavaFX
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fin de partie");
                alert.setHeaderText(null);
                alert.setContentText("Le joueur 1 a gagné !");
                alert.showAndWait();

                // Arrêter le programme après fermeture de la popup
                Platform.exit();
            });
        }
    }

    public void lockMovement1() { canMove1 = false; }
    public void unlockMovement1() { canMove1 = true; }
    public void lockMovement2() { canMove2 = false; }
    public void unlockMovement2() { canMove2 = true; }

    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }

    public int getPv1() { return pv1; }
    public int getPv2() { return pv2; }
    public void setPv1(int pv) { this.pv1 = pv; }
    public void setPv2(int pv) { this.pv2 = pv; }
}
