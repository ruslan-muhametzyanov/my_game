package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.GameState;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.InputHandler;
import com.gpro.flowergotchi.shop.Category;
import com.gpro.flowergotchi.shop.Item;
import com.gpro.flowergotchi.shop.Purchase;
import com.gpro.flowergotchi.util.Utility;
import com.gpro.flowergotchi.xml.XmlFileParserPurchases;

import net.peakgames.libgdx.stagebuilder.core.demo.DemoScreen;
import net.peakgames.libgdx.stagebuilder.core.util.Utils;

/**
 * Created by user on 15.02.2016.
 */
public class ShopScreen extends DemoScreen {
    public static final int ScrollWidth = 486;
    private final FlowergotchiGame game;
    private final StretchViewport fitViewport;
    private final SpriteBatch batch;
    private ImageButton scrollleft;
    private ImageButton scrollright;
    private ScrollPane scrollPane;
    private TextButton message;
    private int scrollNum = 0;
    private boolean isInitialized = false;
    private String[] labels;
    private Category curCategory;
    private Sound click;
    private Table t;
    private Skin skin1;
    private BitmapFont font12;

    public ShopScreen(FlowergotchiGame game) {
        super(game);
        this.game = game;

        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(true, FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        fitViewport = new StretchViewport(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight, cam);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        stage = new Stage(fitViewport, batch);
    }

    private void init(final FlowergotchiGame game) {
        click = game.manager.get("click.wav", Sound.class);

        TextureRegion reg = new TextureRegion(game.manager.getTexture("selectscreens/selectpur.png"));
        reg.flip(false, true);
        Image texture1 = new Image(reg);

        XmlFileParserPurchases xmlParser = new XmlFileParserPurchases("xml/Purchases.xml");
        final Category puchases = xmlParser.parsePurchases();
        curCategory = puchases;

        t = new Table();
        skin1 = game.utility.getDefaultSkin();

        stage.addActor(texture1);

        scrollleft = new ImageButton(new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_left.png"))),
                new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_left_pressed.png"))));
        scrollright = new ImageButton(new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_right.png"))),
                new TextureRegionDrawable(new TextureRegion(game.manager.getTexture("selectscreens/menu_right_pressed.png"))));

        scrollleft.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                scrollPane.setScrollX(scrollPane.getScrollX() - ScrollWidth);
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
                scrollPane.setScrollX(scrollPane.getScrollX() + ScrollWidth);
                ++scrollNum;
                if (scrollNum == curCategory.getSize() - 1) {
                    scrollright.setVisible(false);
                }
                scrollleft.setVisible(true);
                changeLabel(labels[scrollNum]);
                return true;
            }
        });

        scrollleft.setVisible(false);

        fillButtons(puchases);

        scrollPane = new ScrollPane(t);
        scrollPane.setPosition(117, 100);
        scrollPane.setSize(ScrollWidth, 864);
        scrollleft.setPosition(0, 100);
        scrollleft.setSize(90, 960);
        scrollright.setPosition(630, 100);
        scrollright.setSize(90, 960);

        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setFlickScroll(false);

        stage.addActor(scrollPane);
        stage.addActor(scrollleft);
        stage.addActor(scrollright);

        font12 = game.utility.generateFont(52, true);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font12;
        style.fontColor = Color.WHITE;
        style.down = skin1.getDrawable("tip");
        style.up = skin1.getDrawable("tip");

        message = new TextButton("", style);
        message.setText(game.locale.get("choose_purchase"));
        message.getLabel().setWrap(true);
        message.getLabel().setAlignment(Align.center);
        skin1.getAtlas().findRegion("tip").flip(false, true);
        message.setSize(skin1.getDrawable("tip").getMinWidth(), skin1.getDrawable("tip").getMinHeight());
        message.getLabelCell().pad(30, 10, 0, 10);
        message.setPosition((stage.getViewport().getWorldWidth() - message.getWidth()) / 2, 1250 - message.getHeight());
        message.getLabel().setColor(message.getColor().r, message.getColor().g, message.getColor().b, 0.0f);
        message.getLabel().addAction(Actions.sequence(Actions.alpha(1.0f, 0.5f), Actions.delay(1.5f), Actions.alpha(0.0f, 1.0f), Actions.run(new Runnable() {
            @Override
            public void run() {
                message.setText(labels[scrollNum]);
            }
        }), Actions.alpha(1.0f, 0.5f)));
        stage.addActor(message);

        if (!game.getPurchaseManager().isRestored()) {
            try {
                game.getPurchaseManager().restorePurchases();
                game.getPurchaseManager().restoreSuccess();
            } catch (GdxRuntimeException e) {
                e.printStackTrace();
                game.utility.fullMessage(game, stage, skin1, click, font12, "shop_unable", true, null);
            }
        }

        t.setRotation(180);

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

    private void fillButtons(Category puchases) {
        Texture[] envTextures = new Texture[puchases.getSize()];
        TextureRegion[] envTexRegions = new TextureRegion[puchases.getSize()];
        ImageButton.ImageButtonStyle[] buttonStyles = new ImageButton.ImageButtonStyle[puchases.getSize()];
        ImageButton[] buttons = new ImageButton[puchases.getSize()];
        labels = new String[puchases.getSize()];
        t.clear();
        for (int i = 0; i < curCategory.getSize(); ++i) {
            final Item e = curCategory.getAt(i);
            envTextures[i] = game.manager.getTexture(e.image);
            envTexRegions[i] = new TextureRegion(envTextures[i]);
            envTexRegions[i].flip(false, true);
            buttonStyles[i] = new ImageButton.ImageButtonStyle();

            buttonStyles[i].imageUp = new TextureRegionDrawable(envTexRegions[i]);
            buttonStyles[i].imageDown = new TextureRegionDrawable(envTexRegions[i]);
            buttons[i] = new ImageButton(buttonStyles[i]);
            t.add(buttons[i]).width(ScrollWidth).height(864);
            labels[i] = game.locale.get(puchases.getAt(i).name);
            ChangeListener l = new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {

                    click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                    if (curCategory.getAt(scrollNum).getClass().equals(Category.class)) {
                        enterCategory((Category)curCategory.getAt(scrollNum));
                    } else {
                        if (!game.getPurchaseManager().checkIfBought((Purchase)e)) {
                            if (!game.getAppStore().requestPurchase(e.name)) {
                                // unable to buy the item
                                game.utility.fullMessage(game, stage, skin1, click, font12, "shop_unable", true, null);
                            }
                        }


                        // purchase
                    }

                }
            };
            buttons[i].addListener(l);
        }
    }

    private void enterCategory(final Category enter) {
        scrollleft.setVisible(false);
        scrollright.setVisible(false);
        scrollPane.addAction(Actions.sequence(Actions.moveBy(-FlowergotchiGame.screenWidth, 0, 0.5f, Interpolation.pow3),
                Actions.run(new Runnable() {
            @Override
            public void run() {
                scrollNum = 0;
                curCategory = enter;

                if (scrollNum == curCategory.getSize() - 1) {
                    scrollright.setVisible(false);
                } else {
                    scrollright.setVisible(true);
                }
                scrollPane.setScrollX(0);
                fillButtons(curCategory);
                scrollPane.setWidget(t);
                scrollPane.layout();
                scrollPane.setVisible(false);
            }
        }), Actions.moveTo(2 * FlowergotchiGame.screenWidth, scrollPane.getY()), Actions.run(new Runnable() {
            @Override
            public void run() {
                scrollPane.setVisible(true);
            }
        }), Actions.moveBy(-2 * FlowergotchiGame.screenWidth + 117, 0, 0.5f, Interpolation.pow3)));
    }

    public void backOneCategory()
    {
        if (curCategory.getParent() == null) {
            ScreenFade fade = new ScreenFade(game, ScreenFade.FadeMode.FadeOut, new Runnable() {
                @Override
                public void run() {
                    game.setState(GameState.GS_Option);
                    game.backToPreviousScreen();
                }
            });
            stage.addActor(fade);
        } else {
            enterCategory(curCategory.getParent());

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
                game.setState(GameState.GS_ShopScreen);
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

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.clear();

    }
}
