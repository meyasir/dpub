package com.darewro.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.darewro.R;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(List<TimeLineModel> feedList, Orientation orientation, boolean withLinePadding) {
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_timeline, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, int position) {

        TimeLineModel timeLineModel = mFeedList.get(position);
        holder.mDate.setVisibility(View.VISIBLE);
        holder.mDate.setText(timeLineModel.getDate());
        holder.mMessage.setText(timeLineModel.getMessage());
        holder.image.setImageResource(timeLineModel.getImage());
        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, R.color.colorGray));
            holder.mMessage.setTextColor(Color.parseColor("#d8d8d8"));
            holder.mDate.setTextColor(Color.parseColor("#d8d8d8"));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorActive));
            holder.mMessage.setTextColor(Color.parseColor("#4F4F4E"));
            holder.mDate.setTextColor(Color.parseColor("#4F4F4E"));
            holder.mDate.setText(timeLineModel.getDate());
            if(timeLineModel.getMessage().matches("DISPATCHED")){
                final String message = timeLineModel.getDate().toString();
                SpannableString s = new SpannableString(message);
                s.setSpan(new ForegroundColorSpan(Color.parseColor("#225996")), message.length()-11, message.length(), 0);
                s.setSpan(new ForegroundColorSpan(Color.parseColor("#4F4F4E")), 0, message.length()-11, 0);
                holder.mDate.setText(s);
                holder.mDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String cellNo = message.substring(message.length()-11,message.length());
                        final Dialog dialog = new Dialog(mContext,R.style.MyAlertDialogStyle);
                        dialog.setContentView(R.layout.dialog_call);
                        dialog.setCancelable(true);

                        TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
                        String alert="Call to Rider " + cellNo;
                        tvAlert.setText(alert);
                        Button btnOk = (Button)dialog.findViewById(R.id.btnOK);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+cellNo));
                                mContext.startActivity(intent);
                            }
                        });
                        Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            }
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.orderstatus));
        }
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

}
