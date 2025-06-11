package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;

import java.util.Objects;

/**
 * Représente une tuile du jeu avec gestion complète des bonus d'invincibilité.
 * Gère l'affichage, la destruction et l'apparition de bonus.
 */
public class Tile {

    /**
     * Énumération représentant les types de tuiles.
     */
    public enum Type {
        EMPTY,      /**< Tuile vide, traversable */
        BREAKABLE,  /**< Tuile cassable, traversable si cassée */
        WALL        /**< Mur, non cassable et non traversable */
    }

    // Composants de la tuile
    private Rectangle rect;        /**< Représentation graphique de la tuile */
    private Type type;            /**< Type de la tuile */
    private boolean breakable;    /**< Indique si la tuile est cassable */
    private boolean walkable;     /**< Indique si la tuile est traversable */
    private boolean destroyed;    /**< Indique si la tuile a été détruite */

    // Position et grille
    private int x, y;             /**< Position de la tuile sur la grille */
    private GridPane grid;        /**< Référence à la grille pour la gestion des bonus */

    // Textures statiques (chargées une seule fois)
    private static ImagePattern EMPTY_TEXTURE;
    private static ImagePattern BREAKABLE_TEXTURE;
    private static ImagePattern WALL_TEXTURE;
    private static boolean texturesLoaded = false;

    // Constantes pour l'affichage
    private static final Color STROKE_COLOR = Color.GRAY;
    private static final double STROKE_WIDTH = 0.5;

    /**
     * Charge toutes les textures nécessaires pour les tuiles.
     * Cette méthode doit être appelée avant de créer des tuiles.
     */
    public static void loadTextures() {
        if (texturesLoaded) return; // Éviter de recharger les textures

        try {
            EMPTY_TEXTURE = new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/EMPTY.jpg"));
            BREAKABLE_TEXTURE = new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/BREAKABLE.jpg"));
            WALL_TEXTURE = new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/WALL.jpg"));
            texturesLoaded = true;
            System.out.println("Textures des tuiles chargées avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des textures: " + e.getMessage());
        }
    }

    /**
     * Charge une image depuis les ressources
     */
    private static Image loadImage(String path) {
        try {
            return new Image(Objects.requireNonNull(Tile.class.getResourceAsStream(path)));
        } catch (Exception e) {
            System.err.println("Impossible de charger l'image: " + path);
            throw new RuntimeException("Erreur de chargement d'image", e);
        }
    }

    /**
     * Constructeur principal d'une tuile
     *
     * @param x Position X sur la grille
     * @param y Position Y sur la grille
     * @param grid Référence à la grille JavaFX
     */
    public Tile(int x, int y, GridPane grid) {
        this.x = x;
        this.y = y;
        this.grid = grid;
        this.destroyed = false;

        initializeRectangle();
        setType(Type.EMPTY); // Type par défaut
    }

    /**
     * Initialise le rectangle graphique de la tuile
     */
    private void initializeRectangle() {
        rect = new Rectangle(Game.TILE_SIZE, Game.TILE_SIZE);
        rect.setStroke(STROKE_COLOR);
        rect.setStrokeWidth(STROKE_WIDTH);
    }

    /**
     * Retourne le rectangle associé à la tuile (utilisé pour l'affichage).
     *
     * @return Le rectangle JavaFX représentant cette tuile
     */
    public Rectangle getRectangle() {
        return rect;
    }

    /**
     * Définit le type de la tuile et met à jour son apparence et ses propriétés.
     * Gère aussi la logique de bonus d'invincibilité.
     *
     * @param type Le type à appliquer à la tuile
     */
    public void setType(Type type) {
        this.type = type;
        updateTileProperties(type);
        updateTileAppearance(type);
    }

    /**
     * Met à jour les propriétés de la tuile selon son type
     */
    private void updateTileProperties(Type type) {
        switch (type) {
            case EMPTY -> {
                breakable = false;
                walkable = true;
                destroyed = false;
            }
            case BREAKABLE -> {
                breakable = true;
                walkable = false;
                destroyed = false;
            }
            case WALL -> {
                breakable = false;
                walkable = false;
                destroyed = false;
            }
        }
    }

    /**
     * Met à jour l'apparence visuelle de la tuile
     */
    private void updateTileAppearance(Type type) {
        if (!texturesLoaded) {
            System.err.println("Attention: Les textures ne sont pas chargées !");
            return;
        }

        switch (type) {
            case EMPTY -> rect.setFill(EMPTY_TEXTURE);
            case BREAKABLE -> rect.setFill(BREAKABLE_TEXTURE);
            case WALL -> rect.setFill(WALL_TEXTURE);
        }
    }

    /**
     * Vérifie si la tuile est cassable.
     *
     * @return true si cassable, false sinon
     */
    public boolean isBreakable() {
        return breakable && !destroyed;
    }

    /**
     * Vérifie si la tuile est traversable.
     *
     * @return true si traversable, false sinon
     */
    public boolean isWalkable() {
        return walkable;
    }

    /**
     * Vérifie si la tuile a été détruite
     *
     * @return true si détruite, false sinon
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Détruit la tuile et peut faire apparaître un bonus d'invincibilité ou autre.
     * Cette méthode est appelée par les bombes lors de leur explosion.
     *
     * @return Le bonus créé (peut être null si aucun bonus n'apparaît)
     */
    public Bonus destroy() {
        // Vérifier si la tuile peut être détruite
        if (!isBreakable()) {
            return null;
        }

        // Marquer la tuile comme détruite
        destroyed = true;

        // Transformer en tuile vide
        setType(Type.EMPTY);

        // Logging pour debug
        System.out.println("Tuile cassable détruite en position (" + x + ", " + y + ")");

        // Tenter de faire apparaître un bonus
        Bonus bonus = null;
        if (grid != null) {
            bonus = Bonus.trySpawnBonus(x, y, grid);
            if (bonus != null) {
                System.out.println("Bonus " + bonus.getType().getDescription() +
                        " généré en position (" + x + ", " + y + ")");
            }
        }

        return bonus;
    }

    /**
     * Réinitialise la tuile à son état cassable (pour redémarrage de partie)
     */
    public void reset() {
        destroyed = false;
        setType(Type.BREAKABLE); // Ou le type souhaité
    }

    /**
     * Vérifie si cette tuile bloque le mouvement des joueurs
     *
     * @return true si la tuile bloque le mouvement
     */
    public boolean blocksMovement() {
        return !walkable;
    }

    /**
     * Vérifie si cette tuile peut être affectée par une explosion
     *
     * @return true si l'explosion peut affecter cette tuile
     */
    public boolean canBeAffectedByExplosion() {
        return breakable && !destroyed;
    }

    // === GETTERS ===

    /**
     * Retourne le type de la tuile
     *
     * @return Le type de la tuile
     */
    public Type getType() {
        return type;
    }

    /**
     * Retourne la position X de la tuile
     *
     * @return Position X
     */
    public int getX() {
        return x;
    }

    /**
     * Retourne la position Y de la tuile
     *
     * @return Position Y
     */
    public int getY() {
        return y;
    }

    /**
     * Retourne la référence à la grille
     *
     * @return La grille JavaFX
     */
    public GridPane getGrid() {
        return grid;
    }

    // === MÉTHODES DE DEBUG ===

    /**
     * Retourne une représentation textuelle de la tuile
     *
     * @return String décrivant la tuile
     */
    @Override
    public String toString() {
        return "Tile{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", breakable=" + breakable +
                ", walkable=" + walkable +
                ", destroyed=" + destroyed +
                '}';
    }

    /**
     * Vérifie si les textures sont chargées
     *
     * @return true si les textures sont chargées
     */
    public static boolean areTexturesLoaded() {
        return texturesLoaded;
    }
}