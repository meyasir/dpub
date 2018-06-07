package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Spanned;
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
import com.darewro.R;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.MySharedPreferences;
import com.darewro.Utilities.WebService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jaffar on 2018-02-07.
 */

public class PasswordRecoverFragment extends Fragment {

    Activity activity;
    private EditText etEmail;
    private Button btnContinue;
    String PINCode;
    LinearLayout linlaHeaderProgress;
    ProgressBar pbProgress;
    private LinearLayout snackbar;
    Runnable mRunnable;
    Handler mHandler;

    TextView tvHint;
    String mobileNumber,userEmail;
    WebService webService;

    public PasswordRecoverFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        webService = new WebService(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_password_recover, container, false);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        btnContinue = (Button) rootView.findViewById(R.id.btnContinue);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.Progress);
        pbProgress = (ProgressBar) rootView.findViewById(R.id.pbProgress);
        snackbar = (LinearLayout) rootView.findViewById(R.id.snackbar);
        tvHint = (TextView) rootView.findViewById(R.id.tvHint);

        mobileNumber = getArguments().getString(MySharedPreferences.USER_MOBILE_NUMBER);
        userEmail = getArguments().getString(MySharedPreferences.USER_EMAIL);
        tvHint.setText(tvHint.getText().toString()+"\n+92 "+mobileNumber+"\n to reset your password");
        String before="",mid="";
        String data[] = userEmail.split("@");
        for(int i=0;i<data[0].length();i++){
            if(i!=0){
                before+="*";
            }
            else {
                before+=data[0].charAt(i);
            }
        }
        data = data[1].split("\\.");

        for(int i=0;i<data[0].length();i++){
            if(i!=0){
                mid+="*";
            }
            else {
                mid+=data[0].charAt(i);
            }
        }
        etEmail.setHint(before+"@"+mid+"."+data[1]);
        mHandler = new Handler();
        mRunnable = new Runnable() {

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


        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etEmail, InputMethodManager.SHOW_IMPLICIT);
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                etEmail.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(etEmail, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        etEmail.requestFocus();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper helper = new Helper();
                if (helper.isValidMail(etEmail.getText().toString())) {
                    onContinueClick(v);
                } else {
                    // custom dialog
                    final Dialog dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView) dialog.findViewById(R.id.tvAlert);
                    tvAlert.setText(getResources().getString(R.string.not_valid_emal));
                    Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    public void onContinueClick(View view) {
        if (webService.isNetworkConnected()) {
            if (snackbar.getVisibility() == View.VISIBLE) {
                snackbar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mRunnable);
            }
            chechUserEmailAndSendPin();
        } else {
            snackbar.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable, 4 * 1000);
        }
    }

    public void chechUserEmailAndSendPin() {
        btnContinue.setEnabled(false);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        PINCode = generatePIN();
        String phone = "0"+mobileNumber.replace(" ", "");
        webService.checkUserEmailAndSendPin(activity.getResources().getString(R.string.url) + "check_user_email", phone,etEmail.getText().toString(), PINCode,new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean exist = json.getBoolean("exist");
                    if (exist) {
                        linlaHeaderProgress.setVisibility(View.GONE);
                        String message = json.getString("message");
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();

                    } else {
                        btnContinue.setEnabled(true);
                        linlaHeaderProgress.setVisibility(View.GONE);
                        String message = json.getString("message");
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    btnContinue.setEnabled(true);
                    linlaHeaderProgress.setVisibility(View.GONE);
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public String generatePIN() {
        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int) (Math.random() * 9000) + 1000;

        //Store integer in a string
        PINCode = String.valueOf(randomPIN);

        return PINCode;

    }

    public void snackbarClick() {
        snackbar.setVisibility(View.GONE);
        mHandler.removeCallbacks(mRunnable);
    }
}