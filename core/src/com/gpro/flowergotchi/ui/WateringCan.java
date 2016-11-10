package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.flowerlogic.Pot;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.util.Pair;
import com.gpro.flowergotchi.util.Rand;

import java.util.Random;

public class WateringCan extends Actor {
    private final static Vector2 startPoint = new Vector2(768, 400);
    private final static Vector2 bendPoint = new Vector2(400, 200);
    private final Texture can;
    private final GameUI UI;
    private final Random rand;
    private final FlowergotchiGame game;
    private boolean toDrop = false;
    private Pair<Vector2, Vector2> insectZone;
    private Sound watering;

    public WateringCan(final FlowergotchiGame game, GameUI ui, Pot pot, Texture can) {
        this.can = can;
        this.UI = ui;
        this.game = game;
        this.setPosition(startPoint.x, startPoint.y);
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_WaterCan))));
        this.insectZone = pot.getPotInsectZone();
        this.watering = UI.getGame().manager.get("igradrop/watering.ogg", Sound.class);
        rand = new Random();

        MoveToAction move = new MoveToAction();
        move.setPosition(bendPoint.x, bendPoint.y);
        move.setDuration(3);
        move.setInterpolation(Interpolation.fade);

        RunnableAction dropStart = new RunnableAction();
        dropStart.setRunnable(new Runnable() {
            @Override
            public void run() {
                toDrop = true;
                setOrigin(30, 58);
                watering.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
            }
        });
        RotateByAction rotate = new RotateByAction();
        rotate.setAmount(88f);
        rotate.setInterpolation(Interpolation.fade);
        rotate.setDuration(2);
        RunnableAction dropEnd = new RunnableAction();
        dropEnd.setRunnable(new Runnable() {
            @Override
            public void run() {
                toDrop = false;
            }
        });

        MoveToAction move2 = new MoveToAction();
        move2.setPosition(startPoint.x, startPoint.y);
        move2.setDuration(3);
        move2.setInterpolation(Interpolation.swingOut);
        RemoveActorAction remove = new RemoveActorAction();
        remove.setActor(this);
        RunnableAction complete = new RunnableAction();
        complete.setRunnable(new Runnable() {
            @Override
            public void run() {
                UI.getSmileQueue().addSmile("bubble/smile.png", "smile");
                UI.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.WaterGameFinished);
                UI.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.WaterGameFinished);
                UI.setWaterAnimationRunning(false);
            }
        });

        setSize(can.getWidth(), can.getHeight());
        SequenceAction sequence = Actions.sequence(move, dropStart, rotate, dropEnd, move2, complete, remove);
        this.addAction(sequence);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(can, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (toDrop) {
            for (int i = 0; i < 2; ++i) {
                WaterDrop drop = new WaterDrop(this, game.manager.getTexture("igradrop/drop.png"), new Vector2(bendPoint.x + 50 + rand.nextInt(20) - 40, bendPoint.y + 55 + rand.nextInt(5) - 10),
                        new Vector2(Rand.randInt((int) insectZone.getFirst().x, (int) insectZone.getSecond().x), Rand.randInt((int) insectZone.getFirst().y, (int) insectZone.getSecond().y)));

                UI.getStage().addActor(drop);
            }
        }
    }

    public class WaterDrop extends Actor {
        final Texture tex;
        private float counter, duration;
        private Vector2 destPos, startPos;
        private boolean end = false;


        public WaterDrop(WateringCan can, Texture tex, Vector2 pos, Vector2 destPos) {
            this.tex = tex;
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Flower))));
            this.setPosition(pos.x, pos.y);
            this.duration = 1f + rand.nextFloat() / 2;
            this.startPos = pos;
            this.destPos = destPos;
            setSize(tex.getWidth(), tex.getHeight());
        }

        @Override
        public void act(float delta) {
            counter += delta;
            float percent = counter / duration;
            if (!end) {
                if (percent >= 1) {
                    end = true;
                    AlphaAction alpha = new AlphaAction();
                    alpha.setAlpha(0.0f);
                    alpha.setDuration(0.4f);
                    RemoveActorAction remove = new RemoveActorAction();
                    remove.setActor(this);
                    MoveByAction moveByAction = new MoveByAction();
                    float move = Rand.randFloat(20, 50);
                    moveByAction.setAmount(0, move);
                    moveByAction.setDuration(move / 100);

                    SequenceAction sequence = Actions.sequence(moveByAction, remove);
                    ParallelAction parallelAction = new ParallelAction(sequence, alpha);
                    this.addAction(parallelAction);
                    return;
                }
                this.setPosition(startPos.x + (destPos.x - startPos.x) * percent,
                        startPos.y + (destPos.y - startPos.y) * percent * percent);
            }

            super.act(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(tex, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
            batch.setColor(color.r, color.g, color.b, 1f);
        }
    }
}
