package co.bttrfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.nineoldandroids.animation.ValueAnimator;

import co.bttrfly.statics.Constants;

/**
 * Created by jiho on 2/6/15.
 */
public abstract class PictureMenuItemButton extends HideableMenuButton
        implements View.OnClickListener, ValueAnimator.AnimatorUpdateListener {

    protected ValueAnimator showAnimator;
    protected ValueAnimator hideAnimator;

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

    protected void init() {
        showAnimator = ValueAnimator.ofFloat(0f, 1f);
        showAnimator.setDuration(Constants.Integers.ANIMATION_DURATION);
        showAnimator.addUpdateListener(this);
        hideAnimator = ValueAnimator.ofFloat(1f, 0f);
        hideAnimator.setDuration(Constants.Integers.ANIMATION_DURATION);
        hideAnimator.addUpdateListener(this);
        setOnClickListener(this);
    }




    @Override
    public void show() {
        show(true);
    }

    @Override
    public void show(boolean animate) {
        setVisibility(View.VISIBLE);
        if (animate) {
            showAnimator.start();
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
