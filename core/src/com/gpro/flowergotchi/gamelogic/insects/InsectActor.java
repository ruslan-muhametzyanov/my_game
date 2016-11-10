package com.gpro.flowergotchi.gamelogic.insects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

import java.util.Random;

abstract public class InsectActor extends Actor {
    protected Integer parentID;
    Animation walkAnimation; // #3
    Texture walkSheet; // #4
    TextureRegion[] getWalkFrames;
    Texture[] parts;
    int partCount;
    TextureRegion currentFrame;
    float stateTime; // #8

    public Integer getParentID() {
        return parentID;
    }

    public class InsectPart extends Actor {
        final Texture tex;
        final Vector2 pos;
        final Vector2 dir;

        public InsectPart(Texture part, Vector2 pos, Vector2 direction) {
            this.tex = part;
            this.pos = pos;
            this.dir = direction;

            this.setPosition(pos.x, pos.y);
            this.setOrigin(0, 0);
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Bug))));

            Random random = new Random();

            MoveByAction move = new MoveByAction();
            move.setAmount(direction.x, direction.y);
            move.setDuration(1.0f + random.nextFloat() / 3);

            AlphaAction fade = new AlphaAction();
            fade.setAlpha(0);
            fade.setDuration(1.3f);

            ParallelAction parallel = new ParallelAction(move, fade);

            RemoveActorAction remove = new RemoveActorAction();
            remove.setActor(this);

            SequenceAction sequence = Actions.sequence(parallel, remove);

            this.addAction(sequence);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(tex, this.getX(), this.getY());
            batch.setColor(color.r, color.g, color.b, 1f);
        }
    }
}
