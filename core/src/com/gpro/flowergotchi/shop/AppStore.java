package com.gpro.flowergotchi.shop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.Offer;
import com.badlogic.gdx.pay.OfferType;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gpro.flowergotchi.FlowergotchiGame;
import com.gpro.flowergotchi.xml.XmlFileParserPurchases;

/**
 * Created by user on 27.04.2016.
 */
public class AppStore {
    public final static String consumable = "consumable";
    public final static String nonconsumable = "nonconsumable";
    private final FlowergotchiGame game;

    private Category allPurchases;
    public PurchaseManagerConfig purchaseManagerConfig;
    public PurchaseObserver purchaseObserver = new PurchaseObserver() {

        @Override
        public void handleRestore (Transaction[] transactions) {
            for (Transaction transaction : transactions) {
                game.getPurchaseManager().checkTransaction(transaction.getIdentifier());
            }
        }

        @Override
        public void handleRestoreError (Throwable e) {
            // getPlatformResolver().showToast("PurchaseObserver: handleRestoreError!");
            Gdx.app.log("ERROR", "PurchaseObserver: handleRestoreError!: " + e.getMessage());
            throw new GdxRuntimeException(e);
        }

        @Override
        public void handleInstall () {
            // getPlatformResolver().showToast("PurchaseObserver: installed successfully...");
            Gdx.app.log("handleInstall: ", "successfully..");
        }

        @Override
        public void handleInstallError (Throwable e) {
            // getPlatformResolver().showToast("PurchaseObserver: handleInstallError!");
            Gdx.app.log("ERROR", "PurchaseObserver: handleInstallError!: " + e.getMessage());
            throw new GdxRuntimeException(e);
        }

        @Override
        public void handlePurchase (Transaction transaction) {
            game.getPurchaseManager().checkTransaction(transaction.getIdentifier());
        }

        @Override
        public void handlePurchaseError (Throwable e) {
            if (e.getMessage().equals("There has been a Problem with your Internet connection. Please try again later")) {
                // this check is needed because user-cancel is a handlePurchaseError too)
                // getPlatformResolver().showToast("handlePurchaseError: " + e.getMessage());
            }
            throw new GdxRuntimeException(e);
        }

        @Override
        public void handlePurchaseCanceled () {
        }
    };
    private PlatformResolver resolver;

    public AppStore(FlowergotchiGame game) {
        this.game = game;
        purchaseManagerConfig = new PurchaseManagerConfig();

        XmlFileParserPurchases xmlParser = new XmlFileParserPurchases("xml/Purchases.xml");
        allPurchases = xmlParser.parsePurchases();
        addPurchases(allPurchases);

    }

    private void addPurchases(Category puchases) {

        for (Item e : puchases.getItems()) {
            if (e.getClass().equals(Category.class)) {
                addPurchases((Category)e);
            } else {
                Purchase p = (Purchase)e;
                if (p.type.equals(consumable)) {
                    purchaseManagerConfig.addOffer(new Offer().setType(OfferType.CONSUMABLE).setIdentifier(p.name));
                } else if (p.type.equals(nonconsumable)) {
                    purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier(p.name));
                }

            }
        }
    }

    public Purchase getPurchaseByID(String ID)
    {
        return findPurchase(allPurchases, ID);
    }

    private Purchase findPurchase(Category curCat, String id) {
        for (Item p : curCat.getItems()) {
            if (p.getClass() == Category.class) {
                try {
                    return findPurchase((Category)p, id);
                } catch (IllegalArgumentException e) {
                    // not here, skipping
                }
            } else {
                if (p.name.equals(id)) {
                    return (Purchase)p;
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public PlatformResolver getPlatformResolver() {
        return resolver;
    }

    public void setPlatformResolver(PlatformResolver resolver) {
        this.resolver = resolver;

    }

    public boolean requestPurchase(String purID) {
        return getPlatformResolver().requestPurchase(purID);
    }
}
