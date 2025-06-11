package com.example.BomberMAN.mapEditor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class mapEditorController {

    @FXML
    private TextField descriptionField;

    @FXML
    private ListView<String> mapsListView;

    @FXML
    private GridPane mapGrid;

    @FXML
    private Button btnDelete;

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

    private String[][] mapData = new String[rows][cols];
    private Button[][] buttons = new Button[rows][cols];

    private String currentMapName = null;

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
                buttons[i][j] = cell;

                // Définir les cellules sur les bords comme des murs incassables
                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                    setBackgroundImage(cell, wallImage);
                    mapData[i][j] = "WALL";
                } else {
                    setBackgroundImage(cell, emptyImage);
                    mapData[i][j] = "EMPTY";
                }

                // Placer les images des personnages sur les coins réservés
                if (i == 1 && j == 1) {
                    setGraphicImage(cell, player1Image);
                    mapData[i][j] = "EMPTY";
                } else if (i == 1 && j == cols - 2) {
                    mapData[i][j] = "EMPTY";
                    setGraphicImage(cell, player2Image);
                } else if (i == rows - 2 && j == 1) {
                    mapData[i][j] = "EMPTY";
                    setGraphicImage(cell, player3Image);
                } else if (i == rows - 2 && j == cols - 2) {
                    setGraphicImage(cell, player4Image);
                    mapData[i][j] = "EMPTY";
                }

                int x = i, y = j;
                cell.setOnAction(e -> placeCell(cell, x, y));
                mapGrid.add(cell, j, i);
            }
        }
        // Charger la liste des fichiers maps dans le dossier "niveau"
        loadMapFiles();

        // Écouter la sélection dans la liste pour charger la map
        mapsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // double-clic pour charger
                String selectedMap = mapsListView.getSelectionModel().getSelectedItem();
                if (selectedMap != null) {
                    loadMap(selectedMap);
                }
            }
        });
    }

    private void loadMapFiles() {
        File dir = new File("niveau/");
        if (dir.exists() && dir.isDirectory()) {
            String[] files = dir.list((d, name) -> name.endsWith(".map"));
            if (files != null) {
                mapsListView.getItems().clear();
                for (String file : files) {
                    mapsListView.getItems().add(file);
                }
            }
        }
    }

    private void loadMap(String mapFileName) {
        File file = new File("niveau/" + mapFileName);
        if (!file.exists()) {
            showAlert("Erreur", "Le fichier sélectionné n'existe pas.");
            return;
        }

        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            for (int i = 0; i < rows; i++) {
                if (!scanner.hasNextLine()) break;
                String line = scanner.nextLine();
                String[] tokens = line.split(" ");
                for (int j = 0; j < cols && j < tokens.length; j++) {
                    mapData[i][j] = tokens[j];
                    Button cell = buttons[i][j];
                    // Mettre à jour l’affichage selon le type
                    switch (tokens[j]) {
                        case "WALL":
                            setBackgroundImage(cell, wallImage);
                            cell.setGraphic(null);
                            break;
                        case "EMPTY":
                            setBackgroundImage(cell, emptyImage);
                            cell.setGraphic(null);
                            break;
                        case "BREAKABLE":
                            setBackgroundImage(cell, breakableWallImage);
                            cell.setGraphic(null);
                            break;
                        default:
                            setBackgroundImage(cell, emptyImage);
                            cell.setGraphic(null);
                            break;
                    }
                }
            }
            currentMapName = mapFileName;

            // Remplir le TextField avec le nom (sans l’extension)
            if (mapFileName.endsWith(".map")) {
                descriptionField.setText(mapFileName.substring(0, mapFileName.length() - 4));
            } else {
                descriptionField.setText(mapFileName);
            }

            showAlert("Succès", "Carte " + mapFileName + " chargée.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement : " + e.getMessage());
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
        mapData[x][y] = selectedCellType;
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
    private void handleSave() {
        String mapName = descriptionField.getText().trim();

        if (mapName.isEmpty()) {
            showAlert("Erreur", "Le nom de la carte ne peut pas être vide.");
            return;
        }

        // Création du dossier s'il n'existe pas
        File directory = new File("niveau/");
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                showAlert("Erreur", "Impossible de créer le dossier 'maps'.");
                return;
            }
        }

        File file = new File(directory, mapName + ".map");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(mapData[i][j]);
                    if (j < cols - 1) {
                        writer.write(" ");
                    }
                }
                writer.newLine();
            }
            // Met à jour currentMapName
            currentMapName = mapName + ".map";

            showAlert("Succès", "Carte sauvegardée dans maps/" + mapName + ".map");

            // Recharge la liste des maps après sauvegarde
            loadMapFiles();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    @FXML
    public void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/BomberMAN/menu/fxml/Menu-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mapGrid.getScene().getWindow();
            Scene scene = new Scene(root, 800, 800);
            stage.setScene(scene);
            stage.setTitle("Bomberman - Menu");
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le menu.");
        }
    }

    @FXML
    private void handleDeleteMap() {
        String selectedMap = mapsListView.getSelectionModel().getSelectedItem();
        if (selectedMap == null) {
            showAlert("Erreur", "Veuillez sélectionner une carte à supprimer.");
            return;
        }

        // Demander confirmation avant suppression
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Voulez-vous vraiment supprimer la carte '" + selectedMap + "' ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                File file = new File("niveau/" + selectedMap);
                if (file.exists()) {
                    if (file.delete()) {
                        showAlert("Succès", "Carte '" + selectedMap + "' supprimée.");
                        mapsListView.getItems().remove(selectedMap);

                        // Si c’était la map chargée, reset currentMapName
                        if (selectedMap.equals(currentMapName)) {
                            currentMapName = null;
                            descriptionField.clear();
                            // Et vider la grille ou remettre à l’état initial ?
                        }
                    } else {
                        showAlert("Erreur", "Impossible de supprimer la carte.");
                    }
                } else {
                    showAlert("Erreur", "Le fichier n'existe pas.");
                    mapsListView.getItems().remove(selectedMap);
                }
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
