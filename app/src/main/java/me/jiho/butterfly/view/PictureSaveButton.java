package me.jiho.butterfly.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import me.jiho.butterfly.R;
import me.jiho.butterfly.picture.PictureDataManager;
import me.jiho.butterfly.util.ImageFileUtil;
import me.jiho.butterfly.util.MessageUtil;

/**
 * Created by jiho on 2/7/15.
 */
public class PictureSaveButton extends PictureMenuItemButton {
    public PictureSaveButton(Context context) {
        super(context);
    }

    public PictureSaveButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSaveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float progress = (float) animation.getAnimatedValue();
        setTranslationX(getMeasuredWidth() * (-progress) * 2);
        setAlpha(progress);
    }

    @Override
    protected void doOnClick(long pictureId) {
        String pictureUrl = PictureDataManager.getInstance().get(pictureId).getPictureUrl();
        MessageUtil.showMessage(R.string.message_picture_download_start);
        try {
            final File targetFile = ImageFileUtil.createNewImageFile();
            ImageFileUtil.download(
                    pictureUrl,
                    targetFile,
                    new Callable() {
                        @Override
                        public Object call() throws Exception {
                            MessageUtil.showMessage(R.string.message_picture_download_complete);
                            ImageFileUtil.addToGallery(targetFile);
                            return null;
                        }
                    },
                    new Callable() {
                        @Override
                        public Object call() throws Exception {
                            MessageUtil.showDefaultErrorMessage();
                            targetFile.delete();
                            return null;
                        }
                    }
            );
        } catch (IOException e) {
            MessageUtil.showDefaultErrorMessage();
            e.printStackTrace();
        }
    }
}
