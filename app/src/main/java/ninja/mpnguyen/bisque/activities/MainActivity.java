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
import java.util.Observable;
import java.util.Observer;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.databases.MetafyTask;
import ninja.mpnguyen.bisque.databases.PostHelper;
import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.bisque.nio.PostsFetcherTask;
import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.bisque.views.posts.PostsAdapter;
import ninja.mpnguyen.chowders.things.Post;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, Observer {
    private Post[] posts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PostHelper.observable.addObserver(this);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.swapAdapter(new PostsAdapter(null, this, true), false);
        onRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PostHelper.observable.deleteObserver(this);
    }

    @Override
    public void onRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        if (swipeRefreshLayout == null || recyclerView == null) return;

        new PostsFetcherTask(new PostsListener(), this).execute();
    }

    @Override
    public void update(Observable observable, Object data) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        PostsFetchedListener listener = new PostsFetchedListener(this, swipeRefreshLayout, recyclerView);
        new MetafyTask(this, posts, listener).execute();
    }

    private class PostsListener implements FetcherTask.Listener<Post[]> {
        @Override
        public void onStart() {
            // Do nothing
        }

        @Override
        public void onSuccess(@NonNull Post[] result) {
            posts = result;
            update(PostHelper.observable, null);
        }

        @Override
        public void onError() {
            posts = null;
            update(PostHelper.observable, null);
        }
    }

    private class PostsFetchedListener extends RefreshingListener<MetaDataedPost[]> {
        private final WeakReference<Activity> activityRef;
        private final RecyclerView content;

        private PostsFetchedListener(Activity activity, SwipeRefreshLayout refreshLayout, RecyclerView content) {
            super(refreshLayout);
            this.content = content;
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull MetaDataedPost[] result) {
            super.onSuccess(result);
            content.swapAdapter(new PostsAdapter(result, activityRef.get(), false), false);
        }

        @Override
        public void onError() {
            super.onError();
            content.swapAdapter(new PostsAdapter(null, activityRef.get(), false), false);
        }
    }
}
