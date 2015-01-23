package me.jiho.butterfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import me.jiho.butterfly.App;

/**
 * Created by jiho on 1/8/15.
 */
public class ThemeFontTextView extends TextView {
    public ThemeFontTextView(Context context) {
        super(context);
        init();
    }

    public ThemeFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThemeFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(App.getThemeFont());
    }
}
