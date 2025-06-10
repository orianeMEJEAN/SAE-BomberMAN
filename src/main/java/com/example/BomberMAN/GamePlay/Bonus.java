package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;

/**
 * Représente un bonus qui peut apparaître sur la carte après avoir cassé un bloc.
 * Les bonus peuvent être ramassés par les joueurs pour obtenir des améliorations.
 */
public class Bonus {

    public enum BonusType {
        EXTRA_BOMB("Bombe supplémentaire", Color.ORANGE)
        // On pourra ajouter d'autres types plus tard : SPEED, POWER, etc.
        ;

        private final String description;
        private final Color color;

        BonusType(String description, Color color) {
            this.description = description;
            this.color = color;
        }

        public String getDescription() { return description; }
        public Color getColor() { return color; }
    }

    private BonusType type;
    private int x, y;
    private GridPane grid;
    private Circle bonusSprite;
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

        // Pour l'instant, seul le bonus EXTRA_BOMB existe
        // Plus tard on pourra ajouter une sélection aléatoire entre différents types
        BonusType bonusType = BonusType.EXTRA_BOMB;

        return new Bonus(bonusType, x, y, grid);
    }

    /**
     * Crée le sprite visuel du bonus
     */
    private void createBonusSprite() {
        bonusSprite = new Circle(Game.TILE_SIZE / 3);
        bonusSprite.setFill(type.getColor());
        bonusSprite.setStroke(Color.WHITE);
        bonusSprite.setStrokeWidth(2);

        // Centrer le cercle dans la tuile
        bonusSprite.setTranslateX(Game.TILE_SIZE / 2);
        bonusSprite.setTranslateY(Game.TILE_SIZE / 2);

        grid.add(bonusSprite, x, y);
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

            // Ici on pourra ajouter d'autres types de bonus
            // case SPEED: ... break;
            // case POWER: ... break;
        }
    }

    /**
     * Supprime le bonus de la grille
     */
    private void removeFromGrid() {
        grid.getChildren().remove(bonusSprite);
    }

    // Getters
    public BonusType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
}