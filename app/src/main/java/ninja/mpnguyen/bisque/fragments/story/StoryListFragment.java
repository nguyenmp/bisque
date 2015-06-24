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

import java.lang.ref.WeakReference;
import java.util.List;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.things.BisqueStory;
import ninja.mpnguyen.bisque.things.StoryMetadataWrapper;
import ninja.mpnguyen.bisque.views.comments.DividerItemDecoration;
import ninja.mpnguyen.bisque.views.comments.StoryAdapter;
import ninja.mpnguyen.chowders.things.json.Post;

public class StoryListFragment extends Fragment {
    public static final String ARGUMENT_POST = "ninja.mpnguyen.bisque.fragments.story.StoryListFragment.ARGUMENT_POST";
    public static final String ARGUMENT_SHORT_ID = "ninja.mpnguyen.bisque.fragments.story.StoryListFragment.ARGUMENT_SHORT_ID";
    public static final String ARGUMENT_COMMENTS = "ninja.mpnguyen.bisque.fragments.story.StoryListFragment.ARGuMENT_COMMENTS";

    private static final String STATE_STORY = "ninja.mpnguyen.bisque.fragments.story.StoryListFragment.STATE_STORY";
    private static final String STATE_METAFIED_STORY = "ninja.mpnguyen.bisque.fragments.story.StoryListFragment.STATE_METAFIED_STORY";

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
        StoryMetadataWrapper story = getStoryFromArgs();

        initSwipeRefreshView(v, new RefreshListener(this));
        initRecyclerView(v, story);
        webview = initWebView(v);
        initSliderController(v, inflater, story);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (webview != null) webview.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (webview != null) webview.onResume();
    }

    private static void initSliderController(View v, LayoutInflater inflater, StoryMetadataWrapper story) {
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        WebView webview = (WebView) v.findViewById(R.id.webview);
        SlidingUpPanelLayout slidr = (SlidingUpPanelLayout) v.findViewById(R.id.slidinglayout);
        slidr.setPanelSlideListener(new SlideListener(webview, toolbar));

        toolbar.removeAllViews();
        View handle = inflater.inflate(R.layout.story_handle, toolbar, false);
        toolbar.addView(handle);
    }

    private void initRecyclerView(View v, StoryMetadataWrapper story) {
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(v.getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.swapAdapter(new StoryAdapter(story, true, new HideCommentListener(this)), false);
    }

    public static void initSwipeRefreshView(View v, SwipeRefreshLayout.OnRefreshListener listener) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(listener);
    }

    private static WebView initWebView(View v) {
        WebView webview = (WebView) v.findViewById(R.id.webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.webview_progress);
        webview.setWebChromeClient(new WebViewChromeClient(progressBar));
        webview.setWebViewClient(new WebViewClient());
        return webview;
    }

    /**
     * Tries to pull previous data from our state.  If this data is metafied,
     * just display it.  Otherwise, metafy it and display it.
     *
     * If no data is found, we simply drop this call.
     *
     * @see #onRefresh()
     */
    public void updateMetadata() {
        Bundle args = getArguments();
        if (args.containsKey(STATE_METAFIED_STORY)) {
            StoryMetadataWrapper wrapper = (StoryMetadataWrapper) args.getSerializable(STATE_METAFIED_STORY);
            updateMetadata(wrapper);
        } else if (args.containsKey(STATE_STORY)) {
            BisqueStory story = (BisqueStory) args.getSerializable(STATE_STORY);
            updateMetadata(story);
        }
    }

    /** The last metafication task that was run. Used to
     * cancel previous metifications so they don't overlap. */
    MetafyTask task = null;

    /**
     * Stories this non-metafied data from our state and metafy it.
     *
     * The result of this metafication should be stored into the state afterwards.
     */
    public void updateMetadata(BisqueStory story) {
        if (task != null) {
            task.cancel(false);
            task = null;
        }

        Bundle args = getArguments();
        args.putSerializable(STATE_STORY, story);

        View v = getView();
        if (v == null) return;

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        if (swipeRefreshLayout == null) return;

        WebView webview = (WebView) v.findViewById(R.id.webview);
        webview.loadUrl(story.post.url);

        View webbar = v.findViewById(R.id.story_web_bar);
        SlidingUpPanelLayout slidr = (SlidingUpPanelLayout) v.findViewById(R.id.slidinglayout);
        WebBarListener.bindWebController(webbar, new WebBarListener(webview, slidr));
        View commentsbar = v.findViewById(R.id.story_comments_bar);
        CommentsBarListener.bindComments(commentsbar, new CommentsBarListener(story.post.comments_url, v.getContext(), slidr));

        StoryMetafiedListener listener = new StoryMetafiedListener(this, swipeRefreshLayout, null);
        task = new MetafyTask(story, v.getContext(), listener);
        task.execute();
    }

    /** Puts this story into the cache and displays it */
    public void updateMetadata(StoryMetadataWrapper wrapper) {
        // Store result into state
        Bundle args = getArguments();
        args.putSerializable(STATE_METAFIED_STORY, wrapper);

        // Push content onto screen
        View v = getView();
        if (v == null) return;
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.content_view);
        recyclerView.swapAdapter(new StoryAdapter(wrapper, false, new HideCommentListener(this)), false);
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

        updateMetadata();

        onRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webview != null) webview.destroy();
    }

    private StoryMetadataWrapper getStoryFromArgs() {
        // TODO: This used to be fine but now we need to do a DB operation on the UI thread...
        return null;
//        Bundle args = getArguments();
//        Post post = args.containsKey(ARGUMENT_POST) ? (Post) args.getSerializable(ARGUMENT_POST) : null;
//        return new Story(post);
    }

    private String getShortIDFromArgs() {
        Bundle args = getArguments();
        return args.containsKey(ARGUMENT_SHORT_ID) ? args.getString(ARGUMENT_SHORT_ID) : null;
    }

    public void onRefresh() {
        View v = getView();
        if (v == null) return;

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        if (swipeRefreshLayout == null) return;

        String short_id = getShortIDFromArgs();
        StoryFetchedListener listener = new StoryFetchedListener(this, swipeRefreshLayout, getStoryFromArgs());
        new StoryFetcherTask(listener, short_id).execute();
    }

    /** Indirection listener which allows us to prevent memory leaking our fragment + context */
    private static class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        private final WeakReference<StoryListFragment> fRef;

        public RefreshListener(StoryListFragment f) {
            fRef = new WeakReference<>(f);
        }

        @Override
        public void onRefresh() {
            StoryListFragment f = fRef.get();
            if (f != null) f.onRefresh();
        }
    }

}
