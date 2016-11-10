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
import com.gpro.flowergotchi.xml.XmlFileParserEnvironments;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

import java.util.List;

class EnvironmentSelectScreen extends DemoScreen {
    private final FlowergotchiGame game;
    private String[] labels;
    private int envCount;
    private final Stage stage;
    private final StretchViewport fitViewport;
    private final SpriteBatch batch;
    private Image texture1;
    private ImageButton scrollleft;
    private ImageButton scrollright;
    private TextButton message;
    private ScrollPane scrollPane;
    private int scrollNum = 0;
    private boolean isInitialized = false;

    public EnvironmentSelectScreen(final FlowergotchiGame game) {
        super(game);
        this.game = game;

        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(false, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        fitViewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        stage = new Stage(fitViewport, batch);
    }

    private void init(final FlowergotchiGame game) {
        final Sound click = game.manager.get("click.wav", Sound.class);

        loadGraphics();

        texture1 = new Image(game.manager.getTexture("selectscreens/selectenv.png"));


        XmlFileParserEnvironments xmlParser = new XmlFileParserEnvironments("xml/Environments.xml");
        final List<Background.Parameters> environments = xmlParser.parseEnvTextures();

        envCount = environments.size();
        Texture[] envTextures = new Texture[envCount];
        TextureRegion[] envTexRegions = new TextureRegion[envCount];
        ImageButton.ImageButtonStyle[] buttonStyles = new ImageButton.ImageButtonStyle[envCount];
        ImageButton[] buttons = new ImageButton[envCount];
        labels = new String[envCount];


        Table t = new Table();

        Skin skin1 = game.utility.getDefaultSkin();

        stage.addActor(texture1);

        scrollleft = new ImageButton(new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_left.png"))),
                new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_left_pressed.png"))));
        scrollright = new ImageButton(new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_right.png"))),
                new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_right_pressed.png"))));
        scrollleft.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                scrollPane.setScrollX(scrollPane.getScrollX() - 486);
                --scrollNum;
                if (scrollNum == 0) {
                    scrollleft.setVisible(false);
                }
                scrollright.setVisible(true);
                changeLabel(labels[scrollNum]);
                return true;
            }
        });
        scrollright.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                scrollPane.setScrollX(scrollPane.getScrollX() + 486);
                ++scrollNum;
                if (scrollNum == envCount - 1) {
                    scrollright.setVisible(false);
                }
                scrollleft.setVisible(true);
                changeLabel(labels[scrollNum]);
                return true;
            }
        });

        scrollleft.setVisible(false);

        final EnvironmentSelectScreen s = this;
        for (int i = 0; i < envCount; ++i) {
            envTextures[i] = game.manager.get(environments.get(i).back.get(0), Texture.class);//new Texture(environments.get(i).getSecond());
            envTexRegions[i] = new TextureRegion(envTextures[i]);
            buttonStyles[i] = new ImageButton.ImageButtonStyle();

            buttonStyles[i].imageUp = new TextureRegionDrawable(envTexRegions[i]);
            buttonStyles[i].imageDown = new TextureRegionDrawable(envTexRegions[i]);
            buttons[i] = new ImageButton(buttonStyles[i]);
            t.add(buttons[i]).width(486).height(864);
            final Background.Parameters p = environments.get(i);
            labels[i] = game.locale.get(p.name);
            ChangeListener l = new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {

                    click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                        @Override
                        public void run() {
                            s.game.addScreen(new PotSelectScreen(s.game, p));
                        }
                    });
                    stage.addActor(fade);

                }
            };
            buttons[i].addListener(l);
        }

        scrollPane = new ScrollPane(t);
        scrollPane.setPosition(117, 270);
        scrollPane.setSize(486, 864);
        scrollleft.setPosition(0, 260);
        scrollleft.setSize(90, 960);
        scrollright.setPosition(630, 260);
        scrollright.setSize(90, 960);

        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setFlickScroll(false);

        stage.addActor(scrollPane);
        stage.addActor(scrollleft);
        stage.addActor(scrollright);

        final BitmapFont font12 = game.utility.generateFont(52, false);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font12;
        style.fontColor = Color.WHITE;
        style.down = skin1.getDrawable("tip");
        style.up = skin1.getDrawable("tip");

        message = new TextButton("", style);
        message.setText(game.locale.get("choose_place"));
        message.getLabel().setWrap(true);
        message.getLabel().setAlignment(Align.center);
        message.setSize(skin1.getDrawable("tip").getMinWidth(), skin1.getDrawable("tip").getMinHeight());
        message.getLabelCell().pad(30, 10, 0, 10);
        message.setPosition((stage.getViewport().getWorldWidth() - message.getWidth()) / 2, 30);
        message.getLabel().setColor(message.getColor().r, message.getColor().g, message.getColor().b, 0.0f);
        message.getLabel().addAction(Actions.sequence(Actions.alpha(1.0f, 0.5f), Actions.delay(1.5f), Actions.alpha(0.0f, 1.0f), Actions.run(new Runnable() {
            @Override
            public void run() {
                message.setText(labels[scrollNum]);
            }
        }), Actions.alpha(1.0f, 0.5f)));

        stage.addActor(message);

        isInitialized = true;
    }

    private void changeLabel(final String mes) {
        message.getLabel().clearActions();
        message.getLabel().addAction(Actions.sequence(Actions.alpha(0.0f), Actions.run(new Runnable() {
            @Override
            public void run() {
                message.setText(mes);
            }
        }), Actions.alpha(1.0f, 0.5f)));
    }

    private void loadGraphics() {
        XmlFileParserEnvironments xmlParser = new XmlFileParserEnvironments("xml/Environments.xml");
        List<Background.Parameters> environments = xmlParser.parseEnvTextures();
        int envCount = environments.size();
        for (int i = 0; i < envCount; ++i) {
            game.manager.load(environments.get(i).back.get(0), Texture.class);
        }
        game.manager.finishLoading();
    }

    private void unloadGraphics() {
        texture1.remove();
        game.manager.unload("selectscreens/selectenv.png");
        XmlFileParserEnvironments xmlParser = new XmlFileParserEnvironments("xml/Environments.xml");
        List<Background.Parameters> environments = xmlParser.parseEnvTextures();
        int envCount = environments.size();
        for (int i = 0; i < envCount; ++i) {
            game.manager.unload(environments.get(i).back.get(0));
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
            init(game);
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
        stage.clear();
        unloadGraphics();
    }
}
