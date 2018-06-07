package com.darewro.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.darewro.Activities.AppBaseActivity;
import com.darewro.Activities.RegisterActivity;
import com.darewro.R;
import com.darewro.Utilities.Database;
import com.darewro.Utilities.Helper;
import com.darewro.Utilities.MySharedPreferences;
import com.darewro.Utilities.WebService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jaffar on 2018-01-29.
 */

public class SettingsFragment extends Fragment {
    MySharedPreferences mySharedPreferences;
    private Dialog dialogChange;
    private RelativeLayout layoutName,layoutNumber,layoutEmail,layoutPassword,layoutSignOut;
    private TextView tvName,tvNumber,tvEmail;
    Activity activity;
    LinearLayout linlaHeaderProgress;
    private WebService webService;
    public SettingsFragment() {
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
        setInOtherFragment();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        layoutName = (RelativeLayout)rootView.findViewById(R.id.layoutName);
        layoutNumber = (RelativeLayout)rootView.findViewById(R.id.layoutNumber);
        layoutEmail = (RelativeLayout)rootView.findViewById(R.id.layoutEmail);
        layoutPassword = (RelativeLayout)rootView.findViewById(R.id.layoutPassword);
        layoutSignOut = (RelativeLayout)rootView.findViewById(R.id.layoutSignOut);
        linlaHeaderProgress = (LinearLayout)rootView.findViewById(R.id.Progress);
        tvName = (TextView) rootView.findViewById(R.id.tvName);
        tvNumber = (TextView) rootView.findViewById(R.id.tvNumber);
        tvEmail = (TextView) rootView.findViewById(R.id.tvEmail);
        mySharedPreferences = new MySharedPreferences();
        tvName.setText(mySharedPreferences.getUserTitle(activity));
        tvNumber.setText(mySharedPreferences.getUserMobileNumber(activity));
        tvEmail.setText(mySharedPreferences.getUserEmail(activity));

        layoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLayoutClick(view);
            }
        });
        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLayoutClick(view);
            }
        });
        layoutPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLayoutClick(view);
            }
        });
        layoutSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLayoutClick(view);
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
    private void onLayoutClick(View view){
        switch(view.getId()){
            case R.id.layoutName:
                changeName();
                break;
            case R.id.layoutNumber:
                break;
            case R.id.layoutEmail:
                changeEmail();
                break;
            case R.id.layoutPassword:
                changePassword();
                break;
            case R.id.layoutSignOut:
                signOut();
                break;
        }
    }
    public boolean isEditTextEmpty(EditText editText){
        String ed_text = editText.getText().toString().trim();
        if(ed_text.length() == 0 || ed_text.equals("") || ed_text == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void changeName(){
        dialogChange = new Dialog(activity,R.style.MyAlertDialogStyle);
        dialogChange.setContentView(R.layout.dialog_change_name);
        dialogChange.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText etName = (EditText)dialogChange.findViewById(R.id.etName);

        Button btnCancel = (Button)dialogChange.findViewById(R.id.btnCancel);
        Button btnUpdate = (Button)dialogChange.findViewById(R.id.btnUpdate);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(etName);
                dialogChange.dismiss();
            }
        });
        final MySharedPreferences mySharedPreferences = new MySharedPreferences();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag1 = isEditTextEmpty(etName);

                if (flag1) {

                } else {

                    if (webService.isNetworkConnected()) {
                        final String userTitle = etName.getText().toString();
                        hideKeyboard(etName);
                        dialogChange.dismiss();
                        linlaHeaderProgress.setVisibility(View.VISIBLE);
                        webService.changeUserTitle(getString(R.string.url) + "change_user_title", String.valueOf(mySharedPreferences.getUserID(activity)),userTitle, new WebService.VolleyResponseListener() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {

                            }

                            @Override
                            public void onSuccess(String response) {
                                linlaHeaderProgress.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");
                                    if(success){
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                        tvName.setText(userTitle);
                                        ((AppBaseActivity)activity).setTvCustomerName(userTitle);
                                    }
                                    else {
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                Helper h = new Helper();
                                h.volleyErrorMessage(activity,error);
                                linlaHeaderProgress.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        ((AppBaseActivity)activity).setSnackBarVisible();
                    }
                }
            }
        });
        dialogChange.setCancelable(false);
        dialogChange.show();
    }
    public void changeEmail(){
        dialogChange = new Dialog(activity,R.style.MyAlertDialogStyle);
        dialogChange.setContentView(R.layout.dialog_change_email);
        dialogChange.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText etEmail = (EditText)dialogChange.findViewById(R.id.etEmail);

        Button btnCancel = (Button)dialogChange.findViewById(R.id.btnCancel);
        Button btnUpdate = (Button)dialogChange.findViewById(R.id.btnUpdate);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(etEmail);
                dialogChange.dismiss();
            }
        });
        final MySharedPreferences mySharedPreferences = new MySharedPreferences();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag1 = isEditTextEmpty(etEmail);
                Helper helper = new Helper();
                if (flag1) {

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
                else  if (webService.isNetworkConnected()) {
                        final String userEmail = etEmail.getText().toString();
                        hideKeyboard(etEmail);
                        dialogChange.dismiss();
                        linlaHeaderProgress.setVisibility(View.VISIBLE);
                        webService.changeUserEmail(getString(R.string.url) + "change_user_email", String.valueOf(mySharedPreferences.getUserID(activity)),userEmail, new WebService.VolleyResponseListener() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {

                            }

                            @Override
                            public void onSuccess(String response) {
                                linlaHeaderProgress.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");
                                    if(success){
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                        tvEmail.setText(userEmail);
                                        ((AppBaseActivity)activity).setTvCustomerEmialAddress(userEmail);
                                    }
                                    else {
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                Helper h = new Helper();
                                h.volleyErrorMessage(activity,error);
                                linlaHeaderProgress.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        ((AppBaseActivity)activity).setSnackBarVisible();
                    }
            }
        });
        dialogChange.setCancelable(false);
        dialogChange.show();
    }
    public void changePassword(){
        dialogChange = new Dialog(activity,R.style.MyAlertDialogStyle);
        dialogChange.setContentView(R.layout.dialog_change_password);
        dialogChange.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText etCurPassword = (EditText)dialogChange.findViewById(R.id.etCurPassword);
        final EditText etNewPassword = (EditText)dialogChange.findViewById(R.id.etNewPassword);
        final EditText etRePassword = (EditText)dialogChange.findViewById(R.id.etRePassword);

        Button btnCancel = (Button)dialogChange.findViewById(R.id.btnCancel);
        Button btnUpdate = (Button)dialogChange.findViewById(R.id.btnUpdate);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(etCurPassword);
                hideKeyboard(etNewPassword);
                hideKeyboard(etRePassword);
                dialogChange.dismiss();
            }
        });
        final MySharedPreferences mySharedPreferences = new MySharedPreferences();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag1 = isEditTextEmpty(etCurPassword);
                boolean flag2 = isEditTextEmpty(etNewPassword);
                boolean flag3 = isEditTextEmpty(etRePassword);

                if (flag2&&flag3) {
                    final Dialog dialog = new Dialog(activity, R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView) dialog.findViewById(R.id.tvAlert);
                    String alert="";
                    alert = "\nInvalid Password\nPassword is required";
                    tvAlert.setText(alert);
                    Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else if(flag2||flag3){
                    final Dialog dialog = new Dialog(activity, R.style.MyAlertDialogStyle);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);

                    TextView tvAlert = (TextView) dialog.findViewById(R.id.tvAlert);
                    String alert="";
                    alert = "\nPasswords don't match\nNew Passwords don't match, Please try again.";
                    tvAlert.setText(alert);
                    Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else if(!flag2&&!flag3){
                    if(!etNewPassword.getText().toString().matches(etRePassword.getText().toString())){
                        final Dialog dialog = new Dialog(activity, R.style.MyAlertDialogStyle);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(true);

                        TextView tvAlert = (TextView) dialog.findViewById(R.id.tvAlert);
                        String alert="";
                        alert = "\nPasswords don't match\nNew Passwords don't match, Please try again.";
                        tvAlert.setText(alert);
                        Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    else {
                        if (webService.isNetworkConnected()) {
                            hideKeyboard(etCurPassword);
                            hideKeyboard(etNewPassword);
                            hideKeyboard(etRePassword);
                            dialogChange.dismiss();
                            linlaHeaderProgress.setVisibility(View.VISIBLE);
                            webService.changePassword(getString(R.string.url) + "change_user_password", String.valueOf(mySharedPreferences.getUserID(activity)),etCurPassword.getText().toString(), etNewPassword.getText().toString(),new WebService.VolleyResponseListener() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {

                                }

                                @Override
                                public void onSuccess(String response) {
                                    linlaHeaderProgress.setVisibility(View.GONE);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean success = jsonObject.getBoolean("success");
                                        if(success){
                                            String message = jsonObject.getString("message");
                                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            String message = jsonObject.getString("message");
                                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    Helper h = new Helper();
                                    h.volleyErrorMessage(activity,error);
                                    linlaHeaderProgress.setVisibility(View.GONE);
                                }
                            });

                        } else {
                            ((AppBaseActivity)activity).setSnackBarVisible();
                        }
                    }
                }
            }
        });
        dialogChange.setCancelable(false);
        dialogChange.show();
    }

    public void signOut(){
        Database mydb = new Database(activity);
        mydb.drop();
        MySharedPreferences mySharedPreferences = new MySharedPreferences();
        mySharedPreferences.clearPref(activity);
        Intent activity = new Intent(getActivity(), RegisterActivity.class);
        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(activity);
    }

    private void hideKeyboard(View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
