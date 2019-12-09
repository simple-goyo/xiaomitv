package com.fdse.xiaomitv;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.fdse.xiaomitv.util.SharePreferenceUtil;

public class MyApp extends Application {

    private static MyApp instance = null;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    private String appInfo = "I am a custom application.";

    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    SharePreferenceUtil mSpUtil;
    public static final String PREFERENCE_NAME = "_sharedinfo";

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null) {
            mSpUtil = new SharePreferenceUtil(this, "cookie");
        }
        return mSpUtil;
    }
}