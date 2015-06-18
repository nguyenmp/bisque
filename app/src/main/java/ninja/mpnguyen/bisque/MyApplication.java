package ninja.mpnguyen.bisque;

import android.app.Application;

import com.anupcowkur.reservoir.Reservoir;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class MyApplication extends Application {
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Reservoir.init(this, 50_000_000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64250762-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);
    }
}
