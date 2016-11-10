package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.gpro.flowergotchi.FlowergotchiGame;

import java.util.LinkedList;

/**
 * Created by user on 12.01.2016.
 */
class SmileQueue {
    private final GameUI ui;
    private final FlowergotchiGame game;
    private final GameUI gameUI;
    private final LinkedList<Smile> smileQueue;
    private long nextCoolSmile;
    private boolean smileActive = false;

    public SmileQueue(GameUI gameUI, GameUI ui, FlowergotchiGame game) {
        this.gameUI = gameUI;
        this.ui = ui;
        this.game = game;

        smileQueue = new LinkedList<Smile>();
    }

    public void addSmile(String tex, String tag) {
        for (Smile s : smileQueue) {
            if (s.getName().equals(tag)) {
                Gdx.app.log("exists", "exists");
                return;
            }
        }
        Vector2 bubble;
        if (ui.isWallpaperMode()) {
            bubble = new Vector2(game.getWallpaperScreen().getWorld().getActivePot().getPotInsectZone().getSecond());
        } else {
            bubble = new Vector2(game.getGameScreen().getWorld().getActivePot().getPotInsectZone().getSecond());
        }

        bubble.x += 100;
        bubble.y -= 100;
        Smile smile = new Smile(ui, gameUI.getGame().manager, gameUI.getGame().manager.getTexture(tex), bubble);

        smile.setName(tag);
        smileQueue.addLast(smile);
    }

    public void requestChangeSmile(boolean delete) {
        if (!smileQueue.isEmpty()) {
            smileActive = false;
            if (delete) {
                smileQueue.pop();
            }
            if (!smileQueue.isEmpty()) {
                Smile smile = smileQueue.getFirst();
                smileActive = true;
                gameUI.getStage().addActor(smile);
            }
        }
    }

    public long getNextCoolSmile() {
        return nextCoolSmile;
    }

    public void setNextCoolSmile(long nextCoolSmile) {
        this.nextCoolSmile = nextCoolSmile;
    }

    public boolean isSmileActive() {
        return smileActive;
    }
}
