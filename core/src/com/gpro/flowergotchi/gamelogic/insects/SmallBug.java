package com.gpro.flowergotchi.gamelogic.insects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.gpro.flowergotchi.flowerlogic.Pot;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameObjectManager;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.util.Pair;
import com.gpro.flowergotchi.util.Rand;

import java.util.Random;

public class SmallBug extends Insect implements GameObject.DrawableGameObject, GameObjectManager.Loadable {
    private static final float bugSpeed = 32f;
    protected RepeatAction pattern;
    private SmallBugActor bugActor;

    public SmallBug() {
        super();
    }

    public SmallBug(GameWorld world, final Pot pot) {
        super(world);
        this.insectZone = new Pair<Vector2, Vector2>(new Vector2(), new Vector2());
        this.insectZone.getFirst().x += pot.getInsectZone().getFirst().x;
        this.insectZone.getFirst().y += pot.getInsectZone().getFirst().y;
        this.insectZone.getSecond().x += pot.getInsectZone().getSecond().x;
        this.insectZone.getSecond().y += pot.getInsectZone().getSecond().y;
        this.insectZone.getFirst().x += pot.getPosition().x;
        this.insectZone.getFirst().y += pot.getPosition().y;
        this.insectZone.getSecond().x += pot.getPosition().x;
        this.insectZone.getSecond().y += pot.getPosition().y;
        Random random = new Random();
        setPosition(new Vector2(random.nextInt((int) (insectZone.getSecond().x - insectZone.getFirst().x) + 1) + insectZone.getFirst().x,
                random.nextInt((int) (insectZone.getSecond().y - insectZone.getFirst().y) + 1) + insectZone.getFirst().y));
    }

    private void generatePath() {
        boolean isFlipped = false;
        // Generate a path
        Vector2 oldPos = this.getPosition();
        Vector2 firstPos = new Vector2();
        pattern = new RepeatAction();
        SequenceAction sequence = new SequenceAction();
        sequence.setTarget(bugActor);
        int counter = 0;
        do {
            Vector2 newPos = new Vector2();
            do {
                newPos.x = Rand.randInt((int) insectZone.getFirst().x, (int) insectZone.getSecond().x);
                newPos.y = Rand.randInt((int) insectZone.getFirst().y, (int) insectZone.getSecond().y);
            } while (newPos.dst(oldPos) < 40 || newPos.x == oldPos.x || newPos.y == oldPos.y);
            isFlipped = flipActor(isFlipped, oldPos, sequence, newPos, false);
            if (counter == 0) {
                firstPos = new Vector2(newPos);
            }
            MoveToAction move = new MoveToAction();
            move.setDuration(newPos.dst(oldPos) / bugSpeed);
            move.setPosition(newPos.x, newPos.y);
            move.setInterpolation(Interpolation.pow2);
            sequence.addAction(move);
            oldPos = newPos;
            ++counter;
        } while (counter < 8);

        MoveToAction move = new MoveToAction();
        move.setDuration(getPosition().dst(oldPos) / bugSpeed);
        move.setPosition(getPosition().x, getPosition().y);
        move.setInterpolation(Interpolation.pow2);
        isFlipped = flipActor(isFlipped, oldPos, sequence, getPosition(), false);
        sequence.addAction(move);
        if (isFlipped) {
            flipActor(isFlipped, getPosition(), sequence, firstPos, true);
        }

        pattern.setCount(RepeatAction.FOREVER);
        pattern.setAction(sequence);

    }

    private boolean flipActor(boolean isFlipped, Vector2 oldPos, SequenceAction sequence, Vector2 newPos, boolean justflip) {
        if ((isFlipped && newPos.x > oldPos.x) || (!isFlipped && newPos.x < oldPos.x) || justflip) {
            final RunnableAction action = new RunnableAction();
            action.setTarget(bugActor);
            action.setRunnable(new Runnable() {
                @Override
                public void run() {
                    ((SmallBugActor) action.getTarget()).flip();
                }
            });
            isFlipped = !isFlipped;
            sequence.addAction(action);
        }
        return isFlipped;
    }

    public void addToUI(ResourceManager resourceManager, GameUI ui) {
        bugActor = new SmallBugActor(this.getID(), resourceManager);
        this.insectZone.getSecond().x -= bugActor.getWidth() / 3;
        this.insectZone.getFirst().x -= bugActor.getWidth() / 3;
        generatePath();
        bugActor.addToUI(ui, this.getPosition(), pattern);

    }

    public void onUpdateObjectsClient(ResourceManager resourceManager, GameUI ui) {

    }

    @Override
    public void loadGraphics(ResourceManager manager) {
        manager.getTexture("smallbug/bug.png");
    }
}