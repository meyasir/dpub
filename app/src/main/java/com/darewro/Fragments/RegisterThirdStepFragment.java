package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.R;
import com.darewro.Utilities.MySharedPreferences;
import com.darewro.Utilities.WebService;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jaffar on 2018-01-09.
 */

public class RegisterThirdStepFragment extends Fragment implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    MySharedPreferences mySharedPreferences;

    private Button btnCode;
    private TextView codetimer,calltimer;
    private CountDownTimer mCounterDownTime;
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;

    Activity activity;
    String mobileNumber,PINString;
    TextView tvHint;
    RelativeLayout call;

    WebService webService;

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Hides soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    /**
     * Initialize EditText fields.
     */

    public RegisterThirdStepFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_register_third_step, container, false);
        mPinFirstDigitEditText = (EditText)rootView. findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = (EditText)rootView. findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = (EditText)rootView. findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = (EditText)rootView. findViewById(R.id.pin_forth_edittext);
        mPinHiddenEditText = (EditText)rootView. findViewById(R.id.pin_hidden_edittext);
        setPINListeners();

        tvHint = (TextView) rootView.findViewById(R.id.tvHint);
        call = (RelativeLayout) rootView.findViewById(R.id.call);
        codetimer = (TextView) rootView.findViewById(R.id.codetimer);
        btnCode = (Button) rootView.findViewById(R.id.btnCode);
        calltimer = (TextView) rootView.findViewById(R.id.calltimer);
        mobileNumber = getArguments().getString(MySharedPreferences.USER_MOBILE_NUMBER);
        PINString = getArguments().getString(MySharedPreferences.PIN_CODE);
        tvHint.setText("Enter the pin you have received via SMS on +92 "+mobileNumber);

        mCounterDownTime = new CountDownTimer(60000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                codetimer.setText("Resend code "+String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                calltimer.setText("Request a call "+String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                codetimer.setVisibility(View.GONE);
                calltimer.setVisibility(View.GONE);
                btnCode.setVisibility(View.VISIBLE);
                call.setVisibility(View.VISIBLE);
                btnCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCodeBtnClick(view);
                    }
                });
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCodeBtnClick(view);
                    }
                });
            }
        };
        mCounterDownTime.start();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void onCodeBtnClick(View v){
        PINString = generatePIN();
        if(v.getId()==btnCode.getId()){
            String phone = "0"+mobileNumber.replace(" ", "");
            webService.sendPinCode(activity.getResources().getString(R.string.url) + "send_pin_code", phone, PINString, new WebService.VolleyResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {

                }

                @Override
                public void onSuccess(String response) {
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
            btnCode.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
            mCounterDownTime.start();
            codetimer.setVisibility(View.VISIBLE);
            calltimer.setVisibility(View.VISIBLE);

        }
        else if(v.getId()==call.getId()){
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
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 2) {
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 3) {
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 4) {
            mPinForthDigitEditText.setText(s.charAt(3) + "");

            hideSoftKeyboard(mPinForthDigitEditText);
            if(PINString.matches(s.toString())){
                onPinVerify();
            }
            else {
                Toast.makeText(activity, "You entered a wrong PIN", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);

    }

    /**
     * Sets background of the view.
     * This method varies in implementation depending on Android SDK version.
     *
     * @param view       View to which set background
     * @param background Background to set to view
     */
    @SuppressWarnings("deprecation")
    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public void onPinVerify() {
        Bundle args = new Bundle();
        args.putString(MySharedPreferences.USER_MOBILE_NUMBER, mobileNumber);
        args.putString(MySharedPreferences.PIN_CODE, PINString);

        RegisterFourthStepFragment fragment = new RegisterFourthStepFragment();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }


    public String generatePIN()
    {
        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;

        //Store integer in a string
        PINString = String.valueOf(randomPIN);

        return PINString;

    }
}
