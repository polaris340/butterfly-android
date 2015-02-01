package me.jiho.butterfly.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/22/15.
 */
public class DialogUtil {
    public static Dialog getDefaultProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(context.getString(R.string.message_default_progress));
        dialog.setCancelable(false);
        return dialog;
    }
}