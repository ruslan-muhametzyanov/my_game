package com.gpro.flowergotchi.ui.tutorial;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.tutorial.step.Step10Pause;
import com.gpro.flowergotchi.ui.tutorial.step.Step11Wallpaper;
import com.gpro.flowergotchi.ui.tutorial.step.Step1Introduction;
import com.gpro.flowergotchi.ui.tutorial.step.Step2Pour;
import com.gpro.flowergotchi.ui.tutorial.step.Step3Loosening;
import com.gpro.flowergotchi.ui.tutorial.step.Step4Pot;
import com.gpro.flowergotchi.ui.tutorial.step.Step5Light;
import com.gpro.flowergotchi.ui.tutorial.step.Step6ScreenShot;
import com.gpro.flowergotchi.ui.tutorial.step.Step9Statistic;
import com.gpro.flowergotchi.ui.tutorial.step.StepEndTutorial;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 12.01.2016.
 */
public class TutorialPlayer {
    private GameUI ui;
    private Image blur;
    private List<TutorialStep> steps;
    private int currentStep = 0;
    private TutorialCallback waitForCallback;
    private boolean isActive = false;
    private ResourceManager manager;
    private FlowergotchiGame game;

    public TutorialPlayer(FlowergotchiGame game, GameUI ui) {
        this.game = game;
        this.ui = ui;
        this.manager = game.manager;

        steps = new LinkedList<TutorialStep>();
        steps.add(new Step1Introduction(ui, this));
        steps.add(new Step2Pour(ui, this));
        steps.add(new Step3Loosening(ui, this));
        steps.add(new Step5Light(ui, this));
        steps.add(new Step4Pot(ui, this));
        steps.add(new Step6ScreenShot(ui, this));
        steps.add(new Step9Statistic(ui, this));
        steps.add(new Step10Pause(ui, this));
        steps.add(new Step11Wallpaper(ui, this));
        steps.add(new StepEndTutorial(ui, this));

    }

    public ResourceManager getManager() {
        return manager;
    }

    public void play() {
        isActive = true;

        blur = new Image(game.manager.getTexture("blur.png"));
        blur.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        ui.getStage().addActor(blur);

        steps.get(0).prepare();
        steps.get(0).startStep();
    }

    public void customTutorialStart(List<TutorialStep> list) {
        if (list.size() == 0) {
            return;
        }
        steps = list;
        play();
    }

    public void endTutorial() {
        for (Map.Entry<String, Actor> b : ui.getUIButtons().entrySet()) {
            b.getValue().clearActions();
            b.getValue().setX(0);
            b.getValue().setTouchable(Touchable.enabled);
            b.getValue().setVisible(true);
        }
        isActive = false;

        blur.remove();
        game.serviceCallback().setTutorialMode(false);
        game.getPreferences().tutorialComplete();
    }

    public FlowergotchiGame getGame() {
        return game;
    }

    public Stage getStage() {
        return ui.getStage();
    }

    public void requestAdvance(TutorialCallback callbackType) {
        if (!isActive) {
            return;
        }
        if (callbackType == waitForCallback) {
            blur.setVisible(true);
            blur.setColor(blur.getColor().r, blur.getColor().g, blur.getColor().b, 1.0f);
            advance(null);
        }
    }

    public void callback(TutorialCallback callbackType) {
        if (!isActive) {
            return;
        }
        if (callbackType == waitForCallback) {
            switch (callbackType) {
                case WaterGameFinished: {
                    ui.getUIButtons().get("water").setTouchable(Touchable.disabled);
                    ui.getUIButtons().get("water").clearActions();
                    ui.getUIButtons().get("water").setScale(1.0f, 1.0f);
                    ui.getUIButtons().get("water").addAction(Actions.sequence(Actions.delay(1.0f), Actions.moveBy(-200, 0, 1.5f, Interpolation.pow2), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ui.getUIButtons().get("water").setVisible(false);
                        }
                    })));
                }
                break;
                case ShovelGameFinished: {
                    ui.getUIButtons().get("shovel").setTouchable(Touchable.disabled);
                    ui.getUIButtons().get("shovel").clearActions();
                    ui.getUIButtons().get("shovel").setScale(1.0f, 1.0f);
                    ui.getUIButtons().get("shovel").addAction(Actions.sequence(Actions.delay(1.0f), Actions.moveBy(-200, 0, 1.5f, Interpolation.pow2), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ui.getUIButtons().get("shovel").setVisible(false);
                        }
                    })));
                }
                break;
                case PotGameFinished: {
                    ui.getUIButtons().get("pot").setTouchable(Touchable.disabled);
                    ui.getUIButtons().get("pot").clearActions();
                    ui.getUIButtons().get("pot").setScale(1.0f, 1.0f);
                    ui.getUIButtons().get("pot").addAction(Actions.sequence(Actions.delay(1.0f), Actions.moveBy(-200, 0, 1.5f, Interpolation.pow2), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ui.getUIButtons().get("pot").setVisible(false);
                        }
                    })));
                }
                break;
                case LightGameFinished: {
                    ui.getUIButtons().get("light").setTouchable(Touchable.disabled);
                    ui.getUIButtons().get("light").clearActions();
                    ui.getUIButtons().get("light").setScale(1.0f, 1.0f);
                    ui.getUIButtons().get("light").addAction(Actions.sequence(Actions.delay(1.0f), Actions.moveBy(-200, 0, 1.5f, Interpolation.pow2), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ui.getUIButtons().get("light").setVisible(false);
                        }
                    })));
                }
                break;
                case CameraGameFinished: {
                    ui.getUIButtons().get("camera").setTouchable(Touchable.disabled);
                    ui.getUIButtons().get("camera").clearActions();
                    ui.getUIButtons().get("camera").setScale(1.0f, 1.0f);
                    ui.getUIButtons().get("camera").addAction(Actions.sequence(Actions.delay(1.0f), Actions.moveBy(-200, 0, 1.5f, Interpolation.pow2), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ui.getUIButtons().get("camera").setVisible(false);
                        }
                    })));
                }
                break;
                case SpiderGameFinished: {

                }
                break;
                case InsectGameFinished: {

                }
                break;
                case WallpaperGameFinished: {

                }
                break;
                case StatisticGameFinished: {
                    ui.getUIButtons().get("statistic").setTouchable(Touchable.disabled);
                    ui.getUIButtons().get("statistic").clearActions();
                    ui.getUIButtons().get("statistic").setScale(1.0f, 1.0f);
                    ui.getUIButtons().get("statistic").addAction(Actions.sequence(Actions.delay(1.0f), Actions.moveBy(-200, 0, 1.5f, Interpolation.pow2), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ui.getUIButtons().get("statistic").setVisible(false);
                        }
                    })));
                }
                break;
                case PauseGameFinished: {
                    ui.getUIButtons().get("pause").setTouchable(Touchable.disabled);
                    ui.getUIButtons().get("pause").clearActions();
                    ui.getUIButtons().get("pause").setScale(1.0f, 1.0f);
                    ui.getUIButtons().get("pause").addAction(Actions.sequence(Actions.delay(1.0f), Actions.moveBy(-200, 0, 1.5f, Interpolation.pow2), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ui.getUIButtons().get("pause").setVisible(false);
                        }
                    })));
                }
                break;
            }

        }
    }

    public void advance(TutorialCallback callbackType) {
        if (callbackType != null) {
            blur.addAction(Actions.sequence(Actions.alpha(0.0f, 0.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    blur.setVisible(false);
                }
            })));
            waitForCallback = callbackType;
        } else {
            ++currentStep;
            if (currentStep >= steps.size()) {
                endTutorial();
            } else {
                steps.get(currentStep).startStep();
            }
        }
    }

    public enum TutorialCallback {
        WaterGameFinished,
        ShovelGameFinished,
        PotGameFinished,
        LightGameFinished,
        CameraGameFinished,
        SpiderGameFinished,
        InsectGameFinished,
        WallpaperGameFinished,
        PauseGameFinished,
        StatisticGameFinished
    }
}
