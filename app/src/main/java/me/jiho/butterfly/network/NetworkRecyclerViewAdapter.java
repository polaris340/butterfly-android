package me.jiho.butterfly.network;

import java.util.ArrayList;

/**
 * Created by jiho on 1/14/15.
 */
public interface NetworkRecyclerViewAdapter<T> {

    public long getItemId(int position);
    public T getItem(int position);
    public ArrayList<Long> getIdList();
    public int getItemPosition(long itemId);

}
