package com.gpro.flowergotchi.ui;

/**
 * Created by user on 25.12.2015.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

public class Cat extends Actor {
    private static final int FRAME_COLS = 5;
    private static final int FRAME_ROWS = 1;
    private static final int FRAME_COLS2 = 4;
    private static final int FRAME_ROWS2 = 1;
    private static final float dur2 = 0.5f;
    private static final float dur1 = 0.25f;

    private final Animation BwalkAnimation, BwalkAnimation2;
    private final FlowergotchiGame game;
    private float stateTime, stateTime2;
    private final Texture catcat;
    private final Texture cat3;
    private final Texture cat4;
    private final Music cathiss;
    private boolean a = false, b = false, c = false, f = false, g = false;

    public Cat(final FlowergotchiGame game, GameUI ui) {
        this.game = game;
        loadAssets(game);
        this.catcat = game.manager.getTexture("cat/catcat.png");
        this.cat3 = game.manager.getTexture("cat/cat3.png");
        this.cat4 = game.manager.getTexture("cat/cat4.png");
        Texture cat = game.manager.getTexture("cat/cat.png");
        Texture cat2 = game.manager.getTexture("cat/cat2.png");
        this.setPosition(375, 645);
        this.setSize((float) (cat.getWidth() / 5), (float) cat.getHeight());
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Cat))));
        this.setName("cat");
        cathiss = game.manager.get("cat/cathiss.ogg", Music.class);
        cathiss.setVolume(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));

        b = true;

        TextureRegion[][] tmp = TextureRegion.split(cat, cat.getWidth() / FRAME_COLS, cat.getHeight() / FRAME_ROWS);
        TextureRegion[] getWalkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                tmp[i][j].flip(false, true);
                getWalkFrames[index++] = tmp[i][j];
            }
        }
        BwalkAnimation = new Animation(dur1, getWalkFrames);
        stateTime = 0f;

        TextureRegion[][] tmp2 = TextureRegion.split(cat2, cat2.getWidth() / FRAME_COLS2, cat2.getHeight() / FRAME_ROWS2);
        TextureRegion[] getWalkFrames2 = new TextureRegion[FRAME_COLS2 * FRAME_ROWS2];
        int index2 = 0;
        for (int i = 0; i < FRAME_ROWS2; i++) {
            for (int j = 0; j < FRAME_COLS2; j++) {
                tmp2[i][j].flip(false, true);
                getWalkFrames2[index2++] = tmp2[i][j];
            }
        }
        BwalkAnimation2 = new Animation(dur2 / 2, getWalkFrames2);
        stateTime2 = 0f;

        RunnableAction point = Actions.run(new Runnable() {
            @Override
            public void run() {
                f = true;
                b = false;
            }
        });

        RunnableAction point2 = Actions.run(new Runnable() {
            @Override
            public void run() {
                f = false;
                g = true;
            }
        });

        RunnableAction point3 = Actions.run(new Runnable() {
            @Override
            public void run() {
                g = false;
                a = true;
            }
        });


        MoveToAction moveStart = new MoveToAction();
        moveStart.setPosition(ui.getStage().getViewport().getWorldWidth(), this.getY());
        moveStart.setDuration(0.0f);
        moveStart.setInterpolation(Interpolation.linear);

        MoveToAction moveEnd = new MoveToAction();
        moveEnd.setPosition(this.getX(), this.getY());
        moveEnd.setDuration(dur2 * 5);
        moveEnd.setInterpolation(Interpolation.linear);

        this.addAction(Actions.sequence(moveStart, moveEnd, point, Actions.delay(dur1), point2, Actions.delay(dur1), point3, Actions.run(new Runnable() {
            @Override
            public void run() {
                addListener(new ClickListener() {
                    @Override
                    public void touchDragged(InputEvent event, float x, float y, int pointer) {
                        super.touchDragged(event, x, y, pointer);
                        c = true;
                        a = false;
                        cathiss.setVolume(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                        cathiss.play();
                        cathiss.setLooping(true);
                        setX(getX() + x);
                        setY(getY() + y);

                        if ((getX() < 60) || (getX() > 660)) {
                            game.serviceCallback().clientButtonCallback(AndroidCallbackTypes.CB_Cat, null);
                            game.serviceCallback().statCallback(AndroidCallbackTypes.CB_Cat, 1, 0);
                            cathiss.stop();
                            remove();
                        }
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        super.touchUp(event, x, y, pointer, button);
                        c = false;
                        a = true;
                        cathiss.stop();
                        setPosition(375, 645);
                    }
                });
            }
        })));
    }

    @Override
    public boolean remove() {
        game.manager.unload("cat/cat.png");
        game.manager.unload("cat/cat2.png");
        game.manager.unload("cat/catcat.png");
        game.manager.unload("cat/cathiss.ogg");
        game.manager.unload("cat/cat3.png");
        game.manager.unload("cat/cat4.png");
        return super.remove();
    }

    private void loadAssets(FlowergotchiGame game) {
        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.genMipMaps = false;
        param.format = Pixmap.Format.RGBA8888;

        game.manager.load("cat/cat.png", Texture.class, param);
        game.manager.load("cat/cat2.png", Texture.class, param);
        game.manager.load("cat/catcat.png", Texture.class, param);
        game.manager.load("cat/cathiss.ogg", Music.class);
        game.manager.load("cat/cat3.png", Texture.class, param);
        game.manager.load("cat/cat4.png", Texture.class, param);
        game.manager.finishLoading();
    }

    public void draw(Batch batch, float parentAlpha) {

        this.stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = this.BwalkAnimation.getKeyFrame(this.stateTime, true);

        this.stateTime2 += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame2 = this.BwalkAnimation2.getKeyFrame(this.stateTime2, true);

        if (b) {
            batch.draw(currentFrame2, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else if (a) {
            batch.draw(currentFrame, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else if (c) {
            batch.draw(this.catcat, getX() - catcat.getWidth() / 2, getY() - catcat.getHeight() / 2, this.getOriginX(), this.getOriginY(), catcat.getWidth(), catcat.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
        } else if (f) {
            batch.draw(this.cat3, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), cat3.getWidth(), cat3.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
        } else if (g) {
            batch.draw(this.cat4, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), cat4.getWidth(), cat4.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
        }

    }
}
