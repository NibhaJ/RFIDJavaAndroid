package com.example.rfidreadersample.util;

import android.app.Application;

public class InitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);

    }
}
