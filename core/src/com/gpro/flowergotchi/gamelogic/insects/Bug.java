package com.gpro.flowergotchi.gamelogic.insects;

import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.ui.GameUI;

public class Bug extends Insect implements GameObject.DrawableGameObject {
    private BugActor bugActor;

    public Bug(GameWorld world) {
        super(world);
    }

    public void addToUI(ResourceManager resourceManager, GameUI ui) {
        bugActor = new BugActor(ui);
    }

    public void onUpdateObjectsClient(ResourceManager resourceManager, GameUI ui) {

    }

}