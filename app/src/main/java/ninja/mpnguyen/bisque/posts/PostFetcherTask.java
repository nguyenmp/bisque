package ninja.mpnguyen.bisque.posts;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.ref.WeakReference;

import ninja.mpnguyen.chowders.nio.FrontPage;
import ninja.mpnguyen.chowders.things.Post;

public class PostFetcherTask extends AsyncTask<Void, Void, Post[]> {
    public interface Listener {
        void onStart();
        void onSuccess(@NonNull Post[] posts);
        void onError();
    }

    private final WeakReference<Listener> listenerRef;

    public PostFetcherTask(Listener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        final Listener listener = listenerRef.get();
        if (listener != null) listener.onStart();
    }

    @Override
    protected Post[] doInBackground(Void... params) {
        try {
            return FrontPage.get(FrontPage.Sort.Hottest);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Post[] posts) {
        super.onPostExecute(posts);

        final Listener listener = listenerRef.get();
        if (listener == null) return;

        if (posts == null) listener.onError();
        else listener.onSuccess(posts);
    }
}
