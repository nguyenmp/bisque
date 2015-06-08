package ninja.mpnguyen.bisque;

import android.app.Application;

import com.anupcowkur.reservoir.Reservoir;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Reservoir.init(this, 50_000_000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
