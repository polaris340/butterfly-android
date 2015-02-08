package me.jiho.butterfly.picture;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import me.jiho.butterfly.App;
import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.network.DefaultErrorListener;
import me.jiho.butterfly.network.NetworkRecyclerViewAdapter;
import me.jiho.butterfly.network.VolleyRequestQueue;
import me.jiho.butterfly.statics.Constants;
import me.jiho.butterfly.util.MessageUtil;
import me.jiho.butterfly.view.PictureLikeButton;
import me.jiho.butterfly.view.PictureListImageView;

/**
 * Created by jiho on 1/14/15.
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.PictureListViewHolder>
        implements NetworkRecyclerViewAdapter<Picture>, PictureDataObserver, SwipeRefreshLayout.OnRefreshListener {
    public static final String URL_GET_PICTURE = Constants.URLs.API_URL + "picture/";

    public static final int LAYOUT_LIST = 0;
    public static final int LAYOUT_GRID = 1;

    private PictureDataManager.Type type;
    private PictureListFragment fragment;
    private boolean end = false;
    private boolean running = false;

    private Callable onPreLoading;
    private Callable onLoadingComplete;

    private int currentLayout = LAYOUT_LIST;


    public PictureListAdapter(PictureListFragment fragment, PictureDataManager.Type type) {
        this.fragment = fragment;
        this.type = type;
    }


    private boolean isSendRequestAvailable(boolean refresh) {
        return !running && (refresh || !end);
    }

    public void loadMore() {
        loadMore(false);
    }

    @Override
    public void loadMore(final boolean refresh) {
        if (!isSendRequestAvailable(refresh)) return;
        if (refresh) end = false;

        String url = URL_GET_PICTURE;
        if (type == PictureDataManager.Type.SENT) {
            url += "1/";
        } else {
            url += "0/";
        }
        if (refresh)
            url += "0";
        else
            url += getLastId();

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            PictureDataManager manager = PictureDataManager.getInstance();
                            if (refresh) {
                                manager.clear(type);
                            }
                            String dataList = response.getString(Constants.Keys.MESSAGE);
                            Picture[] pictures = Picture.fromJsonArray(dataList);
                            int currentItemCount = getItemCount();
                            for (Picture p:pictures) {
                                manager.add(type, p);
                            }
                            if (pictures.length == 0) {
                                end = true;
                            }
                            running = false;
                            if (!refresh)
                                notifyItemRangeInserted(currentItemCount, getItemCount()-1);
                            else
                                notifyDataSetChanged();


                        } catch (JSONException e) {
                            MessageUtil.showDefaultErrorMessage();
                            e.printStackTrace();
                        }
                        running = false;

                        if (onLoadingComplete != null) {
                            try {
                                onLoadingComplete.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        running = false;


                        if (onLoadingComplete != null) {
                            try {
                                onLoadingComplete.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        running = true;
        if (onPreLoading != null) {
            try {
                onPreLoading.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        VolleyRequestQueue.add(request);
    }

    private long getLastId() {
        if (getItemCount() > 0) {
            Picture lastPicture = getItem(getItemCount()-1);
            if (lastPicture.getSendPictureId() > 0) return lastPicture.getSendPictureId();
            else return lastPicture.getId();
        } else {
            return 0;
        }
    }

    public void setOnPreLoading(Callable c) {
        this.onPreLoading = c;
    }
    public void setOnLoadingComplete(Callable c) {
        this.onLoadingComplete = c;
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
    public void onRefresh() {
        refresh();
    }

    public void refresh() {
        loadMore(true);
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
        private Button userNameButton;
        private PictureLikeButton likeButton;
        private Button showFullImageButton;
        private PictureMenuToggleButton menuButton;

        private Picture pictureData;

        public PictureListViewHolder(View rootView) {
            super(rootView);

            mainImageView = (ImageView) rootView.findViewById(R.id.picturelist_main_image);
            mainImageView.setOnClickListener(this);
            if (currentLayout == LAYOUT_LIST) {
                titleView = (TextView) rootView.findViewById(R.id.picturelist_tv_title);
                userNameButton = (Button) rootView.findViewById(R.id.picturelist_btn_uploader);
                likeButton = (PictureLikeButton) rootView.findViewById(R.id.picturelist_btn_like);
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

                ((ViewGroup)rootView.findViewById(R.id.picturelist_tb_menu))
                        .addView(menuButton.getRootView());

                userNameButton.setOnClickListener(this);
                showFullImageButton.setOnClickListener(this);
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

                this.userNameButton.setText(pictureData.getUploaderName());
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

            }
        }


    }
}
