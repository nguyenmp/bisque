package ninja.mpnguyen.bisque.things;

import android.content.Context;

import java.sql.SQLException;

import ninja.mpnguyen.bisque.databases.CommentHelper;
import ninja.mpnguyen.chowders.things.json.Comment;

public class CommentMetadataWrapper {
    public final CommentMetadata metadata;
    public final Comment comment;

    public CommentMetadataWrapper(CommentMetadata metadata, Comment comment) {
        this.metadata = metadata;
        this.comment = comment;
    }

    public CommentMetadataWrapper(CommentMetadataWrapper other) {
        this.metadata = new CommentMetadata(other.metadata);
        this.comment = new Comment(other.comment);
    }

    public static void hideChildren(boolean hide_children, CommentMetadataWrapper commentMetadataWrapper, Context context) {
        CommentMetadata metadata = commentMetadataWrapper.metadata;
        metadata.hide_children = hide_children;
        try {
            CommentHelper.setMetadata(metadata, context);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void hideChildren(CommentMetadataWrapper commentMetadataWrapper, Context context) {
        hideChildren(true, commentMetadataWrapper, context);
    }

    public static void unhideChildre(CommentMetadataWrapper commentMetadataWrapper, Context context) {
        hideChildren(false, commentMetadataWrapper, context);
    }
}
