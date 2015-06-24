package ninja.mpnguyen.bisque.nio;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;

import ninja.mpnguyen.bisque.services.Cashier;

public abstract class DualFetcher<T> implements Callable<Void> {
    public enum Source {
        Cache, Server
    }

    public interface DualListener<T> {
        void onStart(Source source);
        void onSuccess(Source source, @NonNull T result);
        void onError(Source source);
    }

    private final DualListener<T> listener;

    public DualFetcher(DualListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public Void call() {
        new CallerTask<>(new TaskListener<>(Source.Server, listener), getFetcher()).execute();
        new CacheFetcher<>(new TaskListener<>(Source.Cache, listener), getCashier()).execute();
        return null;
    }

    public abstract Callable<T> getFetcher();

    public abstract Cashier<T> getCashier();

    private static class CacheFetcher<T> extends FetcherTask<T> {
        private final Cashier<T> cashier;

        private CacheFetcher(Listener<T> listener, Cashier<T> cashier) {
            super(listener);
            this.cashier = cashier;
        }

        @Override
        public T doBlockingStuff() throws Exception {
            try {
                return cashier.getFromCache();
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static class TaskListener<T> implements FetcherTask.Listener<T> {
        private final Source source;
        private final DualListener<T> listener;

        private TaskListener(Source source, DualListener<T> listener) {
            this.source = source;
            this.listener = listener;
        }

        @Override
        public void onStart() {
            listener.onStart(source);
        }

        @Override
        public void onSuccess(@NonNull T result) {
            listener.onSuccess(source, result);
        }

        @Override
        public void onError() {
            listener.onError(source);
        }
    }
}
