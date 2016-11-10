package com.gpro.flowergotchi.gamelogic.events;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

/**
 * Created by user on 26.01.2016.
 */
public class EventHelper {

    public void floatingMessage(final FlowergotchiGame game, Stage stage, final Runnable okAction, String image, String topLabel, String messageLabel, String okLabel) {
        BitmapFont font = game.utility.generateFont(52, true);
        BitmapFont smallfont = game.utility.generateFont(36, true);
        final Image blur = new Image(game.manager.getTexture("blur.png"));
        blur.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        stage.addActor(blur);

        Label.LabelStyle topStyle = new Label.LabelStyle(game.utility.generateFont(52, true), Color.WHITE);
        final Label top = new Label(game.locale.get(topLabel), topStyle);
        top.setWrap(true);
        top.setWidth(stage.getViewport().getWorldWidth());
        top.setAlignment(Align.center);
        top.setPosition((stage.getViewport().getWorldWidth() - top.getWidth()) / 2, 100);
        top.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        top.setScale(0);
        top.addAction(Actions.sequence(Actions.scaleBy(1, 1, 1.0f)));
        stage.addActor(top);

        Label.LabelStyle textStyle = new Label.LabelStyle(game.utility.generateFont(28, true), Color.WHITE);
        final Label text = new Label(game.locale.get(messageLabel), textStyle);
        text.setWrap(true);
        text.setWidth(stage.getViewport().getWorldWidth());
        text.setAlignment(Align.center);
        text.setPosition((stage.getViewport().getWorldWidth() - text.getWidth()) / 2, 100 + top.getHeight() * 2);
        text.layout();
        text.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        stage.addActor(text);

        Skin skin1 = new Skin(game.manager.get("images/skin/buttons.pack", TextureAtlas.class));
        TextButton.TextButtonStyle selectButtonstyle = new TextButton.TextButtonStyle();

        selectButtonstyle.down = skin1.getDrawable("defButtonPressed");
        selectButtonstyle.up = skin1.getDrawable("defButton");
        selectButtonstyle.font = game.utility.generateFont(52, true);
        selectButtonstyle.fontColor = Color.WHITE;
        final TextButton selectButton = new TextButton(game.locale.get(okLabel), selectButtonstyle);
        selectButton.getLabel().setAlignment(Align.center);
        selectButton.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        selectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                blur.remove();
                top.remove();
                text.remove();
                selectButton.remove();
                if (okAction != null) {
                    okAction.run();
                }
            }
        });
        selectButton.setPosition((stage.getViewport().getWorldWidth() - selectButton.getWidth()) / 2, 300);
        selectButton.addAction(Actions.sequence(Actions.scaleBy(1, 1, 1.0f)));
        stage.addActor(selectButton);
    }
}
