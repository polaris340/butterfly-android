package me.jiho.butterfly.picture;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.jiho.butterfly.App;
import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.view.PictureListImageView;

/**
 * Created by jiho on 1/14/15.
 */
public class PictureListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView titleView;
    private PictureListImageView mainImageView;
    private Button userNameButton;
    private Button likeButton;

    private Picture pictureData;

    public PictureListViewHolder(View rootView) {
        super(rootView);
        titleView = (TextView) rootView.findViewById(R.id.picturelist_tv_title);
        mainImageView = (PictureListImageView) rootView.findViewById(R.id.picturelist_main_image);
        userNameButton = (Button) rootView.findViewById(R.id.picturelist_btn_uploader);
        likeButton = (Button) rootView.findViewById(R.id.picturelist_btn_like);

        userNameButton.setOnClickListener(this);
        likeButton.setOnClickListener(this);
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
        int likeButtonIcon = 0;
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
        }
    }
}
