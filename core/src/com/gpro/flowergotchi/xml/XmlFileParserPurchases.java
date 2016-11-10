package com.gpro.flowergotchi.xml;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.gpro.flowergotchi.gamelogic.GameWorld;
import com.gpro.flowergotchi.shop.Category;
import com.gpro.flowergotchi.shop.Purchase;

import java.lang.reflect.Constructor;

/**
 * Created by user on 28.04.2016.
 */
public class XmlFileParserPurchases extends XmlFileParser {

    public static final String PurchasesPackage = "com.gpro.flowergotchi.shop.purchases.";

    public XmlFileParserPurchases(String file) {
        super(file);
    }

    private Category parseCategory(Category catlist, XmlReader.Element cat)
    {
        int rootCount = cat.getChildCount();
        for (int i = 0; i < rootCount; ++i) {
            XmlReader.Element elem = cat.getChild(i);
            String name = elem.getAttribute("name");
            String image = elem.getAttribute("image");
            if (elem.getName().equals("category")) {
                Category category = new Category();
                category.name = name;
                category.image = image;
                category.setParent(catlist);
                catlist.addItem(parseCategory(category, elem));
            } else if (elem.getName().equals("purchase")) {
                String type = elem.getAttribute("type");
                String className = elem.getAttribute("className");

                try {
                    Class purchaseClass;
                    try {
                        purchaseClass = Class.forName(PurchasesPackage + className);
                    } catch (Exception e) {
                        Gdx.app.log("XML", "Bad purchases XML file!");
                        e.printStackTrace();
                        throw  new IllegalArgumentException();
                    }
                    Class[] types = {};
                    Object[] parameters = {};
                    Constructor cons;

                        cons = purchaseClass.getConstructor(types);
                    Purchase purchase = (Purchase) cons.newInstance(parameters);

                    purchase.name = name;
                    purchase.image = image;
                    purchase.type = type;
                    catlist.addItem(purchase);
                } catch (Exception e) {
                    Gdx.app.log("XML", "Bad purchase!");
                    e.printStackTrace();
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        return catlist;
    }

    public Category parsePurchases() {

        Category rootCat = new Category();
        return parseCategory(rootCat, root);
    }
}
