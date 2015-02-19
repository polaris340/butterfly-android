package co.bttrfly.debug;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by jiho on 1/15/15.
 */
public class Timer {
    public static HashMap<String, Long> times = new HashMap<>();
    public static final String DEFAULT_TAG = "Timer";

    public static void start(String tag) {
        times.put(tag, System.nanoTime());
    }

    public static void check(String tag) {
        long currentTime = System.nanoTime();
        Log.e(tag, ((double)(currentTime - times.get(tag)) / 1000000) + "ms" );
        times.put(tag, currentTime);
    }

    public static void end(String tag) {
        check(tag);
        times.remove(tag);
        Log.e(tag, "end");
    }
}
