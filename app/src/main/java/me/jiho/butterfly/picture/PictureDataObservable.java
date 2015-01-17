package me.jiho.butterfly.picture;

/**
 * Created by jiho on 1/14/15.
 */
public interface PictureDataObservable {
    public void addObserver(PictureDataManager.Type type, PictureDataObserver observer);
    public void removeObserver(PictureDataManager.Type type, PictureDataObserver observer);
    public void update(PictureDataManager.Type type);
    public void update(PictureDataManager.Type type, long pictureId);
    public void update(long pictureId);
    public void update();
}
