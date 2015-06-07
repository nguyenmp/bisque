package ninja.mpnguyen.bisque.util;

import android.os.Process;

/**
 * Simple utility thread that sets itself to background
 * priority and names itself a background thread.
 */
public class BackgroundThread extends Thread {
    private final Runnable runnable;

    BackgroundThread(String owner, Runnable runnable) {
        super(String.format("%s Background Thread", owner));
        this.runnable = runnable;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        if (runnable != null) runnable.run();
    }
}
