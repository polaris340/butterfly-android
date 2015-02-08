package me.jiho.butterfly.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import me.jiho.butterfly.R;
import me.jiho.butterfly.util.ColorUtil;

/**
 * Created by jiho on 1/12/15.
 */
public class UploadTargetImageView extends ImageView{
    private File currentImageFile;
    private AnimationDrawable placeholder;

    public UploadTargetImageView(Context context) {
        super(context);
        init();
    }

    public UploadTargetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UploadTargetImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // set size to square when image is not set
        int width = getMeasuredWidth();
        if (getDrawable() == null) {
            setMeasuredDimension(width, width);
        }
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        getImagePrimaryColor();
    }

    public void setImageFile(File file) {
        this.currentImageFile = file;
        Glide.with(getContext())
                .load(file)
                .placeholder(R.drawable.loading_placeholder)
                .into(this);
        /*
        this.currentImageFile = file;
        if (file == null)
            setImageURI(null);
        else {
            setImageURI(Uri.fromFile(file));
            //getImagePrimaryColor();
        }
        //*/
    }

    public File getImageFile() {
        return currentImageFile;
    }

    public String getImagePrimaryColor() {
        int color = getResources().getColor(R.color.lightgray);
        try {
            Palette palette = Palette.generate(((BitmapDrawable)getDrawable()).getBitmap(), 1);
            color = palette.getMutedColor(getResources().getColor(R.color.lightgray));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ColorUtil.toHexString(color);
    }
}
