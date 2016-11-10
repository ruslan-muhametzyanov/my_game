package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Ruslan on 24.02.2016.
 */
public class Statistic implements Json.Serializable {

    public static int newCount;
    public static int newStar;
    private int spiderCount;
    private int waterCount;
    private int insectCount;
    private int starCount;
    private int catCount;

    public Statistic() {

    }

    public void newCount(int newCount, int newStar) {
        Statistic.newCount = newCount;
    }


    public void sumSpider() {
        spiderCount = spiderCount + newCount;
        newCount = 0;
    }

    public int getSpider() {
        return spiderCount;
    }

    public void sumWater() {
        waterCount = waterCount + newCount;
        newCount = 0;
    }

    public int getWater() {
        return waterCount;
    }

    public void sumInsect() {
        insectCount = insectCount + newCount;
        newCount = 0;
    }

    public int getInsect() {
        return insectCount;
    }

    public void sumStar() {
        starCount = starCount + newStar;
        newStar = 0;
    }

    public int getStar() {
        return starCount;
    }

    public void sumCat() {
        catCount = catCount + 1;
    }

    public int getCat() {
        return catCount;
    }

    @Override
    public void write(Json json) {
        json.writeValue("spiderCount", spiderCount);
        json.writeValue("waterCount", waterCount);
        json.writeValue("insectCount", insectCount);
        json.writeValue("starCount", starCount);
        json.writeValue("catCount", catCount);
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        spiderCount = jsonMap.get("spiderCount").asInt();
        waterCount = jsonMap.get("waterCount").asInt();
        insectCount = jsonMap.get("insectCount").asInt();
        starCount = jsonMap.get("starCount").asInt();
        catCount = jsonMap.get("catCount").asInt();
    }
}
