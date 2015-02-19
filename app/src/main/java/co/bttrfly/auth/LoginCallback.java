package co.bttrfly.auth;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import co.bttrfly.db.Picture;
import co.bttrfly.picture.PictureDataManager;

/**
 * Created by jiho on 1/10/15.
 */
public class LoginCallback implements Response.Listener<JSONObject> {
    @Override
    public void onResponse(JSONObject response) {
        if (response.has(Auth.KEY_ACCESS_TOKEN)) {
            Auth auth = Auth.getInstance();
            try {
                auth.setAccessToken(response.getString(Auth.KEY_ACCESS_TOKEN));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                PictureDataManager pictureDataManager = PictureDataManager.getInstance();

                for (PictureDataManager.Type type: PictureDataManager.Type.values()) {
                    PictureDataManager.getInstance().clear(type);
                    String pictureArrayString = response.getString(type.getKey());
                    Picture[] pictures = Picture.fromJsonArray(pictureArrayString);
                    for (int i = 0; i < pictures.length; i++) {
                        pictureDataManager.add(type, pictures[i]);
                    }

                    pictureDataManager.update(type);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
