package com.gpro.flowergotchi.ui.tutorial.step;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.ui.tutorial.TutorialStep;

import java.util.Map;

public class Step1Introduction extends TutorialStep {
    public Step1Introduction(GameUI ui, TutorialPlayer player) {
        super(ui, player);
    }

    @Override
    public void prepare() {
        for (Map.Entry<String, Actor> b : ui.getUIButtons().entrySet()) {
            b.getValue().setVisible(false);
        }
    }

    @Override
    public void startStep() {
        stepMessage("t1_top", null, "t1_continue");
    }

    @Override
    public void stopStep() {
        top.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(top)));
        selectButton.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                player.advance(null);
            }
        }), Actions.removeActor(selectButton)));
    }
}
