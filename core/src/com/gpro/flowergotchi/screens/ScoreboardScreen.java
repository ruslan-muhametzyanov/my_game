package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.gamelogic.InputHandler;
import com.gpro.flowergotchi.util.Rand;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user on 28.01.2016.
 */
public class ScoreboardScreen extends DemoScreen {
    private static final int scoreboardMax = 5;

    private FlowergotchiGame game;
    private Stage stage;
    private StretchViewport viewport;
    private BitmapFont font;
    private BitmapFont smallfont;
    private int newScore = -1;
    private int scoreIndex = -1;
    private String flowerName;

    private List<Score> scores;

    public ScoreboardScreen(FlowergotchiGame game, String flowerName, int newScore) {
        super(game);
        this.game = game;
        this.newScore = newScore;
        this.flowerName = flowerName;
        scores = new ArrayList<Score>();

        setupStageViewport();
        initScoreboard();
    }

    private void setupStageViewport() {
        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(true, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);

        SpriteBatch batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);

        viewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);
        stage = new Stage(viewport, batch);

        TextureRegion reg = new TextureRegion(game.manager.getTexture("mainmenuscreen/background.png"));
        reg.flip(false, true);
        Image background = new Image(reg);
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        font = game.utility.generateFont(52, true);
        smallfont = game.utility.generateFont(36, true);
    }

    private void initScoreboard() {
        TextureRegion reg = new TextureRegion(game.manager.getTexture("mainmenuscreen/background.png"));
        reg.flip(false, true);
        Image background = new Image(reg);
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);
        Label.LabelStyle smallstyle = new Label.LabelStyle(smallfont, Color.BLACK);

        Preferences prefs = com.gpro.flowergotchi.Preferences.getPrefs();

        for (int i = 0; i < scoreboardMax; ++i) {
            Integer score = prefs.getInteger("score" + String.valueOf(i), -1);
            if (score == -1) {
                score = Rand.randInt(i * 7500, i * 15000) + Rand.randInt(0, 999);
            }
            String scoreName = prefs.getString("scoreName" + String.valueOf(i), "");
            if (scoreName.equals("")) {
                scoreName = "Роза";
            }

            scores.add(new Score(score, scoreName));
            prefs.putInteger("score" + String.valueOf(i), score);
            prefs.putString("scoreName" + String.valueOf(i), scoreName);

        }

        Score ourScore = new Score(newScore, flowerName);
        if (newScore != -1) {
            scores.add(ourScore);
        }
        Collections.sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score o1, Score o2) {
                if (o1.score > o2.score) {
                    return 1;
                } else if (o1.score < o2.score) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        Collections.reverse(scores);
        scoreIndex = scores.indexOf(ourScore);

        Label top = new Label(game.locale.get("scoreboard_top"), style);
        top.setX((stage.getViewport().getWorldWidth() - top.getWidth()) / 2);
        top.setY(400);
        top.setAlignment(Align.center);
        stage.addActor(top);

        Label message = new Label(game.locale.get("click_back_to_continue"), smallstyle);
        message.setWidth(stage.getViewport().getWorldWidth());
        message.setWrap(true);
        message.setX((stage.getViewport().getWorldWidth() - message.getWidth()) / 2);
        message.setY(stage.getViewport().getWorldHeight() - message.getHeight() - 20);
        message.setAlignment(Align.center);
        stage.addActor(message);

        for (int i = 0; i < scoreboardMax; ++i) {
            Label scoreName = new Label(scores.get(i).name, smallstyle);
            scoreName.setX(100);
            scoreName.setY(400 + 50 * (i + 1));
            scoreName.setAlignment(Align.center);
            if (scoreIndex != -1 && scoreIndex == i) {
                scoreName.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.alpha(0.0f), Actions.delay(0.5f), Actions.alpha(1.0f), Actions.delay(0.5f))));

                prefs.putString("scoreName" + String.valueOf(i), scores.get(i).name);
            }
            stage.addActor(scoreName);

            Label score = new Label(String.valueOf(scores.get(i).score), smallstyle);
            score.setOrigin(Align.right);
            score.setAlignment(Align.right);
            score.setX(stage.getViewport().getWorldWidth() - score.getWidth() - 100);
            score.setY(400 + 50 * (i + 1));
            if (scoreIndex != -1 && scoreIndex == i) {
                score.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.alpha(0.0f), Actions.delay(0.5f), Actions.alpha(1.0f), Actions.delay(0.5f))));
                prefs.putInteger("score" + String.valueOf(i), scores.get(i).score);
            }
            stage.addActor(score);
        }
    }

    public void exitStat() {
        if (newScore != -1) {
            ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                @Override
                public void run() {
                    game.restartGame();
                }
            });
            stage.addActor(fade);
        } else {
            ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                @Override
                public void run() {
                    game.setState(GameState.GS_GamePaused);
                    game.backToPreviousScreen();
                }
            });
            stage.addActor(fade);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int newWidth, int newHeight) {
        super.resize(newWidth, newHeight);
        viewport.update(newWidth, newHeight);
    }

    @Override
    public void show() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {
                game.setState(GameState.GS_ScoreboardScreen);
                stage.addListener(new InputHandler(game, stage));
                Gdx.input.setInputProcessor(stage);
            }
        });
        stage.addActor(fade);
    }

    public class Score {
        public int score;
        public String name;

        public Score(int score, String name) {
            this.score = score;
            this.name = name;
        }
    }
}
