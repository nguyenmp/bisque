package ninja.mpnguyen.bisque;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ninja.mpnguyen.bisque.posts.PostFetcherTask;
import ninja.mpnguyen.bisque.posts.PostsAdapter;
import ninja.mpnguyen.chowders.things.Post;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        View progress = findViewById(R.id.progress_view);
        TextView empty = (TextView) findViewById(R.id.empty_view);

        PostFetchedListener listener = new PostFetchedListener(progress, recyclerView, empty);
        new PostFetcherTask(listener).execute();
    }

    private static class PostFetchedListener implements PostFetcherTask.Listener {
        private final View progress;
        private final RecyclerView content;
        private final TextView empty;

        private PostFetchedListener(View progress, RecyclerView content, TextView empty) {
            this.progress = progress;
            this.content = content;
            this.empty = empty;
        }

        @Override
        public void onStart() {
            progress.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(@NonNull Post[] posts) {

            if (posts.length == 0) {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);

                Context context = progress.getContext();
                String pattern = context.getString(R.string.no_x_found);
                String value = context.getString(R.string.posts);
                empty.setText(String.format(pattern, value));
            } else {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);

                content.setAdapter(new PostsAdapter(posts));
            }
        }

        @Override
        public void onError() {
            Context context = progress.getContext();
            String pattern = context.getString(R.string.no_x_found);
            String value = context.getString(R.string.posts);

            String message = String.format(pattern, value);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
