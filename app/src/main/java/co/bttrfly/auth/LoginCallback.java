package co.bttrfly.auth;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.bttrfly.db.Picture;
import co.bttrfly.picture.PictureDataManager;

/**
 * Created by jiho on 1/10/15.
 */
public class LoginCallback implements Response.Listener<JSONObject> {
    private static final String KEY_RESET = "reset_local_data";

    @Override
    public void onResponse(JSONObject response) {
        if (response.has(Auth.KEY_ACCESS_TOKEN)) {
            Auth auth = Auth.getInstance();
            try {
                auth.setAccessToken(response.getString(Auth.KEY_ACCESS_TOKEN));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            PictureDataManager pictureDataManager = PictureDataManager.getInstance();

            for (PictureDataManager.Type type: PictureDataManager.Type.values()) {
                if (!response.has(type.getKey())) continue;

                String pictureArrayString = response.getString(type.getKey());
                Picture[] pictures = Picture.fromJsonArray(pictureArrayString);

                int insertedCount = 0;
                ArrayList<Long> pictureIdList = pictureDataManager.getPictureIdList(type);
                for (Picture p : pictures) {
                    int index = pictureIdList.indexOf(p.getId());
                    if (index >= 0) {
                        pictureDataManager.put(p);
                        pictureDataManager.update(p.getId());
                    } else {
                        pictureDataManager.add(type, insertedCount++, p);
                    }
                    if (insertedCount > 0) {
                        pictureDataManager.addItems(type, 0, insertedCount);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
