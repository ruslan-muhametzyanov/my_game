package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.GameTimer;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.Stars;
import com.gpro.flowergotchi.gamelogic.Statistic;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.WateringCan;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.util.Rand;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

public class WaterScreen extends DemoScreen {

    private static final float windAccelMax = 0.05f;
    private static final float dropletInterval = 0.4f;
    private final float windMax = 4f;
    private float dropSpeed;
    private final StretchViewport fitViewport;
    private Array<Droplet> droplets = new Array<Droplet>();
    private Pool<Droplet> dropletsPool = new Pool<Droplet>() {
        @Override
        protected Droplet newObject() {
            return new Droplet();
        }
    };
    private FlowergotchiGame game;
    private Sound dropSound;
    private Music rainMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Bucket bucket;
    private Stage stage;
    private GameUI gameUI;
    private float windStrength;
    private float windAccel = windAccelMax;
    private GameTimer timer;
    private Label staymes;
    private Label countLabel;
    private BitmapFont mainFont;
    private boolean gameOver = false;
    private boolean startGame = false;
    private boolean gamePaused = false;
    private boolean gameLose = false, gameWin = false;
    private Stars stars;
    private float windVel = 0.04f;
    private int dropsRequired = 1;
    private float nextDroplet = 3;
    private long nextWindChange = 5;
    private float badDropChance = 0.4f;

    public WaterScreen(FlowergotchiGame game, GameUI gameUI) {
        super(game);
        this.game = game;
        this.gameUI = gameUI;

        setDifficulty(gameUI.getWorld().getActiveFlower());

        windStrength = 0;

        loadGraphics();
        camera = new OrthographicCamera();
        camera.setToOrtho(true, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        fitViewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, camera);
        batch = new SpriteBatch();
        stage = new Stage(fitViewport, batch);

        mainFont = game.utility.getMainFont();

        Statistic.newCount = dropsRequired;
        stars = new Stars(game, gameUI, stage, Stars.Games.WaterGame);
        stars.waterCount(dropsRequired, badDropChance);

        initWaterGame();
        if (!gameUI.getWorld().isTutorialActive() && !FlowergotchiGame.debugMode && !game.getPurchaseManager().isNoAds()) {
            game.serviceCallback().showAdInterstitial();
        }
    }

    private void setDifficulty(Plant plant) {
        switch (plant.getDifficulty()) {
            case Beginner:
                dropsRequired = 30;
                badDropChance = 0.0f;
                dropSpeed = 2.5f;
                break;
            case VeryEasy:
                dropsRequired = 45;
                badDropChance = 0.1f;
                dropSpeed = 2.4f;
                break;
            case Easy:
                dropsRequired = 75;
                badDropChance = 0.15f;
                dropSpeed = 2.3f;
                break;
            case Normal:
                dropsRequired = 100;
                badDropChance = 0.25f;
                dropSpeed = 2.2f;
                break;
            case Hard:
                dropsRequired = 120;
                badDropChance = 0.3f;
                dropSpeed = 2f;
                break;
            case VeryHard:
                dropsRequired = 150;
                badDropChance = 0.4f;
                dropSpeed = 1.8f;
                break;
            default:
                dropsRequired = 180;
                badDropChance = 0.4f;
                dropSpeed = 3.5f;
        }
    }

    private void initWaterGame() {
        TextureRegion fonImage = new TextureRegion(game.manager.getTexture("igradrop/backWater.png"));
        fonImage.flip(false, true);
        Image back = new Image(fonImage);
        back.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_BackgroundBack))));
        stage.addActor(back);

        if (game.getPreferences().playedWaterGame()) {
            BitmapFont font12 = game.utility.getSmallFont();
            Label.LabelStyle style = new Label.LabelStyle(font12, Color.BLACK);

            Label label1 = new Label(game.locale.get("water_message"), style);
            label1.setWrap(true);
            label1.setAlignment(Align.left);
            style.fontColor = new Color(73.0f / 255, 36.0f / 255, 7.0f / 255, 1.0f);


            final Texture background = game.manager.getTexture("images/skin/messageBox.png");

            TextButton btnOK = new TextButton(game.locale.get("ok"), game.utility.getDefaultSkin().get("default", TextButton.TextButtonStyle.class));

            final Dialog dialog = new Dialog("", game.utility.getDefaultSkin(), "default") {
                @Override
                public float getPrefWidth() {
                    return background.getWidth();
                }

                @Override
                public float getPrefHeight() {
                    return background.getHeight();
                }
            };
            dialog.setModal(true);
            dialog.setMovable(false);
            dialog.setResizable(false);

            btnOK.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameUI.getClick().play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    dialog.cancel();
                    dialog.hide();
                    dialog.remove();
                    startGame();
                    game.getPreferences().setPlayedWaterGame(false);
                }
            });

            TextureRegion myTex = new TextureRegion(background);
            myTex.flip(false, true);
            myTex.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            TextureRegionDrawable drawable = new TextureRegionDrawable(myTex);
            dialog.setBackground(drawable);

            dialog.row().colspan(1).center().size(background.getWidth() - 60f, 400f);
            dialog.add(label1).expand();
            dialog.row().colspan(1).size(background.getWidth() - 60f, 100f);
            dialog.button(btnOK);
            dialog.align(Align.center);
            dialog.show(stage).setPosition((stage.getViewport().getWorldWidth() - background.getWidth()) / 2, 300);

            dialog.pack();
            dialog.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_ScreenShot))));
            stage.addActor(dialog);
        } else {
            startGame();
        }
    }

    private void startGame() {
        this.timer = new GameTimer();

        windVel = 0.0f;
        bucket = new Bucket(game.manager, "igradrop/glass");
        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Rectangle b = new Rectangle(bucket.getX(), bucket.getY(), bucket.getWidth(), bucket.getHeight());
                if (b.contains(x, y)) {
                    bucket.setTouched(true);
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (bucket.isTouched()) {
                    bucket.setTouched(false);
                }
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (bucket.isTouched()) {
                    bucket.setX(x - bucket.getWidth() / 2);
                }

            }
        });
        bucket.setPosition(stage.getViewport().getWorldWidth() / 2, 1000);
        stage.addActor(bucket);

        Label.LabelStyle labelStyle = new Label.LabelStyle(mainFont, Color.BLACK);
        staymes = new Label(game.locale.get("water_remain"), labelStyle);
        staymes.setX((stage.getViewport().getWorldWidth() - staymes.getWidth()) / 2);
        staymes.setY(56);
        staymes.setAlignment(Align.center);
        staymes.setColor(staymes.getColor().r, staymes.getColor().g, staymes.getColor().b, 0.0f);
        staymes.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Splatter))));
        stage.addActor(staymes);
        staymes.addAction(Actions.sequence(Actions.delay(3.0f), Actions.alpha(1.0f, 0.4f)));
        countLabel = new Label(String.valueOf(dropsRequired - bucket.getDrops()), labelStyle);
        countLabel.setX((stage.getViewport().getWorldWidth() - countLabel.getWidth()) / 2 + 20);
        countLabel.setY(60);
        countLabel.setAlignment(Align.center);
        countLabel.setColor(countLabel.getColor().r, countLabel.getColor().g, countLabel.getColor().b, 0.0f);
        countLabel.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Splatter))));
        stage.addActor(countLabel);
        countLabel.addAction(Actions.sequence(Actions.delay(3.0f), Actions.alpha(1.0f, 0.4f)));

        game.utility.message(stage, game.manager, mainFont, game.locale.get("water_collect") + " " + String.valueOf(dropsRequired) + " " + game.locale.get("water_drop"), null);

        dropSound = game.manager.get("igradrop/drop.wav", Sound.class);
        rainMusic = game.manager.get("igradrop/rain.mp3", Music.class);
        rainMusic.setLooping(true);
        rainMusic.setVolume(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
        rainMusic.play();
        startGame = true;

        stars.BeginGame();
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.f, 0.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        if (!gamePaused) {
            stage.act(delta);
            if (startGame) {
                timer.tick(delta);
                if (timer.getTime() > nextDroplet && !gameOver) {
                    final Droplet droplet = dropletsPool.obtain();
                    if (Rand.randFloat(0, 1) > badDropChance) {
                        droplet.init(game.manager.getTexture("igradrop/droplet.png"), false);
                    } else {
                        droplet.init(game.manager.getTexture("igradrop/baddroplet.png"), true);
                    }

                    droplets.add(droplet);
                    droplet.setPosition(Rand.randInt(0, (int) (stage.getViewport().getWorldWidth() - droplet.getWidth())), Rand.randFloat(50f, 60f));
                    droplet.setColor(droplet.getColor().r, droplet.getColor().g, droplet.getColor().b, 0.0f);
                    droplet.addAction(Actions.parallel(Actions.alpha(1.0f, 0.4f, Interpolation.pow3In),
                            Actions.sequence(Actions.moveBy(0, stage.getViewport().getWorldHeight() + 100, Rand.randFloat(dropSpeed - 0.3f, dropSpeed + 0.3f), Interpolation.pow2), Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    droplets.removeValue(droplet, false);
                                    dropletsPool.free(droplet);
                                }
                            }))));
                    stage.addActor(droplet);
                    nextDroplet = timer.getTime() + Rand.randFloat(dropletInterval - 0.1f, dropletInterval + 0.1f);
                }

                bucket.updateBounds();
                if (bucket.getDrops() < dropsRequired && !gameOver) {
                    countLabel.setText(String.valueOf(dropsRequired - bucket.getDrops()));
                    for (Droplet d : droplets) {
                        d.updateBounds();
                        if (d.getBounds().overlaps(bucket.getBounds())) {
                            dropSound.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                            if (d.isBad()) {
                                gameLose();
                                gameOver = true;
                                gameLose = true;
                            } else {
                                bucket.setDrops(bucket.getDrops() + 1);
                            }
                            droplets.removeValue(d, false);
                            dropletsPool.free(d);

                            if (bucket.getDrops() == dropsRequired) {
                                gameWin();
                                gameOver = true;
                                gameWin = true;
                                return;
                            }
                        }
                    }
                }

                if (timer.getTime() > nextWindChange) {
                    windStrength = Rand.randFloat(-windMax, windMax);

                    windAccel = windStrength > 0 ? windAccelMax : -windAccelMax;

                    nextWindChange += Rand.randFloat(1.0f, 2.0f);
                }
                windVel = game.utility.clamp(windVel + windAccel, windStrength > 0 ? -Math.abs(windMax) : -Math.abs(windStrength),
                        windStrength > 0 ? Math.abs(windStrength) : Math.abs(windMax));
            }
        }

        if (stars.stop() && gameWin) {
            gameWin2();
        }
        if (stars.stop() && gameLose) {
            gameLose2();
        }

        stage.getActors().sort(gameUI.getComparator());
        stage.draw();
    }

    @Override
    public void show() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {
                Gdx.input.setInputProcessor(stage);
            }
        });
        stage.addActor(fade);
    }

    private void loadGraphics() {
        for (int i = 0; i <= 10; i++) {
            game.manager.load("igradrop/glass" + i + ".png", Texture.class);
        }
        game.manager.load("igradrop/point.png", Texture.class);
        game.manager.load("igradrop/droplet.png", Texture.class);
        game.manager.load("igradrop/baddroplet.png", Texture.class);
        game.manager.load("igradrop/backWater.png", Texture.class);
        game.manager.load("igradrop/drop.wav", Sound.class);
        game.manager.load("igradrop/rain.mp3", Music.class);
        game.manager.finishLoading();
    }

    private void unloadGraphics() {
        for (int i = 0; i <= 10; i++) {
            game.manager.unload("igradrop/glass" + i + ".png");
        }
        game.manager.unload("igradrop/point.png");
        game.manager.unload("igradrop/droplet.png");
        game.manager.unload("igradrop/baddroplet.png");
        game.manager.unload("igradrop/backWater.png");
        game.manager.unload("igradrop/drop.wav");
        game.manager.unload("igradrop/rain.mp3");
    }

    private void gameWin() {

        stars.EndGame();
        stars.gameWin(true);
        stars.result();

        rainMusic.stop();
        staymes.addAction(Actions.alpha(0.0f, 1.0f));
        countLabel.addAction(Actions.alpha(0.0f, 1.0f));
        bucket.addAction(Actions.alpha(0.0f, 1.0f));
        game.utility.message(stage, game.manager, mainFont, game.locale.get("water_win"), new Runnable() {
            @Override
            public void run() {
                for (Droplet d : droplets) {
                    d.remove();
                }
            }
        });
    }

    private void gameWin2() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
            @Override
            public void run() {
                game.serviceCallback().clientButtonCallback(AndroidCallbackTypes.CB_Water, null);
                gameUI.getStage().addActor(new WateringCan(game, gameUI, game.getGameScreen().getWorld().getActivePot(), game.manager.getTexture("igradrop/can.png")));
                gameUI.setWaterAnimationRunning(true);
                game.setState(GameState.GS_GameProcess);
                game.backToPreviousScreen();
                game.serviceCallback().statCallback(AndroidCallbackTypes.CB_Water, Statistic.newCount, Statistic.newStar);
            }
        });
        stage.addActor(fade);
        gameWin = false;
    }

    private void gameLose() {
        stars.EndGame();
        stars.gameWin(false);
        stars.result();

        rainMusic.stop();
        staymes.addAction(Actions.alpha(0.0f, 1.0f));
        countLabel.addAction(Actions.alpha(0.0f, 1.0f));
        bucket.addAction(Actions.alpha(0.0f, 1.0f));

        Sound lose = game.manager.get("gameover.ogg", Sound.class);
        lose.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
        game.utility.message(stage, game.manager, mainFont, game.locale.get("water_lose"), new Runnable() {
            @Override
            public void run() {
                for (Droplet d : droplets) {
                    d.remove();
                }
            }
        });
    }

    private void gameLose2() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
            @Override
            public void run() {
                game.setState(GameState.GS_GameProcess);
                gameUI.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.WaterGameFinished);
                gameUI.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.WaterGameFinished);
                game.backToPreviousScreen();
            }
        });
        stage.addActor(fade);
        gameLose = false;
    }

    @Override
    public void pause() {
        Gdx.app.log("WaterScreen", "paused");
        gamePaused = true;
    }

    @Override
    public void resume() {
        Gdx.app.log("WaterScreen", "resumed");
        gamePaused = false;
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        unloadGraphics();
    }

    @Override
    public void hide() {

    }

    class Droplet extends Actor implements Pool.Poolable {
        boolean isBad;
        Rectangle bounds;
        Texture tex;
        float velX;

        public Droplet() {
            this.reset();
        }

        public void init(Texture tex, boolean isBad) {
            this.tex = tex;
            this.isBad = isBad;
            bounds = new Rectangle();
            this.setSize(tex.getWidth(), tex.getHeight());
            this.setOrigin(12, 10);
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Joystick))));
            updateBounds();
        }

        public boolean isBad() {
            return isBad;
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public void updateBounds() {
            bounds.set(getX(), getY() + getHeight() - 2,
                    getWidth(), 2);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            velX = game.utility.clamp(velX + windAccel, -Math.abs(windMax), Math.abs(windMax));
            this.setX(this.getX() + velX);
            if (getX() >= getStage().getViewport().getWorldWidth()) {
                this.setX(this.getX() - getStage().getViewport().getWorldWidth());
            }
            if (getX() < 0) {
                this.setX(this.getX() + getStage().getViewport().getWorldWidth());

            }
            float maxRot = Math.abs(windStrength) / windMax * 30.0f;
            if (this.getRotation() > maxRot) {
                this.setRotation(this.getRotation() - Math.abs(velX) / 10);
            } else if (this.getRotation() < -maxRot) {
                this.setRotation(this.getRotation() + Math.abs(velX) / 10);
            } else {
                this.setRotation(game.utility.clamp(this.getRotation() + velX / 10, -maxRot, maxRot));
            }

        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (tex == null) {
                return;
            }
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(tex, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
            batch.setColor(color.r, color.g, color.b, 1f);
        }

        @Override
        public void reset() {
            this.tex = null;
            isBad = false;
            velX = 0;
            this.setSize(0, 0);
            this.setOrigin(0, 0);
            bounds = null;
            this.clear();
        }
    }

    class Bucket extends Actor {
        public final int levels = 10;
        private int currentLevel = 0;
        private int dropsCollected = 0;
        private Rectangle bounds;
        private String tex;
        private boolean isTouched = false;

        public Bucket(ResourceManager manager, String tex) {
            this.tex = tex;
            Texture img = manager.getTexture(tex + "0.png");
            this.setSize(img.getWidth(), img.getHeight());
            bounds = new Rectangle();
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Spider))));
            updateBounds();
        }

        public boolean isTouched() {
            return isTouched;
        }

        public void setTouched(boolean touched) {
            isTouched = touched;
        }

        public int getDrops() {
            return dropsCollected;
        }

        public void setDrops(int currentLevel) {
            this.dropsCollected = currentLevel;
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public void updateBounds() {
            bounds.set(getX() + 50, getY(),
                    getWidth() - 50, 50);
        }


        @Override
        protected void positionChanged() {
            if (bucket.getX() < 0) bucket.setX(0);
            if (bucket.getX() > FlowergotchiGame.screenWidth - bucket.getWidth())
                bucket.setX(FlowergotchiGame.screenWidth - bucket.getWidth());
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            currentLevel = dropsCollected * levels / dropsRequired;
            Texture img = game.manager.getTexture(tex + String.valueOf(currentLevel) + ".png");
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(img, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
            batch.setColor(color.r, color.g, color.b, 1f);
        }
    }

}

