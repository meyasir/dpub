package com.darewro.Models;

/**
 * Created by Jaffar on 9/27/2017.
 */
public class RestaurantFoodMenuAddExploreBtnTag {
    int  parent;
    int  child;

    public RestaurantFoodMenuAddExploreBtnTag(int parent, int child) {
        this.parent = parent;
        this.child = child;
    }

    public RestaurantFoodMenuAddExploreBtnTag() {
    }
    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }
}
