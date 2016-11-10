package com.gpro.flowergotchi.gamelogic.achievments;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.gamelogic.GameObject;

/**
 * Created by user on 15.02.2016.
 */
abstract public class Achievment extends GameObject {
    protected boolean isAchieved;

    abstract public boolean isAchieved();


    public void write(Json json) {
        super.write(json);
    }

    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);
    }
}
