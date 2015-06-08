package ninja.mpnguyen.bisque.services;

import com.anupcowkur.reservoir.Reservoir;

public abstract class Cashier<T> {

    public T getFromCache() throws Exception {
        return Reservoir.get(getKey(), getType());
    }

    public void putIntoCache(T t) throws Exception {
        Reservoir.put(getKey(), t);
    }

    public abstract String getKey();

    public abstract Class<T> getType();
}
