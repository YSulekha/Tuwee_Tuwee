package com.codepath.apps.tweet.application;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

//Application class to instantiate DB flow
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //instantiate DB flow
        FlowManager.init(new FlowConfig.Builder(this).build());
    }
}
