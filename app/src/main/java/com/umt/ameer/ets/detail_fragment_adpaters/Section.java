package com.umt.ameer.ets.detail_fragment_adpaters;

/**
 * Created by Ameer on 10/18/2016.
 */

public class Section {
    public String name;

    public boolean isExpanded;

    public Section(String name) {
        this.name = name;
        isExpanded = true;
    }

    public String getName() {
        return name;
    }
}
