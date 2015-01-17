package me.jiho.butterfly.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;

import me.jiho.butterfly.R;
import me.jiho.butterfly.util.ColorUtil;

/**
 * Created by jiho on 1/12/15.
 */
public class UploadTargetImageView extends ImageView{
    private File currentImageFile;


    public UploadTargetImageView(Context context) {
        super(context);
    }

    public UploadTargetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadTargetImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // set size to square when image is not set
        if (getDrawable() == null) {
            int width = getMeasuredWidth();
            setMeasuredDimension(width, width);
        }

    }


    public void setImageFile(File file) {
        this.currentImageFile = file;
        if (file == null)
            setImageURI(null);
        else {
            setImageURI(Uri.fromFile(file));
            getImagePrimaryColor();
        }
    }

    public File getImageFile() {
        return currentImageFile;
    }

    public String getImagePrimaryColor() {
        Palette palette = Palette.generate(((BitmapDrawable)getDrawable()).getBitmap(), 1);
        int color = palette.getMutedColor(getResources().getColor(R.color.lightgray));
        return ColorUtil.toHexString(color);
    }
}
