package ninja.mpnguyen.bisque.views.posts;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.fragments.PostsListFragment;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.bisque.views.errors.ErrorPresenter;
import ninja.mpnguyen.bisque.views.errors.ErrorViewHolder;
import ninja.mpnguyen.bisque.views.progress.ProgressPresenter;
import ninja.mpnguyen.bisque.views.progress.ProgressViewHolder;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_POST = 0, TYPE_ERROR = 1, TYPE_EMPTY = 2, TYPE_LOADING = 3;
    private final MetaDataedPost[] posts;
    private final boolean loading;
    private final PostsListFragment.PostClickListener clickListener;
    private final PostsListFragment.PostHideListener hideListener;

    public PostsAdapter(@Nullable MetaDataedPost[] posts, boolean loading, PostsListFragment.PostClickListener clickListener, PostsListFragment.PostHideListener hideListener) {
        this.posts = posts;
        this.loading = loading;
        this.clickListener = clickListener;
        this.hideListener = hideListener;
        setHasStableIds(true);
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
        if (getItemViewType(position) == TYPE_POST && posts != null) {
            PostItemViewHolder postItemViewHolder = (PostItemViewHolder) viewHolder;
            MetaDataedPost post = posts[position];
            View.OnClickListener listener = new PostItemClickListener(post, this.clickListener);
            postItemViewHolder.vh.cardView.setOnClickListener(listener);
            postItemViewHolder.vh.action_delete.setOnClickListener(new PostHideClickListener(hideListener, post));
            PostsPresenter.bindListItem(postItemViewHolder, new MetaDataedPost(post));
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
    public long getItemId(int position) {
        if (loading) return -1;
        else if (posts == null) return -2;
        else if (posts.length == 0) return -3;
        else return posts[position].metadata.id;
    }

    @Override
    public int getItemCount() {
        // If there's a problem, make space for an error message
        if (loading || posts == null || posts.length == 0) return 1;
        else return posts.length;
    }

    private static class PostHideClickListener implements View.OnClickListener {
        private final PostsListFragment.PostHideListener listener;
        private final MetaDataedPost post;

        private PostHideClickListener(PostsListFragment.PostHideListener listener, MetaDataedPost post) {
            this.listener = listener;
            this.post = post;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) listener.onPostHidden(post);
        }
    }

    private static class PostItemClickListener implements View.OnClickListener {
        private final MetaDataedPost post;
        private final PostsListFragment.PostClickListener listener;

        private PostItemClickListener(MetaDataedPost post, PostsListFragment.PostClickListener listener) {
            this.post = post;
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) listener.onPostClicked(post);
        }
    }
}
