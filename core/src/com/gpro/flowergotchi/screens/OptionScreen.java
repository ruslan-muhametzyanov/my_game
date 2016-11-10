//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.InputHandler;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

public class OptionScreen extends DemoScreen {
    private final FlowergotchiGame game;
    private final boolean isRestart;
    private Image background;
    private StretchViewport viewport;
    private SpriteBatch batch;
    private Stage stage;
    private Skin skin1;
    private Table elements;
    private TextButton soundYesbtn;
    private TextButton soundNobtn;
    private TextButton notifYesbtn;
    private TextButton notifNobtn;
    private Sound click;

    public OptionScreen(FlowergotchiGame game, boolean isRestart) {
        super(game);
        this.game = game;
        this.isRestart = isRestart;
        this.setupStageViewport();
    }

    private void setupStageViewport() {
        OrthographicCamera cam = new OrthographicCamera();
        this.batch = new SpriteBatch();
        cam.setToOrtho(true, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        this.batch.setProjectionMatrix(cam.combined);
        this.viewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);
        this.stage = new Stage(this.viewport, this.batch);

        TextureRegion reg = new TextureRegion(this.game.manager.getTexture("mainmenuscreen/background.png"));
        reg.flip(false, true);
        this.background = new Image(reg);
        this.background.setSize(this.viewport.getWorldWidth(), this.viewport.getWorldHeight());
        this.stage.addActor(this.background);

        this.skin1 = game.utility.getDefaultSkin();

        initButtons();
    }

    private TextButton createButton(Drawable up, Drawable down, BitmapFont font, Color color, String text, final Runnable listener) {
        TextButton.TextButtonStyle selectButtonstyle = new TextButton.TextButtonStyle();
        selectButtonstyle.down = down;
        selectButtonstyle.up = up;
        selectButtonstyle.font = font;
        selectButtonstyle.fontColor = color;
        TextButton button = new TextButton(text, selectButtonstyle);
        button.getLabel().setAlignment(Align.center);
        button.setName(text);
        if (listener != null) {
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    listener.run();
                }
            });
        }

        return button;
    }

    /**
     *
     */
    private void initButtons() {
        click = game.manager.get("click.wav", Sound.class);
        elements = new Table();
        elements.setWidth(stage.getViewport().getWorldWidth());
        BitmapFont font = game.utility.generateFont(52, true);

        Drawable soundYes = Preferences.getVolume() ? skin1.getDrawable("smallYellow") : skin1.getDrawable("smallRed");
        Drawable soundYesPressed = Preferences.getVolume() ? skin1.getDrawable("smallYellowPressed") : skin1.getDrawable("smallRedPressed");
        Drawable soundNo = (Preferences.getVolume()) ? skin1.getDrawable("smallRed") : skin1.getDrawable("smallYellow");
        Drawable soundNoPressed = (Preferences.getVolume()) ? skin1.getDrawable("smallRedPressed") : skin1.getDrawable("smallYellowPressed");

        Drawable notifYes = game.getPreferences().getNotif() ? skin1.getDrawable("smallYellow") : skin1.getDrawable("smallRed");
        Drawable notifYesPressed = game.getPreferences().getNotif() ? skin1.getDrawable("smallYellowPressed") : skin1.getDrawable("smallRedPressed");
        Drawable notifNo = (game.getPreferences().getNotif()) ? skin1.getDrawable("smallRed") : skin1.getDrawable("smallYellow");
        Drawable notifNoPressed = (game.getPreferences().getNotif()) ? skin1.getDrawable("smallRedPressed") : skin1.getDrawable("smallYellowPressed");

        elements.add(createButton(skin1.getDrawable("defButton"), skin1.getDrawable("defButton"), font, Color.WHITE, game.locale.get("option_back"), new Runnable() {
            @Override
            public void run() {
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        OptionScreen.this.game.setState(GameState.GS_GamePaused);
                        OptionScreen.this.game.backToPreviousScreen();
                        Preferences.getPrefs().flush();
                    }
                });
                stage.addActor(fade);
            }
        })).center().colspan(2);
        elements.row();

        if (isRestart) {
            elements.add(createButton(skin1.getDrawable("defButton"), skin1.getDrawable("defButton"), font, Color.WHITE, game.locale.get("option_restart"), new Runnable() {
                @Override
                public void run() {
                    mDialog();
                }
            })).center().colspan(2);

            elements.row();
        }

        notifYesbtn = createButton(notifYes, notifYesPressed, font, Color.WHITE, game.locale.get("yes"), new Runnable() {
            @Override
            public void run() {
                OptionScreen.this.game.getPreferences().setNotif(true);
                game.serviceCallback().setNotificationsEnabled(true);
                buttonPressed();
            }
        });
        elements.add(notifYesbtn).center();
        notifNobtn = createButton(notifNo, notifNoPressed, font, Color.WHITE, game.locale.get("no"), new Runnable() {
            @Override
            public void run() {
                OptionScreen.this.game.getPreferences().setNotif(false);
                game.serviceCallback().setNotificationsEnabled(false);
                buttonPressed();
            }
        });
        elements.add(notifNobtn).center();
        elements.row();

        elements.add(createButton(skin1.getDrawable("defButton"), skin1.getDrawable("defButton"), font, Color.WHITE, game.locale.get("option_notif"), null)).center().colspan(2);
        elements.row();

        soundYesbtn = createButton(soundYes, soundYesPressed, font, Color.WHITE, game.locale.get("yes"), new Runnable() {
            @Override
            public void run() {
                OptionScreen.this.game.getPreferences().setVolume(true);
                buttonPressed();

            }
        });
        elements.add(soundYesbtn).center();
        soundNobtn = createButton(soundNo, soundNoPressed, font, Color.WHITE, game.locale.get("no"), new Runnable() {
            @Override
            public void run() {
                OptionScreen.this.game.getPreferences().setVolume(false);
                buttonPressed();
            }
        });
        elements.add(soundNobtn).center();
        elements.row();

        elements.add(createButton(skin1.getDrawable("defButton"), skin1.getDrawable("defButton"), font, Color.WHITE, game.locale.get("option_sound"), null)).center().colspan(2);
        elements.row();

        elements.setOrigin(Align.center);
        elements.setPosition((stage.getViewport().getWorldWidth() - elements.getWidth()) / 2, 825);

        stage.addActor(elements);
        this.game.setState(GameState.GS_Option);
    }

    private void buttonPressed() {
        Drawable soundYes = Preferences.getVolume() ? skin1.getDrawable("smallYellow") : skin1.getDrawable("smallRed");
        Drawable soundYesPressed = Preferences.getVolume() ? skin1.getDrawable("smallYellowPressed") : skin1.getDrawable("smallRedPressed");
        Drawable soundNo = (Preferences.getVolume()) ? skin1.getDrawable("smallRed") : skin1.getDrawable("smallYellow");
        Drawable soundNoPressed = (Preferences.getVolume()) ? skin1.getDrawable("smallRedPressed") : skin1.getDrawable("smallYellowPressed");

        Drawable notifYes = game.getPreferences().getNotif() ? skin1.getDrawable("smallYellow") : skin1.getDrawable("smallRed");
        Drawable notifYesPressed = game.getPreferences().getNotif() ? skin1.getDrawable("smallYellowPressed") : skin1.getDrawable("smallRedPressed");
        Drawable notifNo = (game.getPreferences().getNotif()) ? skin1.getDrawable("smallRed") : skin1.getDrawable("smallYellow");
        Drawable notifNoPressed = (game.getPreferences().getNotif()) ? skin1.getDrawable("smallRedPressed") : skin1.getDrawable("smallYellowPressed");

        soundYesbtn.getStyle().up = soundYes;
        soundYesbtn.getStyle().down = soundYesPressed;
        soundNobtn.getStyle().up = soundNo;
        soundNobtn.getStyle().down = soundNoPressed;

        notifYesbtn.getStyle().up = notifYes;
        notifYesbtn.getStyle().down = notifYesPressed;
        notifNobtn.getStyle().up = notifNo;
        notifNobtn.getStyle().down = notifNoPressed;
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
        Gdx.gl.glClear(0);
        this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 0.033333335F));
        this.stage.draw();
        Preferences.getPrefs().flush();
    }

    public void show() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {
                Gdx.input.setInputProcessor(stage);
                stage.addListener(new InputHandler(game, stage));
            }
        });
        stage.addActor(fade);
    }

    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
        Gdx.input.setInputProcessor(this.stage);
    }

    public void hide() {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
    }

    public void mDialog() {
        BitmapFont font12 = game.utility.generateFont(48, true);
        Label.LabelStyle style = new Label.LabelStyle(font12, Color.BLACK);

        Label label1 = new Label(game.locale.get("reset_message"), style);
        label1.setWrap(true);
        label1.setAlignment(Align.center);
        style.fontColor = new Color(73.0f / 255, 36.0f / 255, 7.0f / 255, 1.0f);

        Skin tileSkin =  game.utility.getDefaultSkin();;
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = tileSkin.newDrawable("smallRed");
        textButtonStyle.down = tileSkin.newDrawable("smallRedPressed");
        textButtonStyle.font = font12;
        textButtonStyle.fontColor = Color.WHITE;

        TextButton.TextButtonStyle textButtonStyle2 = new TextButton.TextButtonStyle();
        textButtonStyle2.up = tileSkin.newDrawable("smallYellow");
        textButtonStyle2.down = tileSkin.newDrawable("smallYellowPressed");
        textButtonStyle2.font = font12;
        textButtonStyle2.fontColor = Color.WHITE;

        final Texture background = game.manager.getTexture("images/skin/messageBox.png");
        TextureRegion reg = new TextureRegion(background);
        reg.flip(false, true);

        TextButton btnOK = new TextButton(game.locale.get("yes"), textButtonStyle2);
        TextButton btnNO = new TextButton(game.locale.get("no"), textButtonStyle);

        final Dialog dialog = new Dialog("", tileSkin) {
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
                //game.setState(FlowergotchiGame.GameState.GS_GameProcess);
                game.restartGame();
            }
        });

        btnNO.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                dialog.hide();
                dialog.cancel();
                dialog.remove();
                //game.setState(FlowergotchiGame.GameState.GS_GameProcess);
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
        dialog.button(btnNO);
        dialog.align(Align.center);
        dialog.show(stage).setPosition((stage.getViewport().getWorldWidth() - background.getWidth()) / 2, 300);

        dialog.pack();
        this.stage.addActor(dialog);
    }
}
