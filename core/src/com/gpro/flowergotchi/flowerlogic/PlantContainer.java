package com.gpro.flowergotchi.flowerlogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameObjectManager;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.ui.GameUI;

import java.util.ArrayList;
import java.util.List;


abstract public class PlantContainer extends GameObject implements GameObject.DrawableGameObject, GameObjectManager.Loadable {
    protected List<ContainerPlace> Plants;

    PlantContainer() {
        super();
    }

    PlantContainer(GameObjectTypes type) {
        super(type);
        Plants = new ArrayList<ContainerPlace>();
    }

    public void write(Json json) {
        super.write(json);
        json.writeValue("Plants", Plants);
    }

    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);
        JsonValue plants = jsonMap.get("Plants");
        List<ContainerPlace> plantsList = new ArrayList<ContainerPlace>();
        for (JsonValue entry = plants.child(); entry != null; entry = entry.next()) {
            ContainerPlace place = json.readValue(ContainerPlace.class, entry);
            plantsList.add(place);
        }
        Plants = plantsList;
    }

    public abstract void loadGraphics(ResourceManager manager);

    abstract public ContainerPlace addPlant(Plant plant);

    public abstract int getCapacity();

    public ContainerPlace getPlantPlace(int number) {
        return Plants.get(number);
    }

    abstract public void addToUI(ResourceManager resourceManager, GameUI ui);

    public static class ContainerPlace implements Json.Serializable {

        public Vector2 startPoint;
        public Vector2 entryPoints;
        public Integer plantID = -1;

        public ContainerPlace() {

        }

        public ContainerPlace(Vector2 startPoint, Vector2 entryPoints, Integer plantID) {
            this.startPoint = startPoint;
            this.entryPoints = entryPoints;
            this.plantID = plantID;
        }

        public ContainerPlace(Vector2 startPoint, Vector2 entryPoints, Plant plant) {
            this(startPoint, entryPoints, plant == null ? -1 : plant.getID());
        }

        public void write(Json json) {
            json.writeValue("startPoint", startPoint);
            json.writeValue("entryPoints", entryPoints);
            json.writeValue("plantID", plantID);
        }

        public void read(Json json, JsonValue jsonMap) {
            startPoint = new Vector2(jsonMap.get("startPoint").get("x").asFloat(), jsonMap.get("startPoint").get("y").asFloat());
            entryPoints = new Vector2(jsonMap.get("entryPoints").get("x").asFloat(), jsonMap.get("entryPoints").get("y").asFloat());
            plantID = jsonMap.get("plantID").asInt();
        }
    }
}

