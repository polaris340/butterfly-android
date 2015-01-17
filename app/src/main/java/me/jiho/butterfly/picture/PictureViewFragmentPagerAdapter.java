package me.jiho.butterfly.picture;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jiho on 1/15/15.
 */
public class PictureViewFragmentPagerAdapter extends FragmentPagerAdapter
    implements PictureDataObserver {
    private PictureDataManager.Type type;

    public PictureViewFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        throw new UnsupportedOperationException("Constructor 'PictureViewFragmentPagerAdapter(FragmentManager)' not supported. Please use 'PictureViewFragmentPagerAdpater(FragmentManager, PictureDataManager.Type)' instead.");
    }

    public PictureViewFragmentPagerAdapter(FragmentManager fm, PictureDataManager.Type type) {
        super(fm);
        this.type = type;
        PictureDataManager
                .getInstance()
                .addObserver(type, this);
    }


    @Override
    public Fragment getItem(int position) {
        return PictureViewFragment
                .newInstance(
                        type,
                        position
                );
    }

    @Override
    public int getCount() {
        return PictureDataManager
                .getInstance()
                .getPictureIdList(type)
                .size();
    }

    @Override
    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public void update(long pictureId) {
        notifyDataSetChanged();
    }
}
