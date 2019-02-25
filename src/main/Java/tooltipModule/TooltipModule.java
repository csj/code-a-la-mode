package tooltipModule;

import java.io.Console;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.codingame.game.Player;
import com.codingame.game.view.BoardView;
import com.codingame.gameengine.core.AbstractPlayer;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.Module;
import com.codingame.gameengine.module.entities.Entity;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

public class TooltipModule implements Module {

    GameManager<AbstractPlayer> gameManager;
    @Inject
    GraphicEntityModule entityModule;
    Map<Integer, Map<String, Object>> registrations;
    Map<Integer, String[]> extra, newExtra;
    Map<Integer, Map<String, Object>> newRegistrations;
    Map<Integer, Map<String, Object>> previousRegistrations = new HashMap<>();
    int size = 5;

    @Inject
    public TooltipModule(GameManager<AbstractPlayer> gameManager) {
        this.gameManager = gameManager;
        gameManager.registerModule(this);
        registrations = new HashMap<>();
        newRegistrations = new HashMap<>();
        extra = new HashMap<>();
        newExtra = new HashMap<>();
    }

    @Override
    public void onGameInit() {
        sendFrameData();
    }

    @Override
    public void onAfterGameTurn() {
        sendFrameData();
    }

    @Override
    public void onAfterOnEnd() {
        sendFrameData();
    }

    private void sendFrameData() {
        Object[] data = new Object[]{
                newRegistrations,
                newExtra};

        gameManager.setViewData("tooltips", data);
        previousRegistrations.clear();
        for(Integer key : newRegistrations.keySet()){
            previousRegistrations.put(key, newRegistrations.get(key));
        }

        newRegistrations.clear();
        newExtra.clear();
    }

    public void registerEntity(Entity<?> entity) {
        registerEntity(entity, new HashMap<>());
    }

    public void registerEntity(Entity<?> entity, String tooltip) {
        Map<String, Object> params = new HashMap<>();
        String[] tt = tooltip.split("\n");
        for(String s : tt){
            String[] splitted = s.split(":");
            if(splitted.length < 2) continue;
            params.put(splitted[0], splitted[1]);
        }

        registerEntity(entity.getId(), params);
    }


    public void registerEntity(Entity<?> entity, Map<String, Object> params) {
        registerEntity(entity.getId(), params);
    }

    public void registerEntity(int id, Map<String, Object> params) {
        if (!params.equals(registrations.get(id))) {
            newRegistrations.put(id, params);
            registrations.put(id, params);
        }
    }

    boolean deepEquals(String[] a, String[] b) {
        return Arrays.deepEquals(a,b);
    }

    public Map<String, Object> getParams(int id) {
        Map<String, Object> params = registrations.get(id);
        if (params == null) {
            params = new HashMap<>();
            registrations.put(id, params);
        }
        return params;
    }

    public void updateExtraTooltipText(Entity<?> entity, String... lines) {
        int id = entity.getId();
        if (!deepEquals(lines, extra.get(id)))
        {
            newExtra.put(id, lines);
            extra.put(id, lines);
        }
    }
}