package com.darewro.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darewro.Models.User;
import com.darewro.Models.GeneralOrder;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jaffar on 3/1/2017.
 */


/**
 *
 * web Services used for accessing and dealing with MIS which is our SERVER.
 * */

public class WebService {

   /*
   * code started from here only for volley singleton class, Which prevails the connection for the app lifetime
   * */
    Context mContext;
    int MY_SOCKET_TIMEOUT_MS = 15000;

    //new added
    private RequestQueue mRequestQueue;
    private static WebService mInstance;
    /////////////

    public WebService(Context mContext) {
        this.mContext = mContext;
        //new added
        mRequestQueue = getRequestQueue();
        ///////////////
    }
    //new added
    public static synchronized WebService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WebService(context);
        }
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /*
    * ended here singleton class
    * */
    /////////////

    public void proceedGeneralOrder(final String url, final User user1, final User user2, final GeneralOrder generalOrder, final String userID,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put("order_detail",generalOrder.getDetail());
                    params.put("delivery_charges",generalOrder.getDeliveryCharges());
                    params.put("delivery_time",generalOrder.getDeliveryTime());
                    params.put("order_detail",generalOrder.getDetail());
                    params.put("general_order_type",generalOrder.getBringMeOrDropIt());
                    if(generalOrder.getBringMeOrDropIt()=="0") {
                        params.put("picking_location", user2.getAddress());
                        params.put("delivery_location", user1.getAddress());
                        params.put("second_person_name", user2.getTitle());
                        params.put("second_person_mobile_no", user2.getNumber());
                    }
                    else {
                        params.put("picking_location", user1.getAddress());
                        params.put("delivery_location", user2.getAddress());
                        params.put("second_person_name", user1.getTitle());
                        params.put("second_person_mobile_no", user1.getNumber());
                    }
                    params.put("customer_mobile_no", user1.getNumber());
                    params.put("customer_name", user1.getTitle());
                    params.put("order_type", "General");
                    params.put(MySharedPreferences.USER_ID, userID);
                    return params;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }catch (Exception e){}
    }

    public void userRegistration(final String url, final User user, final String PINcode, final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_MOBILE_NUMBER, user.getNumber());
                    params.put(MySharedPreferences.USER_TITLE, user.getTitle());
                    params.put(MySharedPreferences.USER_EMAIL, user.getEmail());
                    params.put("password", PINcode);
                    params.put("mobile_token", user.getTokenKey());

                    return params;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }catch (Exception e){}
    }

    public void customerUpdateTokenKey(final String url, final User user, final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put("user_id", String.valueOf(user.getId()));
                    params.put("mobile_token", user.getTokenKey());

                    return params;
                }
            };

            queue.add(req);
        }catch (Exception e){}
    }

    public void proceedFoodOrder(final String url, final User user, final GeneralOrder generalOrder, final String restaurant_name, final JSONObject jsonObject, final String userID, final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put("delivery_charges",generalOrder.getDeliveryCharges());
                    params.put("delivery_time",generalOrder.getDeliveryTime());
                    params.put("order_detail",generalOrder.getDetail());
                    params.put("customer_name", user.getTitle());
                    params.put("customer_mobile_no", user.getNumber());
                    params.put("picking_location",restaurant_name);
                    params.put("delivery_location", user.getAddress());
                    params.put("cart_items",jsonObject.toString());
                    params.put("order_type", "Food");
                    params.put(MySharedPreferences.USER_ID, userID);
                    return params;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }catch (Exception e){}
    }

    public void getFoodItems(String url, final String res_id,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put("restaurant_id",res_id);

                    return params;
                }
            };

            queue.add(req);
        }catch (Exception e){}
    }

    public void getDeliveryTypes(String url, final String general_or_food,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put("general_or_food",general_or_food);

                    return params;
                }
            };

            queue.add(req);
        }catch (Exception e){}
    }

    public void checkUserAndSendPin(String url, final String userMobileNumber,final String PINCode,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_NAME,userMobileNumber);
                    params.put(MySharedPreferences.PIN_CODE,PINCode);
                    return params;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }catch (Exception e){}
    }
    public void checkUserEmailAndSendPin(String url, final String userMobileNumber,final String userEmail,final String PINCode,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_NAME,userMobileNumber);
                    params.put(MySharedPreferences.USER_EMAIL,userEmail);
                    params.put(MySharedPreferences.PIN_CODE,PINCode);
                    return params;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }catch (Exception e){}
    }

    public void checkUsernameAndPassword(String url, final String userName,final String password,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_NAME,userName);
                    params.put("password",password);
                    return params;
                }
            };
            queue.add(req);
        }catch (Exception e){}
    }

    public void sendPinCode(String url, final String userMobileNumber,final String PINCode,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_MOBILE_NUMBER,userMobileNumber);
                    params.put(MySharedPreferences.PIN_CODE,PINCode);
                    return params;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }catch (Exception e){}
    }

    public void changeUserTitle(String url, final String userID,final String userTitle,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_ID,userID);
                    params.put(MySharedPreferences.USER_TITLE,userTitle);
                    return params;
                }
            };

            queue.add(req);
        }catch (Exception e){}
    }
    public void changeUserEmail(String url, final String userID,final String userEmail,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_ID,userID);
                    params.put(MySharedPreferences.USER_EMAIL,userEmail);
                    return params;
                }
            };

            queue.add(req);
        }catch (Exception e){}
    }
    public void changePassword(String url, final String userID,final String curPassword,final String newPassword,final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_ID,userID);
                    params.put("user_password",curPassword);
                    params.put("new_password",newPassword);
                    return params;
                }
            };

            queue.add(req);
        }catch (Exception e){}
    }
    public void getOrders(String url, final String userID, final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            volleyResponseListener.onSuccess(s);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyResponseListener.onError(volleyError);
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> params = new HashMap<String,String>();
                    params.put(MySharedPreferences.USER_ID,userID);
                    return params;
                }
            };

            queue.add(req);
        }catch (Exception e){}
    }

    public void getData(String url, final VolleyResponseListener volleyResponseListener){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest req = new JsonObjectRequest(url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            volleyResponseListener.onSuccess(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    volleyResponseListener.onError(error);
                }
            });

            queue.add(req);
        }catch (Exception e){}

    }


    public void loadImage(String url, final VolleyImageResponse volleyImageResponse){
        // Retrieves an image specified by the URL, displays it in the UI.

        //RequestQueue queue = Volley.newRequestQueue(mContext);
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        volleyImageResponse.onSuccess(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        volleyImageResponse.onError(error);
                    }
                });

        //queue.add(request);
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        WebService.getInstance(mContext).addToRequestQueue(request);
    }

    public interface VolleyResponseListener {
        void onSuccess(JSONObject jsonObject);
        void onSuccess(String response);
        void onError(VolleyError error);
    }

    public interface VolleyImageResponse {
        void onSuccess(Bitmap bitmap);
        void onError(VolleyError error);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

}
