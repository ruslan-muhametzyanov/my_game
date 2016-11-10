package com.gpro.flowergotchi.flowerlogic.pots;

import com.badlogic.gdx.math.Vector2;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.flowerlogic.Pot;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.util.Pair;

public class KeramicPot1 extends Pot implements GameObject.DrawableGameObject {
    public KeramicPot1() {
        super();
    }

    public KeramicPot1(GameWorld world) {
        super(world);
        initialize();
    }

    @Override
    public void init() {
        fileNameBack = "pots/pot1back.png";
        fileNameFront = "pots/pot1front.png";
        capacity = 1;
        insectZone = new Pair<Vector2, Vector2>(new Vector2(87, 60), new Vector2(250, 70));
    }

    private void initialize() {
        setPosition(new Vector2((FlowergotchiGame.screenWidth - 377) / 2, 820));
        Plants.add(new ContainerPlace(new Vector2(getPosition().x - 60, getPosition().y - 415), new Vector2(getPosition().x + 75, getPosition().y + 15), (Plant) null));
    }
}
