package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

import java.util.Random;

public class PurchaseFlask extends ImageButton {
    private final static Vector2 bendPoint = new Vector2(550, 200);
    private final Texture flask;
    private final GameUI UI;
    private final Random rand;
    private final FlowergotchiGame game;
    private boolean toDrop = false;
    private long timer;
    private int count = 0;
    private Label countPurchase;
    private BitmapFont font;
    private final Array<StarUp> activeStar = new Array<StarUp>();
    private final Pool<StarUp> starPool = new Pool<StarUp>() {
        @Override
        protected StarUp newObject() {
            return new StarUp();
        }
    };

    public PurchaseFlask(final FlowergotchiGame game, final GameUI ui, Texture flask) {
        super(new TextureRegionDrawable(new TextureRegion(flask)),
                new TextureRegionDrawable(new TextureRegion(flask)));
        this.flask = flask;
        this.UI = ui;
        this.game = game;
        this.setPosition(bendPoint.x, bendPoint.y);
        this.setSize(flask.getWidth(), flask.getHeight());
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_UIButtons))));
        rand = new Random();


        font = game.utility.getMainFont();
        Label.LabelStyle labelStyle2 = new Label.LabelStyle();
        labelStyle2.font = font;
        labelStyle2.fontColor = Color.BLACK;

        countPurchase = new Label("", labelStyle2);
        countPurchase.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_WaterCan))));
        countPurchase.setZIndex(0x7FFFFFFF);
        countPurchase.setAlignment(Align.center);
        countPurchase.setPosition(this.getX() + this.getWidth() - 30, this.getY() + this.getHeight() - 30);
        countPurchase.setText(String.valueOf(count));

        UI.getStage().addActor(countPurchase);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(flask, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), false, true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        countPurchase.setText(String.valueOf(count));
        if (toDrop && System.currentTimeMillis() - timer > 200) {
            timer = System.currentTimeMillis();
            StarUp starUp;
            starUp = starPool.obtain();
            starUp.init(game.manager.getTexture("purchases/star.png"), new Vector2(bendPoint.x + 45 + rand.nextInt(30), bendPoint.y));
            activeStar.add(starUp);

            UI.getStage().addActor(starUp);

            if (!starUp.alive){
                activeStar.removeValue(starUp, false);
                starPool.free(starUp);
            }
        }

    }

    public void setVisibility(boolean visible){
        this.setVisible(visible);
        countPurchase.setVisible(visible);
        toDrop = visible;
    }

    public void decrementCountPurchase (){
        this.count = count - 1;
    }

    public int getCountPurchase (){
        return count;
    }

    public void updateValue(int count)
    {
        this.count = count;
    }

    public class StarUp extends Actor implements Pool.Poolable {
        Texture tex;
        private boolean alive;

        public StarUp() {
            this.reset();
        }

        public void init(Texture tex, Vector2 pos) {
            alive = true;
            this.tex = tex;
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Flower))));
            this.setPosition(pos.x, pos.y);
            setSize(tex.getWidth(), tex.getHeight());
            AlphaAction alpha = Actions.alpha(0.0f, 4);
            RunnableAction runOff = Actions.run(new Runnable() {
                @Override
                public void run() {
                    alive = false;
                }
            });
            MoveByAction moveByAction = Actions.moveBy(rand.nextInt(2) - 1, -150, 4);
            ScaleByAction scale = Actions.scaleBy(1.5f, 1.5f, 4);

            SequenceAction sequence = Actions.sequence(moveByAction, runOff);
            ParallelAction parallelAction = new ParallelAction(sequence, scale, alpha);
            this.addAction(parallelAction);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (tex == null){
                return;
            }
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(tex, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
            batch.setColor(color.r, color.g, color.b, 1f);
        }

        @Override
        public void reset() {
            this.tex = null;
            this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Flower))));
            this.setPosition(0, 0);
            setSize(0, 0);
            this.clear();
        }
    }
}
