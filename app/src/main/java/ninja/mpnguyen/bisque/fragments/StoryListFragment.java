package ninja.mpnguyen.bisque.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.List;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.nio.StoryFetcherTask;
import ninja.mpnguyen.bisque.views.comments.StoryAdapter;
import ninja.mpnguyen.chowders.things.json.Post;
import ninja.mpnguyen.chowders.things.json.Story;

public class StoryListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String ARGUMENT_POST = "ninja.mpnguyen.bisque.fragments.StoryListFragment.ARGUMENT_POST";
    public static final String ARGUMENT_SHORT_ID = "ninja.mpnguyen.bisque.fragments.StoryListFragment.ARGUMENT_SHORT_ID";

    public static class Builder {
        private Post post = null;
        private String short_id = null;

        public Builder() {
            super();
        }

        public Builder post(Post post) {
            this.post = post;
            this.short_id = post.short_id;
            return this;
        }

        public Builder url(Uri uri) {
            this.short_id = getShortId(uri);
            return this;
        }

        public Builder url(String uri) {
            this.short_id = getShortId(Uri.parse(uri));
            return this;
        }

        public Builder shortID(String short_id) {
            this.short_id = short_id;
            return this;
        }

        public StoryListFragment build() {
            return StoryListFragment.newInstance(post, short_id);
        }

        private static String getShortId(Uri uri) {
            List<String> paths = uri.getPathSegments();
            if (paths.size() >= 2 && paths.get(0).equalsIgnoreCase("s")) {
                return paths.get(1);
            } else {
                return null;
            }
        }
    }

    private static StoryListFragment newInstance(Post post, String short_id) {
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_POST, post);
        args.putString(ARGUMENT_SHORT_ID, short_id);

        StoryListFragment f = new StoryListFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_list_story, container, false);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        SlidingUpPanelLayout slider = (SlidingUpPanelLayout) v.findViewById(R.id.slidinglayout);
        slider.setPanelSlideListener(new SlideListener((Toolbar) v.findViewById(R.id.toolbar)));

        Story storyFromIntent = getStoryFromArgs();
        recyclerView.swapAdapter(new StoryAdapter(storyFromIntent, true), false);

        WebView webview = (WebView) v.findViewById(R.id.webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webview.loadUrl(getStoryFromArgs().url);

        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.webview_progress);
        webview.setWebChromeClient(new MyChromeClient(progressBar));

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onRefresh();
    }

    private Story getStoryFromArgs() {
        Bundle args = getArguments();
        Post post = args.containsKey(ARGUMENT_POST) ? (Post) args.getSerializable(ARGUMENT_POST) : null;
        return new Story(post);
    }

    private String getShortIDFromArgs() {
        Bundle args = getArguments();
        return args.containsKey(ARGUMENT_SHORT_ID) ? args.getString(ARGUMENT_SHORT_ID) : null;
    }

    @Override
    public void onRefresh() {
        // TODO: I really don't like how we're implementing a fragment as a listener... could memory leak

        View v = getView();
        if (v == null) return;

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        if (swipeRefreshLayout == null) return;

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.content_view);
        if (recyclerView == null) return;

        String short_id = getShortIDFromArgs();
        StoryFetchedListener listener = new StoryFetchedListener(swipeRefreshLayout, recyclerView, getStoryFromArgs());
        new StoryFetcherTask(listener, short_id).execute();
    }

    private static class StoryFetchedListener extends RefreshingListener<Story> {
        private final WeakReference<RecyclerView> contentRef;
        private final Story cachedStory;

        private StoryFetchedListener(@Nullable SwipeRefreshLayout refreshLayout, @Nullable RecyclerView content, Story cachedStory) {
            super(refreshLayout);
            this.contentRef = new WeakReference<>(content);
            this.cachedStory = cachedStory;
        }

        @Override
        public void onSuccess(@NonNull Story result) {
            super.onSuccess(result);

            RecyclerView content = contentRef.get();
            if (content == null) return;
            content.swapAdapter(new StoryAdapter(result, false), false);
        }

        @Override
        public void onError() {
            super.onError();

            RecyclerView content = contentRef.get();
            if (content == null) return;
            content.swapAdapter(new StoryAdapter(cachedStory, false), false);
        }
    }

    private static class SlideListener implements SlidingUpPanelLayout.PanelSlideListener {
        private final WeakReference<Toolbar> toolbarRef;

        private SlideListener(Toolbar toolbar) {
            this.toolbarRef = new WeakReference<>(toolbar);
        }

        @Override
        public void onPanelSlide(View view, float v) {

        }

        @Override
        public void onPanelCollapsed(View view) {
            Toolbar toolbar = toolbarRef.get();
            if (toolbar == null) return;

            toolbar.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            View content = inflater.inflate(R.layout.web_bar, toolbar, false);
            toolbar.addView(content);
        }

        @Override
        public void onPanelExpanded(View view) {
            Toolbar toolbar = toolbarRef.get();
            if (toolbar == null) return;

            toolbar.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            View content = inflater.inflate(R.layout.comments, toolbar, false);
            toolbar.addView(content);
        }

        @Override
        public void onPanelAnchored(View view) {

        }

        @Override
        public void onPanelHidden(View view) {

        }
    }

    private static class MyChromeClient extends WebChromeClient {
        private final WeakReference<ProgressBar> progressRef;

        private MyChromeClient(ProgressBar progressBar) {
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
}
