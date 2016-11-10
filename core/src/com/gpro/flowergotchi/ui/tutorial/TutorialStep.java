package com.gpro.flowergotchi.ui.tutorial;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;

/**
 * Created by user on 12.01.2016.
 */
public abstract class TutorialStep {
    protected final Sound click;
    protected final Skin skin;
    protected GameUI ui;
    protected FlowergotchiGame game;
    protected TutorialPlayer player;
    protected Label top;
    protected TextButton selectButton;
    protected Label text;

    public TutorialStep(GameUI ui, TutorialPlayer player) {
        this.ui = ui;
        this.game = player.getGame();
        this.player = player;
        click = player.getManager().get("click.wav", Sound.class);
        this.skin = game.utility.getDefaultSkin();
    }

    public void stepMessage(String topText, String mainText, String buttonText) {
        int yoffset = 100;
        if (topText != null) {
            top = new Label(player.getGame().locale.get(topText),
                    skin.get("top", Label.LabelStyle.class));
            top.setWrap(true);
            top.setWidth(player.getStage().getViewport().getWorldWidth());
            top.setAlignment(Align.top);
            top.setPosition((player.getStage().getViewport().getWorldWidth() - top.getWidth()) / 2, yoffset);
            top.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
            player.getStage().addActor(top);
            yoffset += top.getHeight() * 2.5f;
        }

        if (mainText != null) {
            text = new Label(player.getGame().locale.get(mainText), skin.get("small", Label.LabelStyle.class));
            text.setWrap(true);
            text.setWidth(player.getStage().getViewport().getWorldWidth());
            text.setAlignment(Align.top);
            text.setPosition((player.getStage().getViewport().getWorldWidth() - text.getWidth()) / 2, yoffset);
            text.layout();
            text.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
            player.getStage().addActor(text);
            yoffset += text.getHeight();
        }

        if (buttonText != null) {
            selectButton = new TextButton(player.getGame().locale.get(buttonText), skin.get("defButton", TextButton.TextButtonStyle.class));
            selectButton.getLabel().setAlignment(Align.center);
            selectButton.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
            selectButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    stopStep();
                }
            });
            selectButton.setPosition((player.getStage().getViewport().getWorldWidth() - selectButton.getWidth()) / 2, yoffset + selectButton.getHeight());
            player.getStage().addActor(selectButton);
        }


    }

    public abstract void startStep();

    public abstract void stopStep();

    public abstract void prepare();
}
