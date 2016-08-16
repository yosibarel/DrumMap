package com.yossibarel.drummap;

import android.app.Application;
import android.os.Environment;

import java.io.File;

import Utils.FileManager;

/**
 * Created by yossibarel on 16/04/16.
 */
public class App extends Application {
    private static File SAVE_DIR;
    private static File APP_DIR;

    public static File getAppDir() {
        return APP_DIR;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        APP_DIR = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC + "/SynthPro/");
        if (!APP_DIR.exists())
            APP_DIR.mkdir();
        LocalData.setSharedPreferences(getSharedPreferences(getPackageName(), MODE_PRIVATE));
        FileManager.create(this);
    }
}
