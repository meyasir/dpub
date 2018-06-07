package com.darewro.Models;

import java.util.ArrayList;

/**
 * Created by Jaffar on 9/27/2017.
 */
public class RestaurantFoodCategory {
    String id;
    String category;
    // ArrayList to store food items
    private ArrayList<RestaurantFoodMenu> restaurantFoodMenus;

    public RestaurantFoodCategory(String id, String category) {
        this.id = id;
        this.category = category;
    }

    public RestaurantFoodCategory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // ArrayList to store food items
    public ArrayList<RestaurantFoodMenu> getRestaurantFoodMenus() {
        return restaurantFoodMenus;
    }

    public void setRestaurantFoodMenus(ArrayList<RestaurantFoodMenu> restaurantFoodMenus) {
        this.restaurantFoodMenus = restaurantFoodMenus;
    }
}
