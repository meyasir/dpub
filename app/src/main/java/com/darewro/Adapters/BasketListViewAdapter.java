package com.darewro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.darewro.Models.FoodItemCart;
import com.darewro.Models.Restaurant;
import com.darewro.R;
import com.darewro.Utilities.Database;

import java.util.List;

/**
 * Created by Jaffar on 8/31/2017.
 */
public class BasketListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<FoodItemCart> foodItemCarts = null;
    private RemoveImageViewClick removeImageViewClick = null;

    public BasketListViewAdapter(Context context, List<FoodItemCart> foodItemCarts , RemoveImageViewClick listener) {
        mContext = context;
        this.foodItemCarts = foodItemCarts;
        inflater = LayoutInflater.from(mContext);
        removeImageViewClick = listener;
    }

    public class ViewHolder {
        TextView quantity;
        TextView name;
        TextView weight;
        TextView price;
        ImageView removeImageView;
        ImageView incBtn;
        ImageView decBtn;
        TextView resName;
    }

    @Override
    public int getCount() {
        return foodItemCarts.size();
    }

    @Override
    public Object getItem(int position) {
        return foodItemCarts.get(position);
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
            view = inflater.inflate(R.layout.basket_list_items, null);
            // Locate the TextViews in listview_item.xml
            holder.quantity = (TextView) view.findViewById(R.id.quantity);
            holder.weight = (TextView) view.findViewById(R.id.weight);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.price = (TextView) view.findViewById(R.id.price);
            holder.removeImageView = (ImageView) view.findViewById(R.id.removeImageView);
            holder.incBtn = (ImageView) view.findViewById(R.id.incBtn);
            holder.decBtn = (ImageView) view.findViewById(R.id.decBtn);
            holder.resName = (TextView) view.findViewById(R.id.resName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(foodItemCarts.get(position).getName());
        if(foodItemCarts.get(position).getWeight()!=null&&!foodItemCarts.get(position).getWeight().matches(""))
            holder.weight.setText(foodItemCarts.get(position).getWeight());
        else
            holder.weight.setVisibility(View.GONE);
        holder.quantity.setText(String.valueOf(foodItemCarts.get(position).getQuantity()));
        int totalPrice = foodItemCarts.get(position).getPrice() * foodItemCarts.get(position).getQuantity();
        holder.price.setText(String.valueOf(totalPrice));

        holder.removeImageView.setTag(position); //For passing the list item index
        holder.incBtn.setTag(position);
        holder.decBtn.setTag(position);

        holder.removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeImageViewClick != null) {
                    removeImageViewClick.onRemoveImageViewClick((Integer) v.getTag());
                }
            }
        });
        holder.incBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeImageViewClick != null) {
                    removeImageViewClick.onIncBtnClick((Integer) v.getTag());
                }
            }
        });
        holder.decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeImageViewClick != null) {
                    removeImageViewClick.onDecBtnClick((Integer) v.getTag());
                }
            }
        });
        Database mydb = new Database(mContext);
        Restaurant restaurant = mydb.getRestaurantCart(foodItemCarts.get(position).getRestaurantID());

        holder.resName.setText(restaurant.getName());
        return view;
    }

    public interface RemoveImageViewClick {
        public abstract void onRemoveImageViewClick(int position);
        public abstract void onIncBtnClick(int position);
        public abstract void onDecBtnClick(int position);
    }

}

