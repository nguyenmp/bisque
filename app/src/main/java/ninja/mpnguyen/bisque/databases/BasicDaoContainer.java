package ninja.mpnguyen.bisque.databases;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ninja.mpnguyen.bisque.things.PostMetadata;

public abstract class BasicDaoContainer<T> implements DaoContainer<T> {
    private Dao<T, Integer> postDao = null;

    @Override
    public Dao<T, Integer> getDao(OrmLiteSqliteOpenHelper dbHelper) throws SQLException {
        if (postDao == null) {
            postDao = dbHelper.getDao(getDataClass());
        }
        return postDao;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTable(connectionSource, getDataClass());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        TableUtils.dropTable(connectionSource, getDataClass(), true);
        onCreate(database, connectionSource);
    }

    @Override
    public void close() {
        postDao = null;
    }

    public abstract Class<T> getDataClass();
}
