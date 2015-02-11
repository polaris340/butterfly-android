package me.jiho.butterfly.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.network.DefaultErrorListener;
import me.jiho.butterfly.network.VolleyRequestQueue;
import me.jiho.butterfly.picture.PictureDataManager;
import me.jiho.butterfly.statics.Constants;
import me.jiho.butterfly.util.DialogUtil;
import me.jiho.butterfly.util.MessageUtil;

/**
 * Created by jiho on 2/7/15.
 */
public class PictureDeleteButton extends PictureMenuItemButton {
    private static final String URL_PICTURE_DELETE = Constants.URLs.API_URL + "picture/";
    private static final String URL_SEND_PICTURE_DELETE = Constants.URLs.API_URL + "send-picture/";

    public PictureDeleteButton(Context context) {
        super(context);
    }

    public PictureDeleteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureDeleteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float progress = (float) animation.getAnimatedValue();
        setTranslationX(getMeasuredWidth() * (-progress));
        setAlpha(progress);
    }

    @Override
    protected void doOnClick(final long pictureId) {
        final Picture pictureData = PictureDataManager.getInstance().get(pictureId);

        new DialogUtil.ConfirmDialog(getContext()) {

            @Override
            protected void onPositiveButtonClicked() {
                String url;
                if (pictureData.getIsMine()) {
                    url = URL_PICTURE_DELETE + pictureId;
                } else {
                    url = URL_SEND_PICTURE_DELETE + pictureData.getSendPictureId();
                }

                Request request = new JsonObjectRequest(
                        Request.Method.DELETE,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                PictureDataManager.getInstance().delete(pictureId);
                                MessageUtil.showMessage("Picture deleted");
                                dismiss();
                            }
                        },
                        new DefaultErrorListener());
                VolleyRequestQueue.add(request);
            }

        }.setMessage(R.string.confirm_delete)
                .create().show();



    }
}
