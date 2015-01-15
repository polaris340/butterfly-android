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

    private HashMap<Long, Picture> pictureHashMap;
    private HashMap<Type, ArrayList<Long>> pictureIdListHashMap;
    private HashMap<Type, PictureDataObserver> observers;



    @Override
    public void update() {

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

    @Override
    public void addObserver(Type type, PictureDataObserver observer) {
        observers.put(type, observer);
    }

    @Override
    public void removeObserver(Type type) {
        observers.remove(type);
    }

    @Override
    public void update(Type type) {
        if (observers.containsKey(type)) {
            observers.get(type).update();
        }
    }

    @Override
    public void update(long pictureId) {
        Iterator<PictureDataObserver> iterator = observers.values().iterator();
        while (iterator.hasNext()) {
            PictureDataObserver observer = iterator.next();
            observer.update(pictureId);
        }

    }
}
