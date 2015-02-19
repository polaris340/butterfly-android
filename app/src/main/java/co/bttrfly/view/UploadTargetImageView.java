package co.bttrfly.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import co.bttrfly.App;
import co.bttrfly.R;
import co.bttrfly.util.ColorUtil;

/**
 * Created by jiho on 1/12/15.
 */
public class UploadTargetImageView extends ImageView{
    private File currentImageFile;

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
    }

    public void setImageFile(File file) {
        this.currentImageFile = file;
        setImageDrawable(null);

        DisplayImageOptions options = App.getDefaultDisplayImageOptionBuilder()
                .showImageOnLoading(R.drawable.loading_placeholder)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .build();
        ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), this, options);
    }


    public File getImageFile() {
        return currentImageFile;
    }

    public String getImagePrimaryColor() {
        int color = getResources().getColor(R.color.lightgray);
        try {
            Palette palette = Palette.generate(((BitmapDrawable)getDrawable()).getBitmap(), 6);
            color = palette.getLightVibrantColor(getResources().getColor(R.color.lightgray));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ColorUtil.toHexString(color);
    }
}
