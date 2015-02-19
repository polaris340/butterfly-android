package co.bttrfly.auth;

/**
 * Created by jiho on 2/8/15.
 */
public interface LoginStateChangeObserver {
    public void onLoginStateChanged(Auth.LoginState loginState);
}
