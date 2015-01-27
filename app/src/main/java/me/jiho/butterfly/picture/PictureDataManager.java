package me.jiho.butterfly.picture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import me.jiho.butterfly.db.Picture;

/**
 * Created by jiho on 1/14/15.
 */
public class PictureDataManager implements PictureDataObservable {
    private static PictureDataManager instance;
    public static final String KEY_TYPE = "type";
    public static final String KEY_POSITION = "position";

    private HashMap<Long, Picture> pictureHashMap;
    private HashMap<Type, ArrayList<Long>> pictureIdListHashMap;
    private HashMap<Type, ArrayList<PictureDataObserver>> observers;



    @Override
    public void update() {
        for (Type type:Type.values()) {
            update(type);
        }
    }

    public enum Type {
        SENT,
        RECEIVED;

        public String getKey() {
            return name().toLowerCase();
        }
    };

    private PictureDataManager() {
        pictureHashMap = new HashMap<>();
        pictureIdListHashMap = new HashMap<>();
        observers = new HashMap<>();
    }
    public static PictureDataManager getInstance() {
        if (instance == null) instance = new PictureDataManager();
        return instance;
    }


    public void put(Picture picture) {
        pictureHashMap.put(picture.getId(), picture);
    }

    public Picture get(long pictureId) {
        return pictureHashMap.get(pictureId);
    }

    public ArrayList<Long> getPictureIdList(Type type) {
        if (!pictureIdListHashMap.containsKey(type)) {
            pictureIdListHashMap.put(type, new ArrayList<Long>());
        }
        return pictureIdListHashMap.get(type);
    }

    public void add(Type type, Picture picture) {
        ArrayList<Long> targetIdList = getPictureIdList(type);
        if (!targetIdList.contains(picture.getId())) targetIdList.add(picture.getId());

        put(picture);
    }

    public void add(Type type, int index, Picture picture) {
        ArrayList<Long> targetIdList = getPictureIdList(type);
        if (!targetIdList.contains(picture.getId())) targetIdList.add(index, picture.getId());
        put(picture);
    }

    public void clear(Type type) {
        getPictureIdList(type).clear();
    }

    @Override
    public void addObserver(Type type, PictureDataObserver observer) {
        if (!observers.containsKey(type)) {
            observers.put(type, new ArrayList<PictureDataObserver>());
        }
        observers.get(type).add(observer);
    }

    @Override
    public void removeObserver(Type type, PictureDataObserver observer) {
        observers.get(type).remove(observer);
    }

    @Override
    public void update(Type type) {
        if (observers.containsKey(type)) {

            Iterator<PictureDataObserver> iterator = observers.get(type).iterator();
            while (iterator.hasNext()) {
                iterator.next().update();
            }
        }
    }


    @Override
    public void update(Type type, long pictureId) {
        Iterator<PictureDataObserver> iterator = observers.get(type).iterator();
        while (iterator.hasNext()) {
            iterator.next().update(pictureId);
        }
    }

    @Override
    public void update(long pictureId) {
        Iterator<Type> iterator = pictureIdListHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            Type type = iterator.next();
            if (getPictureIdList(type).contains(pictureId))
                update(type, pictureId);
        }

    }
}
