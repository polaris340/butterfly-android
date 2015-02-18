package me.jiho.butterfly.picture;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;

import me.jiho.butterfly.auth.Auth;
import me.jiho.butterfly.auth.LoginStateChangeObserver;
import me.jiho.butterfly.db.Picture;
import me.jiho.butterfly.network.DefaultErrorListener;
import me.jiho.butterfly.network.VolleyRequestQueue;
import me.jiho.butterfly.statics.Constants;
import me.jiho.butterfly.util.MessageUtil;

/**
 * Created by jiho on 1/14/15.
 */
public class PictureDataManager implements PictureDataObservable, LoginStateChangeObserver {
    private static PictureDataManager instance;
    public static final String KEY_TYPE = "type";
    public static final String KEY_POSITION = "position";

    private HashMap<Long, Picture> pictureHashMap;
    private HashMap<Type, ArrayList<Long>> pictureIdListHashMap;
    private HashMap<Type, ArrayList<PictureDataObserver>> observers;

    private HashMap<Type, Request> currentRequestHashMap;

    public static final String URL_GET_PICTURE = Constants.URLs.API_URL + "picture/";


    @Override
    public void update() {
        for (Type type:Type.values()) {
            update(type);
        }
    }

    @Override
    public void addItems(Type type, int startPosition, int itemCount) {
        Iterator<PictureDataObserver> iterator = observers.get(type).iterator();
        while (iterator.hasNext()) {
            iterator.next().addItems(startPosition, itemCount);
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



    private PictureDataManager() {
        pictureHashMap = new HashMap<>();
        pictureIdListHashMap = new HashMap<>();
        observers = new HashMap<>();
        currentRequestHashMap = new HashMap<>();
        Auth.getInstance().addLoginStateChangeObserver(this);
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

    public void delete(long pictureId) {
        for (Type t:Type.values()) {
            ArrayList<Long> pictureIdList = pictureIdListHashMap.get(t);
            int index = pictureIdList.indexOf(pictureId);
            if (index >= 0) {
                pictureIdList.remove(index);
                removeItem(t, index);
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
        if (type == PictureDataManager.Type.SENT) {
            url += "1/";
        } else {
            url += "0/";
        }
        if (refresh)
            url += "0";
        else
            url += getLastId(type);

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            PictureDataManager manager = PictureDataManager.getInstance();
                            if (refresh) {
                                manager.clear(type);
                            }
                            int addStartPosition = getPictureIdList(type).size();
                            String dataList = response.getString(Constants.Keys.MESSAGE);
                            Picture[] pictures = Picture.fromJsonArray(dataList);

                            for (Picture p:pictures) {
                                manager.add(type, p);
                            }
                            if (pictures.length > 0) {
                                currentRequestHashMap.remove(type);
                            }

                            if (refresh) {
                                update(type);
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

    private long getLastId(Type type) {
        ArrayList<Long> pictureIdList = getPictureIdList(type);
        if (pictureIdList.size() > 0) {
            Picture lastPicture = get(pictureIdList.get(pictureIdList.size() - 1));
            if (lastPicture.getSendPictureId() > 0) return lastPicture.getSendPictureId();
            else return lastPicture.getId();
        } else {
            return 0;
        }
    }



}
