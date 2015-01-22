package me.jiho.butterfly.auth;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.picture.PictureDataManager;

/**
 * Created by jiho on 1/10/15.
 */
public class LoginCallback implements Response.Listener<JSONObject> {
    @Override
    public void onResponse(JSONObject response) {
        if (response.has(Auth.KEY_ACCESS_TOKEN)) {
            try {
                Auth.getInstance().setAccessToken(response.getString(Auth.KEY_ACCESS_TOKEN));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                PictureDataManager pictureDataManager = PictureDataManager.getInstance();

                String sentPicturesArrayString = response.getString(PictureDataManager.Type.SENT.getKey());
                Picture[] pictures = Picture.fromJsonArray(sentPicturesArrayString);
                for (int i = 0; i < pictures.length; i++) {
                    pictureDataManager.add(PictureDataManager.Type.SENT, pictures[i]);
                }

                pictureDataManager.update(PictureDataManager.Type.SENT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // TODO : processing another data
    }

}
