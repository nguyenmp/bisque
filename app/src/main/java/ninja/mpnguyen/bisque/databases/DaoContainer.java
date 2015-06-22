package ninja.mpnguyen.bisque.databases;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public interface DaoContainer<T> {
    Dao<T, Integer> getDao(OrmLiteSqliteOpenHelper dbHelper) throws SQLException;
    void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) throws SQLException;
    void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException;
    void close();
}
