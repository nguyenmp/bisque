package ninja.mpnguyen.bisque.things;

import android.content.Context;

import java.sql.SQLException;

import ninja.mpnguyen.bisque.databases.PostHelper;
import ninja.mpnguyen.chowders.things.json.Post;

public class MetaDataedPost {
    public final PostMetadata metadata;
    public final Post post;

    public MetaDataedPost(PostMetadata metadata, Post post) {
        this.metadata = metadata;
        this.post = post;
    }

    public MetaDataedPost(MetaDataedPost other) {
        this.metadata = new PostMetadata(other.metadata);
        this.post = new Post(other.post);
    }

    public static void markAsRead(boolean read, MetaDataedPost metaDataedPost, Context context) {
        PostMetadata metadata = metaDataedPost.metadata;
        Post post = metaDataedPost.post;
        metadata.read = read;
        metadata.last_read_comment_count = read ? post.comment_count : 0;
        try {
            PostHelper.setMetadata(metadata, context);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void markAsRead(MetaDataedPost metaDataedPost, Context context) {
        markAsRead(true, metaDataedPost, context);
    }

    public static void markAsUnread(MetaDataedPost metaDataedPost, Context context) {
        markAsRead(false, metaDataedPost, context);
    }
}
