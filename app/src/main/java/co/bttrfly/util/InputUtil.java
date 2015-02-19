package co.bttrfly.util;

/**
 * Created by jiho on 1/22/15.
 */
public class InputUtil {
    public static final String PATTERN_EMAIL = "^(\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.(\\w{2,10}|\\w{2,3}\\.\\w{2}))$";
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 20;

    public static boolean isValidEmail(String email) {
        return email.matches(PATTERN_EMAIL);
    }

    public static boolean isValidPasswordLength(String password) {
        return
                password.length() >= MIN_PASSWORD_LENGTH
                && password.length() <= MAX_PASSWORD_LENGTH;
    }
}
