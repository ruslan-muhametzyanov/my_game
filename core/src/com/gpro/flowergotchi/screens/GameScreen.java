package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.GameRenderer;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.InputHandler;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.ui.GameUI;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

public class GameScreen extends DemoScreen {

    private final FlowergotchiGame game;
    private final GameRenderer renderer;
    private final GameUI gameUI;
    private final ResourceManager manager;
    private GameWorld world;
    private boolean requestUpdate = false;

    public GameScreen(FlowergotchiGame game, Background.Parameters param, Class pot, Plant.Parameters flower) {
        super(game);
        this.game = game;
        world = new GameWorld();
        manager = game.manager;
        renderer = new GameRenderer(game, new Vector2(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight), manager);
        this.game.serviceCallback().createNewWorld(param, pot, flower);
        world = game.updateService(world);
        gameUI = new GameUI(game, renderer, world, manager, false);
        game.setState(GameState.GS_GameProcess);
        world.gameObjectManager().loadLoadable(manager);
        game.manager.finishLoading();
        world.gameObjectManager().addObjectsToUI(manager, gameUI);

        if (!FlowergotchiGame.debugMode) {
            if (game.getPreferences().isFirstStart()) {
                game.serviceCallback().setTutorialMode(true);
                gameUI.startTutorial();
            }
        }

    }

    public void RequestUpdate() {
        this.requestUpdate = true;
    }

    public GameWorld getWorld() {
        return world;
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show");
        gameUI.fadeIn(new Runnable() {
            @Override
            public void run() {
                if (!gameUI.isWallpaperMode()) {
                    Gdx.input.setInputProcessor(gameUI.getStage());
                    gameUI.getStage().addListener(new InputHandler(game, gameUI.getStage()));
                }
            }
        });
        requestWorldUpdate(true);
        if (!getWorld().isTutorialActive() && !FlowergotchiGame.debugMode && !game.getPurchaseManager().isNoAds()) {
            game.serviceCallback().showAdMob(true);
        }

    }

    @Override
    public void render(float delta) {
        if (requestUpdate) {
            requestUpdate = false;
            requestWorldUpdate(false);
        }
        if (gameUI != null) {
            renderer.doRender(gameUI);
        }
    }

    private void requestWorldUpdate(boolean start) {
        if (gameUI.isPlantIsDead()) {
            return;
        }
        do {
            if (!start) {
                game.serviceCallback().updateClientsideVars(world);
            }
            world = game.updateService(world);
        } while (world == null || world.gameObjectManager() == null);
        world.gameObjectManager().loadLoadable(manager);
        game.manager.finishLoading();
        world.gameObjectManager().addObjectsToUI(manager, gameUI);
        world.onUpdateObjects(manager, gameUI);
        if (game.getScreen().getClass() == GameScreen.class) {
            gameUI.updateUI(world);
        }

        Gdx.app.log("GameScreen", "update");
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", "onResize");
        gameUI.fadeIn(null);
        renderer.getBackViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        requestWorldUpdate(false);
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "hide");
        if (!getWorld().isTutorialActive() && !FlowergotchiGame.debugMode && !game.getPurchaseManager().isNoAds()) {
            game.serviceCallback().showAdMob(false);
        }
    }

    @Override
    public void pause() {
        gameUI.pause();
        Gdx.app.log("GameScreen", "pause");
    }

    @Override
    public void resume() {
        Gdx.app.log("GameScreen", "resume");
        gameUI.resume();
        gameUI.fadeIn(new Runnable() {
            @Override
            public void run() {
                if (!gameUI.isWallpaperMode()) {
                    Gdx.input.setInputProcessor(gameUI.getStage());
                }
            }
        });
        requestWorldUpdate(false);
    }

    @Override
    public void dispose() {
    }
}
