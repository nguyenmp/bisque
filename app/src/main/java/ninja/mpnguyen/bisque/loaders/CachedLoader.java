package ninja.mpnguyen.bisque.loaders;

import android.content.Context;

import ninja.mpnguyen.bisque.services.Cashier;

public abstract class CachedLoader<T> extends GenericLoader<T> {
    public CachedLoader(Context context) {
        super(context);
    }

    @Override
    public T loadInBackground() {
        try {
            return getCachier().getFromCache();
        } catch (Exception e) {
            return null;
        }
    }

    public abstract Cashier<T> getCachier();
}
