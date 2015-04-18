package co.bttrfly;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.melnykov.fab.FloatingActionButton;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import co.bttrfly.auth.Auth;
import co.bttrfly.auth.AuthActivity;
import co.bttrfly.gcm.GcmBroadcastReceiver;
import co.bttrfly.location.LocationData;
import co.bttrfly.network.DefaultErrorListener;
import co.bttrfly.network.MultipartRequest;
import co.bttrfly.network.VolleyRequestQueue;
import co.bttrfly.picture.PictureDataManager;
import co.bttrfly.picture.PictureListFragment;
import co.bttrfly.statics.Constants;
import co.bttrfly.util.DialogUtil;
import co.bttrfly.util.ImageFileUtil;
import co.bttrfly.util.MessageUtil;
import co.bttrfly.view.UploadTargetImageView;


public class MainActivity extends BaseActivity
        implements View.OnClickListener, View.OnLongClickListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "MainActivity";
    public static final String KEY_FRAGMENT_TYPE = "fragment_type";
    public static final int SELECT_PICTURE = 128;
    public static final int REQUEST_IMAGE_EDIT = 64;
    public static final int REQUEST_IMAGE_CAPTURE = 32;
    public static final String UPLOAD_URL = Constants.URLs.API_URL + "picture";
    public static final String KEY_UPLOAD_IMAGE = "image";
    public static final String KEY_IMAGE_TITLE = "title";
    public static final String KEY_IMAGE_PRIMARY_COLOR = "primary_color";

    private static boolean uploading = false;

    private static NotificationCompat.Builder mNotificationBuilder;
    private static NotificationManager mNotificationManager;

    private enum NotificationType {
        UPLOAD_START,
        UPLOAD_COMPLETE,
        UPLOAD_FAIL
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    MainFragmentPagerAdapter mFragmentPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    DrawerLayout drawer;
    private Tracker mTracker;


    // for gcm
    public static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "375201291734";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;


    // picture upload
    private AlertDialog mUploadPictureDialog;
    private TextView mTitleInput;
    private UploadTargetImageView mUploadTargetImageView;
    private File mUploadTargetFile;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(
                IntentCompat.FLAG_ACTIVITY_CLEAR_TASK
                        |Intent.FLAG_ACTIVITY_NEW_TASK
        );

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!Auth.getInstance().hasAccessToken()) {
            Intent intent = new Intent(this, AuthActivity.class);

            intent.addFlags(
                    IntentCompat.FLAG_ACTIVITY_CLEAR_TASK
                            |Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);

            return;
        }

        try {
            Auth.getInstance().loginWithAccessToken(null, new Callable() {
                @Override
                public Object call() throws Exception {
                    /*
                    Intent intent = AuthActivity.getIntent(MainActivity.this);
                    startActivity(intent);
                    */
                    return null;

                }
            });
        } catch (JSONException e) {
            // this exception will not occur...
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        getSupportActionBar().hide();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        Intent intent = getIntent();
        String type = intent.getStringExtra(KEY_FRAGMENT_TYPE);
        if (type != null && type.equals(PictureDataManager.Type.SENT.name())) {
            mViewPager.setCurrentItem(1);
        }

        String notice = intent.getStringExtra(Constants.Keys.NOTICE);
        if (notice != null) {
            DialogUtil.getNoticeDialog(this, notice).show();
        }

        mViewPager.setOnPageChangeListener(this);



        // set listener
        FloatingActionButton takePictureButton = (FloatingActionButton) findViewById(R.id.btn_take_picture);
        takePictureButton.setOnClickListener(this);
        takePictureButton.setOnLongClickListener(this);



        // gcm
        registerGcm();

        // handle share intent
        handleIntent(intent);


        //create upload dialog
        createPictureUploadDialog();


        mTracker = ((App) App.getContext()).getTracker(App.TrackerName.APP_TRACKER);

    }

    private void registerGcm() {
        context = getApplicationContext();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null && intent.getAction().equals(Intent.ACTION_SEND)) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                showUploadDialogWithImageUri(imageUri);
            }
        }
    }


    @Override
    public boolean onLongClick(View v) {
        if (uploading) {
            showNowUploadingDialog();
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PICTURE);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_PICTURE:
                    Uri selectedImage = data.getData();
                    showUploadDialogWithImageUri(selectedImage);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    mUploadPictureDialog.show();
                    break;
                case REQUEST_IMAGE_EDIT:
                    Uri savedImage = data.getData();

                    if (savedImage.getScheme().equals("file")) {
                        mUploadTargetFile = new File(savedImage.getPath());
                        mUploadTargetImageView.setImageFile(mUploadTargetFile);
                    } else {
                        final Dialog progressDialog = DialogUtil.getDefaultProgressDialog(this);
                        progressDialog.show();
                        ImageFileUtil.copy(savedImage, mUploadTargetFile, new Callable() {
                            @Override
                            public Object call() throws Exception {
                                setUploadTargetFile(null);
                                setUploadTargetFile(mUploadTargetFile);
                                progressDialog.dismiss();
                                return null;
                            }
                        }, new Callable() {
                            @Override
                            public Object call() throws Exception {
                                MessageUtil.showDefaultErrorMessage();
                                progressDialog.dismiss();
                                mUploadPictureDialog.dismiss();
                                return null;
                            }
                        });
                    }
                    break;
            }

        }
    }

    private void startTakePictureActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                if (mUploadTargetFile == null) {
                    mUploadTargetFile = ImageFileUtil.createNewImageFile();
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mUploadTargetFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            } catch (IOException e) {
                e.printStackTrace();
                MessageUtil.showDefaultErrorMessage();
            }
        }
    }

    private void showUploadDialogWithImageUri(Uri uri) {
        try {
            InputStream is = App.getContext().getContentResolver().openInputStream(uri);
            final Dialog progressDialog = DialogUtil.getDefaultProgressDialog(this);
            if (!this.isFinishing()) {
                progressDialog.show();
            }
            if (mUploadTargetFile == null) {
                mUploadTargetFile = ImageFileUtil.createNewImageFile();
            }
            ImageFileUtil.copy(is, mUploadTargetFile, new Callable() {
                @Override
                public Object call() throws Exception {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    mUploadPictureDialog.show();
                    return null;
                }
            }, new Callable() {
                @Override
                public Object call() throws Exception {
                    MessageUtil.showDefaultErrorMessage();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtil.showDefaultErrorMessage();
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START);
        } else if (mUploadPictureDialog.isShowing()) {
            dismissUploadDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PictureDataManager.getInstance().update();
        checkPlayServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                PictureDataManager.getInstance().saveToLocalDB();
                return null;
            }
        }.execute();

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = Auth.getAuthPreference();
        String registrationId = prefs.getString(Auth.KEY_GCM_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Object, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(regid);
                    Log.i(TAG, "register completed");
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }

        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
//    private void sendRegistrationIdToBackend() {
//        // send gcm regid to server
//        try {
//            JSONObject jsonObject = new JSONObject().put("regid", regid);
//            Request request = new JsonObjectRequest(
//                    Request.Method.POST,
//                    Constants.URLs.BASE_URL + "gcm_key_registration",
//                    jsonObject,
//                    null,
//                    new DefaultErrorListener()
//            );
//            VolleyRequestQueue.add(request);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = Auth.getAuthPreference();
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Auth.KEY_GCM_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        try {
            setScreen(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setScreen(int position) throws NullPointerException {
        PictureListFragment currentFragment
                = (PictureListFragment) getSupportFragmentManager()
                .getFragments()
                .get(position);

        // Set screen name.
        mTracker.setScreenName(currentFragment.getScreenName());
        // Send a screen view.
        mTracker.send(new HitBuilders.AppViewBuilder().build());

    }


    private void createPictureUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View rootView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.dialog_send, null);

        mTitleInput = (TextView) rootView.findViewById(R.id.upload_et_title);
        mUploadTargetImageView = (UploadTargetImageView) rootView.findViewById(R.id.upload_iv_target);
        rootView.findViewById(R.id.upload_btn_cancel).setOnClickListener(this);
        rootView.findViewById(R.id.upload_btn_edit_picture).setOnClickListener(this);
        rootView.findViewById(R.id.upload_btn_submit).setOnClickListener(this);

        builder.setView(rootView)
                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK
                                && event.getAction() == KeyEvent.ACTION_UP) {
                            dismissUploadDialog();
                            return true;
                        }

                        return false;
                    }
                });

        mUploadPictureDialog = builder.create();
        mUploadPictureDialog.setCanceledOnTouchOutside(false);
        mUploadPictureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mUploadPictureDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (mUploadTargetFile != null) {
                    mUploadTargetImageView.setImageFile(mUploadTargetFile);
                }
            }
        });
    }

    private void setUploadTargetFile(File file) {
        this.mUploadTargetFile = file;
        if (mUploadPictureDialog.isShowing()) {
            mUploadTargetImageView.setImageFile(file);
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_picture:
                if (uploading) {
                    showNowUploadingDialog();
                } else {
                    startTakePictureActivity();
                }
                break;
            case R.id.upload_btn_cancel:
                dismissUploadDialog();
                break;
            case R.id.upload_btn_edit_picture:
                if (mUploadTargetFile == null) {
                    // TODO : show message

                } else {
                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    editIntent.setDataAndType(Uri.fromFile(mUploadTargetFile), "image/jpg");
                    editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    editIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mUploadTargetFile));
                    editIntent.putExtra("finishActivityOnSaveCompleted", true);
                    startActivityForResult(editIntent, REQUEST_IMAGE_EDIT);
                }
                break;

            case R.id.upload_btn_submit:
                uploadPicture();

                break;
        }
    }

    private void uploadPicture() {
        if (mUploadTargetFile == null) {
            // TODO : show message
            return;
        }
        ImageFileUtil.addToGallery(mUploadTargetFile);
        Request request = new MultipartRequest(UPLOAD_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                uploading = false;
                showNotification(NotificationType.UPLOAD_COMPLETE);


            }
        },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        uploading = false;

                        mUploadTargetFile = null;
                        showNotification(NotificationType.UPLOAD_FAIL);
                    }
                },
                new MultipartRequest.ProgressReporter() {
                    @Override
                    public void transferred(long transferredBytes, int progress) {
                        if (mNotificationBuilder != null) {

                            mNotificationBuilder.setProgress(100, progress, false);
                            mNotificationManager.notify(
                                    GcmBroadcastReceiver.GCM_NOTIFICATION_ID_SENT,
                                    mNotificationBuilder.build());
                        }
                    }
                },
                mUploadTargetFile.length()) {
            @Override
            protected HttpEntity createHttpEntity() {
                try {

                    InputStream inputStream = new FileInputStream(mUploadTargetFile);
                    byte[] data;
                    data = IOUtils.toByteArray(inputStream);

                    InputStreamBody inputStreamBody = new InputStreamBody(
                            new ByteArrayInputStream(data),
                            mUploadTargetFile.getName()
                    );

                    MultipartEntityBuilder multipartEntity
                            = MultipartEntityBuilder.create();
                    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    multipartEntity.addPart(KEY_UPLOAD_IMAGE, inputStreamBody);




                    if (mTitleInput.length() > 0) {
                        multipartEntity.addTextBody(KEY_IMAGE_TITLE,
                                mTitleInput.getText().toString(),
                                ContentType.APPLICATION_JSON);
                    }

                    float[] latLng = mUploadTargetImageView.getLocation();
                    if (latLng != null) {
                        multipartEntity.addTextBody(LocationData.KEY_LATITUDE,
                                Float.toString(latLng[0]));
                        multipartEntity.addTextBody(LocationData.KEY_LONGITUDE,
                                Float.toString(latLng[1]));
                    }

                    multipartEntity.addTextBody(KEY_IMAGE_PRIMARY_COLOR,
                            mUploadTargetImageView.getImagePrimaryColor());

                    return multipartEntity.build();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };


        showNotification(NotificationType.UPLOAD_START);
        MessageUtil.showMessage(R.string.message_upload_start);
        mUploadPictureDialog.dismiss();
        uploading = true;
        VolleyRequestQueue.add(
                request,
                VolleyRequestQueue.TIMEOUT_LONG,
                0
        );
    }

    private void dismissUploadDialog() {
        new DialogUtil.ConfirmDialog(this) {

            @Override
            protected void onPositiveButtonClicked() {
                this.dismiss();
                mUploadPictureDialog.dismiss();
                if (mUploadTargetFile != null) {
                    mUploadTargetFile.delete();
                    mUploadTargetFile = null;
                }
            }
        }
                .setTitle(R.string.app_name)
                .setMessage(R.string.confirm_close_dialog)
                .create()
                .show();
    }

    private static void showNotification(NotificationType type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        boolean doNotification = preferences.getBoolean(
                App.getContext().getString(R.string.key_pref_notification),
                true
        );
        if (!doNotification) return;


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

        if (mNotificationBuilder == null) {
            // create notification
            mNotificationBuilder =
                    new NotificationCompat.Builder(App.getContext())
                            .setSmallIcon(R.drawable.ic_sent_24)
                            .setColor(App.getContext().getResources().getColor(android.R.color.white))
                            .setAutoCancel(true)
                            .setContentTitle(App.getContext().getString(R.string.app_name));
        }

        mNotificationBuilder
                .setContentText(App.getContext().getString(message))
                .setProgress(0, 0, false);

        if (type == NotificationType.UPLOAD_COMPLETE) {
            Intent resultIntent = MainActivity.getIntent(App.getContext());
            resultIntent.putExtra(MainActivity.KEY_FRAGMENT_TYPE, PictureDataManager.Type.SENT.name());

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getContext());
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mNotificationBuilder.setContentIntent(resultPendingIntent);
        }

        if (mNotificationManager == null)
            mNotificationManager =
                    (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(GcmBroadcastReceiver.GCM_NOTIFICATION_ID_SENT, mNotificationBuilder.build());
    }


    private void showNowUploadingDialog() {

        View rootView = LayoutInflater.from(this).inflate(R.layout.dialog_now_uploading, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(rootView)
                .setCancelable(false);
        final Dialog dialog = builder.create();

        rootView.findViewById(R.id.btn_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}


