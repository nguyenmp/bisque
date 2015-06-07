package ninja.mpnguyen.bisque.util;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * Simple utility factory that generates named background threads.
 */
public class BackgroundThreadFactory implements ThreadFactory {
    private final String owner;

    public BackgroundThreadFactory(String owner) {
        super();
        this.owner = owner;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        return new BackgroundThread(owner, r);
    }
}
