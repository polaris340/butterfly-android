package co.bttrfly.picture;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jiho on 1/15/15.
 */
public class PictureViewFragmentPagerAdapter extends FragmentPagerAdapter
    implements PictureDataObserver {
    private PictureDataManager.Type mType;
    private Activity mActivity;

    public PictureViewFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        throw new UnsupportedOperationException("Constructor 'PictureViewFragmentPagerAdapter(FragmentManager)' not supported. Please use 'PictureViewFragmentPagerAdpater(FragmentManager, PictureDataManager.Type)' instead.");
    }

    public PictureViewFragmentPagerAdapter(Activity activity, FragmentManager fm, PictureDataManager.Type type) {
        super(fm);
        this.mActivity = activity;
        this.mType = type;
    }


    @Override
    public Fragment getItem(int position) {
        return PictureViewFragment
                .newInstance(
                        mType,
                        position
                );
    }

    @Override
    public int getCount() {
        return PictureDataManager
                .getInstance()
                .getPictureIdList(mType)
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

    @Override
    public void addItems(int startPosition, int itemCount) {
        notifyDataSetChanged();
    }

    @Override
    public void removeItem(int position) {

        notifyDataSetChanged();
        mActivity.onBackPressed();
    }

    public void addToObservable() {
        PictureDataManager
                .getInstance()
                .addObserver(mType, this);
    }

    public void removeFromObservable() {
        PictureDataManager
                .getInstance()
                .removeObserver(mType, this);
    }

}
