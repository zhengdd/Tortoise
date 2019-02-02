package com.dongdong.animal.app;

import android.app.Application;
import android.content.Context;

import com.dongdong.animal.tortoise.keystorage.KeyStoreManager;

public class App extends Application {

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        KeyStoreManager.turnInit(this);
    }

    public static Context getContext() {
        return context;
    }
}
