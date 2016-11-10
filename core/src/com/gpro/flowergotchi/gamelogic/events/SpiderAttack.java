package com.gpro.flowergotchi.gamelogic.events;

import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.util.Rand;

/**
 * Created by user on 25.01.2016.
 */
public class SpiderAttack extends Event {
    private static final String message_top = "spiderattack_top";
    private static final String message_body = "spiderattack_body";

    public SpiderAttack(int timeBeforeStart, float duration, boolean eventContinious) {
        super(timeBeforeStart, duration, eventContinious);
    }

    public SpiderAttack() {
        this(0, Rand.randInt(2, 20) * FlowergotchiGame.updateRate, false);

    }

    @Override
    public boolean eventEndCondition(GameWorld world) {
        return false;
    }

    @Override
    public void onUpdateObjectsClient(ResourceManager resourceManager, final GameUI ui) {
        if (ui.isWallpaperMode()) {
            return;
        }
        if (this.getClientside().get(messageDrawn) == 0) {
            final SpiderAttack a = this;
            a.getClientside().set(messageDrawn, 1);
            //EventHelper.floatingMessage(ui.getGame(), ui.getStage(), null, "spiders/spider.png", message_top, message_body, "ok");
            ui.getGame().getGameScreen().RequestUpdate();

        }
    }


    @Override
    public void startEvent(GameWorld world) {

    }


    @Override
    public void eventHandle(GameWorld world) {
        super.eventHandle(world);
        if (Rand.randFloat(0.0f, 1.0f) < 0.5 && world.getActiveFlower().getIntVarVal(IntGameVariables.Var_Spider) == 0) {
            world.getActiveFlower().setIntVarVal(IntGameVariables.Var_Spider, 1);
        }
    }

    @Override
    public boolean eventEnd(GameWorld world) {
        return true;
    }
}
