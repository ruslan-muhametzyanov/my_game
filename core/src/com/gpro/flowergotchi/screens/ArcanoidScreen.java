package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.Arcanoid.Ball;
import com.gpro.flowergotchi.gamelogic.Arcanoid.Brick;
import com.gpro.flowergotchi.gamelogic.Arcanoid.BrickDestroy;
import com.gpro.flowergotchi.gamelogic.Arcanoid.Platform;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameTimer;
import com.gpro.flowergotchi.gamelogic.Stars;
import com.gpro.flowergotchi.gamelogic.Statistic;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.gamelogic.insects.InsectActor;
import com.gpro.flowergotchi.gamelogic.insects.SmallBug;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.util.Rand;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ArcanoidScreen extends DemoScreen {

    private final static String tex2 = "arcanoid/bugpart";
    private final FlowergotchiGame game;
    private final OrthographicCamera camera;
    private final Stage stage;
    private Platform platform;
    private Ball ball;
    private Array<Brick> bricks;
    private Brick brick;
    private Texture[] parts;
    private final int partCount = 8;
    private final BitmapFont mainFont;
    private boolean startGame = false, endGame = false;
    private int blockCount;
    private final GameUI ui;
    private final Sound hit;
    private final Sound squish;
    private Stars stars;
    private boolean gameWin = false, gameLose = false, gamePaused = false;
    private GameTimer timer;

    public ArcanoidScreen(FlowergotchiGame game, GameUI ui, int count) {
        super(game);
        this.game = game;
        this.blockCount = count * 4;
        this.ui = ui;

        loadGraphics();
        camera = new OrthographicCamera();
        camera.setToOrtho(true, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        StretchViewport fitViewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, camera);
        SpriteBatch batch = new SpriteBatch();
        stage = new Stage(fitViewport, batch);
        mainFont = game.utility.getMainFont();

        Statistic.newCount = count;
        hit = game.manager.get("arcanoid/hit.ogg", Sound.class);
        squish = game.manager.get("arcanoid/squish.ogg", Sound.class);

        InitDialog();
        if (!ui.getWorld().isTutorialActive() && !FlowergotchiGame.debugMode && !game.getPurchaseManager().isNoAds()) {
            game.serviceCallback().showAdInterstitial();
        }
    }


    private void InitDialog() {
        TextureRegion backgroundTexture = new TextureRegion(game.manager.getTexture("arcanoid/fon.png"));
        backgroundTexture.flip(false, true);
        Image back = new Image(backgroundTexture);
        back.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_BackgroundBack))));
        stage.addActor(back);
        this.timer = new GameTimer();
        timer.setActive(false);

        stars = new Stars(game, ui, stage, Stars.Games.ArcanoidGame);

        if (game.getPreferences().playedArcanoidGame()) {
            ui.showOkDialog(stage, "arcanoid_message", new Runnable() {
                @Override
                public void run() {
                    timer.setActive(true);
                    game.utility.message(stage, game.manager, mainFont, game.locale.format("arcanoid_top", blockCount), null);
                    game.getPreferences().setPlayedArcanoidGame(false);
                }
            });
        } else {
            timer.setActive(true);
            game.utility.message(stage, game.manager, mainFont, game.locale.format("arcanoid_top", blockCount), null);
        }

    }

    private void startGame() {
        platform = new Platform(game.manager.getTexture("arcanoid/platform.png"));
        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Rectangle b = new Rectangle(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
                if (b.contains(x, y)) {
                    platform.setTouched(true);
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (platform.isTouched()) {
                    platform.setTouched(false);
                }
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (platform.isTouched()) {
                    platform.setX(x - platform.getWidth() / 2);
                }
            }
        });
        platform.setPosition((FlowergotchiGame.screenWidth - platform.getWidth()) / 2, 1200);
        stage.addActor(platform);

        ball = new Ball(new Vector2(-600, -600), game.manager.getTexture("arcanoid/ball.png"));
        ball.setPosition((FlowergotchiGame.screenWidth - 50) / 2, 1100);
        stage.addActor(ball);

        parts = new Texture[partCount];
        for (int i = 1; i <= partCount; ++i) {
            parts[i - 1] = game.manager.getTexture(tex2 + String.valueOf(i) + ".png");
        }

        bricks = new Array<Brick>();
        for (int i = 0; i < blockCount; i++) {
            switch (i) {
                case 0:
                case 5:
                case 10:
                case 15:
                case 20:
                case 25:
                    brick = new Brick(game.manager.getTexture("arcanoid/bug.png"), 25 * i + (int) Math.round((Math.random() * 200) - 100),
                            500 + 5 * i, 138, 80, stage.getViewport().getWorldWidth());
                    break;
                case 1:
                case 6:
                case 11:
                case 16:
                case 21:
                case 26:
                    brick = new Brick(game.manager.getTexture("arcanoid/bug.png"), 25 * i + (int) Math.round((Math.random() * 200) - 100),
                            400 + 5 * i, 138, 80, stage.getViewport().getWorldWidth());
                    break;
                case 2:
                case 7:
                case 12:
                case 17:
                case 22:
                case 27:
                    brick = new Brick(game.manager.getTexture("arcanoid/bug.png"), 25 * i + (int) Math.round((Math.random() * 200) - 100),
                            300 + 5 * i, 138, 80, stage.getViewport().getWorldWidth());
                    break;
                case 3:
                case 8:
                case 13:
                case 18:
                case 23:
                case 28:
                    brick = new Brick(game.manager.getTexture("arcanoid/bug.png"), 25 * i + (int) Math.round((Math.random() * 200) - 100),
                            200 + 5 * i, 138, 80, stage.getViewport().getWorldWidth());
                    break;
                case 4:
                case 9:
                case 14:
                case 19:
                case 24:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                    brick = new Brick(game.manager.getTexture("arcanoid/bug.png"), 25 * i + (int) Math.round((Math.random() * 200) - 100),
                            100 + 5 * i, 138, 80, stage.getViewport().getWorldWidth());
                    break;
            }
            bricks.add(brick);
            stage.addActor(brick);
        }

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
        if (gamePaused) {
            return;
        }

        timer.tick(delta);
        camera.update();
        stage.act(delta);
        stage.getActors().sort(ui.getComparator());
        stage.draw();

        if (timer.getTime() > 3) {
            if (!startGame) {
                startGame();
            }
        } else {
            return;
        }

        if (startGame && !endGame) {
            checkCollision();
            platform.updateBounds();
            ball.updateBounds();
            ball.update(delta);

            final Random random = new Random();
            Iterator<Brick> iter3 = bricks.iterator();
            while (iter3.hasNext()) {
                Brick brick = iter3.next();
                brick.update(delta);
                brick.updateBounds();

                if (brick.getBounds().overlaps(ball.getBounds())) {
                    squish.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    brick.remove();

                    for (int i = 1; i <= partCount; ++i) {
                        BrickDestroy part = new BrickDestroy(parts[i - 1], new Vector2(brick.getX() + brick.getWidth() / 2 + Rand.randFloat(-brick.getWidth() / 2, brick.getWidth() / 2),
                                brick.getY() + brick.getHeight() / 2 + random.nextFloat() - 2),
                                new Vector2(ball.getVelocity().x / Rand.randFloat(5, 10) + Rand.randFloat(0, 20), ball.getVelocity().y / Rand.randFloat(5, 10) + Rand.randFloat(0, 20)));
                        stage.addActor(part);
                    }

                    Rectangle rect = intersect(brick.getBounds(), ball.getBounds());
                    if (rect.width < rect.height) {
                        ball.getVelocity().x *= -1;
                        if (rect.x == brick.getX())
                            ball.setX(brick.getX() - ball.getWidth());
                        else
                            ball.setX(brick.getX() + brick.getWidth());
                    } else {
                        ball.getVelocity().y *= -1;
                        if (rect.y == brick.getY())
                            ball.setY(brick.getY() - ball.getHeight());
                        else
                            ball.setY(brick.getY() + brick.getHeight());
                    }
                    iter3.remove();


                }
            }
            if (bricks.size == 0) {
                gameWin();
                endGame = true;
                gameWin = true;
            }
            if (ball.getY() >= 1280) {
                gameLose();
                endGame = true;
                gameLose = true;
            }
        }

        if (stars.stop() && gameWin) {
            gameWin2();
        }
        if (stars.stop() && gameLose) {
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
        game.getGameScreen().RequestUpdate();
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

    private Rectangle intersect(Rectangle r1, Rectangle r2) {
        Rectangle intersection = new Rectangle();
        intersection.setX(Math.max(r1.x, r2.x));
        intersection.setY(Math.max(r1.y, r2.y));
        intersection.setWidth(Math.min(r1.x + r1.width, r2.x + r2.width) - intersection.x);
        intersection.setHeight(Math.min(r1.y + r1.height, r2.y + r2.height) - intersection.y);
        return intersection;
    }

    private void gameWin() {
        stars.EndGame();
        stars.gameWin(true);
        stars.result();

        ball.remove();
        platform.remove();
        for (Brick b : bricks) {
            b.remove();
        }
        game.utility.message(stage, game.manager, mainFont, game.locale.get("arcanoid_win"), null);

        List<Integer> list = new ArrayList<Integer>();
        for (Map.Entry<Integer, GameObject> it : game.getGameScreen().getWorld().gameObjectManager().getManager().entrySet()) {
            GameObject obj = it.getValue();
            if (obj instanceof SmallBug) {
                list.add(1);
                game.serviceCallback().squishBug(obj.getID());
                game.serviceCallback().clientButtonCallback(AndroidCallbackTypes.CB_Insects, list);
                game.getGameScreen().RequestUpdate();
            }
        }

        for (Actor a : ui.getStage().getActors()) {
            if (a instanceof InsectActor) {
                {
                    a.remove();
                }
            }
        }
    }

    private void gameWin2() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
            @Override
            public void run() {
                game.setState(GameState.GS_GameProcess);
                game.backToPreviousScreen();
                game.serviceCallback().statCallback(AndroidCallbackTypes.CB_Insects, Statistic.newCount, Statistic.newStar);
            }
        });
        stage.addActor(fade);
    }

    private void gameLose() {
        stars.EndGame();
        stars.gameWin(false);
        stars.result();

        ball.remove();
        platform.remove();
        for (Brick b : bricks) {
            b.remove();
        }

        Sound lose = game.manager.get("gameover.ogg", Sound.class);
        lose.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
        game.utility.message(stage, game.manager, mainFont, game.locale.get("arcanoid_lose"), null);
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

    private void loadGraphics() {
        for (int i = 1; i <= 8; i++) {
            game.manager.load("arcanoid/bugpart" + i + ".png", Texture.class);
        }
        game.manager.load("arcanoid/bug.png", Texture.class);
        game.manager.load("arcanoid/ball.png", Texture.class);
        game.manager.load("arcanoid/fon.png", Texture.class);
        game.manager.load("arcanoid/platform.png", Texture.class);
        game.manager.load("arcanoid/hit.ogg", Sound.class);
        game.manager.load("arcanoid/squish.ogg", Sound.class);
        game.manager.finishLoading();
    }

    private void unloadGraphics() {
        for (int i = 1; i <= 8; i++) {
            game.manager.unload("arcanoid/bugpart" + i + ".png");
        }
        game.manager.unload("arcanoid/bug.png");
        game.manager.unload("arcanoid/ball.png");
        game.manager.unload("arcanoid/fon.png");
        game.manager.unload("arcanoid/platform.png");
        game.manager.unload("arcanoid/hit.ogg");
        game.manager.unload("arcanoid/squish.ogg");
    }

    private void checkCollision() {
        if (platform.getBounds().overlaps(ball.getBounds()) && ball.getY() < platform.getY()) {
            ball.setY(platform.getY() - ball.getHeight());
            hit.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
            Random random = new Random();
            int i = random.nextInt(60);

            if (ball.getX() > platform.getX() + platform.getWidth() / 4 - ball.getWidth() / 2 && ball.getX() < platform.getX() + (platform.getWidth() / 4) * 3 - ball.getWidth() / 2) {
                if (ball.getVelocity().x > 0) {
                    ball.setVelocityX(-100 - i);
                } else if (ball.getVelocity().x < 0) {
                    ball.setVelocityX(100 + i);
                }
                ball.setVelocityY(-800);
            } else if (ball.getX() < platform.getX() + platform.getWidth() / 4 - ball.getWidth() / 2 || ball.getX() > platform.getX() + (platform.getWidth() / 4) * 3 - ball.getWidth() / 2) {
                if (ball.getVelocity().x > 0) {
                    ball.setVelocityX(-1000);
                } else if (ball.getVelocity().x < 0) {
                    ball.setVelocityX(1000);
                }
                ball.setVelocityY(-500);
            }
        }
    }
}



