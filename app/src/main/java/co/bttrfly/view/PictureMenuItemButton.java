package co.bttrfly.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import co.bttrfly.statics.Constants;

/**
 * Created by jiho on 2/6/15.
 */
public abstract class PictureMenuItemButton extends HideableMenuButton
        implements ValueAnimator.AnimatorUpdateListener, View.OnClickListener {
    private ValueAnimator showAnimator;
    private ValueAnimator hideAnimator;

    public PictureMenuItemButton(Context context) {
        super(context);
        init();
    }

    public PictureMenuItemButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PictureMenuItemButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        showAnimator = ValueAnimator.ofFloat(0f, 1f);
        showAnimator.addUpdateListener(this);
        hideAnimator = ValueAnimator.ofFloat(1f, 0f);
        hideAnimator.addUpdateListener(this);
        hideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        showAnimator.setDuration(Constants.Integers.ANIMATION_DURATION);
        hideAnimator.setDuration(Constants.Integers.ANIMATION_DURATION);

        setOnClickListener(this);
    }




    @Override
    public void show() {
        show(true);
    }

    @Override
    public void show(boolean animate) {
        setAlpha(0f);
        setVisibility(View.VISIBLE);
        if (animate) {
            showAnimator.start();
        } else {
            setAlpha(1f);
        }
    }

    @Override
    public void hide() {
        hide(true);
    }

    @Override
    public void hide(boolean animate) {
        if (animate) {
            hideAnimator.start();
        } else {
            setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) doOnClick((long)tag);
    }

    protected abstract void doOnClick(long pictureId);
}
