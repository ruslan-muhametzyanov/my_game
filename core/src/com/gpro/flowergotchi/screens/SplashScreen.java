package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;

public class SplashScreen implements Screen {
    private static final float delay = 2.0f;

    private final FlowergotchiGame game;
    private StretchViewport viewport;
    private Stage stage;

    public SplashScreen(FlowergotchiGame game) {
        this.game = game;

    }

    public static void loadAsset(FlowergotchiGame game) {
        game.manager.load("click.wav", Sound.class);
        game.manager.load("sounds/music.ogg", Music.class);
        game.manager.load("sounds/plantisdead.ogg", Music.class);
        game.manager.load("sounds/star.ogg", Sound.class);
        game.manager.load("images/skin/buttons.pack", TextureAtlas.class);

        game.manager.finishLoading();
        TextureAtlas textureAtlas = game.manager.get("images/skin/buttons.pack", TextureAtlas.class);
        textureAtlas.findRegion("defButtonPressed").flip(false, true);
        textureAtlas.findRegion("defButton").flip(false, true);
        textureAtlas.findRegion("smallYellow").flip(false, true);
        textureAtlas.findRegion("smallYellowPressed").flip(false, true);
        textureAtlas.findRegion("smallRed").flip(false, true);
        textureAtlas.findRegion("smallRedPressed").flip(false, true);
        game.utility.loadDefault();
    }

    @Override
    public void show() {
        OrthographicCamera camera = new OrthographicCamera(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        camera.setToOrtho(false, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        Texture gproLogo = game.manager.getTexture("mainmenuscreen/gpro.png");

        SpriteBatch batcher = new SpriteBatch();
        viewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, camera);
        stage = new Stage(viewport, batcher);

        Image img = new Image(gproLogo);
        img.setSize(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        img.setColor(img.getColor().r, img.getColor().g, img.getColor().b, 0.0f);
        img.addAction(Actions.sequence(Actions.alpha(1.0f, 1.0f), Actions.run(new Runnable() {
            @Override
            public void run() {
                loadAsset(game);
            }
        }), Actions.parallel(Actions.run(new Runnable() {
            @Override
            public void run() {

                game.manager.update();
            }
        }), Actions.delay(delay)), Actions.run(new Runnable() {
            @Override
            public void run() {
                game.manager.finishLoading();
            }
        }), Actions.alpha(0.0f, 1.0f), Actions.run(new Runnable() {
            @Override
            public void run() {
                MainMenuScreen screen = new MainMenuScreen(game);
                game.addScreen(screen);
            }
        })));
        stage.addActor(img);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

}
