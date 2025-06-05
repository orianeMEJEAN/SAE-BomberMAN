package com.example.BomberMAN;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile
{
    public enum Type
    {
        EMPTY, BREAKABLE, WALL
    }

    private Rectangle rect;
    private Type type;
    private boolean breakable;
    private boolean walkable;

    public Tile(int x, int y)
    {
        rect = new Rectangle(Game.TILE_SIZE, Game.TILE_SIZE);
        rect.setStroke(Color.GRAY);
        setType(Type.EMPTY);
    }

    public Rectangle getRectangle()
    {
        return rect;
    }

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

    public boolean isBreakable()
    {
        return breakable;
    }

    public boolean isWalkable()
    {
        return type == Type.EMPTY;
    }

    public void destroy()
    {
        if (breakable)
        {
            rect.setFill(Color.LIGHTGREEN);
            breakable = false;
            walkable = true;
        }
    }
}