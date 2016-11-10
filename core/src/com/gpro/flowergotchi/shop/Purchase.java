package com.gpro.flowergotchi.shop;

/**
 * Created by user on 28.04.2016.
 */
public abstract class Purchase extends Item {
    public String type;

    public Purchase() {
    }

    public abstract void onRestore(LocalPurchaseManager localPurchaseManager, AppStore appStore);
    public abstract boolean isBought(LocalPurchaseManager localPurchaseManager, AppStore appStore);
}
