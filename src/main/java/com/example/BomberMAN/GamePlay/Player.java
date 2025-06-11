package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Pos;


/**
 * Classe représentant les deux joueurs du jeu BomberMAN.
 * Gère le déplacement, la pose de bombes, la gestion des vies, et l'affichage de l'écran de fin.
 */
public class Player
{
    // Position des deux joueurs
    private int x1, y1; // Joueur 1
    private int x2, y2; // Joueur 2
//    private int x3, y3; // Joueur 3
//    private int x4, y4; // Joueur 4

    // Points de vie des joueurs
    private int pv1 = 2, pv2 = 2;
//  private int pv3 = 2, pv4 = 2;

    // Sprites des joueurs
    private ImageView sprite, sprite2;
//    private ImageView sprite3, sprite4;

    // Grille du jeu et carte des tuiles
    private GridPane grid;
    private Tile[][] tiles;

    // Images pour les directions du joueur 1
    private Image imgUp, imgDown, imgLeft, imgRight, imgDefault;

    // Images pour les directions du joueur 2
    private Image imgUp2, imgDown2, imgLeft2, imgRight2, imgDefault2;

//    // Images pour les directions du joueur 3
//    private Image imgUp3, imgDown3, imgLeft3, imgRight3, imgDefault3;
//
//    // Images pour les directions du joueur 4
//    private Image imgUp4, imgDown4, imgLeft4, imgRight4, imgDefault4;

    // Contrôle des mouvements
    private boolean canMove1 = true;
    private boolean canMove2 = true;
//    private boolean canMove3 = true;
//    private boolean canMove4 = true;

    private boolean bombCooldownPlayer1 = false;
    private boolean bombCooldownPlayer2 = false;
    private boolean isMoving = true;

    // Contrôle des bombes - Système de bombes multiples
    private int currentBombsPlayer1 = 0;
    private int currentBombsPlayer2 = 0;
    private int maxBombsPlayer1 = 1;
    private int maxBombsPlayer2 = 1;

    // Système d'invincibilité
    private boolean isInvinciblePlayer1 = false;
    private boolean isInvinciblePlayer2 = false;
    private Timeline invincibilityTimerPlayer1;
    private Timeline invincibilityTimerPlayer2;

    // Nouveau champ pour le thème actuel
    private String currentTheme;
    private Game game;

    /**
     * Constructeur du joueur.
     *
     * @param x     Position X initiale du joueur 1
     * @param y     Position Y initiale du joueur 1
     * @param x2    Position X initiale du joueur 2
     * @param y2    Position Y initiale du joueur 2
     * @param grid  Grille de jeu
     * @param tiles Carte des tuiles
     * @param game
     */
    public Player(int x, int y, int x2, int y2, GridPane grid, Tile[][] tiles, Game game, String theme)
    {
        this.x1 = x;
        this.y1 = y;
        this.x2 = x2;
        this.y2 = y2;
//        this.x3 = x3;
//        this.y3 = y3;
//        this.x4 = x4;
//        this.y4 = y4;

        this.grid = grid;
        this.tiles = tiles;
        this.game = game;
        this.currentTheme = theme;

        loadImages();
        createSprites();
    }

    /**
     * Charge toutes les images nécessaires pour les joueurs
     */
    private void loadImages()
    {
        // Chemin de base pour les ressources des joueurs
        String basePathJ1 = "/com/example/BomberMAN/BomberMAN/J1/" + currentTheme + "/";
        String basePathJ2 = "/com/example/BomberMAN/BomberMAN/J2/" + currentTheme + "/";

        // Chargement des images du joueur 1
        imgDefault = new Image(getClass().getResource(basePathJ1 + "player.png").toExternalForm());
        imgUp = new Image(getClass().getResource(basePathJ1 + "player_up.png").toExternalForm());
        imgDown = new Image(getClass().getResource(basePathJ1 + "player_down.png").toExternalForm());
        imgLeft = new Image(getClass().getResource(basePathJ1 + "player_left.png").toExternalForm());
        imgRight = new Image(getClass().getResource(basePathJ1 + "player_right.png").toExternalForm());

        // Chargement des images du joueur 2
        imgDefault2 = new Image(getClass().getResource(basePathJ2 + "Player2-default.png").toExternalForm());
        imgUp2 = new Image(getClass().getResource(basePathJ2 + "Player2-up.png").toExternalForm());
        imgDown2 = new Image(getClass().getResource(basePathJ2 + "Player2-down.png").toExternalForm());
        imgLeft2 = new Image(getClass().getResource(basePathJ2 + "Player2-left.png").toExternalForm());
        imgRight2 = new Image(getClass().getResource(basePathJ2 + "Player2-right.png").toExternalForm());
    }

    /**
     * Crée les sprites des joueurs et les place sur la grille
     */
    private void createSprites()
    {
        if(currentTheme == "Manoir")
        {
            // Sprite du joueur 1
            sprite = new ImageView(imgDefault);
            sprite.setFitWidth(25);
            sprite.setFitHeight(40);
            grid.add(sprite, x1, y1);

            // Sprite du joueur 2
            sprite2 = new ImageView(imgDefault2);
            sprite2.setFitWidth(25);
            sprite2.setFitHeight(40);
            grid.add(sprite2, x2, y2);
        }
        else
        {
            // Sprite du joueur 1
            sprite = new ImageView(imgDefault);
            sprite.setFitWidth(40);
            sprite.setFitHeight(40);
            grid.add(sprite, x1, y1);

            // Sprite du joueur 2
            sprite2 = new ImageView(imgDefault2);
            sprite2.setFitWidth(40);
            sprite2.setFitHeight(40);
            grid.add(sprite2, x2, y2);
        }
    }

    /**
     * Déplace le joueur 1 si possible dans la direction donnée.
     * @param dx Direction en X (-1 gauche, +1 droite)
     * @param dy Direction en Y (-1 haut, +1 bas)
     */
    public void movePlayer1(int dx, int dy)
    {
        if (!isMoving) return;
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

    /**
     * Déplace le joueur 2 si possible dans la direction donnée.
     * @param dx Direction en X
     * @param dy Direction en Y
     */
    public void movePlayer2(int dx, int dy)
    {
        if (!isMoving) return;
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

    /**
     * Place une bombe à la position actuelle du joueur 1.
     */
    public void placeBombPlayer1()
    {
        if (!canMove1 || bombCooldownPlayer1 ||currentBombsPlayer1 >= maxBombsPlayer1) return;
        bombCooldownPlayer1 = true;

        lockMovement1();
        currentBombsPlayer1++;
        final int bombX = x1;
        final int bombY = y1;

        sprite.setVisible(false);

        // Chemin de l'image GIF de la bombe du joueur 1 en fonction du thème
        Image gif = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/" + currentTheme + "/BomberManV2.gif").toExternalForm());
        ImageView gifView = new ImageView(gif);
        gifView.setFitWidth(40);
        gifView.setFitHeight(40);
        grid.add(gifView, bombX, bombY);

        Timeline waitGIF = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            grid.getChildren().remove(gifView);

            // NOUVEAU: Passer la référence du jeu à la bombe
            new Bomb(bombX, bombY, grid, tiles, this, game);

            grid.getChildren().remove(sprite);
            grid.add(sprite, x1, y1);
            sprite.setVisible(true);
            unlockMovement1();
        }));
        waitGIF.setCycleCount(1);
        waitGIF.play();

        Timeline cd = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> bombCooldownPlayer1 = false));
        cd.setCycleCount(1);
        cd.play();
    }

    /**
     * Place une bombe à la position actuelle du joueur 2.
     */
    public void placeBombPlayer2()
    {
        if (!canMove2|| bombCooldownPlayer2 || currentBombsPlayer2 >= maxBombsPlayer2) return;
        bombCooldownPlayer2 = true;

        lockMovement2();
        currentBombsPlayer2++;
        final int bombX = x2;
        final int bombY = y2;

        sprite2.setVisible(false);

        // Chemin de l'image GIF de la bombe du joueur 2 en fonction du thème
        Image gif = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J2/" + currentTheme + "/BomberManP2V1.gif").toExternalForm());
        ImageView gifView = new ImageView(gif);
        gifView.setFitWidth(40);
        gifView.setFitHeight(40);
        grid.add(gifView, bombX, bombY);

        Timeline waitGIF = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            grid.getChildren().remove(gifView);
            new Bomb(bombX, bombY, grid, tiles, this, game);
            grid.getChildren().remove(sprite2);
            grid.add(sprite2, x2, y2);
            sprite2.setVisible(true);
            unlockMovement2();
        }));
        waitGIF.setCycleCount(1);
        waitGIF.play();

        Timeline cd = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> bombCooldownPlayer2 = false));
        cd.setCycleCount(1);
        cd.play();
    }

    /**
     * Appelée par la bombe quand elle explose pour libérer le joueur 1
     */
    public void releaseBombPlayer1()
    {
        if (currentBombsPlayer1 > 0) {
            currentBombsPlayer1--;
        }
    }

    /**
     * Appelée par la bombe quand elle explose pour libérer le joueur 2
     */
    public void releaseBombPlayer2()
    {
        if (currentBombsPlayer2 > 0) {
            currentBombsPlayer2--;
        }
    }

    /**
     * Augmente la limite de bombes du joueur 1
     */
    public void increaseBombLimitPlayer1()
    {
        maxBombsPlayer1++;
    }

    /**
     * Augmente la limite de bombes du joueur 2
     */
    public void increaseBombLimitPlayer2()
    {
        maxBombsPlayer2++;
    }

    /**
     * Active l'invincibilité pour le joueur 1 pendant 5 secondes
     */
    public void activateInvincibilityPlayer1()
    {
        // Si déjà invincible, arrêter le timer précédent
        if (invincibilityTimerPlayer1 != null) {
            invincibilityTimerPlayer1.stop();
        }

        isInvinciblePlayer1 = true;

        // Effet visuel : faire clignoter le sprite
        Timeline blinkTimer = new Timeline(
                new KeyFrame(Duration.millis(200), e -> sprite.setOpacity(0.3)),
                new KeyFrame(Duration.millis(400), e -> sprite.setOpacity(1.0))
        );
        blinkTimer.setCycleCount(25); // 5 secondes de clignotement
        blinkTimer.play();

        // Timer pour désactiver l'invincibilité après 5 secondes
        invincibilityTimerPlayer1 = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            isInvinciblePlayer1 = false;
            sprite.setOpacity(1.0); // S'assurer que l'opacité est normale
            System.out.println("Joueur 1 n'est plus invincible");
        }));
        invincibilityTimerPlayer1.setCycleCount(1);
        invincibilityTimerPlayer1.play();
    }

    /**
     * Active l'invincibilité pour le joueur 2 pendant 5 secondes
     */
    public void activateInvincibilityPlayer2()
    {
        // Si déjà invincible, arrêter le timer précédent
        if (invincibilityTimerPlayer2 != null) {
            invincibilityTimerPlayer2.stop();
        }

        isInvinciblePlayer2 = true;

        // Effet visuel : faire clignoter le sprite
        Timeline blinkTimer = new Timeline(
                new KeyFrame(Duration.millis(200), e -> sprite2.setOpacity(0.3)),
                new KeyFrame(Duration.millis(400), e -> sprite2.setOpacity(1.0))
        );
        blinkTimer.setCycleCount(25); // 5 secondes de clignotement
        blinkTimer.play();

        // Timer pour désactiver l'invincibilité après 5 secondes
        invincibilityTimerPlayer2 = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            isInvinciblePlayer2 = false;
            sprite2.setOpacity(1.0); // S'assurer que l'opacité est normale
            System.out.println("Joueur 2 n'est plus invincible");
        }));
        invincibilityTimerPlayer2.setCycleCount(1);
        invincibilityTimerPlayer2.play();
    }

    /**
     * Gère la mort du joueur 1.
     */
    public void deathJ1()
    {
        // Si le joueur est invincible, il ne meurt pas
        if (isInvinciblePlayer1) {
            System.out.println("Joueur 1 est invincible ! Pas de dégâts.");
            return;
        }

        if (pv1 <= 0)
        {
            canMove1 = false;
            sprite.setVisible(false);
            System.out.println("Joueur 1 éliminé....");
            Platform.runLater(() -> showGameOverScreen("Le joueur 2 a gagné !"));
        }
    }

    /**
     * Gère la mort du joueur 2.
     */
    public void deathJ2()
    {
        // Si le joueur est invincible, il ne meurt pas
        if (isInvinciblePlayer2) {
            System.out.println("Joueur 2 est invincible ! Pas de dégâts.");
            return;
        }

        if (pv2 <= 0)
        {
            canMove2 = false;
            sprite2.setVisible(false);
            System.out.println("Joueur 2 éliminé....");
            Platform.runLater(() -> showGameOverScreen("Le joueur 1 a gagné !"));
        }
    }

    /**
     * Affiche l'écran de fin de jeu
     */
    private void showGameOverScreen(String winnerMessage)
    {
        Stage stage = (Stage) grid.getScene().getWindow();

        VBox gameOverLayout = new VBox(30);
        gameOverLayout.setAlignment(Pos.CENTER);
        gameOverLayout.setStyle("-fx-background-color: #2c3e50; -fx-padding: 50px;");

        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Arial", 48));
        gameOverText.setStyle("-fx-fill: #e74c3c; -fx-font-weight: bold;");

        Text winnerText = new Text(winnerMessage);
        winnerText.setFont(Font.font("Arial", 32));
        winnerText.setStyle("-fx-fill: #f39c12; -fx-font-weight: bold;");

        Button replayButton = new Button("REJOUER");
        replayButton.setPrefSize(200, 50);
        replayButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-background-radius: 10px; -fx-cursor: hand;");
        replayButton.setOnMouseEntered(e -> replayButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                "-fx-background-radius: 10px; -fx-cursor: hand;"));
        replayButton.setOnMouseExited(e -> replayButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-background-radius: 10px; -fx-cursor: hand;"));
        replayButton.setOnAction(e -> restartGame(stage));

        Button quitButton = new Button("QUITTER");
        quitButton.setPrefSize(200, 50);
        quitButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 10px; -fx-cursor: hand;");
        quitButton.setOnMouseEntered(e -> quitButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-color: #c0392b; -fx-text-fill: white; " +
                "-fx-background-radius: 10px; -fx-cursor: hand;"));
        quitButton.setOnMouseExited(e -> quitButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 10px; -fx-cursor: hand;"));
        quitButton.setOnAction(e -> Platform.exit());

        gameOverLayout.getChildren().addAll(gameOverText, winnerText, replayButton, quitButton);

        Scene gameOverScene = new Scene(gameOverLayout, 800, 600);
        stage.setScene(gameOverScene);
    }

    /**
     * Redémarre le jeu en créant une nouvelle instance de Game
     */
    private void restartGame(Stage stage)
    {
        try {
            System.out.println("Redémarrage du jeu...");

            // Créer une nouvelle instance de Game et la démarrer
            Game newGame = new Game(true);
            newGame.setCurrentThemes(currentTheme);
            newGame.start(stage);

            System.out.println("Nouveau jeu démarré avec succès !");

        } catch (Exception e) {
            System.err.println("Erreur lors du redémarrage : " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    // Méthodes de verrouillage / déverrouillage des mouvements
    public void lockMovement1() { canMove1 = false; }
    public void unlockMovement1() { canMove1 = true; }
    public void lockMovement2() { canMove2 = false; }
    public void unlockMovement2() { canMove2 = true; }
//    public void lockMovement3() { canMove3 = false; }
//    public void unlockMovement3() { canMove3 = true; }
//    public void lockMovement4() { canMove4 = false; }
//    public void unlockMovement4() { canMove4 = true; }

    // Getters / Setters
    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }
    //    public int getX3() { return x3; }
//    public int getY3() { return y3; }
//    public int getX4() { return x4; }
//    public int getY4() { return y4; }
    public int getCurrentBombsPlayer1() { return currentBombsPlayer1; }
    public int getCurrentBombsPlayer2() { return currentBombsPlayer2; }
    public int getMaxBombsPlayer1() { return maxBombsPlayer1; }
    public int getMaxBombsPlayer2() { return maxBombsPlayer2; }
    public boolean canPlaceBombPlayer1() { return currentBombsPlayer1 < maxBombsPlayer1; }
    public boolean canPlaceBombPlayer2() { return currentBombsPlayer2 < maxBombsPlayer2; }


    public int getPv1() { return pv1; }
    public int getPv2() { return pv2; }
    public void setPv1(int pv) { this.pv1 = pv; }
    public void setPv2(int pv) { this.pv2 = pv; }
//    public int getPv3() { return pv3; }
//    public int getPv4() { return pv4; }
//    public void setPv3(int pv) { this.pv3 = pv; }
//    public void setPv4(int pv) { this.pv4 = pv; }

    // Getters pour l'invincibilité
    public boolean isInvinciblePlayer1() { return isInvinciblePlayer1; }
    public boolean isInvinciblePlayer2() { return isInvinciblePlayer2; }

    public boolean isObstacle(int x, int y)
    {
        Tile cases = tiles[y][x];
        if (cases.getType() == Tile.Type.EMPTY)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}