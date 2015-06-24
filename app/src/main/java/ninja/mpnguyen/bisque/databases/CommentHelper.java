package ninja.mpnguyen.bisque.databases;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Observable;

import ninja.mpnguyen.bisque.things.CommentMetadataWrapper;
import ninja.mpnguyen.bisque.things.CommentMetadata;
import ninja.mpnguyen.chowders.things.json.Comment;

public class CommentHelper extends Observable {
    public static CommentHelper observable = new CommentHelper();

    private CommentHelper() {
        super();
    }

    public void notifyChanged() {
        setChanged();
        notifyObservers();
    }

    public static CommentMetadataWrapper getMetadata(Comment comment, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<CommentMetadata, Integer> dao = databaseHelper.getCommentDao();
            QueryBuilder<CommentMetadata, Integer> builder = dao.queryBuilder();
            builder.where().eq(CommentMetadata.COLUMN_NAME_SHORT_ID, comment.short_id);
            PreparedQuery<CommentMetadata> query = builder.prepare();
            CommentMetadata commentMetadata = dao.queryForFirst(query);
            if (commentMetadata == null) {
                commentMetadata = createMetadata(comment, context);
            }
            databaseHelper.close();
            return new CommentMetadataWrapper(commentMetadata, comment);
        }
    }

    public static boolean setMetadata(CommentMetadata comment, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<CommentMetadata, Integer> dao = databaseHelper.getCommentDao();
            int update = dao.update(comment);

            observable.notifyChanged();
            databaseHelper.close();
            return update == 1;
        }
    }

    public static CommentMetadata createMetadata(Comment comment, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<CommentMetadata, Integer> dao = databaseHelper.getCommentDao();
            CommentMetadata commentMetadata = new CommentMetadata(comment);
            int create = dao.create(commentMetadata);

            observable.notifyChanged();
            databaseHelper.close();
            return create == 1 ? commentMetadata : null;
        }
    }
}
