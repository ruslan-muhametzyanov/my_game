package com.gpro.flowergotchi.gamelogic.Arcanoid;

/**
 * Created by user on 21.12.2015.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

public class Platform extends Actor {

    private Rectangle bounds;
    private Texture texture;
    private boolean isTouched;

    public Platform(Texture texture) {
        super();
        this.texture = texture;
        this.setSize(texture.getWidth(), texture.getHeight());
        bounds = new Rectangle();
        updateBounds();
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Splatter))));
    }

    public boolean isTouched() {
        return isTouched;
    }

    public void setTouched(boolean touched) {
        isTouched = touched;
    }

    @Override
    protected void positionChanged() {
        if (this.getX() < 0) this.setX(0);
        if (this.getX() > FlowergotchiGame.screenWidth - this.getWidth())
            this.setX(FlowergotchiGame.screenWidth - this.getWidth());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
    }
}