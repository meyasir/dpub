package com.darewro.Utilities;

import android.app.Application;

/**
 * Created by Jaffar on 9/25/2017.
 */

/*
*
* it is used for the type face in the android application all over except the editText
*
* but it's not used, i guess
* */
public class DarewroApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SANS", "fonts/Roboto-BOLD.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}