package me.jiho.butterfly;

import android.app.Application;
import android.content.Context;

import java.net.CookieHandler;
import java.net.CookieManager;

import me.jiho.butterfly.location.LastLocationManager;

/**
 * Created by jiho on 1/7/15.
 */
public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;


        // set to use cookie
        CookieManager cookieManage = new CookieManager();
        CookieHandler.setDefault(cookieManage);

        // get last location when app started
        LastLocationManager.getLastKnownLocation();
    }

    public static Context getContext() {
        return context;
    }
}
