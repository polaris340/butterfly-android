package me.jiho.butterfly.picture;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import me.jiho.butterfly.App;
import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.network.DefaultErrorListener;
import me.jiho.butterfly.network.MultipartRequest;
import me.jiho.butterfly.network.VolleyRequestQueue;
import me.jiho.butterfly.statics.Constants;
import me.jiho.butterfly.util.FileUtil;
import me.jiho.butterfly.util.MessageUtil;
import me.jiho.butterfly.view.UploadTargetImageView;

/**
 * Created by jiho on 1/12/15.
 */
public class PictureUploadDialogFragment extends DialogFragment
        implements View.OnClickListener, View.OnLongClickListener {
    public static final int REQUEST_IMAGE_CAPTURE = 32;
    public static final int REQUEST_IMAGE_EDIT = 64;
    public static final int REQUEST_IMAGE_SELECT = 128;
    public static final String TAG = "dialog_send";
    public static final String UPLOAD_URL = Constants.URLs.API_URL + "picture";
    public static final String KEY_UPLOAD_IMAGE = "image";
    //public static final String KEY_IMAGE_RATIO = "image_ratio";
    public static final String KEY_IMAGE_TITLE = "title";
    public static final String KEY_IMAGE_PRIMARY_COLOR = "primary_color";


    private static final int NOTIFICATION_ID = 4096;

    private UploadTargetImageView uploadTargetImageView;
    private File uploadTargetFile;
    private EditText titleInput;
    private static boolean uploading = false;



    private enum NotificationType {
        UPLOAD_START,
        UPLOAD_COMPLETE,
        UPLOAD_FAIL
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = null;


        if (!uploading) {
            View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_send, null);

            View takePictureButton = rootView.findViewById(R.id.upload_btn_take_picture);
            takePictureButton.setOnClickListener(this);
            takePictureButton.setOnLongClickListener(this);
            rootView.findViewById(R.id.upload_btn_edit_picture).setOnClickListener(this);
            rootView.findViewById(R.id.upload_btn_cancel).setOnClickListener(this);
            rootView.findViewById(R.id.upload_btn_submit).setOnClickListener(this);
            titleInput = (EditText) rootView.findViewById(R.id.upload_et_title);
            uploadTargetImageView = (UploadTargetImageView) rootView.findViewById(R.id.upload_iv_target);


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setView(rootView)
                    .setCancelable(false);

            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (uploadTargetFile == null) {
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        startTakePictureActivity();
                    }
                });
            } else {
                uploadTargetImageView.setImageFile(uploadTargetFile);
            }
        } else {
            View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_now_uploading, null);
            rootView.findViewById(R.id.btn_close_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setView(rootView)
                    .setCancelable(false);
            dialog = builder.create();
        }
        return dialog;
    }

    private void startTakePictureActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            if (uploadTargetFile == null) {
                // 현재 파일이 없는 경우에만 생성
                // 있으면 덮어씀
                try {
                    uploadTargetFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
            }
            // Continue only if the File was successfully created
            if (uploadTargetFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(uploadTargetFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_SELECT:
                    // 새로 찍은 사진이거나 복사된 사진이니까 지우자
                    if (uploadTargetFile != null) uploadTargetFile.delete();
                    Uri selectedImage = data.getData();
                    setUploadTargetFile(selectedImage);
                    break;
                case REQUEST_IMAGE_EDIT:
                    Uri savedImage = data.getData();
                    uploadTargetFile = new File(savedImage.getPath());
                case REQUEST_IMAGE_CAPTURE:
                    //uploadTargetImageView.setImageFile(null);
                    uploadTargetImageView.setImageFile(uploadTargetFile);
                    break;
            }
        } else {
            // 실패하면 이전파일 되돌림
            uploadTargetFile = uploadTargetImageView.getImageFile();
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";


        File storageDir = new File(
                Environment.getExternalStorageDirectory()
                , App.getContext().getString(R.string.app_name)
        );


        if (!storageDir.exists()) {
            Log.e("storageDir", "not exists");
            Log.e("mkdirs", storageDir.mkdirs() + "");
        }

        File newImageFile = new File(
                storageDir,
                imageFileName
        );

        newImageFile.createNewFile();

        return newImageFile;
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.upload_btn_take_picture:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_btn_take_picture:
                startTakePictureActivity();
                break;
            case R.id.upload_btn_edit_picture:
                if (uploadTargetFile == null) {
                    // TODO : show message

                } else {
                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    editIntent.setDataAndType(Uri.fromFile(uploadTargetFile), "image/jpg");
                    editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    editIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(uploadTargetFile));
                    editIntent.putExtra("finishActivityOnSaveCompleted", true);
                    startActivityForResult(editIntent, REQUEST_IMAGE_EDIT);
                }
                break;
            case R.id.upload_btn_cancel:
                if (uploadTargetFile != null)
                    uploadTargetFile.delete();
                dismiss();
                break;
            case R.id.upload_btn_submit:
                FileUtil.addToGallery(uploadTargetFile);
                if (uploadTargetFile == null) {
                    // TODO : show message
                    break;
                }
                Request request = new MultipartRequest(UPLOAD_URL, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Picture picture = null;
                        try {
                            picture = Picture.fromJson(response.getString(Constants.Keys.MESSAGE));
                            PictureDataManager.getInstance().add(PictureDataManager.Type.SENT, 0, picture);
                            PictureDataManager.getInstance().update(PictureDataManager.Type.SENT);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        uploading = false;

                        showNotification(NotificationType.UPLOAD_COMPLETE);

                    }
                },
                        new DefaultErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                super.onErrorResponse(error);
                                uploading = false;

                                showNotification(NotificationType.UPLOAD_FAIL);
                            }
                        },
                        new MultipartRequest.ProgressReporter() {
                            @Override
                            public void transferred(int transferredBytes, int totalSize) {
                                Log.i("transferred", transferredBytes + "/" + totalSize);
                            }
                        }) {
                    @Override
                    protected HttpEntity createHttpEntity() {
                        try {

                            InputStream inputStream = new FileInputStream(uploadTargetFile);
                            byte[] data;
                            data = IOUtils.toByteArray(inputStream);

                            InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(data), uploadTargetFile.getName());

                            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
                            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                            multipartEntity.addPart(KEY_UPLOAD_IMAGE, inputStreamBody);




                            if (titleInput.length() > 0) {
                                multipartEntity.addTextBody(KEY_IMAGE_TITLE,
                                        titleInput.getText().toString(),
                                        ContentType.APPLICATION_JSON);
                            }

                            multipartEntity.addTextBody(KEY_IMAGE_PRIMARY_COLOR,
                                    uploadTargetImageView.getImagePrimaryColor(),
                                    ContentType.APPLICATION_JSON);

                            return multipartEntity.build();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };



                showNotification(NotificationType.UPLOAD_START);
                MessageUtil.showMessage(R.string.message_upload_start);
                dismiss();
                uploading = true;
                VolleyRequestQueue.add(
                        request,
                        VolleyRequestQueue.TIMEOUT_LONG,
                        0
                );
                break;
        }
    }


    private static void showNotification(NotificationType type) {
        int message = 0;
        switch (type) {
            case UPLOAD_START:
                message = R.string.message_sending_picture;
                break;
            case UPLOAD_COMPLETE:
                message = R.string.message_send_complete;
                break;
            case UPLOAD_FAIL:
                message = R.string.message_send_fail;
                break;
        }

        // create notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(App.getContext())
                        .setSmallIcon(R.drawable.ic_sent_24)
                        .setColor(App.getContext().getResources().getColor(android.R.color.white))
                        .setContentTitle(App.getContext().getString(R.string.app_name))
                        .setContentText(App.getContext().getString(message));

//        if (type == NotificationType.UPLOAD_START) {
//            mBuilder.setOngoing(true);
//        } else {
//            mBuilder.setOngoing(false);
//        }
        // mId allows you to update the notification later on.
        NotificationManager mNotificationManager =
                (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    public void setUploadTargetFile(Uri contentUri) {
        try {
            setUploadTargetFile(App.getContext().getContentResolver().openInputStream(contentUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setUploadTargetFile(File file) {
        try {
            InputStream is = new FileInputStream(file);
            setUploadTargetFile(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setUploadTargetFile(InputStream is) {
        if (uploadTargetFile == null) {
            try {
                uploadTargetFile = createImageFile();
            } catch (IOException e) {
                MessageUtil.showDefaultErrorMessage();
                e.printStackTrace();
                return;
            }
        }
        FileUtil.copy(is, uploadTargetFile, new Callable() {
            @Override
            public Object call() throws Exception {
                if (getDialog() != null) {
                    //uploadTargetImageView.setImageFile(null);
                    uploadTargetImageView.setImageFile(uploadTargetFile);
                }
                return null;
            }
        }, new Callable() {
            @Override
            public Object call() throws Exception {
                MessageUtil.showDefaultErrorMessage();
                return null;
            }
        });
    }
}
