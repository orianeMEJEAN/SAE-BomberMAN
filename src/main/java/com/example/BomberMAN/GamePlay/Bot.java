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
    private boolean hasPlacedBomb = false;

    private Tile[][] tiles;
    private boolean isEscaping = false;

    private int dXNiv1Escape = 0, dYNiv1Escape = 0, dXNiv2Escape = 0, dYNiv2Escape = 0;

    private int xBomb, yBomb;

    public Bot(Player botPlayer, Tile[][] tiles)
    {
        this.player = botPlayer;
        this.tiles = tiles;
        random = new Random();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            // Si le bot est en train de fuir, il ne fait rien d'autre
            if (isInBombRange(player.getX2(), player.getY2()))
            {
                System.out.println("Perimettre bomb");
                startBombTimer();
                escapeFromBomb();
            }
            else
            {
                System.out.println("direction joueur");
                moveTowardsPlayer();

                // Si à côté du joueur, on place une bombe et on fuit
                if (nextToPlayer())
                {
                    if (!hasPlacedBomb)
                    {
                        player.placeBombPlayer2();
                        xBomb = player.getX2();
                        yBomb = player.getY2();
                        hasPlacedBomb = true;
                        startBombTimer();
                        escapeFromBomb();
                    }
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private boolean nextToPlayer()
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

    private boolean escapePathFound()
    {
        boolean path = false;
        dXNiv1Escape = 0;
        dYNiv1Escape = 0;
        dXNiv2Escape = 0;
        dYNiv2Escape = 0;

         // xb , yb + 1 (bas)
         if(canMove(player.getX2(), player.getY2() + 1))
         {
             dXNiv1Escape = 0;
             dYNiv1Escape = 1;

             // x, y + 1 (bas)
             if(canMove(player.getX2(), player.getY2() + 2))
             {
                 dXNiv2Escape = 0;
                 dYNiv2Escape = 1;
                 path = true;
             }
             // x + 1, y (droite)
             else if(canMove(player.getX2() + 1, player.getY2()))
             {
                 dXNiv2Escape = 1;
                 dYNiv2Escape = 0;
                 path = true;
             }
             // x - 1, y (gauche)
             else if(canMove(player.getX2() - 1, player.getY2()))
             {
                 dXNiv2Escape = -1;
                 dYNiv2Escape = 0;
                 path = true;
             }
         }
         // xb , yb - 1 (haut)
         else if (canMove(player.getX2(), player.getY2() - 1))
         {
             dXNiv1Escape = 0;
             dYNiv1Escape = -1;

             // x, y - 1 (haut)
             if(canMove(player.getX2(), player.getY2() - 2))
             {
                 dXNiv2Escape = 0;
                 dYNiv2Escape = -1;
                 path = true;
             }
             // x + 1, y (droite)
             else if(canMove(player.getX2() + 1, player.getY2()))
             {
                 dXNiv2Escape = 1;
                 dYNiv2Escape = 0;
                 path = true;
             }
             // x - 1, y (gauche)
             else if(canMove(player.getX2() - 1, player.getY2()))
             {
                 dXNiv2Escape = -1;
                 dYNiv2Escape = 0;
                 path = true;
             }
         }
         // xb + 1, yb (droite)
         else if (canMove(player.getX2() + 1, player.getY2()))
         {
             dXNiv1Escape = 1;
             dYNiv1Escape = 0;

             // x, y - 1 (haut)
             if(canMove(player.getX2(), player.getY2() - 1))
             {
                 dXNiv2Escape = 0;
                 dYNiv2Escape = -1;
                 path = true;
             }
             // x, y + 1 (bas)
             if(canMove(player.getX2(), player.getY2() + 2))
             {
                 dXNiv2Escape = 0;
                 dYNiv2Escape = 1;
                 path = true;
             }
             // x + 1, y (droite)
             else if(canMove(player.getX2() + 2, player.getY2()))
             {
                 dXNiv2Escape = 1;
                 dYNiv2Escape = 0;
                 path = true;
             }
         }
         // xb - 1, yb (gauche)
         else if (canMove(player.getX2() - 1, player.getY2()))
         {
             dXNiv1Escape = -1;
             dYNiv1Escape = 0;

             // x, y - 1 (haut)
             if(canMove(player.getX2(), player.getY2() - 1))
             {
                 dXNiv2Escape = 0;
                 dYNiv2Escape = -1;
                 path = true;
             }
             // x, y + 1 (bas)
             if(canMove(player.getX2(), player.getY2() + 2))
             {
                 dXNiv2Escape = 0;
                 dYNiv2Escape = 1;
                 path = true;
             }
             // x - 1, y (gauche)
             else if(canMove(player.getX2() - 2, player.getY2()))
             {
                 dXNiv2Escape = -1;
                 dYNiv2Escape = 0;
                 path = true;
             }
         }
        else
        {
            System.out.println("Bot ne trouve pas de chemin pour fuir !");
        }
        return path;
    }

    private boolean moveRandomly()
    {
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        int tries = 0;
        while (tries < 10)
        {
            int dir = random.nextInt(4);
            int newX = player.getX2() + dx[dir];
            int newY = player.getY2() + dy[dir];

            if (canMove(newX, newY))
            {
                player.movePlayer2(dx[dir], dy[dir]);
                return true;
            }
            else if (isBreakableWall(newX, newY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(newX, newY);
                return true;
            }
            tries++;
        }
        return false;
    }

    private boolean moveTowardsPlayer()
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
                else if (isBreakableWall(nextX, botY) && !hasPlacedBomb)
                {
                    placeBombToBreakWall(nextX, botY);
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
                else if (isBreakableWall(botX, nextY) && !hasPlacedBomb)
                {
                    placeBombToBreakWall(botX, nextY);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean moveTowardsPlayerWithoutBFS()
    {
        int botX = player.getX2();
        int botY = player.getY2();

        int targetX = player.getX1();
        int targetY = player.getY1();

        boolean invertX = false;
        boolean invertY = false;

        int dx = 0, dy = 0;

        if (botX < targetX) dx = 1;
        else if (botX > targetX) dx = -1;

        if (botY < targetY) dy = 1;
        else if (botY > targetY) dy = -1;

//        if (dx != 0)
//        {
//            int nextX = botX + dx;
//            if (canMove(nextX, botY))
//            {
//                player.movePlayer2(dx, 0);
//                return true;
//            }
//            else if (isBreakableWall(nextX, botY) && !hasPlacedBomb)
//            {
//                placeBombToBreakWall(nextX, botY);
//                xBomb = botX;
//                yBomb = botY;
//                return true;
//            }
//        }
//
//        if (dy != 0)
//        {
//
//            int nextY = botY + dy;
//            if (canMove(botX, nextY))
//            {
//                player.movePlayer2(0, dy);
//                return true;
//            }
//            else if (isBreakableWall(botX, nextY) && !hasPlacedBomb)
//            {
//                placeBombToBreakWall(botX, nextY);
//                xBomb = botX;
//                yBomb = botY;
//                escape();
//                return true;
//            }
//        }

        // Essayer horizontalement
        if (dx != 0)
        {
            int nextX = botX + dx;
            if (canMove(nextX, botY))
            {
                player.movePlayer2(dx, 0);
                return true;
            }
            else if (isBreakableWall(nextX, botY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(nextX, botY);
                return true;
            }
            else
            {
                invertX = true;
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
            else if (isBreakableWall(botX, nextY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(botX, nextY);
                return true;
            }
            else
            {
                invertY = true;
            }
        }

        // Test horizontalement dans l'autre sens
        if(dx != 0 && invertX && dy != 0)
        {
            int nextX = botX + (dx * -1);
            if (canMove(nextX, botY))
            {
                player.movePlayer2(dx * -1, 0);
                // Déplacement supplémentaire verticalement
                if(canMove(nextX, botY + dy))
                {
                    player.movePlayer2(0, dy);
                    return true;
                }
                else if (isBreakableWall(nextX, botY + dy) && !hasPlacedBomb)
                {
                    placeBombToBreakWall(nextX, botY + dy);
                    return true;
                }
            }
            else if (isBreakableWall(nextX, botY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(nextX, botY);
                return true;
            }
        }

        // Test verticalement dans l'autre sens
        if(dy != 0 && invertY && dx != 0)
        {
            int nextY = botY + (dy * -1);
            if (canMove(botX, nextY))
            {
                player.movePlayer2(0, (dy * -1));

                // déplacement supplémentaire horizontalement
                if(canMove(botX + dx, nextY))
                {
                    player.movePlayer2(dx, 0);
                    return true;
                }
                else if (isBreakableWall(botX + dx, nextY) && !hasPlacedBomb)
                {
                    placeBombToBreakWall(botX + dx, nextY);
                    return true;
                }
            }
            else if (isBreakableWall(botX, nextY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(botX, nextY);
                return true;
            }
        }

        if (dx == 0 && dy != 0)
        {
            int nextY = botY + dy;
            if (canMove(botX, nextY))
            {
                player.movePlayer2(0, dy);
                return true;
            }
            else if (isBreakableWall(botX, nextY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(botX, nextY);
                return true;
            }
            else
            {
                invertY = true;
            }
        }

        if(dx != 0 && dy == 0)
        {
            int nextX = botX + dx;
            if (canMove(nextX, botY))
            {
                player.movePlayer2(dx, 0);
                return true;
            }
            else if (isBreakableWall(nextX, botY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(nextX, botY);
                return true;
            }
            else
            {
                invertX = true;
            }
        }

        if (dx == 0 && dy != 0 && invertX)
        {
            int nextY = botY + (dy * -1);
            int nextX = botX + dy;

            if (canMove(nextX, botY))
            {
                player.movePlayer2(dy, 0);

                if(canMove(nextX, botY + dy))
                {
                    player.movePlayer2(0, dy);
                    return true;
                }
                else if (isBreakableWall(nextX, botY) && !hasPlacedBomb)
                {
                    placeBombToBreakWall(nextX, botY);
                    return true;
                }
            }
            else if (isBreakableWall(botX, nextY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(botX, nextY);
                return true;
            }
        }

        if (dx != 0 && dy == 0 && invertY)
        {
            int nextY = botY + dx;
            int nextX = botX + (dx * -1);

            if (canMove(botX, nextY))
            {
                player.movePlayer2(0, dx);

                if(canMove(botX + dx, nextY))
                {
                    player.movePlayer2(dx, 0);
                    return true;
                }
                else if (isBreakableWall(botX, nextY) && !hasPlacedBomb)
                {
                    placeBombToBreakWall(botX, nextY);
                    return true;
                }
            }
            else if (isBreakableWall(nextX, botY) && !hasPlacedBomb)
            {
                placeBombToBreakWall(nextX, botY);
                return true;
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

    private void startBombTimer()
    {
        Timeline bombTimer = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            hasPlacedBomb = false;
        }));
        bombTimer.setCycleCount(1);
        bombTimer.play();
    }

    private void placeBombToBreakWall(int x, int y)
    {
        player.placeBombPlayer2();
        xBomb = player.getX2();
        yBomb = player.getY2();
        System.out.println("Bot a placé une bombe pour casser le mur à : (" + x + ", " + y + ")");
        hasPlacedBomb = true;
    }

    private boolean escapeFromBomb()
    {
        int botX = player.getX2();
        int botY = player.getY2();

        // Parcours de toutes les cases
        for (int y = 0; y < Game.GRID_HEIGHT; y++)
        {
            for (int x = 0; x < Game.GRID_WIDTH; x++)
            {
                if (canMove(x, y) && !isInBombRange(x, y))
                {
                    List<int[]> path = findPathBFS(botX, botY, x, y);
                    if (path != null && !path.isEmpty())
                    {
                        int[] next = path.get(0);
                        player.movePlayer2(next[0] - botX, next[1] - botY);
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

    private boolean isInBombRange(int x, int y)
    {
        int bombRadius = 2; // ou récupère-le dynamiquement si configurable
        if (x == xBomb && Math.abs(y - yBomb) <= bombRadius) return true;
        if (y == yBomb && Math.abs(x - xBomb) <= bombRadius) return true;
        return false;
    }

    private boolean canMove(int x, int y)
    {
        return x >= 0 && x < Game.GRID_WIDTH && y >= 0 && y < Game.GRID_HEIGHT && !player.isObstacle(x, y);
    }
}