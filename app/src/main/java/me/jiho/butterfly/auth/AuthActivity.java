package me.jiho.butterfly.auth;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import me.jiho.butterfly.R;

public class AuthActivity extends ActionBarActivity {
    public static final int POSITION_SIGN_IN = 0;
    public static final int POSITION_SIGN_UP = 1;
    private static final int PRESS_BACK_INTERVAL = 2000;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    FragmentPagerAdapter mFragmentPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    long lastBackPressedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportActionBar().hide();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFragmentPagerAdapter = new AuthFragmentPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auth, menu);
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

    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 1) {
            setCurrentItem(0);
        } else {
            //long pressedTime = System.currentTimeMillis();
            //if (pressedTime - lastBackPressedTime < PRESS_BACK_INTERVAL) {
                super.onBackPressed();
            //} else {
            //    MessageUtil.showMessage(R.string.message_press_back_again);
            //}
            //lastBackPressedTime = pressedTime;
        }
    }

    public void changePage(int position) {
        mViewPager.setCurrentItem(position);
    }

}
