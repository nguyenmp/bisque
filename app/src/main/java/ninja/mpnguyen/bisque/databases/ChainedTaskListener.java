package ninja.mpnguyen.bisque.databases;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.nio.FetcherTask;

public abstract class ChainedTaskListener<A, B> implements FetcherTask.Listener<A> {
    private final WeakReference<FetcherTask.Listener<B>> listenerRef;

    public ChainedTaskListener(FetcherTask.Listener<B> listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Override
    public void onStart() {
        FetcherTask.Listener<B> listener = listenerRef.get();
        if (listener != null) listener.onStart();
    }

    @Override
    public void onSuccess(@NonNull A result) {
        FetcherTask.Listener<B> listener = listenerRef.get();
        if (listener == null) return;

        FetcherTask<B> next = getNext(listener, result);
        if (next != null) next.execute();
    }

    @Override
    public void onError() {
        FetcherTask.Listener<B> listener = listenerRef.get();
        if (listener != null) listener.onError();
    }

    public abstract FetcherTask<B> getNext(FetcherTask.Listener<B> listener, A input);
}
