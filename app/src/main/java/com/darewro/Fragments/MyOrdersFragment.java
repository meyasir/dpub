package com.darewro.Fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.Models.DeliveryType;
import com.darewro.Models.Restaurant;
import com.darewro.R;
import com.darewro.Utilities.MySharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jaffar on 2018-01-29.
 */

public class MyOrdersFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int int_items = 2 ;

    Activity activity;

    public MyOrdersFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        viewPager = (ViewPager)rootView. findViewById(R.id.view_pager);
        tabLayout = (TabLayout)rootView. findViewById(R.id.tabs);

        viewPager.setAdapter(new MyAdapter(getFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    setInProcessOrder();
                }else {
                    setInPastOrder();
                }
            }
        });
        setInProcessOrder();
        return rootView;
    }

    public void setInProcessOrder(){
        ((AppBaseActivity)activity).setInOrderDetail(false);
        ((AppBaseActivity)activity).setInRestaurant(false);
        ((AppBaseActivity)activity).setInDropDetail(false);
        ((AppBaseActivity)activity).setInPickDetail(false);
        ((AppBaseActivity)activity).setInCompleteDetail(false);
        ((AppBaseActivity)activity).setInRestaurantMenu(false);
        ((AppBaseActivity)activity).setInOtherFragment(false);
        ((AppBaseActivity)activity).setInProcessOrder(true);
        ((AppBaseActivity)activity).setInPastOrder(false);
    }
    public void setInPastOrder(){
        ((AppBaseActivity)activity).setInOrderDetail(false);
        ((AppBaseActivity)activity).setInRestaurant(false);
        ((AppBaseActivity)activity).setInDropDetail(false);
        ((AppBaseActivity)activity).setInPickDetail(false);
        ((AppBaseActivity)activity).setInCompleteDetail(false);
        ((AppBaseActivity)activity).setInRestaurantMenu(false);
        ((AppBaseActivity)activity).setInOtherFragment(false);
        ((AppBaseActivity)activity).setInProcessOrder(false);
        ((AppBaseActivity)activity).setInPastOrder(true);

    }
    private class MyAdapter extends FragmentStatePagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new InProcessFragment();
                case 1 : return new PastOrdersFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return "In process";
                case 1 :
                    return "Past Orders";
            }
            return null;
        }

    }
}
