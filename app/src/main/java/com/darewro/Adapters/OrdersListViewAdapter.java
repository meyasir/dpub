package com.darewro.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darewro.Models.Orders;
import com.darewro.Models.Restaurant;
import com.darewro.R;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jaffar on 2018-02-02.
 */

public class OrdersListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Orders> orders = null;
    private ArrayList<Orders> arraylist;
    private OnViewClick onViewClick = null;
    public OrdersListViewAdapter(Context context,
                                 List<Orders> orders, OnViewClick listener) {
        mContext = context;
        this.orders = orders;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Orders>();
        this.arraylist.addAll(orders);
        onViewClick = listener;
    }

    public class ViewHolder {
        TextView date;
        TextView price;
        TextView status;
        TextView tvPickLocation;
        TextView tvDropAddress;
        TextView reason;
        ImageView img;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.adapter_orders_item_list, null);
            // Locate the TextViews in listview_item.xml
            holder.img = (ImageView) view.findViewById(R.id.img);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.price = (TextView) view.findViewById(R.id.price);
            holder.tvPickLocation = (TextView) view.findViewById(R.id.tvPickLocation);
            holder.tvDropAddress = (TextView) view.findViewById(R.id.tvDropAddress);
            holder.status = (TextView) view.findViewById(R.id.status);
            holder.reason = (TextView) view.findViewById(R.id.reason);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(orders.get(position).getStatus().matches("Cancelled")){
            holder.img.setVisibility(View.VISIBLE);
        }
        else {
            holder.img.setVisibility(View.GONE);
        }
        String date[]  = orders.get(position).getDate().split(" ");
        String time = date[1];
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
        Date d = null;
        try {
            d = f1.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat f2 = new SimpleDateFormat("h:mma");
        time = f2.format(d).toLowerCase(); // "12:18am"
        date = date[0].split("-");
        String month="";
        if(date[1].matches("01"))
            month="Jan";
        else if(date[1].matches("02"))
            month="Feb";
        else if(date[1].matches("03"))
            month="Mar";
        else if(date[1].matches("04"))
            month="Apr";
        else if(date[1].matches("05"))
            month="May";
        else if(date[1].matches("06"))
            month="June";
        else if(date[1].matches("07"))
            month="July";
        else if(date[1].matches("08"))
            month="Aug";
        else if(date[1].matches("09"))
            month="Sep";
        else if(date[1].matches("10"))
            month="Oct";
        else if(date[1].matches("11"))
            month="Nov";
        else if(date[1].matches("12"))
            month="Dec";
        String day = date[2];

        holder.date.setText(day+" "+month+", "+time);
        holder.price.setText("PKR "+orders.get(position).getPrice());
        holder.tvDropAddress.setText(orders.get(position).getDropAddress());
        holder.tvPickLocation.setText(orders.get(position).getPickAddress());
        holder.status.setText(orders.get(position).getStatus());
        if(orders.get(position).getCancelReason()!=null
                &&orders.get(position).getCancelReason().length()!=0
                &&!orders.get(position).getCancelReason().matches("")) {
            holder.reason.setVisibility(View.VISIBLE);
            holder.reason.setText(orders.get(position).getCancelReason());
        }
        else {
            holder.reason.setVisibility(View.GONE);
        }
        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onViewClick.onViewClick(orders.get(position).getId(),orders.get(position).getDate(),
                        orders.get(position).getPrice(),orders.get(position).getStatus());

            }
        });

        return view;
    }

    public interface OnViewClick{
        public abstract void onViewClick(int id, String date, String price, String status);
    }
}
