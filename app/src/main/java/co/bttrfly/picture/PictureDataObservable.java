package co.bttrfly.picture;

/**
 * Created by jiho on 1/14/15.
 */
public interface PictureDataObservable {
    public enum Type {
        SENT,
        RECEIVED;

        public String getKey() {
            return name().toLowerCase();
        }
    };

    public void addObserver(Type type, PictureDataObserver observer);
    public void removeObserver(Type type, PictureDataObserver observer);
    public void update(Type type);
    public void update(Type type, long pictureId);
    public void update(long pictureId);
    public void update();
    public void addItems(Type type, int startPosition, int itemCount);
    public void removeItem(Type type, int position);



}
