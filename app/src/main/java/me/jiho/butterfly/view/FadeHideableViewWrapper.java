package me.jiho.butterfly.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

import me.jiho.butterfly.statics.Constants;

/**
 * Created by jiho on 2/9/15.
 */
public class FadeHideableViewWrapper extends HideableViewWrapper implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator showAnimator;
    private ValueAnimator hideAnimator;
    public FadeHideableViewWrapper(View view) {
        super(view);
        init();
    }

    private void init() {
        showAnimator = ValueAnimator.ofFloat(0f, 1f);
        showAnimator.addUpdateListener(this);
        showAnimator.setDuration(Constants.Integers.ANIMATION_DURATION);
        hideAnimator = ValueAnimator.ofFloat(1f, 0f);
        hideAnimator.addUpdateListener(this);
        hideAnimator.setDuration(Constants.Integers.ANIMATION_DURATION);

        showAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override public void onAnimationEnd(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });

        hideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        view.setAlpha((float) animation.getAnimatedValue());
    }

    @Override
    public void show() {
        showAnimator.start();
    }

    @Override
    public void hide() {
        hideAnimator.start();
    }
}
