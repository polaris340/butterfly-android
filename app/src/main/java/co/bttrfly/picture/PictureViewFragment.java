package co.bttrfly.picture;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import co.bttrfly.MainActivity;
import co.bttrfly.R;
import co.bttrfly.db.Picture;
import co.bttrfly.network.DefaultErrorListener;
import co.bttrfly.network.VolleyRequestQueue;
import co.bttrfly.statics.Constants;
import co.bttrfly.util.DialogUtil;
import co.bttrfly.util.MessageUtil;
import co.bttrfly.view.FadeHideableViewWrapper;
import co.bttrfly.view.PictureLikeButton;
import co.bttrfly.view.PinchZoomImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jiho on 1/15/15.
 */
public class PictureViewFragment extends Fragment implements View.OnClickListener {
    private static final long HEADER_FOOTER_HIDE_DELAY = 1000;
    public static final String URL_GET_PICTURE = Constants.URLs.API_URL + "picture";


    private FadeHideableViewWrapper header;
    private FadeHideableViewWrapper footer;

    private Picture pictureData;

    private PinchZoomImageView mainImageView;
    private TextView titleView;
    private TextView uploaderName;
    private View countryButtonWrapper;
    private PictureLikeButton likeButton;
    private Button sendCountButton;
    private View rootView;
    private PictureMenuToggleButton pictureMenuToggleButton;

    private PictureDataObservable.Type type;


    public static PictureViewFragment newInstance(long pictureId) {
        PictureViewFragment fragment = new PictureViewFragment();

        Bundle args = new Bundle();
        args.putLong(Constants.Keys.PICTURE_ID, pictureId);
        fragment.setArguments(args);

        return fragment;
    }

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
        rootView = inflater.inflate(R.layout.fragment_picture_view, container, false);

        mainImageView = (PinchZoomImageView) rootView.findViewById(R.id.pictureview_main_image);



        Bundle args = getArguments();

        Picture picture = null;

        PictureDataManager manager = PictureDataManager.getInstance();
        if (args.containsKey(Constants.Keys.PICTURE_ID)) {
            long pictureId = args.getLong(Constants.Keys.PICTURE_ID);

            final Dialog dialog = DialogUtil.getDefaultProgressDialog(getActivity());
            Request request = new JsonObjectRequest(
                    Request.Method.GET,
                    URL_GET_PICTURE + "/" + pictureId,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Picture picture = null;
                            try {
                                picture = Picture.fromJson(response.getString(Constants.Keys.DATA));
                                setPictureData(picture);
                            } catch (JSONException e) {
                                MessageUtil.showDefaultErrorMessage();
                                e.printStackTrace();
                                getActivity().finish();
                            }

                            dialog.hide();
                        }
                    },
                    new DefaultErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            super.onErrorResponse(error);
                            getActivity().finish();
                        }
                    }

            );
            dialog.show();
            VolleyRequestQueue.add(request);
            //load data from server

        } else {

            try {
                type = PictureDataManager.Type.valueOf(getArguments().getString(PictureDataManager.KEY_TYPE));
                int position = getArguments().getInt(PictureDataManager.KEY_POSITION);
                picture = manager
                        .get(
                                manager.getPictureIdList(type).get(position)
                        );
            } catch (ArrayIndexOutOfBoundsException e) {
                Intent intent = MainActivity.getIntent(getActivity());
                getActivity().startActivity(intent);
                return rootView;
            }

        }

        titleView = (TextView) rootView.findViewById(R.id.pictureview_tv_title);



        ((TextView) rootView.findViewById(R.id.pictureview_label_by))
                .setTextColor(getResources().getColor(R.color.white_12));
        uploaderName = (TextView) rootView.findViewById(R.id.pictureview_tv_uploader);
        uploaderName.setTextColor(getResources().getColor(R.color.white_30));


        countryButtonWrapper = rootView.findViewById(R.id.pictureview_ll_country_button_wrap);
        sendCountButton = (Button) rootView.findViewById(R.id.pictureview_btn_send_count);
        sendCountButton.setTextColor(getResources().getColor(R.color.white_100));



        likeButton = (PictureLikeButton) rootView.findViewById(R.id.pictureview_btn_like);
        likeButton.setTextColor(getResources().getColor(R.color.white_100));

        View headerView = rootView.findViewById(R.id.pictureview_rl_header);
        View footerView = rootView.findViewById(R.id.pictureview_rl_footer);
        headerView.setBackgroundColor(getResources().getColor(R.color.black_54));
        footerView.setBackgroundColor(getResources().getColor(R.color.black_54));
        header = new FadeHideableViewWrapper(headerView);
        footer = new FadeHideableViewWrapper(footerView);
        header.setDuration(Constants.Integers.ANIMATION_DURATION_LONG);
        footer.setDuration(Constants.Integers.ANIMATION_DURATION_LONG);
        //header.hide(HEADER_FOOTER_HIDE_DELAY);
        //footer.hide(HEADER_FOOTER_HIDE_DELAY);

        TextView fromLabel = (TextView) rootView.findViewById(R.id.pictureview_label_from);
        fromLabel.setTextColor(getResources().getColor(R.color.white_70));

        // set PictureMenuButton

        pictureMenuToggleButton = PictureMenuToggleButton.getDefault(getActivity());
        pictureMenuToggleButton.getMenuToggleButton().setColor(
                getResources().getColor(R.color.white_70)
        );


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


        if (picture != null) {
            setPictureData(picture);
        }

        return rootView;
    }

    public void setPictureData(Picture picture) {
        PictureDataManager.getInstance().put(picture);
        this.pictureData = picture;

        ImageLoader.getInstance().displayImage(pictureData.getPictureUrl(), mainImageView);

        String title = pictureData.getTitle();
        if (title == null || title.equals("null")) {
            titleView.setTextColor(getResources().getColor(R.color.white_70));
            titleView.setText(R.string.label_untitled);
        } else {
            titleView.setText(title);
            titleView.setTextColor(getResources().getColor(R.color.white_100));
        }

        uploaderName.setText(pictureData.getUploaderName());

        likeButton.setPictureId(pictureData.getId());

        if (type == null) {
            if (pictureData.getIsMine()) {
                type = PictureDataObservable.Type.SENT;
            } else {
                type = PictureDataObservable.Type.RECEIVED;
            }
        }

        if (type == PictureDataManager.Type.SENT) {
            countryButtonWrapper.setVisibility(View.GONE);
            sendCountButton.setText(pictureData.getSendCountString());
            sendCountButton.setTextColor(getResources().getColor(R.color.white_100));
            sendCountButton.setTag(pictureData);
        } else {
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
        }

        if (type == PictureDataManager.Type.SENT) {
            countryButtonWrapper.setVisibility(View.GONE);
            sendCountButton.setText(pictureData.getSendCountString());
            sendCountButton.setTextColor(getResources().getColor(R.color.white_100));
            sendCountButton.setTag(pictureData);
        } else {
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
        }

        pictureMenuToggleButton.setPictureId(pictureData.getId());
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
