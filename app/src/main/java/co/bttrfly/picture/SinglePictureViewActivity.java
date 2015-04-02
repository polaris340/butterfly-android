package co.bttrfly.picture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import co.bttrfly.BaseActivity;
import co.bttrfly.MainActivity;
import co.bttrfly.R;
import co.bttrfly.statics.Constants;

public class SinglePictureViewActivity extends BaseActivity implements PictureDataObserver {


    public static Intent getIntent(Context context, long pictureId) {
        Intent intent = new Intent(context, SinglePictureViewActivity.class);
        intent.putExtra(Constants.Keys.PICTURE_ID, pictureId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        long pictureId = intent.getLongExtra(Constants.Keys.PICTURE_ID, 0);
        if (pictureId <= 0) {
            startActivity(MainActivity.getIntent(this));
        } else {
            setContentView(R.layout.activity_single_picture_view);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, PictureViewFragment.newInstance(pictureId))
                        .commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PictureDataManager pictureDataManager = PictureDataManager.getInstance();
        pictureDataManager.addObserver(PictureDataObservable.Type.SENT, this);
    }

    @Override
    protected void onPause() {
        PictureDataManager pictureDataManager = PictureDataManager.getInstance();
        pictureDataManager.removeObserver(PictureDataObservable.Type.SENT, this);
        super.onPause();
    }

    @Override
    public void update() {

    }

    @Override
    public void update(long pictureId) {

    }

    @Override
    public void addItems(int startPosition, int itemCount) {

    }

    @Override
    public void removeItem(int position) {
        startActivity(MainActivity.getIntent(this));

    }
}
