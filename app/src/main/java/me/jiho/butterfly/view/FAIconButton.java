package me.jiho.butterfly.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import me.jiho.butterfly.App;

/**
 * Created by jiho on 1/8/15.
 */
public class FAIconButton extends Button {
    private static Typeface typeface;
    public FAIconButton(Context context) {
        super(context);
        init();
    }

    public FAIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FAIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(
                App.getFaFont()
        );
    }
}
