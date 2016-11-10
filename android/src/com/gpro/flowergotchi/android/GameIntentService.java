package com.gpro.flowergotchi.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.flowerlogic.Pot;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;

public class GameIntentService extends Service {
    private final ServiceBinder binder = new ServiceBinder();
    private GameSession session;
    private Notification notif;

    public GameIntentService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, Launcher.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //noinspection deprecation
        notif = new Notification(R.drawable.notif, "Flowergotchi",
                System.currentTimeMillis());
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        //noinspection deprecation
        notif.setLatestEventInfo(this, "Flowergotchi", "Flowergotchi", pendingIntent);

        session = new GameSession(this);
        if (isWorldCreated()) {
            session.ourWorld = session.loadGame(this);
        }
    }

    private boolean isWorldCreated() {
        try {
            FileInputStream fi = openFileInput("save.json");
            fi.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void createNewWorld(Background.Parameters param, Class pot, Plant.Parameters flower) {
        if (isWorldCreated()) {
            session.ourWorld = session.loadGame(this);
        } else {
            Background back = new Background(param);
            session.ourWorld.setBackground(back);
            Class[] types = {GameWorld.class};
            Class[] types2 = {GameWorld.class, Vector2.class};
            Object[] parameters2 = {session.ourWorld, new Vector2(session.ourWorld.getWorldSize().x / 2, session.ourWorld.getWorldSize().y / 2)};
            Constructor constructor;
            Constructor cons;

            try {
                Class flowerClass;
                try {
                    flowerClass = Class.forName(flower.flowerClass);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                cons = flowerClass.getConstructor(types2);
                Plant myplant = (Plant) cons.newInstance(parameters2);
                myplant.setName(flower.flowerName);
                constructor = pot.getConstructor(types);
                Object[] parameters = {session.ourWorld};
                Pot mypot = (Pot) constructor.newInstance(parameters);
                myplant.plant(mypot);
                session.saveGame(this, session.ourWorld.saveGame());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void notifyAbout(String about, boolean lights) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, Launcher.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Notification.FLAG_AUTO_CANCEL);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1337, notificationIntent, 0);
        if (pendingIntent == null) {
            notif.defaults |= Notification.DEFAULT_LIGHTS;
            notif.defaults |= Notification.DEFAULT_VIBRATE;
            notif.setLatestEventInfo(this, "Flowergotchi", about, pendingIntent);
            nm.notify(1337, notif);
        } else if (lights) {
            notif.defaults &= ~Notification.DEFAULT_LIGHTS;
            notif.defaults &= ~Notification.DEFAULT_VIBRATE;
        }
    }

    public void clearNotifications() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1337);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "update");
        if (session != null && isWorldCreated()) {
            session.updateSession(this);
        }
        return START_STICKY;
    }

    public GameWorld updateWorld(GameWorld world) {
        if (session != null && isWorldCreated()) {
            world = session.loadGame(this);
        }

        return world;
    }

    int clientButtonCallback(AndroidCallbackTypes type, List<Integer> args) {
        switch (type) {
            case CB_Insects: {
                Plant plant = session.getOurWorld().getActiveFlower();
                int toKill = 2;
                if (args != null) {
                    toKill = args.get(0);
                }
                plant.setIntVarVal(IntGameVariables.Var_Insects, plant.getIntVarVal(IntGameVariables.Var_Insects) - toKill);
            }
            break;
            case CB_Water: {
                Plant plant = session.getOurWorld().getActiveFlower();
                plant.setIntVarVal(IntGameVariables.Var_Water, plant.getIntVarVal(IntGameVariables.Var_Water) + plant.getIntVar(IntGameVariables.Var_Water).getRange() / 2);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.cancel(1337);
            }
            break;
            case CB_Light: {
                session.getOurWorld().setLightEnabled(!session.getOurWorld().isLightEnabled());
                Plant plant = session.getOurWorld().getActiveFlower();
                plant.setIntVarVal(IntGameVariables.Var_Light, 0);
            }
            break;
            case CB_Loosening: {
                Plant plant = session.getOurWorld().getActiveFlower();
                plant.setIntVarVal(IntGameVariables.Var_Loosening, plant.getIntVarVal(IntGameVariables.Var_Loosening) + plant.getIntVar(IntGameVariables.Var_Water).getRange() / 2);
            }
            break;
            case CB_OpenPot: {
                Pot pot = session.getOurWorld().getActivePot();
                pot.setDrawInside(!pot.isDrawInside());
            }
            break;
            case CB_Spider: {
                Plant plant = session.getOurWorld().getActiveFlower();
                plant.setIntVarVal(IntGameVariables.Var_Spider, 0);
            }
            break;
            case CB_Cat: {
                Plant plant = session.getOurWorld().getActiveFlower();
                plant.setIntVarVal(IntGameVariables.Var_Cat, 0);
            }
            break;
            case CB_Poison: {
                Plant plant = session.getOurWorld().getActiveFlower();
                plant.poison();
            }
            break;
        }
        session.saveGame(this, session.ourWorld.saveGame());
        return 0;
    }

    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        session.getOurWorld().saveGame();
    }

    public void squishBug(Integer ID) {
        session.ourWorld.gameObjectManager().removeObject(ID);
        session.saveGame(this, session.ourWorld.saveGame());
    }

    public void setNotificationsEnabled(boolean set) {
        SharedPreferences prefs = this.getSharedPreferences(GameSession.PREF_NOTIF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(GameSession.PREF_NOTIF, set);
        editor.apply();

    }

    public void pauseGame() {
        session.setGamePaused(true);
        session.saveGame(this, session.ourWorld.saveGame());
    }

    public void resumeGame() {
        session.setGamePaused(false);
        session.saveGame(this, session.ourWorld.saveGame());
    }

    public void setTutorialMode(boolean mode) {
        session.ourWorld.setIsTutorialActive(mode);
        session.setGamePaused(mode);
        session.saveGame(this, session.ourWorld.saveGame());
    }

    public void updateClientsideVars(GameWorld world) {
        session.ourWorld.gameObjectManager().updateServiceFromClient(world);
    }

    public void statCallback(AndroidCallbackTypes type, int count, int star) {
        switch (type) {
            case CB_Insects: {
                session.getOurWorld().getStatistic().newCount(count, star);
                session.getOurWorld().getStatistic().sumInsect();
                session.getOurWorld().getStatistic().sumStar();
            }
            break;
            case CB_Water: {
                session.getOurWorld().getStatistic().newCount(count, star);
                session.getOurWorld().getStatistic().sumWater();
                session.getOurWorld().getStatistic().sumStar();
            }
            break;
            case CB_Spider: {
                session.getOurWorld().getStatistic().newCount(count, star);
                session.getOurWorld().getStatistic().sumSpider();
                session.getOurWorld().getStatistic().sumStar();
            }
            break;
            case CB_Loosening: {
                session.getOurWorld().getStatistic().newCount(count, star);
                session.getOurWorld().getStatistic().sumStar();
            }
            break;
            case CB_Cat: {
                session.getOurWorld().getStatistic().sumCat();
            }
            break;
        }
        session.saveGame(this, session.ourWorld.saveGame());
    }

    public void restartWallpaper() {
        Intent wallpaperIntent = new Intent(Wallpaper.BROADCAST_ACTION);
        wallpaperIntent.putExtra(Wallpaper.RESTART_ACTION, true);
        this.sendBroadcast(wallpaperIntent);
    }


    public class ServiceBinder extends Binder {
        GameIntentService getService() {
            return GameIntentService.this;
        }
    }
}