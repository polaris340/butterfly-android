package me.jiho.butterfly.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import me.jiho.butterfly.App;
import me.jiho.butterfly.location.LastLocationManager;
import me.jiho.butterfly.network.DefaultErrorListener;
import me.jiho.butterfly.network.VolleyRequestQueue;
import me.jiho.butterfly.statics.Constants;

/**
 * Created by jiho on 1/7/15.
 */
public class Auth {
    private static final String URL_LOGIN = Constants.URLs.BASE_URL + "login";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_PREF_AUTH = "auth";
    public static final String KEY_FB_ID = "fb_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";



    private String accessToken;

    private static Auth instance = new Auth();

    public static Auth getInstance() {
        return instance;
    }

    private Auth() {
        accessToken = Auth.getAuthPreference()
                .getString(KEY_ACCESS_TOKEN, null);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        Auth.getAuthPreference()
                .edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .apply();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isLogin() {
        return accessToken != null;
    }



    public void loginWithAccessToken() throws JSONException {
        loginWithAccessToken(null, null);
    }

    public void loginWithAccessToken(final Callable loginCallback, final Callable errorCallback) throws JSONException {
        if (accessToken == null) throw new NullPointerException();
        JSONObject jsonObject = new JSONObject().put(KEY_ACCESS_TOKEN, accessToken);
        login(jsonObject, loginCallback, errorCallback);
    }

    public void login(JSONObject requestData) throws JSONException {
        login(requestData, null, null);
    }

    public void login(JSONObject requestData, final Callable loginCallback, final Callable errorCallback) throws JSONException {


        // add location data
        LastLocationManager.LocationData lastLocation = LastLocationManager.getLocationData();
        if (lastLocation != null) {
            requestData
                    .put(LastLocationManager.KEY_LATITUDE, lastLocation.latitude)
                    .put(LastLocationManager.KEY_LONGITUDE, lastLocation.longitude);
        }

        Request request = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, requestData,
                new LoginCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);

                        if (loginCallback != null) {
                            try {
                                loginCallback.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);

                        if (errorCallback != null) {
                            try {
                                errorCallback.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        VolleyRequestQueue.add(request);
    }


    public static SharedPreferences getAuthPreference() {
        return App.getContext().getSharedPreferences(KEY_PREF_AUTH, Context.MODE_PRIVATE);
    }


}
