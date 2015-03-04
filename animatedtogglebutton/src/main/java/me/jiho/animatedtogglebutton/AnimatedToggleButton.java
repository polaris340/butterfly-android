package me.jiho.animatedtogglebutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Interpolator;
import android.widget.CompoundButton;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by user on 2015. 2. 2..
 */
public abstract class AnimatedToggleButton extends CompoundButton {
    private static final String TAG = "AnimatedToggleButton";
    public static final int DEFAULT_ANIMATION_DURATION = 200;

    protected float animationProgress = 0f;
    protected Paint paint;

    private ValueAnimator checkAnimator;
    private ValueAnimator uncheckAnimator;
    private float rotateAngle = 0f;

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

    protected void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        setClickable(true);
        if (isChecked()) animationProgress = 1f;
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
        drawIcon(canvas);
    }

    protected abstract void drawIcon(Canvas canvas);

    public void setColor(int color) {
        paint.setColor(color);
    }

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
        ViewHelper.setRotation(this, rotateAngle * animationProgress);
        invalidate();
    }

    public void setRotateAngle(float newAngle) {
        this.rotateAngle = newAngle;
    }

    public void setInterpolator(Interpolator interpolator) {
        setInterpolator(interpolator, true);
        setInterpolator(interpolator, false);
    }
    public void setInterpolator(Interpolator interpolator, boolean checked) {
        if (checked) {
            checkAnimator.setInterpolator(interpolator);
        } else {
            uncheckAnimator.setInterpolator(interpolator);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked() != checked) {
            try {
                if (checked) checkAnimator.start();
                else uncheckAnimator.start();
            } catch (NullPointerException e) {
                Log.i(TAG, "Animator may not initialized yet.");
            }
        }
        super.setChecked(checked);

    }

}
