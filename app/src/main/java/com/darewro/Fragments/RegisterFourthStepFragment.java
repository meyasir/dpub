package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.Models.User;
import com.darewro.R;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.MySharedPreferences;
import com.darewro.Utilities.WebService;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

/**
 * Created by Jaffar on 2018-01-09.
 */

public class RegisterFourthStepFragment extends Fragment {

    MySharedPreferences mySharedPreferences;
    Activity activity;
    private EditText etName;
    private EditText etEmail;
    private Button btnContinue;
    private int hintDefaultColor;
    String mobileNumber,PINString;
    WebService webService;
    LinearLayout linlaHeaderProgress;
    ProgressBar pbProgress;
    private LinearLayout snackbar;
    Runnable mRunnable;
    Handler mHandler;
    public RegisterFourthStepFragment() {
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

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register_fourth_step, container, false);
        etName = (EditText)rootView.findViewById(R.id.etName);
        etEmail = (EditText)rootView.findViewById(R.id.etEmail);
        setDefaultHintColor(etEmail);
        setDefaultHintColor(etName);
        btnContinue = (Button) rootView.findViewById(R.id.btnContinue);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.Progress);
        pbProgress = (ProgressBar) rootView.findViewById(R.id.pbProgress);
        snackbar = (LinearLayout)rootView.findViewById(R.id.snackbar);
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

        mobileNumber = getArguments().getString(MySharedPreferences.USER_MOBILE_NUMBER);
        PINString = getArguments().getString(MySharedPreferences.PIN_CODE);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                etName.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        etName.requestFocus();
        webService = new WebService(activity);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper helper = new Helper();
                boolean flag1=isEditTextEmpty(etName);
                boolean flag2=isEditTextEmpty(etEmail);
                if (flag1||flag2){
                    final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
                    String alert="";
                    if(flag1)
                        alert+="\n"+getResources().getString(R.string.name_alert);
                    if(flag2)
                        alert+="\n"+getResources().getString(R.string.email_alert);
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
                else if(!helper.isValidMail(etEmail.getText().toString())){
                    final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
                    String alert="";
                    alert+="\n"+getResources().getString(R.string.not_valid_emal);
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
                else {
                    onContinueClick(v);
                }
            }
        });
        // Inflate the layout for this fragment
        return rootView;
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
    public void onContinueClick(View view) {
        if(webService.isNetworkConnected()) {
            if(snackbar.getVisibility()==View.VISIBLE) {
                snackbar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mRunnable);
            }
            registerUser();
        }
        else {
            snackbar.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable, 4 * 1000);
        }
    }
    public void registerUser(){
        btnContinue.setEnabled(false);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        User user = new User();
        user.setTitle(etName.getText().toString());
        user.setEmail(etEmail.getText().toString());
        String phone = "0"+mobileNumber.replace(" ", "");
        user.setNumber(phone);
        user.setTokenKey(FirebaseInstanceId.getInstance().getToken());
        webService.userRegistration(getString(R.string.url) + "user_registration", user,PINString, new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean success = json.getBoolean("success");
                    if (success) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);
                        mySharedPreferences = new MySharedPreferences();
                        mySharedPreferences.getPrefs(activity);
                        mySharedPreferences.setUserID(activity,json.getInt(MySharedPreferences.USER_ID));
                        mySharedPreferences.setRoleID(activity,2);
                        mySharedPreferences.setUserMobileNumber(activity,"+92"+mobileNumber.replace(" ", ""));
                        mySharedPreferences.setUserTitle(activity,etName.getText().toString());
                        mySharedPreferences.setUserEmail(activity,etEmail.getText().toString());
                        mySharedPreferences.setRegister(activity,true);
                        linlaHeaderProgress.setVisibility(View.GONE);
                        String message = json.getString("message");
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(activity, AppBaseActivity.class);
                        i.putExtra("Uniqid","From_Register");
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                    } else {
                        btnContinue.setEnabled(true);
                        linlaHeaderProgress.setVisibility(View.GONE);
                        String message = json.getString("message");
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    btnContinue.setEnabled(true);
                    linlaHeaderProgress.setVisibility(View.GONE);
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onError(VolleyError error) {
                btnContinue.setEnabled(true);
                linlaHeaderProgress.setVisibility(View.GONE);
                Helper h = new Helper();
                h.volleyErrorMessage(activity,error);
            }
        });
    }

    public void snackbarClick(){
        snackbar.setVisibility(View.GONE);
        mHandler.removeCallbacks(mRunnable);
    }
}

