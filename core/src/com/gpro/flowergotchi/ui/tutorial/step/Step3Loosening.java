package com.gpro.flowergotchi.ui.tutorial.step;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.ui.tutorial.TutorialStep;

public class Step3Loosening extends TutorialStep {
    private Image phone;

    public Step3Loosening(GameUI ui, TutorialPlayer player) {
        super(ui, player);
    }

    @Override
    public void startStep() {
        stepMessage("t3_top", "t3_message", "t3_continue");
        Texture texture1 = new Texture("shovel/shovelPhone.png");
        TextureRegion reg1 = new TextureRegion(texture1);
        reg1.flip(false, true);
        phone = new Image(reg1);
        phone.setSize(texture1.getWidth(), texture1.getHeight());
        phone.setOrigin(Align.top);
        phone.setAlign(Align.center);
        phone.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PlantDead))));
        phone.setPosition((player.getStage().getViewport().getWorldWidth() - phone.getWidth()) / 2, 100 + top.getHeight() * 2 + 2 * text.getHeight() + phone.getHeight());
        phone.addAction(Actions.forever(Actions.sequence(Actions.rotateTo(-15f, 0.75f, Interpolation.pow2In), Actions.rotateTo(15f, 0.75f, Interpolation.pow2In))));
        player.getStage().addActor(phone);

        ui.getUIButtons().get("shovel").moveBy(-200, 0);
        ui.getUIButtons().get("shovel").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2),
                Actions.forever(Actions.sequence(Actions.scaleTo(1.15f, 1.15f, 0.5f, Interpolation.pow2In), Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2Out)))));
        ui.getUIButtons().get("shovel").setVisible(true);
    }

    @Override
    public void stopStep() {
        top.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(top)));
        text.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(text)));
        selectButton.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                player.advance(TutorialPlayer.TutorialCallback.ShovelGameFinished);
            }
        }), Actions.removeActor(selectButton)));
        phone.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(phone)));
    }

    @Override
    public void prepare() {

    }
}
