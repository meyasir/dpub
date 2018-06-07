package com.darewro.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Adapters.BasketListViewAdapter;
import com.darewro.Fragments.HelpFragment;
import com.darewro.Fragments.HomeFragment;
import com.darewro.Fragments.MyOrdersFragment;
import com.darewro.Fragments.SettingsFragment;
import com.darewro.Models.User;
import com.darewro.Models.DeliveryType;
import com.darewro.Models.FoodItemCart;
import com.darewro.Models.GeneralOrder;
import com.darewro.Models.Restaurant;
import com.darewro.R;
import com.darewro.Utilities.Database;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.MySharedPreferences;
import com.darewro.Utilities.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaffar on 2018-01-24.
 */

public class AppBaseActivity extends AppCompatActivity {

    boolean inOrderDetail;
    boolean inDropDetail;
    boolean inPickDetail;
    boolean inCompleteDetail;
    boolean inRestaurant;
    boolean inRestaurantMenu;
    boolean inOtherFragment;
    boolean inProcessOrder;
    boolean inPastOrder;

    public boolean isInProcessOrder() {
        return inProcessOrder;
    }
    public void setInProcessOrder(boolean inProcessOrder) {
        this.inProcessOrder = inProcessOrder;
    }

    public boolean isInPastOrder() {
        return inPastOrder;
    }
    public void setInPastOrder(boolean inPastOrder) {
        this.inPastOrder = inPastOrder;
    }

    public boolean isInOtherFragment() {
        return inOtherFragment;
    }
    public void setInOtherFragment(boolean inOtherFragment) {
        this.inOtherFragment = inOtherFragment;
    }

    public boolean isInOrderDetail() {return inOrderDetail;}
    public void setInOrderDetail(boolean inOrderDetail) {this.inOrderDetail = inOrderDetail;}

    public boolean isInDropDetail() {return inDropDetail;}
    public void setInDropDetail(boolean inDropDetail) {this.inDropDetail = inDropDetail;}

    public boolean isInPickDetail() {return inPickDetail;}
    public void setInPickDetail(boolean inPickDetail) {this.inPickDetail = inPickDetail;}

    public boolean isInCompleteDetail() {return inCompleteDetail;}
    public void setInCompleteDetail(boolean inCompleteDetail) {this.inCompleteDetail = inCompleteDetail;}

    public boolean isInRestaurant() {return inRestaurant;}
    public void setInRestaurant(boolean inRestaurant) {this.inRestaurant = inRestaurant;}

    public boolean isInRestaurantMenu() {return inRestaurantMenu;}
    public void setInRestaurantMenu(boolean inRestaurantMenu) {this.inRestaurantMenu = inRestaurantMenu;}

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    private ActionBarDrawerToggle drawerToggle;

    public ArrayList<Restaurant> getRestaurantList() {
        return restaurantArrayList;
    }
    public void setRestaurantList(ArrayList<Restaurant> restaurantList) {
        restaurantArrayList.addAll(restaurantList);
    }
    ArrayList<Restaurant> restaurantArrayList= new ArrayList<>();

    public boolean isTotalLayoutVisible() {
        if(totalLayout.getVisibility()==View.VISIBLE)
            return true;
        else
            return false;
    }
    public void setTotalLayoutVisible() {
        totalLayout.setVisibility(View.VISIBLE);
    }
    public void setTotalLayoutGone() {
        totalLayout.setVisibility(View.GONE);
    }
    private LinearLayout totalLayout;

    BasketListViewAdapter ba;

    private WebService webService;
    Database mydb;
    private Dialog dialogBasket;
    private Dialog dialogCheckOut;
    private LinearLayout snackbar;
    private int hintDefaultColor;
    private TextView tvResNames;
    public TextView tvCount,tvTotal,tvCustomerName,tvCustomerEmialAddress;
    public void setTvCustomerEmialAddress(String email) {
        this.tvCustomerEmialAddress.setText(email);
    }
    public void setTvCustomerName(String name) {
        this.tvCustomerName.setText(name);
    }

    ArrayList<DeliveryType> deliveryTypeArrayList = new ArrayList<>();
    public void setDeliveryTypeArrayList(ArrayList<DeliveryType> deliveryTypes) {
        deliveryTypeArrayList.addAll(deliveryTypes);
    }

    public void addAllFoodItemCartArrayList(List<FoodItemCart> foodItemCartList) {
        foodItemCartArrayList.clear();
        foodItemCartArrayList.addAll(foodItemCartList);
    }
    private List<FoodItemCart> foodItemCartArrayList;

    Runnable mRunnable;
    Handler mHandler;
    private static int countRestros,countRImage;
    void init(){
        mydb = new Database(this);
        webService = new WebService(this);
        snackbar = (LinearLayout)findViewById(R.id.snackbar);
        totalLayout = (LinearLayout)findViewById(R.id.totalLayout);
        tvCount = (TextView)findViewById(R.id.tvCount);
        tvTotal = (TextView)findViewById(R.id.tvTotal);
        mHandler=new Handler();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_base);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        View headerView = nvDrawer.getHeaderView(0);
        tvCustomerName = (TextView) headerView.findViewById(R.id.tvCustomerName);
        MySharedPreferences mySharedPreferences = new MySharedPreferences();
        tvCustomerName.setText(mySharedPreferences.getUserTitle(this));
        tvCustomerEmialAddress = (TextView) headerView.findViewById(R.id.tvCustomerEmialAddress);
        tvCustomerEmialAddress.setText(mySharedPreferences.getUserEmail(this));
        init();
        mRunnable=new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                snackbar.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
            }
        };
        if(getIntent().getAction()==null) {
            //obtain  Intent Object send  from SenderActivity
            Intent intent = this.getIntent();
        /* Obtain String from Intent  */
            if (intent != null) {
                String strdata = intent.getExtras().getString("Uniqid");
                if (strdata.equals("From_Register")) {
                    loadHomeFragment();
                }
                else if(strdata.equals("From_Notification")){
                    loadMyOrderFragment();
                }
                else if(strdata.equals("From_SplashScreen")){
                    loadHomeFragment();
                }
                else if(strdata.equals("From_Order")){
                    loadMyOrderFragment();
                }

            } else {
                //do something here
            }
        }
    }

    public void isCartAvailable(){
        foodItemCartArrayList = mydb.getAllFoodItemCart();
        if(foodItemCartArrayList.size()>0){
            totalLayout.setVisibility(View.VISIBLE);
            int totalcount=0,totalPrice=0;
            for(FoodItemCart foodItemCart:foodItemCartArrayList){

                try {
                    totalcount += Integer.valueOf(foodItemCart.getQuantity());
                    totalPrice += foodItemCart.getPrice() * foodItemCart.getQuantity();
                } catch (NumberFormatException e) {
                    //fi.getPrice() is not a number;
                }
            }
            tvCount.setText(Integer.toString(totalcount));
            tvTotal.setText(""+totalPrice);
        }
        else {
            totalLayout.setVisibility(View.GONE);
        }
    }

    void loadHomeFragment(){
        Bundle bundle = new Bundle();
        nvDrawer.getMenu().getItem(0).setChecked(true);
//        bundle.putString("Uniqid","AppBaseActivity");
        // Set action bar title
        setTitle(nvDrawer.getMenu().getItem(0).getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();
    }

    void loadMyOrderFragment(){
        Bundle bundle = new Bundle();
        nvDrawer.getMenu().getItem(1).setChecked(true);
        // Set action bar title
        setTitle(nvDrawer.getMenu().getItem(1).getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new MyOrdersFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
//                if (!isTotalLayoutVisible()) {
//                    Database mydb = new Database(this);
//                    List<FoodItemCart> foodItemCartList = mydb.getAllFoodItemCart();
//                    if (foodItemCartList.size() > 0)
//                        setLinlaHeaderProgressVisible();
//                }
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_orders:
                lastPosition=-1;
                setTotalLayoutGone();
                fragmentClass = MyOrdersFragment.class;
                break;
//            case R.id.nav_notification:
//                fragmentClass = HomeFragment.class;
//                break;
            case R.id.nav_setting:
                lastPosition=-1;
                setTotalLayoutGone();
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_help:
                lastPosition=-1;
                setTotalLayoutGone();
                fragmentClass = HelpFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void onBasketClick(View view) {

        dialogBasket = new Dialog(AppBaseActivity.this,R.style.SuperMaterialTheme);
        dialogBasket.setContentView(R.layout.dialog_basket);
        dialogBasket.setTitle("Your Basket");
        // if button is clicked, close the custom dialog
        ListView listView = (ListView)dialogBasket.findViewById(R.id.listView);
        final TextView tvtotalPrice = (TextView)dialogBasket.findViewById(R.id.tvtotalPrice);
        //final TextView tvCharges = (TextView)dialogBasket.findViewById(R.id.tvCharges);
        final TextView tvTime = (TextView)dialogBasket.findViewById(R.id.tvTime);
        tvResNames = (TextView)dialogBasket.findViewById(R.id.tvResNames);
        String names="";
        for(Restaurant restaurant: mydb.getAllRestaurantCart()){
            names+=restaurant.getName()+"\n";
        }
        tvResNames.setText(names);
        int dc = Integer.valueOf(deliveryTypeArrayList.get(0).getExpectedDeliveryTime());
        if(dc<=60)
            //tvTime.setText(deliveryTypeArrayList.get(0).getExpectedDeliveryTime()+" minutes");
            tvTime.setText("25 to 45 Minutes after getting ready");
        else{
            int hours = dc/60;
            dc = dc%60;
            int minutes = dc;
            //tvTime.setText(hours+" Hours"+" and "+minutes+" minutes");
            tvTime.setText("25 to 45 Minutes after getting ready");
        }
        //tvCharges.setText(deliveryTypeArrayList.get(0).getExpectedCharges());
        final Button btnCheckOut = (Button)dialogBasket.findViewById(R.id.btnCheckOut);
        tvtotalPrice.setText(tvTotal.getText().toString());
        /// Pass results to RestroListViewAdapter Class
        ba = new BasketListViewAdapter(this, foodItemCartArrayList ,new BasketListViewAdapter.RemoveImageViewClick() {
            @Override
            public void onRemoveImageViewClick(int position) {
                FoodItemCart foodItemCart = (FoodItemCart) ba.getItem(position);
                mydb.deleteFoodItemCart(String.valueOf(foodItemCart.getId()));
                if(!mydb.isRestaurantCartFoodItemExist(foodItemCart.getRestaurantID())){
                    mydb.deleteRestaurantCart(String.valueOf(foodItemCart.getRestaurantID()));
                    String names="";
                    for(Restaurant restaurant:mydb.getAllRestaurantCart()){
                        names+=restaurant.getName()+"\n";
                    }
                    tvResNames.setText(names);
                }
                List<FoodItemCart> foodItemCarts = mydb.getAllFoodItemCart();
                int totalcount=0;
                int totalPrice=0;

                for (FoodItemCart fic: foodItemCarts) {
                    totalcount += Integer.valueOf(fic.getQuantity());
                    try {
                        totalPrice += fic.getPrice() * fic.getQuantity();
                    } catch (NumberFormatException e) {
                        //fi.getPrice() is not a number;
                    }
                }
                tvCount.setText(Integer.toString(totalcount));
                tvTotal.setText("" + totalPrice);
                tvtotalPrice.setText(Integer.toString(totalPrice));
                if(foodItemCarts.size()==0){
                    if(View.VISIBLE==totalLayout.getVisibility()) {
                        totalLayout.setVisibility(View.GONE);
                    }
                    dialogBasket.dismiss();
                }
                foodItemCartArrayList.clear();
                foodItemCartArrayList.addAll(mydb.getAllFoodItemCart());
                ba.notifyDataSetChanged();
            }

            @Override
            public void onIncBtnClick(int position) {
                int totalcount=0;
                int totalPrice=0;

                FoodItemCart foodItemCart = (FoodItemCart)ba.getItem(position);
                int q = (foodItemCart.getQuantity()+1);
                foodItemCart.setQuantity(q);
                mydb.updateFoodItemCartQuantity(String.valueOf(foodItemCart.getId()),foodItemCart);

                List<FoodItemCart> foodItemCarts = mydb.getAllFoodItemCart();
                for (FoodItemCart fic: foodItemCarts) {
                    totalcount += Integer.valueOf(fic.getQuantity());
                    try {
                        totalPrice += fic.getPrice() * fic.getQuantity();
                    } catch (NumberFormatException e) {
                        //fi.getPrice() is not a number;
                    }
                }
                tvCount.setText(Integer.toString(totalcount));
                tvTotal.setText("" + totalPrice);
                tvtotalPrice.setText(Integer.toString(totalPrice));
                foodItemCartArrayList.clear();
                foodItemCartArrayList.addAll(mydb.getAllFoodItemCart());
                ba.notifyDataSetChanged();
            }

            @Override
            public void onDecBtnClick(int position) {
                int totalcount=0;
                int totalPrice=0;

                FoodItemCart foodItemCart = (FoodItemCart) ba.getItem(position);
                int q = (foodItemCart.getQuantity()-1);
                foodItemCart.setQuantity(q);
                if(q<1){
                    mydb.deleteFoodItemCart(String.valueOf(foodItemCart.getId()));
                    if(!mydb.isRestaurantCartFoodItemExist(foodItemCart.getRestaurantID())){
                        mydb.deleteRestaurantCart(String.valueOf(foodItemCart.getRestaurantID()));
                        String names="";
                        for(Restaurant restaurant:mydb.getAllRestaurantCart()){
                            names+=restaurant.getName()+"\n";
                        }
                        tvResNames.setText(names);
                    }
                }
                else {
                    mydb.updateFoodItemCartQuantity(String.valueOf(foodItemCart.getId()),foodItemCart);
                }
                List<FoodItemCart> foodItemCarts = mydb.getAllFoodItemCart();
                for (FoodItemCart fic: foodItemCarts) {
                    totalcount+= Integer.valueOf(fic.getQuantity());
                    try {
                        totalPrice += fic.getPrice() * fic.getQuantity();
                    } catch (NumberFormatException e) {
                        //fi.getPrice() is not a number;
                    }
                }
                tvCount.setText(Integer.toString(totalcount));
                tvTotal.setText("" + totalPrice);
                tvtotalPrice.setText(Integer.toString(totalPrice));
                if(foodItemCarts.size()==0){
                    if(View.VISIBLE==totalLayout.getVisibility()) {
                        totalLayout.setVisibility(View.GONE);
                    }
                    dialogBasket.dismiss();
                }
                foodItemCartArrayList.clear();
                foodItemCartArrayList.addAll(mydb.getAllFoodItemCart());
                ba.notifyDataSetChanged();
            }
        });
        // Binds the Adapter to the ListView
        listView.setAdapter(ba);
//        setListViewHeightBasedOnChildren(listView);
        dialogBasket.show();

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBasket.dismiss();
                prceedOrder();
            }
        });
    }

    public boolean isEditTextEmpty(EditText editText){
        String ed_text = editText.getText().toString().trim();
        if(ed_text.length() == 0 || ed_text.equals("") || ed_text == null)
        {
            if(editText.getCurrentHintTextColor()!= Color.RED) {
                hintDefaultColor = editText.getCurrentHintTextColor();
                editText.setHintTextColor(Color.RED);
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    public void prceedOrder(){
        dialogCheckOut = new Dialog(AppBaseActivity.this,R.style.MyAlertDialogStyle);
        dialogCheckOut.setContentView(R.layout.dialog_checkout);
        dialogCheckOut.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText etName = (EditText)dialogCheckOut.findViewById(R.id.etName);
        final EditText etNumber = (EditText)dialogCheckOut.findViewById(R.id.etNumber);
        final EditText etAddress = (EditText)dialogCheckOut.findViewById(R.id.etAddress);
        final EditText etDetail = (EditText)dialogCheckOut.findViewById(R.id.etDetail);
        final LinearLayout linlaHeaderProgress = (LinearLayout) dialogCheckOut.findViewById(R.id.Progress);
        setDefaultHintColor(etName);
        setDefaultHintColor(etNumber);
        setDefaultHintColor(etAddress);
        final Button btnCancel = (Button)dialogCheckOut.findViewById(R.id.btnCancel);
        final Button btnOrder = (Button)dialogCheckOut.findViewById(R.id.btnOrder);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCheckOut.dismiss();
                Log.d("Order_address",etAddress.getText().toString());
            }
        });
        final MySharedPreferences mySharedPreferences = new MySharedPreferences();
        etName.setText(mySharedPreferences.getUserTitle(this));
        etNumber.setText(mySharedPreferences.getUserMobileNumber(this));
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag1 = isEditTextEmpty(etName);
                boolean flag2 = isEditTextEmpty(etNumber);
                boolean flag3 = isEditTextEmpty(etAddress);
                Helper helper = new Helper();
                if (flag1 || flag2 || flag3) {
                    final Dialog dialog = new Dialog(AppBaseActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView) dialog.findViewById(R.id.tvAlert);
                    String alert = "";
                    if (flag1)
                        alert += "\n" + getResources().getString(R.string.name_alert);
                    if (flag2)
                        alert += "\n" + getResources().getString(R.string.contact_number_alert);
                    if (flag3)
                        alert += "\n" + getResources().getString(R.string.address_alert);

                    tvAlert.setText(alert);
                    Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else if (!helper.isValidMobile(etNumber)) {
                    // custom dialog
                    final Dialog dialog = new Dialog(AppBaseActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView) dialog.findViewById(R.id.tvAlert);
                    tvAlert.setText(getResources().getString(R.string.number_alert));
                    Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {

                    JSONArray foodItemID = new JSONArray();
                    JSONArray foodItemQuantity = new JSONArray();

                    List<FoodItemCart> foodItemCarts = mydb.getAllFoodItemCart();
                    for(FoodItemCart fic: foodItemCarts){
                        foodItemID.put(fic.getId());
                        foodItemQuantity.put(fic.getQuantity());
                    }

                    if (webService.isNetworkConnected()) {
                        btnOrder.setEnabled(false);
                        btnCancel.setEnabled(false);
                        linlaHeaderProgress.setVisibility(View.VISIBLE);
                        User user = new User(etName.getText().toString(), etNumber.getText().toString(), etAddress.getText().toString());
                        final JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("food_Item_ID", foodItemID);
                            jsonObject.put("food_Item_quantity", foodItemQuantity);
                            GeneralOrder generalOrder = new GeneralOrder();
                            generalOrder.setDetail(etDetail.getText().toString());
                            generalOrder.setDeliveryCharges(deliveryTypeArrayList.get(0).getExpectedCharges());
                            generalOrder.setDeliveryTime(deliveryTypeArrayList.get(0).getExpectedDeliveryTime());
                            Log.d("data",jsonObject.toString());

                            webService.proceedFoodOrder(getString(R.string.url) + "save_and_process", user,generalOrder,tvResNames.getText().toString(), jsonObject, String.valueOf(mySharedPreferences.getUserID(AppBaseActivity.this)),new WebService.VolleyResponseListener() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {

                                }

                                @Override
                                public void onSuccess(String response) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response);
                                        boolean success = jsonObject1.getBoolean("success");
                                        if(success){
                                            linlaHeaderProgress.setVisibility(View.GONE);
                                            dialogCheckOut.dismiss();
                                            totalLayout.setVisibility(View.INVISIBLE);

                                            mydb.drop();
                                            setTotalLayoutGone();
                                            FragmentManager fm = getSupportFragmentManager();
                                            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                                fm.popBackStack();
                                            }
                                            Intent i = new Intent(AppBaseActivity.this, AppBaseActivity.class);
                                            i.putExtra("Uniqid","From_Order");
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        }
                                        else {
                                            linlaHeaderProgress.setVisibility(View.GONE);
                                            dialogCheckOut.dismiss();
                                            Toast.makeText(AppBaseActivity.this, jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                                @Override
                                public void onError(VolleyError error) {
                                    btnOrder.setEnabled(true);
                                    btnCancel.setEnabled(true);
                                    Helper h = new Helper();
                                    h.volleyErrorMessage(AppBaseActivity.this,error);
                                    linlaHeaderProgress.setVisibility(View.GONE);
                                }
                            });

                        } catch (Exception ex) {
                            btnOrder.setEnabled(true);
                            btnCancel.setEnabled(true);
                            Toast.makeText(AppBaseActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                            linlaHeaderProgress.setVisibility(View.VISIBLE);
                        }
                    } else {
                        setSnackBarVisible();
                    }
                }
            }
        });
        dialogCheckOut.setCancelable(false);
        dialogCheckOut.show();
    }

    public void setDefaultHintColor(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText.getCurrentHintTextColor()== Color.RED)
                    editText.setHintTextColor(hintDefaultColor);
            }
        });
    }

    public void onOrderStatusClick(View view) {
    }

    public void onSnackbarClick(View view) {
        setSnackBarGone();
    }
    public void setSnackBarVisible(){
        snackbar.setVisibility(View.VISIBLE);
        mHandler.postDelayed(mRunnable, 4 * 1000);
    }
    public void setSnackBarGone(){
        snackbar.setVisibility(View.GONE);
        mHandler.removeCallbacks(mRunnable);
    }
    public boolean isSnackBarVisible(){
        if(snackbar.getVisibility()==View.VISIBLE)
            return true;
        else
            return false;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public int lastPosition = -1;
    @Override
    public void onBackPressed() {

        //int count = getSupportFragmentManager().getBackStackEntryCount();
        if(inRestaurantMenu||inDropDetail||inOtherFragment||inPastOrder||inProcessOrder) {
            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            loadHomeFragment();
        }
        else if(inPickDetail||inCompleteDetail) {
            getSupportFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }

    }

}
