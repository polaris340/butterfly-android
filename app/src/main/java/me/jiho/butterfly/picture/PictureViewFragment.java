package me.jiho.butterfly.picture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;

/**
 * Created by jiho on 1/15/15.
 */
public class PictureViewFragment extends Fragment {


    public static PictureViewFragment newInstance(PictureDataManager.Type type, int position) {
        Bundle args = new Bundle();
        args.putString(PictureDataManager.KEY_TYPE, type.name());
        args.putInt(PictureDataManager.KEY_POSITION, position);
        PictureViewFragment fragment = new PictureViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_picture_view, container, false);

        ImageView mainImageView = (ImageView) rootView.findViewById(R.id.pictureview_main_image);
        PictureDataManager.Type type = PictureDataManager.Type.valueOf(getArguments().getString(PictureDataManager.KEY_TYPE));
        int position = getArguments().getInt(PictureDataManager.KEY_POSITION);
        PictureDataManager manager = PictureDataManager.getInstance();
        Picture pictureData = manager
                .get(
                        manager.getPictureIdList(type).get(position)
                );
        //mainImageView.setBackgroundColor(pictureData.getColor());
        Glide.with(this)
                .load(pictureData.getPictureUrl())
                .crossFade()
                .into(mainImageView);



        return rootView;
    }
}
