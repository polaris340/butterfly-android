package co.bttrfly.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by jiho on 3/4/15.
 */
public class PictureListRecyclerView extends RecyclerView {
    private OnLayoutManagerChangeListener mOnLayoutManagerChangeListener;

    public PictureListRecyclerView(Context context) {
        super(context);
    }

    public PictureListRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureListRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        LayoutManager prevLayoutManager = getLayoutManager();
        super.setLayoutManager(layout);

        if (prevLayoutManager != null
                && !prevLayoutManager.equals(layout)
                && mOnLayoutManagerChangeListener != null) {
            mOnLayoutManagerChangeListener.onLayoutChange(this, prevLayoutManager, layout);
        }
    }

    public void setOnLayoutManagerChangeListener(OnLayoutManagerChangeListener listener) {
        this.mOnLayoutManagerChangeListener = listener;
    }


    public static interface OnLayoutManagerChangeListener {
        public void onLayoutChange(RecyclerView recyclerView, LayoutManager oldLayoutManager, LayoutManager newLayoutManager);
    }
}
