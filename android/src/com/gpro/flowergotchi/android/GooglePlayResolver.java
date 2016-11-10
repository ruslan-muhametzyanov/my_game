package com.gpro.flowergotchi.android;

import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.shop.PlatformResolver;

public class GooglePlayResolver extends PlatformResolver {

    public final static String GOOGLEKEY  = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsyTXuobbTBiLKmIGBFcCyg+du00jq+8A/4NxSejzCs4LqCOV97DUk24fbBumrMkCciI349rg9m9lDGE6AIZAJQVWFUD+7RUWxSdt7+sVGpUrKRJLX5Lgq2XZd0nh2RqXKf2enirrWG5XRkOnCLZNBhwYYkWJuHcIwY3gYwttb/UXMd0oPOyzuxyTnTkSzJDPpuPTzNr1N3q3QGOQQHVUz81rqt8s/pPA7ugps1uaVKkICqD9WXwgLThFjzngBcfzTJjRl5agVk2F963thIq/hjsBeRrJxnDm7VyynSbLGv2Mu6kiEqCu32fDhKMK6yVON3OvOTFRJz1pvLxls64bCwIDAQAB";

    public GooglePlayResolver(FlowergotchiGame game) {
        super(game);

        PurchaseManagerConfig config = game.getAppStore().purchaseManagerConfig;
        config.addStoreParam(PurchaseManagerConfig.STORE_NAME_ANDROID_GOOGLE, GOOGLEKEY);
        initializeIAP(game.getAppStore().purchaseObserver, config);
    }
}
