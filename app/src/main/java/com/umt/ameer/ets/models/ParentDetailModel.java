package com.umt.ameer.ets.models;

/**
 * Created by Mushi on 2/1/2017.
 */

import java.util.ArrayList;

public class ParentDetailModel {
    private String name;

    // ArrayList to store child objects
    private ArrayList<ChildDetailModel> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ArrayList to store child objects
    public ArrayList<ChildDetailModel> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ChildDetailModel> children) {
        this.children = children;
    }
}
