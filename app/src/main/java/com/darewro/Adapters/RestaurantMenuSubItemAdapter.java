package com.darewro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.darewro.Models.RestaurantFoodSubMenu;
import com.darewro.Models.RestaurantFoodSubMenuAddTag;
import com.darewro.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaffar on 2018-01-12.
 */

public class RestaurantMenuSubItemAdapter extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<RestaurantFoodSubMenu> restaurantFoodSubMenus = null;
    private ArrayList<RestaurantFoodSubMenu> arraylist;
    private AddImageViewClick addImageViewClick = null;

    private String foodName;

    public RestaurantMenuSubItemAdapter(Context context, String foodName , List<RestaurantFoodSubMenu> restaurantFoodSubMenus, AddImageViewClick listener ){
        mContext = context;
        this.restaurantFoodSubMenus = restaurantFoodSubMenus;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<RestaurantFoodSubMenu>();
        this.arraylist.addAll(restaurantFoodSubMenus);
        addImageViewClick = listener;
        this.foodName = foodName;
    }

    public class ViewHolder {
        TextView weight;
        TextView price;
        ImageView addImageView;
    }
    @Override
    public int getCount() {
        return restaurantFoodSubMenus.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurantFoodSubMenus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.adapter_sub_food_items, null);
            // Locate the TextViews in listview_item.xml
            holder.weight = (TextView) view.findViewById(R.id.weight);
            holder.price = (TextView) view.findViewById(R.id.price);
            holder.addImageView = (ImageView) view.findViewById(R.id.addImageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RestaurantFoodSubMenuAddTag tag = new RestaurantFoodSubMenuAddTag(foodName,position);
        // Set the results into TextViews
        holder.weight.setText(restaurantFoodSubMenus.get(position).getWeight());
        holder.price.setText(String.valueOf(restaurantFoodSubMenus.get(position).getPrice()));
        holder.addImageView.setTag(tag);
        holder.addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addImageViewClick != null) {
                    addImageViewClick.onAddImageViewClick((RestaurantFoodSubMenuAddTag) v.getTag());
                }
            }
        });

        return view;
    }

    public interface AddImageViewClick {
        public abstract void onAddImageViewClick(RestaurantFoodSubMenuAddTag tag);
    }

}
