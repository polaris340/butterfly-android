package me.jiho.butterfly.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/22/15.
 */
public class DialogManager {
    public static Dialog getDefaultProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.label_sign_up);
        dialog.setMessage(context.getString(R.string.message_default_progress));
        return dialog;
    }
}
