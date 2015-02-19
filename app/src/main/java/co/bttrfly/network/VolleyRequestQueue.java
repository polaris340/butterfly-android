package co.bttrfly.network;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import co.bttrfly.App;

/**
 * Created by jiho on 1/10/15.
 */
public class VolleyRequestQueue {
    private static RequestQueue requestQueue;

    public static final int TIMEOUT_LONG = 50000;

    public static void add(Request request) {
        VolleyRequestQueue.add(request, null);
    }

    public static void add(Request request, int timeout, int maxRetry) {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(timeout,
                maxRetry,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        VolleyRequestQueue.add(request, retryPolicy);
    }

    public static void add(Request request, RetryPolicy retryPolicy) {
        if (retryPolicy != null) request.setRetryPolicy(retryPolicy);
        try {
            requestQueue.add(request);
        } catch (NullPointerException e) {
            requestQueue = Volley.newRequestQueue(App.getContext());
            requestQueue.add(request);
        }
    }

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(App.getContext());
        }
        return requestQueue;
    }

}
