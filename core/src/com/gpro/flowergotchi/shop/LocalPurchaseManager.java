package com.gpro.flowergotchi.shop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.Preferences;
import com.gpro.flowergotchi.util.Utility;

import java.lang.reflect.Method;

/**
 * Created by user on 10.05.2016.
 */
public class LocalPurchaseManager implements Json.Serializable {
    private FlowergotchiGame game;
    // No-Ads are bought
    private boolean noAds = false;
    // Bought fertilizers count
    private int fertilizerCount = 0;
    // Flowers
    private boolean matricariaBought = false;
    private boolean rosaBought = false;
    private boolean moneyTreeBought = false;

    public boolean isRestored() {
        return restored;
    }

    private boolean restored;

    public LocalPurchaseManager() {

    }

    public LocalPurchaseManager(FlowergotchiGame game) {
        this.game = game;
    }

    public int getFertilizerCount() {
        return fertilizerCount;
    }

    public void restorePurchases() {
        loadData();
        game.getAppStore().getPlatformResolver().requestPurchaseRestore();
    }

    public boolean isNoAds() {
        return noAds;
    }

    public void setNoAds() {
        noAds = true;
    }

    protected void checkTransaction (String ID) {
        Purchase p;
        try {
            p = game.getAppStore().getPurchaseByID(ID);
            p.onRestore(this, game.getAppStore());
        } catch (IllegalArgumentException e) {
            Gdx.app.log("checkTransaction", "Bad purchase!");
        }
        saveData();
    }

    public void addFertilizer() {
        ++fertilizerCount;
    }

    public void decFertilizer() {
        --fertilizerCount;
    }

    public boolean checkIfBought(Purchase pur) {
        return !pur.type.equals(AppStore.consumable) && pur.isBought(this, game.getAppStore());
    }

    public boolean isMatricariaBought() {
        return matricariaBought;
    }

    public void setMatricariaBought() {
        this.matricariaBought = true;
    }

    public boolean isRosaBought() {
        return rosaBought;
    }

    public void setRosaBought() {
        this.rosaBought = true;
    }

    public boolean isMoneyTreeBought() {
        return moneyTreeBought;
    }

    public void setMoneyTreeBought() {
        this.moneyTreeBought = true;
    }

    public void saveData()
    {
        Json json = new Json();
        String str;
        json.setOutputType(JsonWriter.OutputType.json);
        str = json.prettyPrint(this);

        FileHandle handle = Gdx.files.local("save2.json");
        handle.writeString(Utility.encrypt(Preferences.PREFS_REUSE, Preferences.PREFS_USAGE, str), false);
    }

    public boolean loadData()
    {
        FileHandle file = Gdx.files.local("save2.json");
        if (!file.exists()) {
            return false;
        }
        String text = file.readString();

        Json json = new Json();
        LocalPurchaseManager temp = json.fromJson(LocalPurchaseManager.class, Utility.decrypt(Preferences.PREFS_REUSE, Preferences.PREFS_USAGE, text));
        this.fertilizerCount = temp.fertilizerCount;
        return true;
    }

    @Override
    public void write(Json json) {
        json.writeValue("fertilizerCount", fertilizerCount);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        fertilizerCount = jsonData.get("fertilizerCount").asInt();
    }

    public void restoreSuccess() {
        restored = true;
    }
}
