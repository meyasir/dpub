package com.darewro.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.Adapters.RestaurantMenuCustomAdapter;
import com.darewro.Adapters.RestaurantMenuSubItemAdapter;
import com.darewro.Models.FoodItemCart;
import com.darewro.Models.Restaurant;
import com.darewro.Models.RestaurantFoodCategory;
import com.darewro.Models.RestaurantFoodMenu;
import com.darewro.Models.RestaurantFoodMenuAddExploreBtnTag;
import com.darewro.Models.RestaurantFoodSubMenu;
import com.darewro.Models.RestaurantFoodSubMenuAddTag;
import com.darewro.R;
import com.darewro.Utilities.Database;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaffar on 2018-01-23.
 */

public class RestaurantMenuFragment extends Fragment {
    Database mydb;
    int resID;
    RestaurantMenuSubItemAdapter sfi;

    private Dialog dialogSubFoodItems;

    Activity activity;
    private ExpandableListView lvFoodItems;
    private TextView tvRestroName,tvRestroDesc;
    private ImageView logo,refreshImageView;
    LinearLayout linlaHeaderProgress;
    private RestaurantMenuCustomAdapter restaurantMenuCustomAdapter;
    private ArrayList<RestaurantFoodCategory> restaurantFoodCategories;

    RelativeLayout data;

    WebService webService;
    public RestaurantMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);
        setInRestaurantMenu();
        mydb = new Database(activity);

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tvRestroName = (TextView)rootView.findViewById(R.id.tvRestroName);
        tvRestroDesc = (TextView)rootView.findViewById(R.id.tvRestroDesc);
        logo = (ImageView)rootView.findViewById(R.id.logo);
        data = (RelativeLayout)rootView.findViewById(R.id.data);
        lvFoodItems = (ExpandableListView)rootView.findViewById(R.id.lvFoodItems);
        refreshImageView = (ImageView)rootView.findViewById(R.id.refreshImageView);
        linlaHeaderProgress = (LinearLayout)rootView.findViewById(R.id.Progress);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshClick(view);
            }
        });
        tvRestroName.setText(getArguments().getString(RestaurantFragment.RESTRO_NAME));
        tvRestroDesc.setText(getArguments().getString(RestaurantFragment.RESTRO_DESC));
        resID = getArguments().getInt(RestaurantFragment.RESTRO_ID);
        byte[] byteArray = getArguments().getByteArray(RestaurantFragment.RESTRO_IMAGE);
        if(byteArray!=null)
            logo.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        else
            logo.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant_black_24dp));

        webService = new WebService(activity);
        loadData();
        // Inflate the layout for this fragment
        return rootView;
    }
    public void setInRestaurantMenu(){
        ((AppBaseActivity)activity).setInOrderDetail(false);
        ((AppBaseActivity)activity).setInRestaurant(false);
        ((AppBaseActivity)activity).setInDropDetail(false);
        ((AppBaseActivity)activity).setInPickDetail(false);
        ((AppBaseActivity)activity).setInCompleteDetail(false);
        ((AppBaseActivity)activity).setInRestaurantMenu(true);
        ((AppBaseActivity)activity).setInOtherFragment(false);
        ((AppBaseActivity)activity).setInProcessOrder(false);
        ((AppBaseActivity)activity).setInPastOrder(false);

    }
    private void loadData() {
        if(webService.isNetworkConnected()) {
            if(((AppBaseActivity)activity).isSnackBarVisible()) {
                ((AppBaseActivity)activity).setSnackBarGone();
            }
            refreshImageView.setVisibility(View.GONE);
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            getFoodItems();
        }
        else {
            ((AppBaseActivity)activity).setSnackBarVisible();
        }
    }
    public void getFoodItems(){
        webService.getFoodItems(getString(R.string.url) + "get_food_items", String.valueOf(resID), new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    restaurantFoodCategories = new ArrayList<>();
                    JSONArray foodData = jsonObject.getJSONArray("food_data");
                    for (int fd = 0; fd < foodData.length(); fd++) {
                        JSONObject restaurantfoodcategory = foodData.getJSONObject(fd);
                        //Create "menu category" class object
                        final RestaurantFoodCategory restaurantFoodCategory = new RestaurantFoodCategory();
                        restaurantFoodCategory.setCategory(restaurantfoodcategory.getString("restaurant_food_category"));
                        restaurantFoodCategory.setId(restaurantfoodcategory.getString("restaurant_food_category_id"));
                        restaurantFoodCategory.setRestaurantFoodMenus(new ArrayList<RestaurantFoodMenu>());
                        JSONArray foodcategoryJSONArray = restaurantfoodcategory.getJSONArray("restaurant_food_menus");
                        for (int fi = 0; fi < foodcategoryJSONArray.length(); fi++) {
                            JSONObject restaurantFoodMenuJSONObject = foodcategoryJSONArray.getJSONObject(fi);
                            // Create "menu items" class object
                            final RestaurantFoodMenu restaurantFoodMenu = new RestaurantFoodMenu();
                            restaurantFoodMenu.setName(restaurantFoodMenuJSONObject.getString("restaurant_food_name"));
                            restaurantFoodMenu.setDesc(restaurantFoodMenuJSONObject.getString("restaurant_food_description"));
                            restaurantFoodMenu.setRestaurantFoodSubMenus(new ArrayList<RestaurantFoodSubMenu>());
                            if(restaurantFoodMenuJSONObject.getInt("total_sub_menus")>1){
                                JSONArray restaurantFoodSubMenus = restaurantFoodMenuJSONObject.getJSONArray("restaurant_food_sub_menus");
                                for (int sf = 0; sf < restaurantFoodSubMenus.length(); sf++) {
                                    JSONObject subfoodsJSONObject = restaurantFoodSubMenus.getJSONObject(sf);
                                    final RestaurantFoodSubMenu restaurantFoodSubMenu = new RestaurantFoodSubMenu();
                                    restaurantFoodSubMenu.setId(subfoodsJSONObject.getInt("restaurant_food_menu_id"));
                                    restaurantFoodSubMenu.setPrice(subfoodsJSONObject.getInt("restaurant_food_price"));
                                    restaurantFoodSubMenu.setWeight(subfoodsJSONObject.getString("restaurant_food_quantity"));
                                    restaurantFoodMenu.getRestaurantFoodSubMenus().add(restaurantFoodSubMenu);
                                }
                            }
                            else {
                                final RestaurantFoodSubMenu restaurantFoodSubMenu = new RestaurantFoodSubMenu();
                                restaurantFoodSubMenu.setId(restaurantFoodMenuJSONObject.getInt("restaurant_food_menu_id"));
                                restaurantFoodSubMenu.setPrice(restaurantFoodMenuJSONObject.getInt("restaurant_food_price"));
                                restaurantFoodSubMenu.setWeight(restaurantFoodMenuJSONObject.getString("restaurant_food_quantity"));
                                restaurantFoodMenu.getRestaurantFoodSubMenus().add(restaurantFoodSubMenu);
                            }
                            //Add "menu items" class object to "menu category" class object
                            restaurantFoodCategory.getRestaurantFoodMenus().add(restaurantFoodMenu);

                        }
                        restaurantFoodCategories.add(restaurantFoodCategory);
                    }
                    setFoodItems();

                } catch (JSONException e) {
                    // HIDE THE SPINNER AFTER LOADING FEEDS
                    refreshImageView.setVisibility(View.VISIBLE);
                    linlaHeaderProgress.setVisibility(View.GONE);
                    Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // HIDE THE SPINNER AFTER LOADING FEEDS
                refreshImageView.setVisibility(View.VISIBLE);
                linlaHeaderProgress.setVisibility(View.GONE);
                Helper h = new Helper();
                h.volleyErrorMessage(activity,error);
            }
        });
    }

    public void setFoodItems(){
        /// Pass results to RestroListViewAdapter Class
        restaurantMenuCustomAdapter = new RestaurantMenuCustomAdapter(activity, restaurantFoodCategories, new RestaurantMenuCustomAdapter.AddImageViewClick() {
            @Override
            public void onAddImageViewClick(RestaurantFoodMenuAddExploreBtnTag tag, ImageView imageView) {
                boolean exist = false;
                int parent, child;
                parent = tag.getParent();
                child = tag.getChild();
                List<FoodItemCart> foodItemCarts = mydb.getAllFoodItemCart();

                Toast.makeText(activity, "Item added to cart", Toast.LENGTH_SHORT).show();
                for(FoodItemCart fic:foodItemCarts){
                    if(fic.getId()==restaurantFoodCategories.get(parent).getRestaurantFoodMenus().get(child).getRestaurantFoodSubMenus().get(0).getId()){
                        exist = true;
                        FoodItemCart foodItemCart = mydb.getFoodItemCart(fic.getId());
                        int q = (foodItemCart.getQuantity()+1);
                        foodItemCart.setQuantity(q);
                        mydb.updateFoodItemCartQuantity(String.valueOf(fic.getId()),foodItemCart);
                        ((AppBaseActivity)activity).addAllFoodItemCartArrayList(mydb.getAllFoodItemCart());
                        break;
                    }
                }
                if (!exist) {
                    boolean resExist=false;
                    for(FoodItemCart foodItemCart: mydb.getAllFoodItemCart()){
                        if(foodItemCart.getRestaurantID()==resID){
                            resExist = true;
                            break;
                        }
                    }
                    if(!resExist){
                        Restaurant restaurant = new Restaurant();
                        restaurant.setId(resID);
                        restaurant.setName(tvRestroName.getText().toString());
                        mydb.addRestaurantCart(restaurant);
                    }
                    RestaurantFoodMenu fi = restaurantFoodCategories.get(parent).getRestaurantFoodMenus().get(child);
                    FoodItemCart foodItemCart = new FoodItemCart();
                    foodItemCart.setId(fi.getRestaurantFoodSubMenus().get(0).getId());
                    foodItemCart.setName(fi.getName());
                    foodItemCart.setWeight(fi.getRestaurantFoodSubMenus().get(0).getWeight());
                    foodItemCart.setPrice(fi.getRestaurantFoodSubMenus().get(0).getPrice());
                    foodItemCart.setQuantity(1);
                    foodItemCart.setRestaurantID(resID);
                    mydb.addFoodItemCart(foodItemCart);
                    ((AppBaseActivity)activity).addAllFoodItemCartArrayList(mydb.getAllFoodItemCart());
                }


                if (!((AppBaseActivity)activity).isTotalLayoutVisible())
                    ((AppBaseActivity)activity).setTotalLayoutVisible();

                int totalcount = 0;
                int totalPrice = 0;
                foodItemCarts = mydb.getAllFoodItemCart();
                for (FoodItemCart fic: foodItemCarts) {
                    totalcount += Integer.valueOf(fic.getQuantity());
                    try {
                        totalPrice += fic.getPrice() * fic.getQuantity();
                    } catch (NumberFormatException e) {
                        //fi.getPrice() is not a number;
                    }
                }
                ((AppBaseActivity)activity).tvCount.setText(Integer.toString(totalcount));
                ((AppBaseActivity)activity).tvTotal.setText("" + totalPrice);
            }
        }, new RestaurantMenuCustomAdapter.ExploreImageViewClick() {
            @Override
            public void onExploreImageViewClick(RestaurantFoodMenuAddExploreBtnTag tag) {
                int parent, child;
                parent = tag.getParent();
                child = tag.getChild();
                setSubFoodItems(restaurantFoodCategories.get(parent).getRestaurantFoodMenus().get(child).getRestaurantFoodSubMenus(),
                        restaurantFoodCategories.get(parent).getRestaurantFoodMenus().get(child).getName(),parent,child);
            }
        });

        // Binds the Adapter to the ListView
        lvFoodItems.setAdapter(restaurantMenuCustomAdapter);
        lvFoodItems.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {
                //   parent.smoothScrollToPositionFromTop(groupPosition, 0);
                parent.setSelectionFromTop(groupPosition,0);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parent.smoothScrollToPositionFromTop(groupPosition, 0);
                    }
                },100);
                return false;
            }
        });
        lvFoodItems.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    lvFoodItems.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
//        lvFoodItems.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                if (lastExpandedPosition != -1
//                        && groupPosition != lastExpandedPosition) {
//                    lvFoodItems.collapseGroup(lastExpandedPosition);
//                }
//                lastExpandedPosition = groupPosition;
//            }
//        });
        data.setVisibility(View.VISIBLE);
        // HIDE THE SPINNER AFTER LOADING FEEDS
        linlaHeaderProgress.setVisibility(View.GONE);
    }
    private int lastExpandedPosition = -1;
    Handler handler = new Handler();

    public void setSubFoodItems(final ArrayList<RestaurantFoodSubMenu> subFoodItems, String foodName, final int parent, final int child){
        dialogSubFoodItems = new Dialog(activity,R.style.MyAlertDialogStyle);
        dialogSubFoodItems.setContentView(R.layout.dialog_sub_item);
        dialogSubFoodItems.setTitle("Sub Items");
        // if button is clicked, close the custom dialog
        ListView listView = (ListView)dialogSubFoodItems.findViewById(R.id.listView);
        final TextView tvFoodName = (TextView)dialogSubFoodItems.findViewById(R.id.tvFoodName);

        tvFoodName.setText(foodName);
        sfi = new RestaurantMenuSubItemAdapter(activity, foodName, subFoodItems, new RestaurantMenuSubItemAdapter.AddImageViewClick() {
            @Override
            public void onAddImageViewClick(RestaurantFoodSubMenuAddTag tag) {
                int position = tag.getPosition();
                String foodName = tag.getName();
                RestaurantFoodSubMenu subFoodItem = (RestaurantFoodSubMenu) sfi.getItem(position);
                boolean exist = false;
                List<FoodItemCart> foodItemCarts = mydb.getAllFoodItemCart();
                Toast.makeText(activity, "Item added to cart", Toast.LENGTH_SHORT).show();

                for(FoodItemCart fic:foodItemCarts){
                    if(fic.getId()==subFoodItem.getId()){
                        exist = true;
                        FoodItemCart foodItemCart = mydb.getFoodItemCart(fic.getId());
                        int q = (foodItemCart.getQuantity()+1);
                        foodItemCart.setQuantity(q);
                        mydb.updateFoodItemCartQuantity(String.valueOf(fic.getId()),foodItemCart);
                        ((AppBaseActivity)activity).addAllFoodItemCartArrayList(mydb.getAllFoodItemCart());
                        break;
                    }
                }
                if (!exist) {
                    boolean resExist=false;
                    for(FoodItemCart foodItemCart: mydb.getAllFoodItemCart()){
                        if(foodItemCart.getRestaurantID()==resID){
                            resExist = true;
                            break;
                        }
                    }
                    if(!resExist){
                        Restaurant restaurant = new Restaurant();
                        restaurant.setId(resID);
                        restaurant.setName(tvRestroName.getText().toString());
                        mydb.addRestaurantCart(restaurant);
                    }
                    FoodItemCart foodItemCart = new FoodItemCart();
                    foodItemCart.setId(subFoodItem.getId());
                    foodItemCart.setName(foodName);
                    foodItemCart.setWeight(subFoodItem.getWeight());
                    foodItemCart.setPrice(subFoodItem.getPrice());
                    foodItemCart.setQuantity(1);
                    foodItemCart.setRestaurantID(resID);
                    mydb.addFoodItemCart(foodItemCart);
                    ((AppBaseActivity)activity).addAllFoodItemCartArrayList(mydb.getAllFoodItemCart());
                }

                if (!((AppBaseActivity)activity).isTotalLayoutVisible())
                    ((AppBaseActivity)activity).setTotalLayoutVisible();

                int totalcount = 0;
                int totalPrice = 0;
                foodItemCarts = mydb.getAllFoodItemCart();
                for (FoodItemCart fic: foodItemCarts) {
                    totalcount += Integer.valueOf(fic.getQuantity());
                    try {
                        totalPrice += fic.getPrice() * fic.getQuantity();
                    } catch (NumberFormatException e) {
                        //fi.getPrice() is not a number;
                    }
                }
                ((AppBaseActivity)activity).tvCount.setText(Integer.toString(totalcount));
                ((AppBaseActivity)activity).tvTotal.setText("" + totalPrice);
            }
        });
        // Binds the Adapter to the ListView
        listView.setAdapter(sfi);
//        setListViewHeightBasedOnChildren(listView);
        dialogSubFoodItems.show();
    }

    public void onRefreshClick(View view) {
        loadData();
    }
}
