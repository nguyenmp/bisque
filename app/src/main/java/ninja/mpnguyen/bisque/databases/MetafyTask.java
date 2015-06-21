package ninja.mpnguyen.bisque.databases;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;

import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.chowders.things.json.Post;

public class MetafyTask extends FetcherTask<MetaDataedPost[]> {
    private final WeakReference<Context> contextRef;
    private final Post[] posts;
    private final boolean showDeleted;

    public MetafyTask(@Nullable Context context, @NonNull Post[] posts, FetcherTask.Listener<MetaDataedPost[]> listener, boolean showDeleted) {
        super(listener);
        this.contextRef = new WeakReference<>(context);
        this.posts = posts;
        this.showDeleted = showDeleted;
    }

    @Override
    public MetaDataedPost[] doBlockingStuff() throws Exception {
        Context context = contextRef.get();
        if (context == null) return null;

        try {
            ArrayList<MetaDataedPost> metaDataedPosts = new ArrayList<>(posts.length);
            for (Post post : posts) {
                MetaDataedPost metadata = PostHelper.getMetadata(post, context);
                if (showDeleted || !metadata.metadata.hidden) metaDataedPosts.add(metadata);
            }
            return metaDataedPosts.toArray(new MetaDataedPost[metaDataedPosts.size()]);
        } catch (SQLException e) {
            return null;
        }
    }
}
