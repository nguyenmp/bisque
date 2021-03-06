package ninja.mpnguyen.bisque.nio;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

public abstract class FetcherTask<T> extends AsyncTask<Void, Void, T> {
    public interface Listener<T> {
        void onStart();
        void onSuccess(@NonNull T result);
        void onError();
    }

    private final Listener<T> listener;

    public FetcherTask(Listener<T> listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onStart();
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

        if (result == null) listener.onError();
        else listener.onSuccess(result);
    }
}
