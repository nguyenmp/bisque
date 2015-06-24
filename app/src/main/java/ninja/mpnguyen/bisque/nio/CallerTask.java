package ninja.mpnguyen.bisque.nio;

import java.util.concurrent.Callable;

public class CallerTask<T> extends FetcherTask<T> {
    private final Callable<T> callable;

    protected CallerTask(Listener<T> listener, Callable<T> callable) {
        super(listener);
        this.callable = callable;
    }

    @Override
    public T doBlockingStuff() throws Exception {
        if (callable != null) return callable.call();
        else return null;
    }
}
