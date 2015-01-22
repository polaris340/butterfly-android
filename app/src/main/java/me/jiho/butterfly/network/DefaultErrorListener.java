package me.jiho.butterfly.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import me.jiho.butterfly.util.MessageUtil;

/**
 * Created by jiho on 1/11/15.
 */
public class DefaultErrorListener implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO : default error handling
        MessageUtil.showDefaultErrorMessage();
    }
}
