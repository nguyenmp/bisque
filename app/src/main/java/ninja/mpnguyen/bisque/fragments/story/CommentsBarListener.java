package ninja.mpnguyen.bisque.fragments.story;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.R;

class CommentsBarListener {
    private final String url;
    private final WeakReference<Context> contextRef;
    private final WeakReference<SlidingUpPanelLayout> slidrRef;

    CommentsBarListener(String url, Context context, SlidingUpPanelLayout slidr) {
        this.url = url;
        this.contextRef = new WeakReference<>(context);
        this.slidrRef = new WeakReference<>(slidr);
    }

    public static void bindComments(View comment_bar, final CommentsBarListener listener) {
        comment_bar.findViewById(R.id.comments_action_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLink();
            }
        });
        comment_bar.findViewById(R.id.comments_action_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBrowser();
            }
        });
        comment_bar.findViewById(R.id.comments_action_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShare();
            }
        });
    }

    public void onBrowser() {
        Context context = contextRef.get();
        if (context == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public void onShare() {
        Context context = contextRef.get();
        if (context == null) return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    public void onLink() {
        SlidingUpPanelLayout slidr = slidrRef.get();
        if (slidr == null) return;

        slidr.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }
}
