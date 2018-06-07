package com.darewro.Models;

import java.util.ArrayList;

/**
 * Created by Jaffar on 8/28/2017.
 */
public class RestaurantFoodMenu {

    String name;
    String desc;
    ArrayList<RestaurantFoodSubMenu> restaurantFoodSubMenus;

    public RestaurantFoodMenu() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<RestaurantFoodSubMenu> getRestaurantFoodSubMenus() {
        return restaurantFoodSubMenus;
    }

    public void setRestaurantFoodSubMenus(ArrayList<RestaurantFoodSubMenu> restaurantFoodSubMenus) {
        this.restaurantFoodSubMenus = restaurantFoodSubMenus;
    }
}
