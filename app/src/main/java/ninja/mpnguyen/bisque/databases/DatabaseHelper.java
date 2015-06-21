package ninja.mpnguyen.bisque.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ninja.mpnguyen.bisque.things.CommentMetadata;
import ninja.mpnguyen.bisque.things.PostMetadata;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "bisque.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 4;

    public static final Object dbLock = new Object();

    // the DAO object we use to access the MetadataedPostData table
    private Dao<PostMetadata, Integer> postDao = null;
    private Dao<CommentMetadata, Integer> commentDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, PostMetadata.class);
            TableUtils.createTable(connectionSource, CommentMetadata.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, PostMetadata.class, true);
            TableUtils.dropTable(connectionSource, CommentMetadata.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our PostMetadata class. It will create it or just give the cached
     * value.
     */
    public Dao<PostMetadata, Integer> getPostDao() throws SQLException {
        if (postDao == null) {
            postDao = getDao(PostMetadata.class);
        }
        return postDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our PostMetadata class. It will create it or just give the cached
     * value.
     */
    public Dao<CommentMetadata, Integer> getCommentDao() throws SQLException {
        if (commentDao == null) {
            commentDao = getDao(CommentMetadata.class);
        }
        return commentDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        postDao = null;
    }
}
