package com.darewro.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jaffar on 2018-01-15.
 */

public class MySharedPreferences {
    public static String MY_PREFS_NAME = "com.darewro";

    public static String IS_FIRST = "firstrun";
    public static String IS_REGISTER = "register";

    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_MOBILE_NUMBER = "user_mobile_number";
    public static final String USER_TITLE = "user_title";
    public static final String USER_NAME = "user_name";
    public static final String PIN_CODE= "pin_code";
    public static final String ROLE_ID = "role_id";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE);

    }

    public static boolean isFirstRun(Context context) {
        return getPrefs(context).getBoolean(IS_FIRST, false);
    }
    public static void setFirstRun(Context context,boolean firstRun) {
        getPrefs(context).edit().putBoolean(IS_FIRST, firstRun).commit();
    }

    public static int getUserID(Context context) {
        return getPrefs(context).getInt(USER_ID, 0);
    }
    public static void setUserID(Context context,int userID) {
        getPrefs(context).edit().putInt(USER_ID, userID).commit();
    }

    public static int getRoleID(Context context) {
        return getPrefs(context).getInt(ROLE_ID, 0);
    }
    public static void setRoleID(Context context,int roleID) {
        getPrefs(context).edit().putInt(ROLE_ID, roleID).commit();
    }

    public static boolean isRegister(Context context) {
        return getPrefs(context).getBoolean(IS_REGISTER, false);
    }
    public static void setRegister(Context context,boolean register) {
        getPrefs(context).edit().putBoolean(IS_REGISTER, register).commit();
    }

    public static String getUserMobileNumber(Context context) {
        return getPrefs(context).getString(USER_MOBILE_NUMBER, "");
    }
    public static void setUserMobileNumber(Context context,String mobileNumber) {
        getPrefs(context).edit().putString(USER_MOBILE_NUMBER, mobileNumber).commit();
    }

    public static String getUserTitle(Context context) {
        return getPrefs(context).getString(USER_TITLE, "");
    }
    public static void setUserTitle(Context context,String userTitle) {
        getPrefs(context).edit().putString(USER_TITLE, userTitle).commit();
    }
    public static String getUserEmail(Context context) {
        return getPrefs(context).getString(USER_EMAIL, "");
    }
    public static void setUserEmail(Context context,String customerEmail) {
        getPrefs(context).edit().putString(USER_EMAIL, customerEmail).commit();
    }

    public static void clearPref(Context context){
        getPrefs(context).edit().putInt(USER_ID, 0).commit();
        getPrefs(context).edit().putInt(ROLE_ID, 0).commit();
        getPrefs(context).edit().putBoolean(IS_REGISTER, false).commit();
        getPrefs(context).edit().putString(USER_MOBILE_NUMBER, "").commit();
        getPrefs(context).edit().putString(USER_TITLE, "").commit();
        getPrefs(context).edit().putString(USER_EMAIL, "").commit();
    }
}
