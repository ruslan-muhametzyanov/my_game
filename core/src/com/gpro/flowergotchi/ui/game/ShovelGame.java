package com.gpro.flowergotchi.ui.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.GameTimer;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.Stars;
import com.gpro.flowergotchi.gamelogic.Statistic;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.Shovel;
import com.gpro.flowergotchi.ui.Tube;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.util.Pair;
import com.gpro.flowergotchi.util.Rand;

public class ShovelGame {
    private static final int STAGE1 = 1;
    private static final int STAGE12 = 12;
    private static final int STAGE2 = 2;

    private int rotMax = 30;
    private int progressMax = 1500;
    private float changeIntervalMin = 0.5f;
    private float changeIntervalMax = 0.8f;
    private float counterThreshold = 0.3f;
    private float rotStep;
    private float rotThreshold;
    private float rotBadThreshold;

    private Shovel shovel;
    private final FlowergotchiGame game;
    private final GameUI ui;
    private final Pair<Vector2, Vector2> insectZone;
    private int stageNum;
    private IntegerGameVar progressMeter;
    private float curRotation;
    private float curRotSpeed;
    private IntegerGameVar curRotVar;
    private float targetRotation;
    private IntegerGameVar shovelRotation;
    private float nextPositionChange = 2;
    private GameTimer timer;
    private Tube progressTube;
    private Joystick joystick;
    private float counter;
    private boolean isActive = false;
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private boolean startGame;
    private Stars stars;
    private boolean gameWin = false;
    private Image backTrans;

    public ShovelGame(FlowergotchiGame game, GameWorld world, GameUI ui) {
        this.game = game;
        this.ui = ui;
        this.insectZone = world.getActivePot().getPotInsectZone();

        setDifficulty(world.getActiveFlower().getDifficulty());
        initShovelGame();
    }

    public void setDifficulty(Plant.PlantDifficulty difficulty) {
        switch (difficulty) {
            case Beginner:
                rotMax = 60;
                progressMax = 400;
                changeIntervalMin = 0.5f;
                changeIntervalMax = 0.8f;
                counterThreshold = 0.5f;
                rotStep = 0.004f;
                break;
            case VeryEasy:
                rotMax = 50;
                progressMax = 500;
                changeIntervalMin = 0.45f;
                changeIntervalMax = 0.75f;
                counterThreshold = 0.4f;
                rotStep = 0.006f;
                break;
            case Easy:
                rotMax = 45;
                progressMax = 750;
                changeIntervalMin = 0.4f;
                changeIntervalMax = 0.7f;
                counterThreshold = 0.35f;
                rotStep = 0.008f;
                break;
            case Normal:
                rotMax = 40;
                progressMax = 850;
                changeIntervalMin = 0.35f;
                changeIntervalMax = 0.65f;
                counterThreshold = 0.3f;
                rotStep = 0.01f;
                break;
            case Hard:
                rotMax = 35;
                progressMax = 100;
                changeIntervalMin = 0.3f;
                changeIntervalMax = 0.65f;
                counterThreshold = 0.2f;
                rotStep = 0.012f;
                break;
            case VeryHard:
                rotMax = 30;
                progressMax = 1200;
                changeIntervalMin = 0.3f;
                changeIntervalMax = 0.6f;
                counterThreshold = 0.15f;
                rotStep = 0.014f;
                break;
        }
        rotThreshold = (float) rotMax / 3;
        rotBadThreshold = 2 * (float) rotMax / 3;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isActive() {
        return isActive;
    }

    private void initShovelGame() {
        stars = new Stars(game, ui, ui.getStage(), Stars.Games.ShovelGame);

        shovel = new Shovel(game, ui, insectZone);
        shovel.setPosition(200, 500);
        timer = new GameTimer();
        shovel.prepareShovel(STAGE1);

        backTrans = new Image(new Texture("shovel/backTrans.png"));
        backTrans.setSize(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        backTrans.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_ShovelBack))));
        ui.getStage().addActor(backTrans);

        curRotation = 0;
        curRotSpeed = 0;
        shovelRotation = new IntegerGameVar(IntGameVariables.Var_NoType, -rotMax, rotMax, 0);
        progressMeter = new IntegerGameVar(IntGameVariables.Var_NoType, 0, progressMax, progressMax / 4);
        curRotVar = new IntegerGameVar(IntGameVariables.Var_NoType, -rotMax, rotMax, 0);

        progressTube = new Tube(progressMeter, game.manager.getTexture("shovel/tubeBar.png"), game.manager.getTexture("shovel/valueBar.png"), true, Color.YELLOW);

        progressTube.setPosition(shovel.getX(), shovel.getY() - progressTube.getHeight());

        joystick = new Joystick("shovel/shovelBack.png", "shovel/shovelPosition.png", "shovel/shovelToGo.png", curRotVar, shovelRotation);
        joystick.setBackPos(joystick.getBackSize().x / 2, ui.getStage().getViewport().getWorldHeight() - joystick.getBackSize().y);

        ui.getStage().addActor(progressTube);
        ui.getStage().addActor(joystick);
        ui.getStage().addActor(shovel);
        stageNum = STAGE1;
        shovel.prepareShovel(stageNum);
        isActive = true;
        startGame = false;
        initMessage();
        stars.BeginGame();
    }

    public void gameLoop(float delta) {
        if (gameOver) {
            if (stars.stop()) {
                gameOver();
            }
            return;
        }

        if (gamePaused) {
            return;
        }

        timer.tick(delta);
        if (stageNum > 0 && stageNum != STAGE12) {
            if (timer.getTime() >= nextPositionChange) {
                nextPositionChange += Rand.randFloat(changeIntervalMin, changeIntervalMax);
                targetRotation = Rand.randFloat(0, 1) > 0.5 ? -0.1f : 0.1f;
            }
            if (curRotSpeed < targetRotation) {
                curRotSpeed += (rotStep);
            } else if (curRotSpeed > targetRotation) {
                curRotSpeed -= (rotStep);
            }
            curRotSpeed += curRotation / (4 * rotMax);
            curRotSpeed = game.utility.clamp(curRotSpeed, -0.6f, 0.6f);
            curRotSpeed += joystick.getOffset() / 100;
            curRotSpeed = game.utility.clamp(curRotSpeed, -3, 3);

            curRotation += curRotSpeed;
            curRotation = game.utility.clamp(curRotation, -rotMax, rotMax);
            curRotVar.set((int) (curRotation));
            updateTubes();
            shovel.updateRotation(shovelRotation.get() / 2);
            if (Math.abs(curRotation) > rotBadThreshold) {
                counter += timer.getTime() - timer.loadTime();
                if (counter > counterThreshold) {
                    progressMeter.sub(2);
                }

            } else if (Math.abs(curRotation) < rotThreshold) {
                counter = 0;
                progressMeter.add(1);
            }
            if (progressMeter.get() == progressMeter.getMin()) {
                gameLose();
                return;
            } else if (progressMeter.get() == progressMeter.getMax()) {
                gameWin();
                return;
            }

            if (joystick.isAccelerometer()) {
                joystick.accelerometerCallback();
            }
        }
        timer.saveTime();
    }

    private void gameOver() {
        if (gameWin) {
            game.serviceCallback().clientButtonCallback(AndroidCallbackTypes.CB_Loosening, null);
            game.serviceCallback().statCallback(AndroidCallbackTypes.CB_Water, 0, Statistic.newStar);
        }
        destroyShovelGame();
        for (Actor b : ui.getUIButtons().values()) {
            b.setTouchable(Touchable.enabled);
        }
    }

    private void updateTubes() {

        progressTube.updateVariable(progressMeter, true);
        progressTube.setPosition(shovel.getX(), shovel.getY() - progressTube.getHeight());
    }

    private void gameWin() {
        gameWin = true;
        if (stageNum == STAGE2) {
            stars.EndGame();
            stars.gameWin(true);
            stars.result();

            shovel.addAction(Actions.sequence(Actions.alpha(0.0f, 1.0f), Actions.removeActor(shovel)));
            joystick.clearListeners();
            joystick.addAction(Actions.sequence(Actions.alpha(0.0f, 1.0f), Actions.removeActor(joystick)));
            progressTube.addAction(Actions.sequence(Actions.alpha(0.0f, 1.0f), Actions.removeActor(progressTube)));
            game.utility.message(ui.getStage(), game.manager, game.utility.getMainFont(), game.locale.get("shovel_win"), null);
            gameOver = true;
        } else {
            shovel.prepareShovel(STAGE2);
            progressMeter.set(progressMax / 2);
            nextPositionChange += 3000;
            stageNum = STAGE12;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    stageNum = STAGE2;
                }
            }, 2);
        }
    }

    private void gameLose() {
        stars.EndGame();
        stars.gameWin(false);
        stars.result();

        shovel.addAction(Actions.sequence(Actions.alpha(0.0f, 1.0f), Actions.removeActor(shovel)));
        joystick.clearListeners();
        joystick.addAction(Actions.sequence(Actions.alpha(0.0f, 1.0f), Actions.removeActor(joystick)));
        progressTube.addAction(Actions.sequence(Actions.alpha(0.0f, 1.0f), Actions.removeActor(progressTube)));
        Sound lose = game.manager.get("gameover.ogg", Sound.class);
        lose.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
        game.utility.message(ui.getStage(), game.manager, game.utility.getMainFont(), game.locale.get("shovel_lose"), null);
        gameOver = true;
    }

    private void destroyShovelGame() {
        isActive = false;
        stars = null;
        joystick = null;
        shovel = null;
        timer = null;
        progressTube = null;
        backTrans.remove();
        backTrans = null;
        ui.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.ShovelGameFinished);
        ui.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.ShovelGameFinished);
    }

    public void initMessage() {
        if (game.getPreferences().playedShovelGame()) {
            BitmapFont font12 = game.utility.getSmallFont();
            Label.LabelStyle style = new Label.LabelStyle(font12, Color.BLACK);

            Label label1 = new Label(game.locale.get("shovel_message"), style);
            label1.setWrap(true);
            label1.setAlignment(Align.left);
            style.fontColor = new Color(73.0f / 255, 36.0f / 255, 7.0f / 255, 1.0f);

            final Texture background = game.manager.getTexture("images/skin/messageBox.png");

            TextButton btnOK = new TextButton(game.locale.get("ok"), game.utility.getDefaultSkin().get("default", TextButton.TextButtonStyle.class));

            final Dialog dialog = new Dialog("", game.utility.getDefaultSkin()) {
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
                    ui.getClick().play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    dialog.hide();
                    dialog.cancel();
                    dialog.remove();
                    startGame = true;
                    game.getPreferences().setPlayedShovelGame(false);
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
            dialog.show(ui.getStage()).setPosition((ui.getStage().getViewport().getWorldWidth() - background.getWidth()) / 2, 300);

            dialog.pack();
            dialog.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_ScreenShot))));
            ui.getStage().addActor(dialog);
        } else {
            startGame = true;
        }
    }

    public boolean getStart() {
        return startGame;
    }

    public void requestPause() {
        gamePaused = true;
    }

    public void requestResume() {
        gamePaused = false;
    }

    class Joystick extends Actor {
        public static final int borderSize = 10;
        final float accelMax = 5.0f;
        final Texture joyArea;
        final Texture joy;
        final Texture position;
        final float offsetMax;
        final Vector2 backPos;
        float offset;
        boolean dragged;
        final IntegerGameVar var;
        final IntegerGameVar pos;
        final boolean accelerometer;

        public Joystick(String joyarea, String joystick, String position, IntegerGameVar pos, IntegerGameVar var) {
            this.var = var;
            this.pos = pos;
            this.backPos = new Vector2();
            this.joyArea = game.manager.getTexture(joyarea);
            this.setOrigin(Align.center);
            this.joy = game.manager.getTexture(joystick);
            this.position = game.manager.getTexture(position);
            this.setSize(joy.getWidth(), joy.getHeight());
            offsetMax = (this.joyArea.getWidth() - this.joy.getWidth() - 2 * borderSize) / 2;
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
            this.accelerometer = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

            if (!accelerometer) {
                this.addListener(new DragListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        dragged = true;
                        setTapSquareSize(0);
                        offset = 0;
                        return super.touchDown(event, x, y, pointer, button);
                    }

                    @Override
                    public void touchDragged(InputEvent event, float x, float y, int pointer) {
                        super.touchDragged(event, x, y, pointer);
                        offset = offset + getDeltaX() - joy.getWidth() / 2;
                        offset = game.utility.clamp(offset, -offsetMax, offsetMax);
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        super.touchUp(event, x, y, pointer, button);
                        dragged = false;

                    }
                });
            }
        }

        public boolean isAccelerometer() {
            return accelerometer;
        }

        public void setBackPos(float x, float y) {
            backPos.set(x, y);
            setPosition(x, y);
        }

        public Vector2 getBackSize() {
            return new Vector2(joyArea.getWidth(), joyArea.getHeight());
        }

        public void accelerometerCallback() {
            float accelX = Gdx.input.getAccelerometerX();
            accelX = game.utility.clamp(accelX, -accelMax, accelMax);
            offset = -accelX * (offsetMax / accelMax);

        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, 0.75f * color.a);
            batch.draw(joyArea, backPos.x - joyArea.getWidth() / 2 + joy.getWidth() / 2, backPos.y - joyArea.getHeight() / 2 + joy.getHeight() / 2,
                    this.getOriginX(), this.getOriginY(), joyArea.getWidth(), joyArea.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                    0, 0, joyArea.getWidth(), joyArea.getHeight(), true, true);
            batch.setColor(color.r, color.g, color.b, 1f * color.a);
            if (!isAccelerometer()) {
                batch.draw(joy, this.getX(), this.getY(),
                        this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                        0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
            }
            batch.draw(position, backPos.x + position.getWidth() / 4 + 2 * pos.get() * offsetMax / pos.getRange(), backPos.y + position.getHeight() / 4,
                    this.getOriginX(), this.getOriginY(), position.getWidth(), position.getHeight(), getScaleX(), getScaleY(), -this.getRotation(),
                    0, 0, position.getWidth(), position.getHeight(), true, true);
            batch.setColor(color.r, color.g, color.b, 1f);
        }


        public float getOffset() {
            return offset;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            setX(backPos.x + offset);
            var.set((int) (offset / offsetMax * var.getRange() / 2));
            if (!dragged) {
                if (Math.abs(offset) > 0.1) {
                    offset = offset * 0.85f;
                } else {
                    offset = 0;
                }
            }
        }
    }
}
