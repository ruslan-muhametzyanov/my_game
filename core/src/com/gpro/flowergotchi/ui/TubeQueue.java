package com.gpro.flowergotchi.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by user on 12.01.2016.
 */
class TubeQueue {
    private List<Pair<Image, Tube>> Tubes;
    private Map<IntGameVariables, Pair<String, IntegerGameVar>> tubeData;
    private ResourceManager manager;
    private Stage stage;

    public TubeQueue(Stage stage, ResourceManager manager, Plant plant) {
        this.manager = manager;
        this.stage = stage;
        Tubes = new ArrayList<Pair<Image, Tube>>();


        tubeData = new TreeMap<IntGameVariables, Pair<String, IntegerGameVar>>();
        tubeData.put(IntGameVariables.Var_Water, new Pair("water", new IntegerGameVar(plant.getIntVar(IntGameVariables.Var_Water))));
        tubeData.put(IntGameVariables.Var_Light, new Pair("light", new IntegerGameVar(plant.getIntVar(IntGameVariables.Var_Light))));
        tubeData.put(IntGameVariables.Var_Insects, new Pair("insect", new IntegerGameVar(plant.getIntVar(IntGameVariables.Var_Insects))));
        tubeData.put(IntGameVariables.Var_Loosening, new Pair("loosening", new IntegerGameVar(plant.getIntVar(IntGameVariables.Var_Loosening))));
        //tubeData.put(IntGameVariables.Var_NextStage, new Pair("percent", new IntegerGameVar(plant.getIntVar(IntGameVariables.Var_NextStage))));
    }

    public void update(Plant plant) {
        for (Map.Entry<IntGameVariables, Pair<String, IntegerGameVar>> e : tubeData.entrySet()) {
            if (plant.getIntVar(e.getKey()).get().compareTo(e.getValue().getSecond().get()) != 0) {
                boolean exists = false;
                for (Pair<Image, Tube> p : Tubes) {
                    if (p.getSecond().getVariable().getTag() == e.getKey()) {
                        p.getSecond().updateVariable(plant.getIntVar(e.getKey()));
                        e.getValue().getSecond().set(plant.getIntVarVal(e.getKey()));
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    Tube tube = new Tube(e.getValue().getSecond(),
                            manager.getTexture("statistics/tubeBar.png"),
                            manager.getTexture("statistics/lightBar.png"),
                            true, null);
                    tube.updateVariable(e.getValue().getSecond(), true);
                    TextureRegion reg = new TextureRegion(manager.getTexture("statistics/" + e.getValue().getFirst() + "Icon.png"));
                    reg.flip(true, false);
                    Image icon = new Image(reg);
                    this.addTube(new Pair<Image, Tube>(icon, tube), new IntegerGameVar(plant.getIntVar(e.getKey())));
                    e.getValue().getSecond().set(plant.getIntVarVal(e.getKey()));
                }
            }
        }
    }

    public void addTube(final Pair<Image, Tube> pair, final IntegerGameVar newVar) {
        pair.getSecond().setPosition(stage.getViewport().getWorldWidth() + 200, stage.getViewport().getWorldHeight() - 100 - 100 * Tubes.size());
        pair.getSecond().addAction(Actions.sequence(Actions.moveBy(-400, 0, 1.5f, Interpolation.pow3Out), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        pair.getSecond().updateVariable(newVar);
                    }
                }), Actions.delay(2.0f), Actions.moveBy(400, 0, 1.5f, Interpolation.pow3In),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Tubes.remove(Tubes.indexOf(pair));
                    }
                }), Actions.removeActor(pair.getSecond())));
        pair.getFirst().setPosition(stage.getViewport().getWorldWidth() + 180 - pair.getSecond().getWidth(), stage.getViewport().getWorldHeight() - 130 - 100 * Tubes.size());
        pair.getFirst().setAlign(Align.topLeft);
        pair.getFirst().setOrigin(Align.center);
        pair.getFirst().setRotation(180);
        pair.getFirst().addAction(Actions.sequence(Actions.moveBy(-400, 0, 1.5f, Interpolation.pow3Out), Actions.delay(2.0f), Actions.moveBy(400, 0, 1.5f, Interpolation.pow3In),
                Actions.removeActor(pair.getFirst())));
        pair.getFirst().setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_UIButtons))));
        Tubes.add(pair);
        stage.addActor(pair.getFirst());
        stage.addActor(pair.getSecond());
    }
}
