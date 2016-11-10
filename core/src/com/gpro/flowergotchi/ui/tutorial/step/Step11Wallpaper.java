package com.gpro.flowergotchi.ui.tutorial.step;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.ui.tutorial.TutorialPlayer;
import com.gpro.flowergotchi.ui.tutorial.TutorialStep;

public class Step11Wallpaper extends TutorialStep {
    public Step11Wallpaper(GameUI ui, TutorialPlayer player) {
        super(ui, player);
    }

    @Override
    public void startStep() {
        stepMessage("t11_top", "t11_message", "t11_continue");
    }

    @Override
    public void stopStep() {
        top.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(top)));
        text.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.removeActor(text)));
        player.advance(TutorialPlayer.TutorialCallback.WallpaperGameFinished);
        selectButton.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                ui.getTutorialPlayer().callback(TutorialPlayer.TutorialCallback.WallpaperGameFinished);
                ui.getTutorialPlayer().requestAdvance(TutorialPlayer.TutorialCallback.WallpaperGameFinished);
            }
        }), Actions.removeActor(selectButton)));
    }

    @Override
    public void prepare() {

    }
}
