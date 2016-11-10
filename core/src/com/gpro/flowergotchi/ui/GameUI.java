package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.AndroidPushNotificationServiceCallback;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.GameRenderer;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.gamelogic.insects.InsectActor;
import com.gpro.flowergotchi.screens.ArcanoidScreen;
import com.gpro.flowergotchi.screens.ScoreboardScreen;
import com.gpro.flowergotchi.screens.ScreenFade;
import com.gpro.flowergotchi.screens.ScreenshotFactory;
import com.gpro.flowergotchi.screens.VictoryScreen;
import com.gpro.flowergotchi.screens.WaterScreen;
import com.gpro.flowergotchi.ui.game.ShovelGame;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class GameUI {
    private final FlowergotchiGame game;
    private final Skin skin;
    private final Stage stage;
    public final ActorComparator comparator;

    private boolean isAnimationRunning = false, isPoisonAnimationRunning = false;
    private Sound click;
    private boolean isBirdFlying = false;
    private final TutorialPlayer tutorialPlayer;
    private Map<String, Actor> UIButtons;
    private boolean isWallpaperMode = false;
    private final Map<Integer, Boolean> objectsOnUI;
    private Image light;
    private TubeQueue tubeQueue;
    private boolean plantIsDead = false;
    private boolean gamePaused = false;
    private final SmileQueue ourSmileQueue;
    private long secondBird = 0;
    private ShovelGame shovelGame;
    private Group pauseDialogTable;
    private Statistics stats;
    private final MessageQueue mesQueue;
    private boolean plantIsFinished;
    private long timer = 0;
    private ImageButton scrollright;
    private boolean showButtons;
    private PurchaseFlask purchaseFlask;
    private boolean isVisibleLight = true;
    private boolean isVisibleWater = true;
    private boolean isVisibleShovel = true;

    public GameUI(FlowergotchiGame game, GameRenderer renderer, GameWorld world, ResourceManager manager, boolean wallpaperMode) {
        this.game = game;
        isWallpaperMode = wallpaperMode;
        comparator = new ActorComparator();
        ourSmileQueue = new SmileQueue(this, this, game);

        objectsOnUI = new TreeMap<Integer, Boolean>();
        stage = new Stage(renderer.getBackViewport(), renderer.getSpriteBatch());

        skin = new Skin();
        skin.addRegions(game.manager.get("buttons/buttons.pack", TextureAtlas.class));

        tutorialPlayer = new TutorialPlayer(game, this);
        mesQueue = new MessageQueue(game, this);
        createPauseDialog();
        if (!isWallpaperMode()) {
            initUIElements(manager);
            stats = new Statistics(game, this, world);

        }

        secondBird = System.currentTimeMillis();
        timer = System.currentTimeMillis();
        showButtons = true;
    }

    public FlowergotchiGame getGame() {
        return game;
    }

    private void createPauseDialog() {
        pauseDialogTable = new Group();
        pauseDialogTable.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));

        final Image blur = new Image(game.manager.getTexture("blur.png"));
        blur.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        pauseDialogTable.addActor(blur);

        Label.LabelStyle style = new Label.LabelStyle(game.utility.getMainFont(), Color.WHITE);
        final Label text = new Label(game.locale.get("game_paused"), style);
        text.setAlignment(Align.center);
        text.setPosition((stage.getViewport().getWorldWidth() - text.getWidth()) / 2, 300);
        text.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        pauseDialogTable.addActor(text);

        if (!isWallpaperMode) {
            final TextButton selectButton = new TextButton(game.locale.get("game_resume"), game.utility.getDefaultSkin().get("defButton", TextButton.TextButtonStyle.class));
            selectButton.getLabel().setAlignment(Align.center);
            selectButton.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
            selectButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    game.serviceCallback().resumeGame();

                }
            });
            selectButton.setPosition((stage.getViewport().getWorldWidth() - selectButton.getWidth()) / 2, 300 + text.getHeight());
            pauseDialogTable.addActor(selectButton);
        }

        pauseDialogTable.setVisible(false);
        stage.addActor(pauseDialogTable);
    }

    public boolean isBirdFlying() {
        return isBirdFlying;
    }

    public void setIsBirdFlying(boolean isBirdFlying) {
        if (!isBirdFlying) {
            secondBird = System.currentTimeMillis();
        }
        this.isBirdFlying = isBirdFlying;
    }

    public boolean isPlantIsDead() {
        return plantIsDead;
    }

    public TutorialPlayer getTutorialPlayer() {
        return tutorialPlayer;
    }

    public Map<String, Actor> getUIButtons() {
        return UIButtons;
    }

    public SmileQueue getSmileQueue() {
        return ourSmileQueue;
    }

    public boolean isWallpaperMode() {
        return isWallpaperMode;
    }

    public void requestChangeSmile(boolean delete) {
        ourSmileQueue.requestChangeSmile(delete);
    }

    public boolean objectOnUI(Integer id) {
        return objectsOnUI.containsKey(id) && objectsOnUI.get(id);
    }

    public void setObjectOnUI(Integer id) {
        objectsOnUI.put(id, true);
    }

    public Actor getActorByName(String name) {
        for (Actor a : this.getStage().getActors()) {
            if (a.getName() != null && a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public AndroidPushNotificationServiceCallback gameCallback() {
        return game.serviceCallback();
    }

    public Stage getStage() {
        return stage;
    }

    private void initUIElements(ResourceManager manager) {
        click = manager.get("click.wav", Sound.class);
        UIButtons = new TreeMap<String, Actor>();

        Map<Integer, String> aMap = new TreeMap<Integer, String>();
        aMap.put(2, "water");
        aMap.put(3, "shovel");
        aMap.put(4, "light");
        aMap.put(5, "pot");
        aMap.put(6, "camera");
        aMap.put(7, "statistic");
        aMap.put(8, "pause");
        Map<Integer, Vector2> coordMap = new TreeMap<Integer, Vector2>();
        coordMap.put(2, new Vector2(10, 100));
        coordMap.put(3, new Vector2(10, 260));
        coordMap.put(4, new Vector2(10, 420));
        coordMap.put(5, new Vector2(10, 620));
        coordMap.put(6, new Vector2(10, 780));
        coordMap.put(7, new Vector2(10, 940));
        coordMap.put(8, new Vector2(10, 1100));
        Map<Integer, ChangeListener> listenerMap = new TreeMap<Integer, ChangeListener>();


        Texture tex = game.manager.getTexture("light.png");
        TextureRegion reg = new TextureRegion(tex);
        reg.flip(false, true);
        light = new Image(new TextureRegionDrawable(reg));
        light.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Light))));
        stage.addActor(light);

        Map<Integer, String> StandardUIButtons = Collections.unmodifiableMap(aMap);
        final GameUI gameUI = this;

        ChangeListener cameraListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));

                BitmapFont font12 = game.utility.getSmallFont();
                Label.LabelStyle style = new Label.LabelStyle(font12, Color.BLACK);

                Label label1 = new Label(game.locale.get("screenshot_message"), style);
                label1.setWrap(true);
                label1.setAlignment(Align.left);
                style.fontColor = new Color(73.0f / 255, 36.0f / 255, 7.0f / 255, 1.0f);

                final Texture background = game.manager.getTexture("images/skin/messageBox.png");

                TextButton btnOK = new TextButton(game.locale.get("yes"), game.utility.getDefaultSkin().get("default", TextButton.TextButtonStyle.class));

                TextButton btnNO = new TextButton(game.locale.get("no"), game.utility.getDefaultSkin().get("defaultRed", TextButton.TextButtonStyle.class));

                final Dialog dialog = new Dialog("", game.utility.getDefaultSkin()) {
                    @Override
                    public float getPrefWidth() {
                        return background.getWidth();
                    }

                    @Override
                    public float getPrefHeight() {
                        return background.getHeight();
                    }
                };
                dialog.setModal(true);
                dialog.setMovable(false);
                dialog.setResizable(false);

                btnOK.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                        dialog.setVisible(false);
                        dialog.hide();
                        dialog.cancel();
                        dialog.remove();
                        gameUI.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.CameraGameFinished);
                        float delay1 = 0.1f;
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                ScreenshotFactory screenshotFactory = new ScreenshotFactory(game);
                                screenshotFactory.saveScreenshot();
                            }
                        }, delay1);


                        gameUI.getUIButtons().get("camera").addAction(Actions.sequence(Actions.delay(1.0f), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                gameUI.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.CameraGameFinished);
                            }
                        })));
                    }
                });

                btnNO.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                        dialog.cancel();
                        dialog.hide();
                        dialog.remove();
                        gameUI.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.CameraGameFinished);

                        gameUI.getUIButtons().get("camera").addAction(Actions.sequence(Actions.delay(1.0f), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                gameUI.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.CameraGameFinished);
                            }
                        })));
                    }
                });

                TextureRegion myTex = new TextureRegion(background);
                myTex.flip(false, true);
                myTex.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                TextureRegionDrawable drawable = new TextureRegionDrawable(myTex);
                dialog.setBackground(drawable);

                dialog.row().colspan(1).center().size(background.getWidth() - 60f, 300f);
                dialog.add(label1).expand();
                dialog.row().colspan(2).size(background.getWidth() - 60f, 100f);
                dialog.button(btnOK);
                dialog.button(btnNO);
                dialog.align(Align.center);
                dialog.show(stage).setPosition((stage.getViewport().getWorldWidth() - background.getWidth()) / 2, 300);

                dialog.pack();
                dialog.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_ScreenShot))));
                stage.addActor(dialog);
                timer = System.currentTimeMillis();
            }
        };

        ChangeListener waterListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {

                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                if (!isWaterAnimationRunning()) {
                    ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                        @Override
                        public void run() {
                            game.setState(GameState.GS_GameWater);

                            game.addScreen(new WaterScreen(game, gameUI));
                        }
                    });
                    stage.addActor(fade);
                }

            }
        };

        ChangeListener lightListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                game.serviceCallback().clientButtonCallback(AndroidCallbackTypes.CB_Light, null);
                ourSmileQueue.addSmile("bubble/smile.png", "smile");
                gameUI.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.LightGameFinished);
                gameUI.getUIButtons().get("light").addAction(Actions.sequence(Actions.delay(1.0f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        gameUI.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.LightGameFinished);
                    }
                })));
                timer = System.currentTimeMillis();
            }
        };

        ChangeListener looseningListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                if (shovelGame == null || !shovelGame.isActive()) {
                    shovelGame = new ShovelGame(game, game.getGameScreen().getWorld(), gameUI);
                }

            }
        };

        ChangeListener potListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                game.serviceCallback().clientButtonCallback(AndroidCallbackTypes.CB_OpenPot, null);
                gameUI.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.PotGameFinished);
                gameUI.getUIButtons().get("pot").addAction(Actions.sequence(Actions.delay(1.0f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        gameUI.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.PotGameFinished);
                    }
                })));
                timer = System.currentTimeMillis();
            }
        };

        ChangeListener pauseListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                game.serviceCallback().pauseGame();
                timer = System.currentTimeMillis();
            }
        };

        ChangeListener statisticListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                stats.getStatistic();
                timer = System.currentTimeMillis();
            }
        };

        listenerMap.put(2, waterListener);
        listenerMap.put(3, looseningListener);
        listenerMap.put(4, lightListener);
        listenerMap.put(5, potListener);
        listenerMap.put(6, cameraListener);
        listenerMap.put(7, statisticListener);
        listenerMap.put(8, pauseListener);

        for (Map.Entry<Integer, String> b : StandardUIButtons.entrySet()) {
            ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
            buttonStyle.imageDown = skin.getDrawable(b.getValue() + "OnClick");
            buttonStyle.imageUp = skin.getDrawable(b.getValue());
            ImageButton button = new ImageButton(buttonStyle);
            button.setTransform(true);
            button.addListener(listenerMap.get(b.getKey()));
            button.setPosition(coordMap.get(b.getKey()).x, coordMap.get(b.getKey()).y);
            button.setSize(150, 150);
            button.setOrigin(Align.center);
            button.setRotation(180);
            button.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_UIButtons))));

            this.stage.addActor(button);
            UIButtons.put(b.getValue(), button);
        }


        purchaseFlask = new PurchaseFlask(game, gameUI, game.manager.getTexture("purchases/flask.png"));
        purchaseFlask.setVisibility(false);
        this.stage.addActor(purchaseFlask);

        purchaseFlask.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!isPoisonAnimationRunning() && !isWallpaperMode()) {
                    stage.addActor(new Poison(game, gameUI, game.getGameScreen().getWorld().getActivePot()));
                    setPoisonAnimationRunning(true);
                    purchaseFlask.decrementCountPurchase();
                    game.getPurchaseManager().decFertilizer();
                    game.getPurchaseManager().saveData();
                }
            }
        });


        scrollright = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("selectscreens/menu_right.png"))),
                new TextureRegionDrawable(new TextureRegion(new Texture("selectscreens/menu_right_pressed.png"))));
        scrollright.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_UIButtons))));
        scrollright.setPosition(17, (stage.getViewport().getWorldHeight() - scrollright.getHeight()) / 2);
        scrollright.setVisible(false);
        stage.addActor(scrollright);
        scrollright.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                timer = System.currentTimeMillis();
                showButtons();
            }
        });
    }

    public void updateUI(GameWorld world) {
        Plant plant = (Plant) world.gameObjectManager().findFirstObject(Plant.class);

        if (plant.isDead()) {
            if (!plantIsDead) {
                plantIsDead = true;
                deadDialog(world);
            }
        }

        if (plant.isFinished()) {
            if (!plantIsFinished) {
                plantIsFinished = true;
                final GameUI ui = this;
                this.fadeOut(new Runnable() {
                    @Override
                    public void run() {
                        VictoryScreen screen = new VictoryScreen(game, ui);
                        game.setScreen(screen);
                    }
                });
            }
            return;
        }

        if (world.isGamePaused() && !world.isTutorialActive()) {
            if (!gamePaused) {
                gamePaused = true;
                pauseDialog();
            }
        } else {
            if (gamePaused) {
                game.setState(GameState.GS_GameProcess);
                pauseDialogTable.setVisible(false);
                gamePaused = false;
            }
        }

        if (tubeQueue == null) {
            tubeQueue = new TubeQueue(stage, game.manager, plant);
        }

        if (plant.getIntVarVal(IntGameVariables.Var_Water) < 0) {
            ourSmileQueue.addSmile("bubble/water.png", "wantwater");
        }
        if (plant.getIntVarVal(IntGameVariables.Var_Loosening) < 0) {
            ourSmileQueue.addSmile("bubble/loosening.png", "wantloosening");
        }
        if (plant.getIntVarVal(IntGameVariables.Var_Insects) > 0) {

            ourSmileQueue.addSmile("bubble/insects.png", "insects");
        }
        if (plant.getIntVarVal(IntGameVariables.Var_NextStage) > 0) {
            // TO-DO : add next stage notif
        }
        if (plant.getIntVarVal(IntGameVariables.Var_Light) > 0
                && plant.getIntVarVal(IntGameVariables.Var_Water) > 0
                && plant.getIntVarVal(IntGameVariables.Var_Loosening) > 0
                && plant.getIntVarVal(IntGameVariables.Var_Insects) == 0
                && System.currentTimeMillis() > ourSmileQueue.getNextCoolSmile() + FlowergotchiGame.updateRate) {
            ourSmileQueue.addSmile("bubble/smile.png", "smile");
            ourSmileQueue.setNextCoolSmile(System.currentTimeMillis());
        }
        if (plant.getIntVarVal(IntGameVariables.Var_Light) < 0
                && plant.getIntVarVal(IntGameVariables.Var_Water) < 0
                && plant.getIntVarVal(IntGameVariables.Var_Loosening) < 0
                && plant.getIntVarVal(IntGameVariables.Var_Insects) > 0
                && System.currentTimeMillis() > ourSmileQueue.getNextCoolSmile() + FlowergotchiGame.updateRate) {
            ourSmileQueue.addSmile("bubble/sad.png", "sad");
            ourSmileQueue.setNextCoolSmile(System.currentTimeMillis());
        }
        /*if (!isWallpaperMode() && !getWorld().isTutorialActive()) {
            if (!plant.getIntVar(IntGameVariables.Var_Water).isMedLevel() && !plant.getIntVar(IntGameVariables.Var_Water).isLowLevel()) {
                this.getUIButtons().get("water").setVisible(false);
                isVisibleWater = false;
            } else {
                this.getUIButtons().get("water").setVisible(true);
                isVisibleWater = true;
            }

            if (!plant.getIntVar(IntGameVariables.Var_Loosening).isMedLevel() && !plant.getIntVar(IntGameVariables.Var_Water).isLowLevel()) {
                this.getUIButtons().get("shovel").setVisible(false);
                isVisibleShovel = false;
            } else {
                this.getUIButtons().get("shovel").setVisible(true);
                isVisibleShovel = true;
            }

            if (!plant.getIntVar(IntGameVariables.Var_Light).isMedLevel() && !plant.getIntVar(IntGameVariables.Var_Water).isLowLevel()) {
                this.getUIButtons().get("light").setVisible(false);
                isVisibleLight = false;
            } else {
                this.getUIButtons().get("light").setVisible(true);
                isVisibleLight = true;
            }
            light.setVisible(world.isLightEnabled());
        } else {
            if (plant.getIntVar(IntGameVariables.Var_Cat).get() == 0) {
                if (this.getActorByName("cat") != null) {
                    Cat cat = (Cat) this.getActorByName("cat");
                    cat.remove();
                }
            }
            if (plant.getIntVar(IntGameVariables.Var_Spider).get() == 0) {
                if (this.getActorByName("spiderweb") != null) {
                    SpiderWeb web = (SpiderWeb) this.getActorByName("spiderweb");
                    web.remove();
                }
            }
        }*/

        for (Actor a : getStage().getActors()) {
            if (a instanceof InsectActor) {
                if (world.gameObjectManager().getObjectByID(((InsectActor) a).getParentID()) == null) {
                    a.remove();
                }
            }
        }

        if (plant.getIntVar(IntGameVariables.Var_Spider).get() > 0) {
            if (this.getActorByName("spiderweb") == null) {
                SpiderWeb spiderWeb = new SpiderWeb(game, this, game.manager.getTexture("spiders/spiderweb.png"));
                stage.addActor(spiderWeb);
                mesQueue.addMessage("mes_firstspider", "mes_firstspider", null);
            }
            ourSmileQueue.addSmile("bubble/spider.png", "spider");
        }


        if (plant.getIntVar(IntGameVariables.Var_Cat).get() > 0) {
            if (this.getActorByName("cat") == null) {
                Cat cat = new Cat(game, this);
                stage.addActor(cat);
                mesQueue.addMessage("mes_firstcat", "mes_firstcat", null);
            }
            ourSmileQueue.addSmile("bubble/cat.png", "catsmile");
        }

        if (!this.isWallpaperMode()) {
            purchaseFlask.updateValue(game.getPurchaseManager().getFertilizerCount());
        }

        // Show tubes
        tubeQueue.update(plant);
        mesQueue.update();
    }

    private void pauseDialog() {
        pauseDialogTable.setVisible(true);
        game.setState(GameState.GS_GameInGamePause);
    }

    private void deadDialog(final GameWorld world) {
        final Music dead = game.manager.get("sounds/plantisdead.ogg");
        Image blur = new Image(game.manager.getTexture("blur.png"));
        blur.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        stage.addActor(blur);

        TextureRegion ripTex = new TextureRegion(game.manager.getTexture("rip.png"));
        ripTex.flip(false, true);
        Image rip = new Image(new TextureRegionDrawable(ripTex));
        rip.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        rip.setOrigin(Align.center);
        rip.setAlign(Align.center);
        rip.setPosition((stage.getViewport().getWorldWidth() - rip.getWidth()) / 2, 300);
        stage.addActor(rip);

        Label.LabelStyle style = new Label.LabelStyle(game.utility.getMainFont(), Color.WHITE);
        Label text = new Label(game.locale.get("plant_dead"), style);
        text.setAlignment(Align.center);
        text.setPosition((stage.getViewport().getWorldWidth() - text.getWidth()) / 2, 300 + rip.getHeight());
        text.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        stage.addActor(text);

        if (!isWallpaperMode) {
            dead.setLooping(false);
            dead.setVolume(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
            dead.play();
            TextButton selectButton = new TextButton(game.locale.get("plant_dead_ok"), game.utility.getDefaultSkin().get("defButton", TextButton.TextButtonStyle.class));
            selectButton.getLabel().setAlignment(Align.center);
            selectButton.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
            selectButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ScoreboardScreen screen = new ScoreboardScreen(game, world.getActiveFlower().getName(), world.getActiveFlower().getIntVarVal(IntGameVariables.Var_Progress));
                    dead.stop();
                    game.setScreen(screen);
                }
            });
            selectButton.setPosition((stage.getViewport().getWorldWidth() - selectButton.getWidth()) / 2, 300 + rip.getHeight() + text.getHeight());
            stage.addActor(selectButton);
        }
    }

    public void showOkDialog(Stage stage, String message, final Runnable listener) {
        Label.LabelStyle style = new Label.LabelStyle(game.utility.getSmallFont(), Color.BLACK);

        Label label1 = new Label(game.locale.get(message), style);
        label1.setWrap(true);
        label1.setAlignment(Align.left);
        style.fontColor = new Color(73.0f / 255, 36.0f / 255, 7.0f / 255, 1.0f);

        final Texture background = game.manager.getTexture("images/skin/messageBox.png");

        TextButton btnOK = new TextButton(game.locale.get("ok"), game.utility.getDefaultSkin().get("default", TextButton.TextButtonStyle.class));

        final Dialog dialog = new Dialog("", game.utility.getDefaultSkin()) {
            @Override
            public float getPrefWidth() {
                return background.getWidth();
            }

            @Override
            public float getPrefHeight() {
                return background.getHeight();
            }
        };
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        btnOK.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                dialog.hide();
                dialog.cancel();
                dialog.remove();
                listener.run();
            }
        });

        TextureRegion myTex = new TextureRegion(background);
        myTex.flip(false, true);
        myTex.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegionDrawable drawable = new TextureRegionDrawable(myTex);
        dialog.setBackground(drawable);

        dialog.row().colspan(1).center().size(background.getWidth() - 60f, 300f);
        dialog.add(label1).expand();
        dialog.row().colspan(1).size(background.getWidth() - 60f, 100f);
        dialog.button(btnOK);
        dialog.align(Align.center);
        dialog.show(stage).setPosition((stage.getViewport().getWorldWidth() - background.getWidth()) / 2, 300);

        dialog.pack();
        dialog.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_ScreenShot))));
        stage.addActor(dialog);
    }

    public void onDrawUI() {
        if (!isWallpaperMode()) {
            if (purchaseFlask.getCountPurchase()>0){
                purchaseFlask.setVisibility(true);
            } else {
                purchaseFlask.setVisibility(false);
            }

            if (System.currentTimeMillis() - timer > 10000 && showButtons && !game.getPreferences().isFirstStart()){
                hideButtons();
            }
        }


        if (!isWallpaperMode() && Gdx.input.isTouched()) {
            secondBird = System.currentTimeMillis();
        }

        if (!isBirdFlying && (System.currentTimeMillis() - secondBird) > 60000) {
            Bird bird = new Bird(game, this, getWorld().getActiveBack());
            stage.addActor(bird);
            this.setIsBirdFlying(true);
        }

        stage.getBatch().enableBlending();
        stage.act(Gdx.graphics.getDeltaTime());

        if (!ourSmileQueue.isSmileActive()) {
            requestChangeSmile(false);
        }
        if (shovelGame != null && shovelGame.getStart()) {
            shovelGame.gameLoop(Gdx.graphics.getDeltaTime());
            if (!shovelGame.isActive()) {
                shovelGame = null;
            }
        }
        stage.getActors().sort(comparator);

        stage.draw();
    }

    public boolean isShovelGameActive() {
        return shovelGame != null && shovelGame.isActive();
    }

    public GameWorld getWorld() {
        if (isWallpaperMode()) {
            return game.getWallpaperScreen().getWorld();
        } else return game.getGameScreen().getWorld();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isWaterAnimationRunning() {
        return isAnimationRunning;
    }

    public void setWaterAnimationRunning(boolean isAnimationRunning) {
        this.isAnimationRunning = isAnimationRunning;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isPoisonAnimationRunning() {
        return isPoisonAnimationRunning;
    }

    public void setPoisonAnimationRunning(boolean isPoisonAnimationRunning) {
        this.isPoisonAnimationRunning = isPoisonAnimationRunning;
    }

    public void fadeIn(Runnable runnable) {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, runnable);
        stage.addActor(fade);
    }

    public void fadeOut(Runnable runnable) {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, runnable);
        stage.addActor(fade);
    }

    public void removeWeb() {
        if (this.getActorByName("spiderweb") != null) {
            SpiderWeb spiderWeb = (SpiderWeb) this.getActorByName("spiderweb");
            spiderWeb.remove();
        }
    }

    public void startTutorial() {
        tutorialPlayer.play();
    }

    public void getArcanoid() {
        if (!isWallpaperMode()) {
            game.addScreen(new ArcanoidScreen(game, this, game.getGameScreen().getWorld().getActiveFlower().getIntVarVal(IntGameVariables.Var_Insects)));
        }
    }


    public Sound getClick() {
        return click;
    }



    public ActorComparator getComparator() {
        return comparator;
    }

    public void pause() {
        if (isShovelGameActive()) {
            shovelGame.requestPause();
        }
    }

    public void resume() {
        if (isShovelGameActive()) {
            shovelGame.requestResume();
        }
    }

    private class ActorComparator implements Comparator<Actor> {
        @Override
        public int compare(Actor arg0, Actor arg1) {
            if (((ActorParameters) (arg0.getUserObject())).getParam(IntGameVariables.Var_DrawLevel).get()
                    < ((ActorParameters) (arg1.getUserObject())).getParam(IntGameVariables.Var_DrawLevel).get()) {
                return -1;
            } else if (((ActorParameters) (arg0.getUserObject())).getParam(IntGameVariables.Var_DrawLevel).get()
                    .equals(((ActorParameters) (arg1.getUserObject())).getParam(IntGameVariables.Var_DrawLevel).get())) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    public void hideButtons(){
        for (final Map.Entry<String, Actor> b : getUIButtons().entrySet()){
            b.getValue().setTouchable(Touchable.disabled);
            b.getValue().addAction(Actions.sequence(Actions.moveBy(-200, 0, 1.5f, Interpolation.pow2), Actions.run(new Runnable() {
                public void run() {
                    b.getValue().setVisible(false);
                }
            })));
        }
        scrollright.addAction(Actions.sequence(Actions.delay(1.5f), Actions.visible(true)));
        showButtons = false;
    }

    public void showButtons(){

        if (isVisibleWater) {
            this.getUIButtons().get("water").setVisible(true);
            this.getUIButtons().get("water").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2)));
            this.getUIButtons().get("water").setTouchable(Touchable.enabled);
        }

        if (isVisibleShovel) {
            this.getUIButtons().get("shovel").setVisible(true);
            this.getUIButtons().get("shovel").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2)));
            this.getUIButtons().get("shovel").setTouchable(Touchable.enabled);
        }

        if (isVisibleLight) {
            this.getUIButtons().get("light").setVisible(true);
            this.getUIButtons().get("light").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2)));
            this.getUIButtons().get("light").setTouchable(Touchable.enabled);
        }


        this.getUIButtons().get("pot").setVisible(true);
        this.getUIButtons().get("pot").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2)));
        this.getUIButtons().get("pot").setTouchable(Touchable.enabled);
        this.getUIButtons().get("camera").setVisible(true);
        this.getUIButtons().get("camera").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2)));
        this.getUIButtons().get("camera").setTouchable(Touchable.enabled);
        this.getUIButtons().get("statistic").setVisible(true);
        this.getUIButtons().get("statistic").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2)));
        this.getUIButtons().get("statistic").setTouchable(Touchable.enabled);
        this.getUIButtons().get("pause").setVisible(true);
        this.getUIButtons().get("pause").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2)));
        this.getUIButtons().get("pause").setTouchable(Touchable.enabled);

        scrollright.setVisible(false);
        showButtons = true;
    }
}
