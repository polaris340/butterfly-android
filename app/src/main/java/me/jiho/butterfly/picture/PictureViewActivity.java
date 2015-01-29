package me.jiho.butterfly.picture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import me.jiho.butterfly.R;

public class PictureViewActivity extends ActionBarActivity {


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    PictureDataManager.Type type;

    private PictureViewFragmentPagerAdapter mFragmentPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        //getSupportActionBar().hide();

        Intent intent = getIntent();
        type = PictureDataManager.Type.valueOf(intent.getStringExtra(PictureDataManager.KEY_TYPE));
        int position = intent.getIntExtra(PictureDataManager.KEY_POSITION, 0);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFragmentPagerAdapter = new PictureViewFragmentPagerAdapter(getSupportFragmentManager(), type);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(position);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_view, menu);
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
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PictureDataManager.KEY_POSITION, mViewPager.getCurrentItem());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFragmentPagerAdapter.addToObservable();
    }

    @Override
    protected void onPause() {
        mFragmentPagerAdapter.removeFromObservable();
        super.onPause();
    }
}
