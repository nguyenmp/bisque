package ninja.mpnguyen.bisque.fragments.story;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.R;

class SlideListener implements SlidingUpPanelLayout.PanelSlideListener {
    private final WeakReference<Toolbar> toolbarRef;

    SlideListener(Toolbar toolbar) {
        this.toolbarRef = new WeakReference<>(toolbar);
    }

    @Override
    public void onPanelSlide(View view, float v) {
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
        Toolbar toolbar = toolbarRef.get();
        if (toolbar == null) return;

        toolbar.findViewById(R.id.story_comments_bar).setVisibility(View.GONE);
    }

    @Override
    public void onPanelExpanded(View view) {
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
