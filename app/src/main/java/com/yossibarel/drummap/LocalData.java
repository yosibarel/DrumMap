package com.yossibarel.drummap;

import android.content.SharedPreferences;

/**
 * Created by yossibarel on 16/04/16.
 */
public class LocalData {

    private static SharedPreferences mSharedPreferences;

    public static void setSharedPreferences(SharedPreferences sp) {
        mSharedPreferences = sp;
    }

    public static void setBpm(float bpm) {
        mSharedPreferences.edit().putFloat("bpm", bpm).commit();
    }

    public static float getBpm() {
        return mSharedPreferences.getFloat("bpm", 140);
    }
}
