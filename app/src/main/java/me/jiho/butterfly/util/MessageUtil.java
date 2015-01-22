package me.jiho.butterfly.util;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import me.jiho.butterfly.App;
import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/17/15.
 */
public class MessageUtil {

    public static void showDefaultErrorMessage() {
        showMessage(R.string.error_default);
    }

    public static void showMessage(int stringRes) {
        showMessage(stringRes, Toast.LENGTH_SHORT);
    }

    public static void showMessage(int stringRes, int duration) {
        Toast toast = new Toast(App.getContext());
        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.toast_default, null);
        ((TextView)view.findViewById(R.id.toast_message)).setText(stringRes);
        toast.setView(view);
        toast.setDuration(duration);
        toast.show();
    }

    public static void showMessage(String message) {
        showMessage(message, Toast.LENGTH_SHORT);
    }

    public static void showMessage(String message, int duration) {
        Toast toast = new Toast(App.getContext());
        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.toast_default, null);
        ((TextView)view.findViewById(R.id.toast_message)).setText(message);
        toast.setView(view);
        toast.setDuration(duration);
        toast.show();
    }

}
