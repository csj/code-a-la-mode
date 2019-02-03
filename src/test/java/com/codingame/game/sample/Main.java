package com.codingame.game.sample;

import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {

//        System.err.println("hallo");
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.setSeed(-2749567458568488030L);
//        System.err.println("mcfly?");

        // Adds as many player as you need to test your game
//        gameRunner.addAgent(IceCreamPlayer.class);
        gameRunner.addAgent(NaiveAllItemsPlayer.class);
        gameRunner.addAgent(WaitPlayer.class);
        gameRunner.addAgent(WaitPlayer.class);

        // gameRunner.addAgent("python3 /home/user/player.py");

        gameRunner.start();
    }
}
