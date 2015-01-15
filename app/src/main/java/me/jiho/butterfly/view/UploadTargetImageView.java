package me.jiho.butterfly.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;

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
        else
            setImageURI(Uri.fromFile(file));
    }

    public File getImageFile() {
        return currentImageFile;
    }
}
