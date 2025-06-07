package com.example.BomberMAN.GamePlay;

import com.example.BomberMAN.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Random;

public class Bot
{
    private Player player;
    private Random random;
    private Timeline timeline;
    private boolean hasPlacedBomb = false;
    private boolean moveTowardsNext = true;


    public Bot(Player botPlayer)
    {
        this.player = botPlayer;
        random = new Random();

        timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            if (moveTowardsNext)
            {
                moveTowardsPlayer();
            }
            else {
                moveRandomly();
            }
            moveTowardsNext = !moveTowardsNext;

            if (nextToPlayer())
            {
                if (!hasPlacedBomb)
                {
                    player.placeBombPlayer2();
                    escape();
                    hasPlacedBomb = true;
                }
            }
            else
            {
                hasPlacedBomb = false;
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

    public void escape()
    {
        // Essaie de faire 2 pas vers le bas
        if (canMove(player.getX2(), player.getY2() + 1))
        {
            player.movePlayer2(0, 2);
        }
        // Essaie de faire 2 pas vers le haut
        else if (canMove(player.getX2(), player.getY2() - 1))
        {
            player.movePlayer2(0, -2);
        }
        // Sinon essaie 2 pas vers la droite
        else if (canMove(player.getX2() + 1, player.getY2()))
        {
            player.movePlayer2(2, 0);
        }
        // Sinon essaie 2 pas vers la gauche
        else if (canMove(player.getX2() - 1, player.getY2()))
        {
            player.movePlayer2(-2, 0);
        }
        // Sinon essaie bas + droite
        else if (canMove(player.getX2() + 1, player.getY2() + 1))
        {
            player.movePlayer2(1, 1);
        }
        // Sinon essaie haut + droite
        else if (canMove(player.getX2() + 1, player.getY2() - 1))
        {
            player.movePlayer2(1, -1);
        }
        // Sinon essaie bas + gauche
        else if (canMove(player.getX2() - 1, player.getY2() + 1))
        {
            player.movePlayer2(-1, 1);
        }
        // Sinon essaie haut + gauche
        else if (canMove(player.getX2() - 1, player.getY2() - 1))
        {
            player.movePlayer2(-1, -1);
        }
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

        int dx = 0, dy = 0;

        if (botX < targetX) dx = 1;
        else if (botX > targetX) dx = -1;

        if (botY < targetY) dy = 1;
        else if (botY > targetY) dy = -1;

        // Essayer de bouger horizontalement en premier
        if (dx != 0 && canMove(botX + dx, botY)) {
            player.movePlayer2(dx, 0);
            return true;
        }
        // Sinon essayer verticalement
        else if (dy != 0 && canMove(botX, botY + dy)) {
            player.movePlayer2(0, dy);
            return true;
        }

        return false;
    }

    private boolean canMove(int x, int y)
    {
        return x >= 0 && x < Game.GRID_WIDTH && y >= 0 && y < Game.GRID_HEIGHT && !player.isObstacle(x, y);
    }
}