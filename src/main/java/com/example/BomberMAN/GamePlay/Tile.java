package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;
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

    // === STATIC TEXTURES BY THEME ===
    private static Map<String, Map<Type, ImagePattern>> THEME_TEXTURES = new HashMap<>();
    private static String currentTheme = "BomberMan"; // Thème par défaut

    /**
     * Charge toutes les textures pour tous les thèmes disponibles.
     */
    public static void loadAllTextures()
    {
        loadTexturesForTheme("BomberMan");
        loadTexturesForTheme("Manoir");
    }

    /**
     * Charge les textures pour un thème spécifique.
     */
    private static void loadTexturesForTheme(String theme)
    {
        Map<Type, ImagePattern> textures = new HashMap<>();

        try
        {
            switch (theme)
            {
                case "BomberMan" -> {
                    textures.put(Type.EMPTY, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/EMPTY.jpg")));
                    textures.put(Type.BREAKABLE, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/BREAKABLE.jpg")));
                    textures.put(Type.WALL, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/WALL.jpg")));
                }
                case "Manoir" -> {
                    textures.put(Type.EMPTY, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_Manoir/EMPTY.png")));
                    textures.put(Type.BREAKABLE, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_Manoir/BREAKABLE.png")));
                    textures.put(Type.WALL, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_Manoir/WALL.png")));
                }
                default -> {
                    textures.put(Type.EMPTY, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/EMPTY.jpg")));
                    textures.put(Type.BREAKABLE, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/BREAKABLE.jpg")));
                    textures.put(Type.WALL, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/WALL.jpg")));
                }
            }
            THEME_TEXTURES.put(theme, textures);
        }
        catch (Exception e)
        {
            System.err.println("Erreur lors du chargement des textures pour le thème " + theme + ": " + e.getMessage());
            if (!theme.equals("BomberMan"))
            {
                loadTexturesForTheme("BomberMan");
            }
        }
    }

    /**
     * Change le thème actuel et met à jour toutes les tuiles existantes.
     */
    public static void setCurrentTheme(String theme)
    {
        if (THEME_TEXTURES.containsKey(theme)) {
            currentTheme = theme;
            // Notifier que le thème a changé (pour déclencher la mise à jour des tuiles)
            System.out.println("Thème des textures changé vers : " + theme);
        } else {
            System.err.println("Thème inconnu : " + theme);
        }
    }

    /**
     * Retourne le thème actuellement utilisé.
     */
    public static String getCurrentTheme()
    {
        return currentTheme;
    }

    private static Image loadImage(String path)
    {
        try
        {
            return new Image(Objects.requireNonNull(Tile.class.getResourceAsStream(path)));
        }
        catch (Exception e)
        {
            System.err.println("Impossible de charger l'image : " + path);
            return null;
        }
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
        updateAppearance();
        updateProperties();
    }

    /**
     * Met à jour l'apparence de la tuile selon le thème actuel.
     */
    private void updateAppearance()
    {
        Map<Type, ImagePattern> currentTextures = THEME_TEXTURES.get(currentTheme);

        if (currentTextures != null && currentTextures.containsKey(type)) {
            rect.setFill(currentTextures.get(type));
        } else {
            // Fallback vers des couleurs par défaut si les textures ne sont pas disponibles
            switch (type) {
                case EMPTY -> rect.setFill(Color.LIGHTGRAY);
                case BREAKABLE -> rect.setFill(Color.BROWN);
                case WALL -> rect.setFill(Color.DARKGRAY);
            }
        }
    }

    /**
     * Met à jour les propriétés de la tuile selon son type.
     */
    private void updateProperties()
    {
        switch (type)
        {
            case EMPTY -> {
                breakable = false;
                walkable = true;
            }
            case BREAKABLE -> {
                breakable = true;
                walkable = false;
            }
            case WALL -> {
                breakable = false;
                walkable = false;
            }
        }
    }

    /**
     * Force la mise à jour de l'apparence de cette tuile.
     * Utile quand le thème change et qu'il faut rafraîchir l'affichage.
     */
    public void refreshAppearance()
    {
        updateAppearance();
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
            setType(Type.EMPTY);
        }
    }

    /**
     * Recupère le type de tile (WALL, BREAKABLE ou EMPTY)
     */
    public Type getType()
    {
        return type;
    }
}