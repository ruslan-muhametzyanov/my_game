package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.util.Rand;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Background extends GameObject implements GameObject.DrawableGameObject, GameObjectManager.Loadable, Json.Serializable {
    private static final Calendar calendar = GregorianCalendar.getInstance();
    private final Date date = new Date();
    private String background;
    private String morningTexture;
    private String dayTexture;
    private String eveningTexture;
    private String nightTexture;
    private String currentTexture;
    private Vector2 p1;
    private Vector2 p2;
    private int ambientCount;
    private String folder;
    private float insectAttract;

    public Background() {
        super(GameObjectTypes.GO_Background);
        setPosition(new Vector2(1, 1));
    }

    public Background(Parameters param) {
        this();
        this.background = param.back.get(1);
        this.morningTexture = param.back.get(2);
        this.dayTexture = param.back.get(3);
        this.eveningTexture = param.back.get(4);
        this.nightTexture = param.back.get(5);
        this.p1 = param.p1;
        this.p2 = param.p2;
        this.ambientCount = param.ambientCount;
        this.folder = param.folder;

        this.insectAttract = param.insectAttract;

        currentTexture = selectBackground();
    }

    public float getInsectAttract() {
        return insectAttract;
    }

    public Vector2 getP1() {
        return p1;
    }

    public Vector2 getP2() {
        return p2;
    }

    private String selectBackground() {
        date.setTime(System.currentTimeMillis());   // given date
        calendar.setTime(date);
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 0 && calendar.get(Calendar.HOUR_OF_DAY) < 6) {
            return nightTexture;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) >= 6 && calendar.get(Calendar.HOUR_OF_DAY) < 12) {
            return morningTexture;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) >= 12 && calendar.get(Calendar.HOUR_OF_DAY) < 18) {
            return dayTexture;
        } else {
            return eveningTexture;
        }
    }

    public void addToUI(ResourceManager resourceManager, GameUI ui) {
        Texture tex = resourceManager.getTexture(background);
        TextureRegion reg = new TextureRegion(tex, tex.getWidth(), tex.getHeight());
        reg.flip(false, true);
        Image img = new Image(reg);
        img.setPosition(0, 0);
        img.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, DrawOrderLevels.D_BackgroundFront)));
        img.setName(this.getObjectName() + "back");
        ui.getStage().addActor(img);

        currentTexture = selectBackground();
        tex = resourceManager.getTexture(currentTexture);
        reg = new TextureRegion(tex, tex.getWidth(), tex.getHeight());
        reg.flip(false, true);
        img = new Image(reg);
        img.setPosition(0, 0);
        img.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, DrawOrderLevels.D_BackgroundBack)));
        img.setName(this.getObjectName());
        ui.getStage().addActor(img);
    }

    public void onUpdateObjectsClient(ResourceManager resourceManager, GameUI ui) {
        if (currentTexture != null && !currentTexture.equals(selectBackground())) {
            Texture tex = resourceManager.getTexture(selectBackground());
            TextureRegion reg = new TextureRegion(tex, tex.getWidth(), tex.getHeight());
            reg.flip(false, true);
            Image imgBack = (Image) ui.getActorByName(this.getObjectName());
            if (imgBack == null) {
                Gdx.app.exit();
                return;
            }
            imgBack.setDrawable(new TextureRegionDrawable(reg));
            imgBack.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, DrawOrderLevels.D_BackgroundFront)));
        }
        if (ambientCount > 0 && !ui.isWallpaperMode()) {
            Music ambient = resourceManager.get(folder + "/ambient" + String.valueOf(Rand.randInt(1, ambientCount)) + ".ogg");
            if (!ambient.isPlaying()) {
                ambient.setVolume(1.0f * (Preferences.getVolume() ? 1.0f : 0.0f));
                ambient.setLooping(false);
                ambient.play();
                Gdx.app.log("sound", "1");
            }
        }
    }

    public void write(Json json) {
        super.write(json);
        json.writeValue("background", background);
        json.writeValue("morningTexture", morningTexture);
        json.writeValue("dayTexture", dayTexture);
        json.writeValue("eveningTexture", eveningTexture);
        json.writeValue("nightTexture", nightTexture);
        json.writeValue("p1", p1);
        json.writeValue("p2", p2);
        json.writeValue("ambientCount", ambientCount);
        json.writeValue("folder", folder);
        json.writeValue("insectAttract", insectAttract);
    }

    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);
        background = jsonMap.get("background").asString();
        morningTexture = jsonMap.get("morningTexture").asString();
        dayTexture = jsonMap.get("dayTexture").asString();
        eveningTexture = jsonMap.get("eveningTexture").asString();
        nightTexture = jsonMap.get("nightTexture").asString();
        p1 = new Vector2(jsonMap.get("p1").get("x").asFloat(), jsonMap.get("p1").get("y").asFloat());
        p2 = new Vector2(jsonMap.get("p2").get("x").asFloat(), jsonMap.get("p2").get("y").asFloat());
        ambientCount = jsonMap.get("ambientCount").asInt();
        folder = jsonMap.get("folder").asString();
        insectAttract = jsonMap.get("insectAttract").asFloat();
    }

    public void loadGraphics(ResourceManager resourceManager) {
        resourceManager.getTexture(background);
        resourceManager.getTexture(morningTexture);
        resourceManager.getTexture(dayTexture);
        resourceManager.getTexture(eveningTexture);
        resourceManager.getTexture(nightTexture);
        if (ambientCount > 0) {
            for (int i = 1; i <= ambientCount; i++) {
                try {
                    resourceManager.get(folder + "/ambient" + String.valueOf(i) + ".ogg");
                } catch (GdxRuntimeException e) {
                    resourceManager.load(folder + "/ambient" + String.valueOf(i) + ".ogg", Music.class);
                }
            }
        }
    }

    public static class Parameters {
        public List<String> back;
        public final Vector2 p1;
        public final Vector2 p2;
        public final int ambientCount;
        public final String folder;
        public final String name;
        public final float insectAttract;

        public Parameters(List<String> back, Vector2 p1, Vector2 p2, int ambientCount, String folder, String name,
                          float insectAttract) {
            this.back = back;
            this.p1 = p1;
            this.p2 = p2;
            this.ambientCount = ambientCount;
            this.folder = folder;
            this.name = name;
            this.insectAttract = insectAttract;
        }
    }
}
