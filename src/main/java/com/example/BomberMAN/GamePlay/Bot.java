package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Random;
import java.util.*;

public class Bot
{
    private Player player;
    private Random random;
    private Timeline timeline;

    private Map<Integer, Boolean> hasPlacedBombMap;
    private Map<Integer, int[]> bombCoordinatesMap;

    private Tile[][] tiles;

    public Bot(Player botPlayer, Tile[][] tiles)
    {
        this.player = botPlayer;
        this.tiles = tiles;
        random = new Random();

        hasPlacedBombMap = new HashMap<>();
        bombCoordinatesMap = new HashMap<>();

        hasPlacedBombMap.put(2, false);
        hasPlacedBombMap.put(3, false);
        hasPlacedBombMap.put(4, false);

        bombCoordinatesMap.put(2, new int[]{0, 0});
        bombCoordinatesMap.put(3, new int[]{0, 0});
        bombCoordinatesMap.put(4, new int[]{0, 0});

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            handleBotLogic(2, player.getX2(), player.getY2());

            handleBotLogic(3, player.getX3(), player.getY3());

            handleBotLogic(4, player.getX4(), player.getY4());
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleBotLogic(int playerNum, int currentX, int currentY) {
        boolean currentHasPlacedBomb = hasPlacedBombMap.get(playerNum);
        int[] currentBombCoords = bombCoordinatesMap.get(playerNum);
        int bombX = currentBombCoords[0];
        int bombY = currentBombCoords[1];

        if (isInBombRange(currentX, currentY, bombX, bombY))
        {
            System.out.println("Perimettre bomb (Player " + playerNum + ")");
            startBombTimer(playerNum);
            escapeFromBomb(playerNum);
        }
        else
        {
            System.out.println("direction joueur (Player " + playerNum + ")");
            switch (playerNum) {
                case 2:
                    moveTowardsPlayer2();
                    if (nextToPlayer2()) {
                        if (!currentHasPlacedBomb) {
                            player.placeBombPlayer2();
                            bombCoordinatesMap.put(playerNum, new int[]{player.getX2(), player.getY2()});
                            hasPlacedBombMap.put(playerNum, true);
                            startBombTimer(playerNum);
                            escapeFromBomb(playerNum);
                        }
                    }
                    break;
                case 3:
                    moveTowardsPlayer3();
                    if (nextToPlayer3()) {
                        if (!currentHasPlacedBomb) {
                            player.placeBombPlayer3();
                            bombCoordinatesMap.put(playerNum, new int[]{player.getX3(), player.getY3()});
                            hasPlacedBombMap.put(playerNum, true);
                            startBombTimer(playerNum);
                            escapeFromBomb(playerNum);
                        }
                    }
                    break;
                case 4:
                    moveTowardsPlayer4();
                    if (nextToPlayer4()) {
                        if (!currentHasPlacedBomb) {
                            player.placeBombPlayer4();
                            bombCoordinatesMap.put(playerNum, new int[]{player.getX4(), player.getY4()});
                            hasPlacedBombMap.put(playerNum, true);
                            startBombTimer(playerNum);
                            escapeFromBomb(playerNum);
                        }
                    }
                    break;
            }
        }
    }


    private boolean nextToPlayer2()
    {
        if (((player.getX2() - player.getX1() == 1) && (player.getY2() - player.getY1() == 0)) || ((player.getX2() - player.getX1() == 0) && (player.getY2() - player.getY1() == 1)) || ((player.getX2() - player.getX1() == 0) && (player.getY2() - player.getY1() == 0)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean nextToPlayer3()
    {
        if (((player.getX3() - player.getX1() == 1) && (player.getY3() - player.getY1() == 0)) || ((player.getX3() - player.getX1() == 0) && (player.getY3() - player.getY1() == 1)) || ((player.getX3() - player.getX1() == 0) && (player.getY3() - player.getY1() == 0)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean nextToPlayer4()
    {
        if (((player.getX4() - player.getX1() == 1) && (player.getY4() - player.getY1() == 0)) || ((player.getX4() - player.getX1() == 0) && (player.getY4() - player.getY1() == 1)) || ((player.getX4() - player.getX1() == 0) && (player.getY4() - player.getY1() == 0)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean moveTowardsPlayer2()
    {
        int botX = player.getX2();
        int botY = player.getY2();
        int targetX = player.getX1();
        int targetY = player.getY1();

        List<int[]> path = findPathBFS(botX, botY, targetX, targetY);

        if (path != null && !path.isEmpty())
        {
            int[] nextStep = path.get(0);
            int dx = nextStep[0] - botX;
            int dy = nextStep[1] - botY;
            player.movePlayer2(dx, dy);
            System.out.println("BFS find");
            return true;
        }
        else
        {
            System.out.println("BFS not find");
            int dx = 0, dy = 0;

            if (botX < targetX) dx = 1;
            else if (botX > targetX) dx = -1;

            if (botY < targetY) dy = 1;
            else if (botY > targetY) dy = -1;

            // Essayer horizontalement
            if (dx != 0)
            {
                int nextX = botX + dx;
                if (canMove(nextX, botY))
                {
                    player.movePlayer2(dx, 0);
                    return true;
                }
                else if (isBreakableWall(nextX, botY) && !hasPlacedBombMap.get(2)) // Use specific flag for player 2
                {
                    placeBombToBreakWall(2, nextX, botY);
                    return true;
                }
            }
            // Essayer verticalement
            if (dy != 0)
            {
                int nextY = botY + dy;
                if (canMove(botX, nextY))
                {
                    player.movePlayer2(0, dy);
                    return true;
                }
                else if (isBreakableWall(botX, nextY) && !hasPlacedBombMap.get(2)) // Use specific flag for player 2
                {
                    placeBombToBreakWall(2, botX, nextY);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean moveTowardsPlayer3()
    {
        int botX = player.getX3();
        int botY = player.getY3();
        int targetX = player.getX1();
        int targetY = player.getY1();

        List<int[]> path = findPathBFS(botX, botY, targetX, targetY);

        if (path != null && !path.isEmpty())
        {
            int[] nextStep = path.get(0);
            int dx = nextStep[0] - botX;
            int dy = nextStep[1] - botY;
            player.movePlayer3(dx, dy);
            System.out.println("BFS find");
            return true;
        }
        else
        {
            System.out.println("BFS not find");
            int dx = 0, dy = 0;

            if (botX < targetX) dx = 1;
            else if (botX > targetX) dx = -1;

            if (botY < targetY) dy = 1;
            else if (botY > targetY) dy = -1;

            // Essayer horizontalement
            if (dx != 0)
            {
                int nextX = botX + dx;
                if (canMove(nextX, botY))
                {
                    player.movePlayer3(dx, 0);
                    return true;
                }
                else if (isBreakableWall(nextX, botY) && !hasPlacedBombMap.get(3)) // Use specific flag for player 3
                {
                    placeBombToBreakWall(3, nextX, botY);
                    return true;
                }
            }
            // Essayer verticalement
            if (dy != 0)
            {
                int nextY = botY + dy;
                if (canMove(botX, nextY))
                {
                    player.movePlayer3(0, dy);
                    return true;
                }
                else if (isBreakableWall(botX, nextY) && !hasPlacedBombMap.get(3)) // Use specific flag for player 3
                {
                    placeBombToBreakWall(3, botX, nextY);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean moveTowardsPlayer4()
    {
        int botX = player.getX4();
        int botY = player.getY4();
        int targetX = player.getX1();
        int targetY = player.getY1();

        List<int[]> path = findPathBFS(botX, botY, targetX, targetY);

        if (path != null && !path.isEmpty())
        {
            int[] nextStep = path.get(0);
            int dx = nextStep[0] - botX;
            int dy = nextStep[1] - botY;
            player.movePlayer4(dx, dy);
            System.out.println("BFS find");
            return true;
        }
        else
        {
            System.out.println("BFS not find");
            int dx = 0, dy = 0;

            if (botX < targetX) dx = 1;
            else if (botX > targetX) dx = -1;

            if (botY < targetY) dy = 1;
            else if (botY > targetY) dy = -1;

            // Essayer horizontalement
            if (dx != 0)
            {
                int nextX = botX + dx;
                if (canMove(nextX, botY))
                {
                    player.movePlayer4(dx, 0);
                    return true;
                }
                else if (isBreakableWall(nextX, botY) && !hasPlacedBombMap.get(4)) // Use specific flag for player 4
                {
                    placeBombToBreakWall(4, nextX, botY);
                    return true;
                }
            }
            // Essayer verticalement
            if (dy != 0)
            {
                int nextY = botY + dy;
                if (canMove(botX, nextY))
                {
                    player.movePlayer4(0, dy);
                    return true;
                }
                else if (isBreakableWall(botX, nextY) && !hasPlacedBombMap.get(4)) // Use specific flag for player 4
                {
                    placeBombToBreakWall(4, botX, nextY);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBreakableWall(int x, int y)
    {
        Tile cases = tiles[y][x];
        if (cases.getType() == Tile.Type.BREAKABLE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Modify startBombTimer to take the player number
    private void startBombTimer(int playerNum)
    {
        Timeline bombTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            hasPlacedBombMap.put(playerNum, false); // Reset bomb state for this specific bot
            bombCoordinatesMap.put(playerNum, new int[]{0, 0}); // Reset bomb coordinates
        }));
        bombTimer.setCycleCount(1);
        bombTimer.play();
    }

    // Consolidate placeBombToBreakWall into a single method
    private void placeBombToBreakWall(int playerNum, int x, int y)
    {
        int currentX = 0;
        int currentY = 0;

        switch (playerNum) {
            case 2:
                player.placeBombPlayer2();
                currentX = player.getX2();
                currentY = player.getY2();
                break;
            case 3:
                player.placeBombPlayer3();
                currentX = player.getX3();
                currentY = player.getY3();
                break;
            case 4:
                player.placeBombPlayer4();
                currentX = player.getX4();
                currentY = player.getY4();
                break;
        }
        bombCoordinatesMap.put(playerNum, new int[]{currentX, currentY});
        System.out.println("Bot " + playerNum + " a placé une bombe pour casser le mur à : (" + x + ", " + y + ")");
        hasPlacedBombMap.put(playerNum, true);
        startBombTimer(playerNum); // Start timer for this specific bot's bomb
    }

    // Consolidate escapeFromBomb into a single method
    private boolean escapeFromBomb(int playerNum)
    {
        int botX = 0;
        int botY = 0;
        switch (playerNum) {
            case 2:
                botX = player.getX2();
                botY = player.getY2();
                break;
            case 3:
                botX = player.getX3();
                botY = player.getY3();
                break;
            case 4:
                botX = player.getX4();
                botY = player.getY4();
                break;
        }

        // Get the bomb coordinates relevant to this bot
        int[] currentBombCoords = bombCoordinatesMap.get(playerNum);
        int bombX = currentBombCoords[0];
        int bombY = currentBombCoords[1];


        // Parcours de toutes les cases
        for (int y = 0; y < Game.GRID_HEIGHT; y++)
        {
            for (int x = 0; x < Game.GRID_WIDTH; x++)
            {
                // Ensure isInBombRange uses the correct bomb coordinates for the current bot
                if (canMove(x, y) && !isInBombRange(x, y, bombX, bombY))
                {
                    List<int[]> path = findPathBFS(botX, botY, x, y);
                    if (path != null && !path.isEmpty())
                    {
                        int[] next = path.get(0);
                        switch (playerNum) {
                            case 2:
                                player.movePlayer2(next[0] - botX, next[1] - botY);
                                break;
                            case 3:
                                player.movePlayer3(next[0] - botX, next[1] - botY);
                                break;
                            case 4:
                                player.movePlayer4(next[0] - botX, next[1] - botY);
                                break;
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private List<int[]> findPathBFS(int startX, int startY, int goalX, int goalY)
    {
        boolean[][] visited = new boolean[Game.GRID_HEIGHT][Game.GRID_WIDTH];
        Map<String, int[]> parentMap = new HashMap<>();
        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        while (!queue.isEmpty())
        {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (x == goalX && y == goalY)
            {
                // Reconstruire le chemin
                List<int[]> path = new LinkedList<>();
                while (x != startX || y != startY)
                {
                    path.add(0, new int[]{x, y});
                    int[] parent = parentMap.get(x + "," + y);
                    x = parent[0];
                    y = parent[1];
                }
                return path;
            }

            for (int i = 0; i < 4; i++)
            {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (nx >= 0 && nx < Game.GRID_WIDTH && ny >= 0 && ny < Game.GRID_HEIGHT && !visited[ny][nx] && canMove(nx, ny))
                {
                    visited[ny][nx] = true;
                    parentMap.put(nx + "," + ny, new int[]{x, y});
                    queue.add(new int[]{nx, ny});
                }
            }
        }

        return null; // Aucun chemin trouvé
    }

    // Modify isInBombRange to take specific bomb coordinates
    private boolean isInBombRange(int x, int y, int bombX, int bombY)
    {
        int bombRadius = 2; // ou récupère-le dynamiquement si configurable
        if (x == bombX && Math.abs(y - bombY) <= bombRadius) return true;
        if (y == bombY && Math.abs(x - bombX) <= bombRadius) return true;
        return false;
    }

    private boolean canMove(int x, int y)
    {
        return x >= 0 && x < Game.GRID_WIDTH && y >= 0 && y < Game.GRID_HEIGHT && !player.isObstacle(x, y);
    }
}