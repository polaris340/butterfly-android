package co.bttrfly.picture;

/**
 * Created by jiho on 1/14/15.
 */
public interface PictureDataObserver {
    public void update();
    public void update(long pictureId);
    public void addItems(int startPosition, int itemCount);
    public void removeItem(int position);
}
