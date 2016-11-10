package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

public class Tube extends Actor {
    private static final float FilledXOffset = 2;
    private static final float FilledYOffset = 2;

    private final Texture filled;
    private final Texture tube;
    private boolean horisontal = false;
    private Float setHeight = 0f;
    private Float curHeight = 0f;
    private IntegerGameVar variable;
    private Color color;


    public Tube(IntegerGameVar variable, Texture tube, Texture filled, boolean horisontal, Color color) {
        this.variable = variable;
        this.tube = tube;
        this.filled = filled;

        setSize(tube.getWidth(), tube.getHeight());
        this.horisontal = horisontal;
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Tube))));
        if (color != null) {
            this.color = color;
        }
    }

    public void updateVariable(IntegerGameVar var, boolean fast) {
        this.variable = var;
        if (horisontal) {
            setHeight = (this.variable.get() + Math.abs(this.variable.getMin())) * (float) filled.getWidth() / (this.variable.getRange());
        } else {
            setHeight = (this.variable.get() + Math.abs(this.variable.getMin())) * (float) filled.getHeight() / (this.variable.getRange());
        }
        if (fast) {
            curHeight = setHeight;
        }
    }

    public void updateVariable(IntegerGameVar var) {
        updateVariable(var, false);
    }

    public IntegerGameVar getVariable() {
        return variable;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (Math.abs(curHeight - setHeight) < 2.0f) {
            curHeight = setHeight;
            return;
        }
        if (curHeight < setHeight) {
            curHeight = curHeight + Math.abs(setHeight - curHeight) / 40;
        } else if (curHeight > setHeight) {
            curHeight = curHeight - Math.abs(curHeight - setHeight) / 40;
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color orig = batch.getColor();
        if (color != null) {
            batch.setColor(color);
        } else {
            if (getVariable().isMedLevel()) {
                batch.setColor(Color.YELLOW);
            } else if (getVariable().isLowLevel()) {
                batch.setColor(Color.RED);
            } else {
                batch.setColor(Color.GREEN);
            }

        }
        if (horisontal) {
            batch.draw(filled, getX() + FilledXOffset, getY() + FilledYOffset, 0, 0, curHeight.intValue(), filled.getHeight(), this.getScaleX(), this.getScaleY(), -this.getRotation(),
                    0, 0, filled.getWidth(), filled.getHeight(), true, false);
        } else {
            batch.draw(filled, getX() + FilledXOffset, getY() + FilledYOffset + (filled.getHeight() - curHeight.intValue()), 0, 0,
                    filled.getWidth(), filled.getHeight(), this.getScaleX(), this.getScaleY(), -this.getRotation(),
                    0, (filled.getHeight() - curHeight.intValue()), filled.getWidth(), filled.getHeight(), false, true);
        }
        batch.setColor(orig);
        batch.draw(tube, getX(), getY(), 0, 0, tube.getWidth(), tube.getHeight(), this.getScaleX(), this.getScaleY(), -this.getRotation(),
                0, 0, tube.getWidth(), tube.getHeight(), false, true);

    }
}