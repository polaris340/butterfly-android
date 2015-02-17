package me.jiho.butterfly.util;

import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import me.jiho.butterfly.App;
import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/30/15.
 */
public class ImageFileUtil {
    public static File createNewImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";


        File storageDir = new File(
                Environment.getExternalStorageDirectory()
                , App.getContext().getString(R.string.app_name)
        );


        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File newImageFile = new File(
                storageDir,
                imageFileName
        );

        newImageFile.createNewFile();

        return newImageFile;
    }

    public static void download(final String stringUrl, final File into, final Callable successCallback, final Callable errorCallback) {
        new AsyncTask<Object, Object, Boolean>() {

            @Override
            protected Boolean doInBackground(Object[] params) {
                try {
                    URL url = new URL(stringUrl);

                    URLConnection connection = url.openConnection();
                    InputStream is = connection.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);


                    // TODO : check buffer size
                    ByteArrayBuffer baf = new ByteArrayBuffer(50);
                    int current;
                    while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                    }

                    /* Convert the Bytes read to a String. */
                    FileOutputStream fos = new FileOutputStream(into);
                    fos.write(baf.toByteArray());
                    fos.close();

                    return true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }


            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);

                if (success) {
                    if (successCallback != null) {
                        try {
                            successCallback.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                            MessageUtil.showDefaultErrorMessage();
                        }
                    }
                } else {
                    if (errorCallback != null) {
                        try {
                            errorCallback.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                            MessageUtil.showDefaultErrorMessage();
                        }
                    } else {
                        MessageUtil.showDefaultErrorMessage();
                    }
                }
            }
        }.execute();
    }


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

    public static void copy(final File src, final File dst, final Callable successCallback, final Callable errorCallback) {
        try {
            InputStream is = new FileInputStream(src);
            copy(is, dst, successCallback, errorCallback);
        } catch (FileNotFoundException e) {
            try {
                errorCallback.call();
            } catch (Exception e1) {
                MessageUtil.showDefaultErrorMessage();
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void copy(final Uri src, final File dst, final Callable successCallback, final Callable errorCallback) {
        try {
            InputStream is = App.getContext().getContentResolver().openInputStream(src);
            copy(is, dst, successCallback, errorCallback);
        } catch (FileNotFoundException e) {
            try {
                errorCallback.call();
            } catch (Exception e1) {
                MessageUtil.showDefaultErrorMessage();
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void copy(final InputStream is, final File dst, final Callable successCallback, final Callable errorCallback) {
        new AsyncTask<Object, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Object[] params) {
                try {
                    FileOutputStream newfos = new FileOutputStream(dst);
                    int readcount = 0;
                    byte[] buffer = new byte[1024];
                    while ((readcount = is.read(buffer, 0, 1024)) != -1) {
                        newfos.write(buffer, 0, readcount);
                    }
                    newfos.close();
                    is.close();
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


    public static float[] getLoactionFromExif(File file) {
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            float[] latLng = new float[2];

            if (exif.getLatLong(latLng)) {
                return latLng;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
