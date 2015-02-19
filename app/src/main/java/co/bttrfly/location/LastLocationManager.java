package co.bttrfly.location;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import co.bttrfly.App;
import co.bttrfly.auth.Auth;

/**
 * Created by jiho on 1/10/15.
 */
public class LastLocationManager {
    private static volatile LocationData mLastLocation;
    public static GoogleApiClient mGoogleApiClient;
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    public static synchronized void getLastKnownLocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(App.getContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);

                        if (lastLocation != null) {
                            mLastLocation = new LocationData(lastLocation.getLatitude(), lastLocation.getLongitude());
                            Auth.getAuthPreference()
                                    .edit()
                                    .putString(KEY_LATITUDE, Double.toString(mLastLocation.latitude))
                                    .putString(KEY_LONGITUDE, Double.toString(mLastLocation.longitude))
                                    .apply();
                        }

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static LocationData getLocationData() {
        if (mLastLocation == null) { // get location data from preference
            mLastLocation = new LocationData();
            SharedPreferences authPreference = Auth.getAuthPreference();
            if (!authPreference.contains(KEY_LATITUDE)
                    || !authPreference.contains(KEY_LONGITUDE))
                return null;
            mLastLocation.latitude = Double.parseDouble(authPreference.getString(KEY_LATITUDE, null));
            mLastLocation.longitude = Double.parseDouble(authPreference.getString(KEY_LONGITUDE, null));
        }
        return mLastLocation;
    }

    public static class LocationData {
        public double latitude;
        public double longitude;

        public LocationData() {

        }

        public LocationData(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

}
