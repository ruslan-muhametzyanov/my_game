package com.gpro.flowergotchi.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.AndroidPushNotificationServiceCallback;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.GameWorld;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Wallpaper extends AndroidLiveWallpaperService implements AndroidPushNotificationServiceCallback {
    public final static String BROADCAST_ACTION = "com.gpro.flowergotchi.android.Wallpaper";
    public final static String RESTART_ACTION = "restart";
    private GameSession session;
    private BroadcastReceiver br;
    private boolean broadcastRegistered = false;
    private FlowergotchiGame game;


    public Wallpaper() {
        super();
        session = new GameSession();
    }

    @Override
    public void updateClientsideVars(GameWorld world) {

    }

    @Override
    public void statCallback(AndroidCallbackTypes type, int count, int star) {

    }

    @Override
    public void onCreateApplication() {
        super.onCreateApplication();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useCompass = false;
        config.useWakelock = false;
        config.useAccelerometer = false;

        game = new FlowergotchiGame(this, Locale.getDefault().getDisplayLanguage());
        initialize(game, config);
    }

    private void registerBroadcast(final FlowergotchiGame game) {
        if (!broadcastRegistered) {
            br = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getBooleanExtra(Wallpaper.RESTART_ACTION, false)) {
                        restartWallpaper();
                    }
                    if (game.getWallpaperScreen() == null) {
                        Log.d("wallpaperonReceive", "fail");
                        return;
                    }

                    game.getWallpaperScreen().RequestUpdate();
                }
            };
            IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
            registerReceiver(br, intFilt);
            broadcastRegistered = true;
        }
    }

    private void restartWallpaper() {
        Log.d("restart", "wallpaper");
        if (broadcastRegistered) {
            unregisterReceiver(br);
            broadcastRegistered = false;
        }
        game.restartGame();
    }

    @Override
    public void onDestroy() {
        if (broadcastRegistered) {
            unregisterReceiver(br);
            broadcastRegistered = false;
        }
        super.onDestroy();

    }

    @Override
    public void startGameLoopService() {

    }

    @Override
    public void setNotificationsEnabled(boolean set) {

    }

    @Override
    public boolean isWorldCreated() {
        try {
            FileInputStream fi = openFileInput("save.json");
            fi.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public GameWorld updateSession(GameWorld world) {
        if (session != null && isWorldCreated()) {
            world = session.loadGame(this);
        }
        return world;
    }

    @Override
    public void resumeGame() {

    }

    @Override
    public void pauseGame() {

    }

    @Override
    public void setTutorialMode(boolean mode) {

    }

    @Override
    public void createNewWorld(Background.Parameters param, Class pot, Plant.Parameters flower) {
        if (!broadcastRegistered) {
            registerBroadcast(game);
        }
        try {
            FileInputStream fs = openFileInput("save.json");
            fs.close();
            session.ourWorld = session.loadGame(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void squishBug(Integer ID) {
        // TODO : implement
    }

    @Override
    public void restart(boolean destroy) {

    }

    @Override
    public int getActivityType() {
        return FlowergotchiGame.activityWallpaper;
    }

    @Override
    public int clientButtonCallback(AndroidCallbackTypes type, List<Integer> args) {
        return 0;
    }

    @Override
    public void galleryAddPic(String fpath) {

    }

    @Override
    public void showAdMob(boolean show) {

    }

    @Override
    public void showAppRates() {

    }

    @Override
    public void showAdInterstitial() {

    }
}
