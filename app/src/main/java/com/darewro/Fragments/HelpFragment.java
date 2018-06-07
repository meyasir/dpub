package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.R;

/**
 * Created by Jaffar on 2018-01-29.
 */

public class HelpFragment  extends Fragment {

    LinearLayout layoutCall;
    Activity activity;
    public HelpFragment() {
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
        setInOtherFragment();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        layoutCall = (LinearLayout)rootView.findViewById(R.id.layoutCall);
        layoutCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallCick(view);
            }
        });
        return rootView;
    }
    public void setInOtherFragment(){
        ((AppBaseActivity)activity).setInOrderDetail(false);
        ((AppBaseActivity)activity).setInRestaurant(false);
        ((AppBaseActivity)activity).setInDropDetail(false);
        ((AppBaseActivity)activity).setInPickDetail(false);
        ((AppBaseActivity)activity).setInCompleteDetail(false);
        ((AppBaseActivity)activity).setInRestaurantMenu(false);
        ((AppBaseActivity)activity).setInOtherFragment(true);
        ((AppBaseActivity)activity).setInProcessOrder(false);
        ((AppBaseActivity)activity).setInPastOrder(false);
    }

    public void onCallCick(View view) {
        final String cellNo = "03000341381";
        final Dialog dialog = new Dialog(activity,R.style.MyAlertDialogStyle);
        dialog.setContentView(R.layout.dialog_call);
        dialog.setCancelable(true);

        TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
        String alert="Call us now " + cellNo;
        tvAlert.setText(alert);
        Button btnOk = (Button)dialog.findViewById(R.id.btnOK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+cellNo));
                activity.startActivity(intent);
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
}
