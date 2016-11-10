package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.ui.GameUI;

import java.util.ArrayList;

abstract public class GameObject implements Json.Serializable {
    private Integer ID;
    private Vector2 position;
    private GameObjectTypes type;
    private long timeSinceLastUpdate;

    private ArrayList<Integer> clientSideVars;

    protected GameObject() {
        type = GameObjectTypes.GO_Abstract;
    }

    protected GameObject(GameObjectTypes _type) {
        position = new Vector2(0, 0);
        type = _type;
        ID = -1;
        clientSideVars = new ArrayList<Integer>();
    }

    public void write(Json json) {
        json.writeValue("ID", ID);
        json.writeValue("position", position);
        json.writeValue("type", type);
        json.writeValue("timeSinceLastUpdate", timeSinceLastUpdate);
        json.writeValue("clientSideVars", clientSideVars);

    }

    public String getObjectName() {
        return this.getClass().getName() + (String.valueOf(ID));
    }

    public void read(Json json, JsonValue jsonMap) {
        ID = jsonMap.get("ID").asInt();
        position = new Vector2(jsonMap.get("position").get("x").asFloat(), jsonMap.get("position").get("y").asFloat());
        type = GameObjectTypes.valueOf(jsonMap.get("type").asString());
        timeSinceLastUpdate = jsonMap.get("timeSinceLastUpdate").asLong();
        clientSideVars = json.readValue(ArrayList.class, jsonMap.get("clientSideVars"));
    }

    protected void addToGame(GameObjectManager manager) {
        this.ID = manager.addObject(this);
    }

    public int getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public GameObjectTypes getType() {
        return type;
    }

    abstract public void onUpdateObjectsClient(ResourceManager resourceManager, GameUI ui);

    public Vector2 getPosition() {
        return position;
    }

    protected void setPosition(Vector2 position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public ArrayList<Integer> getClientside() {
        return clientSideVars;
    }

    public void updateClientside(ArrayList clientside) {
        this.clientSideVars = clientside;
    }

    public enum GameObjectTypes {
        GO_Abstract,
        GO_Plant,
        GO_Background,
        GO_Pot,
        GO_Decoration,
        GO_Event
    }

    public interface DrawableGameObject {
        void addToUI(ResourceManager resourceManager, GameUI ui);

    }
}