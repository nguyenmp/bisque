package ninja.mpnguyen.bisque.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.views.posts.PostsAdapter;
import ninja.mpnguyen.bisque.nio.PostsFetcherTask;
import ninja.mpnguyen.chowders.things.Post;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        if (swipeRefreshLayout == null || recyclerView == null) return;

        PostsFetchedListener listener = new PostsFetchedListener(this, swipeRefreshLayout, recyclerView);
        new PostsFetcherTask(listener).execute();
    }

    private static class PostsFetchedListener implements PostsFetcherTask.Listener<Post[]> {
        private final SwipeRefreshLayout refreshLayout;
        private final RecyclerView content;
        private final WeakReference<Activity> activityRef;

        private PostsFetchedListener(Activity activity, SwipeRefreshLayout refreshLayout, RecyclerView content) {
            this.refreshLayout = refreshLayout;
            this.content = content;
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onStart() {
            refreshLayout.setRefreshing(true);
        }

        @Override
        public void onSuccess(@NonNull Post[] posts) {
            refreshLayout.setRefreshing(false);
            content.swapAdapter(new PostsAdapter(posts, activityRef.get()), false);
        }

        @Override
        public void onError() {
            refreshLayout.setRefreshing(false);
            content.swapAdapter(new PostsAdapter(null, activityRef.get()), false);
        }
    }
}