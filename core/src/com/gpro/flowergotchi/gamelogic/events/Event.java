package com.gpro.flowergotchi.gamelogic.events;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameWorld;

public abstract class Event extends GameObject implements Json.Serializable {
    protected static final int messageDrawn = 0;
    protected boolean timed = true;
    protected float duration;
    protected int timer;
    protected boolean eventContinious;
    protected EventState state;

    // Main constructor
    // Основной конструктор
    public Event(int timeBeforeStart, float duration, boolean eventContinious) {
        this();

        this.duration = duration;
        this.eventContinious = eventContinious;
        this.state = EventState.E_CREATED;
        this.timer = -timeBeforeStart;
        if (duration == 0) {
            timed = false;
        }
    }

    public Event() {
        super(GameObjectTypes.GO_Event);
        setPosition(new Vector2(1, 1));
        this.getClientside().add(Event.messageDrawn, 0);
    }

    public float getDuration() {
        return duration;
    }

    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);

        state = EventState.valueOf(jsonMap.get("state").asString());
        eventContinious = jsonMap.get("eventContinious").asBoolean();
        timer = jsonMap.get("timer").asInt();
        duration = jsonMap.get("duration").asInt();
        timed = jsonMap.get("timed").asBoolean();
    }

    public void write(Json json) {
        super.write(json);

        json.writeValue("state", state);
        json.writeValue("eventContinious", eventContinious);
        json.writeValue("timer", timer);
        json.writeValue("duration", duration);
        json.writeValue("timed", timed);

    }

    abstract public void startEvent(GameWorld world);

    public void eventHandle(GameWorld world) {

    }

    abstract public boolean eventEndCondition(GameWorld world);

    abstract public boolean eventEnd(GameWorld world);

    boolean isEventContinious() {
        return eventContinious;
    }

    public EventState getState() {
        return state;
    }

    public void setState(EventState state) {
        this.state = state;
    }

    public void updateTimer() {
        timer += FlowergotchiGame.updateRate;
    }

    public int getTimer() {
        return timer;
    }

    public boolean isTimed() {
        return timed;
    }

    public void restart() {
        state = EventState.E_CREATED;
        timer = 0;
    }


    public enum EventState {
        E_CREATED,
        E_STARTED,
        E_PAUSED,
        E_ENDED
    }
}
