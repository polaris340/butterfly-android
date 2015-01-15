package me.jiho.butterfly.picture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/13/15.
 */
public class PictureListFragment extends Fragment {

    public static final String KEY_TYPE = "type";



    private PictureDataManager.Type type;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private PictureListAdapter adapter;

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
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PictureListAdapter(type);
        PictureDataManager.getInstance().addObserver(type, adapter);
        recyclerView.setAdapter(adapter);

        ImageView fragmentHeader = (ImageView) rootView.findViewById(R.id.picturelist_fragment_header);
        switch (type) {
            case SENT:
                fragmentHeader.setImageResource(R.drawable.ic_sent_24);
                break;
            case RECEIVED:
                fragmentHeader.setImageResource(R.drawable.ic_received_24);
                break;
        }

        return rootView;
    }
}
