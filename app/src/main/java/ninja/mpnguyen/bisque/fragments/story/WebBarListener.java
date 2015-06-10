package ninja.mpnguyen.bisque.fragments.story;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.R;

class WebBarListener {
    private final WeakReference<WebView> webviewRef;
    private final WeakReference<SlidingUpPanelLayout> slidrRef;

    WebBarListener(WebView webview, SlidingUpPanelLayout slidr) {
        this.webviewRef = new WeakReference<>(webview);
        this.slidrRef = new WeakReference<>(slidr);
    }

    static void bindWebController(View web_bar, final WebBarListener listener) {
        web_bar.findViewById(R.id.webview_action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBackward();
            }
        });
        web_bar.findViewById(R.id.webview_action_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onForward();
            }
        });
        web_bar.findViewById(R.id.webview_action_browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBrowser();
            }
        });
        web_bar.findViewById(R.id.webview_action_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShare();
            }
        });
        web_bar.findViewById(R.id.webview_action_comments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onComments();
            }
        });
    }

    public void onBackward() {
        WebView webview = webviewRef.get();
        if (webview == null) return;

        webview.goBack();
    }

    public void onForward() {
        WebView webview = webviewRef.get();
        if (webview == null) return;

        webview.goForward();
    }

    public void onBrowser() {
        WebView webview = webviewRef.get();
        if (webview == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(webview.getUrl()));
        Context context = webview.getContext();
        context.startActivity(intent);
    }

    public void onShare() {
        WebView webview = webviewRef.get();
        if (webview == null) return;

        String url = webview.getUrl();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        Context context = webview.getContext();
        context.startActivity(intent);
    }

    public void onComments() {
        SlidingUpPanelLayout slidr = slidrRef.get();
        if (slidr == null) return;

        slidr.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
}
