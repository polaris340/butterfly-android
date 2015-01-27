package me.jiho.butterfly.picture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.concurrent.Callable;

import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/13/15.
 */
public class PictureListFragment extends Fragment
    implements View.OnClickListener {

    public static final String KEY_TYPE = "type";
    public static final int REQUEST_CODE_PICTURE_VIEW = 16;


    private PictureDataManager.Type type;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private PictureListAdapter adapter;
    private ImageView fragmentHeader;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static PictureListFragment newInstance(PictureDataManager.Type type) {
        Bundle args = new Bundle();
        args.putString(KEY_TYPE, type.name());
        PictureListFragment instance = new PictureListFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        type = PictureDataManager.Type.valueOf(args.getString(KEY_TYPE));
        View rootView = inflater.inflate(R.layout.fragment_picture_list, null);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.picture_recycler_view);
        adapter = new PictureListAdapter(this, type);
        PictureDataManager.getInstance().addObserver(type, adapter);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.picturelist_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(adapter);

        adapter.setOnPreLoading(new Callable() {
            @Override
            public Object call() throws Exception {
                swipeRefreshLayout.setRefreshing(true);
                return null;
            }
        });
        adapter.setOnLoadingComplete(new Callable() {
            @Override
            public Object call() throws Exception {
                swipeRefreshLayout.setRefreshing(false);
                return null;
            }
        });

        fragmentHeader = (ImageView) rootView.findViewById(R.id.picturelist_fragment_header);
        fragmentHeader.setOnClickListener(this);
        switch (type) {
            case SENT:
                fragmentHeader.setImageResource(R.drawable.ic_sent_24);
                break;
            case RECEIVED:
                fragmentHeader.setImageResource(R.drawable.ic_received_24);
                break;
        }

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                float newY = fragmentHeader.getTranslationY()-(dy/2);
                if (newY > 0) newY = 0;
                else if (newY < (-fragmentHeader.getMeasuredHeight())) {
                    newY = -fragmentHeader.getMeasuredHeight();
                }
                fragmentHeader.setTranslationY(newY);

                if (layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount()-1) {
                    adapter.loadMore();
                }
            }
        });



        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.picturelist_fragment_header:
                recyclerView.smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICTURE_VIEW) {
                int position = data.getIntExtra(PictureDataManager.KEY_POSITION,0);
                recyclerView.scrollToPosition(position);
            }
        }
    }
}
