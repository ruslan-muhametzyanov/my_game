package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.InputHandler;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.ui.GameUI;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

/**
 * Created by user on 11.04.2016.
 */
public class VictoryScreen extends DemoScreen {

    private final GameUI ui;
    private final StretchViewport fitViewport;
    private final SpriteBatch batch;
    FlowergotchiGame game;

    public VictoryScreen(FlowergotchiGame game, GameUI ui) {
        super(game);
        this.game = game;
        this.ui = ui;

        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(false, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        fitViewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        stage = new Stage(fitViewport, batch);

        init();
    }

    private void init() {
        final GameWorld world = game.getGameScreen().getWorld();

        TextureRegion reg = new TextureRegion(this.game.manager.getTexture("mainmenuscreen/background.png"));
        reg.flip(false, false);
        Image background = new Image(reg);
        background.setSize(this.fitViewport.getWorldWidth(), this.fitViewport.getWorldHeight());
        this.stage.addActor(background);

        Table t = new Table();

        final BitmapFont font12 = game.utility.generateFont(52, false);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font12;
        style.fontColor = Color.BLACK;

        Label mes = new Label(game.locale.get("flowergotchi_wingame"), style);
        mes.setAlignment(Align.center);
        mes.setWrap(true);
        t.add(mes).width(720).spaceBottom(20);
        t.row();
        mes = new Label(game.locale.format("flowergotchi_score", world.getActiveFlower().getIntVarVal(IntGameVariables.Var_Progress)), style);
        mes.setWrap(true);
        mes.setAlignment(Align.center);
        t.add(mes).width(720).spaceBottom(60);
        t.row();

        Label message = new Label(game.locale.get("fg_continue"), style);
        message.setPosition((stage.getViewport().getWorldWidth() - message.getWidth()) / 2, 220);
        message.setAlignment(Align.center);
        message.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.alpha(0.0f), Actions.delay(0.5f), Actions.alpha(1.0f), Actions.delay(0.5f))));
        t.add(message).width(720);

        ClickListener listener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                    @Override
                    public void run() {
                        ScoreboardScreen screen = new ScoreboardScreen(game, world.getActiveFlower().getName(), world.getActiveFlower().getIntVarVal(IntGameVariables.Var_Score));
                        game.setScreen(screen);
                    }
                });
                stage.addActor(fade);
                return true;
            }
        };
        stage.addListener(listener);
        ScrollPane scrollPane = new ScrollPane(t);
        scrollPane.setPosition(0, 50);
        scrollPane.setSize(720, 864);
        stage.addActor(scrollPane);
    }

    @Override
    public void show() {
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {
                Gdx.input.setInputProcessor(stage);
                stage.addListener(new InputHandler(game, stage));
                game.setState(GameState.GS_Victory);
            }
        });
        stage.addActor(fade);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {
        Gdx.app.log("GameScreen", "pause");
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
    }
}
