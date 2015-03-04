package co.bttrfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import co.bttrfly.R;
import co.bttrfly.statics.Constants;

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
