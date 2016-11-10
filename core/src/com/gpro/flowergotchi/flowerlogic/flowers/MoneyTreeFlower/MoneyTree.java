package com.gpro.flowergotchi.flowerlogic.flowers.MoneyTreeFlower;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.util.Pair;

public class MoneyTree extends Plant {

    public MoneyTree() {
        super();
    }

    public MoneyTree(GameWorld world, Vector2 position) {
        super(world);
        setPosition(position);
        loadDefaultValues();
    }

    @Override
    public void init() {
        stageCount = 23;
        stagesArray = new Pair[stageCount];
        stagesArray[0] = new Pair<String, Integer>("moneytree/1", 0);
        stagesArray[1] = new Pair<String, Integer>("moneytree/2", 2);
        stagesArray[2] = new Pair<String, Integer>("moneytree/3", 10);
        stagesArray[3] = new Pair<String, Integer>("moneytree/4", 22);
        stagesArray[4] = new Pair<String, Integer>("moneytree/5", 50);
        stagesArray[5] = new Pair<String, Integer>("moneytree/6", 100);
        stagesArray[6] = new Pair<String, Integer>("moneytree/7", 200);
        stagesArray[7] = new Pair<String, Integer>("moneytree/8", 400);
        stagesArray[8] = new Pair<String, Integer>("moneytree/9", 650);
        stagesArray[9] = new Pair<String, Integer>("moneytree/10", 1000);
        stagesArray[10] = new Pair<String, Integer>("moneytree/11", 1400);
        stagesArray[11] = new Pair<String, Integer>("moneytree/12", 1800);
        stagesArray[12] = new Pair<String, Integer>("moneytree/13", 2400);
        stagesArray[13] = new Pair<String, Integer>("moneytree/14", 3400);
        stagesArray[14] = new Pair<String, Integer>("moneytree/15", 5500);
        stagesArray[15] = new Pair<String, Integer>("moneytree/16", 7500);
        stagesArray[16] = new Pair<String, Integer>("moneytree/17", 10000);
        stagesArray[17] = new Pair<String, Integer>("moneytree/18", 12500);
        stagesArray[18] = new Pair<String, Integer>("moneytree/19", 17000);
        stagesArray[19] = new Pair<String, Integer>("moneytree/20", 22000);
        stagesArray[20] = new Pair<String, Integer>("moneytree/21", 28000);
        stagesArray[21] = new Pair<String, Integer>("moneytree/22", 32000);
        stagesArray[22] = new Pair<String, Integer>("moneytree/23", 38000);
        stagesArray[22] = new Pair<String, Integer>("moneytree/23", 45000);
    }

    @Override
    public PlantDifficulty getDifficulty() {
        if (getIntVarVal(IntGameVariables.Var_Progress) < 100) {
            return PlantDifficulty.Beginner;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 1000) {
            return PlantDifficulty.VeryEasy;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 5000) {
            return PlantDifficulty.Easy;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 18000) {
            return PlantDifficulty.Normal;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 30000) {
            return PlantDifficulty.Hard;
        }
        return PlantDifficulty.VeryHard;
    }

    @Override
    public Image updateImage(ResourceManager resourceManager) {
        Image texture = setNewImage(resourceManager);
        texture.setPosition(this.getPlantPlace().startPoint.x - 44, this.getPlantPlace().startPoint.y + 55);
        return texture;
    }
}
