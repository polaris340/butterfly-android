package me.jiho.butterfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import me.jiho.butterfly.App;

/**
 * Created by jiho on 1/8/15.
 */
public class FAIconTextView extends TextView {

    public FAIconTextView(Context context) {
        super(context);
        init();
    }

    public FAIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FAIconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(
                App.getFaFont()
        );
    }
}
