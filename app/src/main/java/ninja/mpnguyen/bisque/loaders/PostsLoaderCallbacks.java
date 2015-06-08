package ninja.mpnguyen.bisque.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.chowders.things.json.Post;

public abstract class PostsLoaderCallbacks implements LoaderManager.LoaderCallbacks<Post[]> {
    public static final int DISK_CACHED = 0, SERVER = 1;

    private final WeakReference<Context> contextRef;
    private final String topic;

    private boolean serverReceived;

    public PostsLoaderCallbacks(Context context, String topic) {
        this.contextRef = new WeakReference<>(context);
        this.topic = topic;
    }

    @Override
    public Loader<Post[]> onCreateLoader(int id, Bundle args) {
        Context context = contextRef.get();
        if (context == null) return null;
        else if (id == DISK_CACHED) return new CachedPostsLoader(context, topic);
        else return new PostsFromServerLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<Post[]> loader, Post[] data) {
        int id = loader.getId();
        if (id == DISK_CACHED && !serverReceived) onResult(data, false);
        else if (id == SERVER) {
            serverReceived = true;
            onResult(data, true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Post[]> loader) {
        onRefreshing();
    }

    public abstract void onRefreshing();
    public abstract void onResult(Post[] data, boolean fromServer);
}
