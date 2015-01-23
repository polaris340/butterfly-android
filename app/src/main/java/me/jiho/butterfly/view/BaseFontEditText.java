package me.jiho.butterfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import me.jiho.butterfly.App;

/**
 * Created by jiho on 1/8/15.
 */
public class BaseFontEditText extends EditText {
    public BaseFontEditText(Context context) {
        super(context);
        init();
    }

    public BaseFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseFontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(App.getBaseFont());
    }
}
