package com.gpro.flowergotchi.gamelogic.gamevar;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class StringGameVar extends GameVar<StringGameVariables, String> {
    public StringGameVar() {
        super(StringGameVariables.Str_NoType);
    }

    public StringGameVar(StringGameVariables tag) {
        super(tag);
    }

    public void read(Json json, JsonValue jsonMap) {
        tag = StringGameVariables.valueOf(jsonMap.get("tag").asString());
        var = jsonMap.get("var").asString();
    }

    public void write(Json json) {
        json.writeValue("tag", tag);
        json.writeValue("var", var);
    }
}