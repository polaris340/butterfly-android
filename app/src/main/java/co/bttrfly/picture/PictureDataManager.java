package co.bttrfly.picture;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import co.bttrfly.auth.Auth;
import co.bttrfly.auth.LoginStateChangeObserver;
import co.bttrfly.db.Picture;
import co.bttrfly.db.PictureDatabaseManager;
import co.bttrfly.network.DefaultErrorListener;
import co.bttrfly.network.VolleyRequestQueue;
import co.bttrfly.statics.Constants;
import co.bttrfly.util.MessageUtil;

/**
 * Created by jiho on 1/14/15.
 */
public class PictureDataManager implements PictureDataObservable, LoginStateChangeObserver {
    private static PictureDataManager instance;
    public static final String KEY_TYPE = "type";
    public static final String KEY_POSITION = "position";

    private ConcurrentHashMap<Long, Picture> pictureHashMap;
    private HashMap<Type, ArrayList<Long>> pictureIdListHashMap;
    private HashMap<Type, HashSet<PictureDataObserver>> observers;

    private HashMap<Type, Request> currentRequestHashMap;

    public static final String URL_GET_PICTURE = Constants.URLs.API_URL + "picture/";
    public static final String URL_GET_PICTURE_REFRESH = Constants.URLs.API_URL + "picture-refresh/";


    private PictureDataManager() {
        pictureHashMap = new ConcurrentHashMap<>();
        pictureIdListHashMap = new HashMap<>();
        observers = new HashMap<>();
        for (Type t:Type.values()) {
            observers.put(t, new HashSet<PictureDataObserver>());
        }
        currentRequestHashMap = new HashMap<>();
        Auth.getInstance().addLoginStateChangeObserver(this);

        loadFromLocalDB();
    }

    @Override
    public void update() {
        for (Type type:Type.values()) {
            update(type);
        }
    }

    @Override
    public void addItems(Type type, int startPosition, int itemCount) {
        try {

            Iterator<PictureDataObserver> iterator = observers.get(type).iterator();
            while (iterator.hasNext()) {
                iterator.next().addItems(startPosition, itemCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeItem(Type type, int position) {
        Iterator<PictureDataObserver> iterator = observers.get(type).iterator();
        while (iterator.hasNext()) {
            iterator.next().removeItem(position);
        }
    }

    @Override
    public void onLoginStateChanged(Auth.LoginState loginState) {
        switch (loginState) {
            case PENDING:
                //case LOGGED_IN:
                // cancel all current request
                for (Type t:Type.values()) {
                    if (currentRequestHashMap.containsKey(t))
                        currentRequestHashMap.remove(t).cancel();
                }
                break;
        }
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

    public void delete(final long pictureId) {
        for (Type t:Type.values()) {
            ArrayList<Long> pictureIdList = pictureIdListHashMap.get(t);
            int index = pictureIdList.indexOf(pictureId);
            if (index >= 0) {
                pictureIdList.remove(index);
                removeItem(t, index);
                pictureHashMap.remove(pictureId);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PictureDatabaseManager.getInstance().delete(pictureId);
                    }
                }).start();

            }
        }

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
            observers.put(type, new HashSet<PictureDataObserver>());
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


    public void loadMore(final Type type, final boolean refresh,
                         final Callable onPreLoading, final Callable onLoadingComplete) {
        if (currentRequestHashMap.containsKey(type)) {
            if (refresh) {
                currentRequestHashMap.remove(type).cancel();
            } else {
                return;
            }
        }

        String url = URL_GET_PICTURE;
        if (refresh) url = URL_GET_PICTURE_REFRESH;

        url += type.getKey();

        url += ("/"+getLastId(type));

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int addStartPosition = getPictureIdList(type).size();
                            String dataList = response.getString(Constants.Keys.MESSAGE);
                            Picture[] pictures = Picture.fromJsonArray(dataList);


                            int addCount = 0;
                            ArrayList<Long> idList = getPictureIdList(type);
                            for (Picture p:pictures) {
                                if (refresh) {
                                    int position = idList.indexOf(p.getId());
                                    if (position >= 0) {
                                        put(p);
                                        update(p.getId());
                                    } else {
                                        add(type, addCount++, p);
                                    }
                                } else {
                                    add(type, p);
                                }
                            }


                            if (refresh || pictures.length > 0) {
                                currentRequestHashMap.remove(type);
                            }

                            if (refresh) {
                                if (addCount > 0) {
                                    addItems(type, 0, addCount);
                                }
                            } else {
                                addItems(type, addStartPosition, pictures.length);
                            }


                        } catch (JSONException e) {
                            MessageUtil.showDefaultErrorMessage();
                            e.printStackTrace();
                        }

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
                        currentRequestHashMap.remove(type);

                        if (onLoadingComplete != null) {
                            try {
                                onLoadingComplete.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        if (onPreLoading != null) {
            try {
                onPreLoading.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentRequestHashMap.put(type, request);
        VolleyRequestQueue.add(request);
    }

    public long getLastId(Type type) {
        ArrayList<Long> pictureIdList = getPictureIdList(type);
        if (pictureIdList.size() > 0) {
            Picture lastPicture = get(pictureIdList.get(pictureIdList.size() - 1));
            if (lastPicture.getSendPictureId() > 0) return lastPicture.getSendPictureId();
            else return lastPicture.getId();
        } else {
            return 0;
        }
    }

    public void loadFromLocalDB() {
        List<Picture> pictureList = PictureDatabaseManager.getInstance().selectAll();
        Iterator<Picture> iterator = pictureList.iterator();
        while (iterator.hasNext()) {
            Picture picture = iterator.next();
            Type type = Type.SENT;
            if (picture.isReceived()) {
                type = Type.RECEIVED;
            }

            add(type, picture);
        }
    }

    public void saveToLocalDB() {
        PictureDatabaseManager dbManager = PictureDatabaseManager.getInstance();
        Collection<Picture> pictures = pictureHashMap.values();

        for (Picture p:pictures) {
            dbManager.upsert(p);
        }
    }


}
