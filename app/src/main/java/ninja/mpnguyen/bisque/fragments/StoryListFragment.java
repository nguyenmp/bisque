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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

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

        Story storyFromIntent = getStoryFromArgs();
        recyclerView.swapAdapter(new StoryAdapter(storyFromIntent, true), false);

        WebView webview = (WebView) v.findViewById(R.id.webview);
        webview.loadUrl(getStoryFromArgs().url);

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
}
