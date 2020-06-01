package com.omilia.demo;

import android.app.Application;

import com.omilia.sdk.OmiliaClient;

public class App extends Application {

    // overrides
    @Override
    public final void onCreate() {

        super.onCreate();

        OmiliaClient.launch(this);
    }
}
