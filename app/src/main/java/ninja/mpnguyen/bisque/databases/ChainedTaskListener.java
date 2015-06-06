package ninja.mpnguyen.bisque.databases;

import android.support.annotation.NonNull;

import ninja.mpnguyen.bisque.nio.FetcherTask;

public abstract class ChainedTaskListener<A, B> implements FetcherTask.Listener<A> {
    private final FetcherTask.Listener<B> listener;

    public ChainedTaskListener(FetcherTask.Listener<B> listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        listener.onStart();
    }

    @Override
    public void onSuccess(@NonNull A result) {
        FetcherTask<B> next = getNext(listener, result);
        if (next != null) next.execute();
    }

    @Override
    public void onError() {
        if (listener != null) listener.onError();
    }

    public abstract FetcherTask<B> getNext(FetcherTask.Listener<B> listener, A input);
}
