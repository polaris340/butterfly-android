package me.jiho.butterfly.view;

import android.content.Context;
import android.util.AttributeSet;

import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/8/15.
 */
public class ThemeFontTextView extends CustomFontTextView {
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
        setFont(getContext().getString(R.string.font_theme));
    }
}
