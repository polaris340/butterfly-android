package co.bttrfly.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import co.bttrfly.R;
import co.bttrfly.util.MessageUtil;

/**
 * Created by jiho on 1/11/15.
 */
public class DefaultErrorListener implements Response.ErrorListener {
    public static final String KEY_MESSAGE = "message";

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error.networkResponse == null) {
            // network error
            MessageUtil.showMessage(R.string.error_network);
        } else {
            try {
                String errorResponseString = new String(error.networkResponse.data);
                JSONObject errorJsonObject = new JSONObject(errorResponseString);
                String message = errorJsonObject.getString(KEY_MESSAGE);
                if (message != null) {
                    MessageUtil.showMessage(message);
                }
            } catch (Exception e) {
                MessageUtil.showDefaultErrorMessage();
            }
        }
    }
}
