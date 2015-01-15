package me.jiho.butterfly.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by jiho on 1/8/15.
 */
public class FontAwesomeButton extends Button {
    public FontAwesomeButton(Context context) {
        super(context);
        init();
    }

    public FontAwesomeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontAwesomeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf");
        setTypeface(font);
    }
}
