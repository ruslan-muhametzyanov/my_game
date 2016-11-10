package com.gpro.flowergotchi.ui.tutorial.step;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.ui.tutorial.TutorialStep;

public class Step9Statistic extends TutorialStep {

    public Step9Statistic(GameUI ui, TutorialPlayer player) {
        super(ui, player);
    }

    @Override
    public void startStep() {
        stepMessage("t9_top", "t9_message", "t9_continue");

        ui.getUIButtons().get("statistic").moveBy(-200, 0);
        ui.getUIButtons().get("statistic").addAction(Actions.sequence(Actions.moveBy(200, 0, 1.5f, Interpolation.pow2),
                Actions.forever(Actions.sequence(Actions.scaleTo(1.15f, 1.15f, 0.5f, Interpolation.pow2In), Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.pow2Out)))));
        ui.getUIButtons().get("statistic").setVisible(true);
    }

    @Override
    public void stopStep() {
        top.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(top)));
        text.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(text)));
        selectButton.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                player.advance(TutorialPlayer.TutorialCallback.StatisticGameFinished);
            }
        }), Actions.removeActor(selectButton)));
    }

    @Override
    public void prepare() {

    }
}
