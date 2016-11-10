package com.gpro.flowergotchi.gamelogic.achievments;

import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.ui.GameUI;

/**
 * Created by user on 24.02.2016.
 */
public class SingleAchievment extends Achievment {


    @Override
    public boolean isAchieved() {
        return false;
    }

    @Override
    public void onUpdateObjectsClient(ResourceManager resourceManager, GameUI ui) {

    }
}
