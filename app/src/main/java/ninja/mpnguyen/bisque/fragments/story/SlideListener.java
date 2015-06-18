package ninja.mpnguyen.bisque.fragments.story;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.R;

class SlideListener implements SlidingUpPanelLayout.PanelSlideListener {
    private final WeakReference<WebView> webviewRef;
    private final WeakReference<Toolbar> toolbarRef;

    SlideListener(WebView webview, Toolbar toolbar) {
        this.webviewRef = new WeakReference<>(webview);
        this.toolbarRef = new WeakReference<>(toolbar);
    }

    @Override
    public void onPanelSlide(View view, float v) {
        WebView webView = webviewRef.get();
        if (webView != null) {
            webView.onPause();
        }

        Toolbar toolbar = toolbarRef.get();
        if (toolbar == null) return;

        View comments = toolbar.findViewById(R.id.story_comments_bar);
        comments.setVisibility(View.VISIBLE);
        comments.setAlpha(v);
        View web = toolbar.findViewById(R.id.story_web_bar);
        web.setVisibility(View.VISIBLE);
        web.setAlpha(1 - v);
    }

    @Override
    public void onPanelCollapsed(View view) {
        WebView webView = webviewRef.get();
        if (webView != null) {
            webView.onResume();
        }

        Toolbar toolbar = toolbarRef.get();
        if (toolbar == null) return;

        toolbar.findViewById(R.id.story_comments_bar).setVisibility(View.GONE);
    }

    @Override
    public void onPanelExpanded(View view) {
        WebView webView = webviewRef.get();
        if (webView != null) {
            webView.onPause();
        }

        Toolbar toolbar = toolbarRef.get();
        if (toolbar == null) return;

        toolbar.findViewById(R.id.story_web_bar).setVisibility(View.GONE);
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

    }
}
