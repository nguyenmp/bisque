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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import ninja.mpnguyen.chowders.nio.StoryFetcher;
import ninja.mpnguyen.chowders.things.Comment;
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

        View progress = findViewById(R.id.progress_view);
        TextView empty = (TextView) findViewById(R.id.empty_view);

        Intent intent = getIntent();
        Post post = intent.hasExtra(EXTRA_POST) ? (Post) intent.getSerializableExtra(EXTRA_POST) : null;
        if (post != null) {
            progress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            recyclerView.setAdapter(new Adapter(new Story(post)));
        }

        String short_id = getShortId(intent.getData());
        new FetchTask(short_id, progress, recyclerView, empty).execute();
    }

    private String getShortId(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths.size() >= 2 && paths.get(0).equalsIgnoreCase("s")) {
            return paths.get(1);
        } else {
            return null;
        }
    }

    private static class FetchTask extends AsyncTask<Void, Void, Story> {
        private final String short_id;
        private final View progress;
        private final TextView empty;
        private final RecyclerView content;

        private FetchTask(String short_id, View progress, RecyclerView content, TextView empty) {
            this.short_id = short_id;
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
        protected Story doInBackground(Void... params) {
            try {
                return StoryFetcher.get(short_id);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Story story) {
            super.onPostExecute(story);

            if (story == null) {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);

                Context context = progress.getContext();context.getString(R.string.no_x_found);
                String pattern = context.getString(R.string.could_not_load_x);
                String value = context.getString(R.string.comments);
                empty.setText(String.format(pattern, value));
            } else {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);

                content.setAdapter(new Adapter(story));
            }
        }
    }

    private static class CommentViewHolder extends StoryViewHolder {
        public final CardView cardView;
        public final TextView text, tags;
        public CommentViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.text = (TextView) itemView.findViewById(R.id.info_text);
            this.tags = (TextView) itemView.findViewById(R.id.tags);
        }
    }

    private static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_POST = 0, TYPE_COMMENT = 1;
        private final Story story;

        private Adapter(Story story) {
            this.story = story;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            if (itemType == TYPE_COMMENT) {
                return CommentPresenter.inflateListItem(inflater, viewGroup);
            } else {
                return MainActivity.PostPresenter.inflateListItem(inflater, viewGroup);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return TYPE_POST;
            else return TYPE_COMMENT;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder storyViewHolder, int position) {
            if (getItemViewType(position) == TYPE_POST) {
                MainActivity.PostPresenter.bindListItem((MainActivity.PostViewHolder) storyViewHolder, story);
                ((MainActivity.PostViewHolder) storyViewHolder).cardView.setOnClickListener(new PostClickListener(story));
            } else {
                Comment comment = story.comments[position - 1];
                CommentPresenter.bindListItem((CommentViewHolder) storyViewHolder, comment);
            }
        }

        @Override
        public int getItemCount() {
            return story.comments.length + 1;
        }
    }

    private static class PostClickListener implements View.OnClickListener {
        private final Post post;

        private PostClickListener(Post post) {
            this.post = post;
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            String link = post.url;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(intent);
        }
    }

    private static abstract class StoryViewHolder extends RecyclerView.ViewHolder {
        public StoryViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class CommentPresenter {
        public static CommentViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
            View postView = inflater.inflate(R.layout.list_item_comment, viewGroup, false);
            return new CommentViewHolder(postView);
        }

        public static void bindListItem(CommentViewHolder holder, Comment comment) {
            holder.text.setText(Html.fromHtml(comment.comment));
            holder.tags.setText(comment.commenting_user.username);
        }
    }
}
