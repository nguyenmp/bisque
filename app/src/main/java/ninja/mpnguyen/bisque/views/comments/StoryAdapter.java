package ninja.mpnguyen.bisque.views.comments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.things.CommentMetadataWrapper;
import ninja.mpnguyen.bisque.things.StoryMetadataWrapper;
import ninja.mpnguyen.bisque.views.errors.ErrorPresenter;
import ninja.mpnguyen.bisque.views.errors.ErrorViewHolder;
import ninja.mpnguyen.bisque.views.posts.PostViewHolder;
import ninja.mpnguyen.bisque.views.posts.PostsPresenter;
import ninja.mpnguyen.bisque.views.progress.ProgressPresenter;
import ninja.mpnguyen.bisque.views.progress.ProgressViewHolder;
import ninja.mpnguyen.chowders.things.json.Post;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_POST = 0, TYPE_COMMENT = 1, TYPE_ERROR = 2, TYPE_EMPTY = 3, TYPE_LOADING = 4, TYPE_HIDDEN_COMMENT = 5;
    private final StoryMetadataWrapper storyWrapper;
    private final boolean showLoading;
    private final HideCommentListener hideListener;

    public interface HideCommentListener {
        void setCommentHidden(CommentMetadataWrapper commentWrapper, boolean hidden);
    }

    public StoryAdapter(StoryMetadataWrapper storyWrapper, boolean showLoading, HideCommentListener hideListener) {
        super();
        this.storyWrapper = storyWrapper;
        this.showLoading = showLoading;
        this.hideListener = hideListener;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (itemType == TYPE_COMMENT) {
            return CommentPresenter.inflateListItem(inflater, viewGroup);
        } else if (itemType == TYPE_POST){
            return PostsPresenter.inflateItem(inflater, viewGroup);
        } else if (itemType == TYPE_ERROR || itemType == TYPE_EMPTY) {
            return ErrorPresenter.inflateListItem(inflater, viewGroup);
        } else if (itemType == TYPE_HIDDEN_COMMENT) {
            return HiddenCommentPresenter.inflateListItem(inflater, viewGroup);
        } else {
            return ProgressPresenter.inflateListItem(inflater, viewGroup);
        }
    }

    @Override
    public long getItemId(int position) {
        int type = getItemViewType(position);
        if (type == TYPE_POST) {
            return 0; // Just force the story to have it's own unique id as 0
        } else if (type == TYPE_COMMENT) {
            return storyWrapper.commentWrappers[position - 1].comment.short_id.hashCode();
        } else if (type == TYPE_ERROR){
            return -1;
        } else if (type == TYPE_LOADING) {
            return -2;
        } else if (type == TYPE_HIDDEN_COMMENT) {
            return storyWrapper.commentWrappers[position - 1].comment.short_id.hashCode() * -1;
        } else {
            return -3;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (storyWrapper == null) return showLoading ? TYPE_LOADING : TYPE_ERROR;
        else {
            if (position == 0) return TYPE_POST;
            else if (showLoading) return TYPE_LOADING;
            else if (storyWrapper.commentWrappers == null) return TYPE_ERROR;
            else if (storyWrapper.commentWrappers.length == 0) return TYPE_EMPTY;
            else if (storyWrapper.commentWrappers[position - 1].metadata.hide_children) return TYPE_HIDDEN_COMMENT;
            else return TYPE_COMMENT;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_POST) {
            PostsPresenter.bindItem((PostViewHolder) viewHolder, storyWrapper.postWrapper);
            ((PostViewHolder) viewHolder).itemView.setOnClickListener(new PostClickListener(storyWrapper.postWrapper.post));
        } else if (type == TYPE_COMMENT){
            CommentViewHolder commentHolder = (CommentViewHolder) viewHolder;
            CommentMetadataWrapper commentWrapper = storyWrapper.commentWrappers[position - 1];
            CommentPresenter.bindListItem(commentHolder, commentWrapper.comment);
            commentHolder.content.setOnClickListener(new CommentClickListener(commentWrapper, hideListener));
        } else if (type == TYPE_ERROR) {
            ErrorViewHolder errorViewHolder = (ErrorViewHolder) viewHolder;
            Context context = errorViewHolder.errorView.getContext();
            String pattern = context.getString(R.string.could_not_load_x);
            String value = context.getString(R.string.comments);
            String message = String.format(pattern, value);
            ErrorPresenter.bindListItem(errorViewHolder, message);
        } else if (type == TYPE_EMPTY) {
            ErrorViewHolder errorViewHolder = (ErrorViewHolder) viewHolder;
            Context context = errorViewHolder.errorView.getContext();
            String pattern = context.getString(R.string.no_x_found);
            String value = context.getString(R.string.comments);
            String message = String.format(pattern, value);
            ErrorPresenter.bindListItem(errorViewHolder, message);
        } else if (type == TYPE_LOADING) {
            ProgressViewHolder progressViewHolder = (ProgressViewHolder) viewHolder;
            Context context = progressViewHolder.loadingText.getContext();
            String text = context.getString(R.string.comments);
            ProgressPresenter.bindListItem(progressViewHolder, text);
        } else if (type == TYPE_HIDDEN_COMMENT) {
            HiddenCommentViewHolder hiddenCommentViewHolder = (HiddenCommentViewHolder) viewHolder;
            CommentMetadataWrapper commentWrapper = storyWrapper.commentWrappers[position - 1];
            HiddenCommentPresenter.bindListItem(hiddenCommentViewHolder, commentWrapper);
            hiddenCommentViewHolder.content.setOnClickListener(new CommentClickListener(commentWrapper, hideListener));
        }
    }

    @Override
    public int getItemCount() {
        if (storyWrapper == null) return 1;
        else if (showLoading) return 2;
        else if (storyWrapper.commentWrappers == null) return 2;
        else if (storyWrapper.commentWrappers.length == 0) return 2;
        else return storyWrapper.commentWrappers.length + 1;
    }

    public static class CommentClickListener implements View.OnClickListener {
        private final CommentMetadataWrapper commentWrapper;
        private final HideCommentListener listener;

        public CommentClickListener(CommentMetadataWrapper commentWrapper, HideCommentListener listener) {
            this.commentWrapper = commentWrapper;
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            this.listener.setCommentHidden(commentWrapper, !commentWrapper.metadata.hide_children);
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
}
