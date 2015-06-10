package ninja.mpnguyen.bisque.fragments.story;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.nio.StoryFetcherTask;
import ninja.mpnguyen.bisque.views.comments.StoryAdapter;
import ninja.mpnguyen.chowders.things.json.Post;
import ninja.mpnguyen.chowders.things.json.Story;

public class StoryListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String ARGUMENT_POST = "ninja.mpnguyen.bisque.fragments.story.StoryListFragment.ARGUMENT_POST";
    public static final String ARGUMENT_SHORT_ID = "ninja.mpnguyen.bisque.fragments.story.StoryListFragment.ARGUMENT_SHORT_ID";
    public static final String ARGUMENT_COMMENTS = "ninja.mpnguyen.bisque.fragments.story.StoryListFragment.ARGuMENT_COMMENTS";

    public static class Builder {
        private Post post = null;
        private String short_id = null;
        private boolean showCommentsFirst = false;

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

        public Builder showCommentsFirst(boolean showCommentsFirst) {
            this.showCommentsFirst = showCommentsFirst;
            return this;
        }

        public Builder shortID(String short_id) {
            this.short_id = short_id;
            return this;
        }

        public StoryListFragment build() {
            return StoryListFragment.newInstance(post, short_id, showCommentsFirst);
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

    private static StoryListFragment newInstance(Post post, String short_id, boolean showCommentsFirst) {
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_POST, post);
        args.putString(ARGUMENT_SHORT_ID, short_id);
        args.putBoolean(ARGUMENT_COMMENTS, showCommentsFirst);

        StoryListFragment f = new StoryListFragment();
        f.setArguments(args);
        return f;
    }

    private WebView webview;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_list_story, container, false);
        Story story = getStoryFromArgs();

        initSwipeRefreshView(v, this);
        initRecyclerView(v, story);
        webview = initWebView(v, story);
        initSliderController(v, inflater, story);

        return v;
    }

    private static void initSliderController(View v, LayoutInflater inflater, Story story) {
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        SlidingUpPanelLayout slidr = (SlidingUpPanelLayout) v.findViewById(R.id.slidinglayout);
        slidr.setPanelSlideListener(new SlideListener(toolbar));

        toolbar.removeAllViews();
        View handle = inflater.inflate(R.layout.story_handle, toolbar, false);
        toolbar.addView(handle);
        View webbar = handle.findViewById(R.id.story_web_bar);
        WebView webview = (WebView) v.findViewById(R.id.webview);
        WebBarListener.bindWebController(webbar, new WebBarListener(webview, slidr));
        View commentsbar = handle.findViewById(R.id.story_comments_bar);
        CommentsBarListener.bindComments(commentsbar, new CommentsBarListener(story.comments_url, inflater.getContext(), slidr));

    }

    private static void initRecyclerView(View v, Story story) {
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.swapAdapter(new StoryAdapter(story, true), false);
    }

    public static void initSwipeRefreshView(View v, SwipeRefreshLayout.OnRefreshListener listener) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(listener);
    }

    private static WebView initWebView(View v, Story story) {
        WebView webview = (WebView) v.findViewById(R.id.webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webview.loadUrl(story.url);

        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.webview_progress);
        webview.setWebChromeClient(new WebViewChromeClient(progressBar));
        webview.setWebViewClient(new WebViewClient());
        return webview;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean showCommentsFirst = getArguments().getBoolean(ARGUMENT_COMMENTS);
                if (showCommentsFirst) {
                    SlidingUpPanelLayout slidr = (SlidingUpPanelLayout) view.findViewById(R.id.slidinglayout);
                    slidr.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        }, 300);

        onRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webview != null) webview.destroy();
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

}
