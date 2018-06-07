package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.Adapters.OrdersListViewAdapter;
import com.darewro.Adapters.RestroListViewAdapter;
import com.darewro.Models.DeliveryType;
import com.darewro.Models.Orders;
import com.darewro.Models.Restaurant;
import com.darewro.R;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.MySharedPreferences;
import com.darewro.Utilities.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaffar on 2018-01-31.
 */

public class PastOrdersFragment extends Fragment {
    private Dialog dialogOrderDetail;

    private List<Orders> ordersList;
    private ImageView refreshImageView;
    private LinearLayout progress,content;
    Activity activity;
    OrdersListViewAdapter ordersListViewAdapter;

    private WebService webService;
    private ListView lvOrders;

    public PastOrdersFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_past_orders, container, false);
        refreshImageView = (ImageView)rootView. findViewById(R.id.refreshImageView);
        progress = (LinearLayout) rootView. findViewById(R.id.Progress);

        lvOrders = (ListView)rootView.findViewById(R.id.lvOrders);
        content = (LinearLayout) rootView.findViewById(R.id.content);
        progress = (LinearLayout) rootView.findViewById(R.id.Progress);
        refreshImageView = (ImageView) rootView.findViewById(R.id.refreshImageView);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshClick(view);
            }
        });
        content.setVisibility(View.GONE);
        loadAllOrders();
        // Inflate the layout for this fragment
        return rootView;
    }

    public void loadAllOrders(){
        if (webService.isNetworkConnected()){
            if(((AppBaseActivity)activity).isSnackBarVisible()) {
                ((AppBaseActivity)activity).setSnackBarGone();
            }
            refreshImageView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            getAllOrders();

        }
        else {
            ((AppBaseActivity)activity).setSnackBarVisible();
            refreshImageView.setVisibility(View.VISIBLE);
        }
    }

    public void getAllOrders() {
        MySharedPreferences mySharedPreferences = new MySharedPreferences();
        webService.getOrders(getString(R.string.url) + "get_past_orders_by_user_id", String.valueOf(mySharedPreferences.getUserID(activity)),new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean order = jsonObject.getBoolean("order");
                    if (order){
                        ordersList = new ArrayList<>();
                        JSONArray orderDetails = jsonObject.getJSONArray("order_detail");
                        for (int i = 0; i < orderDetails.length(); i++) {
                            JSONObject od = orderDetails.getJSONObject(i);
                            Orders orders = new Orders();
                            orders.setId(od.getInt("order_id"));
                            orders.setDate(od.getString("order_date_time"));
                            orders.setPrice(od.getString("delivery_charges"));
                            orders.setStatus(od.getString("OrderStatus"));
                            orders.setPickAddress(od.getString("order_picking_address"));
                            orders.setDropAddress(od.getString("order_drop_address"));
                            orders.setCancelReason(od.getString("reason"));

                            ordersList.add(orders);
                        }
                        setOrders();
                    }
                    else {
                        // HIDE THE SPINNER AFTER LOADING FEEDS
                        progress.setVisibility(View.GONE);
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    // HIDE THE SPINNER AFTER LOADING FEEDS
                    refreshImageView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // HIDE THE SPINNER AFTER LOADING FEEDS
                refreshImageView.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                Helper h = new Helper();
                h.volleyErrorMessage(activity,error);
            }
        });
    }


    public void setOrders(){
        /// Pass results to RestroListViewAdapter Class
        ordersListViewAdapter = new OrdersListViewAdapter(activity, ordersList, new OrdersListViewAdapter.OnViewClick() {
            @Override
            public void onViewClick(int id, String date, String price, String status) {
                dialogOrderDetail = new Dialog(activity,R.style.SuperMaterialTheme);
                dialogOrderDetail.setContentView(R.layout.dialog_basket);
                dialogOrderDetail.setTitle("Order Detail");
            }
        });
        // Binds the Adapter to the ListView
        lvOrders.setAdapter(ordersListViewAdapter);
        progress.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    public void onRefreshClick(View view) {
        loadAllOrders();
    }
}
