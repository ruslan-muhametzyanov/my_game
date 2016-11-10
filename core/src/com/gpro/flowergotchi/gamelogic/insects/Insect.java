package com.gpro.flowergotchi.gamelogic.insects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.util.Pair;

abstract public class Insect extends GameObject implements GameObject.DrawableGameObject {
    Pair<Vector2, Vector2> insectZone;

    Insect() {
        super(GameObjectTypes.GO_Decoration);
    }

    Insect(GameWorld world) {
        this();
        addToGame(world.gameObjectManager());
    }

    public void write(Json json) {
        super.write(json);
        json.writeValue("insectZone", insectZone);
    }

    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);
        insectZone = json.readValue(Pair.class, jsonMap.get("insectZone"));
    }
}

