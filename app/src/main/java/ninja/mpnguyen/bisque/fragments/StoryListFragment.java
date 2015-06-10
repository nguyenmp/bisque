package ninja.mpnguyen.bisque.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.webkit.WebViewClient;
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
    public static final String ARGUMENT_COMMENTS = "ninja.mpnguyen.bisque.fragments.StoryListFragment.ARGuMENT_COMMENTS";

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

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        SlidingUpPanelLayout slidr = (SlidingUpPanelLayout) v.findViewById(R.id.slidinglayout);
        slidr.setPanelSlideListener(new SlideListener(toolbar));

        Story storyFromIntent = getStoryFromArgs();
        recyclerView.swapAdapter(new StoryAdapter(storyFromIntent, true), false);

        webview = (WebView) v.findViewById(R.id.webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webview.loadUrl(getStoryFromArgs().url);

        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.webview_progress);
        webview.setWebChromeClient(new MyChromeClient(progressBar));
        webview.setWebViewClient(new WebViewClient());

        toolbar.removeAllViews();
        View handle = inflater.inflate(R.layout.story_handle, toolbar, false);
        toolbar.addView(handle);
        View webbar = handle.findViewById(R.id.story_web_bar);
        bindWebController(webbar, new WebControllerListener(webview, slidr));
        View commentsbar = handle.findViewById(R.id.story_comments_bar);
        bindComments(commentsbar, new CommentsControllerListener(getStoryFromArgs().comments_url, inflater.getContext(), slidr));

        return v;
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

    private static void bindWebController(View web_bar, final WebControllerListener listener) {
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

    private static class WebControllerListener {
        private final WeakReference<WebView> webviewRef;
        private final WeakReference<SlidingUpPanelLayout> slidrRef;

        WebControllerListener(WebView webview, SlidingUpPanelLayout slidr) {
            this.webviewRef = new WeakReference<>(webview);
            this.slidrRef = new WeakReference<>(slidr);
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

    public void bindComments(View comment_bar, final CommentsControllerListener listener) {
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

    private static class CommentsControllerListener {
        private final String url;
        private final WeakReference<Context> contextRef;
        private final WeakReference<SlidingUpPanelLayout> slidrRef;

        private CommentsControllerListener(String url, Context context, SlidingUpPanelLayout slidr) {
            this.url = url;
            this.contextRef = new WeakReference<>(context);
            this.slidrRef = new WeakReference<>(slidr);
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

    private static class SlideListener implements SlidingUpPanelLayout.PanelSlideListener {
        private final WeakReference<Toolbar> toolbarRef;

        private SlideListener(Toolbar toolbar) {
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
