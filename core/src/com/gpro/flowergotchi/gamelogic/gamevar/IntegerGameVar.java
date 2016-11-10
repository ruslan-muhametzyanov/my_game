package com.gpro.flowergotchi.gamelogic.gamevar;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class IntegerGameVar extends GameVar<IntGameVariables, Integer> {

    private Integer min;
    private Integer max;

    public IntegerGameVar() {
        super(IntGameVariables.Var_NoType);
    }

    public IntegerGameVar(IntGameVariables tag) {
        super(tag);
    }


    public IntegerGameVar(IntGameVariables tag, Integer _min, Integer _max) {
        this(tag);
        min = _min;
        max = _max;

        var = max;
    }

    public IntegerGameVar(IntGameVariables tag, Integer _value) {
        this(tag);
        min = Integer.MIN_VALUE;
        max = Integer.MAX_VALUE;
        set(_value);
    }

    public IntegerGameVar(IntGameVariables tag, Integer _min, Integer _max, Integer _value) {
        this(tag);
        min = _min;
        max = _max;
        this.set(_value);
    }

    public IntegerGameVar(IntegerGameVar copy) {
        this(copy.getTag());
        min = copy.getMin();
        max = copy.getMax();
        this.set(copy.get());
    }

    public boolean isMedLevel() {
        if (this.getTag() == IntGameVariables.Var_Light) {
            return this.getPercent() < 0.35 || this.getPercent() > 0.65;
        } else if (this.getTag() == IntGameVariables.Var_Insects) {
            return this.getPercent() > 0;
        }
        return this.getPercent() < 0.8;
    }

    public boolean isLowLevel() {
        if (this.getTag() == IntGameVariables.Var_Light) {
            return this.getPercent() < 0.25 || this.getPercent() > 0.75;
        } else if (this.getTag() == IntGameVariables.Var_Insects) {
            return this.getPercent() > 0.5;
        }
        return this.getPercent() < 0.5;
    }

    public void read(Json json, JsonValue jsonMap) {
        tag = IntGameVariables.valueOf(jsonMap.get("tag").asString());
        var = jsonMap.get("var").asInt();
        max = jsonMap.get("max").asInt();
        min = jsonMap.get("min").asInt();
    }

    public void write(Json json) {
        json.writeValue("tag", tag);
        json.writeValue("var", var);
        json.writeValue("max", max);
        json.writeValue("min", min);
    }

    public void set(Integer newVar) {
        if (newVar > max) {
            var = max;
        } else if (newVar < min) {
            var = min;
        } else {
            var = newVar;
        }
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public Float getPercent() {
        return (float) (var - min) / getRange();
    }

    public Integer getRange() {
        return max - min;
    }

    public void add(Integer val) {
        this.set(var + val);
    }

    public void sub(Integer val) {
        this.set(var - val);
    }
}
