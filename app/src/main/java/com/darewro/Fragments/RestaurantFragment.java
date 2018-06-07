package com.darewro.Fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.Adapters.RestroListViewAdapter;
import com.darewro.Models.DeliveryType;
import com.darewro.Models.Restaurant;
import com.darewro.R;
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

public class RestaurantFragment extends Fragment {

    private List<Restaurant> restaurantList;
    private ImageView refreshImageView;
    int countRestros,countRImage;
    LinearLayout linlaHeaderProgress;
    RelativeLayout relativeLayout;

    Activity activity;
    RestroListViewAdapter restroListViewAdapter;
    private WebService webService;
    private ListView lvRestro;

    public static final String RESTRO_NAME = "restro_name";
    public static final String RESTRO_DESC = "restro_desc";
    public static final String RESTRO_IMAGE = "restro_image";
    public static final String RESTRO_ID = "restro_id";
    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        webService = new WebService(activity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_restaurant, container, false);

        linlaHeaderProgress = (LinearLayout)rootView. findViewById(R.id.Progress);
        relativeLayout = (RelativeLayout) rootView. findViewById(R.id.relativeLayout);
        refreshImageView = (ImageView) rootView. findViewById(R.id.refreshImageView);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshClick(view);
            }
        });
        lvRestro = (ListView)rootView.findViewById(R.id.lvRestro);
        if(((AppBaseActivity)activity).getRestaurantList()==null||((AppBaseActivity)activity).getRestaurantList().size()==0)
            loadData();
        else
            setRestaurants();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void loadData() {
        relativeLayout.setVisibility(View.GONE);
        if (webService.isNetworkConnected()){
            if(((AppBaseActivity)activity).isSnackBarVisible()) {
                ((AppBaseActivity)activity).setSnackBarGone();
            }
            refreshImageView.setVisibility(View.GONE);
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            getRestaurants();

        }
        else {
            ((AppBaseActivity)activity).setSnackBarVisible();
            refreshImageView.setVisibility(View.VISIBLE);
        }
    }
    public void getRestaurants() {
        webService.getData(getString(R.string.url) + "get_restaurants", new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean exist = jsonObject.getBoolean("exist");
                    if (exist){
                        ArrayList<Restaurant> mRestaurantArrayList = new ArrayList<>();
                        ArrayList<DeliveryType> mDeliveryTypeArrayList = new ArrayList<>();
                        JSONArray deliveryTypes = jsonObject.getJSONArray("delivery_types");
                        for (int i = 0; i < deliveryTypes.length(); i++) {
                            JSONObject dt = deliveryTypes.getJSONObject(i);
                            DeliveryType deliveryType = new DeliveryType();
                            deliveryType.setId(dt.getInt("delivery_id"));
                            deliveryType.setTitle(dt.getString("delivery_type_title"));
                            deliveryType.setDetail(dt.getString("delivery_type_detail"));
                            deliveryType.setExpectedCharges(dt.getString("expected_charges"));
                            deliveryType.setExpectedDeliveryTime(dt.getString("expected_delivery_time"));
                            deliveryType.setExpectedDeliveryTime(dt.getString("expected_delivery_time"));

                            mDeliveryTypeArrayList.add(deliveryType);
                        }
                        JSONArray restaurants = jsonObject.getJSONArray("restaurants");

                        for (int r = 0; r < restaurants.length(); r++) {
                            countRestros++;
                            JSONObject restaurantJSON = restaurants.getJSONObject(r);
                            final Restaurant restaurant = new Restaurant();
                            restaurant.setId(restaurantJSON.getInt("restaurant_id"));
                            restaurant.setName(restaurantJSON.getString("restaurant_name"));
                            restaurant.setDesc(restaurantJSON.getString("restaurant_location"));
                            restaurant.setOpen(restaurantJSON.getBoolean("closed_open"));
                            restaurant.setOpeningTime(restaurantJSON.getString("restaurant_start_time"));
                            restaurant.setClosingTime(restaurantJSON.getString("restaurant_close_time"));
                            String data[] = restaurantJSON.getString("restaurant_logo").split("\\.");

                            String resImageName = data[0];
                            String resImageExe = data[1];
                            webService.loadImage(getResources().getString(R.string.image_url) + resImageName + "_thumb." + resImageExe, new WebService.VolleyImageResponse() {
                                @Override
                                public void onSuccess(Bitmap bitmap) {
                                    countRImage++;
                                    restaurant.setImage(bitmap);
                                    if (countRestros == countRImage) {
                                        setRestaurants();
                                    }
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    countRImage++;
                                    restaurant.setImage(null);
                                    if (countRestros == countRImage) {
                                        setRestaurants();
                                    }
                                }
                            });
                            mRestaurantArrayList.add(restaurant);
                        }
                        ((AppBaseActivity)activity).setRestaurantList(mRestaurantArrayList);
                        ((AppBaseActivity)activity).setDeliveryTypeArrayList(mDeliveryTypeArrayList);
                    }
                    else {
                        // HIDE THE SPINNER AFTER LOADING FEEDS
                        refreshImageView.setVisibility(View.VISIBLE);
                        linlaHeaderProgress.setVisibility(View.GONE);
                        Toast.makeText(activity, "No restaurant found", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    // HIDE THE SPINNER AFTER LOADING FEEDS
                    refreshImageView.setVisibility(View.VISIBLE);
                    linlaHeaderProgress.setVisibility(View.GONE);
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(String response) {

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

    public void setRestaurants(){
        /// Pass results to RestroListViewAdapter Class
        if(!((AppBaseActivity)activity).isTotalLayoutVisible()&&((AppBaseActivity)activity).isInRestaurant())
            ((AppBaseActivity)activity).isCartAvailable();
        restroListViewAdapter = new RestroListViewAdapter(activity, ((AppBaseActivity)activity).getRestaurantList(),new RestroListViewAdapter.OnViewClick() {
            @Override
            public void onViewClick(int position, int id, String name, String desc, byte[] bytes) {
                // Send single item click data to SingleItemView Class
                webService = new WebService(activity);
                if (webService.isNetworkConnected()){
                    if(((AppBaseActivity)activity).isSnackBarVisible()) {
                        ((AppBaseActivity)activity).setSnackBarGone();
                    }
                    ((AppBaseActivity)activity).setLastPosition(position);
                    RestaurantMenuFragment fragment = new RestaurantMenuFragment();
                    Bundle args = new Bundle();
                    args.putInt(RESTRO_ID, id);
                    args.putString(RESTRO_NAME, name);
                    args.putString(RESTRO_DESC, desc);
                    args.putByteArray(RESTRO_IMAGE,bytes);

                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.flContent, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
                }
                else {
                    ((AppBaseActivity)activity).setSnackBarVisible();
                }
            }
        });
        // Binds the Adapter to the ListView
        lvRestro.setAdapter(restroListViewAdapter);
        lvRestro.setSelection(((AppBaseActivity)activity).getLastPosition());
        relativeLayout.setVisibility(View.VISIBLE);
        linlaHeaderProgress.setVisibility(View.GONE);
    }

    public void onRefreshClick(View view) {
        loadData();
    }

}
