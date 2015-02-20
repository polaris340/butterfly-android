package co.bttrfly.view;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.bttrfly.App;
import co.bttrfly.R;
import co.bttrfly.db.Picture;
import co.bttrfly.location.LocationData;
import co.bttrfly.network.DefaultErrorListener;
import co.bttrfly.network.VolleyRequestQueue;
import co.bttrfly.statics.Constants;
import co.bttrfly.util.DialogUtil;
import co.bttrfly.util.MessageUtil;

/**
 * Created by jiho on 2/20/15.
 */
public class SendCountButton extends BaseFontButton {
    private static final String URL_GET_RECEIVED_POINTS = Constants.URLs.API_URL + "received-points/";


    public SendCountButton(Context context) {
        super(context);
        init();
    }

    public SendCountButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SendCountButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    final Picture pictureData = (Picture) tag;
                    if (pictureData.getSendCount() == 0) {
                        return;
                    }
                    final Dialog dialog = DialogUtil.getDefaultProgressDialog(v.getContext());
                    dialog.show();
                    Request request = new JsonObjectRequest(
                            URL_GET_RECEIVED_POINTS + pictureData.getId(),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray data = response.getJSONArray(Constants.Keys.DATA);
                                        Gson gson = new Gson();
                                        ArrayList<LocationData> receivedLocations = new ArrayList<>();
                                        for (int i = 0; i < data.length(); i++) {

                                            String jsonLocation = data.getString(i);
                                            LocationData location = gson.fromJson(jsonLocation, LocationData.class);
                                            if (location.latitude != 0d || location.longitude != 0d) {
                                                receivedLocations.add(location);
                                            }

                                        }
                                        if (receivedLocations.size() == 0) {
                                            MessageUtil.showMessage(
                                                    String.format(
                                                            App.getContext().getString(R.string.message_send_count),
                                                            pictureData.getSendCount())
                                            );
                                        } else {
                                            DialogUtil
                                                    .getMapDialog(v.getContext(), receivedLocations)
                                                    .show();
                                        }
                                        dialog.hide();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        dialog.hide();
                                    }

                                }
                            },
                            new DefaultErrorListener()
                    );
                    VolleyRequestQueue.add(request);
                }
            }
        });
    }
}
