package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.InputHandler;
import com.gpro.flowergotchi.util.Pair;
import com.gpro.flowergotchi.xml.XmlFileParserFlowers;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;

import java.util.List;

class FlowerSelectScreen extends DemoScreen {
    private final FlowergotchiGame game;
    private final String pot;
    private final Background.Parameters param;
    private int flowerCount;
    private final Stage stage;
    private final StretchViewport fitViewport;
    private final SpriteBatch batch;
    private ImageButton scrollleft;
    private ImageButton scrollright;
    private ScrollPane scrollPane;
    private int scrollNum = 0;
    private boolean inputMode = false;
    private boolean isInitialized = false;

    public FlowerSelectScreen(final FlowergotchiGame game, final Background.Parameters param, final String pot) {
        super(game);
        this.game = game;
        this.param = param;
        this.pot = pot;

        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(false, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        fitViewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        stage = new Stage(fitViewport, batch);
    }

    private void loadGraphics() {
        XmlFileParserFlowers xmlFlowerParser = new XmlFileParserFlowers("xml/Flowers.xml");
        List<Pair<String, Plant.Parameters>> flowers = xmlFlowerParser.parseFlowers();
        int flowerCount = flowers.size();
        for (int i = 0; i < flowerCount; ++i) {
            game.manager.load(flowers.get(i).getFirst(), Texture.class);
        }
        game.manager.finishLoading();
    }

    private void unloadGraphics() {
        XmlFileParserFlowers xmlFlowerParser = new XmlFileParserFlowers("xml/Flowers.xml");
        List<Pair<String, Plant.Parameters>> flowers = xmlFlowerParser.parseFlowers();
        int flowerCount = flowers.size();
        for (int i = 0; i < flowerCount; ++i) {
            game.manager.unload(flowers.get(i).getFirst());
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
            init();
        }
        ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeIn, new Runnable() {
            @Override
            public void run() {
                game.setState(GameState.GS_SelectScreen);
                stage.addListener(new InputHandler(game, stage));
                Gdx.input.setInputProcessor(stage);
            }
        });
        stage.addActor(fade);
    }

    private void init() {
        loadGraphics();

        final Sound click = game.manager.get("click.wav", Sound.class);


        Image background = new Image(game.manager.getTexture("selectscreens/selectflower.png"));

        XmlFileParserFlowers xmlParser = new XmlFileParserFlowers("xml/Flowers.xml");
        List<Pair<String, Plant.Parameters>> flowers = xmlParser.parseFlowers();

        flowerCount = flowers.size();
        Texture[] flowerTextures = new Texture[flowerCount];
        TextureRegion[] flowerTexRegions = new TextureRegion[flowerCount];
        ImageButton.ImageButtonStyle[] buttonStyles = new ImageButton.ImageButtonStyle[flowerCount];
        ImageButton[] buttons = new ImageButton[flowerCount];

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
                scrollPane.setScrollX(scrollPane.getScrollX() - 486);

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
                scrollPane.setScrollX(scrollPane.getScrollX() + 486);
                ++scrollNum;
                if (scrollNum == flowerCount - 1) {
                    scrollright.setVisible(false);
                }
                scrollleft.setVisible(true);
                return true;
            }
        });

        for (int i = 0; i < flowerCount; ++i) {
            flowerTextures[i] = game.manager.get(flowers.get(i).getFirst(), Texture.class);
            flowerTexRegions[i] = new TextureRegion(flowerTextures[i]);

            final String flower = flowers.get(i).getSecond().flowerClass;
            buttonStyles[i] = new ImageButton.ImageButtonStyle();

            buttonStyles[i].imageUp = new TextureRegionDrawable(flowerTexRegions[i]);
            buttonStyles[i].imageDown = new TextureRegionDrawable(flowerTexRegions[i]);
            buttons[i] = new ImageButton(buttonStyles[i]);
            buttons[i].addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, -20, 0.5f, Interpolation.pow2), Actions.moveBy(0, 20, 0.5f, Interpolation.pow2))));
            t.add(buttons[i]).width(486).height(864);
            final Plant.Parameters p = flowers.get(i).getSecond();
            final ImageButton imgbutton = buttons[i];
            final ChangeListener l = new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    if (!inputMode) {
                        click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                        Input.TextInputListener listener = new Input.TextInputListener() {
                            @Override
                            public void input(final String text) {
                                ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                                    @Override
                                    public void run() {
                                        Plant.Parameters flowerParams = new Plant.Parameters(flower, text);
                                        game.setScreen(new LoadingGameScreen(game, param, pot, flowerParams, true, false));

                                    }
                                });
                                stage.addActor(fade);

                            }

                            @Override
                            public void canceled() {
                                inputMode = false;
                            }
                        };
                        Gdx.input.getTextInput(listener, game.locale.get("flower_dialog"), game.locale.get(p.flowerName), game.locale.get("flower_dialog"));
                        inputMode = true;
                    }
                }
            };
            imgbutton.addListener(l);
        }

        scrollleft.setVisible(false);

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

        Skin skin1 = game.utility.getDefaultSkin();

        final BitmapFont font12 = game.utility.generateFont(52, false);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font12;
        style.fontColor = Color.WHITE;
        style.down = skin1.getDrawable("tip");
        style.up = skin1.getDrawable("tip");

        TextButton message = new TextButton("", style);
        message.setText(game.locale.get("choose_flower"));
        message.getLabel().setWrap(true);
        message.getLabel().setAlignment(Align.center);
        message.setSize(skin1.getDrawable("tip").getMinWidth(), skin1.getDrawable("tip").getMinHeight());
        message.getLabelCell().pad(30, 10, 0, 10);
        message.setPosition((stage.getViewport().getWorldWidth() - message.getWidth()) / 2, 30);
        stage.addActor(message);

        isInitialized = true;
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