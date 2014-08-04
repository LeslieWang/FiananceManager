package cn.leslie.financemanager;

import android.app.Application;

import cn.leslie.financemanager.data.DataManager;

/**
 * Main entry of our application.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.createInstance(this);
    }
}
