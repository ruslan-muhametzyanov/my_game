package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.InputHandler;
import com.gpro.flowergotchi.util.Pair;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

import java.util.Map;
import java.util.TreeMap;

class MainMenuScreen extends DemoScreen {

    private final FlowergotchiGame game;
    private final SpriteBatch batch;
    private final Stage stage;
    private final StretchViewport viewport;
    private Music back;
    private boolean isInitialized = false;
    private Sound click;
    private Skin skin1;
    private BitmapFont font12;
    private Table buttonContainer;

    public MainMenuScreen(final FlowergotchiGame game) {
        super(game);
        this.game = game;

        OrthographicCamera cam = new OrthographicCamera();
        batch = new SpriteBatch();
        cam.setToOrtho(true, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        batch.setProjectionMatrix(cam.combined);
        viewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);

        stage = new Stage(viewport, batch);
        stage.addListener(new InputHandler(game, stage));

        buttonContainer = new Table();
        buttonContainer.setOrigin(Align.center);
    }

    private void init(final FlowergotchiGame game) {
        back = game.manager.get("sounds/music.ogg", Music.class);

        TextureRegion reg = new TextureRegion(game.manager.getTexture("mainmenuscreen/background.png"));
        reg.flip(false, true);
        Image background = new Image(reg);
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        skin1 = game.utility.getDefaultSkin();

        font12 = game.utility.generateFont(52, true);

        initButtons();
        isInitialized = true;
    }

    private void initButtons() {
        click = game.manager.get("click.wav", Sound.class);
        final Map<Integer, Pair<String, String>> langButtons = new TreeMap<Integer, Pair<String, String>>();
        int buttonCount = 5;

        langButtons.put(buttonCount--, new Pair<String, String>("startgame", game.locale.get("menu_startgame")));
        langButtons.put(buttonCount--, new Pair<String, String>("option", game.locale.get("menu_options")));
        langButtons.put(buttonCount--, new Pair<String, String>("shop", game.locale.get("menu_shop")));
        langButtons.put(buttonCount--, new Pair<String, String>("rate", game.locale.get("menu_rate")));
        langButtons.put(buttonCount, new Pair<String, String>("about", game.locale.get("menu_about")));

        final Map<String, ChangeListener> listeners = new TreeMap<String, ChangeListener>();
        listeners.put("startgame", new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_SelectScreen);
                        game.addScreen(new EnvironmentSelectScreen(game));
                    }
                });
                stage.addActor(fade);
                back.stop();
            }
        });

        listeners.put("option", new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_Option);
                        game.addScreen(new OptionScreen(game, false));
                    }
                });
                stage.addActor(fade);
            }
        });

        listeners.put("shop", new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_ShopScreen);
                        game.addScreen(new ShopScreen(game));
                    }
                });
                stage.addActor(fade);
            }
        });

        listeners.put("rate", new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                game.serviceCallback().showAppRates();
            }
        });

        listeners.put("about", new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_About);
                        game.addScreen(new AboutScreen(game));
                    }
                });
                stage.addActor(fade);
            }
        });

        for (Map.Entry<Integer, Pair<String, String>> b : langButtons.entrySet()) {
            TextButton.TextButtonStyle selectButtonstyle = new TextButton.TextButtonStyle();
            selectButtonstyle.down = skin1.getDrawable("defButtonPressed");
            selectButtonstyle.up = skin1.getDrawable("defButton");
            selectButtonstyle.font = font12;
            selectButtonstyle.fontColor = Color.WHITE;
            TextButton selectButton = new TextButton(b.getValue().getSecond(), selectButtonstyle);
            selectButton.getLabel().setAlignment(Align.center);
            selectButton.addListener(listeners.get(b.getValue().getFirst()));
            buttonContainer.add(selectButton);
            buttonContainer.row();
        }

        buttonContainer.setPosition((viewport.getWorldWidth()) / 2, viewport.getWorldHeight() - 450);

        stage.addActor(buttonContainer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void show() {
        if (!isInitialized) {
            init(game);
        }
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {
                game.setState(GameState.GS_Menu);
                Gdx.input.setInputProcessor(stage);
                back.setLooping(false);
                back.setVolume(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                back.play();
            }
        });
        stage.addActor(fade);
    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stu
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub


    }
}
