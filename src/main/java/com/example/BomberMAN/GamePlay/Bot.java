package com.example.BomberMAN.GamePlay;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Random;

public class Bot
{
    private Player player;
    private Random random = new Random();

    public Bot(Player player)
    {
        this.player = player;
        startAI();
    }

    private void startAI()
    {
        Timeline aiLoop = new Timeline(new KeyFrame(Duration.seconds(1), e -> makeMove()));
        aiLoop.setCycleCount(Timeline.INDEFINITE);
        aiLoop.play();
    }

    private void makeMove()
    {
        int dx = 0, dy = 0;
        int direction = random.nextInt(5);

        switch (direction)
        {
            case 0: dy = -1; break;
            case 1: dy = 1; break;
            case 2: dx = -1; break;
            case 3: dx = 1; break;
            case 4:
                player.placeBombPlayer2();
                return;
        }

        player.movePlayer2(dx, dy);
    }
}