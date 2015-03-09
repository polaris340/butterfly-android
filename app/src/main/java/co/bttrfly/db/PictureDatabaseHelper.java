package co.bttrfly.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Created by jiho on 3/9/15.
 */
public class PictureDatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "butterfly";

    private static final int DATABASE_VERSION = 1;

    private Dao<Picture, Long> pictureDao;

    public PictureDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database,ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Picture.class);
        } catch (SQLException e) {
            Log.e(PictureDatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Picture.class, true);
            onCreate(db, connectionSource);
        } catch (java.sql.SQLException e) {
            Log.e(PictureDatabaseHelper.class.getName(), "Can't drop databases", e);
        }
    }

    /**
     * Use this function to access data
     * from database
     *
     * @return
     */
    public Dao<Picture, Long> getDao() {
        if (null == pictureDao) {
            try {
                pictureDao = getDao(Picture.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return pictureDao;
    }
}
