package me.jiho.butterfly.picture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.Callable;

import me.jiho.butterfly.R;
import me.jiho.butterfly.view.FadeHideableViewWrapper;
import me.jiho.butterfly.view.HideableViewWrapper;

public class PictureViewActivity extends ActionBarActivity {
    private static final int LOAD_DATA_OFFSET_COUNT = 3;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    PictureDataManager.Type type;
    HideableViewWrapper progressBarWrapper;

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

        progressBarWrapper = new FadeHideableViewWrapper(findViewById(R.id.pictureview_progress_bar));


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(position);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position > mFragmentPagerAdapter.getCount() - LOAD_DATA_OFFSET_COUNT) {

                    PictureDataManager.getInstance().loadMore(type, false, new Callable() {
                        @Override
                        public Object call() throws Exception {
                            progressBarWrapper.show();
                            return null;
                        }
                    }, new Callable() {
                        @Override
                        public Object call() throws Exception {
                            progressBarWrapper.hide();
                            return null;
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
