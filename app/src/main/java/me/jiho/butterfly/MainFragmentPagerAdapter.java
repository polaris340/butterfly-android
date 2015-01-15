package me.jiho.butterfly;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.jiho.butterfly.picture.PictureDataManager;
import me.jiho.butterfly.picture.PictureListFragment;

/**
 * Created by jiho on 1/13/15.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PictureListFragment.newInstance(PictureDataManager.Type.RECEIVED);
            case 1:
                return PictureListFragment.newInstance(PictureDataManager.Type.SENT);

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
