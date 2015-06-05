package ninja.mpnguyen.bisque.nio;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import java.lang.ref.WeakReference;

/**
 * There's a bug in the android swipe refresh layout where calling setRefreshing
 * before the view has been measured will not actually set the refreshing view.
 *
 * https://code.google.com/p/android/issues/detail?id=77712
 *
 * That used to work on earlier version of the android.support.v4,
 * but from version 21.0.0 ongoing it doesn't work and still exists
 * with android.support.v4:21.0.3 released at 10-12 December, 2014
 * and this is the reason.
 *
 * SwipeRefreshLayout indicator does not appear when the setRefreshing(true)
 * is called before the SwipeRefreshLayout.onMeasure().
 *
 * If it's not very crucial to your app to show the SwipeRefreshLayout once
 * the view is started. You can just post it to a time in the future by
 * using handlers or any thing you want.
 *
 * https://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
 */
public class RefreshingListener<T> implements FetcherTask.Listener<T> {
    public volatile boolean enabled = true;
    private final WeakReference<SwipeRefreshLayout> refreshRef;

    private final Runnable callback = new Runnable() {
        @Override
        public void run() {
            final SwipeRefreshLayout refreshLayout = refreshRef.get();
            if (refreshLayout == null || enabled == false) return;
            refreshLayout.setRefreshing(true);
        }
    };

    public RefreshingListener(SwipeRefreshLayout refreshLayout) {
        this.refreshRef = new WeakReference<>(refreshLayout);
    }

    @Override
    public void onStart() {
        showLoading();
    }

    @Override
    public void onSuccess(@NonNull T posts) {
        hideLoading();
    }

    @Override
    public void onError() {
        hideLoading();
    }

    private void showLoading() {
        enabled = true;
        final SwipeRefreshLayout refreshLayout = refreshRef.get();
        if (refreshLayout == null) return;

        refreshLayout.postDelayed(callback, 1000);
    }

    private void hideLoading() {
        enabled = false;

        SwipeRefreshLayout refreshLayout = refreshRef.get();
        if (refreshLayout == null) return;

        refreshLayout.removeCallbacks(callback);
        refreshLayout.setRefreshing(false);
    }
}
