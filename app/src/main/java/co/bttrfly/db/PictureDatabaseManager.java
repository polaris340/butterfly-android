package co.bttrfly.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jiho on 3/9/15.
 */
public class PictureDatabaseManager {
    private static PictureDatabaseManager instance;
    private SQLExceptionHandler mSQLExceptionHandler;

    public static void init(Context context) {
        if (null==instance) {
            instance = new PictureDatabaseManager(context);
        }
    }

    static public PictureDatabaseManager getInstance() {
        return instance;
    }

    private PictureDatabaseHelper helper;
    private PictureDatabaseManager(Context ctx) {
        helper = new PictureDatabaseHelper(ctx);
        mSQLExceptionHandler = new SQLExceptionHandler() {
            @Override
            public void handleException(SQLException e) {
                e.printStackTrace();
            }
        };
    }

    private PictureDatabaseHelper getHelper() {
        return helper;
    }

    /******* CRUD functions ******/

    /**
     * This function will add 1 entry of country
     * which is 1 complete row in the table
     *
     * @return List of Countries
     */
    public void add(Picture p) {
        try {
            getHelper().getDao().create(p);
        } catch (SQLException e) {
            if (mSQLExceptionHandler != null) {
                mSQLExceptionHandler.handleException(e);
            }
        }
    }

    /**
     * This function will return all data from
     * country table in an array of Countries
     *
     * @return List of Countries
     */
    public List<Picture> selectAll() {
        List<Picture> pictures = null;
        try {
            Dao<Picture, Long> dao = getHelper().getDao();
            QueryBuilder<Picture, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("sendPictureId", false).orderBy("id", false);
            pictures = queryBuilder.query();
        } catch (SQLException e) {
            if (mSQLExceptionHandler != null) {
                mSQLExceptionHandler.handleException(e);
            }
        }
        return pictures;
    }

    public void setSQLExceptionHandler(SQLExceptionHandler handler) {
        this.mSQLExceptionHandler = handler;
    }

    /**
     * This function will delete country
     * from table
     *
     */
    public void delete(long pictureId) {
        try {
            getHelper().getDao().deleteById(pictureId);
        } catch (SQLException e) {
            if (mSQLExceptionHandler != null) {
                mSQLExceptionHandler.handleException(e);
            }
        }
    }

    /**
     * This function will update picture
     * in table
     *
     */
    public void update(Picture picture) {
        try {
            getHelper().getDao().update(picture);
        } catch (SQLException e) {
            if (mSQLExceptionHandler != null) {
                mSQLExceptionHandler.handleException(e);
            }
        }
    }

    public void upsert(Picture picture) {
        try {
            getHelper().getDao().createOrUpdate(picture);
        } catch (SQLException e) {
            if (mSQLExceptionHandler != null) {
                mSQLExceptionHandler.handleException(e);
            }
        }
    }

    public void truncate() {
        try {
            TableUtils.clearTable(
                    getHelper().getConnectionSource(),
                    Picture.class
            );
        } catch (SQLException e) {
            if (mSQLExceptionHandler != null) {
                mSQLExceptionHandler.handleException(e);
            }
        }
    }

    public static interface SQLExceptionHandler {
        public void handleException(SQLException e);
    }

}
