package com.gpro.flowergotchi.android;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.AndroidPushNotificationServiceCallback;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.Background;
import com.gpro.flowergotchi.gamelogic.GameWorld;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Launcher extends AndroidApplication implements AndroidPushNotificationServiceCallback {

    public final static String BROADCAST_ACTION = "com.gpro.flowergotchi.android.Launcher";
    public final static String Ad_ID = "";
    public static final int ADTIME = 3600 * 1000;
    public static final String TOP_BANNER = "";
    public static final String INTERSTITIAL = "";
    private FlowergotchiGame game;
    private ServiceConnection sConn;
    private GameIntentService service;

    private boolean serviceBound;
    private boolean recieverRegistered;
    private BroadcastReceiver br;
    private PendingIntent alarmIntent;

    private AdView adView;
    private AdRequest adRequest, interstitialAdRequest;
    private InterstitialAd interstitialAd;
    private SharedPreferences preferences;
    private Handler handler;
    private void setupAlarmManager(Context appContext) {
        AlarmManager am = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(appContext, GameReciever.class);
        alarmIntent = PendingIntent.getBroadcast(appContext, 0, intent1, 0);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), FlowergotchiGame.updateRate, alarmIntent);
    }

    private void setupReciever() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (game.getGameScreen() == null) {
                    Log.d("launcheronReceive", "fail");
                    return;
                }
                game.getGameScreen().RequestUpdate();

            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);
        recieverRegistered = true;
    }

    private void setupBackgroundService() {
        Intent intent = new Intent(this, GameIntentService.class);
        startService(intent);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                service = ((GameIntentService.ServiceBinder) binder).getService();
                serviceBound = true;
                Log.d("bind", "MainActivity onServiceConnected");
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d("bind", "MainActivity onServiceDisconnected");
                serviceBound = false;
                service.stopSelf();
            }
        };
        bindService(intent, sConn, BIND_AUTO_CREATE);
    }

    /**
     * Called when application is being set up
     *
     * @param savedInstanceState   instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = true;
        config.useCompass = false;

        game = new FlowergotchiGame(this, Locale.getDefault().getDisplayLanguage());
        View gameView = initializeForView(game, config);
        setupAds(gameView);
        setupAppStore();
    }

    private void setupAppStore() {
        game.addAppStore();
        game.getAppStore().setPlatformResolver(new GooglePlayResolver(game));
        try {
            game.getAppStore().getPlatformResolver().installIAP();
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }

    }

    private void setupAds(View gameView) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            if (msg.what == 0)
                adView.setVisibility(View.GONE);
            else if (msg.what == 1) {
                adView.setVisibility(View.VISIBLE);
                adRequest = new AdRequest.Builder()
                        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR) // Указываем тестовый режим на эмуляторе
                        //.addTestDevice(Ad_ID) // ID устройства. Его видно в логе после первого запуска
                        .build();
                adView.loadAd(adRequest);
            }
            }
        };

        RelativeLayout layout = new RelativeLayout(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(TOP_BANNER);
        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR) // Указываем тестовый режим на эмуляторе
                .addTestDevice(Ad_ID) // ID устройства. Его видно в логе после первого запуска
                .build();
        adView.loadAd(adRequest);

        layout.addView(gameView);

        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        layout.addView(adView, adParams);


        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL);

        interstitialAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR) // Указываем тестовый режим на эмуляторе
                .addTestDevice(Ad_ID) // ID устройства. Его видно в логе после первого запуска
                .build();
        interstitialAd.loadAd(interstitialAdRequest);
        //interstitialAd.isLoaded();


        setContentView(layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restart(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Gdx.input = this.getInput();
    }

    /**
     * Update our world using fresh copy from service
     *
     * @param world world to update
     * @return updated world
     */
    @Override
    public GameWorld updateSession(GameWorld world) {
        if (service != null) {
            return service.updateWorld(world);
        }
        return world;
    }

    @Override
    public void startGameLoopService() {
        setupBackgroundService();
        setupReciever();
        setupAlarmManager(this.getApplicationContext());
    }

    @Override
    public int clientButtonCallback(AndroidCallbackTypes type, List<Integer> args) {
        Log.d("callback", "callback" + String.valueOf(type));
        if (service != null) {
            return service.clientButtonCallback(type, args);
        }
        return -1;
    }

    @Override
    public void restart(boolean destroy) {
        if (destroy && alarmIntent != null) {
            AlarmManager am = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            am.cancel(alarmIntent);
        }
        if (recieverRegistered) {
            unregisterReceiver(br);
            recieverRegistered = false;
        }
        if (serviceBound) {
            unbindService(sConn);
            if (destroy) {
                service.restartWallpaper();
                service.stopSelf();
            }
            serviceBound = false;
        }

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1337);
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
    public int getActivityType() {
        return FlowergotchiGame.activityLauncher;
    }

    @Override
    public void setNotificationsEnabled(boolean set) {
        if (service != null) {
            service.setNotificationsEnabled(set);
        }
    }

    @Override
    public void setTutorialMode(boolean mode) {
        if (service != null) {
            service.setTutorialMode(mode);
        }
    }

    @Override
    public void updateClientsideVars(GameWorld world) {
        if (service != null) {
            service.updateClientsideVars(world);
        }
    }

    @Override
    public void statCallback(AndroidCallbackTypes type, int count, int star) {
        Log.d("statistic", "callback" + String.valueOf(type));
        if (service != null) {
            service.statCallback(type, count, star);
        }
    }

    @Override
    public void resumeGame() {
        if (service != null) {
            service.resumeGame();
        }
    }

    @Override
    public void pauseGame() {
        if (service != null) {
            service.pauseGame();
        }
    }

    @Override
    public void createNewWorld(Background.Parameters param, Class pot, Plant.Parameters flower) {
        if (service != null) {
            service.createNewWorld(param, pot, flower);
        }
    }

    @Override
    public void squishBug(Integer ID) {
        if (service != null) {
            service.squishBug(ID);
        }
    }

    @Override
    public void galleryAddPic(String fpath) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(fpath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void showAdMob(boolean show) {
        handler.sendEmptyMessage(show ? 1 : 0);
    }

    @Override
    public void showAppRates() {
        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gpro.flowergotchi.android")));
    }

    @Override
    public void showAdInterstitial() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        try {
            runOnUiThread(new Runnable() {
                @SuppressLint("CommitPrefEdits")
                public void run() {
                    if (interstitialAd.isLoaded() && System.currentTimeMillis() - preferences.getLong("AdTime", 0) >= ADTIME) {
                        interstitialAd.show();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putLong("AdTime", System.currentTimeMillis());
                        editor.commit();
                    } else {
                        interstitialAdRequest = new AdRequest.Builder().build();
                        interstitialAd.loadAd(interstitialAdRequest);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}