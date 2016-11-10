package com.gpro.flowergotchi.ui.tutorial.step;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.ui.tutorial.TutorialStep;

public class Step6ScreenShot extends TutorialStep {
    public Step6ScreenShot(GameUI ui, TutorialPlayer player) {
        super(ui, player);
    }

    @Override
    public void startStep() {
        stepMessage("t6_top", "t6_message", "t6_continue");

        ui.getUIButtons().get("camera").moveBy(-200, 0);
        ui.getUIButtons().get("camera").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2),
                Actions.forever(Actions.sequence(Actions.scaleTo(1.15f, 1.15f, 0.5f, Interpolation.pow2In), Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2Out)))));
        ui.getUIButtons().get("camera").setVisible(true);
    }

    @Override
    public void stopStep() {
        top.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(top)));
        text.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(text)));
        selectButton.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(selectButton)));
        player.advance(TutorialPlayer.TutorialCallback.CameraGameFinished);
    }

    @Override
    public void prepare() {

    }
}
