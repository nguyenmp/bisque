package ninja.mpnguyen.bisque.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import ninja.mpnguyen.bisque.util.BackgroundThreadFactory;

public class PrefetcherService extends IntentService {
    public static final String EXTRA_PREFETCHER = "ninja.mpnguyen.bisquic.services.PrefetcherService.EXTRA_PREFETCHER";

    // Only have 20 items in queue at any time
    private static final int CAPACITY = 20;
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final ThreadFactory FACTORY = new BackgroundThreadFactory("Prefetcher");
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREADS, FACTORY);

    public PrefetcherService() {
        super("Prefetcher Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!intent.hasExtra(EXTRA_PREFETCHER)) return;

        Prefetcher prefetcher = (Prefetcher) intent.getSerializableExtra(EXTRA_PREFETCHER);
        if (prefetcher == null) return;

        EXECUTOR.submit(new PrefetcherJob(prefetcher));
    }

    public static void prefetch(Context context, Prefetcher prefetcher) {
        Intent intent = new Intent(context, PrefetcherService.class);
        intent.putExtra(EXTRA_PREFETCHER, prefetcher);
        context.startService(intent);
    }

    private static class PrefetcherJob implements Runnable {
        private final Prefetcher prefetcher;

        private PrefetcherJob(Prefetcher prefetcher) {
            this.prefetcher = prefetcher;
        }

        @Override
        public void run() {
            prefetcher.cachePrefetchable(prefetcher.prefetch());
        }
    }
}
