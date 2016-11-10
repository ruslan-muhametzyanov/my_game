package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

public class GameMenuScreen extends DemoScreen {
    private final FlowergotchiGame game;
    private StretchViewport viewport;
    private Stage stage;
    private Skin skin1;
    private Table buttonContainer;
    private BitmapFont font;
    private Sound click;

    public GameMenuScreen(FlowergotchiGame game) {
        super(game);
        this.game = game;

        setupStageViewport();
        if (!game.getGameScreen().getWorld().isTutorialActive() && !FlowergotchiGame.debugMode && !game.getPurchaseManager().isNoAds()) {
            game.serviceCallback().showAdInterstitial();
        }
    }

    private void setupStageViewport() {
        OrthographicCamera cam = new OrthographicCamera();
        SpriteBatch batch = new SpriteBatch();
        cam.setToOrtho(true, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        batch.setProjectionMatrix(cam.combined);
        viewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);
        buttonContainer = new Table();
        buttonContainer.setOrigin(Align.center);
        stage = new Stage(viewport, batch);

        TextureRegion reg = new TextureRegion(game.manager.getTexture("mainmenuscreen/background.png"));
        reg.flip(false, true);
        Image background = new Image(reg);
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        skin1 = game.utility.getDefaultSkin();

        font = game.utility.generateFont(52, true);

        initButtons();
        game.setState(GameState.GS_GamePaused);
    }

    private void initButtons() {
        click = game.manager.get("click.wav", Sound.class);
        final Map<Integer, Pair<String, String>> langButtons = new TreeMap<Integer, Pair<String, String>>();
        int buttonCount = 6;
        langButtons.put(buttonCount--, new Pair<String, String>("shop", game.locale.get("menu_shop")));
        langButtons.put(buttonCount--, new Pair<String, String>("continue", game.locale.get("menu_continue")));
        langButtons.put(buttonCount--, new Pair<String, String>("inst", game.locale.get("menu_inst")));
        langButtons.put(buttonCount--, new Pair<String, String>("option", game.locale.get("menu_options")));
        langButtons.put(buttonCount--, new Pair<String, String>("rate", game.locale.get("menu_rate")));
        langButtons.put(buttonCount, new Pair<String, String>("about", game.locale.get("menu_about")));


        final Map<String, ChangeListener> listeners = new TreeMap<String, ChangeListener>();
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
                //game.getAppStore().getPlatformResolver().requestPurchase(AppStore.purchase_fullVersion);
            }
        });


        listeners.put("continue", new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_GameProcess);
                        game.backToPreviousScreen();
                    }
                });
                stage.addActor(fade);
            }
        });

        listeners.put("inst", new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.addScreen(new ScoreboardScreen(game, "", -1));
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

        listeners.put("option", new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_Option);
                        game.addScreen(new OptionScreen(game, true));
                    }
                });
                stage.addActor(fade);

            }
        });

        for (Map.Entry<Integer, Pair<String, String>> b : langButtons.entrySet()) {
            TextButton.TextButtonStyle selectButtonstyle = new TextButton.TextButtonStyle();
            selectButtonstyle.down = skin1.getDrawable("defButtonPressed");
            selectButtonstyle.up = skin1.getDrawable("defButton");
            selectButtonstyle.font = font;
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
        Preferences.getPrefs().flush();
    }

    @Override
    public void show() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {
                Gdx.input.setInputProcessor(stage);
                stage.addListener(new InputHandler(game, stage));
            }
        });
        stage.addActor(fade);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }
}
