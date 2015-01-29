package me.jiho.butterfly.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by jiho on 1/29/15.
 * Wrap view for hide and show animation
 */
public class Hideable {
    private View view;
    private Animation showAnimation;
    private Animation hideAnimation;

    public Hideable(View view) {
        this.view = view;
        init();
    }

    public Hideable(View view, Animation showAnimation, Animation hideAnimation) {
        this.view = view;
        this.showAnimation = showAnimation;
        this.hideAnimation = hideAnimation;
        init();
    }

    public Hideable(View view, int showAnimationRes, int hideAnimationRes) {
        this.view = view;
        this.showAnimation = AnimationUtils.loadAnimation(view.getContext(), showAnimationRes);
        this.hideAnimation = AnimationUtils.loadAnimation(view.getContext(), hideAnimationRes);
        init();
    }

    private void init() {
        /*
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        */
    }

    public void hide() {
        //view.startAnimation(hideAnimation);
        view.setVisibility(View.GONE);
    }

    public void show() {
        //view.startAnimation(showAnimation);
        view.setVisibility(View.VISIBLE);
    }

    public boolean isShown() {
        return view.isShown();
    }
}
