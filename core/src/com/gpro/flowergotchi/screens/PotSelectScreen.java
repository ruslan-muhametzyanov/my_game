package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.InputHandler;
import com.gpro.flowergotchi.util.Pair;
import com.gpro.flowergotchi.xml.XmlFileParserPots;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

import java.util.List;

class PotSelectScreen extends DemoScreen {
    private final FlowergotchiGame game;
    private final Stage stage;
    private final StretchViewport fitViewport;
    private final SpriteBatch batch;
    private final Background.Parameters param;
    private int potCount;
    private ImageButton scrollleft;
    private ImageButton scrollright;
    private ScrollPane scrollPane;
    private int scrollNum = 0;
    private boolean isInitialized = false;

    public PotSelectScreen(final FlowergotchiGame game, final Background.Parameters param) {
        super(game);
        this.game = game;
        this.param = param;

        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(false, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        fitViewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        stage = new Stage(fitViewport, batch);

    }

    private void init(final FlowergotchiGame game, final Background.Parameters param) {
        loadGraphics();

        final Sound click = game.manager.get("click.wav", Sound.class);


        Image background = new Image(game.manager.getTexture("selectscreens/selectpot.png"));


        XmlFileParserPots xmlParser = new XmlFileParserPots("xml/Pots.xml");
        List<Pair<String, String>> pots = xmlParser.parsePots();

        potCount = pots.size();
        Texture[] potTextures = new Texture[potCount];
        TextureRegion[] potTexRegions = new TextureRegion[potCount];
        ImageButton.ImageButtonStyle[] buttonStyles = new ImageButton.ImageButtonStyle[potCount];
        ImageButton[] buttons = new ImageButton[potCount];


        Table t = new Table();

        stage.addActor(background);

        scrollleft = new ImageButton(new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_left.png"))),
                new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_left_pressed.png"))));
        scrollright = new ImageButton(new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_right.png"))),
                new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_right_pressed.png"))));
        scrollleft.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                scrollPane.setScrollX(scrollPane.getScrollX() - 540);
                --scrollNum;
                if (scrollNum == 0) {
                    scrollleft.setVisible(false);
                }
                scrollright.setVisible(true);
                return true;
            }
        });
        scrollright.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                scrollPane.setScrollX(scrollPane.getScrollX() + 540);
                ++scrollNum;
                if (scrollNum == potCount - 1) {
                    scrollright.setVisible(false);
                }
                scrollleft.setVisible(true);
                return true;
            }
        });

        scrollleft.setVisible(false);

        for (int i = 0; i < potCount; ++i) {
            potTextures[i] = game.manager.get(pots.get(i).getSecond(), Texture.class);
            potTexRegions[i] = new TextureRegion(potTextures[i]);
            final String pot = pots.get(i).getFirst();
            buttonStyles[i] = new ImageButton.ImageButtonStyle();

            buttonStyles[i].imageUp = new TextureRegionDrawable(potTexRegions[i]);
            buttonStyles[i].imageDown = new TextureRegionDrawable(potTexRegions[i]);
            buttons[i] = new ImageButton(buttonStyles[i]);
            buttons[i].addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, -20, 0.5f, Interpolation.pow2), Actions.moveBy(0, 20, 0.5f, Interpolation.pow2))));
            t.add(buttons[i]).width(540).height(960);
            ChangeListener l = new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                        @Override
                        public void run() {
                            game.addScreen(new FlowerSelectScreen(game, param, pot));

                        }
                    });
                    stage.addActor(fade);

                }
            };
            buttons[i].addListener(l);
        }

        scrollPane = new ScrollPane(t);
        scrollPane.setPosition(90, 215);
        scrollPane.setSize(540, 960);
        scrollleft.setPosition(0, 260);
        scrollleft.setSize(90, 960);
        scrollright.setPosition(630, 260);
        scrollright.setSize(90, 960);

        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setFlickScroll(false);

        stage.addActor(scrollPane);
        stage.addActor(scrollleft);
        stage.addActor(scrollright);

        Skin skin1 = game.utility.getDefaultSkin();;

        final BitmapFont font12 = game.utility.generateFont(52, false);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font12;
        style.fontColor = Color.WHITE;
        style.down = skin1.getDrawable("tip");
        style.up = skin1.getDrawable("tip");

        TextButton message = new TextButton("", style);
        message.setText(game.locale.get("choose_pot"));
        message.getLabel().setWrap(true);
        message.getLabel().setAlignment(Align.center);
        message.setSize(skin1.getDrawable("tip").getMinWidth(), skin1.getDrawable("tip").getMinHeight());
        message.getLabelCell().pad(30, 10, 0, 10);
        message.setPosition((stage.getViewport().getWorldWidth() - message.getWidth()) / 2, 30);

        stage.addActor(message);

        isInitialized = true;
    }

    private void loadGraphics() {

        XmlFileParserPots xmlPotParser = new XmlFileParserPots("xml/Pots.xml");
        List<Pair<String, String>> pots = xmlPotParser.parsePots();
        int potCount = pots.size();
        for (int i = 0; i < potCount; ++i) {
            game.manager.load(pots.get(i).getSecond(), Texture.class);
        }
        game.manager.finishLoading();
    }

    private void unloadGraphics() {
        game.manager.unload("selectscreens/selectpot.png");
        XmlFileParserPots xmlPotParser = new XmlFileParserPots("xml/Pots.xml");
        List<Pair<String, String>> pots = xmlPotParser.parsePots();
        int potCount = pots.size();
        for (int i = 0; i < potCount; ++i) {
            game.manager.unload(pots.get(i).getSecond());
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
    public void show() {
        if (!isInitialized) {
            init(game, param);
        }
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {
                game.setState(GameState.GS_SelectScreen);
                Gdx.input.setInputProcessor(stage);
                stage.addListener(new InputHandler(game, stage));
            }
        });
        stage.addActor(fade);
    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        unloadGraphics();
    }
}
