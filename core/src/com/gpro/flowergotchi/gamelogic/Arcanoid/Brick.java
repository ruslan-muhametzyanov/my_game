package com.gpro.flowergotchi.gamelogic.Arcanoid;

/**
 * Created by user on 21.12.2015.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

public class Brick extends Actor {

    private final int spriteCount = 4;
    private float screenWidth;
    private Vector2 velocity;
    private boolean mFlip;
    private Rectangle bounds;
    private TextureRegion[] getWalkFrames;
    private Animation walkAnimation;
    private float stateTime;
    private float x;
    private float y;
    private float width;
    private float height;
    private Texture walkSheet;

    public Brick(Texture texture, float x, float y, float width, float height, float screenWidth) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.screenWidth = screenWidth;
        velocity = new Vector2();
        velocity.x = -100 + (int) Math.round((Math.random() * 150) - 150);
        bounds = new Rectangle();
        bounds.setSize(width, height);

        walkSheet = texture;

        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / spriteCount, walkSheet.getHeight());
        getWalkFrames = new TextureRegion[spriteCount];
        int index = 0;
        for (int j = 0; j < spriteCount; j++) {
            tmp[0][j].flip(true, false);
            getWalkFrames[index++] = tmp[0][j];
        }

        walkAnimation = new Animation(0.2f, getWalkFrames);
        stateTime = 0f;
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Splatter))));

    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void move(float delta) {
        this.x += velocity.x * delta;
        this.y += velocity.y * delta;
    }

    private void checkCollision() {

        if (this.x <= 0) {
            this.velocity.x *= -1;
            x = 0;
            mFlip = true;
        } else if (this.x >= screenWidth - this.width) {
            this.velocity.x *= -1;
            this.x = screenWidth - this.width;
            mFlip = false;
        }
    }

    public void update(float delta) {
        checkCollision();
        move(delta);
        getFlip();
    }

    public boolean getFlip() {
        return mFlip;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        currentFrame.flip(getFlip(), true);
        batch.draw(currentFrame, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        currentFrame.flip(getFlip(), true);
    }
}
