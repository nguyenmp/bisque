package ninja.mpnguyen.bisque.databases;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;

import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.chowders.things.Post;

public class MetafyTask extends FetcherTask<MetaDataedPost[]> {
    private final WeakReference<Context> contextRef;
    private final Post[] posts;

    public MetafyTask(Context context, Post[] posts, FetcherTask.Listener<MetaDataedPost[]> listener) {
        super(listener);
        this.contextRef = new WeakReference<>(context);
        this.posts = posts;
    }

    @Override
    public MetaDataedPost[] doBlockingStuff() throws Exception {
        Context context = contextRef.get();
        if (context == null) return null;

        try {
            ArrayList<MetaDataedPost> metaDataedPosts = new ArrayList<>(posts.length);
            for (Post post : posts) {
                MetaDataedPost metadata = PostHelper.getMetadata(post, context);
                metaDataedPosts.add(metadata);
            }
            return metaDataedPosts.toArray(new MetaDataedPost[metaDataedPosts.size()]);
        } catch (SQLException e) {
            return null;
        }
    }
}
