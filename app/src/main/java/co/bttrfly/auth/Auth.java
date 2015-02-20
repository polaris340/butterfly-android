package co.bttrfly.auth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import co.bttrfly.App;
import co.bttrfly.location.LastLocationManager;
import co.bttrfly.location.LocationData;
import co.bttrfly.network.DefaultErrorListener;
import co.bttrfly.network.VolleyRequestQueue;
import co.bttrfly.statics.Constants;

/**
 * Created by jiho on 1/7/15.
 */
public class Auth {
    private static final String URL_LOGIN = Constants.URLs.BASE_URL + "login";
    private static final String URL_SIGNUP = Constants.URLs.BASE_URL + "signup";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_PREF_AUTH = "auth";
    public static final String KEY_FB_ID = "fb_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GCM_REG_ID = "gcm_reg_id";

    public enum LoginState {
        NOT_LOGGED_IN,
        PENDING,
        LOGGED_IN
    }

    private String accessToken;
    private ArrayList<LoginStateChangeObserver> loginStateChangeObservers;
    private LoginState currentLoginState = LoginState.NOT_LOGGED_IN;

    private static Auth instance = new Auth();

    public static Auth getInstance() {
        return instance;
    }

    private Auth() {
        loginStateChangeObservers = new ArrayList<>();
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

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public boolean isLogin() {
        return currentLoginState == LoginState.LOGGED_IN;
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
        LocationData lastLocation = LastLocationManager.getLocationData();
        if (lastLocation != null) {
            requestData
                    .put(LocationData.KEY_LATITUDE, lastLocation.latitude)
                    .put(LocationData.KEY_LONGITUDE, lastLocation.longitude);
        }

        // add gcm reg key
        // TODO : 항상 보내면 안되는데..
        SharedPreferences sharedPreferences = getAuthPreference();
        String regid = sharedPreferences.getString(KEY_GCM_REG_ID, null);
        if (regid != null) {
            requestData.put(KEY_GCM_REG_ID, regid);
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
                        setCurrentLoginState(LoginState.LOGGED_IN);
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
                        setCurrentLoginState(LoginState.NOT_LOGGED_IN);
                    }
                });
        setCurrentLoginState(LoginState.PENDING);
        VolleyRequestQueue.add(request);
    }

    public void signup(String email, String password, final Callable successCallback, final Dialog dialog) throws JSONException {
        JSONObject requestData = new JSONObject()
                .put(KEY_EMAIL, email)
                .put(KEY_PASSWORD, password);

        // add location data
        LocationData lastLocation = LastLocationManager.getLocationData();
        if (lastLocation != null) {
            requestData
                    .put(LocationData.KEY_LATITUDE, lastLocation.latitude)
                    .put(LocationData.KEY_LONGITUDE, lastLocation.longitude);
        }

        // add gcm reg key
        SharedPreferences sharedPreferences = getAuthPreference();
        String regid = sharedPreferences.getString(KEY_GCM_REG_ID, null);
        if (regid != null) {
            requestData.put(KEY_GCM_REG_ID, regid);
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                URL_SIGNUP,
                requestData,
                new LoginCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        // start main activity
                        try {
                            successCallback.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setCurrentLoginState(LoginState.LOGGED_IN);

                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        setCurrentLoginState(LoginState.NOT_LOGGED_IN);
                    }
                });
        setCurrentLoginState(LoginState.PENDING);
        VolleyRequestQueue.add(request);
    }

    public static void logout() {

        getAuthPreference().edit().clear().commit();
        Intent intent = new Intent(App.getContext(), AuthActivity.class);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK
                        |Intent.FLAG_ACTIVITY_NEW_TASK
        );

        App.getContext().startActivity(intent);

    }

    public static SharedPreferences getAuthPreference() {
        return App.getContext().getSharedPreferences(KEY_PREF_AUTH, Context.MODE_PRIVATE);
    }

    private void setCurrentLoginState(LoginState newState) {
        currentLoginState = newState;
        for (LoginStateChangeObserver o:loginStateChangeObservers) {
            o.onLoginStateChanged(currentLoginState);
        }
    }

    public void addLoginStateChangeObserver(LoginStateChangeObserver observer) {
        loginStateChangeObservers.add(observer);
    }

}
