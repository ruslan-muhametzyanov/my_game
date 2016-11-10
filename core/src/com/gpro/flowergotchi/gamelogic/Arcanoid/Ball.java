package com.gpro.flowergotchi.gamelogic.Arcanoid;

/**
 * Created by user on 21.12.2015.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

public class Ball extends Actor {

    private final Vector2 velocity;
    private final Rectangle bounds;
    private final Texture texture;


    public Ball(Vector2 vel, Texture texture) {
        super();
        this.texture = texture;
        velocity = vel.cpy();
        this.setSize(texture.getWidth(), texture.getHeight());
        bounds = new Rectangle();
        bounds.setSize(texture.getWidth(), texture.getHeight());
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Splatter))));
    }

    public void move(float delta) {
        this.setX(this.getX() + velocity.x * delta);
        this.setY(this.getY() + velocity.y * delta);
    }

    private void checkCollision() {
        if (this.getX() <= 0) {
            this.getVelocity().x *= -1;
            this.setX(0);
        } else if (this.getX() >= 720 - this.getWidth()) {
            this.getVelocity().x *= -1;
            this.setX(720 - this.getWidth());
        }

        /*if(ball.getY() >= wHeight) {
            ball.getVelocity().y *= -1;
            ball.setY(wHeight);
        }*/
        else if (this.getY() <= 0) {
            this.getVelocity().y *= -1;
            this.setY(0);
        }
    }

    public void update(float delta) {
        checkCollision();
        move(delta);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocityY(int y) {
        this.velocity.y = y;
    }

    public void setVelocityX(int x) {
        this.velocity.x = x;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void updateBounds() {
        bounds.set(getX(), getY(),
                getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
    }
}
