package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.Models.DeliveryType;
import com.darewro.R;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetailFragment extends Fragment {

    ArrayList<DeliveryType> deliveryTypeArrayList;
    ArrayList<RadioButton> deliveryTypeRBArrayList;
    private Context context;
    private int rbDefaultColor;
    private Button btnBringMe, btnDropIt;
    private ImageView helpImageView;
    private WebService webService;
    private String bringMeOrDropIt;
    LinearLayout linearLayoutDT;
    Activity activity;
    LinearLayout linlaHeaderProgress;
    RelativeLayout relativeLayout;
    private ImageView refreshImageView;
    ProgressBar pbProgress;
    boolean deliveryTypesExist,deliveryTypesShow;
    public OrderDetailFragment() {
        // Required empty public constructor

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        deliveryTypesShow=false;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);

//        ((AppBaseActivity)activity).setLastPosition(-1);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        linlaHeaderProgress = (LinearLayout)rootView. findViewById(R.id.Progress);
        relativeLayout = (RelativeLayout)rootView.findViewById(R.id.relativeLayout);
        refreshImageView = (ImageView)rootView. findViewById(R.id.refreshImageView);
        pbProgress = (ProgressBar)rootView. findViewById(R.id.pbProgress);
        pbProgress.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
        linearLayoutDT = (LinearLayout)rootView.findViewById(R.id.linearLayoutDT);
        btnBringMe = (Button)rootView.findViewById(R.id.btnBringMe);
        btnDropIt= (Button)rootView.findViewById(R.id.btnDropIt);
        helpImageView = (ImageView)rootView.findViewById(R.id.helpImageView);

        webService = new WebService(activity);

        if(!deliveryTypesShow) {
            loadData();
        }
        else {
            relativeLayout.setVisibility(View.VISIBLE);
            setDeliveryTypes();
        }
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshClick(v);
            }
        });
        helpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageViewClick(v);
            }
        });
        btnBringMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick(v);
            }
        });
        btnDropIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick(v);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }


    private void loadData() {
        if (webService.isNetworkConnected()){
            if(((AppBaseActivity)activity).isSnackBarVisible()) {
                ((AppBaseActivity)activity).setSnackBarGone();
            }
            refreshImageView.setVisibility(View.GONE);
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            getDeliveryTypes();

        }
        else {
            refreshImageView.setVisibility(View.VISIBLE);
            ((AppBaseActivity)activity).setSnackBarVisible();
        }
    }
    public void getDeliveryTypes(){
        refreshImageView.setVisibility(View.GONE);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        webService.getDeliveryTypes(activity.getResources().getString(R.string.url) + "get_delivery_types", "0", new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    deliveryTypesExist = jsonObject.getBoolean("exist");
                    if(deliveryTypesExist){
                        deliveryTypeArrayList = new ArrayList<>();
                        JSONArray deliveryTypes = jsonObject.getJSONArray("delivery_types");
                        for (int i=0; i<deliveryTypes.length(); i++){
                            JSONObject dt = deliveryTypes.getJSONObject(i);
                            DeliveryType deliveryType = new DeliveryType();
                            deliveryType.setId(dt.getInt("delivery_id"));
                            deliveryType.setTitle(dt.getString("delivery_type_title"));
                            deliveryType.setDetail(dt.getString("delivery_type_detail"));
                            deliveryType.setExpectedCharges(dt.getString("expected_charges"));
                            deliveryType.setExpectedDeliveryTime(dt.getString("expected_delivery_time"));
                            deliveryTypeArrayList.add(deliveryType);
                        }
                        // HIDE THE SPINNER AFTER LOADING FEEDS
                        linlaHeaderProgress.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        // HIDE THE SPINNER AFTER LOADING FEEDS
                        refreshImageView.setVisibility(View.VISIBLE);
                        linlaHeaderProgress.setVisibility(View.GONE);
                        Toast.makeText(activity, "Error loading. Tap to retry", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    refreshImageView.setVisibility(View.VISIBLE);
                    linlaHeaderProgress.setVisibility(View.GONE);
                    Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // HIDE THE SPINNER AFTER LOADING FEEDS
                refreshImageView.setVisibility(View.VISIBLE);
                linlaHeaderProgress.setVisibility(View.GONE);
                Helper h = new Helper();
                h.volleyErrorMessage(activity,error);
            }
        });
    }

    public void setDeliveryTypes(){

        deliveryTypeRBArrayList = new ArrayList<>();
        for(DeliveryType deliveryType:deliveryTypeArrayList){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            RadioButton radioButton = new RadioButton(activity);
            radioButton.setId(deliveryType.getId());
            radioButton.setText(deliveryType.getTitle());
            radioButton.setTextAppearance(activity,android.R.style.TextAppearance_Medium);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeliveryTypeRadioButtonClicked(v);
                }
            });
            radioButton.setLayoutParams(params);
            deliveryTypeRBArrayList.add(radioButton);

            linearLayoutDT.addView(radioButton);
            TextView textView = new TextView(activity);
            textView.setText(deliveryType.getDetail());
            textView.setTextAppearance(activity,android.R.style.TextAppearance_Small);
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels_5 = (int) (5*scale + 0.5f);
            int dpAsPixels_16 = (int) (16*scale + 0.5f);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(dpAsPixels_16,0,dpAsPixels_16,dpAsPixels_5);
            linearLayoutDT.addView(textView);
        }
//        if(deliveryTypeRBArrayList.size()==1){
            RadioButton radioButton = deliveryTypeRBArrayList.get(1);
            radioButton.setChecked(true);
//        }
//        linearLayoutDT.setVisibility(View.VISIBLE);

    }

    public void onNextClick(View view) {
//        if(deliveryTypesExist&&!deliveryTypesShow){
        setDeliveryTypes();
//        }
        deliveryTypesShow = true;
        boolean check = false;
        if(deliveryTypesExist&&deliveryTypesShow){
            for(RadioButton radioButton:deliveryTypeRBArrayList){
                if(radioButton.isChecked()){
                    check = true;
                    break;
                }
            }
        }
        if(deliveryTypesShow&&!check){
            for (RadioButton radioButton:deliveryTypeRBArrayList){
                if(radioButton.getCurrentTextColor()!=Color.RED) {
                    rbDefaultColor = radioButton.getCurrentTextColor();
                    radioButton.setTextColor(Color.RED);
                }
            }
            final Dialog dialog = new Dialog(activity,R.style.MyAlertDialogStyle);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(true);

            TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
            String alert="Select delivery type";
            tvAlert.setText(alert);
            Button btnOk = (Button)dialog.findViewById(R.id.btnOK);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        switch(view.getId()){
            case R.id.btnBringMe:
                bringMeOrDropIt = "0";
                break;
            case R.id.btnDropIt:
                bringMeOrDropIt = "1";
                break;
        }
        DropDetailFragment fragment = new DropDetailFragment();
        Bundle args = new Bundle();
        if(deliveryTypesExist&&deliveryTypesShow) {
            for (int i=0; i<deliveryTypeRBArrayList.size();i++) {
                RadioButton radioButton=deliveryTypeRBArrayList.get(i);
                if (radioButton.isChecked()) {
                    args.putString("delivery_charges", deliveryTypeArrayList.get(i).getExpectedCharges());
                    args.putString("delivery_time", deliveryTypeArrayList.get(i).getExpectedDeliveryTime());
                    args.putString("delivery_type", deliveryTypeArrayList.get(i).getTitle());
                    args.putString("delivery_type_detail", deliveryTypeArrayList.get(i).getDetail());
                    break;
                }
            }
        }
        args.putString("order_type", bringMeOrDropIt);

        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    public void onDeliveryTypeRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        for(RadioButton radioButton:deliveryTypeRBArrayList){
            if(radioButton.getCurrentTextColor()==Color.RED) {
                radioButton.setTextColor(rbDefaultColor);
            }
            if(radioButton.getId()!=view.getId()){
                radioButton.setChecked(false);
            }
        }
    }
    public void onRefreshClick(View view) {
        loadData();
    }

    public void onImageViewClick(View view) {
        switch(view.getId()) {
            case R.id.helpImageView:
                // custom dialog
                final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
                dialog.setContentView(R.layout.dialog_help);
                dialog.setCancelable(true);

                Button btnOk = (Button)dialog.findViewById(R.id.btnOK);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }
}
