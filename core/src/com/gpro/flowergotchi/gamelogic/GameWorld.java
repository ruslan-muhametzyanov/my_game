package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.shop.LocalPurchaseManager;
import com.gpro.flowergotchi.ui.GameUI;

import java.util.EnumSet;

public class GameWorld implements Json.Serializable {

    GameObjectManager manager;
    private GameTimer timer;
    private Vector2 worldSize;
    private boolean lightEnabled = false;
    private boolean gamePaused = false;
    private boolean isTutorialActive = false;
    private Statistic statistic;

    public GameWorld() {
    }

    public GameWorld(Vector2 worldSize) {
        manager = new GameObjectManager(this);
        this.setWorldSize(worldSize);
        timer = new GameTimer();
        statistic = new Statistic();

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isTutorialActive() {
        return isTutorialActive;
    }

    public void setIsTutorialActive(boolean isTutorialActive) {
        this.isTutorialActive = isTutorialActive;
    }

    public boolean isGamePaused() {
        return gamePaused;
    }

    public void setGamePaused(boolean gamePaused) {
        this.gamePaused = gamePaused;
    }

    public boolean isLightEnabled() {
        return lightEnabled;
    }

    public void setLightEnabled(boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
    }

    public GameTimer getTimer() {
        return timer;
    }

    public void write(Json json) {
        json.writeValue("worldSize", worldSize);
        json.writeValue("objectManager", manager);
        json.writeValue("timer", timer);
        json.writeValue("statistic", statistic);
        json.writeValue("lightEnabled", lightEnabled);
        json.writeValue("gamePaused", gamePaused);
        json.writeValue("isTutorialActive", isTutorialActive);
        
    }

    public void read(Json json, JsonValue jsonMap) {
        worldSize = new Vector2(jsonMap.get("worldSize").get("x").asFloat(), jsonMap.get("worldSize").get("y").asFloat());
        manager = json.readValue(GameObjectManager.class, jsonMap.get("objectManager"));
        timer = json.readValue(GameTimer.class, jsonMap.get("timer"));
        statistic = json.readValue(Statistic.class, jsonMap.get("statistic"));
        lightEnabled = jsonMap.get("lightEnabled").asBoolean();
        gamePaused = jsonMap.get("gamePaused").asBoolean();
        isTutorialActive = jsonMap.get("isTutorialActive").asBoolean();
        manager.linkToWorld(this);
    }

    public EnumSet<Plant.NotificationTypes> updateWorld() {
        if (!gamePaused) {
            manager.manageEvents();
            return manager.doPlantLogic(isTutorialActive);
        }

        return EnumSet.of(Plant.NotificationTypes.N_PauseGame);
    }

    public String saveGame() {
        Json json = new Json();
        String str;
        json.setOutputType(JsonWriter.OutputType.json);
        str = json.prettyPrint(this);
        return str;
    }

    public Plant getActiveFlower() {
        return (Plant) manager.findFirstObject(Plant.class);
    }

    public com.gpro.flowergotchi.flowerlogic.Pot getActivePot() {
        return (com.gpro.flowergotchi.flowerlogic.Pot) manager.findFirstObject(com.gpro.flowergotchi.flowerlogic.Pot.class);
    }

    public void setBackground(Background background) {
        manager.addObject(background);
    }

    public void onUpdateObjects(ResourceManager resourceManager, GameUI ui) {
        manager.onUpdateObjects(resourceManager, ui);
    }

    public GameObjectManager gameObjectManager() {
        return manager;
    }

    public Vector2 getWorldSize() {
        return worldSize;
    }

    private void setWorldSize(Vector2 worldSize) {
        this.worldSize = worldSize;
    }

    public Background getActiveBack() {
        return (Background) manager.findFirstObject(Background.class);
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public GameObjectManager getManager() {
        return manager;
    }
}