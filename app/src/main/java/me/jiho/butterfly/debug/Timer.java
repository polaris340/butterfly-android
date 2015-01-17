package me.jiho.butterfly.debug;

import android.util.Log;

/**
 * Created by jiho on 1/15/15.
 */
public class Timer {
    private static long time;
    public static void start() {
        time = System.nanoTime();
    }
    public static void check(String tag) {
        long currentTime = System.nanoTime();
        Log.e(tag, ((double)(currentTime - time) / 1000000) + "ms" );
        time = currentTime;

    }
}
