package ninja.mpnguyen.bisque.nio;

import android.support.annotation.NonNull;

/**
 * This is a simple cache/server listener that ignores the cache hit after a
 * server hit. It also only shows errors for the server (not the cache).
 * @param <T> The result to listen for
 */
public class SimpleDualListener<T> implements DualFetcher.DualListener<T> {
    private volatile boolean ignoreCache = false;
    private final FetcherTask.Listener<T> listener;

    public SimpleDualListener(FetcherTask.Listener<T> listener) {
        this.listener = listener;
    }

    @Override
    public void onStart(DualFetcher.Source source) {
        listener.onStart();
    }

    @Override
    public void onSuccess(DualFetcher.Source source, @NonNull T result) {
        if (source == DualFetcher.Source.Cache && !ignoreCache) {
            listener.onSuccess(result);
        } else if (source == DualFetcher.Source.Server) {
            ignoreCache = true;
            listener.onSuccess(result);
        }
    }

    @Override
    public void onError(DualFetcher.Source source) {
        if (source == DualFetcher.Source.Server) listener.onError();
    }
}
