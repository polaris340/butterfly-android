package me.jiho.animatedtogglebutton;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CompoundButton;

/**
 * Created by user on 2015. 2. 2..
 */
public abstract class AnimatedToggleButton extends CompoundButton {
    public static final int DEFAULT_ANIMATION_DURATION = 200;
    protected float animationProgress = 0f;

    private ValueAnimator checkAnimator;
    private ValueAnimator uncheckAnimator;

    public AnimatedToggleButton(Context context) {
        super(context);
        init();
    }

    public AnimatedToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        checkAnimator = ValueAnimator.ofFloat(0f, 1f);
        uncheckAnimator = ValueAnimator.ofFloat(1f, 0f);

        checkAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        checkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((float) animation.getAnimatedValue());
            }
        });
        uncheckAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        uncheckAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((float) animation.getAnimatedValue());
            }
        });
    }

    @Override
    final protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw(canvas, animationProgress);
    }

    protected abstract void draw(Canvas canvas, float animationProgress);

    public void setDuration(int duration) {
        checkAnimator.setDuration(duration);
        uncheckAnimator.setDuration(duration);
    }

    public void setProgress(float newProgress) {
        if (newProgress < 0f || newProgress > 1f) {
            // Throw exception
            throw new IllegalArgumentException("Progress must in range 0f to 1f");
        }
        this.animationProgress = newProgress;
        invalidate();
    }


    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        try {
            if (checked) checkAnimator.start();
            else uncheckAnimator.start();
        } catch (NullPointerException e) {
            Log.i("AnimatedToggleButton", "Animator not initialized yet.");
        }
    }
}
