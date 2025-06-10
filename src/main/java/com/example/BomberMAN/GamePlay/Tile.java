package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;

import java.util.Objects;

public class Tile
{
    /**
     * Énumération représentant les types de tuiles.
     */
    public enum Type
    {
        EMPTY,      /**< Tuile vide, traversable */
        BREAKABLE,  /**< Tuile cassable, traversable si cassée */
        WALL        /**< Mur, non cassable et non traversable */
    }

    private Rectangle rect;    /**< Représentation graphique de la tuile */
    private Type type;         /**< Type de la tuile */
    private boolean breakable; /**< Indique si la tuile est cassable */
    private boolean walkable;  /**< Indique si la tuile est traversable */

    // === STATIC TEXTURES ===
    private static ImagePattern EMPTY_TEXTURE;
    private static ImagePattern BREAKABLE_TEXTURE;
    private static ImagePattern WALL_TEXTURE;

    private int x, y; // Position de la tuile
    private GridPane grid; // Référence à la grille pour les bonus

    public static void loadTextures()
    {
        EMPTY_TEXTURE = new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/EMPTY.jpg"));
        BREAKABLE_TEXTURE = new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/BREAKABLE.jpg"));
        WALL_TEXTURE = new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/WALL.jpg"));
    }

    private static Image loadImage(String path)
    {
        return new Image(Objects.requireNonNull(Tile.class.getResourceAsStream(path)));
    }

    public Tile(int x, int y)
    {
        rect = new Rectangle(Game.TILE_SIZE, Game.TILE_SIZE);
        rect.setStroke(Color.GRAY);
        setType(Type.EMPTY);
    }

    /**
     * Retourne le rectangle associé à la tuile (utilisé pour l'affichage).
     *
     * @return Le rectangle JavaFX représentant cette tuile
     */
    public Rectangle getRectangle()
    {
        return rect;
    }

    /**
     * Définit le type de la tuile et met à jour son apparence et ses propriétés.
     *
     * @param type Le type à appliquer à la tuile
     */
    public void setType(Type type)
    {
        this.type = type;

        switch (type)
        {
            case EMPTY -> {
                rect.setFill(EMPTY_TEXTURE);
                breakable = false;
                walkable = true;
            }
            case BREAKABLE -> {
                rect.setFill(BREAKABLE_TEXTURE);
                breakable = true;
                walkable = false;
            }
            case WALL -> {
                rect.setFill(WALL_TEXTURE);
                breakable = false;
                walkable = false;
            }
        }
    }

    /**
     * Vérifie si la tuile est cassable.
     *
     * @return true si cassable, false sinon
     */
    public boolean isBreakable()
    {
        return breakable;
    }

    /**
     * Vérifie si la tuile est traversable.
     *
     * @return true si traversable, false sinon
     */
    public boolean isWalkable()
    {
        return walkable;
    }

    /**
     * Détruit une tuile cassable, la transforme en tuile vide.
     */
    public void destroy()
    {
        if (breakable)
        {
            setType(Type.EMPTY); // Replace by EMPTY tile with texture
        }
    }


    public Type getType()
    {
        return type;
    }
}