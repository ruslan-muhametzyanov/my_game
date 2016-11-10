package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.screens.GameMenuScreen;
import com.gpro.flowergotchi.screens.ScoreboardScreen;
import com.gpro.flowergotchi.screens.ScreenFade;
import com.gpro.flowergotchi.screens.ShopScreen;

public class InputHandler extends InputListener {
    private final FlowergotchiGame game;
    private final Stage stage;

    public InputHandler(FlowergotchiGame game, Stage stage) {
        this.game = game;
        this.stage = stage;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (stage == null) {
            throw new NullPointerException();
        }
        // Check if stage is currently in fading mode
        for (Actor a : stage.getActors()) {
            if (ScreenFade.class.isInstance(a)) {
                return false;
            }
        }
        if (keycode == Input.Keys.BACK) {
            if (game.getState() == GameState.GS_GameProcess) {
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_GamePaused);
                        game.addScreen(new GameMenuScreen(game));
                    }
                });
                stage.addActor(fade);
            } else //noinspection StatementWithEmptyBody,StatementWithEmptyBody
                if (game.getState() == GameState.GS_GameInGamePause) {
            } else if (game.getState() == GameState.GS_ScoreboardScreen) {
                Gdx.input.setInputProcessor(null);
                ((ScoreboardScreen) game.getScreen()).exitStat();
            } else if (game.getState() == GameState.GS_GameStatistics) {
                    //Gdx.input.setInputProcessor(null);
                    //((GameMenuScreen) game.getScreen()).hideStatistics(this);
                } else if (game.getState() == GameState.GS_Victory) {

            } else if (game.getState() == GameState.GS_GamePaused) {
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_GameProcess);
                        game.backToPreviousScreen();
                    }
                });
                stage.addActor(fade);
            } else if (game.getState() == GameState.GS_Option) {
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_GamePaused);
                        game.backToPreviousScreen();
                    }
                });
                stage.addActor(fade);
            } else if (game.getState() == GameState.GS_ShopScreen) {
                ((ShopScreen) game.getScreen()).backOneCategory();
            } else if (game.getState() == GameState.GS_About) {
                    ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                        @Override
                        public void run() {
                            game.setState(GameState.GS_GamePaused);
                            game.backToPreviousScreen();
                        }
                    });
                    stage.addActor(fade);
            } else if (game.getState() == GameState.GS_GameLight
                    || game.getState() == GameState.GS_GameWater) {
                game.setState(GameState.GS_GameProcess);
                game.backToPreviousScreen();
            } else if (game.getState() == GameState.GS_SelectScreen) {
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        game.setState(GameState.GS_Menu);
                        game.backToPreviousScreen();
                    }
                });
                stage.addActor(fade);
            } else if (game.getState() == GameState.GS_Menu) {
                Gdx.app.exit();
            } else {
                game.backToPreviousScreen();
            }

        }
        return super.keyDown(event, keycode);
    }
}
