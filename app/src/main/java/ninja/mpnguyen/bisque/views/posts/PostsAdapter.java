package ninja.mpnguyen.bisque.views.posts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.activities.StoryActivity;
import ninja.mpnguyen.bisque.views.errors.ErrorPresenter;
import ninja.mpnguyen.bisque.views.errors.ErrorViewHolder;
import ninja.mpnguyen.bisque.views.progress.ProgressPresenter;
import ninja.mpnguyen.bisque.views.progress.ProgressViewHolder;
import ninja.mpnguyen.chowders.things.Post;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_POST = 0, TYPE_ERROR = 1, TYPE_EMPTY = 2, TYPE_LOADING = 3;
    private final Post[] posts;
    private final boolean loading;
    private final WeakReference<Activity> activityGet;

    public PostsAdapter(Post[] posts, Activity activity, boolean loading) {
        this.posts = posts;
        this.activityGet = new WeakReference<>(activity);
        this.loading = loading;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_POST) return PostsPresenter.inflateListItem(inflater, viewGroup);
        else if (viewType == TYPE_LOADING) return ProgressPresenter.inflateListItem(inflater, viewGroup);
        else return ErrorPresenter.inflateListItem(inflater, viewGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        if (getItemViewType(position) == TYPE_POST) {
            PostItemViewHolder postItemViewHolder = (PostItemViewHolder) viewHolder;
            Post post = posts[position];
            View.OnClickListener listener = new PostItemClickListener(activityGet.get(), post);
            postItemViewHolder.cardView.setOnClickListener(listener);
            PostsPresenter.bindListItem(postItemViewHolder, post);
        } else if (type == TYPE_LOADING) {
            ProgressViewHolder progressViewHolder = (ProgressViewHolder) viewHolder;
            Context context = progressViewHolder.loadingText.getContext();
            String posts = context.getString(R.string.posts);
            ProgressPresenter.bindListItem(progressViewHolder, posts);
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
        if (loading) return TYPE_LOADING;
        else if (posts == null) return TYPE_ERROR;
        else if (posts.length == 0) return TYPE_EMPTY;
        else return TYPE_POST;
    }

    @Override
    public int getItemCount() {
        // If there's a problem, make space for an error message
        if (loading || posts == null || posts.length == 0) return 1;
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
            activity.startActivity(intent);
        }
    }
}
