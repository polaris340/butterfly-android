package co.bttrfly.picture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import co.bttrfly.R;
import co.bttrfly.db.Picture;
import co.bttrfly.statics.Constants;
import co.bttrfly.util.DialogUtil;
import co.bttrfly.view.FadeHideableViewWrapper;
import co.bttrfly.view.PictureLikeButton;
import co.bttrfly.view.PinchZoomImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jiho on 1/15/15.
 */
public class PictureViewFragment extends Fragment implements View.OnClickListener {
    private static final long HEADER_FOOTER_HIDE_DELAY = 1000;

    private FadeHideableViewWrapper header;
    private FadeHideableViewWrapper footer;
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
        ImageLoader.getInstance().displayImage(pictureData.getPictureUrl(), mainImageView);


        TextView titleView = (TextView) rootView.findViewById(R.id.pictureview_tv_title);
        String title = pictureData.getTitle();
        if (title == null || title.equals("null")) {
            titleView.setTextColor(getResources().getColor(R.color.white_70));
            titleView.setText(R.string.label_untitled);
        } else {
            titleView.setText(title);
            titleView.setTextColor(getResources().getColor(R.color.white_100));
        }

        View countryButtonWrapper = rootView.findViewById(R.id.pictureview_ll_country_button_wrap);
        Button sendCountButton = (Button) rootView.findViewById(R.id.pictureview_btn_send_count);
        sendCountButton.setTextColor(getResources().getColor(R.color.white_100));
        if (type == PictureDataManager.Type.RECEIVED) {
            sendCountButton.setVisibility(View.GONE);
            Button countryButton = (Button) rootView.findViewById(R.id.pictureview_btn_country_name);
            countryButton.setTextColor(getResources().getColor(R.color.white_100));
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
            sendCountButton.setTextColor(getResources().getColor(R.color.white_100));
            sendCountButton.setTag(pictureData);
        }



        PictureLikeButton likeButton = (PictureLikeButton) rootView.findViewById(R.id.pictureview_btn_like);
        likeButton.setPictureId(pictureData.getId());
        likeButton.setTextColor(getResources().getColor(R.color.white_100));

        View headerView = rootView.findViewById(R.id.pictureview_rl_header);
        View footerView = rootView.findViewById(R.id.pictureview_rl_footer);
        headerView.setBackgroundColor(getResources().getColor(R.color.black_54));
        footerView.setBackgroundColor(getResources().getColor(R.color.black_54));
        header = new FadeHideableViewWrapper(headerView);
        footer = new FadeHideableViewWrapper(footerView);
        header.setDuration(Constants.Integers.ANIMATION_DURATION_LONG);
        footer.setDuration(Constants.Integers.ANIMATION_DURATION_LONG);
        header.hide(HEADER_FOOTER_HIDE_DELAY);
        footer.hide(HEADER_FOOTER_HIDE_DELAY);

        TextView fromLabel = (TextView) rootView.findViewById(R.id.pictureview_label_from);
        fromLabel.setTextColor(getResources().getColor(R.color.white_70));

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

        View menuButtonRootView = pictureMenuToggleButton.getRootView();
        ((ViewGroup) rootView.findViewById(R.id.pictureview_tb_menu))
                .addView(menuButtonRootView);


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
