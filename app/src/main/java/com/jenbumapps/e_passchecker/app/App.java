package com.jenbumapps.e_passchecker.app;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    public static volatile Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();
    }
}
