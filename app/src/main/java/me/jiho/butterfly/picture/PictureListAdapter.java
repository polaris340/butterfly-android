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
import me.jiho.butterfly.view.PictureListImageView;

/**
 * Created by jiho on 1/14/15.
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.PictureListViewHolder>
        implements NetworkRecyclerViewAdapter<Picture>, PictureDataObserver {

    private PictureDataManager.Type type;
    private PictureListFragment fragment;

    public PictureListAdapter(PictureListFragment fragment, PictureDataManager.Type type) {
        this.fragment = fragment;
        this.type = type;
    }

    @Override
    public void loadMore(boolean refresh) {

    }
    @Override
    public PictureListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.listitem_picture, parent, false);
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


    public class PictureListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView titleView;
        private PictureListImageView mainImageView;
        private Button userNameButton;
        private Button likeButton;
        private Button showFullImageButton;

        private Picture pictureData;

        public PictureListViewHolder(View rootView) {
            super(rootView);
            titleView = (TextView) rootView.findViewById(R.id.picturelist_tv_title);
            mainImageView = (PictureListImageView) rootView.findViewById(R.id.picturelist_main_image);
            userNameButton = (Button) rootView.findViewById(R.id.picturelist_btn_uploader);
            likeButton = (Button) rootView.findViewById(R.id.picturelist_btn_like);
            showFullImageButton = (Button) rootView.findViewById(R.id.picturelist_btn_show_image);

            userNameButton.setOnClickListener(this);
            likeButton.setOnClickListener(this);
            mainImageView.setOnClickListener(this);
            showFullImageButton.setOnClickListener(this);
        }

        public void setPictureData(Picture pictureData) {
            this.pictureData = pictureData;
            String title = pictureData.getTitle();
            if (title == null || title.equals("null")) {
                title = App.getContext().getString(R.string.label_untitled);
            }
            this.titleView.setText(title);
            this.likeButton.setText(Integer.toString(pictureData.getLikeCount()));
            this.userNameButton.setText(pictureData.getUploaderName());
            this.mainImageView.setImageRatio(pictureData.getImageRatio());
            this.mainImageView.setBackgroundColor(pictureData.getColor());
            if (pictureData.getImageRatio() > 1) {
                this.showFullImageButton.setVisibility(View.VISIBLE);
                this.mainImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                this.showFullImageButton.setVisibility(View.GONE);
                this.mainImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            int likeButtonIcon;
            if (pictureData.getIsLiked()) {
                likeButtonIcon = R.drawable.heart_active_18;
            } else {
                likeButtonIcon = R.drawable.heart_disabled_18;
            }

            this.likeButton.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    likeButtonIcon,
                    0
            );


            Glide.with(App.getContext())
                    .load(pictureData.getPictureUrl())
                            //.placeholder(R.drawable.loading_spinner)
                    .crossFade()
                    .into(this.mainImageView);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.picturelist_btn_like:
                    ((Button) v).setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.heart_active_18,
                            0
                    );
                    break;
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

            }
        }
    }
}
