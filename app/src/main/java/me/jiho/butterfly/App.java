package me.jiho.butterfly;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.net.CookieHandler;
import java.net.CookieManager;

import me.jiho.butterfly.location.LastLocationManager;
import me.jiho.butterfly.statics.Constants;

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

        mDefaultDisplayImageOptionBuilder = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
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


}
