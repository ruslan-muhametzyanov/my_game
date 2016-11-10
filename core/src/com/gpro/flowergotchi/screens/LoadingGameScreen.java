package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.Background;

public class LoadingGameScreen implements Screen {

    private static final int maxImage = 12;
    private final FlowergotchiGame game;
    private final Stage stage;
    private final StretchViewport viewport;
    private final boolean loadSplash;
    private Animation loadanim;
    private BitmapFont mainFont;
    private Class pot;
    private float counter = 0;
    private final SpriteBatch batch;
    private boolean updated = false;
    private Plant.Parameters flower;
    private final boolean isWallpaper;
    private final Background.Parameters param;
    private Label wallpaperPreparing;
    private boolean isInitialised = false;

    public LoadingGameScreen(FlowergotchiGame game, Background.Parameters param, String pot, Plant.Parameters flower, boolean splashLoaded, boolean wallpaper) {
        this.game = game;
        this.isWallpaper = wallpaper;
        this.param = param;
        if (pot != null) {
            try {
                this.pot = Class.forName(pot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (flower != null) {
            this.flower = flower;
        }

        this.loadSplash = !splashLoaded;
        OrthographicCamera cam = new OrthographicCamera();
        batch = new SpriteBatch();

        cam.setToOrtho(false, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        batch.setProjectionMatrix(cam.combined);
        viewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);

        stage = new Stage(viewport, batch);
    }

    public void loadFlowerAndFont() {
        Texture[] loading = new Texture[maxImage];
        TextureRegion[] loadingReg = new TextureRegion[maxImage];
        for (int i = 1; i <= maxImage; ++i) {
            game.manager.load("loadingscreen/flower" + Integer.toString(i) + ".png", Texture.class);
        }
        game.manager.finishLoading();
        for (int i = 1; i <= maxImage; ++i) {
            loading[i - 1] = game.manager.get("loadingscreen/flower" + Integer.toString(i) + ".png");
            loadingReg[i - 1] = new TextureRegion(loading[i - 1]);
        }

        loadanim = new Animation(0.08f, loadingReg);
    }

    private void loadAsset() {
        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.genMipMaps = false;
        param.format = Pixmap.Format.RGBA8888;

        game.manager.load("buttons/buttons.pack", TextureAtlas.class);

        game.manager.load("smallbug/bug.png", Texture.class, param);

        game.manager.load("spiders/spiderweb.png", Texture.class, param);
        game.manager.load("images/skin/messageBox.png", Texture.class, param);
        game.manager.load("images/skin/message.png", Texture.class, param);
        game.manager.load("arcanoid/tutorial.png", Texture.class, param);
        game.manager.load("images/skin/mediumMessageBox.png", Texture.class, param);
        game.manager.load("images/skin/emptyStar.png", Texture.class, param);
        game.manager.load("images/skin/fullStar.png", Texture.class, param);

        game.manager.load("rip.png", Texture.class, param);
        game.manager.load("blur.png", Texture.class, param);

        game.manager.load("igradrop/can.png", Texture.class, param);
        game.manager.load("gameover.ogg", Sound.class);
        game.manager.load("shovel/dig1.ogg", Sound.class);
        game.manager.load("shovel/dig2.ogg", Sound.class);
        game.manager.load("igradrop/watering.ogg", Sound.class);

        game.manager.load("sounds/camera.ogg", Sound.class);


        game.manager.load("statistics/clock.png", Texture.class, param);
        game.manager.load("statistics/indic.png", Texture.class, param);
        game.manager.load("statistics/cursor.png", Texture.class, param);

        game.manager.load("bubble/cloud1.png", Texture.class, param);
        game.manager.load("bubble/cloud2.png", Texture.class, param);
        game.manager.load("bubble/cloud3.png", Texture.class, param);
        game.manager.load("bubble/smile.png", Texture.class, param);

        game.manager.load("bubble/smile.png", Texture.class, param);
        game.manager.load("bubble/loosening.png", Texture.class, param);
        game.manager.load("bubble/water.png", Texture.class, param);
        game.manager.load("bubble/insects.png", Texture.class, param);
        game.manager.load("bubble/sad.png", Texture.class, param);
        game.manager.load("bubble/cat.png", Texture.class, param);
        game.manager.load("bubble/light.png", Texture.class, param);
        game.manager.load("bubble/spider.png", Texture.class, param);
    }

    @Override
    public void show() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {

            }
        });
        stage.addActor(fade);
    }

    private void update() {
        if (game.manager.update()) {
            // Check if game is ready
            if (isWallpaper) {
                if (!game.serviceCallback().isWorldCreated()) {
                    wallpaperPreparing.setVisible(true);
                    return;
                } else {
                    wallpaperPreparing.setVisible(false);
                }
            }

            Runnable runnable;
            if (isWallpaper) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        WallpaperScreen wallpaperScreen = new WallpaperScreen(game);
                        game.setWallpaperScreen(wallpaperScreen);
                        game.setScreen(wallpaperScreen);
                    }
                };
            } else {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        GameScreen screen = new GameScreen(game, param, pot, flower);
                        game.setGameScreen(screen);
                        game.setScreen(screen);
                    }
                };
            }
            ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, runnable);
            stage.addActor(fade);
            updated = true;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isInitialised) {
            loadFlowerAndFont();

            if (isWallpaper) {
                mainFont = game.utility.generateFont(56, false);
                wallpaperPreparing = new Label(game.locale.get("wallpaper_preparing"), new Label.LabelStyle(mainFont, Color.FOREST));
                wallpaperPreparing.setWrap(true);
                wallpaperPreparing.setSize(FlowergotchiGame.screenWidth, 200);
                wallpaperPreparing.setAlignment(Align.center);
                wallpaperPreparing.setOrigin(Align.center);
                wallpaperPreparing.setPosition(0, 200);
                wallpaperPreparing.setVisible(false);

                stage.addActor(wallpaperPreparing);
            }
            if (loadSplash) {
                SplashScreen.loadAsset(game);
            }
            loadAsset();

            isInitialised = true;
        }

        if (!updated) {
            update();
        }

        counter += delta;
        TextureRegion currentFrame = loadanim.getKeyFrame(counter, true);

        batch.begin();
        batch.draw(currentFrame, (viewport.getWorldWidth() - currentFrame.getRegionWidth()) / 2, (viewport.getWorldHeight() - currentFrame.getRegionHeight()) / 2);
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (isWallpaper) {
            wallpaperPreparing.remove();
            mainFont.dispose();
        }

        for (int i = 1; i <= maxImage; ++i) {
            game.manager.unload("loadingscreen/flower" + Integer.toString(i) + ".png");
        }
        stage.dispose();
    }
}
