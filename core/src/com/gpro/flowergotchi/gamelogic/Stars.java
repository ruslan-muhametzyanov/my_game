package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;


public class Stars {

    private final GameUI ui;
    public boolean bClick, win;
    private long beginGame, endGame, timeCount;
    private FlowergotchiGame game;
    private Sound click;
    private Stage stage;
    private Games games;
    private int spiderCount;
    private int drops;
    private float badDropChanse;
    private Dialog dialogStar;
    private Skin tileSkinStar;


    public Stars(FlowergotchiGame game, GameUI ui, Stage stage, Games games) {
        this.game = game;
        this.stage = stage;
        this.games = games;
        this.ui = ui;
        bClick = false;

        click = game.manager.get("click.wav", Sound.class);

        InitDialog();
    }

    public void BeginGame() {
        beginGame = System.currentTimeMillis();
    }

    public void EndGame() {
        endGame = System.currentTimeMillis();
    }

    private long TimeCount() {
        this.timeCount = endGame - beginGame;
        return timeCount / 1000;
    }


    public void result() {
        if (!win) {
            resultTable(0);
        } else {
            switch (games) {
                case SpiderGame: {
                    if ((TimeCount() / spiderCount) < 7) {
                        resultTable(3);
                    } else if ((TimeCount() / spiderCount) < 10) {
                        resultTable(3);
                    } else if ((TimeCount() / spiderCount) >= 10) {
                        resultTable(3);
                    }
                }
                break;
                case ArcanoidGame: {
                    if (TimeCount() < 10) {
                        resultTable(3);
                    } else if (TimeCount() < 20) {
                        resultTable(2);
                    } else if (TimeCount() >= 20) {
                        resultTable(1);
                    }
                }
                break;
                case ShovelGame: {
                    if (TimeCount() < 10) {
                        resultTable(3);
                    } else if (TimeCount() < 20) {
                        resultTable(2);
                    } else if (TimeCount() >= 20) {
                        resultTable(1);
                    }
                }
                break;
                case WaterGame: {
                    if (((TimeCount() * (1 - badDropChanse)) / drops) < 0.75) {
                        resultTable(3);
                    } else if (((TimeCount() * (1 - badDropChanse)) / drops) < 1) {
                        resultTable(2);
                    } else if (((TimeCount() * (1 - badDropChanse)) / drops) >= 1) {
                        resultTable(1);
                    }
                }
                break;
            }
        }
    }

    private void InitDialog() {
        tileSkinStar = game.utility.getDefaultSkin();
        final Texture backgroundStar = game.manager.getTexture("images/skin/mediumMessageBox.png");


        TextButton btnOKStar = new TextButton(game.locale.get("ok"), tileSkinStar.get("default", TextButton.TextButtonStyle.class));
        btnOKStar.setPosition((backgroundStar.getWidth() - btnOKStar.getWidth()) / 2, 210);

        dialogStar = new Dialog("", tileSkinStar, "defaultMedium") {
            @Override
            public float getPrefWidth() {
                return backgroundStar.getWidth();
            }

            @Override
            public float getPrefHeight() {
                return backgroundStar.getHeight();
            }
        };
        dialogStar.setSize(backgroundStar.getWidth(), backgroundStar.getHeight());
        dialogStar.setModal(true);
        dialogStar.setMovable(false);
        dialogStar.setResizable(false);

        btnOKStar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                dialogStar.cancel();
                dialogStar.hide();
                dialogStar.remove();
                dialogStar = null;
                bClick = true;
            }
        });

        TextureRegion emptyStar = new TextureRegion(game.manager.getTexture("images/skin/emptyStar.png"));
        emptyStar.flip(false, true);
        for (int i = 1; i <= 3; i++) {
            Image star = new Image(emptyStar);
            star.setPosition((dialogStar.getWidth() / 4) * i - (star.getWidth()) / 2, 60);
            dialogStar.addActor(star);
        }

        TextureRegion myTexStar = new TextureRegion(backgroundStar);
        myTexStar.flip(false, true);
        myTexStar.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegionDrawable drawableStar = new TextureRegionDrawable(myTexStar);
        dialogStar.setBackground(drawableStar);


        dialogStar.addActor(btnOKStar);
        dialogStar.align(Align.center);
        dialogStar.show(stage).setPosition((stage.getViewport().getWorldWidth() - backgroundStar.getWidth()) / 2,
                (stage.getViewport().getWorldHeight() - backgroundStar.getHeight()) / 2);

        dialogStar.pack();
        dialogStar.setVisible(false);
        dialogStar.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_ScreenShot))));
        stage.addActor(dialogStar);
    }

    private void resultTable(int count) {
        dialogStar.setVisible(true);
        Statistic.newStar = count;

        TextureRegion fullStar = new TextureRegion(game.manager.getTexture("images/skin/fullStar.png"));
        fullStar.flip(false, true);

        if (count > 0) {
            Sound start = game.manager.get("sounds/star.ogg", Sound.class);
            start.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
        }
        for (int i = 1; i <= count; i++) {
            Image star = new Image(fullStar);
            star.setPosition(((dialogStar.getWidth() / 4) * i - star.getWidth() / 2) - 5, 47);
            star.setOrigin(Align.center);
            star.setScale(0.0f);
            star.addAction(Actions.sequence(Actions.delay(0.5f * i - 0.5f), Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2In)));
            dialogStar.addActor(star);
        }
    }

    public boolean stop() {
        return bClick;
    }

    public void gameWin(boolean win) {
        this.win = win;
    }

    public void spiderCount(int spiderCount) {
        this.spiderCount = spiderCount;
    }

    public void waterCount(int drops, float badDropChanse) {
        this.drops = drops;
        this.badDropChanse = badDropChanse;
    }

    public enum Games {
        SpiderGame,
        WaterGame,
        ArcanoidGame,
        ShovelGame
    }
}
