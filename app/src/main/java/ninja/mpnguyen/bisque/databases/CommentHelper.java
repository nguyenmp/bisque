package ninja.mpnguyen.bisque.databases;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Observable;

import ninja.mpnguyen.bisque.things.MetaDataedComment;
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

    public static MetaDataedComment getMetadata(Comment comment, Context context) throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Dao<CommentMetadata, Integer> dao = databaseHelper.getCommentDao();
        QueryBuilder<CommentMetadata, Integer> builder = dao.queryBuilder();
        builder.where().eq(CommentMetadata.COLUMN_NAME_SHORT_ID, comment.short_id);
        PreparedQuery<CommentMetadata> query = builder.prepare();
        CommentMetadata storedComments = dao.queryForFirst(query);
        if (storedComments == null) {
            storedComments = createMetadata(comment, context);
        }
        return new MetaDataedComment(storedComments, comment);
    }

    public static boolean setMetadata(CommentMetadata comment, Context context) throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Dao<CommentMetadata, Integer> dao = databaseHelper.getCommentDao();
        int update = dao.update(comment);

        observable.notifyChanged();
        return update == 1;
    }

    public static CommentMetadata createMetadata(Comment comment, Context context) throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Dao<CommentMetadata, Integer> dao = databaseHelper.getCommentDao();
        CommentMetadata commentMetadata = new CommentMetadata(comment);
        int create = dao.create(commentMetadata);

        observable.notifyChanged();
        return create == 1 ? commentMetadata : null;
    }
}
