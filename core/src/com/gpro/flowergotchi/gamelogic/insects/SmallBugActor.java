package com.gpro.flowergotchi.gamelogic.insects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;

class SmallBugActor extends InsectActor {
    private final static String tex = "smallbug/bug.png";
    final int spriteCount = 4;
    private boolean flipped = false;

    public SmallBugActor(final Integer parent, ResourceManager manager) {
        this.parentID = parent;

        walkSheet = manager.getTexture(tex);

        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / spriteCount, walkSheet.getHeight());
        getWalkFrames = new TextureRegion[spriteCount];
        int index = 0;
        for (int j = 0; j < spriteCount; j++) {
            tmp[0][j].flip(false, true);
            getWalkFrames[index++] = tmp[0][j];
        }

        walkAnimation = new Animation(0.2f, getWalkFrames);

        stateTime = 0f;
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Bug))));
        this.setSize(walkSheet.getWidth() / spriteCount, walkSheet.getHeight());
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void flip() {
        this.flipped = !flipped;
    }

    public void addToUI(final GameUI ui, Vector2 startPoint, RepeatAction pattern) {
        this.setPosition(startPoint.x, startPoint.y);
        this.addAction(pattern);
        this.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ui.getArcanoid();
                return true;
            }
        });
        ui.getStage().addActor(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        if (isFlipped()) {
            currentFrame.flip(true, false);
        }


        batch.draw(currentFrame, this.getX(), this.getY());
        if (isFlipped()) {
            currentFrame.flip(true, false);
        }
    }
}
