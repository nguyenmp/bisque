package ninja.mpnguyen.bisque.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.nio.StoryFetcherTask;
import ninja.mpnguyen.bisque.views.comments.StoryAdapter;
import ninja.mpnguyen.chowders.things.Post;
import ninja.mpnguyen.chowders.things.Story;

public class StoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_POST = "ninja.mpnguyen.bisque.activities.StoryActivity.EXTRA_POST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Story storyFromIntent = getStoryFromIntent();
        if (storyFromIntent != null) {
            recyclerView.swapAdapter(new StoryAdapter(storyFromIntent), false);
        }

        onRefresh();
    }

    private Story getStoryFromIntent() {
        Intent intent = getIntent();
        Post post = intent.hasExtra(EXTRA_POST) ? (Post) intent.getSerializableExtra(EXTRA_POST) : null;
        return new Story(post);
    }

    private String getShortId(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths.size() >= 2 && paths.get(0).equalsIgnoreCase("s")) {
            return paths.get(1);
        } else {
            return null;
        }
    }

    @Override
    public void onRefresh() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        if (swipeRefreshLayout == null) return;

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        if (recyclerView == null) return;

        Intent intent = getIntent();
        String short_id = getShortId(intent.getData());
        StoryFetchedListener listener = new StoryFetchedListener(swipeRefreshLayout, recyclerView, getStoryFromIntent());
        new StoryFetcherTask(listener, short_id).execute();
    }

    private static class StoryFetchedListener extends RefreshingListener<Story> {
        private final WeakReference<RecyclerView> contentRef;
        private final Story cachedStory;

        private StoryFetchedListener(SwipeRefreshLayout refreshLayout, RecyclerView content, Story cachedStory) {
            super(refreshLayout);
            this.contentRef = new WeakReference<>(content);
            this.cachedStory = cachedStory;
        }

        @Override
        public void onSuccess(@NonNull Story story) {
            super.onSuccess(story);

            RecyclerView content = contentRef.get();
            if (content == null) return;
            content.swapAdapter(new StoryAdapter(story), false);
        }

        @Override
        public void onError() {
            super.onError();

            RecyclerView content = contentRef.get();
            if (content == null) return;
            content.swapAdapter(new StoryAdapter(cachedStory), false);
        }
    }

}
