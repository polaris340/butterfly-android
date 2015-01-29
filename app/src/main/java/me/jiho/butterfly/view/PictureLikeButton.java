package me.jiho.butterfly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.network.DefaultErrorListener;
import me.jiho.butterfly.network.VolleyRequestQueue;
import me.jiho.butterfly.picture.PictureDataManager;
import me.jiho.butterfly.statics.Constants;
import me.jiho.butterfly.util.MessageUtil;

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
                                    try {
                                        Picture picture = Picture.fromJson(response.getString(Constants.Keys.MESSAGE));
                                        PictureDataManager pictureDataManager = PictureDataManager.getInstance();
                                        pictureDataManager.put(picture);
                                        pictureDataManager.update(pictureId);

                                        setText(picture.getLikeCountString());
                                        //pictureDataManager.update(picture.getId());
                                    } catch (JSONException e) {
                                        MessageUtil.showDefaultErrorMessage();
                                        e.printStackTrace();
                                    }
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
