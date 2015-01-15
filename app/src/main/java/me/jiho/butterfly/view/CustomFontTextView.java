package me.jiho.butterfly.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jiho on 1/8/15.
 */
public class CustomFontTextView extends TextView implements FontChangeable {
    public CustomFontTextView(Context context) {
        super(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setFont(String fontPath) {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), fontPath);
        setTypeface(font);
    }

}
