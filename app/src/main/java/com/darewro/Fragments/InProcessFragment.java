package com.darewro.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.R;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.MySharedPreferences;
import com.darewro.Utilities.WebService;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Jaffar on 2018-01-31.
 */

public class InProcessFragment extends Fragment {

    private CountDownTimer mReadyTimer;//mDeliveryTime;

    TextView tvOrderNumber,tvESTime,tvESRTime,tvDeliverCharges,tvBillAmount;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
    }

    private WebService webService;
    Activity activity;

    LinearLayout progress,content,layoutCharges;
    RelativeLayout layoutReadyTimer;
    ImageView refreshImageView;

    private String remainingReadyTime,remainingDeliveryTime,riderName,riderNumber,maxTime,minTime,deliveryCharges,bill;
    public InProcessFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_in_process, container, false);
        mRecyclerView = (RecyclerView)rootView. findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

        tvOrderNumber = (TextView) rootView.findViewById(R.id.tvOrderNumber);
        tvESTime = (TextView) rootView.findViewById(R.id.tvESTime);
        tvESRTime= (TextView) rootView.findViewById(R.id.tvESRTime);
        tvDeliverCharges= (TextView) rootView.findViewById(R.id.tvDeliverCharges);
        tvBillAmount= (TextView) rootView.findViewById(R.id.tvBillAmount);

        content = (LinearLayout) rootView.findViewById(R.id.contant);
        layoutCharges = (LinearLayout) rootView.findViewById(R.id.layoutCharges);
        progress = (LinearLayout) rootView.findViewById(R.id.Progress);
        layoutReadyTimer = (RelativeLayout) rootView.findViewById(R.id.layoutReadyTimer);
        refreshImageView = (ImageView) rootView.findViewById(R.id.refreshImageView);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshClick(view);
            }
        });
        content.setVisibility(View.GONE);
        loadInProcessOrder();
        // Inflate the layout for this fragment
        return rootView;
    }

    public void loadInProcessOrder(){
        if (webService.isNetworkConnected()){
            if(((AppBaseActivity)activity).isSnackBarVisible()) {
                ((AppBaseActivity)activity).setSnackBarGone();
            }
            refreshImageView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            getInProcessOrder();

        }
        else {
            ((AppBaseActivity)activity).setSnackBarVisible();
            refreshImageView.setVisibility(View.VISIBLE);
        }
    }

    public void getInProcessOrder(){
        MySharedPreferences mySharedPreferences = new MySharedPreferences();
        webService.getOrders(getString(R.string.url) + "get_current_order_by_user_id",String.valueOf(mySharedPreferences.getUserID(activity)), new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    progress.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean order = jsonObject.getBoolean("order");
                    if (order){
                        maxTime = jsonObject.getString("max_time");
                        minTime = jsonObject.getString("min_time");
                        JSONObject orderDetail = jsonObject.getJSONObject("order_detail");
                        tvOrderNumber.setText("#"+orderDetail.getString("order_id"));
                        String orderStatus = orderDetail.getString("order_status");
                        String orderType = orderDetail.getString("order_type");
                        remainingReadyTime = orderDetail.getString("remaining_ready_time");
                        remainingDeliveryTime = orderDetail.getString("remaining_delivery_time");
                        deliveryCharges = orderDetail.getString("delivery_charges");
                        bill = jsonObject.getString("restuarant_total");
                        if(!orderDetail.isNull("rider_name"))
                            riderName = orderDetail.getString("rider_name");
                        if(!orderDetail.isNull("office_no"))
                            riderNumber = orderDetail.getString("office_no");
                        remainingDeliveryTime = orderDetail.getString("remaining_delivery_time");
                        initView(orderStatus,orderType);
                    }
                    else {
                        // HIDE THE SPINNER AFTER LOADING FEEDS
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
    private void initView(String orderStatus,String orderType) {
        setDataListItems(orderStatus,orderType);
        mTimeLineAdapter = new TimeLineAdapter(mDataList, Orientation.VERTICAL, true);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }
    private void setDataListItems(String orderStatus,String orderType){

        if(orderType.matches("General Order")) {
            mDataList.add(new TimeLineModel("ORDER PLACED", "We have recieved your order", OrderStatus.COMPLETED, R.drawable.orderplaced));
            if(orderStatus.matches("2")) {
//                int time = Integer.valueOf(remainingDeliveryTime);
//                mDeliveryTime=new CountDownTimer(time*1000, 1000) {
//                    public void onTick(long millisUntilFinished) {
//                        int seconds = (int) (millisUntilFinished / 1000) % 60 ;
//                        int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
//                        int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
//                        tvESTime.setText(String.format("%02d:%02d:%02d",hours,minutes,seconds));
//                    }
//                    public void onFinish() {
//                        tvESTime.setText("Time Up");
//                    }
//                };
//                mDeliveryTime.start();
                tvESTime.setText("SOON");
                mDataList.add(new TimeLineModel("ORDER CONFIRMED", "Your order has been confirmed", OrderStatus.ACTIVE, R.drawable.order_confirmed_active));
                mDataList.add(new TimeLineModel("DISPATCHED", "Your order is on the way", OrderStatus.INACTIVE, R.drawable.dispatch_inactive));
                mDataList.add(new TimeLineModel("DELIVERED", "It's at your doorstep", OrderStatus.INACTIVE, R.drawable.deliver));
                tvDeliverCharges.setText("PKR "+deliveryCharges);
                layoutCharges.setVisibility(View.VISIBLE);
            }
            if(orderStatus.matches("3")){
//                int time = Integer.valueOf(remainingDeliveryTime);
//                mDeliveryTime=new CountDownTimer(time*1000, 1000) {
//                    public void onTick(long millisUntilFinished) {
//                        int seconds = (int) (millisUntilFinished / 1000) % 60 ;
//                        int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
//                        int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
//                        tvESTime.setText(String.format("%02d:%02d:%02d",hours,minutes,seconds));
//                    }
//                    public void onFinish() {
//                        tvESTime.setText("Time Up");
//                    }
//                };
//                mDeliveryTime.start();
                tvESTime.setText("SOON");
                mDataList.add(new TimeLineModel("ORDER CONFIRMED", "Your order has been confirmed", OrderStatus.COMPLETED, R.drawable.order_confirmed_active));
                mDataList.add(new TimeLineModel("DISPATCHED", riderName+" has been assigned to your order\nRider cell#: " + riderNumber, OrderStatus.ACTIVE, R.drawable.dispatch_active));
                mDataList.add(new TimeLineModel("DELIVERED", "It's at your doorstep", OrderStatus.INACTIVE, R.drawable.deliver));
                tvDeliverCharges.setText("PKR "+deliveryCharges);
                layoutCharges.setVisibility(View.VISIBLE);
            }
            if(orderStatus.matches("4")){
                mDataList.add(new TimeLineModel("ORDER CONFIRMED", "Your order has been confirmed", OrderStatus.COMPLETED, R.drawable.order_confirmed_active));
                mDataList.add(new TimeLineModel("DISPATCHED", "Your order is on the way", OrderStatus.COMPLETED, R.drawable.dispatch_active));
                mDataList.add(new TimeLineModel("DELIVERED", "It's at your doorstep", OrderStatus.ACTIVE, R.drawable.deliver));
                tvDeliverCharges.setText("PKR "+deliveryCharges);
                layoutCharges.setVisibility(View.VISIBLE);
            }
        }
        if(orderType.matches("Food Order")) {
            if(orderStatus.matches("1")) {
                mDataList.add(new TimeLineModel("ORDER PLACED", "We have recieved your order", OrderStatus.ACTIVE, R.drawable.orderplaced));
                mDataList.add(new TimeLineModel("RESTAURANT CONFIRMED", "Your order has been confirmed", OrderStatus.INACTIVE, R.drawable.order_confirmed_inactive));
                mDataList.add(new TimeLineModel("PREPARING", "Your order is getting ready", OrderStatus.INACTIVE, R.drawable.chef_inactive));
                mDataList.add(new TimeLineModel("DISPATCHED", "Your order is on the way", OrderStatus.INACTIVE, R.drawable.dispatch_inactive));
                mDataList.add(new TimeLineModel("DELIVERED", "It's at your doorstep", OrderStatus.INACTIVE, R.drawable.deliver));
            }
            if(orderStatus.matches("2")) {
//                int time = Integer.valueOf(remainingDeliveryTime);
//                mDeliveryTime=new CountDownTimer(time*1000, 1000) {
//                    public void onTick(long millisUntilFinished) {
//                        int seconds = (int) (millisUntilFinished / 1000) % 60 ;
//                        int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
//                        int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
//                        tvESTime.setText(String.format("%02d:%02d:%02d",hours,minutes,seconds));
//                    }
//                    public void onFinish() {
//                        tvESTime.setText("Time Up");
//                    }
//                };
//                mDeliveryTime.start();
//                tvESTime.setText("After food prepare");
                layoutReadyTimer.setVisibility(View.VISIBLE);
                int time1 = Integer.valueOf(remainingReadyTime);
                mReadyTimer=new CountDownTimer(time1*1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                        int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
                        int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
                        tvESRTime.setText(String.format("%02d:%02d:%02d",hours,minutes,seconds));
                    }
                    public void onFinish() {
                        tvESRTime.setText("Time Up");
                        layoutReadyTimer.setVisibility(View.GONE);
                        tvESTime.setText("in "+minTime+" to "+maxTime+" minutes");
                    }
                };
                mReadyTimer.start();
                mDataList.add(new TimeLineModel("ORDER PLACED", "We have recieved your order", OrderStatus.COMPLETED, R.drawable.orderplaced));
                mDataList.add(new TimeLineModel("RESTAURANT CONFIRMED", "Your order has been confirmed", OrderStatus.COMPLETED, R.drawable.order_confirmed_active));
                mDataList.add(new TimeLineModel("PREPARING", "Your order is getting ready", OrderStatus.ACTIVE, R.drawable.chef_active));
                mDataList.add(new TimeLineModel("DISPATCHED", "Your order is on the way", OrderStatus.INACTIVE, R.drawable.dispatch_inactive));
                mDataList.add(new TimeLineModel("DELIVERED", "It's at your doorstep", OrderStatus.INACTIVE, R.drawable.deliver));
                tvDeliverCharges.setText("PKR "+deliveryCharges);
                layoutCharges.setVisibility(View.VISIBLE);
                tvBillAmount.setText("PKR "+bill);
            }
            if(orderStatus.matches("3")){
//                int time = Integer.valueOf(remainingDeliveryTime);
//                mDeliveryTime=new CountDownTimer(time*1000, 1000) {
//                    public void onTick(long millisUntilFinished) {
//                        int seconds = (int) (millisUntilFinished / 1000) % 60 ;
//                        int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
//                        int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
//                        tvESTime.setText(String.format("%02d:%02d:%02d",hours,minutes,seconds));
//                    }
//                    public void onFinish() {
//                        tvESTime.setText("Time Up");
//                    }
//                };
//                mDeliveryTime.start();
                layoutReadyTimer.setVisibility(View.VISIBLE);
                int time1 = Integer.valueOf(remainingReadyTime);
                mReadyTimer=new CountDownTimer(time1*1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                        int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
                        int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
                        tvESRTime.setText(String.format("%02d:%02d:%02d",hours,minutes,seconds));
                    }
                    public void onFinish() {
                        tvESRTime.setText("Time Up");
                        layoutReadyTimer.setVisibility(View.GONE);
                        tvESTime.setText("in "+minTime+" to "+maxTime+" minutes");
                    }
                };
                mReadyTimer.start();
                mDataList.add(new TimeLineModel("ORDER PLACED", "We have recieved your order", OrderStatus.COMPLETED, R.drawable.orderplaced));
                mDataList.add(new TimeLineModel("RESTAURANT CONFIRMED", "Your order has been confirmed", OrderStatus.COMPLETED, R.drawable.order_confirmed_active));
                mDataList.add(new TimeLineModel("PREPARING", "Your order is getting ready", OrderStatus.COMPLETED, R.drawable.chef_active));
                mDataList.add(new TimeLineModel("DISPATCHED", riderName+" has been assigned to your order\nRider cell#: " + riderNumber, OrderStatus.ACTIVE, R.drawable.dispatch_active));
                //on click call to rider
                mDataList.add(new TimeLineModel("DELIVERED", "It's at your doorstep", OrderStatus.INACTIVE, R.drawable.deliver));
                tvDeliverCharges.setText("PKR "+deliveryCharges);
                tvBillAmount.setText("PKR "+bill);
                layoutCharges.setVisibility(View.VISIBLE);
            }
            if(orderStatus.matches("4")){
                mDataList.add(new TimeLineModel("ORDER PLACED", "We have recieved your order", OrderStatus.COMPLETED, R.drawable.orderplaced));
                mDataList.add(new TimeLineModel("RESTAURANT CONFIRMED", "Your order has been confirmed", OrderStatus.COMPLETED, R.drawable.order_confirmed_active));
                mDataList.add(new TimeLineModel("PREPARING", "We are preparing your order", OrderStatus.COMPLETED, R.drawable.chef_active));
                mDataList.add(new TimeLineModel("DISPATCHED", "Your order is on the way", OrderStatus.COMPLETED, R.drawable.dispatch_active));
                mDataList.add(new TimeLineModel("DELIVERED", "It's at your doorstep", OrderStatus.ACTIVE, R.drawable.deliver));
                tvDeliverCharges.setText("PKR "+deliveryCharges);
                tvBillAmount.setText("PKR "+bill);
                layoutCharges.setVisibility(View.VISIBLE);
            }
        }
        content.setVisibility(View.VISIBLE);
    }

    public void onRefreshClick(View view) {
        loadInProcessOrder();
    }

}
