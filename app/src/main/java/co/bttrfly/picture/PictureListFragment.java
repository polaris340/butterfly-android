package co.bttrfly.picture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import java.util.concurrent.Callable;

import co.bttrfly.R;
import co.bttrfly.auth.Auth;
import co.bttrfly.auth.LoginStateChangeObserver;
import co.bttrfly.view.FadeHideableViewWrapper;
import co.jiho.animatedtogglebutton.ListGridToggleButton;

/**
 * Created by jiho on 1/13/15.
 */
public class PictureListFragment extends Fragment
        implements View.OnClickListener, View.OnLongClickListener, LoginStateChangeObserver, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "FRAGMENT_PICTURE_LIST";

    public static final String KEY_TYPE = "type";
    public static final int REQUEST_CODE_PICTURE_VIEW = 16;

    private PictureDataManager.Type type;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private PictureListAdapter adapter;
    private View fragmentHeader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListGridToggleButton layoutToggleButton;

    private Callable onPreLoading;
    private Callable onLoadingComplete;

    private FadeHideableViewWrapper emptyLabel;

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
        if (type == PictureDataObservable.Type.DISCOVER) {
            PictureDataManager
                    .getInstance()
                    .loadMore(
                            PictureDataObservable.Type.DISCOVER,
                            true,
                            null,
                            new Callable() {
                                @Override
                                public Object call() throws Exception {
                                    loadedAfterInitialData();
                                    return null;
                                }
                            });

        } else {
            Auth.getInstance().addLoginStateChangeObserver(this);
        }

        linearLayoutManager = new LinearLayoutManager(getActivity());
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.picturelist_refresh_layout);
        swipeRefreshLayout.setRefreshing(true);


        ImageButton fragmentHeaderIcon = (ImageButton) rootView.findViewById(R.id.picturelist_list_icon);
        fragmentHeaderIcon.setOnClickListener(this);
        fragmentHeaderIcon.setOnLongClickListener(this);
        switch (type) {
            case SENT:
                fragmentHeaderIcon.setImageResource(R.drawable.ic_sent_24);
                break;
            case RECEIVED:
                fragmentHeaderIcon.setImageResource(R.drawable.ic_received_24);
                break;
            case DISCOVER:
                fragmentHeaderIcon.setImageResource(R.drawable.ic_settings_18);
                break;
        }



        onPreLoading = new Callable() {
            @Override
            public Object call() throws Exception {
                swipeRefreshLayout.setRefreshing(true);
                emptyLabel.hide();
                return null;
            }
        };
        onLoadingComplete = new Callable() {
            @Override
            public Object call() throws Exception {
                swipeRefreshLayout.setRefreshing(false);
                if (adapter.getItemCount() == 0) {
                    emptyLabel.show();
                }
                return null;
            }
        };


        fragmentHeader = rootView.findViewById(R.id.picturelist_fragment_header);
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

                if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount()-1) {
                    PictureDataManager.getInstance().loadMore(
                            type,
                            false,
                            onPreLoading,
                            onLoadingComplete);
                }
            }
        });


        // empty label
        View emptyLabelView = rootView.findViewById(R.id.picturelist_message_empty);
        emptyLabel = new FadeHideableViewWrapper(emptyLabelView);



        // toggle layout
        layoutToggleButton = (ListGridToggleButton) rootView.findViewById(R.id.picturelist_tb_layout);
        layoutToggleButton.setVisibility(View.GONE);
        layoutToggleButton.setColor(getResources().getColor(R.color.primary));



        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.picturelist_list_icon:
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

    @Override
    public void onLoginStateChanged(Auth.LoginState loginState) {
        switch (loginState) {
            case LOGGED_IN:
                loadedAfterInitialData();
                break;
        }
    }

    @Override
    public void onRefresh() {
        PictureDataManager.getInstance().loadMore(type, true, onPreLoading, onLoadingComplete);
    }

    private void loadedAfterInitialData() {
        // add adapter after logged in
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            emptyLabel.show();
        }

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                LinearLayoutManager currentLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (currentLayoutManager.findLastVisibleItemPosition()
                        == adapter.getItemCount() - 1) {
                    PictureDataManager.getInstance().loadMore(type, false,
                            onPreLoading,// TODO : 그냥 null로 할까..
                            onLoadingComplete);
                }

            }
        });

        new FadeHideableViewWrapper(layoutToggleButton).show();
        layoutToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int firstVisibleItemPosition;
                if (isChecked) {
                    firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                    adapter.setLayout(PictureListAdapter.LAYOUT_LIST);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    recyclerView.scrollToPosition(firstVisibleItemPosition);
                } else {
                    firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    adapter.setLayout(PictureListAdapter.LAYOUT_GRID);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.scrollToPosition(firstVisibleItemPosition);
                }
            }
        });


        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        /*
        switch (type) {
            case SENT:
            case RECEIVED:
                Intent intent = new Intent(getActivity(), DiscoverActivity.class);
                startActivity(intent);
                break;
            case DISCOVER:
                getActivity().finish();
                break;
        }
        //*/
        return false;
    }

    public String getScreenName() {
        return TAG + "|" + type.name();
    }
}
