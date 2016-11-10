package com.gpro.flowergotchi.flowerlogic.pots;

import com.badlogic.gdx.math.Vector2;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.flowerlogic.Pot;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.util.Pair;

public class HexPot extends Pot implements GameObject.DrawableGameObject {
    public HexPot() {
        super();
    }

    public HexPot(GameWorld world) {
        super(world);
        initialize();
    }

    @Override
    public void init() {
        fileNameBack = "pots/pot5back.png";
        fileNameFront = "pots/pot5front.png";
        capacity = 1;
        insectZone = new Pair<Vector2, Vector2>(new Vector2(96, 37), new Vector2(268, 45));
    }

    private void initialize() {
        setPosition(new Vector2((FlowergotchiGame.screenWidth - 366) / 2, 820));
        Plants.add(new ContainerPlace(new Vector2(getPosition().x - 67, getPosition().y - 415), new Vector2(getPosition().x + 75, getPosition().y + 15), (Plant) null));
    }
}
