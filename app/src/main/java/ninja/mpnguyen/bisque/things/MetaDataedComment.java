package ninja.mpnguyen.bisque.things;

import android.content.Context;

import java.sql.SQLException;

import ninja.mpnguyen.bisque.databases.CommentHelper;
import ninja.mpnguyen.chowders.things.json.Comment;

public class MetaDataedComment {
    public final CommentMetadata metadata;
    public final Comment comment;

    public MetaDataedComment(CommentMetadata metadata, Comment comment) {
        this.metadata = metadata;
        this.comment = comment;
    }

    public MetaDataedComment(MetaDataedComment other) {
        this.metadata = new CommentMetadata(other.metadata);
        this.comment = new Comment(other.comment);
    }

    public static void hideChildren(boolean hide_children, MetaDataedComment metaDataedComment, Context context) {
        CommentMetadata metadata = metaDataedComment.metadata;
        metadata.hide_children = hide_children;
        try {
            CommentHelper.setMetadata(metadata, context);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void hideChildren(MetaDataedComment metaDataedComment, Context context) {
        hideChildren(true, metaDataedComment, context);
    }

    public static void unhideChildre(MetaDataedComment metaDataedComment, Context context) {
        hideChildren(false, metaDataedComment, context);
    }
}
