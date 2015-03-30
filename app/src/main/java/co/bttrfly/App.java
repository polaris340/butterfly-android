package co.bttrfly;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;

import co.bttrfly.db.PictureDatabaseManager;
import co.bttrfly.location.LastLocationManager;
import co.bttrfly.picture.PictureDataManager;
import co.bttrfly.statics.Constants;
import co.bttrfly.util.ImageFileUtil;
import io.fabric.sdk.android.Fabric;

/**
 * Created by jiho on 1/7/15.
 */
public class App extends Application {
    private static final int MEMORY_CACHE_SIZE = 2 * 1024 * 1024;
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
    private static final int DISK_CACHE_FILE_COUNT = 100;

    private static Context context;
    private static Typeface themeFont;
    private static Typeface baseFont;
    private static Typeface faFont;

    private static DisplayImageOptions.Builder mDefaultDisplayImageOptionBuilder;

    public enum TrackerName {
        APP_TRACKER
    }

    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();



    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
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

        mDefaultDisplayImageOptionBuilder = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(Constants.Integers.ANIMATION_DURATION));

        // AUIL Settings
        DisplayImageOptions mDefaultDisplayImageOptions = mDefaultDisplayImageOptionBuilder.build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(mDefaultDisplayImageOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MEMORY_CACHE_SIZE))
                .diskCacheSize(DISK_CACHE_SIZE)
                .diskCacheFileCount(DISK_CACHE_FILE_COUNT)
                .build();
        ImageLoader.getInstance().init(config);


        PictureDatabaseManager.init(this);
        // to initialize
        PictureDataManager.getInstance();


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

    public static DisplayImageOptions.Builder getDefaultDisplayImageOptionBuilder() {
        return mDefaultDisplayImageOptionBuilder;
    }



    public synchronized Tracker getTracker(TrackerName trackerName) {
        if (!mTrackers.containsKey(trackerName)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(R.xml.tracker);
            mTrackers.put(trackerName, t);

        }
        return mTrackers.get(trackerName);
    }

}
