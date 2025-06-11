package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import java.util.Random;

/**
 * Représente un bonus qui peut apparaître sur la carte après avoir cassé un bloc.
 * Les bonus peuvent être ramassés par les joueurs pour obtenir des améliorations.
 */
public class Bonus {

    public enum BonusType {
        EXTRA_BOMB("Bombe supplémentaire", Color.ORANGE),
        INVINCIBILITY("Invincibilité", Color.GOLD);

        private final String description;
        private final Color color;

        BonusType(String description, Color color) {
            this.description = description;
            this.color = color;
        }

        public String getDescription() { return description; }
    }

    private BonusType type;
    private int x, y;
    private GridPane grid;
    private ImageView bonusSpriteView;

    private static final Random random = new Random();

    // Probabilité d'apparition d'un bonus (30%)
    private static final double BONUS_SPAWN_CHANCE = 0.3;

    /**
     * Constructeur privé pour créer un bonus à une position donnée
     */
    private Bonus(BonusType type, int x, int y, GridPane grid) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.grid = grid;

        createBonusSprite();
    }

    /**
     * Méthode statique pour potentiellement créer un bonus à une position donnée.
     * Retourne null si aucun bonus n'est généré.
     */
    public static Bonus trySpawnBonus(int x, int y, GridPane grid) {
        // Vérifier si un bonus doit apparaître (probabilité)
        if (random.nextDouble() > BONUS_SPAWN_CHANCE) {
            return null; // Pas de bonus cette fois
        }

        // Sélection aléatoire du type de bonus
        BonusType[] availableTypes = BonusType.values();
        BonusType bonusType = availableTypes[random.nextInt(availableTypes.length)];

        return new Bonus(bonusType, x, y, grid);
    }

    /**
     * Crée le sprite visuel du bonus
     */
    private void createBonusSprite() {
        String imagePath;
        switch (type) {
            case EXTRA_BOMB:
                imagePath = "/com/example/BomberMAN/BomberMAN/Bonus/bomb_buff.png";
                break;
            case INVINCIBILITY:
                imagePath = "/com/example/BomberMAN/BomberMAN/Bonus/invincibility.png";
                break;
            default:
                imagePath = "/com/example/BomberMAN/BomberMAN/Bonus/bomb_buff.png";
                break;
        }

        Image image = new Image(getClass().getResourceAsStream(imagePath));
        bonusSpriteView = new ImageView(image);

        bonusSpriteView.setFitWidth(Game.TILE_SIZE / 1.5);
        bonusSpriteView.setFitHeight(Game.TILE_SIZE / 1.5);

        // Centrer l'image dans la tuile
        bonusSpriteView.setTranslateX((Game.TILE_SIZE - bonusSpriteView.getFitWidth()) / 1.5);
        bonusSpriteView.setTranslateY((Game.TILE_SIZE - bonusSpriteView.getFitHeight()) / 1.5);

        grid.add(bonusSpriteView, x, y);
    }

    /**
     * Vérifie si un joueur est sur la position du bonus et l'applique si c'est le cas.
     * @param player Le joueur à vérifier
     * @return true si le bonus a été ramassé, false sinon
     */
    public boolean checkAndApplyBonus(Player player) {
        boolean collected = false;

        // Vérifier le joueur 1
        if (player.getX1() == x && player.getY1() == y) {
            applyBonusToPlayer(player, 1);
            collected = true;
        }
        // Vérifier le joueur 2
        else if (player.getX2() == x && player.getY2() == y) {
            applyBonusToPlayer(player, 2);
            collected = true;
        }

        if (collected) {
            removeFromGrid();
        }

        return collected;
    }

    /**
     * Applique l'effet du bonus au joueur spécifié
     */
    private void applyBonusToPlayer(Player player, int playerNumber) {
        switch (type) {
            case EXTRA_BOMB:
                if (playerNumber == 1) {
                    player.increaseBombLimitPlayer1();
                    System.out.println("Joueur 1 a ramassé un bonus : " + type.getDescription() +
                            " (Bombes max: " + player.getMaxBombsPlayer1() + ")");
                } else {
                    player.increaseBombLimitPlayer2();
                    System.out.println("Joueur 2 a ramassé un bonus : " + type.getDescription() +
                            " (Bombes max: " + player.getMaxBombsPlayer2() + ")");
                }
                break;

            case INVINCIBILITY:
                if (playerNumber == 1) {
                    player.activateInvincibilityPlayer1();
                    System.out.println("Joueur 1 a ramassé un bonus : " + type.getDescription() + " (5 secondes)");
                } else {
                    player.activateInvincibilityPlayer2();
                    System.out.println("Joueur 2 a ramassé un bonus : " + type.getDescription() + " (5 secondes)");
                }
                break;
        }
    }

    /**
     * Supprime le bonus de la grille
     */
    private void removeFromGrid() {
        if (bonusSpriteView != null) {
            grid.getChildren().remove(bonusSpriteView);
        }
    }

    // Getters
    public BonusType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
}