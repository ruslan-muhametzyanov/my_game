package com.gpro.flowergotchi.ui.tutorial.step;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.ui.tutorial.TutorialStep;

public class Step10Pause extends TutorialStep {
    private Image image;

    public Step10Pause(GameUI ui, TutorialPlayer player) {
        super(ui, player);
    }

    @Override
    public void startStep() {
        stepMessage("t10_top", "t10_message", "t10_continue");

        Texture texture = new Texture("buttons/pause.png");
        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.flip(true, true);
        image = new Image(textureRegion);
        image.setSize(texture.getWidth(), texture.getHeight());
        image.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        image.setPosition((player.getStage().getViewport().getWorldWidth() - image.getWidth()) / 2, 100 + top.getHeight() * 2 + text.getHeight() + 2 * image.getHeight());
        player.getStage().addActor(image);

        /*ui.getUIButtons().get("pause").moveBy(-200, 0);
        ui.getUIButtons().get("pause").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2),
                Actions.forever(Actions.sequence(Actions.scaleTo(1.15f, 1.15f, 0.5f, Interpolation.pow2In), Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2Out)))));
        ui.getUIButtons().get("pause").setVisible(true);
        ui.getUIButtons().get("pause").setTouchable(Touchable.disabled);*/
    }

    @Override
    public void stopStep() {
        top.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(top)));
        text.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(text)));
        selectButton.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(selectButton)));
        player.advance(TutorialPlayer.TutorialCallback.PauseGameFinished);
        ui.getUIButtons().get("pause").addAction(Actions.sequence(Actions.delay(1.0f), Actions.run(new Runnable() {
            @Override
            public void run() {
                ui.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.PauseGameFinished);
                ui.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.PauseGameFinished);
            }
        })));
        image.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(image)));
    }

    @Override
    public void prepare() {

    }
}
