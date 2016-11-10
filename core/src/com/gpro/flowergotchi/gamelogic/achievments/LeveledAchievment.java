package com.gpro.flowergotchi.gamelogic.achievments;

/**
 * Created by user on 24.02.2016.
 */
abstract public class LeveledAchievment extends Achievment {
    private int levelCount;

    @Override
    public boolean isAchieved() {
        return false;
    }
}
