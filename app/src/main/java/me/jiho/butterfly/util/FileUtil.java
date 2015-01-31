package me.jiho.butterfly.util;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import me.jiho.butterfly.App;

/**
 * Created by jiho on 1/30/15.
 */
public class FileUtil {
    public static File fromUri(Uri uri) {
        // And to convert the image URI (content:// format) to the direct file system path of the image file
        // From "http://www.androidsnippets.com/get-file-path-of-gallery-image"
        // http://stackoverflow.com/questions/12714701/deprecated-managedquery-issue

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = App.getContext().getContentResolver().query(uri,
                proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        Log.e("uri", uri.toString());
        Log.e(uri.getPath(), "" + filePath);

        return new File(filePath);
    }


    // TODO : merge to one function
    public static void copy(final File src, final File dst, final Callable successCallback, final Callable errorCallback) {
        new AsyncTask<Object, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Object[] params) {
                try {
                    FileInputStream fis = new FileInputStream(src);
                    FileOutputStream newfos = new FileOutputStream(dst);
                    int readcount = 0;
                    byte[] buffer = new byte[1024];
                    while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
                        newfos.write(buffer, 0, readcount);
                    }
                    newfos.close();
                    fis.close();
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    try {
                        successCallback.call();
                    } catch (Exception e) {
                        MessageUtil.showDefaultErrorMessage();
                        e.printStackTrace();
                    }
                } else {
                    try {
                        errorCallback.call();
                    } catch (Exception e) {
                        MessageUtil.showDefaultErrorMessage();
                        e.printStackTrace();
                    }
                }

            }
        }.execute();

    }

    // TODO : merge to one function
    public static void copy(final Uri src, final File dst, final Callable successCallback, final Callable errorCallback) {
        new AsyncTask<Object, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Object[] params) {
                try {
                    InputStream fis = App.getContext().getContentResolver().openInputStream(src);
                    FileOutputStream newfos = new FileOutputStream(dst);
                    int readcount = 0;
                    byte[] buffer = new byte[1024];
                    while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
                        newfos.write(buffer, 0, readcount);
                    }
                    newfos.close();
                    fis.close();
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    try {
                        successCallback.call();
                    } catch (Exception e) {
                        MessageUtil.showDefaultErrorMessage();
                        e.printStackTrace();
                    }
                } else {
                    try {
                        errorCallback.call();
                    } catch (Exception e) {
                        MessageUtil.showDefaultErrorMessage();
                        e.printStackTrace();
                    }
                }

            }
        }.execute();
    }


    public static void addToGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        App.getContext().sendBroadcast(mediaScanIntent);
    }

}
