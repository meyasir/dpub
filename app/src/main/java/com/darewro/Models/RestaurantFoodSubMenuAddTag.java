package com.darewro.Models;

/**
 * Created by Jaffar on 2018-01-21.
 */

public class RestaurantFoodSubMenuAddTag {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;

    public RestaurantFoodSubMenuAddTag(String name, int position) {
        this.name = name;
        this.position = position;
    }
}
