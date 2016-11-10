package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.util.Rand;

/**
 * Created by user on 29.12.2015.
 */
public class GravityProjectile extends Actor {
    private static final float gravity = 0.25f;

    private final Texture tex;
    private float counter;
    private final Vector2 velocity = new Vector2(Rand.randFloat(-2, 2), Rand.randFloat(-4, -2));

    public GravityProjectile(Texture tex, Vector2 position, float rotateTo, float alphaTo, float dur) {
        this.tex = tex;
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_ShovelGround))));
        this.setPosition(position.x, position.y);
        this.setSize(tex.getWidth(), tex.getHeight());
        RotateByAction rotate = new RotateByAction();
        rotate.setAmount(rotateTo);

        rotate.setDuration(dur + 1.0f);

        DelayAction delayAction = new DelayAction(dur);
        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setAlpha(alphaTo);


        alphaAction.setDuration(1.0f);
        RemoveActorAction remove = new RemoveActorAction();
        remove.setActor(this);

        SequenceAction sequence1 = Actions.sequence(delayAction, alphaAction);
        SequenceAction sequence2 = Actions.sequence(rotate, remove);
        ParallelAction parralel = new ParallelAction(sequence1, sequence2);
        this.addAction(parralel);
    }

    @Override
    public void act(float delta) {
        counter += delta;
        velocity.y += gravity * counter;

        this.setPosition(this.getX() + velocity.x, this.getY() + velocity.y);
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
