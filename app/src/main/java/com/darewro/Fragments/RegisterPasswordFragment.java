package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jaffar on 2018-01-29.
 */

public class RegisterPasswordFragment extends Fragment {
    MySharedPreferences mySharedPreferences;

    Activity activity;
    WebService webService;
    LinearLayout linlaHeaderProgress;
    ProgressBar pbProgress;
    private LinearLayout snackbar;
    Runnable mRunnable;
    Handler mHandler;
    TextView tvForgotPassword,tvHelp;
    ImageView callImageView;

    private EditText etPassword;
    private Button btnContinue;
    String mobileNumber,userTitle,userEmail;
    int userID,roleID;
    public RegisterPasswordFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_register_password, container, false);
        etPassword = (EditText)rootView.findViewById(R.id.etPassword);
        btnContinue = (Button) rootView.findViewById(R.id.btnContinue);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.Progress);
        pbProgress = (ProgressBar) rootView.findViewById(R.id.pbProgress);
        snackbar = (LinearLayout)rootView.findViewById(R.id.snackbar);
        tvForgotPassword =(TextView)rootView.findViewById(R.id.tvForgotPassword);
        tvHelp =(TextView)rootView.findViewById(R.id.tvHelp);
        callImageView =(ImageView) rootView.findViewById(R.id.callImageView);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onForgotPasswordCick(view);
            }
        });
        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallCick(view);
            }
        });
        callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallCick(view);
            }
        });

        mobileNumber = getArguments().getString(MySharedPreferences.USER_MOBILE_NUMBER);
        userTitle = getArguments().getString(MySharedPreferences.USER_TITLE);
        userEmail = getArguments().getString(MySharedPreferences.USER_EMAIL);
        userID = getArguments().getInt(MySharedPreferences.USER_ID);
        roleID = getArguments().getInt(MySharedPreferences.ROLE_ID);

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
        //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(etPassword, InputMethodManager.SHOW_IMPLICIT);
//        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                etPassword.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.showSoftInput(etPassword, InputMethodManager.SHOW_IMPLICIT);
//                    }
//                });
//            }
//        });
//        etPassword.requestFocus();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPassword.getText().toString()!=""&&!etPassword.getText().toString().matches("")) {
                    onContinueClick(v);
                }
                else {
                    // custom dialog
                    final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView)dialog.findViewById(R.id.tvAlert);
                    tvAlert.setText("Please enter your password");
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
            checkUsernameAndPassword();
        }
        else {
            snackbar.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable, 4 * 1000);
        }
    }
    public void onForgotPasswordCick(View view) {
        if(webService.isNetworkConnected()) {
            if(snackbar.getVisibility()==View.VISIBLE) {
                snackbar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mRunnable);
            }
            getEmailAndGoToPasswordRecover();
        }
        else {
            snackbar.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable, 4 * 1000);
        }
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
    public void checkUsernameAndPassword(){
        btnContinue.setEnabled(false);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        String phone = "0"+mobileNumber.replace(" ", "");
        webService.checkUsernameAndPassword(activity.getResources().getString(R.string.url) + "check_username_and_password",phone , etPassword.getText().toString(), new WebService.VolleyResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean success = json.getBoolean("success");
                    if(success){
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
                         linlaHeaderProgress.setVisibility(View.GONE);
                        String message = json.getString("message");
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        mySharedPreferences = new MySharedPreferences();
                        mySharedPreferences.getPrefs(activity);
                        mySharedPreferences.setUserID(activity,userID);
                        mySharedPreferences.setRoleID(activity,roleID);
                        mySharedPreferences.setUserMobileNumber(activity,"+92"+mobileNumber.replace(" ", ""));
                        mySharedPreferences.setUserTitle(activity,userTitle);
                        mySharedPreferences.setUserEmail(activity,userEmail);
                        mySharedPreferences.setRegister(activity,true);
                        sendRegistrationToServer(userID);
                        Intent i = new Intent(activity, AppBaseActivity.class);
                        i.putExtra("Uniqid","From_Register");
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                    else {
                        linlaHeaderProgress.setVisibility(View.GONE);
                        String message = json.getString("message");
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        btnContinue.setEnabled(true);
                    }
                } catch (JSONException e) {
                    linlaHeaderProgress.setVisibility(View.GONE);
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnContinue.setEnabled(true);
                }

            }

            @Override
            public void onError(VolleyError error) {
                linlaHeaderProgress.setVisibility(View.GONE);
                Helper h = new Helper();
                h.volleyErrorMessage(activity,error);
                btnContinue.setEnabled(true);
            }
        });
    }
    public void getEmailAndGoToPasswordRecover(){
        Bundle args = new Bundle();
        args.putString(MySharedPreferences.USER_MOBILE_NUMBER, mobileNumber);
        args.putString(MySharedPreferences.USER_EMAIL, userEmail);
        PasswordRecoverFragment fragment = new PasswordRecoverFragment();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }
    public void snackbarClick(){
        snackbar.setVisibility(View.GONE);
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     */
    private void sendRegistrationToServer(int id) {
        // TODO: Implement this method to send token to your app server.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        User user = new User();
        user.setId(id);
        user.setTokenKey(refreshedToken);
        webService.customerUpdateTokenKey(getResources().getString(R.string.url) + "user_token_update", user, new WebService.VolleyResponseListener() {
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

    }
}
