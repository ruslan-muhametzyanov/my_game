package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Statistics {
    private final Stage stage;
    private FlowergotchiGame game;
    private GameUI gameUI;
    private Image clock, indic, cursor, blur;
    private Label health, hours, plantName, plantHours, statWater, statInsect, statSpider, statStar, statCat, score;
    private Image statWaterImg, statInsectImg, statSpiderImg, statStarImg, statCatImg;
    private List<Pair<Tube, Image>> ColorTubes;
    private ImageButton scrollleft, scrollright;
    private int scrollNum = 0;
    private TextButton closeButton;
    private boolean end = false;
    private BitmapFont font;
    private BitmapFont smallFont;
    private Sound click;


    public Statistics(FlowergotchiGame game, final GameUI gameUI, GameWorld world) {
        this.game = game;
        this.gameUI = gameUI;
        this.stage = gameUI.getStage();
        loadAssets(game);
        initStatistic();
        initTubes(world);
    }

    private void loadAssets(FlowergotchiGame game) {
        click = game.manager.get("click.wav", Sound.class);
        game.manager.getTexture("statistics/tubeBar.png");
        game.manager.getTexture("statistics/tubeBarStat.png");

        game.manager.getTexture("statistics/looseningBar.png");
        game.manager.getTexture("statistics/insectBar.png");
        game.manager.getTexture("statistics/lightBar.png");
        game.manager.getTexture("statistics/waterBar.png");

        game.manager.getTexture("statistics/looseningBarStat.png");
        game.manager.getTexture("statistics/insectBarStat.png");
        game.manager.getTexture("statistics/lightBarStat.png");
        game.manager.getTexture("statistics/waterBarStat.png");

        game.manager.getTexture("statistics/waterIcon.png");
        game.manager.getTexture("statistics/looseningIcon.png");
        game.manager.getTexture("statistics/insectIcon.png");
        game.manager.getTexture("statistics/lightIcon.png");
        game.manager.getTexture("statistics/percentIcon.png");
    }

    public void initStatistic() {
        Stage stage = gameUI.getStage();
        font = game.utility.getMainFont();
        smallFont = game.utility.getSmallFont();

        Skin skin1 = game.utility.getDefaultSkin();

        closeButton = new TextButton(game.locale.get("menu_exit"), skin1.get("defButton", TextButton.TextButtonStyle.class));
        closeButton.setPosition((stage.getViewport().getWorldWidth() - closeButton.getWidth()) / 2, stage.getViewport().getWorldHeight() - 190);
        closeButton.setVisible(false);
        closeButton.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(closeButton);

        scrollleft = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("selectscreens/menu_left.png"))),
                new TextureRegionDrawable(new TextureRegion(new Texture("selectscreens/menu_left_pressed.png"))));
        scrollright = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("selectscreens/menu_right.png"))),
                new TextureRegionDrawable(new TextureRegion(new Texture("selectscreens/menu_right_pressed.png"))));
        scrollleft.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        scrollleft.setPosition(17, (stage.getViewport().getWorldHeight() - scrollleft.getHeight()) / 2);
        scrollleft.setVisible(false);
        stage.addActor(scrollleft);

        scrollright.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        scrollright.setPosition(630, (stage.getViewport().getWorldHeight() - scrollright.getHeight()) / 2);
        scrollright.setVisible(false);
        stage.addActor(scrollright);

        blur = new Image(game.manager.getTexture("blur.png"));
        blur.setVisible(false);
        blur.setZIndex(0);
        blur.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_blur))));
        stage.addActor(blur);

        ColorTubes = new ArrayList<Pair<Tube, Image>>();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));

                scrollright.setVisible(false);
                scrollleft.setVisible(false);
                closeButton.setVisible(false);

                if (scrollNum == 0) {
                    hideFirstStatistics();
                    end = true;
                } else if (scrollNum == 1) {
                    hideSecondStatistic();
                    end = true;
                }
            }
        });

        scrollleft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));

                closeButton.setTouchable(Touchable.disabled);
                scrollright.setTouchable(Touchable.disabled);
                scrollleft.setTouchable(Touchable.disabled);

                --scrollNum;
                if (scrollNum == 0) {
                    scrollleft.setVisible(false);
                }
                scrollright.setVisible(true);
                hideSecondStatistic();
                firstStatistic();
            }
        });

        scrollright.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));

                closeButton.setTouchable(Touchable.disabled);
                scrollright.setTouchable(Touchable.disabled);
                scrollleft.setTouchable(Touchable.disabled);

                ++scrollNum;
                if (scrollNum == 1) {
                    scrollright.setVisible(false);
                }
                scrollleft.setVisible(true);
                hideFirstStatistics();
                secondStatistic();
            }
        });
    }

    private void initTubes(GameWorld world) {
        final int tubesCount = 4;
        final Plant plant = (Plant) world.gameObjectManager().findFirstObject(Plant.class);
        Pair<Pair<Vector2, String>, IntegerGameVar>[] tubeData = new Pair[tubesCount];
        tubeData[0] = new Pair<Pair<Vector2, String>, IntegerGameVar>(new Pair<Vector2, String>(new Vector2(270, 580), "water"), plant.getIntVar(IntGameVariables.Var_Water));
        tubeData[1] = new Pair<Pair<Vector2, String>, IntegerGameVar>(new Pair<Vector2, String>(new Vector2(270, 460), "light"), plant.getIntVar(IntGameVariables.Var_Light));
        tubeData[2] = new Pair<Pair<Vector2, String>, IntegerGameVar>(new Pair<Vector2, String>(new Vector2(270, 340), "insect"), plant.getIntVar(IntGameVariables.Var_Insects));
        tubeData[3] = new Pair<Pair<Vector2, String>, IntegerGameVar>(new Pair<Vector2, String>(new Vector2(270, 220), "loosening"), plant.getIntVar(IntGameVariables.Var_Loosening));
        //tubeData[4] = new Pair<Pair<Vector2, String>, IntegerGameVar>(new Pair<Vector2, String>(new Vector2(270, 700), "percent"), plant.getIntVar(IntGameVariables.Var_NextStage));

        for (int i = 0; i < tubesCount; ++i) {
            Tube tube = new Tube(tubeData[i].getSecond(),
                    game.manager.getTexture("statistics/tubeBarStat.png"),
                    game.manager.getTexture("statistics/lightBarStat.png"),
                    true, i > 3 ? Color.YELLOW : null);
            tube.setPosition(tubeData[i].getFirst().getFirst().x, stage.getViewport().getWorldHeight() - tube.getHeight() - tubeData[i].getFirst().getFirst().y);
            tube.setVisible(false);
            tube.setOrigin(Align.center);
            tube.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
            stage.addActor(tube);
            TextureRegion reg = new TextureRegion(game.manager.getTexture("statistics/" + tubeData[i].getFirst().getSecond() + "Icon.png"));
            reg.flip(false, true);
            Image icon = new Image(reg);
            icon.setAlign(Align.topLeft);
            icon.setPosition(tubeData[i].getFirst().getFirst().x - 150, stage.getViewport().getWorldHeight() - icon.getHeight() - tubeData[i].getFirst().getFirst().y);
            icon.setVisible(false);
            icon.setOrigin(Align.center);
            icon.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
            stage.addActor(icon);
            ColorTubes.add(new Pair<Tube, Image>(tube, icon));
        }

        Label.LabelStyle labelStyle2 = new Label.LabelStyle();
        labelStyle2.font = font;
        Label.LabelStyle labelStyle3 = new Label.LabelStyle();
        labelStyle3.font = smallFont;

        TextureRegion reg = new TextureRegion(game.manager.getTexture("statistics/indic.png"));
        reg.flip(false, true);
        indic = new Image(new TextureRegionDrawable(reg));
        indic.setPosition((stage.getViewport().getWorldWidth() - indic.getWidth()) / 2, stage.getViewport().getWorldHeight() - indic.getHeight() - 800);
        indic.setVisible(false);
        indic.setOrigin(Align.center);
        indic.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(indic);

        reg = new TextureRegion(game.manager.getTexture("statistics/clock.png"));
        reg.flip(false, true);
        clock = new Image(reg);
        clock.setPosition((stage.getViewport().getWorldWidth() - clock.getWidth()) / 2, stage.getViewport().getWorldHeight() - clock.getHeight() - 900);
        clock.setOrigin(Align.center);
        clock.setVisible(false);
        clock.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(clock);

        plantHours = new Label("", labelStyle2);
        plantHours.setAlignment(Align.right);
        plantHours.setPosition((clock.getX() + clock.getWidth() / 2) + 70, ((clock.getY() + clock.getHeight() / 2) + 10));
        plantHours.setVisible(false);
        plantHours.setOrigin(Align.center);
        plantHours.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(plantHours);

        hours = new Label("", labelStyle3);
        hours.setAlignment(Align.right);
        hours.setPosition((clock.getX() + clock.getWidth() / 2) + 55, ((clock.getY() + clock.getHeight() / 2) + 55));
        hours.setVisible(false);
        hours.setOrigin(Align.center);
        hours.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(hours);

        health = new Label("", labelStyle3);
        health.setAlignment(Align.right);
        health.setPosition((indic.getX() + indic.getWidth() / 2) + 70, ((indic.getY() + indic.getHeight() / 2)) + 50);
        health.setRotation(20);
        health.setVisible(false);
        health.setOrigin(Align.center);
        health.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(health);

        reg = new TextureRegion(game.manager.getTexture("statistics/cursor.png"));
        reg.flip(false, true);
        cursor = new Image(reg);
        cursor.setPosition(indic.getX() + 140, indic.getY() + 130);
        cursor.setOrigin(0, 11);
        cursor.setVisible(false);
        cursor.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(cursor);

        plantName = new Label(plant.getName(), labelStyle2);
        plantName.setAlignment(Align.right);
        plantName.setPosition((stage.getViewport().getWorldWidth() - plantName.getWidth()) / 2, 40);
        plantName.setRotation(20);
        plantName.setVisible(false);
        plantName.setOrigin(Align.center);
        plantName.setColor(plantName.getColor().r, plantName.getColor().g, plantName.getColor().b, 0.0f);
        plantName.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(plantName);

        score = new Label(game.locale.format("score", String.valueOf(plant.getIntVarVal(IntGameVariables.Var_Score))), labelStyle2);
        score.setAlignment(Align.right);
        score.setPosition((stage.getViewport().getWorldWidth() - score.getWidth()) / 2, 120);
        score.setRotation(20);
        score.setVisible(false);
        score.setOrigin(Align.center);
        score.setColor(score.getColor().r, score.getColor().g, score.getColor().b, 0.0f);
        score.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(score);

        TextureRegion water = new TextureRegion(game.manager.getTexture("statistics/smile4.png"));
        water.flip(false, true);
        statWaterImg = new Image(water);
        statWaterImg.setPosition(130, 450);
        statWaterImg.setOrigin(Align.center);
        statWaterImg.setVisible(false);
        statWaterImg.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statWaterImg);
        statWater = new Label("", labelStyle2);
        statWater.setPosition(statWaterImg.getX() + 170, statWaterImg.getY() + 75);
        statWater.setVisible(false);
        statWater.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statWater);

        TextureRegion insect = new TextureRegion(game.manager.getTexture("statistics/smile2.png"));
        insect.flip(false, true);
        statInsectImg = new Image(insect);
        statInsectImg.setPosition(430, 450);
        statInsectImg.setOrigin(Align.center);
        statInsectImg.setVisible(false);
        statInsectImg.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statInsectImg);
        statInsect = new Label("", labelStyle2);
        statInsect.setPosition(statInsectImg.getX() + 170, statInsectImg.getY() + 75);
        statInsect.setVisible(false);
        statInsect.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statInsect);

        TextureRegion spider = new TextureRegion(game.manager.getTexture("statistics/smile1.png"));
        spider.flip(false, true);
        statSpiderImg = new Image(spider);
        statSpiderImg.setPosition(130, 630);
        statSpiderImg.setOrigin(Align.center);
        statSpiderImg.setVisible(false);
        statSpiderImg.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statSpiderImg);
        statSpider = new Label("", labelStyle2);
        statSpider.setPosition(statSpiderImg.getX() + 170, statSpiderImg.getY() + 75);
        statSpider.setVisible(false);
        statSpider.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statSpider);

        TextureRegion star = new TextureRegion(game.manager.getTexture("statistics/smile5.png"));
        star.flip(false, true);
        statStarImg = new Image(star);
        statStarImg.setPosition(430, 630);
        statStarImg.setOrigin(Align.center);
        statStarImg.setVisible(false);
        statStarImg.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statStarImg);
        statStar = new Label("", labelStyle2);
        statStar.setPosition(statStarImg.getX() + 170, statStarImg.getY() + 75);
        statStar.setVisible(false);
        statStar.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statStar);

        TextureRegion cat = new TextureRegion(game.manager.getTexture("statistics/smile6.png"));
        cat.flip(false, true);
        statCatImg = new Image(cat);
        statCatImg.setPosition(130, 810);
        statCatImg.setOrigin(Align.center);
        statCatImg.setVisible(false);
        statCatImg.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statCatImg);
        statCat = new Label("", labelStyle2);
        statCat.setPosition(statCatImg.getX() + 170, statCatImg.getY() + 75);
        statCat.setVisible(false);
        statCat.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_stat))));
        stage.addActor(statCat);
    }

    public void getStatistic() {

        closeButton.setTouchable(Touchable.disabled);
        scrollright.setTouchable(Touchable.disabled);
        scrollleft.setTouchable(Touchable.disabled);

        game.setState(GameState.GS_GameStatistics);

        closeButton.setVisible(true);
        scrollright.setVisible(true);

        blur.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        blur.setZIndex(0x7FFFFFFF);
        blur.setVisible(true);
        AlphaAction alpha = new AlphaAction();
        alpha.setDuration(0.5f);
        alpha.setAlpha(1.0f);
        blur.addAction(alpha);

        firstStatistic();
        if (!gameUI.getWorld().isTutorialActive() && !FlowergotchiGame.debugMode && !game.getPurchaseManager().isNoAds()) {
            game.serviceCallback().showAdMob(false);
        }
    }

    private void firstStatistic() {
        final Plant plant = (Plant) game.getGameScreen().getWorld().gameObjectManager().findFirstObject(Plant.class);

        for (Pair<Tube, Image> t : ColorTubes) {
            t.getFirst().setVisible(true);
            t.getSecond().setVisible(true);
            t.getFirst().setZIndex(0x7FFFFFFF);
            t.getSecond().setZIndex(0x7FFFFFFF);
            t.getFirst().setScale(0.0f);
            t.getSecond().setScale(0.0f);
            t.getFirst().clearActions();
            t.getSecond().clearActions();
            t.getFirst().addAction(Actions.scaleTo(1.0f, 1.0f, 1.0f, Interpolation.pow2In));
            t.getSecond().addAction(Actions.scaleTo(1.0f, 1.0f, 1.0f, Interpolation.pow2In));
            if (plant.getIntVar(t.getFirst().getVariable().getTag()) != null) {
                t.getFirst().updateVariable(plant.getIntVar(t.getFirst().getVariable().getTag()));
            }
        }

        plantName.setVisible(true);
        plantName.setZIndex(0x7FFFFFFF);
        plantName.addAction(Actions.alpha(1.0f, 0.5f, Interpolation.pow2In));

        score.setVisible(true);
        score.setZIndex(0x7FFFFFFF);
        score.addAction(Actions.alpha(1.0f, 0.5f, Interpolation.pow2In));

        indic.setVisible(true);
        indic.setZIndex(0x7FFFFFFF);
        indic.setScale(0.0f);
        indic.addAction(Actions.sequence(Actions.scaleTo(1.0f, 1.0f, 1.0f, Interpolation.pow2In), Actions.run(new Runnable() {
            @Override
            public void run() {
                cursor.setVisible(true);
                cursor.setZIndex(0x7FFFFFFF);
                cursor.setRotation(180 + plant.getIntVar(IntGameVariables.Var_Health).getPercent() * 180);
                cursor.setScale(0.0f);
                cursor.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2In));
                health.setVisible(true);
                health.setZIndex(0x7FFFFFFF);
                health.setText(game.locale.get("stat_health"));
                health.addAction(Actions.alpha(1.0f, 1.0f, Interpolation.pow2In));
                closeButton.setTouchable(Touchable.enabled);
                scrollright.setTouchable(Touchable.enabled);
                scrollleft.setTouchable(Touchable.enabled);
            }
        })));
    }

    public void hideFirstStatistics() {
        for (Pair<Tube, Image> t : ColorTubes) {
            t.getFirst().addAction(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In));
            t.getSecond().addAction(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In));
        }
        plantName.addAction(Actions.alpha(0.0f, 0.5f, Interpolation.pow2In));
        score.addAction(Actions.alpha(0.0f, 0.5f, Interpolation.pow2In));
        indic.addAction(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In));
        cursor.addAction(Actions.sequence(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In), Actions.run(new Runnable() {
            @Override
            public void run() {
                if (end) {
                    hideBlur();
                }
            }
        })));

        health.setVisible(false);
    }

    private void secondStatistic() {
        clock.setVisible(true);
        clock.setZIndex(0x7FFFFFFF);
        clock.setScale(0.0f);
        clock.addAction(Actions.sequence(Actions.scaleTo(1.0f, 1.0f, 1.0f, Interpolation.pow2In), Actions.run(new Runnable() {
            @Override
            public void run() {
                plantHours.setVisible(true);
                plantHours.setZIndex(0x7FFFFFFF);
                plantHours.setText(String.format("%4.0f", game.getGameScreen().getWorld().getTimer().getTime() / 3600));
                plantHours.addAction(Actions.alpha(1.0f, 1.0f, Interpolation.pow2In));
                hours.setVisible(true);
                hours.setText(game.locale.get("stat_hours"));
                hours.setZIndex(0x7FFFFFFF);
                hours.addAction(Actions.alpha(1.0f, 1.0f, Interpolation.pow2In));
                closeButton.setTouchable(Touchable.enabled);
                scrollright.setTouchable(Touchable.enabled);
                scrollleft.setTouchable(Touchable.enabled);
            }
        })));

        statWaterImg.setVisible(true);
        statWaterImg.setZIndex(0x7FFFFFFF);
        statWaterImg.setScale(0.0f);
        statWaterImg.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2In));
        statWater.setVisible(true);
        statWater.setZIndex(0x7FFFFFFF);
        statWater.setText(String.valueOf(game.getGameScreen().getWorld().getStatistic().getWater()));
        statWater.addAction(Actions.alpha(1.0f, 1.0f, Interpolation.pow2In));

        statInsectImg.setVisible(true);
        statInsectImg.setZIndex(0x7FFFFFFF);
        statInsectImg.setScale(0.0f);
        statInsectImg.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2In));
        statInsect.setVisible(true);
        statInsect.setZIndex(0x7FFFFFFF);
        statInsect.setText(String.valueOf(game.getGameScreen().getWorld().getStatistic().getInsect()));
        statInsect.addAction(Actions.alpha(1.0f, 1.0f, Interpolation.pow2In));

        statSpiderImg.setVisible(true);
        statSpiderImg.setZIndex(0x7FFFFFFF);
        statSpiderImg.setScale(0.0f);
        statSpiderImg.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2In));
        statSpider.setVisible(true);
        statSpider.setZIndex(0x7FFFFFFF);
        statSpider.setText(String.valueOf(game.getGameScreen().getWorld().getStatistic().getSpider()));
        statSpider.addAction(Actions.alpha(1.0f, 1.0f, Interpolation.pow2In));

        statStarImg.setVisible(true);
        statStarImg.setZIndex(0x7FFFFFFF);
        statStarImg.setScale(0.0f);
        statStarImg.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2In));
        statStar.setVisible(true);
        statStar.setZIndex(0x7FFFFFFF);
        statStar.setText(String.valueOf(game.getGameScreen().getWorld().getStatistic().getStar()));
        statStar.addAction(Actions.alpha(1.0f, 1.0f, Interpolation.pow2In));

        statCatImg.setVisible(true);
        statCatImg.setZIndex(0x7FFFFFFF);
        statCatImg.setScale(0.0f);
        statCatImg.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2In));
        statCat.setVisible(true);
        statCat.setZIndex(0x7FFFFFFF);
        statCat.setText(String.valueOf(game.getGameScreen().getWorld().getStatistic().getCat()));
        statCat.addAction(Actions.alpha(1.0f, 1.0f, Interpolation.pow2In));
    }

    private void hideSecondStatistic() {
        clock.addAction(Actions.sequence(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In), Actions.run(new Runnable() {
            @Override
            public void run() {
                if (end) {
                    hideBlur();
                }
            }
        })));

        plantHours.setVisible(false);
        hours.setVisible(false);
        statWater.setVisible(false);
        statWaterImg.addAction(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In));
        statInsect.setVisible(false);
        statInsectImg.addAction(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In));
        statSpider.setVisible(false);
        statSpiderImg.addAction(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In));
        statStar.setVisible(false);
        statStarImg.addAction(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In));
        statCat.setVisible(false);
        statCatImg.addAction(Actions.scaleTo(0.0f, 0.0f, 0.5f, Interpolation.pow2In));
    }

    private void hideBlur() {
        AlphaAction alpha = new AlphaAction();
        alpha.setDuration(0.5f);
        alpha.setAlpha(0.0f);
        blur.addAction(Actions.sequence(alpha, Actions.run(new Runnable() {
            @Override
            public void run() {
                blur.setVisible(false);
                game.setState(GameState.GS_GameProcess);
                gameUI.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.StatisticGameFinished);
                gameUI.getUIButtons().get("statistic").addAction(Actions.sequence(Actions.delay(1.0f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        gameUI.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.StatisticGameFinished);
                    }
                })));
            }
        })));

        end = false;
        scrollNum = 0;
        if (!gameUI.getWorld().isTutorialActive() && !FlowergotchiGame.debugMode && !game.getPurchaseManager().isNoAds()) {
            game.serviceCallback().showAdMob(true);
        }

    }
}
