import com.codingame.gameengine.runner.MultiplayerGameRunner;
import players.IceCreamPlayer;
import players.MovePlayer;
import players.WaitPlayer;

public class Main {
    public static void main(String[] args) {

//        System.err.println("hallo");
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
//        System.err.println("mcfly?");

        // Adds as many player as you need to test your game
        gameRunner.addAgent(IceCreamPlayer.class);
        gameRunner.addAgent(IceCreamPlayer.class);
        gameRunner.addAgent(IceCreamPlayer.class);
        gameRunner.addAgent(IceCreamPlayer.class);

        // gameRunner.addAgent("python3 /home/user/player.py");

        gameRunner.start();
    }
}
