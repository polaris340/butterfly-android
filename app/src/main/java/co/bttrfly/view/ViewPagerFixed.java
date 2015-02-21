package co.bttrfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * to fix PhotoViewAttacher issue
 * https://github.com/chrisbanes/PhotoView/issues/31
 */
/** Custom your own ViewPager to extends support ViewPager. java source: */
/** Created by azi on 2013-6-21.  */

public class ViewPagerFixed extends android.support.v4.view.ViewPager {

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
