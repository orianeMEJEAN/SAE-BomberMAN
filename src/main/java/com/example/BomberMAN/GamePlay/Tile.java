package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * La classe Tile représente une seule tuile sur le plateau de jeu BomberMan.
 * Elle gère le type de tuile (vide, cassable, mur), son apparence graphique et ses propriétés de jeu.
 */
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
    /**
     * Carte statique stockant les textures (ImagePattern) pour chaque type de tuile,
     * organisées par nom de thème. La clé externe est le nom du thème (String),
     * et la clé interne est le Type de la tuile.
     */
    private static Map<String, Map<Type, ImagePattern>> THEME_TEXTURES = new HashMap<>();
    /** Le nom du thème actuellement sélectionné pour l'affichage des tuiles. Initialisé à "BomberMan". */
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
                default -> { // Fallback au thème BomberMan si le thème spécifié n'est pas reconnu
                    textures.put(Type.EMPTY, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/EMPTY.jpg")));
                    textures.put(Type.BREAKABLE, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/BREAKABLE.jpg")));
                    textures.put(Type.WALL, new ImagePattern(loadImage("/com/example/BomberMAN/BomberMAN/texture_Maps/Theme_BomberMan/WALL.jpg")));
                }
            }
            THEME_TEXTURES.put(theme, textures); // Stocke les textures chargées pour ce thème
        }
        catch (Exception e)
        {
            System.err.println("Erreur lors du chargement des textures pour le thème " + theme + ": " + e.getMessage());
            // Si le chargement échoue et que ce n'est pas déjà le thème BomberMan, essayer de charger BomberMan
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
            System.err.println("Thème inconnu : " + theme + ". Le thème actuel reste : " + currentTheme);
        }
    }

    /**
     * Retourne le nom du thème actuellement utilisé pour l'affichage des tuiles.
     * @return Le nom du thème courant.
     */
    public static String getCurrentTheme()
    {
        return currentTheme;
    }

    /**
     * Charge une image à partir d'un chemin de ressource donné.
     * Gère les exceptions si l'image ne peut pas être chargée et affiche un message d'erreur.
     * @param path Le chemin d'accès à l'image (par exemple, "/images/my_image.png").
     * @return L'objet Image chargé, ou null si le chargement échoue.
     */
    private static Image loadImage(String path)
    {
        try
        {
            return new Image(Objects.requireNonNull(Tile.class.getResourceAsStream(path)));
        }
        catch (Exception e)
        {
            System.err.println("Impossible de charger l'image : " + path + ". Erreur: " + e.getMessage());
            return null;
        }
    }

    /**
     * Constructeur de la classe Tile.
     * Crée une nouvelle tuile à la position spécifiée et initialise sa représentation graphique.
     * Par défaut, la tuile est de type EMPTY.
     * @param x La coordonnée X de la tuile dans la grille.
     * @param y La coordonnée Y de la tuile dans la grille.
     */
    public Tile(int x, int y)
    {
        // Initialise le Rectangle graphique avec la taille de tuile définie dans la classe Game.
        rect = new Rectangle(Game.TILE_SIZE, Game.TILE_SIZE);
        // Définit une bordure grise pour le rectangle.
        rect.setStroke(Color.GRAY);
        // Définit le type initial de la tuile comme vide.
        setType(Type.EMPTY);
    }

    /**
     * Retourne la représentation graphique (Rectangle JavaFX) de cette tuile.
     * Ce rectangle est utilisé pour l'affichage sur la scène JavaFX.
     * @return Le Rectangle JavaFX associé à cette tuile.
     */
    public Rectangle getRectangle()
    {
        return rect;
    }

    /**
     * Définit le type de la tuile et met à jour son apparence graphique ainsi que ses propriétés
     * (cassable, traversable) en conséquence.
     * @param type Le nouveau type à appliquer à la tuile (EMPTY, BREAKABLE, WALL).
     */
    public void setType(Type type)
    {
        this.type = type;
        updateAppearance(); // Met à jour la texture ou la couleur de la tuile.
        updateProperties(); // Met à jour les drapeaux 'breakable' et 'walkable'.
    }

    /**
     * Met à jour l'apparence visuelle de la tuile (remplissage du Rectangle)
     * en fonction de son type actuel et du thème sélectionné.
     * Si les textures du thème ne sont pas disponibles, des couleurs par défaut sont utilisées.
     */
    private void updateAppearance()
    {
        Map<Type, ImagePattern> currentTextures = THEME_TEXTURES.get(currentTheme);

        if (currentTextures != null && currentTextures.containsKey(type)) {
            rect.setFill(currentTextures.get(type)); // Applique la texture du thème.
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
     * Met à jour les propriétés internes de la tuile (breakable, walkable)
     * en fonction de son type actuel.
     */
    private void updateProperties()
    {
        switch (type)
        {
            case EMPTY -> {
                breakable = false; // Une tuile vide n'est pas cassable.
                walkable = true;   // Une tuile vide est traversable.
            }
            case BREAKABLE -> {
                breakable = true;  // Une tuile cassable est, par définition, cassable.
                walkable = false;  // Une tuile cassable n'est pas traversable avant d'être cassée.
            }
            case WALL -> {
                breakable = false; // Un mur n'est pas cassable.
                walkable = false;  // Un mur n'est pas traversable.
            }
        }
    }

    /**
     * Force la mise à jour de l'apparence de cette tuile.
     * Cette méthode est utile lorsque le thème global du jeu change et que toutes les tuiles
     * doivent refléter la nouvelle esthétique sans changer leur type.
     */
    public void refreshAppearance()
    {
        updateAppearance();
    }

    /**
     * Vérifie si la tuile est de type cassable et peut être détruite.
     * @return true si la tuile est cassable, false sinon.
     */
    public boolean isBreakable()
    {
        return breakable;
    }

    /**
     * Vérifie si la tuile est traversable par les joueurs ou d'autres entités.
     * @return true si la tuile est traversable, false sinon.
     */
    public boolean isWalkable()
    {
        return walkable;
    }

    /**
     * Détruit la tuile si elle est de type cassable.
     * Si la tuile est cassable, son type est changé en EMPTY (vide) et son apparence est mise à jour.
     */
    public void destroy()
    {
        if (breakable) // Vérifie si la tuile est actuellement cassable.
        {
            setType(Type.EMPTY); // Change le type de la tuile en EMPTY.
        }
    }

    /**
     * Récupère le type actuel de la tuile (WALL, BREAKABLE ou EMPTY).
     * @return Le type de la tuile.
     */
    public Type getType()
    {
        return type;
    }
}