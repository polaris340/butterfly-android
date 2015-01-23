package me.jiho.butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;

import me.jiho.butterfly.auth.Auth;
import me.jiho.butterfly.auth.AuthActivity;
import me.jiho.butterfly.picture.PictureUploadDialogFragment;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Auth.getInstance().isLogin()) {
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

        getSupportActionBar().hide();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);


        // set listener
        FloatingActionButton takePictureButton = (FloatingActionButton) findViewById(R.id.btn_take_picture);
        takePictureButton.setOnClickListener(this);

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

}
