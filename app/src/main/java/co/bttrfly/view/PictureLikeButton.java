package co.bttrfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import co.bttrfly.R;
import co.bttrfly.db.Picture;
import co.bttrfly.network.DefaultErrorListener;
import co.bttrfly.network.VolleyRequestQueue;
import co.bttrfly.picture.PictureDataManager;
import co.bttrfly.statics.Constants;

/**
 * Created by jiho on 1/29/15.
 */
public class PictureLikeButton extends BaseFontButton {
    public static final String URL_LIKE = Constants.URLs.API_URL + "like-picture/";
    private long pictureId = 0;
    public PictureLikeButton(Context context) {
        super(context);
        init();
    }

    public PictureLikeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PictureLikeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pictureId == 0) return;
                final Picture pictureData = PictureDataManager.getInstance().get(pictureId);
                setEnabled(false);
                pictureData.setIsLiked(
                        !pictureData.getIsLiked()
                );
                if (pictureData.getIsLiked()) {
                    pictureData.setLikeCount(
                            pictureData.getLikeCount() + 1
                    );
                } else {
                    pictureData.setLikeCount(
                            pictureData.getLikeCount() - 1
                    );
                }
                setText(pictureData.getLikeCountString());

                setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        (pictureData.getIsLiked() ? R.drawable.heart_active_18 : R.drawable.heart_disabled_18),
                        0
                );
                Request request = new JsonObjectRequest(
                        Request.Method.POST,
                        URL_LIKE + pictureData.getId(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (pictureData.getId() == pictureId) {// 그사이에 바뀌지 않았으면..
                                    setEnabled(true);
                                }

                            }
                        },
                        new DefaultErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                super.onErrorResponse(error);
                                if (pictureData.getId() == pictureId) {// 그사이에 바뀌지 않았으면..

                                    setEnabled(true);
                                    pictureData.setIsLiked(
                                            !pictureData.getIsLiked()
                                    );

                                    if (pictureData.getIsLiked()) {
                                        pictureData.setLikeCount(pictureData.getLikeCount() + 1);
                                    } else {
                                        pictureData.setLikeCount(pictureData.getLikeCount() - 1);
                                    }
                                    setText(pictureData.getLikeCountString());

                                    setCompoundDrawablesWithIntrinsicBounds(
                                            0,
                                            0,
                                            (pictureData.getIsLiked() ? R.drawable.heart_active_18 : R.drawable.heart_disabled_18),
                                            0
                                    );
                                }
                            }
                        }
                );
                VolleyRequestQueue.add(request);
            }
        });
    }

    public void setPictureId(long pictureId) {
        this.pictureId = pictureId;

        Picture pictureData = PictureDataManager.getInstance().get(pictureId);
        setText(pictureData.getLikeCountString());
        int likeButtonIcon;
        if (pictureData.getIsLiked()) {
            likeButtonIcon = R.drawable.heart_active_18;
        } else {
            likeButtonIcon = R.drawable.heart_disabled_18;
        }

        setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                likeButtonIcon,
                0
        );

    }
}
