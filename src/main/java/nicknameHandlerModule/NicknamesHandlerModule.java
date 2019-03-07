package nicknameHandlerModule;

import java.util.ArrayList;
import java.util.List;

import com.codingame.game.Player;
import com.codingame.gameengine.core.Module;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

public class NicknamesHandlerModule implements Module {
    List<Text> nicknames = new ArrayList<>(2);
    private MultiplayerGameManager<Player> gameManager;
    
    @Inject
    public NicknamesHandlerModule(MultiplayerGameManager<Player> gameManager) {
        this.gameManager = gameManager;
        gameManager.registerModule(this);
    }
    
    @Override
    public void onGameInit() {
        gameManager.setViewGlobalData("nicks", nicknames.stream().mapToInt(n -> n.getId()).toArray());
    }

    public void registerNickname(Text nick) {
        nicknames.add(nick);
    }

    @Override
    public void onAfterGameTurn() {

    }

    @Override
    public void onAfterOnEnd() {

    }
}
