package ninja.mpnguyen.bisque.views.posts;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.activities.UserActivity;
import ninja.mpnguyen.bisque.things.StoredPost;
import ninja.mpnguyen.chowders.things.Post;

public class PostsPresenter {
    public static PostItemViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.list_item_post, viewGroup, false);
        return new PostItemViewHolder(postView);
    }

    public static void bindListItem(PostItemViewHolder holder, final StoredPost post) {
        int titleColorRes = post.read ? R.color.story_title_read : R.color.story_title_unread;
        Context context = holder.itemView.getContext();
        Resources resources = context.getResources();
        int titleColor = resources.getColor(titleColorRes);
        holder.text.setTextColor(titleColor);
        holder.text.setText(post.title);

        holder.tags.setText(Arrays.toString(post.tags) + "    " + post.comment_count + " comments");

        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra(UserActivity.EXTRA_USER, post.submitter_user);
                intent.putExtra(UserActivity.EXTRA_USERNAME, post.submitter_user.username);
                context.startActivity(intent);
            }
        });
    }

    public static PostViewHolder inflateItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.view_post, viewGroup, false);
        return new PostViewHolder(postView);
    }

    public static void bindItem(PostViewHolder holder, Post post) {
        holder.text.setText(post.title);
        holder.tags.setText(Arrays.toString(post.tags) + "    " + post.comment_count + " comments");
    }
}
