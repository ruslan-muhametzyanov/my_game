package com.gpro.flowergotchi.flowerlogic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gpro.flowergotchi.gamelogic.ActorParameters;
import com.gpro.flowergotchi.gamelogic.DrawOrderLevels;
import com.gpro.flowergotchi.gamelogic.GameObject;
import com.gpro.flowergotchi.gamelogic.GameObjectManager;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.gamelogic.ResourceManager;
import com.gpro.flowergotchi.gamelogic.Statistic;
import com.gpro.flowergotchi.gamelogic.gamevar.IntGameVariables;
import com.gpro.flowergotchi.gamelogic.gamevar.IntegerGameVar;
import com.gpro.flowergotchi.gamelogic.gamevar.StringGameVar;
import com.gpro.flowergotchi.gamelogic.gamevar.StringGameVariables;
import com.gpro.flowergotchi.gamelogic.insects.SmallBug;
import com.gpro.flowergotchi.ui.GameUI;
import com.gpro.flowergotchi.util.Pair;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

abstract public class Plant extends GameObject implements Json.Serializable, GameObjectManager.Loadable, GameObject.DrawableGameObject {
    public static final float Health_Low = 0.3f;
    public static final float Health_Med = 0.56f;
    public static final float Health_High = 1;
    public static final int spiderDamage = 20;
    public static final int insectDamage = 3;
    protected Pair<String, Integer>[] stagesArray;
    protected int stageCount;
    protected PlantContainer.ContainerPlace plantPlace;
    protected Map<IntGameVariables, IntegerGameVar> IntVariables;
    protected Map<StringGameVariables, StringGameVar> StringVariables;
    protected Integer planter;
    private String name;

    public Plant() {
        super(GameObjectTypes.GO_Plant);
        init();
    }

    public Plant(GameWorld world) {
        super(GameObjectTypes.GO_Plant);
        init();
        addToGame(world.gameObjectManager());
        IntVariables = new TreeMap<IntGameVariables, IntegerGameVar>();
        StringVariables = new TreeMap<StringGameVariables, StringGameVar>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void loadDefaultValues() {
        IntVariables.put(IntGameVariables.Var_Water, new IntegerGameVar(IntGameVariables.Var_Water, -180, 180));
        IntVariables.put(IntGameVariables.Var_Light, new IntegerGameVar(IntGameVariables.Var_Light, -180, 180, 0));
        IntVariables.put(IntGameVariables.Var_Insects, new IntegerGameVar(IntGameVariables.Var_Insects, 0, 8, 0));
        IntVariables.put(IntGameVariables.Var_InsectsTimer, new IntegerGameVar(IntGameVariables.Var_InsectsTimer, 0, 15, 0));
        IntVariables.put(IntGameVariables.Var_Loosening, new IntegerGameVar(IntGameVariables.Var_Loosening, -360, 360));
        IntVariables.put(IntGameVariables.Var_Health, new IntegerGameVar(IntGameVariables.Var_Health, 0, 45000));
        IntVariables.put(IntGameVariables.Var_Progress, new IntegerGameVar(IntGameVariables.Var_Progress, 0, 100000, 0));
        IntVariables.put(IntGameVariables.Var_NextStage, new IntegerGameVar(IntGameVariables.Var_NextStage, 0, 100, 0));
        IntVariables.put(IntGameVariables.Var_Time, new IntegerGameVar(IntGameVariables.Var_Time, 0, Integer.MAX_VALUE, 0));
        IntVariables.put(IntGameVariables.Var_Spider, new IntegerGameVar(IntGameVariables.Var_Spider, 0, 1, 0));
        IntVariables.put(IntGameVariables.Var_SpiderTimer, new IntegerGameVar(IntGameVariables.Var_SpiderTimer, 0, 35, 0));
        IntVariables.put(IntGameVariables.Var_Cat, new IntegerGameVar(IntGameVariables.Var_Cat, 0, 1, 0));
        IntVariables.put(IntGameVariables.Var_CatTimer, new IntegerGameVar(IntGameVariables.Var_CatTimer, 0, 45, 0));
        IntVariables.put(IntGameVariables.Var_StageNum, new IntegerGameVar(IntGameVariables.Var_StageNum, 0, stageCount, 0));
        IntVariables.put(IntGameVariables.Var_Score, new IntegerGameVar(IntGameVariables.Var_Score, 0, 10000000, 0));

    }

    public boolean isDead() {
        return getIntVarVal(IntGameVariables.Var_Health) <= IntVariables.get(IntGameVariables.Var_Health).getMin();
    }

    public boolean isFinished() {
        return getIntVarVal(IntGameVariables.Var_StageNum) == stageCount;
    }

    public float getHealthType(IntegerGameVar health) {
        if (health.getPercent() > Health_Med) {
            return Health_High;
        } else if (health.getPercent() > Health_Low) {
            return Health_Med;
        } else {
            return Health_Low;
        }
    }

    public float getCurrentStageProgress() {
        int stageNum = getStageNum();
        if (stageNum < stageCount) {
            return (float) (getIntVarVal(IntGameVariables.Var_Progress) - stagesArray[stageNum - 1].getSecond()) / (stagesArray[stageNum].getSecond() - stagesArray[stageNum - 1].getSecond());
        }
        return 1;
    }

    protected int getStageNum() {
        int i;
        for (i = 0; i < stageCount; ++i) {
            if (this.getIntVarVal(IntGameVariables.Var_Progress) < stagesArray[i].getSecond()) {
                return i;
            }
        }
        return stageCount;
    }

    public void poison(){
        float percent = getCurrentStageProgress();
        int stageNum = getStageNum();
        if (stageNum < stageCount-1) {
            int newValue = (int) ((stagesArray[stageNum+1].getSecond()-stagesArray[stageNum].getSecond())*percent
                    + stagesArray[stageNum].getSecond());
            setIntVarVal(IntGameVariables.Var_Progress, newValue);
        }else if (stageNum==stageCount-1){
            int newValue = stagesArray[stageNum].getSecond();
            setIntVarVal(IntGameVariables.Var_Progress, newValue);
        }
    }

    public EnumSet<NotificationTypes> doLogic(GameObjectManager manager) {
        EnumSet<NotificationTypes> returnType = EnumSet.of(NotificationTypes.N_Normal);

        Random rand = new Random();

        this.setIntVarVal(IntGameVariables.Var_Water, this.getIntVarVal(IntGameVariables.Var_Water) - 1);
        this.setIntVarVal(IntGameVariables.Var_Loosening, this.getIntVarVal(IntGameVariables.Var_Loosening) - 1);
        this.setIntVarVal(IntGameVariables.Var_SpiderTimer, this.getIntVarVal(IntGameVariables.Var_SpiderTimer) + 1);
        this.setIntVarVal(IntGameVariables.Var_InsectsTimer, this.getIntVarVal(IntGameVariables.Var_InsectsTimer) + 1);
        this.setIntVarVal(IntGameVariables.Var_CatTimer, this.getIntVarVal(IntGameVariables.Var_CatTimer) + 1);
        this.setIntVarVal(IntGameVariables.Var_StageNum, getStageNum());

        if (manager.getWorld().isLightEnabled()) {
            this.setIntVarVal(IntGameVariables.Var_Light, this.getIntVarVal(IntGameVariables.Var_Light) + 1);
        } else {
            this.setIntVarVal(IntGameVariables.Var_Light, this.getIntVarVal(IntGameVariables.Var_Light) - 1);
        }

        if (this.getIntVarVal(IntGameVariables.Var_SpiderTimer).equals(this.getIntVar(IntGameVariables.Var_SpiderTimer).getMax()) && rand.nextFloat() > 0.9) {
            this.setIntVarVal(IntGameVariables.Var_SpiderTimer, 0);
            this.setIntVarVal(IntGameVariables.Var_Spider, 1);
        }

        if (this.getIntVarVal(IntGameVariables.Var_InsectsTimer).equals(this.getIntVar(IntGameVariables.Var_InsectsTimer).getMax()) && rand.nextFloat() > 0.9
                && this.getIntVarVal(IntGameVariables.Var_Insects) < this.getIntVar(IntGameVariables.Var_Insects).getMax()) {
            this.setIntVarVal(IntGameVariables.Var_Insects, this.getIntVarVal(IntGameVariables.Var_Insects) + 1);
            new SmallBug(manager.getWorld(), (Pot) manager.getObjectByID(planter));
            this.setIntVarVal(IntGameVariables.Var_InsectsTimer, 0);
        }

        if (this.getIntVarVal(IntGameVariables.Var_CatTimer).equals(this.getIntVar(IntGameVariables.Var_CatTimer).getMax()) && rand.nextFloat() > 0.95) {
            if (this.getIntVarVal(IntGameVariables.Var_Cat) == 0) {
                this.setIntVarVal(IntGameVariables.Var_CatTimer, 0);
                this.setIntVarVal(IntGameVariables.Var_Cat, 1);
            }
        }

        int addHealth = 0;
        boolean dontIncrease = false;
        if (this.getIntVarVal(IntGameVariables.Var_Water) < 0) {
            dontIncrease = true;
            addHealth += this.getIntVarVal(IntGameVariables.Var_Water);
            returnType.add(NotificationTypes.N_NeedsWater);
        }
        if (this.getIntVar(IntGameVariables.Var_Light).getPercent() < 0.25) {
            dontIncrease = true;
            addHealth += this.getIntVarVal(IntGameVariables.Var_Light);
            returnType.add(NotificationTypes.N_TooSmallLight);
        }
        if (this.getIntVar(IntGameVariables.Var_Light).getPercent() > 0.75) {
            dontIncrease = true;
            addHealth -= this.getIntVarVal(IntGameVariables.Var_Light);
            returnType.add(NotificationTypes.N_TooMuchLight);
        }

        if (this.getIntVarVal(IntGameVariables.Var_Loosening) < 0) {
            dontIncrease = true;
            addHealth += this.getIntVarVal(IntGameVariables.Var_Loosening);
            returnType.add(NotificationTypes.N_NeedsLoosening);
        }
        if (this.getIntVarVal(IntGameVariables.Var_Insects) > 2) {
            dontIncrease = true;
            addHealth -= insectDamage * this.getIntVarVal(IntGameVariables.Var_Insects);
            returnType.add(NotificationTypes.N_RemoveInsects);
        }
        if (this.getIntVarVal(IntGameVariables.Var_Spider) > 0) {
            dontIncrease = true;
            addHealth -= spiderDamage;
            returnType.add(NotificationTypes.N_RemoveSpider);
        }

        if (!dontIncrease) {
            addHealth += this.getIntVarVal(IntGameVariables.Var_Water) + this.getIntVarVal(IntGameVariables.Var_Light) + this.getIntVarVal(IntGameVariables.Var_Loosening);
            if (this.getIntVar(IntGameVariables.Var_Water).getPercent() > 0.9 &&
                    (this.getIntVar(IntGameVariables.Var_Light).getPercent() <= 0.6 && this.getIntVar(IntGameVariables.Var_Light).getPercent() >= 0.4)
                    && this.getIntVar(IntGameVariables.Var_Loosening).getPercent() > 0.9 && this.getIntVarVal(IntGameVariables.Var_Insects) == 0) {
                addHealth *= 2;
            }
        }

        this.setIntVarVal(IntGameVariables.Var_Health, this.getIntVarVal(IntGameVariables.Var_Health) + addHealth);

        if (this.getIntVarVal(IntGameVariables.Var_Health) > Health_Med) {
            this.getIntVar(IntGameVariables.Var_Progress).add(1);
        }
        this.setIntVarVal(IntGameVariables.Var_NextStage, (int) (getCurrentStageProgress() * 100));
        this.getIntVar(IntGameVariables.Var_Time).add(1);

        this.calculateScore(manager);

        return returnType;
    }

    private void calculateScore(GameObjectManager manager) {
        final int spiderCoeff = 40;
        final int catCoeff = 30;
        final int insectCoeff = 15;
        final int starCoeff = 50;
        final int waterCoeff = 2;
        Integer score = this.getIntVarVal(IntGameVariables.Var_Progress);
        score += manager.getWorld().getStatistic().getSpider() * spiderCoeff;
        score += manager.getWorld().getStatistic().getCat() * catCoeff;
        score += manager.getWorld().getStatistic().getInsect() * insectCoeff;
        score += manager.getWorld().getStatistic().getStar() * starCoeff;
        score += manager.getWorld().getStatistic().getWater() * waterCoeff;
        this.setIntVarVal(IntGameVariables.Var_Score, score);
    }

    public void onUpdateObjectsClient(ResourceManager resourceManager, GameUI ui) {
        Image texture = (Image) ui.getActorByName(this.getObjectName());

        if (this.getHealthType(getIntVar(IntGameVariables.Var_Health)) != this.getHealthType(((ActorParameters) texture.getUserObject()).getParam(IntGameVariables.Var_Health))
                || this.getStageNum() != ((ActorParameters) texture.getUserObject()).getParam(IntGameVariables.Var_StageProgress).get()) {
            texture.setName("");
            texture.addAction(Actions.sequence(Actions.alpha(1.0f, 1.7f, Interpolation.pow2), Actions.removeActor(texture)));

            Image newtexture = updateImage(resourceManager);
            newtexture.setColor(newtexture.getColor().r, newtexture.getColor().g, newtexture.getColor().b, 0);
            newtexture.addAction(Actions.alpha(1.0f, 2.0f, Interpolation.pow2));

            ui.getStage().addActor(newtexture);
        }
    }

    abstract public void init();

    protected String getTextureFromStage(IntegerGameVar health) {
        int stage = getStageNum();
        if (getHealthType(health) == Health_Med) {
            return stagesArray[stage - 1].getFirst() + "a.png";
        } else if (getHealthType(health) == Health_Low) {
            return stagesArray[stage - 1].getFirst() + "b.png";
        }
        return stagesArray[stage - 1].getFirst() + ".png";

    }

    public void addToUI(ResourceManager resourceManager, GameUI ui) {
        Image texture = updateImage(resourceManager);
        ui.getStage().addActor(texture);
    }

    protected Image setNewImage(ResourceManager resourceManager) {
        Texture tex = resourceManager.getTexture(getTextureFromStage(this.getIntVar(IntGameVariables.Var_Health)));

        TextureRegion reg = new TextureRegion(tex, tex.getWidth(), tex.getHeight());
        reg.flip(false, true);
        Image texture = new Image(reg);
        texture.setUserObject(new ActorParameters(new IntegerGameVar(IntGameVariables.Var_DrawLevel, (DrawOrderLevels.D_Flower)),
                new IntegerGameVar(IntGameVariables.Var_Health, getIntVarVal(IntGameVariables.Var_Health)),
                new IntegerGameVar(IntGameVariables.Var_StageProgress, getStageNum())));
        texture.setName(this.getObjectName());
        texture.setOrigin(texture.getWidth() / 2, texture.getHeight() / 2);
        return texture;
    }

    public abstract Image updateImage(ResourceManager resourceManager);

    public abstract PlantDifficulty getDifficulty();

    public void plant(PlantContainer planter) {
        this.planter = planter.getID();
        PlantContainer.ContainerPlace p = planter.addPlant(this);
        this.setPosition(p.startPoint); //Todo: remove
        plantPlace = p;
    }

    public IntegerGameVar getIntVar(IntGameVariables variable) {
        return IntVariables.get(variable);
    }

    public Integer getIntVarVal(IntGameVariables variable) {
        return this.IntVariables.get(variable).get();
    }

    public void setIntVarVal(IntGameVariables variable, Integer newval) {
        IntegerGameVar var = this.IntVariables.get(variable);
        var.set(newval);
        this.IntVariables.put(variable, var);
    }

    public void write(Json json) {
        super.write(json);
        json.writeValue("IntVariables", IntVariables);
        json.writeValue("StringVariables", StringVariables);
        json.writeValue("planter", planter);
        json.writeValue("plantPlace", plantPlace);
        json.writeValue("name", name);
    }

    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);
        JsonValue intVariables = jsonMap.get("IntVariables");
        Map<IntGameVariables, IntegerGameVar> intMap = new TreeMap<IntGameVariables, IntegerGameVar>();
        for (JsonValue entry = intVariables.child(); entry != null; entry = entry.next()) {
            IntGameVariables key = IntGameVariables.valueOf(entry.get("tag").asString());
            IntegerGameVar value = json.readValue(IntegerGameVar.class, entry);
            intMap.put(key, value);
        }
        JsonValue stringVariables = jsonMap.get("StringVariables");
        Map<StringGameVariables, StringGameVar> stringMap = new TreeMap<StringGameVariables, StringGameVar>();
        for (JsonValue entry = stringVariables.child(); entry != null; entry = entry.next()) {
            StringGameVariables key = StringGameVariables.valueOf(entry.get("tag").asString());
            StringGameVar value = json.readValue(StringGameVar.class, entry);
            stringMap.put(key, value);
        }
        IntVariables = intMap;
        StringVariables = stringMap;

        planter = json.readValue(Integer.class, jsonMap.get("planter"));
        plantPlace = json.readValue(PlantContainer.ContainerPlace.class, jsonMap.get("plantPlace"));
        name = jsonMap.get("name").asString();
    }

    protected PlantContainer.ContainerPlace getPlantPlace() {
        return plantPlace;
    }

    public void loadGraphics(ResourceManager resourceManager) {
        resourceManager.getTexture(getTextureFromStage(this.getIntVar(IntGameVariables.Var_Health)));
    }

    public enum PlantDifficulty {
        Beginner,
        VeryEasy,
        Easy,
        Normal,
        Hard,
        VeryHard
    }

    public enum NotificationTypes {
        N_Normal,
        N_NeedsWater,
        N_TooMuchLight,
        N_TooSmallLight,
        N_NeedsLoosening,
        N_RemoveInsects,
        N_RemoveSpider,
        N_Dead,
        N_PauseGame
    }

    public static class Parameters {
        public final String flowerClass;
        public final String flowerName;

        public Parameters(String flowerClass, String flowerName) {
            this.flowerClass = flowerClass;
            this.flowerName = flowerName;
        }
    }
}
