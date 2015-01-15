package me.jiho.butterfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jiho on 1/13/15.
 */
public class PictureListImageView extends ImageView {
    private float imageRatio = 1f;

    public PictureListImageView(Context context) {
        super(context);
    }

    public PictureListImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureListImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageRatio(float imageRatio) {
        this.imageRatio = imageRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = (int)(width * imageRatio);

        setMeasuredDimension(width, height);
    }
}
