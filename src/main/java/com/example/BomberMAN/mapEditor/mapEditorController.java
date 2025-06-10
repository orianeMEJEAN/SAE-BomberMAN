package com.example.BomberMAN.mapEditor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

public class mapEditorController {

    @FXML
    private GridPane mapGrid;

    private final int rows = 11;
    private final int cols = 13;
    private String selectedCellType = "EMPTY";

    private Image wallImage;
    private Image emptyImage;
    private Image breakableWallImage;
    private Image player1Image;
    private Image player2Image;
    private Image player3Image;
    private Image player4Image;

    @FXML
    public void initialize() {
        // Charger les images depuis le répertoire des ressources
        wallImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/WALL.jpg").toExternalForm());
        emptyImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/EMPTY.jpg").toExternalForm());
        breakableWallImage = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/texture_Maps/MAP1/BREAKABLE.jpg").toExternalForm());

        // Charger les images des personnages
        player1Image = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J1/player.png").toExternalForm());
        player2Image = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J2/Player2-default.png").toExternalForm());
        player3Image = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J3/p3_default.png").toExternalForm());
        player4Image = new Image(getClass().getResource("/com/example/BomberMAN/BomberMAN/J4/p4_default.png").toExternalForm());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Button cell = new Button();
                cell.setPrefSize(40, 40);

                // Définir les cellules sur les bords comme des murs incassables
                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                    setBackgroundImage(cell, wallImage);
                } else {
                    setBackgroundImage(cell, emptyImage);
                }

                // Placer les images des personnages sur les coins réservés
                if (i == 1 && j == 1) {
                    setGraphicImage(cell, player1Image);
                } else if (i == 1 && j == cols - 2) {
                    setGraphicImage(cell, player2Image);
                } else if (i == rows - 2 && j == 1) {
                    setGraphicImage(cell, player3Image);
                } else if (i == rows - 2 && j == cols - 2) {
                    setGraphicImage(cell, player4Image);
                }

                int x = i, y = j;
                cell.setOnAction(e -> placeCell(cell, x, y));
                mapGrid.add(cell, j, i);
            }
        }
    }

    private void setBackgroundImage(Button button, Image image) {
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(1, 1, true, true, false, true)
        );
        button.setBackground(new Background(backgroundImage));
    }

    private void setGraphicImage(Button button, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(15);
        imageView.setFitHeight(20);
        button.setGraphic(imageView);
    }

    private void placeCell(Button cell, int x, int y) {
        // Empêcher la modification des murs incassables sur les bords
        if (x == 0 || x == rows - 1 || y == 0 || y == cols - 1) {
            return;
        }

        // Empêcher la modification des coins réservés aux joueurs
        if ((x == 1 && y == 1) || (x == 1 && y == cols - 2) || (x == rows - 2 && y == 1) || (x == rows - 2 && y == cols - 2)) {
            return;
        }

        // Empêcher la placement de murs incassables autour des positions initiales des joueurs sauf en diagonale
        if (selectedCellType.equals("WALL") || selectedCellType.equals("BREAKABLE")) {
            if ((x == 1 && (y == 0 || y == 2)) || (x == 2 && y == 1) ||
                    (x == 1 && y == cols - 3) || (x == 2 && y == cols - 2) ||
                    (x == rows - 2 && (y == 0 || y == 2)) || (x == rows - 3 && y == 1) ||
                    (x == rows - 2 && y == cols - 3) || (x == rows - 3 && y == cols - 2)) {
                return;
            }
        }

        switch (selectedCellType) {
            case "WALL":
                setBackgroundImage(cell, wallImage);
                break;
            case "EMPTY":
                setBackgroundImage(cell, emptyImage);
                break;
            case "BREAKABLE":
                setBackgroundImage(cell, breakableWallImage);
                break;
        }
    }

    @FXML
    private void handleWallSelection() {
        selectedCellType = "WALL";
    }

    @FXML
    private void handleEmptySelection() {
        selectedCellType = "EMPTY";
    }

    @FXML
    private void handleBreakableWallSelection() {
        selectedCellType = "BREAKABLE";
    }

    @FXML
    public void handleSave() {
        System.out.println("Sauvegarde de la map...");
        // Implémenter la logique d’export de la grille
    }

    @FXML
    public void handleLoad() {
        System.out.println("Chargement d'une map...");
        // Implémenter la logique de chargement
    }

    @FXML
    public void handleReturn() {
        // Fermer la fenêtre de création de map
        Stage stage = (Stage) mapGrid.getScene().getWindow();
        stage.close();
    }
}
