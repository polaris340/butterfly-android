package co.bttrfly.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import co.bttrfly.R;
import co.bttrfly.db.Picture;
import co.bttrfly.network.DefaultErrorListener;
import co.bttrfly.network.VolleyRequestQueue;
import co.bttrfly.picture.PictureDataManager;
import co.bttrfly.statics.Constants;
import co.bttrfly.util.DialogUtil;
import co.bttrfly.util.MessageUtil;

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
        setTranslationX(getMeasuredWidth() * (-progress) * 2);
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
