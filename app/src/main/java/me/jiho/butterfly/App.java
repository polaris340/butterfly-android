package me.jiho.butterfly;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.net.CookieHandler;
import java.net.CookieManager;

import me.jiho.butterfly.location.LastLocationManager;

/**
 * Created by jiho on 1/7/15.
 */
public class App extends Application {
    private static Context context;

    private static Typeface themeFont;
    private static Typeface baseFont;
    private static Typeface faFont;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // Glide setup
        // https://github.com/bumptech/glide/wiki/Configuration


        // set to use cookie
        CookieManager cookieManage = new CookieManager();
        CookieHandler.setDefault(cookieManage);

        // get last location when app started
        LastLocationManager.getLastKnownLocation();

        themeFont = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.font_theme));
        baseFont = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.font_base));
        faFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf");
    }

    public static Context getContext() {
        return context;
    }

    public static Typeface getThemeFont() {
        return themeFont;
    }
    public static Typeface getBaseFont() {
        return baseFont;
    }
    public static Typeface getFaFont() {
        return faFont;
    }


}
