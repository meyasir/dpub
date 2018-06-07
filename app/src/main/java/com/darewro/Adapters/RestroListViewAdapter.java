package com.darewro.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darewro.Models.Restaurant;
import com.darewro.R;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jaffar on 8/23/2017.
 */
public class RestroListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Restaurant> restaurants = null;
    private ArrayList<Restaurant> arraylist;
    private OnViewClick onViewClick = null;
    public RestroListViewAdapter(Context context,
                                 List<Restaurant> restaurants, OnViewClick listener) {
        mContext = context;
        this.restaurants = restaurants;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Restaurant>();
        this.arraylist.addAll(restaurants);
        onViewClick = listener;
    }

    public class ViewHolder {
        TextView name;
        TextView desc;
        ImageView image,timeImageView;
        CardView cardView;
        TextView close,timing;
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurants.get(position);    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.adapter_restro_list, null);
            // Locate the TextViews in listview_item.xml
            holder.cardView = (CardView) view.findViewById(R.id.card_view);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.close = (TextView) view.findViewById(R.id.close);
            holder.timing = (TextView) view.findViewById(R.id.timing);
            holder.desc = (TextView) view.findViewById(R.id.desc);
            // Locate the ImageView in listview_item.xml
            holder.image = (ImageView) view.findViewById(R.id.imageView1);
            holder.timeImageView = (ImageView) view.findViewById(R.id.timeImageView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(!restaurants.get(position).isOpen()){
            holder.cardView.setClickable(false);
            holder.close.setVisibility(View.VISIBLE);
            holder.timing.setVisibility(View.VISIBLE);
            holder.timeImageView.setVisibility(View.VISIBLE);
            String time ="";
            DateFormat f1 = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
            Date d = null;
            try {
                d = f1.parse(restaurants.get(position).getOpeningTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat f2 = new SimpleDateFormat("h:mma");
            time = f2.format(d).toLowerCase(); // "12:18am"
            d = null;
            try {
                d = f1.parse(restaurants.get(position).getClosingTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            time +=" to "+ f2.format(d).toLowerCase(); // "12:18am"
            holder.timing.setText(time);
            // Listen for ListView Item Click
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Toast.makeText(mContext, "Restaurant closed now", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            holder.cardView.setClickable(true);
            holder.close.setVisibility(View.GONE);
            holder.timing.setVisibility(View.GONE);
            holder.timeImageView.setVisibility(View.GONE);
            // Listen for ListView Item Click
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    byte[] byteArray;
                    if(restaurants.get(position).getImage()!=null) {
                        restaurants.get(position).getImage().compress(Bitmap.CompressFormat.PNG, 100, bStream);
                        byteArray = bStream.toByteArray();
                    }
                    else {
                        byteArray = null;
                    }
                    onViewClick.onViewClick(position, restaurants.get(position).getId(),restaurants.get(position).getName(),
                            restaurants.get(position).getDesc(),byteArray);

                }
            });
        }
        // Set the results into TextViews
        holder.name.setText(restaurants.get(position).getName());
        holder.desc.setText(restaurants.get(position).getDesc());
        // Set the results into ImageView
        if (holder.image != null) {
            holder.image.setVisibility(View.VISIBLE);
            if(restaurants.get(position).getImage()!=null)
                holder.image.setImageBitmap(restaurants.get(position).getImage());
            else
                holder.image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_restaurant_black_24dp));
        }

        return view;
    }

    public interface OnViewClick{
        public abstract void onViewClick(int position, int id, String name, String desc, byte[] bytes);
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        restaurants.clear();
        if (charText.length() == 0) {
            restaurants.addAll(arraylist);
        } else {
            for (Restaurant ic : arraylist) {
                if (ic.getName().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                    restaurants.add(ic);
                }
            }
        }
        notifyDataSetChanged();
    }
}
