package ninja.mpnguyen.bisque.services;

import com.anupcowkur.reservoir.Reservoir;

public abstract class Cashier<T> {
    public static final Object lock = new Object();

    public T getFromCache() throws Exception {
        synchronized (lock) {
            return Reservoir.get(getKey(), getType());
        }
    }

    public void putIntoCache(T t) throws Exception {
        synchronized (lock) {
            if (t != null) Reservoir.put(getKey(), t);
        }
    }

    public abstract String getKey();

    public abstract Class<T> getType();
}
