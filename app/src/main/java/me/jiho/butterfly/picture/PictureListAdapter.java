package me.jiho.butterfly.picture;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.jiho.butterfly.App;
import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.network.NetworkRecyclerViewAdapter;
import me.jiho.butterfly.util.DialogUtil;
import me.jiho.butterfly.util.MessageUtil;
import me.jiho.butterfly.view.PictureLikeButton;
import me.jiho.butterfly.view.PictureListImageView;

/**
 * Created by jiho on 1/14/15.
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.PictureListViewHolder>
        implements NetworkRecyclerViewAdapter<Picture>, PictureDataObserver {

    public static final int LAYOUT_LIST = 0;
    public static final int LAYOUT_GRID = 1;

    private PictureDataManager.Type type;
    private PictureListFragment fragment;


    private int currentLayout = LAYOUT_LIST;


    public PictureListAdapter(PictureListFragment fragment, PictureDataManager.Type type) {
        this.fragment = fragment;
        this.type = type;
    }



    @Override
    public PictureListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (currentLayout == LAYOUT_LIST)
            v = inflater.inflate(R.layout.listitem_picture_linear, parent, false);
        else {
            v = inflater.inflate(R.layout.listitem_picture_grid, parent, false);
        }
        return new PictureListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PictureListViewHolder holder, int position) {
        holder.setPictureData(getItem(position));
    }

    @Override
    public int getItemCount() {
        return getIdList().size();
    }


    @Override
    public Picture getItem(int position) {
        return PictureDataManager
                .getInstance()
                .get(getItemId(position));
    }

    @Override
    public ArrayList<Long> getIdList() {
        return PictureDataManager
                .getInstance()
                .getPictureIdList(type);
    }

    @Override
    public int getItemPosition(long itemId) {
        return getIdList().indexOf(itemId);
    }

    @Override
    public long getItemId(int position) {
        return getIdList().get(position);
    }

    @Override
    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public void update(long pictureId) {
        int position = getItemPosition(pictureId);
        if (position >= 0) {
            notifyItemChanged(position);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return currentLayout;
    }

    public void setLayout(int layout) {
        this.currentLayout = layout;
    }

    public class PictureListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView titleView;
        private ImageView mainImageView;
        private Button countryNameButton;
        private PictureLikeButton likeButton;
        private Button showFullImageButton;
        private PictureMenuToggleButton menuButton;
        private View countryButtonWrapper;
        private Button sendCountButton;

        private Picture pictureData;

        public PictureListViewHolder(View rootView) {
            super(rootView);

            mainImageView = (ImageView) rootView.findViewById(R.id.picturelist_main_image);
            mainImageView.setOnClickListener(this);
            if (currentLayout == LAYOUT_LIST) {
                titleView = (TextView) rootView.findViewById(R.id.pictureview_tv_title);
                countryNameButton = (Button) rootView.findViewById(R.id.pictureview_btn_country_name);
                countryButtonWrapper = rootView.findViewById(R.id.pictureview_ll_country_button_wrap);
                sendCountButton = (Button) rootView.findViewById(R.id.pictureview_btn_send_count);
                likeButton = (PictureLikeButton) rootView.findViewById(R.id.pictureview_btn_like);
                showFullImageButton = (Button) rootView.findViewById(R.id.picturelist_btn_show_image);

                PictureMenuToggleButton.Builder builder = new PictureMenuToggleButton.Builder(rootView.getContext());
                builder.setLayout(R.layout.btn_picture_menu)
                        .setMenuToggleButtonId(R.id.picturemenu_btn_menu)
                        .addButton(R.id.picturemenu_btn_save)
                        .addButton(R.id.picturemenu_btn_delete);

                menuButton = builder.create();


                menuButton.getMenuToggleButton().setColor(
                        rootView.getContext().getResources().getColor(R.color.black_54)
                );

                ((ViewGroup)rootView.findViewById(R.id.pictureview_tb_menu))
                        .addView(menuButton.getRootView());

                countryNameButton.setOnClickListener(this);
                showFullImageButton.setOnClickListener(this);
                sendCountButton.setOnClickListener(this);
            }
        }

        public void setPictureData(Picture pictureData) {
            this.pictureData = pictureData;
            this.mainImageView.setBackgroundColor(pictureData.getColor());

            if (currentLayout == LAYOUT_LIST) {
                String title = pictureData.getTitle();
                if (title == null || title.equals("null")) {
                    title = App.getContext().getString(R.string.label_untitled);
                    this.titleView.setTextColor(App.getContext().getResources().getColor(R.color.black_26));
                } else {
                    this.titleView.setTextColor(App.getContext().getResources().getColor(R.color.black_87));
                }
                this.titleView.setText(title);


                this.likeButton.setPictureId(pictureData.getId());

                if (type == PictureDataManager.Type.RECEIVED) {
                    this.countryButtonWrapper.setVisibility(View.VISIBLE);
                    this.sendCountButton.setVisibility(View.GONE);
                    String countryName = pictureData.getCountryName();
                    if (countryName == null || countryName.equals("null") || countryName.equals("")) {
                        countryName = App.getContext().getResources().getString(R.string.label_unknown);
                        this.countryNameButton.setEnabled(false);
                    } else {
                        this.countryNameButton.setEnabled(true);
                    }
                    this.countryNameButton.setText(countryName);
                } else {
                    this.countryButtonWrapper.setVisibility(View.GONE);
                    this.sendCountButton.setVisibility(View.VISIBLE);
                    this.sendCountButton.setText(pictureData.getSendCountString());
                }
                ((PictureListImageView) this.mainImageView).setImageRatio(pictureData.getImageRatio());
                if (pictureData.getImageRatio() > 1) {
                    this.showFullImageButton.setVisibility(View.VISIBLE);
                    this.mainImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    this.showFullImageButton.setVisibility(View.GONE);
                    this.mainImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }

                menuButton.setChecked(false);
                menuButton.setPictureId(pictureData.getId());
            }

            String pictureUrl;
            if (currentLayout == LAYOUT_LIST) {
                pictureUrl = pictureData.getPictureUrl();
            } else {pictureUrl = pictureData.getThumbnailUrl();

            }
            Glide.with(App.getContext())
                    .load(pictureUrl)
                    .into(this.mainImageView);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.picturelist_btn_show_image:
                case R.id.picturelist_main_image:
                    Intent intent = new Intent(v.getContext(),PictureViewActivity.class);
                    intent.putExtra(PictureDataManager.KEY_TYPE, type.name());
                    intent.putExtra(PictureDataManager.KEY_POSITION, PictureDataManager
                            .getInstance()
                            .getPictureIdList(type)
                            .indexOf(pictureData.getId()));
                    fragment.startActivityForResult(intent, PictureListFragment.REQUEST_CODE_PICTURE_VIEW);
                    break;
                case R.id.pictureview_btn_send_count:
                    MessageUtil.showMessage(
                            String.format(App.getContext().getString(R.string.message_send_count),
                                    pictureData.getSendCount())
                    );
                    break;
                case R.id.pictureview_btn_country_name:
                    DialogUtil.getMapDialog(
                            v.getContext(),
                            pictureData.getLatitude(),
                            pictureData.getLongitude()
                    ).show();
                    break;
            }
        }


    }
}
