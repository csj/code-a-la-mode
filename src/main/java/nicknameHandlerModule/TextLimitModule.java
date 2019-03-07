package nicknameHandlerModule;

import java.util.ArrayList;
import java.util.List;

import com.codingame.game.Player;
import com.codingame.gameengine.core.Module;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

public class TextLimitModule implements Module {

    class TextLimit {
        int textId;
        int width, height;
        boolean shrink;
    }

    List<TextLimit> limits = new ArrayList<>(6);
    private MultiplayerGameManager<Player> gameManager;
    
    @Inject
    public TextLimitModule(MultiplayerGameManager<Player> gameManager) {
        this.gameManager = gameManager;
        gameManager.registerModule(this);
    }
    
    @Override
    public void onGameInit() {
        gameManager.setViewGlobalData("limits", limits.toArray());
    }

    private void setAvailableSpace(Text nick, int width, int height) {
        TextLimit textLimit = new TextLimit();
        textLimit.textId = nick.getId();
        textLimit.width = width;
        textLimit.height = height;

        limits.add(textLimit);
    }

    public void limitAvailableSpace(Text nick, int width, int height) {
        setAvailableSpace(nick, width, height);
    }

    @Override
    public void onAfterGameTurn() {

    }

    @Override
    public void onAfterOnEnd() {

    }
}
