package co.bttrfly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import co.bttrfly.auth.Auth;
import co.bttrfly.auth.AuthActivity;
import co.bttrfly.picture.PictureDataManager;
import co.bttrfly.picture.PictureListFragment;
import co.bttrfly.picture.PictureUploadDialogFragment;
import co.bttrfly.statics.Constants;
import co.bttrfly.util.DialogUtil;


public class MainActivity extends BaseActivity
        implements View.OnClickListener, View.OnLongClickListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "MainActivity";
    public static final String KEY_FRAGMENT_TYPE = "fragment_type";
    public static final int SELECT_PICTURE = 128;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!Auth.getInstance().hasAccessToken()) {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                            |Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);
            return;
        }


        try {
            Auth.getInstance().loginWithAccessToken();
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
        context = getApplicationContext();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }

        // handle share intent
        String action = intent.getAction();
        if (action != null && intent.getAction().equals(Intent.ACTION_SEND)) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                showUploadDialogWithImageUri(imageUri);
            }
        }


        mTracker = ((App) App.getContext()).getTracker(App.TrackerName.APP_TRACKER);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_picture:
                DialogFragment uploadDialogFragment = new PictureUploadDialogFragment();
                uploadDialogFragment.show(getSupportFragmentManager(), PictureUploadDialogFragment.TAG);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImage = data.getData();
                showUploadDialogWithImageUri(selectedImage);
            }
        }
    }

    private void showUploadDialogWithImageUri(Uri uri) {
        PictureUploadDialogFragment uploadDialogFragment = new PictureUploadDialogFragment();
        uploadDialogFragment.setUploadTargetFile(uri);
        uploadDialogFragment.show(getSupportFragmentManager(), PictureUploadDialogFragment.TAG);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START);
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
}


