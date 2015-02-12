package me.jiho.butterfly.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jiho on 1/15/15.
 */
public class PinchZoomImageView extends ImageView {
    PhotoViewAttacher mAttacher;

    public PinchZoomImageView(Context context) {
        super(context);
        init();
    }

    public PinchZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinchZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAttacher = new PhotoViewAttacher(this);

    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mAttacher.update();
    }

    public PhotoViewAttacher getPhotoViewAttacher() {
        return mAttacher;
    }
}
