package com.gpro.flowergotchi.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Base64Coder;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utility {
    public static final String FONT_CHARS = "абвгдежзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>©";
    private final FlowergotchiGame game;
    private Skin tileSkin;
    private BitmapFont mainFont;
    private BitmapFont smallFont;
    private boolean loaded = false;

    public Utility(FlowergotchiGame game) {
        this.game = game;
    }

    public void loadDefault()
    {
        if (!loaded) {
            initFonts();
            initDefaultSkin(game);
            loaded = true;
        }

    }

    private void initDefaultSkin(FlowergotchiGame game) {
        TextureAtlas textureAtlas = game.manager.get("images/skin/buttons.pack", TextureAtlas.class);
        tileSkin = new Skin(textureAtlas);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = tileSkin.newDrawable("smallYellow");
        textButtonStyle.down = tileSkin.newDrawable("smallYellowPressed");
        textButtonStyle.font = mainFont;
        textButtonStyle.fontColor = Color.WHITE;
        tileSkin.add("default", textButtonStyle);
        TextButton.TextButtonStyle redtextButtonStyle = new TextButton.TextButtonStyle();
        redtextButtonStyle.up = tileSkin.newDrawable("smallRed");
        redtextButtonStyle.down = tileSkin.newDrawable("smallRedPressed");
        redtextButtonStyle.font = mainFont;
        redtextButtonStyle.fontColor = Color.WHITE;
        tileSkin.add("defaultRed", redtextButtonStyle);

        TextButton.TextButtonStyle selectButtonstyle = new TextButton.TextButtonStyle();
        selectButtonstyle.down = tileSkin.newDrawable("defButtonPressed");
        selectButtonstyle.up = tileSkin.newDrawable("defButton");
        selectButtonstyle.font = mainFont;
        selectButtonstyle.fontColor = Color.WHITE;
        tileSkin.add("defButton", selectButtonstyle);


        final Texture background = game.manager.getTexture("images/skin/messageBox.png");
        Window.WindowStyle windowStyle = new Window.WindowStyle(getSmallFont(), Color.WHITE, new TextureRegionDrawable(new TextureRegion(background)));
        tileSkin.add("default", windowStyle);
        final Texture backgroundStar = game.manager.getTexture("images/skin/mediumMessageBox.png");
        Window.WindowStyle windowStyleStar = new Window.WindowStyle(getSmallFont(), Color.WHITE, new TextureRegionDrawable(new TextureRegion(backgroundStar)));
        tileSkin.add("defaultMedium", windowStyleStar);

        Label.LabelStyle topStyle = new Label.LabelStyle(getMainFont(), Color.WHITE);
        tileSkin.add("top", topStyle);
        Label.LabelStyle smallStyle = new Label.LabelStyle(getSmallFont(), Color.WHITE);
        tileSkin.add("small", smallStyle);
    }

    public Object cloneObject(Object obj) {
        try {
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(obj));
            }
            return clone;
        } catch (Exception e) {
            return null;
        }
    }

    public float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return new String(Base64Coder.encode(encrypted));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64Coder.decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public void message(Stage stage, ResourceManager manager, BitmapFont font, String mes, Runnable run) {
        TextureRegion texturecol = new TextureRegion(manager.getTexture("images/skin/message.png"));
        texturecol.flip(false, true);
        final Image imagecol = new Image(new TextureRegionDrawable(texturecol));
        imagecol.setX((stage.getViewport().getWorldWidth() - imagecol.getWidth()) / 2);
        imagecol.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_UIButtons))));
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label message = new Label(mes, style);
        message.setX((stage.getViewport().getWorldWidth() - message.getWidth()) / 2);
        message.setY(56);
        message.setAlignment(Align.center);
        message.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_UIButtons))));

        stage.addActor(imagecol);
        stage.addActor(message);
        if (run != null) {
            imagecol.addAction(Actions.sequence(Actions.delay(2.0f), Actions.alpha(0.0f, 1.0f), Actions.run(run), Actions.removeActor(imagecol)));
        } else {
            imagecol.addAction(Actions.sequence(Actions.delay(2.0f), Actions.alpha(0.0f, 1.0f), Actions.removeActor(imagecol)));
        }

        message.addAction(Actions.sequence(Actions.delay(2.0f), Actions.alpha(0.0f, 1.0f), Actions.removeActor(message)));
    }

    public void fullMessage(FlowergotchiGame game, Stage stage, Skin tileSkin, final Sound click, BitmapFont font, String mes, boolean flip, final Runnable run) {
        TextureRegion fonImage = new TextureRegion(game.manager.getTexture("blur.png"));
        fonImage.flip(false, true);
        final Image back = new Image(fonImage);
        back.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_blur))));
        stage.addActor(back);

        Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);

        Label label1 = new Label(game.locale.get(mes), style);
        label1.setWrap(true);
        label1.setAlignment(Align.left);
        style.fontColor = new Color(73.0f / 255, 36.0f / 255, 7.0f / 255, 1.0f);

        final Texture background = game.manager.getTexture("images/skin/messageBox.png");

        TextButton btnOK = new TextButton(game.locale.get("ok"), tileSkin.get("default", TextButton.TextButtonStyle.class));

        final Dialog dialog = new Dialog("", tileSkin) {
            @Override
            public float getPrefWidth() {
                return background.getWidth();
            }

            @Override
            public float getPrefHeight() {
                return background.getHeight();
            }
        };
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        btnOK.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                click.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                dialog.hide();
                dialog.cancel();
                dialog.remove();
                back.remove();

                if (run != null) {
                    run.run();
                }
            }
        });

        TextureRegion myTex = new TextureRegion(background);
        myTex.flip(false, !flip);
        myTex.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegionDrawable drawable = new TextureRegionDrawable(myTex);
        dialog.setBackground(drawable);

        dialog.row().colspan(1).center().size(background.getWidth() - 60f, 300f);
        dialog.add(label1).expand();
        dialog.row().colspan(1).size(background.getWidth() - 60f, 100f);
        dialog.button(btnOK);

        dialog.align(Align.center);
        dialog.show(stage).setPosition((stage.getViewport().getWorldWidth() - background.getWidth()) / 2, 300);

        dialog.pack();
        dialog.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_ScreenShot))));
        stage.addActor(dialog);
    }

    public BitmapFont generateFont(int size, boolean flip) {
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().equals("zh")) {
            return new BitmapFont(Gdx.files.internal("chinese4.fnt"), flip);
        }
        FreeTypeFontGenerator generator;
        FreeTypeFontGenerator.FreeTypeFontParameter parameter;
        generator = new FreeTypeFontGenerator(Gdx.files.internal("arch.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.flip = flip;
        parameter.characters = Utility.FONT_CHARS;
        BitmapFont font = generator.generateFont(parameter);

        generator.dispose();
        return font;
    }

    public Skin getDefaultSkin() {
        return tileSkin;
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public BitmapFont getMainFont() {
        return mainFont;
    }

    private void initFonts() {
        mainFont = this.generateFont(56, true);
        smallFont = this.generateFont(36, true);
    }
}
