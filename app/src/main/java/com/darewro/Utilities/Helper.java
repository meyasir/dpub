package com.darewro.Utilities;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jaffar on 9/27/2017.
 */


/**
* edited by yasir on 19-may-18
*
* helper class deal with:
* 1- checking mobile validity
* 2- checking mobile number validity
* 3- checking emial validity
* 4-  volley is used for: (as by android guides)
* Automatic scheduling of network requests.
  Multiple concurrent network connections.
  Transparent disk and memory response caching with standard HTTP cache coherence.
  Support for request prioritization.
  Cancellation request API. You can cancel a single request, or you can set blocks or scopes of requests to cancel.
  Ease of customization, for example, for retry and backoff.
  Strong ordering that makes it easy to correctly populate your UI with data fetched asynchronously from the network.
  Debugging and tracing tools.
*
 *  */

public class Helper {

    public Helper() {
    }

    /**
    * For Valid Mobile You need to consider 7 digit to 13 digit because some country have 7 digit mobile number.
     * If your main target is your own country then you can match with the length. Assuming India has 10 digit mobile number.
     * Also we can not check like mobile number must starts with 9 or 8 or anything.
    *
      For mobile number I used this two Function
    * */
    public boolean isValidMobile(EditText editText) {
        String phone = editText.getText().toString();
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
//                txtPhone.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     *
     * This description is added for the commented method i-e isEmailValid(string email)
     */

    /**
    * Alternative method of the following isValidMail
     *
     * public static boolean isEmailValid(String email) {
     String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
     Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
     Matcher matcher = pattern.matcher(email);
     return matcher.matches();
     }
    *
     *
     * The below method is working the same as the above,not tested but looks almost same, test it later
    * */


    public boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        return check;
    }


    /**
     *
     * First of all getText from editText and set proper validations for checking mobile number like below:
     *
     * */

    public boolean isValidMobileNumber(EditText editText) {
        String phone = editText.getText().toString().replace(" ", "");
        boolean check = false;
        Log.d("checknumber", phone);
        Log.d("checknumber", "" + phone.length());
        final String regex = "^\\d{11}$";
        if (!Pattern.matches(regex, phone)) {
            Log.d("checknumber", "Pattern match");
            if (phone.length() == 10) {
                // if(phone.length() != 10) {
                check = true;
//                txtPhone.setError("Not Valid Number");
            } else {
                check = false;
            }
        } else {
            check = false;
        }
        return check;
    }


    /**
    *
     * The VolleyError object has a networkResponse reference, try to check it to see if you can get
     * some useful information from there.
    *
     * The networkResponse is null because in a TimeoutError no data is received from the server -- hence the timeout.
     * Instead, you need generic client side strings to display when one of these events occur. You can check for the
     * VolleyError's type using instanceof to differentiate between error types since you have no network response
     * to work with
     *
     * */

    public void volleyErrorMessage(Context context, VolleyError error) {
        String message = null;
        if (error instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (error instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (error instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
