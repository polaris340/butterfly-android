package me.jiho.butterfly.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import me.jiho.butterfly.R;
import me.jiho.butterfly.statics.Constants;

/**
 * Created by jiho on 2/11/15.
 */
public class ColorAnimatedProgressBar extends View {
    private ValueAnimator valueAnimator;

    public ColorAnimatedProgressBar(Context context) {
        super(context);
        init();
    }

    public ColorAnimatedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorAnimatedProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        valueAnimator = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                getResources().getColor(R.color.material_red_500),
                getResources().getColor(R.color.material_orange_500),
                getResources().getColor(R.color.material_yellow_500),
                getResources().getColor(R.color.material_light_green_500),
                getResources().getColor(R.color.material_teal_500),
                getResources().getColor(R.color.material_light_blue_500),
                getResources().getColor(R.color.material_indigo_500),
                getResources().getColor(R.color.material_purple_500)
        );

        valueAnimator.setDuration(Constants.Integers.ANIMATION_DURATION_LONG*5);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int)animation.getAnimatedValue();
                setBackgroundColor(color);
            }
        });
        valueAnimator.start();
    }
}
