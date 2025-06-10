package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
    private int x, y; // Position de la tuile
    private GridPane grid; // Référence à la grille pour les bonus


    /**
     * Constructeur de la tuile.
     *
     * @param x Position en X (non utilisée directement ici)
     * @param y Position en Y (non utilisée directement ici)
     */
    public Tile(int x, int y)
    {
        this.x = x;
        this.y = y;
        rect = new Rectangle(Game.TILE_SIZE, Game.TILE_SIZE);
        rect.setStroke(Color.GRAY);
        setType(Type.EMPTY);
    }

    public void setGrid(GridPane grid) { this.grid = grid; }
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
                rect.setFill(Color.LIGHTGREEN);
                breakable = false;
                walkable = true;
            }
            case BREAKABLE -> {
                rect.setFill(Color.BROWN);
                breakable = true;
                walkable = false;
            }
            case WALL -> {
                rect.setFill(Color.DARKGRAY);
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
     * Détruit la tuile et peut faire apparaître un bonus
     * @return Le bonus créé, ou null si aucun bonus n'apparaît
     */
    public Bonus destroy()
    {
        if (breakable && grid != null)
        {
            rect.setFill(Color.LIGHTGREEN);
            breakable = false;
            walkable = true;
            type = Type.EMPTY;

            // Tenter de faire apparaître un bonus
            return Bonus.trySpawnBonus(x, y, grid);
        }
        return null;
    }

    /**
     * Retourne le type actuel de la tuile.
     *
     * @return Le type de la tuile
     */
    public Type getType()
    {
        return type;
    }

}