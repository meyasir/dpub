package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darewro.Activities.AppBaseActivity;
import com.darewro.R;
import com.darewro.Utilities.Helper;

public class DropDetailFragment extends Fragment {

    private TextView tvTitle;
    private EditText etName, etNumber, etAddress,etOrderDet;

    private int hintDefaultColor;
    private Button btnSubmit;

    String order_type,delivery_charges,delivery_time,delivery_type,delivery_type_detail;
    public DropDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_drop_detail, container, false);
        setInDrop();
//        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        //Retrieve the value
        order_type = getArguments().getString("order_type");
        delivery_charges = getArguments().getString("delivery_charges");
        delivery_time = getArguments().getString("delivery_time");
        delivery_type = getArguments().getString("delivery_type");
        delivery_type_detail = getArguments().getString("delivery_type_detail");
        tvTitle = (TextView)rootView.findViewById(R.id.textView2);
        etOrderDet = (EditText)rootView.findViewById(R.id.etOrderDet);
        etName = (EditText)rootView.findViewById(R.id.etName);
        etNumber = (EditText)rootView.findViewById(R.id.etNumber);
        etAddress = (EditText)rootView.findViewById(R.id.etAddress);
        btnSubmit = (Button)rootView.findViewById(R.id.btnSubmit);

        if(order_type.matches("0")) {
            tvTitle.setText("Pick Location");
        }
        else {
            tvTitle.setText("Drop Location");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick(v);
            }
        });

        setDefaultHintColor(etOrderDet);
        setDefaultHintColor(etName);
        setDefaultHintColor(etNumber);
        setDefaultHintColor(etAddress);
        // Inflate the layout for this fragment
        return rootView;
    }
    public void setInDrop(){
        ((AppBaseActivity)getActivity()).setInOrderDetail(false);
        ((AppBaseActivity)getActivity()).setInRestaurant(false);
        ((AppBaseActivity)getActivity()).setInDropDetail(true);
        ((AppBaseActivity)getActivity()).setInPickDetail(false);
        ((AppBaseActivity)getActivity()).setInCompleteDetail(false);
        ((AppBaseActivity)getActivity()).setInRestaurantMenu(false);
        ((AppBaseActivity)getActivity()).setInOtherFragment(false);
        ((AppBaseActivity)getActivity()).setInProcessOrder(false);
        ((AppBaseActivity)getActivity()).setInPastOrder(false);

    }
    public void onNextClick(View view) {
        boolean flag=isEditTextEmpty(etOrderDet);
        boolean flag1=isEditTextEmpty(etName);
        boolean flag2=isEditTextEmpty(etNumber);
        boolean flag3=isEditTextEmpty(etAddress);
        Helper helper = new Helper();
        if (flag||flag1||flag2||flag3){
            final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(true);

            TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
            String alert="";
            if(flag)
                alert+=getResources().getString(R.string.item_detail_alert);
            if(flag1)
                alert+="\n"+getResources().getString(R.string.name_alert);
            if(flag2)
                alert+="\n"+getResources().getString(R.string.contact_number_alert);
            if(flag3)
                alert+="\n"+getResources().getString(R.string.address_alert);

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
        else if(!helper.isValidMobile(etNumber)){
            // custom dialog
            final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(true);

            TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
            tvAlert.setText(getResources().getString(R.string.number_alert));
            Button btnOk = (Button)dialog.findViewById(R.id.btnOK);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        else {
            Bundle args = new Bundle();
            args.putString("order_detail", etOrderDet.getText().toString());
            args.putString("order_type", order_type);
            args.putString("delivery_charges", delivery_charges);
            args.putString("delivery_time", delivery_time);
            args.putString("delivery_type", delivery_type);
            args.putString("delivery_type_detail", delivery_type_detail);

            args.putString("name1", etName.getText().toString());
            args.putString("contact1", etNumber.getText().toString());
            args.putString("address1", etAddress.getText().toString());

            PickDetailFragment fragment = new PickDetailFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.flContent, fragment).addToBackStack(null).commit();

        }
    }

    public void setDefaultHintColor(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText.getCurrentHintTextColor()== Color.RED)
                    editText.setHintTextColor(hintDefaultColor);
            }
        });
    }
    public boolean isEditTextEmpty(EditText editText){
        String ed_text = editText.getText().toString().trim();
        if(ed_text.length() == 0 || ed_text.equals("") || ed_text == null)
        {
            if(editText.getCurrentHintTextColor()!=Color.RED) {
                hintDefaultColor = editText.getCurrentHintTextColor();
                editText.setHintTextColor(Color.RED);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

}
