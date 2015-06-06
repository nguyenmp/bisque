package ninja.mpnguyen.bisque.databases;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Observable;

import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.bisque.things.PostMetadata;
import ninja.mpnguyen.chowders.things.Post;

public class PostHelper extends Observable {
    public static PostHelper observable = new PostHelper();

    private PostHelper() {
        super();
    }

    public void notifyChanged() {
        setChanged();
        notifyObservers();
    }

    public static MetaDataedPost getMetadata(Post post, Context context) throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Dao<PostMetadata, Integer> dao = databaseHelper.getDao();
        QueryBuilder<PostMetadata, Integer> builder = dao.queryBuilder();
        builder.where().eq(PostMetadata.COLUMN_NAME_SHORT_ID, post.short_id);
        PreparedQuery<PostMetadata> query = builder.prepare();
        PostMetadata storedPosts = dao.queryForFirst(query);
        if (storedPosts == null) {
            storedPosts = createMetadata(post, context);
        }
        return new MetaDataedPost(storedPosts, post);
    }

    public static boolean setMetadata(PostMetadata post, Context context) throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Dao<PostMetadata, Integer> dao = databaseHelper.getDao();
        int update = dao.update(post);

        observable.notifyChanged();
        return update == 1;
    }

    public static PostMetadata createMetadata(Post post, Context context) throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Dao<PostMetadata, Integer> dao = databaseHelper.getDao();
        PostMetadata postMetadata = new PostMetadata(post);
        int create = dao.create(postMetadata);

        observable.notifyChanged();
        return create == 1 ? postMetadata : null;
    }
}
