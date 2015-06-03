package ninja.mpnguyen.bisque;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;

import ninja.mpnguyen.chowders.nio.FrontPage;
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
        new FetchTask(progress, recyclerView, empty).execute();
    }

    private static class FetchTask extends AsyncTask<Void, Void, Post[]> {
        private final View progress;
        private final TextView empty;
        private final RecyclerView content;

        private FetchTask(View progress, RecyclerView content, TextView empty) {
            this.progress = progress;
            this.content = content;
            this.empty = empty;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
        }

        @Override
        protected Post[] doInBackground(Void... params) {
            try {
                return FrontPage.get(FrontPage.Sort.Hottest);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Post[] posts) {
            super.onPostExecute(posts);

            if (posts == null || posts.length == 0) {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);

                Context context = progress.getContext();
                String pattern = posts != null ? context.getString(R.string.no_x_found) : context.getString(R.string.could_not_load_x);
                String value = context.getString(R.string.posts);
                empty.setText(String.format(pattern, value));
            } else {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);

                content.setAdapter(new PostsAdapter(posts));
            }
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public final CardView cardView;
        public final TextView text, tags;
        public PostViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.text = (TextView) itemView.findViewById(R.id.info_text);
            this.tags = (TextView) itemView.findViewById(R.id.tags);
        }
    }

    private static class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {
        private final Post[] posts;

        private PostsAdapter(Post[] posts) {
            this.posts = posts;
        }

        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            return PostPresenter.inflateListItem(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(PostViewHolder postViewHolder, int position) {
            Post post = posts[position];
            View.OnClickListener listener = new PostItemClickListener(post);
            postViewHolder.cardView.setOnClickListener(listener);
            PostPresenter.bindListItem(postViewHolder, post);
        }

        private static class PostItemClickListener implements View.OnClickListener {
            private final Post post;

            private PostItemClickListener(Post post) {
                this.post = post;
            }

            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, StoryActivity.class);
                intent.putExtra(StoryActivity.EXTRA_POST, post);
                intent.setData(Uri.parse(post.comments_url));
                context.startActivity(intent);
            }
        }

        @Override
        public int getItemCount() {
            return posts.length;
        }
    }

    public static class PostPresenter {
        public static PostViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
            View postView = inflater.inflate(R.layout.list_item_post, viewGroup, false);
            return new PostViewHolder(postView);
        }

        public static void bindListItem(PostViewHolder holder, Post post) {
            holder.text.setText(post.title);
            holder.tags.setText(Arrays.toString(post.tags));
        }
    }
}
