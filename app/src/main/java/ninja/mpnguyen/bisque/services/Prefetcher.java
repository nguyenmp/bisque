package ninja.mpnguyen.bisque.services;

import java.io.Serializable;

public interface Prefetcher<T> extends Serializable {
    T prefetch();
    void cachePrefetchable(T t);
}
