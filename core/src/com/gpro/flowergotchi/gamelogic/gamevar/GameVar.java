package com.gpro.flowergotchi.gamelogic.gamevar;

import com.badlogic.gdx.utils.Json;

abstract public class GameVar<TAG, TYPE> implements Json.Serializable {


    TYPE var;
    TAG tag;

    GameVar(TAG tag) {
        this.tag = tag;
    }

    public TAG getTag() {
        return tag;
    }

    public TYPE get() {
        return var;
    }

    public void set(TYPE newVar) {
        var = newVar;
    }
}
