package com.darewro.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.darewro.Fragments.HomeFragment;
import com.darewro.R;
import com.darewro.Utilities.MySharedPreferences;
import io.fabric.sdk.android.Fabric;


/**
 * edited by yasir
 *
 * This splash activity have two run methods (offered by Runnable interface), one starting AppBaseActivity
 * & another starting RegisterActivity which is base on the logic of shared Preferences.
 *
 *  Set time to handler and call Handler().postDelayed, it will call run method of runnable after set time
 *  and redirect to main app screen.
 *
 * */


public class SplashScreenActivity extends AppCompatActivity {

    MySharedPreferences mySharedPreferences;
    private Handler mHandler = new Handler();


    /*
    *Create a thread and set time to sleep after that redirect to main app screen.
    * */

    //run method starting AppBaseActivity
    private Runnable mRunnableAppBase = new Runnable() {
        public void run() {
            // This method will be executed once the timer is over
            // Start your app main activity
            Intent i = new Intent(SplashScreenActivity.this, AppBaseActivity.class);
            i.putExtra("Uniqid","From_SplashScreen");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            // remove this activity
            finish();
        }
    };
    //run method starting RegisterActivity
    private Runnable mRunnableRegister = new Runnable() {
        public void run() {
            Intent activity = new Intent(SplashScreenActivity.this, RegisterActivity.class);
            activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(activity);
        }
    };
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
//    ProgressBar pbProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

//        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
//        pbProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
        mySharedPreferences = new MySharedPreferences();
        mySharedPreferences.getPrefs(this);

        //Set time to handler and call Handler().postDelayed,
        // it will call run method of runnable after set time and redirect to main app screen.
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

            }
        }, SPLASH_TIME_OUT);

    }

    //onResumed, where user left and come back to show the right screen
    @Override
    protected void onResume() {
        super.onResume();

        if (mySharedPreferences.isFirstRun(this)) {
//             Do first run stuff here then set 'firstrun' as false
//             using the following line to edit/commit prefs
            mySharedPreferences.setFirstRun(this, false);
            mySharedPreferences.isRegister(this);

        }
        if(!mySharedPreferences.isRegister(this))
            mHandler.postDelayed(mRunnableRegister, SPLASH_TIME_OUT);
        else {
            mHandler.postDelayed(mRunnableAppBase, SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
