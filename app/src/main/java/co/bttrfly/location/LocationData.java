package co.bttrfly.location;

/**
 * Created by jiho on 2/20/15.
 */
public class LocationData {
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    public double latitude;
    public double longitude;

    public LocationData() {

    }

    public LocationData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
