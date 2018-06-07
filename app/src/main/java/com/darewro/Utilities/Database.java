package com.darewro.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.darewro.Models.FoodItemCart;
import com.darewro.Models.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaffar on 2018-01-22.
 */

/**
*
 * edited by yasir 19-may-18
 *
 *
* */


public class Database extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "darewro.db";

    // Table Names
    private static final String TABLE_FOOD_ITEM_CART = "tbl_food_item_cart";
    private static final String TABLE_RESTAURANT_CART = "tbl_restaurant_cart";

    // Food Item Cart table - column names
    private static final String FOOD_ITEM_CART_ID = "food_item_cart_id";
    private static final String FOOD_ITEM_CART_NAME = "food_item_cart_name";
    private static final String FOOD_ITEM_CART_WEIGHT = "food_item_cart_weight";
    private static final String FOOD_ITEM_CART_PRICE = "food_item_cart_price";
    private static final String FOOD_ITEM_CART_QUANTITY = "food_item_cart_quantity";

    // Restaurant Cart table - column names
    private static final String RESTAURANT_CART_ID = "restaurant_cart_id";
    private static final String RESTAURANT_CART_NAME = "restaurant_cart_name";

    // Table Create Statements
    // Food Item Cart table create statement
    private static final String CREATE_TABLE_FOOD_ITEM_CART = "CREATE TABLE "
            + TABLE_FOOD_ITEM_CART + "(" + FOOD_ITEM_CART_ID + " INTEGER PRIMARY KEY," + FOOD_ITEM_CART_NAME
            + " TEXT," + FOOD_ITEM_CART_WEIGHT + " TEXT," + FOOD_ITEM_CART_PRICE
            + " INTEGER," + FOOD_ITEM_CART_QUANTITY + " INTEGER," + RESTAURANT_CART_ID
            + " INTEGER" + ")";

    // Restaurant Cart table create statement
    private static final String CREATE_TABLE_RESTAURANT_CART = "CREATE TABLE "
            + TABLE_RESTAURANT_CART + "(" + RESTAURANT_CART_ID + " INTEGER PRIMARY KEY," + RESTAURANT_CART_NAME
            + " TEXT" + ")";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_FOOD_ITEM_CART);
        db.execSQL(CREATE_TABLE_RESTAURANT_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEM_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT_CART);
        // create new tables
        onCreate(db);
    }

    public void drop(){
        SQLiteDatabase db = this.getWritableDatabase();
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEM_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT_CART);
        // create new tables
        onCreate(db);
    }

    // ------------------------ "restaurant cart" table methods ----------------//
    /*
    * Creating a restaurant cart
    */
    public long addRestaurantCart(Restaurant restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RESTAURANT_CART_ID, restaurant.getId());
        values.put(RESTAURANT_CART_NAME, restaurant.getName());

        // insert row
        long food_item_cart_id = db.insert(TABLE_RESTAURANT_CART, null, values);

        return food_item_cart_id;
    }

    /**
     * get single restaurant cart
     */
    public Restaurant getRestaurantCart(int restaurantCartID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_RESTAURANT_CART + " WHERE "
                + RESTAURANT_CART_ID + " = " + restaurantCartID;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Restaurant restaurant = new Restaurant();
        restaurant.setId(c.getInt(c.getColumnIndex(RESTAURANT_CART_ID)));
        restaurant.setName(c.getString(c.getColumnIndex(RESTAURANT_CART_NAME)));

        return restaurant;
    }

    /**
     * getting all restaurant cart
     * */
    public List<Restaurant> getAllRestaurantCart() {
        List<Restaurant> restaurants= new ArrayList<Restaurant>();
        String selectQuery = "SELECT  * FROM " + TABLE_RESTAURANT_CART;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Restaurant restaurant = new Restaurant();

                restaurant.setId(c.getInt(c.getColumnIndex(RESTAURANT_CART_ID)));
                restaurant.setName(c.getString(c.getColumnIndex(RESTAURANT_CART_NAME)));
                // adding to user list
                restaurants.add(restaurant);
            } while (c.moveToNext());
        }

        return restaurants;
    }

    /**
     * delete restaurant cart
     * */
    public void deleteRestaurantCart(String restaurantCartID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_RESTAURANT_CART +" WHERE "+ RESTAURANT_CART_ID +" = " + restaurantCartID);
    }


    // ------------------------ "food item cart" table methods ----------------//
    /*
    * Creating a user
    */
    public long addFoodItemCart(FoodItemCart foodItemCart) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FOOD_ITEM_CART_ID, foodItemCart.getId());
        values.put(FOOD_ITEM_CART_NAME, foodItemCart.getName());
        values.put(FOOD_ITEM_CART_WEIGHT, foodItemCart.getWeight());
        values.put(FOOD_ITEM_CART_PRICE, foodItemCart.getPrice());
        values.put(FOOD_ITEM_CART_QUANTITY, foodItemCart.getQuantity());
        values.put(FOOD_ITEM_CART_QUANTITY, foodItemCart.getQuantity());
        values.put(RESTAURANT_CART_ID, foodItemCart.getRestaurantID());

        // insert row
        long food_item_cart_id = db.insert(TABLE_FOOD_ITEM_CART, null, values);

        return food_item_cart_id;
    }

    /**
     * get single food item cart
     */
    public FoodItemCart getFoodItemCart(int foodItemCartID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_FOOD_ITEM_CART + " WHERE "
                + FOOD_ITEM_CART_ID + " = " + foodItemCartID;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        FoodItemCart foodItemCart = new FoodItemCart();
        foodItemCart.setId(c.getInt(c.getColumnIndex(FOOD_ITEM_CART_ID)));
        foodItemCart.setName(c.getString(c.getColumnIndex(FOOD_ITEM_CART_NAME)));
        foodItemCart.setWeight(c.getString(c.getColumnIndex(FOOD_ITEM_CART_WEIGHT)));
        foodItemCart.setPrice(c.getInt(c.getColumnIndex(FOOD_ITEM_CART_PRICE)));
        foodItemCart.setQuantity(c.getInt(c.getColumnIndex(FOOD_ITEM_CART_QUANTITY)));
        foodItemCart.setRestaurantID(c.getInt(c.getColumnIndex(RESTAURANT_CART_ID)));

        return foodItemCart;
    }

    /**
     * get single food item cart for a restaurant cart id
     */
    public boolean isRestaurantCartFoodItemExist(int restaurantCartID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_FOOD_ITEM_CART + " WHERE "
                + RESTAURANT_CART_ID + " = " + restaurantCartID;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null&&c.getCount()!=0)
            return true;
        else
            return false;
    }

    /**
     * getting all food items cart
     * */
    public List<FoodItemCart> getAllFoodItemCart() {
        List<FoodItemCart> foodItemCarts= new ArrayList<FoodItemCart>();
        String selectQuery = "SELECT  * FROM " + TABLE_FOOD_ITEM_CART;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                FoodItemCart foodItemCart = new FoodItemCart();

                foodItemCart.setId(c.getInt(c.getColumnIndex(FOOD_ITEM_CART_ID)));
                foodItemCart.setName(c.getString(c.getColumnIndex(FOOD_ITEM_CART_NAME)));
                foodItemCart.setWeight(c.getString(c.getColumnIndex(FOOD_ITEM_CART_WEIGHT)));
                foodItemCart.setPrice(c.getInt(c.getColumnIndex(FOOD_ITEM_CART_PRICE)));
                foodItemCart.setQuantity(c.getInt(c.getColumnIndex(FOOD_ITEM_CART_QUANTITY)));
                foodItemCart.setRestaurantID(c.getInt(c.getColumnIndex(RESTAURANT_CART_ID)));
                // adding to user list
                foodItemCarts.add(foodItemCart);
            } while (c.moveToNext());
        }

        return foodItemCarts;
    }

    /**
     * delete food item cart
     * */
    public void deleteFoodItemCart(String foodItemCartID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_FOOD_ITEM_CART +" WHERE "+ FOOD_ITEM_CART_ID +" = " + foodItemCartID);
    }

    /**
     * update food item cart quantity
     * */
    public int updateFoodItemCartQuantity(String foodItemCartID,FoodItemCart foodItemCart) {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues c = new ContentValues();
        c.put(FOOD_ITEM_CART_QUANTITY,foodItemCart.getQuantity());

        int id = db.update(TABLE_FOOD_ITEM_CART, c, FOOD_ITEM_CART_ID + " = ?", new String[]{foodItemCartID});
        return id;
    }
}
