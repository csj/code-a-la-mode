package com.codingame.game.sample;

import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
//        gameRunner.setSeed(-2749567458568488030L);

        // Adds as many player as you need to test your game
//        gameRunner.addAgent(IceCreamPlayer.class);
        gameRunner.addAgent(NaiveAllItemsPlayer.class);
        gameRunner.addAgent(NaiveAllItemsPlayer.class);
        gameRunner.addAgent(NaiveAllItemsPlayer.class);
//        gameRunner.addAgent(HugPlayer.class);

        gameRunner.setLeagueLevel(4);

        gameRunner.start();
    }
}
