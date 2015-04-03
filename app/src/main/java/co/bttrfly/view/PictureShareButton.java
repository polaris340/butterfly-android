package co.bttrfly.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import co.bttrfly.R;

/**
 * Created by jiho on 2/7/15.
 */
public class PictureShareButton extends PictureMenuItemButton {
    private static final String URL_SHARE_PICTURE = "http://bttrfly.co/picture/";

    public PictureShareButton(Context context) {
        super(context);
    }

    public PictureShareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureShareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void doOnClick(long pictureId) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, URL_SHARE_PICTURE + pictureId);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getContext().getString(R.string.message_share));
        getContext().startActivity(Intent.createChooser(intent, "Share"));
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float progress = (float) animation.getAnimatedValue();
        ViewHelper.setTranslationX(this, progress * (getMeasuredWidth() * (-3)));
        ViewHelper.setAlpha(this, progress);
    }
}
