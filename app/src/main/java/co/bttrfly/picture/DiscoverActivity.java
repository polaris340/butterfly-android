package co.bttrfly.picture;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import co.bttrfly.R;

public class DiscoverActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_discover);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, PictureListFragment.newInstance(PictureDataObservable.Type.DISCOVER))
                    .commit();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        PictureDataManager.getInstance().clear(PictureDataObservable.Type.DISCOVER);
    }
}
