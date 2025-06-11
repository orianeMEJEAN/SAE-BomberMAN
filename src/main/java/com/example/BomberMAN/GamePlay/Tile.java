package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;

import java.util.Objects;
import java.util.HashMap;
import java.util.Map;

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
     * Charge toutes les textures nécessaires pour les tuiles.
     * Cette méthode doit être appelée avant de créer des tuiles.
     * */
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