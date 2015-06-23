package ninja.mpnguyen.bisque.things;

import android.content.Context;

import java.io.Serializable;
import java.sql.SQLException;

import ninja.mpnguyen.bisque.databases.PostHelper;
import ninja.mpnguyen.chowders.things.json.Post;

public class PostMetadataWrapper implements Serializable {
    public final PostMetadata metadata;
    public final Post post;

    public PostMetadataWrapper(PostMetadata metadata, Post post) {
        this.metadata = metadata;
        this.post = post;
    }

    public PostMetadataWrapper(PostMetadataWrapper other) {
        this.metadata = new PostMetadata(other.metadata);
        this.post = new Post(other.post);
    }

    public static void markAsRead(boolean read, PostMetadataWrapper postMetadataWrapper, Context context) {
        PostMetadata metadata = postMetadataWrapper.metadata;
        Post post = postMetadataWrapper.post;
        metadata.read = read;
        metadata.last_read_comment_count = read ? post.comment_count : 0;
        try {
            PostHelper.setMetadata(metadata, context);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void markAsRead(PostMetadataWrapper postMetadataWrapper, Context context) {
        markAsRead(true, postMetadataWrapper, context);
    }

    public static void markAsUnread(PostMetadataWrapper postMetadataWrapper, Context context) {
        markAsRead(false, postMetadataWrapper, context);
    }
}
