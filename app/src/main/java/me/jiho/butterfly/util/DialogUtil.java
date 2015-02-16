package me.jiho.butterfly.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import me.jiho.butterfly.App;
import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/22/15.
 */
public class DialogUtil {
    public static Dialog getDefaultProgressDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        Dialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(view)
                .create();
        return dialog;
    }

    public static Dialog getMapDialog(Context context, double latitude, double longitude) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_map, null);
        ImageView mapImageView = (ImageView) view.findViewById(R.id.mapdialog_iv_map);
        Button closeButton = (Button) view.findViewById(R.id.mapdialog_btn_close);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final Dialog dialog = builder.create();

        DisplayImageOptions options = App.getDefaultDisplayImageOptionBuilder()
                .showImageOnLoading(R.drawable.loading_placeholder)
                .build();
        ImageLoader.getInstance().displayImage(
                "http://maps.googleapis.com/maps/api/staticmap?center="+
                        latitude+","+longitude+
                        "&zoom=6&size=640x640&maptype=terrain" +
                "&markers=size:small%7Ccolor:red%7C"+latitude+","+longitude+"&sensor=false",
                mapImageView,
                options
        );


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static abstract class ConfirmDialog {
        private AlertDialog.Builder builder;
        private Dialog dialog;
        private View rootView;

        public ConfirmDialog(Context context) {
            builder = new AlertDialog.Builder(context);
            rootView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);
            rootView.findViewById(R.id.confirm_btn_negative).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNegativeButtonClicked();
                }
            });
            rootView.findViewById(R.id.confirm_btn_positive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPositiveButtonClicked();
                }
            });

            builder.setView(rootView);
        }

        public ConfirmDialog setTitle(int stringRes) {
            ((TextView) rootView.findViewById(R.id.confirm_title)).setText(stringRes);
            return this;
        }

        public ConfirmDialog setMessage(int stringRes) {
            ((TextView) rootView.findViewById(R.id.confirm_message)).setText(stringRes);
            return this;
        }


        public Dialog create() {
            dialog = builder.create();
            return dialog;
        }

        public void dismiss() {
            dialog.dismiss();
        }

        protected abstract void onPositiveButtonClicked();
        protected void onNegativeButtonClicked() {
            dismiss();
        };
    }

}
