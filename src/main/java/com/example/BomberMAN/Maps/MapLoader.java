package com.example.BomberMAN.Maps;

import com.example.BomberMAN.GamePlay.Tile;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public static Tile[][] loadMap(String mapPath, GridPane grid) {
        List<Tile[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(mapPath))) {
            String line;
            int y = 0;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split(" ");
                Tile[] row = new Tile[tokens.length];

                for (int x = 0; x < tokens.length; x++) {
                    Tile tile = new Tile(x, y, grid); // intègre le tile dans le GridPane
                    String token = tokens[x];

                    switch (token) {
                        case "WALL" -> tile.setType(Tile.Type.WALL);
                        case "EMPTY" -> tile.setType(Tile.Type.EMPTY);
                        case "." -> {
                            if (Math.random() < 0.7) {
                                tile.setType(Tile.Type.BREAKABLE);
                            } else {
                                tile.setType(Tile.Type.EMPTY);
                            }
                        }
                        default -> tile.setType(Tile.Type.EMPTY); // sécurité
                    }

                    row[x] = tile;
                }

                rows.add(row);
                y++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows.toArray(new Tile[0][]);
    }
}
