package ninja.mpnguyen.bisque;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.bisque.nio.StoryFetcherTask;
import ninja.mpnguyen.bisque.views.comments.StoryAdapter;
import ninja.mpnguyen.chowders.things.Post;
import ninja.mpnguyen.chowders.things.Story;

public class StoryActivity extends AppCompatActivity {
    public static final String EXTRA_POST = "ninja.mpnguyen.bisque.StoryActivity.EXTRA_POST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        Post post = intent.hasExtra(EXTRA_POST) ? (Post) intent.getSerializableExtra(EXTRA_POST) : null;
        if (post != null) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.swapAdapter(new StoryAdapter(new Story(post)), false);
        }

        String short_id = getShortId(intent.getData());
        StoryFetchedListener listener = new StoryFetchedListener(swipeRefreshLayout, recyclerView);
        new StoryFetcherTask(listener, short_id).execute();
    }

    private String getShortId(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths.size() >= 2 && paths.get(0).equalsIgnoreCase("s")) {
            return paths.get(1);
        } else {
            return null;
        }
    }

    private static class StoryFetchedListener implements FetcherTask.Listener<Story> {
        private final SwipeRefreshLayout refreshLayout;
        private final RecyclerView content;

        private StoryFetchedListener(SwipeRefreshLayout refreshLayout, RecyclerView content) {
            this.refreshLayout = refreshLayout;
            this.content = content;
        }

        @Override
        public void onStart() {
            refreshLayout.setRefreshing(true);
        }

        @Override
        public void onSuccess(@NonNull Story story) {
            refreshLayout.setRefreshing(false);
            content.swapAdapter(new StoryAdapter(story), false);
        }

        @Override
        public void onError() {
            refreshLayout.setRefreshing(false);
            content.swapAdapter(new StoryAdapter(null), false);
        }
    }

}
