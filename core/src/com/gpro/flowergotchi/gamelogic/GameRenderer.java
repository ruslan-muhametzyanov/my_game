package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.ui.GameUI;

public class GameRenderer {
    private final OrthographicCamera cam;
    private final SpriteBatch batch;
    private final ResourceManager manager;
    private final StretchViewport backViewport;

    public GameRenderer(FlowergotchiGame game, Vector2 renderSize, ResourceManager manager) {
        cam = new OrthographicCamera();
        batch = new SpriteBatch();

        cam.setToOrtho(true, renderSize.x, renderSize.y);

        backViewport = new StretchViewport(renderSize.x, renderSize.y, cam);
        this.manager = manager;
    }

    public ResourceManager getResourceManager() {
        return manager;
    }

    public StretchViewport getBackViewport() {
        return backViewport;
    }

    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    public void doRender(GameUI gameUI) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameUI.onDrawUI();
    }

}