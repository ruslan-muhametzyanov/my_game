package com.gpro.flowergotchi;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Logger;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.screens.GameScreen;
import com.gpro.flowergotchi.screens.LoadingGameScreen;
import com.gpro.flowergotchi.screens.SplashScreen;
import com.gpro.flowergotchi.screens.WallpaperScreen;
import com.gpro.flowergotchi.shop.AppStore;
import com.gpro.flowergotchi.shop.LocalPurchaseManager;
import com.gpro.flowergotchi.util.Utility;

import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.demo.DemoLocalizationService;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Main game class
 */
public class FlowergotchiGame extends AbstractGame {
    public final static long updateRate = 60000;
    public static final int screenWidth = 720;
    public static final int screenHeight = 1280;

    public static final int versionMajor = 1;
    public static final int versionMinor = 0;
    public static final int activityLauncher = 0;
    public static final int activityWallpaper = 1;

    public static boolean debugMode = false;
    private final String lang;
    /// Our callback for service
    private final AndroidPushNotificationServiceCallback callback;
    public ResourceManager manager;
    public I18NBundle locale;
    public Utility utility;
    private Preferences preferences;
    private GameState state, prevGameState;
    private WallpaperScreen wallpaperScreen;
    private GameScreen gameScreen;
    private com.gpro.flowergotchi.shop.AppStore appStore;
    private LocalPurchaseManager purManager;

    public FlowergotchiGame(AndroidPushNotificationServiceCallback callback, String lang) {
        this.callback = callback;
        this.lang = lang;
    }

    public AppStore getAppStore() {
        return appStore;
    }

    // Wallpaper screen
    public WallpaperScreen getWallpaperScreen() {
        return wallpaperScreen;
    }

    public void setWallpaperScreen(WallpaperScreen wallpaperScreen) {
        this.wallpaperScreen = wallpaperScreen;
    }

    // Game screen

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    // Service callback

    public AndroidPushNotificationServiceCallback serviceCallback() {
        return callback;
    }

    /**
     * Return current game state
     *
     * @return current state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets new game state
     *
     * @param state new state
     */
    public void setState(GameState state) {
        this.state = state;
        prevGameState = state;
    }

    /**
     * Asks service to update game world with fresh copy of objects
     *
     * @param world our world
     * @return fresh copy
     */
    public GameWorld updateService(GameWorld world) {
        return this.callback.updateSession(world);
    }

    @Override
    public List<Vector2> getSupportedResolutions() {
        List<Vector2> supportedScreenResolutions = new LinkedList<Vector2>();
        supportedScreenResolutions.add(new Vector2(FlowergotchiGame.screenWidth, FlowergotchiGame.screenHeight));
        return supportedScreenResolutions;
    }

    @Override
    public LocalizationService getLocalizationService() {
        return new DemoLocalizationService();
    }

    /**
     * Called when game is being created
     */

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        purManager = new LocalPurchaseManager(this);
        if (callback.getActivityType() == FlowergotchiGame.activityLauncher) {
            try {
                purManager.restorePurchases();
                purManager.restoreSuccess();
            } catch (GdxRuntimeException e) {
                e.printStackTrace();

            }
        }

        preferences = new Preferences(this);
        manager = new ResourceManager();
        manager.getLogger().setLevel(Logger.DEBUG);
        Texture.setAssetManager(manager);

        utility = new Utility(this);

        FileHandle baseFileHandle = Gdx.files.internal("locale/lang");
        Locale loc = new Locale(lang);
        locale = I18NBundle.createBundle(baseFileHandle, loc);

        initialize(screenWidth, screenHeight, screenWidth, screenHeight);
        Gdx.input.setCatchBackKey(true);
        switch (callback.getActivityType()) {
            case FlowergotchiGame.activityLauncher: {
                this.serviceCallback().startGameLoopService();
                this.serviceCallback().showAdMob(false);
                if (Gdx.files.isLocalStorageAvailable()) {
                    FileHandle file = Gdx.files.local("save.json");
                    if (file.exists()) {
                        this.setScreen(new LoadingGameScreen(this, null, null, null, false, false));
                    } else {
                        this.setScreen(new SplashScreen(this));
                    }
                } else {
                    this.setScreen(new SplashScreen(this));
                }
            }
            break;
            case FlowergotchiGame.activityWallpaper:

                this.setScreen(new LoadingGameScreen(this, null, null, null, false, true));
                break;
        }

    }

    public void restartGame() {
        if (callback.getActivityType() == FlowergotchiGame.activityLauncher) {
            FileHandle file = Gdx.files.local("save.json");
            serviceCallback().restart(true);
            file.delete();
            setGameScreen(null);
        } else {
            setWallpaperScreen(null);

        }

        dispose();
        create();
    }

    @Override
    public void pause() {
        super.pause();
        prevGameState = state;
        state = GameState.GS_Minimized;
    }

    @Override
    public void resume() {
        super.resume();
        setState(prevGameState);
    }

    @Override
    public void dispose() {
        manager.CleanUp();
        super.dispose();
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public GameState getPrevState() {
        return prevGameState;
    }

    public void addAppStore() {
        appStore = new AppStore(this);
    }

    public LocalPurchaseManager getPurchaseManager() {
        return purManager;
    }
}
