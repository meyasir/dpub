package com.darewro.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.Models.User;
import com.darewro.Models.GeneralOrder;
import com.darewro.R;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.MySharedPreferences;
import com.darewro.Utilities.WebService;

import org.json.JSONObject;


public class CompleteOrderFragment extends Fragment {

    private TextView etOrderDet, etPName, etPNumber, etPAddress, etDName, etDNumber, etDAddress;//tvDeliveryTypeDetail;
    private RadioButton rbBD;//rbDT;
    private Button btnOrder;
    private TextView pickOrDrop;
    LinearLayout linlaHeaderProgress;
    ProgressBar pbProgress;

    ImageView editImageView;
    Dialog dialogEdit;
    private WebService webService;
    String order_detail,order_type,name,contact,address,name1,contact1,address1,delivery_charges,delivery_time,delivery_type,delivery_type_detail;
    private LinearLayout snackbar;
    Runnable mRunnable;
    Handler mHandler;
    public CompleteOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_complete_order, container, false);
        setInComplete();

//        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        rbBD = (RadioButton) rootView.findViewById(R.id.rbBD);
//        rbDT = (RadioButton) rootView.findViewById(R.id.rbDT);
        etOrderDet = (TextView)rootView.findViewById(R.id.etOrderDet);
        etPName = (TextView)rootView.findViewById(R.id.etPName);
        etPNumber = (TextView)rootView.findViewById(R.id.etPNumber);
        etPAddress = (TextView)rootView.findViewById(R.id.etPAddress);
        etDName = (TextView)rootView.findViewById(R.id.etDName);
        etDNumber = (TextView)rootView.findViewById(R.id.etDNumber);
        etDAddress = (TextView) rootView.findViewById(R.id.etDAddress);
        editImageView = (ImageView) rootView.findViewById(R.id.editImageView);
        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditClick(view);
            }
        });
//        tvDeliveryTypeDetail = (TextView) rootView.findViewById(R.id.tvDeliveryTypeDetail);
        btnOrder = (Button)rootView.findViewById(R.id.btnOrder);
        pickOrDrop = (TextView)rootView.findViewById(R.id.textView1);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.Progress);
        pbProgress = (ProgressBar) rootView.findViewById(R.id.pbProgress);
        snackbar = (LinearLayout)rootView.findViewById(R.id.snackbar);
        pbProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        mHandler=new Handler();
        mRunnable=new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                snackbar.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
            }
        };
        snackbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbarClick();
            }
        });

        order_detail = getArguments().getString("order_detail");
        order_type = getArguments().getString("order_type");
        delivery_charges = getArguments().getString("delivery_charges");
        delivery_time = getArguments().getString("delivery_time");
        delivery_type = getArguments().getString("delivery_type");
        delivery_type_detail = getArguments().getString("delivery_type_detail");
        name = getArguments().getString("name");
        contact = getArguments().getString("contact");
        address = getArguments().getString("address");
        name1 = getArguments().getString("name1");
        contact1 = getArguments().getString("contact1");
        address1 = getArguments().getString("address1");
        etOrderDet.setText(order_detail);
        etPName.setText(name);
        etPNumber.setText(contact);
        etPAddress.setText(address);
        etDName.setText(name1);
        etDNumber.setText(contact1);
        etDAddress.setText(address1);
//        rbDT.setText(delivery_type);
//        tvDeliveryTypeDetail.setText(delivery_type_detail);
        webService = new WebService(getActivity());
        if(order_type.matches("0")) {
            rbBD.setText("Bring Items");
            pickOrDrop.setText("Pick Location");
        }
        else {
            rbBD.setText("Drop Items");
            pickOrDrop.setText("Drop Location");
        }

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProceedClick(v);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
    public void setInComplete(){
        ((AppBaseActivity)getActivity()).setInOrderDetail(false);
        ((AppBaseActivity)getActivity()).setInRestaurant(false);
        ((AppBaseActivity)getActivity()).setInDropDetail(false);
        ((AppBaseActivity)getActivity()).setInPickDetail(false);
        ((AppBaseActivity)getActivity()).setInCompleteDetail(true);
        ((AppBaseActivity)getActivity()).setInRestaurantMenu(false);
        ((AppBaseActivity)getActivity()).setInOtherFragment(false);
        ((AppBaseActivity)getActivity()).setInProcessOrder(false);
        ((AppBaseActivity)getActivity()).setInPastOrder(false);
    }
    public void snackbarClick(){
        snackbar.setVisibility(View.GONE);
        mHandler.removeCallbacks(mRunnable);
    }
    public void onProceedClick(View view) {
        if(webService.isNetworkConnected()) {
            if(snackbar.getVisibility()==View.VISIBLE) {
                snackbar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mRunnable);
            }
            proceedOrder();
        }
        else {
            snackbar.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable, 4 * 1000);
        }
    }

    public void proceedOrder(){
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        btnOrder.setEnabled(false);
        User user1 = new User(name,contact,address);
        User user2 = new User(name1,contact1,address1);
        GeneralOrder generalOrder = new GeneralOrder(order_detail,order_type,delivery_charges,delivery_time);
        MySharedPreferences mySharedPreferences = new MySharedPreferences();
        webService.proceedGeneralOrder(getString(R.string.url) + "save_and_process", user1, user2, generalOrder, String.valueOf(mySharedPreferences.getUserID(getActivity())),new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean success = json.getBoolean("success");
                    if (success) {
                        linlaHeaderProgress.setVisibility(View.GONE);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }
                        Intent i = new Intent(getActivity(), AppBaseActivity.class);
                        i.putExtra("Uniqid","From_Order");
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        btnOrder.setEnabled(true);
                        linlaHeaderProgress.setVisibility(View.GONE);
                        String message = json.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    btnOrder.setEnabled(true);
                    linlaHeaderProgress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onError(VolleyError error) {
                btnOrder.setEnabled(true);
                linlaHeaderProgress.setVisibility(View.GONE);
                Helper h = new Helper();
                h.volleyErrorMessage(getActivity(),error);
            }
        });
    }

    public void onEditClick(View v){
        dialogEdit = new Dialog(getActivity(),R.style.SuperMaterialTheme);
        dialogEdit.setContentView(R.layout.dialog_edit_order_detail);
        dialogEdit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogEdit.setCancelable(true);
        final EditText metItemDetails = (EditText)dialogEdit.findViewById(R.id.etItemDetails);
        final EditText metName = (EditText)dialogEdit.findViewById(R.id.etName);
        final EditText metNumber = (EditText)dialogEdit.findViewById(R.id.etNumber);
        final EditText metAddress = (EditText)dialogEdit.findViewById(R.id.etAddress);
        final EditText metPName = (EditText)dialogEdit.findViewById(R.id.etPName);
        final EditText metPNumber = (EditText)dialogEdit.findViewById(R.id.etPNumber);
        final EditText metPAddress = (EditText)dialogEdit.findViewById(R.id.etPAddress);
        final TextView textView3 = (TextView)dialogEdit.findViewById(R.id.textView3);
        textView3.setText(pickOrDrop.getText().toString());
        metItemDetails.setText(etOrderDet.getText());
        metName.setText(etPName.getText());
        metNumber.setText(etPNumber.getText());
        metAddress.setText(etPAddress.getText());
        metPName.setText(etDName.getText());
        metPNumber.setText(etDNumber.getText());
        metPAddress.setText(etDAddress.getText());

        Button btnSave = (Button)dialogEdit.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag=isEditTextEmpty(metItemDetails);
                boolean flag1=isEditTextEmpty(metName);
                boolean flag2=isEditTextEmpty(metNumber);
                boolean flag3=isEditTextEmpty(metAddress);
                boolean flag4=isEditTextEmpty(metPName);
                boolean flag5=isEditTextEmpty(metPNumber);
                boolean flag6=isEditTextEmpty(metPAddress);
                Helper helper = new Helper();
                if (flag||flag1||flag2||flag3||flag4||flag5||flag6){
                    final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
                    String alert="Required fields missing";

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
                else if(!helper.isValidMobile(metNumber)){
                    // custom dialog
                    final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
                    tvAlert.setText("Your number is not valid");
                    Button btnOk = (Button)dialog.findViewById(R.id.btnOK);
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else if(!helper.isValidMobile(metPNumber)){
                    // custom dialog
                    final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
                    tvAlert.setText("Person number is not valid");
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
                    etOrderDet.setText(metItemDetails.getText().toString());
                    etPName.setText(metName.getText().toString());
                    etPNumber.setText(metNumber.getText().toString());
                    etPAddress.setText(metAddress.getText().toString());
                    etDName.setText(metPName.getText().toString());
                    etDNumber.setText(metPNumber.getText().toString());
                    etDAddress.setText(metPAddress.getText().toString());
                    hideKeyboard(metItemDetails);
                    hideKeyboard(metName);
                    hideKeyboard(metNumber);
                    hideKeyboard(metAddress);
                    hideKeyboard(metPName);
                    hideKeyboard(metPNumber);
                    hideKeyboard(metPAddress);
                    dialogEdit.dismiss();
                }
            }
        });
        dialogEdit.show();
    }
    private void hideKeyboard(View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private int hintDefaultColor;

    public boolean isEditTextEmpty(EditText editText){
        String ed_text = editText.getText().toString().trim();
        if(ed_text.length() == 0 || ed_text.equals("") || ed_text == null)
        {
            if(editText.getCurrentHintTextColor()!= Color.RED) {
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
