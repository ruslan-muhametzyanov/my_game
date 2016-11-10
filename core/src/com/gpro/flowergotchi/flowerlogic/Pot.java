package com.gpro.flowergotchi.flowerlogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameObjectManager;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.util.Pair;


abstract public class Pot extends PlantContainer implements GameObject.DrawableGameObject, GameObjectManager.Loadable {
    protected String fileNameBack;
    protected String fileNameFront;
    protected int capacity;
    protected Pair<Vector2, Vector2> insectZone;  /// insect moving zone

    IntegerGameVar volume;
    IntegerGameVar size;
    private boolean drawInside;                   /// whether draw the border or not

    public Pot() {
        super(GameObjectTypes.GO_Pot);
        init();
        drawInside = false;
    }

    public Pot(GameWorld world) {
        this();
        init();
        addToGame(world.getManager());
    }

    abstract public void init();

    public Pair<Vector2, Vector2> getInsectZone() {
        return insectZone;
    }

    public Pair<Vector2, Vector2> getPotInsectZone() {
        return new Pair<Vector2, Vector2>(new Vector2(insectZone.getFirst().x + getPosition().x, insectZone.getFirst().y + getPosition().y),
                new Vector2(insectZone.getSecond().x + getPosition().x, insectZone.getSecond().y + getPosition().y));
    }


    public boolean isDrawInside() {
        return drawInside;
    }

    public void setDrawInside(boolean drawInside) {
        this.drawInside = drawInside;
    }

    public void write(Json json) {
        super.write(json);
        json.writeValue("drawInside", isDrawInside());

    }

    public void loadGraphics(ResourceManager manager) {
        manager.getTexture(fileNameBack);
        manager.getTexture(fileNameFront);
    }

    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);
        setDrawInside(jsonMap.get("drawInside").asBoolean());
    }

    public ContainerPlace addPlant(Plant plant) {
        for (ContainerPlace p : Plants) {
            if (p.plantID == -1) {
                p.plantID = plant.getID();
                return p;
            }
        }
        return null;
    }

    public int getCapacity() {
        return capacity;
    }

    public void addToUI(ResourceManager manager, GameUI ui) {
        Texture backTex = manager.getTexture(fileNameBack);
        Texture frontTex = manager.getTexture(fileNameFront);
        TextureRegion backReg = new TextureRegion(backTex, backTex.getWidth(), backTex.getHeight());
        TextureRegion frontReg = new TextureRegion(frontTex, frontTex.getWidth(), frontTex.getHeight());
        backReg.flip(false, true);
        frontReg.flip(false, true);

        Image back, front;
        back = new Image(backReg);
        front = new Image(frontReg);
        front.setName(this.getObjectName());

        back.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PotBack))));
        front.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_PotFront))));
        back.setPosition(getPosition().x, getPosition().y);
        back.setOrigin(0, 0);
        front.setPosition(getPosition().x, getPosition().y);
        front.setOrigin(0, 0);

        ui.getStage().addActor(front);
        ui.getStage().addActor(back);
    }

    public void onUpdateObjectsClient(ResourceManager resourceManager, GameUI ui) {
        Image frontImg = (Image) ui.getActorByName(this.getObjectName());
        if (frontImg == null) {
            Gdx.app.log("NOTFOUND", "NOTFOUND");
            Gdx.app.exit();
            return;
        }

        if (isDrawInside()) {
            frontImg.setVisible(false);
        } else {
            frontImg.setVisible(true);
        }
    }

}
