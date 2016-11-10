package com.gpro.flowergotchi.shop.purchases;

import com.gpro.flowergotchi.shop.AppStore;
import com.gpro.flowergotchi.shop.LocalPurchaseManager;
import com.gpro.flowergotchi.shop.Purchase;

/**
 * Created by user on 10.05.2016.
 */
public class PurMatricaria  extends Purchase {

    @Override
    public void onRestore(LocalPurchaseManager localPurchaseManager, AppStore appStore) {
        localPurchaseManager.setMatricariaBought();
    }

    @Override
    public boolean isBought(LocalPurchaseManager localPurchaseManager, AppStore appStore) {
        return localPurchaseManager.isMatricariaBought();
    }
}
