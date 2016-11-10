package com.gpro.flowergotchi.gamelogic;

import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by user on 18.12.2015.
 */
public class ActorParameters {
    private final Map<IntGameVariables, IntegerGameVar> Parameters;

    public ActorParameters(IntegerGameVar... vars) {
        Parameters = new TreeMap<IntGameVariables, IntegerGameVar>();
        for (IntegerGameVar var : vars) {
            this.addParam(var);
        }
    }

    public void addParam(IntegerGameVar param) {
        Parameters.put(param.getTag(), param);
    }

    public ActorParameters setParam(IntGameVariables param, Integer newVal) {
        Parameters.get(param).set(newVal);
        return this;
    }

    public IntegerGameVar getParam(IntGameVariables param) {
        return Parameters.get(param);
    }

    public Map<IntGameVariables, IntegerGameVar> getParameters() {
        return Parameters;
    }
}
