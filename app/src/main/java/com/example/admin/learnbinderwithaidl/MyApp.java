package com.example.admin.learnbinderwithaidl;

import android.app.Application;
import android.content.Context;

/**
 * @author zhou.jn
 * @creator_at 2018/8/11 16:01
 */
public class MyApp extends Application {

    private static MyApp instance;

    public MyApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
}
