package com.gpro.flowergotchi.flowerlogic.flowers.MatricariaFlower;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.util.Pair;

public class Matricaria extends Plant {
    public Matricaria() {
        super();
    }

    public Matricaria(GameWorld world, Vector2 position) {
        super(world);
        setPosition(position);
        loadDefaultValues();
    }

    @Override
    public void init() {
        stageCount = 23;
        stagesArray = new Pair[stageCount];
        stagesArray[0] = new Pair<String, Integer>("matricaria/1", 0);
        stagesArray[1] = new Pair<String, Integer>("matricaria/2", 2);
        stagesArray[2] = new Pair<String, Integer>("matricaria/3", 6);
        stagesArray[3] = new Pair<String, Integer>("matricaria/4", 15);
        stagesArray[4] = new Pair<String, Integer>("matricaria/5", 40);
        stagesArray[5] = new Pair<String, Integer>("matricaria/6", 70);
        stagesArray[6] = new Pair<String, Integer>("matricaria/7", 150);
        stagesArray[7] = new Pair<String, Integer>("matricaria/8", 250);
        stagesArray[8] = new Pair<String, Integer>("matricaria/9", 400);
        stagesArray[9] = new Pair<String, Integer>("matricaria/10", 620);
        stagesArray[10] = new Pair<String, Integer>("matricaria/11", 800);
        stagesArray[11] = new Pair<String, Integer>("matricaria/12", 1100);
        stagesArray[12] = new Pair<String, Integer>("matricaria/13", 1500);
        stagesArray[13] = new Pair<String, Integer>("matricaria/14", 2500);
        stagesArray[14] = new Pair<String, Integer>("matricaria/15", 3500);
        stagesArray[15] = new Pair<String, Integer>("matricaria/16", 5000);
        stagesArray[16] = new Pair<String, Integer>("matricaria/17", 7000);
        stagesArray[17] = new Pair<String, Integer>("matricaria/18", 8500);
        stagesArray[18] = new Pair<String, Integer>("matricaria/19", 10000);
        stagesArray[19] = new Pair<String, Integer>("matricaria/20", 12000);
        stagesArray[20] = new Pair<String, Integer>("matricaria/21", 14400);
        stagesArray[21] = new Pair<String, Integer>("matricaria/22", 18000);
        stagesArray[22] = new Pair<String, Integer>("matricaria/23", 21000);
        stagesArray[22] = new Pair<String, Integer>("matricaria/23", 25000);
    }

    @Override
    public PlantDifficulty getDifficulty() {
        if (getIntVarVal(IntGameVariables.Var_Progress) < 100) {
            return PlantDifficulty.Beginner;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 400) {
            return PlantDifficulty.VeryEasy;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 2000) {
            return PlantDifficulty.Easy;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 7000) {
            return PlantDifficulty.Normal;
        } else if (getIntVarVal(IntGameVariables.Var_Progress) < 15000) {
            return PlantDifficulty.Hard;
        }
        return PlantDifficulty.VeryHard;
    }

    @Override
    public Image updateImage(ResourceManager resourceManager) {
        Image texture = setNewImage(resourceManager);
        texture.setPosition(this.getPlantPlace().startPoint.x, this.getPlantPlace().startPoint.y);
        return texture;
    }
}
