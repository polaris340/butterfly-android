package co.bttrfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import co.bttrfly.App;

/**
 * Created by jiho on 1/8/15.
 */
public class ThemeFontButton extends Button {
    public ThemeFontButton(Context context) {
        super(context);
        init();
    }

    public ThemeFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThemeFontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(App.getThemeFont());
    }
}
