package com.example.BomberMAN.Maps;

import com.example.BomberMAN.Game;
import com.example.BomberMAN.GamePlay.Tile;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsable du chargement des cartes depuis des fichiers.
 * Version corrigée avec support des bonus via la référence GridPane.
 */
public class MapLoader
{
    /**
     * Charge une carte depuis un fichier et crée les tuiles correspondantes.
     * Version corrigée qui passe la référence GridPane aux tuiles pour permettre le spawn des bonus.
     *
     * @param path Le chemin vers le fichier de carte
     * @param grid La référence à la grille JavaFX pour la gestion des bonus
     * @return Un tableau 2D de tuiles représentant la carte
     */
    public static Tile[][] loadMap(String path, GridPane grid)
    {
        List<Tile[]> rows = new ArrayList<>();
        int totalTiles = 0;
        int breakableTiles = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            String line;
            int y = 0;

            while ((line = br.readLine()) != null)
            {
                String[] tokens = line.trim().split(" ");
                Tile[] row = new Tile[tokens.length];

                for (int x = 0; x < tokens.length; x++)
                {
                    Tile tile = new Tile(x, y, grid);
                    String token = tokens[x];

                    switch (token)
                    {
                        case "WALL" -> {
                            tile.setType(Tile.Type.WALL);
                        }
                        case "EMPTY" -> {
                            tile.setType(Tile.Type.EMPTY);
                        }
                        case "." -> {
                            // 70% de chance d'avoir un bloc cassable
                            if (Math.random() < 0.7) {
                                tile.setType(Tile.Type.BREAKABLE);
                                breakableTiles++;
                            } else {
                                tile.setType(Tile.Type.EMPTY);
                            }
                        }
                        default -> {
                            // Cas par défaut pour les tokens non reconnus
                            tile.setType(Tile.Type.EMPTY);
                        }
                    }

                    row[x] = tile;
                    totalTiles++;
                }

                rows.add(row);
                y++;
            }
        }
        catch (Exception e)
        {
            System.err.println("Erreur lors du chargement de la carte: " + e.getMessage());
            e.printStackTrace();

            // Retourner une carte d'urgence si le chargement échoue
            return createEmergencyMap(grid);
        }

        return rows.toArray(new Tile[0][]);
    }

    /**
     * Crée une carte d'urgence basique si le chargement du fichier échoue.
     *
     * @param grid La référence à la grille JavaFX
     * @return Une carte d'urgence basique
     */
    private static Tile[][] createEmergencyMap(GridPane grid)
    {
        int width = Game.GRID_WIDTH;
        int height = Game.GRID_HEIGHT;
        Tile[][] emergencyMap = new Tile[height][width];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                Tile tile = new Tile(x, y, grid);

                // Créer des murs sur les bords
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
                {
                    tile.setType(Tile.Type.WALL);
                }
                // Créer une grille de murs internes
                else if (x % 2 == 0 && y % 2 == 0)
                {
                    tile.setType(Tile.Type.WALL);
                }
                // Laisser des espaces libres autour des positions de départ
                else if ((x == 1 && y == 1) || (x == 2 && y == 1) || (x == 1 && y == 2) ||
                        (x == width - 2 && y == height - 2) || (x == width - 3 && y == height - 2) || (x == width - 2 && y == height - 3))
                {
                    tile.setType(Tile.Type.EMPTY);
                }
                // Créer des blocs cassables ailleurs
                else if (Math.random() < 0.6)
                {
                    tile.setType(Tile.Type.BREAKABLE);
                }
                else
                {
                    tile.setType(Tile.Type.EMPTY);
                }

                emergencyMap[y][x] = tile;
            }
        }
        return emergencyMap;
    }
}