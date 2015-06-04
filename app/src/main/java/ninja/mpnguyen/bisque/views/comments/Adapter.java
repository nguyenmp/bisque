package ninja.mpnguyen.bisque.views.comments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.StoryActivity;
import ninja.mpnguyen.bisque.views.posts.PostViewHolder;
import ninja.mpnguyen.bisque.views.posts.PostsPresenter;
import ninja.mpnguyen.chowders.things.Comment;
import ninja.mpnguyen.chowders.things.Post;
import ninja.mpnguyen.chowders.things.Story;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_POST = 0, TYPE_COMMENT = 1;
    private final Story story;

    public Adapter(Story story) {
        super();
        this.story = story;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (itemType == TYPE_COMMENT) {
            return CommentPresenter.inflateListItem(inflater, viewGroup);
        } else {
            return PostsPresenter.inflateListItem(inflater, viewGroup);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return 0; // Just force the story to have it's own unique id as 0
        } else {
            return story.comments[position - 1].short_id.hashCode();
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
            PostsPresenter.bindListItem((PostViewHolder) storyViewHolder, story);
            ((PostViewHolder) storyViewHolder).cardView.setOnClickListener(new PostClickListener(story));
        } else {
            Comment comment = story.comments[position - 1];
            CommentPresenter.bindListItem((CommentViewHolder) storyViewHolder, comment);
        }
    }

    @Override
    public int getItemCount() {
        return story.comments.length + 1;
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
