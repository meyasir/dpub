package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
 * Created by Jaffar on 2018-01-09.
 */

public class RegisterSecondStepFragment extends Fragment {

    Activity activity;
    private EditText etNumber;
    private Button btnContinue;
    String PINCode;
    LinearLayout linlaHeaderProgress;
    ProgressBar pbProgress;
    private LinearLayout snackbar;
    Runnable mRunnable;
    Handler mHandler;

    WebService webService;
    public RegisterSecondStepFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_register_second_step, container, false);
        etNumber = (EditText)rootView.findViewById(R.id.etNumber);
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


        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etNumber, InputMethodManager.SHOW_IMPLICIT);
        etNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                etNumber.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(etNumber, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        etNumber.requestFocus();
        etNumber.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null&&!s.toString().matches("")
                        &&s.toString().startsWith("0")&&s.toString().length()==2){
                    etNumber.setText((Spanned)etNumber.getText().delete(0 , 1));
                    etNumber.setSelection(1);
                }
                // Remove spacing char
                if (s.length() > 0 && s.length()==4) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        String num = s.toString();
                        etNumber.setText(num.substring(0,3));
                        etNumber.setSelection(3);
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && s.length() == 4) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c)) {
                        String num = s.toString();
                        etNumber.setText(num.substring(0,3)+" "+num.substring(3,num.length()));
                        etNumber.setSelection(num.length()+1);
                    }
                }
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper helper = new Helper();
                if(helper.isValidMobileNumber(etNumber)) {
                    onContinueClick(v);
                }
                else {
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
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
    public void onContinueClick(View view) {
        if(webService.isNetworkConnected()) {
            if(snackbar.getVisibility()==View.VISIBLE) {
                snackbar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mRunnable);
            }
            final Dialog dialog = new Dialog(activity,R.style.MyAlertDialogStyle);
            dialog.setContentView(R.layout.dialog_check_number);
            dialog.setCancelable(true);

            TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
            String alert="Is your mobile\nnumber correct?\n\n+92 "+etNumber.getText().toString();
            tvAlert.setText(alert);
            Button btnOk = (Button)dialog.findViewById(R.id.btnOK);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    checkUserAndSendPin();
                }
            });
            Button btnEdit = (Button)dialog.findViewById(R.id.btnEdit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
        else {
            snackbar.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable, 4 * 1000);
        }
    }
    public void checkUserAndSendPin(){
        btnContinue.setEnabled(false);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        String phone = "0"+etNumber.getText().toString().replace(" ", "");
        PINCode = generatePIN();
        webService.checkUserAndSendPin(activity.getResources().getString(R.string.url) + "check_user", phone, PINCode, new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean exist = json.getBoolean("exist");
                    if(exist){
                        linlaHeaderProgress.setVisibility(View.GONE);
                        Bundle args = new Bundle();
                        args.putString(MySharedPreferences.USER_MOBILE_NUMBER, etNumber.getText().toString());
                        args.putInt(MySharedPreferences.USER_ID, json.getInt(MySharedPreferences.USER_ID));
                        args.putInt(MySharedPreferences.ROLE_ID, json.getInt(MySharedPreferences.ROLE_ID));
                        if(!json.isNull(MySharedPreferences.USER_EMAIL))
                            args.putString(MySharedPreferences.USER_EMAIL, json.getString(MySharedPreferences.USER_EMAIL));
                        else
                            args.putString(MySharedPreferences.USER_EMAIL, "");
                        args.putString(MySharedPreferences.USER_TITLE, json.getString(MySharedPreferences.USER_TITLE));
                        RegisterPasswordFragment fragment = new RegisterPasswordFragment();
                        fragment.setArguments(args);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
                    }
                    else {
                        linlaHeaderProgress.setVisibility(View.GONE);
                        Bundle args = new Bundle();
                        args.putString(MySharedPreferences.USER_MOBILE_NUMBER, etNumber.getText().toString());
                        args.putString(MySharedPreferences.PIN_CODE, PINCode);
                        RegisterThirdStepFragment fragment = new RegisterThirdStepFragment();
                        fragment.setArguments(args);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
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
    public String generatePIN()
    {
        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;

        //Store integer in a string
        PINCode = String.valueOf(randomPIN);

        return PINCode;

    }
    public void snackbarClick(){
        snackbar.setVisibility(View.GONE);
        mHandler.removeCallbacks(mRunnable);
    }
}
