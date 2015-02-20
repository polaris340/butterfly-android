package co.bttrfly.statics;

/**
 * Created by jiho on 1/10/15.
 */
public interface Constants {
    public interface URLs {
        public static final String BASE_URL = "http://bttrfly.co:8000/";
        public static final String API_URL = BASE_URL + "api/";
    }

    public interface Keys {
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
    }

    public interface Integers {
        public static final int ANIMATION_DURATION = 200;
        public static final int ANIMATION_DURATION_LONG = 500;
    }
}
