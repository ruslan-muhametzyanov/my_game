package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.GameTimer;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.Stars;
import com.gpro.flowergotchi.gamelogic.Statistic;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.GravityProjectile;
import com.gpro.flowergotchi.util.Rand;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

public class SpiderScreen extends DemoScreen {
    private final int splatSounds = 4;
    private final float spiderSpeed;
    private final int splatCount;

    private final FlowergotchiGame game;
    private final GameUI ui;
    private final OrthographicCamera camera;
    private final Stage stage;
    private final Sound spiderdead;
    private final Sound[] splatSound = new Sound[splatSounds];
    private Sound spiderhaha;
    private Joystick joystick;
    private GameTimer timer;
    private BitmapFont mainFont;
    private boolean startGame = false;
    private Splatter splatter;
    private int spiderCount;
    private final Stars stars;
    private boolean gameWin = false, gameLose = false, gamePaused = false;


    public SpiderScreen(final FlowergotchiGame game, GameUI ui, int spiderCount, float spiderSpeed, int splatCount) {
        super(game);
        this.game = game;
        this.ui = ui;
        this.spiderCount = spiderCount;
        this.spiderSpeed = spiderSpeed;
        this.splatCount = splatCount;


        loadGraphics();

        spiderdead = game.manager.get("spiders/goodsplat.ogg", Sound.class);
        spiderhaha = game.manager.get("spiders/spiderhaha.ogg", Sound.class);
        for (int i = 1; i <= 4; i++) {
            splatSound[i - 1] = game.manager.get("spiders/splat" + i + ".ogg", Sound.class);
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(true, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        StretchViewport stretchViewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, camera);
        SpriteBatch batch = new SpriteBatch();
        stage = new Stage(stretchViewport, batch);
        stars = new Stars(game, ui, stage, Stars.Games.SpiderGame);
        stars.spiderCount(spiderCount);

        Statistic.newCount = spiderCount;

        initGame();
        if (!ui.getWorld().isTutorialActive() && !FlowergotchiGame.debugMode && !game.getPurchaseManager().isNoAds()) {
            game.serviceCallback().showAdInterstitial();
        }
    }

    public void initGame() {
        mainFont = game.utility.getMainFont();
        TextureRegion backImage = new TextureRegion(game.manager.get("spiders/set.png", Texture.class));
        backImage.flip(false, true);
        Image back = new Image(backImage);
        back.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_BackgroundBack))));
        stage.addActor(back);

        if (game.getPreferences().playedSpiderGame()) {
            BitmapFont font12 = game.utility.getSmallFont();
            Label.LabelStyle style = new Label.LabelStyle(font12, Color.BLACK);

            Label label1 = new Label(game.locale.get("spider_message"), style);
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
                    startGame();
                    game.getPreferences().setPlayedSpiderGame(false);
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
            dialog.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_UIButtons))));
            stage.addActor(dialog);
        } else {
            startGame();
        }
    }

    private void startGame() {
        timer = new GameTimer();

        splatter = new Splatter(this, new Vector2(300, 300), splatCount);
        stage.addActor(splatter);
        joystick = new Joystick(game.manager, "spiders/joystickarea.png", "spiders/joystick.png", splatter);
        joystick.setBackPos(joystick.getBackSize().x - 150, stage.getViewport().getWorldHeight() - joystick.getBackSize().y + 50);

        stage.addActor(joystick);


        for (int i = 0; i < spiderCount; ++i) {
            stage.addActor(new Spider(new Vector2(Rand.randInt(0, (int) stage.getViewport().getWorldWidth()), Rand.randInt(0, (int) stage.getViewport().getWorldHeight()))));
        }
        startGame = true;

        for (int i = 0; i < splatter.getSplatCount(); i++) {
            Life life = new Life(splatter, i);
            life.setPosition(50 * i, 0);
            stage.addActor(life);
        }

        stars.BeginGame();
    }

    private void gameWin() {
        stars.EndGame();
        stars.gameWin(true);
        stars.result();

        joystick.addAction(Actions.alpha(0.0f, 1.0f));
        game.utility.message(stage, game.manager, mainFont, game.locale.get("spider_win"), null);
    }

    private void gameWin2() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
            @Override
            public void run() {
                game.serviceCallback().clientButtonCallback(AndroidCallbackTypes.CB_Spider, null);
                game.setState(GameState.GS_GameProcess);
                ui.removeWeb();
                game.backToPreviousScreen();
                game.serviceCallback().statCallback(AndroidCallbackTypes.CB_Spider, Statistic.newCount, Statistic.newStar);
            }
        });
        stage.addActor(fade);
    }

    private void gameLose() {
        stars.EndGame();
        stars.gameWin(false);
        stars.result();

        joystick.addAction(Actions.alpha(0.0f, 1.0f));
        Sound lose = game.manager.get("gameover.ogg", Sound.class);
        lose.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
        game.utility.message(stage, game.manager, mainFont, game.locale.get("splat_broken"), null);
    }

    private void gameLose2() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
            @Override
            public void run() {
                game.setState(GameState.GS_GameProcess);
                game.backToPreviousScreen();
            }
        });
        stage.addActor(fade);
    }

    public void loadGraphics() {
        game.manager.load("spiders/set.png", Texture.class);
        game.manager.load("spiders/joystickarea.png", Texture.class);
        game.manager.load("spiders/joystick.png", Texture.class);
        game.manager.load("spiders/spider.png", Texture.class);
        game.manager.load("spiders/spiderdead.png", Texture.class);
        game.manager.load("spiders/spmouth1.png", Texture.class);
        game.manager.load("spiders/spmouth2.png", Texture.class);
        game.manager.load("spiders/spmouth3.png", Texture.class);
        game.manager.load("spiders/splatbroken1.png", Texture.class);
        game.manager.load("spiders/splatbroken2.png", Texture.class);
        game.manager.load("spiders/life.png", Texture.class);

        game.manager.load("spiders/goodsplat.ogg", Sound.class);
        game.manager.load("spiders/break.ogg", Sound.class);
        game.manager.load("spiders/spiderhaha.ogg", Sound.class);
        for (int i = 1; i <= 12; i++) {
            game.manager.load("spiders/splatter" + i + ".png", Texture.class);
        }
        for (int i = 1; i <= 4; i++) {
            game.manager.load("spiders/splat" + i + ".ogg", Sound.class);
        }
        game.manager.finishLoading();
    }

    private void unloadGraphics() {
        game.manager.unload("spiders/set.png");
        game.manager.unload("spiders/joystickarea.png");
        game.manager.unload("spiders/joystick.png");

        game.manager.unload("spiders/goodsplat.ogg");
        game.manager.unload("spiders/break.ogg");
        game.manager.unload("spiders/spiderhaha.ogg");
        game.manager.unload("spiders/spider.png");
        game.manager.unload("spiders/spiderdead.png");
        game.manager.unload("spiders/spmouth1.png");
        game.manager.unload("spiders/spmouth2.png");
        game.manager.unload("spiders/spmouth3.png");
        game.manager.unload("spiders/splatbroken1.png");
        game.manager.unload("spiders/splatbroken2.png");
        game.manager.unload("spiders/life.png");

        for (int i = 1; i <= 4; i++) {
            game.manager.unload("spiders/splat" + i + ".ogg");
        }
        for (int i = 1; i <= 12; i++) {
            game.manager.unload("spiders/splatter" + i + ".png");
        }
        game.manager.finishLoading();
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        if (gamePaused) {
            return;
        }
        if (startGame) {
            timer.tick(delta);
        }
        stage.act();
        stage.getActors().sort(ui.getComparator());
        stage.draw();

        if (startGame) {
            timer.saveTime();
        }

        if (stars.stop() && gameWin) {
            gameWin2();
        } else if (stars.stop() && gameLose) {
            gameLose2();
        }
    }

    @Override
    public void pause() {
        gamePaused = true;
    }

    @Override
    public void resume() {
        gamePaused = false;
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        unloadGraphics();
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
        gamePaused = false;
    }

    @Override
    public void hide() {
        gamePaused = true;
    }

    class Splatter extends Actor {
        private final SpiderScreen screen;
        final Texture splatter;
        Animation splatAnim;
        private float counter = 0;
        private boolean isSplatting = false;
        private final int splatFrame = 2;
        private final Rectangle splatZone = new Rectangle(32, 29, 68, 80);
        private int splatCount = 5;

        public Splatter(SpiderScreen screen, Vector2 pos, int splatCount) {
            this.screen = screen;
            this.setPosition(pos.x, pos.y);
            this.splatCount = splatCount;
            splatter = game.manager.getTexture("spiders/splatter1.png");
            this.setSize(splatter.getWidth(), splatter.getHeight());
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Splatter))));
        }

        public int getSplatCount() {
            return splatCount;
        }

        public void setSplatCount(int splatCount) {
            this.splatCount = splatCount;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, parentAlpha);
            if (isSplatting) {
                counter += timer.getTime() - timer.loadTime();
                TextureRegion currentFrame = splatAnim.getKeyFrame(counter, false);
                if (splatAnim.isAnimationFinished(counter)) {
                    isSplatting = false;
                    batch.draw(splatter, this.getX(), this.getY(),
                            this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                            0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
                } else {
                    currentFrame.flip(false, true);
                    batch.draw(currentFrame, getX(), getY());
                    currentFrame.flip(false, true);
                }
            } else {
                batch.draw(splatter, this.getX(), this.getY(),
                        this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                        0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
            }

            batch.setColor(color.r, color.g, color.b, 1f);
        }

        public void splat() {
            if (!isSplatting && splatCount > 0) {
                counter = 0;
                isSplatting = true;
                TextureRegion splat[] = new TextureRegion[11];
                for (int i = 0; i < 11; ++i) {
                    splat[i] = new TextureRegion(game.manager.getTexture("spiders/splatter" + Integer.toString(i + 2) + ".png"));
                }
                splatSound[Rand.randInt(0, splatSounds - 1)].play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                splatAnim = new Animation(0.03f, splat);
                --splatCount;
            }
        }

        public void move(float dx, float dy) {
            if (!isSplatting) {
                this.setPosition(game.utility.clamp(this.getX() + dx, 0, stage.getViewport().getWorldWidth() - splatter.getWidth()),
                        game.utility.clamp(this.getY() + dy, 0, stage.getViewport().getWorldHeight() - splatter.getHeight()));
            }
        }

        public void breakSplatter() {
            GravityProjectile broken1 = new GravityProjectile(game.manager.getTexture("spiders/splatbroken1.png"), new Vector2(this.getX(), this.getY()), 0, 0, 3.0f);
            GravityProjectile broken2 = new GravityProjectile(game.manager.getTexture("spiders/splatbroken2.png"), new Vector2(this.getX(), this.getY()), 0, 0, 3.0f);
            stage.addActor(broken1);
            stage.addActor(broken2);
            Sound b = game.manager.get("spiders/break.ogg", Sound.class);
            b.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
            gameLose();
            gameLose = true;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (isSplatting) {
                if (splatAnim.getKeyFrameIndex(counter) == splatFrame) {
                    for (Actor a : screen.stage.getActors()) {
                        if (a instanceof Spider) {
                            Rectangle spider = new Rectangle((int) a.getX(), (int) a.getY(), (int) a.getWidth(), (int) a.getHeight());
                            Rectangle splatter = new Rectangle(splatZone);
                            splatter.x += this.getX();
                            splatter.y += this.getY();
                            if (spider.overlaps(splatter) && ((Spider) a).isVulnerable()) {
                                ((Spider) a).die();
                                break;
                            } else {
                                if (Rand.randFloat(0, 1) > 0.9) {
                                    spiderhaha.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                                }
                            }
                        }
                    }
                    if (splatCount <= 0) {
                        if (spiderCount > 0) {
                            breakSplatter();
                            remove();
                        }
                    }
                }

            }
        }
    }

    class Spider extends Actor {
        final Texture spid;
        final Texture spiddead;
        final Animation mouthAnim;
        private float counter = 0;
        private boolean isDead = false;
        private final boolean isVulnerable = true;
        private float stayTime = spiderSpeed;
        private float stayCounter = 0;

        public Spider(Vector2 pos) {
            this.setPosition(pos.x, pos.y);

            spid = game.manager.getTexture("spiders/spider.png");
            spiddead = game.manager.getTexture("spiders/spiderdead.png");
            this.setSize(spid.getWidth(), spid.getHeight());
            this.setOrigin(Align.center);
            TextureRegion mouth[] = new TextureRegion[4];
            for (int i = 0; i < 3; ++i) {
                mouth[i] = new TextureRegion(game.manager.getTexture("spiders/spmouth" + Integer.toString(i + 1) + ".png"));
            }
            mouth[3] = new TextureRegion(game.manager.getTexture("spiders/spmouth2.png"));
            mouthAnim = new Animation(0.2f, mouth);
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Spider))));

            this.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.moveBy(0, 40, 0.25f, Interpolation.pow2),
                    Actions.sequence(Actions.moveBy(0, -40, 0.25f, Interpolation.pow2))))));
        }

        public boolean isVulnerable() {
            return isVulnerable;
        }

        public void die() {
            if (!isDead) {
                isDead = true;
                spiderdead.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                this.clearActions();
                this.setOrigin(Align.center);
                this.setScale(0.5f);

                this.addAction(Actions.sequence(Actions.scaleTo(1.0f, 1.0f, 0.3f, Interpolation.pow5In),
                        Actions.scaleTo(0.0f, 0.0f, 1.0f, Interpolation.pow2Out), Actions.removeActor(this)));
                this.setSize(spiddead.getWidth(), spiddead.getHeight());
                if (--spiderCount <= 0) {
                    gameWin();
                    gameWin = true;
                }
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, parentAlpha);
            if (isDead) {
                batch.draw(spiddead, this.getX(), this.getY(),
                        this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                        0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
            } else {
                counter += timer.getTime() - timer.loadTime();
                TextureRegion currentFrame = mouthAnim.getKeyFrame(counter, true);
                batch.draw(spid, this.getX(), this.getY(),
                        this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                        0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
                batch.draw(currentFrame, getX() + 40, getY() + 40);
            }

            batch.setColor(color.r, color.g, color.b, 1f);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            stayCounter += delta;
            if (stayCounter >= stayTime && !isDead) {
                stayCounter = 0;
                Vector2 oldPos = new Vector2(this.getX(), this.getY());
                Vector2 newPos = new Vector2();
                do {
                    newPos.x = Rand.randInt(spid.getWidth(), (int) stage.getViewport().getWorldWidth() - spid.getWidth());
                    newPos.y = Rand.randInt(spid.getHeight(), (int) stage.getViewport().getWorldHeight() - spid.getHeight());
                } while (newPos.dst(oldPos) < 40 || newPos.x == oldPos.x || newPos.y == oldPos.y);
                this.clearActions();
                this.addAction(Actions.sequence(Actions.moveTo(newPos.x, newPos.y, 0.5f, Interpolation.pow4),
                        Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.moveBy(0, 40, 0.35f, Interpolation.pow2),
                                Actions.sequence(Actions.moveBy(0, -40, 0.25f, Interpolation.pow2))))));
                stayTime = Rand.randFloat(spiderSpeed - 0.2f, spiderSpeed + 0.2f);
            }
        }
    }

    class Joystick extends Actor {
        public static final float sensitivity = 0.2f;
        final Texture joyArea;
        final Texture joy;
        final float offsetMax;
        final Vector2 backPos;
        final Vector2 offset;
        boolean dragged;
        final Splatter splatter;

        public Joystick(ResourceManager manager, String joyarea, String joystick, final Splatter splatter) {
            this.splatter = splatter;
            this.backPos = new Vector2();
            this.joyArea = manager.getTexture(joyarea);
            this.setOrigin(Align.center);
            this.joy = manager.getTexture(joystick);
            this.setSize(joy.getWidth(), joy.getHeight());
            offsetMax = this.joyArea.getWidth() / 2;
            this.offset = new Vector2();
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Joystick))));


            this.addListener(new DragListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    dragged = true;
                    setTapSquareSize(0);
                    offset.set(0, 0);
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    super.touchDragged(event, x, y, pointer);
                    offset.set(offset.x + getDeltaX() - joy.getWidth() / 2, offset.y + getDeltaY() - joy.getHeight() / 2);
                    offset.limit(offsetMax);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    dragged = false;
                    splatter.splat();
                }
            });
        }

        public void setBackPos(float x, float y) {
            backPos.set(x, y);
            setPosition(x, y);
        }

        public Vector2 getBackSize() {
            return new Vector2(joyArea.getWidth(), joyArea.getHeight());
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, 0.5f * color.a);
            batch.draw(joyArea, backPos.x - joyArea.getWidth() / 2 + joy.getWidth() / 2, backPos.y - joyArea.getHeight() / 2 + joy.getHeight() / 2,
                    this.getOriginX(), this.getOriginY(), joyArea.getWidth(), joyArea.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                    0, 0, joyArea.getWidth(), joyArea.getHeight(), true, true);
            batch.setColor(color.r, color.g, color.b, 1f * color.a);
            batch.draw(joy, this.getX(), this.getY(),
                    this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                    0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
            batch.setColor(color.r, color.g, color.b, 1f);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            setPosition(backPos.x + offset.x, backPos.y + offset.y);
            splatter.move(offset.x * sensitivity, offset.y * sensitivity);
            if (!dragged) {
                if (offset.len() > 0.1) {
                    offset.setLength(offset.len() * 0.85f);
                } else {
                    offset.set(0, 0);
                }
            }
        }
    }


    class Life extends Actor {
        private final Texture life;
        private final Splatter splatter;
        private final int ID;

        public Life(Splatter splatter, int ID) {
            this.splatter = splatter;
            this.ID = ID;

            life = game.manager.getTexture("spiders/life.png");
            this.setSize(life.getWidth(), life.getHeight());
            this.setOrigin(Align.center);
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Spider))));
        }

        public int getID() {
            return this.ID;
        }

        public void die() {
            this.addAction(Actions.removeActor(this));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(life, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(),
                    getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (splatter.isSplatting && getID() == splatter.getSplatCount()) {
                this.die();
            }
        }
    }
}
