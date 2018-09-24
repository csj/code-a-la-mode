package com.codingame.game.sample;

import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {

//        System.err.println("hallo");
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
//        System.err.println("mcfly?");

        // Adds as many player as you need to test your game
        gameRunner.addAgent(VanillaIceCreamPlayer.class);
        gameRunner.addAgent(NaiveAllItemsPlayer.class);
        gameRunner.addAgent(VanillaIceCreamPlayer.class);
        gameRunner.addAgent(NaiveAllItemsPlayer.class);

        // gameRunner.addAgent("python3 /home/user/player.py");

        gameRunner.start();
    }
}
