package ninja.mpnguyen.bisque.nio;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public abstract class FetcherTask<T> extends AsyncTask<Void, Void, T> {
    public interface Listener<T> {
        void onStart();
        void onSuccess(@NonNull T result);
        void onError();
    }

    private final WeakReference<Listener<T>> listenerRef;

    public FetcherTask(Listener<T> listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        final Listener listener = listenerRef.get();
        if (listener != null) listener.onStart();
    }

    @Override
    protected T doInBackground(Void... params) {
        try {
            return doBlockingStuff();
        } catch (Exception e) {
            return null;
        }
    }

    public abstract T doBlockingStuff() throws Exception;

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);

        final Listener<T> listener = listenerRef.get();
        if (listener == null) return;

        if (result == null) listener.onError();
        else listener.onSuccess(result);
    }
}
