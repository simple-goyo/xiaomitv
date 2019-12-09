package com.fdse.xiaomitv.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public String getUserToken(Context context,String key){
        mSharedPreferences = context.getSharedPreferences("userToken", Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key,null);
    }

}
