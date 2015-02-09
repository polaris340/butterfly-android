package me.jiho.butterfly.view;

import android.view.View;

/**
 * Created by jiho on 1/29/15.
 * Wrap view for hide and show animation
 */
public class HideableViewWrapper {
    protected View view;

    public HideableViewWrapper(View view) {
        this.view = view;
    }


    public void hide() {
        view.setVisibility(View.GONE);
    }

    public void show() {
        view.setVisibility(View.VISIBLE);
    }

    public boolean isShown() {
        return view.isShown();
    }
}
