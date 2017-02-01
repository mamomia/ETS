package com.umt.ameer.ets.detail_fragment_adpaters;

/**
 * Created by Ameer on 10/18/2016.
 */
public class Item {
    public String product_size;
    public String product_price;
    public String name;
    public int id;

    public Item(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
