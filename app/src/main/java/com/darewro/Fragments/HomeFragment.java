package com.darewro.Fragments;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darewro.Activities.AppBaseActivity;
import com.darewro.R;


public class HomeFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int int_items = 2 ;

    Activity activity;

    public HomeFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = (ViewPager)rootView. findViewById(R.id.view_pager);
        tabLayout = (TabLayout)rootView. findViewById(R.id.tabs);

        viewPager.setAdapter(new MyAdapter(getFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

//        if(((AppBaseActivity)activity).isInDropDetail()) {
//            Toast.makeText(activity, "Ok", Toast.LENGTH_SHORT).show();
//            viewPager.setCurrentItem(1);
//            if(((AppBaseActivity)activity).isTotalLayoutVisible())
//                ((AppBaseActivity)activity).setTotalLayoutGone();
//            setInOrder();
//        }
//        else {

//        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    if(!((AppBaseActivity)activity).isTotalLayoutVisible()&&
                            ((AppBaseActivity)activity).getRestaurantList()!=null&&
                            ((AppBaseActivity)activity).getRestaurantList().size()!=0)
                        ((AppBaseActivity)activity).isCartAvailable();
                    setInRestaurant();
                }else {
                    if(((AppBaseActivity)activity).isTotalLayoutVisible())
                        ((AppBaseActivity)activity).setTotalLayoutGone();
                    setInOrder();
                }
            }
        });
        setInRestaurant();
        return rootView;
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
                case 0 : return new RestaurantFragment();
                case 1 : return new OrderDetailFragment();
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
                    return "FOOD DELIVERY";
                case 1 :
                    return "ITEMS DELIVERY";
            }
            return null;
        }

    }
    public void setInRestaurant(){
        ((AppBaseActivity)activity).setInOrderDetail(false);
        ((AppBaseActivity)activity).setInRestaurant(true);
        ((AppBaseActivity)activity).setInDropDetail(false);
        ((AppBaseActivity)activity).setInPickDetail(false);
        ((AppBaseActivity)activity).setInCompleteDetail(false);
        ((AppBaseActivity)activity).setInRestaurantMenu(false);
        ((AppBaseActivity)activity).setInOtherFragment(false);
        ((AppBaseActivity)activity).setInProcessOrder(false);
        ((AppBaseActivity)activity).setInPastOrder(false);

    }
    public void setInOrder(){
        ((AppBaseActivity)activity).setInOrderDetail(true);
        ((AppBaseActivity)activity).setInRestaurant(false);
        ((AppBaseActivity)activity).setInDropDetail(false);
        ((AppBaseActivity)activity).setInPickDetail(false);
        ((AppBaseActivity)activity).setInCompleteDetail(false);
        ((AppBaseActivity)activity).setInRestaurantMenu(false);
        ((AppBaseActivity)activity).setInOtherFragment(false);
        ((AppBaseActivity)activity).setInProcessOrder(false);
        ((AppBaseActivity)activity).setInPastOrder(false);

    }
//    public static int currentPage;
//    private static class PageListener extends ViewPager.SimpleOnPageChangeListener {
//        public void onPageSelected(int position) {
//            Log.d("VIEWPAGER", "page selected " + position);
//            currentPage = position;
//        }
//    }
//    /**
//     * Get the current view position from the ViewPager by
//     * extending SimpleOnPageChangeListener class and adding your method
//     */
//    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
//
//        private int currentPage;
//
//        @Override
//        public void onPageSelected(int position) {
//            currentPage = position;
//        }
//
//        public final int getCurrentPage() {
//            return currentPage;
//        }
//    }
}
