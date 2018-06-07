package com.darewro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.darewro.Models.RestaurantFoodCategory;
import com.darewro.Models.RestaurantFoodMenu;
import com.darewro.R;
import com.darewro.Models.RestaurantFoodMenuAddExploreBtnTag;

import java.util.ArrayList;

/**
 * Created by Jaffar on 9/27/2017.
 */
public class RestaurantMenuCustomAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private ArrayList<RestaurantFoodCategory> foodCategories;
    private AddImageViewClick addImageViewClick = null;
    private ExploreImageViewClick exploreImageViewClick = null;
    public RestaurantMenuCustomAdapter(Context context, ArrayList<RestaurantFoodCategory> foodCategories
            , AddImageViewClick listener, ExploreImageViewClick listener1) {
        this._context = context;
        this.foodCategories = foodCategories;
        addImageViewClick = listener;
        exploreImageViewClick = listener1;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.foodCategories.get(groupPosition).getRestaurantFoodMenus().get(childPosition).getName();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        if(this.foodCategories.get(groupPosition).getRestaurantFoodMenus().get(childPosition).getRestaurantFoodSubMenus().size()>1){
            return 0;
        }
        else {
            long id = Long.valueOf(this.foodCategories.get(groupPosition).getRestaurantFoodMenus().get(childPosition).getRestaurantFoodSubMenus().get(0).getId());
            return id;
        }
    }
    public class ViewHolder {
        TextView name;
        TextView desc;
        TextView price,pricetitle;
        TextView weight;
        ImageView addImageView;
        ImageView exploreImageView;
    }
    public String getChildName(int groupPosition, int childPosition) {
        return this.foodCategories.get(groupPosition).getRestaurantFoodMenus().get(childPosition).getName();
    }
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final RestaurantFoodCategory restaurantFoodCategory = this.foodCategories.get(groupPosition);
        final RestaurantFoodMenu restaurantFoodMenu = restaurantFoodCategory.getRestaurantFoodMenus().get(childPosition);
//        Toast.makeText(this._context, restaurantFoodMenu.getName(), Toast.LENGTH_SHORT).show();
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.adapter_food_item_list, null);

            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.weight = (TextView) convertView.findViewById(R.id.weight);
            holder.desc = (TextView) convertView.findViewById(R.id.desc);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.pricetitle = (TextView) convertView.findViewById(R.id.pricetitle);
            holder.addImageView = (ImageView) convertView.findViewById(R.id.addImageView);
            holder.exploreImageView = (ImageView) convertView.findViewById(R.id.exploreImageView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set the results into TextViews
        holder.name.setText(restaurantFoodMenu.getName());
        if(restaurantFoodMenu.getDesc().length() == 0 || restaurantFoodMenu.getDesc().equals("")
                || restaurantFoodMenu.getDesc() == null || restaurantFoodMenu.getDesc().matches("")){
            holder.desc.setVisibility(View.GONE);
        }
        else{
            holder.desc.setVisibility(View.VISIBLE);
            holder.desc.setText(restaurantFoodMenu.getDesc());
        }
        if(restaurantFoodMenu.getRestaurantFoodSubMenus().size()==1){
            if(restaurantFoodMenu.getRestaurantFoodSubMenus().get(0).getWeight().length() == 0 || restaurantFoodMenu.getRestaurantFoodSubMenus().get(0).getWeight().equals("")
                    || restaurantFoodMenu.getRestaurantFoodSubMenus().get(0).getWeight() == null || restaurantFoodMenu.getRestaurantFoodSubMenus().get(0).getWeight().matches("")){
                holder.weight.setVisibility(View.GONE);
            }
            else{
                holder.weight.setVisibility(View.VISIBLE);
                holder.weight.setText(restaurantFoodMenu.getRestaurantFoodSubMenus().get(0).getWeight());
            }
            holder.price.setText(String.valueOf(restaurantFoodMenu.getRestaurantFoodSubMenus().get(0).getPrice()));

            RestaurantFoodMenuAddExploreBtnTag tag = new RestaurantFoodMenuAddExploreBtnTag(groupPosition,childPosition);
            holder.addImageView.setTag(tag); //For passing the list item index

            holder.addImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(addImageViewClick != null){
                        addImageViewClick.onAddImageViewClick((RestaurantFoodMenuAddExploreBtnTag)holder.addImageView.getTag(),holder.addImageView);
                    }
                }
            });
            holder.addImageView.setVisibility(View.VISIBLE);
            holder.exploreImageView.setVisibility(View.GONE);
            holder.price.setVisibility(View.VISIBLE);
            holder.pricetitle.setVisibility(View.VISIBLE);
        }
        else {
            holder.addImageView.setVisibility(View.GONE);
            holder.exploreImageView.setVisibility(View.VISIBLE);
            holder.price.setVisibility(View.GONE);
            holder.pricetitle.setVisibility(View.GONE);
            holder.weight.setVisibility(View.GONE);
            RestaurantFoodMenuAddExploreBtnTag tag = new RestaurantFoodMenuAddExploreBtnTag(groupPosition,childPosition);
            holder.exploreImageView.setTag(tag); //For passing the list item index

            holder.exploreImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(exploreImageViewClick != null){
                        exploreImageViewClick.onExploreImageViewClick((RestaurantFoodMenuAddExploreBtnTag)holder.exploreImageView.getTag());
                    }
                }
            });
        }

        return convertView;
    }
    public interface AddImageViewClick{
        public abstract void onAddImageViewClick(RestaurantFoodMenuAddExploreBtnTag tag,ImageView imageView);
    }
    public interface ExploreImageViewClick{
        public abstract void onExploreImageViewClick(RestaurantFoodMenuAddExploreBtnTag tag);
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        int size=0;
        if(this.foodCategories.get(groupPosition).getRestaurantFoodMenus()!=null)
            size = this.foodCategories.get(groupPosition).getRestaurantFoodMenus().size();
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.foodCategories.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.foodCategories.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        final RestaurantFoodCategory menuCategory = this.foodCategories.get(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.adapter_food_category, null);
        }

        // Get  menu_category_row.xml file element and set value
        ((TextView) convertView.findViewById(R.id.tvfc)).setText(menuCategory.getCategory());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    @Override
    public void notifyDataSetChanged()
    {
        // Refresh List rows
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty()
    {
        return ((this.foodCategories == null) || this.foodCategories.isEmpty());
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }
}

