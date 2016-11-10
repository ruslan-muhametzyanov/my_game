package com.gpro.flowergotchi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;

import java.nio.ByteBuffer;

public class ScreenshotFactory {
    private static int counter = 1;
    private final FlowergotchiGame game;
    String st;

    public ScreenshotFactory(FlowergotchiGame game) {
        this.game = game;
    }


    public void saveScreenshot() {
        Sound camera = game.manager.get("sounds/camera.ogg", Sound.class);
        camera.play(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
        Gdx.app.log("Screenshot", "Screenshot");
        try {
            FileHandle fh;
            do {
                fh = new FileHandle(Gdx.files.getExternalStoragePath() + "/Pictures/" + "Flowergotchi_" + counter++ + ".png");
            }
            while (fh.exists());
            Pixmap pixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            PixmapIO.writePNG(fh, pixmap);
            st = String.valueOf(fh);
            //PixmapIO.readCIM(fh);
            //Gdx.files.external(Gdx.files.getExternalStoragePath() +"Flowergochi_" + counter++ + ".png").moveTo(Gdx.files.local(Gdx.files.getLocalStoragePath() + "Flowergochi_" + counter++ + ".png"));
            pixmap.dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }
        game.serviceCallback().galleryAddPic(st);
    }

    private Pixmap getScreenshot(int x, int y, int w, int h, boolean yDown) {
        final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);

        if (yDown) {
            ByteBuffer pixels = pixmap.getPixels();
            int numBytes = w * h * 4;
            byte[] lines = new byte[numBytes];
            int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
        }

        return pixmap;
    }
}