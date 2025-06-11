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
 * Gère les dégâts aux joueurs en tenant compte de l'invincibilité.
 */
public class Bomb {
    private ImageView bombSprite;
    private GridPane grid;
    private Tile[][] tiles;
    private Player player;
    private boolean active = true;
    private int playerNumber; // 1 ou 2, pour identifier quel joueur a posé la bombe
    private Game game; // Référence au jeu pour ajouter les bonus

    // Constantes d'explosion
    private static final int EXPLOSION_RADIUS = 1; // Rayon d'explosion (croix de 3x3)
    private static final double EXPLOSION_DURATION = 0.5; // Durée d'affichage de l'explosion
    private static final double BOMB_TIMER = 2.0; // Temps avant explosion

    /**
     * Constructeur principal avec référence au jeu
     */
    public Bomb(int x, int y, GridPane grid, Tile[][] tiles, Player player, Game game) {
        this.grid = grid;
        this.tiles = tiles;
        this.player = player;
        this.game = game;

        // Déterminer quel joueur a posé la bombe
        if (x == player.getX1() && y == player.getY1()) {
            this.playerNumber = 1;
        } else {
            this.playerNumber = 2;
        }

        createBombSprite(x, y);
        startExplosionTimer(x, y);
        //grid.add(bombSprite, x, y);

        Timeline explosionDelay = new Timeline(new KeyFrame(Duration.seconds(2), ev -> explode(x, y)));
        explosionDelay.setCycleCount(1);
        explosionDelay.play();
    }

    /**
     * Constructeur de compatibilité (sans game)
     */
    public Bomb(int x, int y, GridPane grid, Tile[][] tiles, Player player) {
        this(x, y, grid, tiles, player, null);
    }

    /**
     * Crée et place le sprite de la bombe sur la grille
     */
    private void createBombSprite(int x, int y) {
        try {
            if(Tile.getCurrentTheme() == "Manoir")
            {
                Image bombImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/bomb_Manoir.png").toExternalForm());
                bombSprite = new ImageView(bombImage);
                bombSprite.setFitWidth(20);
                bombSprite.setFitHeight(20);
            }
            else
            {
                Image bombImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/bomb.png").toExternalForm());
                bombSprite = new ImageView(bombImage);
                bombSprite.setFitWidth(Game.TILE_SIZE);
                bombSprite.setFitHeight(Game.TILE_SIZE);
            }
            grid.add(bombSprite, x, y);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de la bombe: " + e.getMessage());
        }
    }

    /**
     * Démarre le timer d'explosion de la bombe
     */
    private void startExplosionTimer(int x, int y) {
        Timeline explosionDelay = new Timeline(new KeyFrame(Duration.seconds(BOMB_TIMER), ev -> explode(x, y)));
        explosionDelay.setCycleCount(1);
        explosionDelay.play();
    }

    /**
     * Gère l'explosion de la bombe : animation, destruction de tuiles, et dégâts aux joueurs
     */
    private void explode(int x, int y) {
        if (!active) return; // Éviter les explosions multiples

        active = false;

        // Supprimer le sprite de la bombe
        if (bombSprite != null) {
            grid.getChildren().remove(bombSprite);
        }

        // Créer l'animation d'explosion
        createExplosionAnimation(x, y);

        // Libérer le joueur qui a posé cette bombe
        releaseBombFromPlayer();

        // Gérer les effets de l'explosion dans toutes les directions
        handleExplosionEffects(x, y);
    }

    /**
     * Crée et affiche l'animation d'explosion
     */
    private void createExplosionAnimation(int x, int y) {
        try {
            Image explosionImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/explosion.png").toExternalForm());
            ImageView explosionSprite = new ImageView(explosionImage);

            // Taille de l'explosion (3x3 cases)
            explosionSprite.setFitWidth(Game.TILE_SIZE * 3);
            explosionSprite.setFitHeight(Game.TILE_SIZE * 3);

            // Positionner l'explosion centrée sur la bombe
            grid.add(explosionSprite, x - 1, y - 1, 3, 3);

            // Supprimer l'explosion après un délai
            Timeline clearExplosion = new Timeline(
                    new KeyFrame(Duration.seconds(EXPLOSION_DURATION), e -> grid.getChildren().remove(explosionSprite))
            );
            clearExplosion.setCycleCount(1);
            clearExplosion.play();

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image d'explosion: " + e.getMessage());
        }
    }

    /**
     * Libère la bombe du compteur du joueur qui l'a posée
     */
    private void releaseBombFromPlayer() {
        if (playerNumber == 1) {
            player.releaseBombPlayer1();
        } else {
            player.releaseBombPlayer2();
        }
    }

    /**
     * Gère tous les effets de l'explosion (destruction et dégâts)
     */
    private void handleExplosionEffects(int x, int y) {
        // Directions d'explosion : centre + croix (haut, bas, gauche, droite)
        int[][] explosionPattern = {
                {0, 0},   // Centre
                {1, 0},   // Droite
                {-1, 0},  // Gauche
                {0, 1},   // Bas
                {0, -1}   // Haut
        };

        for (int[] direction : explosionPattern) {
            int explosionX = x + direction[0];
            int explosionY = y + direction[1];

            // Vérifier les limites de la grille
            if (isValidPosition(explosionX, explosionY)) {
                // Traiter la destruction des tuiles
                handleTileDestruction(explosionX, explosionY);

                // Traiter les dégâts aux joueurs
                handlePlayerDamage(explosionX, explosionY);
            }
        }
    }

    /**
     * Vérifie si une position est valide dans la grille
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < Game.GRID_WIDTH && y >= 0 && y < Game.GRID_HEIGHT;
    }

    /**
     * Gère la destruction des tuiles cassables
     */
    private void handleTileDestruction(int x, int y) {
        Tile tile = tiles[y][x];

        if (tile != null && tile.isBreakable()) {
            // Détruire la tuile et récupérer un éventuel bonus
            Bonus bonus = tile.destroy();

            // Ajouter le bonus au jeu s'il existe
            if (bonus != null && game != null) {
                game.addBonus(bonus);
                System.out.println("Bonus " + bonus.getType().getDescription() +
                        " apparu en position (" + x + ", " + y + ")");
            }
        }
    }

    /**
     * Gère les dégâts aux joueurs, en tenant compte de l'invincibilité
     */
    private void handlePlayerDamage(int x, int y) {
        // Vérifier le joueur 1
        if (x == player.getX1() && y == player.getY1()) {
            if (player.isInvinciblePlayer1()) {
                System.out.println("Joueur 1 touché par explosion mais INVINCIBLE ! Aucun dégât.");
            } else {
                System.out.println("Joueur 1 touché par explosion !");
                player.setPv1(0);
                player.deathJ1();
            }
        }

        // Vérifier le joueur 2
        if (x == player.getX2() && y == player.getY2()) {
            if (player.isInvinciblePlayer2()) {
                System.out.println("Joueur 2 touché par explosion mais INVINCIBLE ! Aucun dégât.");
            } else {
                System.out.println("Joueur 2 touché par explosion !");
                player.setPv2(0);
                player.deathJ2();
            }
        }
    }

    /**
     * Vérifie si la bombe est encore active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Force l'explosion de la bombe (pour les réactions en chaîne)
     */
    public void forceExplode(int x, int y) {
        if (active) {
            explode(x, y);
        }
    }

    /**
     * Retourne le numéro du joueur qui a posé cette bombe
     */
    public int getPlayerNumber() {
        return playerNumber;
    }
}