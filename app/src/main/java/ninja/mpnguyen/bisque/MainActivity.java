package ninja.mpnguyen.bisque;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import ninja.mpnguyen.chowders.nio.FrontPage;
import ninja.mpnguyen.chowders.things.Post;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        View progress = findViewById(R.id.progress_view);
        View empty = findViewById(R.id.empty_view);
        new FetchTask(progress, recyclerView, empty).execute();
    }

    private static class FetchTask extends AsyncTask<Void, Void, Post[]> {
        private final View progress, empty;
        private final RecyclerView content;

        private FetchTask(View progress, RecyclerView content, View empty) {
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
            } else {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);

                content.setAdapter(new Adapter(posts));
            }
        }
    }

    private static class PostViewHolder extends RecyclerView.ViewHolder {
        public final TextView text;
        public PostViewHolder(View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(R.id.info_text);
        }
    }

    private static class Adapter extends RecyclerView.Adapter<PostViewHolder> {
        private final Post[] posts;

        private Adapter(Post[] posts) {
            this.posts = posts;
        }

        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View postView = inflater.inflate(R.layout.list_item_post, viewGroup, false);
            return new PostViewHolder(postView);
        }

        @Override
        public void onBindViewHolder(PostViewHolder postViewHolder, int i) {
            Post post = posts[i];
            postViewHolder.text.setText(post.title);
        }

        @Override
        public int getItemCount() {
            return posts.length;
        }
    }
}
