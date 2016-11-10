package com.gpro.flowergotchi;

import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.GameWorld;

import java.util.List;

public interface AndroidPushNotificationServiceCallback {
    void startGameLoopService();

    GameWorld updateSession(GameWorld world);

    void createNewWorld(Background.Parameters param, Class pot, Plant.Parameters flower);

    void squishBug(Integer ID);

    int clientButtonCallback(AndroidCallbackTypes type, List<Integer> args);

    void restart(boolean force);

    int getActivityType();

    void setNotificationsEnabled(boolean set);

    void galleryAddPic(String fpath);

    void resumeGame();

    void pauseGame();

    void setTutorialMode(boolean mode);

    void updateClientsideVars(GameWorld world);

    void statCallback(AndroidCallbackTypes type, int count, int star);

    boolean isWorldCreated();

    void showAdMob(boolean show);

    void showAppRates();

    void showAdInterstitial();
}