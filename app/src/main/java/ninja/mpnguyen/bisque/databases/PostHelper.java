package ninja.mpnguyen.bisque.databases;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Observable;

import ninja.mpnguyen.bisque.things.PostMetadataWrapper;
import ninja.mpnguyen.bisque.things.PostMetadata;
import ninja.mpnguyen.chowders.things.json.Post;

public class PostHelper extends Observable {
    public static PostHelper observable = new PostHelper();

    private PostHelper() {
        super();
    }

    public void notifyChanged() {
        setChanged();
        notifyObservers();
    }

    public static PostMetadataWrapper getMetadata(Post post, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<PostMetadata, Integer> dao = databaseHelper.getPostDao();
            QueryBuilder<PostMetadata, Integer> builder = dao.queryBuilder();
            builder.where().eq(PostMetadata.COLUMN_NAME_SHORT_ID, post.short_id);
            PreparedQuery<PostMetadata> query = builder.prepare();
            PostMetadata postMetadata = dao.queryForFirst(query);
            if (postMetadata == null) {
                postMetadata = createMetadata(post, context);
            }
            databaseHelper.close();
            return new PostMetadataWrapper(postMetadata, post);
        }
    }

    public static boolean setMetadata(PostMetadata post, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<PostMetadata, Integer> dao = databaseHelper.getPostDao();
            int update = dao.update(post);

            observable.notifyChanged();
            databaseHelper.close();
            return update == 1;
        }
    }

    public static PostMetadata createMetadata(Post post, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<PostMetadata, Integer> dao = databaseHelper.getPostDao();
            PostMetadata postMetadata = new PostMetadata(post);
            int create = dao.create(postMetadata);

            observable.notifyChanged();
            databaseHelper.close();
            return create == 1 ? postMetadata : null;
        }
    }
}
