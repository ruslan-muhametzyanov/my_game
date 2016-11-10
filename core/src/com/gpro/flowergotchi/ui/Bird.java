package com.gpro.flowergotchi.ui;

/**
 * Created by user on 25.12.2015.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

public class Bird extends Actor {
    private final Animation walkAnimation1;
    private final Animation walkAnimation2;
    private final Animation walkAnimation3;
    private float stateTime;
    private boolean a, b, c;
    private boolean tap1 = false, tap2 = false;
    private Sound tap;
    private final FlowergotchiGame game;
    private float vol;

    public Bird(FlowergotchiGame game, final GameUI ui, Background back) {

        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Bird))));
        Vector2 p1 = back.getP1();
        Vector2 p2 = back.getP2();
        this.setPosition(-500, -400);
        this.setSize(500, 450);
        this.game = game;
        loadAssets(game);
        vol = 1.0f * (Preferences.getVolume() ? 1.0f : 0.0f);
        if (ui.isWallpaperMode()) {
            vol = 0;
        }

        a = true;
        b = false;
        c = false;

        MoveToAction moveToAction1 = new MoveToAction();
        moveToAction1.setPosition(p1.x, p1.y);
        moveToAction1.setDuration(2f);

        RunnableAction runnableAction1 = Actions.run(new Runnable() {
            @Override
            public void run() {
                a = false;
                b = true;
                c = false;
            }
        });

        MoveToAction moveToAction2 = new MoveToAction();
        moveToAction2.setPosition(p1.x, p1.y);
        moveToAction2.setDuration(2f);

        RunnableAction runnableAction2 = Actions.run(new Runnable() {
            @Override
            public void run() {
                a = false;
                b = false;
                c = true;
            }
        });

        MoveToAction moveToAction3 = new MoveToAction();
        moveToAction3.setPosition(p2.x, p2.y);
        moveToAction3.setDuration(2f);

        RemoveActorAction remove = new RemoveActorAction();
        remove.setActor(this);

        SequenceAction sequence = Actions.sequence(moveToAction1, runnableAction1, moveToAction2, runnableAction2, moveToAction3, Actions.run(new Runnable() {
            @Override
            public void run() {
                ui.setIsBirdFlying(false);
            }
        }), remove);

        this.addAction(sequence);

        Texture[] walkSheet1 = new Texture[8];
        TextureRegion[] walkFrames1 = new TextureRegion[8];
        for (int i = 1; i < 9; i++) {
            walkSheet1[i - 1] = game.manager.getTexture("bird/bird" + Integer.toString(i) + ".png");
            walkFrames1[i - 1] = new TextureRegion(walkSheet1[i - 1]);
            walkFrames1[i - 1].flip(false, true);
        }
        walkAnimation1 = new Animation(0.25f, walkFrames1);
        stateTime = 0f;

        Texture[] walkSheet2 = new Texture[8];
        TextureRegion[] walkFrames2 = new TextureRegion[8];
        for (int i = 9; i < 17; i++) {
            walkSheet2[i - 9] = game.manager.getTexture("bird/bird" + Integer.toString(i) + ".png");
            walkFrames2[i - 9] = new TextureRegion(walkSheet2[i - 9]);
            walkFrames2[i - 9].flip(false, true);
        }
        walkAnimation2 = new Animation(0.25f, walkFrames2);

        Texture[] walkSheet3 = new Texture[2];
        TextureRegion[] walkFrames3 = new TextureRegion[2];
        for (int i = 17; i < 19; i++) {
            walkSheet3[i - 17] = game.manager.getTexture("bird/bird" + Integer.toString(i) + ".png");
            walkFrames3[i - 17] = new TextureRegion(walkSheet3[i - 17]);
            walkFrames3[i - 17].flip(false, true);
        }
        walkAnimation3 = new Animation(0.25f, walkFrames3);

    }

    @Override
    public boolean remove() {
        for (int i = 1; i <= 18; i++) {
            game.manager.unload("bird/bird" + i + ".png");
        }
        game.manager.unload("bird/birdtap.ogg");
        return super.remove();
    }

    private void loadAssets(FlowergotchiGame game) {
        game.manager.load("bird/birdtap.ogg", Sound.class);

        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Nearest;
        param.genMipMaps = false;
        param.format = Pixmap.Format.RGBA8888;
        for (int i = 1; i <= 18; i++) {
            game.manager.load("bird/bird" + i + ".png", Texture.class, param);
        }
        game.manager.finishLoading();
        tap = game.manager.get("bird/birdtap.ogg", Sound.class);
    }

    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame1 = walkAnimation1.getKeyFrame(stateTime, true);
        TextureRegion currentFrame2 = walkAnimation2.getKeyFrame(stateTime, true);
        TextureRegion currentFrame3 = walkAnimation3.getKeyFrame(stateTime, true);

        if (a) {
            batch.draw(currentFrame1, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        if (b) {
            if (!tap1 && walkAnimation2.getKeyFrameIndex(stateTime - walkAnimation1.getAnimationDuration()) == 4) {
                tap.play(vol);
                tap1 = true;
            }
            if (!tap2 && walkAnimation2.getKeyFrameIndex(stateTime - walkAnimation1.getAnimationDuration()) == 6) {
                tap.play(vol);
                tap2 = true;
            }
            batch.draw(currentFrame2, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        if (c) {
            batch.draw(currentFrame3, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
    }
}
