package com.gpro.flowergotchi.shop;

/**
 * Created by user on 21.04.2016.
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.PurchaseSystem;
import com.gpro.flowergotchi.FlowergotchiGame;

public abstract class PlatformResolver {

    public FlowergotchiGame game;

    protected PurchaseManager mgr;
    PurchaseObserver purchaseObserver;
    PurchaseManagerConfig config;

    public PlatformResolver (FlowergotchiGame game) {
        this.game = game;
    }

    public void initializeIAP (PurchaseObserver purchaseObserver, PurchaseManagerConfig config) {
        this.purchaseObserver = purchaseObserver;
        this.config = config;
        if (PurchaseSystem.hasManager()) { // install and get the manager automatically via reflection
            this.mgr = PurchaseSystem.getManager();
        }
    }

    public void installIAP() {
        // set and install the manager manually
        if (mgr != null) {
            PurchaseSystem.setManager(mgr);
            mgr.install(purchaseObserver, config, true);	// dont call PurchaseSystem.install() because it may bind openIAB!
            Gdx.app.log("gdx-pay", "calls purchasemanager.install() manually");
        }
        else {
            Gdx.app.log("gdx-pay", "initializeIAP(): purchaseManager == null => call PurchaseSystem.hasManager()");
            if (PurchaseSystem.hasManager()) { // install and get the manager automatically via reflection
                this.mgr = PurchaseSystem.getManager();
                Gdx.app.log("gdx-pay", "calls PurchaseSystem.install() via reflection");
                PurchaseSystem.install(purchaseObserver, config); // install the observer
                Gdx.app.log("gdx-pay", "installed manager: " + this.mgr.toString());
            }
        }
    }

    public boolean requestPurchase (String productString) {
        if (mgr != null) {
            mgr.purchase(productString);	// dont call PurchaseSystem... because it may bind openIAB!
            Gdx.app.log("gdx-pay", "calls purchasemanager.purchase()");
            return true;
        } else {
            Gdx.app.log("gdx-pay", "ERROR: requestPurchase(): purchaseManager == null");
            return false;
        }
    }

    public void requestPurchaseRestore () {
        if (mgr != null) {
            mgr.purchaseRestore();	// dont call PurchaseSystem.purchaseRestore(); because it may bind openIAB!
            Gdx.app.log("gdx-pay", "calls purchasemanager.purchaseRestore()");
        } else {
            Gdx.app.log("gdx-pay", "ERROR: requestPurchaseRestore(): purchaseManager == null");
        }
    }

    public PurchaseManager getPurchaseManager () {
        return mgr;
    }

    public void dispose () {
        if (mgr != null) {
            Gdx.app.log("gdx-pay", "calls purchasemanager.dispose()");
            mgr.dispose();		// dont call PurchaseSystem... because it may bind openIAB!
            mgr = null;
        }
    }

    public Information getInformation(String identifier){
        if (mgr != null) {
            return mgr.getInformation(identifier);
        } else {
            if (PurchaseSystem.hasManager()) { // install and get the manager automatically via reflection
                this.mgr = PurchaseSystem.getManager();
                return mgr.getInformation(identifier);
            }
            return null;
        }
    }
}
