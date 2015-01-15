package me.jiho.butterfly.picture;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.jiho.butterfly.R;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.network.NetworkRecyclerViewAdapter;

/**
 * Created by jiho on 1/14/15.
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListViewHolder>
        implements NetworkRecyclerViewAdapter<Picture>, PictureDataObserver {

    private PictureDataManager.Type type;


    public PictureListAdapter(PictureDataManager.Type type) {
        this.type = type;
    }

    @Override
    public void loadMore(boolean refresh) {

    }
    @Override
    public PictureListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_picture, parent, false);
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
}
