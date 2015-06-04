package ninja.mpnguyen.bisque.views.posts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.StoryActivity;
import ninja.mpnguyen.bisque.views.errors.ErrorPresenter;
import ninja.mpnguyen.bisque.views.errors.ErrorViewHolder;
import ninja.mpnguyen.chowders.things.Post;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_POST = 0, TYPE_ERROR = 1, TYPE_EMPTY = 2;
    private final Post[] posts;
    private final WeakReference<Activity> activityGet;

    public PostsAdapter(Post[] posts, Activity activity) {
        this.posts = posts;
        this.activityGet = new WeakReference<>(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_POST) return PostsPresenter.inflateListItem(inflater, viewGroup);
        else return ErrorPresenter.inflateListItem(inflater, viewGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_POST) {
            PostViewHolder postViewHolder = (PostViewHolder) viewHolder;
            Post post = posts[position];
            View.OnClickListener listener = new PostItemClickListener(activityGet.get(), post);
            postViewHolder.cardView.setOnClickListener(listener);
            PostsPresenter.bindListItem(postViewHolder, post);
        } else {
            ErrorViewHolder errorViewHolder = (ErrorViewHolder) viewHolder;
            Context context = errorViewHolder.errorView.getContext();
            String pattern = posts == null ? context.getString(R.string.could_not_load_x) : context.getString(R.string.no_x_found);
            String value = context.getString(R.string.posts);
            String message = String.format(pattern, value);
            ErrorPresenter.bindListItem(errorViewHolder, message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (posts == null) return TYPE_ERROR;
        else if (posts.length == 0) return TYPE_EMPTY;
        else return TYPE_POST;
    }

    @Override
    public int getItemCount() {
        // If there's a problem, make space for an error message
        if (posts == null || posts.length == 0) return 1;
        else return posts.length;
    }

    private static class PostItemClickListener implements View.OnClickListener {
        private final WeakReference<Activity> activityRef;
        private final Post post;

        private PostItemClickListener(Activity activity, Post post) {
            this.activityRef = new WeakReference<>(activity);
            this.post = post;
        }

        @Override
        public void onClick(View v) {
            Activity activity = activityRef.get();
            if (activity == null) return;
            Intent intent = new Intent(activity, StoryActivity.class);
            intent.putExtra(StoryActivity.EXTRA_POST, post);
            intent.setData(Uri.parse(post.comments_url));

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, "card_selection_transform");
            activity.startActivity(intent, options.toBundle());
        }
    }
}
