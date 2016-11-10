package com.gpro.flowergotchi.shop;

import java.util.ArrayList;

/**
 * Created by user on 29.04.2016.
 */
public class Category extends Item {
    private ArrayList<Item> items;

    public void setParent(Category parent) {
        this.parent = parent;
    }

    private Category parent;

    public Category() {
        this.items = new ArrayList<Item>();
        this.parent = null;
    }

    public void addItem(Item item)
    {
        items.add(item);
    }

    public int getSize() {
        return items.size();
    }

    public Item getAt(int index)
    {
        return items.get(index);
    }

    public Category getParent() {
        return parent;
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
