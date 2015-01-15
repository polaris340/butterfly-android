package me.jiho.butterfly.picture;

/**
 * Created by jiho on 1/14/15.
 */
public interface PictureDataObserver {
    public void update();
    public void update(long pictureId);
}
