package me.jiho.butterfly.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by jiho on 1/8/15.
 */
public class CustomFontEditText extends EditText implements FontChangeable {
    public CustomFontEditText(Context context) {
        super(context);
    }

    public CustomFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setFont(String fontPath) {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), fontPath);
        setTypeface(font);
    }


}
