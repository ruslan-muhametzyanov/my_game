package com.gpro.flowergotchi.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.util.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.EnumSet;

public class GameSession {
    public static final String PREF_NOTIF = "notif";
    GameWorld ourWorld;
    private GameIntentService service;
    private boolean notifyPause = false;

    private final static int blockSize = 16;

    public GameSession(GameIntentService service) {
        this();
        this.service = service;
    }

    public GameSession() {
        Vector2 worldSize = new Vector2(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight);
        ourWorld = new GameWorld(worldSize);
    }



    public GameWorld loadGame(Context context) {
        GameWorld world = new GameWorld();
        try {
            InputStream inputStream = context.openFileInput("save.json");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(Utility.decrypt(Preferences.PREFS_REUSE, Preferences.PREFS_USAGE, line));
            }
            inputStream.close();

            Json json = new Json();
            String contents = builder.toString();
            world = json.fromJson(GameWorld.class, contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return world;
    }

    public void saveGame(Context context, String str) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    context.openFileOutput("save.json", Context.MODE_PRIVATE)));

            int pos = 0;
            while (pos < str.length()) {
                String sub = String.copyValueOf(str.toCharArray(), pos, pos + blockSize >= str.length() ? str.length() - pos : blockSize);
                String encrypted = Utility.encrypt(Preferences.PREFS_REUSE, Preferences.PREFS_USAGE, sub);

                bw.write(encrypted);
                bw.write('\n');
                pos += blockSize;
            }

            bw.close();

            Intent wallpaperIntent = new Intent(Wallpaper.BROADCAST_ACTION);
            context.sendBroadcast(wallpaperIntent);
            Intent launcherIntent = new Intent(Launcher.BROADCAST_ACTION);
            context.sendBroadcast(launcherIntent);
        } catch (Exception t) {
            t.printStackTrace();

        }
    }

    public GameWorld getOurWorld() {
        return ourWorld;
    }

    public void updateSession(Context context) {
        ourWorld.getTimer().tick((float) FlowergotchiGame.updateRate / 1000);
        Log.d("updateSession", "updateSession");
        ourWorld.getTimer().saveTime();
        EnumSet<Plant.NotificationTypes> returnType = ourWorld.updateWorld();
        if (getNotif(context)) {
            if (returnType.contains(Plant.NotificationTypes.N_NeedsWater)) {
                service.notifyAbout(context.getString(R.string.watering), true);
            }
            if (returnType.contains(Plant.NotificationTypes.N_TooSmallLight)) {
                service.notifyAbout(context.getString(R.string.minLightning), true);
            }
            if (returnType.contains(Plant.NotificationTypes.N_TooMuchLight)) {
                service.notifyAbout(context.getString(R.string.maxLightning), true);
            }
            if (returnType.contains(Plant.NotificationTypes.N_NeedsLoosening)) {
                service.notifyAbout(context.getString(R.string.loosening), true);
            }
            if (returnType.contains(Plant.NotificationTypes.N_RemoveInsects)) {
                service.notifyAbout(context.getString(R.string.insect), true);
            }
            if (returnType.contains(Plant.NotificationTypes.N_RemoveSpider)) {
                service.notifyAbout(context.getString(R.string.spider), true);
            }
            if (returnType.contains(Plant.NotificationTypes.N_Dead)) {
                service.notifyAbout(context.getString(R.string.dead), true);
            }
            if (returnType.contains(Plant.NotificationTypes.N_PauseGame)) {
                service.notifyAbout(context.getString(R.string.pause), false);
                Log.d("this", "this");
                notifyPause = true;
            } else if (!ourWorld.isGamePaused() && notifyPause) {
                service.clearNotifications();
                notifyPause = false;
            }
            if (returnType.contains(Plant.NotificationTypes.N_Normal)) {
                service.clearNotifications();
            }
        }
        saveGame(context, ourWorld.saveGame());
    }

    public boolean getNotif(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NOTIF, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_NOTIF, true);
    }

    public void setGamePaused(boolean pause) {
        ourWorld.setGamePaused(pause);
    }


}