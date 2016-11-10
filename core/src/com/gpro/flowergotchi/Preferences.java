package com.gpro.flowergotchi;

import com.badlogic.gdx.Gdx;

/**
 * Created by user on 27.04.2016.
 */
public class Preferences {
    private static final String PREF_FIRSTWATER = "firstWater";
    private static final String PREF_FIRSTSPIDER = "firstSpider";
    private static final String PREF_FIRSTARCANOID = "firstArcanoid";
    private static final String PREF_FIRSTSHOVEL = "firstShovel";
    private static final String PREF_FIRSTSTART = "firstStart";
    private static final String PREF_VOLUME = "volume";
    private static final String PREF_NOTIF = "notif";
    private static final String PREFS_NAME = "game";
    public static final String PREFS_USAGE = "96DRgKjdQc1kRTP7";
    public static final String PREFS_REUSE = "lrmM2pscen7trB0l";
    private final FlowergotchiGame game;

    private static com.badlogic.gdx.Preferences preferences;

    public Preferences(FlowergotchiGame game) {
        this.game = game;
    }

    public static com.badlogic.gdx.Preferences getPrefs() {
        if (preferences == null) {
            preferences = Gdx.app.getPreferences(PREFS_NAME);
        }
        return preferences;
    }

    public boolean playedWaterGame() {
        return getPrefs().getBoolean(PREF_FIRSTWATER, true);
    }

    public void setPlayedWaterGame(boolean a) {
        getPrefs().putBoolean(PREF_FIRSTWATER, a);
        getPrefs().flush();
    }

    public boolean playedArcanoidGame() {
        return getPrefs().getBoolean(PREF_FIRSTARCANOID, true);
    }

    public void setPlayedArcanoidGame(boolean a) {
        getPrefs().putBoolean(PREF_FIRSTARCANOID, a);
        getPrefs().flush();
    }

    public boolean playedShovelGame() {
        return getPrefs().getBoolean(PREF_FIRSTSHOVEL, true);
    }

    public void setPlayedShovelGame(boolean a) {
        getPrefs().putBoolean(PREF_FIRSTSHOVEL, a);
        getPrefs().flush();
    }

    public boolean isFirstStart() {
        return !getPrefs().getBoolean(PREF_FIRSTSTART);
    }

    public void tutorialComplete() {
        getPrefs().putBoolean(PREF_FIRSTSTART, true);
        getPrefs().flush();
    }

    public boolean playedSpiderGame() {
        return getPrefs().getBoolean(PREF_FIRSTSPIDER, true);
    }

    public void setPlayedSpiderGame(boolean a) {
        getPrefs().putBoolean(PREF_FIRSTSPIDER, a);
        getPrefs().flush();
    }

    public boolean getNotif() {
        return getPrefs().getBoolean(PREF_NOTIF, true);
    }

    public void setNotif(boolean notif) {
        game.serviceCallback().setNotificationsEnabled(notif);
        getPrefs().putBoolean(PREF_NOTIF, notif);
        getPrefs().flush();
    }

    public boolean checkMessageShown(String id) {
        return getPrefs().getBoolean(id, false);
    }

    public void setMessageShown(String id) {
        getPrefs().putBoolean(id, true);
        getPrefs().flush();
    }

    public static boolean getVolume() {
        return getPrefs().getBoolean(PREF_VOLUME, true);
    }

    public void setVolume(boolean volume) {
        getPrefs().putBoolean(PREF_VOLUME, volume);
        getPrefs().flush();
    }
}
