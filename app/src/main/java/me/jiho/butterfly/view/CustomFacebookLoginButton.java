package me.jiho.butterfly.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.facebook.widget.LoginButton;

/**
 * Created by jiho on 1/8/15.
 */
public class CustomFacebookLoginButton extends LoginButton {
    public CustomFacebookLoginButton(Context context) {
        super(context);
        init();
    }

    public CustomFacebookLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFacebookLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf");
        setTypeface(font);
    }
}
