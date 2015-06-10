package ninja.mpnguyen.bisque.fragments.story;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

class WebViewChromeClient extends WebChromeClient {
    private final WeakReference<ProgressBar> progressRef;

    WebViewChromeClient(ProgressBar progressBar) {
        this.progressRef = new WeakReference<>(progressBar);
        progressBar.setMax(100);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

        ProgressBar progressBar = progressRef.get();
        if (progressBar == null) return;

        progressBar.setProgress(newProgress);

        if (progressBar.getMax() == newProgress) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            progressBar.startAnimation(animation);
        } else if (0 == newProgress) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            progressBar.startAnimation(animation);
        }
    }
}
