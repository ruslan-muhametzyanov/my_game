package com.gpro.flowergotchi.flowerlogic.flowers.RosaFlower;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.util.Pair;

public class Rosa extends Plant {

    public Rosa() {
        super();
    }

    public Rosa(GameWorld world, Vector2 position) {
        super(world);
        setPosition(position);
        loadDefaultValues();
    }

    public void init() {
        stageCount = 23;
        stagesArray = new Pair[stageCount];
        stagesArray[0] = new Pair<String, Integer>("rosa/23", 0);
        stagesArray[1] = new Pair<String, Integer>("rosa/22", 2);
        stagesArray[2] = new Pair<String, Integer>("rosa/21", 8);
        stagesArray[3] = new Pair<String, Integer>("rosa/20", 20);
        stagesArray[4] = new Pair<String, Integer>("rosa/19", 40);
        stagesArray[5] = new Pair<String, Integer>("rosa/18", 80);
        stagesArray[6] = new Pair<String, Integer>("rosa/17", 150);
        stagesArray[7] = new Pair<String, Integer>("rosa/16", 350);
        stagesArray[8] = new Pair<String, Integer>("rosa/15", 500);
        stagesArray[9] = new Pair<String, Integer>("rosa/14", 750);
        stagesArray[10] = new Pair<String, Integer>("rosa/13", 1200);
        stagesArray[11] = new Pair<String, Integer>("rosa/12", 1800);
        stagesArray[12] = new Pair<String, Integer>("rosa/11", 2800);
        stagesArray[13] = new Pair<String, Integer>("rosa/10", 4400);
        stagesArray[14] = new Pair<String, Integer>("rosa/9", 6700);
        stagesArray[15] = new Pair<String, Integer>("rosa/8", 8800);
        stagesArray[16] = new Pair<String, Integer>("rosa/7", 11000);
        stagesArray[17] = new Pair<String, Integer>("rosa/6", 13000);
        stagesArray[18] = new Pair<String, Integer>("rosa/5", 15500);
        stagesArray[19] = new Pair<String, Integer>("rosa/4", 18000);
        stagesArray[20] = new Pair<String, Integer>("rosa/3", 21000);
        stagesArray[21] = new Pair<String, Integer>("rosa/2", 25000);
        stagesArray[22] = new Pair<String, Integer>("rosa/1", 30000);
        stagesArray[22] = new Pair<String, Integer>("rosa/1", 35000);
    }

    @Override
    public PlantDifficulty getDifficulty() {
        if (getIntVarVal(IntGameVariables.Var_Progress) < 100) {
            return PlantDifficulty.Beginner;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 700) {
            return PlantDifficulty.VeryEasy;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 4000) {
            return PlantDifficulty.Easy;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 11000) {
            return PlantDifficulty.Normal;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 25000) {
            return PlantDifficulty.Hard;
        }
        return PlantDifficulty.VeryHard;
    }

    @Override
    public Image updateImage(ResourceManager resourceManager) {
        Image texture = setNewImage(resourceManager);
        texture.setPosition(this.getPlantPlace().startPoint.x + 125, this.getPlantPlace().startPoint.y + 30);
        return texture;
    }

}
