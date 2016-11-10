package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.GameRenderer;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.ui.GameUI;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

public class WallpaperScreen extends DemoScreen {
    private GameUI gameUI;
    private boolean requestUpdate = false;
    private FlowergotchiGame game;
    private GameRenderer renderer;
    private GameWorld world;
    private ResourceManager manager;

    public WallpaperScreen(FlowergotchiGame game) {
        super(game);
        this.game = game;


        world = new GameWorld();
        manager = game.manager;
        renderer = new GameRenderer(game, new Vector2(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight), manager);
        this.game.serviceCallback().createNewWorld(null, null, null);
        world = this.game.serviceCallback().updateSession(world);
        gameUI = new GameUI(game, renderer, world, manager, true);
        world.gameObjectManager().loadLoadable(manager);
        world.gameObjectManager().addObjectsToUI(manager, gameUI);
    }

    public void RequestUpdate() {
        this.requestUpdate = true;
    }

    @Override
    public void show() {
        requestWorldUpdate();
    }

    @Override
    public void render(float delta) {
        if (requestUpdate) {
            requestUpdate = false;
            requestWorldUpdate();
        }
        renderer.doRender(gameUI);

    }

    public GameWorld getWorld() {
        return world;
    }

    @Override
    public void resize(int width, int height) {
        requestWorldUpdate();
        renderer.getBackViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    private void requestWorldUpdate() {

        Gdx.app.log("wallpaper", "update");
        Gdx.app.log("wallpaper", String.valueOf(world.hashCode()));
        do {
            world = game.updateService(world);
        } while (world == null || world.gameObjectManager() == null);
        Gdx.app.log("wallpaper", String.valueOf(world.hashCode()));
        world.gameObjectManager().loadLoadable(manager);
        world.gameObjectManager().addObjectsToUI(manager, gameUI);
        world.onUpdateObjects(manager, gameUI);
        gameUI.updateUI(world);
        Gdx.graphics.requestRendering();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        requestWorldUpdate();
    }

    @Override
    public void dispose() {
    }
}

