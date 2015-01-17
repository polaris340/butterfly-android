package me.jiho.butterfly.util;

/**
 * Created by jiho on 1/15/15.
 */
public class ColorUtil {
    public static String toHexString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
