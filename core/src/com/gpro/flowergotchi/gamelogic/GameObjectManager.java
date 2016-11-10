package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.events.Event;
import com.gpro.flowergotchi.gamelogic.events.SpiderAttack;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.util.Rand;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class GameObjectManager implements Json.Serializable {
    private Integer objectCounter;
    private Map<Integer, GameObject> AllObjects;
    private GameWorld world;

    /**
     * Default constructor (for Json)
     */
    public GameObjectManager() {

    }

    /**
     * Default constructor
     *
     * @param _world world to bind
     */
    GameObjectManager(GameWorld _world) {
        AllObjects = new ConcurrentHashMap<Integer, GameObject>();
        objectCounter = 0;
        linkToWorld(_world);
    }

    /**
     * Json write to file
     *
     * @param json       JSON object
     */
    public void write(Json json) {
        json.writeValue("objectCounter", objectCounter);
        json.writeValue("AllObjects", AllObjects);
    }

    /**
     * Json read from file
     *
     * @param json       JSON object
     * @param jsonMap    JSON map object
     */
    public void read(Json json, JsonValue jsonMap) {
        objectCounter = jsonMap.get("objectCounter").asInt();
        JsonValue allObjects = jsonMap.get("AllObjects");
        Map<Integer, GameObject> objectsMap = new TreeMap<Integer, GameObject>();
        for (JsonValue entry = allObjects.child(); entry != null; entry = entry.next()) {
            try {
                Class className = Class.forName(entry.get("class").asString());
                Integer key = Integer.valueOf(entry.name());
                GameObject value = (GameObject) json.readValue(className, entry);
                objectsMap.put(key, value);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        AllObjects = objectsMap;
    }

    /**
     * Set world for this object manager
     *
     * @param world new world
     */
    public void linkToWorld(GameWorld world) {
        this.world = world;
    }

    /**
     * Load all loadable objects in object manager
     *
     * @param manager Resource manager to use
     */
    public void loadLoadable(ResourceManager manager) {
        for (Map.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject obj = it.getValue();
            if (obj instanceof Loadable) {
                ((Loadable) obj).loadGraphics(manager);
            }
        }
    }

    public void addObjectsToUI(ResourceManager manager, GameUI ui) {
        for (Map.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject obj = it.getValue();
            if (!ui.objectOnUI(obj.getID()) && obj instanceof GameObject.DrawableGameObject) {
                ((GameObject.DrawableGameObject) obj).addToUI(manager, ui);
                ui.setObjectOnUI(obj.getID());
            }
        }
    }

    public void manageEvents() {
        // Add new events
        Background back = this.getWorld().getActiveBack();
        if (Rand.randFloat(0, 1) < back.getInsectAttract()) {
            if (findFirstObject(SpiderAttack.class) == null) {
                SpiderAttack spiderAttack = new SpiderAttack();
                addObject(spiderAttack);
            }

        }
        handleEvents();
    }

    private void handleEvents() {
        for (ConcurrentHashMap.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject object = it.getValue();
            if (object.getType() == GameObject.GameObjectTypes.GO_Event) {
                Event ev = (Event) object;
                if (ev.getState() != Event.EventState.E_PAUSED) {
                    ev.updateTimer();
                    if (ev.getTimer() >= 0 && ev.getState() == Event.EventState.E_CREATED) {
                        ev.setState(Event.EventState.E_STARTED);
                        ev.startEvent(this.getWorld());
                    }
                    boolean toRemove = false;
                    if (ev.isTimed()) {
                        if (ev.getTimer() >= ev.getDuration() && ev.getState() == Event.EventState.E_STARTED) {
                            toRemove = true;
                        }
                    } else if (ev.eventEndCondition(this.getWorld())) {
                        toRemove = true;
                    }
                    if (toRemove) {
                        if (ev.eventEnd(this.getWorld())) {
                            this.removeObject(ev.getID());
                            return;
                        } else {
                            ev.restart();
                        }
                        continue;
                    }
                    ev.eventHandle(this.getWorld());
                }
            }
        }
    }


    /**
     * Get object from manager using its ID
     *
     * @param id id to use
     * @return object to return
     */
    public GameObject getObjectByID(Integer id) {
        return AllObjects.get(id);
    }

    /**
     * Add new object to manager
     *
     * @param object object to add
     * @return ID for new object
     */
    Integer addObject(GameObject object) {
        object.setID(objectCounter);
        AllObjects.put(objectCounter, object);
        ++objectCounter;

        return objectCounter - 1;
    }

    EnumSet<Plant.NotificationTypes> doPlantLogic(boolean isTutorialActive) {
        EnumSet<Plant.NotificationTypes> returnType = EnumSet.of(Plant.NotificationTypes.N_Normal);
        for (ConcurrentHashMap.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject object = it.getValue();
            if (object.getType() == GameObject.GameObjectTypes.GO_Plant) {
                Plant plant = (Plant) object;
                if (!isTutorialActive) {
                    returnType = plant.doLogic(this);
                    if (plant.isDead()) {
                        returnType.add(Plant.NotificationTypes.N_Dead);
                    }
                }

                return returnType; // FIXME : bad
            }
        }
        return returnType;
    }

    public int objectTypeCount(GameObject.GameObjectTypes gameObjectType) {
        int counter = 0;
        for (Map.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject object = it.getValue();
            if (object.getType() == gameObjectType) {
                ++counter;
            }
        }
        return counter;
    }

    public int objectCount(Class objectClass) {
        int counter = 0;
        for (Map.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject object = it.getValue();
            if (objectClass.isInstance(object)) {
                ++counter;
            }
        }
        return counter;
    }

    public void onUpdateObjects(ResourceManager manager, GameUI ui) {
        for (Map.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject object = it.getValue();
            object.onUpdateObjectsClient(manager, ui);
        }
    }

    public GameObject findFirstObject(Class<?> type) {
        for (Map.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject object = it.getValue();
            if (type.isAssignableFrom(object.getClass())) {
                return object;
            }
        }
        return null;
    }

    public void removeObject(Integer ID) {
        if (AllObjects.containsKey(ID)) {
            AllObjects.remove(ID);
        }
    }

    public GameWorld getWorld() {
        return world;
    }

    public void updateServiceFromClient(GameWorld world) {
        for (Map.Entry<Integer, GameObject> it : AllObjects.entrySet()) {
            GameObject object = it.getValue();
            GameObject analog = world.gameObjectManager().getObjectByID(object.getID());
            if (analog != null) {
                object.updateClientside(analog.getClientside());
            }
        }
    }

    public Map<Integer, GameObject> getManager() {
        return AllObjects;
    }

    /**
     * Interface for all objects that require loading before use
     */
    public interface Loadable {
        void loadGraphics(ResourceManager manager);
    }
}
