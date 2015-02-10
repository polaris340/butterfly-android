package me.jiho.butterfly.picture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.jiho.butterfly.App;
import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.util.DialogUtil;
import me.jiho.butterfly.util.MessageUtil;
import me.jiho.butterfly.view.HideableViewWrapper;
import me.jiho.butterfly.view.PictureLikeButton;
import me.jiho.butterfly.view.PinchZoomImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jiho on 1/15/15.
 */
public class PictureViewFragment extends Fragment implements View.OnClickListener {

    private HideableViewWrapper header;
    private HideableViewWrapper footer;
    private Picture pictureData;


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

        PinchZoomImageView mainImageView = (PinchZoomImageView) rootView.findViewById(R.id.pictureview_main_image);
        PictureDataManager.Type type = PictureDataManager.Type.valueOf(getArguments().getString(PictureDataManager.KEY_TYPE));
        int position = getArguments().getInt(PictureDataManager.KEY_POSITION);
        PictureDataManager manager = PictureDataManager.getInstance();
        pictureData = manager
                .get(
                        manager.getPictureIdList(type).get(position)
                );
        //mainImageView.setBackgroundColor(pictureData.getColor());
        Glide.with(this)
                .load(pictureData.getPictureUrl())
                .crossFade()
                .into(mainImageView);

        TextView titleView = (TextView) rootView.findViewById(R.id.pictureview_tv_title);
        String title = pictureData.getTitle();
        if (title == null || title.equals("null")) {
            titleView.setTextColor(getResources().getColor(R.color.white_70));
            titleView.setText(R.string.label_untitled);
        } else {
            titleView.setText(title);
        }

        View countryButtonWrapper = rootView.findViewById(R.id.pictureview_ll_country_button_wrap);
        Button sendCountButton = (Button) rootView.findViewById(R.id.pictureview_btn_send_count);
        if (type == PictureDataManager.Type.RECEIVED) {
            sendCountButton.setVisibility(View.GONE);
            Button countryButton = (Button) rootView.findViewById(R.id.pictureview_btn_country_name);
            String countryName = pictureData.getCountryName();
            if (countryName == null || countryName.equals("null") || countryName.equals("")) {
                countryButton.setText(getString(R.string.label_unknown));
                countryButton.setEnabled(false);
            } else {
                countryButton.setText(countryName);
            }
            countryButton.setOnClickListener(this);
        } else {
            countryButtonWrapper.setVisibility(View.GONE);
            sendCountButton.setText(pictureData.getSendCountString());
            sendCountButton.setOnClickListener(this);
        }



        PictureLikeButton likeButton = (PictureLikeButton) rootView.findViewById(R.id.pictureview_btn_like);
        likeButton.setPictureId(pictureData.getId());

        header = new HideableViewWrapper(rootView.findViewById(R.id.pictureview_rl_header));
        footer = new HideableViewWrapper(rootView.findViewById(R.id.pictureview_rl_footer));

        // set PictureMenuButton
        PictureMenuToggleButton.Builder builder = new PictureMenuToggleButton.Builder(getActivity())
                .setLayout(R.layout.btn_picture_menu)
                .setMenuToggleButtonId(R.id.picturemenu_btn_menu)
                .addButton(R.id.picturemenu_btn_delete)
                .addButton(R.id.picturemenu_btn_save);
        PictureMenuToggleButton pictureMenuToggleButton = builder.create();
        pictureMenuToggleButton.getMenuToggleButton().setColor(
                getResources().getColor(R.color.white_70)
        );
        pictureMenuToggleButton.setPictureId(pictureData.getId());
        ((ViewGroup) rootView.findViewById(R.id.pictureview_tb_menu))
                .addView(pictureMenuToggleButton.getRootView());


        mainImageView
                .getPhotoViewAttacher()
                .setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float v, float v2) {
                        if (header.isShown()) {
                            header.hide();
                            footer.hide();
                        } else {
                            header.show();
                            footer.show();
                        }
                    }
                });


        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pictureview_btn_send_count:
                MessageUtil.showMessage(
                        String.format(App.getContext().getString(R.string.message_send_count),
                                pictureData.getSendCount())
                );
                break;
            case R.id.pictureview_btn_country_name:
                DialogUtil.getMapDialog(
                        getActivity(),
                        pictureData.getLatitude(),
                        pictureData.getLongitude()
                ).show();
                break;
        }
    }
}
