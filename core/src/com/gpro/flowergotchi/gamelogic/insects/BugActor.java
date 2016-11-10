package com.gpro.flowergotchi.gamelogic.insects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gpro.flowergotchi.AndroidCallbackTypes;
import com.gpro.flowergotchi.ui.GameUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class BugActor extends InsectActor {
    public BugActor(final GameUI ui) {
        final Random random = new Random();

        this.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 1; i <= partCount; ++i) {
                    InsectPart part = new InsectPart(parts[i - 1], new Vector2(getX() + random.nextFloat() + 107 / 2, getY() + random.nextFloat() + 62 / 2),
                            new Vector2(random.nextInt(100) - 50, random.nextInt(100) - 50));
                    ui.getStage().addActor(part);
                }
                remove();
                List<Integer> list = new ArrayList<Integer>();
                list.add(1);
                ui.gameCallback().clientButtonCallback(AndroidCallbackTypes.CB_Insects, list);

                return true;
            }
        });


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

    }
}
