package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class GameTimer implements Json.Serializable {
    private float currentTime;
    private float lastTime;
    private boolean isActive;

    public GameTimer() {
        this.lastTime = currentTime = 0;
        isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void write(Json json) {
        json.writeValue("currentTime", currentTime);
        json.writeValue("lastTime", lastTime);
        json.writeValue("isActive", isActive);
    }

    public void read(Json json, JsonValue jsonMap) {
        currentTime = jsonMap.get("currentTime").asFloat();
        lastTime = jsonMap.get("lastTime").asFloat();
        isActive = jsonMap.get("isActive").asBoolean();
    }

    public float getTime() {
        return currentTime;
    }

    public void tick(float delta) {
        if (isActive()) {
            currentTime += delta;
        }
    }

    public float loadTime() {
        return lastTime;
    }

    public void saveTime() {
        lastTime = currentTime;
    }
}
