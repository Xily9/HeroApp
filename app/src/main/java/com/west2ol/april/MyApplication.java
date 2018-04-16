package com.west2ol.april;

import android.app.Application;


public class MyApplication extends Application {
    public static MyApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
       // LitePal.initialize(this);
        mInstance = this;
    }
    public static MyApplication getInstance(){
        return mInstance;
    }
}
