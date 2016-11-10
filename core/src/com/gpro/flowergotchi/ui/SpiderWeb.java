package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.screens.ScreenFade;
import com.gpro.flowergotchi.screens.SpiderScreen;

/**
 * Created by user on 18.12.2015.
 */
public class SpiderWeb extends Actor {
    Texture web;

    public SpiderWeb(final FlowergotchiGame game, final GameUI ui, Texture web) {
        this.web = web;
        this.setPosition(210, 500);
        this.setSize(web.getWidth(), web.getHeight());
        this.setOrigin(Align.center);
        this.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Spiderweb))));
        this.setScale(0.0f);
        this.setName("spiderweb");
        final SpiderWeb t = this;
        final int spiderCount;
        final int splatCount;
        final float spiderSpeed;

        Plant plant = ui.getWorld().getActiveFlower();
        switch (plant.getDifficulty()) {
            case Beginner:
                spiderCount = 1;
                splatCount = 5;
                spiderSpeed = 1.5f;
                break;
            case VeryEasy:
                spiderCount = 2;
                splatCount = 5;
                spiderSpeed = 1.4f;
                break;
            case Easy:
                spiderCount = 3;
                splatCount = 6;
                spiderSpeed = 1.2f;
                break;
            case Normal:
                spiderCount = 4;
                splatCount = 6;
                spiderSpeed = 1.1f;
                break;
            case Hard:
                spiderCount = 5;
                splatCount = 7;
                spiderSpeed = 1f;
                break;
            case VeryHard:
                spiderCount = 6;
                splatCount = 7;
                spiderSpeed = 1f;
                break;
            default:
                spiderCount = 6;
                splatCount = 7;
                spiderSpeed = 0.9f;
        }
        this.addAction(Actions.sequence(Actions.scaleTo(1.0f, 1.0f, 3.0f, Interpolation.bounceIn), Actions.run(new Runnable() {
            @Override
            public void run() {
                t.addListener(new ClickListener() {
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                            @Override
                            public void run() {
                                game.addScreen(new SpiderScreen(game, ui, spiderCount, spiderSpeed, splatCount));
                            }
                        });
                        ui.getStage().addActor(fade);

                        return true;
                    }
                });
            }
        })));

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(web, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), -this.getRotation(), 0, 0, (int) this.getWidth(), (int) this.getHeight(), true, true);
    }
}
