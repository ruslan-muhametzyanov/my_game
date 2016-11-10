package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

public class ScreenFade extends Actor {
    private Texture tex;

    public ScreenFade(FlowergotchiGame game, FadeMode fadeType, Runnable listener) {
        this(game, fadeType, listener, null);
    }

    public ScreenFade(FlowergotchiGame game, FadeMode fadeType, Runnable listener, Runnable loader) {
        tex = game.manager.getTexture("fade.png");

        this.setSize(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Fade))));

        this.setPosition(0, 0);
        AlphaAction alpha = new AlphaAction();
        if (fadeType == FadeMode.FadeOut) {
            this.setColor(1.0f, 1.0f, 1.0f, 0.0f);
            alpha.setAlpha(1.0f);
        } else if (fadeType == FadeMode.FadeIn) {
            this.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            alpha.setAlpha(0.0f);
        }

        alpha.setDuration(0.5f);
        RemoveActorAction remove = new RemoveActorAction();
        remove.setActor(this);

        SequenceAction sequenceAction = new SequenceAction(alpha);
        if (loader != null) {
            RunnableAction runnable = new RunnableAction();
            runnable.setRunnable(loader);
            sequenceAction.addAction(runnable);
        }
        if (listener != null) {
            RunnableAction run = new RunnableAction();
            run.setRunnable(listener);
            sequenceAction.addAction(run);
        }
        sequenceAction.addAction(remove);

        this.addAction(sequenceAction);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(tex, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(),
                -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
        batch.setColor(color.r, color.g, color.b, 1f);
    }

    public enum FadeMode {
        FadeOut,
        FadeIn
    }
}
